package com.distiya.rd.cipherencryption.controller;

import com.distiya.rd.cipherencryption.data.EncryptionMessage;
import com.distiya.rd.cipherencryption.data.FileEncryptRequest;
import com.distiya.rd.cipherencryption.mqtt.MqttClient;
import com.distiya.rd.cipherencryption.util.KeyPairEncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class EncryptController {

    @Autowired(required = false)
    private MqttClient mqttClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(path = "/encrypt/file",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> encryptFile(@RequestBody FileEncryptRequest request){
        try {
            List<String> encryptedSlices = KeyPairEncryptionUtil.encryptFile(request.getFileLocation(), request.getPublicKeyLocation());
            EncryptionMessage encryptionMessage = new EncryptionMessage(request.getFileName(),"",encryptedSlices);
            mqttClient.publishMessage(Promise.promise(),objectMapper.writerFor(EncryptionMessage.class).writeValueAsString(encryptionMessage).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("File encryption got failed",e);
        }
        return Mono.empty().then();
    }
}
