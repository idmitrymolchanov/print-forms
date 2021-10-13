package com.example.printforms.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultAdvice {
    @ExceptionHandler(PrintedFormsException.class)
    public ResponseEntity<ErrorResponseEntity> handleFileNotFoundException(PrintedFormsException e) {
        ErrorResponseEntity response = new ErrorResponseEntity(e.getException(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
