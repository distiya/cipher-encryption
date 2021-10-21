package com.distiya.rd.cipherencryption.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MqttConfig {

    private Boolean enable = Boolean.TRUE;
    private String brokerURL;
    private Boolean cleanSession = Boolean.TRUE;
    private Boolean autoReconnect = Boolean.TRUE;
    private Integer keepAlive = 10;
    private Integer maxInFlight = 800;
    private Integer maxReconnectDelay = 10;
    private Integer connectTimeout = 15;
    private Integer defaultQOS = 1;
    private String publishTopic;
    private Boolean enableTopicSubscription = Boolean.TRUE;

}
