package com.example.quarkus.infrastructure.pubsub;

import com.google.pubsub.v1.PubsubMessage;
import org.apache.avro.specific.SpecificRecordBase;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class PubSubGateway<Domain, Avro extends SpecificRecordBase, Publisher extends PubSubPublisherImpl<Avro>> {

    @Inject
    BinaryMessageSerializer serializer;
    @Inject
    PubSubPublisher<Avro> publisher;

    @Inject
    PubSubTransactionalOutboxRepository repository;

    @Transactional(Transactional.TxType.MANDATORY)
    public UUID sendMessage(Domain content) {

        Avro messageDto = convertToAvro(content);
        UUID messageId = UUID.randomUUID();

        try {
            PubsubMessage message = serializeAndSaveMessage(messageDto, messageId);
            publisher.publishMessage(message, messageId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return messageId;
    }

    @Transactional(Transactional.TxType.MANDATORY)
    PubsubMessage serializeAndSaveMessage(Avro content, UUID messageId) throws IOException {
        PubsubMessage message = serializer.serializeMessage(content, getMessageAttributes(), messageId);
        repository.persistMessage(publisher.getProjectId(), publisher.getTopicId(), messageId, message);
        return message;
    }

    protected abstract Avro convertToAvro(Domain content);

    protected abstract Map<String, String> getMessageAttributes();
}
