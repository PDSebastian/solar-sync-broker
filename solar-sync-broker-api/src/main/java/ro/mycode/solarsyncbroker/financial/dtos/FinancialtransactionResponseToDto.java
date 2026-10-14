package ro.mycode.solarsyncbroker.financial.dtos;

import java.time.LocalDateTime;

public record FinancialtransactionResponseToDto(
        Long id,
        LocalDateTime timestamp,
        Long houseId,
        String transactionType,
        double amountKwh,
        double unitPrice,
        double totalValue
) {
}
