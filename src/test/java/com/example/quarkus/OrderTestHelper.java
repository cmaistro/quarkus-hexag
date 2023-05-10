package com.example.quarkus;

import com.example.quarkus.application.http.dto.OrderDto;
import com.example.quarkus.application.http.dto.OrderItemDto;
import com.example.quarkus.domain.model.Order;
import com.example.quarkus.domain.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderTestHelper {

    public static Order createUnsavedTestOrderFactory() {

        return new Order(null, null, UUID.randomUUID(),
                List.of(new OrderItem(1L, UUID.randomUUID(), BigDecimal.valueOf(150), 1L)), BigDecimal.valueOf(10),
                BigDecimal.valueOf(20));

    }

    public static OrderDto createNewTestOrderDtoFactory() {
        var orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(UUID.randomUUID());
        orderItemDto.setPrice(BigDecimal.valueOf(25.50));
        orderItemDto.setQuantity(2);

        var orderDto = new OrderDto();
        orderDto.setCustomerId(UUID.randomUUID());
        orderDto.setItems(List.of(orderItemDto));
        orderDto.setDiscount(BigDecimal.valueOf(2.00));
        orderDto.setFreightValue(BigDecimal.valueOf(10.00));

        return orderDto;

    }

}
