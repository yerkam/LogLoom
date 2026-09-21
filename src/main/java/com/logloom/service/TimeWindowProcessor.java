package com.logloom.service;

import com.logloom.domain.ClickstreamEvent;
import com.logloom.domain.WindowMetrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// This class processes clickstream events and 
// aggregates metrics over 15-minute time windows.
public class TimeWindowProcessor {
    // Key: Start of the window (seconds), Value: Metrics
    private final ConcurrentHashMap<Long, WindowMetrics> activeWindows = new ConcurrentHashMap<>();

    public void processEvent(ClickstreamEvent event) {
        // 900 seconds = 15 minutes
        long windowStart = (event.timestamp() / 900) * 900;

        activeWindows.compute(windowStart, (key, metrics) -> {
            if (metrics == null) {
                metrics = new WindowMetrics();
            }
            
            // If the event is a purchase, add the price,
            // otherwise add 0 (only increments the counter)
            double priceToAdd = "purchase".equals(event.eventType()) ? event.price() : 0.0;
            metrics.addEvent(priceToAdd);
            
            return metrics;
        });
    }

    public Map<Long, WindowMetrics> getActiveWindows() {
        return activeWindows;
    }
}