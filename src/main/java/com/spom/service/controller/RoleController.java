package com.spom.service.controller;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.RoleDto;
import com.spom.service.service.RoleService;
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
@RequestMapping("/role")
public class RoleController {
    private final Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    /**
     * @param roleDto
     * @return
     */
    @RequestMapping(value = "/addRole", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> addRole(@RequestBody RoleDto roleDto) {
        log.trace("Start RoleController.. addRole RequestBody :: {}", roleDto);
        CommonResponse response = new CommonResponse();

        try {

            Boolean result = this.roleService.isDuplicateRole(roleDto);
            if (result)
                response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Duplicate Role"));
            else {

                response.addData("Role", this.roleService.saveRole(roleDto));
                response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Role Saved"));
            }

        } catch (Exception e) {
			log.error("Error in RoleController addRole Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Error Role Save"));
        }
        log.trace("End RoleController.. addRole Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

