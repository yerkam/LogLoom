package com.logloom.domain;

// This class represents the metrics for a time window.
public class WindowMetrics {
    private int totalEvents = 0;
    private double totalRevenue = 0.0;

    public void addEvent(double price) {
        this.totalEvents++;
        this.totalRevenue += price;
    }

    public int getTotalEvents() {
        return totalEvents;
    }
    public double getTotalRevenue() {
        return totalRevenue;
    }
}