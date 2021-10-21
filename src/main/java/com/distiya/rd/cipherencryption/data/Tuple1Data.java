package com.distiya.rd.cipherencryption.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tuple1Data<T1> {

    private T1 t1;

    public static <T1> Tuple1Data<T1> of(T1 t1){
        return new Tuple1Data<>(t1);
    }
}
