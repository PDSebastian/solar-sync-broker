package ro.mycode.solarsyncbroker.financial.service.queryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;
import ro.mycode.solarsyncbroker.financial.repository.FinancialTransactionRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FinancialQueryServiceImpl implements FinancialQueryService {

    private final FinancialTransactionRepository transactionRepository;

    public FinancialQueryServiceImpl(FinancialTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<FinancialTransaction> getTransactionsByHouseId(Long houseId) {
        return transactionRepository.findByHouseId(houseId);
    }

    @Override
    public List<FinancialTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
