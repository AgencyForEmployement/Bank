package com.agency.bank.service;

import com.agency.bank.model.Reservation;
import com.agency.bank.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> getAllWithClients() {
        return reservationRepository.getAllWithClients();
    }

    public void deleteById(Reservation reservation) {
        reservationRepository.deleteById(reservation.getId());
    }

    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
}
