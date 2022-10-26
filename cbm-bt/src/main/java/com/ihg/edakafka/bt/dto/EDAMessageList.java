package com.acme.cbmkafka.bt.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EDAMessageList implements Serializable {
    protected String propertyCode;
    protected Long lastSequenceNumberFlushed = -1L;
    protected Long lastSequenceNumberToFlush = -1L;
    
    protected List<EDAMessage> messageList = new ArrayList<EDAMessage>();
    
	public String getPropertyCode() {
		return propertyCode;
	}
	
	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}
	
	public List<EDAMessage> getMessageList() {
		return messageList;
	}
	
	public void setMessageList(List<EDAMessage> messageList) {
		this.messageList = messageList;
	}

	public Long getLastSequenceNumberFlushed() {
		return lastSequenceNumberFlushed;
	}

	public void setLastSequenceNumberFlushed(Long lastSequenceNumberFlushed) {
		this.lastSequenceNumberFlushed = lastSequenceNumberFlushed;
	}

	public Long getLastSequenceNumberToFlush() {
		return lastSequenceNumberToFlush;
	}

	public void setLastSequenceNumberToFlush(Long lastSequenceNumberToFlush) {
		this.lastSequenceNumberToFlush = lastSequenceNumberToFlush;
	}

	public void addMessage(EDAMessage message) {
		messageList.add(message);
	}
	
	public int getSize() {
		return messageList.size();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EDAMessageList [propertyCode=").append(propertyCode).append(", messageList=")
				.append(messageList).append("]");
		return builder.toString();
	}
}
