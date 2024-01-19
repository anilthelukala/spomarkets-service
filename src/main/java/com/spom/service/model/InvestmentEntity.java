package com.spom.service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Data
@Document(collection = "investment")
public class InvestmentEntity {

	@Id
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
