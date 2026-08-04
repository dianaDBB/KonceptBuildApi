package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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

    @Autowired
    private ClientPaymentInvoiceRepository clientPaymentInvoiceRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void loadCache() {
        refreshCache();
    }

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(allEntries = true, cacheNames = {"workers", "clients", "works", "wages", "invoices", "payments", "workerHistory"})
    public void refreshCache() {
        getAllWorkers();
        getAllClients();
        getAllWorks();
        getAllWages();
        getAllClientInvoices();
        getAllClientPayments();
    }


    @Override
    @Cacheable(value = "workers")
    public List<WorkerDto> getAllWorkers() {
        Map<UUID, WorkerHistoryDto> workerHistoryById = workerHistoryRepository.findByValidToIsNull().stream()
                .collect(Collectors.toMap(history -> history.getWorker().getId(), WorkerHistoryDto::new));

        List<WorkerEntity> allWorkers = workerRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return allWorkers.stream()
                .map(worker -> new WorkerDto(worker, workerHistoryById.get(worker.getId())))
                .toList();
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
    @Cacheable(value = "workers", key = "#id")
    public Optional<WorkerDto> getWorkerById(UUID id) {
        return this.getAllWorkers().stream()
                .filter(worker -> worker.getId().equals(id))
                .findFirst();
    }

    @Override
    @Cacheable(value = "clients")
    public List<ClientDto> getAllClients() {
        List<ClientEntity> allClients = clientRepository.findAll(Sort.by(Sort.Direction.ASC, "companyName"));
        return allClients.stream().map(ClientDto::new).toList();
    }

    @Override
    @Cacheable(value = "clients", key = "#clientId")
    public Optional<ClientDto> getClientById(UUID clientId) {
        return this.getAllClients().stream()
                .filter(client -> client.getId().equals(clientId))
                .findFirst();
    }

    @Override
    @Cacheable(value = "works")
    public List<WorkDto> getAllWorks() {
        List<WorkEntity> allWorks = workRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return allWorks.stream()
                .map(work -> new WorkDto(work, new ClientDto(work.getClient())))
                .toList();
    }

    @Override
    @Cacheable(value = "works", key = "#id")
    public Optional<WorkDto> getWorkById(UUID id) {
        return this.getAllWorks().stream()
                .filter(work -> work.getId().equals(id))
                .findFirst();
    }

    @Override
    @Cacheable(value = "wages")
    public List<WageDto> getAllWages() {
        List<WageEntity> allWages = wageRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
        return allWages.stream().map(WageDto::new).toList();
    }

    @Override
    @Cacheable(value = "workerHistory", key = "{#workerId, #year, #month}")
    public Optional<WorkerHistoryDto> getWorkerHistory(UUID workerId, Integer year, Integer month) {
        LocalDate date = LocalDate.of(year, month, 1);

        List<WorkerHistoryEntity> allWorkersHistory = workerHistoryRepository.findAll(Sort.by(Sort.Direction.ASC, "workerId"));
        return allWorkersHistory.stream()
                .map(WorkerHistoryDto::new)
                .filter(workerHistory -> workerHistory.getWorkerId().equals(workerId))
                .filter(workerHistory -> !workerHistory.getValidFrom().isAfter(date) &&
                        (workerHistory.getValidTo() == null || !workerHistory.getValidTo().isBefore(date))
                )
                .findFirst();
    }

    @Override
    @Cacheable(value = "invoices")
    public List<ClientInvoiceDto> getAllClientInvoices() {
        List<ClientInvoiceEntity> allClientInvoices = clientInvoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "docNumber"));
        return allClientInvoices.stream().map(ClientInvoiceDto::new).toList();
    }

    @Override
    @Cacheable(value = "invoices", key = "#invoiceId")
    public Optional<ClientInvoiceDto> getClientInvoiceById(UUID invoiceId) {
        return this.getAllClientInvoices().stream()
                .filter(invoice -> invoice.getId().equals(invoiceId))
                .findFirst();
    }

    @Override
    @Cacheable(value = "payments")
    public List<ClientPaymentDto> getAllClientPayments() {
        List<ClientPaymentInvoiceEntity> allPaymentInvoices = clientPaymentInvoiceRepository.findAll();
        Map<UUID, List<ClientPaymentInvoiceEntity>> paymentInvoicesByPaymentId =
                allPaymentInvoices.stream().collect(Collectors.groupingBy(paymentInvoice -> paymentInvoice.getPayment().getId()));

        List<ClientPaymentEntity> allClientPayments = clientPaymentRepository.findAll(Sort.by(Sort.Direction.DESC, "paymentDate"));
        return allClientPayments.stream().map(payment -> new ClientPaymentDto(payment,
                paymentInvoicesByPaymentId.getOrDefault(payment.getId(), List.of()))).toList();
    }

    @Override
    @Cacheable(value = "payments", key = "#paymentId")
    public Optional<ClientPaymentDto> getClientPaymentById(UUID paymentId) {
        return this.getAllClientPayments().stream()
                .filter(payment -> payment.getId().equals(paymentId))
                .findFirst();
    }
}
