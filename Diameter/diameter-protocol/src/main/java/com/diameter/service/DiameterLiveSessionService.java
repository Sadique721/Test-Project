package com.diameter.service;

import com.diameter.model.DiameterLiveSession;
import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.Map;

public interface DiameterLiveSessionService {

    DiameterLiveSession createSession(DiameterLiveSession session) throws ValidationException;

    DiameterLiveSession updateSession(String sessionId, DiameterLiveSession session) throws ValidationException;

    void deleteSession(String sessionId) throws ValidationException;

    Map<String, Object> getSessions(String sessionId, String transactionId, String msisdn, String imsi, String status, LocalDate createdAt, LocalDate fromDate, LocalDate toDate, int page, int size);
    
    void process(DiameterLiveSession dto);
}