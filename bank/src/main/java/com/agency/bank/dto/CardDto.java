package com.agency.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    private String paymentId;
    private String pan;
    private String securityCode;
    private String cardHolderName;
    private String dateExpiration;
    private String description;
    private String amount;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
}
