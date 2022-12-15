package com.example.quarkus.domain.services.validators;

import com.example.quarkus.OrderTestHelper;
import com.example.quarkus.domain.exceptions.ValidatorException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class MinimumItemsValidatorTest {

    @Inject
    MinimumItemsValidator minimumItemsValidator;

    @Test
    void shouldValidateMinimumItemsQuantitySuccessfully() {
        var order = OrderTestHelper.createUnsavedTestOrderFactory();

        minimumItemsValidator.validate(order);

    }

    @Test
    void shouldValidateDiscountAboveLimitSuccessfullyThrowingAnException() {
        var order = OrderTestHelper.createUnsavedTestOrderFactory();
        order.setOrderItems(new ArrayList<>());

        assertThrows(ValidatorException.class, (() -> minimumItemsValidator.validate(order)));

    }

}