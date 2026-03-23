package com.finance.moneyowl.exceptions;

import java.text.MessageFormat;

public class UnAuthorisedException extends MoneyowlApplicationException {
    public UnAuthorisedException(String message) {
        super(message);
    }

    public UnAuthorisedException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

}
