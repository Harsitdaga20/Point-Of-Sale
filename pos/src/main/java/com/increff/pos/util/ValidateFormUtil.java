package com.increff.pos.util;


import com.increff.pos.service.ApiException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

public class ValidateFormUtil {


    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static <T> void validateForm(T form) throws ApiException {
        Set<ConstraintViolation<T>> violations = validator.validate(form);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessageForField = violation.getMessage();
                errorMessage.append(fieldName).append("- ").append(errorMessageForField).append(". ");
            }
            throw new ApiException(errorMessage.toString());
        }
    }

    public static void validateDate(ZonedDateTime start,ZonedDateTime end) throws ApiException {
        if (end.isBefore(start)) {
            throw new ApiException("End date must be later than or equal to the start date");
        }
        ZonedDateTime now=ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        now=now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (end.isAfter(now)) {
            throw new ApiException("End date cannot be in the future");
        }

        if (end.isEqual(now)) {
            throw new ApiException("End date cannot be today's date");
        }
    }
}
