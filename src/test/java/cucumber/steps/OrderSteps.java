package cucumber.steps;

import com.example.quarkus.application.http.OrdersResource;
import com.example.quarkus.application.http.dto.OrderDto;
import com.example.quarkus.application.http.dto.OrderItemDto;
import com.example.quarkus.domain.model.OrderItem;
import com.example.quarkus.infrastructure.repositories.OrderRepository;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderSteps {

    @TestHTTPEndpoint(OrdersResource.class)
    @TestHTTPResource
    URL ordersEndpoint;

    OrderDto orderDto;

    Response response;

    UUID orderId;

    @Inject
    OrderRepository orderRepository;

    static PubSubEmulatorContainer emulator;

    @BeforeAll
    public static void setUp() {

        emulator = new PubSubEmulatorContainer(
                DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:380.0.0-emulators"));

    }

    @Before
    public void startUp() {
        orderDto = null;
    }

    private void createTopic(String topicId, TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider) throws IOException {
        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings)) {
            TopicName topicName = TopicName.of("testing", topicId);
            topicAdminClient.createTopic(topicName);
        }
    }

    private void createSubscription(String subscriptionId, String topicId, TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider) throws IOException {
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings);
        SubscriptionName subscriptionName = SubscriptionName.of("testing", subscriptionId);
        subscriptionAdminClient.createSubscription(subscriptionName, TopicName.of("testing", topicId),
                PushConfig.getDefaultInstance(), 10);
    }

    @io.cucumber.java.en.Given("^User wants to create an order$")
    public void userWantsToCreateAnOrder() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(UUID.randomUUID());
        orderItemDto.setPrice(BigDecimal.valueOf(154.54));
        orderItemDto.setQuantity(2);
        orderDto = new OrderDto();
        orderDto.setCustomerId(UUID.randomUUID());
        orderDto.setItems(List.of(orderItemDto));
        orderDto.setFreightValue(BigDecimal.valueOf(15.50));
    }

    @When("^User calls the create order API endpoint$")
    public void userCallsTheCreateOrderAPIEndpoint() {
        response = given().contentType(ContentType.JSON).body(orderDto).when().post("/orders").andReturn();
    }

    @Then("^User should get a successful response$")
    public void userShouldGetASuccessfulResponse() {
        assertEquals(201, response.getStatusCode());
    }

    @And("^Receive the orderId for this request$")
    public void receiveTheOrderIdForThisRequest() {
        var location = response.getHeader("location");
        orderId = UUID.fromString(location.substring(location.lastIndexOf("/") + 1));
        assertNotNull(orderId);

    }

    @And("Save the order successfully")
    public void saveTheOrderSuccessfully() {
        var savedOrder = orderRepository.find("externalId", orderId);
        assertEquals(orderDto.getCustomerId(), savedOrder.firstResult().getCustomerId());
    }

    @Then("User should get a failed response")
    public void userShouldGetAFailedResponse() {
        assertEquals(422, response.getStatusCode());
    }

    @And("The order don't have any items")
    public void theOrderDonTHaveAnyItems() {
        orderDto.setItems(new ArrayList<>());
    }

    @And("The order has a discount of {double}")
    public void theOrderHasADiscountOf(double arg0) {
        orderDto.setDiscount(BigDecimal.valueOf(arg0));
    }

    @Given("User wants to create an order with these items")
    public void userWantsToCreateAnOrderWithTheseItems(List<Map<String, String>> items) {
        orderDto = new OrderDto();
        orderDto.setCustomerId(UUID.randomUUID());
        orderDto.setFreightValue(BigDecimal.valueOf(15.50));

        var orderItemDtos = items.stream().map(item -> {
            var dto = new OrderItemDto();
            dto.setProductId(UUID.fromString(item.get("itemId")));
            dto.setQuantity(Integer.valueOf(item.get("quantity")));
            dto.setPrice(BigDecimal.valueOf(Double.valueOf(item.get("price"))));
            return dto;
        }).collect(Collectors.toList());
        orderDto.setItems(orderItemDtos);
    }

    @And("The order has a discount of <discount>")
    public void theOrderHasADiscountOfDiscount(Double discount) {
        orderDto.setDiscount(BigDecimal.valueOf(discount));
    }
}
