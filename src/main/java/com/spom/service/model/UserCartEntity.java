package com.spom.service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "user_cart")
public class UserCartEntity {
	
	@Id
	private String id;
	private String userName;
	private String userToken;
	private String propertyId;
	private Long noOfSelectedUnits;
	private Boolean isActive;
	private Date cartCreatedDate;
    private Date modifiedDate;
    private String createdBy;
    private String modifiedBy;
	
}
