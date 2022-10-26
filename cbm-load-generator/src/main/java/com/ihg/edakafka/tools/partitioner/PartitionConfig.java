package com.acme.cbmkafka.tools.partitioner;

import java.util.HashMap;

public class PartitionConfig {
	
	private static PartitionConfig partitionConfig;
	
	private HashMap <String, Integer> map;  
	
	private PartitionConfig(){}
	
	private PartitionConfig(String pathToConfig){
		loadMapping(pathToConfig);
	}
	
	public PartitionConfig getInstance(){
        if(partitionConfig == null){
        	partitionConfig = new PartitionConfig("dummy_path");
        }
        return partitionConfig;
    }
	
	private void loadMapping(String pathToConfig) {
        map = new HashMap<String, Integer>();
        map.put("ATLFR", 0);
        map.put("DCSGH", 0);
        map.put("JKSOO", 0);
        map.put("IXGES", 0);
        map.put("AAAAA", 1);
        map.put("BBBBB", 1);
        map.put("CCCCC", 1);
        map.put("DDDDD", 1);
	}
	
	public HashMap<String, Integer> getMapping() {
		return map;
	}
	
}
