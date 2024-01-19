package com.spom.service.dto;

import java.util.Date;

import com.spom.service.model.PropertyEntity;

import lombok.Data;

@Data
public class UserCartDto {
	private String id;
	private String userName;
	private String userToken;
	private String propertyId;
	private Long noOfSelectedUnits;
	private Boolean isActive;
	private PropertyFractionalisationDto propertyFractionalisation;
	private PropertyEntity property;
	private Date cartCreatedDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
}
