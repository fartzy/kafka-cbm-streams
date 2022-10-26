package com.acme.cbmkafka.bt.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.acme.cbmkafka.bt.serialization.KeyValueListDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EDAMessage implements Serializable, Comparable {
	
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
    public int compareTo(Object o) {
           int result =this.getSequence().getPart1().compareTo(((EDAMessage)o).getSequence().getPart1());
           if(result ==0) {
               result =  (this.getSequence().getPart2() < ((EDAMessage) o).getSequence().getPart2() ? -1 : (this.getSequence().getPart2() == ((EDAMessage) o).getSequence().getPart2() ? 0 : 1));
           }
           return  result;
    }
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EDAMessage [metadata=").append(metadata).append(", context=").append(context)
		.append(", sequence=").append(sequence).append(", message=").append(message).append("]");
		return builder.toString();
	}	
}
