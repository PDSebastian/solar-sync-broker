package ro.mycode.solarsyncbroker.battery.command.model;

import jakarta.persistence.*;
import lombok.*;
import ro.mycode.solarsyncbroker.house.model.House;

import java.time.LocalDateTime;

@Entity
@Table(name = "command_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "command_type", nullable = false)
    private String commandType;

    @Column(name = "power_kw", nullable = false)
    private Double powerKw;

    @Column(nullable = false)
    private String status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "soc_after_percent")
    private Double socAfterPercent;

    @Column(name = "executed_by", nullable = false)
    private String executedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}