package com.diameter.service;

import com.diameter.model.DiameterSessionCdr;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.Map;

public interface DiameterSessionCdrService {

    DiameterSessionCdr createCdr(DiameterSessionCdr cdr) throws ValidationException;

    Map<String, Object> getCdr(Long id, String sessionId, String transactionId, String msisdn, String imsi, String status, String serviceType, LocalDate createdDate, LocalDate fromDate, LocalDate toDate, int page, int size);

    DiameterSessionCdr updateCdr(Long id, DiameterSessionCdr cdr) throws ValidationException;

    void deleteCdr(Long id) throws ValidationException;

}