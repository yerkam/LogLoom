package com.logloom.ports;

import com.logloom.domain.ClickstreamEvent;

// This interface defines a strategy for parsing raw log lines into ClickstreamEvent objects. 
// Implementations of this interface can provide different parsing strategies, such as JSON parsing, CSV parsing, etc.
public interface EventParserStrategy {
    ClickstreamEvent parse(String rawLogLine);
}