package com.spom.service.service;

import org.springframework.dao.DataAccessException;


import reactor.core.publisher.Mono;

public interface FrankieOneService {
    Mono<String> createFrankieOneSession() throws DataAccessException, Exception;
}
