package com.logloom.filter;

import com.logloom.domain.ClickstreamEvent;

// This abstract class defines a filter in a chain of responsibility pattern for processing ClickstreamEvent objects.
public abstract class EventFilter {
    private EventFilter nextFilter;

    // This method links the current filter to the next filter in the chain and returns the next filter for further chaining.
    public EventFilter linkWith(EventFilter nextFilter) {
        this.nextFilter = nextFilter;
        return nextFilter;
    }

    // This abstract method must be implemented by subclasses to define the specific filtering logic for ClickstreamEvent objects.
    public abstract boolean check(ClickstreamEvent event);

    // This method checks the next filter in the chain, if it exists, and returns true if the event passes all filters in the chain.
    protected boolean checkNext(ClickstreamEvent event) {
        if (nextFilter == null) {
            return true; // If there is no next filter, the event passes all filters in the chain
        }
        return nextFilter.check(event);
    }
}