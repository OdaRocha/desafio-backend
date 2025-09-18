package com.simplesdental.product.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.simplesdental.product.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static br.com.fluentvalidator.predicate.StringPredicate.*;
import static java.util.function.Predicate.not;

@Component
public class LoginValidator extends AbstractValidator<LoginRequest>  {

    final int MIN_PASSWORD_LENGTH = 5;
    final int MAX_PASSWORD_LENGTH = 101;
    final int MAX_EMAIL_LENGTH = 51;

    @Override
    public void rules() {
        ruleFor(LoginRequest::email)
                .must(not(stringEmptyOrNull()))
                .withMessage("Email é obrigatório para login")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("Email")
                .critical()
                .must(stringSizeLessThan(MAX_EMAIL_LENGTH))
                .withMessage("Email deve ter menos que 50 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("Email");

        ruleFor(LoginRequest::password)
                .must(not(stringEmptyOrNull()))
                .withMessage("Senha é obrigatória para o login")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("Password")
                .critical()
                .must(stringSizeLessThan(MAX_PASSWORD_LENGTH))
                .withMessage("Senha deve ter menos que 100 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("Password")
                .must(stringSizeGreaterThan(MIN_PASSWORD_LENGTH))
                .withMessage("Senha deve ter mais que 6 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("Password");
    }
}
