package com.agency.bank.repository;

import com.agency.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query("select t from Transaction t left join fetch t.client c where c.id = ?2 and t.paymentId = ?1")
    Transaction findByPaymentId(int paymentId, long id);
}
