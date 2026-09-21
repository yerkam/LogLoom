package com.logloom.filter;

import com.logloom.domain.ClickstreamEvent;

// This class implements a filter to check for invalid data in ClickstreamEvent objects.
public class InvalidDataFilter extends EventFilter {
    @Override
    public boolean check(ClickstreamEvent event) {
        // Check for invalid data: negative price or null/empty product ID
        if (event.price() < 0 || event.productId() == null || event.productId().isEmpty()) {
            // System.err.println("Rejected (Invalid Data): " + event.getProductId());
            return false;
        }
        return checkNext(event); // Pass to the next filter in the chain if data is valid
    }
}