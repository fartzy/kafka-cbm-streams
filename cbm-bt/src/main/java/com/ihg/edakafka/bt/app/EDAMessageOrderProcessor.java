package com.acme.cbmkafka.bt.app;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.acme.cbmkafka.bt.config.MessageOrderProcessorConfig;
import com.acme.cbmkafka.bt.dto.EDAMessage;
import com.acme.cbmkafka.bt.dto.EDAMessageList;

public class EDAMessageOrderProcessor extends AbstractProcessor<String, EDAMessage> {
	final static Logger logger = LogManager.getLogger(EDAMessageOrderProcessor.class.getName());
	
	private KeyValueStore<String, EDAMessageList> messageOrderStore;
	private ProcessorContext context;
	
	private MessageOrderProcessorConfig myConfig;

	public EDAMessageOrderProcessor(MessageOrderProcessorConfig myConfig) {
		this.myConfig = myConfig;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		this.context = context;

		this.context.schedule(myConfig.getBatchDuration(), PunctuationType.WALL_CLOCK_TIME, new Punctuator() {

			@Override
			public void punctuate(long streamTime) {
				logger.info("\nFlushing messageOrderStore with size :" + messageOrderStore.approximateNumEntries() + " \n");

				KeyValueIterator<String, EDAMessageList> it = messageOrderStore.all();

				long currentTime = System.currentTimeMillis();
				while (it.hasNext()) {
					KeyValue<String, EDAMessageList> kv = it.next();
					String key = kv.key;
					EDAMessageList messageList = kv.value;

					logger.info("Forwarding message list for key :" + messageList.getPropertyCode() + ": with size :" + messageList.getSize() + ":");
					List<EDAMessage> tempMessageList = messageList.getMessageList();
					Collections.sort(tempMessageList);

					Long flushedSeqNumber = messageList.getLastSequenceNumberFlushed();
					Long toFlushSeqNumber = messageList.getLastSequenceNumberToFlush();
					int ii = 0;
					
					for(EDAMessage message : tempMessageList) {
						if(flushedSeqNumber != -1 && flushedSeqNumber > message.getSequence().getPart2()) {
							// Got a late arrival.... but too late, we already moved on. Send to dead letter queue
							logger.info("Sending seqNumber :" + message.getSequence().getPart2() + ": to DLQ as its too late. We already flushed seqNumber :" + flushedSeqNumber + ":");
							ii++;
							continue;
						}
						
						if(flushedSeqNumber == -1 || 
								(flushedSeqNumber + 1) == message.getSequence().getPart2()) {
							logger.info("Working on property :" + key + ":NORMAL: flushedSeqNumber =" + flushedSeqNumber + " toFlushSeqNumber =" + toFlushSeqNumber + " message.getSequenceNumber()=" + message.getSequence().getPart2());
							context.forward(key, message, myConfig.getSuccessProcessor());
							flushedSeqNumber = message.getSequence().getPart2();
							ii++;
						} else if(toFlushSeqNumber >= message.getSequence().getPart2()) {
							logger.info("Working on property :" + key + ":FORCEFLUSH: flushedSeqNumber =" + flushedSeqNumber + " toFlushSeqNumber =" + toFlushSeqNumber + " message.getSequenceNumber()=" + message.getSequence().getPart2());
							// Flush the message as we waited for one more batch and send alert about the hole
							context.forward(key, message, myConfig.getSuccessProcessor());
							
							// Alert with missed messages between flushedSeqNumber and toFlushSeqNumber
							context.forward(key, "Missed message between sequence number " + flushedSeqNumber + " and " + message.getSequence().getPart2(), myConfig.getAlertSink());
							
							flushedSeqNumber = message.getSequence().getPart2();
							ii++;
							
						} else {
							// Got a hole in sequence number. Late arrival? Stop forwarding and wait for next batch
							toFlushSeqNumber = tempMessageList.get(tempMessageList.size() - 1).getSequence().getPart2();
							messageList.setLastSequenceNumberToFlush(toFlushSeqNumber);
							
							logger.info("Working on property :" + key + ":HOLE: flushedSeqNumber =" + flushedSeqNumber + " toFlushSeqNumber =" + toFlushSeqNumber + " message.getSequenceNumber()=" + message.getSequence().getPart2() + " New toFlushSeqNumber =" + messageList.getLastSequenceNumberToFlush());
							break;
						}
					}
					
					if(ii == tempMessageList.size()) {
						logger.info("Batch done. Clearing " + ii + " records from " + tempMessageList.size());
						// All done in this batch
						tempMessageList.clear();
						logger.info("Batch done. New list size = " + tempMessageList.size());
					} else {
						logger.info("Batch done. Clearing " + ii + " records from " + tempMessageList.size());
						// Only remove the messages flushed
						tempMessageList.subList(0, ii).clear();
						
						logger.info("Batch done. New list size = " + tempMessageList.size());
					}
					
					messageList.setLastSequenceNumberFlushed(flushedSeqNumber);	
					
//					messageList.setLastSequenceNumberFlushed(-1L);	
//					messageList.setLastSequenceNumberToFlush(-1L);
					
					messageList.setMessageList(tempMessageList);
					messageOrderStore.put(key, messageList);
				}

				it.close();

				logger.info("\nAfter flush messageOrderStore with size :" + messageOrderStore.approximateNumEntries() + " \n");
				context.commit();
			}});

		messageOrderStore = (KeyValueStore<String, EDAMessageList>) this.context.getStateStore("cbm-message-list");
		Objects.requireNonNull(messageOrderStore, "State store can't be null");

	}

	@Override    
	public void process(String key, EDAMessage cbmMessage) {

		EDAMessageList messageList = messageOrderStore.get(cbmMessage.getPropertyCode());
		
		if(messageList == null) {
			logger.info("Creating a new EDAMessageList");
			messageList = new EDAMessageList();
			messageList.setPropertyCode(cbmMessage.getPropertyCode());
		}
		
		logger.info("Processing " + cbmMessage);

		messageList.addMessage(cbmMessage);
		messageOrderStore.put(cbmMessage.getPropertyCode(), messageList);

		this.context.commit();
	}
}
