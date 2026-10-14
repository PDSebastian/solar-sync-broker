package ro.mycode.solarsyncbroker.financial.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class FinancialTransactionNotFoundException extends RuntimeException {
    public FinancialTransactionNotFoundException() {
        super(ErrorConstants.FINANCIAL_TRANSACTION_NOT_FOUND);
    }
}