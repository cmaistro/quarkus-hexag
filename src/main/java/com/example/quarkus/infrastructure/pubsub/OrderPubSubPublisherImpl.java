package com.example.quarkus.infrastructure.pubsub;

import om.cmd.create_order.Order;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderPubSubPublisherImpl extends PubSubPublisherImpl<Order> {
    @Override
    public String getProjectId() {
        return "testing";
    }

    @Override
    public String getTopicId() {
        return "order.fct.order_created";
    }

}
