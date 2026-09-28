package com.example.monolith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.contracts.MessagingTopology;
import com.example.monolith.domain.WalletEventOutbox;
import com.example.monolith.messaging.WalletEventOutboxPublisher;
import com.example.monolith.repository.WalletEventOutboxRepository;

class WalletEventOutboxPublisherTests {
    @Test
    void publishesPendingEventAndMarksItAsPublished() {
        WalletEventOutboxRepository repository = org.mockito.Mockito.mock(WalletEventOutboxRepository.class);
        RabbitTemplate rabbit = org.mockito.Mockito.mock(RabbitTemplate.class);
        WalletEventOutbox event = new WalletEventOutbox(7L, 3L, "BRL");
        when(repository.findTop50ByPublishedAtIsNullOrderByOccurredAtAsc()).thenReturn(List.of(event));

        new WalletEventOutboxPublisher(repository, rabbit).publishPending();

        verify(rabbit).convertAndSend(eq(MessagingTopology.WALLET_EVENTS_EXCHANGE),
                eq(MessagingTopology.WALLET_CREATED_KEY), any(), any(MessagePostProcessor.class));
        assertThat(event.getPublishedAt()).isNotNull();
        assertThat(event.getAttempts()).isEqualTo(1);
    }
}
