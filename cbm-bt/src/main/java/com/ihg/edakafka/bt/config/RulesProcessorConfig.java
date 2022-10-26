package com.acme.cbmkafka.bt.config;

public class RulesProcessorConfig {
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RulesProcessorConfig [name=").append(name).append("]");
		return builder.toString();
	}

}
