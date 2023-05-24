package com.example.quarkus.infrastructure.pubsub.repository.entities;

import com.example.quarkus.infrastructure.pubsub.repository.entities.enums.SendingStatus;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_pubsub_message")
public class PubSubMessageEntity {

    @Id
    private UUID id;
    private String projectId;
    private String topicId;
    private byte[] messageData;
    private SendingStatus sendingStatus;

}
