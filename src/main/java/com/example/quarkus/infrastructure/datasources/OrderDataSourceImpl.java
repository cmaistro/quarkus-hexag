package com.example.quarkus.infrastructure.datasources;

import com.example.quarkus.domain.model.Order;
import com.example.quarkus.domain.repositories.OrderDataSource;
import com.example.quarkus.infrastructure.repositories.OrderRepository;
import com.example.quarkus.infrastructure.repositories.entities.OrderEntity;
import com.example.quarkus.infrastructure.repositories.mappers.OrderMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderDataSourceImpl implements OrderDataSource {
    @Inject
    OrderMapper orderMapper;
    @Inject
    OrderRepository orderRepository;

    Logger logger = Logger.getLogger(OrderDataSourceImpl.class);

    @Transactional
    @Override
    public Order saveOrder(Order order) {

        OrderEntity orderEntity = orderMapper.toEntity(order);

        orderRepository.persist(orderEntity);

        return orderMapper.toDomain(orderEntity);
    }
}
