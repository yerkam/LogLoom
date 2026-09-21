// Dosya yolu: Main.java (Kök pakette)
package com.logloom;

import com.logloom.adapters.*;
import com.logloom.ports.*;
import com.logloom.service.*;

public class Main {
    public static void main(String[] args) {
        // Hadoop'un Windows üzerindeki köprü dosyalarının yolu
        System.setProperty("hadoop.home.dir", "C:\\hadoop");

        String inputLogFile = "ecommerce_logs.json";
        String outputReportFile = "analytics_report_" + System.currentTimeMillis() + ".csv";

        // 1. Strateji ve Portları Seç (Dependency Injection)
        EventParserStrategy jsonParser = new JsonEventParser();
        MetricsExporterPort exporter = new CsvMetricsExporter(); 
        
        // 2. Servisleri Ayağa Kaldır
        TimeWindowProcessor windowProcessor = new TimeWindowProcessor();
        LogProcessorEngine engine = new LogProcessorEngine(jsonParser, windowProcessor);
        
        System.out.println("Log işleme başlatılıyor...");
        long startTime = System.currentTimeMillis(); // Chronometer start
        
        // 3. Nehir Akışını (Producer-Consumer) Başlat
        engine.startProcessing(inputLogFile);
        
        // 4. Okuma bittikten sonra sonuçları dışa aktar
        System.out.println("Sonuçlar raporlanıyor...");
        exporter.export(windowProcessor.getActiveWindows(), outputReportFile);

        long endTime = System.currentTimeMillis();   // Chronometer end
        System.out.println("Processing time: " + (endTime - startTime) / 1000.0 + " seconds.");
    }
}