package com.acme.cbmkafka.tools.generator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IntFieldGenerator extends FieldGenerator {
	protected int minVal = 0;
	protected int maxVal = 0;
	protected int seqStart = 0;
	protected int currentSeq = 0;
	
	protected ArrayList<Integer> sampleList = new ArrayList<Integer>();
	
    public IntFieldGenerator(String configLine) {
    	super(configLine);
    	
    	if(sampleType.equalsIgnoreCase("range")) {
    		String [] rangeVals = sampleData.split("-");
    		
    		if(rangeVals != null && rangeVals.length == 2) {
    			minVal = convertToInt(rangeVals[0]);
    			maxVal = convertToInt(rangeVals[1]);
    		}
    	} else if(sampleType.equalsIgnoreCase("list")) {
    		String [] listVals = sampleData.split(",");
    		
    		if(listVals != null && listVals.length > 0) {
    			for(String listVal : listVals) {
    			    sampleList.add(convertToInt(listVal));
    			}
    		}
    	} else if(sampleType.equalsIgnoreCase("file")) {
    		try {
    	        Path filePath = Paths.get(sampleData);
    	        List<String> samples = Files.readAllLines(filePath, StandardCharsets.UTF_8);
    	        
    	        if(samples != null && samples.size() > 0) {
    	        	for(String sample : samples) {
    	        		sampleList.add(convertToInt(sample));
    	        	}
    	        }
    	        
    		} catch(Exception ex) {
    			System.out.println("Caught exception while reading the config field sample file :" + sampleData + ":");
    			ex.printStackTrace();
    		}
    	} else if(sampleType.equalsIgnoreCase("sequence")) {
            seqStart = convertToInt(sampleData); 
            currentSeq = seqStart;
    	}
    }
    
    public Object generateField() {
    	int retVal = 0;
    	
    	if(sampleType.equalsIgnoreCase("range")) {
    		retVal = getNextRandomIntFromRange(minVal, maxVal);   		
    	} else if(sampleType.equalsIgnoreCase("sequence")) {
    		retVal = currentSeq;
    		currentSeq++;
    	}
    	else if(sampleType.equalsIgnoreCase("list") ||
    			sampleType.equalsIgnoreCase("file")) {
    		
    		if(sampleList.size() > 0) {
    		    int index = getNextRandomIntFromRange(0, (sampleList.size() - 1));
    		
    		    if(index >= 0) {
    			    retVal = sampleList.get(index);
    		    }
    		}
    		
    	} else {
    		System.out.println("Invalid sampleType :" + sampleType + ": Cannot generate field.");
    	}
    	
    	return new Integer(retVal);
    }
	
//	private int convertToInt(String strVal) {
//		int intVal = 0;
//		try {
//			intVal = Integer.parseInt(strVal);
//		} catch (Exception ex) {
//			System.out.println("Invalid param. Cannot convert to int :" + strVal + ":");
//		}
//		
//		return intVal;
//	}
}
