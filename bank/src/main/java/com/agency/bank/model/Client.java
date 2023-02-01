package com.agency.bank.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private long id;
    @Column
    private String name;
    @Column
    private String surname;
    @Column
    private String merchantId;
    @Column
    private String merchantPassword;
    @OneToOne(fetch = FetchType.EAGER)
    private Account account;
    @OneToOne(fetch = FetchType.EAGER,cascade =  CascadeType.ALL)
    private Card card;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER,cascade =  CascadeType.PERSIST)
    private List<Reservation> reservations;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY,cascade =  CascadeType.PERSIST)
    private List<Transaction> transactions;
}
