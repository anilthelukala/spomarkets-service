package com.spom.service.service;

import com.spom.service.dto.RolePermission;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.RoleEntity;
import com.spom.service.model.RolePermissionEntity;
import com.spom.service.repository.RolePermissionRepository;
import com.spom.service.repository.RoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public Mono<RolePermissionEntity> saveRolePermission(RolePermission rolePermission) throws Exception {
        Mono<RoleEntity> monoRole = roleRepository.findById(rolePermission.getRoleDto().getId());
        if (null == monoRole || null == monoRole.block()) {
            throw new Exception("Role not found");
        }
        RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        Mono<RolePermissionEntity> monoRolePermission = rolePermissionRepository.findByServiceAndFeatureAndActionAndRole(rolePermission.getService(), rolePermission.getFeature(), rolePermission.getAction(), rolePermission.getRoleDto().getId());
        if (null != monoRolePermission) {
            rolePermissionEntity = monoRolePermission.block();
            if (null == rolePermissionEntity) {
                rolePermissionEntity = new RolePermissionEntity();
                BeanUtils.copyProperties(rolePermission, rolePermissionEntity);
                rolePermissionEntity.setRole(rolePermission.getRoleDto().getId());
                rolePermissionEntity.setCreatedBy(userInfo.getEmail());
                rolePermissionEntity.setCreatedDate(new Date());
            } else {
                rolePermissionEntity.setRoleFlag(rolePermission.getRoleFlag());
                rolePermissionEntity.setModifiedBy(userInfo.getEmail());
                rolePermissionEntity.setModifiedDate(new Date());
            }
        } else {
            BeanUtils.copyProperties(rolePermission, rolePermissionEntity);
            rolePermissionEntity.setRole(rolePermission.getRoleDto().getId());
            rolePermissionEntity.setCreatedBy(userInfo.getEmail());
            rolePermissionEntity.setCreatedDate(new Date());
        }
        return rolePermissionRepository.save(rolePermissionEntity);
    }

}
