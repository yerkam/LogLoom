package com.logloom.adapters;

import com.logloom.domain.WindowMetrics;
import com.logloom.ports.MetricsExporterPort;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

// This class implements the MetricsExporterPort interface to export aggregated metrics in Parquet format.
public class ParquetMetricsExporter implements MetricsExporterPort {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    // Define the Avro schema for the Parquet file. This schema describes the structure of the data to be written.
    private final String SCHEMA_JSON = "{"
            + "\"type\": \"record\","
            + "\"name\": \"WindowMetrics\","
            + "\"fields\": ["
            + "  {\"name\": \"windowStart\", \"type\": \"string\"},"
            + "  {\"name\": \"totalEvents\", \"type\": \"int\"},"
            + "  {\"name\": \"totalRevenue\", \"type\": \"double\"}"
            + "]}";

    @Override
    public void export(Map<Long, WindowMetrics> windows, String outputPath) {
        Schema schema = new Schema.Parser().parse(SCHEMA_JSON);
        Path path = new Path(outputPath);

        Map<Long, WindowMetrics> sortedWindows = new TreeMap<>(windows);

        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(path)
                .withSchema(schema)
                .withConf(new Configuration())
                .withCompressionCodec(CompressionCodecName.SNAPPY) // Big Data standart sıkıştırması
                .build()) {

            for (Map.Entry<Long, WindowMetrics> entry : sortedWindows.entrySet()) {
                GenericRecord record = new GenericData.Record(schema);
                
                String readableTime = formatter.format(Instant.ofEpochSecond(entry.getKey()));
                
                record.put("windowStart", readableTime);
                record.put("totalEvents", entry.getValue().getTotalEvents());
                record.put("totalRevenue", entry.getValue().getTotalRevenue());

                writer.write(record);
            }
            System.out.println("Parquet report exported successfully: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Parquet export error: " + e.getMessage());
        }
    }
}