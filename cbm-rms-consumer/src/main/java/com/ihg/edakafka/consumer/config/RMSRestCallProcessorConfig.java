package com.acme.cbmkafka.consumer.config;

import java.util.List;

import com.acme.cbmkafka.consumer.dto.KeyValuePair;

public class RMSRestCallProcessorConfig {

	private String name;
    private String baseRestUrl;
    private int connectTimeout = 100;
    private int socketTimeout = 100;
    
    private List<KeyValuePair> httpHeaders;
    
    private String successSinkName;
    private String errorSinkName;
    
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBaseRestUrl() {
		return baseRestUrl;
	}
	
	public void setBaseRestUrl(String baseRestUrl) {
		this.baseRestUrl = baseRestUrl;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	public int getSocketTimeout() {
		return socketTimeout;
	}
	
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
	public List<KeyValuePair> getHttpHeaders() {
		return httpHeaders;
	}
	
	public void setHttpHeaders(List<KeyValuePair> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	
	public String getSuccessSinkName() {
		return successSinkName;
	}
	
	public void setSuccessSinkName(String successSinkName) {
		this.successSinkName = successSinkName;
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
		builder.append("RMSRestCallProcessorConfig [name=").append(name).append(", baseRestUrl=").append(baseRestUrl)
				.append(", connectTimeout=").append(connectTimeout).append(", socketTimeout=").append(socketTimeout)
				.append(", httpHeaders=").append(httpHeaders).append(", successSinkName=").append(successSinkName)
				.append(", errorSinkName=").append(errorSinkName).append("]");
		return builder.toString();
	}
}
