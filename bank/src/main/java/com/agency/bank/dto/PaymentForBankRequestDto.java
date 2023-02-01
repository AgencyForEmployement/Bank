package com.agency.bank.dto;

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
    private String description;
    private int merchantOrderId;
    private LocalDateTime merchantTimestamp;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
}