package com.acme.cbmkafka.consumer.app;

import java.util.Objects;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.acme.cbmkafka.consumer.config.RMSRestCallProcessorConfig;
import com.acme.cbmkafka.consumer.dto.EDAMessage;
import com.acme.cbmkafka.consumer.dto.KeyValuePair;

public class RMSRestCallProcessor extends AbstractProcessor<String, EDAMessage> {
	final static Logger logger = LogManager.getLogger(RMSRestCallProcessor.class.getName());

	private ProcessorContext context;

	private RMSRestCallProcessorConfig myConfig;

	RMSRestCallProcessor(RMSRestCallProcessorConfig myConfig) {
		this.myConfig = myConfig;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		this.context = context;

		Objects.requireNonNull(myConfig, "Config object can't be null");
	}

	@Override    
	public void process(String key, EDAMessage edaMessage) {

		if(edaMessage == null) {
			logger.info("EDAMessage is null. Ignoring...");
			this.context.commit();
			return;		
		}

		logger.info("Processing EDA message :" + edaMessage.toString() + "\n");

		String restResponse = "ERROR";

		try {
			String url = myConfig.getBaseRestUrl();
			Request request = Request.Post(url)
					.connectTimeout(myConfig.getConnectTimeout())
					.socketTimeout(myConfig.getSocketTimeout());

			if(myConfig.getHttpHeaders() != null) {
				for(int ii = 0; ii < myConfig.getHttpHeaders().size(); ii++) {
					KeyValuePair kv = myConfig.getHttpHeaders().get(ii);

					if(kv != null) {
						request.addHeader(kv.getKey(), kv.getValue());
					}
				}
			}

			String sendMessage = edaMessage.getMessage();

			restResponse = request.bodyString(sendMessage, ContentType.APPLICATION_JSON)
					.execute().returnContent().asString();
		} catch(Throwable th) {
			th.printStackTrace();
			logger.warn(restResponse + ":" + th.getMessage(),  th);
		}

		logger.info("Send Status : " + restResponse);

		if(restResponse.equalsIgnoreCase("ERROR") == false) {
			this.context.forward(key, edaMessage.getPropertyCode() + " : " + edaMessage.getSequence().getPart2() + " --> SUCCESS", myConfig.getSuccessSinkName());
		} else {
			this.context.forward(key, edaMessage.getPropertyCode() + " : " + edaMessage.getSequence().getPart2() + " --> ERROR", myConfig.getErrorSinkName());
		}

		this.context.commit();
	}
}
