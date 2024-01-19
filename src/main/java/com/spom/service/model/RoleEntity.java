package com.spom.service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "role")
public class RoleEntity {
    @Transient
    private List<RolePermissionEntity> permissions;
    @Id
    private String id;
    private String name;
    private String code;
    private Boolean roleActiveFlag;
    private Date createdDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
    
}
