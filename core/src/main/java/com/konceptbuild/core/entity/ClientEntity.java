package com.konceptbuild.core.entity;

import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.request.ClientRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.util.UUID;

@Entity
@Table(name = "client")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code_number", nullable = false, unique = true, insertable = false, updatable = false)
    private Integer codeNumber;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code", nullable = false, unique = true, insertable = false, updatable = false)
    private String code;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "address")
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "nif", nullable = false, unique = true)
    private String nif;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_country_code", nullable = false)
    private String phoneCountryCode;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "note", nullable = false)
    private String note;


    public ClientEntity(ClientRequest request) {
        this.id = request.id();
        this.companyName = request.companyName();
        this.address = request.address();
        this.postalCode = request.postalCode();
        this.city = request.city();
        this.district = request.district();
        this.nif = request.nif();
        this.contact = request.contact();
        this.email = request.email();
        this.phoneCountryCode = request.phoneCountryCode();
        this.phone = request.phone();
        this.status = request.status();
        this.note = request.note();
    }
}
