package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.enums.*;
import com.konceptbuild.core.enums.InvoiceStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ConfigsServiceImpl implements ConfigsService {
    @Override
    public List<StatusDto> getStatus() {
        return Arrays.stream(Status.values())
                .map(code -> StatusDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }

    @Override
    public List<WorkerContractTypeDto> getWorkerContractType() {
        return Arrays.stream(WorkerContractType.values())
                .map(code -> WorkerContractTypeDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }

    @Override
    public List<WorkStatusDto> getWorkStatus() {
        return Arrays.stream(WorkStatus.values())
                .map(code -> WorkStatusDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }

    @Override
    public List<AttendanceCodeDto> getAttendanceCodes() {
        return Arrays.stream(AttendanceCode.values())
                .map(code -> AttendanceCodeDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .paid(code.isPaid())
                        .build())
                .toList();
    }

    @Override
    public List<PaymentMethodDto> getPaymentMethod() {
        return Arrays.stream(PaymentMethod.values())
                .map(code -> PaymentMethodDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }

    @Override
    public List<ClientPaymentTypeDto> getClientPaymentTypes() {
        return Arrays.stream(ClientPaymentType.values())
                .map(code -> ClientPaymentTypeDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }

    @Override
    public List<InvoiceStatusDto> getInvoiceStatus() {
        return Arrays.stream(InvoiceStatus.values())
                .map(code -> InvoiceStatusDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }

    @Override
    public List<AgingDto> getAging() {
        return Arrays.stream(Aging.values())
                .map(code -> AgingDto.builder()
                        .code(code.getCode())
                        .label(code.getLabel())
                        .build())
                .toList();
    }
}
