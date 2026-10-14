package ro.mycode.solarsyncbroker.financial.service.commandService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;
import ro.mycode.solarsyncbroker.financial.repository.FinancialTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FinancialCommandServiceImpl implements FinancialCommandService {

    private final FinancialTransactionRepository transactionRepository;

    public FinancialCommandServiceImpl(FinancialTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public FinancialTransaction recordTransaction(Long houseId, String type, double amountKwh, double unitPrice) {
        double totalValue = amountKwh * unitPrice;

        FinancialTransaction tx = new FinancialTransaction();
        tx.setTimestamp(LocalDateTime.now());
        tx.setHouseId(houseId);
        tx.setTransactionType(type);
        tx.setAmountKwh(amountKwh);
        tx.setUnitPrice(unitPrice);
        tx.setTotalValue(totalValue);

        return transactionRepository.save(tx);
    }
}