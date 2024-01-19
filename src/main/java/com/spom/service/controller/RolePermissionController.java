package com.spom.service.controller;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.RolePermission;
import com.spom.service.service.RolePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rolepermission")
public class RolePermissionController {

    private final Logger log = LoggerFactory.getLogger(RolePermissionController.class);

    @Autowired
    private RolePermissionService rolePermissionService;

    @RequestMapping(value = "/addRolePermission", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> addRolePermission(@RequestBody RolePermission rolePermission) {
        log.trace("Start RolePermissionController.. addRolePermission RequestBody :: {}", rolePermission);
        CommonResponse response = new CommonResponse();

        try {
            response.addData("RolePermission", this.rolePermissionService.saveRolePermission(rolePermission).block());
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Role Permission Saved"));

        } catch (Exception e) {
            log.error("Error in RolePermissionController addRolePermission Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
        }
        log.trace("End RolePermissionController.. addRolePermission Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
