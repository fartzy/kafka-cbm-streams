package com.acme.cbmkafka.tools.generator;

public class FieldGeneratorFactory {

	private static FieldGeneratorFactory instance = null;

	private FieldGeneratorFactory() {

	}

	public static FieldGeneratorFactory getInstance() {
		if(instance == null) {
			instance = new FieldGeneratorFactory();
		}

		return instance;
	}

	public FieldGenerator createFieldGenerator(String fieldConfig) {		
		FieldGenerator generator = null;
        if(fieldConfig == null || fieldConfig.length() == 0) {
        	return generator;
        }
        
        String [] tokens = fieldConfig.split("::");
        
        if(tokens == null || tokens.length < 5) {
        	return generator;
        }
        
        if(tokens[1] == null || tokens[1].length() == 0) {
        	return generator;
        }
        
        if(tokens[1].equalsIgnoreCase("INT")) {
        	generator = new IntFieldGenerator(fieldConfig);
//        } else if(tokens[1].equalsIgnoreCase("LONG")) {
//        	generator = new LongFieldGenerator(fieldConfig);
//        } else if(tokens[1].equalsIgnoreCase("DOUBLE")) {
//        	generator = new DoubleFieldGenerator(fieldConfig);
        } else if(tokens[1].equalsIgnoreCase("STRING")) {
        	generator = new StringFieldGenerator(fieldConfig);
        } else if(tokens[1].equalsIgnoreCase("DATE")) {
        	generator = new DateFieldGenerator(fieldConfig);
        } else if(tokens[1].equalsIgnoreCase("EDASEQNUM")) {
        	generator = new EDASequenceNumberGenerator(fieldConfig);       	
        } else {
        	return generator;
        }
        
        return generator;
	}
}
