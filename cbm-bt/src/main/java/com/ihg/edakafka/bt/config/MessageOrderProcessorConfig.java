package com.acme.cbmkafka.bt.config;

public class MessageOrderProcessorConfig {

	private String name;
	private String store;
	private int batchDuration;
	private String successProcessor;
	private String alertSink;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStore() {
		return store;
	}
	
	public void setStore(String store) {
		this.store = store;
	}
	
	public int getBatchDuration() {
		return batchDuration;
	}
	
	public void setBatchDuration(int batchDuration) {
		this.batchDuration = batchDuration;
	}

	public String getSuccessProcessor() {
		return successProcessor;
	}

	public void setSuccessProcessor(String successProcessor) {
		this.successProcessor = successProcessor;
	}

	public String getAlertSink() {
		return alertSink;
	}

	public void setAlertSink(String alertSink) {
		this.alertSink = alertSink;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageOrderProcessorConfig [name=").append(name).append(", store=").append(store)
				.append(", batchDuration=").append(batchDuration).append(", successProcessor=").append(successProcessor)
				.append(", alertSink=").append(alertSink).append("]");
		return builder.toString();
	}
}