package com.example.rabbit_demo.controller;

import com.example.rabbit_demo.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value= "api")
@RequiredArgsConstructor
public class TestController {
    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/test")
    public void reloadConfig() {
        rabbitTemplate.convertAndSend
                (RabbitMQConfig.getInstanceRef().getExchange()
                        , RabbitMQConfig.getInstanceRef().getRoutingKeyHello(), "hi guide");
    }
}
