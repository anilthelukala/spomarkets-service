package com.spom.service.repository;

import com.spom.service.model.PropertyEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PropertyRepository extends ReactiveMongoRepository<PropertyEntity, String> {

    Flux<PropertyEntity> findByProjectId(String projectId);

    Mono<PropertyEntity> findById(String projectId);

    Mono<Void> deleteByProjectId(String projectId);

    Flux<PropertyEntity> findByPostalCode(Long postalCode);

    Flux<PropertyEntity> findByProjectIdAndPostalCode(String projectId, Long postalCode);

}