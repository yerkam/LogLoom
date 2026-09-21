package com.logloom.domain;

// This record represents a clickstream event with various attributes 
// such as timestamp, user ID, event type, product ID, price, and client IP address.
public record ClickstreamEvent(
    long timestamp,
    String userId,
    String eventType,
    String productId,
    double price,
    String clientIp
) {}  
