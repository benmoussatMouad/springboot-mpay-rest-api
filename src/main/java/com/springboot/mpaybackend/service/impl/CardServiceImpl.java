package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Client;
import com.springboot.mpaybackend.entity.ClientCard;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.AddCardDto;
import com.springboot.mpaybackend.payload.CardDto;
import com.springboot.mpaybackend.repository.CardRepository;
import com.springboot.mpaybackend.repository.ClientRepository;
import com.springboot.mpaybackend.service.CardService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {
    private CardRepository cardRepository;
    private ModelMapper modelMapper;
    private ClientRepository clientRepository;

    public CardServiceImpl(CardRepository cardRepository, ModelMapper modelMapper, ClientRepository clientRepository) {

        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
        this.clientRepository = clientRepository;
    }

    @Override
    public CardDto addCard(AddCardDto dto, String clientUsername) {



        Client client = clientRepository.findByUserUsernameAndDeletedFalse(clientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "username", clientUsername));

        ClientCard card = modelMapper.map(dto, ClientCard.class);
        card.setClient(client);

        ClientCard savedCard = cardRepository.save(card);

        return modelMapper.map(savedCard, CardDto.class);
    }

    @Override
    public List<CardDto> getCardsByClientId(Long clientId) {

        List<ClientCard> clientCards = cardRepository.findByClientIdAndDeletedFalse(clientId);

        return clientCards.stream().map(card -> modelMapper.map(card, CardDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<CardDto> getCardsByClientUsername(String username) {
        List<ClientCard> clientCards = cardRepository.findByClientUserUsernameAndDeletedFalse(username);

        return clientCards.stream().map(card -> modelMapper.map(card, CardDto.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteCard(Long id, String name) {
        List<ClientCard> clientCards = cardRepository.findByClientUserUsernameAndDeletedFalse(name);

        if (!clientCards.stream().anyMatch(card -> card.getId().equals(id))) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Card does not belong to client");
        }
        
        ClientCard card = cardRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new ResourceNotFoundException("Client card", "id", id));

        card.setDeleted(true);

        cardRepository.save(card);
    }

    
}
