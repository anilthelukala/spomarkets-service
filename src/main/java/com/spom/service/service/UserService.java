package com.spom.service.service;

import com.spom.service.dto.User;
import com.spom.service.dto.UserRoleDto;
import com.spom.service.model.UserEntity;
import org.springframework.dao.DataAccessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface UserService {
    Mono<UserEntity> findUserById(String id) throws DataAccessException;

    Flux<UserEntity> findUserByUsername(String username) throws DataAccessException;

    Collection<User> findAllUsers() throws DataAccessException;

    UserEntity saveUser(User user) throws DataAccessException;

    Boolean isDuplicateUser(User user);

    Boolean isDuplicateUserForUpdate(User user);

    UserEntity updateUser(User userDto) throws Exception;

    Mono<UserEntity> assignUserRole(UserRoleDto userRoleDto) throws Exception;

	String resetPassword(User user) throws Exception;

	User findByEmail(String username);

    Mono<UserEntity> kycUpdate(User user) throws Exception;
}
