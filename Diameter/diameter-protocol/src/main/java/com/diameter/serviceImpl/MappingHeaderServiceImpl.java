package com.diameter.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diameter.model.AvpCondition;
import com.diameter.model.MappingDetail;
import com.diameter.model.MappingHeader;
import com.diameter.repository.MappingHeaderRepository;
import com.diameter.service.MappingHeaderService;

@Service
public class MappingHeaderServiceImpl implements MappingHeaderService {

    private static final Logger logger = LoggerFactory.getLogger(MappingHeaderServiceImpl.class);

    @Autowired
    private MappingHeaderRepository repository;

    @Override
    public MappingHeader createOrUpdateMapping(MappingHeader mappingHeader) throws ValidationException {
        logger.info("Create/Update request received for mapping: {}", mappingHeader.getRequestType());

        /* ================= BASIC VALIDATION ================= */
        if (mappingHeader.getRequestType() == null || mappingHeader.getRequestType().trim().isEmpty()) {
            throw new ValidationException("requestType is mandatory.");
        }
        if (mappingHeader.getResponseType() == null || mappingHeader.getResponseType().trim().isEmpty()) {
            throw new ValidationException("responseType is mandatory.");
        }

        /* ================= NORMALIZE CORE FIELDS ================= */
        // 🔹 Normalize existing requestType (handles old DB values safely)
        String normalizedRequestType = normalizeCcRequestType(mappingHeader.getCcRequestType());

        String ccRequestType = normalizeCcRequestType(mappingHeader.getCcRequestType());

        // 🔹 Normalize optional CC request type (INITIAL / UPDATE / TERMINATE)
        mappingHeader.setCcRequestType(
                normalizeCcRequestType(mappingHeader.getCcRequestType())
        );

        // 🔹 Normalize peer list (List<String> → CSV String)
        String peerValue = joinPeers(mappingHeader.getPeer());
        mappingHeader.setPeer(
                peerValue != null ? List.of(peerValue.split(",")) : null
        );

        /* ================= CHECK EXISTING RECORD ================= */
        List<MappingHeader> existingList = repository.findByRequestAndResponseType(normalizedRequestType, mappingHeader.getResponseType(), mappingHeader.getApplication(), mappingHeader.getVendorId(), ccRequestType);

        MappingHeader existing = (existingList == null || existingList.isEmpty()) ? null : existingList.get(0);

        boolean isNew = (existing == null);
        MappingHeader response;

        /* ================= CREATE / UPDATE ================= */
        if (isNew) {
            String newId = UUID.randomUUID().toString();
            mappingHeader.setId(newId);
            mappingHeader.setCreatedDate(LocalDateTime.now());

            repository.save(mappingHeader);

            logger.info("Inserted new mapping header with ID: {}", newId);
            response = mappingHeader;

        } else {
            mappingHeader.setId(existing.getId());
            mappingHeader.setCreatedDate(existing.getCreatedDate()); // preserve
            mappingHeader.setModifiedDate(LocalDateTime.now());

            repository.update(mappingHeader);

            logger.info("Updated existing mapping header ID: {}", existing.getId());
            response = mappingHeader;

            // 🔹 Remove old details before re-inserting
            repository.deleteMappingDetailsByHeaderId(existing.getId());
        }

        /* ================= DETAILS HANDLING ================= */

        validateMandatoryAVPs(mappingHeader);
        saveMappingDetails(mappingHeader);
        
        //Condition
        saveAvpConditionDetails(mappingHeader);

        return response;
    }


    @Override
    public MappingHeader updateMapping(MappingHeader mappingHeader) throws ValidationException {
        MappingHeader existing = repository.findById(mappingHeader.getId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapping not found with ID: " + mappingHeader.getId());
        }

        mappingHeader.setModifiedDate(LocalDateTime.now());
        repository.update(mappingHeader);
        logger.info("Updated mapping header ID: {}", mappingHeader.getId());

        repository.deleteMappingDetailsByHeaderId(mappingHeader.getId());
        repository.deleteMappingConditionsByHeaderId(mappingHeader.getId());
        validateMandatoryAVPs(mappingHeader);
        saveMappingDetails(mappingHeader);
        saveAvpConditionDetails(mappingHeader);

        return mappingHeader;
    }

    private void validateMandatoryAVPs(MappingHeader mappingHeader) throws ValidationException {
        if (mappingHeader.getDetails() == null || mappingHeader.getDetails().isEmpty()) {
            throw new ValidationException("At least one AVP must be defined for this command.");
        }

        for (MappingDetail detail : mappingHeader.getDetails()) {
            // Validate mandatory flag and presence
        	String req = detail.getRequestAvp();
            String resp = detail.getResponseAvp();

            boolean hasReq = req != null && !req.trim().isEmpty();
            boolean hasResp = resp != null && !resp.trim().isEmpty();

            if (detail.isMandatory()) {
                // When mandatory, requestAvp must be present
                if (!hasReq) {
                    throw new ValidationException("Mandatory mapping detail missing request AVP (requestAvp required) for detail with responseAvp: " + resp);
                }
            } else {
                // When not mandatory, at least one of requestAvp or responseAvp must be present
                if (!hasReq && !hasResp) {
                    throw new ValidationException("Non-mandatory mapping detail must have either requestAvp or responseAvp defined.");
                }
            }
        }
    }

    private void saveMappingDetails(MappingHeader mappingHeader) {
        if (mappingHeader.getDetails() == null || mappingHeader.getDetails().isEmpty()) return;

        for (MappingDetail detail : mappingHeader.getDetails()) {
            if (detail.getId() == null || detail.getId().trim().isEmpty()) {
                detail.setId(UUID.randomUUID().toString());
            }
            detail.setMappingHeaderId(mappingHeader.getId());
            detail.setCreatedDate(LocalDateTime.now());
            repository.saveMappingDetail(detail);
            logger.info("Saved mapping detail [{}] for header [{}]", detail.getRequestAvp(), mappingHeader.getResponseType());
        }
    }
    
    private void saveAvpConditionDetails(MappingHeader mappingHeader) {
        if (mappingHeader.getAvpConditions() == null || mappingHeader.getAvpConditions().isEmpty()) return;

        for (AvpCondition avpCondition: mappingHeader.getAvpConditions()) {
            if (avpCondition.getId() == null || avpCondition.getId().trim().isEmpty()) {
            	avpCondition.setId(UUID.randomUUID().toString());
            }
            avpCondition.setMappingHeaderId(mappingHeader.getId());
            avpCondition.setCreatedDate(LocalDateTime.now());
            repository.saveMappingCondition(avpCondition);
            logger.info("Saved mapping detail [{}] for header [{}]", avpCondition.getAvpCode(), avpCondition.getVendorId());
        }
    }

    @Override
    public void deleteMapping(String id) {
        MappingHeader header = repository.findById(id);
        if (header == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapping not found with ID: " + id);
        }
        repository.deleteMappingConditionsByHeaderId(id);
        repository.deleteMappingDetailsByHeaderId(id);
        repository.deleteById(id);
        logger.info("Deleted mapping header and details for ID: {}", id);
    }

    @Override
    public List<MappingHeader> getAllMappings() {
        List<MappingHeader> headers = repository.findAll();

        for (MappingHeader header : headers) {
            List<MappingDetail> details =
                    repository.findDetailsByHeaderId(header.getId());
            header.setDetails(details);
            header.setAvpConditions(repository.findConditionsByHeaderId(header.getId()));
        }

        return headers;
    }

    @Override
    public MappingHeader getMappingById(String id) {
        MappingHeader header = repository.findById(id);
        if (header == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapping not found with ID: " + id);
        }
        List<MappingDetail> details = repository.findDetailsByHeaderId(id);
        header.setDetails(details);
        header.setAvpConditions(repository.findConditionsByHeaderId(header.getId()));
        return header;
    }

    @Override
    public MappingHeader getMappingByCommandName(String commandName) {
        MappingHeader header = repository.findByCommandCode(commandName);
        if (header == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mapping not found with command name: " + commandName);
        }
        List<MappingDetail> details = repository.findDetailsByHeaderId(header.getId());
        header.setDetails(details);
        header.setAvpConditions(repository.findConditionsByHeaderId(header.getId()));
        return header;
    }

    @Override
    public List<MappingHeader> getMappingsByRequestAndResponseType(
            String requestType,
            String responseType,
            String application,
            Integer vendorId,
            String ccRequestType) {

        logger.info("Fetching mappings by requestType={}, responseType={}, application={}, vendorId={}, ccRequestType={}",
                requestType, responseType, application, vendorId, ccRequestType);

        List<MappingHeader> headers =
                repository.findByRequestAndResponseType(
                        requestType, responseType, application, vendorId, ccRequestType
                );

        if (headers == null || headers.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Mapping not found with given filters"
            );
        }

        for (MappingHeader header : headers) {
            header.setDetails(
                    repository.findDetailsByHeaderId(header.getId())
            );
            header.setAvpConditions(repository.findConditionsByHeaderId(header.getId()));
        }

        return headers;
    }

    private String normalizeCcRequestType(String ccRequestType) throws ValidationException {

        if (ccRequestType == null || ccRequestType.trim().isEmpty()) {
            return null; // optional field
        }
        String value = ccRequestType.trim().toUpperCase();
        if (!List.of("INITIAL_REQUEST", "UPDATE_REQUEST", "TERMINATION_REQUEST","EVENT_REQUEST").contains(value)) {
            throw new ValidationException("Invalid ccRequestType. Allowed values: INITIAL_REQUEST, UPDATE_REQUEST, TERMINATION_REQUEST,EVENT_REQUEST");
        }
        return value;
    }

    private String joinPeers(List<String> peers) {
        if (peers == null || peers.isEmpty()) {
            return null;
        }
        return peers.stream().map(String::trim).filter(p -> !p.isEmpty()).distinct().collect(Collectors.joining(","));
    }
}