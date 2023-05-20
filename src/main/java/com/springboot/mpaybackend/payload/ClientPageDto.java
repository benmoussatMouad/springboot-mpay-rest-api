package com.springboot.mpaybackend.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ClientPageDto {

    @Schema(name = "count", description = "how many elements in the whole database")
    private Long count;

    @Schema(name = "Clients Page",
            description = "Contains as many as specified in size parameter")
    private List<ClientDto> clients;

}
