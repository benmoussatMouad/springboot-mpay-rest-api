package com.springboot.mpaybackend.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BankPageDto {

    @Schema(name = "count", description = "how many elements in the whole database")
    private Long count;
    @Schema(name = "Banks List", description = "The banks page, contains the number specified in size parameter")
    private List<BankDto> banks;
}
