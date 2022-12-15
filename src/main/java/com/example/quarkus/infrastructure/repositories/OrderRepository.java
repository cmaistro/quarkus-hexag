package com.example.quarkus.infrastructure.repositories;

import com.example.quarkus.infrastructure.repositories.entities.OrderEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<OrderEntity> {

}
