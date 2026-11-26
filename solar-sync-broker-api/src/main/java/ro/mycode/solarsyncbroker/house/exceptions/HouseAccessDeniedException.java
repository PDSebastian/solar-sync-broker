package ro.mycode.solarsyncbroker.house.exceptions;

import ro.mycode.solarsyncbroker.system.constants.ErrorConstants;

public class HouseAccessDeniedException extends RuntimeException {
    public HouseAccessDeniedException() {
        super(ErrorConstants.HOUSE_ACCESS_DENIED);
    }
}
