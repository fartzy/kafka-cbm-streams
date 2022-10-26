package com.acme.cbmkafka.tools.generator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class StringFieldGenerator extends FieldGenerator {
	public final String SELECTION_TYPE_ROUNDROBIN = "roundrobin";
	public final String SELECTION_TYPE_RANDOM = "random";
	
	protected int minVal = 0;
	protected int maxVal = 0;
	protected ArrayList<String> sampleList = new ArrayList<String>();
	
	protected String selection = SELECTION_TYPE_RANDOM;
	protected int sampleIndex = 0;
	
    public StringFieldGenerator(String configLine) {
    	super(configLine);
    	
    	if(sampleType.equalsIgnoreCase("list")) {
    		String [] listVals = sampleData.split(",");
    		
    		if(listVals != null && listVals.length > 0) {
    			for(String listVal : listVals) {
    			    sampleList.add(listVal);
    			}
    		}
    		
    		selection = getSelection(configLine);
    	} else if(sampleType.equalsIgnoreCase("file")) {
    		try {
    	        Path filePath = Paths.get(sampleData);
    	        List<String> samples = Files.readAllLines(filePath, StandardCharsets.UTF_8);
    	        
    	        if(samples != null && samples.size() > 0) {
    	        	for(String sample : samples) {
    	        		sampleList.add(sample);
    	        	}
    	        }
    	        
    	        selection = getSelection(configLine);
    		} catch(Exception ex) {
    			System.out.println("Caught exception while reading the config field sample file :" + sampleData + ":");
    			ex.printStackTrace();
    		}
    	} else if(sampleType.toLowerCase().endsWith("blob")) {
    		String [] rangeVals = sampleData.split("-");
    		
    		if(rangeVals != null && rangeVals.length == 2) {
    			minVal = convertToInt(rangeVals[0]);
    			maxVal = convertToInt(rangeVals[1]);  			
    		} else {
    			System.out.println("Cannot generate blobs. Invalid range values for sampleType blob :" + sampleData + ":");
    		}
    	}
    }
    
    public Object generateField() {
    	String retVal = getDefaultVal();
    	
    	if(sampleType.equalsIgnoreCase("list") ||
    			sampleType.equalsIgnoreCase("file")) {
    		
    		if(sampleList.size() > 0) {
    		    int index = getSampleIndex();
    		
    		    if(index >= 0) {
    			    retVal = sampleList.get(index);
    		    }
    		}
    		
    	} else if(sampleType.toLowerCase().endsWith("blob")) {
    		int size = getNextRandomIntFromRange(minVal, maxVal);
    		
    		if(sampleType.toLowerCase().startsWith("alphanumeric")) {
    			retVal = RandomStringUtils.randomAlphanumeric(size);
    		} else if(sampleType.toLowerCase().startsWith("alpha")) {
    			retVal = RandomStringUtils.randomAlphabetic(size);
    		} else if(sampleType.toLowerCase().startsWith("numeric")) {
    			retVal = RandomStringUtils.randomNumeric(size);
    		} else if(sampleType.toLowerCase().startsWith("ascii")) {
    			retVal = RandomStringUtils.randomAscii(size);
    		} else {
    			System.out.println("Invalid sampleType :" + sampleType + ": Cannot generate field. Using default :" + retVal + ":");
    		}
    		
    	} else {
    		System.out.println("Invalid sampleType :" + sampleType + ": Cannot generate field. Using default :" + retVal + ":");
    	}
    	
    	return retVal;
    }
    
    private String getSelection(String configLine) {
    	String selectionType = SELECTION_TYPE_RANDOM;
		if(configLine != null) {       
			String [] tokens = configLine.split("::");

			if(tokens != null && tokens.length == 6) {
				selectionType = tokens[5];

				if(selectionType != null && selectionType.trim().length() > 0) {
					if(selectionType.equalsIgnoreCase(SELECTION_TYPE_ROUNDROBIN)) {
						selectionType = SELECTION_TYPE_ROUNDROBIN;
					}
				}
			}
		}
		
		return selectionType;
    }
    
    private int getSampleIndex() {
    	int retIndex = 0;
    	
    	if(selection.equalsIgnoreCase(SELECTION_TYPE_RANDOM)) {
    		retIndex = getNextRandomIntFromRange(0, (sampleList.size() - 1));
    	} else if(selection.equalsIgnoreCase(SELECTION_TYPE_ROUNDROBIN)) {
    		retIndex = sampleIndex++;
    		
    		if(sampleIndex == sampleList.size()) {
    			sampleIndex = 0;
    		}
    	}
    	
    	return retIndex;
    }
}
