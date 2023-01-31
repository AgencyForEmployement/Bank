package com.agency.bank.dto;

import com.agency.bank.enums.TransactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionDto {
    private TransactionStatus transactionStatus;
    private int merchantOrderId;
    private int acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private int paymentId;
    private double amount;
    private String description;
}
