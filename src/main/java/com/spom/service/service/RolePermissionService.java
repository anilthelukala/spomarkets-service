package com.spom.service.service;


import com.spom.service.dto.RolePermission;
import com.spom.service.model.RolePermissionEntity;
import reactor.core.publisher.Mono;

public interface RolePermissionService {
    Mono<RolePermissionEntity> saveRolePermission(RolePermission rolePermission) throws Exception;
}
