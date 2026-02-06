package com.raki.pos.tax;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaxPolicyDTO {
    private Long id;
    private String name;
    private double rate;
    private String taxType;
}
