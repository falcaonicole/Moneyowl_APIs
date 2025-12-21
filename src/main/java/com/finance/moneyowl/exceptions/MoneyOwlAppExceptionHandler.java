package com.finance.moneyowl.exceptions;

import com.finance.moneyowl.generatedmodels.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class MoneyOwlAppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MoneyowlApplicationException.class)
    public ResponseEntity<Error> handleMoneyOwlException(MoneyowlApplicationException e) {
        return new ResponseEntity<>(new Error(INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getErrorMessage()), INTERNAL_SERVER_ERROR);
    }
}

