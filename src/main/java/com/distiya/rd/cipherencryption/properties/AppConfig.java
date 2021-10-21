package com.distiya.rd.cipherencryption.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.config")
public class AppConfig {

    private MqttConfig mqtt = new MqttConfig();
    private EncryptionConfig encryption = new EncryptionConfig();

}
