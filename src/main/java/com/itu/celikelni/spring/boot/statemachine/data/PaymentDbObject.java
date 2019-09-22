package com.itu.celikelni.spring.boot.statemachine.data;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.UUID;


/** DTO = Data Transfer Object */

@Document(collection="Payments")
public class PaymentDbObject {

    @Id
    public UUID id;

    private String state;

    @PersistenceConstructor
    public PaymentDbObject(UUID id, String state){
        System.out.println("****************** DB OBJECT FOR PAYMENT IS CREATED *************");
        this.id = id;
        this.state = state;
        System.out.println("****************** UUID: " + this.id+" ******************");
        System.out.println("****************** STATE: " + this.state+" ******************");
    }

}


