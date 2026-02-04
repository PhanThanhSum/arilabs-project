package com.example.airlabproject.nats;

import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.JetStreamManagement;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

@Component
public class JetStreamInit {
    public JetStreamInit(Connection nc) throws Exception {
        JetStreamManagement jsm = nc.jetStreamManagement();

        StreamConfiguration streamConfig = StreamConfiguration.builder()
                .name("SCHEDULE_STREAM")
                .subjects("schedule.*")
                .storageType(StorageType.File)
                .build();

        try {
            jsm.addStream(streamConfig);
        } catch (Exception e) {
            System.out.println("Stream already exists");
        }
    }
}
