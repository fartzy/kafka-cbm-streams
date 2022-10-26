package com.acme.cbmkafka.bt.serialization;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.acme.cbmkafka.bt.dto.KeyValuePair;

public class KeyValueListDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<List<KeyValuePair>> {

	final static Logger logger = LogManager.getLogger(KeyValueListDeserializer.class.getName());
	
    @Override
    public List<KeyValuePair> deserialize(JsonParser jsonParser, 
            DeserializationContext deserializationContext) throws IOException {

    	logger.debug("Deserializing :" + jsonParser.getText());
    	
    	List<KeyValuePair> kvList = jsonParser.readValueAs(new TypeReference<List<KeyValuePair>>() { });
    	return kvList;
    }
}