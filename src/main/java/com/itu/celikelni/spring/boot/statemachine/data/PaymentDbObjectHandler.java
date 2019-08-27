package com.itu.celikelni.spring.boot.statemachine.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentRepository;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObject;

@Component
public class PaymentDbObjectHandler {

    @Autowired
    private PaymentRepository paymentRepository;


    // Add to database
    public PaymentDbObject insertPayment(PaymentDbObject paymentDbObject){
        return paymentRepository.insert(paymentDbObject);
    }

    // Update in database
    public PaymentDbObject updatePayment(PaymentDbObject paymentDbObject){
        return paymentRepository.insert(paymentDbObject);
    }

}
