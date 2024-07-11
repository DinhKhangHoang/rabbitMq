package com.example.rabbit_demo.rabbitMq;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerFactory {
    private final List<Worker> workers;

    public Worker getWorker(String routingKey) {
        return this.workers.stream()
                .filter(f -> f.getRoutingKey().equalsIgnoreCase(routingKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Resource type or action is not support"));
    }
}
