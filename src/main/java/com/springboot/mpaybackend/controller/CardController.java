package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.AddCardDto;
import com.springboot.mpaybackend.payload.CardDto;
import com.springboot.mpaybackend.service.CardService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/card")
public class CardController {

    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("{client_username}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<CardDto> addCard(@RequestBody AddCardDto dto, @PathVariable("client_username") String username) {

        return ResponseEntity.ok(cardService.addCard(dto, username));
    }

    @GetMapping("{key}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT')")
    public ResponseEntity<List<CardDto>> getCards(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the cards", example = "by=id OR by=username")
            String filter) {
        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok( cardService.getCardsByClientId( id ) );
            case "username":
                return ResponseEntity.ok( cardService.getCardsByClientUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }
}
