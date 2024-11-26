package com.aleksey.booking.hotels.validator;

import com.aleksey.booking.hotels.annotation.MarkSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MarkSizeValidator implements ConstraintValidator<MarkSize, Byte> {

    private int min;
    private int max;

    @Override
    public void initialize(MarkSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Byte aByte, ConstraintValidatorContext constraintValidatorContext) {
        return aByte >= min && aByte <= max;
    }
}