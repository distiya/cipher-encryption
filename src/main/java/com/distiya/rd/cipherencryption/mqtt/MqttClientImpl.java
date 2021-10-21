package com.distiya.rd.cipherencryption.mqtt;

import com.distiya.rd.cipherencryption.properties.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
public class MqttClientImpl implements MqttClient{

    @Autowired
    @Setter
    private AppConfig appConfig;

    @Autowired
    @Setter
    private ObjectMapper objectMapper;

    @Getter
    private MqttAsyncClient asyncClient;

    private MemoryPersistence memoryPersistence;

    @PostConstruct
    public void initializeClient(){
        initiateMqttClientCreation(Promise.promise());
    }

    private void initiateMqttClientCreation(Promise<Void> completionMarkerPromise){
        this.memoryPersistence = new MemoryPersistence();
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(this.appConfig.getMqtt().getCleanSession());
        connectOptions.setKeepAliveInterval(this.appConfig.getMqtt().getKeepAlive());
        connectOptions.setAutomaticReconnect(this.appConfig.getMqtt().getAutoReconnect());
        connectOptions.setConnectionTimeout(this.appConfig.getMqtt().getConnectTimeout());
        connectOptions.setMaxInflight(this.appConfig.getMqtt().getMaxInFlight());
        connectOptions.setMaxReconnectDelay(this.appConfig.getMqtt().getMaxReconnectDelay());
        try {
            createClient(completionMarkerPromise,connectOptions);
        } catch (MqttException e) {
            log.error("Unable to connect to mqtt broker");
            completionMarkerPromise.tryFail(e);
        }
    }

    private void createClient(Promise<Void> completionMarker, MqttConnectOptions connectOptions) throws MqttException{
        this.asyncClient = new MqttAsyncClient(this.appConfig.getMqtt().getBrokerURL(), UUID.randomUUID().toString(),memoryPersistence);
        this.asyncClient.setCallback(new MqttClientCallBack(this.appConfig, this, objectMapper));
        this.asyncClient.connect(connectOptions,null,new MqttActionCallBack(completionMarker,2));
    }

    private void subscribeToTopics(Promise<Void> completionMarker, String[] topics, int[] qos){
        try {
            this.asyncClient.subscribe(topics,qos,null,new MqttActionCallBack(completionMarker,1));
        } catch (MqttException e) {
            log.error("Topic Subscription Failed",e);
            completionMarker.tryFail(e);
        }
    }

    private void publishMessage(Promise<Void> completionMarker, String topic, MqttMessage mqttMessage) throws MqttException{
        this.asyncClient.publish(topic,mqttMessage,null,new MqttActionCallBack(completionMarker,3));
    }

    @Override
    public void publishMessage(Promise<Void> completionMarkerPromise, String topic, byte[] payload, int qos) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload);
        mqttMessage.setQos(qos);
        try {
            publishMessage(completionMarkerPromise,topic,mqttMessage);
        }catch (MqttException e){
            completionMarkerPromise.tryFail(e);
        }
    }

    @Override
    public void publishMessage(Promise<Void> completionMarkerPromise, String topic, byte[] payload) {
        publishMessage(completionMarkerPromise,topic,payload,appConfig.getMqtt().getDefaultQOS());
    }

    @Override
    public void publishMessage(Promise<Void> completionMarkerPromise, byte[] payload) {
        publishMessage(completionMarkerPromise,appConfig.getMqtt().getPublishTopic(),payload,appConfig.getMqtt().getDefaultQOS());
    }

    @Override
    public void subscribeToTopics(String[] topics, int[] qos) {
        subscribeToTopics(Promise.promise(),topics,qos);
    }
}
