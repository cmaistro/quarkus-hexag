package com.example.quarkus.domain.services.validators;

import com.example.quarkus.domain.Order;
import com.example.quarkus.domain.exceptions.ValidatorException;
import com.example.quarkus.domain.services.OrderValidator;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MinimumItemsValidator implements OrderValidator {

    private static Long minimumCountItems = 1L;
    private Logger logger = Logger.getLogger(MaximumDiscountValidator.class);

    @Override
    public void validate(Order order) {

        logger.info(String.format("Validating Order: orderCountItems: %s, minimumCountItems: %s",
                order.getOrderItems().size(), minimumCountItems));
        if (order.getOrderItems().size() < minimumCountItems) {
            throw new ValidatorException(
                    String.format("This order not contains the minimum count items (%s)", minimumCountItems), "ERR-01",
                    "OrderItems");
        }
    }
}
