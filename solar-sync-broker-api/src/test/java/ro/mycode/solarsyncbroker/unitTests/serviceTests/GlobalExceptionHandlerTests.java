package ro.mycode.solarsyncbroker.unitTests.serviceTests;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ro.mycode.solarsyncbroker.battery.exceptions.AccessDeniedExceptions;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryAlreadyExistsException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryPowerLimitException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatterySocLimitException;
import ro.mycode.solarsyncbroker.financial.exceptions.FinancialTransactionNotFoundException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;
import ro.mycode.solarsyncbroker.system.exceptions.ApiErrorResponse;
import ro.mycode.solarsyncbroker.system.exceptions.GlobalExceptionHandler;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyExistsException;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private void assertResponse(ResponseEntity<ApiErrorResponse> res, HttpStatus expected, String message) {
        assertThat(res.getStatusCode()).isEqualTo(expected);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(res.getStatusCode().value());
        assertThat(res.getBody().message()).isEqualTo(message);
    }

    @Test
    void userNotFound_returns404() {
        assertResponse(handler.handleNotFoundExceptions(new UserNotFoundException()),
                HttpStatus.NOT_FOUND, ErrorConstants.USER_NOT_FOUND);
    }

    @Test
    void houseNotFound_returns404() {
        assertResponse(handler.handleNotFoundExceptions(new HouseNotFoundException()),
                HttpStatus.NOT_FOUND, ErrorConstants.HOUSE_NOT_FOUND);
    }

    @Test
    void batteryNotFound_returns404() {
        assertResponse(handler.handleNotFoundExceptions(new BatteryNotFoundException()),
                HttpStatus.NOT_FOUND, ErrorConstants.BATTERY_NOT_FOUND);
    }

    @Test
    void financialTransactionNotFound_returns404() {
        assertResponse(handler.handleNotFoundExceptions(new FinancialTransactionNotFoundException()),
                HttpStatus.NOT_FOUND, ErrorConstants.FINANCIAL_TRANSACTION_NOT_FOUND);
    }

    @Test
    void userAlreadyExists_returns409() {
        assertResponse(handler.handleAlreadyExistsExceptions(new UserAlreadyExistsException()),
                HttpStatus.CONFLICT, ErrorConstants.USER_ALREADY_EXISTS);
    }

    @Test
    void houseAlreadyExists_returns409() {
        assertResponse(handler.handleAlreadyExistsExceptions(new HouseAlreadyExistsException()),
                HttpStatus.CONFLICT, ErrorConstants.HOUSE_ALREADY_EXISTS);
    }

    @Test
    void batteryAlreadyExists_returns409() {
        assertResponse(handler.handleAlreadyExistsExceptions(new BatteryAlreadyExistsException()),
                HttpStatus.CONFLICT, ErrorConstants.BATTERY_ALREADY_EXISTS);
    }

    @Test
    void accessDenied_returns403() {
        assertResponse(handler.handleAccessDeniedExceptions(new AccessDeniedExceptions()),
                HttpStatus.FORBIDDEN, ErrorConstants.ACCESS_DENIED);
    }

    @Test
    void houseAccessDenied_returns403() {
        assertResponse(handler.handleAccessDeniedExceptions(new HouseAccessDeniedException()),
                HttpStatus.FORBIDDEN, ErrorConstants.HOUSE_ACCESS_DENIED);
    }

    @Test
    void batteryPowerLimit_returns400() {
        assertResponse(handler.handleBadRequestExceptions(new BatteryPowerLimitException()),
                HttpStatus.BAD_REQUEST, ErrorConstants.BATTERY_POWER_LIMIT);
    }

    @Test
    void batterySocLimit_returns400() {
        assertResponse(handler.handleBadRequestExceptions(new BatterySocLimitException()),
                HttpStatus.BAD_REQUEST, ErrorConstants.BATTERY_SOC_LIMIT);
    }
}
