package com.acme.cbmkafka.tools.generator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DateFieldGenerator extends FieldGenerator {
	protected int minVal = 0;
	protected int maxVal = 0;
	protected ArrayList<String> sampleList = new ArrayList<String>();
	
    public DateFieldGenerator(String configLine) {
    	super(configLine);
    	
        if(sampleType.equalsIgnoreCase("list")) {
    		String [] listVals = sampleData.split(",");
    		
    		if(listVals != null && listVals.length > 0) {
    			for(String listVal : listVals) {
    			    sampleList.add(listVal);
    			}
    		}
    	} else if(sampleType.equalsIgnoreCase("file")) {
    		try {
    	        Path filePath = Paths.get(sampleData);
    	        List<String> samples = Files.readAllLines(filePath, StandardCharsets.UTF_8);
    	        
    	        if(samples != null && samples.size() > 0) {
    	        	for(String sample : samples) {
    	        		sampleList.add(sample);
    	        	}
    	        }
    	        
    		} catch(Exception ex) {
    			System.out.println("Caught exception while reading the config field sample file :" + sampleData + ":");
    			ex.printStackTrace();
    		}
    	}
    }
    
    public Object generateField() {
    	String retVal = getDefaultVal();
    	
    	if(sampleType.equalsIgnoreCase("list") ||
    			sampleType.equalsIgnoreCase("file")) {
    		
    		if(sampleList.size() > 0) {
    		    int index = getNextRandomIntFromRange(0, (sampleList.size() - 1));
    		
    		    if(index >= 0) {
    			    retVal = sampleList.get(index);
    		    }
    		}
    		
    	} else if(sampleType.equalsIgnoreCase("current")) {
    		Date currentTime = new Date();
    		
    		retVal = new SimpleDateFormat(getSampleData()).format(currentTime);
    	} else {
    		System.out.println("Invalid sampleType :" + sampleType + ": Cannot generate field.");
    	}
    	
    	return retVal;
    }
}