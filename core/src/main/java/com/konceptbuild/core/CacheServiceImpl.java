package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.entity.ClientEntity;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.repository.ClientRepository;
import com.konceptbuild.core.repository.WorkerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ClientRepository clientRepository;

    private volatile List<WorkerDto> workers = List.of();

    private volatile List<ClientDto> clients = List.of();

    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void loadCache() {
        refreshCache();
    }

    @Override
    public synchronized void refreshCache() {
        List<WorkerEntity> allWorkers = workerRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        workers = allWorkers.stream().map(WorkerDto::new).collect(Collectors.toList());

        List<ClientEntity> allClients = clientRepository.findAll(Sort.by(Sort.Direction.ASC, "companyName"));
        clients = allClients.stream().map(ClientDto::new).collect(Collectors.toList());
    }

    @Override
    public List<WorkerDto> getAllWorkers() {
        return new ArrayList<>(workers);
    }

    @Override
    public List<ClientDto> getAllClients() {
        return new ArrayList<>(clients);
    }
}
