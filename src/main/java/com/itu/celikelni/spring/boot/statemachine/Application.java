package com.itu.celikelni.spring.boot.statemachine;
import com.itu.celikelni.spring.boot.statemachine.constant.Events;
import com.itu.celikelni.spring.boot.statemachine.constant.States;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private StateMachine<States, Events> stateMachine;

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


    }
}
