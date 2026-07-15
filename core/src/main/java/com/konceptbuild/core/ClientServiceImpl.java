package com.konceptbuild.core;

import com.konceptbuild.core.dto.Status;
import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.entity.ClientEntity;
import com.konceptbuild.core.filter.ClientSortField;
import com.konceptbuild.core.filter.SortDirection;
import com.konceptbuild.core.filter.ClientFilter;
import com.konceptbuild.core.repository.ClientRepository;
import com.konceptbuild.core.request.ClientRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<ClientDto> search(ClientFilter filter) {
        Comparator<ClientDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        // Keep inactive clients at the end, except when sorting by status.
        if (filter.sortBy() != ClientSortField.STATUS) {
            comparator = Comparator
                    .comparing((ClientDto client) -> client.getStatus() == Status.INACTIVE)
                    .thenComparing(comparator);
        }

        return cacheServiceImpl.getAllClients().stream()
                .filter(client -> matchesString(client.getCode(), filter.code()))
                .filter(client -> matchesString(client.getCompanyName(), filter.companyName()))
                .filter(client -> matchesString(client.getAddress(), filter.address()))
                .filter(client -> matchesString(client.getPostalCode(), filter.postalCode()))
                .filter(client -> matchesString(client.getCity(), filter.city()))
                .filter(client -> matchesString(client.getDistrict(), filter.district()))
                .filter(client -> matchesString(client.getNif(), filter.nif()))
                .filter(client -> matchesString(client.getContact(), filter.contact()))
                .filter(client -> matchesString(client.getEmail(), filter.email()))
                .filter(client -> matchesString(client.getPhone(), filter.phone()))
                .filter(client -> filter.status() == null || filter.status() == client.getStatus())
                .filter(client -> matchesString(client.getNote(), filter.note()))
                .sorted(comparator)
                .toList();
    }

    private boolean matchesString(String value, String query) {
        return query == null || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    private Comparator<ClientDto> comparatorFor(ClientSortField field, SortDirection sortDirection) {
        Comparator<String> stringComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed())
                        : Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

        return switch (field) {
            case CODE -> Comparator.comparing(ClientDto::getCode, stringComparator);
            case COMPANY_NAME -> Comparator.comparing(ClientDto::getCompanyName, stringComparator);
            case ADDRESS -> Comparator.comparing(ClientDto::getAddress, stringComparator);
            case POSTAL_CODE -> Comparator.comparing(ClientDto::getPostalCode, stringComparator);
            case CITY -> Comparator.comparing(ClientDto::getCity, stringComparator);
            case DISTRICT -> Comparator.comparing(ClientDto::getDistrict, stringComparator);
            case NIF -> Comparator.comparing(ClientDto::getNif, stringComparator);
            case CONTACT -> Comparator.comparing(ClientDto::getContact, stringComparator);
            case EMAIL -> Comparator.comparing(ClientDto::getEmail, stringComparator);
            case PHONE -> Comparator.comparing(ClientDto::getPhone, stringComparator);
            case STATUS -> Comparator.comparing(
                    ClientDto::getStatus,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case NOTE -> Comparator.comparing(ClientDto::getNote, stringComparator);

        };
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
