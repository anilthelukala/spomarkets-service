package com.spom.service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import lombok.Data;

@Data
@Document(collection = "property")
public class PropertyEntity {

	@Id
	private String id;
	private String propertyCode;
	private String propertyName;
	private String description;
	private String addressLine1;
	private String addressLine2;
	private Long postalCode;
	private String image;
	private String videoLink;
	private String projectId;
	private Boolean propertyActiveFlag;
	private String propertyType;
	private Double baseRate;
	private Double latestRate;
	private Double gearedPercentage;
	private Double capitalGrowthRate;
	private Double historicalSuburbGrowthRate;
	private Double latestBlockValuation;
	private Date settlementDate;
	private Long bedType;
	private Long bathType;
	private Long parkingType;
	private String soldStatus;
	private PropertyFractionalisationEntity propertyFractionalisationEntity;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;
	private Date targetCompletionDate;

}
