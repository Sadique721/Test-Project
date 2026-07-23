package com.savbill.partnermanagement.auditLog.service;


import com.savbill.partnermanagement.auditLog.Constants.AuditLogConstants;
import com.savbill.partnermanagement.auditLog.Constants.PageableResponse;
import com.savbill.partnermanagement.auditLog.model.AuditTrailResponseModel;
import com.savbill.partnermanagement.auditLog.model.CustomObject;
import com.savbill.partnermanagement.constants.CommonConstants;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.dto.PaginationRequestDTO;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class AuditTrailService {

    @Autowired
    Javers javers;

    public HashMap<String, Object> getAllAuditTrails(Changes changes, Integer pageIndex, Integer pageSize) {
        Integer mvnoId = getLoggedInMvnoId();
        // Add byObject to the response map
        List<AuditTrailResponseModel> byObjects = changes.groupByCommit().stream()
                .flatMap(byCommit -> {
                    String username = byCommit.getCommit().getAuthor(); // Get the username
                    LocalDateTime auditDate = byCommit.getCommit().getCommitDate();
                    return byCommit.groupByObject().stream().map(byObject -> {
                        AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();
                        auditTrail.setEntityName(byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1));
                        List<String> snapshotResponse = getSnapshot(changes, byObject);
                        auditTrail.setSnapshot(snapshotResponse.toString());
                        auditTrail.setAuthorUserName(username); // Include the username
                        auditTrail.setUpdatedOn(auditDate);
                        auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                        String entityType = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1);
                        String propertyValue = getPropertyValue(byObject.getGlobalId().value(), entityType, byCommit.getCommit().getId());
                        String specificName = getEntityNameFromProperty(propertyValue);
                        if(specificName.equals("")){
                            specificName = extractNamefromSnapShot(snapshotResponse.toString());
                            auditTrail.setEntityName(specificName);
                        }else{
                            auditTrail.setEntityName(specificName);
                        }
                        auditTrail.setEntityType(entityType);
                        // Extracting changetype from each property change object
                        if (!byObject.getPropertyChanges().isEmpty()) {
                            PropertyChange propertyChange = byObject.getPropertyChanges().get(0);
                            JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
                            String changeType = propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString();
                            String actionType = getActionType(changeType, propertyChangeObj, propertyChange);
                            auditTrail.setActionType(actionType);
                        }

                        auditTrail.setIpAddress(byCommit.getCommit().getProperties().get(AuditLogConstants.IP_ADDRESS));
                        auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get(AuditLogConstants.TEAMS));

                        return auditTrail;
                    });
                })
                .collect(Collectors.toList());
        // Create a response map to store total records and paginated audit trails
        HashMap<String, Object> response = new HashMap<>();
        response.put("byObject", byObjects);
        // Return the response map
        return response;
    }

    public HashMap<String, Object> getAllAuditSubmodule(Changes changes,String submodulename, Integer pageIndex, Integer pageSize) {
        // List to store the audit trail response models
        List<AuditTrailResponseModel> auditTrails = new ArrayList<>();

        // Iterate through changes grouped by commit
        changes.groupByCommit().forEach(byCommit -> {
            // Iterate through changes grouped by object
            byCommit.groupByObject().forEach(byObject -> {
                // Extract module name from the global ID type name
                String entityName = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1, byObject.getGlobalId().getTypeName().length());

                if (entityName.equals(submodulename)) {
                    // Get property value based on global ID, module name, and commit ID
                    String propertyValue = getPropertyValue(byObject.getGlobalId().value(), entityName, byCommit.getCommit().getId());

                    // Iterate through property changes of the object
                    byObject.getPropertyChanges().forEach(propertyChange -> {
                        // Convert property change to JSON object
                        JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();


                        // Check if the change type is not "ListChange"
                        if (!propertyChangeObj.get("changeType").getAsString().equals("ListChange")) {
                            // Create an audit trail response model
                            AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();

                            // Set various properties of the audit trail
                            auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                            auditTrail.setEntityName(entityName);
                            auditTrail.setSubmoduleName(submodulename);
                            auditTrail.setCommitId(new Double(byCommit.getCommit().getId().toString()).intValue());
                            auditTrail.setAuthorUserName(byCommit.getCommit().getAuthor());
                            auditTrail.setUpdatedOn(byCommit.getCommit().getCommitDate());
                            auditTrail.setAuthorUserId(byCommit.getCommit().getProperties().get("user_id") != null ? Integer.parseInt(byCommit.getCommit().getProperties().get("user_id")) : 0);
                            auditTrail.setIpAddress(byCommit.getCommit().getProperties().get("ip_address"));
                            auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get("Teams"));
                            auditTrail.setMvnoId(byCommit.getCommit().getProperties().get("mvnoId") != null ? Integer.parseInt(byCommit.getCommit().getProperties().get("mvnoId")) : 0);
                            auditTrail.setActionType(propertyChangeObj.get("changeType").getAsString());
                            auditTrail.setColumnName(propertyChange.getPropertyName());
                            auditTrail.setRecordId(new Double(propertyChange.getAffectedLocalId().toString()).intValue());
                            auditTrail.setRecordName(propertyValue == "null" ? "" : propertyValue);
                            auditTrail.setOldValue(propertyChange.getLeft() != null ? propertyChange.getLeft().toString() : "");
                            auditTrail.setNewValue(propertyChange.getRight() != null ? propertyChange.getRight().toString() : "");

                            // Add the audit trail to the list
                            auditTrails.add(auditTrail);
                        }
                    });
                }
            });
        });

        // Calculate the start and end index for pagination
        int startIndex = pageIndex * pageSize;
        int endIndex = Math.min(startIndex + pageSize, auditTrails.size());

        // Create a response map to store total records and paginated audit trails
        HashMap<String, Object> response = new HashMap<>();
        response.put(AuditLogConstants.TOTAL_RECORDS, auditTrails.size());
        response.put(AuditLogConstants.AUDITTRAILS, auditTrails.subList(startIndex, endIndex));

        // Return the response map
        return response;
    }
    public HashMap<String, Object> getAuditTraildByModule(String pkgName, Integer pageIndex, Integer pageSize) {
        List<AuditTrailResponseModel> auditTrails = new ArrayList<>();
        Changes changes = null;
        HashMap<String, Object> response = new HashMap<>();
        try {
            changes = javers.findChanges(QueryBuilder.byClass(Class.forName(pkgName)).withChildValueObjects().build());

//            changes.groupByCommit().forEach(byCommit -> {
//                byCommit.groupByObject().forEach(byObject -> {
//                    String moduleName = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1, byObject.getGlobalId().getTypeName().length());
//                    String propertyValue = getPropertyValue(byObject.getGlobalId().value(), moduleName, byCommit.getCommit().getId());
//                    byObject.getPropertyChanges().forEach(propertyChange -> {
//                        JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
//                        if (!propertyChangeObj.get("changeType").getAsString().equals("ListChange")) {
//                            AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();
//                            auditTrail.setModuleName(moduleName);
//                            auditTrail.setCommitId(new Double(byCommit.getCommit().getId().toString()).intValue());
//                            auditTrail.setAuthorUserName(byCommit.getCommit().getAuthor());
//                            auditTrail.setUpdatedOn(byCommit.getCommit().getCommitDate());
//                            auditTrail.setAuthorUserId(byCommit.getCommit().getProperties().get("user_id") != null ? Integer.parseInt(byCommit.getCommit().getProperties().get("user_id")) : 0);
//                            auditTrail.setIpAddress(byCommit.getCommit().getProperties().get("ip_address"));
//                            auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get("Teams"));
//                            auditTrail.setMvnoId(byCommit.getCommit().getProperties().get("mvnoId") != null ? Integer.parseInt(byCommit.getCommit().getProperties().get("mvnoId")) : 0);
//                            auditTrail.setActionType(propertyChangeObj.get("changeType").getAsString());
//                            auditTrail.setColumnName(propertyChange.getPropertyName());
//                            auditTrail.setRecordId(new Double(propertyChange.getAffectedLocalId().toString()).intValue());
//                            auditTrail.setRecordName(propertyValue == "null" ? "" : propertyValue);
//                            auditTrail.setOldValue(propertyChange.getLeft() != null ? propertyChange.getLeft().toString() : "");
//                            auditTrail.setNewValue(propertyChange.getRight() != null ? propertyChange.getRight().toString() : "");
//                            auditTrails.add(auditTrail);
//                        }
//                    });
//                });
//
//            });

            int startIndex = pageIndex * pageSize;
            int endIndex = Math.min(startIndex + pageSize, auditTrails.size());
            response.put("totalRecords", auditTrails.size());
            response.put("auditTrails", auditTrails.subList(startIndex, endIndex));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return response;
    }



    public HashMap<String, Object> getOldAuditTrails(Changes changes, String moduleName, Integer pageIndex, Integer pageSize) {
        // List to store the audit trail response models
        List<AuditTrailResponseModel> auditTrails = new ArrayList<>();

        // Iterate through changes grouped by commit
        changes.groupByCommit().forEach(byCommit -> {
            // Iterate through changes grouped by object
            byCommit.groupByObject().forEach(byObject -> {
                // Extract module name from the global ID type name
                String entityName = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1, byObject.getGlobalId().getTypeName().length());

                // Check if the module name matches the specified moduleName
                if (moduleName.equals(CommonConstants.MODULE.MODULE_PARTNER)) {
                    // Get property value based on global ID, module name, and commit ID
                    String propertyValue = getPropertyValue(byObject.getGlobalId().value(), entityName, byCommit.getCommit().getId());
                    // Iterate through property changes of the object
                    byObject.getPropertyChanges().forEach(propertyChange -> {
                        // Convert property change to JSON object
                        JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
                        // Get the change type
                        String changeType = propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString();

                        // Check if the change type is not "ListChange"
                        if (!propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString().equals(AuditLogConstants.LISTCHANGE)) {
                            // Create an audit trail response model
                            AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();

                            // Set various properties of the audit trail
                            auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                            auditTrail.setEntityName(entityName);
                            auditTrail.setCommitId(new Double(byCommit.getCommit().getId().toString()).intValue());
                            auditTrail.setAuthorUserName(byCommit.getCommit().getAuthor());
                            auditTrail.setUpdatedOn(byCommit.getCommit().getCommitDate());
                            auditTrail.setAuthorUserId(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID)) : 0);
                            auditTrail.setIpAddress(byCommit.getCommit().getProperties().get(AuditLogConstants.IP_ADDRESS));
                            auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get(AuditLogConstants.TEAMS));
                            auditTrail.setMvnoId(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID)) : 0);
                            String actionType = getActionType(changeType, propertyChangeObj,propertyChange);
                            auditTrail.setActionType(actionType);
                            auditTrail.setColumnName(propertyChange.getPropertyName());
                            auditTrail.setRecordId(new Double(propertyChange.getAffectedLocalId().toString()).intValue());
                            auditTrail.setRecordName(propertyValue == "null" ? "" : propertyValue);
                            auditTrail.setOldValue(propertyChange.getLeft() != null ? propertyChange.getLeft().toString() : "");
                            auditTrail.setNewValue(propertyChange.getRight() != null ? propertyChange.getRight().toString() : "");

                            // Add the audit trail to the list
                            auditTrails.add(auditTrail);
                        }
                    });
                }
            });
        });

        int totalRecords = auditTrails.size();
      // Calculate the start index for the current page
        int startIndex = Math.min(pageIndex * pageSize, totalRecords); // Start from the index of the latest record
        startIndex = Math.max(0, startIndex); // Ensure startIndex is within bounds

       // Calculate the end index for the current page
        int endIndex = totalRecords; // End at the total record count
        endIndex = Math.min(endIndex, totalRecords); // Ensure endIndex is within bounds

      // Create a response map to store total records and paginated audit trails
        HashMap<String, Object> response = new HashMap<>();
        response.put(AuditLogConstants.TOTAL_RECORDS, totalRecords);
        response.put(AuditLogConstants.AUDITTRAILS, auditTrails.subList(startIndex, endIndex));
        return response;
    }


    public HashMap<String, Object> getAuditTrailsByOperation(Changes changes,String operation, Integer pageIndex, Integer pageSize) {
        // List to store the audit trail response models
        List<AuditTrailResponseModel> auditTrails = new ArrayList<>();
        // Iterate through changes grouped by commit
        changes.groupByCommit().forEach(byCommit -> {
            // Iterate through changes grouped by object
            byCommit.groupByObject().forEach(byObject -> {
                // Extract module name from the global ID type name
                String entityName = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1, byObject.getGlobalId().getTypeName().length());

                // Get property value based on global ID, module name, and commit ID
                String propertyValue = getPropertyValue(byObject.getGlobalId().value(), entityName, byCommit.getCommit().getId());

                // Iterate through property changes of the object
                byObject.getPropertyChanges().forEach(propertyChange -> {
                    // Convert property change to JSON object
                    JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
                    // Get the change type
                    String changeType = propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString();

                    // Check if the change type is not "ListChange"
                    if (!propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString().equals(AuditLogConstants.LISTCHANGE)) {
                        // Create an audit trail response model
                        AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();

                        // Set various properties of the audit trail
                        auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                        auditTrail.setEntityName(entityName);
                        auditTrail.setCommitId(new Double(byCommit.getCommit().getId().toString()).intValue());
                        auditTrail.setAuthorUserName(byCommit.getCommit().getAuthor());
                        auditTrail.setUpdatedOn(byCommit.getCommit().getCommitDate());
                        auditTrail.setAuthorUserId(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID)) : 0);
                        auditTrail.setIpAddress(byCommit.getCommit().getProperties().get(AuditLogConstants.IP_ADDRESS));
                        auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get(AuditLogConstants.TEAMS));
                        auditTrail.setMvnoId(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID)) : 0);
                        String actionType = getActionType(changeType, propertyChangeObj,propertyChange);
                        auditTrail.setActionType(actionType);
                        auditTrail.setColumnName(propertyChange.getPropertyName());
                        auditTrail.setRecordId(new Double(propertyChange.getAffectedLocalId().toString()).intValue());
                        auditTrail.setRecordName(propertyValue == "null" ? "" : propertyValue);
                        auditTrail.setOldValue(propertyChange.getLeft() != null ? propertyChange.getLeft().toString() : "");
                        auditTrail.setNewValue(propertyChange.getRight() != null ? propertyChange.getRight().toString() : "");
                        // Add the audit trail to the list
                        auditTrails.add(auditTrail);
                    }
                });
            });
        });

        // Calculate the start and end index for pagination
        int startIndex = pageIndex * pageSize;
        int endIndex = Math.min(startIndex + pageSize, auditTrails.size());

// Filter audit trails by action type
        List<AuditTrailResponseModel> filteredAuditTrails = auditTrails.stream()
                .filter(trail -> trail.getActionType().equalsIgnoreCase(operation))
                .collect(Collectors.toList());

        // Create a response map to store total records and paginated audit trails
        HashMap<String, Object> response = new HashMap<>();
        response.put(AuditLogConstants.TOTAL_RECORDS, filteredAuditTrails.size());
        response.put(AuditLogConstants.AUDITTRAILS, filteredAuditTrails.subList(startIndex, endIndex));

        // Return the response map
        return response;
    }


    private String getPropertyValue(String globalId, String moduleName, CommitId commitId) {
        List<CdoSnapshot> snapshots = null;
        String[] propertyValue = new String[1];
        String[] split = globalId.split("\\/");
        String localId = "";
        String typeName = "";
        if (split.length == 2) {
            typeName = split[0].toString();
            localId = split[1].toString();
        }
        String propertyName = getPropertyName(moduleName);
        try {

            int parsedLocalId = Integer.parseInt(localId);
            QueryBuilder queryBuilder = QueryBuilder.byInstanceId(parsedLocalId, Class.forName(typeName)).withCommitId(commitId);
            snapshots = javers.findSnapshots(queryBuilder.build());
//            for (CdoSnapshot cdoSnapshot : snapshots) {
//                Object value = cdoSnapshot.getState().getPropertyValue(propertyName);
//                if (value != null) {
//                    propertyValue[0] = String.valueOf(value);
//                    // If a non-null value is found, break the loop
//                    break;
//                }
//            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return snapshots.toString();
    }

    private String getPropertyName(String moduleName) {
        String propertyName = "";
        switch (moduleName) {
            case "Country":
                propertyName = "country";
                break;
            case "CreditDocument":
                propertyName = "creditdocumentno";
                break;
            case "Customers":
                propertyName = "username";
                break;
            default:
                propertyName = "name";
        }
        return propertyName;
    }


    private String getActionType(String changeType, JsonObject propertyChangeObj, PropertyChange propertyChange) {
        switch (changeType) {
            case AuditLogConstants.INITIAL_VALUE_CHANGE:
                return AuditLogConstants.CREATE;
            case AuditLogConstants.VALUE_CHANGE:
                if (AuditLogConstants.IS_DELETE.equals(propertyChange.getPropertyName()) && Boolean.TRUE.equals(propertyChange.getRight()) || AuditLogConstants.IS_DELETED.equals(propertyChange.getPropertyName()) && Boolean.TRUE.equals(propertyChange.getRight())) {
                    return AuditLogConstants.DELETE;
                }else if (propertyChange.getRight() == null && propertyChange.getLeft() != null) {
                    return AuditLogConstants.DELETE;
                }else {
                    return AuditLogConstants.UPDATE;
                }
            case AuditLogConstants.REFERENCE_CHANGE:
                if (propertyChange.getRight() != null && propertyChange.getLeft() != null) {
                    return AuditLogConstants.UPDATE;
                } else if ((AuditLogConstants.IS_DELETE.equals(propertyChange.getPropertyName()) && propertyChange.getRight() == null && propertyChange.getLeft() != null)||(AuditLogConstants.IS_DELETE.equals(propertyChange.getPropertyName()) && propertyChange.getRight() != null && propertyChange.getLeft() == null)) {
                    return AuditLogConstants.DELETE;
                }else if(propertyChange.getLeft()!=null && propertyChange.getRight()== null){
                    return AuditLogConstants.DELETE;
                }else {
                    return AuditLogConstants.CREATE;
                }
            case AuditLogConstants.TERMINALVALUE_CHANGE:
                return AuditLogConstants.DELETE;
            default:
                return null;

        }

    }

    public List<String> getSnapshot(Changes changes, ChangesByObject byObject) {
        HashMap<String, Object> snapshotResponse = new HashMap<>();
        List<String> snapshots = new ArrayList<>();
        StringBuilder combinedDiffBuilder = new StringBuilder(); // StringBuilder to construct the combined diff
        Javers javers = JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .build();
        byObject.getPropertyChanges().forEach(propertyChange -> {
            JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
            String changeType = propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString();

            if (!changeType.equals(AuditLogConstants.LISTCHANGE)) {
                String entityName = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1);
                AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();
                auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                auditTrail.setEntityName(entityName);
                Object oldObject = propertyChange.getLeft();
                Object newObject = propertyChange.getRight();

                String oldStringValue = convertToString(oldObject);
                String newStringValue = convertToString(newObject);
                CustomObject oldCustomObject = new CustomObject(oldStringValue);
                CustomObject newCustomObject = new CustomObject(newStringValue);
                Diff diff = javers.compare(oldCustomObject, newCustomObject);
                //assertThat(diff.getChanges()).isNotEmpty();
                combinedDiffBuilder.append(diff.prettyPrint().replaceAll("com\\.savbill.*?(?=\\s|$)", entityName)).append("\n");
            }

        });
        snapshots.add(combinedDiffBuilder.toString().trim());
//        snapshotResponse.put("snapshot", snapshots);
        return snapshots;
    }
    private String convertToString(Object object) {
        return String.valueOf(object);
    }
    public PageableResponse<GenericDataDTO> searchAuditTrailsByModule(Changes changes, String moduleName, String entityName, PaginationRequestDTO paginationRequestDTO, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Integer currentMvnoId = getLoggedInMvnoId();
        try {
            if (paginationRequestDTO.getPage() > 0) {
                paginationRequestDTO.setPage(paginationRequestDTO.getPage() - 1);
            }
            PageableResponse<GenericDataDTO> pageableResponse = new PageableResponse<>();
            Pageable pageable = PageRequest.of(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createDate"));
            // List to store the audit trail response models
            List<AuditTrailResponseModel> auditTrails = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            // Iterate through changes grouped by commit
            if(startDateTime!=null & endDateTime!=null) {
                changes.groupByCommit().stream().filter(changesByCommit -> changesByCommit.getCommit().getCommitDate().isAfter(startDateTime) && changesByCommit.getCommit().getCommitDate().isBefore(endDateTime)).forEach(byCommit -> {
                    // Iterate through changes grouped by object
                    byCommit.groupByObject().forEach(byObject -> {
                        // Extract module name from the global ID type name
                        String entityType = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1, byObject.getGlobalId().getTypeName().length());
                        byObject.getPropertyChanges().forEach(propertyChange -> {
//                        // Convert property change to JSON object
                            JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
                            // Check if the module name matches the specified moduleName
                            if (moduleName.equalsIgnoreCase(CommonConstants.MODULE.MODULE_PARTNER)) {
                                // Get property value based on global ID, module name, and commit ID
                                String propertyValue = getPropertyValue(byObject.getGlobalId().value(), entityType, byCommit.getCommit().getId());
                                String changeType = propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString();

                                String value = extractStateValue(propertyValue);
                                String[] pairs = value.split(",\\s*");
                                String dynamicValue = "state:(\\{.*?\\})";
                                String performMatching = performMatching(value, dynamicValue);
                                // Iterate over the pairs and extract key-value components
                                for (String pair : pairs) {
                                    String[] keyValue = pair.split(":");
                                    if (keyValue.length == 2) {
                                        String key = keyValue[0].trim();
                                        String value1 = keyValue[1].trim();
                                        // Process or use key and value as needed
                                        int commitMvnoId = byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID)) : 0;
                                        if (commitMvnoId == currentMvnoId)
                                            if (byCommit.getCommit().getCommitDate().isAfter(startDateTime) && byCommit.getCommit().getCommitDate().isBefore(endDateTime)) {
                                                AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();

                                                // Set various properties of the audit trail
                                                auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                                                String specificName = getEntityNameFromProperty(propertyValue);
                                                List<String> snapshotResponse = getSnapshot(changes, byObject);
                                                if (specificName.equals("")) {
                                                    specificName = extractNamefromSnapShot(snapshotResponse.toString());
                                                    auditTrail.setEntityName(specificName);
                                                } else {
                                                    auditTrail.setEntityName(specificName);
                                                }
                                                auditTrail.setEntityType(entityType);
                                                auditTrail.setCommitId(new Double(byCommit.getCommit().getId().toString()).intValue());
                                                auditTrail.setAuthorUserName(byCommit.getCommit().getAuthor());
                                                auditTrail.setUpdatedOn(byCommit.getCommit().getCommitDate());
                                                auditTrail.setAuthorUserId(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID)) : 0);
                                                auditTrail.setIpAddress(byCommit.getCommit().getProperties().get(AuditLogConstants.IP_ADDRESS));
                                                auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get(AuditLogConstants.TEAMS));
                                                auditTrail.setMvnoId(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID)) : 0);
                                                String actionType = getActionType(changeType, propertyChangeObj, propertyChange);
                                                auditTrail.setActionType(actionType);
                                                auditTrail.setColumnName(propertyChange.getPropertyName());
                                                auditTrail.setRecordId(new Double(propertyChange.getAffectedLocalId().toString()).intValue());
                                                auditTrail.setRecordName(propertyValue == "null" ? "" : propertyValue);

                                                //                                System.out.println("Snapshot State: " + snapshot.getState().getPropertyValue("name"));
                                                auditTrail.setOldValue(propertyChange.getLeft() != null ? propertyChange.getLeft().toString() : "");
                                                auditTrail.setNewValue(propertyChange.getRight() != null ? propertyChange.getRight().toString() : "");


                                                // Add the audit trail to the list
                                                auditTrails.add(auditTrail);
                                                System.out.println(auditTrails);

                                            }
                                    }
                                }
                                //Removin duplicate entry from the list before returning it
                                Set<AuditTrailResponseModel> set = new HashSet<AuditTrailResponseModel>(auditTrails);
                                auditTrails.clear();
                                auditTrails.addAll(set);
                            }
                        });
                    });


                });

            }else{
                changes.groupByCommit().stream().forEach(byCommit -> {
                    // Iterate through changes grouped by object
                    byCommit.groupByObject().forEach(byObject -> {
                        // Extract module name from the global ID type name
                        String entityType = byObject.getGlobalId().getTypeName().substring(byObject.getGlobalId().getTypeName().lastIndexOf(".") + 1, byObject.getGlobalId().getTypeName().length());
                        byObject.getPropertyChanges().forEach(propertyChange -> {
//                        // Convert property change to JSON object
                            JsonObject propertyChangeObj = javers.getJsonConverter().toJsonElement(propertyChange).getAsJsonObject();
                            // Check if the module name matches the specified moduleName
                            if (moduleName.equalsIgnoreCase(CommonConstants.MODULE.MODULE_PARTNER)) {
                                // Get property value based on global ID, module name, and commit ID
                                String propertyValue = getPropertyValue(byObject.getGlobalId().value(), entityType, byCommit.getCommit().getId());
                                String changeType = propertyChangeObj.get(AuditLogConstants.CHANGETYPE).getAsString();

                                String value = extractStateValue(propertyValue);
                                String[] pairs = value.split(",\\s*");
                                String dynamicValue = "state:(\\{.*?\\})";
                                String performMatching = performMatching(value, dynamicValue);
                                // Iterate over the pairs and extract key-value components
                                for (String pair : pairs) {
                                    String[] keyValue = pair.split(":");
                                    if (keyValue.length == 2) {
                                        String key = keyValue[0].trim();
                                        String value1 = keyValue[1].trim();
                                        // Process or use key and value as needed
                                        int commitMvnoId = byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID)) : 0;
                                        if (commitMvnoId == currentMvnoId)
                                            if (key.contains("name") && value1.equalsIgnoreCase(entityName)) {
                                                AuditTrailResponseModel auditTrail = new AuditTrailResponseModel();

                                                // Set various properties of the audit trail
                                                auditTrail.setModuleName(CommonConstants.MODULE.MODULE_PARTNER);
                                                String specificName = getEntityNameFromProperty(propertyValue);
                                                List<String> snapshotResponse = getSnapshot(changes, byObject);
                                                if (specificName.equals("")) {
                                                    specificName = extractNamefromSnapShot(snapshotResponse.toString());
                                                    auditTrail.setEntityName(specificName);
                                                } else {
                                                    auditTrail.setEntityName(specificName);
                                                }
                                                auditTrail.setEntityType(entityType);
                                                auditTrail.setCommitId(new Double(byCommit.getCommit().getId().toString()).intValue());
                                                auditTrail.setAuthorUserName(byCommit.getCommit().getAuthor());
                                                auditTrail.setUpdatedOn(byCommit.getCommit().getCommitDate());
                                                auditTrail.setAuthorUserId(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.USER_ID)) : 0);
                                                auditTrail.setIpAddress(byCommit.getCommit().getProperties().get(AuditLogConstants.IP_ADDRESS));
                                                auditTrail.setAuthorUserTeams(byCommit.getCommit().getProperties().get(AuditLogConstants.TEAMS));
                                                auditTrail.setMvnoId(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID) != null ? Integer.parseInt(byCommit.getCommit().getProperties().get(AuditLogConstants.MVNOID)) : 0);
                                                String actionType = getActionType(changeType, propertyChangeObj, propertyChange);
                                                auditTrail.setActionType(actionType);
                                                auditTrail.setColumnName(propertyChange.getPropertyName());
                                                auditTrail.setRecordId(new Double(propertyChange.getAffectedLocalId().toString()).intValue());
                                                auditTrail.setRecordName(propertyValue == "null" ? "" : propertyValue);

                                                //                                System.out.println("Snapshot State: " + snapshot.getState().getPropertyValue("name"));
                                                auditTrail.setOldValue(propertyChange.getLeft() != null ? propertyChange.getLeft().toString() : "");
                                                auditTrail.setNewValue(propertyChange.getRight() != null ? propertyChange.getRight().toString() : "");

                                                // Add the audit trail to the list
                                                auditTrails.add(auditTrail);
                                                System.out.println(auditTrails);

                                            }

                                    }
                                }
                                //Removin duplicate entry from the list before returning it
                                Set<AuditTrailResponseModel> set = new HashSet<AuditTrailResponseModel>(auditTrails);
                                auditTrails.clear();
                                auditTrails.addAll(set);
                            }
                        });
                    });


                });
            }

            int startIndex = paginationRequestDTO.getPage() * paginationRequestDTO.getPageSize();
            int endIndex = Math.min(startIndex + paginationRequestDTO.getPageSize(), auditTrails.size());

            GenericDataDTO genericDataDTO = new GenericDataDTO();

            genericDataDTO.setDataList(auditTrails);
            genericDataDTO.setResponseCode(200);
            genericDataDTO.setResponseMessage("Search Successfully");


            return pageableResponse.convert(new PageImpl<>(genericDataDTO.getDataList().subList(startIndex, endIndex), PageRequest.of(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize()), genericDataDTO.getDataList().size()));
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        return null;
    }
    public int getLoggedInMvnoId() {
        int loggedInMvnoId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            loggedInMvnoId = -1;
        }
        return loggedInMvnoId;
    }
    public String performMatching(String input, String dynamicValue) {
        // Create a regex pattern using the dynamic value
        Pattern pattern = Pattern.compile(dynamicValue);

        // Create a matcher using the input string
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            return input;
        }
        return input;
    }








    public String getEntityNameFromProperty(String propertyValue) {
        String value = extractStateValue(propertyValue);

        String[] pairs = value.split(",\\s*");
        String dynamicValue = "state:(\\{.*?\\})";
        String performMatching = performMatching(value, dynamicValue);
        // Iterate over the pairs and extract key-value components
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value1 = keyValue[1].trim();
                // Process or use key and value as needed
                if ((key.contains("name")||key.equals("pincode")) ){
                    return value1;
                }
            }
        }
        return  "";
    }

    private static String extractNamefromSnapShot(String responseString) {
        // Define the pattern to match the 'name' changed: 'value' -> 'null' part
        Pattern pattern = Pattern.compile("'name' changed: '(.*?)' -> 'null'");

        // Create a matcher with the given response string
        Matcher matcher = pattern.matcher(responseString);

        // Check if the pattern is found in the response string
        if (matcher.find()) {
            // Extract and return the matched name
            return matcher.group(1);
        } else {
            // Return null or an appropriate value if the pattern is not found
            return null;
        }
    }


public String extractStateValue(String input) {
    // Find the index of the substring "state:{"
    int startIndex = input.indexOf("state:{");

    // If "state:{" is found, find the end index of the state value
    if (startIndex != -1) {
        // Find the index of the closing curly brace "}" after "state:{"
        int endIndex = input.indexOf("}", startIndex);
        if (endIndex != -1) {
            // Extract the state value substring
            return input.substring(startIndex + "state:{".length(), endIndex);
        }
    }
    // If "state:{" or "}" is not found, return null or an appropriate message
    return null;
}

}
