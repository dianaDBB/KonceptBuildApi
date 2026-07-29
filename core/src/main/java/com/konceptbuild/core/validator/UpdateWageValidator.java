package com.konceptbuild.core.validator;

import com.konceptbuild.core.request.UpdateWageRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateWageValidator implements ConstraintValidator<ValidWage, UpdateWageRequest> {
    @Override
    public boolean isValid(UpdateWageRequest request, ConstraintValidatorContext context) {
        return true;
    }
}
