package com.spom.service.dto;

import com.spom.service.model.PropertyEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
public class ProjectDto {
	private String id;
	private String projectName;
	private String summary;
	private List<PropertyEntity> propertiesAssociated;
	private String performanceIndicators;
	private Boolean projectActiveFlag;
	private Date sattlementDate;
	private Double platformCharges;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;
}
