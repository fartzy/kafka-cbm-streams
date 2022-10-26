package com.acme.cbmkafka.consumer.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

	final static Logger logger = LogManager.getLogger(JsonDeserializer.class.getName());

	private ObjectMapper mapper = new ObjectMapper();
	private Class<T> deserializedClass;

	public JsonDeserializer(Class<T> deserializedClass) {
		this.deserializedClass = deserializedClass;
	}

	public JsonDeserializer() {
	}

	@Override
	public void configure(Map<String, ?> map, boolean b) {
	}

	@Override
	public T deserialize(String s, byte[] bytes) {
		if(bytes == null) {
			return null;
		}

		try {
			return mapper.readValue(new String(bytes),deserializedClass);
		} catch(Exception ex) {
			logger.warn("Caught exception deserializing",  ex);
		}

		return null;
	}

	@Override
	public void close() {

	}
}
