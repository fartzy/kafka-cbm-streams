package com.acme.cbmkafka.bt.app;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.bt.dto.EDAMessage;

public class RulesProcessor extends AbstractProcessor<String, EDAMessage> {
	
	final static Logger logger = LogManager.getLogger(RulesProcessor.class.getName());
	private ProcessorContext context;

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		this.context = context;
	}

	@Override    
	public void process(String key, EDAMessage cbmMessage) {
		if(cbmMessage == null) {
			logger.info("EDAMessage is null. Ignoring...");
			this.context.commit();
			return;		
		}

		logger.info("Processing EDA message :" + cbmMessage.toString() + "\n");
		
		// No Rules defined... only a placeholder

		this.context.forward(key, cbmMessage);
	}
}
