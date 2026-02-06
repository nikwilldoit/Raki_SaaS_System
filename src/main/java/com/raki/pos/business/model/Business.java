package com.raki.pos.business.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {
    private Integer id;
    private String businessType;
    private String name;
    private String address;
    private String phone;
    private String isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}