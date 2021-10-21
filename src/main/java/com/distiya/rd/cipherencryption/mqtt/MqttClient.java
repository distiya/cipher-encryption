package com.distiya.rd.cipherencryption.mqtt;

import io.vertx.core.Promise;

public interface MqttClient {

    void publishMessage(Promise<Void> completionMarkerPromise, String topic, byte[] payload, int qos);
    void publishMessage(Promise<Void> completionMarkerPromise, String topic, byte[] payload);
    void publishMessage(Promise<Void> completionMarkerPromise, byte[] payload);
    void subscribeToTopics(String[] topics, int[] qos);
}
