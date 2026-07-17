package com.konceptbuild.core.validator;

import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.request.WorkRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WorkValidator implements ConstraintValidator<ValidWork, WorkRequest> {
    @Override
    public boolean isValid(WorkRequest request, ConstraintValidatorContext context) {
        if (request.contractedBudget() < 0) {
            context.buildConstraintViolationWithTemplate("Contracted budget must be >= 0")
                    .addPropertyNode("contractedBudget")
                    .addConstraintViolation();

            return false;
        }

        if (request.estimatedCostMaterials() < 0) {
            context.buildConstraintViolationWithTemplate("Estimated cost materials must be >= 0")
                    .addPropertyNode("estimatedCostMaterials")
                    .addConstraintViolation();

            return false;
        }

        if (request.estimatedCostLabor() < 0) {
            context.buildConstraintViolationWithTemplate("Estimated cost labor must be >= 0")
                    .addPropertyNode("estimatedCostLabor")
                    .addConstraintViolation();

            return false;
        }

        if (request.status() == WorkStatus.DONE && request.endDate() == null) {
            context.buildConstraintViolationWithTemplate("If status is done, end date must be provided")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        if (request.status() != WorkStatus.DONE && request.endDate() != null) {
            context.buildConstraintViolationWithTemplate("If status is not done, end date cannot be provided")
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
