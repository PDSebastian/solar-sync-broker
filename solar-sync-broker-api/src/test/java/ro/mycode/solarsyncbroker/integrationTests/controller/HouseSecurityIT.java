package ro.mycode.solarsyncbroker.integrationTests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.system.security.UserPermissions;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste cu login REAL (token din /auth/login), fara @WithMockUser.
 * Acopera B1 (numele permisiunilor din @PreAuthorize) si B2 (register nu mai accepta userType).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HouseSecurityIT {

    private static final String PASSWORD = "Parola12345!";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminEmail;
    private Long adminId;

    @BeforeEach
    void setUp() {
        adminEmail = "admin@it.test";
        User admin = userRepository.save(User.builder()
                .firstName("Admin").lastName("It").age(30)
                .email(adminEmail)
                .password(passwordEncoder.encode(PASSWORD))
                .userType(UserType.ADMIN)
                .permissions(Set.of(UserPermissions.HOUSE_VIEW, UserPermissions.HOUSE_MANAGE))
                .build());
        adminId = admin.getId();
    }

    private String login(String email) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("email", email, "password", PASSWORD));
        String json = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("token").asText();
    }

    private String registerBody(String email, String extraJson) {
        return "{\"firstName\":\"Ana\",\"lastName\":\"Pop\",\"email\":\"" + email
                + "\",\"password\":\"" + PASSWORD + "\",\"age\":25" + extraJson + "}";
    }

    private String houseBody() throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "name", "Casa Pop", "pvPeakPowerKw", 5.0,
                "maxImportPowerKw", 6.0, "maxExportPowerKw", 4.0, "ownerId", adminId));
    }

    // ---------- B1 ----------

    @Test
    void admin_withRealToken_canListHouses() throws Exception {
        String token = login(adminEmail);
        mockMvc.perform(get("/api/v1/houses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void admin_withRealToken_canCreateHouse() throws Exception {
        String token = login(adminEmail);
        mockMvc.perform(post("/api/v1/houses").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(houseBody()))
                .andExpect(status().isCreated());
    }

    @Test
    void houses_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/houses")).andExpect(status().isUnauthorized());
    }

    @Test
    void user_withRealToken_cannotCreateHouse() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("ana@it.test", "")))
                .andExpect(status().isCreated());
        String token = login("ana@it.test");

        mockMvc.perform(post("/api/v1/houses").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(houseBody()))
                .andExpect(status().isForbidden());
    }

    // ---------- B2 ----------

    @Test
    void register_withUserTypeAdmin_stillCreatesUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("evil@it.test", ",\"userType\":\"ADMIN\"")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userType").value("USER"));

        assertThat(userRepository.findByEmail("evil@it.test")).get()
                .satisfies(u -> {
                    assertThat(u.getUserType()).isEqualTo(UserType.USER);
                    assertThat(u.getPermissions()).doesNotContain(UserPermissions.HOUSE_MANAGE);
                });
    }

    @Test
    void register_withUserTypeAdmin_cannotManageHouses() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("evil2@it.test", ",\"userType\":\"ADMIN\"")))
                .andExpect(status().isCreated());
        String token = login("evil2@it.test");

        // token real, dar fara permisiuni de admin
        mockMvc.perform(post("/api/v1/houses").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(houseBody()))
                .andExpect(status().isForbidden());
    }
}
