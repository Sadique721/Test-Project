package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.common.CustIpMapping;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class CustIPMessage {

    List<CustIpMapping> custIpMappingList = new ArrayList<>();

    boolean isMultipleDelete = false;

    public CustIPMessage(List<CustIpMapping> custIpMappingList, boolean isMultipleDelete) {
        this.custIpMappingList = custIpMappingList;
        this.isMultipleDelete = isMultipleDelete;
    }

    public CustIPMessage(List<CustIpMapping> custIpMappingList) {
        this.custIpMappingList = custIpMappingList;
    }
}
