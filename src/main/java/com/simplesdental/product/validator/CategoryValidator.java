package com.simplesdental.product.validator;

import br.com.fluentvalidator.AbstractValidator;
import com.simplesdental.product.dto.CategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeLessThan;
import static java.util.function.Predicate.not;

@Component
public class CategoryValidator extends AbstractValidator<CategoryDTO> {

    @Override
    public void rules() {
        ruleFor(CategoryDTO::name)
                .must(not(stringEmptyOrNull()))
                .withMessage("Nome é obrigatório")
                .withFieldName("name")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .critical()
                .must(stringSizeLessThan(101))
                .withMessage("Nome deve ter menos que 100 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("name");

        ruleFor(CategoryDTO::description)
                .must(stringSizeLessThan(256))
                .when(not(stringEmptyOrNull()))
                .withMessage("Descrição deve ter menos que 255 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("description");
    }
}
