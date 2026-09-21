package com.logloom.adapters;

import com.logloom.domain.WindowMetrics;
import com.logloom.ports.MetricsExporterPort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// This class implements the MetricsExporterPort interface to export aggregated metrics to a CSV file.
public class CsvMetricsExporter implements MetricsExporterPort {
    
    // DateTimeFormatter to convert epoch seconds to a human-readable date-time format.
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                                 .withZone(ZoneId.systemDefault());

    @Override
    public void export(Map<Long, WindowMetrics> windows, String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // CSV Titles
            writer.write("WindowStart,TotalEvents,TotalRevenue\n");
            
            Map<Long, WindowMetrics> sortedWindows = new java.util.TreeMap<>(windows);
            // Iterate through the map of window metrics and write each entry to the CSV file
            for (Map.Entry<Long, WindowMetrics> entry : sortedWindows.entrySet()) {
                long windowStartEpoch = entry.getKey();
                WindowMetrics metrics = entry.getValue();
                
                // Convert epoch seconds to a human-readable date-time string
                String readableTime = formatter.format(Instant.ofEpochSecond(windowStartEpoch));
                
                // Format the line to be written to the CSV file
                String line = String.format(java.util.Locale.US,
                     "%s,%d,%.2f\n",
                     readableTime,
                     metrics.getTotalEvents(),
                     metrics.getTotalRevenue());
                
                writer.write(line);
            }
            System.out.println("CSV report exported successfully: " + outputPath);
        } catch (IOException e) {
            System.err.println("CSV export error: " + e.getMessage());
        }
    }
}