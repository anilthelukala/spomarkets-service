package com.spom.service.service;

import java.util.List;

import com.spom.service.dto.UserCartDto;
import com.spom.service.model.UserCartEntity;

import reactor.core.publisher.Mono;

public interface UserCartService {

    UserCartDto saveUserCart(UserCartDto userCartDto) throws Exception;

    List<UserCartDto> findByUserName(String userName)throws Exception;

	List<UserCartEntity> getAllActiveCart()throws Exception;

	Mono<String> removeCart(String id);

	UserCartDto findById(String cartId);

}
