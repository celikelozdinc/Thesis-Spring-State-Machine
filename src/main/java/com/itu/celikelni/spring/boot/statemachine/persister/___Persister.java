package com.itu.celikelni.spring.boot.statemachine.persister;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import com.itu.celikelni.spring.boot.statemachine.entity.States;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.StateMachinePersist;



@Component
public class ___Persister implements StateMachinePersist<States, Events, Integer> {

    static Map<Integer, States> cache = new HashMap<>(16);

    @Override
    public void write(StateMachineContext<States, Events> context, Integer integer) throws Exception {
        System.out.println("--- PERSIST via WRITE METHOD ---");
        cache.put(integer, context.getState());
    }

    @Override
    public StateMachineContext<States, Events> read(Integer integer) throws Exception {
        System.out.println("--- RESTORE via READ METHOD ---");
        return cache.containsKey(integer) ?
                new DefaultStateMachineContext<>(cache.get(integer), null, null, null, null, null) :
                new DefaultStateMachineContext<>(States.UNPAID, null, null, null, null, null);
    }
}
