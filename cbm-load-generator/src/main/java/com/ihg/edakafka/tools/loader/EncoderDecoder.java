package com.acme.cbmkafka.tools.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class EncoderDecoder {
	public static void main(String [] args) throws Exception {
		String data = new String(Files.readAllBytes(Paths.get("/Users/martz/acme/EDA/messages/SynchronizeProductClasses.json")));

		byte[] compressed = compress(data.getBytes());
		String encodedString = Base64.getEncoder().encodeToString(compressed);

		System.out.println("Encoded: " + encodedString.getBytes().length / 1024 + " Kb"); 

		System.out.println(":::" + encodedString + ":::");
		
//		String decodedString = new String(Base64.getDecoder().decode(encodedString));
		
//		byte[] uncompressed = decompress(Base64.getDecoder().decode(encodedString));
		
//		System.out.println(new String(uncompressed));
		
		
		
		
		byte[] uncompressed = decompress(Base64.getDecoder().decode(encodedString));
		Inflater inflater = new Inflater();   
		inflater.setInput(uncompressed);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(uncompressed.length);  
		byte[] buffer = new byte[1024];  
		while (!inflater.finished()) {  
			int count = inflater.inflate(buffer);  
			outputStream.write(buffer, 0, count);  
		}  
		outputStream.close();  
		byte[] orig = outputStream.toByteArray();  
	}

	public static byte[] compress(byte[] data) throws IOException {  
		Deflater deflater = new Deflater();  
		deflater.setInput(data);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
		deflater.finish();  
		byte[] buffer = new byte[1024];   
		while (!deflater.finished()) {  
			int count = deflater.deflate(buffer); // returns the generated code... index  
			outputStream.write(buffer, 0, count);   
		}  
		outputStream.close();  
		byte[] output = outputStream.toByteArray();  
		System.out.println("Original: " + data.length / 1024 + " Kb");  
		System.out.println("Compressed: " + output.length / 1024 + " Kb");  
		return output;  
	}  

	public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
		Inflater inflater = new Inflater();   
		inflater.setInput(data);  
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
		byte[] buffer = new byte[1024];  
		while (!inflater.finished()) {  
			int count = inflater.inflate(buffer);  
			outputStream.write(buffer, 0, count);  
		}  
		outputStream.close();  
		byte[] output = outputStream.toByteArray();  
		System.out.println("Compressed: " + data.length);  
		System.out.println("Original: " + output.length);  
		return output;  
	} 
	
	

}
