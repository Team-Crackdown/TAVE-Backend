package com.tave.exception;

import lombok.Getter;

public class BusinessLogicException extends RuntimeException{

    private ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode){
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() { return exceptionCode; }
}
