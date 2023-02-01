package com.agency.bank.dto;

import com.agency.bank.enums.TransactionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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
