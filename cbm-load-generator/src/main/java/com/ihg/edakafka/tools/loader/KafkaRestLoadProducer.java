package com.acme.cbmkafka.tools.loader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.acme.cbmkafka.tools.generator.FieldGenerator;
import com.acme.cbmkafka.tools.generator.FieldGeneratorFactory;

public class KafkaRestLoadProducer {
	// config files
	public static final String LOADER_PROPERTIES_FILE         = "kafkaloader.properties";
	public static final String LOADER_TEMPLATE_FILE           = "kafkamessage.template";
	public static final String LOADER_FIELD_CONFIG_FILE       = "kafkamessage.fields";

	// prop file keys
	public static final String KAFKA_REST_PROXY_BASE_URL_KEY      = "kafka.load.producer.resturl";
	public static final String KAFKA_REST_PROXY_TOPIC_NAME_KEY    = "kafka.load.producer.topic";
	public static final String KAFKA_PRODUCER_EVENT_COUNT_KEY     = "kafka.load.producer.eventcount";
	public static final String KAFKA_PRODUCER_EVENT_DELAY_KEY     = "kafka.load.producer.eventdelay";	
	public static final String KAFKA_REST_PROXY_CONN_TIMEOUT_KEY  = "kafka.load.producer.conntimeout";	
	public static final String KAFKA_REST_PROXY_SOCKET_TIMEOUT_KEY   = "kafka.load.producer.sockettimeout";	

	private static KafkaRestLoadProducer instance = null;

	private HashMap<String, FieldGenerator> generatorMap = new HashMap<String, FieldGenerator>();
	private List<String> templateStrList = new ArrayList<String>();

	private KafkaRestProducer producer;

	private int eventCount = 0;
	private int eventDelay = 0;
	private int connTimeout = 0;
	private int socketTimeout = 0;

	private KafkaRestLoadProducer() {

	}

	public static KafkaRestLoadProducer getInstance() {
		if(instance == null) {
			instance = new KafkaRestLoadProducer();
		}

		return instance;
	}

	public void initialize(String configPath) throws Exception {

		//Read the loader properties file
		InputStream fis = null;
		Properties configProps = new Properties();

		Path filepath = Paths.get(configPath, LOADER_PROPERTIES_FILE);

		try {
			System.out.println("Reading kafka loader properties file :" + filepath.toString());
			fis = new FileInputStream(filepath.toString());

			// load the properties
			configProps.load(fis);
		} catch(Exception ex) {
			System.err.println("Caught exception loading the kafkaloader.properties file :" + ex.getMessage());
			System.exit(-1);
		}

		String restProxyBaseUrl = (String) configProps.get(KAFKA_REST_PROXY_BASE_URL_KEY);
		String topicName = (String) configProps.get(KAFKA_REST_PROXY_TOPIC_NAME_KEY);

		if(restProxyBaseUrl == null || restProxyBaseUrl.trim().length() == 0 ||
				topicName == null || topicName.trim().length() == 0) {
			System.err.println("Invalid kafka config params. restProxyUrl = :" + restProxyBaseUrl + ": topicName :" + topicName + ":");
			System.exit(-1);
		}

		// read the generation params
		try {
			eventCount = Integer.parseInt(((String) configProps.get(KAFKA_PRODUCER_EVENT_COUNT_KEY)).trim());
			eventDelay = Integer.parseInt(((String) configProps.get(KAFKA_PRODUCER_EVENT_DELAY_KEY)).trim());
			connTimeout = Integer.parseInt(((String) configProps.get(KAFKA_REST_PROXY_CONN_TIMEOUT_KEY)).trim());
			socketTimeout = Integer.parseInt(((String) configProps.get(KAFKA_REST_PROXY_SOCKET_TIMEOUT_KEY)).trim());
		} catch (Exception ex) {
			System.out.println("Invalid generation params. Using defaults.");
			ex.printStackTrace();
		}

		// Read the template file
		Path templatePath = Paths.get(configPath, LOADER_TEMPLATE_FILE);
		List<String> templateFileList = Files.readAllLines(templatePath, StandardCharsets.UTF_8);

		String oneTemplate = "";

		for(String templateLine : templateFileList) {
			if(!templateLine.trim().startsWith("#")) {
				oneTemplate = oneTemplate + templateLine;
			}
		}

		templateStrList.add(oneTemplate);

		// Read the field config file
		Path fieldConfigPath = Paths.get(configPath, LOADER_FIELD_CONFIG_FILE);
		List<String> fieldConfigs = Files.readAllLines(fieldConfigPath, StandardCharsets.UTF_8);

		for(String fieldConfig : fieldConfigs) {

			if(fieldConfig == null || fieldConfig.trim().startsWith("#")) {
				continue;
			}

			FieldGenerator generator = FieldGeneratorFactory.getInstance().createFieldGenerator(fieldConfig);

			if(generator != null) {
				generatorMap.put(generator.getFieldName(), generator);
			}
		}	    

		// Initialize the kafka producer
		producer = new KafkaRestProducer(restProxyBaseUrl, topicName, connTimeout, socketTimeout);
	}

	public void generateEvents() {
		if(eventCount > 0) {
			// Start generating
			for(int ii=0; ii < eventCount; ii++) {
				generate();
			}	
		} else {
			// indefinite generation
			while(true) {
				generate();
			}
		}
	}

	private void generate() {
		// Start generating
		for(String templateStr : templateStrList) {

			String generatedEvent = templateStr;

			for(String key : generatorMap.keySet()) {
				FieldGenerator generator = (FieldGenerator) generatorMap.get(key);       	        
				Object field = generator.generateField();

				generatedEvent = generatedEvent.replace("${" + key + "}", field.toString());
			}

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readValue(generatedEvent, JsonNode.class);
				generatedEvent = jsonNode.toString();
			} catch(Exception ex) {
				System.out.println("Caught exception with json formatting :" + ex.getMessage());
				ex.printStackTrace();
			}

			System.out.println("Generated event :" + generatedEvent);

			if(eventDelay > 0 ) {
				try {
					Thread.sleep(eventDelay);
				} catch (Exception ex) {
					System.out.println("Caught exception during the delay. eventDelay :" + eventDelay + ":");
				}
			}

			// send the event
		    producer.sendEvent(generatedEvent);
		}	
	}


	public static void main(String [] args) {

		String configFileLocation = null;

		if (args.length > 0 && args[0] != null) {
			configFileLocation = args[0].trim();
		} else {
			System.out.println("Please enter the path where the kafkamessage.properties, kafkamessage.template and kafkamessage.fields are located... ");
			System.out.println("Usage : java -cp kafka-load-producer.jar com.acme.cbmkafka.tools.loader.KafkaRestLoadProducer <config-files-location>");
			System.exit(-1);
		}

		try {
			KafkaRestLoadProducer.getInstance().initialize(configFileLocation);

			long generateStartTime = System.currentTimeMillis();
			KafkaRestLoadProducer.getInstance().generateEvents();

			System.out.println("Generated events in :" + (System.currentTimeMillis() - generateStartTime) + " msecs");
		} catch (Exception ex) {
			System.out.println("Caught exception :" + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
