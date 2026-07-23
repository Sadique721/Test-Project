package com.savbill.radius.services.impl;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.QVLANManagement;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.entity.VLANValidationMapping;
import com.savbill.radius.helper.BulkVlanResponseDto;
import com.savbill.radius.helper.VlanCsvDto;
import com.savbill.radius.helper.VlanManagementDto;
import com.savbill.radius.helper.VlanSearch;
import com.savbill.radius.mvno.Repository.MvnoRepository;
import com.savbill.radius.repository.VlanManagementRepository;
import com.savbill.radius.repository.VlanValidationMappingRepository;
import com.savbill.radius.services.ClientService;
import com.savbill.radius.services.VlanAuditService;
import com.savbill.radius.services.VlanManagementService;
import com.savbill.radius.utils.*;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.radius.utils.*;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class VlanManagementServiceImpl implements VlanManagementService {

    private static final Logger log = LoggerFactory.getLogger(VlanManagementServiceImpl.class);
    @Autowired
    private VlanManagementRepository vlanManagementRepository;

    @Autowired
    private VlanValidationMappingRepository vlanValidationMappingRepository;
    @Autowired
    VlanAuditService vlanAuditService;
    @Autowired
    UpdateDiffFinder updateDiffFinder;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private ClientService clientService;

    @Override
    public VLANManagement save(VlanManagementDto vlanManagementDto, Integer mvnoId) {
        VLANManagement vlan = vlanManagementRepository.findByVlanName(vlanManagementDto.getVlanName());
        if (vlan == null) {
            validateName(vlanManagementDto, mvnoId);
            VLANManagement vlanManagement = new VLANManagement(vlanManagementDto);
            vlanManagement.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            vlanManagement.setCreatedOn(new Timestamp(new Date().getTime()));
            vlanManagement.setLastModifiedOn(new Timestamp(new Date().getTime()));
            VLANValidationMapping newMapping = new VLANValidationMapping();
            if (vlanManagementDto.getMappingList() != null && !vlanManagementDto.getMappingList().isEmpty()) {
                List<VLANValidationMapping> mappingsToSave = new ArrayList<>();

                for (VLANValidationMapping mapping : vlanManagementDto.getMappingList()) {
                    newMapping.setRadiusAttribute(mapping.getRadiusAttribute());
                    newMapping.setRegex(mapping.getRegex());
                    if ("REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}".equalsIgnoreCase(mapping.getRegex())) {
                        newMapping.setRegexValue(vlanManagement.getCircuitType());
                    } else {
                        String regexValue = "CONTAINS{REQ{NAS-Port-Id}," +
                                vlanManagement.getNasPortId2() + " " +
                                vlanManagement.getNasPortId3() + " " +
                                vlanManagement.getNasPortId4() +
                                (vlanManagement.getNasPortId5() != null ? ":" + vlanManagement.getNasPortId5() : ":}") + "}";
                        newMapping.setRegexValue(regexValue);
                    }

                    mappingsToSave.add(newMapping);
                }
                vlanManagement.setMappingList(mappingsToSave);
            }else
            {
                List<VLANValidationMapping> defaultMappings = new ArrayList<>();

                VLANValidationMapping defaultMapping1 = new VLANValidationMapping();
                defaultMapping1.setRadiusAttribute("NAS-Port-Id");
                defaultMapping1.setRegex("REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}");
                defaultMapping1.setRegexValue(vlanManagement.getCircuitType());

                VLANValidationMapping defaultMapping2 = new VLANValidationMapping();
                defaultMapping2.setRadiusAttribute("NAS-Port-Id");
                defaultMapping2.setRegex("{EXP{MERGE{PROFILE{vlan.NAS_PORT_ID_2}},MERGE{ },MERGE{PROFILE{vlan.NAS_PORT_ID_3}},MERGE{ },MERGE{PROFILE{vlan.NAS_PORT_ID_4}},MERGE{:},MERGE{PROFILE{vlan.NAS_PORT_ID_5}}}}");
                String regexValue = "CONTAINS{REQ{NAS-Port-Id}," +
                        vlanManagement.getNasPortId2() + " " +
                        vlanManagement.getNasPortId3() + " " +
                        vlanManagement.getNasPortId4() +
                        (vlanManagement.getNasPortId5() != null ? ":" + vlanManagement.getNasPortId5() : ":}") + "}";
                defaultMapping2.setRegexValue(regexValue);

                defaultMappings.add(defaultMapping1);
                defaultMappings.add(defaultMapping2);

                vlanManagement.setMappingList(defaultMappings);
            }
            return vlanManagementRepository.save(vlanManagement);
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Name Already Exist", null);
        }
    }

    private void validateName(VlanManagementDto vlanManagementDto, Integer mvnoId) {
        Optional<VLANManagement> vlanByName = findByName(vlanManagementDto.getVlanName(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
        if (vlanByName.isPresent()) {
            throw new IllegalArgumentException("VLAN Entry is already exists with name: " + vlanManagementDto.getVlanName());
        }
    }

    private Optional<VLANManagement> findByName(String vlanName, Integer mvnoId) {

        if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
            throw new IllegalArgumentException("mvno id not found");
        }
        QVLANManagement qvlanManagement = QVLANManagement.vLANManagement;
        BooleanExpression boolExp = qvlanManagement.isNotNull();
        boolExp = boolExp.and(qvlanManagement.vlanName.eq(vlanName));
        if (mvnoId == null || mvnoId != 1) {
            boolExp = boolExp.and(qvlanManagement.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
        }
        Optional<VLANManagement> optionalRadiusProfile = vlanManagementRepository.findOne(boolExp);
        return optionalRadiusProfile;
    }

    @Override
    public List<VLANManagement> findAllVlans(Integer mvnoId) {
        try {
            QVLANManagement qvlanManagement = QVLANManagement.vLANManagement;
            BooleanExpression exp = qvlanManagement.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return vlanManagementRepository.findAll();
            else {
                exp = exp.and(qvlanManagement.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<VLANManagement>) vlanManagementRepository.findAll(exp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public VLANManagement findVlanById(Long vlanId, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(vlanId))
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid client id.");
            QVLANManagement qVlanMangement = QVLANManagement.vLANManagement;
            BooleanExpression boolExp = qVlanMangement.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qVlanMangement.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qVlanMangement.vlanId.eq(vlanId));

            Optional<VLANManagement> client = vlanManagementRepository.findOne(boolExp);
            if (!client.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with Vlan id " + vlanId + " . Please enter valid vlan id.");
            }

            return client.get();

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public VLANManagement updateVlanManagement(VlanManagementDto vlanManagementDto, Integer mvnoId) {
        try {
            VLANManagement oldVlan = findVlanById(vlanManagementDto.getVlanId(), mvnoId);

            if (!oldVlan.getVlanName().equalsIgnoreCase(vlanManagementDto.getVlanName())) {
                validateName(vlanManagementDto, mvnoId);
            }

            VLANManagement vlanManagement = new VLANManagement(vlanManagementDto);
            vlanManagement.setVlanId(vlanManagementDto.getVlanId());
            vlanManagement.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            vlanManagement.setCreatedOn(oldVlan.getCreatedOn());
            vlanManagement.setLastModifiedOn(new Timestamp(new Date().getTime()));
            String updated = updateDiffFinder.getUpdatedDiff(oldVlan, vlanManagement);
            vlanAuditService.saveVlanAudit(vlanManagement, vlanManagementDto.getLoggedInUser(), "Update", vlanManagementDto.getStaffId(), updated, "");
            // Get the list of validation mappings
            List<VLANValidationMapping> validationMappingList = vlanManagement.getMappingList();

            for (VLANValidationMapping validationMapping : validationMappingList) {
                if (validationMapping.getRegex().equalsIgnoreCase("REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}")) {
                    validationMapping.setRegexValue(vlanManagement.getCircuitType());
                } else {
                    // Set regex value for NAS-Port-Id mapping
                    String regexValue = "CONTAINS{REQ{NAS-Port-Id}," +
                            vlanManagement.getNasPortId2() + " " +
                            vlanManagement.getNasPortId3() + " " +
                            vlanManagement.getNasPortId4() +
                            (vlanManagement.getNasPortId5() != null ? ":" + vlanManagement.getNasPortId5() : ":}") + "}";

                    validationMapping.setRegexValue(regexValue);
                }
            }
            return vlanManagementRepository.save(vlanManagement);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void deleteByVlanId(Long vlanId, Integer mvnoId) {
        try {
            VLANManagement client = findVlanById(vlanId, mvnoId);
            vlanManagementRepository.deleteById(vlanId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PageableResponse<VLANManagement> findVlansList(Integer mvnoId, PaginationDTO paginationDTO) {
        try {
            QVLANManagement qvlanManagement = QVLANManagement.vLANManagement;
            BooleanExpression exp = qvlanManagement.isNotNull();
            Page<VLANManagement> vlanManagementPage = null;

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));

            if (mvnoId != null && mvnoId == 1)
                vlanManagementPage = vlanManagementRepository.findAll(exp, pageable);
            else {
                exp = exp.and(qvlanManagement.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                vlanManagementPage = vlanManagementRepository.findAll(exp, pageable);
            }
            PageableResponse<VLANManagement> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<VLANManagement>(vlanManagementPage.getContent(), pageable, vlanManagementPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PageableResponse<VLANManagement> findAllVlansBySearch(Integer mvnoId, VlanSearch vlanSearch, PaginationDTO paginationDTO) {
        try {
            QVLANManagement qvlanManagement = QVLANManagement.vLANManagement;
            BooleanExpression exp = qvlanManagement.isNotNull();
            Page<VLANManagement> vlanPage = null;
            List<Long> custIdList = new ArrayList<>();

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(vlanSearch.getVlanName()) && !("null").equalsIgnoreCase(vlanSearch.getVlanName())) {
                exp = exp.and(qvlanManagement.vlanName.contains(vlanSearch.getVlanName()));
            }

            if (ValidateCrudTransactionData.validateStringTypeFieldValue(vlanSearch.getNasIdentifier()) && !("null").equalsIgnoreCase(vlanSearch.getNasIdentifier())) {
                exp = exp.and(qvlanManagement.nasIdentifier.contains(vlanSearch.getNasIdentifier()));
            }

            if (mvnoId != null && mvnoId == 1)
                vlanPage = vlanManagementRepository.findAll(exp, pageable);
            else {
                exp = exp.and(qvlanManagement.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                vlanPage = vlanManagementRepository.findAll(exp, pageable);
            }
            PageableResponse<VLANManagement> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<>(vlanPage.getContent(), pageable, vlanPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    @Override
    public Map<String, Object> addBulkVlan(MultipartFile file, Integer mvnoId, Integer staffId, String loggedInUser) {
        Map<String, Object> response = new HashMap<>();
        LocalDateTime startTime = LocalDateTime.now();
        Map<String, VLANManagement> vlanMap = new HashMap<>();
        AtomicInteger addedListCount = new AtomicInteger();
        try {
            String PATH = clientService.getClientSrvByName(RadiusConstants.BULK_VLAN_PATH, mvnoId).get(0).getValue();
            String mvnoName = mvnoRepository.findMvnoNameById(mvnoId.longValue());
            String subFolderName = "/" + mvnoName + "/";
            String path = PATH + subFolderName;
            String fileName = fileUtility.saveFileToServer(file, path, mvnoId);
            vlanAuditService.saveVlanAudit("-" ,loggedInUser, "Bulk Process", staffId, fileName, "VLAN file read and insertion process started.", "Bulk process started.");

            try {
                log.info("VLAN file read process started");
                vlanMap.putAll(readFile(file, mvnoId, vlanMap, addedListCount.get(), staffId, loggedInUser));
                log.info("VLAN file read process completed");
            } catch (IOException e) {
                e.printStackTrace();
                Integer responseCode = RadiusConstants.FAIL;
                response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
                response.put("responseCode",responseCode);
                response.put("count",addedListCount.get());
                return response;
            }


            CompletableFuture.runAsync(() -> {
                Set<String> vlanNameSets = vlanMap.keySet();

                // Fetch existing VLAN names from the database
                Set<String> existingVlanNames = vlanManagementRepository.findExistingVlanNames(vlanNameSets);
                List<VLANManagement> toBeInsertedData = verifyAlreadyExistOrNot(vlanMap,existingVlanNames);

                log.info("VLAN data save process started");
                addedListCount.set(vlanManagementRepository.saveAll(toBeInsertedData).size());
                log.info("VLAN data save process completed");
                try {
                    vlanValidationMappingRepository.callInsertVlanMappingsProcedure();
                    log.info("Successfully executed stored procedure: insert_vlan_mappings_from_management()");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while executing stored procedure", e);
                }
                vlanAuditService.saveVlanAudit("-" , loggedInUser, "Bulk Process", staffId, fileName, addedListCount + " vlans are created successfully.", "Bulk process completed");
                if (addedListCount.get() > 0) {
                    addAuditForVlan(toBeInsertedData, fileName, mvnoId, staffId, loggedInUser);
                }
                existingVlanNames.forEach(vlanManagement ->
                        vlanAuditService.saveVlanAudit(vlanManagement,loggedInUser, "Bulk Process", staffId, fileName, " vlans are not created.", "Vlans with duplicate names will not be created")
                );
            });
        } catch (CustomValidationException ce) {
            Integer responseCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(RadiusConstants.ERROR_MESSAGE, "file contains invalid data. VLAN_NAME, NAS_IDENTIFIER, NAS_PORT_ID_4, and PRIORITY must not be null or empty.");
            response.put("responseCode",responseCode);
            return response;
        } catch (Exception e) {
            vlanAuditService.saveVlanAudit("-" ,loggedInUser, "Bulk Process", staffId, "-", e.getMessage(), "Error occurs during bulk process");
            e.printStackTrace();
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            response.put("responseCode",responseCode);
            response.put("count",addedListCount.get());
            return response;
        }
        LocalDateTime entTime = LocalDateTime.now();
        log.info("Insert Bulk Vlan time taken: " + ChronoUnit.MILLIS.between(startTime, entTime));
        Integer responseCode = RadiusConstants.SUCCESS;
        response.put(RadiusConstants.MESSAGE, "Process started, Please check audit for updates.");
        response.put("responseCode",responseCode);
        response.put("count",addedListCount.get());
        return response;
    }

    public void addAuditForVlan(List<VLANManagement> toBeInsertedData, String file, Integer mvnoId, Integer staffId, String loggedInUser) {
        try {
            String mvnoName = mvnoRepository.findMvnoNameById(mvnoId.longValue());
            String PATH = clientService.getClientSrvByName(RadiusConstants.BULK_VLAN_PATH, mvnoId).get(0).getValue();
            String subFolderName = "/" + mvnoName + "/";
            String path = PATH + subFolderName;
//            String fileName = fileUtility.saveFileToServer(file, path, mvnoId);
            CompletableFuture.runAsync(() -> {
                try {
                    toBeInsertedData.forEach(vlanManagement ->
                            vlanAuditService.saveVlanAudit(vlanManagement, loggedInUser, "Create", staffId, vlanManagement.toString(), file)
                    );
                } catch (Exception e) {
                    log.error("Async addAuditForVlan failed: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            log.error("Async addAuditForVlan failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Map<String, VLANManagement> readFile(MultipartFile file, Integer mvnoId, Map<String, VLANManagement> vlanMap, int addedListCount, Integer staffId, String loggedInUser) throws IOException {
        String fileName = file.getOriginalFilename();

        if (fileName.endsWith(".csv")) {
            vlanMap = readCsv(file.getInputStream(), mvnoId);
        } else if (fileName.endsWith(".xlsx")) {
            vlanMap = readXls(file, mvnoId, addedListCount, loggedInUser, staffId);
        }
        return vlanMap;
    }

    public List<VLANManagement> verifyAlreadyExistOrNot(Map<String, VLANManagement> vlanManagementMap,Set<String> existingVlanNames) {
//        Set<String> vlanNameSets = vlanManagementMap.keySet();
//
//        // Fetch existing VLAN names from the database
//        Set<String> existingVlanNames = vlanManagementRepository.findExistingVlanNames(vlanNameSets);

        if (existingVlanNames.isEmpty()) {
            return new ArrayList<>(vlanManagementMap.values());
        }

        log.error(String.format("Vlans with duplicate names will not be created: %s", existingVlanNames));
        // Filter out the duplicates
        return vlanManagementMap.values().stream()
                .filter(vlan -> !existingVlanNames.contains(vlan.getVlanName()))
                .collect(Collectors.toList());
    }


    private List<VLANManagement> verifyAlreadyExistOrNotForUpdate(Map<String, VLANManagement> vlanManagementMap, Integer mvnoId, String stattName, String action, Integer staffId, MultipartFile file) {
        Set<String> vlanNameSets = vlanManagementMap.keySet();

        List<VLANManagement> allByVlanNameIn = vlanManagementRepository.findVlanNameByVlanNameIn(vlanNameSets);

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Integer validatedMvnoId = ValidateCrudTransactionData.validateMvnoId(mvnoId);
            Map<VLANManagement, VLANManagement> vlanMap
                    = new HashMap<VLANManagement, VLANManagement>();
                allByVlanNameIn.forEach(vlanManagement -> {
                    VLANManagement vlanManagementFromMap = vlanManagementMap.get(vlanManagement.getVlanName());
                    if (vlanManagementFromMap != null) {
                        VLANManagement oldVlanManagement = vlanManagement;
                        vlanManagementFromMap.setVlanId(vlanManagement.getVlanId());
                        vlanManagementFromMap.setCreatedOn(vlanManagement.getCreatedOn());
                        vlanManagementFromMap.setLastModifiedOn(currentTimestamp);
                        vlanManagementFromMap.setMvnoId(validatedMvnoId);
                        vlanMap.put(vlanManagementFromMap, oldVlanManagement);
                    }
                });


        Set<String> alreadyExistVlanNames = allByVlanNameIn.stream().map(VLANManagement::getVlanName).collect(Collectors.toSet());

        if (vlanManagementMap.size() == allByVlanNameIn.size()) {
            return new ArrayList<>(vlanManagementMap.values());
        } else {
            ArrayList<VLANManagement> vlanManagements = new ArrayList<>(vlanManagementMap.values());

            List<String> notExisted = vlanManagements.stream()
                    .filter(vlanManagement -> !alreadyExistVlanNames.contains(vlanManagement.getVlanName()))
                    .collect(Collectors.toList())
                    .stream()
                    .map(VLANManagement::getVlanName).collect(Collectors.toList());
            log.error(String.format("Vlans which are not updated as Vlan does not exists with names: %s", notExisted));

            return vlanManagements.stream().filter(vlanManagement -> alreadyExistVlanNames.contains(vlanManagement.getVlanName())).collect(Collectors.toList());
        }
    }

    public void addVlanAuditForUpdate(Map<VLANManagement, VLANManagement> vlanMap, String staffName, Integer staffId, Integer mvnoId, String file, String action) {
        try {
            String mvnoName = mvnoRepository.findMvnoNameById(mvnoId.longValue());
            String PATH = clientService.getClientSrvByName(RadiusConstants.BULK_VLAN_PATH, mvnoId).get(0).getValue();
            String subFolderName = "/" + mvnoName + "/";
            String path = PATH + subFolderName;
//            String fileName = fileUtility.saveFileToServer(file, path, mvnoId);
            Map<VLANManagement, String> vlanDifferenceMap = new HashMap<VLANManagement, String>();

                for (Map.Entry<VLANManagement, VLANManagement> set :
                        vlanMap.entrySet()) {
                    String differences = updateDiffFinder.getUpdatedDiff(set.getValue(), set.getKey());
                    vlanDifferenceMap.put(set.getKey(), differences);
                }
            CompletableFuture.runAsync(() -> {
                vlanAuditService.saveVlanAuditForUpdate(vlanDifferenceMap, staffName, action, staffId, file);
            });
        }catch (Exception e){
            e.printStackTrace();
            log.info(String.format("Something went wrong during save vlan audit"));
        }
    }

    @Override
    @Transactional
    public int delete(List<Long> ids, Integer mvnoId) {
        findAllVlans(ids, ValidateCrudTransactionData.validateMvnoId(mvnoId));
        int count = vlanManagementRepository.deleteByVlanIdIn(ids);
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        log.info(String.format("Vlan Management deleted successfully: %s", count));
        return count;

    }

//    @Override
//    public Integer updateBulkVlan(MultipartFile file, Integer mvnoId) {
//        int addedListCount = 0;
//        Map<String, VLANManagement> vlanMap = new HashMap<>();
//
//        try {
//            vlanMap = readFile(file, mvnoId, vlanMap, addedListCount);
//
//            List<VLANManagement> toBeInsertedData =  verifyAlreadyExistOrNotForUpdate(vlanMap, mvnoId);
//
//            addedListCount = vlanManagementRepository.saveAll(toBeInsertedData).size();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//        return addedListCount;
//    }

    @Override
    public Integer updateBulkVlan(MultipartFile file, Integer mvnoId, Integer staffId, String userName) {
        Map<String, VLANManagement> vlanMap = new HashMap<>();
        AtomicInteger addedListCount = new AtomicInteger();

        try {
            String PATH = clientService.getClientSrvByName(RadiusConstants.BULK_VLAN_PATH, mvnoId).get(0).getValue();
            String mvnoName = mvnoRepository.findMvnoNameById(mvnoId.longValue());
            String subFolderName = "/" + mvnoName + "/";
            String path = PATH + subFolderName;
            String fileName = fileUtility.saveFileToServer(file, path, mvnoId);
            vlanAuditService.saveVlanAudit("-" ,userName, "Bulk Process", staffId, fileName, "VLAN file read and update process started.", "Bulk process started.");

            try {
                vlanMap.putAll(readFile(file, mvnoId, vlanMap, addedListCount.get(), staffId, userName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            CompletableFuture.runAsync(() -> {
                List<VLANManagement> toBeInsertedData = verifyAlreadyExistOrNotForUpdate(vlanMap, mvnoId, userName, "Update", staffId, file);

                log.info("save vlans started");
                addedListCount.set(vlanManagementRepository.saveAll(toBeInsertedData).size());
                log.info("vlans saved successfully");

                try {
                    log.info("Started execute store procedure");
                    vlanValidationMappingRepository.callInsertVlanMappingsProcedure();
                    log.info("Successfully executed stored procedure: insert_vlan_mappings_from_management()");
                } catch (Exception e) {
                    log.error("Error while executing stored procedure", e);
                }
                Long start = System.currentTimeMillis();
                Long endTime = System.currentTimeMillis();
                vlanAuditService.saveVlanAudit("-" ,userName, "Bulk Process", staffId, fileName, addedListCount + " vlans are updated successfully.", "Bulk process completed");

                Set<String> vlanNameSets = vlanMap.keySet();

                List<VLANManagement> allByVlanNameIn = vlanManagementRepository.findVlanNameByVlanNameIn(vlanNameSets);

                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                Integer validatedMvnoId = ValidateCrudTransactionData.validateMvnoId(mvnoId);

                Map<VLANManagement, VLANManagement> vlanMap1
                        = new HashMap<VLANManagement, VLANManagement>();
                allByVlanNameIn.forEach(vlanManagement -> {
                    VLANManagement vlanManagementFromMap = vlanMap.get(vlanManagement.getVlanName());
                    if (vlanManagementFromMap != null) {
                        VLANManagement oldVlanManagement = vlanManagement;
                        vlanManagementFromMap.setVlanId(vlanManagement.getVlanId());
                        vlanManagementFromMap.setCreatedOn(vlanManagement.getCreatedOn());
                        vlanManagementFromMap.setLastModifiedOn(currentTimestamp);
                        vlanManagementFromMap.setMvnoId(validatedMvnoId);
                        vlanMap1.put(vlanManagementFromMap, oldVlanManagement);
                    }
                });
                log.info("Started save vlan audit");
                addVlanAuditForUpdate(vlanMap1, userName, staffId, mvnoId, fileName, "Update");
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return addedListCount.get();
    }

    private void addMappingToValidationTable(List<VLANManagement> vlanList) {
        for (VLANManagement vlan : vlanList) {
            String vlanId = vlan.getVlanId().toString();
            // Delete old mappings for this VLAN
            vlanValidationMappingRepository.deleteByVlanId(vlanId);

            // Insert new mappings
            VLANValidationMapping mapping1 = new VLANValidationMapping();
            mapping1.setVlanId(vlanId.toString());
            mapping1.setRadiusAttribute("NAS-Port-Id");
            mapping1.setRegex("...REGEX{\\b(pppoe|clips)\\b,{PROFILE{vlan.CIRCUIT_TYPE}}}");

            VLANValidationMapping mapping2 = new VLANValidationMapping();
            mapping2.setVlanId(vlanId.toString());
            mapping2.setRadiusAttribute("NAS-Port-Id");
            mapping2.setRegex("...{EXP{MERGE{PROFILE{vlan.NAS_PORT_ID_2}},MERGE{ },MERGE{PROFILE{vlan.NAS_PORT_ID_3}},MERGE{ },MERGE{PROFILE{vlan.NAS_PORT_ID_4}},MERGE{:},MERGE{PROFILE{vlan.NAS_PORT_ID_5}}}}");

            vlanValidationMappingRepository.saveAll(Arrays.asList(mapping1, mapping2));
        }
    }

    @Override
    @Transactional
    public int delete(List<Long> ids, Integer mvnoId, Integer staffId, String userName) {
        List<VLANManagement> lists = IterableUtils.toList(findAllVlans(ids, ValidateCrudTransactionData.validateMvnoId(mvnoId)));
        int count = vlanManagementRepository.deleteByVlanIdIn(ids);
        lists.stream().forEach(vlanManagement ->
                vlanAuditService.saveVlanAudit(vlanManagement, userName, "Delete", staffId, vlanManagement.toString(), "")
        );
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        log.info(String.format("Vlan Management deleted successfully: %s", count));
        return count;

    }


    private Iterable<VLANManagement> findAllVlans(List<Long> ids, Integer mvnoId) {
        Iterable<VLANManagement> liveUsers = null;
        try {
            if (ids == null || ids.isEmpty())
                throw new IllegalArgumentException("Please enter valid VLAN ids.");
            QVLANManagement qLiveUser = QVLANManagement.vLANManagement;
            BooleanExpression boolExp = qLiveUser.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLiveUser.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qLiveUser.vlanId.in(ids));

            liveUsers = vlanManagementRepository.findAll(boolExp);
            if (!liveUsers.iterator().hasNext()) {
                throw new RuntimeException("No record is found with given ids");
            }
            return liveUsers;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }

    private Map<String, VLANManagement> readXls(MultipartFile file, Integer mvnoId, int addedListCount, String loggedInUser, Integer staffId) {
        List<VLANManagement> vlans = new ArrayList<>();

        Workbook workbook = null;
        Map<String, VLANManagement> vlanIdMap = new HashMap<>();
        Map<String, Integer> columnMapping = new HashMap<>();
        try (InputStream fis = file.getInputStream()) {

            workbook = new XSSFWorkbook(fis);
            workbook.forEach(sheet -> {
                log.info(" Sheet Name " + sheet.getSheetName());
                DataFormatter dataFormatter = new DataFormatter();
                int index = 0;
                for (Row row : sheet) {
                    if (index == 0) {
                        for (Cell cell : row) {
                            columnMapping.put(dataFormatter.formatCellValue(cell), cell.getColumnIndex());
                        }
                        index++;
                        continue;
                    }
                    ;
                    String nasIdentifier = dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_IDENTIFIER")));
                    String nasPortId4 = dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_PORT_4")));
                    String priorityStr = dataFormatter.formatCellValue(row.getCell(columnMapping.get("PRIORITY")));
                    String vlanName = dataFormatter.formatCellValue(row.getCell(columnMapping.get("VLAN_NAME")));

                    // Validation for required fields
                    if (nasIdentifier == null || nasIdentifier.trim().isEmpty() ||
                            nasPortId4 == null || nasPortId4.trim().isEmpty() ||
                            priorityStr == null || priorityStr.trim().isEmpty() ||
                            vlanName == null || vlanName.trim().isEmpty()) {
                        throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),
                                "file contains invalid data. VLAN_NAME, NAS_IDENTIFIER, NAS_PORT_4, and PRIORITY must not be null or empty.", null);
                    }
                    if (row.getCell(columnMapping.get("VLAN_NAME")) != null && !dataFormatter.formatCellValue(row.getCell(columnMapping.get("VLAN_NAME"))).isEmpty()) {
                        VLANManagement vlanManagement = new VLANManagement();
                        vlanManagement.setVlanName(dataFormatter.formatCellValue(row.getCell(columnMapping.get("VLAN_NAME"))));
                        vlanManagement.setRADIUS_ATTRIBUTE_GROUP_ID(columnMapping.get("RADIUS_ATTRIBUTE_GROUP_ID") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("RADIUS_ATTRIBUTE_GROUP_ID"))) : null);
                        vlanManagement.setNasType(columnMapping.get("NAS_TYPE") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_TYPE"))) : null);
                        vlanManagement.setCircuitType(columnMapping.get("CIRCUIT_TYPE") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("CIRCUIT_TYPE"))) : null);
                        vlanManagement.setNasIdentifier(columnMapping.get("NAS_IDENTIFIER") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_IDENTIFIER"))) : null);
                        vlanManagement.setNasPortId1(columnMapping.get("NAS_PORT_1") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_PORT_1"))) : null);
                        vlanManagement.setNasPortId2(columnMapping.get("NAS_PORT_2") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_PORT_2"))) : null);
                        vlanManagement.setNasPortId3(columnMapping.get("NAS_PORT_3") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_PORT_3"))) : null);
                        vlanManagement.setNasPortId4(columnMapping.get("NAS_PORT_4") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_PORT_4"))) : null);
                        vlanManagement.setNasPortId5(columnMapping.get("NAS_PORT_5") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAS_PORT_5"))) : null);
                        vlanManagement.setCallingStationId(columnMapping.get("CALLING_STATION_ID") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("CALLING_STATION_ID"))) : null);
                        vlanManagement.setFilterId(columnMapping.get("FILTER_ID") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("FILTER_ID"))) : null);
                        vlanManagement.setContextName(columnMapping.get("CONTEXT_NAME") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("CONTEXT_NAME"))) : null);
                        vlanManagement.setForwardPolicy(columnMapping.get("FORWARD_POLICY") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("FORWARD_POLICY"))) : null);
                        vlanManagement.setHttpRedirectProfileName(columnMapping.get("HTTP_REDIRECT_PROFILE") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("HTTP_REDIRECT_PROFILE"))) : null);
                        vlanManagement.setRateLimitRate(columnMapping.get("RATE_LIMIT_RATE") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("RATE_LIMIT_RATE"))) : null);
                        vlanManagement.setRateLimitBurst(columnMapping.get("RATE_LIMIT_BURST") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("RATE_LIMIT_BURST"))) : null);
                        vlanManagement.setQosPolicingPolicyName(columnMapping.get("QOS_POLICING_POLICY_NAME") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("QOS_POLICING_POLICY_NAME"))) : null);
                        vlanManagement.setQosMeteringPolicyName(columnMapping.get("QOS_METERING_POLICY_NAME") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("QOS_METERING_POLICY_NAME"))) : null);
                        vlanManagement.setPppoeUrl(columnMapping.get("PPPOE_URL") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("PPPOE_URL"))) : null);
                        vlanManagement.setPppDnsPrimary(columnMapping.get("PPP_DNS_PRIMARY") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("PPP_DNS_PRIMARY"))) : null);
                        vlanManagement.setPppDnsSecondary(columnMapping.get("PPP_DNS_SECONDARY") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("PPP_DNS_SECONDARY"))) : null);
                        vlanManagement.setPppNbnsPrimary(columnMapping.get("PPP_NBNS_PRIMARY") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("PPP_NBNS_PRIMARY"))) : null);
                        vlanManagement.setSessionTimeOut(columnMapping.get("SESSION_TIMEOUT") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("SESSION_TIMEOUT"))) : null);
                        vlanManagement.setIdleTimeOut(columnMapping.get("IDLE_TIMEOUT") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("IDLE_TIMEOUT"))) : null);
                        vlanManagement.setFramedIpAddress(columnMapping.get("FRAMED_IP_ADDRESS") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("FRAMED_IP_ADDRESS"))) : null);
                        vlanManagement.setRbDhcpMaxLeases(columnMapping.get("RB_DHCP_MAX_LEASES") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("RB_DHCP_MAX_LEASES"))) : null);
                        vlanManagement.setIpAddressPoolName(columnMapping.get("IP_ADDRESS_POOL_NAME") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("IP_ADDRESS_POOL_NAME"))) : null);
                        vlanManagement.setNatProfileName(columnMapping.get("NAT_PROFILE_NAME") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("NAT_PROFILE_NAME"))) : null);
                        vlanManagement.setRbInterfaceName(columnMapping.get("RB_INTERFACE_NAME") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("RB_INTERFACE_NAME"))) : null);
                        vlanManagement.setHttpRedirectUrl(columnMapping.get("HTTP_REDIRECT_URL") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("HTTP_REDIRECT_URL"))) : null);
                        vlanManagement.setFramedIpv6Prefix(columnMapping.get("FRAMED_IPV6_PREFIX") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("FRAMED_IPV6_PREFIX"))) : null);
                        vlanManagement.setDelegatedIpv6Prefix(columnMapping.get("DELEGATED_IPV6_PREFIX") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("DELEGATED_IPV6_PREFIX"))) : null);
                        vlanManagement.setFramedInterfaceId(columnMapping.get("FRAMED_INTERFACE_ID") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("FRAMED_INTERFACE_ID"))) : null);
                        vlanManagement.setFramedIpv6Pool(columnMapping.get("FRAMED_IPV6_POOL") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("FRAMED_IPV6_POOL"))) : null);
                        vlanManagement.setIpv6Option(columnMapping.get("IPV6_OPTION") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("IPV6_OPTION"))) : null);
                        vlanManagement.setIpv6Dns(columnMapping.get("IPV6_DNS") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("IPV6_DNS"))) : null);
                        vlanManagement.setDelegatedMaxPrefix(columnMapping.get("DELEGATED_MAX_PREFIX") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("DELEGATED_MAX_PREFIX"))) : null);
                        vlanManagement.setDelegatedIpv6Pool(columnMapping.get("DELEGATED_IPV6_POOL") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("DELEGATED_IPV6_POOL"))) : null);
                        vlanManagement.setSubProfile(columnMapping.get("SUB_PROFILE") != null ? dataFormatter.formatCellValue(row.getCell(columnMapping.get("SUB_PROFILE"))) : null);
                        vlanManagement.setPriority(columnMapping.get("PRIORITY") != null ? parseLong(dataFormatter.formatCellValue(row.getCell(columnMapping.get("PRIORITY")))) : null);
                        vlanManagement.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
                        vlanManagement.setCreatedOn(new Timestamp(new Date().getTime()));
                        vlanManagement.setLastModifiedOn(new Timestamp(new Date().getTime()));


                        vlanIdMap.put(vlanManagement.getVlanName(), vlanManagement);
                        vlans.add(vlanManagement);
                    }
                }
                processMappings(file, vlanIdMap, columnMapping);
            });
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception While reading file: " + ex.getMessage());
        }
        finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return vlanIdMap;
    }


    private Long parseLong(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void processMappings(MultipartFile file, Map<String, VLANManagement> vlanManagementMap, Map<String, Integer> columnMapping) {
        Workbook workbook = null;
        try (InputStream fis = file.getInputStream()) {
            workbook = new XSSFWorkbook(fis);
            workbook.forEach(sheet -> {
                DataFormatter dataFormatter = new DataFormatter();
                int index = 0;
                for (Row row : sheet) {
                    if (index == 0) {
                        for (Cell cell : row) {
                            columnMapping.put(dataFormatter.formatCellValue(cell), cell.getColumnIndex());
                        }
                        index++;
                        continue;
                    }
                    ;
                    List<VLANValidationMapping> vlanMappingList = new ArrayList<>();
                    if (columnMapping.get("VLAN_NAME_FOR_MAPPING") != null && row.getCell(columnMapping.get("VLAN_NAME_FOR_MAPPING")) != null && !dataFormatter.formatCellValue(row.getCell(columnMapping.get("VLAN_NAME_FOR_MAPPING"))).isEmpty()) {
                        String vlanName = dataFormatter.formatCellValue(row.getCell(columnMapping.get("VLAN_NAME_FOR_MAPPING")));
                        VLANValidationMapping mapping = new VLANValidationMapping();
                        VLANManagement vlanManagement = vlanManagementMap.get(vlanName);
                        //  mapping.setVlanId(vlanId);
                        mapping.setRadiusAttribute(dataFormatter.formatCellValue(row.getCell(columnMapping.get("PROFILE_ATTRIBUTE"))));
                        mapping.setRegex(dataFormatter.formatCellValue(row.getCell(columnMapping.get("REGEX"))));
                        vlanManagement.getMappingList().add(mapping);
                    }
                }
            });
        } catch (EncryptedDocumentException | IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Map<String, VLANManagement> readCsv(InputStream stream, Integer mvnoId) throws IOException {

        try {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.typedSchemaFor(VlanCsvDto.class)
                    .withColumnSeparator(CsvSchema.DEFAULT_COLUMN_SEPARATOR)
                    .withHeader()
                    .withColumnReordering(true)
                    .withArrayElementSeparator(CsvSchema.DEFAULT_ARRAY_ELEMENT_SEPARATOR);

            List<VlanCsvDto> rows = mapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES)
                    .readerFor(VlanCsvDto.class)
                    .with(CsvParser.Feature.TRIM_SPACES)
                    .with(schema)
                    .<VlanCsvDto>readValues(stream)
                    .readAll();

            // Validate required fields
            Optional<VlanCsvDto> invalidRow = rows.stream()
                    .filter(dto -> !RadiusUtils.notNullNotEmpty(dto.getNasIdentifier()) ||
                            !RadiusUtils.notNullNotEmpty(dto.getVlanName()) ||
                            !RadiusUtils.notNullNotEmpty(dto.getNasPortId4()) ||
                            !RadiusUtils.notNullNotEmpty(dto.getPriority() != null ? dto.getPriority().toString() : "")) // Assuming Priority field exists
                    .findFirst();

            if (invalidRow.isPresent()) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"file contains invalid data. VLAN_NAME, NAS_IDENTIFIER, NAS_PORT_ID_4, and PRIORITY must not be null or empty.",null);
            }

            Map<String, VLANManagement> vlanMap = rows.stream()
                    .filter(dto -> RadiusUtils.notNullNotEmpty(dto.getVlanName()))
                    .map((VlanCsvDto dto) -> convertToEntity(dto, mvnoId))
                    .collect(Collectors.toList())
                    .stream()
                    .collect(Collectors.toMap(VLANManagement::getVlanName, vlanManagement -> vlanManagement));


            rows.stream()
                    .filter(dto -> RadiusUtils.notNullNotEmpty(dto.getMappingName()))
                    .forEach((VlanCsvDto dto) -> processMappings(dto, vlanMap));

            return vlanMap;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Exception While reading file: " + ex.getMessage());
        }
    }

    private static void processMapping(List<VlanCsvDto> rows, Map<String, VLANManagement> vlan) {

        for (VlanCsvDto row : rows) {
            if (row.getMappingName() == null || row.getMappingName().isEmpty())
                continue;
            if (row.getRadiusAttribute() != null && row.getRegex() != null) {
                VLANManagement vlanManagement = vlan.get(row.getMappingName());
                VLANValidationMapping mapping = new VLANValidationMapping();
                mapping.setRadiusAttribute(row.getRadiusAttribute());
                mapping.setRegex(row.getRegex());
                vlanManagement.getMappingList().add(mapping);
            }
        }
    }

    private void processMappings(VlanCsvDto row, Map<String, VLANManagement> vlan) {
        VLANManagement vlanManagement = vlan.get(row.getMappingName());
        VLANValidationMapping mapping = new VLANValidationMapping();
        mapping.setRadiusAttribute(row.getRadiusAttribute());
        mapping.setRegex(row.getRegex());
        vlanManagement.getMappingList().add(mapping);
    }

    private VLANManagement convertToEntity(VlanCsvDto dto, Integer mvnoId) {
        VLANManagement vlan = new VLANManagement();
        vlan.setVlanName(dto.getVlanName());
        vlan.setNasType(dto.getNasType());
        vlan.setCircuitType(dto.getCircuitType());
        vlan.setNasIdentifier(dto.getNasIdentifier());
        vlan.setNasPortId1(dto.getNasPortId1());
        vlan.setNasPortId2(dto.getNasPortId2());
        vlan.setNasPortId3(dto.getNasPortId3());
        vlan.setNasPortId4(dto.getNasPortId4());
        vlan.setNasPortId5(dto.getNasPortId5());
        vlan.setCallingStationId(dto.getCallingStationId());
        vlan.setContextName(dto.getContextName());
        vlan.setFilterId(dto.getFilterId());
        vlan.setForwardPolicy(dto.getForwardPolicy());
        vlan.setHttpRedirectProfileName(dto.getHttpRedirectProfileName());
        vlan.setRateLimitRate(dto.getRateLimitRate());
        vlan.setRateLimitBurst(dto.getRateLimitBurst());
        vlan.setQosPolicingPolicyName(dto.getQosPolicingPolicyName());
        vlan.setQosMeteringPolicyName(dto.getQosMeteringPolicyName());
        vlan.setPppoeUrl(dto.getPppoeUrl());
        vlan.setPppDnsPrimary(dto.getPppDnsPrimary());
        vlan.setPppDnsSecondary(dto.getPppDnsSecondary());
        vlan.setPppNbnsPrimary(dto.getPppNbnsPrimary());
        vlan.setSessionTimeOut(dto.getSessionTimeOut());
        vlan.setIdleTimeOut(dto.getIdleTimeOut());
        vlan.setFramedIpAddress(dto.getFramedIpAddress());
        vlan.setRbDhcpMaxLeases(dto.getRbDhcpMaxLeases());
        vlan.setIpAddressPoolName(dto.getIpAddressPoolName());
        vlan.setNatProfileName(dto.getNatProfileName());
        vlan.setRbInterfaceName(dto.getRbInterfaceName());
        vlan.setHttpRedirectUrl(dto.getHttpRedirectUrl());
        vlan.setFramedIpv6Prefix(dto.getFramedIpv6Prefix());
        vlan.setDelegatedIpv6Prefix(dto.getDelegatedIpv6Prefix());
        vlan.setFramedInterfaceId(dto.getFramedInterfaceId());
        vlan.setFramedIpv6Pool(dto.getFramedIpv6Pool());
        vlan.setIpv6Option(dto.getIpv6Option());
        vlan.setIpv6Dns(dto.getIpv6Dns());
        vlan.setDelegatedMaxPrefix(dto.getDelegatedMaxPrefix());
        vlan.setDelegatedIpv6Pool(dto.getDelegatedIpv6Pool());
        vlan.setSubProfile(dto.getSubProfile());
        vlan.setPriority(dto.getPriority());
        vlan.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
        vlan.setCreatedOn(new Timestamp(new Date().getTime()));
        vlan.setLastModifiedOn(new Timestamp(new Date().getTime()));
        return vlan;
    }

    @Override
    public List<BulkVlanResponseDto> exportVlan(Integer mvnoId) {
        List<BulkVlanResponseDto> vlanManagements = vlanManagementRepository.findAllByMvno(mvnoId);
        return vlanManagements;
    }
}
