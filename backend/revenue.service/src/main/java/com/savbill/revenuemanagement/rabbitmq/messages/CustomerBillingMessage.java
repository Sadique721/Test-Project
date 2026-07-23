package com.savbill.revenuemanagement.rabbitmq.messages;

import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.autoassign.CreditDocumentPaymentPojo;
import com.savbill.revenuemanagement.core.dto.invoice.AdditionalInformationDTO;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBillingMessage implements Serializable {

    public static final String BILLING = "billing";
    public static final String CUST_ID = "custId";
    public static final String STR_DATE = "strDate";
    public static final String IS_CANCEL_REGENERATE = "isCancelRegenerate";

    public static final String PACKAGE_REL_ID = "packageRelId";

    public static final String IS_CAF_CUSTOMER = "isCAFCustomer";

    public static final String IS_CAF_CUSTOMER_DIRECT_CHARGE = "isCafFDC";

    public static final String RENEWAL_ID = "renewalId";

    public static final String CREATED_BY_NAME="createdByName";

    public static final String oldDebitDocId = "oldDebitDocId";
    public static final String CUSTOMER_INVENTORY_MAPP_ID = "customerInventoryMappId";

    public static final String CUSTOMER_INVENTORY_CAF_TO_CUSTOMER = "inventorycaftocustomer";


    public static final String CURRENT_LOGGED_IN_STAFF = "currentUserLoggedInId";
    public static final String DISCOUNT = "discount";

    public static final String BILL_RUN_ID = "billRunId";

    public static  final String PAYMENT_SOURCE = "paymentSource";

    public static final String BUIDS = "buIds";

    public static final String MVNOID = "mvnoId";

    public static final  String ISLCO = "isLco";

    public static final String PARTNERID = "partnerId";

    public static final String CREATEDBYID = "createById";

    public static final String CREATEDBYNAME = "createByName";

    public static final String OVERRIDECHARGES = "overridecharges";

    public static final String ADDITIONALINFORMATIONDTO = "additionalInformationDTO";
    public static final String POSTPAIDADVANCE = "postpaidAdvance";
    public static final String DISCOUNTTYPE = "discountType";


    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private Integer loggedInStaffId;
    private String createdByName;
    private String updateByName;
    private String paymentOwner;
    private Integer oldDebitDocumentId;
    private Integer paymentOwnerId;
    private Integer renewalId;
    private String type;
    private Boolean isCaptiveportal;

    private Integer parentId;

    private List<Integer> childIds;

    private List<Integer> custChargeIds;

    private String paymentSource;
    private Map<String, Object> data;

    private RecordPaymentPojo recordPaymentDTO;

    private TraceContext traceContext;

    private AdditionalInformationDTO additionalInformationDTO;

    private List<Integer> cprIds ;

    private  List<Integer> oldCprIdsForChangePLan;

    private LocalDate billdate;
    private String billdateToday;
    private String custType;
    private String renew;

    private Integer newServiceId;

    private boolean isMvnoCustomer;

    private List<Integer> debitDocDetailIds;

    private String referenceNo;

    private Boolean isEarlyBillDate;

    private String customerStatus;

    private LocalDate ispFromDate;

    private LocalDate ispToDate;
    private Double discount;

    private Double totalPrice;

    private boolean isAutoPaymentAdjustment;
    private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojos;
    private boolean trailPlanFromToday;
    private boolean trailPlanFromTrailDay;
    private boolean cafCustomerApprove;
    private Integer payableChildId;
    private boolean tracerIdNotRequired;
    private boolean planValidityChangePlan;

    public CustomerBillingMessage(CustomerBillingMessage other) {
        this.messageId = other.messageId;
        this.message = other.message;
        this.sourceName = other.sourceName;
        this.messageDate = other.messageDate;
        this.currentUser = other.currentUser;
        this.loggedInStaffId = other.loggedInStaffId;
        this.createdByName = other.createdByName;
        this.updateByName = other.updateByName;
        this.paymentOwner = other.paymentOwner;
        this.oldDebitDocumentId = other.oldDebitDocumentId;
        this.paymentOwnerId = other.paymentOwnerId;
        this.renewalId = other.renewalId;
        this.type = other.type;
        this.custChargeIds = other.custChargeIds;
        this.childIds = other.childIds;
        this.cprIds=other.cprIds;
        this.oldCprIdsForChangePLan=other.oldCprIdsForChangePLan;
        this.billdate = other.billdate;
        this.billdateToday=other.billdateToday;
        this.newServiceId=other.newServiceId;
        this.data = new HashMap<>(other.data);
        this.recordPaymentDTO = other.getRecordPaymentDTO();
        this.isMvnoCustomer = other.isMvnoCustomer;
        this.custType=other.custType;
        this.debitDocDetailIds = other.debitDocDetailIds;
        if(other.isCaptiveportal!=null) {
            this.isCaptiveportal = other.isCaptiveportal;
        }else{
            this.isCaptiveportal=false;
        }
        if (other.isEarlyBillDate!=null){
            this.isEarlyBillDate = other.isEarlyBillDate;
        }else {
            this.isEarlyBillDate = false;
        }
        this.referenceNo = other.referenceNo;
        this.renew = other.renew;
        this.ispFromDate = other.ispFromDate;
        this.ispToDate = other.ispToDate;
        this.discount = other.discount;
        this.totalPrice=other.totalPrice;
        this.isAutoPaymentAdjustment=other.isAutoPaymentAdjustment;
        this.creditDocumentPaymentPojos=other.creditDocumentPaymentPojos;
        if(other.payableChildId != null){
            this.payableChildId = other.payableChildId;
        }
    }

    public CustomerBillingMessage(Map<String, Object> data) {
        this.data = data;
    }
}
