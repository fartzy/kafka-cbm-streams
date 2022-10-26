package com.acme.cbmkafka.bt.common;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.bt.app.BusinessLogicErrorHandler;
import com.acme.cbmkafka.bt.config.MessageRouteProcessorConfig;
import com.acme.cbmkafka.bt.config.RouteConfig;
import com.acme.cbmkafka.bt.dto.EDAMessage;



public class EDAUncaughtExceptionHandler extends AbstractProcessor<String, EDAMessage> implements ProductionExceptionHandler {

	final static Logger logger = LogManager.getLogger(EDAUncaughtExceptionHandler.class.getName());
	private Properties props = new Properties();
	private KafkaProducer<byte[], byte[]> producer;
	private String errorTopic;   
	private boolean retrySend;

	private ProcessorContext context;
	private Exception exception;
	private BusinessLogicErrorHandler bleHandler;	
	
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

        //props.put("production.bootstrap.servers","node101:9191");
        //props.put("production.num.stream.threads", "1");
        //props.put("production.commit.interval.ms", "100");
        //props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
       // props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	   //producer = new KafkaProducer<>(props);        
        
			for (String configKey : configKeys) {
				if(!configs.containsKey(configKey)){
					throw new RuntimeException(String.format("%s did not get passed into EDAUncaughtExceptionHandler (ProductionExceptionHandler)", configKey));
				}

			System.out.println("EDAUncaughtExceptionHandler (ProductionExceptionHandler) - config" + configKey + " :  " + configs.get(configKey));
			}
	        //props.put("production.bootstrap.servers","node101:9191");
	        //props.put("production.num.stream.threads", "1");
	        //props.put("production.commit.interval.ms", "100");
    	   // producer = new KafkaProducer<>(props);
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
            return ProductionExceptionHandlerResponse.FAIL;
        }
    }

	
	public static Logger getLogger() {
		return logger;
	}


	public EDAUncaughtExceptionHandler(Throwable e) {
		this.exception = (Exception) e;
	}

	public ProcessorContext getContext() {
		return context;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) { 
		this.context = context;

	}

	@Override    
	public void process(String key, EDAMessage cbmMessage) {
		this.bleHandler = new BusinessLogicErrorHandler(key, cbmMessage, this);
		if(cbmMessage == null) {
			logger.info("EDAMessage is null. Ignoring...");
			this.context.commit();
			return;		
		}
		try {
			
		}
			
		 catch(Throwable th) {
			logger.warn(th);
		} finally {
			this.context.commit();
		}
	}
}
