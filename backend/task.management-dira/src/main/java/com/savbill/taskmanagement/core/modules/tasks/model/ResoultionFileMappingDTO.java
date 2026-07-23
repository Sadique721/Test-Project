package com.savbill.taskmanagement.core.modules.tasks.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Data
public class ResoultionFileMappingDTO {
    private Long resolutionId;
    private String longitude;
    private String latitude;
    private Long caseId;
    private String remarks;
    List<MultipartFile> fileList=new ArrayList<>();
}
