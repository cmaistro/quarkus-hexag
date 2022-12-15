package com.example.quarkus.application.http;

import com.example.quarkus.application.http.dto.OrderDto;
import com.example.quarkus.application.http.mappers.OrderMapper;
import com.example.quarkus.domain.Order;
import com.example.quarkus.domain.services.OrderService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.UUID;

@RequestScoped
public class OrdersResource implements OrdersApi {

    @Inject
    OrderMapper orderMapper;

    @Inject
    OrderService orderService;

    @Context
    private UriInfo uriInfo;
    @Context
    HttpHeaders headers;

    @Override
    public Response createOrder(OrderDto orderDto) {
        Order order = orderMapper.toDomain(orderDto);

        order = orderService.createOrder(order);

        return Response.created(UriBuilder.fromUri(uriInfo.getRequestUri()).build()).build();
    }

    @Override
    public Response getOrderById(UUID orderId) {
        return null;
    }
}
