package com.konceptbuild.core.entity;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.enums.WorkerContractType;
import com.konceptbuild.core.request.AddWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "worker")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code_number", nullable = false, unique = true, insertable = false, updatable = false)
    private Integer codeNumber;

    @Generated(GenerationTime.INSERT)
    @Column(name = "code", nullable = false, unique = true, insertable = false, updatable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nif", nullable = false, unique = true)
    private String nif;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "phone_country_code", nullable = false)
    private String phoneCountryCode;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "function", nullable = false)
    private String function;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private WorkerContractType workerContractType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public WorkerEntity(WorkerDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.nif = dto.getNif();
        this.status = dto.getStatus();
        this.phoneCountryCode = dto.getPhoneCountryCode();
        this.phone = dto.getPhone();
        this.email = dto.getEmail();
        this.function = dto.getFunction();
        this.workerContractType = dto.getWorkerContractType();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
    }

    public WorkerEntity(AddWorkerRequest request) {
        this.name = request.name();
        this.nif = request.nif();
        this.status = request.status();
        this.phoneCountryCode = request.phoneCountryCode();
        this.phone = request.phone();
        this.email = request.email();
        this.function = request.function();
        this.workerContractType = request.workerContractType();
        this.startDate = request.startDate();
        this.endDate = request.endDate();
    }

    public WorkerEntity(UpdateWorkerRequest request) {
        this.id = request.id();
        this.name = request.name();
        this.nif = request.nif();
        this.status = request.status();
        this.phoneCountryCode = request.phoneCountryCode();
        this.phone = request.phone();
        this.email = request.email();
        this.function = request.function();
        this.workerContractType = request.workerContractType();
        this.startDate = request.startDate();
        this.endDate = request.endDate();
    }
}
