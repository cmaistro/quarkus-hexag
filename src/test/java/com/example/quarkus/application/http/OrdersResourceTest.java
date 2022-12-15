package com.example.quarkus.application.http;

import com.example.quarkus.OrderTestHelper;
import com.example.quarkus.application.http.mappers.OrderMapper;
import com.example.quarkus.domain.Order;
import com.example.quarkus.domain.services.OrderService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(OrdersResource.class)
class OrdersResourceTest {

    @InjectMock
    private OrderService orderServiceMock;

    @Inject
    private OrderMapper orderMapper;

    @Test
    void shouldSaveOrderSuccessfully() {
        var requestOrder = OrderTestHelper.createNewTestOrderDtoFactory();
        var responseOrder = orderMapper.toDomain(requestOrder);
        responseOrder.setId(20L);

        when(orderServiceMock.createOrder(isA(Order.class))).thenAnswer(response -> responseOrder);

        given().contentType(ContentType.JSON).body(requestOrder).when().post().then().statusCode(HttpStatus.SC_CREATED);

    }

}