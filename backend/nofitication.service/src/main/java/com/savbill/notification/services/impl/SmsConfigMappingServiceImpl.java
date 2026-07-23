package com.savbill.notification.services.impl;

import com.savbill.notification.entity.QSmsConfigMapping;
import com.savbill.notification.entity.SmsConfigMapping;
import com.savbill.notification.helper.SmsConfigMappingDto;
import com.savbill.notification.repository.SmsConfigMappingRepository;
import com.savbill.notification.services.SmsConfigMappingService;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import com.savbill.notification.utils.UpdateDiffFinder;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SmsConfigMappingServiceImpl implements SmsConfigMappingService {
    private static Logger log = Logger.getLogger(SmsConfigMappingServiceImpl.class);
    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    UpdateDiffFinder updateDiffFinder;
    @Autowired
    private SmsConfigMappingRepository smsConfigMappingRepository;

    @Override
    public List<SmsConfigMapping> findSmsConfigMappingBySmsConfigId(Long smsConfigId, Long mvnoId) {
        try {
            validateSmsConfigMappingBySmsConfigId(smsConfigId, mvnoId);
            if (mvnoId == 1) {
                return smsConfigMappingRepository.findBySmsConfigId(smsConfigId);
            } else {
                return smsConfigMappingRepository.findBySmsConfigIdAndMvnoId(smsConfigId, mvnoId);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateSmsConfigMappingBySmsConfigId(Long smsConfigId, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(smsConfigId)) {
                throw new IllegalArgumentException("Please enter valid SMS Config id.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<SmsConfigMapping> findAllSmsConfigMapping(Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else {
                if (mvnoId == 1) {
                    return smsConfigMappingRepository.findAll();
                } else {
                    QSmsConfigMapping qSmsConfigMapping = QSmsConfigMapping.smsConfigMapping;
                    BooleanExpression boolExp = qSmsConfigMapping.isNotNull();
                    boolExp = boolExp.and(qSmsConfigMapping.mvnoId.eq(mvnoId).or(qSmsConfigMapping.mvnoId.eq(1L)));
                    return (List<SmsConfigMapping>) smsConfigMappingRepository.findAll(boolExp);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteSmsConfigMappingById(Long id, Long mvnoId) {
        try {
            Optional<SmsConfigMapping> optionalSmsConfigMapping = validateSmsConfigMappingById(id, mvnoId);
            smsConfigMappingRepository.delete(optionalSmsConfigMapping.get());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Optional<SmsConfigMapping> validateSmsConfigMappingById(Long id, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
                throw new IllegalArgumentException("Please enter valid SMS Config Mapping id.");
            }
            Optional<SmsConfigMapping> optionalSmsConfigMapping = smsConfigMappingRepository
                    .findBySmsConfigMappingIdAndMvnoId(id, mvnoId);

            if (!optionalSmsConfigMapping.isPresent()) {
                throw new IllegalArgumentException("No record found for SMS Config Mapping with id : '" + id
                        + "'. Please enter valid SMS Config Mapping id.");
            }
            return optionalSmsConfigMapping;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<SmsConfigMapping> saveSmsConfigMapping(List<SmsConfigMappingDto> smsConfigMappingDtoList, Long mvnoId) {
        try {
            if (smsConfigMappingDtoList == null || smsConfigMappingDtoList.isEmpty()) {
                throw new IllegalArgumentException("Please add SMS Parameter");
            }
            List<SmsConfigMapping> smsConfigMappingList = new ArrayList<>();
            for (SmsConfigMappingDto smsConfigMappingDto : smsConfigMappingDtoList) {
                SmsConfigMapping smsConfigMapping = new SmsConfigMapping(smsConfigMappingDto, mvnoId);
                smsConfigMapping.setSmsConfigId(smsConfigMappingDto.getSmsConfigId());
                //validateSmsConfigMappingData(smsConfigMapping, mvnoId);
                smsConfigMapping.setCreatedOn(new Timestamp(new Date().getTime()));
                smsConfigMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
                // smsConfigMappingRepository.save(smsConfigMapping);
                smsConfigMappingList.add(smsConfigMapping);
            }
            smsConfigMappingRepository.saveAll(smsConfigMappingList);
            return smsConfigMappingRepository
                    .findBySmsConfigIdAndMvnoId(smsConfigMappingDtoList.get(0).getSmsConfigId(), mvnoId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateSmsConfigMappingData(SmsConfigMapping smsConfigMapping, Long mvnoId) {
        if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
            throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
        } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsConfigMapping.getParameter())) {
            throw new IllegalArgumentException(
                    "SMS Config Parameter is mandatory. Please enter valid SMS Config Parameter.");
        } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(smsConfigMapping.getSmsConfigId())) {
            throw new IllegalArgumentException("SMS Config Id is mandatory. Please enter valid SMS Config Id.");
        } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsConfigMapping.getValue())) {
            throw new IllegalArgumentException(
                    "SMS Config Parameter value is mandatory. Please enter valid SMS Config Parameter value.");
        }
    }

    @Override
    @Transactional
    public List<SmsConfigMapping> updateSmsConfigMapping(List<SmsConfigMappingDto> smsConfigMappingDtoList, Long mvnoId,
                                                         Long smsConfigId, HttpServletRequest request) {
        try {
            if (smsConfigMappingDtoList == null || smsConfigMappingDtoList.isEmpty()) {
                throw new IllegalArgumentException("Please add SMS Parameter");
            }
            List<SmsConfigMapping> findAllMappingListold = null;
            String updated = null;
            List<SmsConfigMapping> findAllMappingList = new ArrayList<>();
            if (mvnoId == 1) {
                findAllMappingList = smsConfigMappingRepository.findBySmsConfigId(smsConfigId);
            } else {
                findAllMappingList = smsConfigMappingRepository.findBySmsConfigIdAndMvnoId(smsConfigId, mvnoId);
            }

            findAllMappingListold = findAllMappingList;
            deleteSmsConfigMappings(findAllMappingList);
            for (int i = 0; i < smsConfigMappingDtoList.size(); i++) {
                if (!findAllMappingListold.isEmpty()) {
                    smsConfigMappingDtoList.get(i).setSmsConfigId(smsConfigId);
                    updated = updateDiffFinder.getUpdatedDiff(new SmsConfigMapping(smsConfigMappingDtoList.get(i), mvnoId), findAllMappingListold.get(0));
                }else{
                    smsConfigMappingDtoList.get(i).setSmsConfigId(smsConfigId);
                }
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Notification config details Updated successfully, " + updated + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return smsConfigMappingRepository.saveAll(saveSmsConfigMappings(smsConfigMappingDtoList, mvnoId));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SmsConfigMapping> saveSmsConfigMappings(List<SmsConfigMappingDto> smsConfigMappingDtoList, Long mvnoId) {
        List<SmsConfigMapping> smsConfigMappingList = new ArrayList<>();
        for (SmsConfigMappingDto smsConfigMappingDto : smsConfigMappingDtoList) {
            SmsConfigMapping smsConfigMapping = new SmsConfigMapping(smsConfigMappingDto, mvnoId);
            validateSmsConfigMappingData(smsConfigMapping, mvnoId);
            smsConfigMapping.setCreatedOn(new Timestamp(new Date().getTime()));
            smsConfigMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
            // smsConfigMappingRepository.save(smsConfigMapping);
            smsConfigMappingList.add(smsConfigMapping);
        }
        return smsConfigMappingList;
    }

    private void deleteSmsConfigMappings(List<SmsConfigMapping> findAllMappingList) {
        if (!findAllMappingList.isEmpty()) {
            smsConfigMappingRepository.deleteAll(findAllMappingList);
        }
    }

    private List<SmsConfigMapping> getChangedSmsConfigMapping(List<SmsConfigMapping> dbSmsConfigMappingList,
                                                              List<SmsConfigMapping> SmsConfigMappingList, Long mvnoId, Long smsConfigId) {
        List<SmsConfigMapping> smsConfigMappings = SmsConfigMappingList.stream()
                .filter(smsConfigMapping -> dbSmsConfigMappingList.stream().noneMatch(
                        dbSmsConfigMapping -> dbSmsConfigMapping.getParameter().equals(smsConfigMapping.getParameter())
                                && smsConfigMapping.getValue().equals(dbSmsConfigMapping.getValue())))
                .collect(Collectors.toList());
        List<SmsConfigMapping> saveAttributes = smsConfigMappings.stream()
                .filter(smsConfigMapping -> SmsConfigMappingList.stream()
                        .noneMatch(dbSmsConfigMapping -> smsConfigMapping.getSmsConfigId() == null ? false
                                : smsConfigMapping.getSmsConfigId().equals(dbSmsConfigMapping.getSmsConfigId())
                                && smsConfigMapping.getSmsConfigMappingId() == null
                                ? false
                                : smsConfigMapping.getSmsConfigMappingId()
                                .equals(dbSmsConfigMapping.getSmsConfigMappingId())))
                .collect(Collectors.toList());
        for (SmsConfigMapping smsConfigMapping : saveAttributes) {
            smsConfigMapping.setCreatedOn(new Timestamp(new Date().getTime()));
            smsConfigMapping.setLastModifiedOn(new Timestamp(new Date().getTime()));
            smsConfigMapping.setSmsConfigId(smsConfigId);
            validateSmsConfigMappingData(smsConfigMapping, mvnoId);
            smsConfigMapping.setMvnoId(mvnoId);
            smsConfigMappingRepository.save(smsConfigMapping);
        }
        smsConfigMappings.removeAll(saveAttributes);
        return smsConfigMappings;
    }
}
