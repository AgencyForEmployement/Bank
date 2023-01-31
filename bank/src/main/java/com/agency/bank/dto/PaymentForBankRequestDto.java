package com.agency.bank.dto;

import com.agency.bank.model.PaymentURL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentForBankRequestDto {
    private String merchantId;
    private String merchantPassword;
    private double amount;
    private int merchantOrderId;
    private LocalDateTime merchantTimestamp;
    private PaymentURL successUrl;
    private PaymentURL failedUrl;
    private PaymentURL errorUrl;
}