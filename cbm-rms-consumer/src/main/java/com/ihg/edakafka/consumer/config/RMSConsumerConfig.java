package com.acme.cbmkafka.consumer.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RMSConsumerConfig extends StreamsAppConfig {

    @JsonProperty("streams-source-config")
    private StreamsSourceConfig sourceConfig;
    
    @JsonProperty("filter-processor-config")
    private FilterProcessorConfig filterConfig;
    
    @JsonProperty("rms-rest-call-processor-config")
    private RMSRestCallProcessorConfig rcpConfig;
    
    @JsonProperty("streams-sink-config")
    private List<StreamsSinkConfig> sinkConfigList;

    public StreamsSourceConfig getStreamsSourceConfig() {
        return sourceConfig;
    }

    public void setStreamsSourceConfig(StreamsSourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public FilterProcessorConfig getFilterProcessorConfig() {
        return filterConfig;
    }

    public void setFilterProcessorConfig(FilterProcessorConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public RMSRestCallProcessorConfig getRMSRestCallProcessorConfig() {
        return rcpConfig;
    }

    public void setRMSRestCallProcessorConfig(RMSRestCallProcessorConfig rcpConfig) {
        this.rcpConfig = rcpConfig;
    }

    public List<StreamsSinkConfig> getStreamsSinkConfigList() {
        return sinkConfigList;
    }

    public void setStreamsSinkConfigList(List<StreamsSinkConfig> sinkConfigList) {
        this.sinkConfigList = sinkConfigList;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("BusinessTierConfig [sourceConfig=").append(sourceConfig).append(", filterConfig=")
				.append(filterConfig).append(", rcpConfig=").append(rcpConfig).append(", sinkConfig=").append(sinkConfigList)
				.append("]");
		return builder.toString();
	}
}