package com.savbill.radius.services.impl;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.entity.QAcctCdr;
import com.savbill.radius.entity.QLiveUser;
import com.savbill.radius.repository.AcctCdrRepository;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.services.ResellerService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ResellerServiceImpl implements ResellerService {

    @Autowired
    private AcctCdrRepository acctCdrRepository;
    @Autowired
    private LiveUserRepository liveUserRepository;

    private static final Logger log = LoggerFactory.getLogger(AcctCdrServiceImpl.class);

    @Override
    public Page<AcctCdr> findAllAcctCdr(Integer mvnoId, PaginationDTO paginationDTO, Long locationId) {
        try {
            QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
            BooleanExpression exp = qAcctCdr.isNotNull();

            if(locationId != null)
                exp = exp.and(qAcctCdr.locationId.eq(locationId));
            else
                exp = exp.and(qAcctCdr.locationId.isNull())
                         .or(qAcctCdr.locationId.isNotNull());

            if (mvnoId != null && mvnoId != 1) {
                exp = exp.and(qAcctCdr.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            } else if(mvnoId == 1 && paginationDTO == null) {
                Page<AcctCdr> page = new PageImpl<AcctCdr>(acctCdrRepository.findAll());
                return page;
            }

            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
            if(!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null")))
            {
                exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
                        .or(qAcctCdr.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
            }
            if(!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
            {
                exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
                        .or(qAcctCdr.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
            }
            Page<AcctCdr> page = acctCdrRepository.findAll(exp, pageable);
            return page;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAcctCdrById(Long id, Integer mvnoId, Long locationId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            getAcctCdrById(id, mvnoId, locationId);
            acctCdrRepository.deleteById(id);
            //log.info("AcctCdr deleted succefully: "+id);
        } catch (RuntimeException e) {
         //   log.error("Error while deleting AcctCdr: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public Page<AcctCdr> findAcctCrdByUserName(String userName, String framedIp, Integer mvnoId, PaginationDTO paginationDTO, Long locationId) {
        try {
            QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
            BooleanExpression exp = qAcctCdr.isNotNull();

            if(locationId != null)
                exp = exp.and(qAcctCdr.locationId.eq(locationId));
            else
                exp = exp.and(qAcctCdr.locationId.isNull())
                        .or(qAcctCdr.locationId.isNotNull());

            if(userName == null && framedIp == null && paginationDTO.getFromDate() == null && paginationDTO.getToDate() == null
                    && paginationDTO.getPage() == 0 && paginationDTO.getSize() == 0) {
                Page<AcctCdr> page;
                if(mvnoId == 1)
                    page = new PageImpl<>(acctCdrRepository.findAll());
                else {
                    exp = exp.and(qAcctCdr.mvnoId.in(mvnoId, 1));
                    page = new PageImpl<>((List<AcctCdr>) acctCdrRepository.findAll(exp));
                }
                return page;
            }

            if(!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null")))
            {
                exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
                        .or(qAcctCdr.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
            }
            if(!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
            {
                exp=exp.and(qAcctCdr.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
                        .or(qAcctCdr.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
            }
            if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
                exp = exp.and(qAcctCdr.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));

            if(StringUtils.isBlank(framedIp) || framedIp.equalsIgnoreCase("null"))
            {
                framedIp = "";
            }
            else {
                exp=exp.and(qAcctCdr.framedIpAddress.contains(framedIp));
            }
            if(StringUtils.isBlank(userName) || userName.equalsIgnoreCase("null"))
            {
                userName="";
            }else {
                exp=exp.and(qAcctCdr.userName.contains(userName));
            }
            Predicate builder = exp;
            if(paginationDTO.getSize() < 1) {
                Page<AcctCdr> page = new PageImpl<AcctCdr>((List<AcctCdr>) acctCdrRepository.findAll(builder));
                return page;
            }

            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
            return  acctCdrRepository.findAll(builder, pageable);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public AcctCdr findAcctCdrById(Long cdrId, Integer mvnoId, Long locationId) {
        try {
            QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
            BooleanExpression exp = qAcctCdr.isNotNull();

            if(locationId != null)
                exp = exp.and(qAcctCdr.locationId.eq(locationId));

            if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
                exp = exp.and(qAcctCdr.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            exp = exp.and(qAcctCdr.cdrId.eq(cdrId));
            Optional<AcctCdr> acctCdr = acctCdrRepository.findOne(exp);

            if (!acctCdr.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with AcctCdr id " + cdrId + " . Please enter valid AcctCdr id.");
            }

            return acctCdr.get();

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void getAcctCdrById(Long id, Integer mvnoId, Long locationId) {
        try {
            QAcctCdr qAcctCdr = QAcctCdr.acctCdr;
            BooleanExpression exp = qAcctCdr.isNotNull();

            if(locationId != null)
                exp = exp.and(qAcctCdr.locationId.eq(locationId));
            else
                exp = exp.and(qAcctCdr.locationId.isNull())
                        .or(qAcctCdr.locationId.isNotNull());

            if(ValidateCrudTransactionData.validateMvnoId(mvnoId) != 1)
                exp = exp.and(qAcctCdr.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            exp = exp.and(qAcctCdr.cdrId.eq(id));
            Optional<AcctCdr> acctCdr = acctCdrRepository.findOne(exp);

            if (!acctCdr.isPresent()) {
                throw new IllegalArgumentException(
                        "AcctCdr record not found with id " + id + " or You do not have access to delete or update this record.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<LiveUser> getAll(Integer mvnoId, PaginationDTO paginationDTO, Long locationId) {
        QLiveUser qLiveUser = QLiveUser.liveUser;
        BooleanExpression boolExp = qLiveUser.isNotNull();

        if(locationId != null)
            boolExp = boolExp.and(qLiveUser.locationId.eq(locationId));
        else
            boolExp = boolExp.and(qLiveUser.locationId.isNull())
                    .or(qLiveUser.locationId.isNotNull());

        if (mvnoId != null && mvnoId != 1) {
            boolExp = boolExp.and(qLiveUser.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
        } else if(mvnoId == 1 && paginationDTO == null) {
            Page<LiveUser> page = new PageImpl<LiveUser>(liveUserRepository.findAll());
            return page;
        }
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
        if(!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null")))
        {
            boolExp=boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
                    .or(qLiveUser.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
        }
        if(!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
        {
            boolExp=boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
                    .or(qLiveUser.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
        }
        Page<LiveUser> page = liveUserRepository.findAll(boolExp, pageable);
        return page;
    }

    @Override
    public void delete(Long id, Integer mvnoId, Long locationId) {
        Optional.ofNullable(id).map(longId -> {
            LiveUser liveUser = validateLiveUserForUpdateAndDelete(id, ValidateCrudTransactionData.validateMvnoId(mvnoId), locationId);
            liveUserRepository.deleteById(id);
            MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
            log.info("Live user deleted succefully: "+liveUser.getUserName());
            return true;
        }).orElseThrow(() -> new IllegalArgumentException("Profile not found for Id " + id));
    }

    @Override
    public LiveUser findLiveUserById(Long id, Integer mvnoId, Long locationId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid live user id.");
            QLiveUser qLiveUser = QLiveUser.liveUser;
            BooleanExpression boolExp = qLiveUser.isNotNull();

            if(locationId != null)
                boolExp = boolExp.and(qLiveUser.locationId.eq(locationId));

            if(mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLiveUser.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qLiveUser.cdrID.eq(id));

            Optional<LiveUser> liveUser = liveUserRepository.findOne(boolExp);
            if (!liveUser.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with live user id " + id + " . Please enter valid live user id.");
            }
            return liveUser.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private LiveUser validateLiveUserForUpdateAndDelete(Long id, Integer mvnoId, Long locationId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid live user id.");
            QLiveUser qLiveUser = QLiveUser.liveUser;
            BooleanExpression boolExp = qLiveUser.isNotNull();

            if(locationId != null)
                boolExp = boolExp.and(qLiveUser.locationId.eq(locationId));
            else
                boolExp = boolExp.and(qLiveUser.locationId.isNull())
                                 .or(qLiveUser.locationId.isNotNull());

            if(mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLiveUser.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qLiveUser.cdrID.eq(id));

            Optional<LiveUser> liveUser = liveUserRepository.findOne(boolExp);
            if (!liveUser.isPresent()) {
                throw new RuntimeException("Reseller with id '"+id+"' do not exist or You do not have access to update or delete this record.");
            }
            return liveUser.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<LiveUser> findByUserName(String userName,String framedIpAddress, Integer mvnoId, PaginationDTO paginationDTO, Long locationId) {
        if(StringUtils.isBlank(framedIpAddress) || framedIpAddress.equalsIgnoreCase("null"))
            framedIpAddress = "";
        if(StringUtils.isBlank(userName) || userName.equalsIgnoreCase("null"))
            userName="";

        QLiveUser qLiveUser = QLiveUser.liveUser;
        BooleanExpression boolExp = qLiveUser.isNotNull();

        if(locationId != null)
            boolExp = boolExp.and(qLiveUser.locationId.eq(locationId));
        else
            boolExp = boolExp.and(qLiveUser.locationId.isNull())
                             .or(qLiveUser.locationId.isNotNull());

        if (!userName.isEmpty() && !framedIpAddress.isEmpty())
            boolExp = boolExp.and(qLiveUser.userName.like("%"+ userName +"%")).and(qLiveUser.framedIpAddress.like("%"+ framedIpAddress +"%"));
        else if (!userName.isEmpty())
            boolExp = boolExp.and(qLiveUser.userName.like("%"+ userName +"%"));
        else if(!framedIpAddress.isEmpty())
            boolExp = boolExp.and(qLiveUser.framedIpAddress.like("%"+ framedIpAddress +"%"));

        if(mvnoId == null || mvnoId != 1)
            boolExp = boolExp.and(qLiveUser.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
        if(paginationDTO == null) {
            return new PageImpl<LiveUser>((List<LiveUser>) liveUserRepository.findAll(boolExp));
        }
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
        if(!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null")))
        {
            boolExp=boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))
                    .or(qLiveUser.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
        }
        if(!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null")))
        {
            boolExp=boolExp.and(qLiveUser.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))
                    .or(qLiveUser.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
        }
        Page<LiveUser> page = liveUserRepository.findAll(boolExp, pageable);
        return page;
    }
}
