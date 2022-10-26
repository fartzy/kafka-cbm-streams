package com.acme.cbmkafka.tools.partitioner;

public class PartitionProcessor {

	private PartitionConfig config;
	
	public PartitionProcessor() {
		
	}
	
	public int getPartition(String hotelKey) {
		return config.getMapping().get(hotelKey);
	}
}
