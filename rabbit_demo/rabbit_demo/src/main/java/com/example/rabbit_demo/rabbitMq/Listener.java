package com.example.rabbit_demo.rabbitMq;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Listener {
    private final WorkerFactory workerFactory;

    @RabbitListener(queues = "#{rabbitMQConfig.getInstanceRef().getQueue()}")
    private void process(Message message) {
        try {
            System.out.println("Listener: " + new Gson().toJson(message));
            System.out.println("Listener message: " + new String(message.getBody()));
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();

            workerFactory.getWorker(routingKey).run(new String(message.getBody()));
        } catch (Exception e) {
            System.out.println("BillingGatewayListener: Process message from queue is failed: " + e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}