package com.diameter.service;

import com.diameter.model.DiameterAudit;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.Map;

public interface DiameterAuditService {

    DiameterAudit createAudit(DiameterAudit audit) throws ValidationException;

    Map<String, Object> getAudits(Long id, String transactionId, String sessionId, String msisdn, String imsi, LocalDate createdAt, LocalDate fromDate, LocalDate toDate, int page, int size);

    DiameterAudit updateAudit(Long id, DiameterAudit audit) throws ValidationException;

    void deleteAudit(Long id) throws ValidationException;
}