package com.diameter.serviceImpl;

import com.diameter.model.DiameterAudit;
import com.diameter.repository.DiameterAuditRepository;
import com.diameter.service.DiameterAuditService;
import org.springframework.stereotype.Service;
import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.Map;

@Service
public class DiameterAuditServiceImpl implements DiameterAuditService {

    private final DiameterAuditRepository repository;

    public DiameterAuditServiceImpl(DiameterAuditRepository repository) {
        this.repository = repository;
    }

    @Override
    public DiameterAudit createAudit(DiameterAudit audit) throws ValidationException {
    	Long generatedId = repository.saveAudit(audit);
        repository.saveAuditDetailInformation(generatedId, audit);
        return audit;
    }


    @Override
    public Map<String, Object> getAudits(
            Long id,
            String transactionId,
            String sessionId,
            String msisdn,
            String imsi,
            LocalDate createdAt,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        if (id != null) {
            return repository.getByIdPaginated(id, page, size);
        }

        else if (transactionId != null) {
            return repository.getByTxnPaginated(transactionId, page, size);
        }

        else if (sessionId != null) {
            return repository.getBySessionIdPaginated(sessionId, page, size);
        }

        else if (msisdn != null) {
            return repository.getByMsisdnPaginated(msisdn, page, size);
        }

        else if (imsi != null) {
            return repository.getByImsiPaginated(imsi, page, size);
        }

        else if (createdAt != null) {
            return repository.getByDatePaginated(createdAt, page, size);
        }

        else if (fromDate != null && toDate != null) {
            return repository.getByDateRangePaginated(
                    fromDate,
                    toDate,
                    page,
                    size);
        }

        return repository.getAllAuditPaginated(page, size);
    }


    @Override
    public DiameterAudit updateAudit(Long id, DiameterAudit audit) throws ValidationException {

        repository.updateAudit(id, audit);

        return repository.getAuditById(id);
    }


    @Override
    public void deleteAudit(Long id) throws ValidationException {

        repository.deleteAudit(id);
    }

}