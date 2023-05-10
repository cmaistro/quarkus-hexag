package com.example.quarkus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private UUID externalId;
    private UUID customerId;
    public List<OrderItem> orderItems;
    public BigDecimal discount;
    public BigDecimal freightValue;

    public BigDecimal getTotalItemsValue() {
        return orderItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOrderValue() {
        return this.getTotalItemsValue().add(this.freightValue).subtract(this.discount);
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", externalId=" + externalId + ", customerId=" + customerId + ", orderItems="
                + orderItems + ", discount=" + discount + ", freightValue=" + freightValue + '}';
    }
}
