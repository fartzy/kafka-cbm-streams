package com.acme.cbmkafka.tools.generator;

import java.util.ArrayList;
import java.util.Random;

public class EDASequenceNumberGenerator extends FieldGenerator {
	protected int minVal = 0;
	protected int maxVal = 0;
	protected int seqStart = 0;
	protected int currentSeq = 0;

	protected int orderDelayRangeLow = 0;
	protected int orderDelayRangeHigh = 0;
	protected int orderDelayCount = 0;

	protected int orderSkipRangeLow = 0;
	protected int orderSkipRangeHigh = 0;
	protected int orderSkipCount = 0;

	protected int orderDelayCounter = 0;
	protected int orderSkipCounter = 0;

	protected int delaySeqNum = 0;
	protected int skipSeqNum = 0;

	protected ArrayList<Integer> sampleList = new ArrayList<Integer>();

	public EDASequenceNumberGenerator(String configLine) {
		super(configLine);

		// Get the parameters required for EDA sequence generation
		if(configLine != null) {       
			String [] tokens = configLine.split("::");

			if(tokens != null && tokens.length == 7) {
				String delayConfig = tokens[5];
				String skipConfig = tokens[6];

				if(delayConfig != null) {
					String [] delayTokens = delayConfig.split("-");
					if(delayTokens != null && delayTokens.length == 3) {
						orderDelayRangeLow = convertToInt(delayTokens[0]); 
						orderDelayRangeHigh = convertToInt(delayTokens[1]);
						orderDelayCount = convertToInt(delayTokens[2]);
					}
				}

				if(skipConfig != null) {
					String [] skipTokens = skipConfig.split("-");
					if(skipTokens != null && skipTokens.length == 3) {
						orderSkipRangeLow = convertToInt(skipTokens[0]); 
						orderSkipRangeHigh = convertToInt(skipTokens[1]);
						orderSkipCount = convertToInt(skipTokens[2]);
					}
				}
			}
		}

		if(orderDelayRangeLow > 0 && orderDelayRangeHigh > 0 && orderDelayCount > 0 &&
				orderSkipRangeLow > 0 && orderSkipRangeHigh > 0 && orderSkipCount > 0) {
			sampleType = sampleType;
		} else {
			System.out.println("No proper config for custom generation on EDASequenceNum :" + configLine + ": Defaulting to sequence sample");
			sampleType = "sequence";
		}

		if(sampleType.equalsIgnoreCase("sequence")) {
			seqStart = convertToInt(sampleData); 
			currentSeq = seqStart;
		} else if(sampleType.equalsIgnoreCase("custom")) {
			seqStart = convertToInt(sampleData); 
			currentSeq = seqStart;
			orderDelayCounter = getRandomNumberInRange(orderDelayRangeLow, orderDelayRangeHigh);
			orderSkipCounter = getRandomNumberInRange(orderSkipRangeLow, orderSkipRangeHigh);
		}

	}

	public Object generateField() {
//		System.out.println("sequenceNumber :" + currentSeq + ": delayRandom :" + orderDelayCounter + ": skipRandom :" + orderSkipCounter + ": sendAfter :" + orderDelayCount + ":");
		int retVal = 0;

		if(sampleType.equalsIgnoreCase("sequence")) {
			retVal = currentSeq;
			currentSeq++;
		}
		else if(sampleType.equalsIgnoreCase("custom")) {
			// Handle the skip first
			if(currentSeq >= orderSkipCounter) {
				currentSeq = currentSeq + 1;
				retVal = currentSeq;
				
				orderSkipCounter = currentSeq + getRandomNumberInRange(orderSkipRangeLow, orderSkipRangeHigh);
				
				if(orderDelayCounter == currentSeq) {
					orderDelayCounter = orderDelayCounter + 1;
				}
				currentSeq++;
				return new Integer(retVal);
			}
			
			if(currentSeq == orderDelayCounter) {
					delaySeqNum = currentSeq;

					currentSeq++;
					retVal = currentSeq;
					currentSeq++;

			} else {
				if(delaySeqNum > 0 && currentSeq >= (orderDelayCount + delaySeqNum)) {
					// Now send the delayed sequenceNumber
					retVal = delaySeqNum;
					
					// Reset the orderDelayCounter
					orderDelayCounter = currentSeq + getRandomNumberInRange(orderDelayRangeLow, orderDelayRangeHigh);
					delaySeqNum = 0;

				} else {
					retVal = currentSeq;
				    currentSeq++;
				}
			}

		} else {
			System.out.println("Invalid sampleType :" + sampleType + ": Cannot generate field.");
		}

		return new Integer(retVal);
	}

	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static void main(String [] args) {
		EDASequenceNumberGenerator generator = new EDASequenceNumberGenerator("A::B::C::custom::1::2-6-4::2-50-1");
		
		
		for(int ii = 0; ii < 100; ii++) {
			System.out.println(generator.generateField());
		}
	}
}

