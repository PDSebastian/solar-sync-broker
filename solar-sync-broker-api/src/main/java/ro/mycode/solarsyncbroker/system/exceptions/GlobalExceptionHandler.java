package ro.mycode.solarsyncbroker.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.mycode.solarsyncbroker.battery.exceptions.AccessDeniedExceptions;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryAlreadyExistsException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryNotFoundException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatteryPowerLimitException;
import ro.mycode.solarsyncbroker.battery.exceptions.BatterySocLimitException;
import ro.mycode.solarsyncbroker.financial.exceptions.FinancialTransactionNotFoundException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAccessDeniedException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseAlreadyExistsException;
import ro.mycode.solarsyncbroker.house.exceptions.HouseNotFoundException;
import ro.mycode.solarsyncbroker.users.exceptions.UserAlreadyExistsException;
import ro.mycode.solarsyncbroker.users.exceptions.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            BatteryNotFoundException.class,
            HouseNotFoundException.class,
            FinancialTransactionNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            BatteryAlreadyExistsException.class,
            HouseAlreadyExistsException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAlreadyExistsExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({
            AccessDeniedExceptions.class,
            HouseAccessDeniedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({
            BatteryPowerLimitException.class,
            BatterySocLimitException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e);
    }

    // Statusul din body si cel din raspunsul HTTP vin din acelasi HttpStatus.
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, RuntimeException e) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .message(e.getMessage())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
