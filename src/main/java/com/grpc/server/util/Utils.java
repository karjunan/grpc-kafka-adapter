package com.grpc.server.util;

import com.grpc.server.proto.Messages;
import lombok.extern.log4j.Log4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

@Log4j
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

}
