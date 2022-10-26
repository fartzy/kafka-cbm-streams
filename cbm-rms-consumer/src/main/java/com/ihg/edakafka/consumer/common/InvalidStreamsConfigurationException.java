package com.acme.cbmkafka.consumer.common;

public class InvalidStreamsConfigurationException extends RuntimeException {

    public InvalidStreamsConfigurationException(String message) {
        super(message);
    }

    public InvalidStreamsConfigurationException(Throwable cause) {
        super(cause);
    }
}