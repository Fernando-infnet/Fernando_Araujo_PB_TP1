package com.example.transactions;

import static com.example.contracts.MessagingTopology.WALLET_CREATED_KEY;
import static com.example.contracts.MessagingTopology.DEAD_LETTER_QUEUE;
import static com.example.contracts.MessagingTopology.WALLET_EVENTS_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.example.contracts.WalletCreatedEvent;
import com.example.transactions.messaging.WalletEventListener;
import com.example.transactions.repository.WalletLedgerRepository;

@SpringBootTest(properties = "spring.rabbitmq.listener.simple.auto-startup=true")
@ActiveProfiles("test")
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
class RabbitMessagingIntegrationTests {
    static {
        // Docker 29 rejects the legacy API level selected by docker-java.
        System.setProperty("api.version", System.getProperty("api.version", "1.44"));
    }

    @Container
    static final RabbitMQContainer rabbit = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.13-management-alpine"));

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @Autowired RabbitTemplate template;
    @Autowired RabbitAdmin admin;
    @Autowired WalletLedgerRepository ledgers;
    @SpyBean WalletEventListener walletEventListener;

    @BeforeEach
    void clearDeadLetterQueue() {
        admin.purgeQueue(DEAD_LETTER_QUEUE, false);
    }

    @Test
    void transportsWalletEventThroughRealRabbitMq() {
        UUID eventId = UUID.randomUUID();
        WalletCreatedEvent event = WalletCreatedEvent.create(
                eventId, Instant.now(), 42L, 7L, "BRL");

        template.convertAndSend(WALLET_EVENTS_EXCHANGE, WALLET_CREATED_KEY, event);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(ledgers.findById(42L)).hasValueSatisfying(ledger ->
                        assertThat(ledger.getCurrency()).isEqualTo("BRL")));
    }

    @Test
    void retriesInvalidEventAndRoutesItToDeadLetterQueue() {
        WalletCreatedEvent invalidEvent = WalletCreatedEvent.create(
                null, Instant.now(), 99L, 7L, "BRL");

        template.convertAndSend(WALLET_EVENTS_EXCHANGE, WALLET_CREATED_KEY, invalidEvent);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Message deadLetter = template.receive(DEAD_LETTER_QUEUE, 1_000);
            assertThat(deadLetter).isNotNull();
            assertThat(deadLetter.getMessageProperties().getReceivedRoutingKey())
                    .isEqualTo("dead-letter");
            assertThat(deadLetter.getMessageProperties().getHeaders()).containsKey("x-death");
        });
        verify(walletEventListener, timeout(10_000).times(3))
                .onWalletCreated(argThat(event -> event.eventId() == null));
        assertThat(ledgers.findById(99L)).isEmpty();
    }
}
