package com.diameter.serviceImpl;

import com.diameter.kafka.SavePlanSharedDataMessage;
import com.diameter.kafka.UpdatePlanSharedDataMessage;
import com.diameter.model.PostpaidPlan;
import com.diameter.model.QOSPolicy;
import com.diameter.repository.PostpaidPlanRepository;
import com.diameter.repository.QOSPolicyRepository;
import com.diameter.service.PostpaidPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostpaidPlanServiceImpl implements PostpaidPlanService {

    private static final Logger logger = LoggerFactory.getLogger(PostpaidPlanServiceImpl.class);

    private final PostpaidPlanRepository postpaidPlanRepository;
    private final QOSPolicyRepository qosPolicyRepository;
//    private final PostpaidPlanChargeRepository postpaidPlanChargeRepository;
//    private final ProductPlanMappingRepository productPlanMappingRepository;
//    private final PostPaidPlanServiceAreaMappingRepository postPaidPlanServiceAreaMappingRepository;

    public PostpaidPlanServiceImpl(PostpaidPlanRepository postpaidPlanRepository, QOSPolicyRepository qosPolicyRepository
//                                    ,PostpaidPlanChargeRepository postpaidPlanChargeRepository,
//                                   ProductPlanMappingRepository productPlanMappingRepository,
//                                   PostPaidPlanServiceAreaMappingRepository postPaidPlanServiceAreaMappingRepository
    ) {
        this.postpaidPlanRepository = postpaidPlanRepository;
        this.qosPolicyRepository = qosPolicyRepository;
//        this.postpaidPlanChargeRepository = postpaidPlanChargeRepository;
//        this.productPlanMappingRepository = productPlanMappingRepository;
//        this.postPaidPlanServiceAreaMappingRepository = postPaidPlanServiceAreaMappingRepository;
    }

    @Transactional
    @Override
    public void savePostpaidPlan(SavePlanSharedDataMessage message) throws Exception {

        try {

            // ======================================================
            // 1️⃣ CREATE MASTER ENTITY
            // ======================================================
            PostpaidPlan plan = new PostpaidPlan();
            logger.info("DATA RPG FROM MESSAGE : {}", message.getDataRatePackageGroup());
            // BASIC
            plan.setId(message.getId());
            plan.setName(message.getName());
            plan.setDisplayName(message.getDisplayName());
            plan.setCode(message.getCode());
            plan.setDesc(message.getDesc());
            plan.setCategory(message.getCategory());
            plan.setPlantype(message.getPlantype());
            plan.setPlanGroup(message.getPlanGroup());
            plan.setStatus(message.getStatus());
            plan.setPlanStatus(message.getPlanStatus());
            plan.setMode(message.getMode());
            plan.setUnitsOfValidity(message.getUnitsOfValidity());
            plan.setQuotatype(message.getQuotatype());
            plan.setUsageQuotaType(message.getUsageQuotaType());
            plan.setSmsRatePackageGroup(message.getSmsRatePackageGroup());
            plan.setVoiceRatePackageGroup(message.getVoiceRatePackageGroup());
            plan.setDataRatePackageGroup(message.getDataRatePackageGroup());
            // NUMERIC
            plan.setValidity(message.getValidity());
            plan.setOfferprice(message.getOfferprice());
            plan.setNewOfferPrice(message.getNewOfferPrice());
            plan.setQuota(message.getQuota());
            plan.setChildQuota(message.getChildQuota());
            plan.setSlice(message.getSlice());
            plan.setDbr(message.getDbr());

            // BOOLEAN
            plan.setAllowOverUsage(message.getAllowOverUsage());
            plan.setIsDelete(message.getIsDelete() != null ? message.getIsDelete() : false);
            plan.setBasePlan(message.getBasePlan());

            // DATE
            if (message.getStartDate() != null) {
                plan.setStartDate(LocalDate.parse(message.getStartDate()));
            }
            if (message.getEndDate() != null) {
                plan.setEndDate(LocalDate.parse(message.getEndDate()));
            }

            // MVNO & SERVICE
            plan.setMvnoId(message.getMvnoId());
            plan.setServiceId(message.getServiceId());
            plan.setTimebasepolicyId(message.getTimebasepolicyId());
            plan.setTaxId(message.getTaxId());
            plan.setBuId(message.getBuId());

            plan.setIsDelete(false);
            plan.setCreatedate(LocalDateTime.now());

            // ======================================================
            //  ADDITIONAL FIELDS (NEWLY ADDED)
            // ======================================================

            // PARAM
            plan.setParam1(message.getParam1());
            plan.setParam2(message.getParam2());
            plan.setParam3(message.getParam3());

            // LIMITS
            plan.setSmsLimit(message.getSmsLimit());
            plan.setVoiceLimit(message.getVoiceLimit());

            // RESET INTERVALS
            plan.setSmsResetInterval(message.getSmsResetInterval());
            plan.setVoiceResetInterval(message.getVoiceResetInterval());

            // TYPES
            plan.setSmstype(message.getSmstype());
            plan.setVoicetype(message.getVoicetype());
            plan.setPulse(message.getPulse());

            // QUOTA UNIT DID / INTERCOM
            plan.setQuotaunitdid(message.getQuotaunitdid());
            plan.setQuotaunitintercom(message.getQuotaunitintercom());

            // FLAGS
            plan.setUseQuota(message.getUseQuota() != null ? message.getUseQuota() : false);
            plan.setAddonToBase(message.getAddonToBase() != null ? message.getAddonToBase() : false);
            plan.setAllowdiscount(message.getAllowdiscount() != null ? message.getAllowdiscount() : false);

            // CHUNK
            plan.setChunk(message.getChunk());

            // CURRENCY
            plan.setCurrency(message.getCurrency());

            // HOLD SETTINGS
            plan.setMaxHoldDurationDays(
                    message.getMaxHoldDurationDays() != null ? message.getMaxHoldDurationDays() : 0
            );
            plan.setMaxHoldAttempts(
                    message.getMaxHoldAttempts() != null ? message.getMaxHoldAttempts() : 0
            );

            //TODO Arpit : Added missing fields
            plan.setMaxconcurrentsession(message.getMaxconcurrentsession());
            plan.setMvnoId(message.getMvnoId());
            plan.setTaxamount(message.getTaxamount());
            plan.setNextStaff(message.getNextStaff());
            plan.setAccessibility(message.getAccessibility());
            plan.setBusinessType(message.getBusinessType());
            plan.setCurrency(message.getCurrency());
            plan.setBasePlan(message.getBasePlan());
            plan.setTemplateId(message.getTemplateId());
            plan.setMvnoName(message.getMvnoName());


            // ======================================================
            // 2️⃣ QOS POLICY (DO NOT CHANGE REPO)
            // ======================================================
            if (message.getQospolicy_id() != null) {

                Optional<QOSPolicy> qosOpt =
                        qosPolicyRepository.findOptionalById(
                                String.valueOf(message.getQospolicy_id())
                        );

                if (qosOpt.isPresent()) {
                    plan.setQospolicy(qosOpt.get());
                } else {
                    logger.warn(
                            "QOSPolicy not found for id {}. Saving plan WITHOUT QOS mapping",
                            message.getQospolicy_id()
                    );
                    plan.setQospolicy(null);
                }
            }

            // ======================================================
            // 3️⃣ SAVE MASTER FIRST
            // ======================================================
            PostpaidPlan savedPlan = postpaidPlanRepository.save(plan);

            // ======================================================
            // 4️⃣ SAVE CHARGES (JPA)
            // ======================================================
            //  TODO : TODOkafka : Need to discuss
            /**
             * 	at java.lang.Thread.run(Thread.java:842) ~[?:?]
             * Caused by: org.springframework.orm.jpa.JpaObjectRetrievalFailureException: Unable to find com.diameter.model.Charge with id 968; nested exception is javax.persistence.EntityNotFoundException: Unable to find com.diameter.model.Charge with id 968
             * 	at org.springframework.orm.jpa.EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(EntityManagerFactoryUtils.java:379) ~[spring-orm-5.3.31.jar:5.3.31]
             */
//            if (!CollectionUtils.isEmpty(message.getChargeList())) {
//
//                for (PostpaidPlanCharge charge : message.getChargeList()) {
//
//                    charge.setPlan(savedPlan);  // correct FK mapping
//                    postpaidPlanChargeRepository.save(charge);
//                }
//            }

            // ======================================================
            // 6️⃣ SAVE PRODUCT PLAN MAPPING (JPA)
            // ======================================================
            //  TODO : TODOkafka : Need to discuss
//            if (!CollectionUtils.isEmpty(message.getProductplanmappingList())) {
//
//                for (Productplanmappingdto dto : message.getProductplanmappingList()) {
//
//                    Productplanmapping entity = new Productplanmapping();
//
//                    entity.setId(dto.getId());
//                    entity.setPlanId(savedPlan.getId().longValue());
//                    entity.setProductCategoryId(dto.getProductCategoryId());
//                    entity.setProduct_type(dto.getProduct_type());
//                    entity.setProductId(dto.getProductId());
//                    entity.setRevisedCharge(dto.getRevisedCharge());
//                    entity.setOwnershipType(dto.getOwnershipType());
//                    entity.setName(dto.getName());
//                    entity.setProductQuantity(dto.getProductQuantity());
//
//                    entity.setCreatedById(message.getCreatedById());
//                    entity.setLastModifiedById(message.getLastModifiedById());
//
//                    productPlanMappingRepository.save(entity);
//                }
//            }

            // ======================================================
            // 7️⃣ SAVE POSTPAID PLAN SERVICE AREA MAPPING (JPA)
            // ======================================================
            //  TODO : TODOkafka : Need to discuss
//            if (!CollectionUtils.isEmpty(message.getPostPaidPlanServiceAreaMappingList())) {
//
//                for (PostPaidPlanServiceAreaMapping mapping :
//                        message.getPostPaidPlanServiceAreaMappingList()) {
//
//                    mapping.setPlanId(savedPlan.getId());
//                    mapping.setServiceId(savedPlan.getServiceId());
//
//                    mapping.setCreatedOn(savedPlan.getCreatedate());
//                    mapping.setLastmodifiedOn(LocalDateTime.now());
//
//                    postPaidPlanServiceAreaMappingRepository.save(mapping);
//                }
//            }

            logger.info("Plan with all child mappings created successfully in Diameter DB : {}", message.getName());

        } catch (Exception e) {

            logger.error("Unable to create Plan with name {} , Error: {}",
                    message.getName(), e.getMessage(), e);

            throw new Exception("Plan creation failed in Diameter Service", e);
        }
    }

    @Transactional
    @Override
    public void updatePostpaidPlan(UpdatePlanSharedDataMessage message) throws Exception {

        try {

            if (message == null || message.getId() == null) {
                logger.warn("UpdatePlanSharedDataMessage or ID is null");
                return;
            }

            PostpaidPlan plan = postpaidPlanRepository
                    .findById(message.getId())
                    .orElseThrow(() ->
                            new RuntimeException("PostpaidPlan not found with ID: " + message.getId())
                    );

            // ======================================================
            // 1️⃣ BASIC INFO
            // ======================================================
            plan.setName(message.getName());
            plan.setDisplayName(message.getDisplayName());
            plan.setCode(message.getCode());
            plan.setDesc(message.getDesc());
            plan.setCategory(message.getCategory());
            plan.setPlantype(message.getPlantype());
            plan.setPlanGroup(message.getPlanGroup());
            plan.setStatus(message.getStatus());
            plan.setPlanStatus(message.getPlanStatus());
            plan.setMode(message.getMode());
            plan.setUnitsOfValidity(message.getUnitsOfValidity());
            plan.setQuotatype(message.getQuotatype());
            plan.setUsageQuotaType(message.getUsageQuotaType());
            plan.setSmsRatePackageGroup(message.getSmsRatePackageGroup());
            plan.setVoiceRatePackageGroup(message.getVoiceRatePackageGroup());
            plan.setDataRatePackageGroup(message.getDataRatePackageGroup());

            // ======================================================
            // 2️⃣ NUMERIC
            // ======================================================
            plan.setValidity(message.getValidity());
            plan.setOfferprice(message.getOfferprice());
            plan.setNewOfferPrice(message.getNewOfferPrice());
            plan.setQuota(message.getQuota());
            plan.setChildQuota(message.getChildQuota());
            plan.setSlice(message.getSlice());
            plan.setDbr(message.getDbr());
            plan.setChunk(message.getChunk());
            plan.setQuotatime(message.getQuotatime());
            plan.setQuotadid(message.getQuotadid());
            plan.setQuotaintercom(message.getQuotaintercom());
            plan.setMaxHoldDurationDays(message.getMaxHoldDurationDays());
            plan.setMaxHoldAttempts(message.getMaxHoldAttempts());

            // ======================================================
            // 3️⃣ BOOLEAN FLAGS
            // ======================================================
            plan.setAllowOverUsage(message.getAllowOverUsage());
            plan.setIsDelete(message.getIsDelete() != null ? message.getIsDelete() : false);
            plan.setBasePlan(message.getBasePlan());
            plan.setUseQuota(message.getUseQuota() != null ? message.getUseQuota() : false);
            plan.setAddonToBase(message.getAddonToBase() != null ? message.getAddonToBase() : false);
            plan.setAllowdiscount(message.getAllowdiscount() != null ? message.getAllowdiscount() : false);

            // ======================================================
            // 4️⃣ DATE
            // ======================================================
            if (message.getStartDate() != null) {
                plan.setStartDate(LocalDate.parse(message.getStartDate()));
            }
            if (message.getEndDate() != null) {
                plan.setEndDate(LocalDate.parse(message.getEndDate()));
            }

            // ======================================================
            // 5️⃣ QUOTA / LIMITS
            // ======================================================
            plan.setQuotaUnit(message.getQuotaUnit());
            plan.setQuotaunitdid(message.getQuotaunitdid());
            plan.setQuotaunitintercom(message.getQuotaunitintercom());
            plan.setQuotaunittime(message.getQuotaunittime());
            plan.setQuotaResetInterval(message.getQuotaResetInterval());

            plan.setSmsLimit(message.getSmsLimit());
            plan.setVoiceLimit(message.getVoiceLimit());
            plan.setSmsResetInterval(message.getSmsResetInterval());
            plan.setVoiceResetInterval(message.getVoiceResetInterval());

            plan.setSmstype(message.getSmstype());
            plan.setVoicetype(message.getVoicetype());
            plan.setPulse(message.getPulse());

            // ======================================================
            // 6️⃣ MVNO & SERVICE
            // ======================================================
            plan.setMvnoId(message.getMvnoId());
            plan.setServiceId(message.getServiceId());
            plan.setTimebasepolicyId(message.getTimebasepolicyId());
            plan.setTaxId(message.getTaxId());
            plan.setBuId(message.getBuId());

            // ======================================================
            // 7️⃣ POLICY
            // ======================================================
            if (message.getQospolicy_id() != null) {

                Optional<QOSPolicy> qosOpt =
                        qosPolicyRepository.findOptionalById(
                                String.valueOf(message.getQospolicy_id())
                        );

                if (qosOpt.isPresent()) {
                    plan.setQospolicy(qosOpt.get());
                } else {
                    logger.warn(
                            "QOSPolicy not found for id {} during UPDATE. Plan updated without QOS mapping",
                            message.getQospolicy_id()
                    );
                    plan.setQospolicy(null); // OR keep existing, see note below
                }

            } else {
                plan.setQospolicy(null);
            }

            // ======================================================
            // 8️⃣ OTHER
            // ======================================================
            plan.setDataCategory(message.getDataCategory());
            plan.setCurrency(message.getCurrency());
            plan.setLocation(message.getLocation());
            plan.setQuantity(message.getQuantity());
            plan.setPackage_type(message.getPackage_type());
            plan.setNumber_of_days(message.getNumber_of_days());
            plan.setNo_of_users(message.getNo_of_users());
            plan.setIp_or_ip_pool(message.getIp_or_ip_pool());
            plan.setEvent_per_second(message.getEvent_per_second());
            plan.setCountry(message.getCountry());
            plan.setBusinessType(message.getBusinessType());
            plan.setTemplateId(message.getTemplateId());
            plan.setProductId(message.getProductId());
            plan.setInvoiceToOrg(message.getInvoiceToOrg());
            plan.setRequiredApproval(message.getRequiredApproval());

            //TODO Arpit : Added missing fields
            plan.setMaxconcurrentsession(message.getMaxconcurrentsession());
            plan.setMvnoId(message.getMvnoId());
            plan.setTaxamount(message.getTaxamount());
            plan.setNextStaff(message.getNextStaff());
            plan.setAccessibility(message.getAccessibility());
            plan.setBusinessType(message.getBusinessType());
            plan.setCurrency(message.getCurrency());
            plan.setBasePlan(message.getBasePlan());
            plan.setTemplateId(message.getTemplateId());
            plan.setMvnoName(message.getMvnoName());

            // ======================================================
            // 9️⃣ SAVE MASTER
            // ======================================================
            postpaidPlanRepository.save(plan);

            // ======================================================
            // 🔟 PRODUCT PLAN MAPPING (DELETE + INSERT)
            // ======================================================
            //  TODO : TODOkafka : Need to discuss
//            productPlanMappingRepository.deleteByPlanId(updatedPlan.getId().longValue());
//
//            if (!CollectionUtils.isEmpty(message.getProductplanmappingList())) {
//
//                for (Productplanmappingdto dto : message.getProductplanmappingList()) {
//
//                    Productplanmapping entity = new Productplanmapping();
//
//                    entity.setId(dto.getId());
//                    entity.setPlanId(updatedPlan.getId().longValue());
//                    entity.setProductCategoryId(dto.getProductCategoryId());
//                    entity.setProduct_type(dto.getProduct_type());
//                    entity.setProductId(dto.getProductId());
//                    entity.setRevisedCharge(dto.getRevisedCharge());
//                    entity.setOwnershipType(dto.getOwnershipType());
//                    entity.setName(dto.getName());
//                    entity.setProductQuantity(dto.getProductQuantity());
//
//                    entity.setCreatedById(message.getCreatedById());
//                    entity.setLastModifiedById(message.getLastModifiedById());
//
//                    productPlanMappingRepository.save(entity);
//                }
//            }

            logger.info("Plan with all child mappings updated successfully in Diameter DB : {}", message.getName());

        } catch (Exception e) {

            logger.error("Unable to update Plan with name {} , Error: {}",
                    message.getName(), e.getMessage(), e);

            throw new Exception("Plan updation failed in Diameter Service", e);
        }
    }

}
