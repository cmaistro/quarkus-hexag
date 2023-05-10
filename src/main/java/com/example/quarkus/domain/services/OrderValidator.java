package com.example.quarkus.domain.services;

import com.example.quarkus.domain.model.Order;

public interface OrderValidator {

    void validate(Order order);

}
