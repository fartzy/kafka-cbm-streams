package com.acme.cbmkafka.consumer.app;

import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.consumer.config.FilterProcessorConfig;
import com.acme.cbmkafka.consumer.dto.EDAMessage;

public class FilterProcessor extends AbstractProcessor<String, EDAMessage> {
	final static Logger logger = LogManager.getLogger(FilterProcessor.class.getName());

	private ProcessorContext context;

	private FilterProcessorConfig myConfig;

	FilterProcessor(FilterProcessorConfig myConfig) {
		this.myConfig = myConfig;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		this.context = context;

		Objects.requireNonNull(myConfig, "Config object can't be null");
	}

	@Override    
	public void process(String key, EDAMessage cbmMessage) {

		if(cbmMessage == null) {
			logger.info("EDAMessage is null. Ignoring...");
			this.context.commit();
			return;		
		}

		try {
			if(myConfig.getFilterType() != null && myConfig.getFilterValues() != null) {
				if(myConfig.getFilterType().equalsIgnoreCase("IN")) {
					if(myConfig.getFilterValues().contains(cbmMessage.getMetadata().getType()) == false) {
						logger.info("Message type not in the white list. Filtering out");
						this.context.commit();
						return;
					}
				} else if(myConfig.getFilterType().equalsIgnoreCase("OUT")){
					if(myConfig.getFilterValues().contains(cbmMessage.getMetadata().getType()) == true) {
						logger.info("Message type in the black list. Filtering out");
						this.context.commit();
						return;
					}
				}
			}
		} catch(Throwable th) {
			logger.warn(th);
		}

		logger.info("Filter Processor Forwarding :" + cbmMessage.toString());

		this.context.forward(key, cbmMessage);
	}
}
