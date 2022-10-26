package com.acme.cbmkafka.bt.common;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Deserialization handler that logs a deserialization exception and then
 * signals the processing pipeline to continue processing more records.
 */
public class EDADeserializationExceptionHandler implements DeserializationExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(EDADeserializationExceptionHandler.class);

    @Override
    public DeserializationHandlerResponse handle(final ProcessorContext context,
                                                 final ConsumerRecord<byte[], byte[]> record,
                                                 final Exception exception) {

        log.warn("Exception caught during Deserialization, " +
                 "taskId: {}, topic: {}, partition: {}, offset: {}",
                 context.taskId(), record.topic(), record.partition(), record.offset(),
                 exception);

        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(final Map<String, ?> configs) {
        List<String> configKeys = Arrays.asList("bootstrap.servers",
                "num.stream.threads",
                "commit.interval.ms");
			
			for (String configKey : configKeys) {
				if(!configs.containsKey(configKey)){
					throw new RuntimeException(String.format("%s did not get passed into Deserializer", configKey));
				}

			System.out.println("EDADeserializationExceptionHandler (DeserializationExceptionHandler) - config: " + configKey + ":  " + configs.get(configKey));
			}
    	}
	}