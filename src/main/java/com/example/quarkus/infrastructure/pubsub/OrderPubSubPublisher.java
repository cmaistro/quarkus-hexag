package com.example.quarkus.infrastructure.pubsub;

import com.example.quarkus.domain.model.Order;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderPubSubPublisher extends PubSubPublisher<Order> {
    @Override
    public String getProjectId() {
        return "testing";
    }

    @Override
    public String getTopicId() {
        return "order.fct.order_created";
    }

}
