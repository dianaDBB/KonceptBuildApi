package com.konceptbuild.core;

import com.konceptbuild.core.dto.WageDto;
import com.konceptbuild.core.filter.WageFilter;
import com.konceptbuild.core.request.AddWageRequest;
import com.konceptbuild.core.request.UpdateWageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WageService {
    List<WageDto> search(WageFilter request);

    void add(AddWageRequest request);

    void update(UpdateWageRequest request);
}
