package com.example.quarkus.infrastructure.repositories;

import com.example.quarkus.OrderTestHelper;
import com.example.quarkus.domain.repositories.OrderDataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class OrderDataSourceTest {

    @Inject
    OrderDataSource orderDataSource;

    @Test
    void shouldPersistOrderAndReturn() {

        var order = OrderTestHelper.createUnsavedTestOrderFactory();
        var savedOrder = orderDataSource.saveOrder(order);
        assertNotNull(savedOrder.getId());

    }

}