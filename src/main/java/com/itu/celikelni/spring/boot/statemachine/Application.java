package com.itu.celikelni.spring.boot.statemachine;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObjectHandler;
import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;
import com.itu.celikelni.spring.boot.statemachine.entity.Payment;
import com.itu.celikelni.spring.boot.statemachine.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObject;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachinePersister<States, Events, Integer> stateMachinePersister;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentDbObjectHandler dbObjectHandler;


    @Override
    public void run(String... args) throws Exception {
        String[] argument = args[0].split("=");
        int timeSleep = Integer.parseInt(argument[1]);

        paymentService.prepareEnvironment(timeSleep);
        Payment payment = paymentService.create();

        paymentService.pay(payment.getPaymentId());
        paymentService.receive(payment.getPaymentId());
        paymentService.startfromscratch(payment.getPaymentId());

        paymentService.pay(payment.getPaymentId());
        paymentService.receive(payment.getPaymentId());
        paymentService.startfromscratch(payment.getPaymentId());

        paymentService.pay(payment.getPaymentId());
        paymentService.receive(payment.getPaymentId());
        paymentService.startfromscratch(payment.getPaymentId());

        paymentService.pay(payment.getPaymentId());
        paymentService.receive(payment.getPaymentId());
        paymentService.startfromscratch(payment.getPaymentId());

        paymentService.pay(payment.getPaymentId());
        paymentService.receive(payment.getPaymentId());
        paymentService.startfromscratch(payment.getPaymentId());

        paymentService.destroyEnvironment();

        PaymentDbObject dbObject = new PaymentDbObject("first",10);
        PaymentDbObject dbObject2 = new PaymentDbObject("second",20);


        dbObjectHandler.insertPayment(dbObject);
        dbObjectHandler.insertPayment(dbObject2);



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
