package com.savbill.partnermanagement.modules.partner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerHierarchy {
    String label;
    Boolean expanded;
    List<PartnerHierarchy> children;
}

