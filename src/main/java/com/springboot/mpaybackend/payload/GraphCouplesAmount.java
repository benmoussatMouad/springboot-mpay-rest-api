package com.springboot.mpaybackend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class GraphCouplesAmount {
    private Date date;
    private Double value;
}
