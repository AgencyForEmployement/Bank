package com.agency.bank.service;

import com.agency.bank.model.Client;
import com.agency.bank.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public Client findByPan(String pan) {
        Client c = clientRepository.findClientByPan(pan);
        return c;
    }
}
