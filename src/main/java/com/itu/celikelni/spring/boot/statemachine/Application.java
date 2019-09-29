package com.itu.celikelni.spring.boot.statemachine;
import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;
import com.itu.celikelni.spring.boot.statemachine.entity.Payment;
import com.itu.celikelni.spring.boot.statemachine.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.persist.StateMachinePersister;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObject;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.HashMap;


//W@EnableMongoRepositories("com.itu.celikelni.spring.boot.statemachine.data")
@EnableMongoRepositories(basePackageClasses=PaymentRepository.class)
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private StateMachine<States, Events> stateMachine;

    /*
    @Autowired
    private StateMachinePersister<States, Events, Integer> stateMachinePersister;
     */

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StateMachineEnsemble<String, String> stateMachineEnsemble1;

    @Autowired
    private StateMachineEnsemble<String, String> stateMachineEnsemble2;


    @Override
    public void run(String... args) throws Exception {
        /* Reads timesleep argument */
        String[] argument = args[0].split("=");
        int timeSleep = Integer.parseInt(argument[1]);

        paymentService.prepareEnvironment(timeSleep);

        Payment created = paymentService.create();
        Payment paid = paymentService.pay(created);
        Payment received = paymentService.receive(paid);

        Payment created_2 = paymentService.startfromscratch(received);
        Payment paid_2 =  paymentService.pay(created_2);
        Payment received_2 = paymentService.receive(paid_2);

        Payment created_3 = paymentService.startfromscratch(received_2);
        Payment paid_3 =  paymentService.pay(created_3);
        Payment received_3 = paymentService.receive(paid_3);

        Payment created_4 = paymentService.startfromscratch(received_3);
        Payment paid_4 =  paymentService.pay(created_4);
        Payment received_4 = paymentService.receive(paid_4);
        paymentService.startfromscratch(received_4);


        stateMachineEnsemble1.setState(new DefaultStateMachineContext<String, String>("mockState","mockEvent", new HashMap<String, Object>(), new DefaultExtendedState()));
        StateMachineContext<String, String> context = stateMachineEnsemble2.getState();
        System.out.println("*************READ FROM OTHER. STATE IS " + context.getState());
        System.out.println("*************READ FROM OTHER. EVENT IS " + context.getEvent());

        /*
        paymentService.pay(payment.getPaymentId());
        paymentService.receive(payment.getPaymentId());
        paymentService.startfromscratch(payment.getPaymentId());
         */

        paymentService.destroyEnvironment();


        //TODO: Persist & Restore methods should be fixed
        /**
         stateMachinePersister.restore(stateMachine, 1);
         System.out.println("***********************");
         Message<Events> messagePay = MessageBuilder
         .withPayload(Events.PAY)
         .setHeader("timeSleep", 1000)
         .build();
         stateMachine.sendEvent(messagePay);
         stateMachinePersister.persist(stateMachine, 1);

         stateMachinePersister.restore(stateMachine, 1);
         System.out.println("***********************");
         Message<Events> messageReceive = MessageBuilder
         .withPayload(Events.RECEIVE)
         .setHeader("timeSleep", 1000)
         .build();
         stateMachine.sendEvent(messageReceive);
         stateMachinePersister.persist(stateMachine, 1);

         stateMachinePersister.restore(stateMachine, 1);
         System.out.println("***********************");
         **/


    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
