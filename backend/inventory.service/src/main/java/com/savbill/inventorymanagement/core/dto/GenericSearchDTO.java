package com.savbill.inventorymanagement.core.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GenericSearchDTO {
    private List<GenericSearchModel> filter = new ArrayList<>();
}
