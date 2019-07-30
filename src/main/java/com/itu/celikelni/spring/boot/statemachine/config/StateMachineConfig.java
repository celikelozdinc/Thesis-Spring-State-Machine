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

import java.util.EnumSet;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private File jarfile = new File(System.getProperty("java.class.path"));
    private File logfile;


    /** Default Constructor **/
    public StateMachineConfig(){

        createLogFile();
    }


    /**
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states.withStates()
                .initial(States.UNPAID)
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
                .and()
                .withExternal()
                .source(States.WAITING_FOR_RECEIVE).target(States.DONE)
                .event(Events.RECEIVE);
    }

    /**
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config.withConfiguration()
                .listener(listener());
    }

    public void createLogFile(){

        File dir = jarfile.getAbsoluteFile().getParentFile();
        String path = dir.toString();
        System.out.println("PATH ---> " + path);


        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        String fd = path + "/log_" + year + "_" + month + "_"  + day + "_" + hour + "_" + minute + "_" + second+".txt" ;
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

    @Bean
    public StateMachineListener<States, Events> listener() {


        return new StateMachineListenerAdapter<States, Events>() {

            @Override
            public void transition(Transition<States, Events> transition) {

                /** Write transitions into log file using storeEvents method**/

                if (transition.getTarget().getId() == States.UNPAID) {
                    logger.info("====TRANSITION1=====");
                    storeEvents("UNPAID");
                    return;
                }

                if (transition.getSource().getId() == States.UNPAID
                        && transition.getTarget().getId() == States.WAITING_FOR_RECEIVE) {
                    logger.info("====TRANSITION2=====");
                    storeEvents("UNPAID --> WAITING");
                    return;
                }

                if (transition.getSource().getId() == States.WAITING_FOR_RECEIVE
                        && transition.getTarget().getId() == States.DONE) {
                    logger.info("====TRANSITION3=====");
                    storeEvents("WAITING --> DONE");
                    return;
                }
            }

        };
    }
}
