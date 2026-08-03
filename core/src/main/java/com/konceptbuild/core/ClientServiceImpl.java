package com.konceptbuild.core;

import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.entity.ClientEntity;
import com.konceptbuild.core.filter.ClientSortField;
import com.konceptbuild.core.filter.ClientFilter;
import com.konceptbuild.core.repository.ClientRepository;
import com.konceptbuild.core.request.ClientRequest;
import com.konceptbuild.core.util.ComparatorBuilder;
import com.konceptbuild.core.util.FilterHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<ClientDto> search(ClientFilter filter) {
        Comparator<ClientDto> comparator = ComparatorBuilder.buildComparator(
                filter.sortBy().fieldName(),
                filter.sortDirection(),
                ClientDto.class
        );

        // Keep inactive clients at the end, except when sorting by status
        if (filter.sortBy() != ClientSortField.STATUS) {
            comparator = Comparator
                    .comparing((ClientDto client) -> client.getStatus() == Status.INACTIVE)
                    .thenComparing(comparator);
        }

        return cacheServiceImpl.getAllClients().stream()
                .filter(client -> FilterHelper.matchesString(client.getCode(), filter.code()))
                .filter(client -> FilterHelper.matchesString(client.getCompanyName(), filter.companyName()))
                .filter(client -> FilterHelper.matchesString(client.getAddress(), filter.address()))
                .filter(client -> FilterHelper.matchesString(client.getPostalCode(), filter.postalCode()))
                .filter(client -> FilterHelper.matchesString(client.getCity(), filter.city()))
                .filter(client -> FilterHelper.matchesString(client.getDistrict(), filter.district()))
                .filter(client -> FilterHelper.matchesString(client.getNif(), filter.nif()))
                .filter(client -> FilterHelper.matchesString(client.getContact(), filter.contact()))
                .filter(client -> FilterHelper.matchesString(client.getEmail(), filter.email()))
                .filter(client -> FilterHelper.matchesString(client.getPhone(), filter.phone()))
                .filter(client -> FilterHelper.matchesEnum(client.getStatus(), filter.status()))
                .filter(client -> FilterHelper.matchesString(client.getNote(), filter.note()))
                .sorted(comparator)
                .toList();
    }

    @Override
    public void add(ClientRequest request) {
        clientRepository.findByNif(request.nif())
                .ifPresent(client -> {
                    String clientIdentifier = request.companyName() + " | " + request.nif();
                    throw new IllegalArgumentException("Client already defined - " + clientIdentifier);
                });

        ClientEntity entity = new ClientEntity(request);
        clientRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }

    @Override
    public void update(ClientRequest request) {
        String userIdentifier = request.companyName() + " | " + request.nif() + " | " + request.id();
        ClientEntity currentEntity = clientRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + userIdentifier));

        ClientEntity entity = new ClientEntity(request);
        entity.setCodeNumber(currentEntity.getCodeNumber());
        entity.setCode(currentEntity.getCode());
        clientRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }

    @Override
    public void delete(UUID id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client with ID " + id + " not found"));

        clientRepository.delete(entity);
        cacheServiceImpl.refreshCache();
    }
}
