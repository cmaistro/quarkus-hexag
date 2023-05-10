package com.example.quarkus.infrastructure.pubsub;

import com.google.pubsub.v1.PubsubMessage;
import org.apache.avro.specific.SpecificRecordBase;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class PubSubGateway<Domain, Avro extends SpecificRecordBase, Publisher extends PubSubPublisher<Avro>> {

    @Inject
    BinaryMessageSerializer serializer;
    @Inject
    IPubSubPublisher<Avro> publisher;

    @Inject
    PubSubTransactionalOutboxRepository repository;

    public UUID sendMessage(Domain content) {

        Avro messageDto = convertToAvro(content);
        UUID messageId = UUID.randomUUID();

        try {

            PubsubMessage message = serializer.serializeMessage(messageDto, getMessageAttributes(), messageId);
            repository.persistMessage(publisher.getProjectId(), publisher.getTopicId(), messageId, message);
            publisher.publishMessage(message, messageId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return messageId;
    }

    protected abstract Avro convertToAvro(Domain content);

    protected abstract Map<String, String> getMessageAttributes();
}
