package com.spom.service.model;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "property_stock")
public class PropertyStockEntity {

	@Id
    private String id;
	private String propertyId;
	private String userid;
	private Long offerToSaleUnits;
	private Long bidToBuyUnits;
	private Date offersaleDate;
	private Date bidBuyDate;
	private Double offerRatePerUnit;
	private Double bidRatePerUnit;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date modifiedDate;

	
}
