package com.fitsharingapp.application.common.validator;

import com.fitsharingapp.common.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.fitsharingapp.common.ErrorCode.INVALID_DATA;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw ServiceException.withFormattedMessage(INVALID_DATA, violations);
        }
    }

}
