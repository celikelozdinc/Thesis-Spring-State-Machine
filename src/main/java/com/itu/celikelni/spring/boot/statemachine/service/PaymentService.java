package com.itu.celikelni.spring.boot.statemachine.service;

import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObject;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObjectHandler;
import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import com.itu.celikelni.spring.boot.statemachine.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;
import org.springframework.statemachine.zookeeper.ZookeeperStateMachineEnsemble;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


import java.util.Random;


@Service
public class PaymentService {

    @Autowired
    private PaymentCollector paymentCollector;

    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachine<States,Events> backupStateMachine;

    @Autowired
    private PaymentDbObjectHandler dbObjectHandler;

    @Autowired
    private PaymentPersistenceService persistenceService;

    @Autowired
    private StateMachineEnsemble<States, Events> stateMachineEnsemble1;

    @Autowired
    private StateMachineEnsemble<States, Events> stateMachineEnsemble2;


    private int timeSleep;

    public void prepareEnvironment(int sleep) {
        stateMachine.start();
        backupStateMachine.start();

        timeSleep = sleep;
        Random rand = new Random();
        int orderId = rand.nextInt(1000);
    }

    public void destroyEnvironment() { stateMachine.stop(); backupStateMachine.stop();}

    public Payment create() throws Exception{
        //persistenceService.persister.persist(stateMachine,stateMachine.getUuid());

        Random rand = new Random();
        int orderId = rand.nextInt(1000);
        Payment payment = new Payment(orderId);
        paymentCollector.push(payment);

        /** Payment is created as UNPAID state**/

        PaymentDbObject dbObject = new PaymentDbObject(stateMachine.getUuid(),payment.getPaymentStatus().toString());
        //dbObjectHandler.insertPayment(dbObject);

        return payment;
    }

    public Payment pay(Payment paymentArg) throws Exception{
        //persistenceService.persister.persist(stateMachine,stateMachine.getUuid());
        Integer paymentId = paymentArg.getPaymentId();
        Payment payment = paymentCollector.pop(paymentId);
        Message<Events> messagePay = MessageBuilder
                .withPayload(Events.PAY)
                .setHeader("timeSleep", timeSleep)
                .build();
        stateMachine.sendEvent(messagePay);
        payment.setPaymentStatus(States.WAITING_FOR_RECEIVE);

        PaymentDbObject dbObject = new PaymentDbObject(stateMachine.getUuid(),payment.getPaymentStatus().toString());
        //dbObjectHandler.updatePayment(dbObject);

        return payment;
    }

    public Payment receive(Payment paymentArg) throws Exception{
        //persistenceService.persister.persist(stateMachine,stateMachine.getUuid());
        Integer paymentId = paymentArg.getPaymentId();
        Payment payment = paymentCollector.pop(paymentId);
        Message<Events> messageReceive = MessageBuilder
                .withPayload(Events.RECEIVE)
                .setHeader("timeSleep", timeSleep)
                .build();
        stateMachine.sendEvent(messageReceive);
        payment.setPaymentStatus(States.DONE);

        PaymentDbObject dbObject = new PaymentDbObject(stateMachine.getUuid(),payment.getPaymentStatus().toString());
        //dbObjectHandler.updatePayment(dbObject);

        return payment;
    }

    public Payment startfromscratch(Payment paymentArg) throws Exception{

        /*
        persistenceService.persister.persist(stateMachine,stateMachine.getUuid());
        System.out.println(" ---- RESTORE BEGINS --- ");
        persistenceService.persister.restore(backupStateMachine,stateMachine.getUuid());
        System.out.println("State after restore --> " + backupStateMachine.getState().getId());
        Integer commonVar = backupStateMachine.getExtendedState().get("common", Integer.class);
        System.out.println("common var after restore --> " + commonVar);
        */

        Integer paymentId = paymentArg.getPaymentId();
        Payment payment = paymentCollector.pop(paymentId);
        Message<Events> messageStartFromScratch = MessageBuilder
                .withPayload(Events.STARTFROMSCRATCH)
                .setHeader("timeSleep", timeSleep)
                .build();
        stateMachine.sendEvent(messageStartFromScratch);
        payment.setPaymentStatus(States.UNPAID);

        PaymentDbObject dbObject = new PaymentDbObject(stateMachine.getUuid(),payment.getPaymentStatus().toString());
        //dbObjectHandler.updatePayment(dbObject);

        return payment;
    }

}
