package com.example.quarkus.application.http;

import com.example.quarkus.application.http.dto.OrderDto;
import com.example.quarkus.application.http.mappers.OrderMapper;
import com.example.quarkus.domain.model.Order;
import com.example.quarkus.domain.services.OrderService;
import org.jboss.logging.Logger;

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

    Logger logger = Logger.getLogger(OrdersResource.class);

    @Override
    public Response createOrder(OrderDto orderDto) {

        logger.info("Starting createOrder execution");

        Order order = orderMapper.toDomain(orderDto);

        order = orderService.createOrder(order);

        logger.info("Returning from createOrder execution");

        return Response
                .created(UriBuilder.fromUri(uriInfo.getRequestUri()).path(order.getExternalId().toString()).build())
                .build();

    }

    @Override
    public Response getOrderById(UUID orderId) {
        return null;
    }
}
