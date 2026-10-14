package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;
import ro.mycode.solarsyncbroker.financial.repository.FinancialTransactionRepository;
import ro.mycode.solarsyncbroker.financial.service.commandService.FinancialCommandServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinancialCommandServiceImplTest {

    private FinancialTransactionRepository transactionRepository;
    private FinancialCommandServiceImpl commandService;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(FinancialTransactionRepository.class);
        commandService = new FinancialCommandServiceImpl(transactionRepository);
    }

    @Test
    void recordTransaction_shouldCalculateTotalAndSave() {
        // Given
        Long houseId = 1L;
        String type = "BUY";
        double amountKwh = 10.0;
        double unitPrice = 0.75;

        when(transactionRepository.save(any(FinancialTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FinancialTransaction result = commandService.recordTransaction(houseId, type, amountKwh, unitPrice);


        assertNotNull(result);
        assertEquals(houseId, result.getHouseId());
        assertEquals(type, result.getTransactionType());
        assertEquals(amountKwh, result.getAmountKwh());
        assertEquals(unitPrice, result.getUnitPrice());
        assertEquals(7.5, result.getTotalValue());

        ArgumentCaptor<FinancialTransaction> captor = ArgumentCaptor.forClass(FinancialTransaction.class);
        verify(transactionRepository).save(captor.capture());
        assertEquals(7.5, captor.getValue().getTotalValue());
    }
}