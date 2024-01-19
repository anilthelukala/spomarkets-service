package com.spom.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.spom.service.model.UserCartEntity;

import reactor.core.publisher.Flux;

@Repository
public interface UserCartRepository extends ReactiveMongoRepository<UserCartEntity, String> {

	Flux<UserCartEntity> findByUserNameAndIsActive(String userName,boolean isActive);

	Flux<UserCartEntity> findByIsActive(boolean isActive);

}
