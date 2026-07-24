package com.konceptbuild.core;

import com.konceptbuild.core.dto.AttendanceCodeDto;
import com.konceptbuild.core.dto.StatusDto;
import com.konceptbuild.core.dto.WorkStatusDto;
import com.konceptbuild.core.dto.WorkerContractTypeDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ConfigsService {
    List<StatusDto> getStatus();

    List<WorkerContractTypeDto> getWorkerContractType();

    List<WorkStatusDto> getWorkStatus();

    List<AttendanceCodeDto> getAttendanceCodes();
}
