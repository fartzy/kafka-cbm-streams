package com.acme.cbmkafka.bt.serialization;

import org.apache.commons.lang.SerializationUtils;
import org.apache.kafka.common.serialization.Deserializer;

import com.acme.cbmkafka.bt.dto.EDAMessageList;

import java.util.Map;

public class EDAMessageListDeserializer implements Deserializer<EDAMessageList> {

    public EDAMessageListDeserializer() {
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public EDAMessageList deserialize(String s, byte[] bytes) {
         if(bytes == null){
             return null;
         }
         
         return (EDAMessageList) SerializationUtils.deserialize(bytes);
    }

    @Override
    public void close() {

    }
}
