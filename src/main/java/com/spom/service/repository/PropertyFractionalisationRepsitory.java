package com.spom.service.repository;

import com.spom.service.model.PropertyFractionalisationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PropertyFractionalisationRepsitory
        extends ReactiveMongoRepository<PropertyFractionalisationEntity, String> {

    Mono<PropertyFractionalisationEntity> findByProjectId(String projectId);

    Mono<Void> deleteByProjectId(String projectId);

    Mono<Void> deleteByPropertyId(String propertyId);

	Mono<PropertyFractionalisationEntity> findByPropertyId(String propertyId);

}
