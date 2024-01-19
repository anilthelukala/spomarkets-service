package com.spom.service.dto;

import java.util.Date;

import lombok.Data;

@Data
public class InvestmentDto {
    private String id;
	private String userId;
	private String projectId;
	private String propertyId;
	private String propertyFractionalisationId;
	private Boolean transactionaEnabled;
	private Double purchaseRateForUnit;
	private Double currentMarketRateForUnit;
	private Date purchaseDate;
    private Double rentalYield;
    private Double marketTrend;
	private String billingAddress;
	private String paymentConfirmationId;
    private String paymenetConfimationStatus;
	private Boolean updateStatus;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;
}
