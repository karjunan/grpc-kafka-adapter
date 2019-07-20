package com.grpc.server.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Utils {

    public static String getAvroData() throws Exception {
        return Files.lines(Paths.get("src", "main", "resources", "avro/message.avsc"))
                .collect(Collectors.toList()).stream().collect(Collectors.joining(" "));
    }
}
