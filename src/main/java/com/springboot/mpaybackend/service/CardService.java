package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.AddCardDto;
import com.springboot.mpaybackend.payload.CardDto;

import java.util.List;

public interface CardService {

    CardDto addCard(AddCardDto dto, String clientUsername);

    List<CardDto> getCardsByClientId(Long clientId);

    List<CardDto> getCardsByClientUsername(String username);


}
