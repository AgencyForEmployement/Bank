package com.agency.bank.dto;

import com.agency.bank.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class TransactionPCCResponseDto {
    private TransactionStatus transactionStatus;
    private int merchantOrderId;
    private int acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private int issuerOrderId;
    private LocalDateTime issuerOrderTimestamp;
    private int paymentId;
    private double amount;
    private String description;
    private String payer;
    private String acquirerPan;
}
