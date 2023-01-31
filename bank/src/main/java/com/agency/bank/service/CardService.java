package com.agency.bank.service;

import com.agency.bank.model.Card;
import com.agency.bank.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;

    public Card findByPan(String pan) {
        return cardRepository.findByPan(pan);
    }
}
