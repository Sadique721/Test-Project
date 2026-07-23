package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.Data;

import java.util.List;

@Data
public class FileMappingListDTO {

    private String sectionName;

    private List<FileDetailsDTO> fileDetails;
}
