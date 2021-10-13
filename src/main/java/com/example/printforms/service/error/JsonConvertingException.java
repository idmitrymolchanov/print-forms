package com.example.printforms.service.error;

public class JsonConvertingException extends Exception{
    // Parameterless Constructor
    public JsonConvertingException() {}

    // Constructor that accepts a message
    public JsonConvertingException(String message)
    {
        super(message);
    }

}
