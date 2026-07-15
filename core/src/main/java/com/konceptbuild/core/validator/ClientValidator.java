package com.konceptbuild.core.validator;

import com.konceptbuild.core.request.ClientRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ClientValidator implements ConstraintValidator<ValidClient, ClientRequest> {
    @Override
    public boolean isValid(ClientRequest request, ConstraintValidatorContext context) {
        return true;
    }
}
