package com.itu.celikelni.spring.boot.statemachine.data;

import com.itu.celikelni.spring.boot.statemachine.data.PaymentDbObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends MongoRepository<PaymentDbObject, Integer> {

}