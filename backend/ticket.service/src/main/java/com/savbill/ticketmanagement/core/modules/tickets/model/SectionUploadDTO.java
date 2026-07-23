package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SectionUploadDTO {

    private String name;

    private List<MultipartFile> files;

    private String latitude;

    private String longitude;

    private String opticalRange;
}
