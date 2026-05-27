package com.example.myproject;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway
public interface GemFireGateway {
    @Gateway(requestChannel = "gemfireWriteChannel")
    void writeToGemFire(String payload, @Header("itemKey") String key);
}
