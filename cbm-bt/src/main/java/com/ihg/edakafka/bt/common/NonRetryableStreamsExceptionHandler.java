package com.acme.cbmkafka.bt.common;

import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerRecord; 
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse;

public class NonRetryableStreamsExceptionHandler implements ProductionExceptionHandler {
	
	private Properties props = new Properties();
	private KafkaProducer<byte[], byte[]> producer;
	private String errorTopic;   
	private boolean retrySend;
	
    public boolean isRetrySend() {
		return retrySend;
	}

	public void setRetrySend(boolean retrySend) {
		this.retrySend = retrySend;
	}
    
	@Override
	public void configure(Map<String, ?> configs) {
        List<String> configKeys = Arrays.asList("bootstrap.servers",
                "num.stream.threads",
                "commit.interval.ms");
  
        
			for (String configKey : configKeys) {
				if(!configs.containsKey(configKey)){
					throw new RuntimeException(String.format("NonRetryableStreamsExceptionHandler (ProductionExceptionHandler) - config %s did not get passed into Production", configKey));
				}

			System.out.println("NonRetryableStreamsExceptionHandler (ProductionExceptionHandler) - config: " + configKey + ":  " + configs.get(configKey));
			}
	
    }


	@Override
    public ProductionExceptionHandlerResponse handle(final ProducerRecord<byte[], byte[]> record,
                                                     final Exception exception) {
        //if (exception instanceof RecordTooLargeException) {
    	if (exception instanceof RetriableException) {
    		setRetrySend(true);
            return ProductionExceptionHandlerResponse.CONTINUE;
        } else {
        	setRetrySend(false);
            return ProductionExceptionHandlerResponse.CONTINUE;
        }
    }
    
    
}
