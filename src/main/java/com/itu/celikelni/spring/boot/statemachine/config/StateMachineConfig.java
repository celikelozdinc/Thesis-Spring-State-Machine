package com.itu.celikelni.spring.boot.statemachine.config;

import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;
import com.itu.celikelni.spring.boot.statemachine.persister.___Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.zookeeper.ZookeeperStateMachineEnsemble;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableStateMachine(name = "clientStateMachine")
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Autowired
    private ___Persister fsmStateMachinePersister;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StateMachineEnsemble<States, Events> stateMachineEnsemble1;

    @Autowired
    private StateMachineEnsemble<States, Events> stateMachineEnsemble2;


    /** Default Constructor **/
    public StateMachineConfig(){ }



    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config
                .withDistributed()
                .ensemble(stateMachineEnsemble());
    }


    /**
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states.withStates()
                .initial(States.UNPAID, initializationAction())
                .stateEntry(States.WAITING_FOR_RECEIVE,entryActionForWaiting(),errorDuringWaitingState())
                .stateExit(States.WAITING_FOR_RECEIVE, exitActionForWaiting())
                .stateEntry(States.DONE, entryActionForDone())
                .stateExit(States.DONE, exitActionForDone());
                //import java.util.EnumSet;
                //.states(EnumSet.allOf(States.class));
    }

    /**
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        /** Defines "EXTERNAL" type of transitions **/
        transitions
                .withExternal()
                    .source(States.UNPAID).target(States.WAITING_FOR_RECEIVE)
                    .event(Events.PAY)
                    //.action((c) ->{System.out.println("-----FROM UNPAID TO WAITING----");})
                    //.action(c -> {c.getExtendedState().getVariables().put("key1", "value1");});
                    .action(increaseAction())
                    .and()
                .withExternal()
                    .source(States.WAITING_FOR_RECEIVE).target(States.DONE)
                    .event(Events.RECEIVE)
                    .action(increaseAction())
                    .and()
                .withExternal()
                    .source(States.DONE).target(States.UNPAID)
                    .event(Events.STARTFROMSCRATCH)
                    .action(increaseAction(),errorDuringTransition());
    }


    public void sleepForAWhile(Long sleepTime){
        try {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException ex) {
            // handle error
        }

    }

    @Bean
    public Action<States, Events> entryActionForWaiting() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("Setting state machine ensemble inside entryActionForWaiting");
                stateMachineEnsemble1.setState(new DefaultStateMachineContext<States, Events>(States.WAITING_FOR_RECEIVE,Events.PAY, new HashMap<String, Object>(), new DefaultExtendedState()));

                System.out.println("-----------ENTERING WAITING STATE ACTION------------");
                Integer localVar = context.getExtendedState().get("localVarForWaiting", Integer.class);
                localVar = localVar + 2;
                context.getExtendedState().getVariables().put("localVarForWaiting", localVar);
                /** Must be catched by regarding State Action Error Handling Function **/
                throw new RuntimeException("RUNTIME ERROR DURING WAITING STATE EXECUTION!!!");
            }
        };
    }

    @Bean
    public Action<States, Events> exitActionForWaiting() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("-----------EXITING WAITING STATE ACTION------------");
                Integer localVar = context.getExtendedState().get("localVarForWaiting", Integer.class);
                System.out.println("Local var for waiting state: " + localVar);
            }
        };
    }

    @Bean
    public Action<States, Events> entryActionForDone() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("Setting state machine ensemble inside entryActionForDone");
                stateMachineEnsemble1.setState(new DefaultStateMachineContext<States, Events>(States.DONE,Events.RECEIVE, new HashMap<String, Object>(), new DefaultExtendedState()));

                System.out.println("-----------ENTERING DONE STATE ACTION------------");
                Integer localVar = context.getExtendedState().get("localVarForDone", Integer.class);
                localVar = localVar + 5;
                context.getExtendedState().getVariables().put("localVarForDone", localVar);
            }
        };
    }

    @Bean
    public Action<States, Events> exitActionForDone() {
        return new Action<States, Events>() {

            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("-----------EXITING DONE STATE ACTION------------");
                Integer localVar = context.getExtendedState().get("localVarForDone", Integer.class);
                System.out.println("Local var for done state: " + localVar);
            }
        };
    }

    @Bean
    public Action<States, Events> initializationAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {

                System.out.println("Setting state machine ensemble inside initializationAction");
                stateMachineEnsemble1.setState(new DefaultStateMachineContext<States, Events>(States.UNPAID,null, new HashMap<String, Object>(), new DefaultExtendedState()));

                System.out.println("----------- TRANSITION ACTION FOR INITIALIZATION------------");
                /** Define extended state variable as common variable used inside transition actions **/
                context.getExtendedState().getVariables().put("common", 0);
                /** Define extended state variable as private/local variable used inside state actions **/
                context.getExtendedState().getVariables().put("localVarForWaiting",10);
                context.getExtendedState().getVariables().put("localVarForDone",50);

            }
        };
    }

    @Bean
    public Action<States, Events> increaseAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("-----------TRANSITION ACTION FOR INCREASING VARIABLE------------");

                Object sleep = context.getMessageHeaders().get("timeSleep");
                long longSleep = ((Number) sleep).longValue();

                Map<Object, Object> variables = context.getExtendedState().getVariables();
                Integer commonVar = context.getExtendedState().get("common", Integer.class);

                /* For Initalization Action
                if (foo == null) {
                    logger.info("Init foo to 0");
                    variables.put("foo", 0);
                }*/

                if (commonVar == 0) {
                    logger.info("Switch common variable from 0 to 1");
                    variables.put("common", 1);
                    sleepForAWhile(longSleep);
                } else if (commonVar == 1) {
                    logger.info("Switch common variable from 1 to 2");
                    variables.put("common", 2);
                    sleepForAWhile(longSleep);
                } else if (commonVar == 2) {
                    logger.info("Switch common variable from 2 to 0");
                    variables.put("common", 0);
                    sleepForAWhile(longSleep);
                }
                //throw new RuntimeException("RUNTIME ERROR DURING STATE TRANSITION");
            }
        };
    }

    @Bean
    public Action<States, Events> errorDuringWaitingState() {
        return ctx -> System.out.println("Error during " + ctx.getSource().getId() + ": " + ctx.getException());
    }

    @Bean
    public Action<States, Events> errorDuringTransition() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("Exception message: " + context.getException().getMessage());
            }
        };
    }


    @Bean
    public StateMachinePersister<States, Events, Integer> stateMachinePersist() {
        return new DefaultStateMachinePersister<>(fsmStateMachinePersister);
    }

    @Bean
    public StateMachineEnsemble<States, Events> stateMachineEnsemble() throws Exception {
        return new ZookeeperStateMachineEnsemble<States, Events>(curatorClient(), "/zkPath");
    }

    @Bean
    public CuratorFramework curatorClient() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder().defaultData(new byte[0])
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectString("localhost:2181").build();
        client.start();
        return client;
    }

}
