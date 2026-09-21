package com.logloom.ports;

import com.logloom.domain.WindowMetrics;
import java.util.Map;

// This interface defines a contract for exporting aggregated metrics.
public interface MetricsExporterPort {
    void export(Map<Long, WindowMetrics> windows, String outputPath);
}