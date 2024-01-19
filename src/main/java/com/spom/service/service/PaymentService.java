package com.spom.service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spom.service.common.PaymentStatus;
import com.spom.service.dto.PaymentDto;
import com.spom.service.dto.PaymentStatusDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.InvestmentEntity;
import com.spom.service.model.PaymentEntity;
import com.spom.service.model.PropertyFractionalisationEntity;
import com.spom.service.repository.InvestmentRepository;
import com.spom.service.repository.PaymentRepository;
import com.spom.service.repository.PropertyFractionalisationRepsitory;
import com.spom.service.repository.PropertyRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.TaxRate;
import com.stripe.model.checkout.Session;
import com.stripe.param.TaxRateCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PropertyFractionalisationRepsitory propertyFractionalisationRepsitory;

	// @Autowired
	// private UserCartRepository userCartRepository;

	@Autowired
	private InvestmentRepository investmentRepository;

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@Value("${stripe.spo.success.url}")
	private String spoStripeSuccessUrl;

	@Value("${stripe.spo.failure.url}")
	private String spoStripeFailureUrl;

	public String initiatePayment(List<PaymentDto> paymentDtos) throws StripeException {

		log.trace("Start PaymentService::initiatePayment");
		Map<String, String> stripeResponse = new HashMap<String, String>();
		stripeResponse = createStripeCheckoutSession(paymentDtos);
		String stripeCheckoutSessionId = stripeResponse.get("stripeCheckoutSessionId");
		String stirpeRedirectUrl = stripeResponse.get("stripeRedirectUrl");
		PaymentEntity paymentEntity = null;
		for (PaymentDto paymentDto : paymentDtos) {
			PaymentEntity payment = new PaymentEntity();
			BeanUtils.copyProperties(paymentDto, payment);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserInfo userInfo = (UserInfo) authentication.getPrincipal();

			payment.setCreatedDate(new Date());
			payment.setCreatedBy(userInfo.getEmail());
			payment.setPaymentStatus(PaymentStatus.INITIATED);
			payment.setSessionTransactionId(payment.getSessionTransactionId());
			payment.setPaymentTransactionId(stripeCheckoutSessionId);
			paymentEntity = this.propertyRepository.findById(payment.getPropertyId()).flatMap(existingProperty -> {
				return this.paymentRepository.save(payment).flatMap(savedPayment -> {
					return this.propertyFractionalisationRepsitory.findByPropertyId(savedPayment.getPropertyId())
							.flatMap(existingPropertyFra -> {
								existingPropertyFra
										.setTemporaryUnitsBlocked(existingPropertyFra.getTemporaryUnitsBlocked()
												+ savedPayment.getNoOfUnitsToPurchase());

								return this.propertyFractionalisationRepsitory.save(existingPropertyFra);
							}).thenReturn(savedPayment);
				})
						.doOnError(error -> log.error("Error occured while initiating payment", error))
						.onErrorResume(error -> {
							return Mono.error(new Exception(error));
						});
			}).switchIfEmpty(
					Mono.error(new EntityNotFoundException("Property not found with ID: " + payment.getPropertyId())))
					.block();
		}
		PaymentDto savedPayment = new PaymentDto();
		BeanUtils.copyProperties(paymentEntity, savedPayment);
		log.trace("End Paym:: initiatePayment ResponseBody:: {}", savedPayment.getOrderId());
		return stirpeRedirectUrl;
	}

	public void updatePaymentStatus(PaymentStatusDto paymentStatus) throws Exception {
		log.trace("Start PaymentService::updatePaymentStatus");

		Flux<PaymentEntity> paymentEntity = this.paymentRepository
				.findByPaymentTransactionId(paymentStatus.getPaymentTransactionId());

		if (paymentEntity.collectList().block().size() > 0) {
			// PaymentEntity duplicPaymentEntity = paymentEntity.block();
			// if(null != duplicPaymentEntity && null != duplicPaymentEntity.getId()){
			throw new Exception("Payment Status with PaymentTransactionId '" + paymentStatus.getPaymentTransactionId()
					+ "'  already updated");
			// }
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserInfo userInfo = (UserInfo) authentication.getPrincipal();
		if (PaymentStatus.COMPLETED.toString().equalsIgnoreCase(paymentStatus.getPaymentStatus())) {
			paymentEntity = this.paymentRepository.findByOrderId(paymentStatus.getOrderId())
					.flatMapSequential(existingPayment -> {
						return this.propertyFractionalisationRepsitory
								.findByPropertyId(existingPayment.getPropertyId())
								.flatMap(existingPropertFrac -> {
									existingPropertFrac
											.setTemporaryUnitsBlocked(existingPropertFrac.getTemporaryUnitsBlocked()
													- existingPayment.getNoOfUnitsToPurchase());
									existingPropertFrac.setSoldUnits(existingPropertFrac.getSoldUnits()
											+ existingPayment.getNoOfUnitsToPurchase());
									existingPropertFrac.setModifiedBy(userInfo.getUsername());
									existingPayment.setModifiedDate(new Date());

									// Save the updated Property Fractionalisation
									return this.propertyFractionalisationRepsitory.save(existingPropertFrac)
											.doOnError(error -> log.error(
													"Error occurred while updating Property Fractionalisation", error))
											.onErrorResume(error -> Mono.error(new Exception(error)))
											.then(
													Mono.defer(() -> {
														existingPayment.setPaymentStatus(PaymentStatus
																.valueOf(paymentStatus.getPaymentStatus()
																		.toUpperCase()));
														existingPayment.setPaymentTransactionId(
																paymentStatus.getPaymentTransactionId());
														existingPayment.setModifiedBy(userInfo.getUsername());
														existingPayment.setModifiedDate(new Date());

														// Save the updated Payment
														return this.paymentRepository.save(existingPayment)
																.doOnError(error -> log.error(
																		"Error occurred while updating payment status",
																		error))
																.onErrorResume(
																		error -> Mono.error(new Exception(error)));
													}));
								})
								.switchIfEmpty(Mono.error(new EntityNotFoundException(
										"Property Fractionalisation not found for property with ID: "
												+ existingPayment.getPropertyId())));
					});

			List<PaymentEntity> updatedPaymentEntity = paymentEntity.collectList().block();

			if (updatedPaymentEntity.size() > 0) {
				for (PaymentEntity payEntity : updatedPaymentEntity) {
					if (null != payEntity) {
						InvestmentEntity investmentEntity = new InvestmentEntity();

						Mono<PropertyFractionalisationEntity> monoPropertyFra = propertyFractionalisationRepsitory
								.findByPropertyId(payEntity.getPropertyId());
						PropertyFractionalisationEntity PropertyFractionalisationEntity = monoPropertyFra.block();
						investmentEntity.setProjectId(PropertyFractionalisationEntity.getProjectId());
						investmentEntity.setPropertyId(payEntity.getPropertyId());
						investmentEntity.setPropertyFractionalisationId(PropertyFractionalisationEntity.getId());
						investmentEntity.setTransactionaEnabled(true);
						investmentEntity.setPurchaseRateForUnit(payEntity.getPricePerUnit());
						investmentEntity.setCurrentMarketRateForUnit(payEntity.getPricePerUnit());
						investmentEntity.setPurchaseDate(new Date());
						investmentEntity.setRentalYield((double) 0);
						investmentEntity.setMarketTrend((double) 0);
						investmentEntity.setPaymentConfirmationId(paymentStatus.getPaymentTransactionId());
						investmentEntity.setPaymenetConfimationStatus(paymentStatus.getPaymentStatus().toUpperCase());
						investmentEntity.setCreatedDate(new Date());
						investmentEntity.setCreatedBy(userInfo.getUsername());
						investmentEntity.setUserId(userInfo.getUserId());

						this.investmentRepository.save(investmentEntity)
								.doOnError(error -> log.error("Error occured while save investment", error))
								.onErrorResume(error -> {
									return Mono.error(new Exception(error));
								}).block();

					}

					// investmentEntity.set
				}
			}else {
				throw new Exception("Initiate Payment not found for OrderId '" + paymentStatus.getOrderId()
				);
			}
		} else {
			paymentEntity = this.paymentRepository.findByOrderId(paymentStatus.getOrderId())
					.flatMapSequential(existingPayment -> {return this.propertyFractionalisationRepsitory
							.findByPropertyId(existingPayment.getPropertyId())
							.flatMap(existingPropertFrac -> {
								existingPropertFrac
										.setTemporaryUnitsBlocked(existingPropertFrac.getTemporaryUnitsBlocked()
												- existingPayment.getNoOfUnitsToPurchase());
								existingPropertFrac.setModifiedBy(userInfo.getUsername());
								existingPropertFrac.setModifiedDate(new Date());
								// Save the updated Property Fractionalisation
								return propertyFractionalisationRepsitory.save(existingPropertFrac)
										.doOnError(error -> log.error(
												"Error occurred while updating Property Fractionalisation", error))
										.onErrorResume(error -> Mono.error(new Exception(error)))
										.then( // Continue with the existingPayment update
												Mono.defer(() -> {
													existingPayment.setPaymentStatus(PaymentStatus
															.valueOf(paymentStatus.getPaymentStatus().toUpperCase()));
													existingPayment.setPaymentErrorCode(paymentStatus.getErrorCode());
													existingPayment
															.setPaymentErrorMessage(paymentStatus.getErrorMessage());
													existingPayment.setPaymentTransactionId(
															paymentStatus.getPaymentTransactionId());
													existingPayment.setModifiedBy(userInfo.getUsername());
													existingPayment.setModifiedDate(new Date());
													// Save the updated Payment
													return this.paymentRepository.save(existingPayment)
															.doOnError(error -> log.error(
																	"Error occurred while updating payment status",
																	error))
															.onErrorResume(error -> Mono.error(new Exception(error)));
												}));
							})
							.switchIfEmpty(
									Mono.error(new EntityNotFoundException(
											"Property Fractionalisation not found for property with ID: "
													+ existingPayment.getPropertyId())));});

			List<PaymentEntity> updatedPaymentEntity = paymentEntity.collectList().block();

			if (updatedPaymentEntity.size() == 0) {
				throw new Exception("Initiate Payment not found for OrderId '" + paymentStatus.getOrderId()
						);
			}
			
		}

		log.trace("End PaymentService::updatePaymentStatus");
	}

	private Map<String, String> createStripeCheckoutSession(List<PaymentDto> paymentDtos)
			throws StripeException {
		Stripe.apiKey = stripeApiKey;
		String orderId = paymentDtos.get(0).getOrderId();
		Map<String, String> response = new HashMap<String, String>();
		List<LineItem> lineItems = new ArrayList<>();
		TaxRateCreateParams taxParams =
		TaxRateCreateParams.builder()
			.setDisplayName("Platform Charge")
			.setInclusive(false)
			.setPercentage(new BigDecimal(0.5))
			.setDescription("SPO Platform Charge")
			.build();

		TaxRate taxRate = TaxRate.create(taxParams);

		for (PaymentDto paymentDto : paymentDtos) {
			LineItem item = SessionCreateParams.LineItem.builder()
					.setPriceData(
							SessionCreateParams.LineItem.PriceData.builder()
									.setCurrency("AUD")
									.setProductData(
											SessionCreateParams.LineItem.PriceData.ProductData.builder()
													.setName(paymentDto.getPropertyName())
													.build())
									.setUnitAmount((Double.valueOf(paymentDto.getPricePerUnit()).longValue()) * 100)								
									.build())
					.setQuantity(paymentDto.getNoOfUnitsToPurchase())
					.addTaxRate(taxRate.getId())
					.build();
			lineItems.add(item);
		}
				
		SessionCreateParams params = SessionCreateParams.builder()
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setCustomerEmail(paymentDtos.get(0).getUserName())
				.setSuccessUrl(spoStripeSuccessUrl + orderId)
				.setCancelUrl(spoStripeFailureUrl + orderId)
				.addAllLineItem(lineItems)
				.setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.AUTO)
				.build();

		Session session;
		session = Session.create(params);
		response.put("stripeCheckoutSessionId", session.getId());
		response.put("stripeRedirectUrl", session.getUrl());
		return response;
	}

}
