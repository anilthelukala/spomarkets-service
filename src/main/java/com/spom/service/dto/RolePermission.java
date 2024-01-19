package com.spom.service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RolePermission {
    private String id;
    private String service;
    private String feature;
    private String action;
    private Boolean roleFlag;
    private String role;
    private Date createdDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
    private RoleDto roleDto;
}
