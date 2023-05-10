package com.example.quarkus.application.http.mappers;

import com.example.quarkus.application.http.dto.OrderDto;
import com.example.quarkus.application.http.dto.OrderItemDto;
import com.example.quarkus.domain.model.Order;
import com.example.quarkus.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Mapper(componentModel = "cdi")
public interface OrderMapper {

    @Mapping(source = "id", target = "externalId")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "items", target = "orderItems")
    Order toDomain(OrderDto dto);

    @Mapping(source = "externalId", target = "id")
    OrderDto toDto(Order order);

    OrderItem toDomain(OrderItemDto dto);

    OrderItemDto toDto(OrderItem orderItem);
}
