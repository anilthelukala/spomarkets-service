package com.spom.service.repository;


import com.spom.service.model.UserEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserMstRepository extends ReactiveMongoRepository<UserEntity, String> {


    @Query("{email: ?0}")
    public Flux<UserEntity> findByEmail(String email);


    @Query("{$or: [{ mobileNo: ?0},{email: ?1}]}")
    public Flux<UserEntity> findByMobileNoOrEmail(Long mobileNo, String email);

    @Query("{$and: [{ _id: { $ne: ?0}},{ mobileNo: ?1}]}")
    public Flux<UserEntity> findByIdAndMobileNo(String id, Long mobileNo);
    
    @Query("{email: ?0}")
    public Mono<UserEntity> findUserByEmail(String email);

}
