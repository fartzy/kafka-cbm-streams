package com.acme.cbmkafka.bt.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageRouteProcessorConfig {
	
	private String name;
    private String errorSinkName;
	
	@JsonProperty("routes-config")
	private List<RouteConfig> routesConfig;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RouteConfig> getRoutesConfig() {
		return routesConfig;
	}

	public void setRoutesConfig(List<RouteConfig> routesConfig) {
		this.routesConfig = routesConfig;
	}

	public String getErrorSinkName() {
		return errorSinkName;
	}
	
	public void setErrorSinkName(String errorSinkName) {
		this.errorSinkName = errorSinkName;
	}	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageRouteProcessorConfig [name=").append(name).append(", routes=").append(routesConfig)
				.append("]");
		return builder.toString();
	}
}
