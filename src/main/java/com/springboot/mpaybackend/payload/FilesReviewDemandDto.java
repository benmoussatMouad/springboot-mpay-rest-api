package com.springboot.mpaybackend.payload;

import lombok.Data;

import java.util.List;

@Data
public class FilesReviewDemandDto {
    private String feedback;
    private List<Long> idFilesToReview;
}
