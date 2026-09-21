package com.logloom.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

// This class generates a massive JSONL file with random e-commerce log events for testing purposes.
public class DataGenerator {
    public static void main(String[] args) {
        String fileName = "ecommerce_logs.json"; // Output file name
        int totalLines = 100_000_000; // Number of lines of test data
        
        String[] events = {"view", "add_to_cart", "purchase"};
        String[] products = {"PROD-A", "PROD-B", "PROD-C", "PROD-D", "PROD-E"};
        Random random = new Random();
        
        // Get the current timestamp in seconds to generate random timestamps within the last week
        long currentTimestamp = System.currentTimeMillis() / 1000; 

        System.out.println("Test data is being generated... Please wait.");
        long startTime = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= totalLines; i++) {
                long timestamp = currentTimestamp - random.nextInt(604800); // Random timestamp within the last 7 days
                String userId = "user_" + random.nextInt(10000);
                String eventType = events[random.nextInt(events.length)];
                String productId = products[random.nextInt(products.length)];
                double price = 10 + (random.nextDouble() * 990); // Random price between 10 and 1000
                String clientIp = "192.168." + random.nextInt(255) + "." + random.nextInt(255);

                // Format the log entry as a JSON line
                String jsonLine = String.format(java.util.Locale.US,
                        "{\"timestamp\": %d, \"userId\": \"%s\", \"eventType\": \"%s\", \"productId\": \"%s\", \"price\": %.2f, \"clientIp\": \"%s\"}\n",
                        timestamp, userId, eventType, productId, price, clientIp);

                writer.write(jsonLine);
                
                if (i % 1_000_000 == 0) {
                    System.out.println(i + " lines written...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Successfully generated test data! File: " + fileName);
        System.out.println("Generation time: " + (endTime - startTime) / 1000.0 + " seconds.");
    }
}