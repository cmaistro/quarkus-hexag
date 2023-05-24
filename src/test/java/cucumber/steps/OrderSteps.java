package cucumber.steps;

import com.example.quarkus.application.http.OrdersResource;
import com.example.quarkus.application.http.dto.OrderDto;
import com.example.quarkus.application.http.dto.OrderItemDto;
import com.example.quarkus.domain.model.OrderItem;
import com.example.quarkus.infrastructure.repositories.OrderRepository;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.net.impl.ChannelProvider;
import om.cmd.create_order.Order;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    static TransportChannelProvider channelProvider;
    static NoCredentialsProvider credentialsProvider;

    public static void setUp() throws IOException {

        System.out.println("Starting PubSub Container...");

        emulator = new PubSubEmulatorContainer(
                DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:380.0.0-emulators"));

        emulator.start();

        String hostport = emulator.getEmulatorEndpoint();

        System.out.println("Pubsub emulator: " + hostport);

        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();

        channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

        credentialsProvider = NoCredentialsProvider.create();

        createTopic("order.fct.order_created", channelProvider, credentialsProvider);

        System.out.println("Topic and subscription created");

    }

    @Before
    public void startUp() throws IOException {
        if (emulator == null)
            setUp();
        orderDto = null;
    }

    private static void createTopic(String topicId, TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider) throws IOException {
        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings)) {
            TopicName topicName = TopicName.of("testing", topicId);
            topicAdminClient.createTopic(topicName);
            createSubscription("subscription", topicId, channelProvider, credentialsProvider);
        }
    }

    private static void createSubscription(String subscriptionId, String topicId,
            TransportChannelProvider channelProvider, NoCredentialsProvider credentialsProvider) throws IOException {
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings);
        SubscriptionName subscriptionName = SubscriptionName.of("testing", subscriptionId);
        subscriptionAdminClient.createSubscription(subscriptionName, TopicName.of("testing", topicId),
                PushConfig.getDefaultInstance(), 10);
    }

    private PubsubMessage consumePullMessage() throws IOException {

        SubscriberStubSettings subscriberStubSettings = SubscriberStubSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();

        try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
            PullRequest pullRequest = PullRequest.newBuilder().setMaxMessages(1)
                    .setSubscription(ProjectSubscriptionName.format("testing", "subscription")).build();
            PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);

            return pullResponse.getReceivedMessages(0).getMessage();

        }
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

    @And("Publish an event with this order")
    public void publishAnEventWithThisOrder() throws IOException {
        var event = this.consumePullMessage();
        var order = deseriliazeMessage(event);

        assertEquals(orderDto.getCustomerId().toString(), order.getCustomerId().toString());

    }

    private Order deseriliazeMessage(PubsubMessage message) throws IOException {
        ByteString data = message.getData();

        // Get the schema encoding type.
        String encoding = message.getAttributesMap().get("googclient_schemaencoding");

        // Send the message data to a byte[] input stream.
        InputStream inputStream = new ByteArrayInputStream(data.toByteArray());

        Decoder decoder = null;

        // Prepare an appropriate decoder for the message data in the input stream
        // based on the schema encoding type.
        decoder = DecoderFactory.get().directBinaryDecoder(inputStream, /* reuse= */ null);
        System.out.println("Receiving a binary-encoded message:");

        SpecificDatumReader<Order> reader = new SpecificDatumReader<>(Order.getClassSchema());
        // Obtain an object of the generated Avro class using the decoder.
        Order order = reader.read(null, decoder);
        return order;
    }
}
