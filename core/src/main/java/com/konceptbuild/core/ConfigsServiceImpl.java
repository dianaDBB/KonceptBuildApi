package com.konceptbuild.core;

import com.konceptbuild.core.dto.AttendanceCodeDto;
import com.konceptbuild.core.dto.StatusDto;
import com.konceptbuild.core.dto.WorkStatusDto;
import com.konceptbuild.core.dto.WorkerContractTypeDto;
import com.konceptbuild.core.enums.AttendanceCode;
import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.enums.WorkerContractType;
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
}
