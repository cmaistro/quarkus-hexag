package com.example.quarkus.infrastructure.pubsub.repository;

import com.example.quarkus.infrastructure.pubsub.PubSubTransactionalOutboxRepository;
import com.example.quarkus.infrastructure.pubsub.repository.entities.PubSubMessageEntity;
import com.example.quarkus.infrastructure.pubsub.repository.entities.enums.SendingStatus;
import com.google.pubsub.v1.PubsubMessage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PubSubMessageRepository
        implements PubSubTransactionalOutboxRepository, PanacheRepository<PubSubMessageEntity> {
    @Override
    public void persistMessage(String projectId, String topicId, UUID messageID, PubsubMessage message) {
        PubSubMessageEntity messageEntity = PubSubMessageEntity.builder().withId(messageID)
                .withSendingStatus(SendingStatus.PENDING).withProjectId(projectId).withTopicId(topicId)
                .withMessageData(message.toByteArray()).build();

        this.persist(messageEntity);
    }
}
