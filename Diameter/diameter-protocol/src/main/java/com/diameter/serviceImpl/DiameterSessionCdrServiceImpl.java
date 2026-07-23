package com.diameter.serviceImpl;

import java.time.LocalDate;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.springframework.stereotype.Service;

import com.diameter.model.DiameterSessionCdr;
import com.diameter.repository.DiameterSessionCdrRepository;
import com.diameter.service.DiameterSessionCdrService;

@Service
public class DiameterSessionCdrServiceImpl implements DiameterSessionCdrService {

    private final DiameterSessionCdrRepository repository;

    public DiameterSessionCdrServiceImpl(DiameterSessionCdrRepository repository) {this.repository = repository;}

    @Override
    public DiameterSessionCdr createCdr(DiameterSessionCdr cdr) throws ValidationException {
        repository.saveCdr(cdr);
        return cdr;
    }

    @Override
    public Map<String, Object> getCdr(
            Long id,
            String sessionId,
            String transactionId,
            String msisdn,
            String imsi,
            String status,
            String serviceType,
            LocalDate createdDate,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        if (id != null) {
            return repository.getByIdPaginated(id, page, size);
        }

        else if (sessionId != null) {
            return repository.getBySessionIdPaginated(sessionId, page, size);
        }

        else if (transactionId != null) {
            return repository.getByTransactionIdPaginated(transactionId, page, size);
        }

        else if (msisdn != null) {
            return repository.getByMsisdnPaginated(msisdn, page, size);
        }

        else if (imsi != null) {
            return repository.getByImsiPaginated(imsi, page, size);
        }

        else if (status != null) {
            return repository.getByStatusPaginated(status, page, size);
        }

        else if (serviceType != null && !serviceType.isBlank()) {
            return repository.getByServiceTypePaginated(serviceType, page, size);
        }

        else if (createdDate != null) {
            return repository.getByDatePaginated(createdDate, page, size);
        }

        else if (fromDate != null && toDate != null) {
            return repository.getByDateRangePaginated(fromDate, toDate, page, size);
        }

        return repository.getAllCdrPaginated(page, size);
    }


    @Override
    public DiameterSessionCdr updateCdr(Long id, DiameterSessionCdr cdr) throws ValidationException {

        repository.updateCdr(id, cdr);

        return repository.getCdrById(id);
    }

    @Override
    public void deleteCdr(Long id) throws ValidationException {

        repository.deleteCdr(id);
    }

}