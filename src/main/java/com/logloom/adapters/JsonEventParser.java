package com.logloom.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logloom.domain.ClickstreamEvent;
import com.logloom.ports.EventParserStrategy;

// This class implements the EventParserStrategy interface to provide a 
// JSON parsing strategy for raw log lines.
public class JsonEventParser implements EventParserStrategy {
    private final ObjectMapper objectMapper;

    public JsonEventParser() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ClickstreamEvent parse(String rawLogLine) {
        try {
            return objectMapper.readValue(rawLogLine, ClickstreamEvent.class);
        } catch (JsonProcessingException e) {
            System.err.println("Parse Error (invalid log): " + rawLogLine);
            return null;
        }
    }
}