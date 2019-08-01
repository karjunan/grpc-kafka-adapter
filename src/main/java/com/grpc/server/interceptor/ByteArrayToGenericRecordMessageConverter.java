package com.grpc.server.interceptor;

import com.grpc.server.util.Utils;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.IOException;

@Component
public class ByteArrayToGenericRecordMessageConverter extends AbstractMessageConverter {

        public ByteArrayToGenericRecordMessageConverter() {
            super(new MimeType("application", "*+avro"));
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return (GenericRecord.class == clazz);
        }

        @Override
        protected GenericRecord convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
            Object payload = message.getPayload();
            byte[] received_message = (byte[]) payload;
            GenericRecord record = null;
            System.out.println(received_message);
            Schema schema = new Schema.Parser().parse(Utils.getAvroData());
            DatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
            Decoder decoder = DecoderFactory.get().binaryDecoder(received_message, null);
            try {
                record = reader.read(null, decoder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Message received : " + record);
            return record;
        }
}
