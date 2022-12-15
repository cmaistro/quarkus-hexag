package com.example.quarkus.domain.services;

import com.example.quarkus.domain.Order;

public interface OrderValidator {

    void validate(Order order);

}
