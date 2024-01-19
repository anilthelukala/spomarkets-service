package com.spom.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.spom.service.model.InvestmentEntity;

@Repository
public interface InvestmentRepository extends ReactiveMongoRepository<InvestmentEntity, String> {
    
}
