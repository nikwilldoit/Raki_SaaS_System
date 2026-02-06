package com.raki.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffUser {
    private Integer id;
    private Integer businessId;
    private String name;
    private String email;
    private String phone;
    private String status;
    private String password;   // plain text
    private Integer roleId;
    private String role;
}

