package com.example.quarkus.infrastructure.pubsub;

import com.example.quarkus.domain.model.Order;
import om.cmd.create_order.Item;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderPubSubGateway extends PubSubGateway<Order, om.cmd.create_order.Order, OrderPubSubPublisher> {
    @Override
    protected om.cmd.create_order.Order convertToAvro(Order content) {

        List<Item> items = content.getOrderItems().stream()
                .map(it -> new Item(it.getProductId().toString(), it.getPrice().floatValue(), it.getQuantity()))
                .collect(Collectors.toList());

        om.cmd.create_order.Order dto = new om.cmd.create_order.Order(content.getCustomerId().toString(),
                content.getDiscount().floatValue(), content.getFreightValue().floatValue(), items);

        return dto;
    }

    @Override
    protected Map<String, String> getMessageAttributes() {
        return null;
    }
}
