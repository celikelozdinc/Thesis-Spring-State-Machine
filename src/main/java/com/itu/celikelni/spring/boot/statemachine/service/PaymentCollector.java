package com.itu.celikelni.spring.boot.statemachine.service;

import org.springframework.stereotype.Component;
import com.itu.celikelni.spring.boot.statemachine.entity.Payment;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentCollector {

    private static Map<Integer, Payment> db = new HashMap<>();


    public Payment push(Payment payment){
        db.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Payment pop(int paymentId) {
        return db.get(paymentId);
    }

}
