package ro.mycode.solarsyncbroker.financial.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Column(name = "house_id")
    private Long houseId;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "amount_kwh")
    private double amountKwh;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "total_value")
    private double totalValue;
}