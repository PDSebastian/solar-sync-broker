package ro.mycode.solarsyncbroker.integrationTests.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;
import ro.mycode.solarsyncbroker.financial.repository.FinancialTransactionRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FinancialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FinancialTransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        FinancialTransaction tx = new FinancialTransaction();
        tx.setTimestamp(LocalDateTime.now());
        tx.setHouseId(1L);
        tx.setTransactionType("BUY");
        tx.setAmountKwh(20.0);
        tx.setUnitPrice(0.50);
        tx.setTotalValue(10.0);

        transactionRepository.save(tx);
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_VIEW")
    void getHouseFinancialSummary_shouldReturnTransactions_whenAuthorized() throws Exception {
        mockMvc.perform(get("/houses/1/financial-summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].houseId").value(1))
                .andExpect(jsonPath("$[0].transactionType").value("BUY"))
                .andExpect(jsonPath("$[0].totalValue").value(10.0));
    }

    @Test
    @WithMockUser(authorities = "COMMUNITY_VIEW")
    void getCommunityFinancialSummary_shouldReturnAllTransactions_whenAuthorized() throws Exception {
        mockMvc.perform(get("/houses/community/financial-summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(authorities = "WRONG_AUTHORITY")
    void getHouseFinancialSummary_shouldReturnForbidden_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/houses/1/financial-summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}