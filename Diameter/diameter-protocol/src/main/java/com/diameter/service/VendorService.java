package com.diameter.service;

import java.util.List;
import javax.xml.bind.ValidationException;
import com.diameter.model.Vendor;

public interface VendorService {

    Vendor createVendor(Vendor vendor) throws ValidationException;

    Vendor updateVendor(String id, Vendor vendor) throws ValidationException;

    void deleteVendor(String id);

    Vendor getVendorById(String id);

    List<Vendor> getVendors(String id, Integer vendorId, String name, String status);
}