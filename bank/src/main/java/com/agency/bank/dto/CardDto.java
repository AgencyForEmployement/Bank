package com.agency.bank.dto;

import com.agency.bank.model.PaymentURL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    private int paymentId;
    private String pan;
    private String securityCode;
    private String cardHolderName;
    private String dateExpiration;
    private String description; //sta se kupuje
    private double amount;
    private PaymentURL successUrl;
    private PaymentURL failedUrl;
    private PaymentURL errorUrl;
}
