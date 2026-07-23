package com.savbill.radius.services.impl;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.entity.AuthResponse;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.repository.AcctCdrRepository;
import com.savbill.radius.repository.AuthResponseRepository;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.services.DashBoardService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashBoardService {


    private static final Log log = LogFactory.getLog(DashboardServiceImpl.class);

    @Autowired
    AcctCdrRepository acctCdrRepository;

    @Autowired
    AuthResponseRepository authResponseRepository;

    @Autowired
    private LiveUserRepository liveUserRepository;

    @Override
    public Map<String, Integer> getDailyConsumeDataOfLastSevenDays(Integer mvnoId) throws ParseException {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
        BooleanExpression booleanExpression = qAcctCdr.isNotNull();
        if (mvnoId == 1) {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime())));
        } else {

            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime()))).and(qAcctCdr.mvnoId.in(mvnoId, 1));
        }
        List<AcctCdr> acctCdrList = (List<AcctCdr>) acctCdrRepository.findAll(booleanExpression);
        Map<String, Integer> map = new HashMap<>();
        acctCdrList.forEach(acctCdr -> {
            Date date = new Date(acctCdr.getCreatedDate().getTime());
            String key = sdf.format(date);
            if (map.containsKey(key)) {
                int current = map.get(key);
                if (Objects.nonNull(map.get(key))) {
                    map.put(key, current + Integer.parseInt(acctCdr.getAcctInputOctets()) + Integer.parseInt(acctCdr.getAcctOutputOctets()));
                }
            } else {
                if (acctCdr.getAcctInputOctets() != null && acctCdr.getAcctOutputOctets() != null)
                    map.put(key, Integer.parseInt(acctCdr.getAcctInputOctets()) + Integer.parseInt(acctCdr.getAcctOutputOctets()));
            }
        });
        return map;
    }

    public Long connectedUser(Integer mvnoId) {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        log.info("getting all connected user");
        QLiveUser qLiveUser = QLiveUser.liveUser;
        BooleanExpression boolExp = qLiveUser.isNotNull();
        ArrayList<LiveUser> liveUsers;
        if (mvnoId == 1) {
            liveUsers = (ArrayList<LiveUser>) liveUserRepository.findAll(boolExp);
            return (long) liveUsers.size();
        } else {
            boolExp = boolExp.and(qLiveUser.mvnoId.in(mvnoId, 1));
            liveUsers = (ArrayList<LiveUser>) liveUserRepository.findAll(boolExp);
            return (long) liveUsers.size();
        }
    }

    @Override
    public Map<String, Integer> getAvgSessionTimeByDate(Integer mvnoId) throws ParseException {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
        BooleanExpression booleanExpression = qAcctCdr.isNotNull();
        if (mvnoId == 1) {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime())));
        } else {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime()))).and(qAcctCdr.mvnoId.in(mvnoId, 1));
        }
        List<AcctCdr> acctCdrList = (List<AcctCdr>) acctCdrRepository.findAll(booleanExpression);
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> counterMap = new HashMap<>();
        acctCdrList.forEach(acctCdr -> {
            Date date = new Date(acctCdr.getCreatedDate().getTime());
            String key = sdf.format(date);
            if (map.containsKey(key)) {
                int current = map.get(key);
                if (Objects.nonNull(map.get(key))) {
                    map.put(key, current + Integer.parseInt(acctCdr.getAcctSessionTime()));
                    counterMap.put(key, +1);
                }
            } else {
                if (Objects.nonNull(acctCdr.getAcctSessionTime())) {
                    map.put(key, Integer.parseInt(acctCdr.getAcctSessionTime()));
                    counterMap.put(key, 0);
                }
            }

        });

        counterMap.forEach((key, value) -> {
            if (value > 0) {
                if (map.containsKey(key)) {
                    int avgSession = (map.get(key)) / value;
                    map.put(key, avgSession / 60);
                }

            }
        });
        return map;
    }

    @Override
    public Map<String, Integer> getAuthFailureData(Integer mvnoId) throws ParseException {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        QAuthResponse qAuthResponse = QAuthResponse.authResponse;
        BooleanExpression booleanExpression = qAuthResponse.isNotNull();
        if (mvnoId == 1) {
            booleanExpression = booleanExpression.and(qAuthResponse.eventTime.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime())));
        } else {
            booleanExpression = booleanExpression.and(qAuthResponse.eventTime.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime()))).and(qAuthResponse.mvnoId.in(mvnoId, 1));
        }
        List<AuthResponse> authResponseList = (List<AuthResponse>) authResponseRepository.findAll(booleanExpression);
        Map<String, Integer> map = new HashMap<>();
        authResponseList.forEach(authResponse -> {
            String key = authResponse.getReplyMessage();
            if (map.containsKey(key)) {
                int current = map.get(key);
                if (Objects.nonNull(map.get(key))) {
                    map.put(key, ++current);
                }
            } else {
                if (authResponse.getReplyMessage() != null) map.put(key, 1);
            }
        });
        return map;
    }


    @Override
    public Map<String, Integer> getDailyConsumeDataOfLastSevenDaysForPWSC(Integer mvnoId, Long locationId) throws ParseException {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
        BooleanExpression booleanExpression = qAcctCdr.isNotNull();
        if (mvnoId == 1) {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime())).and(qAcctCdr.locationId.eq(locationId)));

        } else {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime()))).and(qAcctCdr.mvnoId.eq(mvnoId).and(qAcctCdr.locationId.eq(locationId)));
        }
        List<AcctCdr> acctCdrList = (List<AcctCdr>) acctCdrRepository.findAll(booleanExpression);
        Map<String, Integer> map = new HashMap<>();
        acctCdrList.forEach(acctCdr -> {
            Date date = new Date(acctCdr.getCreatedDate().getTime());
            String key = sdf.format(date);
            if (map.containsKey(key)) {
                int current = map.get(key);
                if (Objects.nonNull(map.get(key))) {
                    map.put(key, current + Integer.parseInt(acctCdr.getAcctInputOctets()) + Integer.parseInt(acctCdr.getAcctOutputOctets()));
                }
            } else {
                if (acctCdr.getAcctInputOctets() != null && acctCdr.getAcctOutputOctets() != null)
                    map.put(key, Integer.parseInt(acctCdr.getAcctInputOctets()) + Integer.parseInt(acctCdr.getAcctOutputOctets()));
            }
        });
        return map;
    }

    @Override
    public Long connectedUserForPWSC(Integer mvnoId, Long locationId) {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        QLiveUser qLiveUser = QLiveUser.liveUser;
        BooleanExpression boolExp = qLiveUser.isNotNull();
        ArrayList<LiveUser> liveUsers;
        if (mvnoId == 1) {
            liveUsers = (ArrayList<LiveUser>) liveUserRepository.findAll(boolExp);
            return (long) liveUsers.size();
        } else {
            boolExp = boolExp.and(qLiveUser.mvnoId.in(mvnoId, 1)).and(qLiveUser.locationId.eq(locationId));
            liveUsers = (ArrayList<LiveUser>) liveUserRepository.findAll(boolExp);
            return (long) liveUsers.size();
        }
    }

    @Override
    public Map<String, Integer> getAvgSessionTimeByDateForPWSC(Integer mvnoId, Long locationId) throws ParseException {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
        BooleanExpression booleanExpression = qAcctCdr.isNotNull();
        if (mvnoId == 1) {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime())).and(qAcctCdr.locationId.eq(locationId)));
        } else {
            booleanExpression = booleanExpression.and(qAcctCdr.createdDate.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime()))).and(qAcctCdr.mvnoId.eq(mvnoId).and(qAcctCdr.locationId.eq(locationId)));
        }
        List<AcctCdr> acctCdrList = (List<AcctCdr>) acctCdrRepository.findAll(booleanExpression);
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> counterMap = new HashMap<>();
        acctCdrList.forEach(acctCdr -> {
            Date date = new Date(acctCdr.getCreatedDate().getTime());
            String key = sdf.format(date);
            if (map.containsKey(key)) {
                int current = map.get(key);
                if (Objects.nonNull(map.get(key))) {
                    map.put(key, current + Integer.parseInt(acctCdr.getAcctSessionTime()));
                    counterMap.put(key, +1);
                }
            } else {
                if (Objects.nonNull(acctCdr.getAcctSessionTime())) {
                    map.put(key, Integer.parseInt(acctCdr.getAcctSessionTime()));
                    counterMap.put(key, 0);
                }
            }

        });

        counterMap.forEach((key, value) -> {
            if (value > 0) {
                if (map.containsKey(key)) {
                    int avgSession = (map.get(key)) / value;
                    map.put(key, avgSession / 60);
                }

            }
        });
        return map;
    }

    @Override
    public Map<String, Integer> getAuthFailureDataForPWSC(Integer mvnoId, Long locationId) throws ParseException {
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        QAuthResponse qAuthResponse = QAuthResponse.authResponse;
        BooleanExpression booleanExpression = qAuthResponse.isNotNull();
        if (mvnoId == 1) {
            booleanExpression = booleanExpression.and(qAuthResponse.eventTime.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime())));

        } else {
            booleanExpression = booleanExpression.and(qAuthResponse.eventTime.between(new java.sql.Timestamp(toDate.getTime()), new java.sql.Timestamp(new Date().getTime()))).and(qAuthResponse.mvnoId.eq(mvnoId));
        }
        List<AuthResponse> authResponseList = (List<AuthResponse>) authResponseRepository.findAll(booleanExpression);
        Map<String, Integer> map = new HashMap<>();
        authResponseList.forEach(authResponse -> {
            String key = authResponse.getReplyMessage();
            if (map.containsKey(key)) {
                int current = map.get(key);
                if (Objects.nonNull(map.get(key))) {
                    map.put(key, ++current);
                }
            } else {
                if (authResponse.getReplyMessage() != null) map.put(key, 1);
            }
        });
        return map;
    }
}
