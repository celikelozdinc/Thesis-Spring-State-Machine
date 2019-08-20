package com.itu.celikelni.spring.boot.statemachine.entity;

public class Payment {
    private Integer paymentId;
    private States status;


    public Payment(int argId){
        paymentId = argId;
        status = States.UNPAID;
    }

    public int getPaymentId(){return paymentId;}

    public void setPaymentStatus(States state) {status = state; }

    public States getPaymentStatus(){return status;}
}
