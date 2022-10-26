package com.acme.cbmkafka.bt.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessTierConfig extends StreamsAppConfig {

    @JsonProperty("streams-source-config")
    private StreamsSourceConfig sourceConfig;
    
    @JsonProperty("message-order-processor-config")
    private MessageOrderProcessorConfig mopConfig;
    
    @JsonProperty("rules-processor-config")
    private RulesProcessorConfig rulesConfig;
    
    @JsonProperty("message-route-processor-config")
    private MessageRouteProcessorConfig mrpConfig;
    
    @JsonProperty("streams-sink-config")
    private List<StreamsSinkConfig> sinkConfigList;

    public StreamsSourceConfig getStreamsSourceConfig() {
        return sourceConfig;
    }

    public void setStreamsSourceConfig(StreamsSourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public MessageOrderProcessorConfig getMessageOrderProcessorConfig() {
        return mopConfig;
    }

    public void setMessageOrderProcessorConfig(MessageOrderProcessorConfig mopConfig) {
        this.mopConfig = mopConfig;
    }

    public RulesProcessorConfig getRulesProcessorConfig() {
        return rulesConfig;
    }

    public void setRulesProcessorConfig(RulesProcessorConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
    }
    
    public MessageRouteProcessorConfig getMessageRouteProcessorConfig() {
        return mrpConfig;
    }

    public void setMessageRouteProcessorConfig(MessageRouteProcessorConfig mrpConfig) {
        this.mrpConfig = mrpConfig;
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
		builder.append("BusinessTierConfig [sourceConfig=").append(sourceConfig).append(", mopConfig=")
				.append(mopConfig).append(", mrpConfig=").append(mrpConfig).append(", sinkConfig=").append(sinkConfigList)
				.append("]");
		return builder.toString();
	}
}