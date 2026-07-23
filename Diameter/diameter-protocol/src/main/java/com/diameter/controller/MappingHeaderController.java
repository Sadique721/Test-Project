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
import org.springframework.web.bind.annotation.*;

import com.diameter.model.MappingHeader;
import com.diameter.service.MappingHeaderService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/packetMapping")
public class MappingHeaderController {

    private static final Logger logger = LoggerFactory.getLogger(MappingHeaderController.class);

    @Autowired
    private MappingHeaderService mappingHeaderService;

    @PostMapping
    public ResponseEntity<MappingHeader> createOrUpdateMapping(@Valid @RequestBody MappingHeader mappingHeader)
            throws ValidationException {
        logger.info("POST /api/mappings - Create or update mapping: {}", mappingHeader.getRequestType());
        MappingHeader response = mappingHeaderService.createOrUpdateMapping(mappingHeader);
        logger.info("Mapping processed successfully: {}", mappingHeader.getRequestType());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MappingHeader> updateMapping(@PathVariable("id") String id, @Valid @RequestBody MappingHeader mappingHeader)
            throws ValidationException {
        logger.info("PUT /api/mappings/{} - Updating mapping", id);
        mappingHeader.setId(id);
        MappingHeader response = mappingHeaderService.updateMapping(mappingHeader);
        logger.info("Mapping updated successfully: {}", mappingHeader.getRequestType());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMapping(@PathVariable String id, HttpServletRequest request) {
        logger.info("DELETE /api/packetMapping/{} - Deleting packet mapping", id);
        mappingHeaderService.deleteMapping(id); // throws 404 if not found
        logger.info("Packet mapping deleted successfully: {}", id);
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Packet Mapping Deleted Successfully", request.getRequestURI());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MappingHeader>> getMappings(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "commandName", required = false) String commandName) {

        if (id != null) {
            logger.info("GET /api/mappings?id={} - Fetching by ID", id);
            return ResponseEntity.ok(List.of(mappingHeaderService.getMappingById(id)));
        } else if (commandName != null) {
            logger.info("GET /api/mappings?commandName={} - Fetching by commandName", commandName);
            return ResponseEntity.ok(List.of(mappingHeaderService.getMappingByCommandName(commandName)));
        } else {
            logger.info("GET /api/mappings - Fetching all mappings");
            return ResponseEntity.ok(mappingHeaderService.getAllMappings());
        }
    }

    @GetMapping("/byRequestType")
    public ResponseEntity<List<MappingHeader>> getMappingsByRequestAndResponseType(
            @RequestParam(name = "requestType", required = false) String requestType,
            @RequestParam(name = "responseType", required = false) String responseType,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "vendorId", required = false) Integer vendorId,

            // ✅ NEW PARAM
            @RequestParam(name = "ccRequestType", required = false) String ccRequestType
    ) {

        logger.info("GET /api/packetMapping/byRequestType - requestType: {}, responseType: {}, application: {}, vendorId: {}, ccRequestType: {}",
                requestType, responseType, application, vendorId, ccRequestType);

        List<MappingHeader> mappings =
                mappingHeaderService.getMappingsByRequestAndResponseType(
                        requestType, responseType, application, vendorId, ccRequestType
                );

        if (mappings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(mappings);
    }

}