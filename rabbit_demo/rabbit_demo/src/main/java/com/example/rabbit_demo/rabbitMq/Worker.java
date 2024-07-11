package com.example.rabbit_demo.rabbitMq;



public interface Worker {
    String getRoutingKey();

    void run(String message) throws RuntimeException;
}
