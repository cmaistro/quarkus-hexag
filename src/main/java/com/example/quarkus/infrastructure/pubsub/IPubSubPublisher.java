package com.example.quarkus.infrastructure.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;
import java.util.UUID;

public interface IPubSubPublisher<T> {
    String getProjectId();

    String getTopicId();

    Publisher getPublisher() throws IOException;

    void publishMessage(PubsubMessage message, UUID messageId) throws IOException;
}
