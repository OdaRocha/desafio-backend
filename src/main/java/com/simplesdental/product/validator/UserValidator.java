package com.simplesdental.product.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.simplesdental.product.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeLessThan;
import static java.util.function.Predicate.not;

@Component
public class UserValidator extends AbstractValidator<UserDTO> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    static final int MAX_NAME_LENGTH = 101;
    static final int MAX_EMAIL_LENGTH = 51;
    static final int MAX_PASSWORD_LENGTH = 101;


    @Override
    public void rules() {
        ruleFor(UserDTO::getName)
                .must(not(stringEmptyOrNull()))
                .withMessage("Nome é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("name")
                .critical()
                .must(stringSizeLessThan(MAX_NAME_LENGTH))
                .withMessage("Nome deve ter menos que 100 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("name");

        ruleFor(UserDTO::getEmail)
                .must(not(stringEmptyOrNull()))
                .withMessage("Email é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("email")
                .critical()
                .must(stringSizeLessThan(MAX_EMAIL_LENGTH))
                .withMessage("Email deve ter menos que 50 caracteres")
                .withFieldName("email")
                .critical()
                .must(email -> EMAIL_PATTERN.matcher(email).matches())
                .withMessage("Email inválido")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("email");

        ruleFor(UserDTO::getPassword)
                .must(not(stringEmptyOrNull()))
                .withMessage("Password é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("password")
                .critical()
                .must(stringSizeLessThan(MAX_PASSWORD_LENGTH))
                .withMessage("Password deve ter menos que 100 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("password");

        ruleFor(UserDTO::getRole)
                .must(Objects::nonNull)
                .withMessage("Role é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("role");

    }
}
