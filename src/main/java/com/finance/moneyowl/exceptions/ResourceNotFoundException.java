package com.finance.moneyowl.exceptions;

import java.text.MessageFormat;

public class ResourceNotFoundException extends MoneyowlApplicationException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

}
