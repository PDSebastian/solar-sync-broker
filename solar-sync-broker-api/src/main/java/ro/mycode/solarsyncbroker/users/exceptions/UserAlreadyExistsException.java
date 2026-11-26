package ro.mycode.solarsyncbroker.users.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super(ErrorConstants.USER_ALREADY_EXISTS);
    }
}
