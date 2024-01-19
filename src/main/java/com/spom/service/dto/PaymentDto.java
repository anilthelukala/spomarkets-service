package com.spom.service.dto;

import java.util.Date;

import com.spom.service.common.PaymentMethod;
import com.spom.service.common.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {

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

	public boolean availableUnitCheck(Long totalAvailableUnitsForTrade) {

		if (null == this.getNoOfUnitsToPurchase() || totalAvailableUnitsForTrade < this.getNoOfUnitsToPurchase()
				) {
			return false;
		}

		return true;
	}

public boolean priceValidationCheck(PropertyFractionalisationDto propertyFractionalisation) {

		
		
		// Check and price per unit
		if (Double.compare(propertyFractionalisation.getPricePerUnit(), this.pricePerUnit)!=0) {
			return false;
		} 
		
		double platformCharge=(this.getNoOfUnitsToPurchase() * this.getPricePerUnit() * propertyFractionalisation.getPlatformCharges()) / 100;
		platformCharge = Math.round(platformCharge * 100.0) / 100.0;
		
		// Check platform charges 
		if (Double.compare(platformCharge , this.platformCharge)!=0) {
			return false;
		}
		
		// Calculate expected total price and check against the provided total price
	    double expectedTotalPrice = this.getNoOfUnitsToPurchase() * this.getPricePerUnit()
	            + (this.getNoOfUnitsToPurchase() * this.getPricePerUnit() * propertyFractionalisation.getPlatformCharges()) / 100;
	    
	 // Round off to two decimal places
	    expectedTotalPrice = Math.round(expectedTotalPrice * 100.0) / 100.0;

	    if (Double.compare(expectedTotalPrice, this.getTotalPrice()) != 0) {
	        return false;
	    }

	    return true;
	}


	public boolean productValuesCheck() {
		if ((null != this.getPricePerUnit() && null != this.getTotalPrice() && null != this.getNoOfUnitsToPurchase())
				&& (this.getPricePerUnit() > 0 && this.getTotalPrice() > 0 && this.getNoOfUnitsToPurchase() > 0)
				&& (this.getPricePerUnit() <= this.getTotalPrice())) {
			return true;
		}
		return false;
	}

}
