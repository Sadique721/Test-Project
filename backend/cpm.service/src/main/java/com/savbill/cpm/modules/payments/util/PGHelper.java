package com.savbill.cpm.modules.payments.util;

import java.io.IOException;
import java.util.Map;

import com.savbill.cpm.modules.placeOrder.model.OrderResponseModel;
import com.savbill.cpm.modules.purchaseDetails.model.PGResponseModel;
import com.savbill.cpm.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.savbill.cpm.pojo.api.CustomersPojo;
import com.savbill.cpm.pojo.api.PartnerPojo;

public interface PGHelper {

    OrderResponseModel generateFormData(CustomersPojo customersPojo, PurchaseDetailsDTO purchaseDetailsDTO, PartnerPojo partnerPojo) throws IOException;

    PGResponseModel generatePGResponse(Map<String, Object> response);

}
