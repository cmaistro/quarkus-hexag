package com.example.quarkus.infrastructure.pubsub;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;

import javax.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class BinaryMessageSerializer {

    public PubsubMessage serializeMessage(SpecificRecordBase avro, Map<String, String> messageAttributes,
            UUID messageId) throws IOException {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        Encoder encoder = null;

        System.out.println("Preparing a BINARY encoder...");
        encoder = EncoderFactory.get().directBinaryEncoder(byteStream, /* reuse= */ null);

        avro.customEncode(encoder);
        encoder.flush();

        ByteString data = ByteString.copyFrom(byteStream.toByteArray());
        return PubsubMessage.newBuilder().putAllAttributes(messageAttributes)
                .putAttributes("messageId", messageId.toString()).setData(data).build();

    }

}
