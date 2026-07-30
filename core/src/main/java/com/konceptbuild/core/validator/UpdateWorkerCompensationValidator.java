package com.konceptbuild.core.validator;

import com.konceptbuild.core.request.UpdateWorkerCompensationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateWorkerCompensationValidator implements ConstraintValidator<ValidWorker,
        UpdateWorkerCompensationRequest> {
    @Override
    public boolean isValid(UpdateWorkerCompensationRequest request, ConstraintValidatorContext context) {
        if (request.defaultHours() < 0) {
            context.buildConstraintViolationWithTemplate("Default hours must be >= 0")
                    .addPropertyNode("defaultHours")
                    .addConstraintViolation();

            return false;
        }

        if ((request.hourRate() != null) && (request.monthlySalary() != null || request.tsu() != null || request.mealAllowance() != null || request.accidentInsurance() != null)) {
            context.buildConstraintViolationWithTemplate("If hour rate is defined, then salary, TSU, meal allowance " +
                            "and accident insurance cannot be provided. Or vice-versa.")
                    .addPropertyNode("hourRate")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
