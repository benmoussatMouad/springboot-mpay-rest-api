package com.springboot.mpaybackend.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserAgencyPageDto {

    @Schema(name = "count", description = "how many elements in the whole database")
    private Long count;
    @Schema(name = "List of agency users", description = "The number of element is specified in size parameter")
    private List<UserAgencyResponseDto> userPage;

}
