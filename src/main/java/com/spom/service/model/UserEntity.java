package com.spom.service.model;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document(collection = "user")
public class UserEntity {

    @Transient
    private RoleEntity roleEntity;
    
    @Id
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String countryCode;
    private Long mobileNo;
    private Boolean userActiveFlag;
    private String kycVerification;
    private String streetAddress;
   	private String city;
   	private String state;
   	private String zipcode;
   	private String country;
   	private String oldPassword;
   	private Date kycVerificationDate;
    private Date createdDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
    private String role;
    private Date lockoutEndTime;
    private Long failedAttempts;

    private Long kycAttempts;
    private boolean kycVerificationBlocked;
}
