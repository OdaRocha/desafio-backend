package com.simplesdental.product.exception;

import br.com.fluentvalidator.context.Error;
import br.com.fluentvalidator.context.ValidationResult;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ValidationException extends RuntimeException {
    private final Collection<Error> errors;

    public ValidationException(ValidationResult validationResult) {
        super("Falha na validacao!");
        this.errors = validationResult.getErrors();
    }

}
