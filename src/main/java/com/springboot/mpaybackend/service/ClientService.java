package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.ClientDto;
import com.springboot.mpaybackend.payload.ClientPageDto;
import com.springboot.mpaybackend.payload.CreateClientDto;

import jakarta.validation.Valid;

import java.util.List;

public interface ClientService {
    ClientDto getClient(Long id);

    ClientDto getClientByUsername(String username);

    List<ClientDto> getAllClients();

    ClientPageDto getAllClients(int page, int size, String name, String phone, String pan, Long id);

    ClientDto updateClient(ClientDto dto, Long id);

    ClientDto addClient(CreateClientDto dto);

    void deleteClient(Long id);
}
