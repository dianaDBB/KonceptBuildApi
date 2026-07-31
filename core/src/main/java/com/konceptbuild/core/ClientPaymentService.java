package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientPaymentDto;
import com.konceptbuild.core.filter.ClientPaymentFilter;
import com.konceptbuild.core.request.CreateClientPaymentRequest;
import com.konceptbuild.core.request.UpdateClientPaymentRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ClientPaymentService {
    List<ClientPaymentDto> search(ClientPaymentFilter filter);

    void add(CreateClientPaymentRequest request);

    void update(UpdateClientPaymentRequest request);

    void delete(UUID id);
}
