package com.example.printforms.service.error;

import lombok.Data;

@Data
public class PrintedFormsException extends Exception{
    public PrintedFormsException() {}
    private String exception;
    public PrintedFormsException(String message, String exception)
    {
        super(message);
        this.exception = exception;
    }
}
