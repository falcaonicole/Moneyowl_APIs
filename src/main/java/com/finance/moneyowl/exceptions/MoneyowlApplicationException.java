package com.finance.moneyowl.exceptions;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class MoneyowlApplicationException extends RuntimeException {
    private String errorMessage;

    public MoneyowlApplicationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public MoneyowlApplicationException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }
}
