package com.acme.cbmkafka.consumer.dto;

import java.io.Serializable;

public class KeyValuePair implements Serializable {

	private String key;
	private String value;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyValuePair [key=").append(key).append(", value=").append(value).append("]");
		return builder.toString();
	}
}
