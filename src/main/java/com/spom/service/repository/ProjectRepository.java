package com.spom.service.repository;

import com.spom.service.model.ProjectEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectRepository extends ReactiveMongoRepository<ProjectEntity, String> {

    Mono<ProjectEntity> findById(String projectId);

}
