package com.acme.cbmkafka.consumer.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.acme.cbmkafka.consumer.serialization.KeyValueListDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EDAMessage implements Serializable {
	
	public static final String PROPERTY_CODE_KEY = "hotelcode";
	
	EDAMetadata metadata;

	@JsonProperty(value = "context")
	@JsonDeserialize(using = KeyValueListDeserializer.class)
	private List<KeyValuePair> context;

	EDASequence sequence;
	String message;
	
	public EDAMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(EDAMetadata metadata) {
		this.metadata = metadata;
	}

	public List<KeyValuePair> getContext() {
		return context;
	}

	public void setContext(List<KeyValuePair> context) {
		this.context = context;
	}

	public EDASequence getSequence() {
		return sequence;
	}

	public void setSequence(EDASequence sequence) {
		this.sequence = sequence;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	public String getPropertyCode() {
		String retValue = "";

		for(KeyValuePair kv : context) {
			if(kv.getKey().trim().equalsIgnoreCase(PROPERTY_CODE_KEY)) {
				return kv.getValue();
			}
		}

		return retValue;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EDAMessage [metadata=").append(metadata).append(", context=").append(context)
		.append(", sequence=").append(sequence).append(", message=").append(message).append("]");
		return builder.toString();
	}	
}
