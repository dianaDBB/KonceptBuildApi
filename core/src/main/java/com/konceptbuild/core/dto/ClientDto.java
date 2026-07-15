package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.ClientEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private UUID id;
    private Integer codeNumber;
    private String code;
    private String companyName;
    private String address;
    private String postalCode;
    private String city;
    private String district;
    private String nif;
    private String contact;
    private String email;
    private String phoneCountryCode;
    private String phone;
    private Status status;
    private String note;

    public ClientDto(ClientEntity entity) {
        this.id = entity.getId();
        this.codeNumber = entity.getCodeNumber();
        this.code = entity.getCode();
        this.companyName = entity.getCompanyName();
        this.address = entity.getAddress();
        this.postalCode = entity.getPostalCode();
        this.city = entity.getCity();
        this.district = entity.getDistrict();
        this.contact = entity.getContact();
        this.nif = entity.getNif();
        this.email = entity.getEmail();
        this.phoneCountryCode = entity.getPhoneCountryCode();
        this.phone = entity.getPhone();
        this.status = entity.getStatus();
        this.note = entity.getNote();
    }
}
