package com.acme.cbmkafka.bt.config;

import java.util.List;

public class RouteConfig {
	private List<String> messageTypes;
	private String sinkName;
	
	public List<String> getMessageTypes() {
		return messageTypes;
	}
	
	public void setMessageTypes(List<String> messageTypes) {
		this.messageTypes = messageTypes;
	}
	
	public String getSinkName() {
		return sinkName;
	}
	
	public void setSinkName(String sinkName) {
		this.sinkName = sinkName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RouteConfig [messageTypes=").append(messageTypes).append(", sinkName=").append(sinkName)
				.append("]");
		return builder.toString();
	}
}