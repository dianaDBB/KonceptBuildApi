package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.filter.ClientFilter;
import com.konceptbuild.core.request.ClientRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface ClientService {
    List<ClientDto> search(ClientFilter filter);

    void add(ClientRequest request);

    void update(ClientRequest request);

    void delete(UUID id);
}
