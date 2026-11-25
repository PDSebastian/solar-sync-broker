package ro.mycode.solarsyncbroker.battery.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BatteryConfigurationRequest(

        @NotNull(message = "Puterea maxima de incarcare este obligatorie")
        @Positive(message = "maxChargePowerKw trebuie sa fie mai mare ca 0")
        Double maxChargePowerKw,
        @NotNull(message = "Puterea maxima de descarcare este obligatorie")
        @Positive(message = "maxDischargePowerKw trebuie sa fie mai mare ca 0")
        Double maxDischargePowerKw



) {
}
