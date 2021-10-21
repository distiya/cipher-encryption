package com.distiya.rd.cipherencryption.mqtt;

import com.distiya.rd.cipherencryption.data.EncryptionMessage;
import com.distiya.rd.cipherencryption.properties.AppConfig;
import com.distiya.rd.cipherencryption.util.KeyPairEncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MqttClientCallBack implements MqttCallbackExtended {

    private AppConfig appConfig;
    private MqttClient mqttClient;
    private ObjectMapper objectMapper;

    @Override
    public void connectComplete(boolean b, String s) {
        if(appConfig.getMqtt().getEnableTopicSubscription() && appConfig.getMqtt().getPublishTopic() != null){
            String[] topics = new String[]{appConfig.getMqtt().getPublishTopic()};
            int[] qos = new int[]{appConfig.getMqtt().getDefaultQOS()};
            mqttClient.subscribeToTopics(topics,qos);
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Connection Lost",throwable);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        handleReceivedMessage(Promise.promise(),topic,mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        //to-do
    }

    private void handleReceivedMessage(Promise<Void> completionMarker, String topic, MqttMessage mqttMessage){
        if(appConfig.getMqtt().getEnableTopicSubscription() && topic.equals(appConfig.getMqtt().getPublishTopic())){
            try {
                EncryptionMessage message = objectMapper.readerFor(EncryptionMessage.class).readValue(mqttMessage.getPayload());
                KeyPairEncryptionUtil.decryptFile(message.getEncryptedSlices(),appConfig.getEncryption().getPrivateKeyLocation(),appConfig.getEncryption().getStorageDirectoryLocation(),message.getFileName(),message.getFileType());
                log.info("Mqtt message was handled");
                completionMarker.tryComplete();
            } catch (Exception e) {
                log.error("Error handling incoming mqtt message",e);
                completionMarker.tryFail(e);
            }
        }
    }
}
