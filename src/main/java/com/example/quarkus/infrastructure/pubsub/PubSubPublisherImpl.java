package com.example.quarkus.infrastructure.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.UUID;

public abstract class PubSubPublisherImpl<T> implements PubSubPublisher<T> {

    private static final Logger logger = Logger.getLogger(PubSubPublisherImpl.class);

    @Override
    public Publisher getPublisher() throws IOException {

        TopicName topicName = TopicName.of(getProjectId(), getTopicId());

        return Publisher.newBuilder(topicName).build();

    }

    @Override
    public void publishMessage(PubsubMessage message, UUID messageId) throws IOException {

        Publisher publisher = getPublisher();

        ApiFuture<String> publishResponse = publisher.publish(message);
        ApiFutures.addCallback(publishResponse, new ApiFutureCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.errorv("--> Error publishing message: {0}", throwable);
            }

            @Override
            public void onSuccess(String messageId) {
                logger.infov("{0} --> Published messageId: {1}", publisher.getTopicNameString(), messageId);
            }
        }, MoreExecutors.directExecutor());

    }

}
