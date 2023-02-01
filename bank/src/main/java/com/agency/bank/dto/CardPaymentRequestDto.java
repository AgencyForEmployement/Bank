package com.agency.bank.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardPaymentRequestDto {
    private String pan;
    private String securityCode;
    private String cardHolderName;
    private String dateExpiration;
    private String description; //sta se kupuje
    private String panAcquirer; //to smo mi, web shop
    private double amount;
    public int acquirerOrderId; //id transakcije, tip number 10
    public LocalDateTime acquirerTimestamp;
}
