package com.finance.moneyowl.exceptions;

public class ResourceNotFoundException extends MoneyowlApplicationException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
