package com.acme.cbmkafka.consumer.app;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

import com.acme.cbmkafka.consumer.config.RMSConsumerConfig;
import com.acme.cbmkafka.consumer.dto.EDAMessage;
import com.acme.cbmkafka.consumer.serialization.JsonDeserializer;
import com.acme.cbmkafka.consumer.serialization.JsonSerializer;
import com.acme.cbmkafka.consumer.util.RMSConsumerConfigParser;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RMSConsumerDriver {
	
	final static Logger logger = LogManager.getLogger(RMSConsumerDriver.class.getName());
	
    public static void main(String[] args) throws Exception {

    	RMSConsumerConfig rmsConfig = RMSConsumerConfigParser.parse(args[0], RMSConsumerConfig.class);
    	
    	logger.info(rmsConfig);
    	
        StreamsConfig streamingConfig = new StreamsConfig(getProperties(rmsConfig));

        StringDeserializer stringDeserializer = new StringDeserializer();
        StringSerializer stringSerializer = new StringSerializer();
        
        JsonSerializer<EDAMessage> cbmMessageSerializer = new JsonSerializer<>();
        JsonDeserializer<EDAMessage> cbmMessageDeserializer = new JsonDeserializer<>(EDAMessage.class);
                
        Topology topologyBuilder = new Topology();
        topologyBuilder.addSource(rmsConfig.getStreamsSourceConfig().getName(), stringDeserializer, cbmMessageDeserializer, rmsConfig.getStreamsSourceConfig().getTopic())
                .addProcessor(rmsConfig.getFilterProcessorConfig().getName(), () -> new FilterProcessor(rmsConfig.getFilterProcessorConfig()), rmsConfig.getStreamsSourceConfig().getName())
                .addProcessor(rmsConfig.getRMSRestCallProcessorConfig().getName(), () -> new RMSRestCallProcessor(rmsConfig.getRMSRestCallProcessorConfig()), rmsConfig.getFilterProcessorConfig().getName());
        
        for(int ii = 0; ii < rmsConfig.getStreamsSinkConfigList().size(); ii++) {
            topologyBuilder.addSink(rmsConfig.getStreamsSinkConfigList().get(ii).getName(), 
            		                rmsConfig.getStreamsSinkConfigList().get(ii).getTopic(), 
            		                stringSerializer, stringSerializer, 
            		                rmsConfig.getRMSRestCallProcessorConfig().getName());
        }

        System.out.println("Starting RMSConsumerDriver :" + rmsConfig.getApplicationId());
        logger.info("Initializing RMSConsumerDriver streams app :" + rmsConfig.getApplicationId());
        KafkaStreams streaming = new KafkaStreams(topologyBuilder, streamingConfig);
        
        streaming.cleanUp();
        
        streaming.start();
        System.out.println("Started RMSConsumerDriver :" + rmsConfig.getApplicationId() + " with " + rmsConfig.getNumOfThreads() + " thread(s)");
        logger.info("RMSConsumerDriver ready for messages");
        
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
            	logger.warn("Received terminate signal. Shutting down.");
            	streaming.close();
            }
        }));
    }

    private static Properties getProperties(RMSConsumerConfig rmsConfig) {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, rmsConfig.getClientId());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, rmsConfig.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, rmsConfig.getBrokerList());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, rmsConfig.getReplicationFactor());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, rmsConfig.getAutoOffsetReset());
        
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, rmsConfig.getNumOfThreads()); 
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, rmsConfig.getProcessingGuarantee());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, rmsConfig.getCommitInterval());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, rmsConfig.getMaxPollRecords());
        
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);     
        
        return props;
    }
}
