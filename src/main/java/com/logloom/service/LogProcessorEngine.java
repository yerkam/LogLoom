package com.logloom.service;

import com.logloom.domain.ClickstreamEvent;
import com.logloom.ports.EventParserStrategy;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

// This class orchestrates the reading of log files, 
// parsing of events, and processing of time window metrics.
public class LogProcessorEngine {
    // BlockingQueue to hold raw log lines for processing
    // If this queue is full, the producer will block until space is available.
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10000);
    private final EventParserStrategy parser;
    private final TimeWindowProcessor windowProcessor;
    
    // Number of consumer threads to process the log lines
    // (It can be increased in better CPU environments for better performance,
    // but it should be balanced with the number of available CPU cores and the nature of the workload.)
    private final int consumerCount = 4;
    // A special string to signal the end of processing to consumer threads
    private final String POISON_PILL = "EOF";

    public LogProcessorEngine(EventParserStrategy parser, TimeWindowProcessor windowProcessor) {
        this.parser = parser;
        this.windowProcessor = windowProcessor;
    }

    public void startProcessing(String filePath) {
        // Create a fixed thread pool for consumers
        ExecutorService executor = Executors.newFixedThreadPool(consumerCount);

        // Start the consumer threads
        for (int i = 0; i < consumerCount; i++) {
            // Each consumer thread will run the consume() method
            executor.submit(this::consume);
        }

        // Producer - reads the file and puts lines into the queue
        produce(filePath);

        // Shutdown the executor after all tasks are submitted
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
            System.out.println("Tüm veri işlendi.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void produce(String filePath) {
        // Files.lines() method reads all lines from a file as a Stream.
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                try {
                    queue.put(line); // Send the line to the queue (Blocking)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            // After all lines are read, send a poison pill for each consumer to signal completion
            for (int i = 0; i < consumerCount; i++) {
                queue.put(POISON_PILL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consume() {
        try {
            while (true) {
                String line = queue.take(); // Blocking call, waits for a line to be available
                if (line.equals(POISON_PILL)) {
                    break; // Exit the loop if poison pill is received
                }

                //  Parse the line into a ClickstreamEvent and process it
                ClickstreamEvent event = parser.parse(line);
                if (event != null) {
                    windowProcessor.processEvent(event);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}