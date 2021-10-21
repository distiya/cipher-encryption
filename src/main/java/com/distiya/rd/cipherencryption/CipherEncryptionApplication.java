package com.distiya.rd.cipherencryption;

import com.distiya.rd.cipherencryption.properties.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class CipherEncryptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CipherEncryptionApplication.class, args);
	}

}
