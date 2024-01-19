package com.spom.service.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.constant.SPOMarketsConstants;
import com.spom.service.dto.PaymentDto;
import com.spom.service.dto.PaymentStatusDto;
import com.spom.service.dto.PropertyFractionalisationDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.service.PaymentService;
import com.spom.service.service.PropertyFractionalisationService;

@CrossOrigin
@RestController
@RequestMapping("/payments")
public class PaymentController {

	private final Logger log = LoggerFactory.getLogger(PaymentController.class);

	
	@Autowired
	private PaymentService paymentsService;
	
	@Autowired
    private PropertyFractionalisationService propertyFractionalisationService;
	
	@PostMapping("/initiatePayment")
	public ResponseEntity<?> initiatePayment(@RequestBody List<PaymentDto> payments) {

		log.info("Start PaymentController::initiatePayment RequestBody:: {}", payments);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();
		CommonResponse response = new CommonResponse();
		String orderId = UUID.randomUUID().toString();
		try {
			for(PaymentDto payment:payments) {
				
				PropertyFractionalisationDto propertyFractionalisation = propertyFractionalisationService.findByPropertyId(payment.getPropertyId());
				if(!payment.productValuesCheck()) {
					response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INVALID_VALUES));
					return ResponseEntity.status(HttpStatus.OK).body(response);
				}
				if(!payment.availableUnitCheck(propertyFractionalisation.getTotalAvailableUnitsForTrade())) {
					response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Only "+propertyFractionalisation.getTotalAvailableUnitsForTrade()+" Units are available to purchase"));
					return ResponseEntity.status(HttpStatus.OK).body(response);
				}
				
			if(!payment.priceValidationCheck(propertyFractionalisation)) {
				response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INVALID_PRODUCT_PRICE));
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
			
						
			payment.setOrderId(orderId);
			payment.setUserName(userInfo.getUsername());
			}
			String stripeRedirectUrl = paymentsService.initiatePayment(payments);
			response.addData("stripeRedirectUrl", stripeRedirectUrl);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Payment Initiated"));
			log.info("End PaymentController.. initiatePayment ResponseBody:: {}", response);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error("Error in PaymentController.. initiatePayment :: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}


	@PostMapping("/paymentStatus")
	public ResponseEntity<?> updatePaymentStatus(@RequestBody PaymentStatusDto paymentStatus) {

		log.trace("Start PaymentsController.. updatePaymentStatus RequestBody :: {}", paymentStatus);
		CommonResponse response = new CommonResponse();
		try {
			paymentsService.updatePaymentStatus(paymentStatus);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Payment Status is updated"));
			
		} catch (Exception e) {
			log.error("Error in PaymentsController updatePaymentStatus Error Massage::{}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
			
		}
		log.trace("End PaymentsController.. updatePaymentStatus Response :: {}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);

	}

}
