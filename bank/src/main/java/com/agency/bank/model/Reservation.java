package com.agency.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private long id;
    @Column
    private String description;
    @Column
    private double amount;
    @Column
    private String acquirerAccountNumber;
    @ManyToOne(fetch = FetchType.LAZY,cascade =  CascadeType.ALL)
    @JsonIgnore
    Client client;
}
