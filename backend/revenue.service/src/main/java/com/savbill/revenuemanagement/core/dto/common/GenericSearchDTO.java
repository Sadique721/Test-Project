package com.savbill.revenuemanagement.core.dto.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GenericSearchDTO {

    private List<GenericSearchModel> filter = new ArrayList<>();
}
