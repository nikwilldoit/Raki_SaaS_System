package com.raki.pos.staff;

import lombok.Data;

@Data
public class StaffUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String status;
}
