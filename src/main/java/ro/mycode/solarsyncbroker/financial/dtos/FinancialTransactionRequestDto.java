package ro.mycode.solarsyncbroker.financial.dtos;


public record   FinancialTransactionRequestDto(
         Long houseId,
        String transactionType,
        double amountKwh,
        double unitPrice

) {

}