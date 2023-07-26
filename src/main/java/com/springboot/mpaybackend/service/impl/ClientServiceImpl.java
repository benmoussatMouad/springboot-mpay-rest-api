package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Client;
import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.ClientDto;
import com.springboot.mpaybackend.payload.ClientPageDto;
import com.springboot.mpaybackend.repository.ClientRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.repository.WilayaRepository;
import com.springboot.mpaybackend.service.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    ClientRepository clientRepository;
    ModelMapper modelMapper;
    WilayaRepository wilayaRepository;
    UserRepository userRepository;

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper, WilayaRepository wilayaRepository, UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
        this.wilayaRepository = wilayaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ClientDto getClient(Long id) {
        Client admin = clientRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Client", "id", id ) );
        return modelMapper.map( admin, ClientDto.class );
    }

    @Override
    public ClientDto getClientByUsername(String username) {
        Client client = clientRepository.findByUserUsernameAndDeletedFalse( username )
                .orElseThrow( () -> new ResourceNotFoundException( "Client", "username", username ) );
        return modelMapper.map( client, ClientDto.class );
    }

    @Override
    public List<ClientDto> getAllClients() {
        List<Client> clients = clientRepository.findAll();

        return clients.stream().map( (element -> modelMapper.map( element, ClientDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public ClientPageDto getAllClients(int page, int size) {

        Page<Client> clients = clientRepository.findAll( PageRequest.of( page, size ) );

        List<ClientDto> clientDtos = clients.stream().map( user -> {

            ClientDto dto = modelMapper.map( user, ClientDto.class );
            dto.setWilayaId( user.getWilaya().getId() );
            dto.setUsername( user.getUser().getUsername() );
            return dto;

        } ).collect( Collectors.toList());

        ClientPageDto dto = new ClientPageDto();
        dto.setCount( clients.getTotalElements() );
        dto.setClients( clientDtos );

        return dto;
    }

    @Override
    public ClientDto updateClient(ClientDto dto, Long id) {
        Client client = clientRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Client", "id", id ) );

        if( dto.getFirstName() != null ) client.setFirstName( dto.getFirstName() );
        if( dto.getLastName() != null ) client.setLastName( dto.getLastName() );
        if( dto.getPhone() != null ) client.setPhone( dto.getPhone() );
        if( dto.getAddress() != null ) client.setAddress( dto.getAddress() );
        if( dto.getCommune() != null ) client.setCommune( dto.getCommune() );
        if( dto.getPostalCode() != null ) client.setPostalCode( dto.getPostalCode() );
        if( dto.getWilayaId() != null ) {
            Wilaya wilaya = wilayaRepository.findById( dto.getWilayaId() )
                    .orElseThrow( () -> new ResourceNotFoundException( "Wilaya", "id", dto.getWilayaId() ) );
            client.setWilaya( wilaya );
        }

        Client savedClient = clientRepository.save( client );
        ClientDto newDto = modelMapper.map( savedClient, ClientDto.class );
        newDto.setUsername( savedClient.getUser().getUsername() );
        newDto.setWilayaId( savedClient.getWilaya().getId() );

        return newDto;
    }

    @Override
    public ClientDto addClient(ClientDto dto) {
        return null;
    }

    @Override
    public void deleteClient(Long id) {
        if( clientRepository.existsById( id ) ) {
            clientRepository.deleteById( id );
        } else
            throw new ResourceNotFoundException( "Client", "id", id );
    }
}
