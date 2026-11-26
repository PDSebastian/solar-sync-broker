package ro.mycode.solarsyncbroker.house.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class HouseAlreadyExistsException extends RuntimeException {
    public HouseAlreadyExistsException() {
        super(ErrorConstants.HOUSE_ALREADY_EXISTS);
    }
}
