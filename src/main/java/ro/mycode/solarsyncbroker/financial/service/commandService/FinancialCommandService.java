package ro.mycode.solarsyncbroker.financial.service.commandService;

import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;

public interface FinancialCommandService {
    FinancialTransaction recordTransaction(Long houseId, String type, double amountKwh, double unitPrice);
}