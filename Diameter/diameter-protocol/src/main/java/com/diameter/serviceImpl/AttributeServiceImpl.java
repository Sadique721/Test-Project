package com.diameter.serviceImpl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diameter.model.Attribute;
import com.diameter.model.Vendor;
import com.diameter.repository.AttributeRepository;
import com.diameter.service.AttributeService;

@Service
public class AttributeServiceImpl implements AttributeService {

	private static final Logger logger = LoggerFactory.getLogger(AttributeServiceImpl.class);
	
    @Autowired
    private AttributeRepository repository;

    @Override
    public Attribute createOrUpdateAttribute(Attribute attribute) throws ValidationException {
    	logger.info("Create/Update request received for attribute: {}", attribute.getName());
		/*
		 * Attribute attributeResponse = null; Attribute existingAttribute =
		 * repository.findByName(attribute.getName());
		 * 
		 * if (existingAttribute == null) { String newId = UUID.randomUUID().toString();
		 * attribute.setId(newId); attribute.setCreatedDate(LocalDateTime.now());
		 * logger.info("Inserted new attribute with ID: {}", newId); attributeResponse =
		 * repository.saveAttribute(attribute); } else {
		 * attribute.setId(existingAttribute.getId());
		 * logger.info("Attribute already exists with ID: {}, skipping insert.",
		 * attribute.getId()); }
		 * 
		 * 
		 * String[] vendorIds = attribute.getVendorId().split(","); boolean
		 * allMappingsExist = true; StringBuilder existingVendors = new StringBuilder();
		 * 
		 * // Check if all mappings exist for (String vendorId : vendorIds) { String
		 * trimmed = vendorId.trim(); if (!repository.mappingExists(attribute.getId(),
		 * trimmed)) { allMappingsExist = false; break; } else { if
		 * (existingVendors.length() > 0) { existingVendors.append(", "); }
		 * existingVendors.append(trimmed); } }
		 * 
		 * // If all mappings exist, throw validation exception if (allMappingsExist) {
		 * logger.warn("Mappings already exist for attribute [{}] and all vendors [{}]",
		 * attribute.getId(), existingVendors); throw new
		 * ValidationException("Attribute mappings already exist for vendors: " +
		 * existingVendors); }
		 * 
		 * if( existingAttribute != null && allMappingsExist) {
		 * logger.info("Attribute with all the vendors' mappings already exists",
		 * attribute.getId()); throw new
		 * ValidationException("Attribute with all the vendors' mappings already exists: "
		 * + attribute.getId()); }
		 * 
		 * //String[] vendorIds = attribute.getVendorId().split(","); for (String
		 * vendorId : vendorIds) { String trimmed = vendorId.trim(); if
		 * (!repository.mappingExists(attribute.getId(), trimmed)) { String mappingId =
		 * UUID.randomUUID().toString(); repository.saveAttributeMapping(mappingId,
		 * attribute.getId(), trimmed);
		 * logger.info("Created mapping for attribute [{}] with vendor [{}]",
		 * attribute.getId(), trimmed); } } return attributeResponse;
		 */
    	Attribute attributeResponse = null;
    	Attribute existingAttribute = repository.findByName(attribute.getName());
        boolean isNewAttribute = existingAttribute == null;

        if (isNewAttribute) {
            // Create new attribute
            String newId = UUID.randomUUID().toString();
            attribute.setId(newId);
            attribute.setCreatedDate(LocalDateTime.now());
            attributeResponse = repository.saveAttribute(attribute);
            logger.info("Inserted new attribute with ID: {}", newId);
        } else {
            // Use existing attribute ID
            attribute.setId(existingAttribute.getId());
            attributeResponse = attribute;
            logger.info("Attribute already exists with ID: {}, skipping insert.", attribute.getId());
        }

        // Collect vendor IDs and track which mappings already exist
        String[] vendorIds = attribute.getVendorId().split(",");
        List<String> existingMappings = new ArrayList<>();
        List<String> newMappings = new ArrayList<>();
        List<String> vendorList = Arrays.stream(vendorIds)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        validateVendorIds(vendorList);
        for (String vendorId : vendorList) {
            String trimmed = vendorId.trim();
            if (repository.mappingExists(attribute.getId(), trimmed)) {
                existingMappings.add(trimmed);
            } else {
                newMappings.add(trimmed);
            }
        }

        // If attribute exists and all mappings exist, reject the request
        if (!isNewAttribute && newMappings.isEmpty()) {
            logger.warn("All mappings already exist for attribute [{}] and vendors [{}]", attribute.getId(), String.join(", ", existingMappings));
            throw new ValidationException("Attribute mappings already exist for all specified vendors: " + String.join(", ", existingMappings));
        }

        // Insert only missing mappings
        for (String vendorId : newMappings) {
            String mappingId = UUID.randomUUID().toString();
            repository.saveAttributeMapping(mappingId, attribute.getId(), vendorId);
            logger.info("Created mapping for attribute [{}] with vendor [{}]", attribute.getId(), vendorId);
        }

        return attributeResponse;
    }

    @Override
    public Attribute updateAttribute(Attribute attribute) throws ValidationException {
        // Validate the attribute exists
        Attribute existing = repository.findById(attribute.getId());
        if (existing == null) {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attribute not found with ID: " + attribute.getId());
        }

        // Validate vendor IDs are provided
        if (attribute.getVendorId() == null || attribute.getVendorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor ID(s) must be provided for attribute update.");
        }

        String[] vendorIds = attribute.getVendorId().split(",");
        List<String> vendorList = Arrays.stream(vendorIds)
                                        .map(String::trim)
                                        .filter(s -> !s.isEmpty())
                                        .toList();

        if (vendorList.isEmpty()) {
            throw new IllegalArgumentException("Vendor ID(s) must not be empty after trimming.");
        }
        
        validateVendorIds(vendorList);

        // Update the basic attribute details
        attribute.setModifiedDate(LocalDateTime.now());
        repository.updateAttribute(attribute);
        logger.info("Updated attribute details for ID: {}", attribute.getId());

        // Remove old mappings
        repository.deleteAttributeMappings(attribute.getId());

        // Insert new mappings
        for (String vendorId : vendorList) {
            String mappingId = UUID.randomUUID().toString();
            repository.saveAttributeMapping(mappingId, attribute.getId(), vendorId);
            logger.info("Created mapping for attribute [{}] with vendor [{}]", attribute.getId(), vendorId);
        }

        return attribute;
    }

	private void validateVendorIds(List<String> vendorList) throws ValidationException {
		List<String> validVendors = repository.getValidVendorIds(vendorList);

        List<String> invalidVendors = vendorList.stream()
                .filter(id -> !validVendors.contains(id))
                .toList();

        if (!invalidVendors.isEmpty()) {
            throw new ValidationException("Invalid vendor ID(s): " + String.join(", ", invalidVendors));
        }
	}


    @Override
    public void deleteAttribute(String id) {
    	Attribute attribute = repository.findById(id);
        if (attribute == null) {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attribute not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Deleted attribute and associated mappings for ID: {}", id);
    }

    @Override
    public List<Attribute> getAllAttributes() {
        return repository.findAll();
    }

    @Override
    public Attribute getAttributeById(String id) {
        Attribute attribute = repository.findById(id);
        if (attribute == null) {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attribute not found with ID: " + id);
        }
        return attribute;
    }

    @Override
    public Attribute getAttributeByName(String name) {
        Attribute attribute = repository.findByName(name);
        if (attribute == null) {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attribute not found with name: " + name);
        }
        return attribute;
    }

    @Override
    public List<Attribute> getAttributeByAttributeId(String attributeId) {

        if (!attributeId.matches("\\d+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "attributeId must be numeric"
            );
        }

        Integer id = Integer.parseInt(attributeId);

        List<Attribute> attributes = repository.findByAttributeId(id);

        if (attributes.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Attribute not found with attribute_id: " + attributeId
            );
        }

        return attributes;
    }


    @Override
    public List<Vendor> getAllActiveAttributes(String status) {
        return repository.findAllActiveAttributes(status);
    }
}

