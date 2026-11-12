package ro.mycode.solarsyncbroker.battery.service.commandService;

import org.springframework.stereotype.Service; // Recomandat în loc de @Component
import org.springframework.transaction.annotation.Transactional;
import ro.mycode.solarsyncbroker.battery.command.model.CommandExecution;
import ro.mycode.solarsyncbroker.battery.dtos.*;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.mapper.BatteryMapper;
import ro.mycode.solarsyncbroker.battery.mapper.CommandExecutionMapper;
import ro.mycode.solarsyncbroker.battery.model.Battery;
import ro.mycode.solarsyncbroker.battery.repository.BatteryRepository;
import ro.mycode.solarsyncbroker.battery.repository.CommandExecutionRepository;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedHandler;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.house.model.House;
import ro.mycode.solarsyncbroker.house.repository.HouseRepository;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;
import ro.mycode.solarsyncbroker.users.model.User;
import ro.mycode.solarsyncbroker.users.model.UserType;
import ro.mycode.solarsyncbroker.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BatteryCommandServiceImpl implements BatteryCommandService {

    private static final double MIN_SOC_PERCENT = 10.0;
    private static final double MAX_SOC_PERCENT = 90.0;

    private final BatteryRepository batteryRepository;
    private final BatteryCommandValidator validator;
    private final CommandExecutionRepository repository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;



    public BatteryCommandServiceImpl(BatteryRepository batteryRepository,
                                     BatteryCommandValidator validator,
                                     CommandExecutionRepository repository,
                                     HouseRepository houseRepository,
                                     UserRepository userRepository) {
        this.batteryRepository = batteryRepository;
        this.validator = validator;
        this.repository = repository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }
    private void validateHouseAccess(User user, House house) {
        if (user.getUserType() == UserType.ADMIN || user.getUserType() == UserType.OPERATOR) {
            return;
        }
        boolean isOwner = houseRepository.existsByIdAndOwner_Email(house.getId(), user.getEmail());
        if (!isOwner) {
            throw new HouseAccessDeniedHandler();
        }

    }


    @Override
    public BatteryResponse updateConfiguration(Long houseId, BatteryConfigurationRequest request, String username) {
        return null;
    }

    @Override
    public Battery applyCommand(Battery battery, BatteryCommand command, double deltaHours) {

        validator.validate(battery,command,deltaHours);
        battery.setSocPercent(70.0);
        return batteryRepository.save(battery);
    }

    @Override
    public CommandExecutionResponse executeCommand(Long houseId, BatteryCommandRequest request, String username) {
        return null;
    }

    @Override
    public List<CommandExecutionResponse> getCommandsForHouse(Long houseId, String username) {
        return List.of();
    }
}