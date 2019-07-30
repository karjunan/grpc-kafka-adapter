package com.grpc.server.util;

import com.grpc.server.avro.Message;
import com.grpc.server.proto.Messages;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Utils {

    public static Headers getRecordHaders(Messages.ProducerRequest protoHeader) {
        Headers headers = new RecordHeaders();
        protoHeader.getHeaderMap().entrySet()
                .forEach(k -> {
                    Header header = new RecordHeader(k.getKey(), k.getValue().getBytes());
                    log.info(k.getKey() + " : " + k.getValue());
                    headers.add(header);
                });
        return headers;
    }

    public static GenericRecord getAvroRecord(Messages.ProducerRequest request) {
        String avroSchema = request.getHeaderMap().get("avroSchema");
        log.info("AvroSchema from Header => " + avroSchema);
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(avroSchema);
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("value", request.getValue());
        return avroRecord;
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

    public static String getAvroData() {
        try {
            return Files.lines(Paths.get("src", "main", "resources", "avro","message.avsc"))
                    .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GenericRecord buildRecord() {
        try {


            String schemaPath = "message.avsc";
//        System.out.println();
            Stream<String> schemaString = Files.lines(Paths.get("src", "main", "resources", "message.avsc"));
            String result = schemaString.collect(Collectors.joining(" "));
            System.out.println("result =>" + result);
            Schema schema = new Schema.Parser().parse(result);
            GenericData.Record record = new GenericData.Record(schema);
            return record;
        } catch (Exception ex) {
            log.error("Error while building Generic Record" + ex.getMessage());
        }
        return null;
    }

}
