package ro.mycode.solarsyncbroker.financial.service.queryService;

import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;
import java.util.List;

public interface FinancialQueryService {
    List<FinancialTransaction> getTransactionsByHouseId(Long houseId);
    List<FinancialTransaction> getAllTransactions();
}