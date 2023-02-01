package com.agency.bank.model;

import com.agency.bank.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private long id;
    @Column
    private int paymentId;
    @Column
    private TransactionStatus transactionStatus;
    @Column
    private int merchantOrderId;
    @Column
    private LocalDateTime merchantTimestamp;
    @Column
    private int acquirerOrderId;
    @Column
    private LocalDateTime acquirerTimestamp;
    @Column
    private int issuerOrderId;
    @Column
    private LocalDateTime issuerTimestamp;
    @Column
    private double amount;
    @Column
    private String description;
    @ManyToOne(fetch = FetchType.LAZY,cascade =  CascadeType.ALL)
    @JsonIgnore
    Client client;
}
