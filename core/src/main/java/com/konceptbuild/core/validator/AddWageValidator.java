package com.konceptbuild.core.validator;

import com.konceptbuild.core.request.AddWageRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddWageValidator implements ConstraintValidator<ValidWage, AddWageRequest> {
    @Override
    public boolean isValid(AddWageRequest request, ConstraintValidatorContext context) {
        return true;
    }
}
