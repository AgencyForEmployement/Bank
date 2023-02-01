package com.agency.bank.repository;

import com.agency.bank.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    @Query("select c from Client c left join fetch c.card card where card.pan = ?1")
    Client findClientByPan(String pan);
}
