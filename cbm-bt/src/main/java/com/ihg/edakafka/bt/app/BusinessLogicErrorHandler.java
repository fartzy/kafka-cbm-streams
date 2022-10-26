package com.acme.cbmkafka.bt.app;

import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.bt.common.EDAUncaughtExceptionHandler;
import com.acme.cbmkafka.bt.config.MessageRouteProcessorConfig;
import com.acme.cbmkafka.bt.config.RouteConfig;
import com.acme.cbmkafka.bt.dto.EDAMessage;

public class BusinessLogicErrorHandler {
	private String key;
	private EDAMessage edaMessage;
	private MessageRouteProcessor mrp;
	private MessageRouteProcessorConfig mrpConfig;
	private boolean noRoutesConfigured;
	private boolean edaTypeDefined;
	private boolean edaTypeMatch;
	
	public BusinessLogicErrorHandler(String keyIn, EDAMessage edaMessageIn, MessageRouteProcessor mrpIn) {
		key = keyIn;
		edaMessage = edaMessageIn;
		mrp = mrpIn;
		noRoutesConfigured = false;
		edaTypeDefined = false;
		edaTypeMatch = false;
	}
	
	public BusinessLogicErrorHandler(String key2, EDAMessage edaMessage2,
			EDAUncaughtExceptionHandler cbmUncaughtExceptionHandler) {
		key = key2;
		edaMessage = edaMessage2;
		noRoutesConfigured = false; 
		edaTypeDefined = false;
		edaTypeMatch = false;
	}

	public boolean areNoRoutesConfigured() {
		return noRoutesConfigured;
	}

	public void setNoRoutesConfigured(boolean noRoutesConfigured) {
		this.noRoutesConfigured = noRoutesConfigured;
	}

	public boolean isEdaTypeDefined() {
		return edaTypeDefined;
	}

	public void setEdaTypeDefined(boolean edaTypeDefined) {
		this.edaTypeDefined = edaTypeDefined;
	}

	public boolean isEdaTypeMatch() {
		return edaTypeMatch;
	}

	public void setEdaTypeMatch(boolean edaTypeMatch) {
		this.edaTypeMatch = edaTypeMatch;
	}

	public void processNoRoutesConfigured(){
		// this.context.forward(key, cbmMessage);
		// 20180614 artzmi - sending to error topic
		setNoRoutesConfigured(false);
		mrp.getContext().forward(key,"MESSAGE_ERROR 003 --> " + edaMessage, mrp.getMyConfig().getErrorSinkName());
		// No routes defined
		MessageRouteProcessor.getLogger().info("No route defined. Sending to Error Topic. Please check config file: " + mrpConfig.toString());
	}
	
	public void processNoEdaTypeMatch() {
		// 20180614 artzmi - sending to error topic if there is no messageType match to routeConfig
		mrp.getContext().forward(key,"MESSAGE_ERROR 002 --> " + edaMessage, mrp.getMyConfig().getErrorSinkName());
		MessageRouteProcessor.getLogger().warn("Invalid route configuration. Message Type not in config file." + 
					" Forwarding message to error topic : " + mrp.getMyConfig().getErrorSinkName());
	}
	
	public void processNullEdaType() {		
		// 20180614 artzmi - sending to error topic if there is no messageType at all 
		mrp.getContext().forward(key,"MESSAGE_ERROR 001 --> " + edaMessage, mrp.getMyConfig().getErrorSinkName());
		MessageRouteProcessor.getLogger().warn("Invalid route configuration. No Message Type Defined." + 
                 " Forwarding message to error topic : " + mrp.getMyConfig().getErrorSinkName());
	}
}
