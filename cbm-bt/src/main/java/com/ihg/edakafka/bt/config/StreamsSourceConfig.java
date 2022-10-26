package com.acme.cbmkafka.bt.config;

public class StreamsSourceConfig {

	private String name;
	private String topic;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StreamsSourceConfig [name=").append(name).append(", topic=").append(topic).append("]");
		return builder.toString();
	}
}