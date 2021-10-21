package com.distiya.rd.cipherencryption.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileEncryptRequest {

    private String fileName;
    private String fileLocation;
    private String publicKeyLocation;

}
