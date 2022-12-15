package com.example.quarkus.domain.services;

import com.example.quarkus.OrderTestHelper;
import com.example.quarkus.domain.Order;
import com.example.quarkus.domain.exceptions.ValidatorException;
import com.example.quarkus.domain.repositories.OrderDataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class OrderServiceTest {

    @Inject
    private OrderService orderService;

    @InjectMock
    private OrderDataSource orderDataSourceMock;

    @Test
    void shouldCreateAnOrderSuccessfully() {

        var order = OrderTestHelper.createUnsavedTestOrderFactory();
        var savedOrder = OrderTestHelper.createUnsavedTestOrderFactory();
        savedOrder.setId(20L);
        when(orderDataSourceMock.saveOrder(isA(Order.class))).thenAnswer(response -> savedOrder);

        var responseOrder = orderService.createOrder(order);

        verify(orderDataSourceMock, times(1)).saveOrder(order);
        assertEquals(20L, responseOrder.getId());
    }

    @Test
    void shouldNotCreateAnOrderWithValidationErrors() {

        var order = OrderTestHelper.createUnsavedTestOrderFactory();
        order.setOrderItems(new ArrayList<>());
        when(orderDataSourceMock.saveOrder(isA(Order.class))).thenAnswer(response -> order);

        assertThrows(ValidatorException.class, () -> orderService.createOrder(order));

    }

}