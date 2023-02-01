package com.agency.bank.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private String paymentURL;
    private int paymentId;
    private double amount;
    private String description;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
}