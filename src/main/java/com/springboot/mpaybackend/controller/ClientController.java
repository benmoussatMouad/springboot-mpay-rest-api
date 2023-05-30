package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.ClientService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDto> addClient(@RequestBody ClientDto dto){
        ClientDto savedBank = clientService.addClient(dto);
        return new ResponseEntity<>(savedBank, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getClients() {

        return ResponseEntity.ok( clientService.getAllClients() );
    }

    @GetMapping("page")
    public ResponseEntity<ClientPageDto> getClientsByPage(
            @RequestParam(name = "page")
            @Parameter(description = "The number of the desired page, start from 0") Integer page,
            @RequestParam(name= "size")
            @Parameter(description = "The size of the page") Integer size
    ) {
        return ResponseEntity.ok( clientService.getAllClients( page, size ) );
    }

    @GetMapping("{key}")
    public ResponseEntity<ClientDto> getClientByKey(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok(clientService.getClient( id ));
            case "username":
                return ResponseEntity.ok( clientService.getClientByUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<ClientDto> updateClient(@RequestBody ClientDto dto,
                                                          @PathVariable Long id) {

        return ResponseEntity.ok( clientService.updateClient( dto, id ) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteClient(@PathVariable("id") Long id) {

        clientService.deleteClient( id );
        return ResponseEntity.ok( "Client deleted successfully" );
    }
}
