package com.example.quarkus.domain.services.validators;

import com.example.quarkus.OrderTestHelper;
import com.example.quarkus.domain.exceptions.ValidatorException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class MaximumDiscountValidatorTest {

    @Inject
    MaximumDiscountValidator maximumDiscountValidator;

    @Test
    void shouldValidateDiscountBelowLimitSuccessfully() {
        var order = OrderTestHelper.createUnsavedTestOrderFactory();

        maximumDiscountValidator.validate(order);

    }

    @Test
    void shouldValidateDiscountAboveLimitSuccessfullyThrowingAnException() {
        var order = OrderTestHelper.createUnsavedTestOrderFactory();
        order.setDiscount(BigDecimal.valueOf(16));

        assertThrows(ValidatorException.class, (() -> maximumDiscountValidator.validate(order)));

    }

}