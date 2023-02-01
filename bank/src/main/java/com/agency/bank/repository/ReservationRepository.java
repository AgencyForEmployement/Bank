package com.agency.bank.repository;

import com.agency.bank.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("select r from Reservation r left join fetch r.client c where r.client.id = c.id")
    List<Reservation> getAllWithClients();
}
