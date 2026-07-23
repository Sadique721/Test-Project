package com.savbill.cpm.modules.ChangePlanDTOs;

import com.savbill.cpm.model.postpaid.CreditDebitDocMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditDebitDocMessage {
    List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
}
