package com.example.quarkus.infrastructure.repositories.mappers;

import com.example.quarkus.domain.model.Order;
import com.example.quarkus.domain.model.OrderItem;
import com.example.quarkus.infrastructure.repositories.entities.OrderEntity;
import com.example.quarkus.infrastructure.repositories.entities.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface OrderMapper {

    OrderEntity toEntity(Order order);

    Order toDomain(OrderEntity orderEntity);

    OrderItemEntity toEntity(OrderItem orderItem);

    OrderItem toDomain(OrderItemEntity orderItemEntity);
}
