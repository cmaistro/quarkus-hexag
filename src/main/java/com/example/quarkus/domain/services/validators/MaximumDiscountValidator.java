package com.example.quarkus.domain.services.validators;

import com.example.quarkus.domain.Order;
import com.example.quarkus.domain.exceptions.ValidatorException;
import com.example.quarkus.domain.services.OrderValidator;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class MaximumDiscountValidator implements OrderValidator {

    private static BigDecimal maximumDiscount = BigDecimal.valueOf(10);

    private Logger logger = Logger.getLogger(MaximumDiscountValidator.class);

    @Override
    public void validate(Order order) {

        logger.info(String.format("Validating Order: discount: %s, totalItemsValue: %s, maximumDiscount: %s",
                order.getDiscount(), order.getTotalItemsValue(), maximumDiscount));

        if (order.getTotalItemsValue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentageDiscount = (order.getDiscount().divide(order.getTotalItemsValue(), 4, RoundingMode.UP)
                    .multiply(BigDecimal.valueOf(100)));
            logger.info(String.format("Percent calculated discount value: %s", percentageDiscount));
            if (percentageDiscount.compareTo(maximumDiscount) > 0) {
                throw new ValidatorException(String.format("Discount value is above the maximum allowed (%s)",
                        order.getTotalItemsValue().multiply(maximumDiscount)), "ERR-02", "discount");
            }
        }
    }
}
