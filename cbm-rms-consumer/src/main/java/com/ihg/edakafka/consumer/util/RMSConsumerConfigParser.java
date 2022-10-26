package com.acme.cbmkafka.consumer.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.acme.cbmkafka.consumer.common.InvalidStreamsConfigurationException;
import com.acme.cbmkafka.consumer.config.RMSConsumerConfig;

import java.io.IOException;
import java.nio.file.Paths;

public class RMSConsumerConfigParser {
	private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private RMSConsumerConfigParser() {
    }

    public static RMSConsumerConfig parse(String path, Class<? extends RMSConsumerConfig> clzz) {
        try {
            return MAPPER.readValue(Paths.get(path).toFile(), clzz);
        } catch (IOException e) {
            throw new InvalidStreamsConfigurationException(e);
        }
    }
}