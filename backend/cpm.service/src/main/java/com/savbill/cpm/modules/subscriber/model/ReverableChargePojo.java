package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.savbill.cpm.modules.CommonList.model.CommonListDTO;
import com.savbill.cpm.pojo.api.CustChargeDetailsPojo;

@Data
public class ReverableChargePojo {
    List<CustChargeDetailsPojo> custChargeDetailsList;
    List<CommonListDTO> reversalTypeCommonList;
}
