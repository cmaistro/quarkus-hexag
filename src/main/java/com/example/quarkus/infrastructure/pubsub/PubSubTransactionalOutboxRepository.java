package com.example.quarkus.infrastructure.pubsub;

import com.google.pubsub.v1.PubsubMessage;

import java.util.UUID;

public interface PubSubTransactionalOutboxRepository {

    void persistMessage(String projectId, String topicId, UUID messageID, PubsubMessage message);

}
