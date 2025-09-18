package com.simplesdental.product.validator;


import br.com.fluentvalidator.AbstractValidator;
import com.simplesdental.product.dto.ProductV2DTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.fluentvalidator.predicate.ComparablePredicate.greaterThan;
import static br.com.fluentvalidator.predicate.ObjectPredicate.nullValue;
import static br.com.fluentvalidator.predicate.StringPredicate.stringEmptyOrNull;
import static br.com.fluentvalidator.predicate.StringPredicate.stringSizeLessThan;
import static java.util.function.Predicate.not;

@Component
public class ProductValidator extends AbstractValidator<ProductV2DTO> {

    @Override
    public void rules() {
        ruleFor(ProductV2DTO::getName)
                .must(not(stringEmptyOrNull()))
                .withMessage("Nome é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("name")
                .critical()
                .must(stringSizeLessThan(101))
                .withMessage("Nome deve ter menos que 100 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("name");

        ruleFor(ProductV2DTO::getDescription)
                .must(stringSizeLessThan(256))
                .when(not(stringEmptyOrNull()))
                .withMessage("Descrição deve ter menos que 255 caracteres")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("description");

        ruleFor(ProductV2DTO::getPrice)
                .must(not(nullValue()))
                .withMessage("Preço é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("price")
                .critical()
                .must(greaterThan(BigDecimal.ZERO))
                .withMessage("Preço deve ser maior que zero")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("price");

        ruleFor(ProductV2DTO::getStatus)
                .must(not(nullValue()))
                .withMessage("Status é obrigatório")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("status");

        ruleFor(ProductV2DTO::getCategoryId)
                .must(not(nullValue()))
                .withMessage("Categoria é obrigatória")
                .withCode(HttpStatus.BAD_REQUEST.name())
                .withFieldName("category");
    }
}
