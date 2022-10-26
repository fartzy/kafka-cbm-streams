package com.acme.cbmkafka.bt.config;

public class StreamsAppConfig {

	public static final String CONFIG_FILE_NAME = "streams-config.yaml";

    private String applicationId;
    private String clientId;
    private String groupId;
    private String brokerList;
    private Integer replicationFactor = 1;
    private String autoOffsetReset;
    private Integer numOfThreads = 1;
    private String processingGuarantee = "exactly_once";
    private Integer commitInterval = 200;
    private Integer maxPollRecords = 1000;
    
	public String getApplicationId() {
		return applicationId;
	}
	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getBrokerList() {
		return brokerList;
	}
	
	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}
	
	public Integer getReplicationFactor() {
		return replicationFactor;
	}
	
	public void setReplicationFactor(Integer replicationFactor) {
		this.replicationFactor = replicationFactor;
	}
	
	public String getAutoOffsetReset() {
		return autoOffsetReset;
	}
	
	public void setAutoOffsetReset(String autoOffsetReset) {
		this.autoOffsetReset = autoOffsetReset;
	}
	
	public Integer getNumOfThreads() {
		return numOfThreads;
	}
	
	public void setNumOfThreads(Integer numOfThreads) {
		this.numOfThreads = numOfThreads;
	}
	
	
	public String getProcessingGuarantee() {
		return processingGuarantee;
	}

	public void setProcessingGuarantee(String processingGuarantee) {
		this.processingGuarantee = processingGuarantee;
	}

	public Integer getCommitInterval() {
		return commitInterval;
	}

	public void setCommitInterval(Integer commitInterval) {
		this.commitInterval = commitInterval;
	}

	public Integer getMaxPollRecords() {
		return maxPollRecords;
	}

	public void setMaxPollRecords(Integer maxPollRecords) {
		this.maxPollRecords = maxPollRecords;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StreamsAppConfig [applicationId=").append(applicationId).append(", clientId=").append(clientId)
				.append(", groupId=").append(groupId).append(", brokerList=").append(brokerList)
				.append(", replicationFactor=").append(replicationFactor).append(", autoOffsetReset=")
				.append(autoOffsetReset).append(", numOfThreads=").append(numOfThreads).append(", processingGuarantee=")
				.append(processingGuarantee).append(", commitInterval=").append(commitInterval)
				.append(", maxPollRecords=").append(maxPollRecords).append("]");
		return builder.toString();
	}
}