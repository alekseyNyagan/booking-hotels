package com.aleksey.booking.hotels.annotation;

import com.aleksey.booking.hotels.validator.MarkSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MarkSizeValidator.class)
public @interface MarkSize {
    String message() default "Оценка может быть от 1 до 5!";
    int min() default 1;
    int max() default 5;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}