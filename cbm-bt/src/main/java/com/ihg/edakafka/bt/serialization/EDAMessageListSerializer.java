package com.acme.cbmkafka.bt.serialization;

import org.apache.commons.lang.SerializationUtils;
import org.apache.kafka.common.serialization.Serializer;

import com.acme.cbmkafka.bt.dto.EDAMessageList;

import java.util.Map;

public class EDAMessageListSerializer implements Serializer<EDAMessageList> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String topic, EDAMessageList t) {
        return SerializationUtils.serialize(t);
    }

    @Override
    public void close() {

    }
}