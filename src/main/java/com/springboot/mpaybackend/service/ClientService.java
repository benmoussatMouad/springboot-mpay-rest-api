package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.ClientDto;
import com.springboot.mpaybackend.payload.ClientPageDto;

import java.util.List;

public interface ClientService {
    ClientDto getClient(Long id);

    ClientDto getClientByUsername(String username);

    List<ClientDto> getAllClients();

    ClientPageDto getAllClients(int page, int size);

    ClientDto updateClient(ClientDto dto, Long id);

    ClientDto addClient(ClientDto dto);

    void deleteClient(Long id);
}
