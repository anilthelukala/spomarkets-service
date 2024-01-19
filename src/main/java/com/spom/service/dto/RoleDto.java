package com.spom.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;



@Getter
@Setter
public class RoleDto {
    private String id;
    private String name;
    private String code;
    private Boolean roleActiveFlag;
    private Date createdDate;
    private Date modifiedDate;
}