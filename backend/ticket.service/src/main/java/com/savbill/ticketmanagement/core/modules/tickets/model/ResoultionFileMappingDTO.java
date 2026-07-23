package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
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
