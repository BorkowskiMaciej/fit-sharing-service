package com.fitsharingapp.application.error;

import com.fitsharingapp.common.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }

}
