package com.spom.service.service;

import com.spom.service.dto.RoleDto;
import com.spom.service.model.RoleEntity;
import org.springframework.dao.DataAccessException;

public interface RoleService {
    RoleEntity saveRole(RoleDto userRole) throws DataAccessException;

    Boolean isDuplicateRole(RoleDto roleDto);
}
