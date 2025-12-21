package com.finance.moneyowl.exceptions;

import lombok.Getter;

@Getter
public class MoneyowlApplicationException extends RuntimeException {
    private String errorKey;
    private String errorMessage;

    public MoneyowlApplicationException(String errorKey, String errorMessage) {
        super(errorMessage);
        this.errorKey = errorKey;
        this.errorMessage = errorMessage;
    }

    public MoneyowlApplicationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
