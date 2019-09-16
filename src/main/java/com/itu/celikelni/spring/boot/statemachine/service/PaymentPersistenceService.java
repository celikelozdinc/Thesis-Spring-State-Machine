package com.itu.celikelni.spring.boot.statemachine.service;

import com.itu.celikelni.spring.boot.statemachine.entity.States;
import com.itu.celikelni.spring.boot.statemachine.entity.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.persist.StateMachinePersister;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentPersistenceService {

    @Autowired
    public StateMachinePersister<States, Events, UUID> persister;




}
