package ro.mycode.solarsyncbroker.financial.mapper;

import ro.mycode.solarsyncbroker.financial.dtos.FinancialtransactionResponseToDto;
import ro.mycode.solarsyncbroker.financial.model.FinancialTransaction;

public class FinancialMapper {

    public static FinancialtransactionResponseToDto financialTransactionToResponse(FinancialTransaction tx) {
        if (tx == null) {
            return null;
        }

        return new FinancialtransactionResponseToDto(
                tx.getId(),
                tx.getTimestamp(),
                tx.getHouseId(),
                tx.getTransactionType(),
                tx.getAmountKwh(),
                tx.getUnitPrice(),
                tx.getTotalValue()
        );
    }
}