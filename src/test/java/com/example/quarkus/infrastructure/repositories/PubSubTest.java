package com.example.quarkus.infrastructure.repositories;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static cucumber.steps.OrderSteps.channelProvider;
import static org.aesh.readline.terminal.Key.m;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class PubSubTest {

    String projectId;
    ManagedChannel channel;

    @Rule
    // emulatorContainer {
    public PubSubEmulatorContainer emulator = new PubSubEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:380.0.0-emulators"));

    // }

    public void preparePubSubContainerTopic(String projectId, String topicId) throws IOException {

        this.projectId = projectId;

        String hostport = emulator.getEmulatorEndpoint();

        channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();

        TransportChannelProvider channelProvider = FixedTransportChannelProvider
                .create(GrpcTransportChannel.create(channel));
        NoCredentialsProvider credentialsProvider = NoCredentialsProvider.create();

        createTopic(topicId, channelProvider, credentialsProvider);

    }

    public void preparePubSubSubscription(String topicId) {

    }

    public void shutdown() {
        channel.shutdown();
        emulator.stop();

    // testWithEmulatorContainer {
    public void testSimple() throws IOException {

            String subscriptionId = "my-subscription-id";
            createSubscription(subscriptionId, topicId, channelProvider, credentialsProvider);


            Publisher publisher = Publisher
                    .newBuilder(TopicName.of(projectId, topicId))
                    .setChannelProvider(channelProvider)
                    .setCredentialsProvider(credentialsProvider)
                    .build();
            PubsubMessage message = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8("test message")).build();
            publisher.publish(message);

            SubscriberStubSettings subscriberStubSettings = SubscriberStubSettings
                    .newBuilder()
                    .setTransportChannelProvider(channelProvider)
                    .setCredentialsProvider(credentialsProvider)
                    .build();
            try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
                PullRequest pullRequest = PullRequest
                        .newBuilder()
                        .setMaxMessages(1)
                        .setSubscription(ProjectSubscriptionName.format(PROJECT_ID, subscriptionId))
                        .build();
                PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);

                assertEquals(1, pullResponse.getReceivedMessagesList().size());
                assertEquals("test message", pullResponse.getReceivedMessages(0).getMessage().getData().toStringUtf8());


            }
        } finally {
            channel.shutdown();
        }
    }

    // }

    // createTopic {
    private void createTopic(String topicId, TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider) throws IOException {
        TopicAdminSettings topicAdminSettings = TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(topicAdminSettings)) {
            TopicName topicName = TopicName.of(PROJECT_ID, topicId);
            topicAdminClient.createTopic(topicName);
        }
    }

    // }

    // createSubscription {
    private void createSubscription(String subscriptionId, String topicId, TransportChannelProvider channelProvider,
            NoCredentialsProvider credentialsProvider) throws IOException {
        SubscriptionAdminSettings subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider).setCredentialsProvider(credentialsProvider).build();
        SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings);
        SubscriptionName subscriptionName = SubscriptionName.of(PROJECT_ID, subscriptionId);
        subscriptionAdminClient.createSubscription(subscriptionName, TopicName.of(PROJECT_ID, topicId),
                PushConfig.getDefaultInstance(), 10);
    }
    // }

}