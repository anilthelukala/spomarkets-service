package com.spom.service.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PropertyDto {
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
	private PropertyFractionalisationDto propertyFractionalisation;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;
	private Date targetCompletionDate;
	
	private MultipartFile file;
	private MultipartFile video;
}
