package com.distiya.rd.cipherencryption.config;

import com.distiya.rd.cipherencryption.mqtt.MqttClient;
import com.distiya.rd.cipherencryption.mqtt.MqttClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app.config.mqtt", value = "enable", havingValue = "true", matchIfMissing = true)
    public MqttClient getMqttClient(){
        return new MqttClientImpl();
    }
}
