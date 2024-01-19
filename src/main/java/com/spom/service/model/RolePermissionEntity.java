package com.spom.service.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;


@Data
@Document(collection = "role_permission")
public class RolePermissionEntity {
    
    @Id
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
}
