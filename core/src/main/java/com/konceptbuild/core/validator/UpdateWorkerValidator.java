package com.konceptbuild.core.validator;

import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.request.UpdateWorkerRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateWorkerValidator implements ConstraintValidator<ValidWorker, UpdateWorkerRequest> {
    @Override
    public boolean isValid(UpdateWorkerRequest request, ConstraintValidatorContext context) {
        if (request.status() == Status.INACTIVE && request.endDate() == null) {
            context.buildConstraintViolationWithTemplate("If worker is inactive, end date must be provided")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        if (request.status() == Status.ACTIVE && request.endDate() != null) {
            context.buildConstraintViolationWithTemplate("If user is active, end date cannot be provided")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        if (request.endDate() != null && request.startDate().isAfter(request.endDate())) {
            context.buildConstraintViolationWithTemplate("Start date cannot be after end date")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
