package com.example.monolith.messaging;

import static com.example.contracts.MessagingTopology.TRANSACTION_RESULTS_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.contracts.TransactionResultEvent;

@Component
public class TransactionResultListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionResultListener.class);

    @RabbitListener(queues = TRANSACTION_RESULTS_QUEUE)
    public void receive(TransactionResultEvent event) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", event.correlationId().toString())) {
            log.info("Resultado assíncrono commandId={} status={} transactionId={}",
                    event.commandId(), event.status(), event.transactionId());
        }
    }
}
