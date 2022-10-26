package com.acme.cbmkafka.consumer.serialization;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.util.Map;

public class JsonSerializer<T> implements Serializer<T> {
	final static Logger logger = LogManager.getLogger(JsonSerializer.class.getName());

	ObjectMapper mapper = new ObjectMapper();

	@Override
	public void configure(Map<String, ?> map, boolean b) {

	}

	@Override
	public byte[] serialize(String topic, T t) {
		try {
			return mapper.writeValueAsString(t).getBytes(Charset.forName("UTF-8"));
		} catch(Exception ex) {
			logger.warn("Caught exception serializing",  ex);
		}

		return null;
	}

	@Override
	public void close() {

	}
}