package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import com.savbill.revenuemanagement.autoassign.CreditDocumentPaymentPojo;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustChargeDetailsRevenue;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustPlanMappingRevenue;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustomerChargeHistoryRevenue;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustomerServiceMappingRevenue;
import com.savbill.revenuemanagement.core.dto.invoice.AdditionalInformationDTO;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import lombok.Data;

import java.util.List;

@Data
public class ChangePlanMessage {

    String type;

    Integer renewalId;

    List<CustPlanMappingRevenue> newCustPlanMappingRevenues;

    List<CustChargeDetailsRevenue> custChargeDetailsRevenues;

    List<CustomerChargeHistoryRevenue> customerChargeHistoryRevenues;

    List<CustomerServiceMappingRevenue> customerServiceMappingRevenues;

    List<CustPlanMappingRevenue> oldCustPlanMappingRevenues;

    List<Integer> custChargeIds;
    private Integer createdById;

    private Integer parentId;

    private List<Integer> childIds;

    private String paySource;

    private List<Long> buId;

    private Integer mvnoId;

    private Integer lcoId;

    private Boolean isLco;

    private Integer getCreatedById;

    private String getCreatedByName;

    private RecordPaymentPojo recordPaymentDTO;

    private AdditionalInformationDTO additionalInformationDTO;

    List<Integer> overrideChargeIds;

    private  boolean changePlanNextBillDate;

    private String billDateToday;

    private Boolean isMvnoCustomer;

    private List<Integer> debitDocDetailIds;

    private String custType;
    private String ispFromDate;

    private String ispToDate;
    private Boolean isAutoPaymentRequired=false;
    private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList;
    private Integer payingChildId;
}

