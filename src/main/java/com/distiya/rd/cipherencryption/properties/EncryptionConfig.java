package com.distiya.rd.cipherencryption.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EncryptionConfig {

    private String privateKeyLocation;
    private String storageDirectoryLocation;

}
