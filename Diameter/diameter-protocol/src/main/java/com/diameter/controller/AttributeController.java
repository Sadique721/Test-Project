package com.diameter.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.ValidationException;

import com.diameter.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diameter.model.Attribute;
import com.diameter.service.AttributeService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

	private static final Logger logger = LoggerFactory.getLogger(AttributeController.class);
	
    @Autowired
    private AttributeService attributeService;

    @PostMapping
    public ResponseEntity<Attribute> createOrUpdateAttribute(@Valid @RequestBody Attribute attribute) throws ValidationException {
    	logger.debug("POST /api/attributes - Create or update attribute: {}", attribute.getName());
    	Attribute attributeResponse = attributeService.createOrUpdateAttribute(attribute);
        logger.debug("Attribute processed successfully: {}", attribute.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(attributeResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attribute> updateAttribute(@PathVariable String id, @Valid @RequestBody Attribute attribute) throws ValidationException {
    	logger.debug("PUT /api/attributes/{} - Updating attribute", id);
        attribute.setId(id);
        Attribute attributeResponse = attributeService.updateAttribute(attribute);
        logger.debug("Attribute updated successfully: {}", attribute.getName());
        return ResponseEntity.ok(attributeResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAttribute(@PathVariable String id, HttpServletRequest request) {
        logger.debug("DELETE /api/attributes/{} - Deleting attribute", id);
        attributeService.deleteAttribute(id); // throws 404 if not found
        logger.debug("Attribute deleted successfully: {}", id);
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Attribute deleted successfully", request.getRequestURI());
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<Attribute>> getAllAttributes(
    		@RequestParam(name= "id", required = false) String id,
            @RequestParam(name= "name", required = false) String name,
            @RequestParam(name = "attributeId", required = false) String attributeId
    ) {

        if (id != null) {
        	logger.debug("GET /api/attributes?id={} - Fetching by ID", id);
            return ResponseEntity.ok(List.of(attributeService.getAttributeById(id)));
        } else if (name != null) {
        	logger.debug("GET /api/attributes?name={} - Fetching by name", name);
            return ResponseEntity.ok(List.of(attributeService.getAttributeByName(name)));
        } else if (attributeId != null) {
            logger.debug("GET /api/attributes?attributeId={} - Fetching by attribute_id", attributeId);
            return ResponseEntity.ok(
                    attributeService.getAttributeByAttributeId(attributeId)
            );
        }
        else {
        	logger.debug("GET /api/attributes - Fetching all attributes");
            return ResponseEntity.ok(attributeService.getAllAttributes());
        }
    }
}