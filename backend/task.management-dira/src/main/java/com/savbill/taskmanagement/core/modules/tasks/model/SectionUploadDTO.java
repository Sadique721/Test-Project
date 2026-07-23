package com.savbill.taskmanagement.core.modules.tasks.model;

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
