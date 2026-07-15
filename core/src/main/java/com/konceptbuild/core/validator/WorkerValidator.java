package com.konceptbuild.core.validator;

import com.konceptbuild.core.dto.WorkerContractType;
import com.konceptbuild.core.dto.Status;
import com.konceptbuild.core.request.WorkerRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WorkerValidator implements ConstraintValidator<ValidWorker, WorkerRequest> {
    @Override
    public boolean isValid(WorkerRequest request, ConstraintValidatorContext context) {
        if (request.defaultHours() < 0) {
            context.buildConstraintViolationWithTemplate("DEFAULT_HOURS and must be >= 0")
                    .addPropertyNode("defaultHours")
                    .addConstraintViolation();

            return false;
        }

        if (request.workerContractType() == WorkerContractType.CONTRACTOR && (request.hourRate() == null || request.hourRate() <= 0)) {
            context.buildConstraintViolationWithTemplate("For contractor workers, HOUR_RATE must be > 0")
                    .addPropertyNode("hourRate")
                    .addConstraintViolation();

            return false;
        }

        if (request.workerContractType() == WorkerContractType.INTERNAL && (request.monthlySalary() == null || request.monthlySalary() <= 0)) {
            context.buildConstraintViolationWithTemplate("For internal workers, MONTHLY_SALARY must be > 0")
                    .addPropertyNode("monthlySalary")
                    .addConstraintViolation();

            return false;
        }

        if (request.workerContractType() == WorkerContractType.INTERNAL && (request.tsu() == null || request.tsu() <= 0)) {
            context.buildConstraintViolationWithTemplate("For internal workers, TSU must be > 0")
                    .addPropertyNode("tsu")
                    .addConstraintViolation();

            return false;
        }

        if (request.workerContractType() == WorkerContractType.INTERNAL && (request.mealAllowance() == null || request.mealAllowance() < 0)) {
            context.buildConstraintViolationWithTemplate("For internal workers, MEAL_ALLOWANCE must be >= 0")
                    .addPropertyNode("mealAllowance")
                    .addConstraintViolation();

            return false;
        }

        if (request.workerContractType() == WorkerContractType.INTERNAL && (request.accidentInsurance() == null || request.accidentInsurance() < 0)) {
            context.buildConstraintViolationWithTemplate("For internal workers, ACCIDENT_INSURANCE must be >= 0")
                    .addPropertyNode("accidentInsurance")
                    .addConstraintViolation();

            return false;
        }

        if (request.status() == Status.INACTIVE && request.endDate() == null) {
            context.buildConstraintViolationWithTemplate("If worker is inactive, END_DATE must be provided")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        if (request.status() == Status.ACTIVE && request.endDate() != null) {
            context.buildConstraintViolationWithTemplate("If user is active, END_DATE cannot be provided")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        if (request.endDate() != null && request.startDate().isAfter(request.endDate())) {
            context.buildConstraintViolationWithTemplate("START_DATE cannot be after END_DATE")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
