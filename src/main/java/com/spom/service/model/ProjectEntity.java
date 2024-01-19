package com.spom.service.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "project")
public class ProjectEntity {

	@Transient
	private List<PropertyEntity> propertiesAssociated;
	@Id
	private String id;
	private String projectName;
	private String summary;
	private String performanceIndicators;
	private Boolean projectActiveFlag;
	private Date sattlementDate;
	private Double platformCharges;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;

}
