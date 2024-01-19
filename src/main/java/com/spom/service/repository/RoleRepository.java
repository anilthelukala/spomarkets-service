package com.spom.service.repository;

import com.spom.service.model.RoleEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RoleRepository extends ReactiveMongoRepository<RoleEntity, String> {

    @Query("{$or: [{ name: ?0},{code: ?1}]}")
    Flux<RoleEntity> findByNameOrCode(String name, String code);


}
