package com.spom.service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.spom.service.common.PaymentMethod;
import com.spom.service.common.PaymentStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "payments")
public class PaymentEntity {

	@Id
	private String id;
	private String userName;
	private String propertyName;
	private String sessionTransactionId;
	private String propertyId;
	private Long noOfUnitsToPurchase;
	private Double pricePerUnit;
	private Double platformCharge;
	private Double totalPrice;
	private PaymentMethod paymentMethod;
	private String paymentTransactionId;
	private PaymentStatus paymentStatus;
	private String paymentErrorMessage;
	private String paymentErrorCode;
	private String orderId;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;

}
