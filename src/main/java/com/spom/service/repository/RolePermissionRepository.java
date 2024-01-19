package com.spom.service.repository;

import com.spom.service.model.RolePermissionEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RolePermissionRepository extends ReactiveMongoRepository<RolePermissionEntity, String> {

    @Query("{role: ?0}")
    Flux<RolePermissionEntity> findByRole(String role);

    @Query("{service: ?0,feature :?1,action:?2,role:?3}")
    Mono<RolePermissionEntity> findByServiceAndFeatureAndActionAndRole(String service, String feature, String action,
                                                                       String role);
}
