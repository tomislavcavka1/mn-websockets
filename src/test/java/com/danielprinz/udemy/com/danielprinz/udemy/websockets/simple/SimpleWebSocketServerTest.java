package com.danielprinz.udemy.com.danielprinz.udemy.websockets.simple;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.websocket.RxWebSocketClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class SimpleWebSocketServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleWebSocketServerTest.class);

    @Inject
    @Client("http://localhost:8180")
    RxWebSocketClient client;

    SimpleWebSocketClient webSocketClient;

    @BeforeEach
    void connect() {
        webSocketClient = client.connect(
                SimpleWebSocketClient.class,
                "/ws/simple/prices").blockingFirst();
        LOG.info("Client session: {}", webSocketClient.getSession());
    }

    @Test
    void canReceiveMessagesWithClient() {
        final String message = "Hello";
        webSocketClient.send(message);
        Awaitility.await().timeout(Duration.ofSeconds(10)).untilAsserted(() -> {
            final Object[] messages = webSocketClient.getObservedMessages().toArray();
            LOG.info("Observed Messages {} - {}", webSocketClient.getObservedMessages().size(), messages);
            assertEquals("Connected!", messages[0]);
            assertEquals("Not supported => (" + message + ")", messages[1]);
        });
    }

    @Test
    void canSendReactively() {
        final String message = "Hello";
        LOG.info("Sent {}", webSocketClient.sendReactive(message).blockingGet());
        Awaitility.await().timeout(Duration.ofSeconds(10)).untilAsserted(() -> {
            final Object[] messages = webSocketClient.getObservedMessages().toArray();
            LOG.info("Observed Messages {} - {}", webSocketClient.getObservedMessages().size(), messages);
            assertEquals("Connected!", messages[0]);
            assertEquals("Not supported => (" + message + ")", messages[1]);
        });
    }

}
