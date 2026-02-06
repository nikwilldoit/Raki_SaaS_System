package com.raki.pos.business;

import com.raki.pos.business.model.Business;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDTO {
    private Integer id;
    private String name;
    private String address;
    private String type;
    private String phone;
    private String isActive;

    public static BusinessDTO fromEntity(Business b) {
        if (b == null) return null;
        return new BusinessDTO(
                b.getId(),
                b.getName(),
                b.getAddress(),
                b.getBusinessType(),
                b.getPhone(),
                b.getIsActive()
        );
    }

    public Business toEntity() {
        Business b = new Business();
        b.setId(id);
        b.setName(name);
        b.setAddress(address);
        b.setBusinessType(type);
        b.setPhone(phone);
        b.setIsActive(isActive);
        return b;
    }

}
