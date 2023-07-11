package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class FieldCheckDto {
    private boolean correct;
    private Integer line;
    private Integer positionStart;
    private Integer positionEnd;
    private String feedback;
    private String value;

    public void addFeedback(String feedback) {
        this.feedback += feedback + "\n";
    }

    public FieldCheckDto() {
        this.correct = true;
        this.feedback = "";
    }
}
