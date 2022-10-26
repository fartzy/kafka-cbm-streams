package com.acme.cbmkafka.bt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.acme.cbmkafka.bt.common.InvalidStreamsConfigurationException;
import com.acme.cbmkafka.bt.config.BusinessTierConfig;

import java.io.IOException;
import java.nio.file.Paths;

public class BusinessTierConfigParser {
	private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private BusinessTierConfigParser() {
    }

    public static BusinessTierConfig parse(String path, Class<? extends BusinessTierConfig> clzz) {
        try {
            return MAPPER.readValue(Paths.get(path).toFile(), clzz);
        } catch (IOException e) {
            throw new InvalidStreamsConfigurationException(e);
        }
    }
}