package com.spom.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
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
    @JsonProperty(value = "r_request")
    private String recaptchaResponse;
    private RoleDto roleDto;
    private Date lockoutEndTime;

    private Long kycAttempts;
    private boolean kycVerificationBlocked;
}
