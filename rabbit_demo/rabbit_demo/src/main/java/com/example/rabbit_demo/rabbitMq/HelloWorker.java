package com.example.rabbit_demo.rabbitMq;

import com.example.rabbit_demo.config.RabbitMQConfig;
import org.springframework.stereotype.Service;

@Service
public class HelloWorker implements Worker{
    @Override
    public String getRoutingKey() {
        return RabbitMQConfig.getInstanceRef().getRoutingKeyHello();
    }

    @Override
    public void run(String message) throws RuntimeException {
        System.out.println("HelloWorker: " + message);
        throw new RuntimeException();
    }
}
