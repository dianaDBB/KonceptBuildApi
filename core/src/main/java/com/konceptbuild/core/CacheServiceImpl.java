package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private WorkerHistoryRepository workerHistoryRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private ClientInvoiceRepository clientInvoiceRepository;

    @Autowired
    private ClientPaymentRepository clientPaymentRepository;

    private volatile List<WorkerDto> workers = List.of();

    private volatile List<WorkerHistoryDto> workersHistory = List.of();

    private volatile List<ClientDto> clients = List.of();

    private volatile List<WorkDto> works = List.of();

    private volatile List<WageDto> wages = List.of();

    private volatile List<ClientInvoiceDto> clientInvoices = List.of();

    private volatile List<ClientPaymentDto> clientPayments = List.of();

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void loadCache() {
        refreshCache();
    }

    @Override
    @Transactional(readOnly = true)
    public synchronized void refreshCache() {
        List<WorkerHistoryEntity> allWorkersHistory = workerHistoryRepository.findAll(Sort.by(Sort.Direction.ASC, "workerId"));
        workersHistory = allWorkersHistory.stream().map(WorkerHistoryDto::new).collect(Collectors.toList());

        Map<UUID, WorkerHistoryDto> workerHistoryById = workerHistoryRepository.findByValidToIsNull().stream()
                .collect(Collectors.toMap(history -> history.getWorker().getId(), WorkerHistoryDto::new));

        List<WorkerEntity> allWorkers = workerRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        workers = allWorkers.stream().map(worker -> new WorkerDto(worker, workerHistoryById.get(worker.getId()))).toList();

        List<ClientEntity> allClients = clientRepository.findAll(Sort.by(Sort.Direction.ASC, "companyName"));
        clients = allClients.stream().map(ClientDto::new).collect(Collectors.toList());

        Map<UUID, ClientDto> clientsById = clients.stream().collect(Collectors.toMap(ClientDto::getId, Function.identity()));

        List<WorkEntity> allWorks = workRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        works = allWorks.stream().map(work -> new WorkDto(work, clientsById.get(work.getClientId()))).toList();

        List<WageEntity> allWages = wageRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
        wages = allWages.stream().map(WageDto::new).toList();

        List<ClientInvoiceEntity> allClientInvoices = clientInvoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "docNumber"));
        clientInvoices = allClientInvoices.stream().map(ClientInvoiceDto::new).toList();

        List<ClientPaymentEntity> allClientPayments = clientPaymentRepository.findAll(Sort.by(Sort.Direction.DESC, "paymentDate"));
        clientPayments = allClientPayments.stream().map(ClientPaymentDto::new).toList();
    }

    @Override
    public List<WorkerDto> getAllWorkers() {
        return new ArrayList<>(workers);
    }

    @Override
    public List<WorkerDto> getAllActiveWorkers(Integer year, Integer month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        return this.getAllWorkers().stream()
                .filter(worker -> worker.isActiveDuringPeriod(
                        worker.getStartDate(),
                        worker.getEndDate(),
                        monthStart,
                        monthEnd))
                .toList();
    }

    @Override
    public Optional<WorkerDto> getWorkerById(UUID id) {
        return this.getAllWorkers().stream()
                .filter(work -> work.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ClientDto> getAllClients() {
        return new ArrayList<>(clients);
    }

    @Override
    public Optional<ClientDto> getClientById(UUID clientId) {
        return this.getAllClients().stream()
                .filter(client -> client.getId().equals(clientId))
                .findFirst();
    }

    @Override
    public List<WorkDto> getAllWorks() {
        return new ArrayList<>(works);
    }

    @Override
    public Optional<WorkDto> getWorkById(UUID id) {
        return this.getAllWorks().stream()
                .filter(work -> work.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<WageDto> getAllWages() {
        return new ArrayList<>(wages);
    }

    @Override
    public Optional<WorkerHistoryDto> getWorkerHistory(UUID workerId, Integer year, Integer month) {
        LocalDate date = LocalDate.of(year, month, 1);

        return workersHistory.stream()
                .filter(workerHistory -> workerHistory.getWorkerId().equals(workerId))
                .filter(workerHistory -> !workerHistory.getValidFrom().isAfter(date) &&
                        (workerHistory.getValidTo() == null || !workerHistory.getValidTo().isBefore(date))
                )
                .findFirst();
    }

    @Override
    public List<ClientInvoiceDto> getAllClientInvoices() {
        return new ArrayList<>(clientInvoices);
    }

    @Override
    public Optional<ClientInvoiceDto> getClientInvoiceById(UUID invoiceId) {
        return this.getAllClientInvoices().stream()
                .filter(invoice -> invoice.getId().equals(invoiceId))
                .findFirst();
    }

    @Override
    public List<ClientPaymentDto> getAllClientPayments() {
        return new ArrayList<>(clientPayments);
    }

    @Override
    public Optional<ClientPaymentDto> getClientPaymentById(UUID paymentId) {
        return this.getAllClientPayments().stream()
                .filter(payment -> payment.getId().equals(paymentId))
                .findFirst();
    }
}
