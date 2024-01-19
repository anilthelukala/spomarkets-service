package com.spom.service.dto;

import lombok.Data;

@Data
public class PaymentStatusDto {
    private String orderId;
    private String paymentTransactionId;
    private String paymentStatus;
    private String successMessage;
    private String errorMessage;
    private String errorCode;
}
