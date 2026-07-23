package com.savbill.notification.services.impl;

import com.savbill.notification.entity.QSmsConfig;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.helper.GenericSearchModel;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.SearchSmsRespDto;
import com.savbill.notification.repository.SmsConfigRepository;
import com.savbill.notification.services.SmsConfigService;
import com.savbill.notification.utils.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.notification.utils.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SmsConfigServiceImpl implements SmsConfigService {
    private static Logger log = Logger.getLogger(SmsConfigService.class);
    @Autowired
    SmsConfigRepository smsConfigRepository;
    @Autowired
    UpdateDiffFinder updateDiffFinder;
    @Autowired
    TokenDataExtractor tokenDataExtractor;

    /**
     * Method:- Update SMS Configuration
     *
     * @param smsConfig
     * @param mvnoId
     * @param request
     * @return
     */
    @Override
    public SmsConfig updateSmsConfig(SmsConfig smsConfig, Long mvnoId, HttpServletRequest request) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsConfig.getLastModifiedBy()))
//				System.out.println("LastModifiedBy value is Missing");

                if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                    throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
                } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(smsConfig.getSmsConfigId())) {
                    throw new IllegalArgumentException("Sms config id is mandatory. Please enter valid sms config id");
                } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsConfig.getSmsUrl())) {
                    throw new IllegalArgumentException("Sms url is mandatory. Please enter valid sms url");
                }
            SmsConfig optionalSmsConfig = null;
            if (mvnoId == 1) {
                optionalSmsConfig = smsConfigRepository.findBySmsConfigId(smsConfig.getSmsConfigId()).orElse(null);
            } else {
                optionalSmsConfig = smsConfigRepository.findBySmsConfigIdAndMvnoId(smsConfig.getSmsConfigId(), mvnoId).orElse(null);
            }
            if (Objects.isNull(optionalSmsConfig)) {
                throw new IllegalArgumentException("No record found to update the sms configuration.");
            }
            List<SmsConfig> smsConfigList = smsConfigRepository.findAllBySmsUrlContainingIgnoreCaseAndMvnoId(smsConfig.getSmsUrl(), mvnoId);
            if (!smsConfigList.isEmpty() && smsConfigList.stream().anyMatch(smsConfigure -> !smsConfigure.getSmsConfigId().equals(smsConfig.getSmsConfigId()))) {
                throw new RuntimeException("SMS Url is already exist!");
            }
            SmsConfig addSmsConfig = new SmsConfig();
            addSmsConfig = smsConfig;
            if (mvnoId == 1) {
                addSmsConfig.setMvnoId(optionalSmsConfig.getMvnoId());
            }
            addSmsConfig.setMvnoId(mvnoId);
            addSmsConfig.setLastModifiedDate(LocalDateTime.now());
            addSmsConfig.setLastModifiedBy(smsConfig.getLastModifiedBy());
            addSmsConfig.setCreateDate(optionalSmsConfig.getCreateDate());
            addSmsConfig.setConfigStatus(smsConfig.getConfigStatus());
            addSmsConfig.setCreatedBy(optionalSmsConfig.getCreatedBy());
            if (smsConfig.getServiceType() == null || "".equals(smsConfig.getServiceType())) {
                addSmsConfig.setServiceType(CommonConstants.SERVICE_TYPE_BSS);
            } else {
                addSmsConfig.setServiceType(CommonConstants.SERVICE_TYPE_IWF);
            }
            String updatedValue = updateDiffFinder.getUpdatedDiff(optionalSmsConfig, addSmsConfig);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS Configuration updated successfully, with : " + updatedValue + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return smsConfigRepository.save(addSmsConfig);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method: Find All SMS Configuration Without Pagination
     *
     * @param mvnoId
     * @param buId
     * @param serviceType
     * @return
     */
    @Override
    public List<SmsConfig> findAllSmsConfig(Long mvnoId, Long buId, String serviceType) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else {
                QSmsConfig qSmsConfig = QSmsConfig.smsConfig;
                BooleanExpression boolExp = qSmsConfig.isNotNull();
                if (mvnoId == 1) {
                    if (serviceType == null || CommonConstants.SERVICE_TYPE_BSS.equals(serviceType)) {
                        boolExp = boolExp.and(qSmsConfig.serviceType.eq(CommonConstants.SERVICE_TYPE_BSS));
                    } else {
                        boolExp = boolExp.and(qSmsConfig.serviceType.eq(CommonConstants.SERVICE_TYPE_IWF));
                    }
                    return (List<SmsConfig>) smsConfigRepository.findAll(boolExp, Sort.by(Sort.Direction.DESC, "smsConfigId"));
                } else {
                    boolExp = boolExp.and(qSmsConfig.mvnoId.eq(mvnoId).or(qSmsConfig.mvnoId.eq(1L)));
                    if (serviceType == null || CommonConstants.SERVICE_TYPE_BSS.equals(serviceType)) {
                        boolExp = boolExp.and(qSmsConfig.serviceType.eq(CommonConstants.SERVICE_TYPE_BSS));
                    } else {
                        boolExp = boolExp.and(qSmsConfig.serviceType.eq(CommonConstants.SERVICE_TYPE_IWF));
                    }
                    if (Objects.nonNull(buId)) {
                        boolExp = boolExp.and(qSmsConfig.buId.eq(buId));
                    }
                    if (Objects.isNull(buId)) {
                        boolExp = boolExp.and(qSmsConfig.buId.isNull());
                    }
                    return (List<SmsConfig>) smsConfigRepository.findAll(boolExp, Sort.by(Sort.Direction.DESC, "smsConfigId"));
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Create SMS Configuration
     *
     * @param smsUrl
     * @param mvnoId
     * @param createdBy
     * @param buId
     * @param configStatus
     * @param serviceType
     * @return
     */
    @Override
    public SmsConfig addSmsConfig(String smsUrl, Long mvnoId, String createdBy, Long buId, Boolean configStatus, String serviceType) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(createdBy))
                throw new IllegalArgumentException("Created by name is missing");
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException("Mvno id is mandatory. Please enter valid mvno id");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(smsUrl)) {
                throw new IllegalArgumentException("Sms url is mandatory. Please enter valid sms url");
            } else {
                List<SmsConfig> smsConfigList = smsConfigRepository.findAllBySmsUrlContainingIgnoreCaseAndMvnoId(smsUrl, mvnoId);
                if (!smsConfigList.isEmpty()) {
                    throw new RuntimeException("SMS Url is already exist!");
                }
                SmsConfig smsConfigVo = new SmsConfig();
                smsConfigVo.setMvnoId(mvnoId);
                smsConfigVo.setSmsUrl(smsUrl);
                if (serviceType == null || "".equals(serviceType)) {
                    smsConfigVo.setServiceType(CommonConstants.SERVICE_TYPE_BSS);
                } else {
                    smsConfigVo.setServiceType(CommonConstants.SERVICE_TYPE_IWF);
                }
                smsConfigVo.setCreateDate(LocalDateTime.now());
                smsConfigVo.setCreatedBy(createdBy);
                if (Objects.nonNull(buId)) {
                    smsConfigVo.setBuId(buId);
                }
                smsConfigVo.setConfigStatus(configStatus);
                return smsConfigRepository.save(smsConfigVo);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method: Find SMS Configuration BY ID
     *
     * @param smsConfigId
     * @param mvnoId
     * @return
     */
    @Override
    public SmsConfig findSmsConfigById(Long smsConfigId, Long mvnoId) {
        try {
            QSmsConfig qSmsConfig = QSmsConfig.smsConfig;
            BooleanExpression boolExp = qSmsConfig.isNotNull();
            boolExp = boolExp.and(qSmsConfig.smsConfigId.eq(smsConfigId));
            if (mvnoId != 1)
                boolExp = boolExp.and(qSmsConfig.mvnoId.in(mvnoId, 1));
            Optional<SmsConfig> sms = smsConfigRepository.findOne(boolExp);
            if (sms.isPresent()) {
                return sms.get();
            } else {
                throw new IllegalArgumentException(
                        "No record found with sms config id " + smsConfigId + " . Please enter valid sms config id.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method:- Get SMS Configuration With Pagination
     *
     * @param page
     * @param size
     * @param mvnoId
     * @param buId
     * @param serviceType
     * @return
     */
    @Override
    public Page<SmsConfig> getSmsConfigWithPagination(Integer page, Integer size, Long mvnoId, Long buId, String serviceType) {
        page = page + 1;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("smsConfigId")));
        Page<SmsConfig> smsConfigs;
        if (mvnoId == 1) {
            smsConfigs = smsConfigRepository.findAllByServiceTypeContainingIgnoreCase(serviceType, pageable);
        } else {
            if (buId == null || buId == 0) {
                smsConfigs = smsConfigRepository.findAllByServiceTypeContainingIgnoreCaseAndMvnoIdIn(serviceType, Arrays.asList(mvnoId, 1L), pageable);
            } else {
                smsConfigs = smsConfigRepository.findAllByServiceTypeContainingIgnoreCaseAndMvnoIdInAndBuIdIn(serviceType, Arrays.asList(mvnoId, 1L), Arrays.asList(buId), pageable);
            }
        }
//		if (smsConfigs.isEmpty()) {
//			throw new RuntimeException("No Record Found");
//		}
        return new PageImpl<>(smsConfigs.getContent(), pageable, smsConfigs.getTotalElements());
    }

    @Override
    public Page<SearchSmsRespDto> SerchSmsConfig(String smsUrl, Long mvnoId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SmsConfig> smsConfigs;
        if (mvnoId == 1) {
            smsConfigs = smsConfigRepository.findAllBySmsUrlIsContainingIgnoreCase(smsUrl, pageable);
        } else {
            smsConfigs = smsConfigRepository.findAllBySmsUrlIsContainingIgnoreCaseAndMvnoIdIn(smsUrl, Arrays.asList(mvnoId, 1L), pageable);
        }
        if (smsConfigs.isEmpty()) {
            throw new RuntimeException("No Record Found");
        }
        List<SearchSmsRespDto> collect = smsConfigs.getContent().stream().map(this::setProperties).collect(Collectors.toList());
        return new PageImpl<>(collect, pageable, smsConfigs.getTotalElements());

    }

    public SearchSmsRespDto setProperties(SmsConfig smsConfig) {
        return new SearchSmsRespDto(
                smsConfig.getBuId(),
                smsConfig.getSmsUrl(),
                smsConfig.getMvnoId(),
                smsConfig.getSmsConfigId(),
                smsConfig.getConfigStatus(),
                smsConfig.getServiceType()
        );
    }

    @Override
    public Page<SearchSmsRespDto> SmsConfig(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType) {
        Integer page = requestDTO.getPage();
        Integer size = requestDTO.getPageSize();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("smsUrl")));
        Specification<SmsConfig> spec = Specification.where(null);
        Page<SmsConfig> smsconfig = null;
        if (mvnoId != 1) {
            spec = spec.and((root, query, builder) -> (root.get(NotificationConstants.SmsSearchEnum.MVNO_ID).in(Arrays.asList(mvnoId, 1))));
        }
        if (!serviceType.trim().isEmpty()) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get(NotificationConstants.SmsSearchEnum.SERVICE_TYPE), serviceType));
        }
        if (null != requestDTO.getFilters() && requestDTO.getFilters().size() > 0) {
            for (GenericSearchModel searchModel : requestDTO.getFilters()) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.ANY)) {
                    smsconfig = getAllSmsConfig(searchModel.getFilterValue().trim(), searchModel.getFilterCondition().trim(), spec, pageable, mvnoId, serviceType.trim());
                } else {
                    spec = getSmsConfigByFilter(searchModel.getFilterValue(), searchModel.getFilterCondition(), searchModel.getFilterColumn().trim(), spec, pageable, mvnoId, serviceType);
                }
            }
        }
        if (smsconfig == null || (smsconfig.isEmpty())) {
            smsconfig = smsConfigRepository.findAll(spec, pageable);
        }

//		if(smsconfig==null || (smsconfig.isEmpty())){
//			throw new RuntimeException("No Record Found!");
//		}
        Page<SearchSmsRespDto> map = smsconfig.map(this::setPropertiesToDto);
        return map;
    }

    public Page<SmsConfig> getAllSmsConfig(String value, String filterCondition, Specification<SmsConfig> spec, Pageable pageable, Long mvnoId, String serviceType) {
        SmsConfig smsconfig = new SmsConfig();
        try {
            smsconfig.setSmsUrl(value.trim());
            smsconfig.setSmsConfigId(Long.valueOf(value));
            smsconfig.setServiceType(value.trim());
            smsconfig.setMvnoId(Long.valueOf(value.trim()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("smsUrl", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<SmsConfig> example = Example.of(smsconfig, customExampleMatcher);
        Page<SmsConfig> smsaudits = smsConfigRepository.findAll(example, pageable);
        return smsaudits;
    }

    public Specification<SmsConfig> getSmsConfigByFilter(String value, String filterCondition, String filterColumn, Specification<SmsConfig> spec, Pageable pageable, Long mvnoId, String serviceType) {
//		Specification specification = null;
        if ((filterColumn != null) && (!filterColumn.trim().isEmpty())) {
            if (filterCondition.equalsIgnoreCase("OR")) {
                if (filterColumn.trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.DATE)) {
                    spec = dateFilter(value, filterCondition, spec, filterColumn, mvnoId, serviceType);
                } else {
                    spec = spec.or((root, query, builder) -> builder.like(root.get(filterColumn.trim()), "%" + value.trim() + "%"));
                }
            } else {
                if (filterColumn.trim().equalsIgnoreCase(NotificationConstants.SmsSearchEnum.DATE)) {
                    spec = dateFilter(value, filterCondition, spec, filterColumn, mvnoId, serviceType);

                } else {
                    spec = spec.and((root, query, builder) -> builder.like(root.get(filterColumn.trim()), "%" + value.trim() + "%"));
                }
            }
        }
        return spec;
    }

    public Specification<SmsConfig> dateFilter(String value, String filterCondition, Specification<SmsConfig> spec, String filterColumn, Long mvnoId, String serviceType) {
        JSONObject filterValue = new JSONObject(value);
        String fromDate = filterValue.getString("from") + "T00:00:00";
        String toDate = filterValue.getString("to") + "T23:59:59";
        LocalDateTime fromDateTime = LocalDateTime.parse(fromDate);
        LocalDateTime toDateTime = LocalDateTime.parse(toDate);
        if (filterCondition.equalsIgnoreCase("OR")) {
            spec = spec.or((root, query, builder) -> builder.between(root.get(filterColumn.trim()), fromDateTime, toDateTime));
        } else {
            spec = spec.and((root, query, builder) -> builder.between(root.get(filterColumn.trim()), fromDateTime, toDateTime));
        }
        return spec;
    }

    public SearchSmsRespDto setPropertiesToDto(SmsConfig smsConfig) {
        return new SearchSmsRespDto(
                smsConfig.getSmsConfigId(),
                smsConfig.getSmsUrl(),
                smsConfig.getMvnoId(),
                smsConfig.getBuId(),
                smsConfig.getConfigStatus(),
                smsConfig.getServiceType()
        );
    }

    @Override
    public boolean validation(PaginationRequestDTO requestDTO) {
        for (GenericSearchModel searchModel : requestDTO.getFilters()) {
            if (searchModel.getFilterValue() == null || searchModel.getFilterValue().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}


