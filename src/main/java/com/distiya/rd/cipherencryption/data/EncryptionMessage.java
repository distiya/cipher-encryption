package com.distiya.rd.cipherencryption.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EncryptionMessage {

    private String fileName;
    private String fileType;
    private List<String> encryptedSlices;

}
