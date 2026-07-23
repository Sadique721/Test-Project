import { DatePipe, formatDate } from "@angular/common";
import { Component, ElementRef, Input, OnInit, ViewChild } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgbDateStruct } from "@ng-bootstrap/ng-bootstrap";
import * as FileSaver from "file-saver";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { countries } from "src/app/components/model/country";
import { CustomerManagements } from "src/app/components/model/customer";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomerInventoryMappingService } from "src/app/service/customer-inventory-mapping.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { InvoiceDetailsService } from "src/app/service/invoice-details.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { LoginService } from "src/app/service/login.service";
import { OutwardService } from "src/app/service/outward.service";
import { ProuctManagementService } from "src/app/service/prouct-management.service";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import { StaffService } from "src/app/service/staff.service";
import { InvoiceDetalisModelComponent } from "src/app/components/invoice-detalis-model/invoice-detalis-model.component";
import { InvoicePaymentDetailsModalComponent } from "src/app/components/invoice-payment-details-modal/invoice-payment-details-modal.component";
import { ExternalItemManagementService } from "src/app/service/external-item-management.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { PaymentAmountModelComponent } from "src/app/components/payment-amount-model/payment-amount-model.component";
import { WorkflowAuditDetailsModalComponent } from "src/app/components/workflow-audit-details-modal/workflow-audit-details-modal.component";
import { CustomerplanGroupDetailsModalComponent } from "src/app/components/customerplan-group-details-modal/customerplan-group-details-modal.component";
import { CustomerWithdrawalmodalComponent } from "src/app/components/customer-withdrawalmodal/customer-withdrawalmodal.component";
import { InwardService } from "src/app/service/inward.service";
import { InvoiceMasterService } from "src/app/service/invoice-master.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { ChildCustChangePlanComponent } from "src/app/components/child-cust-change-plan/child-cust-change-plan.component";
import { Subject } from "rxjs";
import { SearchPaymentService } from "src/app/service/search-payment.service";
import { filter, isEqual } from "lodash";
import moment from "moment";
import { Utils } from "src/app/utils/utils";
import { ActivatedRoute, Router } from "@angular/router";
import { NetworkdeviceService } from "src/app/service/networkdevice.service";
import { QuotaDetailsModalComponent } from "../quota-details-modal/quota-details-modal.component";
import { CustomerService } from "src/app/service/customer.service";
import { Table } from "primeng/table";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import { CountryManagementService } from "src/app/service/country-management.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { PartnerService } from "src/app/service/partner.service";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";

declare var $: any;

@Component({
    selector: "app-customer-old",
    templateUrl: "./customer-old.component.html",
    styleUrls: ["./customer-old.component.scss"],
    standalone: false
})
export class CustomerOldComponent implements OnInit {
    // @Input() custType: any;
    custType: any;
    changePlanClicked: Subject<any> = new Subject<any>();
    resetFormEvent: Subject<any> = new Subject<any>();
    inventoryStatusDetailsForReplace = [];
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    KraTitle = RadiusConstants.KRA_PIN;
    @ViewChild("closebutton") closebutton;
    @ViewChild(InvoiceDetalisModelComponent)
    @ViewChild("dt")
    table: Table;
    InvoiceDetailModal: InvoiceDetalisModelComponent;
    @ViewChild(InvoicePaymentDetailsModalComponent)
    invoicePaymentDetailModal: InvoicePaymentDetailsModalComponent;
    @ViewChild(PaymentAmountModelComponent)
    PaymentDetailModal: PaymentAmountModelComponent;
    @ViewChild(WorkflowAuditDetailsModalComponent)
    custauditWorkflowModal: WorkflowAuditDetailsModalComponent;
    @ViewChild(CustomerplanGroupDetailsModalComponent)
    custPlanGroupDataModal: CustomerplanGroupDetailsModalComponent;
    @ViewChild(CustomerWithdrawalmodalComponent)
    withdrawalAmountModal: CustomerWithdrawalmodalComponent;
    @ViewChild(ChildCustChangePlanComponent)
    childCustChangePlanComponent: ChildCustChangePlanComponent;
    @ViewChild(QuotaDetailsModalComponent)
    quotaModalOpen: QuotaDetailsModalComponent;
    parentChargeRecurringCustList: number;
    childChargeRecurringCustList: number;
    changePlanChildValidData: boolean = false;
    UpdateCustPlansData: boolean = false;
    ifModelIsShow: boolean = false;
    fields: any;
    countries: any = countries;
    customerGroupForm: UntypedFormGroup;
    Subscriberform: UntypedFormGroup;
    custLedgerForm: UntypedFormGroup;
    customerCategoryList: any;
    submitted = false;
    custLedgerSubmitted = false;
    isStatusChangeSubMenu = false;
    paymentOwnerError: boolean;
    taxListData: any;
    createcustomerData: CustomerManagements;
    currentPagecustomerListdata = 1;
    customerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerListdatatotalRecords: any;
    customerListData: any = [];
    viewcustomerListData: any = [];
    iscustomerEdit = false;
    customertype = "";
    // CustomertypeSubtype=" " ;
    CustomerSector = "";
    custDetilsCustId;
    customercategory = "";
    searchOption = "";
    searchDeatil = "";
    // fieldEnable = false;
    logoFile;
    filterPlanListCust: any;
    @ViewChild("logoUploader") logoUploader: ElementRef;
    public imgSrc: any;
    isPlanTypeAddon = false;
    paymentAddressData: any = [
        {
            fullAddress: "",
            pincodeId: "",
            areaId: "",
            cityId: "",
            stateId: "",
            countryId: "",
            landmark: ""
        }
    ];

    departmentListData: any;
    departmenttotalRecords: any;
    departmentitemsPerPage: any;
    permanentAddressData: any = [
        {
            fullAddress: "",
            pincodeId: "",
            areaId: "",
            cityId: "",
            stateId: "",
            countryId: "",
            landmark: ""
        }
    ];
    customerLedgerData: any = {
        custname: "",
        plan: "",
        status: "",
        username: "",
        customerLedgerInfoPojo: {
            openingAmount: "",
            closingBalance: ""
        }
    };
    customerLedgerDetailData: any = {
        title: "",
        firstname: "",
        lastname: "",
        contactperson: "",
        gst: "",
        pan: "",
        aadhar: "",
        passportNo: "",
        cafno: "",
        acctno: "",
        username: "",
        mobile: "",
        phone: "",
        email: "",
        serviceareaid: "",
        servicetype: "",
        custtype: "",
        latitude: "",
        longitude: "",
        didno: "",
        voicesrvtype: "",
        partnerid: "",
        invoiceType: "",
        parentExperience: "",
        salesremark: "",
        paymentDetails: {
            amount: "",
            referenceno: "",
            paymode: "",
            paymentdate: ""
        },
        addressList: [
            {
                fullAddress: "",
                pincodeId: "",
                areaId: "",
                cityId: "",
                stateId: "",
                countryId: ""
            }
        ]
    };
    postdata: any = {
        CREATE_DATE: "",
        END_DATE: "",
        id: "",
        amount: "",
        balAmount: "",
        custId: "",
        description: "",
        refNo: "",
        transcategory: "",
        transtype: ""
    };
    startDateCustLedger: any = "";
    endDateCustLedger: any = "";
    customerLedgerListData: any;
    chargeCategoryList: any;
    isPlanEdit = false;
    viewPlanListData: any;
    payMappingListFromArray: UntypedFormArray;
    addressListFromArray: UntypedFormArray;
    paymentDetailsFromArray: UntypedFormArray;
    overChargeListFromArray: UntypedFormArray;
    custMacMapppingListFromArray: UntypedFormArray;
    // uploadDocumentListFromArray: FormArray;
    selectvalue = "";
    paymappingItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    payMappinftotalRecords: String;
    currentPagePayMapping = 1;
    overChargeListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    overChargeListtotalRecords: String;
    currentPageoverChargeList = 1;
    uploadDocumentListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    uploadDocumentListtotalRecords: String;
    currentPageoverUploadDocumentList = 1;
    custMacMapppingListtemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacMapppingListtotalRecords: String;
    currentPagecustMacMapppingList = 1;
    custLedgerItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custLedgertotalRecords: String;
    currentPagecustLedgerList = 1;
    custChargeDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custChargeDeatiltotalRecords: String;
    currentPagecustChargeDeatilList = 1;
    custPlanDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custPlanDeatiltotalRecords: String;
    currentPagecustPlanDeatilList = 1;
    custMacAddItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custreturnItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacAddtotalRecords: String;
    custreturnAddtotalRecords: String;
    currentPagecustMacAddList = 1;
    currentPagereturnAddList = 1;
    currentPageTicketConfig = 1;
    custTicketConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custTicketConfigtotalRecords: String;
    temp = [];
    customerListData1: any;
    customerListDataselector: any;
    totalAddress = 0;
    maxLength = 250;
    macAddresscountNumber = 0;
    searchCustomerName: any;
    searchCustomerType: any = "";
    submit: boolean = false;
    // CustomertypeSubtype:any =" "
    searchData: any;
    customersListData: any;
    addresslength = 0;
    payMappinglength = 0;
    charegelength = 0;
    charge_date: NgbDateStruct | any;
    presentaddress = "";
    require: any;
    ngbBirthcal: NgbDateStruct | any;
    listView = true;
    createView = false;
    areaDetails: any;
    pincodeDeatils: any;
    areaAvailableList: any;
    custTicketList: any = [];
    selectAreaList = false;
    selectPincodeList = false;
    addressListData: any = [];
    macListData: any = [];
    PyamentpincodeDeatils: any;
    permanentpincodeDeatils: any;
    paymentareaDetails: any;
    permanentareaDetails: any;
    paymentareaAvailableList: any;
    permanentareaAvailableList: any;
    planGroupForm: UntypedFormGroup;
    chargeGroupForm: UntypedFormGroup;
    shiftLocationChargeGroupForm: UntypedFormGroup;
    macGroupForm: UntypedFormGroup;
    changePlanForm: UntypedFormGroup;
    changePlanNewForm: UntypedFormGroup;
    changePlansubmitted = false;
    // uploadDocumentGroupForm: FormGroup;
    plansubmitted = false;
    chargesubmitted = false;
    presentGroupForm: UntypedFormGroup;
    paymentGroupForm: UntypedFormGroup;
    permanentGroupForm: UntypedFormGroup;
    chargenewPlanForm: UntypedFormGroup;
    changenewPlanForm: UntypedFormGroup;
    updatePresentGroupForm: UntypedFormGroup;
    validPattern = "^[0-9]{3}$";
    selectAreaListPermanent = false;
    selectAreaListPayment = false;
    selectPincodeListPermanent = false;
    selectPincodeListPayment = false;
    ischecked = false;
    macsubmitted = false;
    uploadDocsubmitted = false;
    chargeList: any;
    selectchargeList = false;
    planData: any = [];
    filterPlanData: any = [];
    // listSearchView: boolean = false;
    isCustomerLedgerOpen = false;
    isCustomerDetailOpen = false;
    isCustomerDetailSubMenu = false;
    customerChangePlan = false;
    partnerDATA: any = [];
    presentAdressDATA: any = [];
    permentAdressDATA: any = [];
    paymentAdressDATA: any = [];
    chargeDATA = [];
    dataPlan = [];
    selectedStaffCust: any = [];
    staffCustList: any = [];
    staffid: any = "";
    paymentOwnerId: any = "";
    postpaidplanData: any;
    serviceAreaDATA: any;
    paymentData: any;
    paymentDataamount: any;
    paymentDatareferenceno: any;
    paymentDatapaymentdate: any;
    paymentDatapaymentMode: any;
    plantypaSelectData: any;
    selectchargeValueShow = false;
    currentDate = new Date();
    customerID: any;
    // change Password
    changePasswordForm: UntypedFormGroup;
    changePasswordvalue: any;
    customerPlanView = false;
    customerTicketView = false;
    custCurrentPlanList: any;
    changePlanCustCurrentPlan: any;
    custFuturePlanList: any;
    custExpiredPlanList: any;
    changeSubmitted = false;
    userName: "";
    viewcustomerPaymentData: any;
    viewCustomerPaymentList = false;
    customerPaymentdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerPaymentdatatotalRecords: String;
    currentPagecustomerPaymentdata = 1;
    customerFuturePlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerFuturePlanListdatatotalRecords: String;
    currentPagecustomerFuturePlanListdata = 1;
    customerExpiryPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerExpiryPlanListdatatotalRecords: String;
    currentPagecustomerExpiryPlanListdata = 1;
    customerCurrentPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCurrentPlanListdatatotalRecords: String;
    currentPagecustomerCurrentPlanListdata = 1;
    custQuotaList: any[];
    returnMapppingList: any[];
    custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custQuotaListtotalRecords: String;
    currentPagecustQuotaList = 1;
    selPlanData: any = {
        quotatype: "",
        quotatime: "",
        quota: "",
        quotaUnit: "",
        quotaunittime: "",
        validity: "",
        offerprice: "",
        taxamount: "",
        activationDate: "",
        expiryDate: "",
        finalAmount: ""
    };
    planByServiceArea: any;
    invoicedropdownValue = [{ docnumber: "Advance", id: 0 }];
    searchLocationForm: UntypedFormGroup;
    planCategoryForm: UntypedFormGroup;
    currentPagesearchLocationList = 1;
    searchLocationItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    searchLocationtotalRecords: String;
    currentPagenearDeviceLocationList = 1;
    nearDeviceLocationItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    nearDeviceLocationtotalRecords: String;
    searchLocationData: any;
    nearDeviceLocationData: any;
    ifsearchLocationModal = false;
    ifNearLocationModal = false;
    iflocationFill = false;
    changePlanData: any = {};
    AclClassConstants;
    AclConstants;
    serviceAreaData: any;
    public loginService: LoginService;
    customerIdINLocationDevice: any;
    NetworkDeviceData: any;
    customertotalRecords = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPage = 1;
    searchkey: string;
    customerLedgerSearchKey: string;
    legershowItemPerPage = 1;
    CurrentPlanShowItemPerPage = 1;
    futurePlanShowItemPerPage = 1;
    expiredShowItemPerPage = 1;
    ticketShowItemPerPage = 1;
    paymentShowItemPerPage = 1;
    assignInventory: boolean;
    assignExternalInventory: boolean;
    assignPlanInventory: boolean;
    customerId: any;
    customerListDatalength = 0;
    customerrMyInventoryView: boolean;
    assignedInventoryList = [];
    currentPageProductListdata = 1;
    productListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productListdatatotalRecords: any;
    first = 0;
    rows = 10;
    totalCDRRecords: number;
    currentPageCDR = 1;
    itemsPerPageCDR = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageCDR = 1;
    ifMyInvoice = false;
    ifCDR = false;
    searchAcctCdrForm: UntypedFormGroup;
    searchCDRSubmitted = false;
    groupDataCDR: any[] = [];
    fileNameCDR = "CDR.xlsx";
    CustomerName: any;
    showItemPerPageInvoice = 1;
    customerAddress: any;

    isDisable: boolean = false;

    InvoiceDATA = new BehaviorSubject({
        InvoiceDATA: ""
    });
    invoiceId = new BehaviorSubject({
        invoiceId: ""
    });
    paymentId = new BehaviorSubject({
        paymentId: ""
    });
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });
    planGroupcustid = new BehaviorSubject({
        planGroupcustid: ""
    });
    wCustID = new BehaviorSubject({
        wCustID: "",
        WalletAmount: ""
    });

    PlanQuota = new BehaviorSubject({
        custid: "",
        PlanData: ""
    });
    searchInvoiceMasterFormGroup: UntypedFormGroup;
    currentPageinvoiceMasterSlab = 1;
    invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoiceMastertotalRecords: String;
    searchInvoiceData: any;
    invoiceMasterListData: any = [];
    isInvoiceSearch = false;
    paymentFormGroup: UntypedFormGroup;
    createPaymentData: any;
    customerData: any;
    customerIdRecord: number;

    assignInventoryModal: boolean;
    inventoryAssignForm: UntypedFormGroup;
    inwardList: any[];
    serviceList: any[];
    externalItemList: any[];
    Customertype: any[];
    CustomertypeSubtype: any[];
    availableQty: number;
    serviceUnit: any;
    unit: any;

    custPackageUnit: any[];
    products = [];
    replaceProducts = [];
    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    invoiceType = [
        { label: "Group", value: "Group" },
        { label: "Independent", value: "Independent" }
    ];
    parentExperience = [
        { label: "Single", value: "Single" },
        { label: "Actual", value: "Actual" }
    ];
    valleyType = [
        { label: "Inside Valley", value: "Inside Valley" },
        { label: "Outside valley", value: "Outside valley" }
    ];
    inventoryType = [
        { label: "Permanant Replacement", value: "Permanant Replacement" },
        { label: "Temporary Replacement", value: "Temporary Replacement" }
    ];
    customerArea = [
        { label: "Rural", value: "Rural" },
        { label: "Urban", value: "Urban" },
        { label: "City", value: "City" }
    ];
    @Input("customerId")
    showQtyError: boolean;
    // userId: number = localStorage.getItem('userId');
    userId: number = +localStorage.getItem("userId");
    macList = [];
    selectedMACAddress: any = [];
    macAddresses: any;
    productHasMac: boolean;
    showQtySelectionError: boolean;
    productHasSerial: boolean;
    showPassword = false;
    _passwordType = "password";
    searchkey2: string;
    suggestioninput: any;
    showNewPassword = false;
    showOLDPassword = false;
    _passwordOLDType = "password";
    _passwordNewType = "password";
    paymentMode = [];
    statusOptions = RadiusConstants.status;
    searchOptionSelect = this.commondropdownService.customerSearchOption;
    selectTitile = [
        { label: "Mr" },
        { label: "Ms" },
        { label: "Mrs" },
        { label: "Miss" },
        { label: "M/S" },
        { label: "Dear" }
    ];
    planDetailsCategory = [
        { label: "Individual", value: "individual" },
        { label: "Plan Group", value: "groupPlan" }
    ];
    CustomerTypeValue = [
        { label: "Customer", value: "customer" },
        { label: "Organization", value: "organization" }
    ];
    totaladjustedAmount = 0;
    celendarTypeData = [{ label: "English" }, { label: "Nepali" }];
    ifIndividualPlan = false;
    ifPlanGroup = false;
    dunningRules: any;
    selectedCategory = "Silver";
    planGroupName: any = "";
    invoiceList = [];
    customerChildsView: boolean;
    pageNumberForChildsPage = 1;
    pageSizeForChildsPage = RadiusConstants.ITEMS_PER_PAGE;
    childCustomerDataList: any = [];
    childCustomerDataTotalRecords: number;
    masterSelected: boolean;
    checklist: any;
    checkedList: any = [];
    childPlanRenewArray: UntypedFormArray = new UntypedFormArray([]);
    pageNumberForChildsPageForChangePlan = 1;
    pageSizeForChildsPageForChangePlan = RadiusConstants.ITEMS_PER_PAGE;
    childCustomerDataListForChangePlan: any = [];
    childCustomerDataTotalRecordsForChangePlan: number;
    allowChangePlan: boolean;
    prepaidParentCustomerList: any;
    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentCustomerListdatatotalRecords: any;
    currentPageplanChangeListdata = 1;
    planChangeListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    planChangeListdatatotalRecords: any;
    selectedParentCust: any = [];
    selectedParentCustId: any;
    parentCustList: any;
    billableCustList: any;
    editCustomerId: any;
    newFirst = 0;
    searchParentCustOption = "";
    searchParentCustValue = "";
    serviceAreaDisable = false;
    parentFieldEnable = false;
    validityUnitFormGroup: UntypedFormGroup;
    validityUnitFormArray: UntypedFormArray;
    bankDataList: any;
    // discount
    customerCustDiscountListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCustDiscountListdatatotalRecords: String;
    currentPagecustomerCustDiscountListdata = 1;
    CustDiscountShowItemPerPage = 1;
    custCustDiscountList: any = [];
    dataDiscountPlan: any = [];
    eventActionData: any = [];
    oldDiscValue: number;
    newDiscValue: number;
    customerUpdateDiscount = false;
    shiftLocationEvent = false;
    FinalAmountList: any = [];
    planMappingList = [];
    planDiscount: number;
    finalOfferPrice: number;
    ifPlanSelectChanePlan = false;
    newAdddiscountdata: any = [];
    maxDiscountValue = 99;
    OldplanGList = [];
    serviceWisePlansData = [];
    changePlanBindigNewPlan = [];
    getAllPlanIvnetoryIdOnPlanIdList: any[];
    newPlanSelectArray: UntypedFormArray;
    UpdateParentCustPlans = true;
    childCustID: any = "";
    childPlanGroupFlag = false;
    lastRenewalPlanGroupID = "";
    lastRenewal_CHILDPlanGroupID = "";
    isInvoiceData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];
    ifWalletMenu = false;
    getWallatData = [];
    chargePlanForm: UntypedFormGroup;
    planDropdownInChageData = [];
    dataChargePlan: any = [];
    addChargeForm: UntypedFormGroup;
    chargeChildGroupForm: UntypedFormGroup;
    overChargeChildListFromArray: UntypedFormArray;
    overChargeListChildItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    overChargeListChildtotalRecords: String;
    currentPageoverChargeListChild = 1;
    customerInventoryMappingId: any;
    customerInventoryMappingIdForReplace: any;
    showReplacementForm = false;
    inventoryStatusDetails = [];
    inventoryStatusView = false;
    staffUser: any;
    //isAdmin = false;
    customerInventoryListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerInventoryListDataCurrentPage = 1;
    customerInventoryListDataTotalRecords: number;
    assignedInventoryListWithSerial = [];
    assignInventoryWithSerial: boolean;
    customerInventoryDetailsListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerInventoryDetailsListDataCurrentPage = 1;
    customerInventoryDetailsListDataTotalRecords: number;
    planListByType: any = [];
    groupPlanListByType: any = [];
    planGroupFlag = false;
    changeplanGroupFlag = false;
    filterPlanGroupListCust: any;
    filterSelectedPlanGroupListCust: any;
    OlddiscountData = [];
    inputMobile = "";
    inputMobileSec = "";
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    currentStatus: any;
    updatedStatus: any;
    custIDStatus: any;
    AllcustApproveList: any = [];
    custChangeStatusConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPagecustChangeStatusConfig = 1;
    custChangeStatusConfigtotalRecords: String;
    changeStatusShowItemPerPage = 1;
    filterNormalPlanGroup = [];
    serviceareaCheck = true;
    invoicePaymentData = [];
    invoiceID = "";
    invoicePaymentItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPageinvoicePaymentList = 1;
    invoicePaymenttotalRecords: number;
    ifInvoicePayment = false;
    allchakedPaymentData = [];
    ispaymentChecked = false;
    allIsChecked = false;
    isSinglepaymentChecked = false;
    planGroupSelected: any;
    planGroupChildSelected: any;
    planList: any;
    planListChild: any;
    selectedPlanChildList = [];
    selectedPlanList = [];
    subisuChange = false;
    selectPlanListIDs = [];
    selectPlanChildListIDs = [];
    changePlanBindigChildNewPlan = [];
    getProductCategoryList: any[];
    getProductByPlanIdList: any[];
    planGroupSelectedSubisu: any;
    planListSubisu: any;
    plansArray: UntypedFormArray;
    customerNetworkLocationDetailData: any;
    newPrice: any;
    isInvoiceToOrg: any = false;
    customerBill: "";
    custInvoiceToOrg: boolean;
    ifChargeGetData = false;
    isServiceOpen: boolean = false;
    isCreditNoteOpen: boolean = false;
    chargeUseCustID = "";
    chargeAllData: any = [];
    childChargeData = [];
    billingCycle = [];
    ifUpdateAddress = false;
    ifUpdateAddressSubmited = false;
    shiftLocationPopId: number;
    shiftLocationOltId: number;
    requestedByID: number;
    branchID: number = 0;
    prepaidValue: number = 0;
    prepaid;
    walletValue: number;
    dueValue: number;
    shiftLocationDTO: any = {
        addressDetails: {
            id: "",
            addressType: "",
            landmark: "",
            areaId: "",
            pincodeId: "",
            cityId: "",
            stateId: "",
            countryId: "",
            isDelete: false
        },
        updateAddressServiceAreaId: "",
        isPaymentAddresSame: "true",
        isPermanentAddress: "true",
        shiftPartnerid: "",
        popid: "",
        oltid: "",
        requestedById: "",
        branchID: ""
    };
    partnerListByServiceArea: any = [];
    AreaListDD: any;
    pincodeDD: any = [];
    assignAppRejectDiscountForm: UntypedFormGroup;
    assignAppRejectShiftLocationForm: UntypedFormGroup;
    reject = false;
    rejectInventoryData = [];
    selectStaffReject: any;
    approved = false;
    approveInventoryData = [];
    selectStaff: any;
    approveId: any;
    resolutionReasonData: any;
    rootCauseReasonData: any;
    branchList: any = [];
    staffUserId: any;
    ticketGroupForm: UntypedFormGroup;
    ticketReasonCategoryData: any;
    statusData: any;
    priorityData: any;
    ticketReasonSubCategoryData: any;
    groupReasonData: any;
    caseTypeData: any;
    customerChargeDataShowChangePlan = [];
    changeNewPlan = "";
    selectPlan0Rplangroup = "";
    changePlanDate: any = [];
    activePlanList: any[];
    ifplanisSubisuSelect = false;
    childPlan_PLANGROUPID = [];
    WalletAmount: any = "";
    workflowAuditDataI: any = [];
    isCustSubTypeCon: boolean = false;
    isShowInvoiceList: boolean = false;

    assignDiscountData: any = [];
    assignShiftLocationData: any = [];
    discountFlageType = "";
    shiftLocationFlagType = "";
    AppRjecHeader = "";
    assignDiscounsubmitted = false;
    assignShiftLocationsubmitted = false;
    searchDBRFormDate: any = "";
    searchDBREndDate: any = "";
    dbrListData: any = [];
    ifPlanEditInput = false;
    currentPageDBRListdata = 1;
    DBRListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    showItemDBRPerPage = 0;
    DBRListdatatotalRecords: any;
    ifShowDBRReport = false;
    private oldMacMappingId: any;
    private assignInventoryCustomerId: any;
    fileName: any;
    file: any = "";
    isTdsFlag: boolean = false;
    isAbbsFlag: boolean = false;
    testamount: number = 0;

    planDataForm: UntypedFormGroup;
    workflowID: number;
    ifWorkflowAuditShow: boolean = false;
    ifAuditDetailsShow: boolean = false;
    ifDunningDetailsShow: boolean = false;
    ifNotificationDetailsShow: boolean = false;
    workflowAuditData: any = [];
    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: String;
    customerStatusDetail: any;
    customerPopName: any = "";
    tdsInclude: boolean = false;
    abbsInclude: boolean = false;
    tdsPercent: number;
    abbsPercent: number;
    currency: string;
    departmentTypeData: any;
    filteredReasonCategoryList: any;
    ticketSourceTypeData: any;
    createTicketData: any = {
        caseForPartner: "",
        caseFor: "",
        caseOrigin: ""
    };
    currentData = this.datePipe.transform(Date(), "yyyy-MM-dd");

    ifcustomerDiscountField = false;
    serviceData: any;
    branchData: any;
    staffList: any;
    remark: string;
    extendValidityForm: UntypedFormGroup;
    custPlanMappingForValidity: any;
    custPlanMappingStartDate: any;
    custPlanMappingEndDate: any;
    custPlanMappingStartDateArray = [];
    custPlanMappingEndDateArray = [];
    chequeDetail = [];

    custPlanMapppingId: any;
    selectedCustService: any = null;
    isDisableServicePlanChange: boolean = false;
    isDisableServiceType: string;
    istrialplan: boolean = false;
    parentCustomerDialogType: any = "";
    customerSelectType: any = "";
    plansByServiceArr = [];
    groupOfferPrices = {};
    offerPrices = {};
    offerPrice = 0;
    isPlanOnDemand: boolean = false;
    dateTime = new Date();
    discountType: any = "One-time";
    discountPermission: any;
    days = [];
    planIds = [];
    planGroupId = null;
    enableChangePlanGroup: boolean = false;
    childPlanData = [];
    parentPurchaseType = null;
    childChangePlanType = null;
    promiseToPayData = [];
    isPromiseToPayModelOpen: boolean = false;
    extendPlangroupId: number = 0;
    showParentCustomerModel = false;
    showSelectStaffModel = false;
    planGroupMapingList: any = [];
    partnerList: any = [];
    isBranchAvailable = false;
    isBranchShiftLocation = false;
    isServiceInShiftLocation: boolean = false;
    displayInvoiceDetails: boolean = false;
    promiseToPayIds = [];
    showPlanConnectionNo = false;
    planForConnection;
    isSelectedInvoice = true;
    isTrialCheckDisable = false;
    // tdsCheck: number = 0;
    // abbsCheck: number = 0;
    chequeDateName = "Cheque Date";

    changePlanTypeValue: any;
    selectedChangePlan: any[];

    isInvoiceDetail = false;
    visibleQuotaDetails: boolean = false;
    constructor(
        private toastr: ToastrService,

        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        private savbillcommonbssservice: SavbillCommonBaseService,
        private ticketManagementService: TicketManagementService,
        public PaymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService,
        public partnerService: PartnerService,
        private searchPaymentService: SearchPaymentService,
        private staffService: StaffService,
        public datepipe: DatePipe,
        loginService: LoginService,
        private customerInventoryMappingService: CustomerInventoryMappingService,
        private recordPaymentService: RecordPaymentService,
        private externalItemManagementService: ExternalItemManagementService,
        private outwardService: OutwardService,
        private inwardService: InwardService,
        private productService: ProuctManagementService,
        private liveUserService: LiveUserService,
        private invoiceDetailsService: InvoiceDetailsService,
        public invoicePaymentListService: InvoicePaymentListService,
        private invoiceMasterService: InvoiceMasterService,
        private systemService: SystemconfigService,
        private networkdeviceService: NetworkdeviceService,
        private datePipe: DatePipe,
        private utils: Utils,
        private route: ActivatedRoute,
        private router: Router,
        private customerService: CustomerService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private countryManagementService: CountryManagementService
    ) {
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        let createAccess = loginService.hasOperationPermission(
            AclClassConstants.ACL_CUSTOMER,
            AclConstants.OPERATION_CUSTOMER_ADD,
            AclConstants.OPERATION_CUSTOMER_ALL
        );

        let editAccess = loginService.hasOperationPermission(
            AclClassConstants.ACL_CUSTOMER,
            AclConstants.OPERATION_CUSTOMER_EDIT,
            AclConstants.OPERATION_CUSTOMER_ALL
        );

        this.discountPermission = this.loginService.hasOperationPermission(
            AclClassConstants.ACL_DISCOUNT,
            AclConstants.OPERATION_DISCOUNT_VIEW,
            AclConstants.OPERATION_DISCOUNT_ALL
        );

        this.iscustomerEdit = !createAccess && editAccess ? true : false;

        this.availableQty = 0;
        // this.inventoryAssignForm.reset();
        this.inventoryAssignForm = this.fb.group({
            id: [""],
            qty: ["1"],
            productId: ["", Validators.required],
            customerId: [this.customerId],
            serviceId: ["", Validators.required],
            inventoryType: [""],
            staffId: [""],
            inwardId: [""],
            assignedDateTime: [new Date(), Validators.required],
            status: ["", Validators.required],
            mvnoId: [""],
            externalItemId: [""],
            itemId: [""]
        });
        this.macList = [];
        this.getsystemconfigListByName("DUNNING_CATEGORY");
        this.childPlanRenewArray = this.fb.array([]);
        this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
            this.tdsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
            this.abbsPercent = res.data.value;
        });

        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
        const url = "/commonList/paymentMode";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.paymentMode = response.dataList;
            },
            (error: any) => { }
        );
    }

    planCreationType() {
        const planBindingType = localStorage.getItem("planBindingType");
        this.isPlanOnDemand = planBindingType === "On-Demand";
    }
    closeParentCustt() {
        this.ifModelIsShow = false;
    }

    openAddressDetails(customerData) {
        this.requestedByID = 0;
        this.ifUpdateAddressSubmited = false;
        this.ifUpdateAddress = true;
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.iflocationFill = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.assignInventoryWithSerial = false;
        this.getpartnerAll();
        this.getWalletData(customerData.id);
        this.searchPrepaidValue();
        if (customerData.serviceareaid) {
            this.isServiceInShiftLocation = true;
            this.shiftLocationDTO.updateAddressServiceAreaId = customerData.serviceareaid;
            this.shiftLocationPopId = customerData.popid;
            this.shiftLocationOltId = customerData.oltid;
            this.getPartnerAllByServiceArea(customerData.serviceareaid);
            this.getStaffDetailById();
            this.branchByServiceAreaID(customerData.serviceareaid);
            var customerAddress = customerData.addressList.find(address => address.version === "NEW");
            this.getPincodeList(customerData.serviceareaid, customerAddress.pincodeId);
            // this.getStaffDetailById(customerData.serviceareaid)
            const data = {
                value: Number(customerAddress.pincodeId)
            };
            this.selectPINCODEChange(data, "");
            this.branchID = customerData.branch;
        }
        if (customerData.partnerid) {
            this.shiftLocationDTO.shiftPartnerid = customerData.partnerid;
        }
        this.shiftLocationDTO.isPermanentAddress = false;
        this.shiftLocationDTO.isPaymentAddresSame = false;
        this.presentGroupForm.patchValue(customerAddress);

        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
        this.isStatusChangeSubMenu = false;
        this.staffSelectList = [];
        this.getStaffDetailById();
    }

    closeParentCust() {
        this.displayInvoiceDetails = false;
        this.showParentCustomerModel = false;
    }

    closeModel() {
        this.visibleQuotaDetails = false;
    }

    saveShiftLocation() {
        this.submit = true;
        this.submitted = true;
        this.ifUpdateAddressSubmited = true;

        if (
            (this.shiftLocationDTO.shiftPartnerid === "" && this.isBranchShiftLocation == false) ||
            (this.branchID == 0 && this.isBranchShiftLocation) ||
            this.requestedByID == 0 ||
            this.presentGroupForm.invalid
        ) {
            return this;
        }
        if (this.shiftLocationChargeGroupForm.valid) {
            if (this.shiftLocationChargeGroupForm.value.type == "Recurring") {
                this.shiftLocationChargeGroupForm.value.billingCycle = 1;
            }
            this.shiftLocationDTO.addressDetails = this.presentGroupForm.getRawValue();
            this.shiftLocationDTO.custChargeOverrideDTO = {
                billableCustomerId: this.shiftLocationChargeGroupForm.value.billableCustomerId,
                custChargeDetailsPojoList: [this.shiftLocationChargeGroupForm.value],
                custid: this.customerLedgerDetailData.id,
                paymentOwnerId: this.shiftLocationChargeGroupForm.value.paymentOwnerId
            };
            this.shiftLocationDTO.popid = this.shiftLocationPopId;
            this.shiftLocationDTO.oltid = this.shiftLocationOltId;
            this.shiftLocationDTO.requestedById = this.requestedByID;
            this.shiftLocationDTO.branchID = this.branchID;
            if (this.shiftLocationDTO.shiftPartnerid === "") {
                this.shiftLocationDTO.shiftPartnerid = 1;
            }
            if (this.shiftLocationDTO.branchID == 0 || !this.isBranchShiftLocation) {
                this.shiftLocationDTO.branchID = null;
            }
            if (this.shiftLocationDTO.popid == 0) {
                this.shiftLocationDTO.popid = null;
            }

            const url = "/shiftCustomerLocation/" + this.customerLedgerDetailData.id;
            this.commondropdownService.postMethod(url, this.shiftLocationDTO).subscribe(
                (response: any) => {
                    $("#openAddressForm").modal("hide");
                    this.toastr.success(`${response.message}`, 'Shift customer location successfully.!');


                    this.getCustomersDetail(this.customerLedgerDetailData.id);
                    this.getCustomerNetworkLocationDetail(this.customerLedgerDetailData.id);
                    this.getNewCustomerAddressForCustomer(this.customerLedgerDetailData.id);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    getLoggedinUserData() {
        const staffId = localStorage.getItem("userId");
        this.staffUserId = localStorage.getItem("userId");
        this.staffService.getById(staffId).subscribe(
            (response: any) => {
                this.staffUser = response.Staff;
                this.userName = this.staffUser.username;
                // if (["Admin"].some(role => this.staffUser.roleName.includes(role))) {
                //   this.isAdmin = true;
                // } else {
                //   this.isAdmin = false;
                // }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    assignTerminationForm: UntypedFormGroup;
    shiftlocationFormRemark: UntypedFormGroup;
    async ngOnInit() {
        let staffID = localStorage.getItem("userId");
        let loggedInUser = localStorage.getItem("loggedInUser");
        this.partnerId = Number(localStorage.getItem("partnerId"));
        this.staffCustList = [
            {
                id: Number(staffID),
                name: loggedInUser,
            },
        ];
        this.custType = this.route.snapshot.paramMap.get("custType")!;
        this.getLoggedinUserData();
        this.planCreationType();
        this.billingSequence();
        window.scroll(0, 0);
        this.assignAppRejectShiftLocationForm = this.fb.group({
            remark: ["", Validators.required],
        });

        this.custLedgerForm = this.fb.group({
            startDateCustLedger: ["", Validators.required],
            endDateCustLedger: ["", Validators.required]
        });
        this.searchLocationForm = this.fb.group({
            searchLocationname: ["", Validators.required]
        });
        this.planCategoryForm = this.fb.group({
            planCategory: [""]
        });

        this.planDataForm = this.fb.group({
            offerPrice: [""],
            discountPrice: [0]
        });

        this.extendValidityForm = this.fb.group({
            downStartDate: ["", Validators.required],
            downEndDate: ["", Validators.required],
            extend_validity_remarks: ["", Validators.required]
        });

        this.assignAppRejectDiscountForm = this.fb.group({
            remark: ["", Validators.required]
        });

        this.paymentFormGroup = this.fb.group({
            amount: [0, [Validators.required, Validators.min(1)]],
            bank: [""],
            branch: [""],
            chequedate: ["", Validators.required],
            chequeno: ["", [Validators.required, Validators.pattern(Regex.numeric)]],
            customerid: ["", Validators.required],
            paymode: ["", Validators.required],
            referenceno: ["", Validators.required],
            remark: ["", Validators.required],
            bankManagement: ["", Validators.required],
            destinationBank: ["", Validators.required],
            reciptNo: [""],
            type: ["Payment"],
            paytype: [""],
            file: [""],
            tdsAmount: [0],
            abbsAmount: [0],
            invoiceId: ["", Validators.required],
            onlinesource: [""]
        });
        // this.paymentFormGroup.controls.onlinesource.disable();
        // this.paymentFormGroup.controls.bank.disable();
        // this.paymentFormGroup.controls.branch.disable();
        // this.paymentFormGroup.controls.chequedate.disable();
        // this.paymentFormGroup.controls.bankManagement.disable();
        // this.paymentFormGroup.controls.chequeno.disable();
        // this.paymentFormGroup.controls.paymentreferenceno.disable();
        // this.paymentFormGroup.controls.barteramount.disable();
        this.resetPayMode();

        this.customerGroupForm = this.fb.group({
            username: ["", Validators.required],
            password: ["", Validators.required],
            firstname: ["", Validators.required],
            lastname: ["", Validators.required],
            email: ["", [Validators?.email]],
            title: ["", Validators.required],
            pan: ["", [Validators.minLength(9), Validators.maxLength(9)]],
            gst: [""],
            aadhar: [""],
            passportNo: [""],
            tinNo: ["", [Validators.minLength(9), Validators.maxLength(9)]],
            contactperson: ["", Validators.required],
            failcount: ["0"],
            // acctno: ['', Validators.required],
            custtype: [this.custType],
            custlabel: ["customer"],
            phone: [""],
            mobile: ["", [Validators.required, Validators.minLength(3)]],
            secondaryMobile: ["", Validators.minLength(3)],
            faxNumber: [""],
            dateOfBirth: [""],
            countryCode: [this.commondropdownService.commonCountryCode],
            dunningType: [""],
            dunningSubType: [""],
            dunningSector: [""],
            dunningSubSector: [""],
            cafno: [""],
            voicesrvtype: [""],
            didno: [""],
            calendarType: ["English", Validators.required],
            partnerid: [""],
            salesremark: [""],
            servicetype: [""],
            serviceareaid: ["", Validators.required],
            status: ["", Validators.required],
            parentCustomerId: [""],
            invoiceType: ["", Validators.required],
            parentExperience: ["Actual", Validators.required],
            latitude: [""],
            longitude: [""],
            // id:[],
            billTo: ["CUSTOMER"],
            billableCustomerId: [""],
            isInvoiceToOrg: [false],
            istrialplan: [false],
            popid: [""],
            staffId: [""],
            discount: ["", [Validators.max(99)]],
            flatAmount: [""],
            plangroupid: [""],
            discountType: [""],
            discountExpiryDate: [""],
            planMappingList: (this.payMappingListFromArray = this.fb.array([])),
            addressList: (this.addressListFromArray = this.fb.array([])),
            overChargeList: (this.overChargeListFromArray = this.fb.array([])),
            custMacMapppingList: (this.custMacMapppingListFromArray = this.fb.array([])),
            branch: [""],
            oltid: [""],
            masterdbid: [""],
            splitterid: [""],
            nasPort: [""],
            framedIp: [""],
            framedIpBind: [""],
            ipPoolNameBind: [""],
            valleyType: [""],
            customerArea: [""],
            // custDocList: this.uploadDocumentListFromArray = this.fb.array([ ]),
            paymentDetails: this.fb.group({
                amount: [""],
                paymode: [""],
                referenceno: [""],
                paymentdate: [""]
            }),
            isCustCaf: ["no"],
            dunningCategory: ["", Validators.required],
            billday: [""],
            department: [""]
        });
        if (this.custType === "Postpaid") {
            this.customerGroupForm.controls["billday"].setValidators(Validators.required);
            this.customerGroupForm.controls["billday"].updateValueAndValidity();
        }
        this.customerGroupForm.controls.invoiceType.disable();
        this.customerGroupForm.controls.parentExperience.disable();
        this.Subscriberform = this.fb.group({
            billinng: [""],
            remarks: [""]
        });
        this.planGroupForm = this.fb.group({
            planId: ["", Validators.required],
            service: ["", Validators.required],
            validity: ["", Validators.required],
            offerprice: [""],
            newAmount: [""],
            discountType: [""],
            discountExpiryDate: [""],
            discount: ["", [Validators.max(99)]],
            istrialplan: [""],
            invoiceType: ["", Validators.required]
        });
        this.planGroupForm.controls.invoiceType.disable();
        this.ticketGroupForm = this.fb.group({
            caseStatus: ["Open"],
            caseTitle: ["", Validators.required],
            caseType: ["", Validators.required],
            currentAssigneeId: [""],
            customerAdditionalEmail: [""],
            customerAdditionalMobileNumber: [""],
            customersId: ["", Validators.required],
            department: [""],
            file: [""],
            finalResolutionId: [""],
            firstRemark: [""],
            groupReasonId: ["", Validators.required],
            helperName: [""],
            nextFollowupDate: [""],
            nextFollowupTime: [""],
            priority: ["", Validators.required],
            reasonSubCategoryId: ["", Validators.required],
            rootCauseReasonId: [""],
            serviceAreaName: ["", Validators.required],
            source: [""],
            subSource: [""],
            ticketReasonCategoryId: ["", Validators.required],
            userName: ["", Validators.required],
            ticketServicemappingList: ["", Validators.required]
        });
        // this.ticketGroupForm.controls.userName.disable();
        // this.ticketGroupForm.controls.serviceAreaName.disable();
        // this.ticketGroupForm.controls.customersId.disable();
        // this.ticketGroupForm.controls.caseStatus.disable();
        this.ticketGroupForm.controls.finalResolutionId.disable();
        this.ticketGroupForm.controls.rootCauseReasonId.disable();
        this.customerGroupForm.controls.dunningSubType.disable();
        this.customerGroupForm.controls.dunningSubSector.disable();
        this.chargeGroupForm = this.fb.group({
            chargeid: ["", Validators.required],
            validity: ["", Validators.required],
            price: ["", Validators.required],
            actualprice: ["", Validators.required],
            charge_date: ["", Validators.required],
            type: ["Recurring", Validators.required],
            staticIPAdrress: [""],
            planid: ["", Validators.required],
            unitsOfValidity: ["", Validators.required],
            discount: [""],
            billingCycle: [""],
            expiry: ["", Validators.required]
        });
        this.shiftLocationChargeGroupForm = this.fb.group({
            chargeid: ["", Validators.required],
            price: ["", Validators.required],
            actualprice: ["", Validators.required],
            charge_date: ["", Validators.required],
            type: ["", Validators.required],
            discount: [""],
            billingCycle: [""],
            id: [""],
            billableCustomerId: [""],
            paymentOwnerId: ["", Validators.required]
        });
        this.overChargeChildListFromArray = this.fb.array([]);
        this.chargeChildGroupForm = this.fb.group({
            chargeid: ["", Validators.required],
            validity: ["", Validators.required],
            price: ["", Validators.required],
            actualprice: ["", Validators.required],
            charge_date: ["", Validators.required],
            type: ["", Validators.required],
            planid: ["", Validators.required],
            unitsOfValidity: ["", Validators.required],
            billingCycle: [""],
            id: [""]
        });
        this.macGroupForm = this.fb.group({
            macAddress: ["", Validators.required]
        });
        this.validityUnitFormArray = this.fb.array([]);
        this.plansArray = this.fb.array([]);
        this.validityUnitFormGroup = this.fb.group({
            validityUnit: [""]
        });
        this.addChargeForm = this.fb.group({
            chargeAdd: [""]
        });
        this.chargenewPlanForm = this.fb.group({
            plancharge: ["", Validators.required]
        });
        this.changenewPlanForm = this.fb.group({
            ChangePlanCategory: ["", Validators.required]
        });
        this.newPlanSelectArray = this.fb.array([]);
        // this.newPlanSelectArray = this.fb.group({
        //   newPlan: [""],
        // });

        this.presentGroupForm = this.fb.group({
            addressType: ["Present", Validators.required],
            landmark: ["", Validators.required],
            areaId: ["", Validators.required],
            pincodeId: ["", Validators.required],
            cityId: ["", Validators.required],
            stateId: ["", Validators.required],
            countryId: ["", Validators.required],
            landmark1: [""]
        });
        this.paymentGroupForm = this.fb.group({
            addressType: ["Payment", Validators.required],
            landmark: [""],
            areaId: [""],
            pincodeId: [""],
            cityId: [""],
            stateId: [""],
            countryId: [""],
            landmark1: [""]
        });
        this.permanentGroupForm = this.fb.group({
            addressType: ["Permanent"],
            landmark: [""],
            areaId: [""],
            pincodeId: [""],
            cityId: [""],
            stateId: [""],
            countryId: [""],
            landmark1: [""]
        });

        this.changePasswordForm = this.fb.group({
            custId: [""],
            newpassword: ["", Validators.required],
            password: [""],
            remarks: [""],
            selfcarepwd: [""]
        });

        this.changePlanForm = this.fb.group({
            connectionNo: [null, Validators.required],
            serviceName: [null],
            serviceNickName: [null],
            purchaseType: ["", Validators.required],
            planId: ["", Validators.required],
            planGroupId: ["", Validators.required],
            planList: [""],
            isPaymentReceived: ["false"],
            remarks: ["", Validators.required],
            paymentOwnerId: ["", Validators.required],
            billableCustomerId: [""],
            addonStartDate: [this.currentData],
            ChangePlanCategory: [""]
        });
        this.changePlanNewForm = this.fb.group({
            isPaymentReceived: ["false"],
            remarks: ["", Validators.required],
            paymentOwnerId: ["", Validators.required],
            billableCustomerId: [""],
            externalRemark: [""]
        });
        this.childPlanRenewArray = this.fb.array([]);
        this.changePlanForm.get("planGroupId").disable();
        this.changePlanForm.get("planList").disable();
        // this.changePlanForm.get("recordPaymentDTO").disable();
        this.searchAcctCdrForm = this.fb.group({
            userName: [this.customerLedgerDetailData.custname],
            framedIpAddress: [""],
            fromDate: [""],
            toDate: [""]
        });

        this.searchInvoiceMasterFormGroup = this.fb.group({
            billfromdate: [""],
            billrunid: [""],
            billtodate: [""],
            custMobile: [""],
            custname: [""],
            docnumber: [""],
            customerid: [""]
        });
        // dropdown
        this.commondropdownService.getPOPList();
        this.commondropdownService.getplanservice();
        // this.commondropdownService.getAllPinCodeNumber();
        this.commondropdownService.getAllPinCodeData();
        // this.commondropdownService.getALLArea();
        this.commondropdownService.getCommonListTitleData();
        this.commondropdownService.getCommonListPaymentData();
        this.commondropdownService.getIppoolData();
        await this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        this.commondropdownService.getChargeForCustomer();
        this.commondropdownService.getchargeAll();
        this.commondropdownService.getChargeTypeByList();
        this.commondropdownService.getPlanPurchaseType();
        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.getsystemconfigList();
        this.commondropdownService.findAllplanGroups();
        this.commondropdownService.getBillToData();
        this.commondropdownService.getTicketPriority();
        this.commondropdownService.getAllActiveBranch();
        this.commondropdownService.getValleyTypee();
        this.commondropdownService.getInsideValley();
        this.commondropdownService.getOutsideValley();
        this.commondropdownService.getBankDetail();
        this.commondropdownService.getBankDestinationDetail();
        this.commondropdownService.getAllActiveStaff();
        this.commondropdownService.getTeamList();
        this.commondropdownService.getQosPolicy();
        this.getCustomerType();
        this.getCustomerSector();
        this.getBankDetail();
        const serviceArea = localStorage.getItem("serviceArea");
        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
            // this.commondropdownService.filterPartnerAll();
        } else {
            this.commondropdownService.getserviceAreaList();
            // this.commondropdownService.getpartnerAll();
        }
        this.getpartnerAll();
        

        // this.productService.getAllProductByServiceId().subscribe((res: any) => {
        //   this.products = res.dataList;
        // });

        this.inventoryAssignForm.get("qty").valueChanges.subscribe(val => {
            const total = this.availableQty - val;
            if (total < 0) {
                this.showQtyError = true;
            } else {
                this.showQtyError = false;
            }

            if (this.productHasMac == true && this.selectedMACAddress.length > val) {
                this.showQtySelectionError = true;
            } else {
                this.showQtySelectionError = false;
            }
        });

        // customer get data
        this.getcustomerList("");

        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: "",
            pageSize: ""
        };
        this.assignTerminationForm = this.fb.group({
            remark: [""]
        });
        this.shiftlocationFormRemark = this.fb.group({
            remark: [""]
        });
        this.getrequiredDepartment();
    }

    getPartnerAllByServiceArea(serviceAreaId) {
        const url = "/getPartnerByServiceAreaId/" + serviceAreaId;
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerListByServiceArea = response.partnerList.filter(item => item.id != 1);
                // console.log("partnerList", response);
            },
            (error: any) => { }
        );
    }

    getpartnerAll() {
        const url = "/partner/all";
        this.partnerService.getMethodNew(url).subscribe(
            (response: any) => {
                this.partnerList = response.partnerlist.filter(item => item.id != 1);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    daySequence() {
        for (let i = 0; i < 31; i++) {
            this.days.push({ label: i + 1 });
        }
    }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus";
        this.recordPaymentService.getMethod(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getCustomer() {
        const url = "/customers/list";
        const custerlist = {};
        this.recordPaymentService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerData = response.customerList;
                this.paymentFormGroup.patchValue({
                    customerid: this.customerLedgerDetailData.id
                });
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    calculateTDS(event) {
        if (!event.target.checked) {
            this.tdsInclude = false;
            this.paymentFormGroup.controls.tdsAmount.disable();
            this.paymentFormGroup.controls.tdsAmount.setValue(0);
        } else {
            this.tdsInclude = true;
            this.paymentFormGroup.controls.tdsAmount.enable();
            this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
        }
    }

    calculateABBS(event) {
        if (!event.target.checked) {
            this.abbsInclude = false;
            this.paymentFormGroup.controls.abbsAmount.disable();
            this.paymentFormGroup.controls.abbsAmount.setValue(0);
        } else {
            this.abbsInclude = true;
            this.paymentFormGroup.controls.abbsAmount.enable();
            this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
        }
    }

    onChangeOFAmount(event) {
        let tdsAmount = ((event * this.tdsPercent) / 100).toFixed(2);
        let abbsAmount = ((event * this.abbsPercent) / 100).toFixed(2);

        // let tdsAmount = 0;
        // let abbsAmount = 0;
        // this.checkedList.forEach(element => {
        //   tdsAmount += element.includeTds ? (element.totalamount * this.tdsPercent) / 100 : 0;
        //   abbsAmount += element.includeAbbs ? (element.totalamount * this.abbsPercent) / 100 : 0;
        // });
        if (!this.paymentFormGroup.controls.abbsAmount.disabled && this.abbsInclude) {
            this.paymentFormGroup.controls.abbsAmount.setValue(abbsAmount);
        }
        if (!this.paymentFormGroup.controls.tdsAmount.disabled && this.tdsInclude) {
            this.paymentFormGroup.controls.tdsAmount.setValue(tdsAmount);
        }
    }

    modalOpenInvoice(id) {
        $("#selectInvoice").modal("show");
        if (id) {
            this.InvoiceListByCustomer(id);
        }
        this.newFirst = 0;
    }

    Amount: any = 0;

    bindInvoice() {
        if (this.checkedList.length >= 1) {
            this.isShowInvoiceList = true;
            this.Amount = 0;
            this.checkedList.forEach(element => {
                if (element.testamount && element.totalamount !== null) {
                    this.Amount =
                        element.adjustedAmount && element.adjustedAmount != null
                            ? element.testamount + this.Amount
                            : element.testamount + this.Amount;
                }
            });
            this.paymentFormGroup.patchValue({
                invoiceId: this.checkedList[0].id,
                amount: parseFloat(this.Amount).toFixed(2)
            });
            this.onChangeOFAmountTest(this.checkedList);
        }
        if (this.checkedList.length == 0) {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please select atleast one invoice or advance mod!');
            }

        } else if (this.checkedList.length == 2) {
            this.checkedList.forEach(element => {
                if (element.docnumber == "Advance") {
                    this.checkedList = [];
                    this.invoiceList.forEach(element => {
                        element.isSelected = false;
                    });
                    this.masterSelected = false;
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Please select advance mode value only!');
                    }


                }
            });
        }
    }

    isAllSelectedInvoice() {
        this.masterSelected = this.invoiceList.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemListInvoice();
    }

    checkUncheckAllInvoice() {
        for (let i = 0; i < this.invoiceList.length; i++) {
            this.invoiceList[i].isSelected = this.masterSelected;
        }
        this.getCheckedItemListInvoice();
    }

    getCheckedItemListInvoice() {
        this.checkedList = [];
        for (let i = 0; i < this.invoiceList.length; i++) {
            if (this.invoiceList[i].isSelected) {
                this.checkedList.push(this.invoiceList[i]);
            }
        }
    }

    addPayment(paymentId) {
        this.submitted = true;
        if (this.paymentFormGroup.valid) {
            if (this.changePlanForm.controls.isPaymentReceived.value == "true") {
                this.changePlanForm.controls.isPaymentReceived.setValue(false);
                this.changePlan();
            } else {
                if (this.paymentFormGroup.value.invoiceId == 0) {
                    this.paymentFormGroup.value.paytype = "advance";
                } else {
                    this.paymentFormGroup.value.paytype = "invoice";
                }

                if (this.checkedList.length == 0) {
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Please select atleat one invoice or advance mode!');
                    }

                    return;
                } else {
                    const url = "/record/payment";
                    this.paymentFormGroup.value.customerid = this.customerLedgerDetailData.id;
                    this.paymentFormGroup.value.type = "Payment";
                    this.createPaymentData = this.paymentFormGroup.value;
                    this.createPaymentData.onlinesource = this.paymentFormGroup.controls.onlinesource.value;
                    this.createPaymentData.filename = this.fileName;
                    let invoiceId = [];
                    // const invoices = [];
                    this.checkedList.forEach(element => {
                        invoiceId.push(element.id);
                        // invoices.push({
                        //   id: element.id,
                        //   amount: element.paymentAmount,
                        //   includeTds: element.includeTds,
                        //   includeAbbs: element.includeAbbs,
                        // });
                    });
                    this.createPaymentData.invoiceId = invoiceId;
                    // this.createPaymentData.invoices = invoices;
                    delete this.createPaymentData.file;
                    const formData = new FormData();
                    var paymentListPojos = [];
                    this.checkedList.forEach(element => {
                        let data = {
                            tdsAmountAgainstInvoice: element.tds,
                            abbsAmountAgainstInvoice: element.abbs,
                            amountAgainstInvoice: element.testamount,
                            invoiceId: element.id
                        };
                        paymentListPojos.push(data);
                    });
                    this.createPaymentData.paymentListPojos = paymentListPojos;
                    formData.append("file", this.file);
                    formData.append("spojo", JSON.stringify(this.createPaymentData));
                    this.recordPaymentService.postMethod(url, formData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.paymentFormGroup.reset();
                            this.openCustomersPaymentData(this.customerIdRecord, "");
                            this.currentPagecustomerPaymentdata = 1;
                            this.invoiceList = [];
                            this.toastr.success(`${response.responseMessage}`, 'Payment Created Successfully!');

                            $("#recordPayment").modal("hide");
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    );
                }
            }
        }
    }

    closePaymentForm() {
        this.paymentFormGroup.reset();
        this.submitted = false;
        this.isShowInvoiceList = false;
        this.checkedList = [];
    }

    createnewCustomer() {
        if (
            !this.loginService.hasOperationPermission(
                AclClassConstants.ACL_CUSTOMER,
                AclConstants.OPERATION_CUSTOMER_ADD,
                AclConstants.OPERATION_CUSTOMER_ALL
            )
        ) {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Sorry you have not privilege to create operation!');
            }

        } else {
            this.listView = false;
            this.createView = true;
            this.selectAreaList = false;
            this.selectPincodeList = false;
            // this.listSearchView = false;
            this.isCustomerDetailOpen = false;
            this.isCustomerDetailSubMenu = false;
            this.customerChangePlan = false;
            this.isCustomerLedgerOpen = false;
            this.customerPlanView = false;
            this.customerTicketView = false;
            this.ifcustomerDiscountField = false;
            this.submitted = false;
            this.plansubmitted = false;
            this.iscustomerEdit = false;
            this.viewCustomerPaymentList = false;
            this.iflocationFill = false;
            this.ifCDR = false;
            this.ifMyInvoice = false;
            this.ifShowDBRReport = false;
            this.ifChargeGetData = false;
            this.isServiceOpen = false;
            this.isCreditNoteOpen = false;
            this.ifWalletMenu = false;
            this.ifUpdateAddress = false;
            this.isStatusChangeSubMenu = false;
            this.ifIndividualPlan = false;
            this.ifPlanGroup = false;
            this.isParantExpirenceEdit = false;
            this.payMappingListFromArray.controls = [];
            this.overChargeListFromArray.controls = [];
            this.custMacMapppingListFromArray.controls = [];
            this.custMacMapppingListFromArray.controls = [];
            this.planCategoryForm.reset();
            // this.uploadDocumentListFromArray.controls = [];
            this.customerGroupForm.controls.username.enable();
            this.customerFormReset();
            this.selCustType();
            if (this.custType === RadiusConstants.CUSTOMER_TYPE.POSTPAID) {
                this.daySequence();
            }
            this.planGroupForm.controls.service.enable();
            this.planGroupForm.controls.planId.enable();
            this.planGroupForm.controls.validity.enable();
            this.customerGroupForm.controls.invoiceType.disable();
            this.customerGroupForm.controls.parentExperience.disable();
            this.planGroupForm.controls.invoiceType.disable();
            this.customerGroupForm.controls.dunningSubType.disable();
            this.customerGroupForm.controls.dunningSubSector.disable();
            this.serviceAreaDisable = false;
            // this.uploadDocumentGroupForm.reset();
            this.viewcustomerListData = [];
            this.addressListData = [];

            this.customerGroupForm.controls.calendarType.setValue("English");
            this.customerGroupForm.controls.custlabel.setValue("customer");
            this.customerGroupForm.patchValue({
                countryCode: this.commondropdownService.commonCountryCode
            });
            this.serviceareaCheck = true;

            this.selCustType();
            // if (!this.isAdmin) {
            //   this.customerGroupForm.patchValue({
            //     serviceareaid: this.staffUser.serviceAreaId,
            //   });
            // }
            this.customerrMyInventoryView = false;
            this.assignInventoryWithSerial = false;
            this.customerChildsView = false;
            this.FinalAmountList = [];
            this.DiscountValueStore = [];
            this.planTotalOffetPrice = 0;
            this.customerUpdateDiscount = false;
            this.shiftLocationEvent = false;
            this.ifWorkflowAuditShow = false;
            this.ifAuditDetailsShow = false;
            this.getNetworkDevicesByType("OLT");
            this.getNetworkDevicesByType("Splitter");
            this.getNetworkDevicesByType("Master DB/DB");
        }
    }

    oltDevices = [];
    spliterDevices = [];
    masterDbDevices = [];
    getNetworkDevicesByType(deviceType) {

        const url = "/NetworkDevice/getNetworkDevicesByDeviceType?deviceType=" + deviceType;
        this.networkdeviceService.getMethod(url).subscribe(
            (response: any) => {
                switch (deviceType) {
                    case "OLT":
                        this.oltDevices = response.dataList;
                        break;
                    case "Splitter":
                        this.spliterDevices = response.dataList;
                        break;
                    case "Master DB/DB":
                        this.masterDbDevices = response.dataList;
                        break;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    listCustomer() {
        this.listView = true;
        this.isDisable = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        //  this.listSearchView = true;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = false;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.iflocationFill = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.customerChildsView = false;
        this.editCustomerId = "";
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    listdetalisCostomer() {
        this.listView = true;
        // this.listSearchView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = false;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.viewCustomerPaymentList = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    customerDetailOpen(custId) {
        this.custDetilsCustId = custId;
        this.listView = false;
        //  this.listSearchView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        // this.getCustomersLedger(custId);
        this.customerIdRecord = custId;
        this.getCustomer();
        this.getCustomersDetail(custId);
        this.getCustomerNetworkLocationDetail(custId);
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.viewCustomerPaymentList = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.getCustQuotaList(custId);
        this.getReturnItemList(custId);
        this.getChildCustomers(custId);
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.getNewCustomerAddressForCustomer(custId);
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
        this.auditData = custId;
        this.GetAuditData(custId, "");
    }

    customerLedgerOpen() {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = true;
        this.customerPlanView = false;
        this.customerTicketView = false;

        this.viewCustomerPaymentList = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    openCustorUpdateDiscount(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.customerChildsView = false;
        this.ifAuditDetailsShow = false;
        this.customerUpdateDiscount = true;
        this.getcustDiscountDetails(id, "", "changeDiscount");
        this.assignInventoryWithSerial = false;
    }

    openCustomersPlan(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = true;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.getcustFuturePlan(id, "");
        this.getcustExpiredPlan(id, "");
        this.getcustCurrentPlan(id, "");
        this.getcustDiscountDetails(id, "");
        this.getTrailPlanList(id, "");
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
        this.extendValidityBulkFlag = false;
        this.promiseToPayBulkFlag = false;
        this.extendValidityBulkId = [];
        this.promiseToPayBulkId = [];
    }

    openCustomersTicket(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = true;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.isStatusChangeSubMenu = false;
        this.ifUpdateAddress = false;
        this.getcustTicket(id, "");
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    ticketModelOpen(id) {
        this.ticketGroupForm.reset();
        this.ticketGroupForm.get("customersId").setValue(this.customerLedgerDetailData.id);
        this.ticketGroupForm.controls.serviceAreaName.setValue(
            this.customerLedgerDetailData.serviceareaName
        );
        this.ticketGroupForm.controls.userName.setValue(this.customerLedgerDetailData.username);
        this.getReasonCategoryByCustomer();
        this.getCaseStatus();
        this.getPriority();
        this.getCaseType();
        this.getDepartmentType();
        this.getTicketSourceType();
        this.getservicesByCustomer(id);
        $("#ticketModel").modal("show");

        this.ticketGroupForm.patchValue({
            priority: "Low"
        });
    }

    getTicketSourceType() {
        const url = "/commonList/ticketSourceType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.ticketSourceTypeData = response.dataList;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    onFileChangeTicket(event) {
        if (event.target.files.length > 0) {
            const file = event.target.files;
            this.ticketGroupForm.patchValue({
                file: file
            });
            // this.custmerDoc.filename = "";
        }
    }

    getResolutionRootCause(value: string): void {
        this.rootCauseReasonData = [];
        this.ticketGroupForm.controls.rootCauseReasonId.enable();
        this.resolutionReasonData.forEach(e => {
            if (e.id === value) {
                e.rootCauseResolutionMappingList.forEach(f => this.rootCauseReasonData.push(f));
                // this.rootCauseReasonData.push(e.rootCauseResolutionMappingList);
            }
        });
    }

    getDepartmentType() {
        const url = "/commonList/departmentType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.departmentTypeData = response.dataList;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    selectDeparment(event) {
        if (this.ticketReasonCategoryData) {
            let array: [] = this.ticketReasonCategoryData;
            this.filteredReasonCategoryList = [];
            array.filter((e: any) => {
                if (e == null || e.department === event.value) {
                    this.filteredReasonCategoryList.push(e);
                }
            });
        }
    }

    getCaseStatus() {
        const url = "/commonList/caseStatus";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.statusData = response.dataList;
                this.ticketGroupForm.controls.caseStatus.setValue("Open");
                // console.log("this.statusData", this.statusData);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getReasonCategoryByCustomer() {
        const url =
            "/ticketReasonCategory/getReasonCategoryByCustomer?customerId=" +
            this.customerLedgerDetailData.id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.ticketReasonCategoryData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getPriority() {
        const url = "/commonList/priority";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.priorityData = response.dataList;
                // console.log("this.priorityData", this.priorityData);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    selReasonCategory(event) {
        this.getSubCategoryByparentCat(event.value);
    }

    getSubCategoryByparentCat(id) {
        const url = "/ticketReasonSubCategory/getSubCategoryReasons?parentCategoryId=" + id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.ticketReasonSubCategoryData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getGroupReasonBySubCat(id): void {
        const selSubCatData = this.ticketReasonSubCategoryData.filter(subCat => subCat.id === id);
        this.groupReasonData = selSubCatData[0].ticketSubCategoryGroupReasonMappingList;
        // console.log('this.groupReasonData', this.groupReasonData);
    }

    selReasonSubCategory(event) {
        this.getGroupReasonBySubCat(event.value);
    }

    getCaseType() {
        const url = "/commonList/caseType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.caseTypeData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    addEditTicket() {
        this.submitted = true;
        if (this.ticketGroupForm.valid) {
            let formData: any = new FormData();
            this.createTicketData = this.ticketGroupForm.getRawValue();

            if (
                this.ticketGroupForm.controls.caseTitle.value == "" ||
                this.ticketGroupForm.controls.caseTitle.value == null
            ) {
                this.ticketReasonCategoryData.filter((item: any) => {
                    if (item.id == this.ticketGroupForm.controls.ticketReasonCategoryId.value) {
                        this.createTicketData.caseTitle = item.categoryName;
                    }
                });
            }
            this.createTicketData.caseForPartner = "Customer";
            this.createTicketData.caseFor = "Customer";
            this.createTicketData.caseOrigin = "Phone";
            this.createTicketData.firstRemark = this.ticketGroupForm.controls.firstRemark.value;
            let fileArray: FileList;
            if (this.createTicketData.file) {
                fileArray = this.createTicketData.file;
                Array.from(fileArray).forEach(file => {
                    formData.append("file", file);
                });
            }
            this.createTicketData.file = "";
            let newFormData = Object.assign({}, this.createTicketData);
            formData.append("entityDTO", JSON.stringify(newFormData));
            const url = "/case/save";
            this.customerManagementService.postMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode === 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    } else if (response.responseCode === 401) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');


                    } else {
                        this.ticketGroupForm.reset();
                        this.ticketGroupForm.controls.caseStatus.setValue("Unassigned");
                        this.getcustTicket(this.customerLedgerDetailData.id, "");
                        $("#ticketModel").modal("hide");
                        this.toastr.success(`${response.message}`, 'Successfully!');


                        this.submitted = false;
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    customerServiceData: any;

    getservicesByCustomer(id) {
        const url = "/ticketReasonCategory/getActiveServiceForSubscribers?customerId=" + id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerServiceData = response.dataList;
                // this.filteredReasonCategoryList = this.ticketReasonCategoryData;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getTicketReasonCategory(serviceLists: any) {
        serviceLists = this.ticketGroupForm.controls.ticketServicemappingList.value;
        if (serviceLists != null) {
            const url = "/ticketReasonCategory/getReasonCategoryByActiveServices";
            this.customerManagementService.postMethod(url, serviceLists).subscribe(
                (response: any) => {
                    this.ticketReasonCategoryData = response.dataList;
                    this.filteredReasonCategoryList = this.ticketReasonCategoryData;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    clearcustPlanMappping() {
        this.custPlanMapppingId = null;
        this.isDisableServicePlanChange = false;
        this.isDisableServiceType = null;
    }

    changePlanType($event) {
        this.planByService = [];
        this.parentPurchaseType = $event.value;
        this.isDisableServicePlanChange = false;
        if (this.selectedCustService != null) {
            if (this.selectedCustService.custPlanStatus.toLowerCase() == "hold") {
                this.isDisableServicePlanChange = true;
                this.isDisableServiceType = "Hold";
            } else if (this.selectedCustService.custPlanStatus.toLowerCase() == "disable") {
                this.isDisableServicePlanChange = true;
                this.isDisableServiceType = "Disable";
            } else if (this.selectedCustService.custPlanStatus.toLowerCase() == "terminate") {
                this.isDisableServicePlanChange = true;
                this.isDisableServiceType = "Terminate";
            }
            switch ($event.value) {
                case "Changeplan": {
                    if (this.selectedCustService.custPlanStatus.toLowerCase() == "ingrace") {
                        this.isDisableServicePlanChange = true;
                        this.isDisableServiceType = "InGrace";
                    }
                    break;
                }
                case "Renew": {
                    if (this.selectedCustService.istrialplan) {
                        this.isDisableServicePlanChange = true;
                        this.isDisableServiceType = "Trail";
                    }
                    break;
                }
                case "Addon": {
                    if (this.selectedCustService.custPlanStatus.toLowerCase() == "stop") {
                        this.isDisableServicePlanChange = true;
                        this.isDisableServiceType = "Stop";
                    } else if (this.selectedCustService.custPlanStatus.toLowerCase() == "ingrace") {
                        this.isDisableServicePlanChange = true;
                        this.isDisableServiceType = "InGrace";
                    } else if (this.selectedCustService.istrialplan) {
                        this.isDisableServicePlanChange = true;
                        this.isDisableServiceType = "Trail";
                    }
                    break;
                }
                default: {
                    this.isDisableServicePlanChange = false;
                }
            }
        }

        if (this.childPlanRenewArray != null && this.childPlanRenewArray.length > 0) {
            this.childPlanRenewArray.controls.forEach(element => {
                element.get("planType").setValue($event.value);
            });
        }

        this.chargenewPlanForm.reset();
        if (this.customerLedgerDetailData.plangroupid) {
            // this.filterPlanListCust = [];
            // this.newPlanGroupData.forEach(planGroup => {
            //   planGroup.planMappingList.forEach(planMapping => {
            //     this.filterPlanListCust.push(planMapping.plan);
            //   });
            // });
        }
        this.changePlanForm.reset(this.changePlanForm.value);
        this.changenewPlanForm.controls.ChangePlanCategory.reset();
        this.selPlanData = [];
        this.finalOfferPrice = 0;
        this.changePlanForm.patchValue({
            purchaseType: $event.value,
            planGroupId: "",
            isPaymentReceived: "false",
            planId: "",
            plancharge: ""
        });
        this.isPlanTypeAddon = false;
        if (!this.customerLedgerDetailData.plangroupid) {
            this.planListByType = [];
        }
        if ($event.value != null && $event.value != undefined) {

            if ($event.value === "Addon") {
                this.isPlanTypeAddon = true;
                this.changeplanGroupFlag = false;
                if (!this.customerLedgerDetailData.plangroupid) {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.serviceId == this.selectedCustService.serviceId &&
                            (plan.planGroup === "Volume Booster" ||
                                plan.planGroup === "Bandwidthbooster" ||
                                plan.planGroup === "DTV Addon")
                    );
                    // this.filterplan();
                } else {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.serviceId == this.selectedCustService.serviceId &&
                            (plan.planGroup === "Volume Booster" ||
                                plan.planGroup === "Bandwidthbooster" ||
                                plan.planGroup === "DTV Addon")
                    );
                }
                this.changePlanForm.get("planGroupId").disable();
                this.changePlanForm.get("planList").disable();
                this.changePlanForm.get("planId").enable();
                this.planGroupFlag = false;
                if (!this.customerLedgerDetailData.plangroupid) {
                    this.getserviceData("");
                } else {
                    this.getserviceData(this.customerLedgerDetailData.planGroupId);
                }
                this.planByService = this.planListByType;

                setTimeout(() => {
                    this.qosSpeedBasefilterPlanData(this.planByService);
                }, 500);

            } else if ($event.value === "Changeplan") {
                if (this.customerLedgerDetailData.plangroupid) {

                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.planGroup === "Registration" || plan.planGroup === "Registration and Renewal"
                    );
                    this.planByService = this.planListByType;
                    this.changePlanForm.get("planGroupId").enable();
                    this.changePlanForm.get("planList").enable();
                    this.changePlanForm.get("planId").disable();
                    this.planGroupFlag = true;
                    this.getPlangroupByPlan(this.customerLedgerDetailData.plangroupid);

                    if (
                        this.customerLedgerDetailData.plangroupid != null ||
                        this.changePlanCustCurrentPlan.length > 1
                    ) {
                        this.changeplanGroupFlag = true;
                    } else {
                        this.changeplanGroupFlag = false;
                    }
                }
                if (!this.customerLedgerDetailData.plangroupid) {
                    this.changePlanForm.get("planGroupId").disable();
                    this.changePlanForm.get("planList").disable();
                    this.changePlanForm.get("planId").enable();
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.planGroup === "Registration" || plan.planGroup === "Registration and Renewal"
                    );
                    this.planByService = this.planListByType;
                    this.planGroupFlag = false;
                    if (this.changePlanCustCurrentPlan.length > 1) {
                        this.changeplanGroupFlag = true;
                    } else {
                        this.changeplanGroupFlag = false;
                    }
                    this.filterplan();
                } else {
                    this.filterSelectedPlanGroupListCust = this.newPlanGroupData.filter(
                        plan =>
                            plan.planGroupType === "Registration" ||
                            plan.planGroupType === "Registration and Renewal"
                    );
                }
                this.isPlanTypeAddon = false;
            } else if ($event.value === "Renew") {
                if (this.customerLedgerDetailData.plangroupid) {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.serviceId == this.selectedCustService.serviceId &&
                            (plan.planGroup === "Renew" || plan.planGroup === "Registration and Renewal")
                    );
                    this.changePlanForm.get("planGroupId").enable();
                    this.changePlanForm.get("planList").enable();
                    this.changePlanForm.get("planId").enable();
                    this.planGroupFlag = true;
                    this.getPlangroupByPlan(this.customerLedgerDetailData.plangroupid);
                    this.filterSelectedPlanGroupListCust = this.newPlanGroupData.filter(
                        plan =>
                            plan.planGroupType === "Renew" || plan.planGroupType === "Registration and Renewal"
                    );
                    if (
                        this.customerLedgerDetailData.plangroupid != null ||
                        this.changePlanCustCurrentPlan.length > 1
                    ) {
                        this.changeplanGroupFlag = true;
                    } else {
                        this.changeplanGroupFlag = false;
                    }
                } else {
                    this.changePlanForm.get("planGroupId").disable();
                    this.changePlanForm.get("planList").disable();
                    this.changePlanForm.get("planId").enable();
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.serviceId == this.selectedCustService.serviceId &&
                            (plan.planGroup === "Renew" || plan.planGroup === "Registration and Renewal")
                    );
                    this.changeplanGroupFlag = false;
                    this.planGroupFlag = false;
                    this.filterplan();
                }
                this.isPlanTypeAddon = false;
            }
        }

        this.planListByType.forEach(e => {
            if (e.quotatype == "Data") {
                e.label =
                    e.name +
                    ` (${this.selectedCustService.is_qosv ? e.quota + " " + e.quotaUnit : ""}
          ${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval + " - "}${e.validity
                    } ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
            } else if (e.quotatype == "Time") {
                e.label =
                    e.name +
                    ` (${e.quotatime} ${e.quotaunittime}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval + " - "
                    }${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
            } else if (e.quotatype == "Both") {
                e.label =
                    e.name +
                    ` (${this.selectedCustService.is_qosv ? e.quota + " " + e.quotaUnit : ""}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval + " and "
                    }${e.quotatime} ${e.quotaunittime}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval
                    }  - ${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
            } else {
                e.label = e.name;
            }
        });
    }

    qosSpeedBasefilterPlanData(planData) {
        let QosPolicyListData: any = {};
        let planFilterData: any = [];
        let filerQosData: any = [];
        let qosSpeed: any;
        this.planByService = [];
        let selectPlan: any = [];
        planFilterData = planData;
        let lengthPlan = planFilterData.length;
        let lengthQos: any;
        const url = "/qosPolicy/" + this.selectCircuitPlanqosPolicyId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            QosPolicyListData = response.data;
            qosSpeed = QosPolicyListData.qosspeed;

            filerQosData = this.commondropdownService.qosPolicyData.filter(
                (e: any) =>
                    Number(e.qosspeed) === Number(qosSpeed) ||
                    Number(e.qosspeed) > Number(qosSpeed) ||
                    e.qosspeed == null
            );
            lengthQos = filerQosData.length;

            filerQosData.forEach((qosEle, i) => {
                let n = i + 1;
                planFilterData.forEach((plan, j) => {
                    let m = j + 1;
                    if (plan.planGroup === "Volume Booster") {
                        selectPlan.push(plan);
                    } else {
                        if (qosEle.id == plan.qospolicyid || plan.qospolicyid == null) {
                            selectPlan.push(plan);
                        }
                    }
                    if (n == lengthQos && m == lengthPlan) {
                        this.planByService = [...new Set(selectPlan)];
                    }
                });
            });
        });
    }

    custServiceData: any = [];
    planSelected: any;
    planByService: any = [];
    changePlanRemark: string;
    isShowConnection = true;
    serviceSerialNumbers = [];

    getserviceData(groupId) {
        let services = [];
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerLedgerDetailData.id +
            "?isAllRequired=true" +
            "&isNotChangePlan=false";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custServiceData = [];
                var keepGping = false;
                response.dataList.forEach(service => {
                    if (!this.custServiceData.find(srv => srv.connection_no === service.connection_no)) {
                        this.custServiceData.push(service);
                    }
                });
                if (this.custServiceData.length > 0) {
                    this.serviceSerialNumbers = [];
                    this.custServiceData.forEach(item => {
                        if (!keepGping) {
                            var filteredItem = item.customerInventorySerialnumberDtos.filter(
                                item => item.primary
                            );
                            if (filteredItem.length > 0) {
                                this.isShowConnection = false;
                                this.serviceSerialNumbers.push({
                                    serialNumber: filteredItem[0].serialNumber,
                                    custPlanMapppingId: item.custPlanMapppingId
                                });
                            } else {
                                this.isShowConnection = true;
                                this.serviceSerialNumbers = [];
                                keepGping = true;
                            }
                        }
                    });
                }
                let data = this.custServiceData;
                this.custServiceData = [];
                data.forEach(element => {
                    if (element.custPlanStatus.toLowerCase() != "terminate") {
                        this.custServiceData.push(element);
                    }
                });

                // services = [...new Set(services)];
                //Need to update conditions when we have more than one current plan or have one group plan then need to open selectPlanGroupChangeService modal open
                if (
                    (groupId != null && groupId != "") ||
                    (this.custServiceData.length > 1 &&
                        this.changenewPlanForm.value.ChangePlanCategory == "groupPlan") ||
                    this.changePlanForm.value.purchaseType === "Renew"
                ) {
                    this.planSelected = null;
                    this.changePlanRemark = null;
                    this.planGroupSelected = groupId;
                    this.getPlanListByGroupId();
                    $("#selectPlanGroupChangeService").modal("show");
                    this.enableChangePlanGroup = false;
                } else {
                    this.planSelected = null;
                    this.changePlanRemark = null;
                    // this.custPlanMapppingId = this.custServiceData[0].custPlanMapppingId;
                    // $("#selectPlanChangeService").modal("show");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    onServiceChange(event) {
        this.isDisableServicePlanChange = false;
        this.changePlanForm.controls.purchaseType.setValue("");
        this.changePlanForm.patchValue({
            purchaseType: "",
            planGroupId: "",
            planId: "",
            ChangePlanCategory: ""
        });
        this.changenewPlanForm.patchValue({
            ChangePlanCategory: "",
            plancharge: ""
        });
        this.custPlanMapppingId = event.value ? event.value : event;
        this.filterplan();
    }

    selectCircuitPlanqosPolicyId: any;

    filterplan() {
        let i: number;
        // this.custCurrentPlanList.forEach(element => {
        //   if (element.custPlanMapppingId == this.custPlanMapppingId) {
        //     i = element.serviceId;
        //     this.selectedCustService = element;
        //   }
        // });
        this.selectCircuitPlanqosPolicyId = "";
        this.custServiceData.forEach(element => {
            if (element.custPlanMapppingId == this.custPlanMapppingId) {
                i = element.serviceId;
                this.selectedCustService = element;
                this.selectCircuitPlanqosPolicyId = element.qosPolicyId;
            }
        });
        this.getParentCustomerData();
        if (this.selectedCustService.billablecust != null) {
            this.billableCustList = [
                {
                    id: this.selectedCustService.billablecust.id,
                    name: this.selectedCustService.billablecust.name
                }
            ];
            this.changePlanForm.patchValue({
                billableCustomerId: this.selectedCustService.billablecust.id
            });
        }

        this.planByService = [];
        this.planListByType.forEach(element => {
            if (element.serviceId == i && element.isDelete == false) {
                this.planByService.push(element);
            }
        });
    }
    filterplanGroup(id, custPlanMapppingId, index) {
        this.custPlanMapppingId = custPlanMapppingId;
        this.planByService = [];
        if (
            this.changenewPlanForm.value.ChangePlanCategory == "groupPlan" ||
            this.changePlanForm.value.purchaseType === "Renew"
        ) {
            this.planByService = this.groupPlanListByType;
            this.planByService.forEach(element => {
                element.disabled = true;
            });
            this.planByService.forEach((element, i) => {
                if (element.serviceId == id) {
                    // this.planByService.push(element);
                    this.planByService[i].disabled = false;
                }
            });
        } else {
            this.changePlanTypeForChangePlan(id);
            var uniqueItems = [];
            for (const item of this.planByService) {
                const found = uniqueItems.some(value => isEqual(value, item));
                if (!found) {
                    uniqueItems.push(item);
                }
            }
            this.plansByServiceArr[index] = uniqueItems;
        }
    }

    changePlanTypeForChangePlan(serviceId) {
        var purchaseType = this.changePlanForm.value.purchaseType;
        if (purchaseType != null && purchaseType != undefined) {
            if (purchaseType === "Addon") {
                this.planByService = this.planListByType.filter(
                    plan =>
                        plan.serviceId == serviceId &&
                        (plan.planGroup === "Volume Booster" || plan.planGroup === "Bandwidthbooster")
                );
            } else if (purchaseType === "Renew") {
                this.planByService = this.planListByType.filter(
                    plan =>
                        (plan.serviceId == serviceId && plan.planGroup === "Renew") ||
                        plan.planGroup === "Registration and Renewal"
                );
            } else if (purchaseType === "Changeplan") {
                this.planByService = this.planListByType.filter(
                    plan =>
                        plan.serviceId == serviceId &&
                        (plan.planGroup === "Registration" || plan.planGroup === "Registration and Renewal")
                );
            }
        }
    }

    addRemark() {
        this.changePlanRemark = null;
        $("#addRemark").modal("show");
    }
    closeSelectPlanGroupChangeService() {
        $("#selectPlanGroupChangeService").modal("hide");
    }

    selectedPlan(e, i) {
        let data = {
            value: e.value,
            index: i
        };
        this.getPlanDetailById(data);

        this.custServiceData[i].newplan = e.value;
        this.enableChangePlanGroup = true;
        // this.custServiceData.forEach(element => {
        //   if (!(element.newplan && element.newplan !== null && element.newplan !== "")) {
        //     this.enableChangePlanGroup = false;
        //   }
        // });
    }

    // selectService() {
    //   let serviceId = this.planselection[0].serviceId;
    //   let data = [];
    //   console.log(serviceId);
    //   this.planListByType.forEach(element => {
    //     if (element.serviceId == serviceId) {
    //       data.push(element);
    //     }
    //   });
    //   this.planListByType = [];
    //   this.planListByType = data;
    //   // this.planListByType = this.planListByType.filter(plan => {
    //   //   plan.serviceId == serviceId;
    //   // });
    //   $("#selectPlanChangeService").modal("hide");
    //   console.log("this.planListByType", this.planListByType);
    // }
    newPlanGroupData: any;
    PlanGroupName: any;
    openCustomersChangePlan(data) {
        if (
            !this.loginService.hasOperationPermission(
                AclClassConstants.ACL_CHANGE_PLAN,
                AclConstants.OPERATION_CHANGE_PLAN_VIEW,
                AclConstants.OPERATION_CHANGE_PLAN_ALL
            )
        ) {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Sorry you have not privilege to change plan operation!');
            }

        } else {
            this.changePlansubmitted = false;
            this.childPlanRenewArray = this.fb.array([]);
            this.changePlanForm.reset();
            this.changenewPlanForm.controls.ChangePlanCategory.reset();
            this.chargenewPlanForm.reset();
            this.chargenewPlanForm.reset();
            // this.changePlanForm.controls.planId.setValue("");
            // this.changePlanForm.controls.planGroupId.setValue("");
            // this.changePlanForm.controls.purchaseType.setValue("");
            // this.changePlanForm.controls.remarks.setValue("");
            this.filterPlanListCust = [];
            this.planListByType = [];
            this.selPlanData = [];
            this.changePlanDate = [];
            this.newAdddiscountdata = [];
            this.planDiscount = 0;
            this.finalOfferPrice = 0;
            this.pageNumberForChildsPageForChangePlan = 1;
            this.pageSizeForChildsPageForChangePlan = RadiusConstants.ITEMS_PER_PAGE;
            this.ifPlanSelectChanePlan = false;
            this.listView = false;
            this.createView = false;
            this.selectAreaList = false;
            this.selectPincodeList = false;
            this.isCustomerDetailOpen = false;
            this.isCustomerLedgerOpen = false;
            this.customerPlanView = false;
            this.customerTicketView = false;
            this.viewCustomerPaymentList = false;
            this.isCustomerDetailSubMenu = true;
            this.customerChangePlan = true;
            this.isPlanTypeAddon = false;
            this.ifCDR = false;
            this.ifMyInvoice = false;
            this.ifShowDBRReport = false;
            this.ifChargeGetData = false;
            this.isServiceOpen = false;
            this.isCreditNoteOpen = false;
            this.ifWalletMenu = false;
            this.ifUpdateAddress = false;
            this.isStatusChangeSubMenu = false;
            this.customerrMyInventoryView = false;
            this.assignInventoryWithSerial = false;
            this.customerUpdateDiscount = false;
            this.shiftLocationEvent = false;
            this.ifWorkflowAuditShow = false;
            this.ifAuditDetailsShow = false;
            this.ifDunningDetailsShow = false;
            this.ifNotificationDetailsShow = false;
            this.changeplanGroupFlag = false;
            this.selectedCustService = null;
            this.UpdateParentCustPlans = true;
            this.custPlanMapppingId = null;
            this.childPlanData = [];
            this.parentPurchaseType = null;
            if (
                this.customerLedgerDetailData.planMappingList != null &&
                this.customerLedgerDetailData.planMappingList.length > 0
            ) {
                this.PlanGroupName = this.customerLedgerDetailData.planMappingList[0].planName;
            }
            this.resetForm();
            this.changePlanNewForm.patchValue({ isPaymentReceived: "false" });
            if (data.plangroupid) {
                this.lastRenewalPlanGroup(data.id);
                this.getplanChangeforplanGroup(data.id);
            }

            this.getcustDiscountDetails(data.id, "");
            this.customerchargeDATA(data.id, "parent");
            this.InvoiceListByCustomer(data.id);
            const checkCustTypeurl = `/isCustomerPrimeOrNot?custId=${data.id}`;
            this.customerManagementService.getMethod(checkCustTypeurl).subscribe((responsePrime: any) => {
                // plan deatils
                let specialPlanURL;
                let planurl;
                let planGroupurl;
                let planCategory;
                let PlanGroupCatogry;
                let plandata1: any = [];
                let plandata2: any = [];
                if (responsePrime.isCustomerPrime) {
                    planurl = `/premierePlan/all?custId=${data.id}&isPremiere=true&serviceAreaId=${this.customerLedgerDetailData.serviceareaid}`;
                    planGroupurl = `/planGroupMappings?mode=""`;
                    specialPlanURL = `/plansByServiceAreaCustId?custId=${data.id}&planmode=SPECIAL&serviceAreaId=${this.customerLedgerDetailData.serviceareaid}`;
                }
                if (this.customerLedgerDetailData.plangroupid != null) {
                    let url = "/findPlanGroupById?planGroupId=" + this.customerLedgerDetailData.plangroupid;

                    this.customerManagementService.getMethod(url).subscribe((response: any) => {
                        PlanGroupCatogry = response.planGroup.category;

                        if (!responsePrime.isCustomerPrime) {
                            planGroupurl =
                                `/planGroupMappings?mode=NORMAL` +
                                "&planCategory=" +
                                PlanGroupCatogry +
                                "&custId=" +
                                this.customerLedgerDetailData.id;
                            planurl =
                                "/plans/serviceArea?planCategory=" +
                                "NORMAL" +
                                "&serviceAreaId=" +
                                this.customerLedgerDetailData.serviceareaid +
                                "&planmode=NORMAL";
                        }
                        this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                            this.filterPlanGroupListCust = response.planGroupList.filter(
                                plan => plan.plantype === this.customerLedgerDetailData.custtype
                            );
                            let data1;
                            let data2;
                            if (this.filterPlanGroupListCust) {
                                data1 = this.filterPlanGroupListCust.filter(
                                    plan => plan.servicearea.id == this.customerLedgerDetailData.serviceareaid
                                );
                                data2 = this.filterNormalPlanGroup.filter(plan =>
                                    plan.servicearea.forEach(e => e == this.customerLedgerDetailData.serviceareaid)
                                );
                            }
                            setTimeout(() => {
                                this.filterPlanGroupListCust = [...data1, ...data2];
                            }, 1000);
                            this.filterPlanGroupListCust.forEach((element, index) => {
                                if (element.planMode == "SPECIAL") {
                                    element.planGroupName = element.planGroupName + " - (SP)";
                                }
                            });
                            this.newPlanGroupData = this.filterPlanGroupListCust;
                        });
                        this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                            this.filterPlanListCust = response.postpaidplanList.filter(
                                plan => plan.plantype === this.customerLedgerDetailData.custtype
                            );
                            this.filterPlanListCust.forEach(element => {
                                if (element.mode == "SPECIAL") {
                                    element.name = element.name + " - (SP)";
                                }
                            });
                        });
                    });
                } else {
                    if (
                        this.customerLedgerDetailData.planMappingList.length > 0 &&
                        !responsePrime.isCustomerPrime
                    ) {
                        const url = "/postpaidplan/" + this.customerLedgerDetailData.planMappingList[0].planId;
                        this.customerManagementService.getMethod(url).subscribe((response: any) => {
                            planCategory = response.postPaidPlan.category;

                            if (!responsePrime.isCustomerPrime) {
                                planurl =
                                    "/plans/serviceArea?planCategory=" +
                                    planCategory +
                                    "&serviceAreaId=" +
                                    this.customerLedgerDetailData.serviceareaid +
                                    "&planmode=NORMAL" +
                                    "&custId=" +
                                    this.customerLedgerDetailData.id;
                            }
                            this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                                this.filterPlanListCust = response.postpaidplanList.filter(
                                    plan => plan.plantype === this.customerLedgerDetailData.custtype
                                );

                                this.filterPlanListCust.forEach(element => {
                                    if (element.mode == "SPECIAL") {
                                        element.name = element.name + " - (SP)";
                                    }
                                });
                            });
                        });
                    } else {
                        if (!responsePrime.isCustomerPrime) {
                            planurl =
                                "/plans/serviceArea?planCategory=" +
                                "Normal" +
                                "&serviceAreaId=" +
                                this.customerLedgerDetailData.serviceareaid +
                                "&planmode=NORMAL" +
                                "&custId=" +
                                this.customerLedgerDetailData.id;
                        }
                        this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                            plandata1 = response.postpaidplanList.filter(
                                plan => plan.plantype === this.customerLedgerDetailData.custtype
                            );

                            if (plandata1.length > 0) {
                                plandata1.forEach((element, i) => {
                                    let n = i + 1;
                                    if (element.mode == "SPECIAL") {
                                        element.name = element.name + " - (SP)";
                                    }
                                });
                            }

                            if (responsePrime.isCustomerPrime) {
                                this.customerManagementService
                                    .getMethod(specialPlanURL)
                                    .subscribe((response: any) => {
                                        plandata2 = response.postpaidplanList.filter(
                                            plan => plan.plantype === this.customerLedgerDetailData.custtype
                                        );

                                        if (plandata2.length > 0) {
                                            plandata2.forEach((element1, j) => {
                                                let m = j + 1;
                                                if (element1.mode == "SPECIAL") {
                                                    element1.name = element1.name + " - (SP)";
                                                }
                                                if (plandata2.length == m) {
                                                    plandata2.forEach((e1, i) => {
                                                        plandata1.forEach((e2, j) => {
                                                            if (e1.id == e2.id) {
                                                                plandata2.splice(i, 1);
                                                            }
                                                            let k = i + 1;
                                                        });
                                                    });
                                                    this.filterPlanListCust = plandata1.concat(plandata2);
                                                }
                                            });
                                        } else if (plandata2.length == 0) {
                                            this.filterPlanListCust = plandata1;
                                        }
                                    });
                            } else {
                                this.filterPlanListCust = plandata1;
                            }
                        });
                    }
                }
            });

            this.changePlanForm.get("isPaymentReceived").setValue("false");
            this.customerChildsView = false;
            this.getserviceData("");
            this.getChildCustomersForChangePlan(data.id);
            this.assignInventoryWithSerial = false;
            this.getcustCurrentPlan(data.id, "");
            //this.staffId=serviceAreaId
            this.serviceAreaId = data.serviceareaid;
            this.getStaffDetailById();
        }
    }

    getplanChangeforplanGroup(id) {
        const url = "/findPlanGroupMappingByCustId?custId=" + id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.filterPlanGroupListCust = response.planGroupMappingList;

                // this.messageService.add({
                //   severity: "success",
                //   summary: "Success",
                //   detail: response.responseMessage,
                //   icon: "far fa-times-circle",
                // });
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    customerchargeDATA(id, custtype) {
        const data = [];
        this.chargeGroupForm.reset();
        this.overChargeListFromArray = this.fb.array([]);
        let i = 0;
        const chargedata = [];
        this.customerChargeDataShowChangePlan = [];
        const url = "/getAllCustomerDirectChargeByCustomer/" + id;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                const ChargeCustList = response.custChargeOverrideList;
                if (ChargeCustList.length > 0) {
                    this.addChargeForm.patchValue({
                        chargeAdd: true
                    });
                }

                ChargeCustList.forEach((element, k) => {
                    if (element.type == "Recurring") {
                        chargedata.push(element);
                        this.customerChargeDataShowChangePlan = chargedata;
                        if (custtype == "parent") {
                            this.parentChargeRecurringCustList = i;
                            this.chargeGroupForm.patchValue(element);
                            this.onAddoverChargeListField();

                            this.overChargeListFromArray.patchValue(chargedata);
                        } else {
                            this.childChargeRecurringCustList = i;
                            this.chargeChildGroupForm.patchValue(element);
                            this.onAddoverChargeChildListField();

                            this.overChargeChildListFromArray.patchValue(chargedata);
                        }
                        i++;
                    }
                    // console.log('kkk' ,this.ChargeRecurringCustList)
                });
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openMyCDR(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = true;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.searchGroupByNameCDR("");
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    openMyInvoice(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = true;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.ifCDR = false;
        this.searchinvoiceMaster(id, "");
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    async exportExcel() {
        this.groupDataCDR = [];
        let data = {
            userName: this.customerLedgerDetailData.username,
            fromDate: this.searchAcctCdrForm.value.fromDate,
            toDate: this.searchAcctCdrForm.value.toDate
        };
        this.customerManagementService.getAllCDRExport(data).subscribe((response: any) => {
            const file = new Blob([response], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            });
            const fileURL = URL.createObjectURL(file);
            FileSaver.saveAs(file, "Sheet");
            // if (response.acctCdrList.length > 0) {
            //   const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.groupDataCDR);
            //   const wb: XLSX.WorkBook = XLSX.utils.book_new();
            //   XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
            //   XLSX.writeFile(wb, this.fileNameCDR);

            // } else {
            //
            //   this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "No record found for export.",
            //     icon: "far fa-times-circle",
            //   });
            // }
        });
    }

    TotalItemPerCDRPage(event) {
        this.showItemPerPageCDR = Number(event.value);
        if (this.currentPageCDR > 1) {
            this.currentPageCDR = 1;
        }
        this.searchGroupByNameCDR(this.showItemPerPageCDR);
    }

    async searchGroupByNameCDR(list) {
        let size;
        this.searchkey = "";
        this.searchkey2 = "";
        const page = this.currentPageCDR;
        if (list) {
            size = list;
            this.itemsPerPageCDR = list;
        } else {
            size = this.itemsPerPageCDR;
        }
        let f = "";
        let t = "";

        if (this.searchAcctCdrForm.value.fromDate) {
            f = this.datepipe.transform(this.searchAcctCdrForm.controls.fromDate.value, "yyyy-MM-dd");
        }
        if (this.searchAcctCdrForm.value.toDate) {
            t = this.datepipe.transform(this.searchAcctCdrForm.controls.toDate.value, "yyyy-MM-dd");
        }

        // this.currentPage = 1;
        this.searchCDRSubmitted = true;
        if (this.searchAcctCdrForm.valid) {
            const userNameForSearch = this.customerLedgerDetailData.custname;
            const framedIpAddress = this.searchAcctCdrForm.value.framedIpAddress
                ? this.searchAcctCdrForm.value.framedIpAddress.trim()
                : "";
            this.groupDataCDR = [];

            this.customerManagementService
                .getAcctCdrDataByUsername(
                    userNameForSearch,
                    framedIpAddress,
                    f,
                    t,
                    this.currentPageCDR,
                    this.itemsPerPageCDR
                )
                .subscribe(
                    (response: any) => {
                        // this.groupDataCDR = response.acctCdr.content;
                        const groupDataCDR = response.acctCdr.content.filter(
                            name => name.userName == this.customerLedgerDetailData.custname
                        );
                        this.groupDataCDR = groupDataCDR;
                        this.totalCDRRecords = response.acctCdr.totalElements;
                    },
                    (error: any) => {
                        this.totalCDRRecords = 0;
                        if (error.error.status == 404) {
                            this.toastr.info(`${error.error.errorMessage}`, 'Info!');


                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    }
                );
        }
    }

    clearSearchCDRForm() {
        this.searchCDRSubmitted = false;
        this.currentPageCDR = 1;
        this.searchAcctCdrForm.reset();
        this.searchGroupByNameCDR("");
    }

    pageCDRChanged(pageNumber) {
        this.currentPageCDR = pageNumber;
        this.searchGroupByNameCDR("");
    }

    openInvoiceModal(id, invoice) {
        // this.invoiceDetailsService.show(id);

        this.isInvoiceDetail = true;
        this.invoiceID = invoice.id;
        this.custID = invoice.custid;
    }

    closeInvoiceDetails() {
        this.isInvoiceDetail = false;
        this.invoiceID = "";
        this.custID = 0;
    }

    openInvoicePaymentModal(id, invoiceId) {
        this.invoicePaymentListService.show(id);
        this.invoiceId.next({
            invoiceId
        });
    }

    pageChangedinvoiceMasterList(pageNumber) {
        this.currentPageinvoiceMasterSlab = pageNumber;
        this.searchinvoiceMaster("", "");
    }

    TotalItemPerPageInvoice(event) {
        this.showItemPerPageInvoice = Number(event.value);
        if (this.currentPageinvoiceMasterSlab > 1) {
            this.currentPageinvoiceMasterSlab = 1;
        }
        this.searchinvoiceMaster("", this.showItemPerPageInvoice);
    }

    searchInvoices() {
        this.currentPageinvoiceMasterSlab = 1;
        this.searchinvoiceMaster("", "");
    }

    searchinvoiceMaster(id, size) {
        let page_list;
        if (size) {
            page_list = size;
            this.invoiceMasteritemsPerPage = size;
        } else {
            if (this.showItemPerPageInvoice == 1) {
                this.invoiceMasteritemsPerPage = this.pageITEM;
            } else {
                this.invoiceMasteritemsPerPage = this.showItemPerPageInvoice;
            }
        }

        let url;

        // if (id) {
        //   this.searchInvoiceMasterFormGroup.value.billrunid = id
        //   this.searchInvoiceMasterFormGroup.patchValue({
        //     billrunid: Number(id),
        //   })
        // }

        const dtoData = {
            page: this.currentPageinvoiceMasterSlab,
            pageSize: this.invoiceMasteritemsPerPage
        };

        this.searchInvoiceMasterFormGroup.value.custMobile = "";
        this.searchInvoiceMasterFormGroup.value.customerid = this.customerLedgerDetailData.id;

        url =
            "/invoice/search?billrunid=" +
            this.searchInvoiceMasterFormGroup.value.billrunid +
            "&docnumber=" +
            this.searchInvoiceMasterFormGroup.value.docnumber.trim() +
            "&customerid=" +
            this.searchInvoiceMasterFormGroup.value.customerid +
            "&billfromdate=" +
            this.searchInvoiceMasterFormGroup.value.billfromdate +
            "&billtodate=" +
            this.searchInvoiceMasterFormGroup.value.billtodate +
            "&custmobile=" +
            this.searchInvoiceMasterFormGroup.value.custMobile.trim() +
            "&isInvoiceVoid=true";
        this.customerManagementService.postMethod(url, dtoData).subscribe(
            (response: any) => {
                const invoiceMasterListData = response.invoicesearchlist.filter(
                    invoice => invoice.custType == this.custType
                );
                this.invoiceMasterListData = invoiceMasterListData;
                this.invoiceMastertotalRecords = response.pageDetails.totalRecords;
                // this.invoiceMasterListData = response.invoicesearchlist;

                this.isInvoiceSearch = true;
                // console.log("this.searchPaymentData", this.invoiceMasterListData);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    clearSearchinvoiceMaster() {
        this.isInvoiceSearch = false;
        this.searchInvoiceMasterFormGroup.reset();
        this.searchInvoiceMasterFormGroup.controls.billrunid.setValue("");
        this.searchInvoiceMasterFormGroup.controls.docnumber.setValue("");
        this.searchInvoiceMasterFormGroup.controls.custname.setValue("");
        this.searchInvoiceMasterFormGroup.controls.billfromdate.setValue("");
        this.searchInvoiceMasterFormGroup.controls.billtodate.setValue("");
        this.searchInvoiceMasterFormGroup.controls.customerid.setValue("");
        this.searchInvoiceMasterFormGroup.controls.staffid.setValue("");
        this.invoiceMasterListData = [];
        this.searchinvoiceMaster("", "");
    }

    // uploadDocumentFormGroup(): FormGroup {
    //   return this.fb.group({
    //     filename: [this.uploadDocumentGroupForm.value.filename],
    //     docStatus: [this.uploadDocumentGroupForm.value.docStatus],
    //     remark: [this.uploadDocumentGroupForm.value.remark],
    //   });
    // }

    // onUploadDocumentList() {
    //   this.uploadDocsubmitted = true;
    //   if (this.uploadDocumentGroupForm.valid) {
    //    // this.uploadDocumentListFromArray.push(this.uploadDocumentFormGroup());
    //     this.uploadDocumentGroupForm.reset();
    //     this.uploadDocsubmitted = false;
    //   } else {
    //     // console.log("I am not valid");
    //   }
    // }

    generatePDFInvoice(custId) {
        if (custId) {
            const url = "/generatePdfByInvoiceId/" + custId;
            this.customerManagementService.generateMethodInvoice(url).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');


                    } else {
                        response.responseCode == 417;
                    }
                    this.toastr.info(`${response.responseMessage}`, 'Info!');


                },

                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    downloadPDFINvoice(docNo, customerName) {
        if (docNo) {
            const downloadUrl = "/invoicePdf/download/" + docNo;
            this.customerManagementService.downloadPDFInvoice(downloadUrl).subscribe(
                (response: any) => {
                    const file = new Blob([response], { type: "application/pdf" });
                    // var fileURL = URL.createObjectURL(file,customerName + docNo);
                    // FileSaver.saveAs(file);
                    const fileURL = URL.createObjectURL(file);
                    FileSaver.saveAs(file, customerName + docNo);
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    TotalPaymentItemPerPage(event) {
        this.paymentShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerPaymentdata > 1) {
            this.currentPagecustomerPaymentdata = 1;
        }
        this.openCustomersPaymentData(this.customerLedgerDetailData.id, this.paymentShowItemPerPage);
    }

    openCustomersPaymentData(id, size) {
        if (
            this.customerLedgerDetailData.parentCustomerId == "null" ||
            this.customerLedgerDetailData.invoiceType == "Group"
        ) {
            this.isDisable = true;
        }
        let page_list;

        if (size) {
            page_list = size;
            this.customerPaymentdataitemsPerPage = size;
        } else {
            if (this.paymentShowItemPerPage == 1) {
                this.customerPaymentdataitemsPerPage = this.pageITEM;
            } else {
                this.customerPaymentdataitemsPerPage = this.paymentShowItemPerPage;
            }
        }

        const url = "/paymentHistory/" + id;
        this.customerManagementService.paymentData(url).subscribe((response: any) => {
            this.viewcustomerPaymentData = response.dataList;
            // console.log("this.viewcustomerPaymentData", this.viewcustomerPaymentData);
            this.viewCustomerPaymentList = true;
            this.listView = false;
            this.createView = false;
            this.selectAreaList = false;
            this.selectPincodeList = false;
            this.isCustomerDetailOpen = false;
            this.isCustomerLedgerOpen = false;
            this.customerPlanView = false;
            this.customerTicketView = false;
            this.isCustomerDetailSubMenu = true;
            this.customerrMyInventoryView = false;
            this.assignInventoryWithSerial = false;
            this.ifCDR = false;
            this.ifMyInvoice = false;
            this.ifShowDBRReport = false;
            this.ifChargeGetData = false;
            this.isServiceOpen = false;
            this.isCreditNoteOpen = false;
            this.ifWalletMenu = false;
            this.ifUpdateAddress = false;
            this.customerChangePlan = false;
            this.isStatusChangeSubMenu = false;
            this.customerChildsView = false;
            this.customerUpdateDiscount = false;
            // this.paymentFormGroup.controls.barteramount.disable();
            this.InvoiceListByCustomer(id);
            this.shiftLocationEvent = false;
            this.ifWorkflowAuditShow = false;
            this.ifAuditDetailsShow = false;
            this.ifDunningDetailsShow = false;
            this.ifNotificationDetailsShow = false;
        });
    }

    TotalTicketItemPerPage(event) {
        this.ticketShowItemPerPage = Number(event.value);
        if (this.currentPageTicketConfig > 1) {
            this.currentPageTicketConfig = 1;
        }
        this.getcustTicket(this.customerLedgerDetailData.id, this.ticketShowItemPerPage);
    }

    getcustTicket(custId, size) {
        let page_list;
        if (size) {
            page_list = size;
            this.custTicketConfigitemsPerPage = size;
        } else {
            if (this.ticketShowItemPerPage == 1) {
                this.custTicketConfigitemsPerPage = this.pageITEM;
            } else {
                this.custTicketConfigitemsPerPage = this.ticketShowItemPerPage;
            }
        }

        const url = "/getCasesByCustomer/" + custId;
        this.ticketManagementService.getCutomerTicketData(url, { page: 1, pageSize: 5 }).subscribe(
            (response: any) => {
                this.custTicketList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    TotalCurrentPlanItemPerPage(event) {
        this.CurrentPlanShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerCurrentPlanListdata > 1) {
            this.currentPagecustomerCurrentPlanListdata = 1;
        }
        this.getcustCurrentPlan(this.customerLedgerDetailData.id, this.CurrentPlanShowItemPerPage);
    }

    getcustCurrentPlan(custId, size) {
        let page_list;
        if (size) {
            page_list = size;
            this.customerCurrentPlanListdataitemsPerPage = size;
        } else {
            if (this.CurrentPlanShowItemPerPage == 1) {
                this.customerCurrentPlanListdataitemsPerPage = this.pageITEM;
            } else {
                this.customerCurrentPlanListdataitemsPerPage = this.CurrentPlanShowItemPerPage;
            }
        }

        const url = "/subscriber/getActivePlanList/" + custId + "?isNotChangePlan=true";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custCurrentPlanList = response.dataList;
                this.changePlanCustCurrentPlan = this.custCurrentPlanList.filter(
                    item =>
                        item.plangroup !== "Volume Booster" &&
                        item.plangroup !== "Bandwidthbooster" &&
                        item.plangroup !== "DTV Addon"
                );
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    pageChangedcustomerDiscountCustListData(pageNumber) {
        this.currentPagecustomerCustDiscountListdata = pageNumber;
        this.getcustDiscountDetails(this.customerLedgerDetailData.id, "");
    }

    TotalCustDiscountItemPerPage(event) {
        this.CustDiscountShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerCustDiscountListdata > 1) {
            this.currentPagecustomerCustDiscountListdata = 1;
        }
        this.getcustDiscountDetails(this.customerLedgerDetailData.id, this.CustDiscountShowItemPerPage);
    }

    getcustDiscountDetails(custId, size, cust360Type = "") {
        let page_list;
        this.OlddiscountData = [];
        if (size) {
            page_list = size;
            this.customerCustDiscountListdataitemsPerPage = size;
        } else {
            if (this.CustDiscountShowItemPerPage == 1) {
                this.customerCustDiscountListdataitemsPerPage = this.pageITEM;
            } else {
                this.customerCustDiscountListdataitemsPerPage = this.CustDiscountShowItemPerPage;
            }
        }

        let custDiscountdatalength = 0;
        let url =
            "/subscriber/fetchCustomerDiscountDetailServiceLevel/" +
            custId +
            "?isExpiredRequired=" +
            (cust360Type === "changeDiscount");
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custCustDiscountList = response.discountDetails;

                while (custDiscountdatalength < this.custCustDiscountList.length) {
                    // const planurl =
                    //   "/postpaidplan/" +
                    //   this.custCustDiscountList[custDiscountdatalength].planId;
                    // this.customerManagementService
                    //   .getMethod(planurl)
                    //   .subscribe((response: any) => {
                    //     this.dataDiscountPlan.push(response.postPaidPlan);
                    //     // console.log("dataPlan", this.dataPlan);
                    //   });

                    if (
                        this.custCustDiscountList[custDiscountdatalength].discount === null ||
                        this.custCustDiscountList[custDiscountdatalength].discount === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].discount = 0;
                    }
                    this.custCustDiscountList[custDiscountdatalength].discount = parseFloat(
                        this.custCustDiscountList[custDiscountdatalength].discount
                    ).toFixed(2);

                    if (
                        this.custCustDiscountList[custDiscountdatalength].newDiscount === null ||
                        this.custCustDiscountList[custDiscountdatalength].newDiscount === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].newDiscount = 0;
                    }
                    this.custCustDiscountList[custDiscountdatalength].newDiscount = parseFloat(
                        this.custCustDiscountList[custDiscountdatalength].newDiscount
                    ).toFixed(2);

                    if (
                        this.custCustDiscountList[custDiscountdatalength].discountType === null ||
                        this.custCustDiscountList[custDiscountdatalength].discountType === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].discountType = "One-time";
                    }
                    if (
                        this.custCustDiscountList[custDiscountdatalength].newDiscountType === null ||
                        this.custCustDiscountList[custDiscountdatalength].newDiscountType === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].newDiscountType = "One-time";
                    }

                    if (
                        this.custCustDiscountList[custDiscountdatalength].discountExpiryDate !== null &&
                        this.custCustDiscountList[custDiscountdatalength].discountExpiryDate !== ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].discountExpiryDate = moment(
                            this.custCustDiscountList[custDiscountdatalength].discountExpiryDate
                        )
                            .utc(true)
                            .toDate();
                    }

                    if (
                        this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate !== null &&
                        this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate !== ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate = moment(
                            this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate
                        )
                            .utc(true)
                            .toDate();
                    }
                    custDiscountdatalength++;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    TotalFuturePlanItemPerPage(event) {
        this.futurePlanShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerFuturePlanListdata > 1) {
            this.currentPagecustomerFuturePlanListdata = 1;
        }
        this.getcustFuturePlan(this.customerLedgerDetailData.id, this.futurePlanShowItemPerPage);
    }

    getcustFuturePlan(custId, size) {
        let page_list;
        if (size) {
            page_list = size;
            this.customerFuturePlanListdataitemsPerPage = size;
        } else {
            if (this.futurePlanShowItemPerPage == 1) {
                this.customerFuturePlanListdataitemsPerPage = this.pageITEM;
            } else {
                this.customerFuturePlanListdataitemsPerPage = this.futurePlanShowItemPerPage;
            }
        }

        const url = "/subscriber/getFuturePlanList/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custFuturePlanList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    TotalExpiredPlanItemPerPage(event) {
        this.expiredShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerExpiryPlanListdata > 1) {
            this.currentPagecustomerExpiryPlanListdata = 1;
        }
        this.getcustExpiredPlan(this.customerLedgerDetailData.id, this.expiredShowItemPerPage);
    }

    getcustExpiredPlan(custId, size) {
        let page_list;
        if (size) {
            page_list = size;
            this.customerExpiryPlanListdataitemsPerPage = size;
        } else {
            if (this.expiredShowItemPerPage == 1) {
                this.customerExpiryPlanListdataitemsPerPage = this.pageITEM;
            } else {
                this.customerExpiryPlanListdataitemsPerPage = this.expiredShowItemPerPage;
            }
        }

        const url = "/subscriber/getExpiredPlanList/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custExpiredPlanList = response.dataList;
                // console.log(" this.custExpiredPlanList", this.custExpiredPlanList);
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    samepresentAddress(event, data) {
        if (event.checked == true) {
            if ("payment" == data) {
                this.getTempPincodeData(this.presentGroupForm.value.pincodeId, "payment");
                this.getAreaData(this.presentGroupForm.value.areaId, "payment");
                this.paymentGroupForm = this.fb.group({
                    addressType: ["Payment"],
                    landmark: [this.presentGroupForm.value.landmark],
                    areaId: [this.presentGroupForm.value.areaId],
                    pincodeId: [this.presentGroupForm.value.pincodeId],
                    cityId: [this.presentGroupForm.value.cityId],
                    stateId: [this.presentGroupForm.value.stateId],
                    countryId: [this.presentGroupForm.value.countryId],
                    landmark1: [this.presentGroupForm.value.landmark1]
                });
            }
            if ("permanet" == data) {
                this.getTempPincodeData(this.presentGroupForm.value.pincodeId, "permanent");
                this.getAreaData(this.presentGroupForm.value.areaId, "permanent");
                this.permanentGroupForm = this.fb.group({
                    addressType: ["Permanent"],
                    landmark: [this.presentGroupForm.value.landmark],
                    areaId: [this.presentGroupForm.value.areaId],
                    pincodeId: [this.presentGroupForm.value.pincodeId],
                    cityId: [this.presentGroupForm.value.cityId],
                    stateId: [this.presentGroupForm.value.stateId],
                    countryId: [this.presentGroupForm.value.countryId],
                    landmark1: [this.presentGroupForm.value.landmark1]
                });
            }
        }

        if (event.checked == false) {
            if ("payment" == data) {
                this.paymentGroupForm.reset();
            }
            if ("permanet" == data) {
                this.permanentGroupForm.reset();
            }
        }
    }

    validityUnitListFormGroup(): UntypedFormGroup {
        return this.fb.group({
            validityUnit: [this.validityUnitFormGroup.value.validityUnit]
        });
    }

    planMappingListFormGroup(): UntypedFormGroup {
        for (const prop in this.planGroupForm.controls) {
            this.planGroupForm.value[prop] = this.planGroupForm.controls[prop].value;
        }

        return this.fb.group({
            planId: [this.planGroupForm.value.planId, Validators.required],
            service: [this.planGroupForm.value.service, Validators.required],
            validity: [this.planGroupForm.value.validity, Validators.required],

            discount: [this.planGroupForm.value.discount ? this.planGroupForm.value.discount : 0],
            billTo: [this.customerGroupForm.value.billTo],
            billableCustomerId: [this.customerGroupForm.value.billableCustomerId],
            newAmount: [this.planGroupForm.value.newAmount],
            invoiceType: [this.planGroupForm.value.invoiceType],
            offerPrice: [this.planGroupForm.value.offerprice],
            isInvoiceToOrg: [this.customerGroupForm.value.isInvoiceToOrg],
            istrialplan: [this.planGroupForm.value.istrialplan],
            discountType: [this.planGroupForm.value.discountType],
            discountExpiryDate: [
                this.planGroupForm.value.discountExpiryDate
                    ? moment(this.planGroupForm.value.discountExpiryDate).utc(true).toDate()
                    : null
            ]
            // id:[]
        });
        return;
    }

    discountValue: any = 0;

    discountvaluesetPercentage(e) {
        let data = [];
        let price = Number(this.planDataForm.value.offerPrice);
        let selDiscount = parseFloat(this.planDataForm.value.discountPrice).toFixed(2);
        // let discount = Number(selDiscount);
        //let selDiscount = parseFloat(this.planDataForm.value.discountPrice).toFixed(2);
        let discount = Number(selDiscount);
        // let DisValue = this.planDataForm.value.offerPrice - this.planDataForm.value.discountPrice;
        let discountPlan = (discount * 100) / price;
        let discountValueNUmber = discountPlan.toFixed(2);
        let value = 100 - Number(discountValueNUmber);

        if (this.ifPlanGroup) {
            if (discount == 0) {
                this.customerGroupForm.patchValue({
                    discount: 0
                });
            } else {
                this.customerGroupForm.patchValue({
                    discount: value.toFixed(2)
                });
            }
        } else {
            this.payMappingListFromArray.value.forEach((element, i) => {
                let n = i + 1;
                if (discount == 0) {
                    element.discount = 0;
                } else {
                    element.discount = value.toFixed(2);
                }

                if (this.payMappingListFromArray.value.length == n) {
                    this.payMappingListFromArray.patchValue(this.payMappingListFromArray.value);
                }
            });
        }
    }

    discountPercentage(e) {
        if (this.ifPlanGroup) {
            this.customerManagementService
                .getofferPriceWithTax(
                    this.planIds,
                    this.customerGroupForm.value.discount,
                    this.planGroupSelected
                )
                .subscribe((response: any) => {
                    if (response.result.finalAmount) {
                        this.finalOfferPrice = response.result.finalAmount.toFixed(3);
                        this.discountValue = response.result.finalAmount.toFixed(3);
                    } else {
                        this.finalOfferPrice = 0;
                        this.discountValue = 0;
                    }
                    this.planDataForm.patchValue({
                        discountPrice: Number(this.discountValue).toFixed(2)
                    });
                });
        } else {
            this.customerManagementService
                .getofferPriceWithTax(this.planGroupForm.value.planId, this.planGroupForm.value.discount)
                .subscribe((response: any) => {
                    if (response.result.finalAmount) {
                        this.finalOfferPrice = response.result.finalAmount.toFixed(3);
                        this.discountValue = response.result.finalAmount.toFixed(3);
                    } else {
                        this.finalOfferPrice = 0;
                        this.discountValue = 0;
                    }
                });
        }
    }

    DiscountValueStore: any = [];

    discountChange(e, index) {
        let lastvalue: any = 0;

        this.customerManagementService
            .getofferPriceWithTax(
                this.payMappingListFromArray.value[index].planId,
                this.payMappingListFromArray.value[index].discount,
                this.payMappingListFromArray.value[index].planGroupId
            )
            .subscribe((response: any) => {
                if (response.result.finalAmount) {
                    lastvalue = response.result.finalAmount.toFixed(3);
                } else {
                    lastvalue = 0;
                }
                this.planDataForm.patchValue({
                    discountPrice: Number(
                        this.planDataForm.value.discountPrice - this.DiscountValueStore[index].value + lastvalue
                    ).toFixed(2)
                });

                this.DiscountValueStore[index].value = lastvalue;
            });
    }

    planTotalOffetPrice = 0;

    onAddplanMappingList() {
        this.plansubmitted = true;
        let offerP = 0;
        let disValue = 0;
        if (this.planGroupForm.valid) {
            this.DiscountValueStore.push({ value: this.discountValue });
            if (this.discountValue == 0) {
                disValue =
                    Number(this.planGroupForm.value.offerprice) +
                    Number(this.planDataForm.value.discountPrice);
            } else {
                disValue = Number(this.discountValue) + Number(this.planDataForm.value.discountPrice);
            }
            this.planDataForm.patchValue({
                discountPrice: disValue.toFixed(2)
            });

            this.planTotalOffetPrice =
                this.planTotalOffetPrice + Number(this.planGroupForm.value.offerprice);

            this.planDataForm.patchValue({
                offerPrice: this.planTotalOffetPrice
            });

            if (this.planGroupForm.value.planId) {
                this.getChargeUsePlanList(this.planGroupForm.value.planId);
            }
            this.payMappingListFromArray.push(this.planMappingListFormGroup());
            this.validityUnitFormArray.push(this.validityUnitListFormGroup());
            this.validityUnitFormGroup.reset();

            this.planGroupForm.reset();
            this.planGroupForm.controls.validity.enable();
            this.plansubmitted = false;
            this.discountType = "One-time";
            this.discountValue = 0;
            if (this.customerGroupForm.value.parentExperience == "Single")
                this.planGroupForm.patchValue({ invoiceType: "Group" });
            else this.planGroupForm.patchValue({ invoiceType: "" });
        } else {
            // console.log("I am not valid");
        }
    }

    getChargeUsePlanList(id) {
        const url = "/postpaidplan/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            const data = response.postPaidPlan;
            this.planDropdownInChageData.push(data);
        });
    }

    MACListFormGroup(): UntypedFormGroup {
        return this.fb.group({
            macAddress: [this.macGroupForm.value.macAddress]
        });
    }

    onAddMACList() {
        this.macsubmitted = true;
        if (this.macGroupForm.valid) {
            this.custMacMapppingListFromArray.push(this.MACListFormGroup());
            this.macGroupForm.reset();

            this.macsubmitted = false;
        } else {
            // console.log("I am not valid");
        }
    }

    createoverChargeListFormGroup(): UntypedFormGroup {
        // this.chargeGroupForm.get("billingCycle").clearValidators();
        // this.chargeGroupForm.get("billingCycle").updateValueAndValidity();
        let billingCycle = this.chargeGroupForm.value.type === "Recurring" ? 1 : "";
        let planName = this.planDropdownInChageData.find(
            plan => plan.id == this.chargeGroupForm.value.planid
        ).planName;
        return this.fb.group({
            // chargeid: [''],
            type: [this.chargeGroupForm.value.type ? this.chargeGroupForm.value.type : "Recurring"],
            chargeid: [this.chargeGroupForm.value.chargeid],
            validity: [this.chargeGroupForm.value.validity],
            price: [this.chargeGroupForm.value.price],
            actualprice: [this.chargeGroupForm.value.actualprice],
            charge_date: [this.chargeGroupForm.value.charge_date],
            planid: [this.chargeGroupForm.value.planid],
            planName: [planName],
            unitsOfValidity: [this.chargeGroupForm.value.unitsOfValidity],
            billingCycle: [billingCycle],
            discount: [this.chargeGroupForm.value.discount],
            staticIPAdrress: [this.chargeGroupForm.value.staticIPAdrress],
            expiry: [moment(this.chargeGroupForm.value.expiry).format("DD-MM-YYYY HH:mm").toString()]
        });
    }

    isStaticIPAdrress(chargeid) {
        if (chargeid !== null && chargeid !== undefined && chargeid !== "") {
            return (
                this.commondropdownService.chargeByTypeData.filter(
                    charge => charge.id === chargeid && charge.chargecategory === "IP"
                ).length > 0
            );
        } else {
            return false;
        }
    }

    onAddoverChargeListField() {
        this.chargesubmitted = true;
        if (this.chargeGroupForm.valid) {
            if (this.chargeGroupForm.value.price >= this.chargeGroupForm.value.actualprice) {
                this.overChargeListFromArray.push(this.createoverChargeListFormGroup());
                this.chargeGroupForm.reset();
                this.chargesubmitted = false;
                this.selectchargeValueShow = false;
            }
        } else {
        }
    }

    serviceBasePlanDATA(event) {
        let planserviceData;
        let planServiceID = "";
        const servicename = event.value;
        this.planGroupForm.controls.istrialplan.reset();
        this.changeTrialCheck();
        const planserviceurl = "/planservice/all";
        this.customerManagementService.getMethod(planserviceurl).subscribe((response: any) => {
            //
            planserviceData = response.serviceList.filter(service => service.name === servicename);
            if (planserviceData.length > 0) {
                planServiceID = planserviceData[0].id;

                // if (this.customerGroupForm.value.custtype) {
                this.plantypaSelectData = this.filterPlanData.filter(
                    id =>
                        id.serviceId === planServiceID &&
                        (id.planGroup === "Registration" || id.planGroup === "Registration and Renewal")
                );
                //console.log("this.plantypaSelectData", this.plantypaSelectData);
                if (this.plantypaSelectData.length === 0) {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'Plan not available for this customer type and service!');
                    }

                }
                // }
                // else {
                //   this.messageService.add({
                //     severity: 'info',
                //     summary: 'Required ',
                //     detail: 'Customer Type Field Required',
                //     icon: 'far fa-times-circle',
                //   });
                // }
            }
        });
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagecustomerListdata > 1) {
            this.currentPagecustomerListdata = 1;
        }
        if (!this.searchkey) {
            this.getcustomerList(this.showItemPerPage);
        } else {
            this.searchcustomer();
        }
    }

    getcustomerList(list) {
        let size;
        this.searchkey = "";
        this.searchkey2 = "";
        const page = this.currentPagecustomerListdata;
        if (list) {
            size = list;
            this.customerListdataitemsPerPage = list;
        } else {
            size = this.customerListdataitemsPerPage;
        }
        const url = `/customers/list/` + this.custType + "?orgcusttype=false";

        const custerlist = {
            page,
            pageSize: size
        };

        this.customerManagementService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerListData = response.customerList;
                const usernameList: string[] = [];
                this.customerListData.forEach(element => {
                    usernameList.push(element.username);
                });
                this.liveUserService
                    .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                        users: usernameList
                    })
                    .subscribe((res: any) => {
                        const liveUsers: string[] = res.liveusers;
                        this.customerListData.forEach(element => {
                            if (liveUsers.findIndex(e => e == element.username) < 0) {
                                element.connectionMode = "Offline";
                            } else {
                                element.connectionMode = "Online";
                            }
                        });
                    });
                this.customerListDataselector = response.customerList;
                this.customerListdatatotalRecords = response.pageDetails.totalRecords;

                if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                    this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                } else {
                    this.customerListDatalength =
                        this.customerListData.length % this.customerListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    selectAreaChange(_event: any, index: any) {
        this.getAreaData(_event.value, index);
    }

    selectPINCODEChange(_event: any, index: any) {
        const url = "/areas/pincode?pincodeId=" + _event.value;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.AreaListDD = response.areaList;
            },
            (error: any) => {
                console.log(error);
            }
        );
        // this.getpincodeData(_event.value, index);
    }

    getTempPincodeData(id: any, index: any) {
        const url = "/pincode/" + id;

        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            if (index === "present") {
                this.pincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.areaAvailableList = response.data.areaList;
                }
            }
            if (index === "payment") {
                this.PyamentpincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.paymentareaAvailableList = response.data.areaList;
                }
            }
            if (index === "permanent") {
                this.permanentpincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.permanentareaAvailableList = response.data.areaList;
                }
            }
        });
    }

    getpincodeData(id: any, index: any) {
        const url = "/pincode/" + id;

        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            if (index === "present") {
                this.areaAvailableList = [];
                this.areaDetails = [];
                this.presentGroupForm.patchValue({
                    cityId: "",
                    stateId: "",
                    countryId: ""
                });
                this.selectAreaList = true;
                this.selectPincodeList = false;
                this.pincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.areaAvailableList = response.data.areaList;
                } else {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'Area detals are not available, please select correct pincode!');
                    }
                }

                // this.presentGroupForm.patchValue({
                //   cityId: Number(this.pincodeDeatils.cityId),
                //   stateId: Number(this.pincodeDeatils.stateId),
                //   countryId: Number(this.pincodeDeatils.countryId),
                // });
            }
            if (index === "payment") {
                this.paymentareaAvailableList = [];
                this.paymentGroupForm.patchValue({
                    cityId: "",
                    stateId: "",
                    countryId: ""
                });
                this.selectAreaListPayment = true;
                this.selectPincodeListPayment = false;
                this.PyamentpincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.paymentareaAvailableList = response.data.areaList;
                } else {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'Area detals are not available, please select correct pincode!');
                    }


                }

                // this.paymentGroupForm.patchValue({
                //   cityId: Number(this.PyamentpincodeDeatils.cityId),
                //   stateId: Number(this.PyamentpincodeDeatils.stateId),
                //   countryId: Number(this.PyamentpincodeDeatils.countryId),
                // });
            }
            if (index === "permanent") {
                this.permanentareaAvailableList = [];
                this.permanentGroupForm.patchValue({
                    cityId: "",
                    stateId: "",
                    countryId: ""
                });
                this.selectAreaListPermanent = true;
                this.selectPincodeListPermanent = false;
                this.permanentpincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.permanentareaAvailableList = response.data.areaList;
                } else {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'Area detals are not available, please select correct pincode!');
                    }

                }

                // this.permanentGroupForm.patchValue({
                //   cityId: Number(this.permanentpincodeDeatils.cityId),
                //   stateId: Number(this.permanentpincodeDeatils.stateId),
                //   countryId: Number(this.permanentpincodeDeatils.countryId),
                // });
            }
        });
    }

    getAreaData(id: any, index: any) {
        const url = "/area/" + id;

        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            if (index === "present") {
                this.areaDetails = response.data;

                this.selectPincodeList = true;

                this.presentGroupForm.patchValue({
                    addressType: "Present",
                    areaId: Number(this.areaDetails.id),
                    pincodeId: Number(this.areaDetails.pincodeId),
                    cityId: Number(this.areaDetails.cityId),
                    stateId: Number(this.areaDetails.stateId),
                    countryId: Number(this.areaDetails.countryId)
                });
            }
            if (index === "payment") {
                this.paymentareaDetails = response.data;

                this.selectPincodeListPayment = true;

                this.paymentGroupForm.patchValue({
                    addressType: "Payment",
                    pincodeId: Number(this.paymentareaDetails.pincodeId),
                    cityId: Number(this.paymentareaDetails.cityId),
                    stateId: Number(this.paymentareaDetails.stateId),
                    countryId: Number(this.paymentareaDetails.countryId)
                });
            }
            if (index === "permanent") {
                this.permanentareaDetails = response.data;

                this.selectPincodeListPermanent = true;
                this.permanentGroupForm.patchValue({
                    addressType: "Permanent",
                    pincodeId: Number(this.permanentareaDetails.pincodeId),
                    cityId: Number(this.permanentareaDetails.cityId),
                    stateId: Number(this.permanentareaDetails.stateId),
                    countryId: Number(this.permanentareaDetails.countryId)
                });
            }
        });
    }

    scrollTo(el: Element): void {
        if (el) {
            el.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    }

    scrollToError(): void {
        const firstElementWithError = document.querySelector(".ng-invalid[formControlName]");
        this.scrollTo(firstElementWithError);
    }

    checkUsernme(customerId) {
        this.submitted = true;
        if (this.customerGroupForm.valid) {
            const url =
                "/customer/customerUsernameIsAlreadyExists/" +
                this.customerGroupForm.controls.username.value;
            this.customerManagementService.getMethod(url).subscribe((response: any) => {
                if (response.isAlreadyExists == true) {
                    (error: any) => {

                        this.toastr.error(`${error.error.ERROR}`, 'Username already exists!!e!');
                    }

                } else {
                    this.addEditcustomer(customerId);
                }
            });
        } else {
            (error: any) => {

                this.toastr.error(`${error.error.ERROR}`, 'Fields are Mandatory or Invalid. Please fill or update those field!');
            }

            this.scrollToError();
        }
    }

    // async onRemoveUploadDocument(chargeFieldIndex: number) {
    //   this.uploadDocumentListFromArray.removeAt(chargeFieldIndex);
    // }

    onKey(event) {
        if (event.key == "Tab") {
            if (this.customerGroupForm.controls.username.value != this.viewcustomerListData.username) {
                const url =
                    "/customer/customerUsernameIsAlreadyExists/" +
                    this.customerGroupForm.controls.username.value;
                this.customerManagementService.getMethod(url).subscribe((response: any) => {
                    if (response.isAlreadyExists == true) {
                        this.toastr.error(`${response.responseMessage}`, 'Username already exists!!');


                    }
                });
            }
        }
    }

    onKeymobilelength(event) {
        const str = this.customerGroupForm.value.mobile.toLocaleString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobile = `${mobilenumberlength} character required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobile = "";
        } else {
            this.inputMobile = `${mobilenumberlength} character required.`;
        }
    }
    onKeymobilelengthsec(event) {
        const str = this.customerGroupForm.value.secondaryMobile.toLocaleString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobileSec = `${mobilenumberlength} character required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobileSec = "";
        } else {
            this.inputMobileSec = `${mobilenumberlength} character required.`;
        }
    }

    addEditcustomer(customerId) {
        this.submitted = true;
        let i = 0;
        let j = 0;
        let K = 0;
        const l = 0;
        let a = 0;
        let b = 0;
        let c = 0;

        if (this.customerGroupForm.valid && this.presentGroupForm.valid) {
            if (
                this.customerGroupForm.value.planMappingList.length > 0 ||
                this.customerGroupForm.value.plangroupid ||
                this.customerGroupForm.value.custlabel === "organization"
            ) {
                if (customerId) {
                    const url = "/customers/" + customerId;
                    this.customerGroupForm.value.flatAmount = this.planDataForm.value.discountPrice;
                    this.customerGroupForm.value.discount = this.customerGroupForm.value.discount
                        ? this.customerGroupForm.value.discount
                        : 0;
                    if (this.presentGroupForm.value.addressType) {
                        this.addressListData.push(this.presentGroupForm.value);
                        // this.addressListData[0].addressType = "Present";
                    }
                    if (this.paymentGroupForm.value.addressType) {
                        this.addressListData.push(this.paymentGroupForm.value);
                        // this.addressListData[1].addressType = "Payment";
                    }
                    if (this.permanentGroupForm.value.addressType) {
                        this.addressListData.push(this.permanentGroupForm.value);
                        // this.addressListData[2].addressType = "Permanent";
                    }
                    if (
                        this.customerGroupForm.value.countryCode == "" ||
                        this.customerGroupForm.value.countryCode == null
                    ) {
                        this.customerGroupForm.value.countryCode = this.commondropdownService.commonCountryCode;
                    }
                    if (
                        this.customerGroupForm.value.calendarType == "" ||
                        this.customerGroupForm.value.calendarType == null
                    ) {
                        this.customerGroupForm.value.calendarType = "English";
                    }

                    this.customerGroupForm.value.discount = this.customerGroupForm.value.discount
                        ? this.customerGroupForm.value.discount
                        : 0;
                    this.createcustomerData = this.customerGroupForm.value;
                    this.createcustomerData.addressList = this.addressListData;

                    this.createcustomerData.failcount = Number(this.createcustomerData.failcount);
                    if (
                        this.customerGroupForm.controls.partnerid.value == null ||
                        this.customerGroupForm.controls.partnerid.value == ""
                    ) {
                        this.createcustomerData.partnerid = 1;
                    } else {
                        this.createcustomerData.partnerid =
                            this.partnerId !== 1
                                ? this.partnerId
                                : this.customerGroupForm.controls.partnerid.value;
                    }
                    // this.createcustomerData.partnerid = Number(this.createcustomerData.partnerid);
                    this.createcustomerData.paymentDetails.amount = Number(
                        this.createcustomerData.paymentDetails.amount
                    );
                    if (this.viewcustomerListData.parentExperience != null) {
                        this.customerGroupForm.controls.parentExperience.enable();
                    }
                    while (a < this.createcustomerData.addressList.length) {
                        this.createcustomerData.addressList[a].areaId = Number(
                            this.createcustomerData.addressList[a].areaId
                        );
                        this.createcustomerData.addressList[a].pincodeId = Number(
                            this.createcustomerData.addressList[a].pincodeId
                        );
                        this.createcustomerData.addressList[a].cityId = Number(
                            this.createcustomerData.addressList[a].cityId
                        );
                        this.createcustomerData.addressList[a].stateId = Number(
                            this.createcustomerData.addressList[a].stateId
                        );
                        this.createcustomerData.addressList[a].countryId = Number(
                            this.createcustomerData.addressList[a].countryId
                        );

                        a++;
                    }
                    while (b < this.createcustomerData.planMappingList.length) {
                        this.createcustomerData.planMappingList[b].planId = Number(
                            this.createcustomerData.planMappingList[b].planId
                        );
                        b++;
                    }

                    while (c < this.createcustomerData.overChargeList.length) {
                        this.createcustomerData.overChargeList[c].chargeid = Number(
                            this.createcustomerData.overChargeList[c].chargeid
                        );
                        this.createcustomerData.overChargeList[c].validity = Number(
                            this.createcustomerData.overChargeList[c].validity
                        );
                        this.createcustomerData.overChargeList[c].price = Number(
                            this.createcustomerData.overChargeList[c].price
                        );
                        this.createcustomerData.overChargeList[c].actualprice = Number(
                            this.createcustomerData.overChargeList[c].actualprice
                        );
                        c++;
                    }
                    if (
                        this.createcustomerData.plangroupid == null ||
                        this.createcustomerData.plangroupid == ""
                    )
                        this.createcustomerData.invoiceType = null;

                    //this.createcustomerData.parentExperience = this.customerGroupForm.controls.parentExperience;

                    this.createcustomerData.custtype = this.custType;
                    this.createcustomerData.acctno = this.viewcustomerListData.acctno;
                    this.createcustomerData.isDunningEnable = this.viewcustomerListData.isDunningEnable;
                    this.createcustomerData.isNotificationEnable =
                        this.viewcustomerListData.isNotificationEnable;
                    this.createcustomerData.username = this.customerGroupForm.controls.username.value;
                    if (this.customerGroupForm.value.plangroupid) {
                        this.createcustomerData.planMappingList = this.plansArray.value;
                    }
                    this.createcustomerData.planPurchaseType = this.customerGroupForm.value.planCategory;
                    // console.log("this.createcustomerData", this.createcustomerData);
                    this.customerManagementService.updateMethod(url, this.createcustomerData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.iscustomerEdit = false;

                            this.customerID = "";
                            this.payMappingListFromArray.controls = [];
                            this.overChargeListFromArray.controls = [];
                            this.custMacMapppingListFromArray.controls = [];
                            //   this.uploadDocumentListFromArray.controls = [];

                            this.customerFormReset();
                            this.customerGroupForm.controls.parentExperience.disable();
                            //  this.uploadDocumentGroupForm.reset();
                            this.viewcustomerListData = [];
                            this.planCategoryForm.reset();
                            this.addressListData = [];
                            this.toastr.success(`${response.responseMessage}`, 'Success!');



                            this.listView = true;
                            this.createView = false;
                            this.selectAreaList = false;
                            this.selectchargeValueShow = false;
                            this.ifIndividualPlan = false;
                            this.ifPlanGroup = false;
                            //    this.listSearchView = false;
                            if (this.searchkey) {
                                this.searchcustomer();
                            } else {
                                this.getcustomerList("");
                            }
                        },
                        (error: any) => {
                            // console.log(error, "error")
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                        }
                    );
                } else {
                    // if (this.presentGroupForm.value.addressType) {
                    this.addressListData.push(this.presentGroupForm.value);
                    this.addressListData[0].version = "NEW";
                    // }
                    if (this.paymentGroupForm.value.addressType) {
                        this.addressListData.push(this.paymentGroupForm.value);
                        // this.addressListData[1].addressType = "Payment";
                    }
                    if (this.permanentGroupForm.value.addressType) {
                        this.addressListData.push(this.permanentGroupForm.value);
                        // this.addressListData[2].addressType = "Permanent";
                    }
                    if (
                        this.customerGroupForm.value.countryCode == "" ||
                        this.customerGroupForm.value.countryCode == null
                    ) {
                        this.customerGroupForm.value.countryCode = this.commondropdownService.commonCountryCode;
                    }
                    if (
                        this.customerGroupForm.value.calendarType == "" ||
                        this.customerGroupForm.value.calendarType == null
                    ) {
                        this.customerGroupForm.value.calendarType = "English";
                    }

                    const url = "/customers";
                    this.customerGroupForm.value.flatAmount = this.planDataForm.value.discountPrice;
                    this.customerGroupForm.value.discount = this.customerGroupForm.value.discount
                        ? this.customerGroupForm.value.discount
                        : 0;

                    this.createcustomerData = this.customerGroupForm.value;

                    this.createcustomerData.addressList = this.addressListData;

                    this.createcustomerData.failcount = Number(this.createcustomerData.failcount);
                    if (
                        this.customerGroupForm.controls.partnerid.value == null ||
                        this.customerGroupForm.controls.partnerid.value == ""
                    ) {
                        this.createcustomerData.partnerid = 1;
                    } else {
                        this.createcustomerData.partnerid =
                            this.partnerId !== 1
                                ? this.partnerId
                                : this.customerGroupForm.controls.partnerid.value;
                    }
                    // this.createcustomerData.partnerid = Number(this.createcustomerData.partnerid);
                    this.createcustomerData.paymentDetails.amount = Number(
                        this.createcustomerData.paymentDetails.amount
                    );
                    while (i < this.createcustomerData.addressList.length) {
                        this.createcustomerData.addressList[i].areaId = Number(
                            this.createcustomerData.addressList[i].areaId
                        );
                        this.createcustomerData.addressList[i].pincodeId = Number(
                            this.createcustomerData.addressList[i].pincodeId
                        );
                        this.createcustomerData.addressList[i].cityId = Number(
                            this.createcustomerData.addressList[i].cityId
                        );
                        this.createcustomerData.addressList[i].stateId = Number(
                            this.createcustomerData.addressList[i].stateId
                        );
                        this.createcustomerData.addressList[i].countryId = Number(
                            this.createcustomerData.addressList[i].countryId
                        );
                        i++;
                    }
                    while (j < this.createcustomerData.planMappingList.length) {
                        this.createcustomerData.planMappingList[j].planId = Number(
                            this.createcustomerData.planMappingList[j].planId
                        );
                        if (this.createcustomerData.planMappingList[j].discount == null) {
                            this.createcustomerData.planMappingList[j].discount = 0;
                        }
                        j++;
                    }

                    // while (l < this.createcustomerData.custDocList.length) {
                    //   this.createcustomerData.custDocList[l].filename = this.createcustomerData.custDocList[l].filename;
                    //   this.createcustomerData.custDocList[l].docStatus = this.createcustomerData.custDocList[l].docStatus;
                    //   this.createcustomerData.custDocList[l].remark = this.createcustomerData.custDocList[l].remark;
                    //   l++;
                    // }

                    while (K < this.createcustomerData.overChargeList.length) {
                        this.createcustomerData.overChargeList[K].chargeid = Number(
                            this.createcustomerData.overChargeList[K].chargeid
                        );
                        this.createcustomerData.overChargeList[K].validity = Number(
                            this.createcustomerData.overChargeList[K].validity
                        );
                        this.createcustomerData.overChargeList[K].price = Number(
                            this.createcustomerData.overChargeList[K].price
                        );
                        this.createcustomerData.overChargeList[K].actualprice = Number(
                            this.createcustomerData.overChargeList[K].actualprice
                        );
                        K++;
                    }
                    this.createcustomerData.custtype = this.custType;
                    if (this.customerGroupForm.value.plangroupid) {
                        this.createcustomerData.planMappingList = this.plansArray.value;
                    }
                    if (
                        this.createcustomerData.plangroupid == null ||
                        this.createcustomerData.plangroupid == ""
                    )
                        this.createcustomerData.invoiceType = null;
                    this.createcustomerData.planPurchaseType = this.planCategoryForm.value.planCategory;

                    return;
                    this.customerManagementService.postMethod(url, this.createcustomerData).subscribe(
                        (response: any) => {
                            if (response.responseCode == 406) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');

                            } else {
                                this.submitted = false;

                                this.payMappingListFromArray.controls = [];
                                this.overChargeListFromArray.controls = [];
                                this.custMacMapppingListFromArray.controls = [];
                                // this.uploadDocumentListFromArray.controls = [];
                                this.addressListData = [];
                                this.customerGroupForm.controls.parentExperience.disable();
                                this.customerFormReset();
                                //  this.uploadDocumentGroupForm.reset();
                                this.selectchargeValueShow = false;
                                this.toastr.success(`${response.message}`, 'Success!');


                                this.listView = true;
                                this.createView = false;
                                this.ifIndividualPlan = false;
                                this.ifPlanGroup = false;
                                // this.listSearchView = false;

                                this.selectAreaList = false;
                                if (this.searchkey) {
                                    this.searchcustomer();
                                } else {
                                    this.getcustomerList("");
                                }
                            }
                        },
                        (error: any) => {
                            // console.log(error, "error")
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    );
                }
            } else {
                (error: any) => {

                    this.toastr.info(`${error.error.ERROR}`, 'Minimum one Plan Details need to ad!');
                }

            }
        } else {
            (error: any) => {

                this.toastr.info(`${error.error.ERROR}`, 'Fields are Mandatory or Invalid. Please fill or update those field');
            }


            this.scrollToError();
        }
    }

    calTypwDisable: boolean = false;

    getBillableCust(billableCustomerId) {
        const url = "/customers/" + billableCustomerId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                var name = response.customers.firstname + " " + response.customers.lastname;
                this.billableCustList = [
                    {
                        name: name,
                        id: billableCustomerId
                    }
                ];
                this.customerGroupForm.patchValue({ billableCustomerId: billableCustomerId });
            },
            error => { }
        );
    }
    isParantExpirenceEdit: boolean;
    editcustomer(chargeid: any) {
        this.getNetworkDevicesByType("OLT");
        this.getNetworkDevicesByType("Splitter");
        this.getNetworkDevicesByType("Master DB/DB");
        this.getcustCurrentPlan(chargeid, "");
        this.getcustFuturePlan(chargeid, "");
        this.DiscountValueStore = [];
        this.discountValue = 0;
        this.planTotalOffetPrice = 0;
        this.planDropdownInChageData = [];
        this.serviceareaCheck = false;
        this.planDataForm.reset();
        const j = 1;
        let k = 0;
        this.totalAddress = 0;
        this.customerID = chargeid;
        this.editCustomerId = chargeid;
        let addres1;
        const planlength = 0;
        let macNumber = 0;
        const uploadDocument = 0;
        this.listView = false;
        this.createView = true;
        this.calTypwDisable = false;
        setTimeout(() => {
            if (this.custCurrentPlanList.length > 0 || this.custFuturePlanList.length > 0) {
                this.calTypwDisable = true;
            }
        }, 1000);

        if (this.custType === RadiusConstants.CUSTOMER_TYPE.POSTPAID) {
            this.daySequence();
        }

        this.commondropdownService.getParentPrepaidCustomer();
        //  this.listSearchView = false;
        this.planGroupForm.controls.planId.enable();
        if (this.payMappingListFromArray.controls) {
            this.payMappingListFromArray.controls = [];
        }
        if (this.overChargeListFromArray.controls) {
            this.overChargeListFromArray.controls = [];
        }
        if (this.custMacMapppingListFromArray.controls) {
            this.custMacMapppingListFromArray.controls = [];
        }
        // if (this.uploadDocumentListFromArray.controls) {
        //   this.uploadDocumentListFromArray.controls = [];
        // }
        this.paymentGroupForm.reset();
        this.permanentGroupForm.reset();
        this.viewcustomerListData = [];

        if (chargeid) {
            const url = "/customers/" + chargeid;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.iscustomerEdit = true;
                    this.viewcustomerListData = response.customers;
                    this.customerGroupForm.patchValue(this.viewcustomerListData);
                    this.getBillableCust(this.viewcustomerListData.billableCustomerId);
                    let serviceAreaId = {
                        value: Number(this.viewcustomerListData.serviceareaid)
                    };
                    this.selServiceArea(serviceAreaId);
                    //this.customerGroupForm.controls.username.disable();
                    this.customerGroupForm
                        .get("billTo")
                        .setValue(this.viewcustomerListData.planMappingList[0].billTo);
                    this.customerGroupForm
                        .get("isInvoiceToOrg")
                        .setValue(this.viewcustomerListData.planMappingList[0].isInvoiceToOrg);
                    this.customerGroupForm.get("isCustCaf").setValue("no");
                    this.viewcustomerListData.custtype;
                    if (this.viewcustomerListData.custtype == this.custType) {
                        let obj = {};
                        this.filterPlanData = [];
                        if (this.commondropdownService.postpaidplanData.length != 0) {
                            obj = this.commondropdownService.postpaidplanData.filter(
                                key => key.plantype === this.custType
                            );
                        }
                        this.filterPlanData = obj;
                        obj = {};
                    } else {
                        let obj = {};
                        this.filterPlanData = [];
                        if (this.commondropdownService.postpaidplanData.length != 0) {
                            obj = this.commondropdownService.postpaidplanData.filter(
                                key => key.plantype === this.custType
                            );
                        }
                        this.filterPlanData = obj;
                        obj = {};
                    }

                    if (this.viewcustomerListData.creditDocuments.length !== 0) {
                        this.customerGroupForm.controls.paymentDetails.patchValue(
                            this.viewcustomerListData.creditDocuments[0]
                        );
                    }
                    if (this.viewcustomerListData.parentCustomerId != null) {
                        this.isParantExpirenceEdit = true;
                        this.customerGroupForm.controls.parentExperience.enable();
                        this.customerGroupForm.controls.parentExperience.patchValue(
                            this.viewcustomerListData.parentExperience
                        );
                    } else {
                        this.customerGroupForm.controls.parentExperience.disable();
                    }

                    if (this.viewcustomerListData.parentCustomerId) {
                        this.parentCustList = [
                            {
                                id: this.viewcustomerListData.parentCustomerId,
                                name: this.viewcustomerListData.parentCustomerName
                            }
                        ];
                    } else {
                        this.parentCustList = [];
                    }

                    if (this.viewcustomerListData.parentCustomerId && this.viewcustomerListData.plangroupid) {
                        this.customerGroupForm.controls.invoiceType.enable();
                        this.planGroupForm.controls.invoiceType.disable();
                    } else {
                        this.customerGroupForm.controls.invoiceType.disable();
                        this.planGroupForm.controls.invoiceType.enable();
                    }

                    if (this.viewcustomerListData.plangroupid) {
                        this.ifIndividualPlan = false;
                        this.ifPlanGroup = true;
                        this.planCategoryForm.patchValue({
                            planCategory: "groupPlan"
                        });
                        this.getPlangroupByPlan(this.viewcustomerListData.plangroupid);
                        this.customerGroupForm.patchValue({
                            plangroupid: this.viewcustomerListData.plangroupid
                        });
                    } else {
                        this.ifIndividualPlan = true;
                        this.ifPlanGroup = false;

                        this.planCategoryForm.patchValue({
                            planCategory: "individual"
                        });

                        // plan deatils

                        let newAmount = 0;
                        let totalAmount = 0;
                        let disValue = 0;
                        this.discountValue = 0;
                        this.DiscountValueStore = [];
                        this.viewcustomerListData.planMappingList.forEach((element, i) => {
                            // this.planGroupForm.patchValue(
                            //   this.viewcustomerListData.planMappingList[planlength]
                            // );
                            this.onAddplanMappingList();

                            if (element.planId) {
                                const planAmount = "";
                                let validityUnit = "";
                                const url = "/postpaidplan/" + element.planId;
                                this.customerManagementService.getMethod(url).subscribe((response: any) => {
                                    this.planDropdownInChageData.push(response.postPaidPlan);
                                    let postpaidplanData = response.postPaidPlan;
                                    validityUnit = response.postPaidPlan.unitsOfValidity;
                                    this.payMappingListFromArray.push(
                                        this.fb.group({
                                            service: element.service,
                                            planId: element.planId,
                                            validity: element.validity,
                                            offerPrice: element.offerPrice,
                                            newAmount: element.newAmount,
                                            discount: element.discount,
                                            istrialplan: element.istrialplan,
                                            invoiceType: element.invoiceType,
                                            isInvoiceToOrg: element.isInvoiceToOrg,
                                            discountType: element.discountType,
                                            discountExpiryDate: [
                                                element.discountExpiryDate
                                                    ? moment(element.discountExpiryDate).utc(true).toDate()
                                                    : null
                                            ]
                                        })
                                    );
                                    this.validityUnitFormArray.push(
                                        this.fb.group({
                                            validityUnit
                                        })
                                    );

                                    let n = i + 1;
                                    newAmount =
                                        postpaidplanData.newOfferPrice != null
                                            ? postpaidplanData.newOfferPrice
                                            : postpaidplanData.offerprice;
                                    totalAmount = Number(totalAmount) + Number(newAmount);

                                    if (this.viewcustomerListData.planMappingList.length == n) {
                                        this.planDataForm.patchValue({
                                            offerPrice: totalAmount,
                                            discountPrice: this.viewcustomerListData.flatAmount.toFixed(2)
                                        });

                                        // this.payMappingListFromArray.value.forEach((e, k) => {
                                        //   let discountValueNUmber: any = 0;
                                        //   let lastvalue: any = 0;
                                        //   let m = i + 1;
                                        //   let price = Number(this.payMappingListFromArray.value[k].offerPrice);
                                        //   let discount = Number(this.payMappingListFromArray.value[k].discount);
                                        //   let DiscountV = (price * discount) / 100;
                                        //   discountValueNUmber = DiscountV.toFixed(3);
                                        //   let discountValue =
                                        //     Number(this.payMappingListFromArray.value[k].offerPrice) -
                                        //     Number(discountValueNUmber);
                                        //   this.discountValue = Number(discountValue);

                                        //   this.DiscountValueStore.push({ value: this.discountValue });
                                        //   if (this.discountValue == 0) {
                                        //     disValue =
                                        //       Number(this.payMappingListFromArray.value[k].offerPrice) +
                                        //       Number(this.planDataForm.value.discountPrice);
                                        //   } else {
                                        //     disValue =
                                        //       Number(this.discountValue) +
                                        //       Number(this.planDataForm.value.discountPrice);
                                        //   }

                                        //   if (this.viewcustomerListData.planMappingList.length == m) {
                                        //     this.planDataForm.patchValue({
                                        //       discountPrice: disValue,
                                        //     });
                                        //   }
                                        // });
                                    }
                                });
                            }
                        });

                        // while (
                        //   this.viewcustomerListData.planMappingList.length > planlength
                        // ) {
                        //   this.planGroupForm.patchValue(
                        //     this.viewcustomerListData.planMappingList[planlength]
                        //   );
                        //   this.onAddplanMappingList();
                        //   this.payMappingListFromArray.patchValue(
                        //     this.viewcustomerListData.planMappingList
                        //   );
                        //   planlength++;
                        // }
                    }

                    this.payMappingListFromArray.patchValue(this.viewcustomerListData.planMappingList);

                    // Address
                    if (this.viewcustomerListData.addressList[0].addressType) {
                        this.getTempPincodeData(this.viewcustomerListData.addressList[0].pincodeId, "present");
                        this.getAreaData(this.viewcustomerListData.addressList[0].areaId, "present");
                        this.presentGroupForm.patchValue(this.viewcustomerListData.addressList[0]);

                        this.selServiceAreaByParent(Number(this.viewcustomerListData.serviceareaid));
                        const data = {
                            value: Number(this.viewcustomerListData.addressList[0].pincodeId)
                        };
                        this.selectPINCODEChange(data, "");
                        this.presentGroupForm.patchValue({
                            pincodeId: Number(this.viewcustomerListData.addressList[0].pincodeId)
                        });
                    }
                    if (this.viewcustomerListData.addressList != null) {
                        this.viewcustomerListData.addressList.forEach(element => {
                            // console.log("element", element);
                            if ("Payment" == element.addressType) {
                                this.getTempPincodeData(element.pincodeId, "payment");
                                this.getAreaData(element.areaId, "payment");
                                this.paymentGroupForm.patchValue(element);
                                this.selectAreaListPayment = true;
                                this.selectPincodeListPayment = true;
                            } else if ("Permanent" == element.addressType || "permanent" == element.addressType) {
                                this.getTempPincodeData(element.pincodeId, "permanent");
                                this.getAreaData(element.areaId, "permanent");
                                this.permanentGroupForm.patchValue(element);
                                this.selectAreaListPermanent = true;
                                this.selectPincodeListPermanent = true;
                            }
                        });
                    }

                    this.viewcustomerListData.overChargeList = this.viewcustomerListData.indiChargeList;
                    // charge
                    while (k < this.viewcustomerListData.indiChargeList.length) {
                        if (this.viewcustomerListData.indiChargeList[k].charge_date) {
                            const format = "yyyy-MM-dd";
                            const locale = "en-US";
                            const myDate = this.viewcustomerListData.indiChargeList[k].charge_date;
                            const formattedDate = formatDate(myDate, format, locale);
                            this.viewcustomerListData.indiChargeList[k].charge_date = formattedDate;

                            const date = this.viewcustomerListData.indiChargeList[k].charge_date.split("-");
                            this.ngbBirthcal = {
                                year: Number(date[0]),
                                month: Number(date[1]),
                                day: Number(date[2])
                            };
                            this.overChargeListFromArray.patchValue([
                                {
                                    charge_date: this.viewcustomerListData.indiChargeList[k].charge_date
                                }
                            ]);
                            // console.log(this.viewcustomerListData.indiChargeList[k].charge_date)
                        }

                        this.chargeGroupForm.patchValue(this.viewcustomerListData.indiChargeList[k]);
                        this.onAddoverChargeListField();

                        this.overChargeListFromArray.patchValue(this.viewcustomerListData.indiChargeList);
                        k++;
                    }

                    // MAc
                    while (this.viewcustomerListData.custMacMapppingList.length > macNumber) {
                        this.macGroupForm.patchValue(this.viewcustomerListData.custMacMapppingList[macNumber]);
                        this.onAddMACList();
                        this.custMacMapppingListFromArray.patchValue(
                            this.viewcustomerListData.custMacMapppingList
                        );
                        macNumber++;
                    }
                    // this.planGroupForm.controls.service.disable();
                    // this.planGroupForm.controls.planId.disable();
                    // this.planGroupForm.controls.validity.disable();
                    this.selectAreaList = true;
                    this.selectPincodeList = true;
                    if (this.viewcustomerListData.dunningType != null) {
                        const data = {
                            value: this.viewcustomerListData.dunningType
                        };
                        this.customerGroupForm.controls.dunningSubType.enable();
                        this.getcustType(data);
                    } else {
                        this.customerGroupForm.controls.dunningSubType.disable();
                    }

                    if (this.viewcustomerListData.dunningSector != null) {
                        this.customerGroupForm.controls.dunningSubSector.enable();
                    } else {
                        this.customerGroupForm.controls.dunningSubSector.disable();
                    }
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    deleteConfirmoncustomer(customerId: number) {
        if (customerId) {
            this.confirmationService.confirm({
                message: "Do you want to delete this customer?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deletecustomer(customerId);
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    deletecustomer(customerId) {
        const url = "/customers/" + customerId;
        this.customerManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPagecustomerListdata != 1 && this.customerListDatalength == 1) {
                    this.currentPagecustomerListdata = this.currentPagecustomerListdata - 1;
                }
                this.toastr.success(`${response.message}`, 'Success!');

                if (this.searchkey) {
                    this.searchcustomer();
                } else {
                    this.getcustomerList("");
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedcustomerList(pageNumber) {
        this.currentPagecustomerListdata = pageNumber;
        if (this.searchkey) {
            this.searchcustomer();
        } else {
            this.getcustomerList("");
        }
    }

    pageChangedpayMapping(pageNumber) {
        this.currentPagePayMapping = pageNumber;
    }

    pageChangedOverChargeList(pageNumber) {
        this.currentPageoverChargeList = pageNumber;
    }

    pageChangedOverUploadDocumentList(pageNumber) {
        this.currentPageoverUploadDocumentList = pageNumber;
    }

    pageChangedcustledgerList(pageNumber) {
        this.currentPagecustLedgerList = pageNumber;
        this.getCustomersLedger(this.customerLedgerDetailData.id, "");
    }

    pageChangedcustChargeDetailList(pageNumber) {
        this.currentPagecustChargeDeatilList = pageNumber;
    }

    pageChangedcustPlanDetailList(pageNumber) {
        this.currentPagecustPlanDeatilList = pageNumber;
    }

    pageChangedcustMacAddDetailList(pageNumber) {
        this.currentPagecustMacAddList = pageNumber;
    }

    pageChangedreturnItemDetailList(pageNumber) {
        this.currentPagereturnAddList = pageNumber;
    }

    pageChangedcustomerPaymentList(pageNumber) {
        this.currentPagecustomerPaymentdata = pageNumber;
        this.openCustomersPaymentData(this.customerLedgerDetailData.id, "");
    }

    pageChangedcustFuturePlanListData(pageNumber) {
        this.currentPagecustomerFuturePlanListdata = pageNumber;
        this.getcustFuturePlan(this.customerLedgerDetailData.id, "");
    }

    pageChangedcustomerExpiryPlanListData(pageNumber) {
        this.currentPagecustomerExpiryPlanListdata = pageNumber;
        this.getcustExpiredPlan(this.customerLedgerDetailData.id, "");
    }

    pageChangedcustomerCurrentPlanListData(pageNumber) {
        this.currentPagecustomerCurrentPlanListdata = pageNumber;
        this.getcustCurrentPlan(this.customerLedgerDetailData.id, "");
    }

    deleteConfirmonChargeField(chargeFieldIndex: number, name: string) {
        if (chargeFieldIndex || chargeFieldIndex == 0) {
            const msgTxt: string = "";
            if (name == "paymapping") {
                msgTxt == "Do you want to delete this Payment ?";
            } else if (name == "chargelist") {
                msgTxt == "Do you want to delete this Charge ?";
            } else if (name == "MAC") {
                msgTxt == "Do you want to delete this MAC Address ?";
            } else if (name == "uploadDocument") {
                msgTxt == "Do you want to delete this Document ?";
            }
            this.confirmationService.confirm({
                message: "Do you want to delete this " + name + "?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    // console.log(name);
                    switch (name) {
                        case "Plan":
                            if (this.ifplanisSubisuSelect == true) {
                                this.ifplanisSubisuSelect = false;
                                this.customerGroupForm.patchValue({
                                    billTo: "CUSTOMER",
                                    parentExperience: "Actual"
                                });
                            }

                            this.onRemovePayMapping(chargeFieldIndex);
                            break;
                        case "Charge":
                            this.onRemoveChargelist(chargeFieldIndex);
                            break;
                        case "MAC":
                            this.onRemoveMACaddress(chargeFieldIndex);
                            break;
                        // case 'uploadDocument':
                        //   this.onRemoveUploadDocument(chargeFieldIndex);
                        //   break;
                    }
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    async onRemovePayMapping(chargeFieldIndex: number) {
        this.planTotalOffetPrice =
            this.planTotalOffetPrice -
            Number(this.payMappingListFromArray.value[chargeFieldIndex].offerPrice);

        this.planDataForm.patchValue({
            offerPrice: this.planTotalOffetPrice,
            discountPrice: Number(
                this.planDataForm.value.discountPrice - this.DiscountValueStore[chargeFieldIndex].value
            ).toFixed(2)
        });

        this.payMappingListFromArray.removeAt(chargeFieldIndex);

        this.DiscountValueStore.splice(chargeFieldIndex, 1);
        if (this.payMappingListFromArray.value.length == 0) {
            this.DiscountValueStore = [];
            this.planTotalOffetPrice = 0;
            this.planDataForm.patchValue({
                discountPrice: 0,
                offerPrice: 0
            });
        }
        this.changeTrialCheck();
    }

    async onRemoveChargelist(index: number) {
        let Data: any;
        //
        if (!this.viewcustomerListData.overChargeList) {
            this.overChargeListFromArray.removeAt(index);
        } else {
            Data = this.viewcustomerListData.overChargeList[index];

            const url = "/subscriber/reverseCharge";
            const ChargeData = {
                charge_id: Data.id,
                custId: this.customerID,
                rev_amt: Data.actualprice,
                rev_date: this.currentDate,
                rev_remarks: "charge_Cancel"
            };

            this.customerManagementService.postMethod(url, ChargeData).subscribe(
                (response: any) => {
                    this.overChargeListFromArray.removeAt(index);
                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    async onRemoveMACaddress(chargeFieldIndex: number) {
        this.custMacMapppingListFromArray.removeAt(chargeFieldIndex);
    }

    searchcustomer() {
        if (this.searchOption !== "cafCreatedDate" && this.searchOption !== "expiryDate") {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchDeatil.trim() ||
                !this.searchkey2 ||
                this.searchkey2 !== this.searchOption.trim()
            ) {
                this.currentPagecustomerListdata = 1;
            }
            this.searchkey = this.searchDeatil.trim();
            this.searchkey2 = this.searchOption.trim();

            this.searchData.filters[0].filterValue = this.searchDeatil.trim();
            this.searchData.filters[0].filterColumn = this.searchOption.trim();
        } else {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchDeatil ||
                !this.searchkey2 ||
                this.searchkey2 !== this.searchOption
            ) {
                this.currentPagecustomerListdata = 1;
            }
            let searchDeatil = this.datePipe.transform(this.searchDeatil, "yyyy-MM-dd");
            this.searchkey = searchDeatil;
            this.searchkey2 = this.searchOption;

            this.searchData.filters[0].filterValue = searchDeatil;
            this.searchData.filters[0].filterColumn = this.searchOption;
        }
        if (this.searchOption == "service") {
            this.getSearchCustomerByService();
        } else {
            this.searchData.page = this.currentPagecustomerListdata;
            this.searchData.pageSize = this.customerListdataitemsPerPage;
            const url = "/customers/search/" + this.custType;
            // console.log("this.searchData", this.searchData)
            this.customerManagementService.postMethod(url, this.searchData).subscribe(
                (response: any) => {
                    this.customerListData = response.customerList;
                    const usernameList: string[] = [];
                    this.customerListData.forEach(element => {
                        usernameList.push(element.username);
                    });

                    this.liveUserService
                        .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                            users: usernameList
                        })
                        .subscribe((res: any) => {
                            const liveUsers: string[] = res.liveusers;
                            this.customerListData.forEach(element => {
                                if (liveUsers.findIndex(e => e == element.username) < 0) {
                                    element.connectionMode = "Offline";
                                } else {
                                    element.connectionMode = "Online";
                                }
                            });
                        });
                    this.customerListdatatotalRecords = response.pageDetails.totalRecords;

                    if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                        this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                    } else {
                        this.customerListDatalength =
                            this.customerListData.length % this.customerListdataitemsPerPage;
                    }
                },
                (error: any) => {
                    this.customerListdatatotalRecords = 0;
                    if (error.error.status == 404) {
                        this.toastr.error(`${error.error.msg}`, 'Failed!');


                        this.customerListData = [];
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                }
            );
        }
    }

    getSearchCustomerByService() {
        const url =
            "/getByCustomerService?page=" +
            this.currentPagecustomerListdata +
            "&pageSize=" +
            this.customerListdataitemsPerPage +
            "&service=" +
            this.searchDeatil +
            "&customerType=" +
            this.custType;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerListData = response.customers.content;
                const usernameList: string[] = [];
                this.customerListData.forEach(element => {
                    usernameList.push(element.username);
                });

                this.liveUserService
                    .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                        users: usernameList
                    })
                    .subscribe((res: any) => {
                        const liveUsers: string[] = res.liveusers;
                        this.customerListData.forEach(element => {
                            if (liveUsers.findIndex(e => e == element.username) < 0) {
                                element.connectionMode = "Offline";
                            } else {
                                element.connectionMode = "Online";
                            }
                        });
                    });
                this.customerListdatatotalRecords = response.customers.totalElements;

                if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                    this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                } else {
                    this.customerListDatalength =
                        this.customerListData.length % this.customerListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.customerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');


                    this.customerListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    clearSearchcustomer() {
        this.getcustomerList("");
        this.searchDeatil = "";
        this.searchOption = "";
        // this.fieldEnable = false;
        this.currentPagecustomerListdata = 1;
    }

    async selCustType() {
        const custType = this.custType;
        let obj: any = [];
        this.filterPlanData = [];
        if (this.commondropdownService.postpaidplanData.length != 0) {
            obj = this.commondropdownService.postpaidplanData.filter(key => key.plantype === custType);

            //
            // if (!this.planGroupForm.controls.service) {
            this.filterPlanData = obj;
            // console.log(" this.filterPlanData", this.filterPlanData);
            // } else if (this.planGroupForm.controls.service) {
            //
            //   this.serviceBasePlanDATA(this.planGroupForm.controls.service);
            // }

            if (this.planGroupForm.value) {
                this.planGroupForm.reset();
                this.plantypaSelectData = [];
            }
        }
    }

    TotalLedgerItemPerPage(event) {
        this.legershowItemPerPage = Number(event.value);
        if (this.currentPagecustLedgerList > 1) {
            this.currentPagecustLedgerList = 1;
        }
        if (!this.customerLedgerSearchKey) {
            this.getCustomersLedger(this.customerLedgerDetailData.id, this.legershowItemPerPage);
        } else {
            this.searchCustomerLedger();
        }
    }

    getCustomersLedger(custId, size) {
        let page_list;
        this.customerLedgerSearchKey = "";
        if (size) {
            page_list = size;
            this.custLedgerItemPerPage = size;
        } else {
            if (this.legershowItemPerPage == 1) {
                this.custLedgerItemPerPage = this.pageITEM;
            } else {
                this.custLedgerItemPerPage = this.legershowItemPerPage;
            }
        }
        const url = "/customerLedgers";
        this.postdata.custId = custId;
        this.customerManagementService.postMethod(url, this.postdata).subscribe(
            (response: any) => {
                this.customerLedgerData = response.customerLedgerDtls;
                this.customerLedgerListData =
                    response.customerLedgerDtls.customerLedgerInfoPojo.debitCreditDetail;
                // console.log("this.customerLedgerData", this.customerLedgerData);
                this.customerLedgerOpen();
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getCustomersDetail(custId) {
        this.planDropdownInChageData = [];
        this.presentAdressDATA = [];
        this.permentAdressDATA = [];
        this.paymentAdressDATA = [];
        this.partnerDATA = [];
        this.chargeDATA = [];
        let plandatalength = 0;
        const chargeLength = 0;
        this.paymentDataamount = "";
        this.paymentDatareferenceno = "";
        this.paymentDatapaymentdate = "";
        this.paymentDatapaymentMode = "";
        this.FinalAmountList = [];

        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerLedgerDetailData = response.customers;
                this.customerAddress = this.customerLedgerDetailData.addressList.find(
                    address => address.version === "NEW"
                );

                if (response.customers.creditDocuments.length !== 0) {
                    this.paymentDataamount = response.customers.creditDocuments[0].amount;
                    this.paymentDatareferenceno = response.customers.creditDocuments[0].referenceno;
                    this.paymentDatapaymentdate = response.customers.creditDocuments[0].paymentdate;
                    this.paymentDatapaymentMode = response.customers.creditDocuments[0].paymode;
                }
                const paymentaddressType = response.customers.addressList.filter(
                    key => key.addressType === "Payment"
                );
                if (paymentaddressType) {
                    this.paymentAddressData = paymentaddressType;
                } else {
                    this.paymentAddressData = {
                        fullAddress: ""
                    };
                }
                const permanentaddressType = response.customers.addressList.filter(
                    key => key.addressType === "Permanent"
                );
                if (permanentaddressType) {
                    this.permanentAddressData = permanentaddressType;
                } else {
                    this.permanentAddressData = {
                        fullAddress: ""
                    };
                }

                //pop Name
                if (this.customerLedgerDetailData.popid) {
                    let partnerurl = "/popmanagement/" + this.customerLedgerDetailData.popid;
                    this.customerManagementService.getMethod(partnerurl).subscribe((response: any) => {
                        this.customerPopName = response.data.name;

                        // console.log("partnerDATA", this.partnerDATA);
                    });
                }

                // partner Name
                if (this.customerLedgerDetailData.partnerid) {
                    const partnerurl = "/partner/" + this.customerLedgerDetailData.partnerid;
                    this.partnerService.getMethodNew(partnerurl).subscribe((response: any) => {
                        this.partnerDATA = response.partnerlist.name;

                        // console.log("partnerDATA", this.partnerDATA);
                    });
                }

                // serviceArea Name
                if (this.customerLedgerDetailData.serviceareaid) {
                    const serviceareaurl = "/serviceArea/" + this.customerLedgerDetailData.serviceareaid;
                    this.savbillCommonBaseService.get(serviceareaurl).subscribe((response: any) => {
                        this.serviceAreaDATA = response.data.name;

                        // console.log("partnerDATA", this.serviceAreaDATA);
                    });
                }

                // Address
                if (this.customerAddress.addressType) {
                    const areaurl = "/area/" + this.customerAddress.areaId;

                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                        this.presentAdressDATA = response.data;
                    });
                }
                if (this.customerLedgerDetailData.addressList.length > 1) {
                    let j = 0;
                    // while (j < this.customerLedgerDetailData.addressList.length) {
                    this.customerLedgerDetailData.addressList.forEach(address => {
                        const addres1 = address.addressType;
                        if (addres1) {
                            if ("Payment" == addres1) {
                                const areaurl = "/area/" + address.areaId;
                                this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                    this.paymentAdressDATA = response.data;
                                });
                            } else {
                                const areaurl = "/area/" + address.areaId;
                                this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                    this.permentAdressDATA = response.data;
                                });
                            }
                        }
                    });
                }

                // }
                if (this.customerLedgerDetailData.planMappingList.length > 0) {
                    
                    this.customerBill = this.customerLedgerDetailData.planMappingList[0].billTo;
                    this.custInvoiceToOrg = this.customerLedgerDetailData.planMappingList[0].isInvoiceToOrg;
                }
                if (this.customerLedgerDetailData.plangroupid) {
                    this.ifIndividualPlan = false;
                    this.ifPlanGroup = true;

                    this.getPlangroupByPlan(this.customerLedgerDetailData.plangroupid);
                    const planGroupurl =
                        "/findPlanGroupById?planGroupId=" + this.customerLedgerDetailData.plangroupid;

                    this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                        this.planGroupName = response.planGroup.planGroupName;
                    });
                } else {
                    this.ifIndividualPlan = true;
                    this.ifPlanGroup = false;
                    this.planMappingList = this.customerLedgerDetailData.planMappingList;
                    this.customerLedgerDetailData.planMappingList =
                        this.customerLedgerDetailData.planMappingList.filter(
                            data => data.custPlanStatus == "Active"
                        );
                    while (plandatalength < this.customerLedgerDetailData.planMappingList.length) {
                        const planId = this.customerLedgerDetailData.planMappingList[plandatalength].planId;
                        let discount;
                        if (
                            this.customerLedgerDetailData.planMappingList[plandatalength].discount == null ||
                            this.customerLedgerDetailData.planMappingList[plandatalength].discount == ""
                        ) {
                            discount = 0;
                        } else {
                            discount = this.customerLedgerDetailData.planMappingList[plandatalength].discount;
                        }

                        const planurl = "/postpaidplan/" + planId;
                        this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                            this.dataPlan.push(response.postPaidPlan);
                            this.planDropdownInChageData.push(response.postPaidPlan);
                            // console.log("dataPlan", this.dataPlan);
                        });

                        this.customerManagementService
                            .getofferPriceWithTax(planId, discount)
                            .subscribe((response: any) => {
                                if (response.result.finalAmount) {
                                    this.FinalAmountList.push(response.result.finalAmount);
                                } else {
                                    this.FinalAmountList.push(0);
                                }
                            });
                        plandatalength++;
                    }
                }

                // charger Data
                if (this.customerLedgerDetailData.indiChargeList.length > 0) {
                    this.addChargeForm.patchValue({
                        chargeAdd: true
                    });

                    this.customerLedgerDetailData.indiChargeList.forEach((element, k) => {
                        if (element.planid) {
                            const url = "/postpaidplan/" + element.planid;
                            this.customerManagementService.getMethod(url).subscribe((response: any) => {
                                this.dataChargePlan.push(response.postPaidPlan);
                            });
                        }
                    });
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    searchCustomerLedger() {
        if (
            !this.customerLedgerSearchKey ||
            this.customerLedgerSearchKey !== this.custLedgerForm.value
        ) {
            this.currentPagecustLedgerList = 1;
        }
        this.customerLedgerSearchKey = this.custLedgerForm.value;

        if (this.legershowItemPerPage == 1) {
            this.custLedgerItemPerPage = this.pageITEM;
        } else {
            this.custLedgerItemPerPage = this.legershowItemPerPage;
        }

        this.custLedgerSubmitted = true;
        if (this.custLedgerForm.valid) {
            this.postdata.CREATE_DATE = this.custLedgerForm.controls.startDateCustLedger.value;
            this.postdata.END_DATE = this.custLedgerForm.controls.endDateCustLedger.value;
        }
        this.getCustomersLedger(this.customerLedgerData.custId, "");
    }

    clearSearchCustomerLedger() {
        this.postdata.CREATE_DATE = "";
        this.postdata.END_DATE = "";
        this.custLedgerForm.controls.startDateCustLedger.setValue("");
        this.custLedgerForm.controls.endDateCustLedger.setValue("");
        this.custLedgerSubmitted = false;
        this.getCustomersLedger(this.customerLedgerData.custId, "");
    }

    selSearchOption(event) {
        // console.log("value", event.value);
        this.searchDeatil = "";
        // if (event.value) {
        //   this.fieldEnable = true;
        // } else {
        //   this.fieldEnable = false;
        // }
    }

    getPlangroupByPlan(planGroupId) {
        this.planDropdownInChageData = [];
        const MappURL = "/findPlanGroupMappingByPlanGroupId?planGroupId=" + planGroupId;
        this.customerManagementService.getMethod(MappURL).subscribe((response: any) => {
            const attributeList = response.planGroupMappingList;
            attributeList.forEach(element => {
                this.planDropdownInChageData.push(element.plan);
            });

            if (this.ifPlanGroup && this.iscustomerEdit) {
                let newAmount = 0;
                let totalAmount = 0;
                attributeList.forEach((element, i) => {
                    let n = i + 1;
                    newAmount =
                        element.plan.newOfferPrice != null
                            ? element.plan.newOfferPrice
                            : element.plan.offerprice;
                    totalAmount = Number(totalAmount) + Number(newAmount);
                    if (attributeList.length == n) {
                        this.planDataForm.patchValue({
                            offerPrice: totalAmount
                        });

                        let price = Number(this.planDataForm.value.offerPrice);
                        let discount = Number(this.customerGroupForm.value.discount);
                        let DiscountV = (price * discount) / 100;
                        let discountValueNUmber = DiscountV.toFixed(2);
                        this.discountValue = Number(discountValueNUmber);
                        let discountfV = Number(this.planDataForm.value.offerPrice) - this.discountValue;
                        this.planDataForm.patchValue({
                            discountPrice: discountfV.toFixed(2)
                        });
                    }
                });
            }
        });
    }

    getPlanValidityForChagre(event) {
        const planId = event.value;
        // const url = "/postpaidplan/" + planId;
        // this.customerManagementService.getMethod(url).subscribe((response: any) => {
        // const planDetailData = response.postPaidPlan;

        var date = moment(
            this.planDropdownInChageData.find(plan => plan.id == planId).expiryDate,
            "DD-MM-yyyy hh:mm:ss"
        ).toDate();

        this.chargeGroupForm.patchValue({
            validity: Number(this.planDropdownInChageData.find(plan => plan.id == planId).validity),
            unitsOfValidity: this.planDropdownInChageData.find(plan => plan.id == planId).unitsOfValidity,
            expiry: date
        });
        let planData = null;
        if (this.customerChangePlan) {
            planData = this.custCustDiscountList.find(element => element.id === this.custPlanMapppingId);
        } else {
            planData = this.payMappingListFromArray.value.find(element => element.planId === planId);
        }
        if (
            planData &&
            planData.discountType === "Recurring" &&
            moment(planData.discountExpiryDate).isSameOrAfter(moment(), "day") &&
            planData.discount > 0
        ) {
            this.confirmationService.confirm({
                message: "Do you want to apply " + planData.discount + " % of  Discount?",
                header: "Change Discount Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.chargeGroupForm.patchValue({
                        discount: planData.discount
                    });
                    this.updateDiscountFromService(event.value, "");
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                    this.chargeGroupForm.patchValue({
                        discount: 0
                    });
                    this.updateDiscountFromService(event.value, "");
                }
            });
        } else if (
            planData &&
            planData.discountType === "Recurring" &&
            moment(planData.discountExpiryDate).isSameOrAfter(moment(), "day") &&
            planData.discount < 0
        ) {
            this.confirmationService.confirm({
                message: "Do you want to over charge customer " + planData.discount + " % ?",
                header: "Change Discount Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.chargeGroupForm.patchValue({
                        discount: planData.discount
                    });
                    this.updateDiscountFromService(event.value, "");
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                    this.chargeGroupForm.patchValue({
                        discount: 0
                    });
                    this.updateDiscountFromService(event.value, "");
                }
            });
        } else {
            this.chargeGroupForm.patchValue({
                discount: 0
            });
            this.updateDiscountFromService(event.value, "");
        }
        //
        // });
    }

    selectcharge(_event: any, type) {
        const chargeId = _event.value;
        let viewChargeData;
        let date;

        date = this.currentDate.toISOString();
        const format = "yyyy-MM-dd";
        const locale = "en-US";
        const myDate = date;
        const formattedDate = formatDate(myDate, format, locale);
        //
        // console.log(this.currentDate);
        const url = "/charge/" + chargeId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            viewChargeData = response.chargebyid;
            this.selectchargeValueShow = true;
            if (type === "shiftLocation") {
                this.shiftLocationChargeGroupForm.patchValue({
                    actualprice: Number(viewChargeData.actualprice),
                    charge_date: formattedDate,
                    type: "One-time"
                });
            } else {
                this.chargeGroupForm.patchValue({
                    actualprice: Number(viewChargeData.actualprice),
                    charge_date: formattedDate,
                    type: "One-time"
                });
            }
        });
    }

    customerFormReset() {
        this.customerGroupForm.reset();
        this.presentGroupForm.reset();
        this.paymentGroupForm.reset();
        this.permanentGroupForm.reset();
        this.chargeGroupForm.reset();
        this.planGroupForm.reset();
        this.macGroupForm.reset();
        this.planDataForm.reset();
        this.validityUnitFormGroup.reset();
        this.planDataForm.controls.offerPrice.setValue("");
        this.planDataForm.controls.discountPrice.setValue("");
        this.validityUnitFormGroup.controls.validityUnit.setValue("");
        this.planGroupForm.controls.planId.setValue("");
        this.planGroupForm.controls.service.setValue("");
        this.planGroupForm.controls.validity.setValue("");
        this.customerGroupForm.controls.pan.setValue("");
        this.customerGroupForm.controls.gst.setValue("");
        this.customerGroupForm.controls.failcount.setValue("");
        this.customerGroupForm.controls.aadhar.setValue("");
        this.customerGroupForm.controls.passportNo.setValue("");
        this.customerGroupForm.controls.voicesrvtype.setValue("");
        this.customerGroupForm.controls.didno.setValue("");
        this.customerGroupForm.controls.salesremark.setValue("");
        this.customerGroupForm.controls.servicetype.setValue("");
        this.customerGroupForm.controls.partnerid.setValue(this.partnerId !== 1 ? this.partnerId : "");
        this.customerGroupForm.controls.billday.setValue("");
        this.customerGroupForm.controls.phone.setValue("");
        this.customerGroupForm.controls.mobile.setValue("");
        this.customerGroupForm.controls.countryCode.setValue("");

        this.customerGroupForm.controls.status.setValue("");
        this.customerGroupForm.controls.serviceareaid.setValue("");
        this.customerGroupForm.controls.title.setValue("");
        this.customerGroupForm.controls.billTo.setValue("CUSTOMER");
        this.customerGroupForm.controls.parentExperience.setValue("Actual");
        this.chargeGroupForm.controls.chargeid.setValue("");
        this.chargeGroupForm.controls.charge_date.setValue("");
        this.chargeGroupForm.controls.planid.setValue("");
        this.chargeGroupForm.controls.type.setValue("");

        this.customerGroupForm.controls.isInvoiceToOrg.setValue(false);
        this.customerGroupForm.controls.istrialplan.setValue(false);
        this.presentGroupForm.controls.areaId.setValue("");
        this.presentGroupForm.controls.pincodeId.setValue("");
        this.presentGroupForm.controls.cityId.setValue("");
        this.presentGroupForm.controls.stateId.setValue("");
        this.presentGroupForm.controls.countryId.setValue("");

        this.paymentGroupForm.controls.areaId.setValue("");
        this.paymentGroupForm.controls.pincodeId.setValue("");
        this.paymentGroupForm.controls.cityId.setValue("");
        this.paymentGroupForm.controls.stateId.setValue("");
        this.paymentGroupForm.controls.countryId.setValue("");

        this.permanentGroupForm.controls.areaId.setValue("");
        this.permanentGroupForm.controls.pincodeId.setValue("");
        this.permanentGroupForm.controls.cityId.setValue("");
        this.permanentGroupForm.controls.stateId.setValue("");
        this.permanentGroupForm.controls.countryId.setValue("");

        this.discountValue = "";
        this.planTotalOffetPrice = 0;
    }

    changePasswordWithpopup() {
        if (this.changePasswordForm.dirty) {
            this.confirmationService.confirm({
                message: "Do you want to change password for this customer?",
                header: "Change Password Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.changePassword();
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    // change Password
    changePassword() {
        const url = "/updatePassword";
        //
        // this.changeSubmitted = false;

        if (this.changePasswordForm.valid) {
            // if (this.changePasswordForm.value.newpassword === this.changePasswordForm.value.selfcarepwd) {
            this.changePasswordForm.value.custId = this.customerLedgerDetailData.id;
            this.changePasswordForm.value.remarks = "";
            this.changePasswordForm.value.selfcarepwd = this.changePasswordForm.value.newpassword;
            this.changePasswordvalue = this.changePasswordForm.value;

            this.customerManagementService.PostSubMethod(url, this.changePasswordvalue).subscribe(
                (response: any) => {
                    if (response.responseCode == 417) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');


                    } else {
                        this.clearChangePasswordForm();
                        this.closebutton.nativeElement.click();
                        // this.changeSubmitted = true;
                        this.toastr.success(`${response.responseMessage}`, 'Password Update Successfully!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
            // }
        }
    }

    clearChangePasswordForm() {
        this.changePasswordForm.reset();
    }

    pageChangedTicketConfig(pageNumber) {
        this.currentPageTicketConfig = pageNumber;
        this.getcustTicket(this.customerLedgerDetailData.id, "");
    }

    getCustQuotaList(custId) {
        this.customerManagementService.getCustQuotaList(custId).subscribe(
            (response: any) => {
                this.custQuotaList = response.custQuotaList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getReturnItemList(custid) {
        this.customerManagementService.getRetunItemList(custid).subscribe(
            (response: any) => {
                this.returnMapppingList = response;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    ifdiscounAllow = true;
    checkIfDiscount(planId) {
        let data: any;
        if (planId !== null && planId !== undefined && planId !== "") {
            // return !this.plantypaSelectData.find(plan => plan.id === planId).allowdiscount;

            data = this.plantypaSelectData.find(plan => plan.id === planId);

            if (data.allowdiscount) {
                this.ifdiscounAllow = false;
            } else {
                this.ifdiscounAllow = true;
            }
        } else {
            this.ifdiscounAllow = false;
        }
    }

    checkIfDiscountPlanGroup(plangroupid) {
        if (plangroupid !== null && plangroupid !== undefined && plangroupid !== "") {
           
            return !this.filterNormalPlanGroup.find(planGroup => planGroup.planGroupId === plangroupid)
                .allowDiscount;
        } else {
            return false;
        }
    }

    pageChangedCustQuotaList(pageNumber) {
        this.currentPagecustQuotaList = pageNumber;
    }

    getPlanDetailById(event) {
        this.planDiscount = 0;
        this.planDropdownInChageData = [];
        this.plansArray = this.fb.array([]);
        this.ifPlanSelectChanePlan = true;
        this.planSelected = event.value ? event.value : event;
        const url = "/postpaidplan/" + this.planSelected;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.selPlanData = response.postPaidPlan;
                this.planDropdownInChageData.push(response.postPaidPlan);

                // console.log("this.selPlanData", this.selPlanData);
                const date = new Date();
                this.selPlanData.activationDate = this.datepipe.transform(date, "dd-MM-yyyy");
                this.selPlanData.expiryDate = date.setDate(date.getDate() + this.selPlanData.validity);
                this.selPlanData.expiryDate = this.datepipe.transform(
                    this.selPlanData.expiryDate,
                    "dd-MM-yyyy"
                );
                this.selPlanData.finalAmount = this.selPlanData.offerprice + this.selPlanData.taxamount;
                this.changePlanStartEndDate();
                let discountData = this.custCustDiscountList.find(element =>
                    element.id === this.custPlanMapppingId
                        ? this.custPlanMapppingId
                        : this.custServiceData[this.currentIndex].customerServiceMappingId
                );
                if (
                    discountData &&
                    discountData.discountType === "Recurring" &&
                    moment(discountData.discountExpiryDate).isSameOrAfter(moment(), "day") &&
                    discountData.discount > 0
                ) {
                    this.confirmationService.confirm({
                        message: "Do you want to apply " + discountData.discount + " % of  Discount?",
                        header: "Change Discount Confirmation",
                        icon: "pi pi-info-circle",
                        accept: () => {
                            this.planDiscount = discountData.discount;
                            this.updateDiscountFromService(event.value, event.index);
                            this.custServiceData[this.currentIndex].newDiscount = discountData.discount;
                        },
                        reject: () => {
                            (error: any) => {

                                this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                            }

                            this.planDiscount = 0;
                            this.custServiceData[this.currentIndex].newDiscount = 0;
                            this.updateDiscountFromService(event.value, event.index);
                        }
                    });
                } else if (
                    discountData &&
                    discountData.discountType === "Recurring" &&
                    moment(discountData.discountExpiryDate).isSameOrAfter(moment(), "day") &&
                    discountData.discount < 0
                ) {
                    this.planDiscount = discountData.discount;
                    this.custServiceData[this.currentIndex].newDiscount = 0;
                    this.updateDiscountFromService(event.value, event.index);
                    // this.confirmationService.confirm({
                    //   message: "Do you want to over charge customer " + discountData.discount + " % ?",
                    //   header: "Change Discount Confirmation",
                    //   icon: "pi pi-info-circle",
                    //   accept: () => {
                    //     this.planDiscount = discountData.discount;
                    //     this.custServiceData[this.currentIndex].newDiscount = discountData.discount;
                    //     this.updateDiscountFromService(event.value, event.index);
                    //   },
                    //   reject: () => {
                    //     this.messageService.add({
                    //       severity: "info",
                    //       summary: "Rejected",
                    //       detail: "You have rejected",
                    //     });
                    //     this.planDiscount = 0;
                    //     this.custServiceData[this.currentIndex].newDiscount = 0;
                    //     this.updateDiscountFromService(event.value, event.index);
                    //   },
                    // });
                } else {
                    this.planDiscount = 0;
                    this.custServiceData[this.currentIndex].newDiscount = 0;
                    this.updateDiscountFromService(event.value, event.index);
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    updateDiscountFromService(id, index) {
        if (
            (this.ifPlanGroup || this.changenewPlanForm.value.ChangePlanCategory === "groupPlan") &&
            this.changePlanForm.value.purchaseType !== "Addon"
        ) {
            this.custServiceData.find(serviceData => serviceData.newplan === id).discount =
                this.planDiscount;
            this.finalOfferPrice = 0;
            this.custServiceData.forEach(custChild => {
                if (index !== "") {
                    this.groupOfferPrices[index] = Number(this.selPlanData.offerprice);
                }
                if (custChild.newplan) {
                    this.customerManagementService
                        .getofferPriceWithTax(
                            custChild.newplan,
                            custChild.discount,
                            this.changenewPlanForm.value.ChangePlanCategory === "groupPlan"
                                ? this.planGroupSelected
                                : ""
                        )
                        .subscribe((response: any) => {
                            if (response.result.finalAmount) {
                                this.finalOfferPrice =
                                    this.finalOfferPrice + Number(response.result.finalAmount.toFixed(3));
                            } else {
                                this.finalOfferPrice = 0;
                            }
                        });
                }
            });
            this.offerPrice = 0;
            for (let obj of Object.keys(this.groupOfferPrices)) {
                this.offerPrice += Number(this.groupOfferPrices[obj]);
            }
        } else {
            this.offerPrice = 0;
            this.changePlanForm.value.discount = this.planDiscount;
            this.finalOfferPrice = 0;
            this.offerPrice += Number(this.selPlanData.offerprice);
            this.customerManagementService
                .getofferPriceWithTax(this.changePlanForm.value.planId, this.planDiscount)
                .subscribe((response: any) => {
                    if (response.result.finalAmount) {
                        this.finalOfferPrice = Number(response.result.finalAmount.toFixed(3));
                    } else {
                        this.finalOfferPrice = 0;
                    }
                });
        }
        if (
            this.customerLedgerDetailData.planMappingList[0].billTo == "ORGANIZATION" ||
            this.customerLedgerDetailData.planMappingList[0].billTo == "Organization"
        ) {
            this.confirmationService.confirm({
                message: "The customer is bill_to organization, do you want to continue?",
                header: "Change Plan Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.subisuChange = true;
                    this.plansArray.push(
                        this.fb.group({
                            planId: this.selPlanData.id,
                            name: this.selPlanData.displayName,
                            service: this.selPlanData.serviceId,
                            validity: this.selPlanData.validity,
                            discount: this.selPlanData.discount,
                            billTo: "ORGANIZATION",
                            offerPrice: this.selPlanData.offerprice,
                            newAmount:
                                this.selPlanData.newAmount != null
                                    ? this.selPlanData.newAmount
                                    : this.selPlanData.offerprice,
                            chargeName: this.selPlanData.chargeList[0].charge.name,
                            isInvoiceToOrg: this.customerGroupForm.value.isInvoiceToOrg,
                            istrialplan: this.customerGroupForm.value.istrialplan
                            // invoiceType: this.customerGroupForm.value.invoiceType,
                        })
                    );
                    // }
                    $("#selectPlanGroup").modal("show");
                },
                reject: () => {
                    this.subisuChange = false;
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                    // $("#selectPlanChange").modal("show");
                }
            });
        }
        // }
    }

    getChangePlan($event) {
        if (
            this.changenewPlanForm.value.ChangePlanCategory == "groupPlan" &&
            this.changePlanForm.value.purchaseType === "Changeplan"
        ) {
            this.changePlanForm.get("planGroupId").setValidators([Validators.required]);
            this.changePlanForm.get("planGroupId").updateValueAndValidity();
            this.confirmationService.confirm({
                message: "Do you want Change Plan to continue?",
                header: "Change Plan Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    // this.subisuChange = true;
                    this.planGroupFlag = true;
                    // this.customerLedgerDetailData.plangroupid = 0;
                    this.getplanChangeforplanGroup(this.custDetilsCustId);
                },
                reject: () => {
                    // this.subisuChange = false;
                    this.planGroupFlag = false;
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                    // $("#selectPlanChange").modal("show");
                }
            });
        } else if (
            this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
            this.customerLedgerDetailData.plangroupid !== null
        ) {
            // this.planGroupFlag = false;
            this.changePlanForm.get("planGroupId").setValue(null);
            this.changePlanForm.get("planGroupId").clearValidators();
            this.changePlanForm.get("planGroupId").updateValueAndValidity();
            this.getplanChangeforplanGroup(this.custDetilsCustId);
            this.modalOpenPlanChange({ value: this.customerLedgerDetailData.plangroupid });
        } else if (this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan") {
            this.planGroupFlag = false;
        }
    }

    getChangePlanForChild($event, childCustForm) {
        var childCust = this.childCustomerDataListForChangePlan.filter(
            item => item.id == childCustForm.value.custId
        );
        if (childCustForm.value.changePlanCategory == "groupPlan") {
            this.confirmationService.confirm({
                message: "Do you want Change Plan to continue?",
                header: "Change Plan Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    // this.subisuChange = true;
                    this.planGroupFlag = true;
                    // this.customerLedgerDetailData.plangroupid = 0;
                    this.getplanChangeforplanGroup(childCustForm.value.custId);
                },
                reject: () => {
                    // this.subisuChange = false;
                    this.planGroupFlag = false;
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                    // $("#selectPlanChange").modal("show");
                }
            });
        } else if (
            childCustForm.value.changePlanCategory !== "groupPlan" &&
            childCust.planGroupId !== null
        ) {
            // this.planGroupFlag = false;
            this.getplanChangeforplanGroup(childCustForm.value.custId);
            this.modalOpenPlanChange({ value: childCust.plangroupid });
        } else if (childCustForm.value.changePlanCategory !== "groupPlan") {
            this.planGroupFlag = false;
        }
    }

    staffDataList: any = [];
    requestedByList: any = [];
    serviceAreaId: any;
    data: any = [];
    staffData: any = [];

    parentStaffListdataitemsPerPageForStaff = RadiusConstants.ITEMS_PER_PAGE;
    parentstaffListdatatotalRecords: any;
    currentPageParentStaffListdata = 1;
    getStaffDetailById() {
        let currentPageForStaff;
        currentPageForStaff = this.currentPageParentStaffListdata;
        const data = {
            page: currentPageForStaff,
            pageSize: this.parentStaffListdataitemsPerPageForStaff
        };
        const url = "/staffuser/list?product=BSS?product=BSS";
        this.customerService.postMethod(url, data).subscribe((response: any) => {
            this.staffData = response.staffUserlist;
            this.parentstaffListdatatotalRecords = response.pageDetails.totalRecords;
            this.staffDataList.forEach((element, i) => {
                element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
                this.data.push(element.id);
            });
        });
    }

    onPaymentTypeChange(data) { }

    resetPayMode() {
        this.paymentFormGroup.controls.chequeno.disable();
        this.paymentFormGroup.controls.chequedate.disable();
        this.paymentFormGroup.controls.bankManagement.disable();
        this.paymentFormGroup.controls.branch.disable();
        this.paymentFormGroup.controls.destinationBank.disable();
        this.paymentFormGroup.controls.reciptNo.enable();
        this.chequeDateName = "Cheque Date";
        this.paymentFormGroup.controls.referenceno.setValidators([]);
        this.paymentFormGroup.controls.chequedate.setValidators([]);
        this.paymentFormGroup.controls.destinationBank.setValidators([]);
        this.paymentFormGroup.controls.bankManagement.setValidators([]);
        this.paymentFormGroup.controls.chequeno.setValidators([]);
        this.paymentFormGroup.controls.onlinesource.setValidators([]);
        this.paymentFormGroup.updateValueAndValidity();
    }

    selPayModeRecord(event) {
        this.resetPayMode();
        const payMode = event.value.toLowerCase();
        if (payMode == "POS".toLowerCase() || payMode == "VatReceiveable".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.chequeDateName = "Transaction date";
        } else if (payMode == "Online".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.referenceno.setValidators([Validators.required]);
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.chequeDateName = "Transaction date";
        } else if (payMode == "Direct Deposit".toLowerCase()) {
            this.paymentFormGroup.controls.branch.enable();
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.disable();
            this.chequeDateName = "Transaction date";
        } else if (payMode == "NEFT_RTGS".toLowerCase()) {
            this.paymentFormGroup.controls.bankManagement.enable();
            this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
            this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
        } else if (payMode == "Cheque".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.bankManagement.enable();
            this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
            this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
            this.paymentFormGroup.controls.chequeno.enable();
            this.paymentFormGroup.controls.chequeno.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequeno.updateValueAndValidity();
            this.paymentFormGroup.controls.branch.enable();
        }
        this.commondropdownService.getOnlineSourceData(payMode.toLowerCase());
        if (this.commondropdownService.onlineSourceData.length > 0) {
            this.paymentFormGroup.controls.onlinesource.setValidators([Validators.required]);
            this.paymentFormGroup.controls.onlinesource.updateValueAndValidity();
        }
        let isAbbsTdsMode = this.checkPaymentMode(payMode);
        if (isAbbsTdsMode) {
            this.paymentFormGroup.patchValue({
                tdsAmount: 0,
                abbsAmount: 0
            });
            if (this.checkedList.length > 0) {
                this.checkedList.map(element => {
                    element.tds = 0;
                    element.abbs = 0;
                });
            }
        }
    }

    selPaySourceRecord(event) {
        const paySource = event.value.toLowerCase();
        switch (paySource) {
            case "Cash_via_Bank".toLowerCase():
                this.paymentFormGroup.controls.destinationBank.enable();
                this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
                this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
                this.paymentFormGroup.controls.branch.enable();
                break;
        }
    }

    changePlan(): void {
        if (this.changePlanForm.controls.isPaymentReceived.value == "true") {
            this.getCustomer();
            $("#recordPayment").modal("show");
            return;
        }
        const newPlan = [];
        if (
            this.childCustomerDataListForChangePlan != null &&
            this.childCustomerDataListForChangePlan.length > 0
        ) {
            this.changePlanClicked.next("Change Plan Clicked");
            // if (this.UpdateParentCustPlans) this.changePlansubmitted = true;
            // } else {
        }
        this.changePlansubmitted = true;

        if (this.subisuChange) {
            this.changePlanForm.patchValue({
                planMappingList: this.plansArray.value
            });
            this.plansArray.value.forEach((element, i) => {
                newPlan.push(element.planId);
            });
        } else {
            this.changePlanForm.patchValue({
                newPlanList: this.selectPlanListIDs,
                planMappingList: null
            });
        }
        // this.changePlanForm.value.remarks = this.changePlanForm.value.remarks
        //   ? this.changePlanForm.value.remarks
        //   : this.changePlanRemark;
        // this.changePlanForm.value.planId = this.changePlanForm.value.planId
        //   ? this.changePlanForm.value.planId
        //   : this.planSelected;

        this.changePlanForm.patchValue({
            planGroupId: this.changePlanForm.value.planGroupId
                ? this.changePlanForm.value.planGroupId
                : this.planGroupSelected,
            planId: this.changePlanForm.value.planId
                ? this.changePlanForm.value.planId
                : this.planSelected,
            remarks: this.changePlanForm.value.remarks
                ? this.changePlanForm.value.remarks
                : this.changePlanRemark
        });

        // if (this.changePlanForm.valid) {
        if (
            this.childChangePlanType !== "Changeplan" &&
            this.changePlanForm.value.purchaseType !== "Changeplan"
        ) {
            // if (
            //   this.changePlanForm.value.paymentOwnerId === null ||
            //   this.changePlanForm.value.paymentOwnerId === ""
            // ) {
            //   this.paymentOwnerError = true;
            // } else {
            if (this.UpdateParentCustPlans && this.changePlanForm.valid) {
                this.changePlanData = this.changePlanForm.value;
                this.changePlanData.isAdvRenewal = false;
                this.changePlanData.custId = this.customerLedgerDetailData.id;

                if (!this.changePlanData.recordPaymentDTO) {
                    this.changePlanData.recordPaymentDTO = {};
                } else {
                    this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
                    this.changePlanData.recordPaymentDTO.custId = this.customerLedgerDetailData.id;
                }
                this.changePlanData.isRefund = false;

                this.changePlanData.discount = this.planDiscount;
                // if (!this.custServiceData) {
                //   if (this.changePlanBindigNewPlan.length == 0) {
                //     this.changePlanData.planBindWithOldPlans = null;
                //   } else {
                //     this.changePlanData.planBindWithOldPlans = this.changePlanBindigNewPlan;
                //   }
                // } else {
                let updatedData = [];
                this.custServiceData.forEach(e => {
                    if (e.newplan) {
                        let data = {
                            newPlanId: e.newplan,
                            custServiceMappingId: e.custPlanMapppingId,
                            discount: e.discount
                        };
                        updatedData.push(data);
                    }
                });
                this.changePlanData.planBindWithOldPlans = updatedData;
                // }
                // this.changePlanData.planList = null;

                if (this.selectPlanListIDs.length !== 0 && !this.subisuChange) {
                    this.changePlanData.newPlanList = this.selectPlanListIDs;
                } else if (this.subisuChange) {
                    this.changePlanData.newPlanList = newPlan;
                    this.changePlanData.planMappingList = this.plansArray.value;
                } else {
                    this.changePlanData.newPlanList = null;
                    this.changePlanData.planMappingList = null;
                }
                if (this.changePlanForm.value.purchaseType == "Addon") {
                    this.changePlanData.addonStartDate = this.currentData;
                }

                this.changePlanData.custServiceMappingId = this.custPlanMapppingId
                    ? this.custPlanMapppingId
                    : this.childCustomerDataListForChangePlan[0].planMappingList[0].custServiceMappingId;
                this.changePlanData.billableCustomerId = this.changePlanForm.value.billableCustomerId;
                this.changePlanData.isParent = true;
            } else {
                this.changePlansubmitted = false;
            }
            // }
            this.changePlanApi();
        } else if (
            this.childChangePlanType === "Changeplan" ||
            this.changePlanForm.value.purchaseType == "Changeplan"
        ) {
            this.newchangePlanfunctionality(this.chargenewPlanForm.value.plancharge);
            // if (this.UpdateParentCustPlans) {
            //   this.changePlanClicked.next("Change Plan Clicked");
            // }
        }
        // }
    }
    changePlanApi() {
        let changePlanRequestDTOList = [];
        if (
            this.UpdateParentCustPlans &&
            this.changePlanForm.valid &&
            this.UpdateCustPlansData &&
            this.changePlanChildValidData
        ) {
            changePlanRequestDTOList.push(this.changePlanData);
            changePlanRequestDTOList.push(...this.childPlanData);
        } else if (
            this.UpdateParentCustPlans &&
            this.changePlanForm.valid &&
            !this.UpdateCustPlansData
        ) {
            changePlanRequestDTOList.push(this.changePlanData);
        } else if (
            this.UpdateCustPlansData &&
            this.changePlanChildValidData &&
            !this.UpdateParentCustPlans
        ) {
            changePlanRequestDTOList.push(...this.childPlanData);
        }
        if (changePlanRequestDTOList.length > 0) {
            const url = "/subscriber/changePlan01";
            this.customerManagementService.postMethod(url, { changePlanRequestDTOList }).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');


                        if (this.UpdateParentCustPlans) {
                            this.changePlanClicked.next("Change Plan Clicked");
                        }
                        $("#selectPlanChangeService").modal("hide");
                        $("#selectPlanGroupChangeService").modal("hide");
                        $("#addRemark").modal("hide");
                        this.resetFormEvent.next(true);
                        this.changePlansubmitted = false;
                        this.planDiscount = 0;
                        this.finalOfferPrice = 0;
                        this.selPlanData = [];
                        this.changePlanBindigNewPlan = [];
                        this.groupOfferPrices = {};
                        this.offerPrice = 0;
                        this.changePlanForm.reset();
                        this.changenewPlanForm.controls.ChangePlanCategory.reset();
                        this.selectPlanListIDs = [];
                        this.changePlanDate = [];
                        if (this.addChargeForm.value.chargeAdd == true) {
                            this.createNewChargeData(this.customerLedgerDetailData.id);
                        }
                        this.changePlanForm.get("isPaymentReceived").setValue("false");
                        this.openCustomersChangePlan(this.customerLedgerDetailData);
                        this.addPayment("");
                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        this.changePlanForm.get("isPaymentReceived").setValue("false");
                    }
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    this.changePlanForm.get("isPaymentReceived").setValue("false");
                }
            );
        }
    }

    changePlanChildData(event) {
        this.childPlanData = [...this.childPlanData, ...event.changePlanRequestDTOList];
    }

    childChangePlanTypeData(event) {
        this.childChangePlanType = event;
    }

    getSelectCustomerPlanType(e, plant) {
        // this.changePlanStartEndDate()
        this.selectPlan0Rplangroup = plant;
        // this.changePlanStartEndDate()
        if (this.selectPlan0Rplangroup == "PlanGroup") {
            this.getserviceData(e.value);
            this.changePlanForm.get("planGroupId").disable();
            this.changePlanForm.get("planList").disable();
            this.changePlanForm.get("planId").disable();
        } else {
            this.changePlanForm.get("planGroupId").disable();
            this.changePlanForm.get("planList").disable();
            this.changePlanForm.get("planId").disable();
            let data = {
                value: e.value,
                index: ""
            };
            this.getPlanDetailById(data);
        }
    }

    newSubisuPrice: any;

    subisuPrice(e) {

        this.newSubisuPrice = e.target.value;
    }

    newchangePlanfunctionality(newPlanID) {
        let newplanGroup = "";
        let newplan = "";
        let planGroup = "";
        let plan = "";
        let planGroupdiscount = 0;
        let planDiscount = "";

        if (this.selectPlan0Rplangroup == "PlanGroup") {
            newplanGroup = newPlanID ? newPlanID : this.planGroupSelected;
            planGroup = this.customerLedgerDetailData.plangroupid;
            planGroupdiscount = this.customerLedgerDetailData.discount;
        } else {
            newplan = newPlanID ? newPlanID : this.planSelected;

            if (this.customerLedgerDetailData.planMappingList.length > 0) {
                let length = this.customerLedgerDetailData.planMappingList.length;
                for (let lastListNum = length - 1; lastListNum > -1; lastListNum--) {
                    if (
                        this.customerLedgerDetailData.planMappingList[lastListNum].plangroup !=
                        "Bandwidthbooster" &&
                        this.customerLedgerDetailData.planMappingList[lastListNum].plangroup != "Volume Booster"
                    ) {
                        plan = this.customerLedgerDetailData.planMappingList[lastListNum].planId;
                        planDiscount = String(this.planDiscount); //this.customerLedgerDetailData.planMappingList[lastListNum].discount;
                        break;
                    }
                }
            }
        }

        let newChangePlan = [];
        let planList: any;
        let staffIdData: any;
        staffIdData = this.staffDataList.id;
        if (this.changePlanForm.valid) {
            if (this.customerLedgerDetailData.plangroupid || this.planGroupFlag) {
                let updatedData = [];
                if (this.filterPlanGroupListCust.length > 0) {
                    newplan = newplan ? newplan : this.filterPlanGroupListCust[0].planMappingList[0].plan.id;
                }
                let secondryData = [
                    {
                        newPlanGroupId: newplanGroup,
                        newPlanId: newplan,
                        planGroupId: planGroup,
                        planId: plan,
                        custServiceMappingId: this.custPlanMapppingId,
                        discount: planGroupdiscount
                    }
                ];
                this.custServiceData.forEach(e => {
                    let data;
                    if (e.newplan) {
                        if (this.subisuChange) {
                            data = {
                                billToOrg: true,
                                newPlanGroupId:
                                    this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
                                        this.customerLedgerDetailData.plangroupid !== null
                                        ? ""
                                        : this.planGroupSelected,
                                planGroupId: this.planGroupSelected,
                                newPlanId: e.newplan,
                                newAmount: this.newSubisuPrice,
                                custServiceMappingId: e.custPlanMapppingId,
                                discount: e.discount
                            };
                        } else {
                            data = {
                                billToOrg: false,
                                newPlanGroupId:
                                    this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
                                        this.customerLedgerDetailData.plangroupid !== null
                                        ? ""
                                        : this.planGroupSelected,
                                planGroupId: this.planGroupSelected,
                                newPlanId: e.newplan,
                                custServiceMappingId: e.custPlanMapppingId,
                                discount: e.discount
                            };
                        }
                        updatedData.push(data);
                    }
                });
                setTimeout(() => {
                    const deactivatePlanReqModels = updatedData.length > 0 ? updatedData : secondryData;
                    if (
                        this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
                        this.customerLedgerDetailData.plangroupid !== null
                    ) {
                        deactivatePlanReqModels.forEach(models => {
                            planList = {
                                custId: this.customerLedgerDetailData.id,
                                deactivatePlanReqModels: [models],
                                planGroupChange: false,
                                planGroupFullyChanged: false,
                                billableCustomerId: this.changePlanForm.value.billableCustomerId,
                                isParent: true
                            };
                            newChangePlan.push(planList);
                        });
                    } else {
                        planList = {
                            custId: this.customerLedgerDetailData.id,
                            deactivatePlanReqModels: updatedData.length > 0 ? updatedData : secondryData,
                            planGroupChange: true,
                            planGroupFullyChanged: true,
                            billableCustomerId: this.changePlanForm.value.billableCustomerId,
                            isParent: true
                        };
                        newChangePlan.push(planList);
                    }
                }, 300);
                // newChangePlan = {
                //   custId: this.customerLedgerDetailData.id,
                //   deactivatePlanReqModels: [
                //     {
                //       newPlanGroupId: newplanGroup,
                //       newPlanId: newplan,
                //       planGroupId: planGroup,
                //       planId: plan,
                //       custServiceMappingId: this.custPlanMapppingId,
                //       discount: planGroupdiscount,
                //     },
                //   ],
                //   planGroupChange: true,
                //   planGroupFullyChanged: true,
                //   paymentOwner: this.changePlanForm.value.paymentOwner,
                // };
            } else {
                let staff = this.staffDataList.find(
                    staff => staff.id === this.changePlanForm.value.paymentOwnerId
                );
                if (this.subisuChange) {
                    planList = {
                        custId: this.customerLedgerDetailData.id,
                        deactivatePlanReqModels: [
                            {
                                billToOrg: true,
                                newPlanGroupId: newplanGroup,
                                newPlanId: newplan,
                                planGroupId: planGroup,
                                planId: plan,
                                newAmount: this.newSubisuPrice,
                                custServiceMappingId: this.custPlanMapppingId,
                                discount: planDiscount
                            }
                        ],
                        planGroupChange: false,
                        planGroupFullyChanged: false,
                        paymentOwner:
                            this.changePlanForm.value.paymentOwnerId != null &&
                                this.changePlanForm.value.paymentOwnerId &&
                                staff != null
                                ? staff.fullName
                                : "",
                        paymentOwnerId: this.changePlanForm.value.paymentOwnerId,
                        billableCustomerId: this.changePlanForm.value.billableCustomerId,
                        isParent: true
                    };
                } else {
                    planList = {
                        custId: this.customerLedgerDetailData.id,
                        deactivatePlanReqModels: [
                            {
                                newPlanGroupId: newplanGroup,
                                newPlanId: newplan,
                                planGroupId: planGroup,
                                planId: plan,
                                custServiceMappingId: this.custPlanMapppingId,
                                discount: planDiscount
                            }
                        ],
                        planGroupChange: false,
                        planGroupFullyChanged: false,
                        paymentOwner:
                            this.changePlanForm.value.paymentOwnerId != null &&
                                this.changePlanForm.value.paymentOwnerId != "" &&
                                staff != null
                                ? staff.fullName
                                : "",
                        paymentOwnerId: this.changePlanForm.value.paymentOwnerId,
                        billableCustomerId: this.changePlanForm.value.billableCustomerId,
                        isParent: true
                    };
                }
                newChangePlan.push(planList);
            }
        }

        setTimeout(() => {
            let deactivatePlanReqDTOS = [];
            if (
                this.UpdateParentCustPlans &&
                this.changePlanForm.valid &&
                this.UpdateCustPlansData &&
                this.changePlanChildValidData
            ) {
                deactivatePlanReqDTOS = [...deactivatePlanReqDTOS, ...newChangePlan];
                deactivatePlanReqDTOS = [...deactivatePlanReqDTOS, ...this.childPlanData];
            } else if (
                this.UpdateParentCustPlans &&
                this.changePlanForm.valid &&
                !this.UpdateCustPlansData
            ) {
                deactivatePlanReqDTOS = [...deactivatePlanReqDTOS, ...newChangePlan];
            } else if (
                this.UpdateCustPlansData &&
                this.changePlanChildValidData &&
                !this.UpdateParentCustPlans
            ) {
                deactivatePlanReqDTOS = [...deactivatePlanReqDTOS, ...this.childPlanData];
            }
            if (deactivatePlanReqDTOS.length > 0) {
                const url = "/subscriber/deactivatePlanInBulk";
                //  console.log("this.changePlanData", this.changePlanData);
                //
                // newChangePlan.forEach(newCP => {
                this.customerManagementService.postMethod(url, { deactivatePlanReqDTOS }).subscribe(
                    (response: any) => {
                        // if (newChangePlan.indexOf(newCP) === newChangePlan.length - 1) {
                        this.chargenewPlanForm.reset();
                        this.changePlanForm.reset();
                        this.changenewPlanForm.controls.ChangePlanCategory.reset();
                        this.changePlansubmitted = false;
                        this.groupOfferPrices = {};
                        this.offerPrice = 0;
                        this.finalOfferPrice = 0;
                        this.getCustomersDetail(this.customerLedgerDetailData.id);
                        this.getCustomerNetworkLocationDetail(this.customerLedgerDetailData.id);
                        this.selectPlan0Rplangroup = "";
                        $("#selectPlanChangeService").modal("hide");
                        $("#addRemark").modal("hide");
                        $("#selectPlanGroupChangeService").modal("hide");
                        this.resetFormEvent.next(true);

                        // }
                        this.toastr.success(`${response.responseMessage}`, 'Success!');


                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            }
            // });
        }, 500);
    }

    changePlanChildValid(event) {
        this.changePlanChildValidData = event;
    }
    openDetailCust(event) {
        this.customerDetailOpen(event);
    }
    updateCustPlans(event) {
        this.UpdateCustPlansData = event;
    }

    changePlanStartEndDate() {
        const newPlan = [];

        if (this.subisuChange) {
            this.changePlanForm.patchValue({
                planMappingList: this.plansArray.value
            });
            this.plansArray.value.forEach((element, i) => {
                newPlan.push(element.planId);
            });
        } else {
            this.changePlanForm.patchValue({
                newPlanList: this.selectPlanListIDs,
                planMappingList: null
            });
        }

        this.changePlanData = this.changePlanForm.value;
        this.changePlanData.isAdvRenewal = false;
        this.changePlanData.custId = this.customerLedgerDetailData.id;
        if (!this.changePlanData.recordPaymentDTO) {
            this.changePlanData.recordPaymentDTO = {};
        } else {
            this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
            this.changePlanData.recordPaymentDTO.custId = this.customerLedgerDetailData.id;
        }
        this.changePlanData.isRefund = false;

        this.changePlanData.discount = this.planDiscount;
        if (this.changePlanBindigNewPlan.length == 0) {
            this.changePlanData.planBindWithOldPlans = null;
        } else {
            this.changePlanData.planBindWithOldPlans = this.changePlanBindigNewPlan;
        }
        // this.changePlanData.planList = null;

        if (this.selectPlanListIDs.length !== 0 && !this.subisuChange) {
            this.changePlanData.newPlanList = this.selectPlanListIDs;
        } else if (this.selectPlanListIDs.length !== 0 && this.subisuChange) {
            this.changePlanData.newPlanList = newPlan;
            this.changePlanData.planMappingList = this.plansArray.value;
        } else {
            this.changePlanData.newPlanList = null;
            this.changePlanData.planMappingList = null;
        }

        if (this.chargenewPlanForm.value.plancharge) {
            this.changePlanData.planId = this.chargenewPlanForm.value.plancharge;
        }
        if (this.changePlanForm.value.purchaseType == "Addon") {
            this.changePlanData.addonStartDate = this.currentData;
        }
        this.changePlanData.isParent = true;
        this.changePlanData.custServiceMappingId = this.custPlanMapppingId;

        // this.changePlanData.newPlanList= this.selectPlanListIDs
        let changePlanRequestDTOList = [];
        if (this.UpdateParentCustPlans) {
            changePlanRequestDTOList.push(this.changePlanData);
        }

        if (this.childPlanData.length > 0) {
            changePlanRequestDTOList.push(...this.childPlanData);
        }

        const url = "/subscriber/getStartAndEndDate";
        //  console.log("this.changePlanData", this.changePlanData);
        this.customerManagementService.postMethod(url, { changePlanRequestDTOList }).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.changePlanDate = response.data;
                } else {
                    this.changePlanForm.get("isPaymentReceived").setValue("false");
                }
            },
            (error: any) => {
                this.changePlanForm.get("isPaymentReceived").setValue("false");
            }
        );
    }

    getPincodeList(serviceAreaId, pincodeId) {
        this.pincodeDD = [];
        if (serviceAreaId) {
            const url = "/serviceArea/" + serviceAreaId;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.serviceAreaData = response.data;
                    this.serviceAreaData.pincodes.forEach(element => {
                        this.commondropdownService.allpincodeNumber.forEach(e => {
                            if (e.pincodeid == element) {
                                this.pincodeDD.push(e);
                            }
                        });
                    });
                    this.presentGroupForm.controls.pincodeId.setValue(pincodeId);
                },
                (error: any) => { }
            );
        }
    }

    selServiceArea(event) {
        this.pincodeDD = [];
        const serviceAreaId = event.value;
        if (serviceAreaId) {
            const url = "/serviceArea/" + serviceAreaId;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.serviceareaCheck = false;
                    this.serviceAreaData = response.data;
                    this.serviceAreaData.pincodes.forEach(element => {
                        this.commondropdownService.allpincodeNumber.forEach(e => {
                            if (e.pincodeid == element) {
                                this.pincodeDD.push(e);
                            }
                        });
                        // this.pincodeDD.push(this.commondropdownService.allpincodeNumber.filter((e)=>e.pincodeid==element))
                    });

                    this.getPlanbyServiceArea(serviceAreaId);
                    if (!this.iscustomerEdit) {
                        this.presentGroupForm.reset();
                    }
                    // this.getAreaData(this.serviceAreaData.areaid, "present");
                },
                (error: any) => { }
            );
            this.getPartnerAllByServiceArea(serviceAreaId);
            this.getServiceByServiceAreaID(serviceAreaId);
            this.getBranchByServiceAreaID(serviceAreaId);
            this.getStaffUserByServiceArea(serviceAreaId);
            this.branchByServiceAreaID(serviceAreaId);
            // this.getStaffDetailById(serviceAreaId);
            this.shiftLocationDTO.shiftPartnerid = "";
        }
    }

    getBranchByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/branchManagement/getAllBranchesByServiceAreaId";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            this.branchData = response.dataList;
            if (this.branchData != null && this.branchData.length > 0) {
                this.isBranchAvailable = true;
                this.customerGroupForm.controls.branch.setValidators(Validators.required);
                this.customerGroupForm.controls.partnerid.clearValidators();
                this.customerGroupForm.updateValueAndValidity();
            } else {
                this.isBranchAvailable = false;
                this.isServiceInShiftLocation = false;
                this.customerGroupForm.controls.partnerid.setValidators(Validators.required);
                this.customerGroupForm.controls.branch.clearValidators();
                this.customerGroupForm.controls.branch.updateValueAndValidity();
                this.customerGroupForm.updateValueAndValidity();
            }
        });
    }

    getServiceByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/serviceArea/getAllServicesByServiceAreaId";
        this.customerManagementService.postMethod(url, data).subscribe((response: any) => {
            this.serviceData = response.dataList;
        });
    }

    getStaffUserByServiceArea(ids) {
        let data = [];
        data.push(ids);
        let url = "/staffsByServiceAreaId/" + ids;
        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            //
            this.staffList = response.dataList;
        });
    }

    getPlanbyServiceArea(serviceAreaId) {
        if (serviceAreaId) {
            this.filterPlanData = [];
            const custType = this.custType;
            const url = "/plans/serviceArea?planmode=ALL&serviceAreaId=" + serviceAreaId;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planByServiceArea = response.postpaidplanList;
                    this.filterPlanData = this.planByServiceArea.filter(plan => plan.plantype == custType);
                    //  console.log("this.filterPlanData", this.filterPlanData);
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    // ........location Data..............
    mylocation() {
        //
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
                    // console.log(
                    //   'Latitude: ' +
                    //     position.coords.latitude +
                    //     'Longitude: ' +
                    //     position.coords.longitude,
                    // )

                    this.iflocationFill = true;
                    this.customerGroupForm.patchValue({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                }
            });
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Geolocation is not supported by this browser!');
            }

        }
    }

    openSearchModel() {
        this.ifsearchLocationModal = true;
        this.currentPagesearchLocationList = 1;
    }

    searchLocation() {
        if (this.searchLocationForm.valid) {
            const url = "/getPlaceId?query=" + this.searchLocationForm.value.searchLocationname.trim();
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.searchLocationData = response.locations;
                },
                (error: any) => {
                    if (error.error.code == 422) {

                        this.toastr.error(`${error.error.error}`, 'Failed!');


                    } else {

                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                }
            );
        }
    }

    clearLocationForm() {
        this.searchLocationForm.reset();
        this.searchLocationData = [];
    }

    pageChangedSearchLocationList(currentPage) {
        this.currentPagesearchLocationList = currentPage;
    }

    filedLocation(placeId) {
        const url = "/getLatitudeAndLongitude?placeId=" + placeId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.ifsearchLocationModal = false;
                this.customerGroupForm.patchValue({
                    latitude: response.location.latitude,
                    longitude: response.location.longitude
                });

                this.iflocationFill = true;
                this.closebutton.nativeElement.click();
                this.searchLocationData = [];
                this.searchLocationForm.reset();
            },
            (error: any) => {
                // console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    clearsearchLocationData() {
        this.searchLocationData = [];
        this.ifsearchLocationModal = false;
        this.searchLocationForm.reset();
    }

    nearMyLocation(custID) {
        this.ifNearLocationModal = true;
        const url = "/customers/" + custID;

        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.viewcustomerListData = response.customers;
            this.customerIdINLocationDevice = this.viewcustomerListData.id;
            this.nearLocation(this.viewcustomerListData);
        });
    }

    nearLocation(data) {
        const deviceData = {
            latitude: data.latitude,
            longitude: data.longitude
        };
        const url = "/getNearbyDevices";
        this.customerManagementService.postMethod(url, deviceData).subscribe(
            (response: any) => {
                this.nearDeviceLocationData = response.locations;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedNearDeviceList(pageNumber) {
        this.currentPagenearDeviceLocationList = pageNumber;
    }

    nearsearchClose() {
        this.ifNearLocationModal = false;
        this.nearDeviceLocationData = [];
    }

    bindNetworkDevice(networkdeviceID) {
        const deviceData = {};

        const url =
            "/customers/bindNetworkDevice?customerId=" +
            this.customerIdINLocationDevice +
            "&networkDeviceId=" +
            networkdeviceID;

        this.customerManagementService.updateMethod(url, deviceData).subscribe(
            (response: any) => {
                this.NetworkDeviceData = response.locations;

                this.getcustomerList("");
                this.closebutton.nativeElement.click();
                this.nearsearchClose();
                this.toastr.success(`${response.customer}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getPlanValidity(event) {
        const planId = event.value;
        this.checkIfDiscount(planId);
        const url = "/postpaidplan/" + planId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                const planDetailData = response.postPaidPlan;
                if (response.postPaidPlan.allowdiscount == true) {
                    this.planGroupForm.patchValue({ discount: null });
                    this.ifcustomerDiscountField = true;
                } else {
                    this.planGroupForm.patchValue({ discount: null });
                    this.ifcustomerDiscountField = false;
                }
                // console.log("this.planDetailData", planDetailData);
                this.planGroupForm.patchValue({
                    validity: Number(planDetailData.validity),
                    offerprice: Number(planDetailData.offerprice)
                });
                this.validityUnitFormGroup.patchValue({
                    validityUnit: planDetailData.unitsOfValidity
                });
                if (planDetailData.category == "Business Promotion") {
                    this.ifplanisSubisuSelect = true;
                    // this.payMappingListFromArray.controls = [];
                    this.customerGroupForm.patchValue({
                        billTo: "ORGANIZATION",
                        isInvoiceToOrg: planDetailData.invoiceToOrg
                    });
                    this.planGroupForm.patchValue({
                        newAmount: Number(planDetailData.newOfferPrice)
                    });
                } else if (
                    this.customerGroupForm.value.billTo == "ORGANIZATION" &&
                    planDetailData.category == "Normal" &&
                    this.ifplanisSubisuSelect == false
                ) {
                    // this.payMappingListFromArray.controls = [];
                    this.ifplanisSubisuSelect = false;
                    this.customerGroupForm.patchValue({
                        billTo: "ORGANIZATION"
                    });
                    this.planGroupForm.patchValue({
                        newAmount: Number(planDetailData.offerprice)
                    });
                } else {
                    this.ifplanisSubisuSelect = false;
                    // this.payMappingListFromArray.controls = [];
                    this.customerGroupForm.patchValue({
                        billTo: "CUSTOMER"
                    });
                }
                this.discountValue = planDetailData.offerprice;

                this.planGroupForm.controls.validity.disable();
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    billtoSelectValue(e) {
        this.payMappingListFromArray.controls = [];
        this.planGroupForm.reset();
        this.customerGroupForm.patchValue({
            plangroupid: ""
        });
    }

    // Paytm Link
    getPaytmLink(custId) {
        this.customerManagementService.getPaytmLink(custId).subscribe(
            (response: any) => {

                this.toastr.success(`${response.msg}`, 'Success!');
            },

            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    openMyInventory(id): void {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.customerrMyInventoryView = true;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = false;
        this.customerTicketView = false;
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.assignInventoryCustomerId = id;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    onKeyAdhar(event) {
        let adharnum = this.customerGroupForm.value.aadhar.replace(/\s/g, "");

        let v = adharnum.match(/(\d{1,4})?(\d{1,4})?(\d{1,4})?/);
        if (v) {
            v = v[1] ? v[1] + (v[2] ? " " + v[2] + (v[3] ? " " + v[3] : "") : "") : "";
            adharnum = v;
        }

        // if(this.customerGroupForm.value.aadhar.length == 14){
        //   let prefix = adharnum.substr(0, adharnum.length - 6);
        //   let suffix = adharnum.substr(-6);
        //   let masked = prefix.replace(/[A-Z\d]/g, '*');
        //   let a = masked + suffix;
        //   this.customerGroupForm.patchValue({
        //     aadhar: a,
        //   })
        // } else{
        this.customerGroupForm.patchValue({
            aadhar: adharnum
        });
        // }
    }

    onKeyPan(e) {
        let panNum = this.customerGroupForm.value.pan.replace(/\s/g, "");
        let v = panNum.match(/([A-Z]{1,5})?([0-9]{1,4})?([A-Z]{1,1})?/);
        if (v) {
            v = v[1] ? v[1] + (v[2] ? " " + v[2] + (v[3] ? v[3] : "") : "") : "";
            panNum = v;
        }

        // if(this.customerGroupForm.value.pan.length == 11){
        //     let prefix = panNum.substr(0, panNum.length - 4);
        //     let suffix = panNum.substr(-4);
        //     let masked = prefix.replace(/[A-Z\d]/g, '*');
        //     let a = masked + suffix;
        //     this.customerGroupForm.patchValue({
        //       pan: a,
        //     })
        // }
        // else{
        this.customerGroupForm.patchValue({
            pan: panNum
        });
        // }
    }

    onKeyGST(e) {
        let gstNum = this.customerGroupForm.value.gst.replace(/\s/g, "");
        let v = gstNum.match(
            /(\d{1,2})?([A-Z]{1,3})?([A-Z]{1,2})?(\d{1,3})?(\d{1,1})?([A-Z]{1,1})?([A-Z\d]{1,1})?([Z]{1,1})?([A-Z\d]{1,1})?/
        );
        if (v) {
            v = v[1]
                ? v[1] +
                (v[2]
                    ? v[2] +
                    (v[3]
                        ? " " +
                        v[3] +
                        (v[4]
                            ? v[4] +
                            (v[5]
                                ? " " +
                                v[5] +
                                (v[6]
                                    ? v[6] + (v[7] ? v[7] + (v[8] ? v[8] + (v[9] ? v[9] : "") : "") : "")
                                    : "")
                                : "")
                            : "")
                        : "")
                    : "")
                : "";
            gstNum = v;
        }

        // if(this.customerGroupForm.value.gst.length == 17){
        //   let prefix = gstNum.substr(0, gstNum.length - 6);
        //   let suffix = gstNum.substr(-6);
        //   let masked = prefix.replace(/[A-Z\d]/g, '*');
        //   let a = masked + suffix;
        //   this.customerGroupForm.patchValue({
        //     gst: a,
        //   })
        // }
        // else
        // {
        this.customerGroupForm.patchValue({
            gst: gstNum
        });
        // }
    }

    getsystemconfigListByName(keyName: string) {
        const url = "/system/configurationListByKey?keyName=" + keyName;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.dunningRules = response.dataList;
            },
            (error: any) => { }
        );
    }

    openMyStatusChange(customerId) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        //  this.listSearchView = true;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.iflocationFill = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.isStatusChangeSubMenu = true;
        this.customerId = customerId;

        this.getapproveStatusList("");
        this.AllcustApproveList = [];
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    TotalChangeStatusItemPerPage(event) {
        this.changeStatusShowItemPerPage = Number(event.value);
        if (this.currentPagecustChangeStatusConfig > 1) {
            this.currentPagecustChangeStatusConfig = 1;
        }
        this.getapproveStatusList(this.changeStatusShowItemPerPage);
    }

    pageChangeStatusConfig(currentPage) {
        this.currentPagecustChangeStatusConfig = currentPage;
        this.getapproveStatusList("");
    }

    getapproveStatusList(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.custChangeStatusConfigitemsPerPage = size;
        } else {
            if (this.changeStatusShowItemPerPage == 1) {
                this.custChangeStatusConfigitemsPerPage = this.pageITEM;
            } else {
                this.custChangeStatusConfigitemsPerPage = this.changeStatusShowItemPerPage;
            }
        }
        this.AllcustApproveList = [];
        const url = `/allCustApprove/${this.customerId}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200 || response.status == 200) {
                    const list = response.customer;
                    // this.AllcustApproveList.push(list);
                    for (let i = list.length; i > 0; i--) {
                        this.AllcustApproveList.push(list[i - 1]);
                    }
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    assigneChangeStatus(id, status) {
        this.custIDStatus = id;
        this.currentStatus = status;
    }

    async changeStatus(updatedStatus, remark) {
        const data = {
            id: this.custIDStatus,
            rf: "bss",
            status: updatedStatus,
            remark: remark
        };
        const url =
            "/changeStatus/" + this.custIDStatus + "?remark=" + remark + "&status=" + updatedStatus;
        this.customerManagementService.updateMethod(url, data).subscribe(
            (response: any) => {
                this.toastr.success(`${response.customer}`, 'Success!');

                this.getcustomerList("");
                this.updatedStatus = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
        this.closeChangeStatus();
    }

    ifApproveStatus = false;
    approveRejectRemark = "";
    apprRejectCustID = "";

    approvestatusModalOpen(custId) {
        this.ifApproveStatus = true;
        this.apprRejectCustID = custId;
        this.approveRejectRemark = "";
        $("#ApproveRejectModal").modal("show");
    }

    rejectstatusModalOpen(custId) {
        this.ifApproveStatus = false;
        this.apprRejectCustID = custId;
        this.approveRejectRemark = "";
        $("#ApproveRejectModal").modal("show");
    }

    statusApporevedRejected() {
        this.approveId = this.apprRejectCustID;
        let custstatus = "";
        if (this.ifApproveStatus == true) {
            this.approved = false;
            this.approveInventoryData = [];
            this.selectStaff = null;
            custstatus = "Approved";
        } else {
            this.reject = false;
            this.selectStaffReject = null;
            this.rejectInventoryData = [];
            custstatus = "Rejected";
        }
        const data = {
            id: this.apprRejectCustID,
            status: custstatus,
            remarks: this.approveRejectRemark
        };

        const url =
            "/changeStatusCustomerApprove/" +
            this.apprRejectCustID +
            "?status=" +
            custstatus +
            "&remarks=" +
            this.approveRejectRemark;
        this.customerManagementService.updateMethod(url, data).subscribe(
            (response: any) => {
                $("#ApproveRejectModal").modal("hide");
                if (this.ifApproveStatus == true) {
                    if (response.result.dataList) {
                        this.approved = true;
                        this.approveInventoryData = response.result.dataList;
                        $("#assignCustomerInventoryModal").modal("show");
                    } else {
                        this.toastr.success(`${response.message}`, 'Success!');

                        this.getapproveStatusList("");
                    }
                } else {
                    if (response.result.dataList) {
                        this.reject = true;
                        this.rejectInventoryData = response.result.dataList;
                        $("#rejectCustomerInventoryModal").modal("show");
                    } else {
                        this.getapproveStatusList("");
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    planSelectType(event) {
        this.planDropdownInChageData = [];
        this.DiscountValueStore = [];
        this.discountValue = 0;
        this.planTotalOffetPrice = 0;
        const planaddDetailType = event.value;
        this.DiscountValueStore = [];
        this.ifplanisSubisuSelect = false;
        this.planDataForm.reset();
        this.customerGroupForm.controls.plangroupid.reset();
        this.customerGroupForm.controls.discount.reset();
        this.customerGroupForm.controls.discountType.reset();
        this.customerGroupForm.controls.discountExpiryDate.reset();
        if (planaddDetailType == "individual") {
            this.ifIndividualPlan = true;
            this.ifPlanGroup = false;
            this.payMappingListFromArray.controls = [];
            if (
                this.customerGroupForm.value.parentCustomerId != null &&
                this.customerGroupForm.value.parentCustomerId != ""
            ) {
                this.planGroupForm.controls.invoiceType.enable();
                this.customerGroupForm.controls.invoiceType.disable();
                if (this.customerGroupForm.value.parentExperience == "Single")
                    this.planGroupForm.patchValue({ invoiceType: "Group" });
                else this.planGroupForm.patchValue({ invoiceType: "" });
            }
        } else if (planaddDetailType == "groupPlan") {
            if (this.serviceAreaData) {
                this.filterNormalPlanGroup = [];
                this.commondropdownService.PrepaidPlanGroupDetails.forEach(element => {
                    if (
                        element.planMode == "NORMAL" &&
                        (element.planGroupType === "Registration" ||
                            element.planGroupType === "Registration and Renewal")
                    ) {
                        this.filterNormalPlanGroup.push(element);
                    }
                });
                let data1;
                let data2;
                if (this.filterNormalPlanGroup) {
                    data1 = this.filterNormalPlanGroup.filter(
                        plan => plan.servicearea.id == this.serviceAreaData.id
                    );
                    data2 = this.filterNormalPlanGroup.filter(plan =>
                        plan.servicearea.filter(e => e == this.serviceAreaData.id)
                    );
                }
                this.filterNormalPlanGroup = [...data1, ...data2];
                if (
                    this.customerGroupForm.value.parentCustomerId != null &&
                    this.customerGroupForm.value.parentCustomerId != ""
                ) {
                    this.customerGroupForm.controls.invoiceType.enable();
                    this.planGroupForm.controls.invoiceType.disable();
                    if (this.customerGroupForm.value.parentExperience == "Single")
                        this.customerGroupForm.patchValue({ invoiceType: "Group" });
                    else this.customerGroupForm.patchValue({ invoiceType: "" });
                }
            }

            this.ifIndividualPlan = false;
            this.ifPlanGroup = true;
            this.customerGroupForm.patchValue({
                plangroupid: ""
            });
        } else {
            this.ifIndividualPlan = false;
            this.ifPlanGroup = false;
        }
    }

    pageChangedInvoicePaymentList(pageNumber) {
        this.currentPageinvoicePaymentList = pageNumber;
    }

    invoicePaymentCloseModal() {
        this.ifInvoicePayment = false;
        this.ispaymentChecked = false;
        this.allIsChecked = false;
        this.isSinglepaymentChecked = false;
        this.invoicePaymentData = [];
        this.allchakedPaymentData = [];
    }

    InvoiceReprint(docnumber, custname) {
        const url = "/regeneratepdfsub/" + docnumber;
        this.invoiceMasterService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, custname);
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    invoiceCancelRemarks = null;
    invoiceCancelRemarksType = null;

    invoiceRemarks(invoice, type) {
        this.invoiceID = invoice.id;
        this.invoiceCancelRemarksType = type;
        $("#invoiceCancelRemarks").modal("show");
    }

    addInvoiceRemarks() {
        if (this.invoiceCancelRemarksType === "void") {
            this.voidInvoice();
        } else if (this.invoiceCancelRemarksType === "cancelRegenerate") {
            this.cancelRegenrateInvoice();
        }
    }

    cancelRegenrateInvoice() {
        const data = {};

        const url =
            "/invoiceV2/cancelAndRegenerate/" +
            this.invoiceID +
            "?isCaf=false&invoiceCancelRemarks=" +
            this.invoiceCancelRemarks;
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                // this.closebutton.nativeElement.click();
                this.ifInvoicePayment = false;
                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.isSinglepaymentChecked = false;
                this.invoiceCancelRemarks = null;
                this.invoiceCancelRemarksType = null;
                this.invoicePaymentData = [];
                this.allchakedPaymentData = [];
                this.searchinvoiceMaster("", "");
                $("#invoiceCancelRemarks").modal("hide");

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`${response.message}`, 'Success!');

                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    invicePaymentList(invoice) {
        this.invoiceID = invoice.id;

        this.invoicePaymentData = [];
        if (invoice.adjustedAmount >= invoice.totalamount) {
            (error: any) => {
                this.toastr.info(`${error.error.error}`, 'Total payment is already adjusted!');
            }

        } else {
            $("#invoicePayment").modal("show");
            const url = "/paymentmapping/" + this.invoiceID;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.invoicePaymentData = response.Paymentlist;
                    this.invoicePaymenttotalRecords = this.invoicePaymentData.length;

                    this.invoicePaymentData.forEach((value, index) => {
                        this.invoicePaymentData[index].isSinglepaymentChecked = false;
                        this.totaladjustedAmount =
                            this.totaladjustedAmount + this.invoicePaymentData[index].adjustedAmount;
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    checkInvoicePaymentAll(event) {
        if (event.checked == true) {
            this.allchakedPaymentData = [];
            const checkedData = this.invoicePaymentData;
            for (let i = 0; i < checkedData.length; i++) {
                this.allchakedPaymentData.push({
                    id: this.invoicePaymentData[i].id,
                    amount: this.invoicePaymentData[i].amount
                });
            }
            this.allchakedPaymentData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSinglepaymentChecked = true;
                    }
                });
            });
            this.ispaymentChecked = true;
            // console.log(this.allchakedPaymentData);
        }
        if (event.checked == false) {
            const checkedData = this.invoicePaymentData;
            this.allchakedPaymentData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSinglepaymentChecked = false;
                    }
                });
            });
            this.allchakedPaymentData = [];
            // console.log(this.allchakedPaymentData);
            this.ispaymentChecked = false;
            this.allIsChecked = false;
        }
    }

    addInvoicePaymentChecked(id, event) {
        if (event.checked) {
            this.invoicePaymentData.forEach((value, i) => {
                if (value.id == id) {
                    this.allchakedPaymentData.push({
                        id: value.id,
                        amount: value.amount
                    });
                }
            });

            if (this.invoicePaymentData.length === this.allchakedPaymentData.length) {
                this.ispaymentChecked = true;
                this.allIsChecked = true;
            }
            // console.log(this.allchakedPaymentData);
        } else {
            const checkedData = this.invoicePaymentData;
            checkedData.forEach(element => {
                if (element.id == id) {
                    element.isSinglepaymentChecked = false;
                }
            });
            this.allchakedPaymentData.forEach((value, index) => {
                if (value.id == id) {
                    this.allchakedPaymentData.splice(index, 1);
                    // console.log(this.allchakedPaymentData);
                }
            });

            if (
                this.allchakedPaymentData.length == 0 ||
                this.allchakedPaymentData.length !== this.invoicePaymentData.length
            ) {
                this.ispaymentChecked = false;
            }
        }
    }

    invoicePaymentAdjsment() {
        const data = {
            invoiceId: this.invoiceID,
            creditDocumentList: this.allchakedPaymentData
        };

        const url = "/invoicePaymentAdjust";
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                // this.closebutton.nativeElement.click();
                this.ifInvoicePayment = false;
                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.isSinglepaymentChecked = false;
                this.invoicePaymentData = [];
                this.allchakedPaymentData = [];
                this.searchinvoiceMaster(this.customerLedgerDetailData.id, "");
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getParentCust(event) {
        if (event.value) {
            this.customerGroupForm.controls.invoiceType.enable();
        } else {
            this.customerGroupForm.controls.invoiceType.disable();
        }
    }

    InvoiceListByCustomer(id) {
        const url = "/invoiceList/byCustomer/" + id;
        this.invoiceList = [];
        const Data = [];
        this.masterSelected = false;

        this.recordPaymentService.getAllInvoiceByCustomer(url).subscribe(
            (response: any) => {
                const invoicedata = [];
                if (response.invoiceList != null && response.invoiceList.length != 0) {
                    Data.push(...response.invoiceList);
                } else {
                    Data.push(...this.invoicedropdownValue);
                }
                this.invoiceList = Data;
                this.invoiceList.forEach(item => {
                    item.tdsCheck = 0;
                    item.abbsCheck = 0;
                    item.includeTds = false;
                    item.includeAbbs = false;
                    item.testamount = this.getPendingAmount(item);
                    // if (item.pendingAmt && item.adjustedAmount) {
                    //   item.testamount = item.totalamount - item.adjustedAmount - item.pendingAmt;
                    // } else if (item.pendingAmt) {
                    //   item.testamount = item.totalamount - item.pendingAmt;
                    // } else if (item.adjustedAmount) {
                    //   item.testamount = item.totalamount - item.adjustedAmount;
                    // } else {
                    //   item.testamount = item.totalamount;
                    // }
                });
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openMyChildCustomer(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.customerTicketView = false;
        this.customerChildsView = true;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.getChildCustomers(id);
        this.customerUpdateDiscount = false;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    getChildCustomers(id) {
        const url = `/getAllActualChildCustomer?customerId=${id}`;
        const data = {
            page: this.pageNumberForChildsPage,
            pageSize: this.pageSizeForChildsPage
        };
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                // this.assignedInventoryList = res.dataList;
                this.childCustomerDataList = response.customerList;
                this.childCustomerDataTotalRecords = response.pageDetails.totalRecords;
                this.childCustomerDataList.forEach(element => {
                    element.isSelected = false;
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    pageChangeEventForChildCustomers(pageNumber: number) {
        this.pageNumberForChildsPage = pageNumber;
        this.getChildCustomers(this.customerLedgerDetailData.id);
    }

    itemPerPageChangeEvent(event) {
        this.pageSizeForChildsPage = Number(event.value);
        this.getChildCustomers(this.customerLedgerDetailData.id);
    }

    // The master checkbox will check/ uncheck all items
    checkUncheckAll() {
        for (let i = 0; i < this.childCustomerDataList.length; i++) {
            this.childCustomerDataList[i].isSelected = this.masterSelected;
        }
        this.getCheckedItemList();
    }

    // Check All Checkbox Checked
    isAllSelected() {
        this.masterSelected = this.childCustomerDataList.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemList();
    }

    // Get List of Checked Items
    getCheckedItemList() {
        this.checkedList = [];
        // this.childCustomerDataList = [];
        for (let i = 0; i < this.childCustomerDataList.length; i++) {
            if (this.childCustomerDataList[i].isSelected) {
                this.checkedList.push(this.childCustomerDataList[i].id);
            }
        }
        // this.childCustomerDataList = JSON.stringify(this.checkedList);
    }

    removeFromParent() {
        const url = `/removeParent`;
        // let data = {
        //   page: this.pageNumberForChildsPage,
        //   pageSize: this.pageSizeForChildsPage,
        // };
        this.customerManagementService.postMethod(url, this.checkedList).subscribe(
            (response: any) => {
                // this.assignedInventoryList = res.dataList;
                this.getChildCustomers(this.customerLedgerDetailData.id);
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    makeParent(oldParentId: number) {
        if (this.checkedList.length == 1) {
            const url = `/updateParent?newParentId=${this.checkedList[0]}&oldParentId=${oldParentId}`;
            // let data = {
            //   page: this.pageNumberForChildsPage,
            //   pageSize: this.pageSizeForChildsPage,
            // };
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    // this.assignedInventoryList = res.dataList;
                    // this.getChildCustomers();
                    this.listCustomer();
                    this.toastr.success(`${response.message}`, 'Changed parent successfully!');


                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');


                }
            );
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please select only one customer to make parent!');

            }
        }
    }

    lastRenewalPlanGroup(id) {
        const url = "/subscriber/lastrenewalplangroupid/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.lastRenewalPlanGroupID = response.lastRenewalPlanGroupId;
        });
    }

    lastRenewalChildPlanGroup(id) {
        const url = "/subscriber/lastrenewalplangroupid/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.lastRenewal_CHILDPlanGroupID = response.lastRenewalPlanGroupId;
        });
    }
    childPlanList: any;
    childPlanGroup: any;
    newPlanGroupIdChild: any;
    getChildCustomersForChangePlan(id) {
        let chargeAvailable: Boolean = false;
        const url = `/getAllChildCustomer?customerId=${id}&invoiceType=Group`;
        const data = {
            page: this.pageNumberForChildsPageForChangePlan,
            pageSize: this.pageSizeForChildsPageForChangePlan
        };
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.childCustomerDataListForChangePlan = response.customerList;
                this.childPlanList = this.childCustomerDataListForChangePlan.filter(e => !e.plangroupid);
                this.childPlanGroup = this.childCustomerDataListForChangePlan.filter(e => e.plangroupid);
                this.childCustomerDataListForChangePlan.forEach((element, i) => {
                    this.getplanChangeforplanGroup(element.id);
                    if (element.indiChargeList.length == 0) {
                        chargeAvailable = false;
                    } else {
                        chargeAvailable = true;
                    }
                    const url = "/subscriber/getActivePlanList/" + element.id + "?isNotChangePlan=false";
                    let planList = [];
                    this.customerManagementService.getMethod(url).subscribe((response: any) => {
                        let childActivePlans = [];
                        let childActivePlanGroup = [];
                        response.dataList.forEach(item => {
                            if (
                                item.invoiceType == "Group" &&
                                item.plangroup !== "Volume Booster" &&
                                item.plangroup !== "Bandwidthbooster" &&
                                item.plangroup !== "DTV Addon" &&
                                !item.plangroupid
                            )
                                childActivePlans.push(item);
                            else if (
                                item.invoiceType == "Group" &&
                                item.plangroup !== "Volume Booster" &&
                                item.plangroup !== "Bandwidthbooster" &&
                                item.plangroup !== "DTV Addon" &&
                                item.plangroupid
                            )
                                childActivePlanGroup.push(item);
                        });

                        if (childActivePlanGroup.length > 0)
                            this.childPlanGroup[i].serviceMappingData = childActivePlanGroup;
                        if (childActivePlans.length > 0)
                            this.childPlanList[i].serviceMappingData = childActivePlans;

                        planList = response.dataList;
                        planList.forEach(plan => {
                            if (plan.plangroupid == null) {
                                this.childPlanGroupFlag = false;
                                this.childPlan_PLANGROUPID.push({
                                    id: plan.planId
                                });
                                this.childPlanRenewArray.push(
                                    this.fb.group({
                                        custId: [element.id],
                                        planId: plan.planId,
                                        connectionNo: plan.connection_no,
                                        planType: ["Renew"],
                                        changePlan: [false],
                                        chargeAblSele: [chargeAvailable],
                                        custPlanMapppingId: plan.custPlanMapppingId,
                                        oldPlanId: plan.planId
                                    })
                                );
                            } else if (plan.plangroupid != null) {
                                this.childPlanGroupFlag = true;
                                let groupId;
                                setTimeout(() => {
                                    this.filterPlanGroupListCust.forEach(e => {
                                        if (e.planGroupName === plan.planGroupName) {
                                            groupId = e.planGroupId;
                                        }
                                    });

                                    this.childPlan_PLANGROUPID.push({ id: groupId });
                                    this.childPlanRenewArray.push(
                                        this.fb.group({
                                            custId: [element.id],
                                            planGroupId: [groupId],
                                            changePlanCategory: "",
                                            connectionNo: plan.connection_no,
                                            planType: ["Renew"],
                                            chargeAblSele: [chargeAvailable]
                                        })
                                    );
                                }, 1000);
                            }
                        });
                    });
                });

                if (this.childCustomerDataListForChangePlan.length > 0) {
                    this.UpdateParentCustPlans = false;
                }
                this.childCustomerDataTotalRecordsForChangePlan = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    pageChangeEventForChildCustomersForChangePlan(pageNumber: number) {
        this.pageNumberForChildsPageForChangePlan = pageNumber;
        this.getChildCustomersForChangePlan(this.customerLedgerDetailData.id);
    }

    itemPerPageChangeEventForChangePlan(event) {
        this.childPlanRenewArray = this.fb.array([]);
        this.pageSizeForChildsPageForChangePlan = Number(event.value);
        this.getChildCustomersForChangePlan(this.customerLedgerDetailData.id);
    }

    childPlanType: any;

    changePlanFromParent() {
        const newPlan = [];
        let PlanData: any = {};
        const pareChildPojo: any = [];
        if (this.subisuChange) {
            this.changePlanForm.patchValue({
                planMappingList: this.plansArray.value
            });
            this.plansArray.value.forEach((element, i) => {
                newPlan.push(element.planId);
            });
        } else {
            this.changePlanForm.patchValue({
                newPlanList: this.selectPlanListIDs,
                planMappingList: null
            });
        }

        if (this.UpdateParentCustPlans == true) {
            this.changePlansubmitted = true;
        }
        this.changePlanForm.patchValue({
            planGroupId: this.changePlanForm.value.planGroupId
                ? this.changePlanForm.value.planGroupId
                : this.planGroupSelected,
            remarks: this.changePlanForm.value.remarks
                ? this.changePlanForm.value.remarks
                : this.changePlanRemark
        });


        if (
            (this.changePlanForm.valid && this.UpdateParentCustPlans == true) ||
            (!this.changePlanForm.valid && this.UpdateParentCustPlans == false)
        ) {
            if (this.changePlanForm.value.purchaseType == "Addon") {
                this.changePlanData.addonStartDate = this.currentData;
            }
            //  this.changePlanData.bindWithOldPlanId = null
            //  this.changePlanData.createdById = null
            //  this.changePlanData.createdByName= null
            //  this.changePlanData.createdate= null
            this.changePlanData.custId = this.customerLedgerDetailData.id;
            this.changePlanData.discount = this.planDiscount;
            this.changePlanData.isAdvRenewal = false;
            this.changePlanData.isPaymentReceived = this.changePlanForm.value.isPaymentReceived;
            this.changePlanData.isRefund = false;
            //  this.changePlanData.lastModifiedById =null
            //  this.changePlanData.lastModifiedByName =null
            //  this.changePlanData.onlinePurType=null
            if (!this.custServiceData) {
                if (this.changePlanBindigNewPlan.length == 0) {
                    this.changePlanData.planBindWithOldPlans = null;
                } else {
                    this.changePlanData.planBindWithOldPlans = this.changePlanBindigNewPlan;
                }
            } else {
                let updatedData = [];
                this.custServiceData.forEach(e => {
                    if (e.newplan) {
                        let data = {
                            newPlanId: e.newplan,
                            custServiceMappingId: e.custPlanMapppingId,
                            discount: e.discount
                        };
                        updatedData.push(data);
                    }
                });
                this.changePlanData.planBindWithOldPlans = updatedData;
            }

            if (
                this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
                this.customerLedgerDetailData.plangroupid !== null
            ) {
                this.changePlanData.planGroupId = this.customerLedgerDetailData.plangroupid;
            } else {
                this.changePlanData.planGroupId = this.changePlanForm.value.planGroupId;
            }
            this.changePlanData.planId = this.changePlanForm.value.planId;

            if (this.selectPlanListIDs.length !== 0 && !this.subisuChange) {
                this.changePlanData.newPlanList = this.selectPlanListIDs;
            } else if (this.selectPlanListIDs.length == 0 && this.subisuChange) {
                this.changePlanData.newPlanList = newPlan;
                this.changePlanData.planMappingList = this.plansArray.value;
            } else {
                this.changePlanData.newPlanList = null;
                this.changePlanData.planMappingList = null;
            }

            // if (this.selectPlanListIDs.length !== 0) {
            //   this.changePlanData.newPlanList = this.selectPlanListIDs;
            // } else {
            //   this.changePlanData.newPlanList = null;
            // }

            // this.changePlanData.newPlanList= this.selectPlanListIDs
            // this.changePlanData.planList=null
            // this.changePlanData.planMappingList=null
            // this.changePlanData.purchaseFrom =null
            // this.changePlanData.purchaseId =null

            if (this.childPlanType) {
                this.changePlanData.purchaseType = this.childPlanType;
            } else {
                this.changePlanData.purchaseType = this.changePlanForm.value.purchaseType;
            }
            // this.changePlanData = this.changePlanForm.value;
            if (!this.changePlanData.recordPaymentDTO) {
                this.changePlanData.recordPaymentDTO = {};
            } else {
                this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
                this.changePlanData.recordPaymentDTO.custId = this.customerLedgerDetailData.id;
            }

            this.changePlanData.custServiceMappingId = this.custPlanMapppingId;
            this.changePlanData.remarks = this.changePlanForm.value.remarks;
            // this.changePlanData.updatedate = null
            // this.changePlanData.walletBalUsed =null
            // this.changePlanData.discount = this.planDiscount;
            if (this.changePlanForm.valid && this.UpdateParentCustPlans == true) {
                pareChildPojo.push(this.changePlanData);
            }
            let ChangePlanData = [];
            this.childPlanRenewArray.value.forEach(element => {
                PlanData = {};

                if (element.changePlan == true) {
                    if (element.planType == "Changeplan" && element.planId != element.oldPlanId) {
                        let changePlanObj: any = {};
                        // if()
                        changePlanObj.custId = element.custId;
                        changePlanObj.planGroupChange = false;
                        changePlanObj.planGroupFullyChanged = false;
                        changePlanObj.paymentOwnerId = "";
                        changePlanObj.billableCustomerId = null;
                        changePlanObj.deactivatePlanReqModels = [];

                        let deactivatePlanReqObj: any = {};
                        deactivatePlanReqObj.newPlanGroupId = "";
                        deactivatePlanReqObj.newPlanId = element.planId;
                        deactivatePlanReqObj.planGroupId = "";
                        deactivatePlanReqObj.planId = element.oldPlanId;
                        deactivatePlanReqObj.custServiceMappingId = element.custPlanMapppingId;
                        deactivatePlanReqObj.discount = 0;
                        changePlanObj.deactivatePlanReqModels.push(deactivatePlanReqObj);
                        //
                        const url = "/subscriber/deactivatePlan";
                    }
                    PlanData.addonStartDate =
                        this.changePlanForm.value.purchaseType == "Addon" ? this.currentData : null;
                    // PlanData.bindWithOldPlanId = null
                    // PlanData.createdById = null
                    // PlanData.createdByName= null
                    // PlanData.createdate= null
                    PlanData.custId = element.custId;
                    PlanData.discount = this.planDiscount;
                    PlanData.isAdvRenewal = false;
                    PlanData.isPaymentReceived = this.changePlanData.isPaymentReceived;
                    PlanData.isRefund = false;

                    // PlanData.lastModifiedById =null
                    // PlanData.lastModifiedByName =null
                    // PlanData.onlinePurType=null
                    if (this.changePlanBindigChildNewPlan.length == 0) {
                        PlanData.planBindWithOldPlans = null;
                    } else {
                        PlanData.planBindWithOldPlans = this.changePlanBindigChildNewPlan;
                    }

                    if (!this.planGroupFlag) {
                        PlanData.planId = element.planId;
                    } else {
                        PlanData.planId = null;
                        PlanData.planGroupId = element.planGroupId;
                    }
                    if (element.planGroupId) {
                        PlanData.planGroupId = element.planGroupId;
                    } else {
                        PlanData.planId = element.planId;
                    }
                    // PlanData.purchaseFrom = null
                    // PlanData.purchaseId = null

                    if (this.selectPlanChildListIDs != null && this.selectPlanChildListIDs.length !== 0) {
                        PlanData.newPlanList = this.selectPlanChildListIDs;
                    } else {
                        PlanData.newPlanList = null;
                    }
                    // PlanData.newPlanList = this.selectPlanChildListIDs;

                    PlanData.purchaseType = this.changePlanData.purchaseType
                        ? this.changePlanData.purchaseType
                        : this.childPlanRenewArray.value[0].planType;

                    if (!PlanData.recordPaymentDTO) {
                        PlanData.recordPaymentDTO = {};
                    } else {
                        PlanData.recordPaymentDTO.isTdsDeducted = false;
                        PlanData.recordPaymentDTO.custId = this.customerLedgerDetailData.id;
                    }
                    PlanData.remarks = this.changePlanData.remarks;
                    PlanData.planMappingList = null;
                    // PlanData.updatedate =null
                    // PlanData.walletBalUsed =null
                    PlanData.custServiceMappingId = element.custPlanMapppingId;
                    PlanData.isParent = true;
                    pareChildPojo.push(PlanData);
                }
            });
            let changePlanRequestDTOList = [];
            if (this.UpdateParentCustPlans) {
                changePlanRequestDTOList.push(...pareChildPojo);
            }

            if (this.childPlanData.length > 0) {
                changePlanRequestDTOList.push(...this.childPlanData);
            }

            if (this.changePlanForm.value.purchaseType !== "Changeplan") {
                const url = "/subscriber/changePlan01";

                this.customerManagementService.postMethod(url, { changePlanRequestDTOList }).subscribe(
                    (response: any) => {
                        if (response.responseCode == 200) {
                            this.toastr.success(`${response.responseMessage}`, 'Successfully!');

                            $("#selectPlanGroupChangeService").modal("hide");
                            $("#addRemark").modal("hide");
                            if (this.addChargeForm.value.chargeAdd == true) {
                                this.createNewChargeData(this.customerLedgerDetailData.id);
                            }
                            this.childChargeData.forEach((element, i) => {
                                const n = i + 1;
                                this.childPlanRenewArray.value.forEach((chData, i) => {
                                    if (chData.custId == element.data.custid && chData.chargeAblSele == true) {
                                        const url = "/createCustChargeOverride";
                                        this.customerManagementService
                                            .postMethod(url, element.data)
                                            .subscribe((response: any) => { });
                                    }
                                });

                                if (n == this.childChargeData.length) {
                                }
                            });

                            this.changePlansubmitted = false;
                            this.changePlanForm.reset();
                            this.changePlanForm.get("isPaymentReceived").setValue("false");
                            this.changePlanBindigNewPlan = [];
                            this.changePlanBindigChildNewPlan = [];
                            this.planDiscount = 0;
                            this.changePlanForm.reset();
                            this.changenewPlanForm.controls.ChangePlanCategory.reset();
                            this.selectPlanListIDs = [];
                            this.selectPlanChildListIDs = [];
                            this.changePlansubmitted = false;
                            this.planDiscount = 0;
                            this.finalOfferPrice = 0;
                            this.groupOfferPrices = {};
                            this.offerPrice = 0;
                            this.selPlanData = [];
                            this.changePlanDate = [];
                            this.chargeGroupForm.reset();
                            this.addChargeForm.reset();
                            this.overChargeListFromArray = this.fb.array([]);
                            this.openCustomersChangePlan(this.customerLedgerDetailData);
                        } else {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                            this.changePlanForm.get("isPaymentReceived").setValue("false");
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                        this.changePlanForm.get("isPaymentReceived").setValue("false");
                        this.UpdateParentCustPlans == false;
                    }
                );
            } else {
                if (this.changePlanForm.value.purchaseType == "Changeplan") {
                    this.newchangePlanfunctionality(this.chargenewPlanForm.value.plancharge);
                }
            }
        }
        // console.log(this.childPlanRenewArray.value);
        // console.log(this.changePlanForm.valid);
        // console.log(this.changePlanForm.value);
        // this.changePlanForm.patchValue({ planList: this.selectedPlanList });

        // this.changePlansubmitted = true;
        // if (this.changePlanForm.valid) {
        //   this.changePlanData = this.changePlanForm.value;
        //   this.changePlanData.isAdvRenewal = false;
        //   this.changePlanData.custId = this.customerLedgerDetailData.id;
        //   if (!this.changePlanData.recordPaymentDTO) {
        //     this.changePlanData.recordPaymentDTO = {};
        //   } else {
        //     this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
        //     this.changePlanData.recordPaymentDTO.custId =
        //       this.customerLedgerDetailData.id;
        //   }
        //   this.changePlanData.isRefund = false;
        //   const url = "/subscriber/changePlan";
        //   this.customerManagementService
        //     .postMethod(url, this.changePlanData)
        //     .subscribe(
        //       (response: any) => {
        //         if (response.responseCode == 200) {
        //           this.childPlanRenewArray.value.forEach((element) => {
        //             this.changePlanData.custId = element.custId;
        //             if (!this.planGroupFlag) {
        //               this.changePlanData.planId = element.planId;
        //             } else {
        //               this.changePlanData.planGroupId = element.planGroupId;
        //               this.changePlanData.planList = this.selectPlanChildListIDs;
        //             }
        //             this.customerManagementService
        //               .postMethod(url, this.changePlanData)
        //               .subscribe((response: any) => {});
        //           });
        //           this.messageService.add({
        //             severity: "success",
        //             summary: "Successfully",
        //             detail: response.responseMessage,
        //             icon: "far fa-check-circle",
        //           });
        //           this.changePlansubmitted = false;
        //           this.changePlanForm.reset();
        //           this.changePlanForm.get("isPaymentReceived").setValue("false");
        //           // this.childPlanRenewArray=this.fb.array([]);
        //         } else {
        //           this.messageService.add({
        //             severity: "error",
        //             summary: "Error",
        //             detail: response.responseMessage,
        //             icon: "far fa-check-circle",
        //           });
        //           this.changePlanForm.get("isPaymentReceived").setValue("false");
        //         }
        //       },
        //       (error: any) => {
        //         // console.log(error, "error")
        //         this.messageService.add({
        //           severity: "error",
        //           summary: "Error",
        //           detail: error.error.ERROR,
        //           icon: "far fa-times-circle",
        //         });
        //         this.changePlanForm.get("isPaymentReceived").setValue("false");
        //
        //       }
        //     );
        // }
    }

    allowChangePlanForParent(value) {
    }

    modalOpenParentCustomer(type) {
        this.parentCustomerDialogType = type;
        this.showParentCustomerModel = true;
        this.customerSelectType = "Billable To";
        if (type === "parent") {
            this.customerSelectType = "Parent";
        }
        this.selectedParentCust = [];
    }

    modalOpenPlanChange(e) {
        this.planGroupSelected =
            this.changePlanForm.value.planGroupId !== undefined &&
                this.changePlanForm.value.planGroupId !== "" &&
                this.changePlanForm.value.planGroupId !== 0 &&
                this.changePlanForm.value.planGroupId !== null
                ? this.changePlanForm.value.planGroupId
                : e.value;
        this.getPlangroupByPlan(this.planGroupSelected);

        if (
            this.customerLedgerDetailData.planMappingList[0].billTo == "ORGANIZATION" ||
            this.customerLedgerDetailData.planMappingList[0].billTo == "Organization"
        ) {
            this.confirmationService.confirm({
                message: "The customer is bill_to organization, do you want to continue?",
                header: "Change Plan Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.subisuChange = true;
                    this.planGroupSelectSubisu(this.planGroupSelected);
                    this.getserviceData(e.value);
                },
                reject: () => {
                    this.subisuChange = false;
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                    this.getserviceData(e.value);
                    // this.getPlanListByGroupId();
                }
            });
        } else {
            this.getserviceData(e.value);
            // this.getPlanListByGroupId();
        }
    }

    resetnewBindingPlan(id) {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);
        this.planList.forEach(element => {
            this.onNewBindingPlanMapping();
        });
        this.serviceWisePlansValue(id);
    }

    resetnewBindingPlansChild(id) {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);
        this.planListChild.forEach(element => {
            this.onNewBindingPlanMapping();
        });
        this.serviceWisePlansValue(id);
    }

    removeSelectServiceWisePlan(event, index) {
        const planId = event.value;
        const servicePlandata = this.serviceWisePlansData[index].planList;
        servicePlandata.forEach((element, i) => {
            if (element.planId == planId) {
                servicePlandata.splice(i, 1);
            }
        });
    }

    serviceWisePlansValue(id) {
        //   this.serviceWisePlansData =[
        //     {
        //       serviceId:24,
        //       planList:[{
        //         planId:2,
        //         planName: '100Mbs'
        //       }]
        //     },
        //     {
        //       serviceId:25,
        //       planList:[
        //         {
        //         planId:3,
        //         planName: 'DTH'
        //       },
        //       {
        //         planId:43,
        //         planName: 'DTH1'
        //       }
        //     ]
        //   }
        // ]
        const url = `/subscriber/serviceWisePlans/` + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.serviceWisePlansData = response.serviceWisePlans;
        });
    }

    onUpdateParentChange(e) {
        this.changePlanForm.reset();
        this.changenewPlanForm.reset();
        this.custPlanMapppingId = null;
        if (e.checked == true) {
            this.UpdateParentCustPlans = true;
        } else {
            this.UpdateParentCustPlans = false;
            this.parentPurchaseType = null;
        }
    }

    modalOpenPlanChildChange(e, custId) {

        this.lastRenewalChildPlanGroup(custId);
        this.childCustID = custId;
        this.changePlanBindigChildNewPlan = [];
        this.selectedPlanChildList = [];
        this.selectPlanChildListIDs = [];
        $("#selectPlanChildChange").modal("show");
        this.planGroupChildSelected = e;
        this.getPlanListByGroupIdChild();
        this.serviceWisePlansValue(custId);
    }

    getPlanListByGroupIdChild() {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);

        const url = `/plansByPlanGroupId?planGroupId=` + this.planGroupChildSelected;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planListChild = response.planList;
                if (this.lastRenewal_CHILDPlanGroupID != this.planGroupChildSelected) {
                    this.planListChild.forEach(element => {
                        this.onNewBindingPlanMapping();
                    });
                }

                // console.log(this.planListChild);
                this.planChangeListdatatotalRecords = this.planListChild.length;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getPlanListByGroupId() {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);

        const url = `/plansByPlanGroupId?planGroupId=` + this.planGroupSelected;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planList = response.planList;
                if (this.custServiceData) {
                    if (
                        this.changenewPlanForm.value.ChangePlanCategory == "groupPlan" ||
                        this.changePlanForm.value.purchaseType === "Renew"
                    )
                        this.groupPlanListByType = this.planList;
                }
                if (this.lastRenewalPlanGroupID != this.planGroupSelected) {
                    this.planList.forEach(element => {
                        this.onNewBindingPlanMapping();
                    });
                }
                this.planChangeListdatatotalRecords = this.planList.length;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    onNewBindingPlanMapping() {
        this.newPlanSelectArray.push(this.createteamConditionForm());
    }

    createteamConditionForm(): UntypedFormGroup {
        return this.fb.group({
            newPlan: [""]
        });
    }

    modalClosePlanChildChange() {
        this.changePlanBindigChildNewPlan = [];
        this.selectPlanChildListIDs = [];

        if (this.selectedPlanChildList.length == 0) {
            this.selectPlanChildListIDs = null;
        }

        if (this.lastRenewal_CHILDPlanGroupID != this.planGroupChildSelected) {
            this.selectedPlanChildList.forEach((element, i) => {
                this.selectPlanChildListIDs.push(element.id);
                this.newPlanSelectArray.value.forEach((data, j) => {
                    if (i == j) {
                        const newId = data.newPlan ? data.newPlan : null;
                        this.changePlanBindigChildNewPlan.push({
                            newPlanId: newId,
                            oldPlanId: element.id
                        });
                    }
                });
            });
        } else {
            this.selectedPlanChildList.forEach((element, i) => {
                this.selectPlanChildListIDs.push(element.id);
            });
        }
        $("#selectPlanChildChange").modal("hide");
    }

    modalClosePlanChange() {
        this.changePlanBindigNewPlan = [];
        this.selectPlanListIDs = [];

        if (this.selectedPlanList.length == 0) {
            this.selectPlanListIDs = null;
        }

        if (this.lastRenewalPlanGroupID != this.planGroupSelected) {
            this.selectedPlanList.forEach((element, i) => {
                this.selectPlanListIDs.push(element.id);
                this.changePlanStartEndDate();
                this.newPlanSelectArray.value.forEach((data, j) => {
                    if (i == j) {
                        const newId = data.newPlan ? data.newPlan : null;
                        this.changePlanBindigNewPlan.push({
                            newPlanId: newId,
                            oldPlanId: element.id
                        });
                    }
                });
            });
        } else {
            this.selectedPlanList.forEach((element, i) => {
                this.selectPlanListIDs.push(element.id);
                this.changePlanStartEndDate();
            });
        }
        $("#selectPlanChange").modal("hide");
    }

    getParentCustomerData() {
        let currentPage;
        // if (pageData) {
        //   currentPage = pageData + 1;
        // } else {
        currentPage = this.currentPageParentCustomerListdata;
        // }

        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const url = "/parentCustomers/list/" + this.custType;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                const list = this.prepaidParentCustomerList;
                const filterList = list.filter(cust => cust.id !== this.editCustomerId);

                this.prepaidParentCustomerList = filterList;

                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    selectedStaff: any = [];
    selectStaffType = "";
    staffSelectList: any = [];
    modalOpenSelectStaff(type) {
        this.parentCustomerDialogType = type;
        this.showSelectStaffModel = true;
        this.selectedStaff = [];
        this.selectStaffType = type;
    }

    selectedStaffChange(event) {
        this.showSelectStaffModel = false;
        let data = event;
        this.staffSelectList.push({
            id: Number(data.id),
            name: data.firstname
        });
        if (this.selectStaffType == "paymentCharge") {
            this.shiftLocationChargeGroupForm.patchValue({
                paymentOwnerId: data.id
            });
        } else if (this.selectStaffType == "requestedByID") {
            this.requestedByID = data.id;
        }
    }

    removeSelectStaff() {
        this.staffSelectList = [];
    }

    closeSelectStaff() {
        this.showParentCustomerModel = false;
        this.showSelectStaffModel = false;
    }

    async selectedCustChange(event) {
        this.showParentCustomerModel = false;
        this.selectedParentCust = event;
        // console.log(
        //   "Parent Expirence called",
        //   this.customerGroupForm.controls.parentExperience.enable()
        // );

        if (this.parentCustomerDialogType === "billable") {
            this.billableCustList = [
                {
                    id: this.selectedParentCust.id,
                    name: this.selectedParentCust.name
                }
            ];
            this.customerGroupForm.patchValue({
                billableCustomerId: this.selectedParentCust.id
            });
        } else if (this.parentCustomerDialogType === "billable-change-plan") {
            this.billableCustList = [
                {
                    id: this.selectedParentCust.id,
                    name: this.selectedParentCust.name
                }
            ];
            this.changePlanForm.patchValue({
                billableCustomerId: this.selectedParentCust.id
            });
            this.changePlanNewForm.patchValue({
                billableCustomerId: this.selectedParentCust.id
            });
        } else if (this.parentCustomerDialogType === "billable-shift-location") {
            this.billableCustList = [
                {
                    id: this.selectedParentCust.id,
                    name: this.selectedParentCust.name
                }
            ];
            this.shiftLocationChargeGroupForm.patchValue({
                billableCustomerId: this.selectedParentCust.id
            });
        } else {
            this.parentCustList = [
                {
                    id: this.selectedParentCust.id,
                    name: this.selectedParentCust.name
                }
            ];
            this.customerGroupForm.patchValue({
                parentCustomerId: this.selectedParentCust.id
            });

            const url = "/customers/" + this.selectedParentCust.id;
            let parentCustServiceAreaId: any;

            await this.customerManagementService.getMethod(url).subscribe((response: any) => {
                parentCustServiceAreaId = response.customers.serviceareaid;
                this.serviceareaCheck = false;
                this.customerGroupForm.patchValue({
                    serviceareaid: parentCustServiceAreaId
                });
                // console.log("response2", parentCustServiceAreaId);
                if (parentCustServiceAreaId) {
                    this.selServiceAreaByParent(parentCustServiceAreaId);
                    this.serviceAreaDisable = true;
                }
                this.customerGroupForm.controls.parentExperience.setValue("Actual");
                this.customerGroupForm.controls.parentExperience.enable();

                // console.log("response1", parentCustServiceAreaId);
            });
            // setTimeout(() => {

            // }, 5000);
            if (
                this.planCategoryForm.value.planCategory != null &&
                this.planCategoryForm.value.planCategory == "groupPlan"
            ) {
                this.customerGroupForm.controls.invoiceType.enable();
                this.planGroupForm.controls.invoiceType.disable();
            } else if (
                this.planCategoryForm.value.planCategory != null &&
                this.planCategoryForm.value.planCategory == "individual"
            ) {
                this.customerGroupForm.controls.invoiceType.disable();
                this.planGroupForm.controls.invoiceType.enable();
            }
        }
    }

    removeSelParentCust(type) {
        this.selectedParentCust = [];
        if (type === "billable") {
            this.billableCustList = [];
            this.customerGroupForm.patchValue({
                billableCustomerId: null
            });
        } else if (type === "billable-change-plan") {
            this.billableCustList = [];
            this.changePlanForm.patchValue({
                billableCustomerId: null
            });
        } else if (type === "billable-shift-location") {
            this.billableCustList = [];
            this.shiftLocationChargeGroupForm.patchValue({
                billableCustomerId: null
            });
        } else {
            this.customerGroupForm.patchValue({
                parentCustomerId: ""
            });
            this.customerGroupForm.controls.invoiceType.setValue("");
            this.customerGroupForm.controls.invoiceType.disable();
            // console.log(
            //   "parenetExpirnceEnable",
            //   this.customerGroupForm.controls.parentExperience.enable()
            // );
            this.planGroupForm.controls.invoiceType.setValue("");
            this.planGroupForm.controls.invoiceType.disable();
            this.customerGroupForm.controls.parentExperience.setValue("");
            this.customerGroupForm.controls.parentExperience.disable();

            this.customerGroupForm.controls.serviceareaid.setValue("");
            this.serviceAreaDisable = false;
            this.parentCustList = [];
        }
        this.isBranchAvailable = false;
    }

    selServiceAreaByParent(id) {
        const serviceAreaId = id;
        this.pincodeDD = [];
        if (serviceAreaId) {
            const url = "/serviceArea/" + serviceAreaId;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.serviceareaCheck = false;
                    this.serviceAreaData = response.data;
                    this.serviceAreaData.pincodes.forEach(element => {
                        this.commondropdownService.allpincodeNumber.forEach(e => {
                            if (e.pincodeid == element) {
                                this.pincodeDD.push(e);
                            }
                        });
                    });
                    this.getPartnerAllByServiceArea(serviceAreaId);
                    this.getServiceByServiceAreaID(serviceAreaId);
                    this.getBranchByServiceAreaID(serviceAreaId);
                    this.getStaffUserByServiceArea(serviceAreaId);
                    this.branchByServiceAreaID(serviceAreaId);
                    // if(this.serviceAreaData.latitude && this.serviceAreaData.longitude){
                    //   this.customerGroupForm.patchValue({
                    //     latitude: this.serviceAreaData.latitude,
                    //     longitude: this.serviceAreaData.longitude,
                    //   })
                    // }

                    // this.getAreaData(this.serviceAreaData.areaid, "present");
                },
                (error: any) => {
                    // console.log(error, 'error')

                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    selParentSearchOption(event) {
        // console.log("value", event.value);
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    searchParentCustomer() {
        const searchParentData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage
        };

        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();

        const url = "/parentCustomers/search/" + this.custType;
        // console.log("this.searchData", this.searchData)
        this.customerManagementService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.msg}`, 'Info!');


                    // this.customerListData = [];
                    this.parentCustomerListdatatotalRecords = 0;
                } else {
                    this.prepaidParentCustomerList = response.parentCustomerList;
                    const list = this.prepaidParentCustomerList;
                    const filterList = list.filter(cust => cust.id !== this.editCustomerId);
                    this.prepaidParentCustomerList = filterList;
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');


                    this.customerListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    openPaymentInvoiceModal(id, paymentId) {
        this.displayInvoiceDetails = true;
        this.PaymentamountService.show(id);
        this.paymentId.next({
            paymentId
        });
    }

    // update Discount
    async updateDiscount() {
        const data = [];

        for (let index = 0; index < this.custCustDiscountList.length; index++) {
            if (
                this.custCustDiscountList[index].discount !==
                this.custCustDiscountList[index].newDiscount ||
                this.custCustDiscountList[index].discountType !==
                this.custCustDiscountList[index].newDiscountType ||
                this.custCustDiscountList[index].discountExpiryDate !==
                this.custCustDiscountList[index].newDiscountExpiryDate
            ) {
                data.push({
                    id: this.custCustDiscountList[index].id,
                    custId: this.custCustDiscountList[index].custId,
                    connectionNo: this.custCustDiscountList[index].connectionNo,
                    serviceName: this.custCustDiscountList[index].serviceName,
                    serviceId: this.custCustDiscountList[index].serviceId,
                    invoiceType: this.custCustDiscountList[index].invoiceType,
                    discount: this.custCustDiscountList[index].discount,
                    newDiscount: this.custCustDiscountList[index].newDiscount,
                    remarks: this.custCustDiscountList[index].remarks,
                    status: this.custCustDiscountList[index].status,
                    discountType:
                        this.custCustDiscountList[index].discountType === null
                            ? "One-time"
                            : this.custCustDiscountList[index].discountType,
                    newDiscountType:
                        this.custCustDiscountList[index].newDiscountType === null
                            ? "One-time"
                            : this.custCustDiscountList[index].newDiscountType,
                    discountExpiryDate: moment(
                        this.custCustDiscountList[index].discountExpiryDate,
                        "DD/MM/YYYY"
                    )
                        .utc(true)
                        .toDate(),
                    newDiscountExpiryDate:
                        this.custCustDiscountList[index].newDiscountType === null ||
                            this.custCustDiscountList[index].newDiscountType === "One-time"
                            ? null
                            : moment(this.custCustDiscountList[index].newDiscountExpiryDate, "DD/MM/YYYY")
                                .utc(true)
                                .toDate()
                });
            }
        }

        this.oldDiscValue = 0;
        this.newDiscValue = 0;

        const url =
            "/subscriber/changeCustomerDiscountServiceLevel/" + this.customerLedgerDetailData.id;
        if (data.length != 0) {
            this.customerManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.getcustDiscountDetails(this.customerLedgerDetailData.id, "", "changeDiscount");
                    this.toastr.success(`${response.message}`, 'Success!');


                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');

                }
            );
        }
    }

    oldDiscValueEdit(id) {
        this.oldDiscValue = id;
        this.newDiscValue = 0;
    }

    newDiscValueEdit(id) {
        this.newDiscValue = id;
        this.oldDiscValue = 0;
    }

    saveNewPlanBinding() {
        const data = [];
        for (let index = 0; index < this.OldplanGList.length; index++) {
            data.push({
                newPlanID: this.OldplanGList[index].newPlanID,
                oldPlanId: this.OldplanGList[index].id
            });
        }
    }

    newPlanBindingCloseModal() { }

    planGroupSelectSubisu(e) {
        if (this.ifPlanGroup) {
            this.customerGroupForm.patchValue({
                discount: 0
            });

            this.planDataForm.patchValue({
                discountPrice: 0
            });
        }
        this.ifcustomerDiscountField = false;
        if (e.value) {
            let url = "/findPlanGroupById?planGroupId=" + e.value;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    const planDetailData = response.planGroup;
                    if (response.planGroup.allowDiscount == true) {
                        this.ifcustomerDiscountField = true;
                    } else {
                        this.ifcustomerDiscountField = false;
                    }
                    if (planDetailData.category == "Business Promotion") {
                        this.ifplanisSubisuSelect = true;
                        this.customerGroupForm.patchValue({
                            billTo: "ORGANIZATION",
                            isInvoiceToOrg: planDetailData.invoiceToOrg
                        });

                        // $("#selectPlanGroup").modal("show");
                        // plansArray.controls = response.planGroup
                        this.planGroupSelectedSubisu = e.value;
                        this.getPlanListByGroupIdSubisu();
                    } else if (
                        this.customerGroupForm.value.billTo == "ORGANIZATION" &&
                        planDetailData.category == "Normal" &&
                        this.ifplanisSubisuSelect == false
                    ) {
                        this.ifplanisSubisuSelect = false;
                        this.customerGroupForm.patchValue({
                            billTo: "ORGANIZATION"
                        });
                        $("#selectPlanGroup").modal("show");
                        this.planGroupSelectedSubisu = e.value;
                        this.getPlanListByGroupIdSubisu();
                    } else {
                        this.ifplanisSubisuSelect = false;
                        this.customerGroupForm.patchValue({
                            billTo: "CUSTOMER"
                        });

                        if (this.customerChangePlan) {
                            $("#selectPlanGroup").modal("show");
                            this.planGroupSelectedSubisu = e.value;
                            this.getPlanListByGroupIdSubisu();
                        } else {
                            this.planGroupSelectedSubisu = e.value;
                        }
                    }
                    let newAmount = 0;
                    let totalAmount = 0;
                    this.planIds = [];
                    planDetailData.planMappingList.forEach((element, i) => {
                        let n = i + 1;
                        newAmount =
                            element.plan.newOfferPrice != null
                                ? element.plan.newOfferPrice
                                : element.plan.offerprice;
                        totalAmount = Number(totalAmount) + Number(newAmount);
                        if (planDetailData.planMappingList.length == n) {
                            this.planDataForm.patchValue({
                                offerPrice: totalAmount
                            });
                        }
                        this.planIds.push(element.plan.id);
                    });
                    this.discountPercentage({ planGroupId: e.value });
                },
                (error: any) => { }
            );
        } else if (this.customerChangePlan) {
            $("#selectPlanGroup").modal("show");
            this.planGroupSelectedSubisu = e;
            this.getPlanListByGroupIdSubisu();
        }

        // if (this.customerGroupForm.value.billTo == "ORGANIZATION") {
        //   $("#selectPlanGroup").modal("show");
        //   this.planGroupSelectedSubisu = e.value;
        //   console.log(this.planGroupSelectedSubisu);
        //   this.getPlanListByGroupIdSubisu();
        // } else if (this.customerChangePlan) {
        //   $("#selectPlanGroup").modal("show");
        //   this.planGroupSelectedSubisu = e;
        //   console.log(this.planGroupSelectedSubisu);
        //   this.getPlanListByGroupIdSubisu();
        // }
        if (e.value) {
            this.getPlangroupByPlan(e.value);
            this.planGroupDataById(e.value);
        }
    }

    planGroupDataById(planGroupId) {
        let url = "/findPlanGroupById?planGroupId=" + planGroupId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.planGroupMapingList = response.planGroup.planMappingList;
        });
    }
    getPlanListByGroupIdSubisu() {
        this.planTotalOffetPrice = 0;
        this.planListSubisu = [];
        this.plansArray.reset();
        this.plansArray = this.fb.array([]);

        const url = `/plansByPlanGroupId?planGroupId=` + this.planGroupSelectedSubisu;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planListSubisu = response.planList;
                this.planListSubisu.forEach((element, i) => {
                    let newAmount =
                        element.newOfferPrice != null ? element.newOfferPrice : element.offerprice;

                    this.plansArray.push(
                        this.fb.group({
                            planId: element.id,
                            name: element.displayName,
                            service: element.serviceId,
                            validity: element.validity,
                            discount: element.discount,
                            billTo: "ORGANIZATION",
                            offerPrice: element.offerprice,
                            newAmount: element.newOfferPrice != null ? element.newOfferPrice : element.offerprice,
                            chargeName: element.chargeList[0].charge.name,
                            isInvoiceToOrg: this.customerGroupForm.value.isInvoiceToOrg
                        })
                    );

                    this.planTotalOffetPrice = this.planTotalOffetPrice + Number(newAmount);
                });


                this.planDataForm.patchValue({
                    offerPrice: this.planTotalOffetPrice
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    modalClosePlanChangeSubisu() {
        $("#selectPlanGroup").modal("hide");
        this.changePlanStartEndDate();
    }

    valueChange(e) {
        if (!this.ifPlanGroup) {
            this.plansArray.value.forEach(element => {
                element.isInvoiceToOrg = e.value;
            });
        }
    }

    valueChangetrailPlan(e) {
        if (e.checked == true) {
            this.plansArray.value.forEach(element => {
                element.istrialplan = true;
            });
        } else {
            this.plansArray.value.forEach(element => {
                element.istrialplan = false;
            });
        }
    }

    addWalletIncustomer(id) {
        let custID = "";
        if (id.value) {
            custID = id.value;
        } else {
            custID = id;
        }
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.ifCDR = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;

        this.ifWalletMenu = true;
        this.ifUpdateAddress = false;
        const data = {
            CREATE_DATE: "",
            END_DATE: "",
            amount: "",
            balAmount: "",
            custId: custID,
            description: "",
            id: "",
            refNo: "",
            transcategory: "",
            transtype: ""
        };
        const url = "/wallet";
        this.customerManagementService.postMethod(url, data).subscribe((response: any) => {
            this.getWallatData = response;
            this.WalletAmount = response.customerWalletDetails;
        });
    }

    getWalletData(custID) {
        const data = {
            CREATE_DATE: "",
            END_DATE: "",
            amount: "",
            balAmount: "",
            custId: custID,
            description: "",
            id: "",
            refNo: "",
            transcategory: "",
            transtype: ""
        };
        const url = "/wallet";
        this.customerManagementService.postMethod(url, data).subscribe((response: any) => {
            this.getWallatData = response;
            this.WalletAmount = response.customerWalletDetails;
            this.walletValue = this.WalletAmount;
            if (this.walletValue >= 0) {
                this.dueValue = 0;
            } else {
                this.dueValue = Math.abs(this.walletValue);
            }
        });
    }

    openchargeDetails(custId) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.isStatusChangeSubMenu = false;
        this.ifCDR = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifChargeGetData = true;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.chargeUseCustID = custId;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    openServiceDetails(custId) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.isStatusChangeSubMenu = false;
        this.ifCDR = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = true;
        this.isCreditNoteOpen = false;
        this.chargeUseCustID = custId;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    openCreditNoteDetails(custId) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.isStatusChangeSubMenu = false;
        this.ifCDR = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = true;
        this.chargeUseCustID = custId;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
    }

    createNewChargeData(customerid) {
        let chargeData = [];
        let pojo = [];
        chargeData = this.overChargeListFromArray.value;
        if (this.customerChargeDataShowChangePlan.length == 0) {
            pojo = this.overChargeListFromArray.value;
        } else {
            chargeData.forEach((element, index) => {
                if (index > this.parentChargeRecurringCustList) {
                    pojo.push(element);
                }
            });
        }

        const url = "/createCustChargeOverride";
        const chargeDta = {
            custChargeDetailsPojoList: pojo,
            custid: customerid
        };
        this.customerManagementService.postMethod(url, chargeDta).subscribe(
            (response: any) => {
                this.addChargeForm.reset();
                this.chargeGroupForm.reset();
                this.overChargeListFromArray = this.fb.array([]);
            },
            (error: any) => { }
        );
    }

    closeChargeModal() {
        this.overChargeChildListFromArray.reset();
        this.overChargeChildListFromArray = this.fb.array([]);
    }

    checkChargeevent(event, data) {
        this.chargeAllData = [];
        this.planDropdownInChageData = [];
        if (event.checked == true) {
            this.chargeAllData = data.value;
            this.customerchargeDATA(this.chargeAllData.custId, "child");
            this.overChargeChildListFromArray.reset();
            this.overChargeChildListFromArray = this.fb.array([]);
            $("#addChildChargeId").modal("show");
            if (this.chargeAllData.planGroupId) {
                this.getPlangroupByPlan(this.chargeAllData.planGroupId);
            }
            if (this.chargeAllData.planId) {
                const url = "/postpaidplan/" + this.chargeAllData.planId;

                this.customerManagementService.getMethod(url).subscribe((response: any) => {
                    this.planDropdownInChageData.push(response.postPaidPlan);
                });
            }
        } else {
            this.chargeAllData = [];
            this.overChargeChildListFromArray.reset();
            this.overChargeChildListFromArray = this.fb.array([]);
        }
    }

    selectChildcharge(_event: any) {
        const chargeId = _event.value;
        let viewChargeData;
        let date;

        date = this.currentDate.toISOString();
        const format = "yyyy-MM-dd";
        const locale = "en-US";
        const myDate = date;
        const formattedDate = formatDate(myDate, format, locale);
        const url = "/charge/" + chargeId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            viewChargeData = response.chargebyid;
            this.selectchargeValueShow = true;
            this.chargeChildGroupForm.patchValue({
                actualprice: Number(viewChargeData.actualprice),
                charge_date: formattedDate,
                type: "One-time"
            });
        });
    }

    getCHILDPlanValidityForChagre(event) {
        const planId = event.value;
        const url = "/postpaidplan/" + planId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            const planDetailData = response.postPaidPlan;
            this.chargeChildGroupForm.patchValue({
                validity: Number(planDetailData.validity),
                unitsOfValidity: planDetailData.unitsOfValidity
            });
        });
    }

    createoverChargeChildListFormGroup(): UntypedFormGroup {
        this.chargeChildGroupForm.get("billingCycle").clearValidators();
        this.chargeChildGroupForm.get("billingCycle").updateValueAndValidity();
        return this.fb.group({
            // chargeid: [''],
            type: [this.chargeChildGroupForm.value.type],
            chargeid: [this.chargeChildGroupForm.value.chargeid],
            validity: [this.chargeChildGroupForm.value.validity],
            price: [this.chargeChildGroupForm.value.price],
            actualprice: [this.chargeChildGroupForm.value.actualprice],
            charge_date: [this.chargeChildGroupForm.value.charge_date],
            planid: [this.chargeChildGroupForm.value.planid],
            unitsOfValidity: [this.chargeChildGroupForm.value.unitsOfValidity],
            billingCycle: [this.chargeChildGroupForm.value.billingCycle],
            id: [this.chargeChildGroupForm.value.id]
        });
    }

    onAddoverChargeChildListField() {
        this.chargesubmitted = true;

        if (this.chargeChildGroupForm.valid) {
            if (this.chargeChildGroupForm.value.price >= this.chargeChildGroupForm.value.actualprice) {
                this.overChargeChildListFromArray.push(this.createoverChargeChildListFormGroup());
                this.chargeChildGroupForm.reset();
                this.chargesubmitted = false;
                this.selectchargeValueShow = false;
            }
        } else {
            // console.log("I am not valid");
        }
    }

    deleteConfirmonChildChargeField(chargeFieldIndex: number, name: string) {
        if (chargeFieldIndex || chargeFieldIndex == 0) {
            const msgTxt = "";
            this.confirmationService.confirm({
                message: "Do you want to delete this " + name + "?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.onRemoveCHILDChargelist(chargeFieldIndex);
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    async onRemoveCHILDChargelist(index: number) {
        this.overChargeChildListFromArray.removeAt(index);
    }

    createChildchargeData() {
        let pojo = [];
        if (this.customerChargeDataShowChangePlan.length == 0) {
            pojo = this.overChargeChildListFromArray.value;
        } else {
            this.overChargeChildListFromArray.value.forEach((element, k) => {
                if (k > this.childChargeRecurringCustList) {
                    pojo.push(element);
                }
            });
        }
        this.childChargeData.push({
            data: {
                custChargeDetailsPojoList: pojo,
                custid: this.chargeAllData.custId
            }
        });
    }

    billingSequence() {
        for (let i = 0; i < 12; i++) {
            this.billingCycle.push({ label: i + 1 });
            // console.log(this.billingCycle)
        }
    }

    selectTypecharge(e) {
        this.chargeGroupForm.get("connection_no").reset();
        this.chargeGroupForm.get("planid").reset();
        this.chargeGroupForm.get("expiry").reset();
        if (e.value == "Recurring") {
            // this.chargeGroupForm.get("billingCycle").setValidators([Validators.required]);
            // this.chargeGroupForm.get("billingCycle").updateValueAndValidity();
        } else {
            this.chargeGroupForm.value.billingCycle = 0;
            // this.chargeGroupForm.get("billingCycle").clearValidators();
            // this.chargeGroupForm.get("billingCycle").updateValueAndValidity();
        }
    }

    selectTypeChildcharge(e) {
        this.chargeChildGroupForm.get("connection_no").reset();
        this.chargeChildGroupForm.get("planid").reset();
        this.chargeChildGroupForm.get("expiry").reset();
        if (e.value == "Recurring") {
            // /this.chargeChildGroupForm.get("billingCycle").setValidators([Validators.required]);
            // this.chargeChildGroupForm.get("billingCycle").updateValueAndValidity();
        } else {
            this.chargeChildGroupForm.value.billingCycle = 0;
            // this.chargeChildGroupForm.get("billingCycle").clearValidators();
            // this.chargeChildGroupForm.get("billingCycle").updateValueAndValidity();
        }
    }

    pageChangedOverChargeChildList(page) {
        this.currentPageoverChargeListChild = page;
    }

    getCustomerAssignedList(id): void {
        const data = {
            filters: [
                {
                    filterValue: id,
                    filterColumn: "customerId"
                }
            ],
            page: 1,
            pageSize: 5,
            sortBy: "createdate",
            sortOrder: 0
        };
        data.page = this.customerInventoryListDataCurrentPage;
        data.pageSize = this.customerInventoryListItemsPerPage;

        this.customerInventoryMappingService.getByCustomerId(data).subscribe(
            (res: any) => {
                this.assignInventoryWithSerial = false;
                this.assignedInventoryList = res.dataList;
                this.customerInventoryListDataTotalRecords = res.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    assignToStaff(flag) {
        let url: any;
        let name: string;
        if (this.isStatusChangeSubMenu) {
            name = "TERMINATION";
        } else if (this.customerUpdateDiscount) {
            name = "CUSTOMER_DISCOUNT";
        } else if (this.shiftLocationEvent) {
            name = "SHIFT_LOCATION";
        } else {
            name = "CUSTOMER_INVENTORY_ASSIGN";
        }
        if (!this.selectStaff && !this.selectStaffReject) {
            url = `/teamHierarchy/assignEveryStaff?entityId=${this.approveId}&eventName=${name}&isApproveRequest=${flag}`;
        } else {
            if (flag) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${name}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${name}&nextAssignStaff=${this.selectStaffReject}&isApproveRequest=${flag}`;
            }
        }

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (flag) {
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    } else {
                        this.toastr.success(`${response.responseMessage}`, 'Approved Successfully!');

                    }
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Rejected Successfully!');

                }
                $("#assignCustomerInventoryModal").modal("hide");
                $("#rejectCustomerInventoryModal").modal("hide");
                if (this.isStatusChangeSubMenu) {
                    this.getapproveStatusList("");
                } else if (this.customerUpdateDiscount) {
                    this.openCustorUpdateDiscount(this.customerLedgerDetailData.id);
                } else if (this.shiftLocationEvent) {
                    this.openCustomerAddress();
                    this.getNewCustomerAddressForCustomer(this.assignShiftLocationData.customerId);
                } else {
                    this.getCustomerAssignedList(this.assignInventoryCustomerId);
                }

                // this.newCustomerAddressDataForCustometr(this.customerLedgerDetailData.id);
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    custWorkflowAuditopen(id, auditcustid) {
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid,
            checkHierachy: "CAF",
            planId: ""
        });
    }

    getCustPlanGroupDataopen(id, planGroupcustid) {
        this.PaymentamountService.show(id);
        this.planGroupcustid.next({
            planGroupcustid
        });
    }

    promiseToPayDetailsClick(id, startDate, endDate, days) {
        this.promiseToPayData = [{ startDate: startDate, endDate: endDate, days: days }];
        this.isPromiseToPayModelOpen = true;
        this.PaymentamountService.show(id);
    }

    custTerminationWorkflowAuditopen(id, auditcustid) {
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid,
            checkHierachy: "Termination",
            planId: ""
        });
    }

    voidInvoice(): void {
        // if (invoice) {
        this.confirmationService.confirm({
            message: "Do you wish to VOID this invoice?",
            header: "VOID Invoice Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                const url = `/invoiceV2/voidInvoice?invoiceId=${this.invoiceID}&invoiceCancelRemarks=${this.invoiceCancelRemarks}`;
                this.customerManagementService.getMethod(url).subscribe(
                    (response: any) => {
                        // this.closebutton.nativeElement.click();
                        this.ifInvoicePayment = false;
                        this.ispaymentChecked = false;
                        this.allIsChecked = false;
                        this.isSinglepaymentChecked = false;
                        this.invoiceCancelRemarks = null;
                        this.invoiceCancelRemarksType = null;
                        this.invoicePaymentData = [];
                        this.allchakedPaymentData = [];
                        this.searchinvoiceMaster("", "");
                        $("#invoiceCancelRemarks").modal("hide");
                        if (response.responseCode == 417) {
                            this.toastr.info(`${response.message}`, 'Info!');


                        } else {
                            this.toastr.success(`${response.message}`, 'Success!');


                        }
                    },
                    (error: any) => {
                        // console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            },
            reject: () => {
                (error: any) => {

                    this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                }

            }
        });
        // }
    }

    // DBR

    approveReplaceInventoryInventory(newId, oldId, status): void {
        this.approveId = newId;
        this.approved = false;
        this.approveInventoryData = [];
        this.selectStaff = null;
        let bool: boolean = false;
        if (status !== "PENDING") {
            bool = true;
        }
        const url = `/inwards/approveReplaceInventory?isApproveRequest=true&oldMacMappingId=${oldId}&newMacMappingId=${newId}&billAble=${bool}`;

        this.customerInventoryMappingService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedInventoryListWithSerial = [];
                this.getCustomerAssignedList(this.assignInventoryCustomerId);
                this.assignInventoryWithSerial = false;
                if (response.dataList) {
                    this.approved = true;
                    this.approveInventoryData = response.dataList;
                    $("#assignCustomerInventoryModal").modal("show");
                } else {
                    this.getCustomerAssignedList(this.assignInventoryCustomerId);
                }

                this.getCustomerAssignedList(this.assignInventoryCustomerId);
                // this.customerInventoryListDataTotalRecords = res.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    checkStatusForRepalce(id): void {
        const url = `/teamHierarchy/getApprovalProgress?entityId=${id}&eventName=CUSTOMER_INVENTORY_ASSIGN`;

        this.customerInventoryMappingService.getMethod(url).subscribe(
            (res: any) => {
                this.inventoryStatusDetailsForReplace = res.dataList;
                // this.inventoryStatusView = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'msg!');


            }
        );
    }

    rejectInventoryReplaceInventory(newId, oldId, status): void {
        this.approveId = newId;
        this.reject = false;
        this.selectStaffReject = null;
        this.rejectInventoryData = [];
        let bool: boolean;
        if (status !== "PENDING") {
            bool = true;
        }
        const url = `/inwards/approveReplaceInventory?isApproveRequest=false&oldMacMappingId=${oldId}&newMacMappingId=${newId}&billAble=${bool}`;

        this.customerInventoryMappingService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedInventoryListWithSerial = [];
                this.getCustomerAssignedList(this.assignInventoryCustomerId);
                this.assignInventoryWithSerial = false;
                if (response.dataList) {
                    this.reject = true;
                    this.rejectInventoryData = response.dataList;
                    $("#rejectCustomerInventoryModal").modal("show");
                } else {
                    this.getCustomerAssignedList(this.assignInventoryCustomerId);
                }

                // this.customerInventoryListDataTotalRecords = res.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    custDiscountWorkflowAuditopen(id, auditcustid, planId) {
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid,
            checkHierachy: "CUSTOMER_DISCOUNT",
            planId
        });
    }

    discountRejected(data) {
        this.approveId = data.id;
        $("#rejectApproveDiscountModal").modal("show");
        this.assignDiscountData = data;
        this.discountFlageType = "Rejected";
        this.AppRjecHeader = "Reject";
        this.assignAppRejectDiscountForm.reset();
    }

    discountApporeved(data) {
        this.approveId = data.id;
        $("#rejectApproveDiscountModal").modal("show");
        this.assignDiscountData = data;
        this.discountFlageType = "approved";
        this.AppRjecHeader = "Approve ";
        this.assignAppRejectDiscountForm.reset();
    }

    assignDiscountApprove() {
        this.assignDiscounsubmitted = true;
        if (this.assignAppRejectDiscountForm.valid) {
            let url = "/approveChangeDiscountServiceLevel";

            let assignCAFData = {
                custPackageId: this.assignDiscountData.id,
                flag: this.discountFlageType,
                nextStaffId: 0,
                planId: this.assignDiscountData.planId,
                remark: this.assignAppRejectDiscountForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };

            this.customerManagementService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    $("#rejectApproveDiscountModal").modal("hide");
                    if (response.dataList) {
                        if (this.discountFlageType == "approved") {
                            this.approved = true;
                            this.approveInventoryData = response.dataList;
                            $("#assignCustomerInventoryModal").modal("show");
                        } else {
                            this.reject = true;
                            this.rejectInventoryData = response.dataList;
                            $("#rejectCustomerInventoryModal").modal("show");
                        }
                    } else {
                        this.openCustorUpdateDiscount(this.customerLedgerDetailData.id);
                    }
                    this.assignAppRejectDiscountForm.reset();
                    this.assignDiscounsubmitted = false;
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    withdrawalAmountModel(modelID, wCustID, WalletAmount) {
        this.PaymentamountService.show(modelID);
        this.wCustID.next({
            wCustID,
            WalletAmount
        });
    }

    openDBRReportDetails() {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.ifCDR = false;
        const now = new Date();
        this.searchDBRFormDate = this.datepipe.transform(now, "yyyy-MM-dd");
        this.searchDBREndDate = this.datepipe.transform(
            new Date(now.setDate(now.getDate() + 30)),
            "yyyy-MM-dd"
        );
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.assignInventoryWithSerial = false;
        this.ifShowDBRReport = true;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
        this.currentPageDBRListdata = 1;
        this.showItemDBRPerPage = 0;
        this.DBRListdatatotalRecords = RadiusConstants.ITEMS_PER_PAGE;
        this.dbrListData = [];
        this.searchDBR();
    }

    TotalItemDBRPerPage(event) {
        this.showItemDBRPerPage = Number(event.value);
        if (this.currentPageDBRListdata > 1) {
            this.currentPageDBRListdata = 1;
        }
        if (!this.searchkey) {
            this.searchDBR();
        }
    }

    // TotalItemDBRPerPage(event) {
    //   this.showItemDBRPerPage = Number(event.value)
    //   if (this.currentPageDBRListdata > 1) {
    //     this.currentPageDBRListdata = 1
    //   }
    //   if (!this.searchkey) {
    //     this.searchDBR()
    //   }
    // }
    outStandingData: any;

    searchDBR() {
        let page_list;
        let size = this.showItemDBRPerPage;
        if (size != 0) {
            page_list = size;
            this.DBRListdataitemsPerPage = size;
        } else {
            if (this.showItemDBRPerPage == 0) {
                this.DBRListdataitemsPerPage = this.pageITEM;
            } else {
                this.DBRListdataitemsPerPage = this.showItemDBRPerPage;
            }
        }

        // this.currentPageDBRListdata = 1;
        let firstDay;
        let lastDay;
        firstDay = this.searchDBRFormDate;
        lastDay = this.searchDBREndDate;
        const url =
            "/getCustomer?custid=" +
            this.customerLedgerDetailData.id +
            "&startdate=" +
            firstDay +
            "&endate=" +
            lastDay;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.dbrListData = response.customerDBRPojos;
                this.outStandingData = response;
                this.DBRListdatatotalRecords = this.dbrListData.length;
                //this.searchDBRFormDate = ''
                // this.searchDBREndDate = ''
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    searchClearDBR() {
        this.searchDBRFormDate = "";
        this.searchDBREndDate = "";

        const now = new Date();
        this.searchDBRFormDate = this.datepipe.transform(now, "yyyy-MM-dd");
        this.searchDBREndDate = this.datepipe.transform(
            new Date(now.setDate(now.getDate() + 30)),
            "yyyy-MM-dd"
        );
        this.searchDBR();
    }

    pageChangedDbrList(pageNumber) {
        this.currentPageDBRListdata = pageNumber;
    }

    downloadInvoice(docId, custId, fileName) {

        const url = "/documentForInvoice/download/" + docId + "/" + custId;
        this.customerManagementService.downloadInvoice(url).subscribe(
            (response: any) => {
                // const file = new Blob([response], { type: "application/pdf" });
                // const fileURL = URL.createObjectURL(file);
                // FileSaver.saveAs(file, customerName + docNo);

                var fileType = "";
                // if (fileName.includes(".png")) {
                //   fileType =
                // }
                var file = new Blob([response], { type: "application/pdf" });
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, fileName);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    stopBilling(id) {
        const url = "/invoiceV2/stop/billing/" + id;
        let data = {};
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                if (this.searchkey) {
                    this.searchcustomer();
                } else {
                    this.getcustomerList("");
                }
                this.toastr.success(`${response.responseMessage}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, "error");

            }
        );
    }

    playstartBilling(id) {
        const url = "/invoiceV2/start/billing/" + id;
        let data = {};
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                if (this.searchkey) {
                    this.searchcustomer();
                } else {
                    this.getcustomerList("");
                }
                this.toastr.success(`${response.responseMessage}`, 'Success!');

            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    onFileChange(event) {
        if (event.target.files.length > 0) {
            this.file = "";
            this.fileName = event.target.files[0].name;
            this.file = event.target.files[0];
            // this.paymentFormGroup.patchValue({
            //   file: file,
            // });
        }
    }

    custTrailPlantotalRecords: String;
    currentTrailPlanListdata = 1;
    custTrailPlanItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TrailPlanList = [];
    custShowTrailPlanShow = 1;

    TotalTrailPlanItemPerPage(event) {
        this.custShowTrailPlanShow = Number(event.value);
        if (this.currentTrailPlanListdata > 1) {
            this.currentTrailPlanListdata = 1;
        }
        this.getTrailPlanList(this.customerLedgerDetailData.id, this.custShowTrailPlanShow);
    }

    getTrailPlanList(custId, size) {
        let page_list;
        if (size) {
            page_list = size;
            this.custTrailPlanItemPerPage = size;
        } else {
            if (this.custShowTrailPlanShow == 1) {
                this.custTrailPlanItemPerPage = this.pageITEM;
            } else {
                this.custTrailPlanItemPerPage = this.custShowTrailPlanShow;
            }
        }
        const url = "/getTrialPlanList/" + custId;
        this.customerManagementService.getProtalMethod(url).subscribe(
            (response: any) => {
                this.TrailPlanList = response.data;

                if (this.TrailPlanList.length > 0) {
                    this.istrialplan = true;
                }
                this.custTrailPlanItemPerPage = this.TrailPlanList.length;
                if (this.TrailPlanList.length > 0) {
                    this.istrialplan = true;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageCustTrailPlanListData(pageNumber) {
        this.currentTrailPlanListdata = pageNumber;
        this.getTrailPlanList(this.customerLedgerDetailData.id, "");
    }

    ExtendDays: any = "";
    trailbtnTypeSelect = "";
    tarialPlanData: any = [];

    extanTrailPlanModel(plan, trailTyep) {
        this.ExtendDays = "";
        this.tarialPlanData = plan;
        $("#trailPlanModel").modal("show");
        this.trailbtnTypeSelect = trailTyep;
    }

    extendTrailPlan() {
        const url = "/subscriber/extendTrailPlan";
        let data = {
            billingStartFrom: this.tarialPlanData.startDate,
            cprId: this.tarialPlanData.planmapid,
            custId: this.tarialPlanData.custId,
            extendDays: this.ExtendDays,
            planGroupId: this.tarialPlanData.plangroupid,
            planId: this.tarialPlanData.planId
        };
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                this.getTrailPlanList(this.customerLedgerDetailData.id, "");
                this.getcustCurrentPlan(this.customerLedgerDetailData.id, "");
                this.tarialPlanData = [];
                this.trailbtnTypeSelect = "";
                $("#trailPlanModel").modal("hide");
                this.toastr.success(`${response.responseMessage}`, 'Success!');

            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    trialDateData: any = [];
    selectScbscribeDate: any = "";
    showTrialPlan: any = " ";
    changeTrialPlanRemark = " ";
    plane: boolean = false;

    showRemark(data: any, data1) {
        const findResult = data1.find(element => element.date == data.value);
        if (findResult.label == "From Today") {
            this.plane = true;
        } else {
            if (findResult.label == "Including Trial period") {
                this.plane = false;
                this.Subscriberform.controls["remarks"].reset();
            } else {
                this.plane = false;
                this.Subscriberform.controls["remarks"].reset();
            }
        }
    }

    closesubscribeTrailPlan() {
        this.Subscriberform.reset();
    }

    subscribeTrailPlanModel(plan) {
        this.plane = false;
        let data1 = [];
        let data2 = [];
        this.trialDateData = [];
        this.selectScbscribeDate = "";
        this.showTrialPlan = data1;
        this.tarialPlanData = plan;
        let currentDate = new Date();
        const format = "dd-MM-yyyy hh:mm a";
        const locale = "en-US";
        const myDate = currentDate;
        const formattedDate = formatDate(myDate, format, locale);

        data1.push({
            date: "CURRENTDATE",
            label: "From Today",
            show: true
        });
        this.trialDateData.push(...data1);
        data2 = [
            {
                date: "INCLUDINGTRIALPERIOD",
                label: "Including Trial period",
                show: false
            }
        ];
        this.trialDateData.push(...data2);
        $("#subscribetrailPlanModel").modal("show");
    }

    subscribeTrailPlan() {
        let remark = this.Subscriberform.get("remarks").value;
        // console.log("remark", remark);
        let data = {
            billingStartFrom: this.selectScbscribeDate,
            cprId: this.tarialPlanData.planmapid,
            custId: this.tarialPlanData.custId,
            extendDays: "",
            planGroupId: this.tarialPlanData.plangroupid,
            planId: this.tarialPlanData.planId,
            remarks: remark
        };
        const url = "/subscriber/trailToNormalPlan";
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                $("#subscribetrailPlanModel").modal("hide");
                this.getTrailPlanList(this.customerLedgerDetailData.id, "");
                this.getcustomerList("");
                if (this.TrailPlanList.length > 0) this.istrialplan = true;
                else this.istrialplan = false;
                this.getcustCurrentPlan(this.customerLedgerDetailData.id, "");
                this.trialDateData = [];
                this.selectScbscribeDate = "";
                this.toastr.success(`${response.responseMessage}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, "error");

            }
        );
    }

    cancelConfirmonTrialmode(plan) {
        this.confirmationService.confirm({
            message: "Do you want to Cancel this Trial Plan?",
            header: "Cancel Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.tarialPlanData = plan;
                this.cancelTrailPlan();
            },
            reject: () => {
                (error: any) => {

                    this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                }


            }
        });
    }

    cancelTrailPlan() {
        let data = {
            billingStartFrom: this.tarialPlanData.startDate,
            cprId: this.tarialPlanData.planmapid,
            custId: this.tarialPlanData.custId,
            extendDays: this.ExtendDays,
            planGroupId: this.tarialPlanData.plangroupid,
            planId: this.tarialPlanData.planId
        };
        const url = "/subscriber/cancel/trailplan";
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                this.getTrailPlanList(this.customerLedgerDetailData.id, "");
                this.tarialPlanData = [];
                this.trailbtnTypeSelect = "";
                $("#trailPlanModel").modal("hide");
                this.toastr.success(`${response.responseMessage}`, 'Success!');

            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    newCustomerAddressDataForCustometr: any;

    getNewCustomerAddressForCustomer(id: any): void {
        const url = "/newcustomeraddress/" + id;

        this.customerManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.newCustomerAddressDataForCustometr = res.newcustomerAddress;

            },
            (error: any) => { }
        );
    }

    graceNumberDays = "";
    // addPayButtonModel(data) {
    //   this.graceNumberDays = "";
    // }
    savegraceDays() {
        let gracePeriod = this.commondropdownService.gracePeriod;

        const url = `/subscriber/promiseToPay/${this.customerLedgerDetailData.id}/${gracePeriod[0].value}`;

        this.customerManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    this.toastr.success(`${res.message}`, 'Successfully!');

                    // $("#IdRemark").modal("hide");
                    this.remark = "";
                } else {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');


                }
                this.onClose();
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    onClose() {
        this.remark = " ";
        $("#IdRemark").modal("hide");
    }

    onCloseValidity() {
        this.extendValidityForm.reset();
        this.custPlanMappingForValidity = null;
        $("#extendValidity").modal("hide");
    }

    showdata: any = [];
    planNotes = false;

    displayNote(type) {
        if (type === "plan") {
            this.planNotes = true;
            this.showdata = this.custCurrentPlanList;
        } else if (type === "invoice") {
            this.planNotes = false;
            this.showdata = this.invoiceMasterListData.filter(
                invoice => invoice.billrunstatus === "Cancelled" || invoice.billrunstatus === "VOID"
            );
        }
    }

    shiftLocationRejected(data) {
        this.approveId = data.id;
        $("#rejectApproveShiftLocationModal").modal("show");
        this.assignShiftLocationData = data;
        this.shiftLocationFlagType = "Rejected";
        this.AppRjecHeader = "Reject";
        this.assignAppRejectShiftLocationForm.reset();
    }

    shiftLocationApproved(data) {
        this.approveId = data.id;
        $("#rejectApproveShiftLocationModal").modal("show");
        this.assignShiftLocationData = data;
        this.shiftLocationFlagType = "approved";
        this.AppRjecHeader = "Apporve ";
        this.assignAppRejectShiftLocationForm.reset();
    }

    shiftWorkflow(data) {
        this.ifModelIsShow = true;
        this.PaymentamountService.show("custauditWorkflowModal");
        this.auditcustid.next({
            auditcustid: data.id,
            checkHierachy: "SHIFT_LOCATION",
            planId: ""
        });
    }

    assignAddressApprove() {
        this.assignShiftLocationsubmitted = true;
        if (this.assignAppRejectShiftLocationForm.valid) {
            let url = "/approveCustomerAddress";

            let assignCAFData = {
                addressId: this.assignShiftLocationData.id,
                flag: this.shiftLocationFlagType,
                nextStaffId: 0,
                remark: this.assignAppRejectShiftLocationForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };

            this.customerManagementService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    $("#rejectApproveShiftLocationModal").modal("hide");
                    if (response.result.dataList) {
                        if (this.shiftLocationFlagType == "approved") {
                            this.approved = true;
                            this.approveInventoryData = response.result.dataList;
                            $("#assignCustomerInventoryModal").modal("show");
                        } else {
                            this.reject = true;
                            this.rejectInventoryData = response.result.dataList;
                            $("#rejectCustomerInventoryModal").modal("show");
                        }
                    } else {
                        this.getNewCustomerAddressForCustomer(this.assignShiftLocationData.customerId);
                    }
                    this.openCustomerAddress();
                    this.assignAppRejectShiftLocationForm.reset();
                    this.assignShiftLocationsubmitted = false;

                    // this.newCustomerAddressDataForCustometr(this.assignShiftLocationData.custId);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    openCustomerAddress() {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifCDR = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = true;
        this.isStatusChangeSubMenu = false;
        this.customerChildsView = false;
        this.customerUpdateDiscount = false;
        //this.getcustDiscountDetails(id, "");
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = true;
    }

    openWorkFlowAudit(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.assignInventoryWithSerial = false;
        this.ifShowDBRReport = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = true;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
        this.getCustomerTeamHierarchy(id);
        this.workflowID = id;
        this.getworkflowAuditDetails("", id, "CAF");
    }

    openAuditDetails(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.assignInventoryWithSerial = false;
        this.ifShowDBRReport = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = true;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = false;
        // this.getCustomerTeamHierarchy(id);
        this.workflowID = id;
        // this.getworkflowAuditDetails("", id, "CAF");
        this.GetAuditData(id, "");
    }

    getworkflowAuditDetails(size, id, name) {
        let page = this.currentPageMasterSlab;
        let page_list;
        if (size) {
            page_list = size;
            this.MasteritemsPerPage = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.MasteritemsPerPage = 5;
            } else {
                this.MasteritemsPerPage = 5;
            }
        }

        this.workflowAuditData = [];

        let data = {
            page: page,
            pageSize: this.MasteritemsPerPage
        };

        let url = "/workflowaudit/list?entityId=" + id + "&eventName=" + name;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.workflowAuditData = response.dataList;
                this.MastertotalRecords = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
                console.log(error, "error");
            }
        );
    }

    pageChangedMasterList(pageNumber) {
        this.currentPageMasterSlab = pageNumber;
        this.getworkflowAuditDetails("", this.workflowID, "CAF");
    }

    TotalItemPerPageWorkFlow(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMasterSlab > 1) {
            this.currentPageMasterSlab = 1;
        }
        this.getworkflowAuditDetails(this.showItemPerPage, this.workflowID, "CAF");
    }

    getCustomerTeamHierarchy(custId) {
        const url = `/teamHierarchy/getApprovalProgress?entityId=${custId}&eventName=CAF`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerStatusDetail = response.dataList;
                // const newList = [];
                // let parentId = null;
                // for (let i = 0; i < this.customerStatusDetail.length; i++) {
                //   for (let j = 0; j < this.customerStatusDetail.length; j++) {
                //     if (parentId == this.customerStatusDetail[j].parentTeamsId) {
                //       newList.push(this.customerStataudiyusDetail[j]);
                //       parentId = this.customerStatusDetail[j].teamsId;
                //     }
                //   }
                // }
                // const list = [];
                // for (let i = newList.length - 1; i >= 0; i--) {
                //   list.push(newList[i]);
                // }
                // console.log(list);
                // this.customerStatusDetail = list;
            },

            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    onChangeOFTDS(event) {
        let tdsAmount = event;
        let abbsAmount = this.paymentFormGroup.controls.abbsAmount.value;
        let totalAmount = this.paymentFormGroup.controls.amount.value;
        let diff = totalAmount - abbsAmount - tdsAmount;

        if (diff < 0 && tdsAmount != 0) {
            this.paymentFormGroup.controls.tdsAmount.setValue(0);
            // this.messageService.add({
            //   severity: "error",
            //   summary: "Error",
            //   detail: "TDS/ABBS total can not be greater than amount.",
            //   icon: "far fa-check-circle",
            // });
        }
    }

    onChangeOFABBS(event) {
        let abbsAmount = event;
        let tdsAmount = this.paymentFormGroup.controls.tdsAmount.value;
        let totalAmount = this.paymentFormGroup.controls.amount.value;
        let diff = totalAmount - abbsAmount - tdsAmount;

        if (diff < 0 && abbsAmount != 0) {
            this.paymentFormGroup.controls.abbsAmount.setValue(0);
            // this.messageService.add({
            //   severity: "error",
            //   summary: "Error",
            //   detail: "TDS/ABBS total can not be greater than amount.",
            //   icon: "far fa-check-circle",
            // });
        }
    }

    getCustomerType() {
        const url = "/commonList/Customer_Type";
        const custerlist = {};
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.Customertype = response.dataList;
                // console.log(this.customerGroupForm.value.subType,"this.customerGroupForm.value.subType");

                if (this.customerGroupForm.value.subType) {
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getSelectCustomerType(event) {
        const selCustomerType = event.value;
        if (selCustomerType == "Paid") {
            this.customerGroupForm.controls.subType.enable();
        }
    }

    getCustomerSector() {
        const url = "/commonList/Customer_Sector";
        const custerlist = {};
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.CustomerSector = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getcustType(event) {
        let value = event.value;
        this.customerGroupForm.controls.dunningSubType.enable();
        let actionUrl = `/commonList/${value}`;
        if (event.value == "Barter") {
            this.isCustSubTypeCon = false;
        } else {
            this.isCustSubTypeCon = true;
            this.getCustomerTypeFlow(actionUrl);
        }
    }

    getCustomerTypeFlow(url) {
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.CustomertypeSubtype = response.dataList;
        });
    }

    getSelectCustomerSector(event) {
        const value = event.value;
        if (value) {
            this.customerGroupForm.controls.dunningSubSector.enable();
        } else {
            this.customerGroupForm.controls.dunningSubSector.disable();
        }
    }

    canExit() {
        return this.utils.canExit(this.customerGroupForm.dirty);
    }

    checkExit(type) {
        if (this.isCustomerDetailSubMenu || !this.customerGroupForm.dirty) {
            this.customerGroupForm.markAsPristine();
            if (type === "create") {
                this.createnewCustomer();
            } else {
                this.listCustomer();
            }
        } else {
            this.confirmationService.confirm({
                header: "Alert",
                message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.customerGroupForm.markAsPristine();
                    if (type === "create") {
                        this.createnewCustomer();
                    } else {
                        this.listCustomer();
                    }
                },
                reject: () => {
                    return false;
                }
            });
        }
    }

    multiServiceData: any = [];

    multiService(date: any) {
        let parts_of_date = date.split("-");

        let output =
            parts_of_date[2] +
            "-" +
            (parts_of_date[1].length <= 1 ? "0" + parts_of_date[1] : parts_of_date[1]) +
            "-" +
            (parts_of_date[0].length <= 1 ? "0" + parts_of_date[0] : parts_of_date[0]);

        let url = `/getDbrByCustomerIdAndDate?custid=${this.customerLedgerDetailData.id}&startdate=${output}`;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.multiServiceData = response;
                $("#multiService").modal("show");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    viewInvoice(docnumber, custname) {
        const url = "/regeneratepdfsub/" + docnumber;
        this.invoiceMasterService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                window.open(fileURL, "_blank");
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    AuditData1: any = [];
    currentPageAuditSlab1 = 1;
    AudititemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    AudittotalRecords1: String;
    auditList: any = [];
    sortOrder = 0;
    auditData: any;

    DunningData: any = [];
    currentPageDunningSlab1 = 1;
    DunningitemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    DunningtotalRecords1: String;
    dunningList: any = [];
    dunningData: any;

    NotificationData: any = [];
    currentPageNotificationSlab1 = 1;
    NotificationitemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    NotificationtotalRecords1: String;
    notificationList: any = [];
    notificationData: any;

    GetAuditData(custId, size) {
        let page = this.currentPageAuditSlab1;
        let page_list;
        if (size) {
            page_list = size;
            this.AudititemsPerPage1 = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.AudititemsPerPage1 = 5;
            } else {
                this.AudititemsPerPage1 = 5;
            }
        }
        this.AuditData1 = [];

        let data = {
            page: page,
            pageSize: this.AudititemsPerPage1,
            sortBy: "id",
            sortOrder: 0
        };
        const url = "/auditLog/getAuditList/" + custId;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.AuditData1 = response.dataList;
                this.AudittotalRecords1 = response.totalRecords;
                //this.auditList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedAuditList(pageNumber) {
        this.currentPageAuditSlab1 = pageNumber;
        this.GetAuditData(this.auditData, "");
    }

    TotalItemPerPageAudit(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageAuditSlab1 > 1) {
            this.currentPageAuditSlab1 = 1;
        }
        this.GetAuditData(this.showItemPerPage, this.auditData);
    }

    pickModalOpen(data) {
        this.visibleQuotaDetails = true;

        let name;
        let entityID;
        if (this.isStatusChangeSubMenu) {
            name = "TERMINATION";
            entityID = data.customerID;
        } else if (this.customerUpdateDiscount) {
            name = "CUSTOMER_DISCOUNT";
            entityID = data.id;
        } else if (this.ifUpdateAddress) {
            name = "SHIFT_LOCATION";
            entityID = data.id;
        } else {
            name = "CUSTOMER_INVENTORY_ASSIGN";
            entityID = data.id;
        }
        let url = "/workflow/pickupworkflow?eventName=" + name + "&entityId=" + entityID;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (this.isStatusChangeSubMenu) {
                    this.getapproveStatusList("");
                } else if (this.customerUpdateDiscount) {
                    this.openCustorUpdateDiscount(this.customerLedgerDetailData.id);
                } else if (this.ifUpdateAddress) {
                    this.openCustomerAddress();
                    this.getNewCustomerAddressForCustomer(data.customerId);
                } else {
                    this.getCustomerAssignedList(this.assignInventoryCustomerId);
                }

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');


                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    openPaymentModal(id) {
        this.searchData.filters[0].filterValue = "";
        this.searchData.filters[0].filterColumn = "";
        this.searchData.page = "";
        this.searchData.pageSize = "";

        let url = "/getChequeDetail/" + id;
        this.searchPaymentService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.chequeDetail = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    saveRemark() {
        let custPlanMappingIdList = [];
        if (this.promiseToPayBulkFlag) {
            this.promiseToPayBulkId.forEach(element => {
                custPlanMappingIdList.push({ custPlanMapping: element });
            });
        } else {
            this.promiseToPayIds.forEach(element => {
                custPlanMappingIdList.push({ custPlanMapping: element });
            });
        }

        let promiseToPayData: any = {
            custId: this.customerLedgerDetailData.id,
            promiseToPay: custPlanMappingIdList,
            promise_to_pay_remarks: this.remark
        };
        const url = `/subscriber/promiseToPayInBulk`;

        this.customerManagementService.postMethod(url, promiseToPayData).subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    this.toastr.success(`${res.message}`, 'Successfully!');

                    // $("#IdRemark").modal("hide");
                    this.getcustCurrentPlan(this.customerLedgerDetailData.id, "");
                    this.getcustFuturePlan(this.customerLedgerDetailData.id, "");
                    this.getcustExpiredPlan(this.customerLedgerDetailData.id, "");
                    this.remark = "";
                    this.onCloseValidity();
                    this.extendValidityBulkFlag = false;
                    this.promiseToPayBulkFlag = false;
                    this.extendValidityBulkId = [];
                    this.promiseToPayBulkId = [];

                    this.remark = "";
                } else {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');

                }
                this.onClose();
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    checkIfChildCustomer(plan) {
        return this.customerLedgerDetailData.parentCustomerId !== null && plan.invoiceType == "Group";
    }
    extendValiditysubmitted: boolean = false;
    extendValidityConfirmation() {
        this.extendValiditysubmitted = true;
        if (this.extendValidityForm.valid) {
            if (
                this.custPlanMappingForValidity.isChildExists &&
                this.custPlanMappingForValidity.isChildExists === true
            ) {
                this.confirmationService.confirm({
                    message: "Do you want to extend validity for child customer?",
                    header: "Extend Validity Confirmation",
                    icon: "pi pi-info-circle",
                    accept: () => {
                        this.extendValidity(true);
                    },
                    reject: () => {
                        this.extendValidity(false);
                    }
                });
            } else {
                this.extendValidity(false);
            }
        }
    }

    extendValidity(isExtentionForChild) {
        let url;
        let extendValidity;
        if (this.extendValidityForm.valid) {
            if (this.extendValidityBulkFlag) {
                let extendData = [];
                let extendValidityData: any;
                let idList = this.extendValidityBulkId;
                idList.forEach(element => {
                    extendValidityData = {
                        custPlanMapppingId: element,
                        extentionforChild: isExtentionForChild
                    };
                    Object.assign(extendValidityData, this.extendValidityForm.value);
                    if (this.planGroupFlag) {
                        Object.assign(extendValidityData, {
                            planGroup: this.planGroupFlag,
                            planGroupId: this.extendPlangroupId
                        });
                    }
                    extendData.push(extendValidityData);
                    extendValidity = { extendPlanValidity: extendData };
                });
            } else {
                let extendPlanValidity = [];
                let extendValidityData: any;
                extendValidityData = {
                    custPlanMapppingId: this.custPlanMappingForValidity.custPlanMapppingId,
                    extentionforChild: isExtentionForChild
                };
                Object.assign(extendValidityData, this.extendValidityForm.value);
                if (this.planGroupFlag) {
                    Object.assign(extendValidityData, {
                        planGroup: this.planGroupFlag,
                        planGroupId: this.extendPlangroupId
                    });
                }
                extendPlanValidity.push(extendValidityData);
                extendValidity = {
                    extendPlanValidity: extendPlanValidity
                };
            }
            url = `/subscriber/extendPlanValidityInBulk`;

            this.customerManagementService.postMethod(url, extendValidity).subscribe(
                (res: any) => {
                    if (res.responseCode == 417) {
                        this.toastr.info(`${res.responseMessage}`, 'Info!');


                    } else {
                        this.toastr.success(`${res.responseMessage}`, 'Successfully!');

                    }
                    // $("#IdRemark").modal("hide");
                    this.getcustCurrentPlan(this.customerLedgerDetailData.id, "");
                    this.getcustFuturePlan(this.customerLedgerDetailData.id, "");
                    this.getcustExpiredPlan(this.customerLedgerDetailData.id, "");
                    this.remark = "";
                    this.onCloseValidity();
                    this.extendValidityBulkFlag = false;
                    this.promiseToPayBulkFlag = false;
                    this.extendValidityBulkId = [];
                    this.promiseToPayBulkId = [];
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');


                }
            );
        }
    }
    isnewDiscount: boolean = true;

    changeValue(value) {
        if (!value.dirty) {
            this.isnewDiscount = false;
            let msg = "value required";
        }
    }

    isExtendInCurrentPlan(custPlanMapppingId, planExpiryDate) {
        if (this.custFuturePlanList != null && this.custFuturePlanList.length > 0) {
            if (this.custCurrentPlanList.length == 1) {
                return (
                    this.custFuturePlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                        .length == 0
                );
            }
            return (
                this.custFuturePlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                    .length == 0 &&
                this.custCurrentPlanList.filter(
                    item =>
                        item.custPlanMapppingId == custPlanMapppingId &&
                        moment(item.expiryDate, "DD/MM/YYYY").toDate() >
                        moment(planExpiryDate, "DD/MM/YYYY").toDate()
                ).length == 0
            );
        }
        return (
            this.custCurrentPlanList.filter(item => {
                item.custPlanMapppingId == custPlanMapppingId &&
                    moment(item.expiryDate, "DD/MM/YYYY").toDate() >
                    moment(planExpiryDate, "DD/MM/YYYY").toDate();
            }).length == 0
        );
    }

    isLatestFuturePlan(custPlanMapppingId, planExpiryDate) {
        if (this.custFuturePlanList != null && this.custFuturePlanList.length > 0) {
            return (
                this.custFuturePlanList.filter(
                    item =>
                        item.custPlanMapppingId == custPlanMapppingId &&
                        moment(item.expiryDate, "DD/MM/YYYY").toDate() >
                        moment(planExpiryDate, "DD/MM/YYYY").toDate()
                ).length == 0
            );
        }
        return false;
    }

    isExtendInExpiredPlan(custPlanMapppingId, planExpiryDate) {
        if (this.custCurrentPlanList != null && this.custCurrentPlanList.length > 0) {
            if (this.custExpiredPlanList.length == 1) {
                return (
                    this.custCurrentPlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                        .length == 0 &&
                    this.custFuturePlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                        .length == 0
                );
            }
            return (
                this.custCurrentPlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                    .length == 0 &&
                this.custFuturePlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                    .length == 0 &&
                this.custExpiredPlanList.filter(
                    item =>
                        item.custPlanMapppingId == custPlanMapppingId &&
                        moment(item.expiryDate, "DD/MM/YYYY").toDate() >
                        moment(planExpiryDate, "DD/MM/YYYY").toDate()
                ).length == 0
            );
        }
        return (
            this.custExpiredPlanList.filter(
                item =>
                    item.custPlanMapppingId == custPlanMapppingId &&
                    moment(item.expiryDate, "DD/MM/YYYY").toDate() >
                    moment(planExpiryDate, "DD/MM/YYYY").toDate()
            ).length == 0 &&
            this.custFuturePlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                .length == 0
        );
        return false;
    }

    isPromiseToPayInExpired(custPlanMapppingId) {
        if (
            (this.custFuturePlanList != null && this.custFuturePlanList.length > 0) ||
            (this.custCurrentPlanList != null && this.custCurrentPlanList.length > 0)
        ) {
            return (
                this.custFuturePlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                    .length == 0 &&
                this.custCurrentPlanList.filter(item => item.custPlanMapppingId == custPlanMapppingId)
                    .length == 0
            );
        }
        return false;
    }

    reassignWorkflow() {
        let url: any;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.custCustDiscountList.id}&eventName=CUSTOMER_DISCOUNT&assignToStaffId=${this.selectStaff}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                $("#reasignPlanGroup").modal("hide");
                this.getCustomer();

                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.getCustomer();
                    this.toastr.success(`${response.responseMessage}`, 'Assigned to the next staff successfully!');

                }
            },
            error => {

                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    StaffReasignList(id) {
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${id}&eventName=CUSTOMER_DISCOUNT`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {

                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }
                if (response.dataList != null) {
                    this.staffDataList = response.dataList;
                    this.approved = true;
                    $("#reasignPlanGroup").modal("show");
                } else {
                    $("#reasignPlanGroup").modal("hide");
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    // This method is used to check that customer has any service/plan with invoice type Independent or not
    // returns 'true' if customer has any service/plan with invoice type Independent otherwise returns 'false'
    hasCustInvoiceTypeIndependent() {
        return (
            this.customerLedgerDetailData.planMappingList.filter(
                item => item.invoiceType === "Independent"
            ).length > 0
        );
    }

    approvableStaff: any = [];
    assignedShiftLocationid: any;
    StaffReasignListShiftLocation(data) {
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.id}&eventName=SHIFT_LOCATION`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedShiftLocationid = data.id;
                this.approvableStaff = [];
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                }
                if (response.dataList != null) {
                    this.getCustomer();
                    this.approvableStaff = response.dataList;
                    this.approved = true;
                    $("#reAssignPLANModal").modal("show");
                } else {
                    $("#reAssignPLANModal").modal("hide");
                }

            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    reassignWorkflowShiftLocation() {
        let url: any;
        this.remark = this.shiftlocationFormRemark.value.remark;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.assignedShiftLocationid}&eventName=SHIFT_LOCATION&assignToStaffId=${this.selectStaff}&remark=${this.remark}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                $("#reAssignSHIFTLOCATIONModal").modal("hide");
                this.getcustomerList("");
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.getcustomerList("");
                    this.toastr.success(`${response.responseMessage}`, 'Assigned to the next staff successfully!');

                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    reassignTerminationWorkflow() {
        this.remark = this.assignTerminationForm.value.remark;
        let url: any;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.customerLedgerDetailData.id}&eventName=TERMINATION&assignToStaffId=${this.selectStaff}&remark=${this.remark}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                $("#customerTermination").modal("hide");
                this.getapproveStatusList("");
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.getCustomer;
                    this.toastr.success(`${response.responseMessage}`, 'Assigned to the next staff successfully!');

                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    StaffReasignListForTermination(id) {
        this.remark = this.assignTerminationForm.value.remark;
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${id}&eventName=TERMINATION&remark=${this.remark}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }
                if (response.dataList != null) {
                    this.staffDataList = response.dataList;
                    this.approved = true;
                    $("#customerTermination").modal("show");
                } else {
                    $("#customerTermination").modal("hide");
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    // Function used to check selected service has invoice type is null or Independent. This will used where we need to check
    // Invoice type based on service.
    isInvoiceTypeIndependentOrNullForService() {
        if (this.selectedCustService != null) {
            if (
                this.selectedCustService.invoiceType == null ||
                this.selectedCustService.invoiceType == ""
            ) {
                return true;
            } else {
                return this.selectedCustService.invoiceType === "Independent";
            }
        }
        return false;
    }
    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
        if (this.trailbtnTypeSelect == "extend") {
            if (Number(this.ExtendDays) > Number(this.commondropdownService.trialPLanMaxLength)) {
                this.ExtendDays = 0;

                (error: any) => {

                    this.toastr.info(`${error.error.ERROR}`, '"Maximmum value is " + this.commondropdownService.trialPLanMaxLength!');
                }


            }
        }
    }

    closeChangeStatus() {
        this.remark = "";
        this.updatedStatus = "";
        $("#changeStatusModal").modal("hide");
    }

    extendValidityForPlanBundle(plan) {
        this.custPlanMappingForValidity = plan;
        this.totalDays = 0;
        this.custPlanMappingEndDate = moment(plan.endDate, "DD/MM/YYYY").toDate();
        this.custPlanMappingStartDate = moment(plan.startDate, "DD/MM/YYYY").toDate();
        if (plan.plangroupid === null) {
            $("#extendValidity").modal("show");
        } else {
            this.confirmationService.confirm({
                message: "Do you want to Extend all services ?",
                header: "Extend Service Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.planGroupFlag = true;
                    this.extendPlangroupId = plan.plangroupid ? plan.plangroupid : 0;
                    $("#extendValidity").modal("show");
                },
                reject: () => {
                    this.planGroupFlag = false;
                    (error: any) => {

                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    chekcPlanGroup(plan, planList) {
        if (plan.plangroupid !== null) {
            let groupPlanList = planList.filter(item => item.plangroupid == plan.plangroupid);
            return groupPlanList[0] === plan;
        }
        return true;
    }

    openShiftLocationForm() {
        $("#openAddressForm").modal("show");
        this.getNetworkDevicesByType("OLT");
        this.shiftLocationChargeGroupForm.reset();
    }

    onAddShiftLocationCharge() {
        this.submitted = true;
        if (this.shiftLocationChargeGroupForm.valid) {
            if (
                this.shiftLocationChargeGroupForm.value.price >=
                this.shiftLocationChargeGroupForm.value.actualprice
            ) {
                // this.overChargeListFromArray.push(this.createoverChargeListFormGroup());
                this.shiftLocationChargeGroupForm.reset();
                this.submitted = false;
                this.selectchargeValueShow = false;
            }
        } else {
        }
    }
    extendValidityBulkFlag: boolean = false;
    promiseToPayBulkFlag: boolean = false;
    extendValidityBulkId = [];
    promiseToPayBulkId = [];
    extendValidityBulk(plan, e) {
        if (e.checked) {
            this.extendValidityBulkFlag = true;
            this.extendValidityBulkId.push(plan.custPlanMapppingId);
            this.planGroupFlag = plan.plangroupid != null;
            this.custPlanMappingForValidity = plan;
            this.extendPlangroupId = plan.plangroupid ? plan.plangroupid : 0;
            this.custPlanMappingEndDate = moment(plan.endDate, "DD/MM/YYYY").toDate();
            this.custPlanMappingStartDate = moment(plan.startDate, "DD/MM/YYYY").toDate();
            this.custPlanMappingEndDateArray.push(moment(plan.endDate, "DD/MM/YYYY").toDate());
            this.custPlanMappingStartDateArray.push(moment(plan.startDate, "DD/MM/YYYY").toDate());
        } else {
            const index = this.extendValidityBulkId.indexOf(plan.custPlanMapppingId);
            if (index > -1) this.extendValidityBulkId.splice(index, 1);
            if (this.extendValidityBulkId.length <= 0) this.extendValidityBulkFlag = false;
        }
    }

    onPromiseToPay(planmapid) {
        this.promiseToPayIds = [];
        this.promiseToPayIds.push(planmapid);
    }
    promiseToPayBulk(id, e) {
        if (e.checked) {
            this.promiseToPayBulkFlag = true;
            this.promiseToPayBulkId.push(id);
        } else {
            this.promiseToPayBulkFlag = false;
            const index = this.promiseToPayBulkId.indexOf(id);
            if (index > -1) this.promiseToPayBulkId.splice(index, 1);
        }
    }
    totalDays: any = 0;
    getTotalDays() {
        var date1 = new Date(this.extendValidityForm.value.downStartDate);
        var date2 = new Date(this.extendValidityForm.value.downEndDate);
        var Difference_In_Time = date2.getTime() - date1.getTime();
        var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24);
        this.totalDays = Difference_In_Days + 1;
    }

    extendValidityForBulk() {
        //min date
        this.custPlanMappingStartDate = this.custPlanMappingStartDateArray.reduce(function (a, b) {
            return a < b ? a : b;
        });
        //min date
        this.custPlanMappingEndDate = this.custPlanMappingEndDateArray.reduce(function (a, b) {
            return a > b ? a : b;
        });

        this.confirmationService.confirm({
            message: "Do you want to Extend all services ?",
            header: "Extend Service Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                $("#extendValidity").modal("show");
            },
            reject: () => {
                this.planGroupFlag = false;
                (error: any) => {

                    this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                }

            }
        });
    }
    openDunningDetails(id) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.assignInventoryWithSerial = false;
        this.ifShowDBRReport = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = true;
        this.ifNotificationDetailsShow = false;
        // this.getCustomerTeamHierarchy(id);
        this.workflowID = id;
        // this.getworkflowAuditDetails("", id, "CAF");
        this.GetDunningData(id, "");
    }
    custID: number;
    GetDunningData(custId, size) {
        let page = this.currentPageDunningSlab1;
        this.custID = custId;
        let page_list;
        if (size) {
            page_list = size;
            this.DunningitemsPerPage1 = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.DunningitemsPerPage1 = 5;
            } else {
                this.DunningitemsPerPage1 = 5;
            }
        }
        this.DunningData = [];

        let data = {
            filters: [
                {
                    filterColumn: "customer",
                    filterValue: custId
                }
            ],

            page: page,
            pageSize: this.DunningitemsPerPage1
        };
        const url = "/dunnninghistory/findByPartnerOrCustomerId";
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.DunningData = response.customerDunningHistory.content;
                this.DunningtotalRecords1 = response.customerDunningHistory.totalElements;
                //this.auditList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    changeStatustest: string;

    async getDunningDisableEnable(id, status) {
        if (status == true) {
            this.changeStatustest = "false";
        }
        if (status == false) {
            this.changeStatustest = "true";
        }
        const url =
            "/customer/changedunningenabalestatus?custId=" +
            id +
            "&dunningStatus=" +
            this.changeStatustest;
        this.changeStatustest = "";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.toastr.success(`${response.msg}`, 'Successfully!');

            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
        setTimeout(async () => {
            await this.getCustomersDetail(id);
        }, 1000);
        setTimeout(async () => {
            await this.getCustomerNetworkLocationDetail(id);
        }, 1000);
    }
    pageChangedMasterDunnningList(pageNumber) {
        this.currentPageDunningSlab1 = pageNumber;
        this.GetDunningData(this.custID, "");
    }

    TotalItemPerPageDunningHistory(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageDunningSlab1 > 1) {
            this.currentPageDunningSlab1 = 1;
        }
        this.GetDunningData(this.custID, this.showItemPerPage);
    }

    openNotificationDetails(username) {
        this.listView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerTicketView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifChargeGetData = false;
        this.isServiceOpen = false;
        this.isCreditNoteOpen = false;
        this.isStatusChangeSubMenu = false;
        this.customerUpdateDiscount = false;
        this.customerChildsView = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.assignInventoryWithSerial = false;
        this.ifShowDBRReport = false;
        this.shiftLocationEvent = false;
        this.ifWorkflowAuditShow = false;
        this.ifAuditDetailsShow = false;
        this.ifDunningDetailsShow = false;
        this.ifNotificationDetailsShow = true;

        // this.getCustomerTeamHierarchy(id);

        // this.getworkflowAuditDetails("", id, "CAF");
        this.GetNotificationData(username, "");
    }
    notificationusername: string;
    GetNotificationData(notificationusername, size) {
        let page = this.currentPageNotificationSlab1;
        this.notificationusername = notificationusername;
        let page_list;
        if (size) {
            page_list = size;
            this.NotificationitemsPerPage1 = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.NotificationitemsPerPage1 = 5;
            } else {
                this.NotificationitemsPerPage1 = 5;
            }
        }
        this.NotificationData = [];

        let data = {
            filters: [
                {
                    filterColumn: "customer",
                    filterValue: notificationusername
                }
            ],

            page: page,
            pageSize: this.NotificationitemsPerPage1
        };
        const url = "/findByCustomerUsername";
        this.customerManagementService.notidicationpostMethod(url, data).subscribe(
            (response: any) => {
                this.NotificationData = response.customerNotificationHistory.content;
                this.NotificationtotalRecords1 = response.customerNotificationHistory.totalElements;
                //this.auditList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    async getNotificationDisableEnable(id, status, e) {
        e.preventDefault();
        if (status == null) {
            this.changeStatustest == "true";
        }
        if (status == true) {
            this.changeStatustest = "false";
        }
        if (status == false) {
            this.changeStatustest = "true";
        }
        const url =
            "/customer/changenotificationenabalestatus?custId=" +
            id +
            "&notificationStatus=" +
            this.changeStatustest;
        this.changeStatustest = "";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.toastr.success(`${response.msg}`, 'Success!');


            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
        setTimeout(async () => {
            await this.getCustomersDetail(id);
        }, 1000);
        setTimeout(async () => {
            await this.getCustomerNetworkLocationDetail(id);
        }, 1000);
    }
    pageChangedMasterNotificationList(pageNumber) {
        this.currentPageNotificationSlab1 = pageNumber;
        this.GetNotificationData(this.notificationusername, "");
    }

    TotalItemPerPageNotificationHistory(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageNotificationSlab1 > 1) {
            this.currentPageNotificationSlab1 = 1;
        }
        this.GetNotificationData(this.notificationusername, this.showItemPerPage);
    }

    branchByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/branchManagement/getAllBranchesByServiceAreaId";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            this.branchData = response.dataList;
            if (this.branchData != null && this.branchData.length > 0) {
                this.isBranchShiftLocation = true;
                // this.isBranchAvailable = true;
            } else {
                this.isBranchShiftLocation = false;
                // this.isBranchAvailable = false;
            }
        });
    }

    branchChange(event: any) {
        this.requestedByID = event.value;
    }

    searchPrepaidValue() {
        this.prepaid = "";
        this.prepaidValue = 0;
        const now = new Date();
        let firstDay;
        let lastDay;
        firstDay = this.datepipe.transform(now, "yyyy-MM-dd");
        lastDay = this.datepipe.transform(new Date(now.setDate(now.getDate() + 1)), "yyyy-MM-dd");
        const url =
            "/getCustomer?custid=" +
            this.customerLedgerDetailData.id +
            "&startdate=" +
            firstDay +
            "&endate=" +
            firstDay;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.dbrListData = response.customerDBRPojos;
                this.dbrListData.forEach(dbr => {
                    var DBRDate = moment(dbr.month, "DD/MM/YYYY").toDate();
                    var today = moment(new Date(), "DD/MM/YYYY").toDate();
                    if (moment(DBRDate.setHours(0, 0, 0, 0)).isSame(moment(today.setHours(0, 0, 0, 0)))) {
                        this.prepaidValue = this.prepaidValue + dbr.pendingamt;
                    }
                });
                this.prepaid = this.prepaidValue.toFixed(2);
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    findDuration(expiryDate: Date) {
        // var datePipe = new DatePipe();
        var start = moment(new Date(new Date().setHours(0, 0, 0, 0)), "DD/MM/YYYY"); //todays date
        var end = moment(new Date(expiryDate), "DD/MM/YYYY"); // another date
        var duration = moment.duration(end.diff(start));

        var days = duration.asDays();
        return Math.trunc(days);
    }
    findPlanDuration(expiryDate) {
        var endDate = moment(expiryDate, "DD/MM/YYYY hh:mm A").toDate();
        var start = moment(new Date(new Date().setHours(0, 0, 0, 0)), "DD/MM/YYYY"); //todays date
        var end = moment(new Date(endDate), "DD/MM/YYYY"); // another date
        var duration = moment.duration(end.diff(start));
        var days = duration.asDays();
        return Math.trunc(days);
    }

    getSerialNumber(plan) {
        return plan.customerInventorySerialnumberDtos.filter(item => item.primary).length > 0
            ? plan.customerInventorySerialnumberDtos.filter(item => item.primary)[0].serialNumber
            : "";
    }

    openPlanConnectionModal(plan) {
        this.planForConnection = plan;
        this.showPlanConnectionNo = true;
    }
    closeDialog() {
        this.planForConnection = null;
        this.showPlanConnectionNo = false;
    }
    parentExperienceSelect(e) {

        this.planGroupForm.value.invoiceType = "Group";
    }

    onSelectedInvoice(event, data, isTDS, isABBS) {
        if (event > 0) {
            this.isSelectedInvoice = false;
            if (isTDS) {
                data.tdsCheck = ((data.totalamount * this.tdsPercent) / 100).toFixed(2);
            }
            if (isABBS) {
                data.abbsCheck = ((data.totalamount * this.abbsPercent) / 100).toFixed(2);
            }
        } else {
            data.includeTds = false;
            data.includeAbbs = false;
            data.tdsCheck = 0;
            data.abbsCheck = 0;
        }
    }

    onChangeOFTDSTest(event, data) {
        if (event && data.totalamount) {
            data.tdsCheck = ((data.totalamount * this.tdsPercent) / 100).toFixed(2);
        } else {
            data.tdsCheck = 0;
        }
    }

    onChangeOFABBSTest(event, data) {
        if (event && data.totalamount) {
            data.abbsCheck = ((data.totalamount * this.abbsPercent) / 100).toFixed(2);
        } else {
            data.abbsCheck = 0;
        }
    }

    onChangeOFAmountTest(event) {
        if (this.checkedList.length >= 1) {
            let formPayModeValue = this.paymentFormGroup.controls.paymode.value.toLowerCase();
            let isAbbsTdsMode = this.checkPaymentMode(formPayModeValue);
            let totaltdsAmount = 0;
            let totalabbsAmount = 0;
            this.checkedList.forEach(element => {
                let tds = 0;
                let abbs = 0;
                if (element.includeTds) {
                    if (element.includeTds === true) {
                        tds = Number(element.tdsCheck);
                        totaltdsAmount = Number(element.tdsCheck) + Number(totaltdsAmount);
                        this.isTdsFlag = true;
                    }
                }
                if (element.includeAbbs) {
                    if (element.includeAbbs === true) {
                        abbs = Number(element.abbsCheck);
                        totalabbsAmount = Number(element.abbsCheck) + Number(totalabbsAmount);
                        this.isAbbsFlag = true;
                    }
                }
                if (isAbbsTdsMode) {
                    element.tds = 0;
                    element.abbs = 0;
                } else {
                    element.tds = tds;
                    element.abbs = abbs;
                }
            });
            const tdsAmount = totaltdsAmount;
            const abbsAmount = totalabbsAmount;

            if (isAbbsTdsMode) {
                this.paymentFormGroup.controls.abbsAmount.setValue(0);
                this.paymentFormGroup.controls.tdsAmount.setValue(0);
            } else {
                if (this.isAbbsFlag) {
                    this.paymentFormGroup.controls.abbsAmount.setValue(abbsAmount);
                }
                if (this.isTdsFlag) {
                    this.paymentFormGroup.controls.tdsAmount.setValue(tdsAmount);
                }
            }
        }
    }

    checkPaymentMode(formPayModeValue) {
        if (
            formPayModeValue == "vatreceiveable" ||
            formPayModeValue == "tds" ||
            formPayModeValue == "abbs"
        ) {
            return true;
        } else {
            return false;
        }
    }

    closeShiftLocation() {
        this.submit = false;
        this.shiftLocationChargeGroupForm.reset();
        this.ifUpdateAddressSubmited = false;
        this.requestedByID = 0;
        this.branchID = 0;
        $("#openAddressForm").modal("hide");
    }

    changeTrialCheck() {
        if (
            this.payMappingListFromArray.value != null &&
            this.payMappingListFromArray.value.length > 0 &&
            this.planGroupForm.value.service != null &&
            this.planGroupForm.value.service != ""
        ) {
            var isCheckingDone = false;
            this.payMappingListFromArray.value.forEach(element => {
                if (!isCheckingDone) {
                    if (element.service == this.planGroupForm.value.service && element.istrialplan) {
                        this.isTrialCheckDisable = true;
                        isCheckingDone = true;
                    } else this.isTrialCheckDisable = false;
                }
            });
        } else {
            this.isTrialCheckDisable = false;
        }

        return false;
    }
    badgeTypeForStatus: any;
    displayStatus: any;
    changePlanTypeSelection: any;
    newPlanSelection: any;
    newPlanData: any = [];
    checkStatus(planStatus, workflowStatus) {
        let status = planStatus.toLowerCase();
        let statusWorkflow = workflowStatus ? workflowStatus.toLowerCase() : "";

        if (statusWorkflow == "new activation" || statusWorkflow == "rejected") {
            if (statusWorkflow == "new activation") this.badgeTypeForStatus = "green";
            else this.badgeTypeForStatus == "red";
            this.displayStatus = workflowStatus.toUpperCase();
        } else {
            this.displayStatus = planStatus.toUpperCase();
            switch (status) {
                case "active":
                case "ingrace":
                    this.badgeTypeForStatus = "green";
                    break;
                case "terminate":
                case "stop":
                case "inactive":
                case "expired":
                    this.badgeTypeForStatus = "red";
                    break;
                case "hold":
                case "disable":
                    this.badgeTypeForStatus = "grey";
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    quotaPlanDetailsModel(modelID, custid, PlanData) {
        this.PaymentamountService.show(modelID);
        this.PlanQuota.next({
            custid,
            PlanData
        });
    }

    closeInvoiceModel() {
        this.invoiceList = [];
        $("#selectInvoice").modal("hide");
        this.masterSelected = false;
    }

    getPendingAmount(item) {
        var amount = 0;
        if (item.pendingAmt && item.adjustedAmount) {
            amount = item.totalamount - item.adjustedAmount - item.pendingAmt;
        } else if (item.pendingAmt) {
            amount = item.totalamount - item.pendingAmt;
        } else if (item.adjustedAmount) {
            amount = item.totalamount - item.adjustedAmount;
        } else {
            amount = item.totalamount;
        }
        if (amount) return amount.toFixed(2);
        else return 0;
    }

    getrequiredDepartment() {
        const url = "/department/all";
        this.countryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.departmentListData = res.departmentList;
                this.departmenttotalRecords = res.pageDetails.totalRecords;

                this.searchkey = "";
            },
            (err: any) => {
                this.toastr.error(`${err.error.ERROR}`, 'Something went wrong while fetching lead origin types!');


            }
        );
    }

    async modalOpenStaff() {
        $("#selectStaff").modal("show");
        await this.getStaffDetailById();
        this.newFirst = 1;
        this.selectedStaffCust = [];
        //  console.log("this.newFirst2", this.newFirst)
    }
    saveSelstaff() {
        this.staffCustList = [
            {
                id: Number(this.selectedStaffCust.id),
                name: this.selectedStaffCust.firstname
            }
        ];
        this.changePlanForm.patchValue({
            paymentOwnerId: this.selectedStaffCust.id
        });
        this.changePlanNewForm.patchValue({
            paymentOwnerId: this.selectedStaffCust.id
        });
        this.paymentOwnerId = this.selectedStaffCust.id;
        this.modalCloseStaff();
    }
    modalCloseStaff() {
        $("#selectStaff").modal("hide");
        this.currentPageParentStaffListdata = 1;
        this.newFirst = 1;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        // console.log("this.newFirst1", this.newFirst)
    }
    removeSelStaff() {
        this.staffCustList = [];
        this.staffid = null;
    }
    paginateStaff(event) {
        this.currentPageParentStaffListdata = event.page + 1;
        // this.first = event.first;
        if (this.searchParentCustValue) {
            this.searchStaffByName();
        } else {
            this.getStaffDetailById();
        }
    }
    searchStaffByName() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            let currentPageForStaff;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage == 1) {
            this.currentPageParentStaffListdata = this.pageITEM;
        } else {
            this.currentPageParentStaffListdata = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchDeatil.trim();
        this.staffService.staffSearch(this.searchData).subscribe(
            (response: any) => {
                //
                this.staffData = response.dataList;
                this.parentstaffListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.parentstaffListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');


                    this.staffData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }
    clearSearchForm() {
        this.searchDeatil = "";

        this.currentPageParentStaffListdata = 1;
    }

    //paymentFlagToggle
    paymentFlagToggle(e) {
        if (e.target.checked) {
            this.changePlanNewForm.controls["paymentOwnerId"].setValidators(Validators.required);
        } else {
            this.changePlanNewForm.controls["paymentOwnerId"].clearValidators();
        }
        this.changePlanNewForm.controls["paymentOwnerId"].updateValueAndValidity();

    }
    resetForm() {
        this.changePlansubmitted = false;
        this.changePlanTypeSelection = null;
        this.changePlanNewForm.reset();
        this.changePlanNewForm.patchValue({
            isPaymentReceived: "false"
        });
        this.custServiceData.forEach(element => {
            element.changeFlag = false;
            element.newPlanSelection = null;
        });
    }
    resetFormType() {

        this.changePlansubmitted = false;
        this.changePlanNewForm.reset();
        this.changePlanNewForm.patchValue({
            isPaymentReceived: "false"
        });
        this.custServiceData.forEach(element => {
            element.changeFlag = false;
            element.newPlanSelection = null;
        });
        this.childPlanList.forEach(element => {
            element.serviceMappingData.forEach(e => {
                element.changeFlag = false;
                element.newPlanSelection = null;
            });
        });
        this.childPlanGroup.forEach(element => {
            element.serviceMappingData.forEach(e => {
                element.changeFlag = false;
                element.newPlanSelection = null;
            });
        });
    }
    changePlanSelection(e, data, i) {
        if (e.checked) {
            let url = "/getPlansByFilters";
            data = {
                changePlanType: this.changePlanTypeSelection.toLowerCase(),
                custId: this.customerLedgerDetailData.id,
                serviceId: data.serviceId,
                customerServiceMappingID: data.customerServiceMappingId
            };
            this.customerManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.newPlanData[i] = response;
                    this.newPlanData[i].forEach(e => {
                        if (e.quotatype == "Data") {
                            e.label =
                                e.name +
                                ` (${data.is_qosv ? e.quota + " " + e.quotaUnit : ""}
              ${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval + " - "}${e.validity
                                } ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
                        } else if (e.quotatype == "Time") {
                            e.label =
                                e.name +
                                ` (${e.quotatime} ${e.quotaunittime}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval + " - "
                                }${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""
                                })`;
                        } else if (e.quotatype == "Both") {
                            e.label =
                                e.name +
                                ` (${data.is_qosv ? e.quota + " " + e.quotaUnit : ""}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval + " and "
                                }${e.quotatime} ${e.quotaunittime}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval
                                }  - ${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""
                                })`;
                        } else {
                            e.label = e.name;
                        }
                    });
                },
                (error: any) => {
                    console.log(error);
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else {
            this.custServiceData[i].changeFlag = false;
            this.custServiceData[i].newPlanSelection = null;
        }
    }
    currentIndex: number;
    selectNewPlan(i) {
        this.currentIndex = i;
        this.getPlanDetailById(this.custServiceData[i].newPlanSelection);
    }
    changePlanBulk() {
        this.changePlansubmitted = true;
        let planFlag = this.custServiceData.some(e => e.newPlanSelection);
        if (this.changePlanNewForm.valid && planFlag) {
            if (this.changePlanTypeSelection == "Renew") {
                let changePlanRequestDTOList = [];
                this.custServiceData.forEach(element => {
                    if (element.changeFlag) {
                        let renewData = {
                            connectionNo: element.connectionNo,
                            serviceName: element.serviceName,
                            serviceNickName: element.serviceName,
                            purchaseType: "Renew",
                            planId: element.newPlanSelection,
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            remarks: this.changePlanNewForm.value.remarks,
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            addonStartDate: null,
                            ChangePlanCategory: "",
                            isAdvRenewal: false,
                            custId: this.customerLedgerDetailData.id,
                            recordPaymentDTO: {},
                            isRefund: false,
                            discount: element.newDiscount,
                            planBindWithOldPlans: [],
                            newPlanList: null,
                            planMappingList: null,
                            custServiceMappingId: element.customerServiceMappingId,
                            isParent: this.customerLedgerDetailData.parentCustomerId ? true : false
                        };
                        changePlanRequestDTOList.push(renewData);
                    }
                });
                this.childPlanList.forEach(e => {
                    e.serviceMappingData.forEach(element => {
                        if (element.changeFlag) {
                            let renewData = {
                                connectionNo: element.connectionNo,
                                serviceName: element.serviceName,
                                serviceNickName: element.serviceName,
                                purchaseType: "Renew",
                                planId: element.newPlanSelection,
                                isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                                remarks: this.changePlanNewForm.value.remarks,
                                paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                                billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                                addonStartDate: null,
                                ChangePlanCategory: "",
                                isAdvRenewal: false,
                                custId: e.id,
                                recordPaymentDTO: {},
                                isRefund: false,
                                discount: element.newDiscount,
                                planBindWithOldPlans: [],
                                newPlanList: null,
                                planMappingList: null,
                                custServiceMappingId: element.customerServiceMappingId,
                                isParent: false
                            };
                            changePlanRequestDTOList.push(renewData);
                        }
                    });
                });
                let url = "/subscriber/changePlan01";
                let finalRenewData = { changePlanRequestDTOList: changePlanRequestDTOList };
                this.customerManagementService.postMethod(url, finalRenewData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 417) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');


                        }
                        this.resetForm();
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                    }
                );
            } else if (this.changePlanTypeSelection == "Addon") {
                let changePlanRequestDTOList = [];
                this.custServiceData.forEach(element => {
                    if (element.changeFlag) {
                        let renewData = {
                            connectionNo: element.connectionNo,
                            serviceName: element.serviceName,
                            serviceNickName: element.serviceName,
                            purchaseType: "Addon",
                            planId: element.newPlanSelection,
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            remarks: this.changePlanNewForm.value.remarks,
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            addonStartDate: new Date(),
                            ChangePlanCategory: "",
                            isAdvRenewal: false,
                            custId: this.customerLedgerDetailData.id,
                            recordPaymentDTO: {},
                            isRefund: false,
                            discount: element.newDiscount,
                            planBindWithOldPlans: [],
                            newPlanList: null,
                            planMappingList: null,
                            custServiceMappingId: element.customerServiceMappingId,
                            isParent: this.customerLedgerDetailData.parentCustomerId ? true : false
                        };
                        changePlanRequestDTOList.push(renewData);
                    }
                });
                this.childPlanList.forEach(e => {
                    e.serviceMappingData.forEach(element => {
                        if (element.changeFlag) {
                            let renewData = {
                                connectionNo: element.connectionNo,
                                serviceName: element.serviceName,
                                serviceNickName: element.serviceName,
                                purchaseType: "Addon",
                                planId: element.newPlanSelection,
                                isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                                remarks: this.changePlanNewForm.value.remarks,
                                paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                                billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                                addonStartDate: new Date(),
                                ChangePlanCategory: "",
                                isAdvRenewal: false,
                                custId: e.id,
                                recordPaymentDTO: {},
                                isRefund: false,
                                discount: element.newDiscount,
                                planBindWithOldPlans: [],
                                newPlanList: null,
                                planMappingList: null,
                                custServiceMappingId: element.customerServiceMappingId,
                                isParent: false
                            };
                            changePlanRequestDTOList.push(renewData);
                        }
                    });
                });
                let url = "/subscriber/changePlan01";
                let finalRenewData = { changePlanRequestDTOList: changePlanRequestDTOList };
                this.customerManagementService.postMethod(url, finalRenewData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 417) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');


                        }
                        this.resetForm();
                    },
                    (error: any) => {
                        console.log(error);
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                let deactivatePlanReqModels = [];
                let deactivatePlanReqModelsChild = [];

                this.custServiceData.forEach(element => {
                    if (element.changeFlag) {
                        let changeDetails = {
                            newPlanGroupId: "",
                            newPlanId: element.newPlanSelection,
                            planGroupId: "",
                            planId: element.planmapid,
                            custServiceMappingId: element.customerServiceMappingId,
                            discount: element.newDiscount
                        };
                        deactivatePlanReqModels.push(changeDetails);
                    }
                });
                let data = {
                    deactivatePlanReqDTOS: [
                        {
                            custId: this.customerLedgerDetailData.id,
                            deactivatePlanReqModels: deactivatePlanReqModels,
                            planGroupChange: false,
                            planGroupFullyChanged: false,
                            paymentOwner: "Sharanya Iyer",
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            isParent: this.customerLedgerDetailData.parentCustomerId ? true : false,
                            remark: this.changePlanNewForm.value.remarks
                        }
                    ]
                };
                this.childPlanList.forEach(e => {
                    e.serviceMappingData.forEach(element => {
                        if (element.changeFlag) {
                            let changeDetails = {
                                newPlanGroupId: "",
                                newPlanId: element.newPlanSelection,
                                planGroupId: "",
                                planId: element.planmapid,
                                custServiceMappingId: element.customerServiceMappingId,
                                discount: element.newDiscount
                            };
                            deactivatePlanReqModelsChild.push(changeDetails);
                        }
                    });
                    let childPojo = {
                        custId: e.id,
                        deactivatePlanReqModels: deactivatePlanReqModelsChild,
                        planGroupChange: false,
                        planGroupFullyChanged: false,
                        paymentOwner: "Sharanya Iyer",
                        paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                        billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                        isParent: false,
                        remark: this.changePlanNewForm.value.remarks
                    };
                    data.deactivatePlanReqDTOS.push(childPojo);
                });
                let url = "/subscriber/deactivatePlanInBulk";
                this.customerManagementService.postMethod(url, data).subscribe(
                    (response: any) => {
                        if (response.responseCode == 417) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Successfully!');

                        }
                        this.resetForm();
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            }
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please Select All The Required Details!');

            }

        }
    }
    changePlanGroupBulk() {
        this.changePlansubmitted = true;
        let planFlag = this.custServiceData.some(e => e.newPlanSelection);

        if (this.changePlanNewForm.valid) {
            if (this.changePlanTypeSelection == "Renew") {
                let planBindWithOldPlans = [];
                let changePlanRequestDTOList = [];
                this.custServiceData.forEach(element => {
                    let data = {
                        newPlanId: element.newPlanSelection,
                        custServiceMappingId: element.customerServiceMappingId,
                        discount: 0
                    };
                    planBindWithOldPlans.push(data);
                });

                let pojo = {
                    purchaseType: "Renew",
                    planGroupId: this.newPlanGroupId,
                    isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                    remarks: this.changePlanNewForm.value.remarks,
                    paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                    billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                    addonStartDate: null,
                    ChangePlanCategory: "",
                    isAdvRenewal: false,
                    custId: this.customerLedgerDetailData.id,
                    recordPaymentDTO: {},
                    isRefund: false,
                    planBindWithOldPlans: planBindWithOldPlans,
                    newPlanList: null,
                    planMappingList: null,
                    isParent: this.customerLedgerDetailData.parentCustomerId ? true : false
                };
                // if (this.childPlanGroup.length > 0) {
                //   this.childPlanGroup.forEach(e => {
                //     if (e.serviceMappingData.length > 0) {
                //       e.serviceMappingData.forEach(element => {
                //         let planBindWithOldPlansChild: any;

                //         let data = {
                //           newPlanId: e.newPlanSelection,
                //           custServiceMappingId: e.customerServiceMappingId,
                //           discount: 0,
                //         };
                //         planBindWithOldPlansChild.push(data);

                //         let renewData = {
                //           purchaseType: "Renew",
                //           planGroupId: this.newPlanGroupId,
                //           isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                //           remarks: this.changePlanNewForm.value.remarks,
                //           paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                //           billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                //           addonStartDate: null,
                //           ChangePlanCategory: "",
                //           isAdvRenewal: false,
                //           custId: this.customerLedgerDetailData.id,
                //           recordPaymentDTO: {},
                //           isRefund: false,
                //           planBindWithOldPlans: planBindWithOldPlansChild,
                //           newPlanList: null,
                //           planMappingList: null,
                //           isParent: this.customerLedgerDetailData.parentCustomerId ? true : false,
                //         };
                //         changePlanRequestDTOList.push(renewData);
                //       });
                //     }
                //   });
                // }

                changePlanRequestDTOList.push(pojo);
                let url = "/subscriber/changePlan01";
                let finalRenewData = { changePlanRequestDTOList: changePlanRequestDTOList };
                this.customerManagementService.postMethod(url, finalRenewData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 417) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Successfully!');


                        }
                        this.resetForm();
                    },
                    (error: any) => {
                        console.log(error);
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            } else if (this.changePlanTypeSelection == "Addon") {
                let changePlanRequestDTOList = [];
                this.custServiceData.forEach(element => {
                    if (element.changeFlag) {
                        let renewData = {
                            connectionNo: element.connectionNo,
                            serviceName: element.serviceName,
                            serviceNickName: element.serviceName,
                            purchaseType: "Addon",
                            planId: element.newPlanSelection,
                            planGroupId: this.newPlanGroupId,
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            remarks: this.changePlanNewForm.value.remarks,
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            addonStartDate: new Date(),
                            ChangePlanCategory: "",
                            isAdvRenewal: false,
                            custId: this.customerLedgerDetailData.id,
                            recordPaymentDTO: {},
                            isRefund: false,
                            discount: element.newDiscount,
                            planBindWithOldPlans: [],
                            newPlanList: null,
                            planMappingList: null,
                            custServiceMappingId: element.customerServiceMappingId,
                            isParent: this.customerLedgerDetailData.parentCustomerId ? true : false
                        };
                        changePlanRequestDTOList.push(renewData);
                    }
                });
                this.childPlanGroup.forEach(e => {
                    e.serviceMappingData.forEach(element => {
                        let renewData = {
                            purchaseType: "Addon",
                            planId: e.newPlanSelection,
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            remarks: this.changePlanNewForm.value.remarks,
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            addonStartDate: new Date(),
                            ChangePlanCategory: "",
                            isAdvRenewal: false,
                            custId: e.id,
                            recordPaymentDTO: {},
                            isRefund: false,
                            discount: e.newDiscount,
                            planBindWithOldPlans: [],
                            newPlanList: null,
                            planMappingList: null,
                            custServiceMappingId: e.customerServiceMappingId,
                            isParent: false
                        };
                        changePlanRequestDTOList.push(renewData);
                    });
                });
                let url = "/subscriber/changePlan01";
                let finalRenewData = { changePlanRequestDTOList: changePlanRequestDTOList };
                this.customerManagementService.postMethod(url, finalRenewData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 417) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');


                        }
                        this.resetForm();
                    },
                    (error: any) => {
                        console.log(error);
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            } else {
                let deactivatePlanReqModels = [];
                let deactivatePlanReqModelsChild = [];

                this.custServiceData.forEach(element => {
                    let data = {
                        billToOrg: false,
                        newPlanGroupId: this.newPlanGroupId,
                        planGroupId: this.customerLedgerDetailData.plangroupid,
                        newPlanId: element.newPlanSelection,
                        custServiceMappingId: element.customerServiceMappingId,
                        discount: 0
                    };
                    deactivatePlanReqModels.push(data);
                });
                let finalData = {
                    deactivatePlanReqDTOS: [
                        {
                            custId: this.customerLedgerDetailData.id,
                            deactivatePlanReqModels: deactivatePlanReqModels,
                            planGroupChange: true,
                            planGroupFullyChanged: true,
                            paymentOwner: "yogesh Patil",
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            isParent: this.customerLedgerDetailData.parentCustomerId ? true : false,
                            remark: this.changePlanNewForm.value.remarks
                        }
                    ]
                };
                this.childPlanGroup.forEach(e => {
                    e.serviceMappingData.forEach(element => {
                        let changeDetails = {
                            billToOrg: false,
                            newPlanGroupId: this.newPlanGroupId,
                            planGroupId: this.customerLedgerDetailData.plangroupid,
                            newPlanId: e.newPlanSelection,
                            custServiceMappingId: e.customerServiceMappingId,
                            discount: 0
                        };
                        deactivatePlanReqModelsChild.push(changeDetails);
                    });
                    let childPojo = {
                        custId: e.id,
                        deactivatePlanReqModels: deactivatePlanReqModelsChild,
                        planGroupChange: true,
                        planGroupFullyChanged: true,
                        paymentOwner: "yogesh Patil",
                        paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                        billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                        isParent: this.customerLedgerDetailData.parentCustomerId ? true : false,
                        remark: this.changePlanNewForm.value.remarks
                    };
                    finalData.deactivatePlanReqDTOS.push(childPojo);
                });
                let url = "/subscriber/deactivatePlanInBulk";
                this.customerManagementService.postMethod(url, finalData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 417) {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');


                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');


                        }
                        this.resetForm();
                    },
                    (error: any) => {

                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }

                );
            }
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please Select All The Required Details!');
            }
        }
    }

    // Table Settings
    onActivityChange(event) {
        const value = event.target.value;
        if (value && value.trim().length) {
            const activity = parseInt(value);

            if (!isNaN(activity)) {
                this.table.filter(activity, "activity", "gte");
            }
        }
    }
    onSelectionPlanType() {
        this.customerManagementService.getMethod;
    }
    planDetails: any;
    modalOpenDetails(id, i) {
        this.newPlanData[i].forEach(e => {
            if (e.id == id) this.planDetails = e;
        });
        $("#planDetails").modal("show");
    }
    planGroupChanges: any;
    planMappingListData: any = [];
    newPlanGroupId: any;

    planGroupChangesChild: any;
    planMappingListDataChild: any = [];
    newPlanGroupIdChilds: any;
    selectPlanGroup(e) {
        this.planMappingListData = [];
        let data: any;
        this.planGroupChanges.forEach(element => {
            if (e.value == element.planGroupId) data = element;
        });
        data.planMappingList.forEach(d => {
            this.planMappingListData.push(d);
        });
    }
    selectPlanGroupChild(e) {
        this.planMappingListDataChild = [];
        let data: any;
        this.planGroupChangesChild.forEach(element => {
            if (e.value == element.planGroupId) data = element;
        });
        data.planMappingList.forEach(d => {
            this.planMappingListDataChild.push(d);
        });
    }
    filterPlanGroup(service) {
        this.planMappingListData.forEach(element => {
            if (element.service == service) element.inactive = false;
            else element.inactive = true;
        });
    }
    getPlanChangeGroup() {
        this.newPlanGroupId = null;
        let url = "/getPlanGroupByFilters";
        let data = {
            changePlanType: this.changePlanTypeSelection.toLowerCase(),
            custId: this.customerLedgerDetailData.id,
            planGroupId: this.customerLedgerDetailData.plangroupid,
            customerServiceMappingID:
                this.customerLedgerDetailData.planMappingList[0].custServiceMappingId
        };
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.planGroupChanges = response;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    productsss: any = [
        {
            id: "1000",
            code: "f230fh0g3",
            name: "Bamboo Watch",
            description: "Product Description",
            image: "bamboo-watch.jpg",
            price: 65,
            category: "Accessories",
            quantity: 24,
            inventoryStatus: "INSTOCK",
            rating: 5,
            orders: [
                {
                    id: "1000",
                    productCode: "f230fh0g3",
                    date: "2020-09-13",
                    amount: 65,
                    quantity: 1,
                    customer: "David James",
                    status: "PENDING"
                },
                {
                    id: "1001",
                    productCode: "f230fh0g3",
                    date: "2020-05-14",
                    amount: 130,
                    quantity: 2,
                    customer: "Leon Rodrigues",
                    status: "DELIVERED"
                },
                {
                    id: "1002",
                    productCode: "f230fh0g3",
                    date: "2019-01-04",
                    amount: 65,
                    quantity: 1,
                    customer: "Juan Alejandro",
                    status: "RETURNED"
                },
                {
                    id: "1003",
                    productCode: "f230fh0g3",
                    date: "2020-09-13",
                    amount: 195,
                    quantity: 3,
                    customer: "Claire Morrow",
                    status: "CANCELLED"
                }
            ]
        }
    ];

    getCustomerNetworkLocationDetail(custId) {
        const url = `/customer/getCustNetworkDetail?customerId=${custId}`;
        this.customerManagementService.getCustNetworkLocDetail(url).subscribe(
            (response: any) => {
                this.customerNetworkLocationDetailData = response.data;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
}
