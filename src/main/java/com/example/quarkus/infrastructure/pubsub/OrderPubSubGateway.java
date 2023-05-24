package com.example.quarkus.infrastructure.pubsub;

import com.example.quarkus.domain.model.Order;
import om.cmd.create_order.Item;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderPubSubGateway extends PubSubGateway<Order, om.cmd.create_order.Order, OrderPubSubPublisherImpl> {
    @Override
    protected om.cmd.create_order.Order convertToAvro(Order content) {

        List<Item> items = content.getOrderItems().stream()
                .map(it -> new Item(it.getProductId().toString(), it.getPrice().floatValue(), it.getQuantity()))
                .collect(Collectors.toList());

        return new om.cmd.create_order.Order(content.getCustomerId().toString(), content.getDiscount().floatValue(),
                content.getFreightValue().floatValue(), items);
    }

    @Override
    protected Map<String, String> getMessageAttributes() {
        return new HashMap<>();
    }
}
