package com.savbill.radius.kafka.message;


import com.savbill.radius.entity.CustIpMapping;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustIPMessage {
    
    List<CustIpMapping> custIpMappingList = new ArrayList<>();

    boolean isMultipleDelete = false;
}
