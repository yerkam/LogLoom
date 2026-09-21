// Dosya yolu: Main.java (Kök pakette)
package com.logloom;

import com.logloom.adapters.CsvMetricsExporter;
import com.logloom.adapters.JsonEventParser;
import com.logloom.ports.EventParserStrategy;
import com.logloom.ports.MetricsExporterPort;
import com.logloom.service.LogProcessorEngine;
import com.logloom.service.TimeWindowProcessor;

public class Main {
    public static void main(String[] args) {
        String inputLogFile = "ecommerce_logs.json"; 
        String outputReportFile = "analytics_report.csv";

        // 1. Strateji ve Portları Seç (Dependency Injection)
        EventParserStrategy jsonParser = new JsonEventParser();
        MetricsExporterPort csvExporter = new CsvMetricsExporter();
        
        // 2. Servisleri Ayağa Kaldır
        TimeWindowProcessor windowProcessor = new TimeWindowProcessor();
        LogProcessorEngine engine = new LogProcessorEngine(jsonParser, windowProcessor);
        
        System.out.println("Log işleme başlatılıyor...");
        
        // 3. Nehir Akışını (Producer-Consumer) Başlat
        engine.startProcessing(inputLogFile);
        
        // 4. Okuma bittikten sonra sonuçları dışa aktar
        System.out.println("Sonuçlar raporlanıyor...");
        csvExporter.export(windowProcessor.getActiveWindows(), outputReportFile);
    }
}