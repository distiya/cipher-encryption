package com.distiya.rd.cipherencryption.mqtt;

import io.vertx.core.Promise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MqttActionCallBack implements IMqttActionListener {

    private Promise<Void> completionMarker;
    private Integer type = 1;

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        if(type == 1){
            log.info("Topic subscription is completed");
        }
        else if(type == 2){
            log.info("Mqtt connection is successful for client id {}",iMqttToken.getClient().getClientId());
        }
        else{
            log.info("Message publishing is successful");
        }
        completionMarker.tryComplete();
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        if(type == 1){
            log.error("Topic subscription is failed",throwable);
        }
        else if(type == 2){
            log.error("Connection attempt is failed",throwable);
        }
        else{
            log.error("Message publishing is failed",throwable);
        }
        completionMarker.tryFail(throwable);
    }
}
