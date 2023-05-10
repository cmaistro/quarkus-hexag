package com.example.quarkus.domain.services;

import com.example.quarkus.domain.model.Order;
import com.example.quarkus.domain.repositories.OrderDataSource;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderDataSource orderDataSource;

    @Inject
    private Instance<OrderValidator> orderValidators;

    Logger logger = Logger.getLogger(OrderService.class);

    public Order createOrder(Order order) {

        logger.info("Starting to process order.");

        for (OrderValidator validator : orderValidators) {
            validator.validate(order);
        }

        order.setExternalId(UUID.randomUUID());

        logger.debug("Order is valid, calling output ports.");

        return orderDataSource.saveOrder(order);

    }
}
