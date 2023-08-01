package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SatimReviewDto {
    @NotEmpty
    private String feedback;
}
