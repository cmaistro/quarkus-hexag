package com.example.quarkus.domain.repositories;

import com.example.quarkus.domain.Order;

public interface OrderDataSource {

    Order saveOrder(Order order);

}
