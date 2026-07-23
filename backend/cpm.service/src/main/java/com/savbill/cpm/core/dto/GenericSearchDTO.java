package com.savbill.cpm.core.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GenericSearchDTO {

    private List<GenericSearchModel> filter = new ArrayList<>();
}
