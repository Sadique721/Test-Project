package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SectionUploadRequest {
    private String name;
    private List<MultipartFile> files;

    private String latitude;

    private String longitude;

    private String opticalRange;
}
