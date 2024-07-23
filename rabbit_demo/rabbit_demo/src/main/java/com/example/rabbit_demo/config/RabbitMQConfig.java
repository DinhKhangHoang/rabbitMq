package com.example.rabbit_demo.config;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@Getter
public class RabbitMQConfig {
    private final static int BASE_TTL = (int) Duration.ofMinutes(1).toMillis();
    private static RabbitMQConfig instanceRef;

    private final String exchange;
    private final String deadLetterExchange;
    private final String queue;
    private final String deadLetterQueue;
    private final String exclusiveQueue;

    private final String[] queues;

    private final String routingKeyReloadConfigDB;
    private final String routingKeyHello;
    private final Integer consumerTimeout;

    public RabbitMQConfig(@Value("${rabbitmq.test.exchange}") String exchange,
                          @Value("${rabbitmq.test.queue}") String queue,
                          @Value("${rabbitmq.test.routing-key.reload}") String routingKeyReloadConfigDB,
                          @Value("${rabbitmq.test.routing-key.hello}") String routingKeyHello,
                          @Value("${rabbitmq.test.queue.x-consumer-timeout}") String consumerTimeoutStr) {
        this.exchange = exchange;
        this.deadLetterExchange = exchange + ".DLQ";

        this.exclusiveQueue = String.format("%s.node.%s", queue, UUID.randomUUID());
        this.queue = queue;
        this.deadLetterQueue = queue + ".DLQ";

        this.queues = new String[]{queue, this.exclusiveQueue};

        //routing key
        this.routingKeyReloadConfigDB = routingKeyReloadConfigDB;
        this.routingKeyHello = routingKeyHello;

        this.consumerTimeout = Integer.parseInt(consumerTimeoutStr);

        initConstructor(this);
    }

    private static void initConstructor(RabbitMQConfig t) {
        instanceRef = t;
    }

    public static RabbitMQConfig getInstanceRef() {
        return instanceRef;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory rabbitConnFactory) {
        return new RabbitTemplate(rabbitConnFactory);
    }

    @Bean
    public Declarables bindings() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "classic");

        DirectExchange ex = new DirectExchange(this.exchange);
        FanoutExchange deadLetterEx = new FanoutExchange(this.deadLetterExchange);

        Queue eq = new Queue(this.exclusiveQueue, true, true, true);

        Queue q = QueueBuilder
                .durable(this.queue)
                .withArgument("x-queue-type", "classic")
                .withArgument("x-consumer-timeout", this.consumerTimeout)
                .deadLetterExchange(this.deadLetterExchange)
                .build();

        // after BASE_TTL time, the message in deadLetterQ will resend to exchange to retry process
        Queue deadLetterQ = QueueBuilder
                .durable(this.deadLetterQueue)
                .withArgument("x-queue-type", "classic")
                .deadLetterExchange(this.exchange)
                .ttl(BASE_TTL)
                .build();

        return new Declarables(
                ex, deadLetterEx,
                q, eq,
                deadLetterQ,
                BindingBuilder.bind(eq).to(ex).with(this.routingKeyReloadConfigDB),
                BindingBuilder.bind(q).to(ex).with(this.routingKeyHello),
                BindingBuilder.bind(deadLetterQ).to(deadLetterEx)
                );
    }
}
