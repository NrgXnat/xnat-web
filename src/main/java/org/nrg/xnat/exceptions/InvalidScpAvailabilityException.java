package org.nrg.xnat.exceptions;

import org.nrg.xapi.exceptions.XapiException;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class InvalidScpAvailabilityException extends XapiException {
    public InvalidScpAvailabilityException(final String s) {
        super(BAD_REQUEST, "\"" + s + "\" is not a valid availability");
    }
}
