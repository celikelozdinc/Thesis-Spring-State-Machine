package com.itu.celikelni.spring.boot.statemachine.data;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


/** DTO = Data Transfer Object */

@Document
public class PaymentDbObject {

    private String text;
    private Integer number;

    @PersistenceConstructor
    public PaymentDbObject(String st, Integer num){
        this.text = st;
        this.number = num;
    }

}


