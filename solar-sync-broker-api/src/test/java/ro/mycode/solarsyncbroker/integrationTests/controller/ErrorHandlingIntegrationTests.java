package ro.mycode.solarsyncbroker.integrationTests.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ErrorHandlingIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private BatteryRepository batteryRepository;

    private House ownedByOther;

    @BeforeEach
    void setUp() {
        batteryRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(user("admin@test.ro", UserType.ADMIN));
        userRepository.save(user("intruder@test.ro", UserType.USER));
        User owner = userRepository.save(user("owner@test.ro", UserType.USER));

        ownedByOther = houseRepository.save(House.builder()
                .name("Casa Owner").pvPeakPowerKw(5.0)
                .maxImportPowerKw(10.0).maxExportPowerKw(10.0)
                .enabled(true).owner(owner).build());
    }

    private User user(String email, UserType type) {
        return User.builder().firstName("Test").lastName("User").email(email)
                .password("parola").age(30).userType(type).build();
    }

    @Test
    @WithMockUser(username = "admin@test.ro", authorities = {"house:view"})
    void getHouse_unknownId_returns404WithBody() throws Exception {
        mockMvc.perform(get("/api/v1/houses/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(ErrorConstants.HOUSE_NOT_FOUND));
    }

    @Test
    @WithMockUser(username = "intruder@test.ro", authorities = {"house:view"})
    void getHouse_notOwner_returns403WithBody() throws Exception {
        mockMvc.perform(get("/api/v1/houses/{id}", ownedByOther.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value(ErrorConstants.HOUSE_ACCESS_DENIED));
    }
}
