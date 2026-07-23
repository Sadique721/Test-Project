package com.diameter.controller;

import com.diameter.model.ApiResponse;
import com.diameter.model.Vendor;
import com.diameter.service.VendorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);
    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    /**
     * CREATE Vendor
     */
    @PostMapping
    public ResponseEntity<Vendor> createVendor(@RequestBody @Valid Vendor vendor)
            throws ValidationException {

        logger.debug("POST /vendors - Creating vendor: {}", vendor.getName());
        Vendor createdVendor = service.createVendor(vendor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVendor);
    }

    /**
     * READ Vendors (with optional filters)
     */
    @GetMapping
    public ResponseEntity<List<Vendor>> getVendors(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "vendorId", required = false) Integer vendorId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) String status) {

        logger.debug("GET /vendors - id: {}, vendorId: {}, name: {}, status: {}",
                id, vendorId, name, status);

        List<Vendor> vendors = service.getVendors(id, vendorId, name, status);
        return ResponseEntity.ok(vendors);
    }

    /**
     * READ Vendor by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable String id) {
        logger.debug("GET /vendors/{} - Fetch vendor", id);
        Vendor vendor = service.getVendorById(id);
        return ResponseEntity.ok(vendor);
    }

    /**
     * UPDATE Vendor
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(
            @PathVariable String id,
            @RequestBody @Valid Vendor vendor)
            throws ValidationException {

        logger.debug("PUT /vendors/{} - Updating vendor", id);
        Vendor updatedVendor = service.updateVendor(id, vendor);
        return ResponseEntity.ok(updatedVendor);
    }

    /**
     * DELETE Vendor (Hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteVendor(@PathVariable String id, HttpServletRequest request) {
        logger.debug("DELETE /vendors/{} - Deleting vendor", id);
        service.deleteVendor(id); // throws 404 if not found
        logger.debug("Vendor deleted successfully: {}", id);
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "Vendor Deleted Successfully", request.getRequestURI());
        return ResponseEntity.ok(response);
    }
}