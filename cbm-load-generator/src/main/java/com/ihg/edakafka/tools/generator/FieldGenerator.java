package com.acme.cbmkafka.tools.generator;

import java.util.Random;

public abstract class FieldGenerator {
	
	protected String fieldName = null;
	protected String fieldType = null;
	protected String defaultVal = null;
	protected String sampleType = null;
	protected String sampleData = null;
	
	public FieldGenerator(String configLine) {
        if(configLine != null) {       
            String [] tokens = configLine.split("::");
        
            if(tokens != null && tokens.length >= 5) {
                fieldName = tokens[0];
                fieldType = tokens[1];
                defaultVal = tokens[2];
                sampleType = tokens[3];
                sampleData = tokens[4];
            }
        }
	}
	
	public abstract Object generateField();

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultType) {
		this.defaultVal = defaultType;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getSampleData() {
		return sampleData;
	}

	public void setSampleData(String sampleData) {
		this.sampleData = sampleData;
	}
	
	protected int getNextRandomIntFromRange(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}
	
	public int convertToInt(String strVal) {
		int intVal = 0;
		try {
			intVal = Integer.parseInt(strVal);
		} catch (Exception ex) {
			System.out.println("Invalid param. Cannot convert to int :" + strVal + ":");
		}
		
		return intVal;
	}
}
