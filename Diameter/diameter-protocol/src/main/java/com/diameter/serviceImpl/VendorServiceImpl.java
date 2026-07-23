package com.diameter.serviceImpl;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.diameter.model.Vendor;
import com.diameter.repository.VendorRepository;
import com.diameter.service.VendorService;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VendorServiceImpl implements VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);
    private final VendorRepository repository;

    public VendorServiceImpl(VendorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vendor createVendor(Vendor vendor) throws ValidationException {
        logger.info("Creating vendor: {}", vendor.getName());

        // ✅ Generate UUID for primary key
        if (vendor.getId() == null || vendor.getId().isBlank()) {
            vendor.setId(java.util.UUID.randomUUID().toString());
        }
        return repository.saveVendor(vendor);
    }

    @Override
    public Vendor updateVendor(String id, Vendor vendor) throws ValidationException {
        logger.info("Updating vendor with id: {}", id);
        vendor.setId(id);
        return repository.updateVendor(vendor);
    }

    @Override
    public void deleteVendor(String id) {
        logger.info("Deleting vendor with id: {}", id);
        Vendor vendor = repository.getVendorById(id);
        if (vendor == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vendor not found with ID: " + id
            );
        }
        repository.deleteVendor(id);
        logger.info("Vendor deleted successfully with id: {}", id);
    }


    @Override
    public Vendor getVendorById(String id) {
        logger.info("Fetching vendor with id: {}", id);
        return repository.getVendorById(id);
    }

    @Override
    public List<Vendor> getVendors(String id, Integer vendorId, String name, String status) {
        logger.info("Fetching vendors");
        try {
            if (id != null) {
                Vendor vendor = repository.getVendorById(id);
                return vendor != null ? List.of(vendor) : Collections.emptyList();
            } else if (vendorId != null) {
                Vendor vendor = repository.getVendorByVendorId(vendorId);
                return vendor != null ? List.of(vendor) : Collections.emptyList();
            } else if (name != null) {
                Vendor vendor = repository.getVendorByName(name);
                return vendor != null ? List.of(vendor) : Collections.emptyList();
            } else {
                return repository.getAllVendors(status);
            }
        } catch (Exception e) {
            logger.error("Error fetching vendors", e);
        }
        return Collections.emptyList();
    }
}