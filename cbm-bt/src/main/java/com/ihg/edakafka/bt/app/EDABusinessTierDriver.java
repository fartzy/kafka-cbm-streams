package com.acme.cbmkafka.bt.app;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.processor.UsePreviousTimeOnInvalidTimestamp;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.bt.config.BusinessTierConfig;
import com.acme.cbmkafka.bt.common.NonRetryableStreamsExceptionHandler;
import com.acme.cbmkafka.bt.common.EDADeserializationExceptionHandler;
import com.acme.cbmkafka.bt.common.EDAUncaughtExceptionHandler;
import com.acme.cbmkafka.bt.dto.EDAMessage;
import com.acme.cbmkafka.bt.dto.EDAMessageList;
import com.acme.cbmkafka.bt.serialization.EDAMessageListDeserializer;
import com.acme.cbmkafka.bt.serialization.EDAMessageListSerializer;
import com.acme.cbmkafka.bt.serialization.JsonDeserializer;
import com.acme.cbmkafka.bt.serialization.JsonSerializer;
import com.acme.cbmkafka.bt.util.BusinessTierConfigParser;

import java.util.Properties;

public class EDABusinessTierDriver {

	final static Logger logger = LogManager.getLogger(EDABusinessTierDriver.class.getName());

	public static void main(String[] args) throws Exception {

		BusinessTierConfig btConfig = BusinessTierConfigParser.parse(args[0], BusinessTierConfig.class);

		logger.info(btConfig);

		StreamsConfig streamingConfig = new StreamsConfig(getProperties(btConfig));
		
		StringDeserializer stringDeserializer = new StringDeserializer();
		StringSerializer stringSerializer = new StringSerializer();

		JsonSerializer<EDAMessage> edaMessageSerializer = new JsonSerializer<>();
		JsonDeserializer<EDAMessage> edaMessageDeserializer = new JsonDeserializer<>(EDAMessage.class);

		EDAMessageListSerializer edaMessageListSerializer = new EDAMessageListSerializer();
		EDAMessageListDeserializer edaMessageListDeserializer = new EDAMessageListDeserializer();

		Serde<EDAMessageList> edaMessageListSerde = Serdes.serdeFrom(edaMessageListSerializer,edaMessageListDeserializer);

//		StoreBuilder<KeyValueStore<String, EDAMessageList>> countStoreBuilder = Stores.keyValueStoreBuilder(
//				Stores.persistentKeyValueStore(btConfig.getMessageOrderProcessorConfig().getStore()),
//				Serdes.String(),
//				cbmMessageListSerde);
//
//		Topology topologyBuilder = new Topology();
//		topologyBuilder.addSource(btConfig.getStreamsSourceConfig().getName(), stringDeserializer, cbmMessageDeserializer, btConfig.getStreamsSourceConfig().getTopic())
//		.addProcessor(btConfig.getMessageOrderProcessorConfig().getName(), () -> new EDAMessageOrderProcessor(btConfig.getMessageOrderProcessorConfig()), btConfig.getStreamsSourceConfig().getName())
//		.addStateStore(countStoreBuilder, btConfig.getMessageOrderProcessorConfig().getName());
//
//		for(int ii = 0; ii < btConfig.getStreamsSinkConfigList().size(); ii++) {
//			topologyBuilder.addSink(btConfig.getStreamsSinkConfigList().get(ii).getName(), 
//					btConfig.getStreamsSinkConfigList().get(ii).getTopic(), 
//					stringSerializer, cbmMessageSerializer, 
//					btConfig.getMessageOrderProcessorConfig().getName());
//		}

		Topology topologyBuilder = new Topology();
		topologyBuilder.addSource(btConfig.getStreamsSourceConfig().getName(), stringDeserializer, edaMessageDeserializer, btConfig.getStreamsSourceConfig().getTopic())
		.addProcessor(btConfig.getRulesProcessorConfig().getName(), RulesProcessor::new, btConfig.getStreamsSourceConfig().getName())
		.addProcessor(btConfig.getMessageRouteProcessorConfig().getName(), () -> new MessageRouteProcessor(btConfig.getMessageRouteProcessorConfig()), btConfig.getRulesProcessorConfig().getName());

		for(int ii = 0; ii < btConfig.getStreamsSinkConfigList().size(); ii++) {
			topologyBuilder.addSink(btConfig.getStreamsSinkConfigList().get(ii).getName(), 
					btConfig.getStreamsSinkConfigList().get(ii).getTopic(), 
					stringSerializer, edaMessageSerializer,
					btConfig.getMessageRouteProcessorConfig().getName());
		}
		
		System.out.println("Starting EDABusinessTierDriver :" + btConfig.getApplicationId());
		logger.info("Initializing EDABusinessTierDriver streams app :" + btConfig.getApplicationId());
		KafkaStreams streaming = new KafkaStreams(topologyBuilder, streamingConfig);

		streaming.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("Got exception " + e);
            e.printStackTrace(System.out);

            new EDAUncaughtExceptionHandler(e);
        });
		
		streaming.cleanUp();

		streaming.start();
		
		System.out.println("Started EDABusinessTierDriver :" + btConfig.getApplicationId() + " with " + btConfig.getNumOfThreads() + " thread(s)");
		logger.info("EDABusinessTierDriver ready for messages");
	

		
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
            	logger.warn("Received terminate signal. Shutting down.");
            	streaming.close();
            }
        }));

	}

	private static Properties getProperties(BusinessTierConfig btConfig) {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, btConfig.getClientId());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, btConfig.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, btConfig.getBrokerList());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, btConfig.getReplicationFactor());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, btConfig.getAutoOffsetReset());
        
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, btConfig.getNumOfThreads()); 
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, btConfig.getProcessingGuarantee());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, btConfig.getCommitInterval());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);        
        
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, btConfig.getMaxPollRecords());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        
     

        //props.put(ProducerConfig.BATCH_SIZE_CONFIG, "300");
        //props.put(ProducerConfig.SEND_BUFFER_CONFIG, "300");
        
        //Deserialization
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, EDADeserializationExceptionHandler.class);
        //props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        props.put("deserialization.bootstrap.servers", btConfig.getBrokerList());
        props.put("deserialization.num.stream.threads", btConfig.getNumOfThreads());
        props.put("deserialization.commit.interval.ms", btConfig.getCommitInterval());
        props.put("deserialization.producer.key.serializer", StringSerializer.class);
        props.put("deserialization.producer.value.serializer", StringSerializer.class);
 
        //Production
        props.put("default.production.exception.handler", NonRetryableStreamsExceptionHandler.class);        
        props.put("production.bootstrap.servers",btConfig.getBrokerList());
        props.put("production.num.stream.threads", btConfig.getNumOfThreads());
        props.put("production.commit.interval.ms", btConfig.getCommitInterval());
        props.put("production.key.serializer", StringSerializer.class);
        props.put("production.value.serializer", StringSerializer.class);
	      

        return props;
	}
}
