package com.fitsharingapp.application.common.error;

import com.fitsharingapp.common.ServiceException;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.fitsharingapp.common.ErrorCode.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ServiceException serviceException;
        if (ex instanceof ServiceException) {
            serviceException = (ServiceException) ex;
        } else if (ex instanceof HttpMessageNotReadableException) {
            serviceException = ServiceException.withFormattedMessage(INVALID_DATA, ex.getMessage());
        } else if (ex instanceof HttpMediaTypeNotSupportedException || ex instanceof InvalidMediaTypeException) {
            serviceException = ServiceException.withFormattedMessage(INVALID_CONTENT_TYPE, ex.getMessage());
        } else if (ex instanceof BadCredentialsException || ex instanceof InternalAuthenticationServiceException) {
            serviceException = new ServiceException(BAD_CREDENTIALS);
        } else if (ex instanceof MissingRequestHeaderException) {
            serviceException = ServiceException.withFormattedMessage(MISSING_HEADER, ((MissingRequestHeaderException) ex).getHeaderName());
        } else if (ex instanceof NoResourceFoundException) {
            serviceException = ServiceException.withFormattedMessage(RESOURCE_NOT_FOUND, ((NoResourceFoundException) ex).getResourcePath());
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            serviceException = new ServiceException(INVALID_UUID_IN_PATH);
        } else {
            serviceException = ServiceException.withFormattedMessage(INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return createErrorResponse(serviceException);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ServiceException ex) {
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage()));
    }

}
