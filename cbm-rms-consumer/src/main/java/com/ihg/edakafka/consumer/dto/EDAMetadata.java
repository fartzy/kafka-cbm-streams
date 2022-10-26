package com.acme.cbmkafka.consumer.dto;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EDAMetadata implements Serializable {
    	
    private String id;
    private String type;
    private String source;
    private List<String> destination;
    private String action;
    private String generatedTime;   
    private String transmissionTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<String> getDestination() {
		return destination;
	}

	public void setDestination(List<String> destination) {
		this.destination = destination;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getGeneratedTime() {
		return generatedTime;
	}

	public void setGeneratedTime(String generatedTime) {
		this.generatedTime = generatedTime;
	}

	public String getTransmissionTime() {
		return transmissionTime;
	}

	public void setTransmissionTime(String transmissionTime) {
		this.transmissionTime = transmissionTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EDAMetadata [id=").append(id).append(", type=").append(type).append(", source=").append(source)
				.append(", destination=").append(destination).append(", action=").append(action)
				.append(", generatedTime=").append(generatedTime).append(", transmissionTime=").append(transmissionTime)
				.append("]");
		return builder.toString();
	}
}
