package com.acme.cbmkafka.bt.app;

import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.bt.config.MessageRouteProcessorConfig;
import com.acme.cbmkafka.bt.config.RouteConfig;
import com.acme.cbmkafka.bt.dto.EDAMessage;



public class MessageRouteProcessor extends AbstractProcessor<String, EDAMessage> {
	final static Logger logger = LogManager.getLogger(MessageRouteProcessor.class.getName());
	private ProcessorContext context;
	private BusinessLogicErrorHandler bleHandler;	
	
	public static Logger getLogger() {
		return logger;
	}



	public ProcessorContext getContext() {
		return context;
	}

	private MessageRouteProcessorConfig myConfig;
	
	public MessageRouteProcessorConfig getMyConfig() {
		return myConfig;
	}

	MessageRouteProcessor(MessageRouteProcessorConfig myConfig) {
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
		this.bleHandler = new BusinessLogicErrorHandler(key, cbmMessage, this);
		if(cbmMessage == null) {
			logger.info("EDAMessage is null. Ignoring...");
			this.context.commit();
			return;		
		}


		try {
			
			if(myConfig.getRoutesConfig() != null) {
				for(RouteConfig route : myConfig.getRoutesConfig()) {
					if(route != null && route.getMessageTypes() != null 
							&& route.getMessageTypes().size() > 0 && route.getSinkName() != null) {
						if(route.getMessageTypes().contains(cbmMessage.getMetadata().getType())) {
							logger.info("Forwarding messsage type " + cbmMessage.getMetadata().getType() + 
									" to :" + route.getSinkName());
							bleHandler.setEdaTypeMatch(true);  
							bleHandler.setEdaTypeDefined(true);
							this.context.forward(key, cbmMessage, route.getSinkName());

						} 

					} 
				}
			} else {
				bleHandler.setNoRoutesConfigured(true);
				bleHandler.processNoRoutesConfigured();
				
			}	

			if (cbmMessage.getMetadata().getType() != null ) {
				if (cbmMessage.getMetadata().getType().trim().length() > 0 ) { bleHandler.setEdaTypeDefined(true);}
			}
			if (bleHandler.isEdaTypeDefined() == true && 
					bleHandler.areNoRoutesConfigured() == false && 
						bleHandler.isEdaTypeMatch() == false) {
				
				bleHandler.processNoEdaTypeMatch();
				
			} else if (bleHandler.isEdaTypeDefined() == false && 
					bleHandler.areNoRoutesConfigured() == false && 
					bleHandler.isEdaTypeMatch() == false) {
				
				bleHandler.processNullEdaType();
				
			}
			
		} catch(Throwable th) {
			logger.warn(th);
		} finally {
			this.context.commit();
		}
	}
}
