package com.example.transactions.config;

import static com.example.contracts.MessagingTopology.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMessagingConfig {
    @Bean
    Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    TopicExchange walletEventsExchange() {
        return new TopicExchange(WALLET_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    DirectExchange transactionCommandsExchange() {
        return new DirectExchange(TRANSACTION_COMMANDS_EXCHANGE, true, false);
    }

    @Bean
    TopicExchange transactionEventsExchange() {
        return new TopicExchange(TRANSACTION_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue walletEventsQueue() {
        return QueueBuilder.durable(WALLET_EVENTS_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    Queue transactionCreateQueue() {
        return QueueBuilder.durable(TRANSACTION_CREATE_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding walletEventsBinding(Queue walletEventsQueue, TopicExchange walletEventsExchange) {
        return BindingBuilder.bind(walletEventsQueue).to(walletEventsExchange).with("wallet.#");
    }

    @Bean
    Binding transactionCreateBinding(Queue transactionCreateQueue, DirectExchange transactionCommandsExchange) {
        return BindingBuilder.bind(transactionCreateQueue).to(transactionCommandsExchange).with(TRANSACTION_CREATE_KEY);
    }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DEAD_LETTER_KEY);
    }
}
