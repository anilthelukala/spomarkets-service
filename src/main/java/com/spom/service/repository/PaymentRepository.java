package com.spom.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.spom.service.model.PaymentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<PaymentEntity, String> {

	Mono<PaymentEntity> findById(String paymentId);

    Flux<PaymentEntity> findByPaymentTransactionId(String paymentTransactionId);

    
	Flux<PaymentEntity> findByOrderId(String orderId);

}
