package com.itu.celikelni.spring.boot.statemachine.data;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPaymentStateMachineRepo extends MongoDbStateMachineRepository {
}
