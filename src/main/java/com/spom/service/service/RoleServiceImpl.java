package com.spom.service.service;

import com.spom.service.dto.RoleDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.RoleEntity;
import com.spom.service.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;


@Component
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleEntity saveRole(RoleDto role) throws DataAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        RoleEntity roleEntity = null;
        if (null != role.getId()) {
            Mono<RoleEntity> monoRole = roleRepository.findById(role.getId());
            roleEntity = monoRole.block();
            roleEntity.setCode(role.getCode());
            roleEntity.setName(role.getName());
            roleEntity.setRoleActiveFlag(role.getRoleActiveFlag());
            roleEntity.setModifiedDate(new Date());
            roleEntity.setModifiedBy(userInfo.getEmail());
        } else {
            roleEntity = new RoleEntity();
            roleEntity.setCode(role.getCode());
            roleEntity.setName(role.getName());
            roleEntity.setRoleActiveFlag(true);
            roleEntity.setCreatedDate(new Date());
            roleEntity.setCreatedBy(userInfo.getEmail());
        }
        return roleRepository.save(roleEntity).doOnError(error -> log.error("Error in saving Role", error)).onErrorResume(error -> {
            return Mono.error(new Exception(error));
        }).block();
    }

    @Override
    public Boolean isDuplicateRole(RoleDto roleDto) {
        Flux<RoleEntity> roleFlux = roleRepository.findByNameOrCode(roleDto.getName(), roleDto.getCode());
        if (roleFlux != null) {
            if (roleFlux.collectList().block().size() > 0)
                return true;
            else
                return false;

        } else
            return false;
    }

}
