package com.itu.celikelni.spring.boot.statemachine.config;

import com.itu.celikelni.spring.boot.statemachine.constant.Events;
import com.itu.celikelni.spring.boot.statemachine.constant.States;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.StateContext;

import java.util.EnumSet;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private File jarfile = new File(System.getProperty("java.class.path"));
    private File logfile;


    /** Default Constructor **/
    public StateMachineConfig(){ createLogFile(); }


    /**
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states.withStates()
                .initial(States.UNPAID, initializationAction())
                .states(EnumSet.allOf(States.class));
    }

    /**
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
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
                .action(increaseAction());
    }


    /**
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config.withConfiguration()
                .machineId("client-state-machine")
                .listener(listener());
    }

    public void createLogFile(){

        File dir = jarfile.getAbsoluteFile().getParentFile();
        String path = dir.toString();
        System.out.println("PATH ---> " + path);


        String fd = path + "/log_" + getTimeStamp() + ".txt" ;
        try
        {
            logfile = new File(fd);
            if (!logfile.exists()) {
                System.out.println("Logfile does not exist. Create new one.");
                logfile.createNewFile();
            }
            else if (logfile.exists()){
                System.out.println("Logfile exists on filesystem. Deletes previous one & creates new one.");
                logfile.delete();
                logfile.createNewFile();
            }
        }
        catch (Exception e){
            System.out.println("Exception occured during creating log file: " + e);
        }

    }


    public void storeEvents(String log){
        try {
            FileWriter fileWriter = new FileWriter(logfile,true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(log);  //New line
            printWriter.close();
        }
        catch (Exception e) {System.out.println("Exception occured during flushing into log file: " + e);}


    }


    public String getTimeStamp(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int ms = now.get(Calendar.MILLISECOND);

        String ts = year + "." + month + "." +  day + "_" + hour + "." + minute + "." + second + "." + ms;
        return ts;
    }


    public void sleepForAWhile(Long sleepTime){
        try {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } catch (InterruptedException ex) {
            // handle error
        }

    }

    @Bean
    public Action<States, Events> initializationAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("-----------ACTION FOR INITIALIZATION------------");
                context.getExtendedState().getVariables().put("foo", 0);
            }
        };
    }

    @Bean
    public Action<States, Events> increaseAction() {
        return new Action<States, Events>() {
            @Override
            public void execute(StateContext<States, Events> context) {
                System.out.println("-----------ACTION FOR INCREASING FOO VARIABLE------------");

                Object sleep = context.getMessageHeaders().get("timeSleep");
                long longSleep = ((Number) sleep).longValue();

                Map<Object, Object> variables = context.getExtendedState().getVariables();
                Integer foo = context.getExtendedState().get("foo", Integer.class);

                /* For Initalization Action
                if (foo == null) {
                    logger.info("Init foo to 0");
                    variables.put("foo", 0);
                }*/

                if (foo == 0) {
                    logger.info("Switch foo from 0 to 1");
                    variables.put("foo", 1);
                    sleepForAWhile(longSleep);
                } else if (foo == 1) {
                    logger.info("Switch foo from 1 to 2");
                    variables.put("foo", 2);
                    sleepForAWhile(longSleep);
                } else if (foo == 2) {
                    logger.info("Switch foo from 2 to 0");
                    variables.put("foo", 0);
                    sleepForAWhile(longSleep);
                }
            }
        };
    }

    @Bean
    public StateMachineListener<States, Events> listener() {


        return new StateMachineListenerAdapter<States, Events>() {

            @Override
            public void transition(Transition<States, Events> transition) {

                /** Write transitions into log file using storeEvents method**/

                if (transition.getTarget().getId() == States.UNPAID) {
                    logger.info("====INITIALIZATION=====");
                    storeEvents(getTimeStamp() + " >>>>> " + "UNPAID" );
                    return;
                }

                if (transition.getSource().getId() == States.UNPAID
                        && transition.getTarget().getId() == States.WAITING_FOR_RECEIVE) {
                    logger.info("====TRANSITION1=====");
                    storeEvents(getTimeStamp() + " >>>>> " +"UNPAID --> WAITING");
                    return;
                }

                if (transition.getSource().getId() == States.WAITING_FOR_RECEIVE
                        && transition.getTarget().getId() == States.DONE) {
                    logger.info("====TRANSITION2=====");
                    storeEvents(getTimeStamp() + " >>>>> " + "WAITING --> DONE");
                    return;
                }

                if(transition.getSource().getId() == States.DONE
                        && transition.getTarget().getId() == States.UNPAID){
                    logger.info("====TRANSITION3=====");
                    storeEvents(getTimeStamp() + " >>>>> " + "DONE --> UNPAID");
                }


            }

        };
    }
}
