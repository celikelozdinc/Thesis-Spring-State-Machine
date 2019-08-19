package com.itu.celikelni.spring.boot.statemachine;
import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.persist.StateMachinePersister;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachinePersister<States, Events, Integer> stateMachinePersister;


    public void processOneCycle(int sleep){

        Message<Events> messagePay = MessageBuilder
                .withPayload(Events.PAY)
                .setHeader("timeSleep", sleep)
                .build();
        stateMachine.sendEvent(messagePay);

        Message<Events> messageReceive = MessageBuilder
                .withPayload(Events.RECEIVE)
                .setHeader("timeSleep", sleep)
                .build();
        stateMachine.sendEvent(messageReceive);


        Message<Events> messageStartFromScratch = MessageBuilder
                .withPayload(Events.STARTFROMSCRATCH)
                .setHeader("timeSleep", sleep)
                .build();
        stateMachine.sendEvent(messageStartFromScratch);


        /*
        stateMachine.sendEvent(Events.PAY);
        stateMachine.sendEvent(Events.RECEIVE);
        stateMachine.sendEvent(Events.STARTFROMSCRATCH);
         */

    }

    @Override
    public void run(String... args) throws Exception {
        String[] argument = args[0].split("=");
        int timeSleep = Integer.parseInt(argument[1]);

        stateMachine.start();


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


        /**
         * Package 1 iteration of state machine
         * and call them in a for loop
         **/
        int processCounter;
        processCounter = 0;
        do{
            processOneCycle(timeSleep);
            processCounter ++ ;

        } while(processCounter < 7 );


        stateMachine.stop();


    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
