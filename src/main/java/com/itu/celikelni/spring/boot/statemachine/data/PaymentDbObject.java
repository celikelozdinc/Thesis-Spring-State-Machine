package com.itu.celikelni.spring.boot.statemachine.data;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;


/** DTO = Data Transfer Object */

@Document(collection="Payments")
public class PaymentDbObject {

    @Id
    public String Id;

    private String text;
    private Integer number;

    @PersistenceConstructor
    public PaymentDbObject(String st, Integer num){
        System.out.println("****************** DB OBJECT FOR PAYMENT IS CREATED *************");
        this.text = st;
        this.number = num;
    }

}


