package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ConfigsService {
    List<StatusDto> getStatus();

    List<WorkerContractTypeDto> getWorkerContractType();

    List<WorkStatusDto> getWorkStatus();

    List<AttendanceCodeDto> getAttendanceCodes();

    List<PaymentMethodDto> getPaymentMethod();
}
