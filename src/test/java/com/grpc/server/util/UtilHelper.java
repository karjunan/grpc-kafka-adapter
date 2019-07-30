package com.grpc.server.util;

import com.grpc.server.avro.Message;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class UtilHelper {

    public static String getAvroData() throws Exception {
        return Files.lines(Paths.get("src", "main", "resources", "avro/message.avsc"))
                .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));
    }

    public static byte[] convertToByteArray(GenericRecord record) {
        BinaryEncoder encoder = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            DatumWriter<GenericRecord> writer = new SpecificDatumWriter<>(Message.getClassSchema());
            encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(record, encoder);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                encoder.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
