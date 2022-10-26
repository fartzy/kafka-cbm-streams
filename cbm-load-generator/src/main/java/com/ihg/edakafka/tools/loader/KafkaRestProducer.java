package com.acme.cbmkafka.tools.loader;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class KafkaRestProducer
{
	private final String restUrl;
	private final String topicName;
	private int connectTimeout;
	private int socketTimeout;

	public KafkaRestProducer(String baseUrl, String topic, int connTimeout, int sockTimeout)
	{		
        restUrl = baseUrl + "/topics/" + topic;

        topicName = topic;
        connectTimeout = connTimeout;
        socketTimeout = sockTimeout;
	}

	public void sendEvent(String eventStr) {
		String restResponse = "SUCCESS";
		
		try {
			restResponse = Request.Post(restUrl)
				.connectTimeout(connectTimeout)
				.socketTimeout(socketTimeout)
				.addHeader("Content-Type", "application/vnd.kafka.json.v2+json")
				.addHeader("Accept", "application/vnd.kafka.v2+json")
				.bodyString(eventStr, ContentType.APPLICATION_JSON)
				.execute().returnContent().asString();					
		} catch(Throwable th) {
			restResponse = "ERROR : " + th.getMessage();
			th.printStackTrace();
		}
		
		System.out.println("Send Status : " + restResponse);
	}
	
	public static void main(String [] args) {
		if(args == null || args.length < 4) {
			System.out.println("Invalid args. Need <restBaseUrl>, <topicname>, <connTimeout>, and <socketTimeout>");
			System.out.println("Ex : KafkaRestProducer http://192.168.50.101:8082 test_topic 10 10");
			
			System.exit(0);
		}
		
		int cTimeout = 0;
		int sTimeout = 0;
		
		try {
			cTimeout = Integer.parseInt(args[2]);
			sTimeout = Integer.parseInt(args[3]);
			
		} catch(Exception ex) {
			System.out.println("Invalid timeout params :" + args[2] + ":" + args[3] + ": Using defaults");
		}
		
		KafkaRestProducer sp = new KafkaRestProducer(args[0], args[1], cTimeout, sTimeout);
		
		sp.sendEvent("{\"msg\":\"example message\"}");
	}
}