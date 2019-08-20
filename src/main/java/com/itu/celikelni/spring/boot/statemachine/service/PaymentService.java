package com.itu.celikelni.spring.boot.statemachine.service;

import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import com.itu.celikelni.spring.boot.statemachine.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Random;

@Service
public class PaymentService {

    @Autowired
    private PaymentCollector paymentCollector;

    @Autowired
    private StateMachine<States, Events> stateMachine;

    private int timeSleep;

    public void prepareEnvironment(int sleep) { stateMachine.start(); timeSleep = sleep; }

    public void destroyEnvironment() { stateMachine.stop(); }

    public Payment create(){
        Random rand = new Random();
        int orderId = rand.nextInt(1000);
        Payment payment = new Payment(orderId);
        paymentCollector.push(payment);
        /** Payment is created as UNPAID state**/
        return payment;
    }

    public Payment pay(int paymentId) {
        Payment payment = paymentCollector.pop(paymentId);
        Message<Events> messagePay = MessageBuilder
                .withPayload(Events.PAY)
                .setHeader("timeSleep", timeSleep)
                .build();
        stateMachine.sendEvent(messagePay);
        payment.setPaymentStatus(States.WAITING_FOR_RECEIVE);
        return payment;
    }

    public Payment receive(int paymentId) {
        Payment payment = paymentCollector.pop(paymentId);
        Message<Events> messageReceive = MessageBuilder
                .withPayload(Events.RECEIVE)
                .setHeader("timeSleep", timeSleep)
                .build();
        stateMachine.sendEvent(messageReceive);
        payment.setPaymentStatus(States.DONE);
        return payment;
    }

    public Payment startfromscratch(int paymentId){
        Payment payment = paymentCollector.pop(paymentId);
        Message<Events> messageStartFromScratch = MessageBuilder
                .withPayload(Events.STARTFROMSCRATCH)
                .setHeader("timeSleep", timeSleep)
                .build();
        stateMachine.sendEvent(messageStartFromScratch);
        payment.setPaymentStatus(States.UNPAID);
        return payment;
    }

}
