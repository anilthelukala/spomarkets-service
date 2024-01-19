package com.spom.service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "property_fractionalisation")
public class PropertyFractionalisationEntity {

	@Id
	private String id;
	private String projectId;
	private String propertyId;
	private Double totalCostOfProperty;
	private Long totalNoOfPropertyUnits;
	private Long companyHoldingUnits;
	private Long temporaryUnitsBlocked;
	private Double gearedAmountOfProperty;
	private Long totalAvailableUnitsForTrade;
	private Double landPurchaseCost;
	private Double legalCost;
	private Double registrationCost;
	private Long soldUnits;
	private Double pricePerUnit;
	private Date propertyValutaionDate;
	private Long blockSize;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;

}
