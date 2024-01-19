package com.spom.service.service;

import com.spom.service.controller.PaymentController;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.UserEntity;
import com.spom.service.repository.RoleRepository;
import com.spom.service.repository.UserMstRepository;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;



public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    @Autowired
    private UserMstRepository userMstRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.trace("Start DomainUserDetailsService.. loadUserByUsername username :: {}", username);
        Flux<UserEntity> userByUsername = this.userMstRepository.findByEmail(username).flatMap(user -> {
            String roleId = user.getRole();
            return this.roleRepository.findById(roleId).map(role -> {
                user.setRoleEntity(role);
                return user;
            }).thenReturn(user);
        }).doOnError(error -> log.error("Error in DomainUserDetailsService loadUserByUsername Error Massage::{}", error.getMessage())).onErrorResume(error -> {
            return Flux.error(new Exception(error));
        });

        if (null == userByUsername) {
            log.error("Error in DomainUserDetailsService loadUserByUsername For User::{} Error Massage::{} ",username, "Could not find user with that username");
            throw new UsernameNotFoundException("Invalid credentials!");
        }
        UserEntity user = userByUsername.blockFirst();
        if (user == null || !user.getEmail().equals(username)) {
            log.error("Error in DomainUserDetailsService loadUserByUsername For User::{} Error Massage::{} ",username, "Could not find user with that username");
            throw new UsernameNotFoundException("Invalid credentials!");
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRoleEntity().getName()));
        log.trace("End DomainUserDetailsService.. loadUserByUsername Response :: {}", user);
        return new UserInfo(user.getEmail(), user.getPassword(), true, true, true, true, grantedAuthorities, user.getFirstName(), user.getLastName(), user.getEmail(),user.getId());
    }

}