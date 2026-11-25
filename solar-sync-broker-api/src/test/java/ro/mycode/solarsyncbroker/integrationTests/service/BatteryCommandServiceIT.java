package ro.mycode.solarsyncbroker.integrationTests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryAction;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryCommand;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryConfigurationRequest;
import ro.mycode.solarsyncbroker.battery.dtos.BatteryResponse;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.battery.service.commandService.BatteryCommandService;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedHandler;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class BatteryCommandServiceIT {

    @Autowired
    private BatteryCommandService service;

    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private HouseRepository houseRepository;

    private Battery testBattery;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        String email="emailTest@gmail.com";

        batteryRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();

            User user = User.builder()
                    .email(email)
                    .password("password123")
                    .firstName("Test")
                    .lastName("User")
                    .age(30)
                    .userType(UserType.USER)
                    .build();
            user = userRepository.save(user);

            House casa = House.builder()
                    .name("Casa Test")
                    .owner(user)
                    .enabled(true)
                    .pvPeakPowerKw(5.0)
                    .maxImportPowerKw(10.0)
                    .maxExportPowerKw(10.0)
                    .build();
            casa = houseRepository.save(casa);

            testBattery = Battery.builder()
                    .house(casa)
                    .socPercent(50.0)
                    .maxChargePowerKw(3.0)
                    .maxDischargePowerKw(3.0)
                    .efficientyPercent(100.0)
                    .build();
            testBattery = batteryRepository.save(testBattery);
        }

    @Test
    void testIncarcare() {
        BatteryCommand comanda = new BatteryCommand(BatteryAction.CHARGE, 2.0);
        service.applyCommand(testBattery, comanda, 1.0);

        Battery dinDb = batteryRepository.findById(testBattery.getId()).orElseThrow();
        assertEquals(70.0, dinDb.getSocPercent(), 0.0001);
    }

    @Test
    void testDescarcare() {
        BatteryCommand comanda = new BatteryCommand(BatteryAction.DISCHARGE, 2.0);
        service.applyCommand(testBattery, comanda, 1.0);

        Battery dinDb = batteryRepository.findById(testBattery.getId()).orElseThrow();
        assertEquals(70.0, dinDb.getSocPercent(), 0.0001);
    }

    @Test
    void testActualizareConfig() {
        String ownerEmail = "emailTest@gmail.com";
        BatteryConfigurationRequest request = new BatteryConfigurationRequest(4.5, 4.0);
        BatteryResponse response = service.updateConfiguration(testBattery.getHouse().getId(), request, ownerEmail);

        // raspunsul si baza trebuie sa aiba valorile NOI (4.5 / 4.0), nu cele vechi (3.0)
        assertEquals(4.5, response.maxChargePowerKw(), 0.0001);
        assertEquals(4.0, response.maxDischargePowerKw(), 0.0001);
        Battery dinDb = batteryRepository.findById(testBattery.getId()).orElseThrow();
        assertEquals(4.5, dinDb.getMaxChargePowerKw(), 0.0001);
        assertEquals(4.0, dinDb.getMaxDischargePowerKw(), 0.0001);
    }

    @Test
    void testActualizareConfig_strainNuAreAcces_siNuSeSchimbaNimic() {
        User strain = userRepository.save(User.builder()
                .email("strain@gmail.com").password("password123")
                .firstName("Strain").lastName("User").age(40)
                .userType(UserType.USER).build());
        BatteryConfigurationRequest request = new BatteryConfigurationRequest(9.0, 9.0);

        assertThrows(HouseAccessDeniedHandler.class,
                () -> service.updateConfiguration(testBattery.getHouse().getId(), request, strain.getEmail()));

        Battery dinDb = batteryRepository.findById(testBattery.getId()).orElseThrow();
        assertEquals(3.0, dinDb.getMaxChargePowerKw(), 0.0001);
        assertEquals(3.0, dinDb.getMaxDischargePowerKw(), 0.0001);
    }

    @Test
    void testActualizareConfig_casaInexistenta_arunca404() {
        BatteryConfigurationRequest request = new BatteryConfigurationRequest(4.5, 4.0);
        assertThrows(HouseNotFoundException.class,
                () -> service.updateConfiguration(999_999L, request, "emailTest@gmail.com"));
    }
}
