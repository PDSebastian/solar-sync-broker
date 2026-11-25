package ro.mycode.solarsyncbroker.integrationTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.system.security.UserPermissions;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * B3: PATCH /houses/{id}/battery/configuration trebuie sa SALVEZE in baza, nu doar sa raspunda 200.
 * Fara @Transactional pe clasa: fiecare cerere ruleaza in tranzactia ei, deci un GET dupa PATCH
 * citeste din baza, nu din obiectul din memorie.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BatteryConfigurationIT {

    private static final String PASSWORD = "Parola12345!";
    private static final String EMAIL = "admin-battery@it.test";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private BatteryRepository batteryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long houseId;
    private Long batteryId;

    @BeforeEach
    void setUp() {
        cleanUp();
        User admin = userRepository.save(User.builder()
                .firstName("Admin").lastName("Battery").age(30)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .userType(UserType.ADMIN)
                .permissions(Set.of(UserPermissions.BATTERY_VIEW, UserPermissions.BATTERY_CONFIG))
                .build());
        House house = houseRepository.save(House.builder()
                .name("Casa Config").owner(admin).enabled(true)
                .pvPeakPowerKw(5.0).maxImportPowerKw(10.0).maxExportPowerKw(10.0)
                .build());
        Battery battery = batteryRepository.save(Battery.builder()
                .house(house).socPercent(50.0)
                .maxChargePowerKw(3.0).maxDischargePowerKw(3.0).efficientyPercent(95.0)
                .build());
        houseId = house.getId();
        batteryId = battery.getId();
    }

    @AfterEach
    void cleanUp() {
        batteryRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String login() throws Exception {
        String json = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("token").asText();
    }

    @Test
    void patchConfiguration_persistsValues_andReturnsThem() throws Exception {
        String token = login();
        String body = objectMapper.writeValueAsString(Map.of("maxChargePowerKw", 4.5, "maxDischargePowerKw", 4.0));

        mockMvc.perform(patch("/api/v1/houses/{id}/battery/configuration", houseId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxChargePowerKw").value(4.5))
                .andExpect(jsonPath("$.maxDischargePowerKw").value(4.0));

        // Citit din baza, nu din raspunsul PATCH
        Battery fromDb = batteryRepository.findById(batteryId).orElseThrow();
        assertThat(fromDb.getMaxChargePowerKw()).isEqualTo(4.5);
        assertThat(fromDb.getMaxDischargePowerKw()).isEqualTo(4.0);
        // Restul bateriei nu se atinge
        assertThat(fromDb.getSocPercent()).isEqualTo(50.0);
        assertThat(fromDb.getEfficientyPercent()).isEqualTo(95.0);
    }

    @Test
    void getBattery_afterPatch_returnsNewConfiguration() throws Exception {
        String token = login();
        String body = objectMapper.writeValueAsString(Map.of("maxChargePowerKw", 4.5, "maxDischargePowerKw", 4.0));
        mockMvc.perform(patch("/api/v1/houses/{id}/battery/configuration", houseId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/houses/{id}/battery", houseId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxChargePowerKw").value(4.5))
                .andExpect(jsonPath("$.maxDischargePowerKw").value(4.0));
    }

    @Test
    void patchConfiguration_withoutToken_returns401_andChangesNothing() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("maxChargePowerKw", 9.0, "maxDischargePowerKw", 9.0));
        mockMvc.perform(patch("/api/v1/houses/{id}/battery/configuration", houseId)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());

        assertThat(batteryRepository.findById(batteryId).orElseThrow().getMaxChargePowerKw()).isEqualTo(3.0);
    }
}
