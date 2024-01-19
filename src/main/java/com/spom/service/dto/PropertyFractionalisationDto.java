package com.spom.service.dto;

import lombok.*;

import java.util.Date;

@Data
public class PropertyFractionalisationDto {
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
	private Double platformCharges;
	
	
	private Double acquisitionCost;
	private Double developmentCost;
	private Double equity;
	private Double gearingEffect; 
	private Double spoValuation;
	
	 public Double getEquity() {
		 if(null!=this.gearedAmountOfProperty&&null!= this.gearedAmountOfProperty) {
			 return this.gearedAmountOfProperty > 0 ? this.totalCostOfProperty - this.gearedAmountOfProperty : this.totalCostOfProperty;
		 } else {
			 return (double) 0;
		 }
	       
	    }
	
	 public Double getGearingEffect() {
		 if(null!=this.gearedAmountOfProperty&&null!=this.totalCostOfProperty) {
	        return this.gearedAmountOfProperty > 0 ? (this.gearedAmountOfProperty / this.totalCostOfProperty) * 100 : 0;
		 }
	        else {
				 return (double) 0;
			 }
	    }
	 
	 public Double getAcquisitionCost() {
		 if(null!=this.legalCost&&null!=this.registrationCost) {
	        return this.legalCost +this.registrationCost;
		 
	 }
     else {
			 return (double) 0;
		 }
	    }
	 
	 public Double getDevelopmentCost() {
		 if(null!=this.totalCostOfProperty&&null!=this.legalCost&&null!=this.registrationCost&&null!=this.landPurchaseCost) {
	        return this.totalCostOfProperty -(this.legalCost +this.registrationCost+this.landPurchaseCost);
		 
	 }
     else {
			 return (double) 0;
		 }
	    }
	 public Double getSpoValuation() {
		 if(null!=this.gearedAmountOfProperty&&null!= this.gearedAmountOfProperty) {
	        return (this.gearedAmountOfProperty > 0 ? this.totalCostOfProperty - this.gearedAmountOfProperty : this.totalCostOfProperty)/1000;
		 
	 }
     else {
			 return (double) 0;
		 }
	    }
	
}

