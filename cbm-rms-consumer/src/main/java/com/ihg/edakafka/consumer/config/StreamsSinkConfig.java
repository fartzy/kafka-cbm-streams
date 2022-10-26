package com.acme.cbmkafka.consumer.config;

public class StreamsSinkConfig {

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
		builder.append("StreamsSinkConfig [name=").append(name).append(", topic=").append(topic).append("]");
		return builder.toString();
	}
}