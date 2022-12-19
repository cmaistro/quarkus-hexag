package com.example.quarkus.application.http;

import com.example.quarkus.OrderTestHelper;
import com.example.quarkus.application.http.mappers.OrderMapper;
import com.example.quarkus.domain.Order;
import com.example.quarkus.domain.exceptions.ValidatorException;
import com.example.quarkus.domain.services.OrderService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(OrdersResource.class)
class OrdersResourceTest {

    @InjectMock
    OrderService orderServiceMock;

    @Inject
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        Mockito.clearAllCaches();
    }

    @Test
    void shouldSaveOrderSuccessfully() {

        var requestOrder = OrderTestHelper.createNewTestOrderDtoFactory();
        var responseOrder = orderMapper.toDomain(requestOrder);
        responseOrder.setId(20L);
        responseOrder.setExternalId(UUID.randomUUID());

        Mockito.clearAllCaches();
        when(orderServiceMock.createOrder(isA(Order.class))).thenAnswer(response -> responseOrder);

        given().contentType(ContentType.JSON).body(requestOrder).when().post().then().statusCode(HttpStatus.SC_CREATED);

    }

    @Test
    void shouldReturnAValidationError() {

        var requestOrder = OrderTestHelper.createNewTestOrderDtoFactory();

        when(orderServiceMock.createOrder(isA(Order.class)))
                .thenThrow(new ValidatorException("testing message", "ERR-000", "general"));

        given().contentType(ContentType.JSON).body(requestOrder).when().post().then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY)
                .body("developerMessage", equalTo("Request values are invalid."));

    }

    @Test
    void shouldReturnAInternalError() {

        var requestOrder = OrderTestHelper.createNewTestOrderDtoFactory();

        when(orderServiceMock.createOrder(isA(Order.class))).thenThrow(new RuntimeException());

        given().contentType(ContentType.JSON).body(requestOrder).when().post().then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("errorCode", equalTo("ERR-999"))
                .body("message", equalTo("Unexpected error processing your request"));

    }

}