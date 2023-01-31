package com.agency.bank.repository;

import com.agency.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Transaction findByPaymentId(int paymentId);
}
