package com.grpc.server.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class AvroDeSerializer<T extends SpecificRecordBase> implements Deserializer<T> {

    protected final Class<T> targetType;

    public AvroDeSerializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        return null;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            T result = null;

            if (data != null) {
                log.debug("data='{}'", DatatypeConverter.printHexBinary(data));

                DatumReader<GenericRecord> datumReader =
                        new SpecificDatumReader<>(targetType.newInstance().getSchema());
                Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);

                result = (T) datumReader.read(null, decoder);
                log.debug("deserialized data='{}'", result);
            }
            return result;
        } catch (Exception ex) {
            throw new SerializationException (
                    "Can't deserialize data '" + Arrays.toString(data) + "' from topic '" + topic + "'", ex);
        }
    }

    @Override
    public void close() {

    }
}


//    @Override
//    public Object deserialize(String s, byte[] bytes) {
//        return null;
//    }
//
//    @Override
//    public void configure(Map configs, boolean isKey) {
//
//    }
//
//    @Override
//    public Object deserialize(String topic, Headers headers, byte[] data) {
//        GenericRecord record = null;
//        System.out.println(data);
//        Schema schema = new Schema.Parser().parse(Utils.getAvroData());
//        DatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
//        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
//        try {
//            record = reader.read(null, decoder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Message received : " + record);
//        return record;
//
//    }
//
//    @Override
//    public void close() {
//
//    }