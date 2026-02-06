package com.raki.pos.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO {
    private Integer id;
    private Integer businessId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
}
