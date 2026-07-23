import { formatDate, DatePipe } from "@angular/common";
import { CustomerService } from "src/app/service/customer.service";
import { Component, Input, OnInit, ViewChild, Output, EventEmitter, TemplateRef } from "@angular/core";
import {
    UntypedFormArray,
    UntypedFormBuilder,
    UntypedFormGroup,
    Validators,
    UntypedFormControl,
    AbstractControl,
    ValidationErrors
} from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { ToastrService } from 'ngx-toastr';
import { saveAs } from 'file-saver';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { NgbDateStruct } from "@ng-bootstrap/ng-bootstrap";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { CustomerManagements } from "src/app/components/model/customer";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import * as uuid from "uuid";
import {
    AREA,
    CITY,
    COUNTRY,
    PINCODE,
    STATE,
    CUSTOMER_PREPAID,
    CUSTOMER_POSTPAID
} from "src/app/RadiusUtils/RadiusConstants";
import { WorkBook } from 'xlsx';
import * as XLSX from 'xlsx';

import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomerInventoryMappingService } from "src/app/service/customer-inventory-mapping.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { LoginService } from "src/app/service/login.service";
import { StaffService } from "src/app/service/staff.service";
import { CustomerDocumentService } from "../customer-documents/customer-document.service";
import { Regex } from "src/app/constants/regex";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import { OutwardService } from "src/app/service/outward.service";
import { ProuctManagementService } from "src/app/service/prouct-management.service";
import { BehaviorSubject, Observable, Observer, Subscription, interval } from "rxjs";
import { countries } from "src/app/components/model/country";
import { InvoiceDetailsService } from "src/app/service/invoice-details.service";
import { InvoiceDetalisModelComponent } from "../invoice-detalis-model/invoice-detalis-model.component";
import { InvoicePaymentDetailsModalComponent } from "../invoice-payment-details-modal/invoice-payment-details-modal.component";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { PaymentAmountModelComponent } from "src/app/components/payment-amount-model/payment-amount-model.component";
import { ExternalItemManagementService } from "src/app/service/external-item-management.service";
import { WorkflowAuditDetailsModalComponent } from "src/app/components/workflow-audit-details-modal/workflow-audit-details-modal.component";
import { CustomerplanGroupDetailsModalComponent } from "src/app/components/customerplan-group-details-modal/customerplan-group-details-modal.component";
import { CustomerWithdrawalmodalComponent } from "src/app/components/customer-withdrawalmodal/customer-withdrawalmodal.component";
import { InwardService } from "src/app/service/inward.service";
import * as FileSaver from "file-saver";
import { InvoiceMasterService } from "src/app/service/invoice-master.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { CustomerInventoryDetailsService } from "src/app/service/customer-inventory-details.service";
import { RejectedReasonService } from "src/app/service/rejected-reason.service";
import { LeadManagementService } from "src/app/service/lead-management-service";
import { PrepaidRejectedReasonService } from "src/app/service/prepaid-rejected-reason.service";
import { isEqual } from "lodash";
import { ActivatedRoute, Router } from "@angular/router";
import { Utils } from "src/app/utils/utils";
import moment from "moment";
import { NetworkdeviceService } from "src/app/service/networkdevice.service";
import { QuotaDetailsModalComponent } from "src/app/components/quota-details-modal/quota-details-modal.component";
import { CountryManagementService } from "src/app/service/country-management.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { PartnerService } from "src/app/service/partner.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS, RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { LocationService } from "src/app/service/location.service";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { CustNotes } from "../model/CustNotes";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { AreaManagementService } from "src/app/service/area-management.service";
import { BuildingManagementService } from "src/app/service/building-management.service";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatStepper } from "@angular/material/stepper";
import { HttpResponse } from "@angular/common/http";
import { MatButtonModule } from '@angular/material/button';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { CustomerSelectComponent } from "../customer-select/customer-select.component";

import { error } from "console";
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { SelectBuildingDialogComponent } from "../common/select-building-dialog/select-building-dialog.component";
import { SelectorDialogComponent } from "../common/selector-dialog/selector-dialog.component";
pdfMake.vfs = pdfFonts;
declare var $: any;

@Component({
    selector: "app-customer-caf",
    templateUrl: "./customer-caf.component.html",
    styleUrls: ["./customer-caf.component.css"],
    standalone: false
})
export class CustomerCafComponent implements OnInit {
    @Input() instanceData!: any;
    lastFiltersString: string = '';
    @Output() closePaymentConfirm = new EventEmitter();
    @Output() closeMobilenumber = new EventEmitter();
    @Output() closeNearByLoc = new EventEmitter();
    @Output() closeLocation = new EventEmitter();
    @Output() closeApprove = new EventEmitter();
    @Output() closeAddNotes = new EventEmitter();
    @Output() closeReject = new EventEmitter();
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild("openRejectLeadPopup") openRejectLeadPopup: TemplateRef<any>
    @ViewChild("PaymentGateway") PaymentGateway;
    @ViewChild("Mobilenumber") Mobilenumber;
    @ViewChild("PaymentConfirm") PaymentConfirm;
    @ViewChild('ShiftLocation') ShiftLocation!: TemplateRef<any>;
    // @ViewChild('selectStaffDialog') selectStaffDialog!: TemplateRef<any>;
    // @ViewChild('shiftLocationDialog') shiftLocationDialog!: TemplateRef<any>;
    @ViewChild('rejectDialog') rejectDialog!: TemplateRef<any>;
    @ViewChild('approverDialog') approverDialog!: TemplateRef<any>;
    invoicedialogRef: MatDialogRef<any> | null = null;
    DeleteConfirmDialogRef!: MatDialogRef<any>;

    selectBuildingDialogRef: MatDialogRef<SelectBuildingDialogComponent>;
    serviceAreaSelectorDialogRef: MatDialogRef<SelectorDialogComponent>;
    selectedServiceAreaName: string = "";
    showContent: boolean = true;
    isEmailMandatory = false;
    displayedColumns2: string[] = [
        'customerName',
        'docnumber',
        'createdByName',
        'billableToName',
        'totalamount',
        'adjustedAmount',
        'unpaidAmount',
        'billrunstatus',
        'billdate',
        'paymentStatus',
        'action'
    ];
    displayedColumns: string[] = [
        "name",
        "username",
        "service_area",
        "assignee_name",
        "mobile",
        "acctno",
        "status",
        "remainTime",
        "mvnoName",
        "action",
    ];
    displayedColumns1: string[] = [
        "name",
        "latitude",
        "longitude",
        "distance",
        "address",
    ];

    ledgerDataSource = new MatTableDataSource<any>([]);
    ledgerDisplayedColumns: string[] = [
        'createDate',
        'receiptNo',
        'invoiceNo',
        'category',
        'debit',
        'credit',
        'balAmount',
        'remarks'
    ];

    displayedColumns3: string[] = ['fullAddress', 'requestedByName', 'requestedDate', 'version', 'action'];


    //   submitted = false;

    searchDetail = '';
    displayStaff: string[] = ['select', 'name', 'username', 'partner'];

    isVisibleCAFHomeComponent = false;
    selectedStaffCust: any = null;
    showStepper: boolean = false;
    addNotesAccess: boolean = false;

    public stepperVisible = false;
    customerVrn = RadiusConstants.CUSTOMER_VRN;
    customerNid = RadiusConstants.CUSTOMER_NID;

    cityId;
    areaId;
    custData: any = {};
    customerId: number;
    custType: string = "";
    editmode: boolean = false;
    displaymode: boolean = true;
    ipdisplayManagementGroup: UntypedFormGroup;
    ipManagementGroup: UntypedFormGroup;
    macManagementGroup: UntypedFormGroup;
    ipMapppingListFromArray: UntypedFormArray;
    ipMapppingdisplayListFromArray: UntypedFormArray;
    macMapppingListFromArray: UntypedFormArray;
    notificationusername: string;
    ipData: any = [""];
    currentPageStaffListIndex = 0
    currentStaffData: any[] = [];
    pagedStaffDataSource = new MatTableDataSource();
    custId: any = [""];
    customerid: string;
    // customerid: number;
    service: any[] = [];
    custPlanMapppingId: any = [""];
    ipListData: any = [];
    customerNotesListPdf: any = [];
    createIp: boolean = false;
    createMac: boolean = false;
    macSubmitted: boolean = false;
    displayInvoicePaymentDialog: boolean;
    savedConfig: any;
    invoice: any;
    exitBuy: boolean = true;
    paymentstatusCount = RadiusConstants.TIMER_COUNT;
    paymentConfirmationModal: boolean = false;
    subscription2: Subscription;
    obs1$ = interval(1000);
    transactionStatus: boolean = false;
    paymentSucessModel: boolean = false;
    presentAdressDATA: any = [];
    isPaymentGatewayConfigured: boolean = false;
    paymentGateway: any;
    paymentkeyValuePairs: { [key: string]: any } = {};

    isFinanceDetailsToggle = false;
    isAuditDetailsToggle = false;
    isSupportDetailsToggle = false
    isUserDetailsToggle = false

    changeStatus: string;
    editingRecord: any = {};
    currentEditRecord: any;
    editingIndex: number | null = null;
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    cutomerId;
    areaTitle = AREA;
    department = RadiusConstants.DEPARMENT;
    KraTitle = RadiusConstants.KRA_PIN;
    @ViewChild("closebutton") closebutton;
    @ViewChild(InvoiceDetalisModelComponent)
    InvoiceDetailModal: InvoiceDetalisModelComponent;
    @ViewChild(InvoicePaymentDetailsModalComponent)
    invoicePaymentDetailModal: InvoicePaymentDetailsModalComponent;
    @ViewChild(PaymentAmountModelComponent)
    PaymentDetailModal: PaymentAmountModelComponent;
    @ViewChild(WorkflowAuditDetailsModalComponent)
    custauditWorkflowModal: WorkflowAuditDetailsModalComponent;
    @ViewChild(CustomerplanGroupDetailsModalComponent)
    custPlanGroupDataModal: CustomerplanGroupDetailsModalComponent;
    //   @ViewChild(CustomerWithdrawalmodalComponent)
    withdrawalAmountModal: CustomerWithdrawalmodalComponent;
    @ViewChild(QuotaDetailsModalComponent)
    quotaModalOpen: QuotaDetailsModalComponent;
    @ViewChild('approveCustomerDialog') approveCustomerDialog: TemplateRef<any>;
    @ViewChild('rejectCustomerDialog') rejectCustomerDialog: TemplateRef<any>;
    staffTableColumns = ['select', 'name', 'username'];
    bankDataList: any;
    custLedgerForm: UntypedFormGroup;
    fields: any;
    countries: any = countries;
    customerGroupForm: UntypedFormGroup;
    assignCustomerCAFForm: UntypedFormGroup;
    rejectCustomerCAFForm: UntypedFormGroup;
    customerCategoryList: any;
    submitted = false;
    assignCustomerCAFsubmitted: boolean = false;
    displayInvoiceDetails: boolean = false;
    rejectCustomerCAFsubmitted: boolean = false;
    ifModelIsShow: boolean = false;
    assignCustomerCAFId: any;
    nextApproverId: any;
    taxListData: any;
    createcustomerData: CustomerManagements;
    displayFailedPaymentDialog = false;
    currentPagecustomerListdata = 1;
    customerListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    customerListdatatotalRecords: number = 0;
    customerListData: any = [];
    viewcustomerListData: any = [];
    earlydays = [];
    iscustomerEdit = false;
    customertype = "";
    CustomerSector = "";
    custDetilsCustId;
    customercategory = "";
    searchcustomerUrl: any;
    chargeCategoryList: any;
    isPlanEdit = false;
    viewPlanListData: any;
    payMappingListFromArray: UntypedFormArray;
    addressListFromArray: UntypedFormArray;
    paymentDetailsFromArray: UntypedFormArray;
    overChargeListFromArray: UntypedFormArray;
    custMacMapppingListFromArray: UntypedFormArray;
    selectvalue = "";
    displayDialogWithDraw: boolean = false;
    planByServiceArea: any;
    paymappingItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    payMappinftotalRecords: String;
    currentPagePayMapping = 1;
    overChargeListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    overChargeListtotalRecords: String;
    currentPageoverChargeList = 1;
    custMacMapppingListtemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacMapppingListtotalRecords: String;
    currentPagecustMacMapppingList = 1;
    custChargeDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custChargeDeatiltotalRecords: String;
    currentPagecustChargeDeatilList = 1;
    custPlanDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custPlanDeatiltotalRecords: String;
    currentPagecustPlanDeatilList = 1;
    custMacAddItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacAddtotalRecords: String;
    currentPagecustMacAddList = 1;
    custLedgerItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custLedgertotalRecords: String;
    currentPagecustLedgerList = 1;
    customerPaymentdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerPaymentdatatotalRecords: String;
    currentPagecustomerPaymentdata = 1;
    customerFuturePlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerFuturePlanListdatatotalRecords: String;
    currentPagecustomerFuturePlanListdata = 1;
    customerExpiryPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerExpiryPlanListdatatotalRecords: number = 0;
    currentPagecustomerExpiryPlanListdata = 1;
    customerCurrentPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCurrentPlanListdatatotalRecords: String;
    currentPagecustomerCurrentPlanListdata = 1;
    cafRemainTimeSubscription: any;
    temp = [];

    customerListData1: any;
    customerListDataselector: any;
    totalAddress = 0;
    macAddresscountNumber = 0;
    searchCustomerName: any;
    searchCustomerType: any = "";
    searchData: any;
    customersListData: any;
    searchOption = "";
    searchDeatil = "";
    // fieldEnable = false;
    addresslength = 0;
    payMappinglength = 0;
    charegelength = 0;
    charge_date: NgbDateStruct | any;
    presentaddress = "";
    require: any;
    ngbBirthcal: NgbDateStruct | any;
    listView = true;
    isViewTicketMenu = false
    createView = false;
    areaDetails: any;
    pincodeDeatils: any;
    areaAvailableList: any;
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
    plansubmitted = false;
    chargesubmitted = false;
    presentGroupForm: UntypedFormGroup;
    paymentGroupForm: UntypedFormGroup;
    permanentGroupForm: UntypedFormGroup;
    validPattern = "^[0-9]{3}$";
    selectAreaListPermanent = false;
    selectAreaListPayment = false;
    selectPincodeListPermanent = false;
    selectPincodeListPayment = false;
    ischecked = false;
    macsubmitted = false;
    chargeList: any;
    selectchargeList = false;
    planData: any = [];
    filterPlanData: any = [];
    listSearchView = false;
    isCustomerDetailOpen = false;
    dialog: boolean = false;
    dialogDoc: boolean = false;
    PreviewImagesDialog: boolean = false;
    ifcustCaf: boolean = true;
    customerDetailData: any = {
        title: "",
        firstname: "",
        lastname: "",
        contactperson: "",
        gst: "",
        pan: "",
        aadhar: "",
        passportNo: "",
        //cafno: "",
        acctno: "",
        username: "",
        mobile: "",
        // phone: "",
        email: "",
        serviceareaid: "",
        servicetype: "",
        custtype: "",
        latitude: "",
        longitude: "",
        didno: "",
        voicesrvtype: "",
        partnerid: "",
        // salesremark: "",
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
    custCurrentPlanList;
    obs$ = interval(1000);
    custFuturePlanList: any;
    custExpiredPlanList: any;
    partnerDATA: any = [];
    permentAdressDATA: any = [];
    paymentAdressDATA: any = [];
    chargeDATA = [];
    dataPlan = [];
    planserviceData: any;
    serviceAreaDATA: any;
    paymentDataamount: any;
    paymentDatareferenceno: any;
    paymentDatapaymentdate: any;
    paymentDatapaymentMode: any;
    customerApporevedData: any;
    customerRejectedData: any;
    plantypaSelectData: any;
    viewChargeData: any;
    selectchargeValueShow = false;
    currentDate = new Date();
    loggedInUser: any;
    staffUserId: any = [];
    userName: "";
    UserServiceName: "";
    userServiAreaId: any;
    AclClassConstants;
    AclConstants;
    serviceAreaData: any;
    public loginService: LoginService;
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
    searchLocationForm: UntypedFormGroup;
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
    customerLedgerListData: any;
    isCustomerLedgerOpen = false;
    viewcustomerPaymentData: any;
    customerIdINLocationDevice: string;
    NetworkDeviceData: any;
    customerStatusDetail: any;
    customertotalRecords = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPage = 1;
    searchkey: string;
    customerListDatalength = 0;
    custLedgerSubmitted = false;
    customerLedgerSearchKey: string;
    legershowItemPerPage = 1;
    CurrentPlanShowItemPerPage = 1;
    futurePlanShowItemPerPage = 1;
    expiredShowItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ticketShowItemPerPage = 1;
    paymentShowItemPerPage = 1;
    isInvoiceDetail = false;
    assignInventory: boolean;
    assignExternalInventory: boolean;
    customerrMyInventoryView: boolean;
    assignPlanInventory: boolean;
    serviceList: any[];
    getActivePlanList: any[];
    getFuturePlanList: any[];
    getAllPlanIvnetoryIdOnPlanIdList: any[];
    getProductCategoryList: any[];
    getProductByPlanIdList: any[];
    serviceUnit: any;
    custPackageUnit: any[];
    assignedInventoryList = [];
    currentPageProductListdata = 1;
    productListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productListdatatotalRecords: any;
    first = 0;
    rows = 10;
    paymentFormGroup: UntypedFormGroup;
    viewcustomerFailedPaymentData: any;
    dateOfBirth: String;

    createPaymentData: any;
    customerData: any;
    customerIdRecord: number;
    assignInventoryModal: boolean;
    inventoryAssignForm: UntypedFormGroup;
    inwardList: any[];
    Customertype: any[];
    CustomertypeSubtype: any[];
    externalItemList: any[];
    availableQty: number;
    unit: any;
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

    inventoryType = [
        { label: "Permanant Replacement", value: "Permanant Replacement" },
        { label: "Temporary Replacement", value: "Temporary Replacement" }
    ];
    selectedPlanType: any;
    selectedBillingCycle: any;
    planUpgradeDowngrade: any[];
    planBillingCycle: any[];

    @Input("customerId")
    showQtyError: boolean;
    // userId: number = localStorage.getItem('userId');
    userId: number = +localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    macList = [];
    selectedMACAddress: any = [];
    productHasMac: boolean;
    showQtySelectionError: boolean;
    productHasSerial: boolean;
    ifMyInvoice = false;
    showItemPerPageInvoice = 1;
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
    showPassword = false;
    _passwordType = "password";
    searchkey2: string;
    paymentMode: any;
    statusOptions = RadiusConstants.status;
    searchOptionSelect = this.commondropdownService.customerSearchOptionForCAF;
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
    FeasibilityOptions = [];
    // { label: "Not Service Ready (NSR)", value: "Not Service Ready (NSR)" },
    // { label: "Maintenance", value: "Maintenance" }
    totaladjustedAmount = 0;
    celendarTypeData = [{ label: "English" }, { label: "Nepali" }];
    ifIndividualPlan = false;
    ifPlanGroup = false;
    planGroupName: any = "";
    planCategoryForm: UntypedFormGroup;
    prepaidParentCustomerList: any;
    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentCustomerListdatatotalRecords: any;
    selectedParentCust: any = [];
    filterPartnerPlanGroup = [];

    selectedParentCustId: any;
    parentCustList: any;
    editCustomerId: any;
    newFirst = 0;
    searchParentCustOption = "";
    searchParentCustValue = "";
    serviceAreaDisable = false;
    parentFieldEnable = false;
    validityUnitFormGroup: UntypedFormGroup;
    validityUnitFormArray: UntypedFormArray;
    // discount
    customerCustDiscountListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCustDiscountListdatatotalRecords: String;
    currentPagecustomerCustDiscountListdata = 1;
    CustDiscountShowItemPerPage = 1;
    custCustDiscountList: any = [];
    eventActionData: any = [];
    oldDiscValue = 0;
    newDiscValue = 0;
    customerUpdateDiscount = false;
    shiftLocationEvent = false;
    FinalAmountList: any = [];
    planMappingList = [];
    planDiscount: number;
    finalOfferPrice: number;
    offerPrice: number;
    groupOfferPrices = {};
    maxDiscountValue = 99;
    maxLength = 250;
    isInvoiceData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];
    ifWalletMenu = false;
    getWallatData = [];
    planDropdownInChageData = [];

    dataChargePlan: any = [];
    billingCycle: any = [];
    customerInventoryListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerInventoryListDataCurrentPage = 1;
    customerInventoryListDataTotalRecords: number;
    assignInventoryCustomerId: any;
    assignedInventoryListWithSerial = [];
    assignInventoryWithSerial: boolean;
    customerInventoryDetailsListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerInventoryDetailsListDataCurrentPage = 1;
    customerInventoryDetailsListDataTotalRecords: number;
    customerInventoryMappingId: any;
    customerInventoryMappingIdForReplace: any;
    showReplacementForm: boolean;
    inventoryStatusDetails: any;
    inventoryStatusView: boolean;
    isCustomerDetailSubMenu = false;
    staffUser: any;
    // isAdmin = false;
    isCaf = false;
    viewCustomerPaymentList = false;
    customerPlanView = false;
    customerStatusView = false;
    ipManagementView = false;
    macManagementView = false;
    customerCafNotes = false;
    ifUpdateAddress = false;
    ifCafFollowUp = false;
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
        shiftPartnerid: ""
    };
    ifUpdateAddressSubmited = false;
    ifCafFollowupSubmited = false;
    partnerListByServiceArea: any = [];
    OlddiscountData = [];
    AreaListDD: any;
    inputMobile = "";
    inputMobileSec = "";
    filterNormalPlanGroup = [];
    serviceareaCheck = true;
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    pincodeDD: any = [];
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
    invoicedropdownValue = [{ docnumber: "Advance", id: 0 }];
    planGroupSelectedSubisu: any;
    planListSubisu: any;
    plansArray: UntypedFormArray;
    newPrice: any;
    isInvoiceToOrg: any = false;
    customerBill: "";
    custInvoiceToOrg: boolean;
    ifChargeGetData = false;
    chargeUseCustID = "";
    inventoryStatusDetailsForReplace = [];
    assignAppRejectDiscountForm: UntypedFormGroup;
    assignAppRejectShiftLocationForm: UntypedFormGroup;
    Inventoryreject = false;
    rejectInventoryData = [];
    InventoryselectStaffReject: any;
    Inventoryapproved = false;
    approveInventoryData = [];
    InventoryselectStaff: any;
    approveId: any;
    workflowID: number;
    reject = false;
    rejectCAF = [];
    selectStaffReject: any;
    approved = false;
    approveCAF = [];
    reassigndata = [];
    selectStaff: any;
    ifplanisSubisuSelect = false;
    WalletAmount: any = "";
    workflowAuditDataI: any = [];
    currentPageMasterSlabI = 1;
    MasteritemsPerPageI = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecordsI: String;
    assignDiscountData: any = [];
    assignShiftLocationData: any = [];
    dropdownOptions: any[] = [];
    shiftLocationFlagType = "";
    assignShiftLocationsubmitted = false;
    buid: any;
    mvnoid: any;
    staffid: any;
    departmentListData: any;
    departmenttotalRecords: any;
    departmentitemsPerPage: any;
    blockNoOptions: number[];
    isMobileNumberFocus = false;
    discountColumns: string[] = [
        'connectionNo',
        'service',
        'currentDiscountType',
        'currentDiscount',
        'currentExpiryDate',
        'newDiscountType',
        'newDiscount',
        'newExpiryDate',
        'remarks',
        'action'
    ];
    currentDiscountDataSource = new MatTableDataSource<any>([]);
    @ViewChild('documentPreviewDialog') documentPreviewDialog: TemplateRef<any>;
    @ViewChild('previewImagesDialog') previewImagesDialog: TemplateRef<any>;
    @ViewChild("scheduleFollowDialog") scheduleFollowDialog: TemplateRef<any>
    scheduledialogRef!: MatDialogRef<any>;
    // deleteMACMapping(mapping) {
    //
    //   mapping.customerId = null;
    //   this.customerInventoryMappingService
    //     .deleteMacForCustomer(mapping)
    //     .subscribe(
    //       (res: any) => {
    //         this.deleteMacMappInCustomer(mapping.macAddress);
    //         this.getMacMappingsByOutwardId(mapping.outwardId);
    //
    //       },
    //       (error: any) => {
    //
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: error.error.ERROR,
    //           icon: "far fa-times-circle",
    //         });
    //       }
    //     );
    // }
    discountFlageType = "";

    // saveCustomerMACMapping() {
    //   let custMacMapping = [];
    //
    //   this.selectedMACAddress.forEach((element) => {
    //     custMacMapping.push({
    //       macAddress: element.macAddress,
    //       customer: this.customerId,
    //     });
    //   });
    //
    //   this.outwardService.saveCustomerMACMapping(custMacMapping).subscribe(
    //     (res: any) => {
    //       this.macList = [];
    //       // this.messageService.add({
    //       //   severity: 'success',
    //       //   summary: 'Successfully',
    //       //   detail: "Assigend inventory successfully.",
    //       //   icon: 'far fa-check-circle',
    //       // });
    //     },
    //     (error: any) => {
    //
    //       this.messageService.add({
    //         severity: "error",
    //         summary: "Error",
    //         detail: error.error.msg,
    //         icon: "far fa-times-circle",
    //       });
    //     }
    //   );
    // }

    // deleteMacMappInCustomer(macMaddress) {
    //   this.outwardService
    //     .deleteMacMapInCustomer(this.customerId, macMaddress)
    //     .subscribe((res: any) => {});
    AppRjecHeader = "";
    assignDiscounsubmitted = false;
    workflowAuditData: any = [];
    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: String;
    searchDBRFormDate: any = "";
    searchDBREndDate: any = "";
    dbrListData: any = [];
    currentPageDBRListdata = 1;
    DBRListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    showItemDBRPerPage = 0;
    DBRListdatatotalRecords: any;
    ifShowDBRReport = false;
    private oldMacMappingId: any;
    loggedInStaffId = localStorage.getItem("userId");
    fileName: any;
    file: any = "";

    planDataForm: UntypedFormGroup;
    customerPopName: any = "";
    tdsInclude: boolean = true;
    abbsInclude: boolean = true;
    tdsPercent: number;
    abbsPercent: number;
    masterSelected: boolean;
    checklist: any;
    checkedList: any = [];
    currency: string;
    systemConfigCurrency: string;
    isCustSubTypeCon: boolean = false;
    inventoryData = new BehaviorSubject({
        inventoryData: ""
    });
    ifcustomerDiscountField = false;
    serviceData: any;
    branchData: any;
    staffList: any;
    customerChangePlan = false;
    childPlanRenewArray: UntypedFormArray = new UntypedFormArray([]);
    changePlanForm: UntypedFormGroup;
    currentData = this.datePipe.transform(Date(), "yyyy-MM-dd");
    chargenewPlanForm: UntypedFormGroup;
    filterPlanListCust: any;
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
    changePlanDate: any = [];
    newAdddiscountdata: any = [];
    pageNumberForChildsPageForChangePlan = 1;
    pageSizeForChildsPageForChangePlan = RadiusConstants.ITEMS_PER_PAGE;
    filterPlanGroupListCust: any;
    newPlanGroupData: any;
    serviceAreaId: any;
    lastRenewalPlanGroupID = "";
    customerChargeDataShowChangePlan = [];
    parentChargeRecurringCustList: number;
    childChargeRecurringCustList: number;
    addChargeForm: UntypedFormGroup;
    chargeChildGroupForm: UntypedFormGroup;
    overChargeChildListFromArray: UntypedFormArray;
    custServiceData: any = [];
    planSelected: any;
    planByService: any = [];
    changePlanRemark: string;
    planGroupSelected: any;
    customerNetworkLocationDetailData: any;
    childCustomerDataListForChangePlan: any = [];
    childPlanGroupFlag = false;
    childPlan_PLANGROUPID = [];
    UpdateParentCustPlans = true;
    childCustomerDataTotalRecordsForChangePlan: number;
    staffDataList: any = [];
    data: any = [];
    newPlanSelectArray: UntypedFormArray;
    planList: any;
    planChangeListdatatotalRecords: any;
    graceNumberDays = "";
    days = [];
    isPlanTypeAddon = false;
    changeplanGroupFlag = false;
    planGroupFlag = false;
    filterSelectedPlanGroupListCust: any;
    changenewPlanForm: UntypedFormGroup;
    subisuChange = false;
    ifPlanSelectChanePlan = false;
    changePlansubmitted = false;
    selectPlan0Rplangroup = "";
    selectPlanListIDs = [];
    paymentOwnerError: boolean;
    changePlanData: any = {};
    changePlanBindigNewPlan = [];
    childPlanType: any;
    childCustID: any = "";
    changePlanBindigChildNewPlan = [];
    isPartnerSelected: boolean = false;

    imagesArray: any[] = [];
    previewUrl: any;

    uploadDocForm: UntypedFormGroup[] = [];
    selectedFileUploadPreview: any[] = [];

    selectedPlanChildList = [];
    selectPlanChildListIDs = [];
    planGroupChildSelected: any;
    chargeAllData: any = [];
    childChargeData = [];
    lastRenewal_CHILDPlanGroupID = "";
    planListChild: any;
    serviceWisePlansData = [];
    selectedPlanList = [];
    planListByType: any = [];
    groupPlanListByType: any = [];
    dateTime = new Date();
    billableCustList: any;
    parentCustomerDialogType: any = "";
    rejectedReasonId: any;
    leadId: number;
    rejectedReasonList: any = [];
    // close lead related variables....
    rejectLeadFormGroup: UntypedFormGroup;
    rejectedLeadFormSubmitted: boolean = false;
    discountType: any = "One-time";
    plansByServiceArr = [];
    remarks: any;
    planIds = [];
    enableChangePlanGroup: boolean = false;
    selectedCustService: any = null;
    promiseToPayData = [];
    isPromiseToPayModelOpen: boolean = false;
    customerSelectType: any = "";
    showParentCustomerModel = false;

    isServiceOpen = false;
    isPlanOnDemand: boolean = false;
    planGroupMapingList: any = [];

    partnerList: any = [];
    isBranchAvailable = false;

    oltDevices = [];
    spliterDevices = [];
    masterDbDevices = [];

    isTrialCheckDisable = false;
    custQuotaList: any[];
    custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custQuotaListtotalRecords: String;
    currentPagecustQuotaList = 1;
    chequeDateName = "Cheque Date";
    custID: number;
    visibleQuotaDetails: boolean = false;

    PRE_CUST_CONSTANTS;
    POST_CUST_CONSTANTS;
    createAccess: boolean = false;
    editAccess: boolean = false;
    closeCafAccess: boolean = false;
    uploadDocAccess: boolean = false;
    nearByDeviceAccess: boolean = false;
    sendPaymentAccess: boolean = false;
    recordPaymentAccess: boolean = false;
    generatePdfAccess: boolean = false;
    reprintAccess: boolean = false;
    viewInvoiceAccess: boolean = false;
    discountAuditLogAccess: boolean = false;
    scheduleFollowUpAccess: boolean = false;
    rescheduleFollowUpAccess: boolean = false;
    closeFollowUpAccess: boolean = false;
    remarkFollowUpAccess: boolean = false;
    callFollowUpAccess: boolean = false;
    addChargeAccess: boolean = false;
    locationDataByPlan: any = [];
    showLocationMac: boolean = false;
    locationMacForm: UntypedFormGroup;
    overLocationMacArray = this.fb.array([]);
    macData: any = [];
    locationMacData: any = [];
    custLocationData: any = [];
    quotaSharableData = [
        { label: "shareable", value: "shareable" },
        { label: "individual", value: "individual" }
    ];
    billToData: any = [];
    custTitle: any = CUSTOMER_PREPAID;
    CustomerStatusValue: any[] = [];
    cols = [
        {
            field: "name",
            header: "Name",
            customExportHeader: "Name"
        },
        {
            field: "username",
            header: "Username",
            customExportHeader: "Username"
        },
        {
            field: "serviceArea",
            header: "Service Area",
            customExportHeader: "Service Area"
        },
        {
            field: "mobile",
            header: "Mobile Number",
            customExportHeader: "Mobile Number"
        },
        {
            field: "acctno",
            header: "Account No",
            customExportHeader: "Account No"
        },
        {
            field: "status",
            header: "	Status",
            customExportHeader: "Status"
        },
        {
            field: "remainTime",
            header: "	Remaining Time",
            customExportHeader: "Remaining Time"
        },
        {
            field: "mvnoName",
            header: "ISP Name",
            customExportHeader: "ISP Name"
        }
    ];
    fromDate = "";
    toDate = "";

    // changes for Shift Location

    newShiftPageLimitOptions = RadiusConstants.pageLimitOptions;
    newShiftshowItemPerPage = 1;

    newShiftapprovableStaff: any = [];
    newShiftoltDevices = [];
    newShiftspliterDevices = [];
    newShiftmasterDbDevices = [];
    newShiftpartnerList = [];
    newShiftpincodeDD: any = [];
    newShiftpartnerListByServiceArea: any = [];
    newShiftstaffList: any = [];
    newShiftbranchData: any = [];
    newShiftAreaListDD: any = [];
    newShiftareaDetails: any = [];
    newShiftstaffSelectList: any = [];
    newShiftbillableCustList: any = [];
    newShiftselectedParentCust: any = [];
    newShiftassignShiftLocationData: any = [];
    newShiftapproveInventoryData = [];
    newShiftrejectInventoryData = [];
    newShiftshiftLocationFlagType = "";
    newShiftAppRjecHeader = "";

    newShiftassignedShiftLocationid: any;
    newShiftCustomerAddressDataForCustometr: any;
    newShiftselectStaff: any;
    newShiftrequestedByID: number;
    newShiftpaymentOwnerId: number;
    newShiftLocationPopId: number;
    newShiftLocationOltId: number;
    newShiftbranchID: number = 0;
    newShiftwalletValue: number;
    newShiftprepaid: any;
    newShiftdueValue: number;
    newShiftparentCustomerDialogType: any = "";
    newShiftcustomerSelectType: any = "";
    newShiftstaffSelectType = "";
    newShiftapproveId: any;
    newShiftselectStaffReject: any;

    newShiftlocationFormRemark: UntypedFormGroup;
    newShiftLocationChargeGroupForm: UntypedFormGroup;
    newShiftpresentGroupForm: UntypedFormGroup;
    newShiftassignAppRejectShiftLocationForm: UntypedFormGroup;

    newShiftapproved = false;
    newShiftreject = false;
    newShiftserviceAreaDisable = false;
    newShiftisBranchAvailable = false;
    newShiftisBranchShiftLocation = false;
    newShiftisServiceInShiftLocation: boolean = false;
    newShiftsubmitted = false;
    newShiftselectPincodeList = false;
    newShiftshowParentCustomerModel = false;
    newShiftifUpdateAddressSubmited = false;
    newShiftassignShiftLocationsubmitted = false;
    newShiftrejectCustomerInventoryModal: boolean = false;
    newShiftrejectApproveShiftLocationModal: boolean = false;
    newShiftselectedStaff: any = [];
    newShiftstaffCustList = [];
    newShiftisSelectStaff: boolean = false;
    newShiftstaffid;

    newShiftcurrentDate = new Date();
    newShiftselectchargeValueShow = false;

    filesArray: any = [];

    newShiftauditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });

    newShiftchargeType = [{ label: "One-time" }, { label: "Recurring" }];
    newShiftshiftLocationDTO: any = {
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
    newShiftdisplayShiftLocationDetails: boolean = false;
    newShiftaddShiftLocationAccess: boolean = false;
    newShiftifModelIsShow: boolean = false;
    newShiftprepaidValue: number;
    newShiftassignDocSubmitted: boolean;
    newShiftremark: any;
    newShiftassignDocForm: any;
    customerInventoryList: any;
    activePlanList: any;
    paymentHistoryList: any;
    staffUserData: any;
    demographicLabel: any;
    mpinModal: boolean = false;
    mpinForm: UntypedFormGroup;
    momoPayinvoice: any;
    isMpinFormSubmitted: boolean = false;
    inputMobileNumber: string = "";
    payMethod: string;
    activePlanNames: string = "";
    showNotes: boolean = false;
    addNotes: boolean = false;
    invoicePaymentAccess: boolean = false;
    invoicesPaymentAccess: boolean = false;
    reassigncafAccess: boolean = false;
    updateDiscountcafAccess: boolean = false;
    buildingListDD: any[];
    buildingNoDD: any[];
    areaListDD: any[];
    selectedMappingFrom: any;
    failureReasonDialog: boolean = false;
    searchStaffDeatil = "";
    searchReassignStaffDeatil = "";
    currentPageApproveStaffListdata: any;
    approveStaffListdataitemsPerPageForStaff: number = 5;
    approvestaffListdatatotalRecords: number = 0;
    approvestaffReassignListdatatotalRecords: number = 0;
    approveCAFData: any[];
    approveReassignCAFData: any[];
    reassignDataRefresh: any;
    newStaffFirst: number = 0;
    isCredentialMatch: boolean = true;
    transModal: boolean = false;
    transactionNo: any;
    addToWalletOrderId: any;
    failureReason: string = "";
    retryPaymentAccess: boolean = false;
    manuallySettlement: boolean = false;
    framedIpAddress: any;
    isCallDetails: boolean = false;
    newShiftbuildingListDD: any;
    newShiftsubAreaListDD: any;
    newShiftbuildingNoDD: any[];
    subareaTitle = RadiusConstants.SUBAREA;
    buildingTitle = RadiusConstants.BUILDING;
    rejectCafData: any[];
    bankDestination: any;
    selectedInvoice: any = [];
    displayRecordPaymentDialog: boolean = false;
    isShowInvoiceList: boolean;
    destinationbank: boolean;
    isSelectedInvoice = true;
    isAbbsFlag: boolean = false;
    isTdsFlag: boolean = false;
    displaySelectInvoiceDialog: boolean = false;
    invoiceList = [];
    onlineSourceData = [];
    disableShiftButton: boolean = false;
    assignCustomerCAFModal: boolean = false;
    rejectCustomerCAFModal: boolean = false;
    reAssignCustomerCAFModal: boolean = false;
    showLoginPassword = false;
    _loginPasswordType = "password";
    istrialplan: boolean = false;
    custTrailPlanItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TrailPlanList = [];
    custShowTrailPlanShow = 1;
    currentTrailPlanListdata = 1;
    servicePlanId: any;
    customerAddressDetails: any;
    displayMpesaOptionsDialog: boolean;
    selectedMpesaOption: string = "";
    invoiceForMpesa: any;

    isDisplayConvertedAmount: boolean = false;
    convertedExchangeRate: any;
    collectedCurrency: string;

    _AAApasswordType = "password";
    AAAshowPassword = false;

    _CWSCpasswordType = "password";
    CWSCshowPassword = false;

    aclPreCustCode: any;
    exportXLSAccess: boolean = false;
    @Input() function: any = "";
    step1Group: UntypedFormGroup;
    step2Group: UntypedFormGroup;
    step3Group: UntypedFormGroup;
    step4Group: UntypedFormGroup;
    @ViewChild('stepper') stepper: MatStepper;
    @ViewChild("loactionSelect") loactionSelect;
    @ViewChild("AddNotes") AddNotes;
    @ViewChild("Approve") Approve;
    @ViewChild("Reject") Reject;
    @ViewChild("NearByLoc") NearByLoc;
    @ViewChild('rejectApproveDiscountDialog') rejectApproveDiscountDialog!: TemplateRef<any>;
    @ViewChild('customerDiscountDialog') customerDiscountDialog!: TemplateRef<any>;
    @ViewChild('staffDetailsDialog') staffDetailsDialog!: TemplateRef<any>;
    @ViewChild('serviceAreaDialog') serviceAreaDialog!: TemplateRef<any>;
    @ViewChild('planDueAmountTemplate') planDueAmountTemplate!: TemplateRef<any>;
    @ViewChild('reAssignCustomerCAFModal') reAssignCustomerCAFModalDialog!: TemplateRef<any>;

    rejectApproveDiscountDialogRef
    customerDiscountDialogRef
    staffDetailsDialogRef
    serviceAreaDialogRef
    dataSource: MatTableDataSource<any>;
    passwordType = 'password';
    loginPasswordType = 'password'; documentPreview = false;
    selectedType: any;
    serviceURL: any;
    staffColumns = ['select', 'name', 'username'];
    staffListDatasource = new MatTableDataSource<any>([]);
    notesColumns = ['id', 'createdBy', 'createdDate', 'staffTeam', 'notes'];
    paginatedCustomerNotesList = new MatTableDataSource();
    workflowColumns = ['customerName', 'action', 'staffName', 'remark', 'actionDate', 'preview'];
    paginatedWorkflowData = new MatTableDataSource();
    custPlanChangeData = new MatTableDataSource();
    planDisplayedColumns = [
        'serviceName',
        'connectionNo',
        'nickName',
        'planName',
        'planGroup',
        'validity',
        'planStatus',
        'startDate',
        'expiryDate'
    ];
    currentPageParentStaffListdata: any;
    batchStaffid: number;
    @ViewChild("rescheduleFollowUpsModal") rescheduleFollowUpsModal: TemplateRef<any>;
    @ViewChild("closeFollowUpsModal") closeFollowUpsModal: TemplateRef<any>;
    @ViewChild("remarkScheduleFollowupModal") remarkScheduleFollowupModal: TemplateRef<any>;
    @ViewChild("openRejectLeadDialog") openRejectLeadDialog: TemplateRef<any>
    activeStaffListNameSort: any;

    constructor(
        private toastr: ToastrService,
        public matdialog: MatDialog,
        private fb: UntypedFormBuilder,
        private sanitizer: DomSanitizer,
        private customerService: CustomerService,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        public customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        public PaymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService,
        public partnerService: PartnerService,
        public serviceAreaService: ServiceAreaService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private staffService: StaffService,
        loginService: LoginService,
        private customerDocumentService: CustomerDocumentService,
        private customerInventoryMappingService: CustomerInventoryMappingService,
        private recordPaymentService: RecordPaymentService,
        private externalItemManagementService: ExternalItemManagementService,
        private outwardService: OutwardService,
        private inwardService: InwardService,
        private productService: ProuctManagementService,
        private invoiceDetailsService: InvoiceDetailsService,
        public invoicePaymentListService: InvoicePaymentListService,
        private invoiceMasterService: InvoiceMasterService,
        private systemService: SystemconfigService,
        public customerdetailsilsService: CustomerdetailsilsService,
        public CustomerInventoryDetailsService: CustomerInventoryDetailsService,
        public datePipe: DatePipe,
        private rejectedReasonService: RejectedReasonService,
        private leadManagementService: LeadManagementService,
        private prepaidRejectedReasonService: PrepaidRejectedReasonService,
        private networkdeviceService: NetworkdeviceService,
        private utils: Utils,
        private route: ActivatedRoute,
        private router: Router,
        private countryManagementService: CountryManagementService,
        public statusCheckService: StatusCheckService,
        public locationService: LocationService,
        private pincodeManagementService: PincodeManagementService,
        private areaManagementService: AreaManagementService,
        private buildingMangementService: BuildingManagementService,
        public datepipe: DatePipe,
        public dialogg: MatDialog
    ) {
        this.dataSource = new MatTableDataSource([]);
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
            this.tdsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
            this.abbsPercent = res.data.value;
        });
        this.PRE_CUST_CONSTANTS = PRE_CUST_CONSTANTS;
        this.POST_CUST_CONSTANTS = POST_CUST_CONSTANTS;
        this.custType = this.route.snapshot.paramMap.get("custType")!;
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.newShiftaddShiftLocationAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_SHIFT_LOCATION_ADD
                : POST_CUST_CONSTANTS.POST_CUST_SHIFT_LOCATION_ADD
        );
        this.createAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.CREATE_PRE_CUST_CAF_LIST
                : POST_CUST_CONSTANTS.CREATE_POST_CUST_CAF
        );

        this.editAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.EDIT_PRE_CUST_CAF_LIST
                : POST_CUST_CONSTANTS.EDIT_POST_CUST_CAF
        );

        this.closeCafAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.CLOSE_PRE_CUST_CAF_LIST
                : POST_CUST_CONSTANTS.CLOSE_POST_CUST_CAF
        );
        this.uploadDocAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.UPLOAD_DOCS_PRE_CUST
                : POST_CUST_CONSTANTS.UPLOAD_DOCUMENTS_POST_CUST_CAF
        );
        this.nearByDeviceAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_NEAR_BY_DEVICE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_NEARBY_DEVICE
        );
        this.sendPaymentAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PAYMENT_LINK_PRE_CUST_CAF
                : POST_CUST_CONSTANTS.PAYMENT_LINK_POST_CUST_CAF
        );
        this.recordPaymentAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_PAYMENT_RECORD
                : POST_CUST_CONSTANTS.POST_CUST_PAYMENT_RECORD
        );
        this.generatePdfAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_INVOICES_GENERATE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_INVOICES_GENERATE
        );
        this.reprintAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_INVOICES_REPRINT
                : POST_CUST_CONSTANTS.POST_CUST_CAF_INVOICES_REPRINT
        );
        this.viewInvoiceAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_INVOICES_VIEW
                : POST_CUST_CONSTANTS.POST_CUST_CAF_INVOICES_VIEW
        );

        this.discountAuditLogAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_CHANGE_DISCOUNT_AUDIT_DETAILS
                : POST_CUST_CONSTANTS.POST_CUST_CAF_CHANGE_DISCOUNT_AUDIT
        );
        this.scheduleFollowUpAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_FOLLOW_UP_SCHEDULE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_SCHEDULE
        );
        this.rescheduleFollowUpAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_FOLLOW_UP_RESCHEDULE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_RESCHEDULE
        );
        this.closeFollowUpAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_FOLLOW_UP_CLOSE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_FOLLOW_UP_CLOSE
        );

        this.remarkFollowUpAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_FOLLOW_UP_CLOSE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_FOLLOW_UP_CLOSE
        );
        this.callFollowUpAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_FOLLOW_UP_CALL
                : POST_CUST_CONSTANTS.POST_CUST_CAF_CALL
        );
        this.addChargeAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_CHARGE_CREATE_CHARGE
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.showNotes = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.CUSTOMER_CAF_NOTES
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.addNotes = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.ADD_NOTES_PRE_CUST_CAF
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.invoicePaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_INVOICE_PAYMENT
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.invoicesPaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_INVOICES_PAYMENT
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.reassigncafAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.REASSIGN_PRE_CUST_CAF
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.updateDiscountcafAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_CHANGE_UPDATE_DISCOUNT
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.retryPaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.RETRY_CAF_PAYMENTSTATUS
                : POST_CUST_CONSTANTS.POST_RETRY_CAF_PAYMENTSTATUS
        );
        this.manuallySettlement = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.MANUALLY_CAF_SETTLEMENT
                : POST_CUST_CONSTANTS.POST_MANUALLY_CAF_SETTLEMENT
        );

        switch (this.function) {
            case "acct-cdr":
                this.exportXLSAccess = this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_CDR_XLS);
                break;
            case "live_user":
                this.exportXLSAccess = this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_LIVE_USERS_XLS);
                break;
            default:
                this.exportXLSAccess = true;
                break;
        }
        this.availableQty = 0;
        this.custType == "Prepaid"
            ? (this.custTitle = CUSTOMER_PREPAID)
            : (this.custTitle = CUSTOMER_POSTPAID);
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
            externalItemId: [""]
        });

        this.macList = [];
        // this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
        //     this.tdsPercent = res.data != null ? res.data.value : "";
        // });this api will removed by shivam
        // this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
        //     this.abbsPercent = res.data != null ? res.data.value : "";
        // });this api will removed by shivam
        // this api will removed and go to Customer Ledger
        // this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
        //     this.currency = res.data != null ? res.data.value : "";
        // });

        this.cafremaingTime();
        this.getaclEntries();
    }



    openDetails(type: string) {
        switch (type) {
            case "home":
                this.isVisibleCAFHomeComponent = true;
                // this.resetMenuChange();
                this.isSupportDetailsToggle = false;
                this.isAuditDetailsToggle = false;
                this.isUserDetailsToggle = false;
                this.isFinanceDetailsToggle = false;
                this.customerChangePlan = false;
                this.customerStatusView = false;
                this.ifMyInvoice = false;
                this.customerPlanView = false;
                this.viewCustomerPaymentList = false;
                this.ifChargeGetData = false;
                this.customerUpdateDiscount = false;
                this.ifCafFollowUp = false;
                this.customerCafNotes = false;
                this.ifUpdateAddress = false;
                this.isServiceOpen = false;


                break;
            case "finance":
                this.isFinanceDetailsToggle = !this.isFinanceDetailsToggle;
                this.isSupportDetailsToggle = false;
                this.isAuditDetailsToggle = false;
                this.isUserDetailsToggle = false;
                // this.isVisibleCAFHomeComponent = false;

                break;
            case "audit":
                this.isAuditDetailsToggle = !this.isAuditDetailsToggle;
                this.isFinanceDetailsToggle = false;
                // this.isVisibleCAFHomeComponent = false;

                this.isSupportDetailsToggle = false;
                this.isUserDetailsToggle = false;
                break;
            case "support":
                this.isSupportDetailsToggle = !this.isSupportDetailsToggle;

                // this.isVisibleCAFHomeComponent = false;
                this.isFinanceDetailsToggle = false;
                this.isAuditDetailsToggle = false;
                this.isUserDetailsToggle = false;
                break;
            case "user":
                this.isUserDetailsToggle = !this.isUserDetailsToggle;

                // this.isVisibleCAFHomeComponent = false;
                this.isFinanceDetailsToggle = false;
                this.isAuditDetailsToggle = false;
                this.isSupportDetailsToggle = false;
                break;
            default:
                break;
        }
    }

    resetToggles() {
        this.isFinanceDetailsToggle = false;
        this.isAuditDetailsToggle = false;
        this.isSupportDetailsToggle = false;
        this.isUserDetailsToggle = false;
    }


    createStepGroups() {
        // Step 1: Basic Details - All Required Fields
        const step1Controls: any = {
            // Required fields
            firstname: this.customerGroupForm.get('firstname'),
            lastname: this.customerGroupForm.get('lastname'),
            contactperson: this.customerGroupForm.get('contactperson'),
            calendarType: this.customerGroupForm.get('calendarType'),
            pan: this.customerGroupForm.get('pan'),
            custlabel: this.customerGroupForm.get('custlabel'),
            dunningCategory: this.customerGroupForm.get('dunningCategory'),

            // Conditional required - AAA Username/Password (required if not credential match)
            username: this.customerGroupForm.get('username'),
            password: this.customerGroupForm.get('password')
        };

        // Add login credentials only if not in edit mode
        // if (!this.iscustomerEdit) {
        //     step1Controls.loginUsername = this.customerGroupForm.get('loginUsername');
        //     step1Controls.loginPassword = this.customerGroupForm.get('loginPassword');
        // }
        // else {
        //     step1Controls.loginUsername = "";
        //     step1Controls.loginPassword = "";
        // }

        this.step1Group = this.fb.group(step1Controls);

        this.customerGroupForm?.get("custlabel").setValue("customer");
        this.step1Group?.get("custlabel").setValue("customer");
        if (this.commondropdownService?.dunningRules?.length) {
            this.customerGroupForm?.get("dunningCategory").setValue(this.commondropdownService?.dunningRules[0].value);
        }

        // Step 2: Contact Details
        this.step2Group = this.fb.group({
            mobile: this.customerGroupForm.get('mobile'),
            countryCode: this.customerGroupForm.get('countryCode')
        });

        // Step 3: Subscriber Location Details - Required Fields (from presentGroupForm)
        this.step3Group = this.fb.group({
            serviceareaid: this.customerGroupForm.get('serviceareaid'),
            partnerid: this.presentGroupForm.get('partnerid'),
            landmark: this.presentGroupForm.get('landmark'),
            pincodeId: this.presentGroupForm.get('pincodeId'),
            areaId: this.presentGroupForm.get('areaId'),
            cityId: this.presentGroupForm.get('cityId'),
            countryId: this.presentGroupForm.get('countryId'),
            stateId: this.presentGroupForm.get('stateId'),
            // Other fields like building_mgmt_id, subareaId, buildingNumber, etc. are optional
        });
        this.step4Group = this.fb.group({});
    }

    openStepper() {
        this.showStepper = true;
    }
    onNextClick(stepper: MatStepper) {
        this.submitted = true;
        if (this.customerGroupForm.invalid) {
            this.customerGroupForm.markAllAsTouched();
            return;
        }
        stepper.next();
    }
    public hideStepper() {
        this.stepperVisible = false;
    }
    getAllCustomerInventoryList(custId) {
        const url = `/inwards/getAllCustomerInventoryList?custId=${custId}`;
        this.customerManagementService.getCustNetworkLocDetail(url).subscribe(
            (response: any) => {
                this.customerInventoryList = response.dataList;
                const staffId = this.customerInventoryList[0]?.staffId;
                if (staffId) {
                    this.staffService.getStaffUserData(staffId).subscribe((response: any) => {
                        this.staffUserData = response.Staff;
                    });
                }
            },
            (error: any) => { }
        );
    }
    getActivePlanListDetails(custId) {
        const url = `/subscriber/getActivePlanList/${custId}?isNotChangePlan=true`;
        this.customerManagementService.getActivePlanList(url).subscribe(
            (response: any) => {
                this.activePlanList = response.dataList;
            },
            (error: any) => { }
        );
    }

    getPaymentHistory(custId) {
        const url = `/paymentHistory/${custId}`;
        this.customerManagementService.getPaymentHistory(url).subscribe(
            (response: any) => {
                this.paymentHistoryList = response.dataList;
            },
            (error: any) => { }
        );
    }
    roundAmount(amount: number): number {
        return Math.round(amount);
    }

    openAddressDetails(customerData) {
        this.ifUpdateAddressSubmited = false;
        this.newShiftifUpdateAddressSubmited = false;
        this.ifUpdateAddress = true;
        this.ifCafFollowUp = false;
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.newShiftselectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.iflocationFill = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.customerUpdateDiscount = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
        // if (customerData.serviceareaid) {
        //     this.shiftLocationDTO.updateAddressServiceAreaId = customerData.serviceareaid;
        //     this.getPartnerAllByServiceArea(customerData.serviceareaid);
        //     this.getStaffDetailById(customerData.serviceareaid);
        // }
        // if (customerData.partnerid) {
        //     this.shiftLocationDTO.shiftPartnerid = customerData.partnerid;
        // }
        // this.shiftLocationDTO.isPermanentAddress = false;
        // this.shiftLocationDTO.isPaymentAddresSame = false;
        // this.presentGroupForm.patchValue(customerData.addressList[0]);
        // this.shiftLocationEvent = false;

        if (customerData.serviceareaid) {
            this.newShiftshiftLocationDTO.updateAddressServiceAreaId = customerData.serviceareaid;
            this.newShiftgetPartnerAllByServiceArea(customerData.serviceareaid);
            this.getStaffDetailById(customerData.serviceareaid);
        }
        if (customerData.partnerid) {
            this.newShiftshiftLocationDTO.shiftPartnerid = customerData.partnerid;
        }
        this.newShiftshiftLocationDTO.isPermanentAddress = false;
        this.newShiftshiftLocationDTO.isPaymentAddresSame = false;
        this.newShiftpresentGroupForm.patchValue(customerData.addressList[0]);
        this.shiftLocationEvent = false;

        this.getStaffDetailId();
        this.newShiftsearchPrepaidValue();
        this.getMappingFrom();
        this.commondropdownService.getAllPinCodeData();
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        if (history.state.data) {
            this.custData = history.state.data;
            if (this.custData.serviceareaid) {
                this.newShiftisServiceInShiftLocation = true;
                this.newShiftshiftLocationDTO.updateAddressServiceAreaId = this.custData.serviceareaid;
                this.newShiftLocationPopId = this.custData.popid;
                this.newShiftLocationOltId = this.custData.oltid;

                this.newShiftgetPartnerAllByServiceArea(this.custData.serviceareaid);
                this.newShiftbranchByServiceAreaID(this.custData.serviceareaid);
                let serviceAreaId = {
                    value: Number(this.custData.serviceareaid)
                };
                this.newShiftselServiceArea(serviceAreaId, false);
                var customerAddress = this.custData.addressList.find(address => address.version === "NEW");
                // this.getStaffDetailById(customerData.serviceareaid)
                const data = {
                    value: Number(customerAddress.pincodeId)
                };
                this.newShiftselectPINCODEChange(data, "");
                this.newShiftgetAreaData(customerAddress.areaId, "present");
                // const data = {
                //     value: Number(customerAddress.pincodeId)
                // };
                // this.newShiftselectPINCODEChange(data, "");
                this.newShiftpresentGroupForm.patchValue({
                    pincodeId: Number(customerAddress.pincodeId)
                });
                let subAreaEvent = {
                    value: customerAddress.subareaId
                };
                this.newShiftonChangeSubArea(subAreaEvent, "present");
                this.newShiftbranchID = this.custData.branch;
            }
            if (this.custData.partnerid) {
                this.newShiftshiftLocationDTO.shiftPartnerid = this.custData.partnerid;
            }
            this.newShiftshiftLocationDTO.isPermanentAddress = false;
            this.newShiftshiftLocationDTO.isPaymentAddresSame = false;

            this.newShiftpresentGroupForm.patchValue(customerAddress);

            this.newShiftstaffSelectList = [];
        } else this.newShiftgetCustomersDetail(this.customerId);
        this.newShiftgetNewCustomerAddressForCustomer();
    }
    staffData: any = [];
    displayAmountModel = false;
    customerChangePlanDueAmount: any;
    custPackRelId: any;
    oldPlanId: any;

    getStaffDetailId(pageSize?: number) {
        if (pageSize) {
            this.approveStaffListdataitemsPerPageForStaff = pageSize;
        }
        const data = {
            page: this.currentPageParentStaffListdata,
            pageSize: this.approveStaffListdataitemsPerPageForStaff
        };
        const url = "/staffuser/list";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            this.approveCAFData = response.staffUserlist || [];
            this.currentStaffData = this.approveCAFData;
            this.currentPageStaffListIndex = 0;
            this.applyPagination(this.currentStaffData);
        });
    }
    modalOpenAmount() {
        this.displayAmountModel = true;
        this.matdialog.open(this.planDueAmountTemplate, {
            width: '600px',
        });
        this.getCustomerChangePlanDueAmount();
    }
    closeDisplayPlanAmountDetails() {
        this.displayAmountModel = false;
        this.matdialog.closeAll();
    }

    getCustomerChangePlanDueAmount() {
        this.custServiceData.forEach(element => {
            this.custPackRelId = element.planmapid;
            this.oldPlanId = element.planId;
        });
        let pojo = {
            custId: this.custDetilsCustId,
            custPackRelId: this.custPackRelId,
            oldPlanId: this.oldPlanId,
            newPlanId: this.selPlanData.id
        };

        const url = "/customers/getCustomerChangePlanDueAmount";
        this.customerManagementService
            .getCustomerChangePlanDueAmount(url, pojo)
            .subscribe((response: any) => {
                this.customerChangePlanDueAmount = response;
            });
    }

    saveShiftLocation() {
        this.ifUpdateAddressSubmited = true;

        if (this.shiftLocationDTO.shiftPartnerid === "") {
            return this;
        }

        if (this.shiftLocationChargeGroupForm.valid) {
            this.shiftLocationDTO.addressDetails = this.presentGroupForm.getRawValue();

            this.shiftLocationDTO.custChargeOverrideDTO = {
                billableCustomerId: this.shiftLocationChargeGroupForm.value.billableCustomerId,
                custChargeDetailsPojoList: [this.shiftLocationChargeGroupForm.value],
                custid: this.customerDetailData.id,
                paymentOwnerId: this.shiftLocationChargeGroupForm.value.paymentOwnerId
            };

            const url = "/shiftCustomerLocation/" + this.customerDetailData.id;
            this.commondropdownService.postMethod(url, this.shiftLocationDTO).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.message}`, 'Shift customer location successfully!');


                    this.getCustomersDetail(this.customerDetailData.id);
                    this.getCustomerNetworkLocationDetail(this.customerDetailData.id);
                    this.getNewCustomerAddressForCustomer(this.customerDetailData.id);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    getPlanbyPartner(serviceAreaId, partnerId) {
        this.isPartnerSelected = true;
        if (serviceAreaId) {
            this.filterPlanData = [];
            const url = `/partnerplans/serviceArea?planmode=NORMAL&serviceAreaId=${serviceAreaId}&partnerId=${partnerId}`;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planByServiceArea = response.partnerpostpaidplanList;
                    this.filterPlanData = response.partnerpostpaidplanList;
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    getPlangroupByPartner(partnerId) {
        this.isPartnerSelected = true;
        this.planDropdownInChageData = [];
        let partnerGroupurl = `/partnerplanGroupMappings?partnerId=${partnerId}&mode=""`;
        this.customerManagementService.getMethod(partnerGroupurl).subscribe((respose: any) => {
            this.filterPartnerPlanGroup = respose.planGroupList;
            this.filterNormalPlanGroup = respose.planGroupList;
            //   partnerGroupList.forEach(element => {
            //     this.filterPartnerPlanGroup.push(element.push);
            //     this.filterNormalPlanGroup.push(element.push);
            //   });
        });
    }

    closeParentCustt() {
        this.ifModelIsShow = false;
    }

    followupData: any;
    customersId: any;
    followupScheduleForm: UntypedFormGroup;
    followupPopupOpen: boolean;
    followupMinimumDate = new Date();

    closeFollowupForm: UntypedFormGroup;
    closeFollowupFormsubmitted: boolean = false;

    remarkFollowupForm: UntypedFormGroup;
    remarkFollowupFormsubmitted: boolean = false;

    reFollowupScheduleForm: UntypedFormGroup;
    reFollowupFormsubmitted: boolean = false;

    requiredFollowupInfo: any;
    scheduleFollowupPopupOpen() {
        // this.followupPopupOpen = true;
        this.followupScheduleForm.get("followUpName").disable();
        this.scheduledialogRef = this.matdialog.open(this.scheduleFollowDialog, {
            width: '1000px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });
        this.generatedNameOfTheFollowUp(this.customersId);
        // setTimeout(() => {
        //     $("#scheduleFollowup").modal("show");
        // }, 0);
    }
    onFollowupPageChange(event: PageEvent) {
        this.cafFollowupPage = event.pageIndex + 1;
        this.cafFollowupItemsPerPage = event.pageSize;
        this.getCafFollowupList(this.cafFollowupItemsPerPage);
    }


    closeFolloupPopup() {
        this.scheduledialogRef.close();
        this.followupScheduleForm = this.fb.group({
            id: [""],
            followUpName: ["", Validators.required],
            // followUpDatetime: ["", Validators.required],
            followUpDate: ["", Validators.required],
            followUpTime: ["", Validators.required],
            remarks: ["", Validators.required],
            //status: ["", Validators.required],
            isMissed: [false],
            leadMasterId: [""]
        });
        $("#scheduleFollowup").modal("hide");
    }

    openCafFollowup(customerData) {
        this.customersId = customerData.id;
        this.getCafFollowupList("");
        this.customerStatusView = false;
        this.customerCafNotes = false;
        this.ifCafFollowupSubmited = false;
        this.ifCafFollowUp = true;
        this.ifUpdateAddress = false;
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.iflocationFill = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.customerUpdateDiscount = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false
    }

    onTimeChange(event: any) {
        const time = event.value;
        const currentValue = this.followupScheduleForm.get('followUpDatetime')?.value || new Date();

        const updatedDate = new Date(currentValue);

        if (time?.hour !== undefined) {
            updatedDate.setHours(time.hour, time.minute);
        } else if (typeof time === 'string') {
            const [hours, minutes] = time.split(':');
            updatedDate.setHours(+hours, +minutes);
        }
        this.followupScheduleForm.get('followUpDatetime')?.setValue(updatedDate);
    }

    saveCafFollowup() {
        this.ifCafFollowupSubmited = true;
        if (this.followupScheduleForm.valid) {
            const url = "/cafFollowUp/save";
            this.followupData = this.followupScheduleForm.value;
            this.followupData.followUpName = this.generateNameOfFollowUp;
            this.followupData.customersId = this.customersId;
            this.followupData.staffUserId = this.staffid;
            this.followupData.mvnoId = this.mvnoid;
            this.followupData.isMissed = false;
            this.followupData.isSend = false;
            this.followupData.status = "Pending";
            const date = this.followupScheduleForm.get('followUpDate')?.value;
            const time = this.followupScheduleForm.get('followUpTime')?.value;
            // Combine date and time (ensure you parse them properly as Date and string)
            const combinedDateTime = new Date(date);
            if (time) {
                if (typeof time === 'string') {
                    const [hours, minutes] = time.split(':');
                    combinedDateTime.setHours(+hours, +minutes, 0, 0);
                } else if (time instanceof Date) {
                    combinedDateTime.setHours(time.getHours(), time.getMinutes(), 0, 0);
                } else {
                    // add additional type handling if needed
                    console.warn('Unrecognized time format:', time);
                }
            }
            this.followupData = { ...this.followupScheduleForm.value };
            this.followupData.followUpDatetime = this.datePipe.transform(
                combinedDateTime, 'yyyy-MM-dd HH:mm:ss'
            );
            // const myFormattedDate = this.datePipe.transform(
            //     this.followupScheduleForm.value.followUpDatetime,
            //     'yyyy-MM-dd HH:mm:ss'
            // );
            // this.followupData.followUpDatetime = myFormattedDate;
            this.customerManagementService.postMethod(url, this.followupData).subscribe(
                (response: any) => {
                    this.ifCafFollowupSubmited = false;
                    this.scheduledialogRef.close();
                    this.getCafFollowupList("");
                    this.followupScheduleForm.reset();
                    this.toastr.success(`Succesfully Created`, 'Success!');
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
            this.ifCafFollowupSubmited = false;
        }
    }

    rescheduleFollowUp(followUpDetails) {

        this.dateTime = new Date();
        this.dateTime =
            this.dateTime > new Date(followUpDetails.followUpDatetime)
                ? this.dateTime
                : new Date(followUpDetails.followUpDatetime);
        this.followUpId = followUpDetails.id;
        this.generatedNameOfTheReFollowUp(this.customersId);
        this.reFollowupFormsubmitted = false;
        // $("#reScheduleFollowup").modal("show");
        this.reFollowupScheduleForm.get("followUpName").disable();
        this.matdialog.open(this.rescheduleFollowUpsModal, {
            width: "1000px",
            disableClose: true
        });
    }

    saveReFollowup() {
        this.followupData = {};
        this.reFollowupFormsubmitted = true;
        if (this.reFollowupScheduleForm.valid) {
            this.followupData = this.reFollowupScheduleForm.value;
            this.followupData.followUpName = this.generateNameOfReFollowUp;
            this.followupData.customersId = this.customersId;
            this.followupData.staffUserId = this.staffid;
            this.followupData.mvnoId = this.mvnoid;
            this.followupData.isSend = false;
            this.followupData.status = "Pending";
            const date = this.reFollowupScheduleForm.get('followUpDate')?.value;
            const time = this.reFollowupScheduleForm.get('followUpTime')?.value;
            // Combine date and time (ensure you parse them properly as Date and string)
            const combinedDateTime = new Date(date);
            if (time) {
                if (typeof time === 'string') {
                    const [hours, minutes] = time.split(':');
                    combinedDateTime.setHours(+hours, +minutes, 0, 0);
                } else if (time instanceof Date) {
                    combinedDateTime.setHours(time.getHours(), time.getMinutes(), 0, 0);
                } else {
                    console.warn('Unrecognized time format:', time);
                }
            }
            this.followupData.followUpDatetime = this.datePipe.transform(combinedDateTime, 'yyyy-MM-dd HH:mm:ss');
            // this.followupData = {...this.followupScheduleForm.value};
            // this.followupData.followUpDatetime = this.datePipe.transform(
            //     combinedDateTime, 'yyyy-MM-dd HH:mm:ss'
            // );
            // const myFormattedDate = this.datePipe.transform(
            //     this.followupData.followUpDatetime,
            //     "yyyy-MM-dd HH:mm:ss"
            // );
            // this.followupData.followUpDatetime = myFormattedDate;
            const url =
                "/cafFollowUp/reSchedulefollowup?followUpId=" +
                this.followUpId +
                "&remarks=" +
                this.followupData.remarksTemp;

            this.customerManagementService.postMethod(url, this.followupData).subscribe(
                (response: any) => {
                    this.matdialog?.closeAll();
                    this.reFollowupFormsubmitted = false;
                    this.reFollowupScheduleForm.reset();
                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                    $("#reScheduleFollowup").modal("hide");
                    this.reFollowupFormsubmitted = false;
                    this.getCafFollowupList("");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                }
            );
            this.reFollowupFormsubmitted = false;
        } else {
            this.reFollowupScheduleForm.markAllAsTouched();
        }
    }

    closeReFolloupPopup() {
        this.reFollowupFormsubmitted = false;
        $("#reScheduleFollowup").modal("hide");
        this.matdialog.closeAll();
        this.reFollowupScheduleForm.reset();
    }

    rescheduleFollowupRemarks = [
        "Confirm Later",
        "Do Not Call",
        "Expensive Package",
        "Call rejected by Client"
    ];
    cafFollowupList: any = [];
    cafFollowupDatalength = 0;
    cafFollowupPage = 1;
    cafFollowupItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    followupListTotalRecordsForUserAndTeam: any;
    followupListForUserAndTeam: any;

    getCafFollowupList(size) {
        let page = this.cafFollowupPage || 1; // Default to page 1
        if (size) {
            this.cafFollowupItemsPerPage = size;
        }
        let sizeToUse = this.cafFollowupItemsPerPage || 10;

        const url =
            `/cafFollowUp/findAll?customerId=${this.customersId}&page=${page}&pageSize=${sizeToUse}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.cafFollowupList = response?.dataList || [];
                this.followupListTotalRecordsForUserAndTeam = response?.totalRecords || this.cafFollowupList.length;

                // You can remove the following conditional if your backend properly paginates
                if (this.showItemPerPage > this.cafFollowupItemsPerPage) {
                    this.cafFollowupDatalength = this.cafFollowupList.length % this.showItemPerPage;
                } else {
                    this.cafFollowupDatalength = this.cafFollowupList.length % this.cafFollowupItemsPerPage;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }


    pageChangedCafFollowup(pageNumber): void {
        this.cafFollowupPage = pageNumber;
        this.getCafFollowupList("");
    }

    totalCafFollowupItems(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.cafFollowupPage > 1) {
            this.cafFollowupPage = 1;
        }
        this.getCafFollowupList(this.showItemPerPage);
    }

    generateNameOfFollowUp: any;
    generatedNameOfTheFollowUp(customersId) {
        const url = "/cafFollowUp/generateNameOfTheCafFollowUp/" + customersId;

        this.customerManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.generateNameOfFollowUp = await response.data;
                this.generateNameOfFollowUp
                    ? this.followupScheduleForm.controls["followUpName"].setValue(this.generateNameOfFollowUp)
                    : "";
            },
            async (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Something went wrong with 'followup name.' Generation!");

            }
        );
    }

    generateNameOfReFollowUp: any;
    generatedNameOfTheReFollowUp(customersId) {
        const url = "/cafFollowUp/generateNameOfTheCafFollowUp/" + customersId;

        this.customerManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.generateNameOfReFollowUp = await response.data;
                this.generateNameOfReFollowUp
                    ? this.reFollowupScheduleForm.controls["followUpName"].setValue(
                        this.generateNameOfReFollowUp
                    )
                    : "";
            },
            async (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Something went wrong with 'followup name.' Generation!");

            }
        );
    }

    checkFollowUpDatetimeOutDate(obj) {
        if (obj != null && obj != undefined) {
            if (obj.status && obj.status === "Pending") {
                if (obj.followUpDatetime && new Date(obj.followUpDatetime) < new Date()) {
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    followUpId: any;
    closeFollowUp(followUpDetails) {
        this.closeFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        // $("#closeFollowup").modal("show");
        this.matdialog.open(this.closeFollowUpsModal, {
            width: "600px",
            disableClose: true
        });
    }

    closeActionFolloupPopup() {
        $("#closeFollowup").modal("hide");
    }

    saveCloseFollowUp() {
        this.closeFollowupFormsubmitted = true;
        if (this.closeFollowupForm.valid) {
            const url =
                "/cafFollowUp/closefollowup?followUpId=" +
                this.followUpId +
                "&remarks=" +
                this.closeFollowupForm.get("remarks").value;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    // $("#closeFollowup").modal("hide");
                    this.matdialog.closeAll();
                    this.closeFollowupForm.reset();
                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                    this.getCafFollowupList("");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                }
            );
            this.closeFollowupFormsubmitted = false;
        }
    }

    rejectLeadPopupOpen(leadId) {
        this.rejectCustomerCAFForm.reset();
        this.matdialog.open(this.openRejectLeadDialog, {
            width: '1000px'
        })
        this.rejectedReasonList = [];
        this.leadId = leadId;
        this.rejectedReasonId = null;
        $("#openRejectLeadPopup").modal("show");
        this.prepaidRejectedReasonService.getMethod("/rejectReason/all").subscribe(
            async (response: any) => {
                if (response.rejectReasonList && response.rejectReasonList.content.length > 0) {
                    response.rejectReasonList.content.forEach((item: any) =>
                        item?.status === "Active" ? this.rejectedReasonList.push(item) : ""
                    );
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    rejectLead(leadId: any) {

        this.rejectedLeadFormSubmitted = true;
        if (this.rejectLeadFormGroup.valid) {
            if (leadId !== "") {

                let rejectDTOObj = {
                    cafId: leadId,
                    rejectReasonId: this.rejectLeadFormGroup.controls.rejectReasonId.value,
                    rejectSubReasonId: this.rejectLeadFormGroup.controls.rejectSubReasonId.value,
                    remark: this.rejectLeadFormGroup.controls.remark.value
                };

                const url = "/close";

                this.prepaidRejectedReasonService.postMethod(url, rejectDTOObj).subscribe(
                    async (response: any) => {
                        this.rejectedLeadFormSubmitted = false;
                        this.getcustomerList("");
                        if ((await response.status) === 200) {
                            this.toastr.success(`${response.msg}`, 'Success!');

                            // this.getLeadList("");
                            $("#openRejectLeadPopup").modal("hide");
                            this.matdialog.closeAll()
                            this.rejectLeadFormGroup.reset();
                        } else {
                            this.toastr.error(`${response.msg}`, 'Failed!');


                            // this.getLeadList("");
                            $("#openRejectLeadPopup").modal("hide");
                            this.matdialog.closeAll();
                            this.rejectLeadFormGroup.reset();
                        }
                    },
                    (error: any) => {
                        console.log(error);
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        // this.getLeadList("");
                        $("#openRejectLeadPopup").modal("hide");
                        this.matdialog.closeAll();
                        this.rejectLeadFormGroup.reset();
                    }
                );
            } else {
                $("#openRejectLeadPopup").modal("hide");
                this.matdialog.closeAll();
                // this.getLeadList("");

                this.rejectLeadFormGroup.reset();
            }
        } else {
            $("#openRejectLeadPopup").modal("show");
        }
    }

    rejectedSubReasonArr: any;

    selectRejectedReason(id: any) {
        this.rejectedSubReasonArr = [];
        this.rejectedReasonId = id;
        this.rejectedReasonList?.forEach(source =>
            source.rejectSubReasonDtoList?.forEach(subreason =>
                subreason.rejectReasonId === this.rejectedReasonId
                    ? this.rejectedSubReasonArr.push(subreason)
                    : ""
            )
        );
    }

    closeRejectLeadPopup() {
        $("#openRejectLeadPopup").modal("hide");
        this.matdialog.closeAll()
        this.rejectLeadFormGroup.reset();
    }

    followUpDetailsObj: any;
    remarkFollowUp(followUpDetails) {
        this.followUpDetailsObj = followUpDetails;
        this.remarkFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        this.getfollowUpRemarkList(this.followUpId);
        // $("#remarkScheduleFollowup").modal("show");
        this.matdialog.open(this.remarkScheduleFollowupModal, {
            width: "1000px",
            disableClose: true
        });
    }

    closeRemarkPopup() {
        this.remarkFollowupForm.reset();
        this.remarkFollowupFormsubmitted = false;
        $("#remarkScheduleFollowup").modal("hide");
    }

    saveRemarkFollowUp() {
        this.remarkFollowupFormsubmitted = true;
        this.remarkFollowupForm.get("cafFollowUpId").setValue(this.followUpId);
        if (this.remarkFollowupForm.valid) {
            var data = this.remarkFollowupForm.value;
            data.cafFollowUpId = this.followUpId;
            data.mvnoId = this.mvnoid;

            const url = "/cafFollowUp/cafFollowUp/remark";
            this.customerManagementService.postMethod(url, data).subscribe(
                async (response: any) => {
                    // $("#remarkScheduleFollowup").modal("hide");
                    this.matdialog.closeAll();
                    this.remarkFollowupForm.reset();
                    await this.getCafFollowupList("");
                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
            this.remarkFollowupFormsubmitted = false;
        }
    }

    followUpRemarkList: any = [];
    tableWrapperRemarks: any = "";
    scrollIdRemarks: any = "";
    getfollowUpRemarkList(id) {
        this.tableWrapperRemarks = "";
        this.scrollIdRemarks = "";

        const url = "/cafFollowUp/findAll/cafFollowUpRemark/" + id;
        this.customerManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.followUpRemarkList = await response.dataList;
                if (this.followUpRemarkList && this.followUpRemarkList?.length > 3) {
                    this.tableWrapperRemarks = "table-wrapper";
                    this.scrollIdRemarks = "table-scroll-remark";
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    makeACall() {
        this.toastr.info(`Sorry! Please configure call client first..`, 'Info!')
    }

    getPartnerAllByServiceArea(serviceAreaId) {
        const url = "/getPartnerByServiceAreaId/" + serviceAreaId;
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerListByServiceArea = response.partnerList;
                this.partnerList = response.partnerList.filter(item => item.id != 1);
            },
            (error: any) => { }
        );
    }

    isCustDocPending(cafId, nextApproverId) {
        // this.customerDocumentService.isCustDocPending(cafId).subscribe(
        //   (response: any) => {
        //     if (response.data) {
        //       this.messageService.add({
        //         severity: "info",
        //         summary: "Info",
        //         detail: "Customer cannot activate. Document Verification Pending",
        //         icon: "far fa-times-circle"
        //       });
        //     } else {

        this.matdialog.open(this.Approve);
        this.approved = false;
        this.selectStaff = null;
        this.assignCustomerCAFModal = true;
        this.assignCustomerCAFId = cafId;
        this.nextApproverId = nextApproverId;
        //     }
        //   },
        //   (error: any) => {
        //     this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //       detail: error.error.errorMessage,
        //       icon: "far fa-times-circle"
        //     });
        //   }
        // );
    }

    ngOnInit(): void {
        this.getBankDestinationDetail();
        this.isVisibleCAFHomeComponent = true;
        this.commondropdownService.getAllCurrencyData();
        this.demographicLabel = RadiusConstants.DEMOGRAPHICDATA || [];
        this.addNotesForm = this.fb.group({
            id: [""],
            notes: ["", Validators.required]
        });
        this.macManagementGroup = this.fb.group({
            macAddress: ["", [Validators.required]],
            custid: [""],
            custsermappingid: [""]
        });
        this.rejectedSubReasonArr = [];
        this.mvnoid = Number(localStorage.getItem("mvnoId"));
        this.staffid = Number(localStorage.getItem("userId"));
        if (this.custType == "Postpaid") {
            this.planDetailsCategory = this.planDetailsCategory.filter(cat => cat.value != "groupPlan");
        }
        this.getLoggedinUserData();
        this.custLedgerForm = this.fb.group({
            startDateCustLedger: ["", Validators.required],
            endDateCustLedger: ["", Validators.required]
        });
        window.scroll(0, 0);
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

        this.assignAppRejectDiscountForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.customerGroupForm = this.fb.group({
            isCredentialMatchWithAccountNo: [true],
            username: ["", Validators.required],
            password: ["", [Validators.required, this.noSpaceValidator]],
            firstname: ["", Validators.required],
            lastname: ["", Validators.required],
            billday: ["", Validators.required],
            email: ["", [Validators.email]],
            title: [""],
            pan: ["", [Validators.minLength(this.commondropdownService.commonPanNumberLength), Validators.maxLength(this.commondropdownService.commonPanNumberLength)]],
            gst: [""],
            aadhar: [""],
            passportNo: [""],
            //   tinNo: ["", [Validators.minLength(9), Validators.maxLength(9)]],
            contactperson: ["", Validators.required],
            failcount: ["0"],
            //   acctno: [""],
            custtype: [this.custType],
            custlabel: ["customer"],
            feasibilityRequired: [""],
            feasibilityRemark: [""],
            //   phone: ["", [Validators.pattern("^[0-9]*$")]],
            mobile: ["", [Validators.required, Validators.minLength(3)]],
            secondaryMobile: ["", Validators.minLength(3)],
            countryCode: [this.commondropdownService.commonCountryCode || '+91'],
            //   dunningType: [""],
            //   dunningSubType: [""],
            //   dunningSector: [""],
            //   dunningSubSector: [""],
            //   cafno: [""],
            voicesrvtype: [""],
            didno: [""],
            calendarType: ["English", Validators.required],
            partnerid: [""],
            //   salesremark: [""],
            servicetype: [""],
            serviceareaid: ["", Validators.required],
            // customerType: ["customer", Validators.required],
            status: [""],
            parentCustomerId: [""],
            invoiceType: ["", Validators.required],
            parentExperience: ["Actual", Validators.required],
            latitude: [""],
            longitude: [""],
            houseNumber: [""],
            // customerCategory: ["", Validators.required],
            birthDate: [""],
            discount: ["", [Validators.min(-99), Validators.max(99)]],
            plangroupid: [""],
            discountType: [""],
            discountExpiryDate: [""],
            flatAmount: [""],
            //   id: [],
            billTo: ["CUSTOMER"],
            billableCustomerId: [""],
            isInvoiceToOrg: [false],
            istrialplan: [false],
            popid: [""],
            staffId: [""],
            branch: [""],
            planMappingList: (this.payMappingListFromArray = this.fb.array([])),
            addressList: (this.addressListFromArray = this.fb.array([])),
            overChargeList: (this.overChargeListFromArray = this.fb.array([])),
            custMacMapppingList: (this.custMacMapppingListFromArray = this.fb.array([])),
            custdisplayIpMappingList: (this.ipMapppingdisplayListFromArray = this.fb.array([])),
            custIpMappingList: (this.ipMapppingListFromArray = this.fb.array([])),
            paymentDetails: this.fb.group({
                amount: [""],
                paymode: [""],
                referenceno: [""],
                paymentdate: [""]
            }),
            isCustCaf: ["yes"],
            //   valleyType: [""],
            customerArea: [""],
            framedIpBind: [""],
            ipPoolNameBind: [""],
            dunningCategory: ["", Validators.required],
            oltid: [""],
            masterdbid: [""],
            splitterid: [""],
            departmentId: [""],
            locations: [],
            parentQuotaType: [""],
            isParentLocation: [""],
            //   framedIpv6Address: [""],
            //   VLANID: [""],
            //   nasIpAddress: [""],
            //   nasPort: [""],
            //   framedIp: [""],
            //   maxconcurrentsession: ["", Validators.pattern(Regex.numeric)],
            addparam1: [""],
            addparam2: [""],
            addparam3: [""],
            addparam4: [""],
            earlybillday: [""],
            blockNo: [""],
            //   drivingLicence: [""],
            serviceareaName: [""],
            customerVrn: [""],
            customerNid: [""],
            renewPlanLimit: [""],
            graceDay: [{ value: 0, disabled: this.iscustomerEdit }, [Validators.max(30)]],
            loginUsername: ["", Validators.required],
            loginPassword: ["", [Validators.required, this.noSpaceValidator]],
            currency: [""]
        });

        this.customerGroupForm.get("isCredentialMatchWithAccountNo")?.valueChanges.subscribe(value => {
            this.isCredentialMatch = true;
            if (!value) {
                this.customerGroupForm.get("isCredentialMatchWithAccountNo")?.setValue(true, { emitEvent: false });
            }
        });
        this.isCredentialMatchWithAccountNumber(true);

        this.locationMacForm = this.fb.group({
            location: [""],
            mac: [""]
        });

        if (this.custType === "Postpaid") {
            this.customerGroupForm.controls["billday"].setValidators(Validators.required);
            this.customerGroupForm.controls["billday"].updateValueAndValidity();
            this.customerGroupForm.controls.earlybillday.setValidators(Validators.required);
        }
        this.customerGroupForm.controls.invoiceType.disable();
        this.customerGroupForm.controls.parentExperience.disable();
        this.planGroupForm = this.fb.group({
            planId: ["", Validators.required],
            service: ["", Validators.required],
            serviceId: ["", Validators.required],
            validity: [""],
            offerprice: [""],
            newAmount: [""],
            discount: ["", [Validators.min(-99), Validators.max(99)]],
            discountType: [""],
            discountExpiryDate: [""],
            istrialplan: [""],
            invoiceType: [""],
            currency: [""]
        });
        this.planGroupForm.get("discountType")?.valueChanges.subscribe(value => {
            const discountExpiryDateControl = this.planGroupForm.get("discountExpiryDate");

            if (value?.toLowerCase() === "recurring") {
                discountExpiryDateControl?.setValidators(Validators.required);
            } else {
                discountExpiryDateControl?.clearValidators();
            }

            discountExpiryDateControl?.updateValueAndValidity();
        });
        this.planGroupForm.controls.invoiceType.disable();
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

        this.macGroupForm = this.fb.group({
            macAddress: ["", Validators.required]
        });
        this.ipdisplayManagementGroup = this.fb.group({
            ipAddress: [
                "",
                [Validators.required, Validators.pattern("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")]
            ],
            ipType: ["", Validators.required]
        });
        this.ipManagementGroup = this.fb.group({
            ipAddress: [
                "",
                [Validators.required, Validators.pattern("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")]
            ],
            ipType: ["", Validators.required],
            custid: [""],
            custsermappingid: [""]
        });
        this.validityUnitFormArray = this.fb.array([]);
        this.plansArray = this.fb.array([]);
        this.validityUnitFormGroup = this.fb.group({
            validityUnit: [""]
        });
        this.presentGroupForm = this.fb.group({
            addressType: ["Present", Validators.required],
            landmark: ["", Validators.required],
            areaId: ["", Validators.required],
            pincodeId: ["", Validators.required],
            cityId: ["", Validators.required],
            stateId: ["", Validators.required],
            countryId: ["", Validators.required],
            subareaId: [""],
            subareaName: [""],
            building_mgmt_id: [""],
            buildingNumber: [""],
            landmark1: [""],
            version: ["New"]
        });

        this.presentGroupForm?.get("cityId").disable();
        this.presentGroupForm?.get("stateId").disable();
        this.presentGroupForm?.get("countryId").disable();

        this.paymentGroupForm = this.fb.group({
            addressType: ["Payment", Validators.required],
            landmark: [""],
            areaId: [""],
            pincodeId: [""],
            cityId: [""],
            stateId: [""],
            countryId: [""],
            landmark1: [""],
            subareaId: [""],
            building_mgmt_id: [""],
            buildingNumber: [""],
            version: ["New"]
        });
        this.permanentGroupForm = this.fb.group({
            addressType: ["Permanent"],
            landmark: [""],
            areaId: [""],
            pincodeId: [""],
            cityId: [""],
            stateId: [""],
            countryId: [""],
            subareaId: [""],
            building_mgmt_id: [""],
            buildingNumber: [""],
            landmark1: [""],
            version: ["New"]
        });

        this.assignCustomerCAFForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectCustomerCAFForm = this.fb.group({
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

        // this.customerGroupForm.controls.dunningSubType.disable();
        // this.customerGroupForm.controls.dunningSubSector.disable();
        this.searchInvoiceMasterFormGroup = this.fb.group({
            billfromdate: [""],
            billrunid: [""],
            billtodate: [""],
            custMobile: ["", Validators.minLength(3)],
            custname: [""],
            docnumber: [""],
            customerid: [""]
        });

        this.followupScheduleForm = this.fb.group({
            id: [""],
            followUpName: ["", Validators.required],
            // followUpDatetime: ["", Validators.required],
            followUpDate: ["", Validators.required],  // <-- must exist
            followUpTime: ["", Validators.required],
            remarks: ["", Validators.required],
            isMissed: [false],
            customersId: []
        });

        this.closeFollowupForm = this.fb.group({
            followUpId: [""],
            remarks: ["", Validators.required]
        });

        this.remarkFollowupForm = this.fb.group({
            cafFollowUpId: [""],
            remark: ["", Validators.required]
        });

        this.reFollowupScheduleForm = this.fb.group({
            id: [""],
            followUpName: ["", Validators.required],
            // followUpDatetime: ["", Validators.required],
            followUpDate: ["", Validators.required],  // <-- must exist
            followUpTime: ["", Validators.required],
            remarks: ["", Validators.required],
            isMissed: [false],
            customersId: [],
            remarksTemp: [""]
        });

        // Change Plan
        this.changePlanForm = this.fb.group({
            purchaseType: ["", Validators.required],
            planType: ["upgrade",],
            planId: ["", Validators.required],
            planGroupId: ["", Validators.required],
            planList: [""],
            isPaymentReceived: [false],
            remarks: ["", Validators.required],
            paymentOwnerId: [""],
            billableCustomerId: [""],
            recordPaymentDTO: this.fb.group({
                paymentAmount: ["", Validators.required],
                paymentDate: ["", Validators.required],
                paymentMode: ["", Validators.required],
                referenceNo: ["", Validators.required],
                chequeNo: ["", Validators.required],
                bankName: ["", Validators.required],
                chequeDate: ["", Validators.required],
                branch: ["", Validators.required],
                remarks: ["", Validators.required]
                // tdsDeducted: ['', Validators.required],
                // tdsAmount: ['', Validators.required],
            }),
            addonStartDate: [this.currentData],
            ChangePlanCategory: [""]
        });
        this.childPlanRenewArray = this.fb.array([]);
        this.changePlanForm.get("planGroupId").disable();
        this.changePlanForm.get("planList").disable();
        this.changePlanForm.get("recordPaymentDTO").disable();

        this.chargenewPlanForm = this.fb.group({
            plancharge: ["", Validators.required]
        });

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
        this.addChargeForm = this.fb.group({
            chargeAdd: [""]
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
        this.newPlanSelectArray = this.fb.array([]);
        this.changenewPlanForm = this.fb.group({
            ChangePlanCategory: ["", Validators.required]
        });
        // dropdown
        // if (this.statusCheckService.isActiveInventoryService) this.commondropdownService.getPOPList();
        // this.commondropdownService.getplanservice();this api will removed and go to customer create and edit
        // this.commondropdownService.getAllPinCodeNumber();
        // this.commondropdownService.getAllPinCodeData(); this api will removed and go to customer create and edit and shiftlocation
        // this.commondropdownService.getALLArea();
        // this.commondropdownService.getCommonListTitleData(); this api will removed by shivam
        // this.commondropdownService.getCommonListPaymentData();
        // this.commondropdownService.getIppoolData();this api will removed by shivam
        // this.commondropdownService.getPostpaidplanData(); this api will removed and go to customer create and edit and shiftlocation
        // this.commondropdownService.getCountryList();this api will removed and go to customer create and edit and shiftlocation
        // this.commondropdownService.getStateList();this api will removed and go to customer create and edit and shiftlocation
        // this.commondropdownService.getCityList();this api will removed and go to customer create and edit and shiftlocation
        // this.commondropdownService.getChargeForCustomer();this api will removed by shivam
        this.commondropdownService.getsystemconfigList();
        // this.commondropdownService.getchargeAll(); this api will removed by shivam
        //this.commondropdownService.getChargeTypeByList(); this api will removed and go to customer changeplan
        this.getCustomerStatus();
        // this.commondropdownService.findAllplanGroups(); this api will removed and go to customer create and edit
        // this.commondropdownService.getBillToData();
        // this.commondropdownService.getAllActiveBranch();
        // this.commondropdownService.getValleyTypee(); this api will removed and go to customer create and edit
        // this.commondropdownService.getInsideValley(); this api will removed and go to customer create and edit
        // this.commondropdownService.getOutsideValley(); this api will removed and go to customer create and edit
        // this.commondropdownService.getBankDetail();
        // this.commondropdownService.getBankDestinationDetail();
        // this.commondropdownService.getPlanPurchaseType(); this api will removed and go to customer change plan
        this.commondropdownService.getAllActiveStaff();
        this.getStaffNameSort();
        this.commondropdownService.getTeamList();
        // this.getCustomerType();
        // this.getCustomerSector(); this api will removed and go to customer create and edit
        // this.getBankDetail();
        // this.getBillToData();this api will removed and go to customer create and edit

        this.planCreationType();
        this.commondropdownService.panNumberLength$.subscribe(panLength => {
            if (panLength) {
                this.customerGroupForm
                    .get("pan")
                    ?.setValidators([Validators.minLength(panLength), Validators.maxLength(panLength)]);
                this.customerGroupForm.get("pan")?.updateValueAndValidity();
            }
        });
        const serviceArea = localStorage.getItem("serviceArea");
        const serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.getserviceAreaListForCafCustomer();
            // this.commondropdownService.filterPartnerAll();
        } else {
            this.commondropdownService.getserviceAreaListForCafCustomer();
            // this.commondropdownService.getpartnerAll();
        }
        // this.getpartnerAll();this api will removed and go to customer create and edit
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
        // this.billingSequence();this api will removed and go to customer create and edit
        // this.getcustomerList("");
        setTimeout(() => {
            this.selCustType();
        }, 3000);

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
            pageSize: "",
            status: RadiusConstants.CUSTOMER_STATUS.NEW_ACTIVATION,
            fromDate: "",
            toDate: ""
        };

        this.assignAppRejectShiftLocationForm = this.fb.group({
            remark: ["", Validators.required]
        });

        this.rejectLeadFormGroup = this.fb.group({
            leadMasterId: [""],
            rejectReasonId: ["", Validators.required],
            rejectSubReasonId: [""],
            remark: ["", Validators.required],
            leadStatus: ["Closed"]
        });
        // this.getrequiredDepartment();this api will removed and go to customer create and edit

        // this.customerStatusDetail = [
        //   {
        //     teamName: 'Opration',
        //     status: 'approved',
        //   },
        //   {
        //     teamName: 'Qa',
        //     status: 'approved',
        //   },
        //   {
        //     teamName: 'Payment',
        //     status: 'approved',
        //   },
        //   {
        //     teamName: 'Customer Care',
        //     status: 'inprogress',
        //   },
        //   {
        //     teamName: 'Parent Team',
        //     status: 'pending',
        //   },
        // ]

        // this.getAllLocation(); this api will removed and go to customer create and edit and location
        this.macMapppingListFromArray = this.fb.array([]);

        // Changes for Shift Location

        this.newShiftLocationChargeGroupForm = this.fb.group({
            chargeid: [""],
            price: [""],
            actualprice: [""],
            charge_date: [""],
            type: [""],
            discount: [""],
            billingCycle: [""],
            id: [""],
            billableCustomerId: [""],
            paymentOwnerId: [""]
        });
        this.newShiftpresentGroupForm = this.fb.group({
            addressType: ["Present", Validators.required],
            landmark: ["", Validators.required],
            areaId: ["", Validators.required],
            pincodeId: ["", Validators.required],
            cityId: ["", Validators.required],
            stateId: ["", Validators.required],
            countryId: ["", Validators.required],
            landmark1: [""],
            subareaId: [""],
            building_mgmt_id: [""],
            buildingNumber: [""]
        });
        this.newShiftassignAppRejectShiftLocationForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.newShiftassignDocForm = this.fb.group({
            remark: ["", Validators.required]
        });
        // this.newShiftgetpartnerAll();this api will removed by shivam
        // if (this.statusCheckService.isActiveInventoryService) {
        //     this.commondropdownService.getPOPList();
        // }this api will removed and go to customer create and edit
        // this.commondropdownService.getCityList();
        // this.commondropdownService.getStateList();
        // this.commondropdownService.getCountryList();
        // this.commondropdownService.getChargeTypeByList();
        // this.commondropdownService.getChargeTypeByList();
        // this.commondropdownService.getAllPinCodeNumber();
        // this.commondropdownService.getAllPinCodeData();
        this.newShiftlocationFormRemark = this.fb.group({
            remark: [""]
        });
        // this.newShiftgetNewCustomerAddressForCustomer();

        const today = new Date();
        this.dateOfBirth = today.toISOString().split("T")[0];
        // this.checkPaymentGatewayConfiguration();this api will removed and go to customer invoice
        this.mpinForm = this.fb.group({
            countryCode: [""],
            mobileNumber: ["", [Validators.required, Validators.maxLength(10)]]
        });

        this.route.queryParams.subscribe(params => {
            let mobileno = params["mobilenumber"];
            if (mobileno) {
                this.searchOption = "mobile";
                this.searchDeatil = mobileno;
            }
            // this.getcustomerList("");
        });
        // this.getAllActiveStaffData();
        // this.getAllSubAreaData();this api will removed and go to customer create and edit
        // this.getAllBuildingData();this api will removed and go to customer create and edit
        // this.getMappingFrom();
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
            this.systemConfigCurrency = res.data.value;
        });

        this.systemService.getConfigurationByName("CONVERTED_EXCHANGE_RATE").subscribe((res: any) => {
            this.convertedExchangeRate = parseFloat(res?.data?.value.replace(/,/g, "")) || 1;
        });
        this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
            if (lengthObj) {
                this.mpinForm
                    .get("mobileNumber")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(lengthObj.min),
                        Validators.maxLength(lengthObj.max)
                    ]);
                this.mpinForm.get("mobileNumber")?.updateValueAndValidity();
            }
        });
        this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
            if (lengthObj) {
                this.customerGroupForm
                    .get("mobile")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(lengthObj.min),
                        Validators.maxLength(lengthObj.max)
                    ]);
                this.customerGroupForm.get("mobile")?.updateValueAndValidity();
            }
        });
        this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
            if (lengthObj) {
                this.customerGroupForm
                    .get("secondaryMobile")
                    ?.setValidators([
                        Validators.minLength(lengthObj.min),
                        Validators.maxLength(lengthObj.max)
                    ]);
                this.customerGroupForm.get("secondaryMobile")?.updateValueAndValidity();
            }
        });
        this.setDefaultFilter();
        this.getCommonFeasibilityList();
        this.getChangePlanType();
    }
    getChangePlanType() {
        this.customerService.getChangePlanTypeList().subscribe((response: any) => {
            this.planUpgradeDowngrade = response.dataList;
            let plan = this.planUpgradeDowngrade.find(x => x.text == "Upgrade");
            this.selectedPlanType = plan.text;
            // this.changePlanForm.controls["planType"].setValue(this.planUpgradeDowngrade[0].value);
        });
        this.createStepGroups();

    }
    ngAfterViewInit() {



        this.staffData.paginator = this.paginator;
        this.staffData.sort = this.sort;

        this.customerListData.sort = this.sort;
        this.customerListData.paginator = this.paginator;
        // this.matdialog.open(this.customerView);
    }

    getStaffNameSort() {
        const url = "/staffList/dropdown/all2";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.activeStaffListNameSort = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    // createStepGroups() {
    //     // Step 1: Basic Details
    //     this.step1Group = this.fb.group({
    //         firstname: this.customerGroupForm.get('firstname'),
    //         lastname: this.customerGroupForm.get('lastname'),
    //         contactperson: this.customerGroupForm.get('contactperson'),
    //         billday: this.customerGroupForm.get('billday'),
    //         earlybillday: this.customerGroupForm.get('earlybillday'),
    //         isCredentialMatchWithAccountNo: this.customerGroupForm.get('isCredentialMatchWithAccountNo'),
    //         username: this.customerGroupForm.get('username'),
    //         password: this.customerGroupForm.get('password'),
    //         loginUsername: this.customerGroupForm.get('loginUsername'),
    //         loginPassword: this.customerGroupForm.get('loginPassword'),
    //         calendarType: this.customerGroupForm.get('calendarType'),
    //         pan: this.customerGroupForm.get('pan'),
    //         feasibilityRequired: this.customerGroupForm.get('feasibilityRequired'),
    //         staffId: this.customerGroupForm.get('staffId'),
    //         parentCustomerId: this.customerGroupForm.get('parentCustomerId'),
    //         custlabel: this.customerGroupForm.get('custlabel'),
    //         dunningCategory: this.customerGroupForm.get('dunningCategory'),
    //         departmentId: this.customerGroupForm.get('departmentId'),
    //         locations: this.customerGroupForm.get('locations'),
    //         isParentLocation: this.customerGroupForm.get('isParentLocation'),
    //         parentQuotaType: this.customerGroupForm.get('parentQuotaType'),
    //         passportNo: this.customerGroupForm.get('passportNo'),
    //         customerVrn: this.customerGroupForm.get('customerVrn'),
    //         customerNid: this.customerGroupForm.get('customerNid'),
    //         renewPlanLimit: this.customerGroupForm.get('renewPlanLimit')
    //     });

    //     // Step 2: Contact Details
    //     this.step2Group = this.fb.group({
    //         countryCode: this.customerGroupForm.get('countryCode'),
    //         mobile: this.customerGroupForm.get('mobile'),
    //         secondaryMobile: this.customerGroupForm.get('secondaryMobile'),
    //         email: this.customerGroupForm.get('email'),
    //         birthDate: this.customerGroupForm.get('birthDate')
    //     });

    //     // Step 3: Subscriber Location Details (uses presentGroupForm)
    //     this.step3Group = this.presentGroupForm;

    //     // Step 4: Plan Details
    //     this.step4Group = this.fb.group({
    //         offerPrice: this.customerGroupForm.get('offerPrice'),
    //         discountPrice: this.customerGroupForm.get('discountPrice'),
    //         planCategory: this.customerGroupForm.get('planCategory'),
    //         billTo: this.customerGroupForm.get('billTo'),
    //         billableCustomerId: this.customerGroupForm.get('billableCustomerId')
    //     });
    // }
    currentAssigneeName: string = "";
    setDefaultFilter = (): void => {
        // this.searchOption = "currentAssigneeName";
        this.staff = localStorage.getItem("loginUserName");
        console.log("Login Staff Name:", this.staff);
        this.searchcustomer();
    };

    noSpaceValidator(control: AbstractControl): ValidationErrors | null {
        if (control.value && control.value.includes(" ")) {
            return { noSpace: true };
        }
        return null;
    }

    checkPaymentGatewayConfiguration() {
        this.spinner.show();
        this.customerdetailsilsService.getActivePaymentConfiguration().subscribe(
            (response: any) => {
                this.savedConfig = [];
                if (response.status == 204) {
                    this.isPaymentGatewayConfigured = false;
                } else {
                    var activeConfig = response.activePaymentConfig;
                    var config = activeConfig.some(config => config.paymentConfigName == this.paymentGateway);
                    this.savedConfig = activeConfig;
                    const keyValuePairs: { [key: string]: any } = {};
                    for (const config of this.savedConfig) {
                        for (const mappingItem of config.paymentConfigMappingList) {
                            keyValuePairs[mappingItem.paymentParameterName] = mappingItem.paymentParameterValue;
                        }
                    }
                    this.paymentkeyValuePairs = keyValuePairs;
                    this.isPaymentGatewayConfigured = config;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                this.spinner.hide();
            }
        );
    }
    openPaymentGatewaysforInvoicePayment(invoice: any) {
        // this.displayInvoicePaymentDialog = false;

        if (this.savedConfig.length === 0) {
            this.toastr.info('Payment Gateway Configuration Not Found!!!');
        } else if (this.savedConfig.length === 1) {
            const gateway = this.savedConfig[0].paymentConfigName;
            if (gateway === "MoMo Pay") {
                this.spinner.show();
                this.buyMomoInvoicePayment(invoice);
            } else if (gateway === "AIRTEL") {
                this.spinner.show();
                this.airtelPayPlan(invoice);
            } else if (gateway === "MPESA") {
                this.displayMpesaOptionsDialog = true;
                this.invoiceForMpesa = invoice;
                this.invoice = invoice;
                this.payMethod = "MPESA";
                this.matdialog.open(this.Mobilenumber, {
                    width: '400px',
                    disableClose: true,
                    autoFocus: false
                });
                this.showMpinModal(this.invoice);
            } else if (gateway === "SELCOM") {
                this.spinner.show();
                this.selcomPayPlan(invoice);
            } else if (gateway === "Wave Pay") {
                this.spinner.show();
                this.buyWaveMoneyPayPlan(invoice);
            } else if (gateway == "ONEPAY") {
                this.spinner.show();
                this.buyOnePayInvoicePayment(this.invoice);
            } else if (gateway == "TRANSACTEASE") {
                this.spinner.show();
                this.getCustomerAddressDetails(this.invoice);
            } else {
                this.toastr.info('Invoice payment is not available for this gateway!');
            }
        } else if (this.savedConfig.length >= 1) {
            this.invoice = invoice;
            // Open the Angular Material dialog here
            this.matdialog.open(this.PaymentGateway, {
                width: '30%',
                disableClose: true,
                autoFocus: false
            });
            // this.displayInvoicePaymentDialog = true;
        }
    }

    OnCancel() {
        this.matdialog.closeAll()
    }

    invoicePayment(savedConfig: any) {
        this.invoicePaymentpaymentGateway(savedConfig);
    }
    invoicePaymentpaymentGateway(selectedConfig: any) {
        this.payMethod = selectedConfig.paymentConfigName;
        this.matdialog.closeAll();
        if (this.payMethod === "Wave Pay") {
            this.spinner.show();
            this.buyWaveMoneyPayPlan(this.invoice);
        } else if (this.payMethod === "KBZPAY") {
            this.spinner.show();
            this.buyKbzInvoicePayment(this.invoice);
        } else if (this.payMethod == "ONEPAY") {
            this.spinner.show();
            //   this.buyOnePayInvoicePayment(this.invoice);
            this.showMpinModal(this.invoice);
        } else if (this.payMethod == "TRANSACTEASE") {
            this.spinner.show();
            this.getCustomerAddressDetails();
        } else {
            this.matdialog.open(this.Mobilenumber, {
                width: '400px',
                disableClose: true,
                autoFocus: false
            });
            this.showMpinModal(this.invoice);
        }
    }
    buyOnePayInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            isAdvancePayment: true,
            //   isBuyPlan: true,
            merchantName: "ONEPAY",
            customerUserName: this.customerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            payerMobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerDetailData.buId
        };
        this.customerdetailsilsService.buyPlanUsingOnePay(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                // localStorage.setItem("transactionId", response.data.data.orderId),
                this.paymentConfirmationModal = true;
                this.isMpinFormSubmitted = false;
                this.mobileError = false;
                this.inputMobile = "";
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.exitBuy = false;
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                } else if (response.responseCode === 200 && response.data) {
                    const paymentLink = response.data;
                    this.toastr.success(`${response.data.message}`, 'Success!');


                    //   window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Unexpected response received!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');
            }
        );
    }
    invoicePaymentGateway() {
        this.matdialog.closeAll();
        // Open Payment Confirm dialog here
        // this.matdialog.open(this.PaymentConfirm, {
        //     width: '400px',
        //     disableClose: true,
        //     autoFocus: false
        // });
        if (this.payMethod === "MoMo Pay") {
            this.spinner.show();
            this.buyMomoInvoicePayment(this.invoice);
        } else if (this.payMethod === "AIRTEL") {
            this.spinner.show();
            this.airtelPayPlan(this.invoice);
        } else if (this.payMethod === "MPESA") {
            this.displayMpesaOptionsDialog = true;
            this.invoiceForMpesa = this.invoice;
            this.buyMpesaExpressPlan(this.invoiceForMpesa);
        } else if (this.payMethod === "SELCOM") {
            this.spinner.show();
            this.selcomPayPlan(this.invoice);
        } else if (this.payMethod === "ONEPAY") {
            this.spinner.show();
            this.buyOnePayInvoicePayment(this.invoice);
        } else {
            error: (error) => {
                this.toastr.info(`${error.responseMessage}`, 'Invoice payment is not available for this gateway!');
            }
        }
    }

    onKeymobileNumberlength(event) {
        const str = this.mpinForm.value.mobileNumber.toString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
        if (mobilenumberlength === 0 || mobilenumberlength === null) {
            mobilenumberlength = 10;
        }
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobileNumber = `${mobilenumberlength} character required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobileNumber = "";
        } else {
            this.inputMobileNumber = `${mobilenumberlength} character required.`;
        }
    }

    mobileError: boolean = false;

    onInputMobile(event: any) {
        const inputElement = event.target as HTMLInputElement;
        const inputValue = inputElement.value;

        // Check if the input starts with 0
        if (inputValue.startsWith("0")) {
            this.mobileError = true;
        } else {
            this.mobileError = false;
        }
    }

    showMpinModal(invoice) {
        this.spinner.hide();
        this.displayInvoicePaymentDialog = false;
        this.mpinModal = true;
        this.momoPayinvoice = invoice;
        this.mpinForm.controls.countryCode.setValue(this.commondropdownService.commonCountryCode);
        this.mpinForm.controls.mobileNumber.setValue(this.customerDetailData.mobile);
        // this.mpinForm.controls.mobileNumber.reset();
    }

    hideMpinModal() {
        this.closeMobilenumber.emit();
        this.matdialog.closeAll();
        this.isMpinFormSubmitted = false;
        this.mpinForm.reset();
        this.mpinForm.controls.countryCode.setValue("");
        this.mpinForm.controls.mobileNumber.setValue("");
        // this.mpinForm.updateValueAndValidity();
        this.mpinModal = false;
        this.mobileError = false;
        this.inputMobileNumber = "";
    }
    buyMomoInvoicePayment(invoice) {
        this.matdialog.open(this.PaymentConfirm, {
            width: '400px',
            disableClose: true,
            autoFocus: false
        });
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            merchantName: "MoMo Pay",
            customerUserName: this.customerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            invoiceId: invoice.id,
            partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            planId: null,
            hash: null
        };
        this.customerdetailsilsService.buyPlanUsingMomoInvoice(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                (localStorage.setItem("transactionId", response.data.data.orderId));
                this.paymentConfirmationModal = true;
                this.isMpinFormSubmitted = false;
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.mobileError = false;
                this.inputMobileNumber = "";
                this.exitBuy = false;

                // this.subscription2 = this.obs1$.subscribe(d => {
                //   if (this.paymentstatusCount > 0) {
                //     this.paymentstatusCount = this.paymentstatusCount - 1;
                //     this.getStatusSuccessByMomo("SUCCESSFUL");
                //     if (this.transactionStatus === true) {
                //       this.subscription2.unsubscribe();
                //     }
                //   }
                //   if (this.paymentstatusCount == 0) {
                //     this.subscription2.unsubscribe();
                //   }
                // });
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }
    getStatusSuccessByMomo(status) {
        this.spinner.hide();
        let data = {
            orderId: localStorage.getItem("transactionId"),
            status: status
        };
        this.customerdetailsilsService.getIntigrationTransactionstatusInvoice(data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data.istransactionsuccess === "true") {
                        this.transactionStatus = response.istransactionsuccess;
                        let data = {
                            userName: this.customerLedgerData.username,
                            password: this.customerLedgerData.password
                        };
                        // this.getDevice(data);
                        this.paymentConfirmationModal = false;
                        this.subscription2.unsubscribe();
                        this.paymentSucessModel = true;
                    }
                }
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    hidepaymentConfirmDialog() {
        this.closePaymentConfirm.emit();
        this.matdialog.closeAll();
        this.paymentConfirmationModal = false;
        this.displayInvoicePaymentDialog = false;
    }
    hidepaymentSucessDialog() {
        this.paymentSucessModel = false;
    }
    airtelPayPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            merchantName: "AIRTEL",
            customerUserName: this.customerDetailData.username,
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber: this.mpinForm.value.mobileNumber ?? "",
            invoiceId: invoice.id,
            partnerId: this.customerDetailData.partnerid,
            planId: null,
            hash: null,
            accountNumber: this.customerDetailData?.acctno ?? ""
        };
        this.customerdetailsilsService.buyPlanUsingAirtelInvoice(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                this.isMpinFormSubmitted = false;
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.mobileError = false;
                this.inputMobileNumber = "";
                //localStorage.setItem("transactionId"),
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                }
                (localStorage.setItem("transactionId", response.data.data.transaction.id));
                this.paymentConfirmationModal = true;
                this.exitBuy = false;

                // this.subscription2 = this.obs1$.subscribe(d => {
                //     if (this.paymentstatusCount > 0) {
                //         this.paymentstatusCount = this.paymentstatusCount - 1;
                //         this.getStatusSuccessByMomo("SUCCESSFUL");
                //         if (this.transactionStatus === true) {
                //             this.subscription2.unsubscribe();
                //         }
                //     }
                //     if (this.paymentstatusCount == 0) {
                //         this.subscription2.unsubscribe();
                //     }
                // });
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }
    selcomPayPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let customerPaymentDTO = {
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            buid: this.customerDetailData.buId,
            custServiceMappingId: this.customerDetailData.planMappingList[0].custServiceMappingId,
            customerId: this.customerDetailData.id,
            customerUUID: uuid.v4(),
            customerUserName: this.customerDetailData.username,
            invoiceId: invoice.id,
            isBuyPlan: true,
            isFromCaptive: true,
            merchantName: "SELCOM",
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            mvnoId: this.customerDetailData.mvnoId,
            orderId: null,
            partnerId: this.customerDetailData.partnerid,
            partnerPaymentId: this.customerDetailData.partnerPaymentId ?? null,
            planId: this.customerDetailData.planMappingList[0].planId,
            requestFor: this.customerDetailData.requestFor ?? null,
            status: this.customerDetailData.status
        };
        let selcomPayPayment = {
            vendor: "",
            order_id: null,
            buyer_email: this.customerDetailData.email,
            buyer_name: this.customerDetailData.username,
            buyer_phone:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            gateway_buyer_uuid: "",
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            currency: "",
            payment_methods: "",
            "billing.firstname": this.customerDetailData.firstname ?? "",
            "billing.lastname": this.customerDetailData.lastname ?? "",
            "billing.address_1": this.customerDetailData?.addressList[0]?.landmark ?? "",
            "billing.city": this.presentAdressDATA.cityName ?? "",
            "billing.state_or_region": this.presentAdressDATA.stateName ?? "",
            "billing.country": this.presentAdressDATA.countryName ?? "",
            "billing.phone":
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            no_of_items: 1,
            webhook: ""
        };
        let data = {
            customerPaymentDTO: customerPaymentDTO,
            selcomPayPayment: selcomPayPayment
        };
        this.customerdetailsilsService.buyPlanUsingSelcom(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                this.isMpinFormSubmitted = false;
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.mobileError = false;
                this.inputMobileNumber = "";
                //localStorage.setItem("transactionId"),
                if (response.responseCode === 417) {

                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                } else if (response.responseCode === 200 && response.data && response.data.data) {
                    const paymentLink = response.data.data;
                    window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(`${response.responseMessage}`, '"Unexpected response received!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }

    buyWaveMoneyPayPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            isAdvancePayment: true,
            isBuyPlan: true,
            merchantName: "Wave Pay",
            customerUserName: this.customerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            custServiceMappingId: this.customerDetailData.planMappingList[0].custServiceMappingId,
            mobileNumber:
                this.customerDetailData.countryCode.replace("+", "") +
                (this.customerDetailData.mobile ?? ""),
            partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerDetailData.buId,
            planId: this.customerDetailData.planMappingList[0].planId
        };
        this.customerdetailsilsService.buyPlanUsingWaveMoney(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                // localStorage.setItem("transactionId", response.data.data.orderId),
                this.paymentConfirmationModal = true;
                this.isMpinFormSubmitted = false;
                this.mobileError = false;
                this.inputMobile = "";
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.exitBuy = false;
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                } else if (response.responseCode === 200 && response.data) {
                    const paymentLink = response.data;
                    window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(`${response.responseMessage || "Unexpected response received."}`, 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }

    getAllLocation() {
        this.locationService.getAllActiveLocation().subscribe((response: any) => {
            this.locationDataByPlan = response.locationMasterList.map(location => ({
                name: location.name,
                locationMasterId: location.locationMasterId
            }));
        });
    }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
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

    getCustomer() {
        // this.displayRecordPaymentDialog = true;
        this.paymentFormGroup.patchValue({
            customerid: this.customerIdRecord
        });
    }
    getCustomerPaymentRecord() {
        this.displayRecordPaymentDialog = true;
        this.paymentFormGroup.patchValue({
            customerid: this.customerIdRecord
        });
    }

    modalOpenInvoice(id) {
        this.displaySelectInvoiceDialog = true;
        this.isDisplayConvertedAmount = false;
        this.collectedCurrency = this.customerDetailData?.currency
            ? this.customerDetailData?.currency
            : this.systemConfigCurrency;
        if (id) {
            this.InvoiceListByCustomer(id);
        }
        this.newFirst = 0;
    }
    Amount: any = 0;

    bindInvoice() {
        if (this.selectedInvoice.length >= 1) {
            this.isShowInvoiceList = true;
            this.Amount = 0;
            this.selectedInvoice.forEach(element => {
                if (element.testamount !== null) {
                    this.Amount += parseFloat(element.testamount);
                }
            });
            this.paymentFormGroup.patchValue({
                invoiceId: this.selectedInvoice.map(item => item.id),
                amount: this.Amount.toFixed(2)
            });
            this.onChangeOFAmountTest(this.selectedInvoice);
            this.destinationbank = true;
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please select at least one invoice or advance mode!');
            }


        }
        if (this.selectedInvoice.length == 2) {
            this.selectedInvoice.forEach(element => {
                if (element.docnumber == "Advance") {
                    this.selectedInvoice = [];
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
        this.displaySelectInvoiceDialog = false;
    }
    calculateTDS(event) {
        if (!event.target.checked) {
            this.paymentFormGroup.controls.tdsAmount.disable();
            this.paymentFormGroup.controls.tdsAmount.setValue(0);
        } else {
            this.paymentFormGroup.controls.tdsAmount.enable();
            this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
        }
    }
    calculateABBS(event) {
        if (!event.target.checked) {
            this.paymentFormGroup.controls.abbsAmount.disable();
            this.paymentFormGroup.controls.abbsAmount.setValue(0);
        } else {
            this.paymentFormGroup.controls.abbsAmount.enable();
            this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
        }
    }
    onChangeOFTDS(event) {
        let tdsAmount = event;
        let abbsAmount = this.paymentFormGroup.controls.abbsAmount.value;
        let totalAmount = this.paymentFormGroup.controls.amount.value;
        let diff = totalAmount - abbsAmount - tdsAmount;

        if (diff < 0 && tdsAmount != 0) {
            this.paymentFormGroup.controls.tdsAmount.setValue(0);
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'TDS/ABBS total can not be greater than amount!');
            }


        }
    }
    onChangeOFABBS(event) {
        let abbsAmount = event;
        let tdsAmount = this.paymentFormGroup.controls.tdsAmount.value;
        let totalAmount = this.paymentFormGroup.controls.amount.value;
        let diff = totalAmount - abbsAmount - tdsAmount;

        if (diff < 0 && abbsAmount != 0) {
            this.paymentFormGroup.controls.abbsAmount.setValue(0);
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'TDS/ABBS total can not be greater than amount!');
            }


        }
    }

    onChangeOFAmount(event) {
        let tdsAmount = (event * this.tdsPercent) / 100;
        let abbsAmount = (event * this.abbsPercent) / 100;

        // let tdsAmount = 0;
        // let abbsAmount = 0;
        // this.checkedList.forEach(element => {
        //   tdsAmount += element.includeTds ? (element.totalamount * this.tdsPercent) / 100 : 0;
        //   abbsAmount += element.includeAbbs ? (element.totalamount * this.abbsPercent) / 100 : 0;
        // });
        if (!this.paymentFormGroup.controls.abbsAmount.disabled) {
            this.paymentFormGroup.controls.abbsAmount.setValue(abbsAmount);
        }
        if (!this.paymentFormGroup.controls.tdsAmount.disabled) {
            this.paymentFormGroup.controls.tdsAmount.setValue(tdsAmount);
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
        this.selectedInvoice = [];
        for (let i = 0; i < this.invoiceList.length; i++) {
            if (this.invoiceList[i].isSelected) {
                this.selectedInvoice.push(this.invoiceList[i]);
            }
        }
    }
    modalCloseInvoiceList() {
        this.paymentFormGroup.patchValue({
            invoiceId: this.selectedInvoice.id,
            amount: this.selectedInvoice.refundAbleAmount
        });
        this.isShowInvoiceList = true;
        this.displaySelectInvoiceDialog = false;
        this.newFirst = 0;
    }
    onChangeOFAmountTest(event) {
        if (this.selectedInvoice.length >= 1) {
            let isAbbsTdsMode: boolean = false;
            if (this.paymentFormGroup.controls.paymode.value) {
                let formPayModeValue = this.paymentFormGroup.controls.paymode.value.toLowerCase();
                isAbbsTdsMode = this.checkPaymentMode(formPayModeValue);
            }
            let totaltdsAmount = 0;
            let totalabbsAmount = 0;
            this.selectedInvoice.forEach(element => {
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
                // if (this.isAbbsFlag) {
                this.paymentFormGroup.controls.abbsAmount.setValue(abbsAmount);
                // }
                // if (this.isTdsFlag) {
                this.paymentFormGroup.controls.tdsAmount.setValue(tdsAmount);
                // }
            }
        }
    }

    onSelectedInvoice(event, data, isTDS, isABBS) {
        if (event > 0) {
            this.isSelectedInvoice = false;
            if (isTDS) {
                data.tdsCheck = ((data.testamount * this.tdsPercent) / 100).toFixed(2);
            }
            if (isABBS) {
                data.abbsCheck = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
            }
        } else {
            //   data.includeTds = false;
            //   data.includeAbbs = false;
            data.tdsCheck = 0;
            data.abbsCheck = 0;
        }
        data.convertedAmount = data.testamount * this.convertedExchangeRate;
    }

    onConvertedAmountChange(event, data) {
        data.testamount = event / this.convertedExchangeRate;
        // data.convertedAmount = event;
    }

    onChangeOFTDSTest(event, data) {

        if (event.checked && data.totalamount) {
            data.includeTds = true;
            data.tdsCheck = ((data.testamount * this.tdsPercent) / 100).toFixed(2);
            data.tds = ((data.testamount * this.tdsPercent) / 100).toFixed(2);
        } else {
            data.includeTds = false;
            data.tdsCheck = 0;
            data.tds = 0;
        }
    }
    checkPaymentMode(formPayModeValue) {
        if (
            formPayModeValue &&
            (formPayModeValue == "vatreceiveable" ||
                formPayModeValue == "tds" ||
                formPayModeValue == "abbs")
        ) {
            return true;
        } else {
            return false;
        }
    }
    onChangeOFABBSTest(event, data) {
        if (event.checked && data.totalamount) {
            data.includeAbbs = true;
            data.abbsCheck = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
            data.abbs = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
        } else {
            data.includeAbbs = false;
            data.abbsCheck = 0;
            data.abbs = 0;
        }
    }

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
        const url = "/commonList/generic/" + payMode;
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.onlineSourceData = response.dataList;
                this.paymentFormGroup.patchValue({
                    onlinesource: ""
                });
                if (this.onlineSourceData.length > 0) {
                    this.paymentFormGroup.controls.onlinesource.setValidators([Validators.required]);
                    this.paymentFormGroup.controls.onlinesource.updateValueAndValidity();
                } else {
                    this.paymentFormGroup.controls.onlinesource.clearValidators();
                    this.paymentFormGroup.controls.onlinesource.updateValueAndValidity();
                }
                this.paymentFormGroup.updateValueAndValidity();
            },
            (error: any) => {
                this.onlineSourceData = [];
                console.log(error, "error");
            }
        );
        this.paymentFormGroup.updateValueAndValidity();
        let isAbbsTdsMode = this.checkPaymentMode(payMode);
        if (isAbbsTdsMode) {
            this.paymentFormGroup.patchValue({
                tdsAmount: 0,
                abbsAmount: 0
            });
            if (this.selectedInvoice.length > 0) {
                this.selectedInvoice.map(element => {
                    element.tds = 0;
                    element.abbs = 0;
                });
            }
        }
    }

    selPaySourceRecord(event) {
        const paySource = event.value.toLowerCase();

        switch (paySource) {
            case "cash_via_bank":
                this.paymentFormGroup.controls.destinationBank.enable();
                this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
                this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
                this.paymentFormGroup.controls.branch.enable();
                break;
            case "cash_in_hand":
                this.paymentFormGroup.controls.destinationBank.disable();
                this.paymentFormGroup.controls.destinationBank.clearValidators();
                this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
                this.paymentFormGroup.controls.branch.disable();
                break;
            case "cheque_in_hand":
                this.paymentFormGroup.controls.chequedate.enable();
                this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
                this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
                this.paymentFormGroup.controls.bankManagement.enable();
                this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
                this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
                this.paymentFormGroup.controls.chequeno.enable();
                this.paymentFormGroup.controls.chequeno.setValidators([Validators.required]);
                // this.paymentFormGroup.controls.referenceno.clearValidators();
                // this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
                this.paymentFormGroup.controls.reciptNo.enable();
                this.paymentFormGroup.controls.branch.enable();
                this.paymentFormGroup.controls.chequeno.updateValueAndValidity();
                break;
        }
    }
    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    addPayment(paymentId) {
        this.submitted = true;
        if (this.paymentFormGroup.valid) {
            if (this.paymentFormGroup.value.invoiceId == 0) {
                this.paymentFormGroup.value.paytype = "advance";
            } else {
                this.paymentFormGroup.value.paytype = "invoice";
            }

            if (this.selectedInvoice.length == 0) {
                error: (error) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Please select atleat one invoice or advance mode!');
                }

                return;
            }
            const maxSize = 1048576; // 1MB
            if (this.file && this.file.size > maxSize) {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'File size cannot exceed 1MB!');
                }

                return;
            } else {
                const url = "/record/payment";
                this.paymentFormGroup.value.customerid = this.customerDetailData.id;
                this.paymentFormGroup.value.type = "Payment";
                this.createPaymentData = this.paymentFormGroup.value;
                this.createPaymentData.onlinesource = this.paymentFormGroup.controls.onlinesource.value;
                if (this.paymentFormGroup.controls.chequedate.value) {
                    this.createPaymentData.chequedate = this.paymentFormGroup.controls.chequedate.value;
                    this.createPaymentData.chequedatestr = this.paymentFormGroup.controls.chequedate.value;
                }
                this.createPaymentData.filename = this.fileName;
                let invoiceId = [];
                this.selectedInvoice.forEach(element => {
                    invoiceId.push(element.id);
                });
                this.createPaymentData.invoiceId = invoiceId;
                // this.createPaymentData.invoices = invoices;
                delete this.createPaymentData.file;
                const formData = new FormData();
                var paymentListPojos = [];
                this.selectedInvoice.forEach(element => {
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
                this.revenueManagementService.postMethod(url, formData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.destinationbank = false;
                        this.paymentFormGroup.reset();
                        this.openCustomersPaymentData(this.customerId, "");
                        this.currentPagecustomerPaymentdata = 1;
                        this.invoiceList = [];
                        this.file = "";
                        this.fileName = null;
                        this.isShowInvoiceList = false;
                        this.toastr.success(`${response.message}`, 'Success!');

                        ;
                        this.displayRecordPaymentDialog = false;
                        this.selectedInvoice = [];
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                    }
                );
            }
        }
        this.displayRecordPaymentDialog = false;
    }

    getPendingAmount(item) {
        var amount = 0;
        if (item.adjustedAmount) {
            amount = item.totalamount - item.adjustedAmount;
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
    getLoggedinUserData() {
        const staffId = localStorage.getItem("userId");
        this.staffUserId = Number(localStorage.getItem("userId"));

        this.loggedInUser = localStorage.getItem("loggedInUser");
        console.log(this.loggedInUser, "this.loggedInUser");

        this.staffService.getById(staffId).subscribe(
            (response: any) => {
                this.staffUser = response?.Staff;
                this.userName = this.staffUser?.username;
                console.log(this.userName, "this.staffUser?.username");
                //  this.customerGroupForm.value.username = this.staffUser.username;

                // if (["Admin"].some(role => this.staffUser.roleName.includes(role))) {
                //   this.isAdmin = true;
                // } else {
                //   // this.customerGroupForm.get('serviceAreaId').setValue(response.Staff.servicearea.id);
                //   this.isAdmin = false;
                // }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }
    isParantExpirenceEdit: boolean;
    createCustomerCaf() {
        this.commondropdownService.getplanservice();
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        this.commondropdownService.findAllplanGroups();
        this.commondropdownService.getInsideValley();
        this.commondropdownService.getOutsideValley();
        this.commondropdownService.getCustomerCategory();
        this.getBillToData();
        this.getpartnerAll();
        this.billingSequence();
        this.getrequiredDepartment();
        this.getAllLocation();
        if (this.statusCheckService.isActiveInventoryService) {
            this.commondropdownService.getPOPList();
        }
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = true;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        // this.listSearchView = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = false;
        this.customerChangePlan = false;
        this.submitted = false;
        this.plansubmitted = false;
        this.iscustomerEdit = false;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.iflocationFill = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.ifcustomerDiscountField = false;
        this.isParantExpirenceEdit = false;
        this.payMappingListFromArray.controls = [];
        this.overChargeListFromArray.controls = [];
        this.custMacMapppingListFromArray.controls = [];
        this.isCallDetails = false;

        this.ifIndividualPlan = false;
        this.ifPlanGroup = false;
        this.planCategoryForm.reset();
        this.customerFormReset();
        this.planGroupForm.controls.service.enable();
        this.planGroupForm.controls.planId.enable();
        // this.planGroupForm.controls.validity.enable();
        // this.customerGroupForm.controls.username.enable();
        this.customerGroupForm.controls.invoiceType.disable();
        this.customerGroupForm.controls.parentExperience.disable();
        this.planGroupForm.controls.invoiceType.disable();
        // this.customerGroupForm?.get("customerCategory").setValue(this.commondropdownService?.dunningRules[0].value);
        // this.customerGroupForm.controls.dunningSubType.disable();
        // this.customerGroupForm.controls.dunningSubSector.disable();
        this.serviceAreaDisable = false;
        this.viewcustomerListData = [];
        this.addressListData = [];
        this.shiftLocationEvent = false;
        // this.customerGroupForm.controls.calendarType.disable();
        this.customerGroupForm.controls.custlabel.setValue("customer");
        this.customerGroupForm.patchValue({
            countryCode: this.commondropdownService.commonCountryCode
        });
        this.serviceareaCheck = true;
        this.selCustType();
        if (this.custType === RadiusConstants.CUSTOMER_TYPE.POSTPAID) {
            this.daySequence();
            this.earlyDaySequence();
        }
        // if (!this.isAdmin) {
        //   this.customerGroupForm.patchValue({
        //     serviceareaid: this.staffUser.serviceAreaId,
        //   });
        // }

        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.FinalAmountList = [];
        this.ifplanisSubisuSelect = false;
        this.getDevicesByType("OLT");
        this.getDevicesByType("Splitter");
        this.getDevicesByType("Master DB/DB");
        this.commondropdownService.getAllPinCodeData();
        this.getAllPinCodeData();
        this.getALLAreaData();
        // this.getAllBuildingData();
        this.getMappingFrom();
        this.systemService.getConfigurationByName("DEFAULT_CUSTOMER_CATEGORY").subscribe((res: any) => {
            if (res?.data?.value) {
                this.customerGroupForm.controls.dunningCategory.setValue(res?.data?.value);
            }
        });
        this.systemService.getConfigurationByName("isEmailMandatory").subscribe(
            (response: any) => {
                const val = String((response?.data || response)?.value ?? '').trim().toLowerCase();
                this.isEmailMandatory = ['true', '1', 'yes'].includes(val);
                const emailControl = this.customerGroupForm?.get('email');
                if (emailControl) {
                    emailControl.setValidators(this.isEmailMandatory ? [Validators.required, Validators.email] : [Validators.email]);
                    emailControl.updateValueAndValidity();
                }
            },
            (error: any) => {
                console.error("Failed to fetch IS_EMAIL_MANDATORY flag", error);
                this.isEmailMandatory = false;
            }
        );
    }

    getAllPinCodeData() {
        this.pincodeDD = [];
        const url = "/pincode/getAll";
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.pincodeDD = response.dataList;
            },
            (error: any) => { }
        );
    }

    getALLAreaData() {
        this.AreaListDD = [];
        const url = "/area/all";
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.AreaListDD = response.dataList;
                // console.log("areaData", this.areaData);
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

    openSelectBuildingDialogComponent = (): void => {
        this.selectBuildingDialogRef = this.matdialog?.open(SelectBuildingDialogComponent, {
            width: "80%",
            disableClose: true,
            data: {
                dialogref: this.selectBuildingDialogRef,
                areaId: this.areaId
            }
        });
        this.selectBuildingDialogRef?.afterClosed().subscribe(res => {
            this.presentGroupForm?.get("subareaId").setValue(res?.id);
            this.presentGroupForm?.get("subareaName").setValue(res?.name);
            this.onChangeSubArea({ value: res?.id }, 'present');
        })
    }

    openSelectorDialogComponent() {
        this.serviceAreaSelectorDialogRef = this.matdialog?.open(SelectorDialogComponent, {
            width: "80%",
            disableClose: true,
            data: {
                dialogref: this.serviceAreaSelectorDialogRef,
                headerTitle: "Service Area",
                url: RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/serviceArea/all/byStatus"
            }
        });
        this.serviceAreaSelectorDialogRef?.afterClosed().subscribe(res => {
            this.customerGroupForm?.get("serviceareaid").setValue(res?.id);
            this.customerGroupForm?.get("serviceareaName").setValue(res?.name);
            this.selectedServiceAreaName = res?.name;
            this.selServiceArea({ value: res?.id }, true);
        })
    }

    // getAllBuildingData() {
    //     this.buildingListDD = [];
    //     const url = "/buildingmgmt/all";
    //     this.areaManagementService.getMethod(url).subscribe(
    //         (response: any) => {
    //             this.buildingListDD = response.dataList;
    //             // console.log("areaData", this.areaData);
    //         },
    //         (error: any) => {
    //             // this.messageService.add({
    //             //   severity: 'error',
    //             //   summary: 'Error',
    //             //   detail: error.error.ERROR,
    //             //   icon: 'far fa-times-circle',
    //             // })
    //         }
    //     );
    // }

    getMappingFrom() {
        const url = "/buildingRefrence/all";
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                let dunningData = response.dataList;
                if (dunningData?.length > 0) {
                    this.selectedMappingFrom = dunningData[0].mappingFrom;
                }
                // else {
                //     this.messageService.add({
                //         severity: "info",
                //         summary: "Info",
                //         detail: "Please Select First Building Reference Management.",
                //         icon: "far fa-times-circle"
                //     });
                // }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getDevicesByType(deviceType) {
        if (this.statusCheckService.isActiveInventoryService) {
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
    }

    onAddIPList() {
        if (this.ipManagementGroup.valid) {
            this.ipMapppingListFromArray.push(this.ipListFormGroup());
            this.ipManagementGroup.reset();
        }
    }
    onAdddisplayIPList() {
        if (this.ipdisplayManagementGroup.valid) {
            this.ipMapppingdisplayListFromArray.push(this.ipListFormGroup());
            this.ipdisplayManagementGroup.reset();
        } else {
        }
    }
    addMac() {
        this.onAddIPList();
        this.createMac = true;
    }
    closeaddMac() {
        this.createMac = false;
    }
    saveIp() {
        this.createIp = false;
        const url = "/customerIpManagement/save";
        const formArrayData = this.flattenFormArray(this.ipMapppingListFromArray);
        this.customerService.saveIps(url, formArrayData).subscribe(
            (response: any) => {
                this.ipMapppingListFromArray = this.fb.array([]);
                this.getAllIp();
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    editIpById(record, index: number) {
        this.editmode = true;
        this.displaymode = false;
        this.editingIndex = index;
        this.currentEditRecord = record;
        this.editingRecord = { ...this.ipListData[index] };
    }

    saveChanges() {
        if (this.editingRecord) {
            const updatedRecords: { custid: any; ipAddress: any; ipType: any; custsermappingid: any }[] =
                [
                    {
                        custid: this.editingRecord.custid,
                        ipAddress: this.editingRecord.ipAddress,
                        ipType: this.editingRecord.ipType,
                        custsermappingid: this.editingRecord.custsermappingid
                    }
                ];

            const url = "/customerIpManagement/update";
            this.customerService.updateIps(url, updatedRecords).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.message}`, 'IP Address updated successfully!');


                        this.getAllIp();
                    }
                    //   const index = this.ipListData.findIndex(
                    //     ip => ip.custsermappingid === updatedRecords[0].custsermappingid
                    //   );
                    //   if (index !== -1) {
                    //     this.ipListData[index] = updatedRecords[0];
                    //   }
                },
                (error: any) => {
                    // Handle error
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            );
            this.displaymode = true;
            this.editingIndex = null;
        }

        this.editmode = false;
        this.editingRecord = {};
    }
    cancelChangesMac() {
        this.displaymode = true;
        this.editingRecord = {};
        this.editingIndex = null;
    }

    getAllIp() {
        const url = "/customerIpManagement/getIpsByCustId?custId=" + this.customerId;
        this.customerService.getAllIps(url).subscribe(
            (response: any) => {
                this.ipListData = response.customerIps;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    deleteConfirmMac(id) {
        this.confirmationService.confirm({
            message: "Do you want to delete this Mac?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteMac(id);
            },
            reject: () => {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                }

            }
        });
    }

    macListFormGroup(): UntypedFormGroup {
        const selectedService = this.dropdownOptions.find(
            option => option.value === this.macManagementGroup.value.custid
        );
        return this.fb.group({
            macAddress: [this.macManagementGroup.value.macAddress],
            custsermappingid: [this.macManagementGroup.value.custid],
            service: [selectedService.label],
            customer: {
                id: this.customerId
            }
        });
    }
    onAddmacList() {
        this.macSubmitted = true;
        if (this.macManagementGroup.valid) {
            const formGroup = this.macListFormGroup();
            formGroup.addControl("isDeleted", new UntypedFormControl(false));
            this.macMapppingListFromArray.push(this.macListFormGroup());
            this.macManagementGroup.reset();
            this.macSubmitted = false;
        }
    }
    addIp() {
        this.onAddmacList();
        this.createIp = true;
        this.macSubmitted = false;
    }
    closeaddIp() {
        this.createIp = false;
        this.macMapppingListFromArray = this.fb.array([]);
    }
    flattenFormArray(formArray: UntypedFormArray): any[] {
        return formArray.controls.map((group: UntypedFormGroup) => {
            const formData = {};
            Object.keys(group.controls).forEach(key => {
                formData[key] = group.controls[key].value;
            });
            return formData;
        });
    }
    saveMac() {
        this.createIp = false;
        const url = "/customerMacManagement/save";
        const formArrayData = this.flattenFormArray(this.macMapppingListFromArray);
        this.customerService.saveMacs(url, formArrayData).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    this.macMapppingListFromArray = this.fb.array([]);
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.macMapppingListFromArray = this.fb.array([]);
                    this.createMac = false;
                    this.getAllMac();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    editMacById(record, index: number) {
        this.editmode = true;
        this.displaymode = false;
        this.editingIndex = index;
        this.currentEditRecord = record;
        this.editingRecord = { ...this.macListData[index] };
    }

    saveChangesMac() {
        if (this.editingRecord) {
            const updatedRecords: {
                customer: any;
                macAddress: any;
                custsermappingid: any;
                id: any;
                isDeleted: any;
            } = {
                macAddress: this.editingRecord.macAddress,
                custsermappingid: this.editingRecord.custsermappingid,
                customer: {
                    id: this.customerId
                },
                id: this.editingRecord.id,
                isDeleted: false
            };
            const url = "/customerMacManagement/update";
            this.customerService.updateMacs(url, updatedRecords).subscribe(
                (response: any) => {
                    this.getAllMac();
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.message}`, 'MAC Address updated successfully!');

                    }

                    //   const index = this.macListData.findIndex(
                    //     mac => mac.custsermappingid === updatedRecords[0].custsermappingid
                    //   );
                    //   if (index !== -1) {
                    //     this.macListData[index] = updatedRecords[0];
                    //   }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            );
            this.displaymode = true;
            this.editingIndex = null;
        }

        this.editmode = false;
        this.editingRecord = {};
    }
    cancelChanges() {
        this.displaymode = true;
        this.editingRecord = {};
        this.editingIndex = null;
    }

    getAllMac() {
        const url = "/customerMacManagement/findByCustId?custId=" + this.customerId;
        this.customerService.getAllMacs(url).subscribe(
            (response: any) => {
                this.macListData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    deleteConfirm(id) {
        this.confirmationService.confirm({
            message: "Do you want to delete this IP?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteIp(id);
            },
            reject: () => {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                }

            }
        });
    }

    deleteIp(id) {
        const url = "/customerIpManagement/delete?id=" + id;
        this.customerService.deleteIps(url).subscribe(
            (response: any) => {
                if (response.responseCode) {
                    this.toastr.success(`${response.message}`, 'Success!');


                    this.getAllIp();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    deleteMac(id) {
        const url = "/customerMacManagement/delete?custMacMapppingId=" + id;
        this.customerService.deleteMacs(url).subscribe(
            (response: any) => {
                this.getAllMac();
                this.toastr.success(`${response.message}`, 'Deleted Successfully!');


            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    getService() {
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerId +
            "?isAllRequired=true&isNotChangePlan=true";
        this.customerService.getMethod(url).subscribe(
            (response: any) => {
                this.custId = response.dataList;
                this.service = response.dataList.map(item => item.service);
                this.custPlanMapppingId = response.dataList[0].customerServiceMappingId;
                this.dropdownOptions = response.dataList.map(item => ({
                    label: item.service,
                    value: item.customerServiceMappingId
                }));
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }





    listCustomer() {
        this._CWSCpasswordType = "password";
        this._AAApasswordType = "password";
        this.AAAshowPassword = false;
        this.CWSCshowPassword = false;

        this.listView = true;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        // this.listSearchView = true;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = false;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.iflocationFill = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.editCustomerId = "";
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        if (this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
            || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan
        ) {

            //   this.searchOption = "currentAssigneeName";
            // this.searchDeatil = localStorage.getItem("loginUserName");
            this.searchcustomer();
        } else {
            this.getcustomerList("");
        }
    }

    listdetalisCostomer() {
        this.listView = true;
        //  this.listSearchView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = false;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
    }

    customerDetailOpen(custId) {
        this.customerId = custId;
        // this.getAllSubAreaData();
        // this.getAllBuildingData();
        this.custDetilsCustId = custId;
        this.listView = false;
        this.isViewTicketMenu = false
        //  this.listSearchView = false;
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        // this.getCustomersLedger(custId);
        this.customerIdRecord = custId;
        this.getAllCustomerInventoryList(custId);
        this.getActivePlanListDetails(custId);
        this.getPaymentHistory(custId);
        this.getCustomersDetail(custId);
        this.getCustomerNetworkLocationDetail(custId);
        this.InvoiceListByCustomer(custId);
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.auditData = custId;
        this.isCallDetails = false;
        this.getCustQuotaList(custId);
        this.getNewCustomerAddressForCustomer(custId);
        this.GetAuditData(custId, "");
        this.isVisibleCAFHomeComponent = true;
        // this.getFramedIpAddressIp();
        // this.commondropdownService.getAllPinCodeData();
        // this.getAllPinCodeData();
        // this.getALLAreaData();
        // this.getMappingFrom();
    }


    openTicketViewDetails(custId) {
        this.listView = false;
        this.isViewTicketMenu = true
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.ifChargeGetData = false;
        this.chargeUseCustID = custId;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
    }


    serviceAreaAndBuildingNameFromCustomerId() {
        const url = "/BuildingAndSubareaNames/" + this.customerId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.presentAdressDATA.subarea = response?.data?.name;
                this.presentAdressDATA.buildingName = response?.data?.building_name;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getFramedIpAddressIp() {
        const url = "/liveUser/getFramedIpAddress/" + this.customerId;
        this.customerManagementService.savbillRadius(url).subscribe(
            (response: any) => {
                this.framedIpAddress = response.data;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    customerLedgerOpen() {
        this.customerDetailData?.currency
            ? (this.currency = this.customerDetailData?.currency)
            : this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
                this.currency = res.data.value;
            });
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = true;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
    }

    openCustorUpdateDiscount(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = true;
        this.getcustDiscountDetails(id, "");
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
    }

    openCustomersPlan(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = true;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.getcustFuturePlan(id, "");
        this.getcustExpiredPlan(id, "");
        this.getcustCurrentPlan(id, "");
        this.getcustDiscountDetails(id, "");
        this.getTrailPlanList(this.customerId, "");
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
    }

    openCustomerStatus(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerStatusView = true;
        this.getCustomerTeamHierarchy(id);
        this.workflowID = id;
        this.getworkflowAuditDetails("", id, "CAF");
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
    }
    openipManagement(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.workflowID = id;
        this.customerStatusView = false;
        this.ipManagementView = true;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
        this.getAllIp();
        this.getService();
    }

    openCustomerCafNotes(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.workflowID = id;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = true;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
        this.getAllCustomerNotes(id);
        // this.getService();
    }
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerNotesList: any = [];
    totalRecords: number;
    staffDetailModal: boolean = false;
    addNotesForm: UntypedFormGroup;

    custIdForNotes: any;
    addNotesPopup: boolean = false;
    notesSubmitted: boolean = false;
    addNotesData: CustNotes;
    addNotesSetFunction(custId: any) {
        this.matdialog.open(this.AddNotes);
        this.addNotesPopup = true;
        this.custIdForNotes = custId;
    }
    closeNotesModal() {
        this.matdialog.closeAll()
        this.closeAddNotes.emit()
        this.addNotesPopup = false;
        this.addNotesForm.reset();
    }
    saveNotes(leadId: any) {
        this.notesSubmitted = true;
        this.addNotesForm.markAllAsTouched();
        if (this.addNotesForm.valid) {
            if (leadId) {
                const url = "/add/notes";
                this.addNotesData = {
                    id: 0,
                    custId: leadId,
                    notes: this.addNotesForm.controls.notes.value
                };
                this.customerManagementService
                    .postMethodForCustNotes(url, this.addNotesData, this.mvnoid, this.staffid)
                    .subscribe(
                        (response: any) => {
                            this.notesSubmitted = false;
                            if (response.status == 406) {
                                this.addNotesPopup = false;
                                this.addNotesForm.reset();
                                this.toastr.error(`${response.message}`, 'Failed!');


                            } else {
                                if (this.searchkey || this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
                                    || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan) {

                                    this.searchcustomer();

                                } else {
                                    this.getcustomerList("");
                                }
                                this.getAllCustomerNotes(leadId)
                                this.addNotesPopup = false;
                                this.addNotesForm.reset();
                                this.toastr.success(`${response.message}`, 'Success!');
                                this.closeNotesModal();
                            }
                        },
                        (error: any) => {
                            this.addNotesPopup = false;
                            this.addNotesForm.reset();
                            this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                        }
                    );
            } else {
                this.addNotesForm.reset();
                this.addNotesPopup = false;
                error: (error) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Lead Id is missing!');
                }


            }
        } else {
            this.addNotesPopup = false;
            error: (error) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Required column is missing!');
            }


            this.addNotesPopup = true;
        }
    }

    exportPdf() {
        const url = `/findAllCustomerNotes/${this.customerId}`;
        this.customerManagementService.getMethodForCustomerNotesPdf(url).subscribe(
            async (response: any) => {
                this.customerNotesListPdf = await response.customerNotesList.map(
                    (item: any) => ({
                        ...item,
                        createdOn: this.datepipe.transform(item.createdOn, 'dd-MM-yyyy HH:mm:ss')
                    })
                );;

                const docDefinition: any = {
                    content: [
                        { text: 'Customer CAF Notes', style: 'header' },
                        {
                            columns: [
                                { text: `Customer Name: ${this.customerDetailData.custname}`, width: '*' },
                                { text: `Account Number: ${this.customerDetailData.acctno}`, width: '*' }
                            ],
                            margin: [0, 0, 0, 8]
                        },

                        {
                            columns: [
                                { text: `OLT : ${this.customerDetailData.oltName}`, width: '*' },
                                { text: `FAT: ${this.customerDetailData.areaName}`, width: '*' }
                            ],
                            margin: [0, 0, 0, 8]
                        },

                        {
                            columns: [
                                { text: `Service Area: ${this.customerDetailData.serviceareaName}`, width: '*' },
                                { text: `Account Status: ${this.customerDetailData.customerServiceMappingList[0]?.status || '-'}`, width: '*' }
                            ],
                            margin: [0, 0, 0, 20]
                        },

                        {
                            table: {
                                headerRows: 1,
                                widths: ['*', '*', '*'],
                                body: [
                                    ['Notes', 'Created By', 'Created Date and time'],
                                    ...this.customerNotesListPdf.map(c => [c.notes, c.createdByName, c.createdOn])
                                ],
                            },
                        }
                    ],
                    styles: {
                        header: {
                            fontSize: 18,
                            bold: true,
                            alignment: 'center',
                            margin: [0, 0, 0, 15]
                        }
                    }
                };
                pdfMake.createPdf(docDefinition).download('customer-caf-notes.pdf');
            },
        );
    }

    getAllCustomerNotes(custId?: any) {
        const url = `/findAllCustomerNotesWithPagination/${custId || this.customerData?.id}?page=${this.currentPage}&pageSize=${this.itemsPerPage}`;
        this.customerNotesList = [];
        this.customerManagementService.getMethodForCustomerNotes(url).subscribe(
            async (response: any) => {
                if (response?.customerNotesList?.length === 0) {
                    this.customerNotesList = [];
                    this.totalRecords = 0;
                } else {
                    this.customerNotesList = (await response.customerNotesList?.content) || [];
                    this.paginatedCustomerNotesList.data = this.customerNotesList;
                    this.totalRecords = (await response?.customerNotesList?.totalElements) || 0;
                }
            },
            (error: any) => {
                this.customerNotesList = [];
                this.totalRecords = 0;
                this.toastr.error(`${error.error?.msg || "Failed to fetch customer notes"}`, 'Failed!');


            }
        );
    }

    pageChangeEventForChildCustomers(pageNumber: number) {
        this.currentPage = pageNumber;
        this.getAllCustomerNotes();
    }

    itemPerPageChangeEvent(event) {
        this.currentPage = 1;
        this.itemsPerPage = Number(event.value);
        this.getAllCustomerNotes();
    }

    closeModalStaff() {
        this.staffDetailModal = false;
    }

    serviceAreaDetailModal: boolean = false;
    serviceAreaList: any = [];
    branchId: any;

    getServiceByBranch(e) {
        this.branchId = e.value;
        this.serviceareaCheck = false;
        const url = "/findServiceAreaByBranchId?BranchId=" + this.branchId;
        this.savbillCommonBaseService.getConnection(url).subscribe((response: any) => {
            this.serviceAreaList = response.serviceAreaList;
            //$("#PlanDetailsShow").modal("show");
        });
    }

    onClickServiceArea() {
        this.serviceAreaList = this.staffData.serviceAreasNameList;
        this.serviceAreaDetailModal = true;
        this.serviceAreaDialogRef = this.matdialog.open(this.serviceAreaDialog, {
            width: '600px',

        });
    }

    closeModalOfArea() {
        this.serviceAreaDetailModal = false;
    }

    openStaffDetailModal(staffId) {
        // this.staffDetailModal = true;
        this.staffDetailsDialogRef = this.matdialog.open(this.staffDetailsDialog, {
            width: '800px',
        });

        const url = "/getStaffUser/" + staffId;
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.staffData = response.Staff;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openmacManagement(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.workflowID = id;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = true;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.getAllMac();
        this.getService();
    }
    openMyInvoice(id) {
        this.ifMyInvoice = true;
        this.checkPaymentGatewayConfiguration();
        this.isVisibleCAFHomeComponent = false;
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.searchinvoiceMaster(id, "");
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
    }

    // openInvoiceModal(invoice) {
    //     // this.invoiceDetailsService.show(id);

    //     // this.isInvoiceDetail = true;
    //     // this.invoiceID = invoice.id;
    //     // this.custID = invoice.custid;

    //     // Reset visibility to force recreation
    //     this.isInvoiceDetail = false;
    //     // Small delay to allow Angular to register the change (optional)
    //     setTimeout(() => {
    //         this.invoiceID = invoice.id;
    //         this.custID = invoice.custid;
    //         this.isInvoiceDetail = true;
    //     }, 0);

    // }
    openInvoiceModal(id, invoice) {
        this.isInvoiceDetail = true;
        this.invoicedialogRef = this.matdialog.open(InvoiceDetalisModelComponent, {
            width: '1000px',
            disableClose: false,
            data: {
                dialogId: 'InvoiceDetailModal',
                invoiceID: invoice.id,
                custID: invoice.custid,
                sourceType: 'customer',
            },
        });
        this.invoicedialogRef.afterClosed().subscribe(() => {
            this.closeInvoiceDetails();
        });
        this.invoiceID = invoice.id;
        this.custID = invoice.custid;
    }
    closeInvoiceDetails() {
        this.isInvoiceDetail = false;
        this.invoiceID = "";
        this.custID = 0;
    }
    closeInvoiceModel() {
        this.invoiceList = [];
        this.masterSelected = false;
        this.displaySelectInvoiceDialog = false;
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

        let dtoData = {
            page: this.currentPageinvoiceMasterSlab,
            pageSize: this.invoiceMasteritemsPerPage
        };
        let url;

        // if (id) {
        //   this.searchInvoiceMasterFormGroup.value.billrunid = id
        //   this.searchInvoiceMasterFormGroup.patchValue({
        //     billrunid: Number(id),
        //   })
        // }

        this.searchInvoiceMasterFormGroup.value.custMobile = "";
        this.searchInvoiceMasterFormGroup.value.customerid = this.customerDetailData.id;

        url =
            "/trial/invoice/search?billrunid=" +
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
        this.revenueManagementService.postMethod(url, dtoData).subscribe(
            (response: any) => {
                const invoiceMasterListData = response.invoicesearchlist;
                // .filter(
                //   invoice => invoice.custType == "Prepaid"
                // );
                this.invoiceMasterListData = response.invoicesearchlist;

                this.invoiceMastertotalRecords = response.pageDetails.totalRecords;
                // this.invoiceMasterListData = response.invoicesearchlist;

                this.isInvoiceSearch = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, "error");

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
        this.invoiceMasterListData = [];
        this.currentPageinvoiceMasterSlab = 1;
        this.invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
        this.showItemPerPageInvoice = 1;
        this.searchinvoiceMaster("", "");
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
                    landmark1: [this.presentGroupForm.value.landmark1],
                    version: [this.presentGroupForm.value.version]
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
                    landmark1: [this.presentGroupForm.value.landmark1],
                    version: [this.presentGroupForm.value.version]
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
            offerPrice: [this.planGroupForm.value.offerprice],
            isInvoiceToOrg: [this.customerGroupForm.value.isInvoiceToOrg],
            istrialplan: [this.planGroupForm.value.istrialplan],
            discountType: [this.planGroupForm.value.discountType],
            discountExpiryDate: [
                this.planGroupForm.value.discountExpiryDate
                    ? moment(this.planGroupForm.value.discountExpiryDate).utc(true).toDate()
                    : null
            ],
            invoiceType: [this.planGroupForm.value.invoiceType],
            currency: [this.planGroupForm.value.currency]
            // id:[]
        });
        return;
    }

    discountValue: any = 0;

    discountvaluesetPercentage(event: KeyboardEvent) {
        const inputElement = event.target as HTMLInputElement;
        if (
            event.keyCode === 8 ||
            (event.key >= "0" && event.key <= "9") ||
            (event.key === "." && inputElement.value.indexOf(".") === -1) // Allow only one decimal point
        ) {
            let data = [];
            let price = Number(this.planDataForm.value.offerPrice);
            let selDiscount = parseFloat(this.planDataForm.value.discountPrice).toFixed(2);
            let discount = Number(selDiscount);
            let discountPlan = (discount * 100) / price;
            let discountValueNUmber = discountPlan.toFixed(2);
            let value = 100 - Number(discountValueNUmber);

            if (this.ifPlanGroup) {
                if (discount == 0) {
                    this.customerGroupForm.patchValue({
                        discount: 100
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
                        element.discount = 99.99;
                    } else if (value <= 99.99 && value >= -99.99) {
                        element.discount = value.toFixed(2);
                    } else {
                        if (value > 0) {
                            element.discount = 99.99;
                        } else {
                            element.discount = -99.99;
                        }
                    }

                    if (this.payMappingListFromArray.value.length == n) {
                        this.payMappingListFromArray.patchValue(this.payMappingListFromArray.value);
                    }
                });
            }
            return true;
        } else {
            return false;
        }
    }

    discountPercentage(e) {
        let rawValue = e?.target?.value.replace(/,/g, "");
        let newValue = parseFloat(rawValue);

        if (rawValue?.includes("-")) {
            if (Math.abs(newValue) > 99) {
                e.target.value = "-99";
            }
        } else {
            if (newValue > 99) {
                e.target.value = "99";
            } else if (newValue < -99) {
                e.target.value = "-99";
            }
        }

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
        let rawValue = e.target.value.replace(/,/g, "");
        let newValue = parseFloat(rawValue);

        if (rawValue.includes("-")) {
            if (Math.abs(newValue) > 99) {
                e.target.value = "-99";
            }
        } else {
            if (newValue > 99) {
                e.target.value = "99";
            } else if (newValue < -99) {
                e.target.value = "-99";
            }
        }

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
            this.DiscountValueStore.push(this.discountValue);
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
            this.filterChargesByCurrency(this.planGroupForm.value);
            this.payMappingListFromArray.push(this.planMappingListFormGroup());
            if (this.payMappingListFromArray?.length > 0) {
                this.customerGroupForm.get("currency").setValue(this.planGroupForm.value?.currency);
            }
            this.validityUnitFormArray.push(this.validityUnitListFormGroup());
            this.validityUnitFormGroup.reset();
            this.planGroupForm.reset();
            // this.planGroupForm.controls.validity.enable();
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

    filterChargesByCurrency(plan) {
        const selectedCurrency = plan?.currency;

        this.plantypaSelectData = this.plantypaSelectData.filter(plan => {
            const chargeCurrency = plan?.currency ?? this.currency;
            return chargeCurrency === selectedCurrency;
        });
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.customerGroupForm.value.billTo !== 'ORGANIZATION' &&
            this.planGroupForm.value.discountType === 'Recurring') {
            if (this.custType === 'Postpaid') {

                return ['service', 'plan', 'd-type', 'discount', 'discountExpiryDate', 'action'];
            }
            else {
                return ['service', 'plan', 'validity', 'd-type', 'discount', 'discountExpiryDate', 'action'];

            }
        } else {
            if (this.custType === 'Postpaid') {
                const columns: string[] = ['service', 'plan', 'd-type', 'discount', 'action'];
                if (

                    // this.planCategoryForm.value.planCategory === 'individual'
                    this.customerGroupForm.get("parentCustomerId").value &&
                    this.planCategoryForm.get('planCategory').value === 'individual'
                ) {
                    columns.splice(2, 0, 'invoiceType');
                }
                return columns;


                // return ['service', 'plan', 'd-type', 'discount', 'action'];
            }
            else {
                const columns: string[] = ['service', 'plan', 'validity', 'd-type', 'discount', 'action'];

                if (

                    // this.planCategoryForm.value.planCategory === 'individual'
                    this.customerGroupForm.get("parentCustomerId").value &&
                    this.planCategoryForm.get('planCategory').value === 'individual'
                ) {
                    columns.splice(2, 0, 'invoiceType');
                }
                return columns;
                // return ['service', 'plan', 'validity', 'd-type', 'discount', 'action'];

            }
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
    ipdisplayListFormGroup(): UntypedFormGroup {
        return this.fb.group({
            ipAddress: [this.ipdisplayManagementGroup.value.ipAddress],
            ipType: [this.ipdisplayManagementGroup.value.ipType]
        });
    }
    ipListFormGroup(): UntypedFormGroup {
        const selectedService = this.dropdownOptions.find(
            option => option.value === this.ipManagementGroup.value.custid
        );
        return this.fb.group({
            ipAddress: [this.ipManagementGroup.value.ipAddress],
            ipType: [this.ipManagementGroup.value.ipType],
            custsermappingid: [this.ipManagementGroup.value.custid],
            custid: [this.customerId],
            service: [selectedService.label]
        });
    }

    onAddMACList() {
        this.macsubmitted = true;
        if (this.macGroupForm.valid) {
            this.custMacMapppingListFromArray.push(this.MACListFormGroup());
            this.macGroupForm.reset();

            this.macsubmitted = false;
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
            // console.log("I am not valid");
        }
    }

    menuChange(action: string) {
        this.isVisibleCAFHomeComponent = false;
        this.resetMenuChange()
        switch (action) {
            case 'changeDiscount':
                this.openCustorUpdateDiscount(this.customerDetailData.id);
                break;
            case 'customerCafNote':
                this.openCustomerCafNotes(this.customerDetailData.id);
                break;
            case 'customerStatus':
                this.openCustomerStatus(this.customerDetailData.id);
                break;
            case 'invoices':
                this.openMyInvoice(this.customerDetailData.id);
                break;
            case 'payment':
                this.openCustomersPaymentData(this.customerDetailData.id, '');
                break;

            case 'ticket':
                this.openTicketViewDetails(this.customerDetailData.id);
                break;

            case 'customerPlans':
                this.openCustomersPlan(this.customerDetailData.id)
                break;

            case 'chargeManagement':
                this.openchargeDetails(this.customerDetailData.id)
                break;


            case 'wallet':
                this.addWalletIncustomer(this.customerDetailData.id)
                break;
        }
    }

    resetMenuChange() {
        this.customerUpdateDiscount = false;
        this.customerCafNotes = false;
        this.customerStatusView = false;
        this.ifMyInvoice = false;
        this.customerPlanView = false;
    }

    TotalCurrentPlanItemPerPage(event) {
        this.CurrentPlanShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerCurrentPlanListdata > 1) {
            this.currentPagecustomerCurrentPlanListdata = 1;
        }
        this.getcustCurrentPlan(this.customerDetailData.id, this.CurrentPlanShowItemPerPage);
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
        this.custCurrentPlanList = [];

        const url = "/subscriber/getActivePlanList/" + custId + "?isNotChangePlan=true";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custCurrentPlanList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedcustomerDiscountCustListData(pageNumber) {
        this.currentPagecustomerCustDiscountListdata = pageNumber;
        this.getcustDiscountDetails(this.customerDetailData.id, "");
    }

    TotalCustDiscountItemPerPage(event) {
        this.CustDiscountShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerCustDiscountListdata > 1) {
            this.currentPagecustomerCustDiscountListdata = 1;
        }
        this.getcustDiscountDetails(this.customerDetailData.id, this.CustDiscountShowItemPerPage);
    }

    getcustDiscountDetails(custId, size) {
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
        const url = "/subscriber/fetchCustomerDiscountDetailServiceLevel/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custCustDiscountList = response.discountDetails;
                this.currentDiscountDataSource.data = this.custCustDiscountList;
                while (custDiscountdatalength < this.custCustDiscountList.length) {
                    // const planurl =
                    //   '/postpaidplan/' +
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
        this.getcustFuturePlan(this.customerDetailData.id, this.futurePlanShowItemPerPage);
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
        this.getcustExpiredPlan(this.customerDetailData.id, this.expiredShowItemPerPage);
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
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
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
        this.searchkey = "";
        this.searchkey2 = "";

        let size;
        this.searchkey = "";
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
            pageSize: size,
            status: RadiusConstants.CUSTOMER_STATUS.NEW_ACTIVATION
        };
        this.customerManagementService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerListData = new MatTableDataSource(response.customerList);
                this.customerListDataselector = response.customerList;
                this.customerListdatatotalRecords = response.pageDetails.totalRecords;
                // this.customerListData.paginator = this.paginator;
                this.customerListData.sort = this.sort; // assign MatSort instance for sorting

                // if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                //     this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                // } else {
                //     this.customerListDatalength =
                //         this.customerListData.length % this.customerListdataitemsPerPage;
                // }
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

    onChangeSubArea(_event: any, index: any) {
        if (_event.value) {
            const subAreaurl = "/subarea/getAreaIdFromSubAreaId?subAreaId=" + _event.value;
            this.savbillCommonBaseService.get(subAreaurl).subscribe(
                (subarea: any) => {
                    if (subarea.data) {
                        const url = "/area/" + subarea.data;
                        this.savbillCommonBaseService.get(url).subscribe(
                            (response: any) => {
                                if (response.data?.pincodeId) {
                                    const pincodeUrl =
                                        "/pincode/getServicAreaIdByPincode?pincodeid=" + response.data?.pincodeId;
                                    this.savbillCommonBaseService.get(pincodeUrl).subscribe(
                                        (res: any) => {
                                            if (res?.data) {
                                                if (!this.customerGroupForm.controls.serviceareaid.value) {
                                                    this.customerGroupForm.controls.serviceareaid.setValue(res.data?.serviceAreaId);
                                                    this.customerGroupForm.controls.serviceareaName.setValue(res.data?.serviceAreaName);

                                                    let serviceAreaId = {
                                                        value: Number(res.data?.serviceAreaId)
                                                    };
                                                    this.selServiceArea(serviceAreaId, false);
                                                }
                                            }
                                            if (index === "present") {
                                                this.areaDetails = response.data;

                                                this.selectPincodeList = true;

                                                this.presentGroupForm.patchValue({
                                                    addressType: "Present",
                                                    areaId: Number(this.areaDetails.id),
                                                    //   subareaId: Number(subarea.data),
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
                                        },
                                        (error: any) => {
                                            console.log(error, "error");
                                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                                        }
                                    );
                                    let idData;
                                    if (this.selectedMappingFrom === "Pin Code") {
                                        idData = response.data?.pincodeId;
                                    } else if (this.selectedMappingFrom === "Area") {
                                        idData = subarea?.data;
                                    } else {
                                        idData = _event?.value;
                                    }
                                    let building_url =
                                        "/buildingmgmt/getBuildingMgmt?entityname=" +
                                        this.selectedMappingFrom +
                                        "&entityid=" +
                                        idData;
                                    this.savbillCommonBaseService.get(building_url).subscribe(
                                        (response: any) => {
                                            if (response.dataList?.length > 0) {
                                                this.buildingListDD = response.dataList;
                                                if (this.iscustomerEdit) {
                                                    let buildingEvent = {
                                                        value: Number(this.viewcustomerListData.addressList[0].building_mgmt_id)
                                                    };
                                                    this.onChangeBuildingArea(buildingEvent, "");
                                                }
                                            } else {
                                                this.buildingListDD = [];
                                            }
                                        },
                                        (error: any) => {
                                            console.log(error, "error");
                                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                                        }
                                    );
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    onChangeBuildingArea(_event: any, index: any) {
        if (_event.value) {
            this.buildingNoDD = [];
            const url = "/buildingmgmt/getBuildingMgmtNumbers?buildingMgmtId=" + _event.value;
            this.areaManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.buildingNoDD = response.dataList.map(buildingNumber => ({ buildingNumber }));
                    if (this.iscustomerEdit) {
                        this.presentGroupForm.patchValue({
                            buildingNumber: this.viewcustomerListData.addressList[0].buildingNumber
                        });
                    }
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
    }

    selectPINCODEChange(_event: any, index: any) {
        // const url = "/area/pincode?pincodeId=" + _event.value;
        // this.savbillCommonBaseService.get(url).subscribe(
        //   (response: any) => {
        //     this.AreaListDD = response.areaList;
        //   },
        //   (error: any) => {
        //     console.log(error);
        //   }
        // );
        if (_event.value) {
            const url = "/area/pincode?pincodeId=" + _event.value;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.AreaListDD = response.areaList;
                    if (_event.value) {
                        let url = "/pincode/getServicAreaIdByPincode?pincodeid=" + _event.value;
                        this.savbillCommonBaseService.get(url).subscribe(
                            (res: any) => {
                                if (res.data != null) {
                                    // this.getBranchByServiceAreaID(response.data);
                                    // this.getPlanbyServiceArea(response.data);

                                    if (!this.customerGroupForm.controls.serviceareaid.value) {
                                        let serviceAreaId = {
                                            value: Number(res.data?.serviceAreaId)
                                        };
                                        this.selServiceArea(serviceAreaId, false);
                                        this.customerGroupForm.controls.serviceareaid.setValue(res.data?.serviceAreaId);
                                        this.customerGroupForm.controls.serviceareaName.setValue(res.data?.serviceAreaName);
                                    }
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                    }
                    this.presentGroupForm.get("areaId").setValue('');
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
            let building_url =
                "/buildingmgmt/getBuildingMgmt?entityname=" +
                this.selectedMappingFrom +
                "&entityid=" +
                _event.value;
            this.savbillCommonBaseService.get(building_url).subscribe(
                (response: any) => {
                    if (response.dataList?.length > 0) {
                        this.buildingListDD = response.dataList;
                    } else {
                        this.buildingListDD = [];
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
        this.presentGroupForm?.get("subareaId")?.reset();
        this.presentGroupForm?.get("subareaName")?.reset();

        // this.getpincodeData(_event.value, index);
    }

    getTempPincodeData(id: any, index: any) {
        const url = "/pincode/" + id;

        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            if (index === "present") {
                this.pincodeDeatils = response.data;
                if (response.data.areaList && response.data.areaList.length !== 0) {
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

        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
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
                    this.toastr.info(`${response.responseMessage}`, 'Area detals are not available, please select correct pincode!');

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
                    this.toastr.info(`${response.responseMessage}`, 'Area detals are not available, please select correct pincode!');

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
                    this.toastr.info(`${response.responseMessage}`, 'Area detals are not available, please select correct pincode!');


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
        if (id) {
            const url = "/area/" + id;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    if (response.data?.pincodeId) {
                        const pincodeUrl =
                            "/pincode/getServicAreaIdByPincode?pincodeid=" + response.data?.pincodeId;
                        this.savbillCommonBaseService.get(pincodeUrl).subscribe(
                            (res: any) => {
                                if (res.data) {
                                    if (!this.customerGroupForm.controls.serviceareaid.value) {
                                        this.customerGroupForm.controls.serviceareaid.setValue(res.data?.serviceAreaId);
                                        this.customerGroupForm.controls.serviceareaName.setValue(res.data?.serviceAreaName);

                                        // this.getBranchByServiceAreaID(res.data);
                                        // this.getPlanbyServiceArea(res.data);
                                        let serviceAreaId = {
                                            value: Number(res.data?.serviceAreaId)
                                        };
                                        this.selServiceArea(serviceAreaId, false);
                                    }
                                }
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
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                        let idData = this.selectedMappingFrom === "Pin Code" ? response.data?.pincodeId : id;
                        let building_url =
                            "/buildingmgmt/getBuildingMgmt?entityname=" +
                            this.selectedMappingFrom +
                            "&entityid=" +
                            idData;
                        this.savbillCommonBaseService.get(building_url).subscribe(
                            (response: any) => {
                                if (response.dataList?.length > 0) {
                                    this.buildingListDD = response.dataList;
                                } else {
                                    this.buildingListDD = [];
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
            this.presentGroupForm?.get("subareaId").reset();
            this.presentGroupForm?.get("subareaName").reset();
            this.areaId = id;
        }
        // this.areaTitle
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
        const url =
            "/customer/customerUsernameIsAlreadyExists/" +
            this.customerGroupForm.controls.username.value;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            if (response.isAlreadyExists) {
                error: (error) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Username already exists!!');
                }


            } else {
                this.addEditcustomer(customerId);
            }
        });
    }
    // if (this.customerGroupForm.valid) {
    //     const isCredentialMatch =
    //         this.customerGroupForm.controls.isCredentialMatchWithAccountNo.value;
    //     if (isCredentialMatch) {
    //         this.addEditcustomer(customerId);
    //     } else {

    //     }
    // } else {
    //     this.messageService.add({
    //         severity: "error",
    //         summary: "Required",
    //         detail: "Fields are Mandatory or Invalid. Please fill or update those fields.",
    //         icon: "far fa-times-circle"
    //     });
    //     this.scrollToError();
    // }


    onKey(event) {
        if (event.key == "Tab") {
            const url =
                "/customer/customerUsernameIsAlreadyExists/" +
                this.customerGroupForm.controls.username.value;
            this.customerManagementService.getMethod(url).subscribe((response: any) => {
                if (response.isAlreadyExists == true) {
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Username already exists!!');
                    }


                }
            });
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
        let a = 0;
        let b = 0;
        let c = 0;
        let x = 0;

        // if (this.customerGroupForm.valid && this.presentGroupForm.valid) {
        if (
            this.customerGroupForm.getRawValue().planMappingList.length > 0 ||
            this.customerGroupForm.getRawValue().plangroupid ||
            this.customerGroupForm.getRawValue().custlabel === "organization"
        ) {
            this.customerGroupForm.value.pan = this.customerGroupForm.getRawValue().pan.trim();
            if (customerId) {
                this.customerGroupForm.value.status = "NewActivation";

                const url = "/customers/" + customerId;
                this.customerGroupForm.value.flatAmount = this.planDataForm.getRawValue().discountPrice;
                this.customerGroupForm.value.discount = this.customerGroupForm.getRawValue().discount
                    ? this.customerGroupForm.getRawValue().discount
                    : 0;

                if (this.presentGroupForm.value.addressType) {
                    this.cityId = this.presentGroupForm.getRawValue().cityId;
                    this.addressListData.push(this.presentGroupForm.getRawValue());
                    // this.addressListData[0].addressType = "Present";
                    // this.addressListData[0].version = "NEW";
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

                this.customerGroupForm.value.flatAmount = this.planDataForm.value.discountPrice;


                this.customerGroupForm.value.discount = this.customerGroupForm.getRawValue().discount
                    ? this.customerGroupForm.getRawValue().discount
                    : 0;
                this.createcustomerData = this.customerGroupForm.getRawValue();
                this.createcustomerData.customerLocations = this.locationMacData;
                // this.addressListData.forEach(item => {
                //     delete item.subareaName;
                // });

                this.createcustomerData.addressList = this.addressListData;
                this.createcustomerData.addressList[0].cityId = this.cityId;
                this.createcustomerData.addressList[0].stateId = this.presentGroupForm.getRawValue().stateId;
                this.createcustomerData.addressList[0].countryId = this.presentGroupForm.getRawValue().countryId;

                // this.createcustomerData.username = this.staffUser.username;
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
                if (this.viewcustomerListData.parentCustomerId != null) {
                    this.customerGroupForm.controls.parentExperience.enable();
                } else {
                    this.customerGroupForm.controls.parentExperience.disable();
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
                this.createcustomerData.custtype = this.custType;
                this.createcustomerData.acctno = this.viewcustomerListData.acctno;
                this.createcustomerData.username = this.customerGroupForm.controls.username.value;
                if (this.customerGroupForm.value.plangroupid) {
                    this.createcustomerData.planMappingList = this.plansArray.value;
                }
                this.createcustomerData.planPurchaseType = this.planCategoryForm.value.planCategory;

                this.createcustomerData.parentQuotaType = this.customerGroupForm.getRawValue().parentQuotaType;

                while (x < this.createcustomerData.customerLocations.length) {
                    this.createcustomerData.customerLocations[x].locationId = Number(
                        this.locationMacData[x].locationId
                    );
                    this.createcustomerData.customerLocations[x].mac = this.locationMacData[x].mac;
                    this.createcustomerData.customerLocations[x].isParentLocation =
                        this.locationMacData[x].isParentLocation;
                    x++;
                }
                if (this.createcustomerData.birthDate != null) {
                    this.createcustomerData.birthDate = new Date(this.customerGroupForm.getRawValue().birthDate);
                } else {
                    this.createcustomerData.birthDate = null;
                }
                if (
                    this.createcustomerData?.mac_provision == null ||
                    this.createcustomerData?.mac_provision == undefined
                ) {
                    this.createcustomerData.mac_provision = false;
                }
                let departmentId = this.customerGroupForm.value?.departmentId;
                if (departmentId) {
                    let departmentData = this.departmentListData?.find(x => x?.id === departmentId);
                    this.createcustomerData.department = departmentData?.name;
                }
                this.createcustomerData.addressList[0].version = 'New'
                // return
                this.customerManagementService.updateMethod(url, this.createcustomerData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.iscustomerEdit = false;

                        this.payMappingListFromArray.controls = [];
                        this.overChargeListFromArray.controls = [];
                        this.custMacMapppingListFromArray.controls = [];

                        this.customerFormReset();

                        this.viewcustomerListData = [];
                        this.addressListData = [];
                        this.toastr.success(`Successfully Updated`, 'Success!');



                        this.listView = true;
                        this.createView = false;
                        this.selectAreaList = false;
                        this.listSearchView = false;
                        this.selectchargeValueShow = false;
                        this.ifMyInvoice = false;
                        this.isServiceOpen = false;
                        this.ifShowDBRReport = false;
                        this.ifChargeGetData = false;
                        this.ifWalletMenu = false;
                        this.ifUpdateAddress = false;
                        this.ifCafFollowUp = false;
                        this.customerUpdateDiscount = false;
                        this.shiftLocationEvent = false;
                        this.isCallDetails = false;
                        this.planCategoryForm.reset();
                        if (this.searchkey || this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
                            || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan) {
                            this.searchcustomer();
                        } else {
                            this.getcustomerList("");
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                        this.addressListData = [];
                    }
                );
            } else {
                // this.customerGroupForm.value.username = this.staffUser.username;
                // if (this.presentGroupForm.value.addressType) {
                this.presentGroupForm.patchValue({ version: "New" });
                this.addressListData.push(this.presentGroupForm.value);
                // this.addressListData[0].addressType = "Present";
                // }
                if (this.paymentGroupForm.value.addressType) {
                    this.paymentGroupForm.patchValue({ version: "New" });
                    this.addressListData.push(this.paymentGroupForm.value);
                    // this.addressListData[1].addressType = "Payment";
                }
                if (this.permanentGroupForm.value.addressType) {
                    this.paymentGroupForm.patchValue({ version: "New" });
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
                this.customerGroupForm.value.discount = this.customerGroupForm.getRawValue().discount
                    ? this.customerGroupForm.value.discount
                    : 0;

                this.customerGroupForm.value.flatAmount = this.planDataForm.value.discountPrice;
                this.customerGroupForm.value.discount = this.customerGroupForm.getRawValue().discount
                    ? this.customerGroupForm.value.discount
                    : 0;
                this.customerGroupForm.value.status = "NewActivation";
                this.createcustomerData = this.customerGroupForm.getRawValue();

                this.createcustomerData.customerLocations = this.locationMacData;

                // this.addressListData.forEach(item => {
                //     delete item.subareaName;
                // });

                this.createcustomerData.addressList = this.addressListData;
                this.createcustomerData.addressList[0].cityId = this.presentGroupForm.getRawValue().cityId;
                this.createcustomerData.addressList[0].stateId = this.presentGroupForm.getRawValue().stateId;
                this.createcustomerData.addressList[0].countryId = this.presentGroupForm.getRawValue().countryId;

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
                if (
                    this.createcustomerData.plangroupid == null ||
                    this.createcustomerData.plangroupid == ""
                )
                    this.createcustomerData.invoiceType = null;
                this.createcustomerData.custtype = this.custType;
                this.createcustomerData.isCustCaf = "yes";
                this.createcustomerData.acctno = this.viewcustomerListData.acctno;
                if (this.customerGroupForm.value.plangroupid) {
                    this.createcustomerData.planMappingList = this.plansArray.value;
                }
                this.createcustomerData.planPurchaseType = this.planCategoryForm.value.planCategory;

                this.createcustomerData.parentQuotaType = this.customerGroupForm.getRawValue().parentQuotaType;

                while (x < this.createcustomerData.customerLocations.length) {
                    this.createcustomerData.customerLocations[x].locationId = Number(
                        this.locationMacData[x].locationId
                    );
                    this.createcustomerData.customerLocations[x].mac = this.locationMacData[x].mac;
                    this.createcustomerData.customerLocations[x].isParentLocation =
                        this.locationMacData[x].isParentLocation;
                    x++;
                }
                if (this.customerGroupForm.value.birthDate) {
                    this.createcustomerData.birthDate = new Date(this.customerGroupForm.getRawValue().birthDate);
                } else {
                    this.createcustomerData.birthDate = null;
                }
                if (
                    this.createcustomerData?.mac_provision == null ||
                    this.createcustomerData?.mac_provision == undefined
                ) {
                    this.createcustomerData.mac_provision = false;
                }
                let departmentId = this.customerGroupForm.value?.departmentId;
                if (departmentId) {
                    let departmentData = this.departmentListData?.find(x => x?.id === departmentId);
                    this.createcustomerData.department = departmentData?.name;
                }

                const trimData = {
                    ...this.createcustomerData,
                    firstname: this.createcustomerData.firstname.trim(),
                    mobile: this.createcustomerData.mobile.trim(),
                    email: this.createcustomerData?.email?.trim() || "",
                    title: "",
                    lastname: this.createcustomerData.lastname.trim(),
                }
                this.customerManagementService.postMethod(url, trimData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        } else if (response.status == 400) {
                            this.toastr.info(`${response.ERROR.mobile}`, 'Info!');

                        } else {
                            this.submitted = false;

                            this.payMappingListFromArray.controls = [];
                            this.overChargeListFromArray.controls = [];
                            this.custMacMapppingListFromArray.controls = [];
                            this.addressListData = [];

                            this.customerFormReset();
                            this.toastr.success(`Successfully Created`, 'Success!');


                            this.listView = true;
                            this.createView = false;
                            this.listSearchView = false;
                            this.planCategoryForm.reset();
                            this.selectchargeValueShow = false;
                            this.ifMyInvoice = false;
                            this.isServiceOpen = false;
                            this.ifShowDBRReport = false;
                            this.ifChargeGetData = false;
                            this.ifWalletMenu = false;
                            this.ifUpdateAddress = false;
                            this.ifCafFollowUp = false;
                            this.selectAreaList = false;
                            this.customerUpdateDiscount = false;
                            this.shiftLocationEvent = false;
                            this.isCallDetails = false;
                            if (this.searchkey || this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
                                || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan) {
                                this.searchcustomer();
                            } else {
                                this.getcustomerList("");
                            }
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.addressListData = [];
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            }
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Minimum one Plan Details need to add!');
            }
        }
        // } else {


        // }
        // } else {
        //     this.messageService.add({
        //                 severity: "error",
        //                 summary: "Required ",
        //                 detail: "Fields are Mandatory or Invalid. Please fill or update those field.",
        //                 icon: "far fa-times-circle"
        //     });
        //     this.scrollToError();
        // }
    }

    //TODO Need to get billable customer object in customer by id api and remove below api code for quick fix did this
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

    editCustomerProfile(chargeid) {
        this.listCustomer();
        this.editcustomer(chargeid);
    }

    next(stepper: MatStepper) {
        stepper?.next();
    }

    editcustomer(chargeid: any) {
        this.iscustomerEdit = true;
        this.commondropdownService.getplanservice();
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        this.commondropdownService.findAllplanGroups();
        // this.commondropdownService.getValleyTypee();
        this.commondropdownService.getInsideValley();
        this.commondropdownService.getOutsideValley();
        this.commondropdownService.getCustomerCategory();
        // this.getCustomerSector();
        this.getBillToData();
        this.getpartnerAll();
        this.billingSequence();
        this.getrequiredDepartment();
        this.getAllLocation();
        if (this.statusCheckService.isActiveInventoryService) {
            this.commondropdownService.getPOPList();
        }
        this.commondropdownService.getAllPinCodeData();
        this.getAllPinCodeData();
        this.getALLAreaData();
        // this.getAllBuildingData();
        this.getMappingFrom();
        const j = 1;
        let k = 0;
        this.totalAddress = 0;

        this.DiscountValueStore = [];
        this.discountValue = 0;
        this.planTotalOffetPrice = 0;
        this.planDataForm.reset();
        this.planDropdownInChageData = [];
        let addres1;
        const planlength = 0;
        let macNumber = 0;
        this.editCustomerId = chargeid;
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = true;
        this.listSearchView = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.planDropdownInChageData = [];
        this.serviceareaCheck = false;
        this.isCallDetails = false;
        if (this.payMappingListFromArray.controls) {
            this.payMappingListFromArray.controls = [];
        }
        if (this.overChargeListFromArray.controls) {
            this.overChargeListFromArray.controls = [];
        }
        if (this.custMacMapppingListFromArray.controls) {
            this.custMacMapppingListFromArray.controls = [];
        }
        if (this.custType === RadiusConstants.CUSTOMER_TYPE.POSTPAID) {
            this.daySequence();
            this.earlyDaySequence();
        }
        this.paymentGroupForm.reset();
        this.permanentGroupForm.reset();
        this.viewcustomerListData = [];

        if (chargeid) {
            //
            const url = "/customers/" + chargeid;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.iscustomerEdit = true;
                    this.viewcustomerListData = response.customers;

                    this.customerGroupForm?.get("dunningCategory").setValue(this.viewcustomerListData?.dunningCategory);

                    this.getDevicesByType("OLT");
                    this.getDevicesByType("Splitter");
                    this.getDevicesByType("Master DB/DB");
                    if (this.viewcustomerListData.birthDate)
                        this.viewcustomerListData.birthDate = moment(
                            this.viewcustomerListData.birthDate
                        ).format("YYYY-MM-DD");
                    this.customerGroupForm.patchValue(this.viewcustomerListData);
                    if (
                        this.viewcustomerListData?.earlybillday != undefined &&
                        this.viewcustomerListData?.earlybillday != null
                    ) {
                        this.customerGroupForm.patchValue({
                            earlybillday: this.viewcustomerListData?.earlybillday?.toString()
                        });
                    }
                    if (this.viewcustomerListData.billableCustomerId) {
                        this.getBillableCust(this.viewcustomerListData.billableCustomerId);
                    }
                    let serviceAreaId = {
                        value: Number(this.viewcustomerListData.serviceareaid)
                    };
                    this.selServiceArea(serviceAreaId, false);
                    this.customerGroupForm.patchValue(this.viewcustomerListData.customerServiceMappingList)

                    if (this.presentGroupForm) this.presentGroupForm.patchValue(this.viewcustomerListData);
                    this.presentGroupForm?.get("subareaName").setValue(this.viewcustomerListData?.addressList[0]?.subareaName);

                    this.presentGroupForm.updateValueAndValidity();
                    this.presentGroupForm?.updateValueAndValidity();
                    if (this.viewcustomerListData.isCredentialMatchWithAccountNo) {
                        this.customerGroupForm.controls.username.disable();
                        this.customerGroupForm.controls.isCredentialMatchWithAccountNo.disable();
                    } else {
                        this.customerGroupForm.controls.username.enable();
                    }
                    this.customerGroupForm
                        .get("parentQuotaType")
                        .setValue(this.viewcustomerListData.parentQuotaType);

                    if (this.viewcustomerListData.customerLocations.length > 0) {
                        this.customerGroupForm
                            .get("isParentLocation")
                            .setValue(this.viewcustomerListData.customerLocations[0].isParentLocation);

                        var selectedLocation = [];
                        this.custLocationData = [];
                        this.custLocationData = [...this.viewcustomerListData.customerLocations];

                        this.viewcustomerListData.customerLocations.forEach(location => {
                            if (selectedLocation.indexOf(location.locationId) === -1) {
                                selectedLocation.push(location.locationId);
                            }

                            this.overLocationMacArray.push(
                                this.fb.group({
                                    name: [location.locationName],
                                    mac: [location.mac],
                                    locationId: [location.locationId],
                                    isAlreadyAvailable: true,
                                    isParentLocation: location.isParentLocation
                                })
                            );
                        });
                        if (this.overLocationMacArray.value.length > 0) {
                            this.locationMacData = this.overLocationMacArray.value.map(location => ({
                                locationId: location.locationId, //location.locationId
                                mac: location.mac,
                                isParentLocation: location.isParentLocation
                            }));
                        }
                    }
                    this.locationChange(selectedLocation);
                    this.locationMacForm.get("location").setValue(selectedLocation);
                    this.presentGroupForm?.get("subareaName").setValue(this.viewcustomerListData?.addressList[0]?.subareaName);

                    //this.customerGroupForm.controls.username.disable();

                    this.customerGroupForm
                        .get("parentQuotaType")
                        .setValue(this.viewcustomerListData.parentQuotaType);

                    if (this.viewcustomerListData.customerLocations.length > 0) {
                        this.customerGroupForm
                            .get("isParentLocation")
                            .setValue(this.viewcustomerListData.customerLocations[0].isParentLocation);

                        var selectedLocation = [];
                        this.custLocationData = [];
                        this.custLocationData = [...this.viewcustomerListData.customerLocations];

                        this.viewcustomerListData.customerLocations.forEach(location => {
                            if (selectedLocation.indexOf(location.locationId) === -1) {
                                selectedLocation.push(location.locationId);
                            }
                            this.overLocationMacArray.push(
                                this.fb.group({
                                    name: [location.locationName],
                                    mac: [location.mac],
                                    locationId: [location.locationId],
                                    isAlreadyAvailable: true,
                                    isParentLocation: location.isParentLocation
                                })
                            );
                        });

                        if (this.overLocationMacArray.value.length > 0) {
                            this.locationMacData = this.overLocationMacArray.value.map(location => ({
                                locationId: location.locationId, //location.locationId
                                mac: location.mac,
                                isParentLocation: location.isParentLocation
                            }));
                        }
                    }

                    this.locationChange(selectedLocation);
                    this.locationMacForm.get("location").setValue(selectedLocation);

                    this.customerGroupForm
                        .get("billTo")
                        .setValue(this.viewcustomerListData.planMappingList[0].billTo);
                    this.customerGroupForm.get("isCustCaf").setValue("yes");
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
                    if (this.viewcustomerListData?.creditDocuments?.length) {
                        if (this.viewcustomerListData?.creditDocuments?.length > 0) {
                            this.customerGroupForm.controls.paymentDetails.patchValue(
                                this.viewcustomerListData.creditDocuments[0]
                            );
                        }
                    }
                    if (this.viewcustomerListData.parentExperience != null) {
                        this.customerGroupForm.controls.parentExperience.enable();
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
                        this.isParantExpirenceEdit = true;
                        this.customerGroupForm.controls.parentExperience.enable();
                        this.customerGroupForm.controls.parentExperience.patchValue(
                            this.viewcustomerListData.parentExperience
                        );
                    } else {
                        this.customerGroupForm.controls.invoiceType.disable();
                        this.planGroupForm.controls.invoiceType.enable();
                    }

                    this.payMappingListFromArray.patchValue(this.viewcustomerListData.planMappingList);

                    // Address
                    if (this.viewcustomerListData.addressList[0].addressType) {
                        this.getTempPincodeData(this.viewcustomerListData.addressList[0].pincodeId, "present");
                        this.getAreaData(this.viewcustomerListData.addressList[0].areaId, "present");
                        this.presentGroupForm.patchValue(this.viewcustomerListData.addressList[0]);
                        this.selServiceAreaByParent(Number(this.viewcustomerListData.serviceareaid));
                        const data = {
                            value: this.viewcustomerListData.addressList[0].pincodeId
                        };

                        this.selectPINCODEChange(data, "");
                        this.presentGroupForm.patchValue({
                            pincodeId: Number(this.viewcustomerListData.addressList[0].pincodeId)
                        });
                        let subAreaEvent = {
                            value: this.viewcustomerListData.addressList[0].subareaId
                        };

                        this.onChangeSubArea(subAreaEvent, "present");
                        // this.presentGroupForm.patchValue({
                        //     buildingNumber: Number(this.viewcustomerListData.addressList[0].buildingNumber)
                        // })
                    }
                    if (this.viewcustomerListData.addressList != null) {
                        this.viewcustomerListData.addressList.forEach(element => {
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
                    this.step3Group.patchValue(this.permanentGroupForm)

                    this.presentGroupForm?.get("subareaName").setValue(this.viewcustomerListData?.addressList[0]?.subareaName);

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
                                    //   const servicename = this.serviceData.find(
                                    //     item => item.id == element.service
                                    //   ).name;
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
                                            ],
                                            currency: [this.customerGroupForm.get("currency").value]
                                        })
                                    );
                                    this.validityUnitFormArray.push(
                                        this.fb.group({
                                            validityUnit
                                        })
                                    );

                                    let n = i + 1;
                                    newAmount = postpaidplanData.newOfferPrice
                                        ? postpaidplanData.newOfferPrice
                                        : postpaidplanData.offerprice;
                                    totalAmount = Number(totalAmount) + Number(newAmount);

                                    if (this.viewcustomerListData.planMappingList.length == n) {
                                        this.planDataForm.patchValue({
                                            offerPrice: totalAmount,
                                            discountPrice: this.viewcustomerListData.flatAmount
                                                ? this.viewcustomerListData.flatAmount.toFixed(2)
                                                : this.viewcustomerListData.flatAmount
                                        });

                                        // this.payMappingListFromArray.value.forEach((e, k) => {
                                        //   let discountValueNUmber: any = 0;
                                        //   let lastvalue: any = 0;
                                        //   let m = i + 1;
                                        //   let price = Number(this.payMappingListFromArray.value[k].offerPrice);
                                        //   let discount = Number(this.payMappingListFromArray.value[k].discount);
                                        //   let DiscountV = (price * discount) / 100;
                                        //   discountValueNUmber = DiscountV.toFixed(2);
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
                    if (this.viewcustomerListData.dunningType != null) {
                        const data = {
                            value: this.viewcustomerListData.dunningType
                        };
                        // this.customerGroupForm?.controls?.dunningSubType?.enable();
                        this.getcustType(data);
                    } else {
                        // this.customerGroupForm?.controls?.dunningSubType?.disable();
                    }

                    //   if (this.viewcustomerListData.dunningSector != null) {
                    //     this.customerGroupForm.controls.dunningSubSector.enable();
                    //   } else {
                    //     this.customerGroupForm.controls.dunningSubSector.disable();
                    //   }
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
        this.systemService.getConfigurationByName("isEmailMandatory").subscribe(
            (response: any) => {
                const val = String((response?.data || response)?.value ?? '').trim().toLowerCase();
                this.isEmailMandatory = ['true', '1', 'yes'].includes(val);
                const emailControl = this.customerGroupForm?.get('email');
                if (emailControl) {
                    emailControl.setValidators(this.isEmailMandatory ? [Validators.required, Validators.email] : [Validators.email]);
                    emailControl.updateValueAndValidity();
                }
            },
            (error: any) => {
                console.error("Failed to fetch IS_EMAIL_MANDATORY flag", error);
                this.isEmailMandatory = false;
            }
        );
    }

    planSelectType(event) {
        this.planDropdownInChageData = [];
        const planaddDetailType = event.value;

        this.DiscountValueStore = [];
        this.ifplanisSubisuSelect = false;
        this.DiscountValueStore = [];
        this.discountValue = "";
        this.planTotalOffetPrice = 0;
        this.planDataForm.reset();
        this.customerGroupForm.controls.plangroupid.reset();
        this.customerGroupForm.controls.discount.reset();
        this.customerGroupForm.controls.discountType.reset();
        this.customerGroupForm.controls.discountExpiryDate.reset();
        let partnerId = this.customerGroupForm.controls.partnerid.value;
        let serviceAreaId = this.customerGroupForm.controls.serviceareaid.value;

        if (planaddDetailType == "individual") {
            this.ifIndividualPlan = true;
            this.ifPlanGroup = false;
            this.payMappingListFromArray.controls = [];
            if (partnerId && serviceAreaId && !this.isBranchAvailable) {
                this.getPlanbyPartner(serviceAreaId, partnerId);
            }
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
            if (partnerId && serviceAreaId && !this.isBranchAvailable) {
                this.getPlangroupByPartner(partnerId);
            }
            if (this.serviceAreaData) {
                this.filterNormalPlanGroup = [];
                if (this.custType == "Prepaid") {
                    this.commondropdownService.PrepaidPlanGroupDetails.forEach(element => {
                        if (element.planMode == "NORMAL") {
                            this.filterNormalPlanGroup.push(element);
                        }
                    });
                }
                if (this.custType == "Postpaid") {
                    this.commondropdownService.postPlanGroupDetails.forEach(element => {
                        if (element.planMode == "NORMAL") {
                            this.filterNormalPlanGroup.push(element);
                        }
                    });
                }
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
            }
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
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }


                }
            });
        }
    }


    deletecustomer(customerId) {
        //
        const url = "/customers/" + customerId;
        this.customerManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPagecustomerListdata != 1 && this.customerListDatalength == 1) {
                    this.currentPagecustomerListdata = this.currentPagecustomerListdata - 1;
                }
                this.toastr.success(`${response.message}`, 'Success!');

                if (this.searchkey || this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
                    || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan) {
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

    pageChangedCustomerList(event: PageEvent): void {
        this.customerListdataitemsPerPage = event.pageSize;
        this.currentPagecustomerListdata = event.pageIndex + 1;

        if (this.searchkey || this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
            || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan) {
            this.searchcustomer();
        } else {
            this.getcustomerList(this.customerListdataitemsPerPage);
        }
    }


    pageChangedpayMapping(pageNumber) {
        this.currentPagePayMapping = pageNumber;
    }

    pageChangedOverChargeList(pageNumber) {
        this.currentPageoverChargeList = pageNumber;
    }
    deleteConfirmIp(index: number, name: string) {
        if (index || index === 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this " + name + "?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    switch (name) {
                        case "ipAddress":
                            this.ipMapppingdisplayListFromArray.removeAt(index);
                            break;
                        default:
                            break;
                    }
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                }
            });
        }
    }
    // deleteConfirmonChargeField(chargeFieldIndex: number, name: string) {
    //     if (chargeFieldIndex || chargeFieldIndex == 0) {
    //         this.confirmationService.confirm({
    //             message: "Do you want to delete this " + name + "?",
    //             header: "Delete Confirmation",
    //             icon: "pi pi-info-circle",
    //             accept: () => {
    //                 // console.log(name);
    //                 switch (name) {
    //                     case "Plan":
    //                         if (this.ifplanisSubisuSelect == true) {
    //                             this.ifplanisSubisuSelect = false;
    //                             this.customerGroupForm.patchValue({
    //                                 billTo: "CUSTOMER"
    //                             });
    //                         }

    //                         this.onRemovePayMapping(chargeFieldIndex);
    //                         break;
    //                     case "Charge":
    //                         this.onRemoveChargelist(chargeFieldIndex);
    //                         break;
    //                     case "MAC":
    //                         this.onRemoveMACaddress(chargeFieldIndex);
    //                         break;
    //                     // case 'uploadDocument':
    //                     //   this.onRemoveUploadDocument(chargeFieldIndex);
    //                     //   break;
    //                 }
    //             },
    //             reject: () => {
    //                 error: (error) => {
    //                     this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
    //                 }

    //             }
    //         });

    //         // this.confirmationService.confirm({
    //         //   message: "Do you want to delete this charge?",
    //         //   header: "Delete Confirmation",
    //         //   icon: "pi pi-info-circle",
    //         //   accept: () => {
    //         //     //
    //         //     // console.log(name);
    //         //     switch (name) {
    //         //       case "paymapping":
    //         //         this.onRemovePayMapping(chargeFieldIndex);
    //         //         break;

    //         //       case "chargelist":
    //         //         this.onRemoveChargelist(chargeFieldIndex);
    //         //         break;
    //         //       case "MAC":
    //         //         this.onRemoveMACaddress(chargeFieldIndex);
    //         //         break;
    //         //     }
    //         //   },
    //         //   reject: () => {
    //         //     this.messageService.add({
    //         //       severity: "info",
    //         //       summary: "Rejected",
    //         //       detail: "You have rejected",
    //         //     });
    //         //   },
    //         // });
    //     }
    // }
    deleteConfirmonChargeField(chargeFieldIndex: number, name: string, source?: any) {
        if (chargeFieldIndex !== undefined) {
            const DeleteConfirmDialogRef = this.matdialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: `Delete ${name}`,
                    description: `Are you sure you want to delete "${source?.leadSourceName || name}"?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            DeleteConfirmDialogRef.afterClosed().subscribe(result => {
                if (result) {
                    switch (name) {
                        case "Plan":
                            if (this.ifplanisSubisuSelect === true) {
                                this.ifplanisSubisuSelect = false;
                                this.customerGroupForm.patchValue({
                                    billTo: "CUSTOMER"
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
                        //     this.onRemoveUploadDocument(chargeFieldIndex);
                        //     break;
                    }
                } else {
                    this.toastr.info('You have rejected!', 'Info');
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
                this.planDataForm.value.discountPrice - this.DiscountValueStore[chargeFieldIndex]?.value
            ).toFixed(2)
        });

        this.payMappingListFromArray.removeAt(chargeFieldIndex);
        let obj = {
            value: this.servicePlanId
        };
        this.serviceBasePlanDATA(obj);
        this.DiscountValueStore.splice(chargeFieldIndex, 1);
        this.DiscountValueStore = [];
        this.planTotalOffetPrice = 0;
        if (this.payMappingListFromArray.value.length == 0) {
            this.planDataForm.patchValue({
                discountPrice: 0,
                offerPrice: 0
            });
        }
        this.changeTrialCheck();
    }

    async onRemoveChargelist(chargeFieldIndex: number) {
        this.overChargeListFromArray.removeAt(chargeFieldIndex);
    }

    async onRemoveMACaddress(chargeFieldIndex: number) {
        this.custMacMapppingListFromArray.removeAt(chargeFieldIndex);
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

    pageChangedcustledgerList(pageNumber) {
        this.currentPagecustLedgerList = pageNumber;
        this.getCustomersLedger(this.customerDetailData.id, "");
    }

    pageChangedcustomerPaymentList(pageNumber) {
        this.currentPagecustomerPaymentdata = pageNumber;
        this.openCustomersPaymentData(this.customerDetailData.id, "");
    }

    pageChangedcustFuturePlanListData(event) {
        this.futurePlanShowItemPerPage = Number(event.pageSize);
        if (this.currentPagecustomerFuturePlanListdata > 1) {
            this.currentPagecustomerFuturePlanListdata = 1;
        }
        this.currentPagecustomerFuturePlanListdata = event.pageIndex + 1;
        this.getcustFuturePlan(this.customerDetailData.id, this.futurePlanShowItemPerPage);
    }

    pageChangedcustomerExpiryPlanListData(event) {
        this.expiredShowItemPerPage = event.pageSize;
        this.currentPagecustomerExpiryPlanListdata = event?.pageIndex + 1;
        this.getcustExpiredPlan(this.customerDetailData.id, this.expiredShowItemPerPage);
    }

    pageChangedcustomerCurrentPlanListData(event) {
        this.CurrentPlanShowItemPerPage = Number(event.pageSize);
        if (this.currentPagecustomerCurrentPlanListdata > 1) {
            this.currentPagecustomerCurrentPlanListdata = 1;
        }
        this.currentPagecustomerCurrentPlanListdata = event?.pageIndex + 1;
        this.getcustCurrentPlan(this.customerDetailData.id, this.CurrentPlanShowItemPerPage);
    }
    serviceArea: string = '';
    cafCreatedBy: string = '';
    team: string = '';
    staff: string = '';
    username: string = '';
    plan: string = '';
    Service: string = '';
    cafStatus: string = '';
    accountNumber: string = '';
    activationbyname: string = '';
    createbyname: string = '';
    onSearchOptionChange() {
        this.searchDetail = '';
        this.searchDeatil = ''
        this.cafStatus = null;

    }

    searchcustomer() {

        let filters: any[] = [];
        let searchValue = this.searchDeatil || this.searchDetail;

        if (searchValue && this.searchOption) {
            let value = searchValue;
            if (this.searchOption === "cafCreatedDate" || this.searchOption === "firstactivationdate") {
                value = this.datePipe.transform(searchValue, "yyyy-MM-dd");
            } else if (typeof value === 'string') {
                value = value.trim();
            }

            filters.push({
                filterDataType: "",
                filterValue: value,
                filterColumn: this.searchOption,
                filterOperator: "equalto",
                filterCondition: "and"
            });
        }

        if (this.serviceArea) {
            filters.push({ filterDataType: "", filterValue: this.serviceArea, filterColumn: "serviceareaName", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.cafCreatedBy) {
            filters.push({ filterDataType: "", filterValue: this.cafCreatedBy, filterColumn: "createbyname", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.team) {
            filters.push({ filterDataType: "", filterValue: this.team, filterColumn: "currentAssignedTeam", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.cafStatus) {
            filters.push({ filterDataType: "", filterValue: this.cafStatus, filterColumn: "cafStatus", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.activationbyname || this.createbyname) {
            filters.push({ filterDataType: "", filterValue: this.activationbyname || this.createbyname, filterColumn: "activationbyname", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.staff) {
            filters.push({ filterDataType: "", filterValue: this.staff, filterColumn: "currentAssigneeName", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.username) {
            filters.push({ filterDataType: "", filterValue: this.username, filterColumn: "username", filterOperator: "equalto", filterCondition: "and" });
        }
        if (this.plan) {
            filters.push({ filterDataType: "", filterValue: this.plan, filterColumn: "planName", filterOperator: "equalto", filterCondition: "and" });
        }
        if (!searchValue && !this.searchOption && !this.staff) {
            filters.push({
                filterDataType: "",
                filterValue: "",
                filterColumn: "",
                filterOperator: "equalto",
                filterCondition: "and"
            });
        }
        let currentFiltersString = JSON.stringify(filters);
        if (this.lastFiltersString !== currentFiltersString) {
            this.currentPagecustomerListdata = 1;
            this.lastFiltersString = currentFiltersString;
        }
        this.searchData.filters = filters;
        this.searchData.page = this.currentPagecustomerListdata;
        this.searchData.pageSize = this.customerListdataitemsPerPage;

        this.searchData.fromDate = this.datePipe.transform(this.fromDate, "yyyy-MM-dd") || "";
        this.searchData.toDate = this.datePipe.transform(this.toDate, "yyyy-MM-dd") || "";
        // this.searchData.status = this.cafStatus || "";

        const url = "/customers/searchNew/" + this.custType;

        this.customerManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.status == 204 || !response.customerList) {
                    this.customerListData = new MatTableDataSource([]);
                    this.customerListdatatotalRecords = 0;
                    const message = response.msg?.ERROR || 'No records found';
                    this.toastr.info(message, 'Info');
                } else {
                    this.customerListData = new MatTableDataSource(response.customerList);
                    this.customerListData.sort = this.sort;
                    this.customerListdatatotalRecords = response.pageDetails?.totalRecords || 0;
                }
            },
            (error: any) => {
                this.customerListdatatotalRecords = 0;
                this.customerListData = new MatTableDataSource([]);
                this.toastr.error(error.msg?.ERROR || "Something went Wrong!", 'Failed!');
            }
        );
    }
    getSearchLabel(value: string): string {
        const option = this.searchOptionSelect.find(x => x.value === value);
        return option ? option.label : value;
    }

    clearSearchcustomer() {
        this.currentPagecustomerListdata = 1;
        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }
        this.searchDeatil = "";
        this.searchOption = "";
        this.searchDetail = '';
        this.fromDate = "";
        this.toDate = "";
        this.serviceArea = '';
        this.cafCreatedBy = '';
        this.team = '';
        this.staff = '';
        this.username = '';
        this.plan = '';
        this.Service = '';
        this.cafStatus = '';
        this.accountNumber = '';
        this.activationbyname = '';
        this.createbyname = '';
        this.lastFiltersString = '';
        this.getcustomerList("");
    }

    selSearchOption(event) {
        // console.log("value", event.value);
        this.searchDeatil = "";
        // if (this.searchOption == "currentAssigneeName" || this.searchOption == "activationbyname" || this.searchOption == "createbyname") {
        //     this.searchDeatil = localStorage.getItem("loginUserName");
        // }
        //   if (this.searchOption == "currentAssigneeName") {

        // if (event.value) {
        //   this.fieldEnable = true;
        // } else {
        //   this.fieldEnable = false;
        // }
    }

    serviceBasePlanDATA(event) {
        this.servicePlanId = event.value;
        const serviceId = event.value;
        const servicename = this.serviceData.find(item => item.id == serviceId)?.name;
        this.planGroupForm.patchValue({ service: servicename });
        this.planGroupForm.controls.istrialplan.reset();
        if (!this.isBranchAvailable) {
            this.plantypaSelectData = this.filterPlanData.filter(
                id =>
                    id.serviceId === this.planGroupForm.controls.serviceId.value &&
                    (id.planGroup === "Registration" || id.planGroup === "Registration and Renewal") &&
                    id.plantype == this.custType
            );
        } else {
            let planserviceData;
            let planServiceID = "";
            this.changeTrialCheck();
            const planserviceurl = "/planservice/all";
            this.customerManagementService.getMethod(planserviceurl).subscribe((response: any) => {
                planserviceData = response.serviceList.filter(service => service.id === serviceId);
                if (planserviceData.length > 0) {
                    planServiceID = planserviceData[0].id;

                    // if (this.customerGroupForm.value.custtype) {
                    this.plantypaSelectData = this.filterPlanData.filter(
                        id =>
                            id.serviceId === planServiceID &&
                            (id.planGroup === "Registration" || id.planGroup === "Registration and Renewal")
                    );
                    if (this.payMappingListFromArray?.length > 0) {
                        let selectedCurrency = this.payMappingListFromArray?.value[0]?.currency;
                        this.plantypaSelectData = this.plantypaSelectData.filter(plan => {
                            const chargeCurrency = plan?.currency ?? this.currency;
                            return chargeCurrency === selectedCurrency;
                        });
                    }
                    if (this.plantypaSelectData.length === 0) {
                        this.toastr.info('Plan not available for this customer type and service!', 'Info!');

                    }
                    // }
                    // else {
                    //   this.messageService.add({
                    //     severity: 'info',
                    //     summary: 'Required',
                    //     detail: 'Customer Type Field Required',
                    //   });
                    // }
                }
            });
        }
    }

    selCustType() {
        let obj: any = [];
        this.filterPlanData = [];
        if (this.commondropdownService.postpaidplanData.length != 0) {
            obj = this.commondropdownService.postpaidplanData.filter(
                key => key.plantype === this.custType
            );
        }
        this.filterPlanData = obj;
        if (this.planGroupForm.value) {
            this.planGroupForm.reset();
            this.plantypaSelectData = [];
        }
    }

    getCustomersDetail(custId) {
        this.getaclEntries();
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
                this.customerDetailData = response.customers;
                this.presentGroupForm?.get("subareaName")?.setValue(this.customerDetailData?.addressList[0]?.subareaName)
                this.customerId = response.customers.id;
                this.customerGroupForm.controls.calendarType.setValue('English');
                this.customerGroupForm.controls.custlabel.setValue('customer');

                this.customerGroupForm?.get("mobile")?.setValue(this.customerDetailData?.mobile);
                if (response.customers?.creditDocuments?.length) {
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

                //currency
                this.customerDetailData?.currency
                    ? (this.currency = this.customerDetailData?.currency)
                    : this.systemService
                        .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                        .subscribe((res: any) => {
                            this.currency = res.data.value;
                        });

                this.isDisplayConvertedAmount =
                    this.currency !=
                    (this.customerDetailData?.currency
                        ? this.customerDetailData?.currency
                        : this.systemConfigCurrency);

                //pop Name
                if (this.customerDetailData.popid) {
                    if (this.statusCheckService.isActiveInventoryService) {
                        let partnerurl = "/popmanagement/" + this.customerDetailData.popid;
                        this.customerManagementService.getMethod(partnerurl).subscribe((response: any) => {
                            this.customerPopName = response.data.name;

                            // console.log("partnerDATA", this.partnerDATA);
                        });
                    }
                }

                // partner Name
                if (this.customerDetailData.partnerid) {
                    const partnerurl = "/partner/" + this.customerDetailData.partnerid;
                    this.partnerService.getMethodNew(partnerurl).subscribe((response: any) => {
                        this.partnerDATA = response.partnerlist.name;

                        // console.log("partnerDATA", this.partnerDATA);
                    });
                }

                // serviceArea Name
                if (this.customerDetailData.serviceareaid) {
                    const serviceareaurl = "/serviceArea/" + this.customerDetailData.serviceareaid;
                    this.savbillCommonBaseService.get(serviceareaurl).subscribe((response: any) => {
                        this.serviceAreaDATA = response.data.name;

                        // console.log("partnerDATA", this.serviceAreaDATA);
                    });
                }

                // Address
                if (
                    this.customerDetailData.addressList.length > 0 &&
                    this.customerDetailData.addressList[0].addressType
                ) {
                    const areaurl = "/area/" + this.customerDetailData.addressList[0].areaId;

                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                        this.presentAdressDATA = response.data;
                        // // let findsubData = this.subAreaListDD?.find(
                        // //     x => x.id == this.customerDetailData.addressList[0]?.subareaId
                        // // );
                        // // this.presentAdressDATA.subarea = findsubData?.name;
                        // // let findBuildData = this.buildingListDD?.find(
                        // //     x => x.buildingMgmtId == this.customerDetailData.addressList[0]?.building_mgmt_id
                        // // );
                        // this.presentAdressDATA.buildingName = findBuildData?.buildingName;
                        this.presentAdressDATA.buildingNumber =
                            this.customerDetailData.addressList[0]?.buildingNumber;
                        this.serviceAreaAndBuildingNameFromCustomerId();
                        // console.log("presentAdressDATA", this.presentAdressDATA);
                    });
                }
                if (this.customerDetailData.addressList.length > 1) {
                    let j = 0;
                    while (j < this.customerDetailData.addressList.length) {
                        const addres1 = this.customerDetailData.addressList[j].addressType;
                        if (addres1) {
                            if ("Payment" == addres1) {
                                const areaurl = "/area/" + this.customerDetailData.addressList[j].areaId;
                                this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                    this.paymentAdressDATA = response.data;

                                    // console.log("paymentAdressDATA", this.paymentAdressDATA);
                                });
                            } else {
                                const areaurl = "/area/" + this.customerDetailData.addressList[j].areaId;
                                this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                    this.permentAdressDATA = response.data;

                                    // console.log("permentAdressDATA", this.permentAdressDATA);
                                });
                            }
                        }
                        j++;
                    }
                }

                if (this.customerDetailData.planMappingList.length > 0) {
                    this.customerBill = this.customerDetailData.planMappingList[0].billTo;
                    this.custInvoiceToOrg = this.customerDetailData.planMappingList[0].isInvoiceToOrg;
                }

                if (this.customerDetailData.plangroupid) {
                    this.ifIndividualPlan = false;
                    this.ifPlanGroup = true;
                    const planGroupurl =
                        "/findPlanGroupById?planGroupId=" + this.customerDetailData.plangroupid;

                    this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                        this.planGroupName = response.planGroup.planGroupName;
                    });
                } else {
                    this.ifIndividualPlan = true;
                    this.ifPlanGroup = false;
                    this.customerDetailData.planMappingList = this.customerDetailData.planMappingList.filter(
                        data => data.custPlanStatus == "Active"
                    );

                    this.planMappingList = this.customerDetailData.planMappingList;
                    while (plandatalength < this.customerDetailData.planMappingList.length) {
                        const planId = this.customerDetailData.planMappingList[plandatalength].planId;
                        let discount;
                        if (
                            this.customerDetailData.planMappingList[plandatalength].discount == null ||
                            this.customerDetailData.planMappingList[plandatalength].discount == ""
                        ) {
                            discount = 0;
                        } else {
                            discount = this.customerDetailData.planMappingList[plandatalength].discount;
                        }
                        this.activePlanNames = "";
                        if (
                            this.customerDetailData.planMappingList[plandatalength].plangroup !=
                            "Volume Booster" &&
                            this.customerDetailData.planMappingList[plandatalength].plangroup !=
                            "Bandwidth Booster"
                        )
                            this.activePlanNames =
                                this.activePlanNames +
                                this.customerDetailData.planMappingList[plandatalength].planName +
                                ",";

                        const planurl = "/postpaidplan/" + planId;
                        this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                            this.dataPlan.push(response.postPaidPlan);
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
                    // charger Data
                    if (this.customerDetailData.indiChargeList.length > 0) {
                        this.addChargeForm.patchValue({
                            chargeAdd: true
                        });
                    }

                    // let checkCustTypeurl = `/isCustomerPrimeOrNot?custId=${custId}`;
                    // this.customerManagementService
                    //   .getMethod(checkCustTypeurl)
                    //   .subscribe((response: any) => {
                    //     //plan deatils
                    //     let planurl;
                    //     if (response.isCustomerPrime) {
                    //       planurl = `/premierePlan/all?custId=${custId}&isPremiere=true&serviceAreaId=${this.customerDetailData.serviceareaid}`;
                    //     } else {
                    //       planurl =
                    //         "/plans/serviceArea?serviceAreaId=" +
                    //         this.customerDetailData.serviceareaid;
                    //     }
                    //     while (
                    //       plandatalength <
                    //       this.customerDetailData.planMappingList.length
                    //     ) {
                    //       this.customerManagementService
                    //         .getMethod(planurl)
                    //         .subscribe((response: any) => {
                    //           this.dataPlan.push(response.postpaidplanList.name);
                    //           // console.log("dataPlan", this.dataPlan);
                    //         });
                    //       plandatalength++;
                    //     }
                    //   });
                }

                // charger Data
                if (this.customerDetailData.indiChargeList.length > 0) {
                    this.customerDetailData.indiChargeList.forEach(element => {
                        if (element.planid) {
                            const url = "/postpaidplan/" + element.planid;
                            this.customerManagementService.getMethod(url).subscribe((response: any) => {
                                this.dataChargePlan.push(response.postPaidPlan);
                            });
                        }
                    });
                }
                this.customerGroupForm?.get("serviceareaName").patchValue({
                    serviceareaName: this.customerAddressDetails?.serviceareaName
                })

                // console.log("this.paymentAddressData", this.paymentAddressData);
                // console.log("this.permanentAddressData", this.permanentAddressData);
                // console.log("this.customerDetailData", this.customerDetailData);
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    isContactDetailsValid(stepper: MatStepper) {
        this.customerGroupForm?.markAllAsTouched();
        const isMobileValid = this.customerGroupForm?.get("mobile")?.valid;
        const isEmailValid = this.customerGroupForm?.get("email")?.valid;
        if (isMobileValid && isEmailValid) {
            stepper?.next();
        } else {
            this.customerGroupForm?.get("mobile")?.markAsTouched();
            this.customerGroupForm?.get("email")?.markAsTouched();
        }
    }

    rejectCustomerCAFOpen(cafId, nextApproverId) {
        this.matdialog.open(this.Reject);
        this.reject = false;
        this.rejectCustomerCAFModal = true;
        this.assignCustomerCAFId = cafId;
        this.nextApproverId = nextApproverId;
    }

    onStaffSelectChange(event) {
        this.selectStaff = event.value;
    }

    assignToStaff(flag) {
        let url: any;

        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.assignCustomerCAFId}&eventName=${"CAF"}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignCustomerCAFId}&eventName=${"CAF"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.assignCustomerCAFId}&eventName=${"CAF"}&nextAssignStaff=${this.selectStaffReject}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignCustomerCAFId}&eventName=${"CAF"}&isApproveRequest=${flag}`;
            }
        }

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignCustomerCAFModal = false;
                this.rejectCustomerCAFModal = false;
                this.matdialog.closeAll();
                this.reAssignCustomerCAFModal = false;
                this.getcustomerList("");
                this.getCustomer();

                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.getCustomer();
                    this.toastr.success(`${response.message || 'Success!'}`, 'Assigned to the next staff successfully!');

                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    assignCustomerCAF() {
        this.assignCustomerCAFForm.markAllAsTouched()
        this.assignCustomerCAFsubmitted = true;
        if (this.assignCustomerCAFForm.valid) {
            const url = "/approveCaf";
            const assignCAFData = {
                custcafId: this.assignCustomerCAFId,
                nextStaffId: "",
                flag: "approved",
                remark: this.assignCustomerCAFForm.controls.remark.value,
                staffId: Number(localStorage.getItem("userId"))
            };
            const formData = new FormData();
            formData.append("custcafId", this.assignCustomerCAFId);
            formData.append("nextStaffId", "");
            formData.append("flag", "approved");
            formData.append("remark", this.assignCustomerCAFForm.controls.remark.value);
            formData.append("staffId", localStorage.getItem("userId"));
            this.customerManagementService.updateMethod(url, formData).subscribe(
                (response: any) => {
                    this.getcustomerList("");
                    //   this.getCustomer();
                    this.matdialog.closeAll();
                    this.addNotesForm.reset()
                    this.assignCustomerCAFForm.reset();
                    this.assignCustomerCAFsubmitted = false;
                    if (response?.result && response?.result?.dataList != null) {
                        this.approveCAF = response?.result?.dataList;
                        this.approveCAFData = this.approveCAF;
                        this.approved = true;
                        this.matdialog.open(this.approveCustomerDialog, {
                            width: '800px',
                        })
                        this.applyPagination(this.approveCAFData)
                    } else {
                        this.toastr.success(`${response.message}`, 'Success!');
                        this.assignCustomerCAFModal = false;
                        this.matdialog.closeAll();
                    }
                },
                (error: any) => {
                    // console.log(error, "error")
                    if (error.error.status == 417) {
                        this.toastr.info(`${error.error.message}`, 'Info!');
                        this.matdialog.closeAll();

                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        this.matdialog.closeAll();

                    }
                    this.assignCustomerCAFModal = false;
                    this.getcustomerList("");
                    this.getCustomer();
                    this.assignCustomerCAFForm.reset();
                    this.assignCustomerCAFsubmitted = false;
                    this.matdialog.closeAll();
                }
            );
        }
    }

    selectedFiles: File[] = [];
    customerIdData: any;

    rejectCustomerCAF() {
        this.rejectCustomerCAFsubmitted = true;
        if (this.rejectCustomerCAFForm.valid) {
            const url = "/approveCaf";
            const assignCAFData = {
                custcafId: this.assignCustomerCAFId,
                nextStaffId: null,
                flag: "rejected",
                remark: this.rejectCustomerCAFForm.controls.remark.value,
                staffId: Number(localStorage.getItem("userId")),
                files: this.file


            };

            const formData = new FormData();
            formData.append("custcafId", assignCAFData.custcafId.toString());

            formData.append(
                "nextStaffId",
                assignCAFData.nextStaffId == null ? "" : assignCAFData.nextStaffId.toString()
            );
            formData.append("flag", assignCAFData.flag);
            formData.append("remark", assignCAFData.remark);
            formData.append("files", assignCAFData.files);

            formData.append("staffId", assignCAFData.staffId.toString());
            this.selectedFileUploadPreview.forEach((file: File, index: number) => {
                formData.append("files", file, file.name);
                //   formData.append(`files[${index}]`, file);
            });
            this.customerManagementService.updateMethod(url, formData).subscribe(
                (response: any) => {
                    this.getcustomerList("");
                    this.matdialog.closeAll();
                    this.toastr.success(`${response.message}`, 'Success!');


                    this.rejectCustomerCAFModal = true;
                    this.rejectCustomerCAFForm.reset();
                    this.rejectCustomerCAFsubmitted = false;
                    if (response.result.dataList != null) {
                        this.rejectCAF = response.result.dataList;
                        this.rejectCafData = this.rejectCAF;
                        this.reject = true;
                        this.matdialog.open(this.rejectCustomerDialog, {
                            width: '800px',
                        })
                    } else {
                        this.rejectCustomerCAFModal = false;
                    }
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    customerApporeved(id: any) {
        const url = "/customerCaf/approve/" + id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getcustomerList("");
                this.toastr.success(`${response.message}`, 'Success!');


            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    customerRejected(id: any) {
        const url = "/customerCaf/reject/" + id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getcustomerList("");
                this.toastr.success(`${response.message}`, 'Success!');


            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
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
                    newAmount = element.plan.newOfferPrice
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
        //   const planDetailData = response.postPaidPlan;
        this.chargeGroupForm.patchValue({
            validity: Number(this.planDropdownInChageData.find(plan => plan.id == planId).validity),
            unitsOfValidity: this.planDropdownInChageData.find(plan => plan.id == planId).unitsOfValidity,
            expiry: this.planDropdownInChageData.find(plan => plan.id == planId).expiryDate
        });
        let planData = null;
        if (this.customerChangePlan) {
            planData = this.custCustDiscountList.find(element => element.id === this.custPlanMapppingId);
        } else {
            planData = this.payMappingListFromArray.value.find(element => element.planId === planId);
        }
        this.chargeGroupForm.patchValue({
            discount: planData ? planData.discount : 0
        });
        this.updateDiscountFromService(event.value, "");
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
        this.customerGroupForm.controls.title.setValue("");

        this.customerGroupForm.controls.servicetype.setValue("");
        this.customerGroupForm.controls.partnerid.setValue(this.partnerId !== 1 ? this.partnerId : "");
        this.customerGroupForm.controls.billday.setValue("");
        // this.customerGroupForm.controls.phone.setValue("");
        this.customerGroupForm.controls.mobile.setValue("");
        this.customerGroupForm.controls.billTo.setValue("CUSTOMER");
        this.customerGroupForm.controls.countryCode.setValue("");
        this.customerGroupForm.controls.calendarType.setValue("English");
        this.customerGroupForm.controls.custlabel.setValue("customer");
        this.customerGroupForm.controls.isInvoiceToOrg.setValue(false);
        this.customerGroupForm.controls.istrialplan.setValue(false);
        this.customerGroupForm.controls.status.setValue("");
        this.customerGroupForm.controls.serviceareaid.setValue("");
        this.customerGroupForm.controls.title.setValue("");

        this.chargeGroupForm.controls.chargeid.setValue("");
        this.chargeGroupForm.controls.charge_date.setValue("");
        this.chargeGroupForm.controls.planid.setValue("");
        this.chargeGroupForm.controls.type.setValue("");

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

    selServiceArea(event, isFromUI) {
        this.isPartnerSelected = false;
        if (isFromUI) {
            this.pincodeDD = [];
        }
        const serviceAreaId = event.value;
        if (serviceAreaId) {
            const url = "/serviceArea/" + serviceAreaId;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.serviceareaCheck = false;
                    this.serviceAreaData = response.data;
                    if (this.serviceAreaData.serviceAreaType != "private") {
                        this.customerGroupForm.controls.blockNo.clearValidators();
                        this.customerGroupForm.updateValueAndValidity();
                    } else {
                        this.customerGroupForm.controls.blockNo.setValidators(Validators.required);
                        this.customerGroupForm.updateValueAndValidity();
                    }
                    if (this.serviceAreaData.blockNo && !isNaN(this.serviceAreaData.blockNo)) {
                        const maxBlockNo = +this.serviceAreaData.blockNo;
                        this.blockNoOptions = Array.from({ length: maxBlockNo }, (_, i) => i + 1);
                    } else {
                        this.blockNoOptions = []; // Clear options if invalid
                    }
                    if (isFromUI) {
                        this.serviceAreaData.pincodes.forEach(element => {
                            this.commondropdownService.allpincodeNumber.forEach(e => {
                                if (e.pincodeid == element) {
                                    this.pincodeDD.push(e);
                                }
                            });
                            // this.pincodeDD.push(this.commondropdownService.allpincodeNumber.filter((e)=>e.pincodeid==element))
                        });
                    }

                    this.getPlanbyServiceArea(serviceAreaId);

                    if (!this.iscustomerEdit) {
                        if (isFromUI) {
                            this.presentGroupForm.reset();
                        }
                    }
                    // this.getAreaData(this.serviceAreaData.areaid, "present");
                },
                (error: any) => { }
            );
            this.getPartnerAllByServiceArea(serviceAreaId);
            this.getServiceByServiceAreaID(serviceAreaId);
            if (this.partnerId == 1) this.getBranchByServiceAreaID(serviceAreaId);
            this.getStaffUserByServiceArea(serviceAreaId);
            this.shiftLocationDTO.shiftPartnerid = "";
        }
    }
    onPartnerCategoryChange(event: any) { }
    getBranchByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/branchManagement/getAllBranchesByServiceAreaId";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            this.branchData = response.dataList;
            if (this.branchData != null && this.branchData.length > 0) {
                this.isBranchAvailable = true;
                this.customerGroupForm.controls.branch.setValue(response.dataList[0].id);
                this.customerGroupForm.controls.branch.setValidators(Validators.required);
                this.customerGroupForm.controls.partnerid.clearValidators();
                this.customerGroupForm.updateValueAndValidity();
            } else {
                this.isBranchAvailable = false;
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
        this.serviceAreaService.getMethod(url).subscribe((response: any) => {
            //
            this.staffList = response.dataList;
        });
    }

    getPlanbyServiceArea(serviceAreaId) {
        if (serviceAreaId) {
            this.filterPlanData = [];
            const url = "/plans/serviceArea?planmode=ALL&serviceAreaId=" + serviceAreaId;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planByServiceArea = response.postpaidplanList;
                    this.filterPlanData = this.planByServiceArea.filter(
                        plan => plan.plantype == this.custType
                    );
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    TotalLedgerItemPerPage(event) {
        this.legershowItemPerPage = Number(event.value);
        if (this.currentPagecustLedgerList > 1) {
            this.currentPagecustLedgerList = 1;
        }
        if (!this.customerLedgerSearchKey) {
            this.getCustomersLedger(this.customerDetailData.id, this.legershowItemPerPage);
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
        this.getCustomersLedger(this.customerDetailData.id, "");
    }

    // getCustomerAssignedList(id) {
    //
    //   this.customerInventoryMappingService.getByCustomerId(id).subscribe(
    //     (res: any) => {
    //       this.assignedInventoryList = res.dataList;
    //
    //     },
    //     (error: any) => {
    //       this.messageService.add({
    //         severity: "error",
    //         summary: "Error",
    //         detail: error.error.msg,
    //         icon: "far fa-times-circle",
    //       });
    //
    //     }
    //   );
    // }
    // searchProduct() {}
    // clearSearchProduct() {}
    //
    // next() {
    //   this.first = this.first + this.rows;
    // }
    //
    // prev() {
    //   this.first = this.first - this.rows;
    // }
    //
    // reset() {
    //   this.first = 0;
    // }
    //
    // isLastPage(): boolean {
    //   return this.assignedInventoryList
    //     ? this.first === this.assignedInventoryList.length - this.rows
    //     : true;
    // }
    //
    // isFirstPage(): boolean {
    //   return this.assignedInventoryList ? this.first === 0 : true;
    // }

    clearSearchCustomerLedger() {
        this.postdata.CREATE_DATE = "";
        this.postdata.END_DATE = "";
        this.custLedgerForm.controls.startDateCustLedger.setValue("");
        this.custLedgerForm.controls.endDateCustLedger.setValue("");
        this.custLedgerSubmitted = false;
        this.getCustomersLedger(this.customerDetailData.id, "");
    }

    TotalPaymentItemPerPage(event) {
        this.paymentShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerPaymentdata > 1) {
            this.currentPagecustomerPaymentdata = 1;
        }
        this.openCustomersPaymentData(this.customerDetailData.id, this.paymentShowItemPerPage);
    }

    openCustomersPaymentData(id, size) {
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
        this.revenueManagementService.paymentData(url).subscribe((response: any) => {
            this.viewcustomerPaymentData = response.dataList;
            this.viewCustomerPaymentList = true;
            this.listView = false;
            this.isViewTicketMenu = false
            this.createView = false;
            this.selectAreaList = false;
            this.selectPincodeList = false;
            this.isCustomerLedgerOpen = false;
            this.customerPlanView = false;
            this.isCustomerDetailSubMenu = true;
            this.isCustomerDetailOpen = true;
            this.customerChangePlan = false;
            this.customerrMyInventoryView = false;
            this.assignInventoryWithSerial = false;
            this.ifMyInvoice = false;
            this.isServiceOpen = false;
            this.ifShowDBRReport = false;
            this.ifChargeGetData = false;
            this.customerUpdateDiscount = false;
            this.customerStatusView = false;
            this.ipManagementView = false;
            this.macManagementView = false;
            this.customerCafNotes = false;
            this.ifWalletMenu = false;
            this.ifUpdateAddress = false;
            this.ifCafFollowUp = false;
            this.shiftLocationEvent = false;
            this.isCallDetails = false;
        });
        this.getPaymentHistory(id);
        this.revenueManagementService.paymentData(url).subscribe((response: any) => {
            this.viewcustomerPaymentData = response.dataList;
            this.InvoiceListByCustomer(id);
        });
        this.paymentModeData();
        // this.getCustomersDetail(id);
        this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
            this.tdsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
            this.abbsPercent = res.data.value;
        });
        this.getBankDetailType();
        this.getBankDestinationDetail();
        this.customerDetailData?.currency
            ? (this.currency = this.customerDetailData?.currency)
            : this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
                this.currency = res.data.value;
            });
    }
    getBankDetailType() {
        const url = "/bankManagement/searchByStatus?banktype=other";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
                // this.bankDestination = response.dataList.banktype
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getCommonFeasibilityList() {
        this.customerService.getCommonFeasibility().subscribe(
            (response: any) => {
                this.FeasibilityOptions = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getBankDestinationDetail() {
        const url = "/bankManagement/searchByStatus?banktype=operator";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                // this.bankDataList = response.dataList.banktype;
                this.bankDestination = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    closePaymentForm() {
        this.paymentFormGroup.reset();
        this.displayRecordPaymentDialog = false;
        this.submitted = false;
        this.isShowInvoiceList = false;
        this.selectedInvoice = [];
        this.file = "";
        this.fileName = null;
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
            error: (error) => {
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
            const url =
                "/serviceArea/getPlaceId?query=" + this.searchLocationForm.value.searchLocationname.trim();
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.searchLocationData = response.locations;
                },
                (error: any) => {
                    if (error.error.code == 422) {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


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
        const url = "/serviceArea/getLatitudeAndLongitude?placeId=" + placeId;
        this.savbillCommonBaseService.get(url).subscribe(
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
        this.matdialog.open(this.NearByLoc);
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
        const url = "/NetworkDevice/getNearbyDevices";
        this.customerManagementService.postMethodInventory(url, deviceData).subscribe(
            (response: any) => {
                this.nearDeviceLocationData = response.locations;
            },
            (error: any) => {
                this.toastr.error(`No Splitter Profile available`, 'Failed!');

            }
        );
    }

    pageChangedNearDeviceList(pageNumber) {
        this.currentPagenearDeviceLocationList = pageNumber;
    }

    nearsearchClose() {
        this.matdialog.closeAll();
        this.closeNearByLoc.emit();
        this.ifNearLocationModal = false;
        this.nearDeviceLocationData = [];
    }

    getParentCust(event) {
        if (event.value) {
            this.customerGroupForm.controls.invoiceType.enable();
            this.customerGroupForm.controls.parentExperience.enable();
        } else {
            this.customerGroupForm.controls.invoiceType.disable();
            this.customerGroupForm.controls.parentExperience.disable();
        }
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
                this.planGroupForm.patchValue({
                    validity: Number(planDetailData.validity),
                    offerprice: Number(planDetailData.offerprice),
                    currency: planDetailData.currency
                });
                this.validityUnitFormGroup.patchValue({
                    validityUnit: planDetailData.unitsOfValidity
                });
                if (planDetailData.category == "Business Promotion") {
                    this.ifplanisSubisuSelect = true;
                    this.payMappingListFromArray.controls = [];
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
                // this.planGroupForm.controls.validity.disable();
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    isValidPlanDetails(stepper: MatStepper) {
        if (this.payMappingListFromArray?.controls?.length || this.planGroupMapingList?.length) {
            stepper?.next();
        } else {
            this.toastr?.error("Please add Plan Details first", "Error!")
        }
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
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getCustomerTeamHierarchy(custId) {
        const url = `/teamHierarchy/getApprovalProgress?entityId=${custId}&eventName=CAF`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerStatusDetail = response.dataList;
                // const newList = [];
                // let parentId = null;
                // for (const item of this.customerStatusDetail) {
                //   for (const item1 of this.customerStatusDetail) {
                //     if (parentId === item1.parentTeamsId) {
                //       newList.push(item1);
                //       parentId = item1.teamsId;
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
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    assigneInventory(customerId) {
        this.assignInventory = true;
        this.customerId = customerId;
    }

    openMyInventory(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.customerrMyInventoryView = true;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.assignInventoryCustomerId = id;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false
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
        const url = "/regeneratePdfForTrail/" + docnumber;
        this.invoiceMasterService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, custname);
                this.toastr.success(`Successfully`, 'Success!');

            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    cancelRegenrateInvoice(invoice) {
        const data = {};

        const url = "/invoiceV2/cancelAndRegenerate/" + invoice.id + "?isCaf=true";
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                // this.closebutton.nativeElement.click();
                this.ifInvoicePayment = false;
                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.isSinglepaymentChecked = false;
                this.invoicePaymentData = [];
                this.allchakedPaymentData = [];
                this.searchinvoiceMaster("", "");

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
            error: (error) => {
                this.toastr.info(`${error.responseMessage}`, 'Total payment is already adjusted!');
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
                this.searchinvoiceMaster(this.customerDetailData.id, "");
                this.toastr.success(`${response.message}`, 'Success!');


            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    // InvoiceListByCustomer(id) {
    //     const url = "/invoiceList/byCustomer/" + id;
    //     this.invoiceList = [];

    //     this.revenueManagementService.getAllInvoiceByCustomer(url).subscribe(
    //         (response: any) => {
    //             const invoiceList = response.invoiceList;
    //             this.invoiceList.push(...this.invoicedropdownValue);
    //             this.invoiceList.push(...invoiceList);
    //         }
    //         //   (error: any) => {
    //         //     // console.log(error, "error")
    //         //     this.messageService.add({
    //         //       severity: "error",
    //         //       summary: "Error",
    //         //       detail: error.error.ERROR,
    //         //       icon: "far fa-times-circle",
    //         //     });
    //         //
    //         //   }
    //     );
    // }
    InvoiceListByCustomer(id) {
        const url = "/invoiceList/byCustomer/" + id;
        this.invoiceList = [];
        const Data = [];
        this.masterSelected = false;

        this.revenueManagementService.getAllInvoiceByCustomer(url).subscribe(
            (response: any) => {
                const invoicedata = [];
                if (response.invoiceList != null && response.invoiceList.length != 0) {
                    this.invoiceList.push(...response.invoiceList);
                } else {
                    this.invoiceList.push(...this.invoicedropdownValue);
                }
                // this.invoiceList = Data;
                this.invoiceList.forEach(item => {
                    item.tdsCheck = 0;
                    item.abbsCheck = 0;
                    item.tds = 0;
                    item.abbs = 0;
                    item.includeTds = false;
                    item.includeAbbs = false;
                    item.testamount = this.getPendingAmount(item);
                    item.convertedAmount = item.testamount * this.convertedExchangeRate;
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    modalOpenParentCustomer(type) {
        this.parentCustomerDialogType = type;
        // this.showParentCustomerModel = true;
        this.customerSelectType = "Billable To";
        if (type === "parent") {
            this.customerSelectType = "Parent";
        }
        this.selectedParentCust = [];

        const dialogRef = this.dialogg.open(CustomerSelectComponent, {
            width: '900px',
            maxWidth: '90vw',
            autoFocus: false,
            data: {
                type: this.custType,
                custId: this.customerDetailData?.id,
                selectedCust: this.selectedParentCust
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.selectedCustChange(result);
            }
        });
    }

    getParentCustomerData() {
        //
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
    onPageChange(event: any): void {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        this.getAllCustomerNotes(this.customerDetailData?.id);
    }

    async selectedCustChange(event) {
        this.showParentCustomerModel = false;
        this.selectedParentCust = event;
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
            this.customerGroupForm.controls.parentExperience.enable();
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
                // console.log("response1", parentCustServiceAreaId);
            });
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
            this.customerGroupForm.controls.parentExperience.setValue("");
            this.customerGroupForm.controls.parentExperience.disable();
            this.planGroupForm.controls.invoiceType.setValue("");
            this.planGroupForm.controls.invoiceType.disable();

            this.customerGroupForm.controls.serviceareaid.setValue("");
            this.customerGroupForm.controls.branch.setValue("");
            this.customerGroupForm.controls.partnerid.setValue("");
            this.serviceAreaDisable = false;
            this.parentCustList = [];
        }
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
        this.prepaidParentCustomerList = [];
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

        searchParentData.filters[0].filterValue = this.searchParentCustValue.trim();
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

    closeParentCust() {
        this.displayInvoiceDetails = false;
        this.showParentCustomerModel = false;
    }

    closeParentCusttt() {
        this.showParentCustomerModel = false;
    }

    closeModel() {
        this.visibleQuotaDetails = false;
        this.PlanQuota = new BehaviorSubject({
            custid: "",
            PlanData: ""
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

        const url = "/subscriber/changeCustomerDiscountServiceLevel/" + this.customerDetailData.id;
        if (data.length != 0) {
            this.customerManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.getcustDiscountDetails(this.customerDetailData.id, "");
                    this.toastr.success(`Discount Updated Successfully`, 'Success!');


                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


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

    planGroupSelectSubisu(e) {
        if (e.value) {
            let url = "/findPlanGroupById?planGroupId=" + e.value;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    const planDetailData = response.planGroup;
                    if (response.planGroup.allowDiscount == true) {
                        this.ifcustomerDiscountField = true;
                    }
                    if (planDetailData.category == "Business Promotion") {
                        this.ifplanisSubisuSelect = true;
                        this.customerGroupForm.patchValue({
                            billTo: "ORGANIZATION",
                            isInvoiceToOrg: planDetailData.invoiceToOrg
                        });

                        // $('#selectPlanGroup').modal('show')
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

                        let newAmount = 0;
                        let totalAmount = 0;
                        this.planIds = [];
                        planDetailData.planMappingList.forEach((element, i) => {
                            let n = i + 1;
                            newAmount = element.plan.newOfferPrice
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
                        this.discountPercentage({});
                        // if (this.customerChangePlan) {
                        //   $("#selectPlanGroup").modal("show");
                        //   this.planGroupSelectedSubisu = e;
                        //   console.log(this.planGroupSelectedSubisu);
                        //   this.getPlanListByGroupIdSubisu();
                        // }
                    }
                },
                (error: any) => { }
            );
        }

        this.getPlangroupByPlan(e.value);
        this.planGroupDataById(e.value);
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
                    let newAmount = element.newOfferPrice ? element.newOfferPrice : element.offerprice;

                    this.plansArray.push(
                        this.fb.group({
                            planId: element.id,
                            name: element.displayName,
                            service: element.serviceId,
                            validity: element.validity,
                            discount: element.discount,
                            billTo: "ORGANIZATION",
                            offerPrice: element.offerprice,
                            newAmount: element.newOfferPrice ? element.newOfferPrice : element.offerprice,
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
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;

        this.ifWalletMenu = true;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.ifChargeGetData = false;
        this.isCallDetails = false;
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

    openchargeDetails(custId) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.ifChargeGetData = true;
        this.chargeUseCustID = custId;
        this.shiftLocationEvent = false;
        this.isCallDetails = false;
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

    assignToStaffInventory(flag) {
        let url: any;
        let name: string;

        if (this.customerUpdateDiscount) {
            name = "CUSTOMER_DISCOUNT";
        } else if (this.shiftLocationEvent) {
            name = "SHIFT_LOCATION";
        } else {
            name = "CUSTOMER_INVENTORY_ASSIGN";
        }
        if (flag) {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${name}&nextAssignStaff=${this.InventoryselectStaff}&isApproveRequest=${flag}`;
        } else {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${name}&nextAssignStaff=${this.InventoryselectStaffReject}&isApproveRequest=${flag}`;
        }

        this.customerManagementService.getMethod(url).subscribe(
            response => {
                if (flag) {
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'Approved Successfully!');
                    }


                } else {
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'Rejected Successfully!');
                    }


                }
                $("#assignCustomerInventoryModal").modal("hide");
                $("#rejectCustomerInventoryModal").modal("hide");
                if (this.customerUpdateDiscount) {
                    this.openCustorUpdateDiscount(this.customerDetailData.id);
                } else if (this.shiftLocationEvent) {
                    this.openCustomerAddress;
                } else {
                    this.getCustomerAssignedList(this.assignInventoryCustomerId);
                }

                this.getCustomer();
                this.getcustomerList("");
                // this.newCustomerAddressDataForCustometr(this.customerDetailData.id);
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    custWorkflowAuditopen(id, auditcustid) {
        this.getworkflowAuditDetails("", auditcustid, "CAF");
        this.ifModelIsShow = true;
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid,
            checkHierachy: "CAF",
            planId: ""
        });
    }

    custTerminationWorkflowAuditopen(id, auditcustid) {
        this.getworkflowAuditDetails("", auditcustid, "CAF");
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

    custDiscountWorkflowAuditopen(id, auditcustid, planID) {
        this.ifModelIsShow = true;
        this.getworkflowAuditDetails("", auditcustid, "CUSTOMER_DISCOUNT");
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid,
            checkHierachy: "CUSTOMER_DISCOUNT",
            planId: planID
        });
    }

    // DBR

    discountRejected(data) {
        this.rejectApproveDiscountDialogRef = this.matdialog.open(this.rejectApproveDiscountDialog, {
            width: '500px',
        })
        this.assignDiscountData = data;
        this.discountFlageType = "Rejected";
        this.AppRjecHeader = "Reject";
        this.assignAppRejectDiscountForm.reset();
    }

    discountApporeved(data) {
        // $("#rejectApproveDiscountModal").modal("show");
        this.rejectApproveDiscountDialogRef = this.matdialog.open(this.rejectApproveDiscountDialog, {
            width: '500px',
        })
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
                // assignedDate: '',
                // credDocId: '',
                custPackageId: this.assignDiscountData.id,
                // custcafId: '',
                flag: this.discountFlageType,
                // newDiscount: this.assignDiscountData.newDiscount,
                nextStaffId: 0,
                planId: this.assignDiscountData.planId,
                remark: this.assignAppRejectDiscountForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
                // status: ''
            };

            this.customerManagementService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    $("#rejectApproveDiscountModal").modal("hide");
                    if (response.dataList) {
                        this.staffList = response.dataList;
                        if (this.discountFlageType == "approved") {
                            this.approved = true;
                            this.approveInventoryData = response.dataList;
                            $("#assignCustomerInventoryModal").modal("show");
                        } else {
                            this.reject = true;
                            this.rejectInventoryData = response.dataList;
                            $("#rejectCustomerInventoryModal").modal("show");
                        }
                        $("#customerDiscount").modal("show");
                    } else {
                        this.openCustorUpdateDiscount(this.customerDetailData.id);
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
                this.paginatedWorkflowData.data = this.workflowAuditData;
                // this.filesArray = response.dataList?.files;
                this.filesArray = this.workflowAuditData[0]?.files || [];
                this.MastertotalRecords = response.totalRecords;
                this.workflowAuditData.forEach(element => {
                    if (element.files) {
                        this.filesArray = element.files;
                    }
                });
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    pageChangedMasterList(event: PageEvent): void {
        this.currentPageMasterSlab = event.pageIndex + 1;
        this.showItemPerPage = event.pageSize;
        this.getworkflowAuditDetails(this.showItemPerPage, this.workflowID, "CAF");
    }

    TotalItemPerPageWorkFlow(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMasterSlab > 1) {
            this.currentPageMasterSlab = 1;
        }
        this.getworkflowAuditDetails(this.showItemPerPage, this.workflowID, "CAF");
    }

    withdrawalAmountModel(modelID, wCustID, WalletAmount) {
        this.displayDialogWithDraw = true;

        // this.PaymentamountService.show(modelID);
        this.wCustID.next({
            wCustID,
            WalletAmount
        });
    }

    closeSelectStaff() {
        this.displayDialogWithDraw = false;
    }

    selectedStaffChangee(event) {
        this.displayDialogWithDraw = false;
    }

    openDBRReportDetails() {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifChargeGetData = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.ifShowDBRReport = true;
        this.shiftLocationEvent = false;
        this.currentPageDBRListdata = 1;
        this.showItemDBRPerPage = 0;
        this.DBRListdatatotalRecords = RadiusConstants.ITEMS_PER_PAGE;
        this.dbrListData = [];
        this.searchDBR();
        this.isCallDetails = false;
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
        let firstDay;
        let lastDay;
        firstDay = this.searchDBRFormDate;
        lastDay = this.searchDBREndDate;
        const url =
            "/getCustomer?custid=" +
            this.customerDetailData.id +
            "&startdate=" +
            firstDay +
            "&endate=" +
            lastDay;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.dbrListData = response;
                this.DBRListdatatotalRecords = this.dbrListData.length;

                this.searchDBRFormDate = "";
                this.searchDBREndDate = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    searchClearDBR() {
        this.searchDBRFormDate = "";
        this.searchDBREndDate = "";
        this.searchDBR();
    }

    pageChangedDbrList(pageNumber) {
        this.currentPageDBRListdata = pageNumber;
    }

    downloadInvoice(docId, custId, fileName) {

        const url = "/documentForInvoice/download/" + docId + "/" + custId;
        this.customerManagementService.downloadInvoice(url).subscribe(
            (response: any) => {
                var fileType = "";
                // if (fileName.includes(".png")) {
                //   fileType =
                // }
                var file = new Blob([response]);
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, fileName);
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

    newCustomerAddressDataForCustometr: any;
    getNewCustomerAddressForCustomer(id): void {
        const url = "/newcustomeraddress/" + id;

        this.customerManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.newCustomerAddressDataForCustometr = res.newcustomerAddress;
            },
            (error: any) => { }
        );
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
                    }
                    this.openCustomerAddress();
                    this.assignAppRejectShiftLocationForm.reset();
                    this.assignShiftLocationsubmitted = false;

                    // this.newCustomerAddressDataForCustometr(this.customerDetailData.id);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    openCustomerAddress() {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = true;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = true;
        this.isCallDetails = false;
    }

    generatePDFInvoice(custId) {
        if (custId) {
            const url = "/generateTrialPdfByInvoiceId/" + custId;
            this.customerManagementService.generateMethodInvoice(url).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.responseMessage}`, 'success!');


                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }
    //   getCustomerType() {
    //     const url = "/commonList/Customer_Type";
    //     const custerlist = {};
    //     this.commondropdownService.getMethodWithCache(url).subscribe(
    //       (response: any) => {
    //         this.Customertype = response.dataList;
    //         // console.log(this.customerGroupForm.value.subType,"this.customerGroupForm.value.subType");

    //         if (this.customerGroupForm.value.subType) {
    //         }
    //       },
    //       (error: any) => {
    //         console.log(error, "error");
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: error.error.ERROR,
    //           icon: "far fa-times-circle"
    //         });
    //       }
    //     );
    //   }
    getSelectCustomerType(event) {
        const selCustomerType = event.value;
        if (selCustomerType == "Paid") {
            this.customerGroupForm.controls.subType.enable();
        }
    }
    //   getCustomerSector() {
    //     const url = "/commonList/Customer_Sector";
    //     const custerlist = {};
    //     this.commondropdownService.getMethodWithCache(url).subscribe(
    //       (response: any) => {
    //         this.CustomerSector = response.dataList;
    //         console.log(this.CustomerSector, " this.CustomerSector");
    //       },
    //       (error: any) => {
    //         console.log(error, "error");
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: error.error.ERROR,
    //           icon: "far fa-times-circle"
    //         });
    //       }
    //     );
    //   }
    getcustType(event) {
        let value = event.value;
        // this.customerGroupForm.controls.dunningSubType.enable();
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
    //   getSelectCustomerSector(event) {
    //     const value = event.value;
    //     if (value) {
    //       this.customerGroupForm.controls.dunningSubSector.enable();
    //     } else {
    //       this.customerGroupForm.controls.dunningSubSector.disable();
    //     }
    //   }

    downloadPDFINvoice(docNo, customerName) {
        if (docNo) {
            const downloadUrl = "/trialinvoicePdf/download/" + docNo;
            this.customerManagementService.downloadPDFInvoice(downloadUrl).subscribe(
                (response: any) => {
                    const file = new Blob([response], { type: "application/pdf" });
                    const fileURL = URL.createObjectURL(file);
                    FileSaver.saveAs(file, customerName + docNo);
                },
                (error: any) => {
                    // console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    canExit() {
        return this.utils.canExit(this.customerGroupForm.dirty);
    }

    checkExit(type) {
        // this.getcustomerList("");
        this.currentPagecustomerListdata = 1;
        this.customerListdataitemsPerPage = 20;
        if (this.isCustomerDetailSubMenu || !this.customerGroupForm.dirty) {
            this.customerGroupForm.markAsPristine();
            if (type === "create") {
                this.createCustomerCaf();
                this.openStepper();
            } else {
                this.listCustomer();
            }
        } else {

            const dialogRef2 = this.matdialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Alert',
                    description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                    yesLabel: 'Yes',
                    noLabel: 'No'
                }
            });

            dialogRef2.afterClosed().subscribe(result => {
                if (result) {
                    this.customerGroupForm.markAsPristine();
                    if (type === "create") {
                        this.createCustomerCaf();
                    } else {
                        this.listCustomer();
                    }
                } else {
                    return false;
                }
            });
        }
    }
    openInventoryDetailModal(modalId, data) {
        this.CustomerInventoryDetailsService.show(modalId);
        this.inventoryData.next({
            inventoryData: data
        });
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
    pickModalOpen(data) {
        let url = "/workflow/pickupworkflow?eventName=CAF&entityId=" + data.id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.getcustomerList("");
                if (this.searchkey || this.searchOption || this.username || this.searchDeatil || this.searchDeatil || this.staff || this.activationbyname || this.createbyname
                    || this.cafStatus || this.team || this.cafCreatedBy || this.serviceArea || this.plan) {
                    this.searchcustomer();
                } else {
                    this.getcustomerList("");
                }

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'success!');

                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    viewInvoice(docnumber, custname) {
        const url = "/regeneratePdfForTrail/" + docnumber;
        this.invoiceMasterService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                window.open(fileURL, "_blank");
                this.toastr.success(`Successfully`, 'Success!');


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

    openCustomersChangePlan(data) {
        this.isVisibleCAFHomeComponent = false;
        this.paymentModeData();
        this.commondropdownService.getChargeTypeByList("", this.currency);
        // this.commondropdownService.getChargeTypeByList();
        this.commondropdownService.getPlanPurchaseType();
        this.childPlanRenewArray = this.fb.array([]);
        this.changePlanForm.reset();
        this.chargenewPlanForm.reset();
        this.changePlanForm.controls.planId.setValue("");
        this.changePlanForm.controls.planGroupId.setValue("");
        this.changePlanForm.controls.purchaseType.setValue("Changeplan");
        this.changePlanForm.controls.remarks.setValue("");
        this.filterPlanListCust = [];
        this.planListByType = [];
        this.selPlanData = [];
        this.changePlanDate = [];
        this.newAdddiscountdata = [];
        this.planDiscount = 0;
        this.finalOfferPrice = 0;
        this.pageNumberForChildsPageForChangePlan = 1;
        this.pageSizeForChildsPageForChangePlan = RadiusConstants.ITEMS_PER_PAGE;
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = true;
        this.isCustomerLedgerOpen = false;
        this.viewCustomerPaymentList = false;
        this.customerPlanView = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.customerUpdateDiscount = false;
        this.shiftLocationEvent = false;
        this.isPlanTypeAddon = false;
        this.changeplanGroupFlag = true;
        this.ifPlanSelectChanePlan = false;
        this.changeplanGroupFlag = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
        if (data.plangroupid) {
            this.lastRenewalPlanGroup(data.id);
            this.getplanChangeforplanGroup(data.id);
        }

        this.getcustDiscountDetails(data.id, "");
        this.customerchargeDATA(data.id, "parent");
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
                planurl = `/premierePlan/all?custId=${data.id}&isPremiere=true&serviceAreaId=${this.customerDetailData.serviceareaid}`;
                planGroupurl = `/planGroupMappings?mode=""`;
                specialPlanURL = `/plansByServiceAreaCustId?custId=${data.id}&planmode=SPECIAL&serviceAreaId=${this.customerDetailData.serviceareaid}`;
            }
            if (this.customerDetailData.plangroupid != null) {
                let url = "/findPlanGroupById?planGroupId=" + this.customerDetailData.plangroupid;

                this.customerManagementService.getMethod(url).subscribe((response: any) => {
                    PlanGroupCatogry = response.planGroup.category;

                    if (!responsePrime.isCustomerPrime) {
                        planGroupurl =
                            `/planGroupMappings?mode=NORMAL` +
                            "&planCategory=" +
                            PlanGroupCatogry +
                            "&custId=" +
                            this.customerDetailData.id;
                        planurl =
                            "/plans/serviceArea?planCategory=" +
                            "NORMAL" +
                            "&serviceAreaId=" +
                            this.customerDetailData.serviceareaid +
                            "&planmode=NORMAL";
                    }
                    this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                        this.filterPlanGroupListCust = response.planGroupList.filter(
                            plan => plan.plantype === this.customerDetailData.custtype
                        );
                        let data1;
                        let data2;
                        if (this.filterPlanGroupListCust) {
                            data1 = this.filterPlanGroupListCust.filter(
                                plan => plan.servicearea.id == this.customerDetailData.serviceareaid
                            );
                            data2 = this.filterNormalPlanGroup.filter(plan =>
                                plan.servicearea.forEach(e => e == this.customerDetailData.serviceareaid)
                            );
                        }
                        setTimeout(() => {
                            this.filterPlanGroupListCust = [...data1, ...data2];
                        }, 1000);
                        this.filterPlanGroupListCust.forEach((element, index) => {
                            // if (
                            //   element.planGroupId == this.customerDetailData.plangroupid
                            // ) {
                            //   this.filterPlanGroupListCust.splice(index, 1)
                            // }

                            if (element.planMode == "SPECIAL") {
                                element.planGroupName = element.planGroupName + " - (SP)";
                            }
                        });
                        this.newPlanGroupData = this.filterPlanGroupListCust;
                        this.changePlanType("Changeplan", null);

                    });
                    this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                        this.planListByType = response.postpaidplanList.filter(
                            plan => plan.plantype === this.customerDetailData.custtype
                        );

                        this.planListByType.forEach(element => {
                            if (element.mode == "SPECIAL") {
                                element.name = element.name + " - (SP)";
                            }
                        });
                    });
                });
            } else {
                if (this.customerDetailData.planMappingList.length > 0 && !responsePrime.isCustomerPrime) {
                    const url = "/postpaidplan/" + this.customerDetailData.planMappingList[0].planId;
                    this.customerManagementService.getMethod(url).subscribe((response: any) => {
                        planCategory = response.postPaidPlan.category;

                        if (!responsePrime.isCustomerPrime) {
                            planurl =
                                "/plans/serviceArea?planCategory=" +
                                planCategory +
                                "&serviceAreaId=" +
                                this.customerDetailData.serviceareaid +
                                "&planmode=NORMAL" +
                                "&custId=" +
                                this.customerDetailData.id;
                        }
                        this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                            this.filterPlanListCust = response.postpaidplanList.filter(
                                plan => plan.plantype === this.customerDetailData.custtype
                            );
                            this.serviceURL = planurl;
                            this.filterPlanListCust.forEach(element => {
                                if (element.mode == "SPECIAL") {
                                    element.name = element.name + " - (SP)";
                                }
                            });
                            //console.log(this.filterPlanListCust, "DataList plan");
                            this.changePlanType("Changeplan", null);
                        });
                    });
                } else {
                    if (!responsePrime.isCustomerPrime) {
                        planurl =
                            "/plans/serviceArea?planCategory=" +
                            "Normal" +
                            "&serviceAreaId=" +
                            this.customerDetailData.serviceareaid +
                            "&planmode=NORMAL" +
                            "&custId=" +
                            this.customerDetailData.id;
                    }
                    this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                        plandata1 = response.postpaidplanList.filter(
                            plan => plan.plantype === this.customerDetailData.custtype
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
                                        plan => plan.plantype === this.customerDetailData.custtype
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
                                                this.changePlanType("Changeplan", null);
                                            }
                                        });
                                    } else if (plandata2.length == 0) {
                                        this.filterPlanListCust = plandata1;
                                        this.changePlanType("Changeplan", null);
                                    }
                                });
                        } else {
                            this.filterPlanListCust = plandata1;
                            this.changePlanType("Changeplan", null);
                        }
                        // console.log(this.filterPlanListCust, "DataList plan");
                    });
                }

                const loginUserName = localStorage.getItem("loginUserName");
                const userId = localStorage.getItem("userId");
                if (!Array.isArray(this.staffSelectList) || this.staffSelectList.length === 0) {
                    this.staffSelectList = [
                        {
                            id: Number(userId),
                            name: loginUserName
                        }
                    ];
                }
                this.changePlanForm.patchValue({
                    paymentOwnerId: Number(userId)
                });
            }
        });

        this.changePlanForm.get("isPaymentReceived").setValue(false);
        // this.customerChildsView = false; --zulfin
        this.getserviceData("");
        this.getChildCustomersForChangePlan(data.id);
        this.assignInventoryWithSerial = false;
        this.getcustCurrentPlan(data.id, "");
        //this.staffId=serviceAreaId
        this.serviceAreaId = data.serviceareaid;
        this.getStaffDetailById(this.serviceAreaId);
        this.changeUpDownGrade(this.customerDetailData, this.changePlanForm.value.planType);
    }
    lastRenewalPlanGroup(id) {
        const url = "/subscriber/lastrenewalplangroupid/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.lastRenewalPlanGroupID = response.lastRenewalPlanGroupId;
        });
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
    getserviceData(groupId) {
        let services = [];
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerDetailData.id +
            "?isNotChangePlan=false" +
            "&status=NewActivation";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custServiceData = response.dataList;
                this.customerCurrentPlanListdatatotalRecords = this.custServiceData.length;
                this.custPlanChangeData = new MatTableDataSource(this.custServiceData);
                // services = [...new Set(services)];
                if (this.custServiceData.length >= 1) {
                    if (groupId) {
                        this.planSelected = null;
                        this.changePlanRemark = null;
                        this.planGroupSelected = groupId;
                        this.getPlanListByGroupId();
                        $("#selectPlanGroupChangeService").modal("show");
                        this.enableChangePlanGroup = false;
                    } else {
                        this.planSelected = null;
                        this.changePlanRemark = null;
                        // $("#selectPlanChangeService").modal("show");
                    }
                }
                this.filterplan();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
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
                this.childCustomerDataListForChangePlan.forEach(element => {
                    if (element.indiChargeList.length == 0) {
                        chargeAvailable = false;
                    } else {
                        chargeAvailable = true;
                    }
                    const url = "/subscriber/getActivePlanList/" + element.id + "?isNotChangePlan=false";
                    let planList = [];
                    this.customerManagementService.getMethod(url).subscribe((response: any) => {

                        planList = response.dataList;
                        if (planList.length < 2) {
                            this.childPlanGroupFlag = false;
                            this.childPlan_PLANGROUPID.push({
                                id: planList.length > 0 ? [planList[planList.length - 1].planId] : ""
                            });
                            this.childPlanRenewArray.push(
                                this.fb.group({
                                    custId: [element.id],
                                    planId: planList.length > 0 ? [planList[planList.length - 1].planId] : "",
                                    planType: ["Renew"],
                                    changePlan: [false],
                                    chargeAblSele: [chargeAvailable]
                                })
                            );
                        } else if (planList.length >= 2) {
                            this.childPlanGroupFlag = true;
                            let groupId;
                            setTimeout(() => {
                                this.filterPlanGroupListCust.forEach(e => {
                                    if (e.planGroupName == planList[0].planGroupName) {
                                        groupId = e.planGroupId;
                                    }
                                });
                                this.childPlan_PLANGROUPID.push({ id: groupId });
                                this.childPlanRenewArray.push(
                                    this.fb.group({
                                        custId: [element.id],
                                        planGroupId: groupId,
                                        planType: ["Renew"],
                                        changePlan: [false],
                                        chargeAblSele: [chargeAvailable]
                                    })
                                );
                            }, 1000);
                        }
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
    getStaffDetailById(serviceAreaId) {
        const url = "/getstaffuserbyserviceareaid/" + serviceAreaId;
        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            this.staffDataList = response.dataList;
            //console.log("staffDataList", this.data);
            this.staffDataList.forEach((element, i) => {
                element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
                this.data.push(element.id);
            });
        });
        // this.serviceAreaId = this.serviceAreaData.id;
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

    getPlanListByGroupId() {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);

        const url = `/plansByPlanGroupId?planGroupId=` + this.planGroupSelected;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planList = response.planList;
                if (this.custServiceData) {
                    if (this.changenewPlanForm.value.ChangePlanCategory == "groupPlan")
                        this.groupPlanListByType = this.planList;
                    // this.planListByType = this.planList;
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
    onNewBindingPlanMapping() {
        this.newPlanSelectArray.push(this.createteamConditionForm());
    }
    createteamConditionForm(): UntypedFormGroup {
        return this.fb.group({
            newPlan: [""]
        });
    }

    savegraceDays() {
        const url = `/subscriber/promiseToPay/${this.customerDetailData.id}?promise_to_pay_remarks=""`;

        this.customerManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    this.toastr.success(`${res.message}`, 'Success!');


                    // $("#IdgraceDays").modal("hide");
                    this.graceNumberDays = "";
                } else {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');


                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }
    onUpdateParentChange(e) {
        if (e.checked == true) {
            this.UpdateParentCustPlans = true;
        } else {
            this.UpdateParentCustPlans = false;
        }
    }
    filterplan() {
        let i: number;
        this.custServiceData.forEach(element => {
            if (element.customerServiceMappingId == this.custPlanMapppingId) {
                i = element.serviceId;
                this.selectedCustService = element;
            }
        });
        this.getParentCustomerData();
        if (this.selectedCustService?.billablecust != null) {
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

    changePlanType(purchaseType, selected) {
        if (selected) {
            this.custPlanMapppingId = selected.value;
        }

        this.chargenewPlanForm.reset();
        if (this.customerDetailData.plangroupid) {
            this.filterPlanListCust = [];
            this.newPlanGroupData.forEach(planGroup => {
                planGroup.planMappingList.forEach(planMapping => {
                    this.filterPlanListCust.push(planMapping.plan);
                });
            });
        }
        this.changePlanForm.reset(this.changePlanForm.value);
        this.changeplanGroupFlag = false;
        this.selPlanData = [];
        this.finalOfferPrice = 0;
        this.changePlanForm.patchValue({
            purchaseType: purchaseType,
            planGroupId: "",
            isPaymentReceived: false,
            planId: "",
            plancharge: ""
        });
        this.isPlanTypeAddon = false;
        if (!this.customerDetailData.plangroupid) {
            this.planListByType = [];
        }
        if (purchaseType != null && purchaseType != undefined) {

            if (purchaseType === "Addon") {
                this.isPlanTypeAddon = true;
                this.changeplanGroupFlag = false;
                if (!this.customerDetailData.plangroupid) {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.planGroup === "Volume Booster" ||
                            plan.planGroup === "Bandwidthbooster" ||
                            plan.planGroup === "DTV Addon"
                    );
                    this.filterplan();
                } else {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.planGroup === "Volume Booster" ||
                            plan.planGroup === "Bandwidthbooster" ||
                            plan.planGroup === "DTV Addon"
                    );
                }
                this.changePlanForm.get("planGroupId").disable();
                this.changePlanForm.get("planList").disable();
                this.changePlanForm.get("planId").enable();
                this.planGroupFlag = false;
                if (!this.customerDetailData.plangroupid) {
                    this.getserviceData("");
                } else {
                    this.getserviceData(this.customerDetailData.planGroupId);
                }
                this.planByService = this.planListByType;
            } else if (purchaseType === "Changeplan") {
                if (this.customerDetailData.plangroupid) {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.planGroup === "Registration" || plan.planGroup === "Registration and Renewal"
                    );
                    this.changePlanForm.get("planGroupId").enable();
                    this.changePlanForm.get("planList").enable();
                    this.changePlanForm.get("planId").disable();
                    this.planGroupFlag = true;
                    this.getPlangroupByPlan(this.customerDetailData.plangroupid);
                    // if (this.custCurrentPlanList.length > 1) this.changeplanGroupFlag = true;
                    // else this.changeplanGroupFlag = false;
                    this.changeplanGroupFlag = true;
                }
                if (!this.customerDetailData.plangroupid) {
                    this.changePlanForm.get("planGroupId").disable();
                    this.changePlanForm.get("planList").disable();
                    this.changePlanForm.get("planId").enable();
                    this.planListByType = this.filterPlanListCust.filter(
                        plan =>
                            plan.planGroup === "Registration" || plan.planGroup === "Registration and Renewal"
                    );
                    this.planGroupFlag = false;
                    // if (this.custCurrentPlanList.length > 1) this.changeplanGroupFlag = true;
                    // else this.changeplanGroupFlag = false;
                    this.changeplanGroupFlag = false;
                    this.filterplan();
                } else {
                    this.filterSelectedPlanGroupListCust = this.newPlanGroupData.filter(
                        plan =>
                            plan.planGroupType === "Registration" ||
                            plan.planGroupType === "Registration and Renewal"
                    );
                }
                this.isPlanTypeAddon = false;
            } else if (purchaseType === "Renew") {
                if (this.customerDetailData.plangroupid) {
                    this.planListByType = this.filterPlanListCust.filter(
                        plan => plan.planGroup === "Renew" || plan.planGroup === "Registration and Renewal"
                    );
                    this.changePlanForm.get("planGroupId").enable();
                    this.changePlanForm.get("planList").enable();
                    this.changePlanForm.get("planId").disable();
                    this.planGroupFlag = true;
                    this.getPlangroupByPlan(this.customerDetailData.plangroupid);
                    this.filterSelectedPlanGroupListCust = this.newPlanGroupData.filter(
                        plan =>
                            plan.planGroupType === "Renew" || plan.planGroupType === "Registration and Renewal"
                    );
                    // if (this.custCurrentPlanList.length > 1) this.changeplanGroupFlag = true;
                    // else this.changeplanGroupFlag = false;
                    this.changeplanGroupFlag = true;
                } else {
                    this.changePlanForm.get("planGroupId").disable();
                    this.changePlanForm.get("planList").disable();
                    this.changePlanForm.get("planId").enable();
                    this.planListByType = this.filterPlanListCust.filter(
                        plan => plan.planGroup === "Renew" || plan.planGroup === "Registration and Renewal"
                    );
                    this.planGroupFlag = false;
                    this.changeplanGroupFlag = true;
                    this.filterplan();
                }
                this.isPlanTypeAddon = false;
            }
        }
        this.planListByType.forEach(e => {
            if (e.quotatype == "Data") {
                e.label =
                    e.name +
                    ` (${e.quota} ${e.quotaUnit}
          ${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval} 
            - ${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
            } else if (e.quotatype == "Time") {
                e.label =
                    e.name +
                    ` (${e.quotatime} ${e.quotaunittime}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval
                    }  - ${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
            } else if (e.quotatype == "Both") {
                e.label =
                    e.name +
                    ` (${e.quota} ${e.quotaUnit}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval
                    }  and ${e.quotatime} ${e.quotaunittime}${e.quotaResetInterval == "Total" ? "" : "/" + e.quotaResetInterval
                    }  - ${e.validity} ${e.unitsOfValidity} ${e.qospolicyName ? "-" + e.qospolicyName : ""})`;
            } else {
                e.label = e.name;
            }
        });
        if (selected) {
            this.changeUpDownGrade('', null);
        }
    }

    changeUpDownGrade(data, event) {
        let planDiscount = "";
        let plan = "";
        let newURL;
        if (this.customerDetailData.planMappingList.length > 0) {
            let length = this.customerDetailData.planMappingList.length;
            for (let lastListNum = length - 1; lastListNum > -1; lastListNum--) {
                if (
                    this.customerDetailData.planMappingList[lastListNum].plangroup != "Bandwidthbooster" &&
                    this.customerDetailData.planMappingList[lastListNum].plangroup != "Volume Booster"
                ) {
                    plan = this.customerDetailData.planMappingList[lastListNum].planId;
                    planDiscount = String(this.planDiscount);
                    break;
                }
            }
        }
        this.serviceURL =
            "/plans/serviceArea?planCategory=" +
            "NORMAL" +
            "&serviceAreaId=" +
            this.customerDetailData.serviceareaid +
            "&planmode=NORMAL";
        if (event == null || undefined) {
            newURL = this.serviceURL;

        } else {
            newURL = this.serviceURL;
        }

        this.customerManagementService.getPlan(newURL).subscribe((response: any) => {
            this.planByService = response.postpaidplanList;
        });
    }

    changeBillingCycle(event) {
        //  this.selectedBillingCycle = event.value;
        this.changePlanForm.controls.billingCycle.setValue(event.value);
    }

    getChangePlan($event, custid) {
        if (this.changenewPlanForm.value.ChangePlanCategory == "groupPlan") {
            this.changePlanForm.get("planGroupId").setValidators([Validators.required]);
            this.changePlanForm.get("planGroupId").updateValueAndValidity();
            this.confirmationService.confirm({
                message: "Do you want Change Plan to continue?",
                header: "Change Plan Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    // this.subisuChange = true;
                    this.planGroupFlag = true;
                    // this.customerDetailData.plangroupid = 0;
                    this.getplanChangeforplanGroup(this.custDetilsCustId);
                },
                reject: () => {
                    // this.subisuChange = false;

                    this.planGroupFlag = false;
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'Rejected Successfully!');
                    }

                    // $("#selectPlanChange").modal("show");
                }
            });
        } else if (
            this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
            this.customerDetailData.plangroupid !== null
        ) {
            this.changePlanForm.get("planGroupId").setValue(null);
            this.changePlanForm.get("planGroupId").clearValidators();
            this.changePlanForm.get("planGroupId").updateValueAndValidity();
            this.getplanChangeforplanGroup(this.custDetilsCustId);
            this.modalOpenPlanChange({ value: this.customerDetailData.plangroupid });
        } else if (this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan") {
            this.planGroupFlag = false;
        }
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
        if (this.customerDetailData.planMappingList[0].billTo == "ORGANIZATION") {
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
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'Rejected Successfully!');
                    }

                    this.getserviceData(e.value);
                    this.getPlanListByGroupId();
                }
            });
        } else {
            this.getserviceData(e.value);
            this.getPlanListByGroupId();
        }
    }
    getPlanDetailById($event) {
        this.planDropdownInChageData = [];
        this.plansArray = this.fb.array([]);
        this.ifPlanSelectChanePlan = true;
        this.planSelected = $event.value;
        const url = "/postpaidplan/" + this.planSelected;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.selPlanData = response.postPaidPlan;
                this.planDropdownInChageData.push(response.postPaidPlan);

                // console.log("this.selPlanData", this.selPlanData);
                const date = new Date();
                this.selPlanData.activationDate = this.datePipe.transform(date, "dd-MM-yyyy");
                this.selPlanData.expiryDate = date.setDate(date.getDate() + this.selPlanData.validity);
                this.selPlanData.expiryDate = this.datePipe.transform(
                    this.selPlanData.expiryDate,
                    "dd-MM-yyyy"
                );
                this.selPlanData.finalAmount = this.selPlanData.offerprice + this.selPlanData.taxamount;
                this.changePlanStartEndDate();
                let discountData = this.custCustDiscountList.find(
                    element => element.id === this.custPlanMapppingId
                );
                this.planDiscount = discountData.discount ? discountData.discount : 0;
                this.updateDiscountFromService($event.value, $event.index);
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
            this.offerPrice = 0;
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
                                this.finalOfferPrice += Number(response.result.finalAmount.toFixed(3));
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
            this.changePlanForm.value.discount = this.planDiscount;
            this.finalOfferPrice = 0;
            this.offerPrice = this.selPlanData.offerprice;
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
            this.customerDetailData.planMappingList[0].billTo == "ORGANIZATION" ||
            this.customerDetailData.planMappingList[0].billTo == "Organization"
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
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'You have rejected!');
                    }

                    // $("#selectPlanChange").modal("show");
                }
            });
        }
        // }
    }

    getSelectCustomerPlanType(e, plant) {
        this.selectPlan0Rplangroup = plant;
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
    onPaymentTypeChange(data) {
        if (data === "YES") {
            this.changePlanForm.controls.recordPaymentDTO.enable();
            this.changePlanForm.get("recordPaymentDTO").get("chequeDate").disable();
            this.changePlanForm.get("recordPaymentDTO").get("bankName").disable();
            this.changePlanForm.get("recordPaymentDTO").get("branch").disable();
            this.changePlanForm.get("recordPaymentDTO").get("referenceNo").disable();
            this.changePlanForm.get("recordPaymentDTO").get("chequeNo").disable();
        } else {
            this.changePlanForm.controls.recordPaymentDTO.disable();
        }
    }
    selPayMode(event) {
        const payMode = event.value;
        if (payMode == "Cheque") {
            this.changePlanForm.get("recordPaymentDTO").get("chequeDate").enable();
            this.changePlanForm.get("recordPaymentDTO").get("bankName").enable();
            this.changePlanForm.get("recordPaymentDTO").get("branch").disable();
            this.changePlanForm.get("recordPaymentDTO").get("referenceNo").disable();
            this.changePlanForm.get("recordPaymentDTO").get("chequeNo").enable();
            // this.changePlanForm.controls.recordPaymentDTO.chequeDate.enable();

            // this.changePlanForm.controls.recordPaymentDTO.referenceNo.disable();
        } else if (payMode == "Online") {
            this.changePlanForm.get("recordPaymentDTO").get("chequeDate").disable();
            this.changePlanForm.get("recordPaymentDTO").get("bankName").enable();
            this.changePlanForm.get("recordPaymentDTO").get("branch").enable();
            this.changePlanForm.get("recordPaymentDTO").get("referenceNo").enable();
            this.changePlanForm.get("recordPaymentDTO").get("chequeNo").disable();
        } else if (payMode == "Cash") {
            this.changePlanForm.get("recordPaymentDTO").get("chequeDate").disable();
            this.changePlanForm.get("recordPaymentDTO").get("bankName").disable();
            this.changePlanForm.get("recordPaymentDTO").get("branch").disable();
            this.changePlanForm.get("recordPaymentDTO").get("referenceNo").disable();
            this.changePlanForm.get("recordPaymentDTO").get("chequeNo").disable();
        } else if (payMode == "EFTs") {
            this.changePlanForm.get("recordPaymentDTO").get("chequeDate").disable();
            this.changePlanForm.get("recordPaymentDTO").get("bankName").enable();
            this.changePlanForm.get("recordPaymentDTO").get("branch").enable();
            this.changePlanForm.get("recordPaymentDTO").get("referenceNo").enable();
            this.changePlanForm.get("recordPaymentDTO").get("chequeNo").disable();
        }
    }


    changePlan() {
        const newPlan = [];
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
            planId: this.changePlanForm.value.planId
                ? this.changePlanForm.value.planId
                : this.planSelected,
            remarks: this.changePlanForm.value.remarks
                ? this.changePlanForm.value.remarks
                : this.changePlanRemark
        });

        if (this.changePlanForm.valid) {
            if (
                this.changePlanForm.value.paymentOwnerId === null ||
                this.changePlanForm.value.paymentOwnerId === ""
            ) {
                this.paymentOwnerError = true;
            } else {
                if (this.changePlanForm.value.purchaseType !== "Changeplan") {
                    this.changePlanData = this.changePlanForm.value;
                    this.changePlanData.isAdvRenewal = false;
                    this.changePlanData.custId = this.customerDetailData.id;

                    if (!this.changePlanData.recordPaymentDTO) {
                        this.changePlanData.recordPaymentDTO = {};
                    } else {
                        this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
                        this.changePlanData.recordPaymentDTO.custId = this.customerDetailData.id;
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

                    const CustChangePlan = {
                        changePlanRequestDTOList: [this.changePlanData]
                    };
                    const url = "/subscriber/changePlan01";

                    //  console.log("this.changePlanData", this.changePlanData);
                    this.customerManagementService.postMethod(url, CustChangePlan).subscribe(
                        (response: any) => {
                            if (response.responseCode == 200) {
                                this.toastr.success(`${response.responseMessage}`, 'Success!');


                                $("#selectPlanChangeService").modal("hide");
                                $("#selectPlanGroupChangeService").modal("hide");
                                $("#addRemark").modal("hide");
                                this.changePlansubmitted = false;
                                this.planDiscount = 0;
                                this.finalOfferPrice = 0;
                                this.groupOfferPrices = {};
                                this.selPlanData = [];
                                this.changePlanBindigNewPlan = [];
                                this.changePlanForm.reset();
                                this.selectPlanListIDs = [];
                                this.changePlanDate = [];
                                if (this.addChargeForm.value.chargeAdd == true) {
                                    this.createNewChargeData(this.customerDetailData.id);
                                }
                                this.changePlanForm.get("isPaymentReceived").setValue(false);
                                this.openCustomersChangePlan(this.customerDetailData);
                            } else {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');

                                this.changePlanForm.get("isPaymentReceived").setValue(false);
                            }
                        },
                        (error: any) => {
                            // console.log(error, "error")
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            this.changePlanForm.get("isPaymentReceived").setValue(false);
                        }
                    );
                } else {
                    if (this.changePlanForm.value.purchaseType == "Changeplan") {
                        this.newchangePlanfunctionality(this.chargenewPlanForm.value.plancharge);
                    }
                }
            }
        }
    }
    changePlanTypeForChangePlan($event) {
        this.childPlanType = $event.value;
        // this.isPlanTypeAddon = false;
        this.planListByType = [];
        if ($event.value != null && $event.value != undefined) {
            if ($event.value === "Addon") {
                // this.isPlanTypeAddon = true;
                this.planListByType = this.filterPlanListCust.filter(
                    plan => plan.planGroup === "Volume Booster" || plan.planGroup === "Bandwidthbooster"
                );
            } else if ($event.value === "Renew") {
                // this.isPlanTypeAddon = false;
                this.planListByType = this.filterPlanListCust.filter(plan => plan.planGroup === "Renew");
            }
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
    pageChangeEventForChildCustomersForChangePlan(pageNumber: number) {
        this.pageNumberForChildsPageForChangePlan = pageNumber;
        this.getChildCustomersForChangePlan(this.customerDetailData.id);
    }
    itemPerPageChangeEventForChangePlan(event) {
        this.childPlanRenewArray = this.fb.array([]);
        this.pageSizeForChildsPageForChangePlan = Number(event.value);
        this.getChildCustomersForChangePlan(this.customerDetailData.id);
    }
    changePlanFromParent() {
        const newPlan = [];
        let PlanData: any = {};
        let CustChangePlan: any = [];
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
            this.changePlanData.custId = this.customerDetailData.id;
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
                this.customerDetailData.plangroupid !== null
            ) {
                this.changePlanData.planGroupId = this.customerDetailData.plangroupid;
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
            if (this.childPlanType) this.changePlanData.purchaseType = this.childPlanType;
            else this.changePlanData.purchaseType = this.changePlanForm.value.purchaseType;
            // this.changePlanData = this.changePlanForm.value;
            if (!this.changePlanData.recordPaymentDTO) {
                this.changePlanData.recordPaymentDTO = {};
            } else {
                this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
                this.changePlanData.recordPaymentDTO.custId = this.customerDetailData.id;
            }

            this.changePlanData.custServiceMappingId = this.custPlanMapppingId
                ? this.custPlanMapppingId
                : this.childCustomerDataListForChangePlan[0].planMappingList[0].custServiceMappingId;
            this.changePlanData.remarks = this.changePlanForm.value.remarks;
            // this.changePlanData.updatedate = null
            // this.changePlanData.walletBalUsed =null
            // this.changePlanData.discount = this.planDiscount;
            if (this.changePlanForm.valid && this.UpdateParentCustPlans == true) {
                pareChildPojo.push(this.changePlanData);
            }

            this.childPlanRenewArray.value.forEach(element => {
                PlanData = {};
                if (element.changePlan == true) {
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
                    if (this.selectPlanChildListIDs.length !== 0) {
                        PlanData.newPlanList = this.selectPlanChildListIDs;
                    } else {
                        PlanData.newPlanList = null;
                    }
                    // PlanData.newPlanList = this.selectPlanChildListIDs;
                    PlanData.purchaseType = this.changePlanData.purchaseType;

                    if (!PlanData.recordPaymentDTO) {
                        PlanData.recordPaymentDTO = {};
                    } else {
                        PlanData.recordPaymentDTO.isTdsDeducted = false;
                        PlanData.recordPaymentDTO.custId = this.customerDetailData.id;
                    }
                    PlanData.remarks = this.changePlanData.remarks;
                    // PlanData.updatedate =null
                    // PlanData.walletBalUsed =null

                    PlanData.custServiceMappingId = this.custPlanMapppingId
                        ? this.custPlanMapppingId
                        : this.childCustomerDataListForChangePlan[0].planMappingList[0].custServiceMappingId;

                    pareChildPojo.push(PlanData);
                }
            });

            CustChangePlan = {
                changePlanRequestDTOList: pareChildPojo
            };

            if (this.changePlanForm.value.purchaseType !== "Changeplan") {
                if (CustChangePlan.changePlanRequestDTOList.length !== 0) {
                    const url = "/subscriber/changePlan01";

                    this.customerManagementService.postMethod(url, CustChangePlan).subscribe(
                        (response: any) => {
                            if (response.responseCode == 200) {
                                this.toastr.success(`${response.responseMessage}`, 'Success!');


                                $("#selectPlanGroupChangeService").modal("hide");
                                $("#addRemark").modal("hide");
                                if (this.addChargeForm.value.chargeAdd == true) {
                                    this.createNewChargeData(this.customerDetailData.id);
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
                                this.changePlanForm.get("isPaymentReceived").setValue(false);
                                this.changePlanBindigNewPlan = [];
                                this.changePlanBindigChildNewPlan = [];
                                this.planDiscount = 0;
                                this.changePlanForm.reset();
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
                                this.openCustomersChangePlan(this.customerDetailData);
                            } else {

                                this.toastr.error(`${response.responseMessage}`, 'Failed!');


                                this.changePlanForm.get("isPaymentReceived").setValue(false);
                            }
                        },
                        (error: any) => {
                            // console.log(error, "error")
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            this.changePlanForm.get("isPaymentReceived").setValue(false);
                            this.UpdateParentCustPlans == false;
                        }
                    );
                }
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
        //   this.changePlanData.custId = this.customerDetailData.id;
        //   if (!this.changePlanData.recordPaymentDTO) {
        //     this.changePlanData.recordPaymentDTO = {};
        //   } else {
        //     this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
        //     this.changePlanData.recordPaymentDTO.custId =
        //       this.customerDetailData.id;
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
        this.changePlanData.custId = this.customerDetailData.id;
        if (!this.changePlanData.recordPaymentDTO) {
            this.changePlanData.recordPaymentDTO = {};
        } else {
            this.changePlanData.recordPaymentDTO.isTdsDeducted = false;
            this.changePlanData.recordPaymentDTO.custId = this.customerDetailData.id;
        }
        this.changePlanData.isRefund = false;
        this.changePlanData.custServiceMappingId = this.custPlanMapppingId;

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

        // this.changePlanData.newPlanList= this.selectPlanListIDs
        const CustChangePlan = {
            changePlanRequestDTOList: [this.changePlanData]
        };

        const url = "/subscriber/getStartAndEndDate";
        //  console.log("this.changePlanData", this.changePlanData);
        this.customerManagementService.postMethod(url, CustChangePlan).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.changePlanDate = response.data;
                } else {
                    this.changePlanForm.get("isPaymentReceived").setValue(false);
                }
            },
            (error: any) => {
                this.changePlanForm.get("isPaymentReceived").setValue(false);
            }
        );
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
    newchangePlanfunctionality(newPlanID) {
        let newplanGroup = "";
        let newplan = "";
        let planGroup = "";
        let plan = "";
        let planGroupdiscount = 0;
        let planDiscount = "";
        if (this.selectPlan0Rplangroup == "PlanGroup") {
            newplanGroup = newPlanID ? newPlanID : this.planGroupSelected;
            planGroup = this.customerDetailData.plangroupid;
            planGroupdiscount = this.customerDetailData.discount;
        } else {
            newplan = newPlanID ? newPlanID : this.planSelected;

            if (this.customerDetailData.planMappingList.length > 0) {
                let length = this.customerDetailData.planMappingList.length;
                for (let lastListNum = length - 1; lastListNum > -1; lastListNum--) {
                    if (
                        this.customerDetailData.planMappingList[lastListNum].plangroup != "Bandwidthbooster" &&
                        this.customerDetailData.planMappingList[lastListNum].plangroup != "Volume Booster"
                    ) {
                        plan = this.customerDetailData.planMappingList[lastListNum].planId;
                        planDiscount = String(this.planDiscount); //this.customerDetailData.planMappingList[lastListNum].discount;
                        break;
                    }
                }
            }
        }

        let newChangePlan = [];
        let planList: any;
        let staffIdData: any;
        staffIdData = this.staffDataList.id;

        //console.log("staffIdData",staffIdData
        if (this.customerDetailData.plangroupid || this.planGroupFlag) {
            let updatedData = [];
            if (this.filterPlanGroupListCust.length > 0)
                newplan = newplan ? newplan : this.filterPlanGroupListCust[0].planMappingList[0].plan.id;

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
                                    this.customerDetailData.plangroupid !== null
                                    ? ""
                                    : this.planGroupSelected,
                            planGroupId: this.planGroupSelected,
                            newPlanId: e.newplan,
                            custServiceMappingId: e.custServiceMappingId,
                            discount: e.discount
                        };
                    } else {
                        data = {
                            billToOrg: false,
                            newPlanGroupId:
                                this.changenewPlanForm.value.ChangePlanCategory !== "groupPlan" &&
                                    this.customerDetailData.plangroupid !== null
                                    ? ""
                                    : this.planGroupSelected,
                            planGroupId: this.planGroupSelected,
                            newPlanId: e.newplan,
                            custServiceMappingId: e.custServiceMappingId,
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
                    this.customerDetailData.plangroupid !== null
                ) {
                    deactivatePlanReqModels.forEach(models => {
                        planList = {
                            custId: this.customerDetailData.id,
                            deactivatePlanReqModels: [models],
                            planGroupChange: false,
                            planGroupFullyChanged: false
                        };
                        newChangePlan.push(planList);
                    });
                } else {
                    planList = {
                        custId: this.customerDetailData.id,
                        deactivatePlanReqModels: updatedData.length > 0 ? updatedData : secondryData,
                        planGroupChange: true,
                        planGroupFullyChanged: true
                    };
                    newChangePlan.push(planList);
                }
            }, 300);
        } else {
            if (this.subisuChange) {
                planList = {
                    custId: this.customerDetailData.id,
                    deactivatePlanReqModels: [
                        {
                            billToOrg: true,
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
                        this.staffData != null &&
                            this.staffData.length > 0 &&
                            this.changePlanForm.value.paymentOwnerId != null
                            ? this.staffData.filter(
                                staff => staff.id === this.changePlanForm.value.paymentOwnerId
                            )[0].fullName
                            : "",
                    paymentOwnerId: this.changePlanForm.value.paymentOwnerId,
                    billableCustomerId: this.changePlanForm.value.billableCustomerId
                };
            } else {
                planList = {
                    custId: this.customerDetailData.id,
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
                    //   changePlanBillingCycle: this.selectedBillingCycle,
                    paymentOwner:
                        this.staffData != null &&
                            this.staffData.length > 0 &&
                            this.changePlanForm.value.paymentOwnerId != null
                            ? this.staffData.filter(
                                staff => staff.id === this.changePlanForm.value.paymentOwnerId
                            )[0].fullName
                            : "",
                    paymentOwnerId: this.changePlanForm.value.paymentOwnerId,
                    billableCustomerId: this.changePlanForm.value.billableCustomerId
                };
            }
            newChangePlan.push(planList);
        }
        setTimeout(() => {
            const url = "/subscriber/deactivatePlan";
            //  console.log("this.changePlanData", this.changePlanData);
            newChangePlan.forEach(newCP => {
                this.customerManagementService.postMethod(url, newCP).subscribe(
                    (response: any) => {
                        if (newChangePlan.indexOf(newCP) === newChangePlan.length - 1) {
                            this.chargenewPlanForm.reset();
                            this.changePlanForm.reset();
                            this.changenewPlanForm.controls.ChangePlanCategory.reset();
                            this.changePlansubmitted = false;
                            this.getCustomersDetail(this.customerDetailData.id);
                            this.getCustomerNetworkLocationDetail(this.customerDetailData.id);
                            this.openCustomersChangePlan(this.customerDetailData);
                            this.selectPlan0Rplangroup = "";
                            $("#selectPlanChangeService").modal("hide");
                            $("#addRemark").modal("hide");
                            $("#selectPlanGroupChangeService").modal("hide");
                        }
                        this.toastr.success(`${response.responseMessage}`, 'Success!');


                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            });
        }, 500);
    }
    lastRenewalChildPlanGroup(id) {
        const url = "/subscriber/lastrenewalplangroupid/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.lastRenewal_CHILDPlanGroupID = response.lastRenewalPlanGroupId;
        });
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
    resetnewBindingPlan(id) {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);
        this.planList.forEach(element => {
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
    resetnewBindingPlansChild(id) {
        this.newPlanSelectArray.reset();
        this.newPlanSelectArray = this.fb.array([]);
        this.planListChild.forEach(element => {
            this.onNewBindingPlanMapping();
        });
        this.serviceWisePlansValue(id);
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
    filterplanGroup(id, custPlanMapppingId, index) {
        this.custPlanMapppingId = custPlanMapppingId;
        this.planByService = [];

        if (this.changenewPlanForm.value.ChangePlanCategory == "groupPlan") {
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
            this.planByService = this.planListByType;
            this.planByService = this.planByService.filter(item => item.serviceId == id);
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
        this.custServiceData.forEach(element => {
            if (!(element.newplan && element.newplan !== null && element.newplan !== "")) {
                this.enableChangePlanGroup = false;
            }
        });
    }

    closeDialogOfReAssignCustomerCAF() {
        this.matdialog.closeAll();
        this.selectStaff = null;
    }

    StaffReasignList(data) {
        this.reassignDataRefresh = data;
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.id}&eventName=CAF`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Success",
                    //   detail: response.responseMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
                if (response.dataList != null) {
                    this.assignCustomerCAFId = data.id;
                    this.approveCAF = response.dataList;
                    this.reassigndata = this.approveCAF;
                    this.approved = true;
                    this.reAssignCustomerCAFModal = true;
                    this.matdialog.open(this.reAssignCustomerCAFModalDialog, {
                        width: '600px'
                    })
                } else {
                    this.reAssignCustomerCAFModal = false;
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    NewStaffReasignList() {
        let url =
            "/teamHierarchy/reassignWorkflowGetStaffList?entityId=" +
            this.reassignDataRefresh.id +
            "&eventName=CAF";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Success",
                    //   detail: response.responseMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
                if (response.dataList != null) {
                    this.assignCustomerCAFId = this.reassignDataRefresh.id;
                    this.approveCAF = response.dataList;
                    this.reassigndata = this.approveCAF;
                    this.approved = true;
                    // $("#reAssignCustomerCAFModal").modal("show");
                } else {
                    // $("#reAssignCustomerCAFModal").modal("hide");
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    reassignWorkflow() {
        let url: any;
        this.remarks = this.assignCustomerCAFForm.controls.remark;
        if (this.assignCustomerCAFId != null) {
            url = `/teamHierarchy/reassignWorkflow?entityId=${this.assignCustomerCAFId}&eventName=CAF&assignToStaffId=${this.selectStaff}&remark=${this.remarks.value}`;

            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.reAssignCustomerCAFModal = false;
                    this.getcustomerList("");
                    this.getCustomer();
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    } else {
                        this.getCustomer();
                        this.toastr.success(`${response.responseMessage}`, 'Assigned to the next staff successfully!');


                    }
                    this.matdialog.closeAll();
                },
                error => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please Aprove Before Reassigne!');
            }


        }
    }
    assignDiscountToStaffInventory(flag) {
        let url: any;
        let name: string;
        if (this.customerUpdateDiscount) {
            name = "CUSTOMER_DISCOUNT";
        } else if (this.shiftLocationEvent) {
            name = "SHIFT_LOCATION";
        }
        if (flag) {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.assignDiscountData.id}&eventName=${name}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
        } else {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.assignDiscountData.id}&eventName=${name}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
        }

        this.customerManagementService.getMethod(url).subscribe(
            response => {
                if (flag) {
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'Approved Successfully!');
                    }


                    // $("#customerDiscount").modal("hide");
                    this.onCancelStaffAssign()
                } else {
                    error: (error) => {
                        this.toastr.success(`${error.message}`, 'Rejected Successfully!');
                    }



                    $("#customerDiscount").modal("hide");
                }
                this.getCustomer();
                this.getcustomerList("");
                // this.newCustomerAddressDataForCustometr(this.customerDetailData.id);
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                //$("#customerDiscount").modal("hide");
            }
        );
        $("#customerDiscount").modal("hide");
    }
    assignCustDiscountApprove() {
        this.assignDiscounsubmitted = true;
        if (this.assignAppRejectDiscountForm.valid) {
            let url = "/approveChangeDiscountServiceLevel";
            this.getCustomer();
            let assignCAFData = {
                // assignedDate: '',
                // credDocId: '',
                custPackageId: this.assignDiscountData.id,
                // custcafId: '',
                flag: this.discountFlageType,
                // newDiscount: this.assignDiscountData.newDiscount,
                nextStaffId: 0,
                planId: this.assignDiscountData.planId,
                remark: this.assignAppRejectDiscountForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
                // status: ''
            };

            this.customerManagementService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    $("#rejectApproveDiscountModal").modal("hide");
                    this.onCancelDiscount()
                    if (response.dataList) {
                        this.staffList = response.dataList;
                        this.staffListDatasource.data = this.staffList;
                        // if (this.discountFlageType == "approved") {
                        //   this.approved = true;
                        //   this.approveInventoryData = response.dataList;
                        //   $("#assignCustomerInventoryModal").modal("show");
                        // } else {
                        //   this.reject = true;
                        //   this.rejectInventoryData = response.dataList;
                        //   $("#rejectCustomerInventoryModal").modal("show");
                        // }
                        // $("#customerDiscount").modal("show");
                        this.customerDiscountDialogRef = this.matdialog.open(this.customerDiscountDialog, {
                            width: '1000px',
                        })
                    } else {
                        this.openCustorUpdateDiscount(this.customerDetailData.id);
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

    openServiceDetails(custId) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.customerrMyInventoryView = false;
        this.assignInventoryWithSerial = false;
        this.ifMyInvoice = false;
        this.ifShowDBRReport = false;
        this.isServiceOpen = true;
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifChargeGetData = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.chargeUseCustID = custId;
        this.shiftLocationEvent = false;
        this.ifCafFollowUp = false;
        this.isCallDetails = false;
        this.isVisibleCAFHomeComponent = false;
    }

    clearcustPlanMappping() {
        this.custPlanMapppingId = null;
    }

    planCreationType() {
        const planBindingType = localStorage.getItem("planBindingType");
        this.isPlanOnDemand = planBindingType === "On-Demand";
    }

    daySequence() {
        for (let i = 0; i < 31; i++) {
            this.days.push({ label: i + 1 });
        }
    }
    earlyDaySequence() {
        for (let i = 0; i <= 31; i++) {
            this.earlydays.push({ label: i.toString() });
        }
        if (this.createView) {
            this.customerGroupForm.patchValue({
                earlybillday: this.earlydays[0].label
            });
        }
    }
    parentExperienceSelect(e) {

        this.planGroupForm.value.invoiceType = "Group";
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

    pageChangedCustQuotaList(pageNumber) {
        this.currentPagecustQuotaList = pageNumber;
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
                ?.allowDiscount;
        } else {
            return false;
        }
    }

    quotaPlanDetailsModel(modelID, custid, PlanData) {
        this.PaymentamountService.show(modelID);
        this.PlanQuota.next({
            custid,
            PlanData
        });
    }

    openDetailCust(event) {
        this.customerDetailOpen(event);
    }

    getrequiredDepartment() {
        const url = "/department/all";
        this.countryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.departmentListData = res.departmentList;
                // this.departmenttotalRecords = res.pageDetails.totalRecords;

                this.searchkey = "";
            },
            (err: any) => {
                error: (error) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Something went wrong while fetching lead origin types!');
                }


            }
        );
    }

    selectedStaff: any = [];
    selectStaffType = "";
    staffSelectList: any = [];
    showSelectStaffModel = false;
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
        } else if (this.selectStaffType == "changePlanCharge") {
            this.changePlanForm.patchValue({
                paymentOwnerId: data.id
            });
        }
    }

    removeSelectStaff() {
        this.staffSelectList = [];
    }

    closeSelectStafff() {
        this.showParentCustomerModel = false;
    }

    getCustomerNetworkLocationDetail(custId) {
        if (this.statusCheckService.isActiveInventoryService) {
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

    locationMacModelOpen() {
        this.matdialog.open(this.loactionSelect);
        this.getAllLocation();
        this.showLocationMac = true;
    }

    locationMacModelClose() {
        this.matdialog.closeAll()
        this.closeLocation.emit()
        this.showLocationMac = false;
    }

    locationChange(value: any) {
        let locationUrl = "";
        if (value != null && value.length > 0) {
            value.forEach(location => {
                if (locationUrl == "") {
                    locationUrl = locationUrl + "locationId=" + location;
                } else {
                    locationUrl = locationUrl + "&locationId=" + location;
                }
            });

            let isParent;
            if (this.customerGroupForm.value.isParentLocation) {
                isParent = this.customerGroupForm.value.isParentLocation;
            } else {
                isParent = false;
            }
            locationUrl = locationUrl + "&isParentLocation=" + isParent;

            this.locationService.getAllMacByLocation(locationUrl).subscribe((response: any) => {
                this.macData = response.msg;
            });
        }
    }

    macChangeChange(event: any, dd: any) {
        this.overLocationMacArray = this.fb.array([]);
        if (dd.value.length > 0) {
            dd.value.forEach(mac => {
                let findmatch = this.macData.find(data => data.mac === mac);
                if (findmatch) {
                    this.overLocationMacArray.push(
                        this.fb.group({
                            name: [findmatch.name],
                            mac: [findmatch.mac],
                            locationId: [findmatch.locationId],
                            isAlreadyAvailable: false
                        })
                    );
                }
            });
        }

        if (this.custLocationData.length > 0) {
            this.custLocationData.forEach(custLocation => {
                this.overLocationMacArray.push(
                    this.fb.group({
                        name: [custLocation.locationName],
                        mac: [custLocation.mac],
                        locationId: [custLocation.locationId],
                        isAlreadyAvailable: true
                    })
                );
            });
        }
    }

    deleteLocationMapField(locationMapField: any, index: number) {
        const existingIndex = this.custLocationData.findIndex(
            x => x.locationId === locationMapField.value.locationId
        );
        this.custLocationData.splice(existingIndex);
        this.overLocationMacArray.removeAt(index);
    }

    saveLocationMacData() {
        this.locationMacData = this.overLocationMacArray.value.map(location => ({
            locationId: location.locationId, //location.locationId
            mac: location.mac,
            isParentLocation: this.customerGroupForm.value.isParentLocation
        }));
        this.showLocationMac = false;
    }

    locationMacModelCancel() {
        this.locationMacForm = this.fb.group({
            location: ["", Validators.required],
            mac: ["", Validators.required]
        });
        var selectedLocation = [];
        this.custLocationData = [];
        this.overLocationMacArray = this.fb.array([]);
        this.locationMacForm.get("mac").setValue("");
        this.locationMacData = [];

        this.custLocationData = [...this.viewcustomerListData.customerLocations];

        this.viewcustomerListData.customerLocations.forEach(location => {
            if (selectedLocation.indexOf(location.locationId) === -1) {
                selectedLocation.push(location.locationId);
            }
            this.overLocationMacArray.push(
                this.fb.group({
                    name: [location.locationName],
                    mac: [location.mac],
                    locationId: [location.locationId],
                    isAlreadyAvailable: true,
                    isParentLocation: this.customerGroupForm.value.isParentLocation
                })
            );
        });
        if (this.overLocationMacArray.value.length > 0) {
            this.locationMacData = this.overLocationMacArray.value.map(location => ({
                locationId: location.locationId, //location.locationId
                mac: location.mac,
                isParentLocation: location.isParentLocation
            }));
        }
        this.locationChange(selectedLocation);
        this.locationMacForm.get("location").setValue(selectedLocation);
        this.showLocationMac = false;
    }

    parentLocationCheck(event: any) {
        if (event.checked) {
            this.locationMacData = this.locationMacData.map(location => ({
                locationId: location.locationId, //location.locationId
                mac: location.mac,
                isParentLocation: true
            }));
        } else {
            this.locationMacData = this.locationMacData.map(location => ({
                mac: location.mac,
                locationId: location.locationId, //location.locationId
                isParentLocation: false
            }));
        }
    }

    reActivate(id) {
        const url = `/reactivateService?custId=${id}`;
        let data = {};
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data) {
                        this.toastr.success(`${response.message}`, 'Re-activate Sucessfully!');


                        this.getcustomerList("");
                    } else {
                        error: (error) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!!!');
                        }


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

    cafremaingTime() {
        this.cafRemainTimeSubscription = this.obs$.subscribe(e => {
            this.customerListData?.data?.forEach(element => {
                if (element.status != "Active") {
                    if (element.currentStaff == null || element.currentStaff !== null) {
                        const newYearsDate: any = new Date(
                            element.nextfollowupdate + " " + element.nextfollowuptime
                        );

                        const currentDate: any = new Date();
                        if (newYearsDate > currentDate) {
                            const totalSeconds = (newYearsDate - currentDate) / 1000;
                            const minutes = Math.floor(totalSeconds / 60) % 60;
                            const hours = Math.floor(totalSeconds / 3600) % 24;
                            const days = Math.floor(totalSeconds / 3600 / 24);
                            const seconds = Math.floor(totalSeconds) % 60;
                            const remainTime =
                                ("0" + days).slice(-2) +
                                ":" +
                                ("0" + hours).slice(-2) +
                                ":" +
                                ("0" + minutes).slice(-2) +
                                ":" +
                                ("0" + seconds).slice(-2);

                            element.remainTime = remainTime;
                        } else {
                            element.remainTime = "00:00:00:00";
                        }
                    }
                }
            });
        });
    }

    getBillToData() {
        let url = "/commonList/billTo";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                if (this.custType == "Postpaid") {
                    this.billToData = response.dataList.filter(billto => billto.value != "ORGANIZATION");
                } else {
                    this.billToData = response.dataList;
                }
            },
            error => { }
        );
    }

    getFailedPayments() {
        this.viewcustomerFailedPaymentData = [];
        const url = "/onlinePayAudit/allByCustId?custId=" + this.customerId;
        this.customerManagementService.getMethodForIntegration(url).subscribe(
            (response: any) => {
                this.viewcustomerFailedPaymentData = response.onlineAuditData;
                if (this.viewcustomerFailedPaymentData.length !== 0) {
                    this.displayFailedPaymentDialog = true;
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'No Payment Found !!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    closeFailedPaymentForm() {
        this.displayFailedPaymentDialog = false;
    }

    getCustomerStatus() {
        const url = "/commonList/generic/custStatus";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.CustomerStatusValue = response.dataList.filter(
                    status =>
                        status.value !== "Active" &&
                        status.value !== "InActive" &&
                        // status.value !== "NewActivation" &&
                        status.value !== "Reject" &&
                        status.value !== "Suspend" &&
                        status.value !== "Terminate"
                );
            },
            (error: any) => { }
        );
    }
    //  exportCustomer() {
    //     import("xlsx").then(xlsxModule => {
    //         const xlsx = xlsxModule.default || xlsxModule;

    //         let z = this.customerListData.map((ele: any) => {
    //             let x = {};
    //             this.cols.forEach((d: any) => {
    //                 x = { ...x, [d.customExportHeader]: ele?.[d.field] };
    //             });
    //             return x;
    //         });

    //         const worksheet = xlsx.utils.json_to_sheet(z);
    //         const workbook = { Sheets: { data: worksheet }, SheetNames: ["data"] };
    //         const excelBuffer: any = xlsx.write(workbook, {
    //             bookType: "xlsx",
    //             type: "array"
    //         });
    //         this.saveAsExcelFile(excelBuffer, "Customer");
    //     });
    // }

    exportCustomerCAF() {
        import('xlsx').then(xlsxModule => {
            const xlsx = xlsxModule.default || xlsxModule;

            const list = Array.isArray(this.customerListData?.data)
                ? this.customerListData.data
                : Array.isArray(this.customerListData)
                    ? this.customerListData
                    : [];

            if (!list.length) {
                this.toastr.info('No customer data to export');
                return;
            }

            const z = list.map((ele: any) => {
                let x: any = {};
                this.cols.forEach((d: any) => {
                    x = { ...x, [d.customExportHeader]: ele?.[d.field] };
                });
                return x;
            });

            const worksheet = xlsx.utils.json_to_sheet(z);
            const workbook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
            const excelBuffer: any = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
            this.saveAsExcelFile(excelBuffer, 'Customer');
        });
    }


    saveAsExcelFile(buffer: any, fileName: string): void {
        let EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        let EXCEL_EXTENSION = ".xlsx";
        const data: Blob = new Blob([buffer], {
            type: EXCEL_TYPE
        });
        FileSaver.saveAs(data, fileName + "_Export_" + new Date().getTime() + EXCEL_EXTENSION);
    }
    keypressSession(event: any) {
        const pattern = /[0-9]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    // Changes For Shift Location

    newShiftgetCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            if (this.custData.serviceareaid) {
                this.newShiftisServiceInShiftLocation = true;
                this.newShiftshiftLocationDTO.updateAddressServiceAreaId = this.custData.serviceareaid;
                this.newShiftLocationPopId = this.custData.popid;
                this.newShiftLocationOltId = this.custData.oltid;
                this.newShiftgetPartnerAllByServiceArea(this.custData.serviceareaid);
                this.newShiftbranchByServiceAreaID(this.custData.serviceareaid);
                this.newShiftgetWalletData(custId);

                var customerAddress = this.custData.addressList.find((address: any) => address.version === "NEW");
                this.viewcustomerListData = customerAddress;

                if (customerAddress && customerAddress.addressType) {

                    const areaUrl = "/area/pincode?pincodeId=" + customerAddress.pincodeId;
                    this.savbillCommonBaseService.get(areaUrl).subscribe((areaRes: any) => {

                        this.newShiftAreaListDD = areaRes.areaList;

                        // 2. Now patch the form (using Number strictly for mat-select matching)
                        this.newShiftpresentGroupForm.patchValue(customerAddress);
                        this.newShiftpresentGroupForm.patchValue({
                            pincodeId: Number(customerAddress.pincodeId),
                            areaId: Number(customerAddress.areaId)
                        });

                        // 3. Continue fetching background details
                        this.newShiftselServiceAreaByParent(Number(this.custData.serviceareaid));
                        this.newShiftgetAreaData(customerAddress.areaId, "present");

                        let subAreaEvent = { value: customerAddress.subareaId };
                        this.newShiftonChangeSubArea(subAreaEvent, "present");
                    });
                }
                this.newShiftbranchID = this.custData.branch;
            }
            if (this.custData.partnerid) {
                this.newShiftshiftLocationDTO.shiftPartnerid = this.custData.partnerid;
            }
            this.newShiftshiftLocationDTO.isPermanentAddress = false;
            this.newShiftshiftLocationDTO.isPaymentAddresSame = false;

            if (this.viewcustomerListData && !this.viewcustomerListData.addressType) {
                this.newShiftpresentGroupForm.patchValue(this.viewcustomerListData);
            }

            this.newShiftstaffSelectList = [];
        });
    }

    // newShiftcustomerDetailOpen() {
    //     this.router.navigate(["/home/customer-caf/details/" + this.custType + "/x/" + this.customerId]);
    // }

    newShiftgetWalletData(custID) {
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
        this.revenueManagementService.postMethod(url, data).subscribe((response: any) => {
            this.newShiftwalletValue = response.customerWalletDetails;
            if (this.newShiftwalletValue >= 0) {
                this.newShiftdueValue = 0;
            } else {
                this.newShiftdueValue = Math.abs(this.newShiftwalletValue);
            }
        });
    }

    newShiftgetpartnerAll() {
        const url = "/partner/all";
        this.partnerService.getMethodNew(url).subscribe(
            (response: any) => {
                this.newShiftpartnerList = response.partnerlist.filter(item => item.id != 1);
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

    newShiftgetNewCustomerAddressForCustomer(): void {
        const url = "/newcustomeraddress/" + this.customerId;

        this.customerManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.newShiftCustomerAddressDataForCustometr = res.newcustomerAddress;
                if (this.newShiftCustomerAddressDataForCustometr?.length > 0) {
                    this.disableShiftButton = this.newShiftCustomerAddressDataForCustometr.some(
                        item => item.version === "IN_TRANSIT"
                    );
                }
            },
            (error: any) => { }
        );
    }

    newShiftopenShiftLocationForm() {
        this.matdialog.open(this.ShiftLocation, {
            width: '90%',
            disableClose: false
        });

        this.newShiftdisplayShiftLocationDetails = true;
        this.newShiftgetNetworkDevicesByType("OLT");
        this.newShiftLocationChargeGroupForm.reset();
    }

    newShiftgetNetworkDevicesByType(deviceType) {

        const url = "/NetworkDevice/getNetworkDevicesByDeviceType?deviceType=" + deviceType;
        this.networkdeviceService.getMethod(url).subscribe(
            (response: any) => {
                switch (deviceType) {
                    case "OLT":
                        this.newShiftoltDevices = response.dataList;
                        break;
                    case "Splitter":
                        this.newShiftspliterDevices = response.dataList;
                        break;
                    case "Master DB/DB":
                        this.newShiftmasterDbDevices = response.dataList;
                        break;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    newShiftStaffReasignListShiftLocation(data) {
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.id}&eventName=SHIFT_LOCATION`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.newShiftassignedShiftLocationid = data.id;
                this.newShiftapprovableStaff = [];
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'success!');


                }
                if (response.dataList != null) {
                    // this.getCustomer();
                    this.newShiftapprovableStaff = response.dataList;
                    this.newShiftapproved = true;
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

    newShiftreassignWorkflowShiftLocation() {
        let url: any;
        // this.remark = this.shiftlocationFormRemark.value.remark;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.newShiftassignedShiftLocationid}&eventName=SHIFT_LOCATION&assignToStaffId=${this.selectStaff}&remark=${this.newShiftlocationFormRemark.value.remark}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                $("#reAssignSHIFTLOCATIONModal").modal("hide");
                // this.getcustomerList("");
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    // this.getcustomerList("");
                    this.toastr.success(`${response.message}`, 'Assigned to the next staff successfully!');

                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    newShiftselServiceArea(event, isFromUI) {
        if (isFromUI) {
            this.newShiftpincodeDD = [];
        }
        const serviceAreaId = event.value;
        if (serviceAreaId) {
            const url = "/serviceArea/" + serviceAreaId;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    // this.serviceareaCheck = false;
                    let serviceAreaData = response.data;
                    if (isFromUI) {
                        serviceAreaData.pincodes.forEach(element => {

                            this.commondropdownService.allpincodeNumber.forEach(e => {
                                if (e.pincodeid == element) {
                                    this.newShiftpincodeDD.push(e);
                                }
                            });
                        });
                    }
                    if (!this.iscustomerEdit) {
                        if (isFromUI) {
                            this.newShiftpresentGroupForm.reset();
                        }
                    }
                },
                (error: any) => { }
            );
            this.newShiftgetPartnerAllByServiceArea(serviceAreaId);
            this.newShiftgetStaffUserByServiceArea(serviceAreaId);
            this.newShiftbranchByServiceAreaID(serviceAreaId);
            // this.getStaffDetailById(serviceAreaId);
            this.newShiftshiftLocationDTO.shiftPartnerid = "";
        }
    }

    newShiftgetPartnerAllByServiceArea(serviceAreaId) {
        const url = "/getPartnerByServiceAreaId/" + serviceAreaId;
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.newShiftpartnerListByServiceArea = response.partnerList.filter(item => item.id != 1);
                // console.log("partnerList", response);
            },
            (error: any) => { }
        );
    }

    newShiftgetStaffUserByServiceArea(ids) {
        let data = [];
        data.push(ids);
        let url = "/staffsByServiceAreaId/" + ids;
        this.serviceAreaService.getMethod(url).subscribe((response: any) => {
            //
            this.newShiftstaffList = response.dataList;
        });
    }

    newShiftbranchByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/branchManagement/getAllBranchesByServiceAreaId";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            this.newShiftbranchData = response.dataList;
            if (this.newShiftbranchData != null && this.newShiftbranchData.length > 0) {
                this.newShiftisBranchShiftLocation = true;
                if (this.custData.branch) {
                    this.newShiftbranchID = this.custData.branch;
                }
                // this.isBranchAvailable = true;
            } else {
                this.newShiftisBranchShiftLocation = false;
                // this.isBranchAvailable = false;
            }
        });
    }

    newShiftselectPINCODEChange(_event: any, index: any) {
        // const url = "/area/pincode?pincodeId=" + _event.value;
        // this.savbillCommonBaseService.get(url).subscribe(
        //     (response: any) => {
        //         this.newShiftAreaListDD = response.areaList;
        //     },
        //     (error: any) => {
        //         console.log(error);
        //     }
        // );
        if (_event.value) {
            const url = "/area/pincode?pincodeId=" + _event.value;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.newShiftAreaListDD = response.areaList;
                    if (_event.value) {
                        let url = "/pincode/getServicAreaIdByPincode?pincodeid=" + _event.value;
                        this.savbillCommonBaseService.get(url).subscribe(
                            (res: any) => {
                                if (res.data != null) {
                                    // this.getBranchByServiceAreaID(response.data);
                                    // this.getPlanbyServiceArea(response.data);
                                    if (!this.newShiftshiftLocationDTO.updateAddressServiceAreaId) {
                                        let serviceAreaId = {
                                            value: Number(res.data?.serviceAreaId)
                                        };
                                        this.newShiftselServiceArea(serviceAreaId, false);
                                        this.newShiftshiftLocationDTO.updateAddressServiceAreaId = res.data?.serviceAreaId;
                                    }
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                            }
                        );
                    }
                    if (index !== "initialLoad") {
                        this.newShiftpresentGroupForm.get("areaId")?.setValue('');
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
            let building_url =
                "/buildingmgmt/getBuildingMgmt?entityname=" +
                this.selectedMappingFrom +
                "&entityid=" +
                _event.value;
            this.savbillCommonBaseService.get(building_url).subscribe(
                (response: any) => {
                    if (response.dataList?.length > 0) {
                        this.newShiftbuildingListDD = response.dataList;
                    } else {
                        this.newShiftbuildingListDD = [];
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
        // this.getpincodeData(_event.value, index);
    }
    newShiftselectAreaChange(_event: any, index: any) {
        this.newShiftgetAreaData(_event.value, index);
    }

    newShiftgetAreaData(id: any, index: any) {
        // const url = "/area/" + id;

        // this.savbillCommonBaseService.get(url).subscribe((response: any) => {
        //     if (index === "present") {
        //         this.newShiftareaDetails = response.data;

        //         this.newShiftselectPincodeList = true;

        //         this.newShiftpresentGroupForm.patchValue({
        //             addressType: "Present",
        //             areaId: Number(this.areaDetails.id),
        //             pincodeId: Number(this.areaDetails.pincodeId),
        //             cityId: Number(this.areaDetails.cityId),
        //             stateId: Number(this.areaDetails.stateId),
        //             countryId: Number(this.areaDetails.countryId)
        //         });
        //     }
        // });
        if (id) {
            const url = "/area/" + id;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    if (response.data?.pincodeId) {
                        const pincodeUrl =
                            "/pincode/getServicAreaIdByPincode?pincodeid=" + response.data?.pincodeId;
                        this.savbillCommonBaseService.get(pincodeUrl).subscribe(
                            (res: any) => {
                                if (res.data) {
                                    if (!this.newShiftshiftLocationDTO.updateAddressServiceAreaId) {
                                        // this.getBranchByServiceAreaID(res.data);
                                        // this.getPlanbyServiceArea(res.data);
                                        let serviceAreaId = {
                                            value: Number(res.data?.serviceAreaId)
                                        };
                                        this.newShiftselServiceArea(serviceAreaId, false);
                                        this.newShiftshiftLocationDTO.updateAddressServiceAreaId = res.data?.serviceAreaId;
                                    }
                                }
                                if (index === "present") {
                                    this.newShiftareaDetails = response.data;

                                    this.newShiftselectPincodeList = true;

                                    this.newShiftpresentGroupForm.patchValue({
                                        addressType: "Present",
                                        areaId: Number(id),
                                        pincodeId: Number(this.newShiftareaDetails.pincodeId),
                                        cityId: Number(this.newShiftareaDetails.cityId),
                                        stateId: Number(this.newShiftareaDetails.stateId),
                                        countryId: Number(this.newShiftareaDetails.countryId)
                                    });
                                }
                                // if (index === "payment") {
                                //     this.paymentareaDetails = response.data;

                                //     this.selectPincodeListPayment = true;

                                //     this.paymentGroupForm.patchValue({
                                //         addressType: "Payment",
                                //         pincodeId: Number(this.paymentareaDetails.pincodeId),
                                //         cityId: Number(this.paymentareaDetails.cityId),
                                //         stateId: Number(this.paymentareaDetails.stateId),
                                //         countryId: Number(this.paymentareaDetails.countryId)
                                //     });
                                // }
                                // if (index === "permanent") {
                                //     this.permanentareaDetails = response.data;

                                //     this.selectPincodeListPermanent = true;
                                //     this.permanentGroupForm.patchValue({
                                //         addressType: "Permanent",
                                //         pincodeId: Number(this.permanentareaDetails.pincodeId),
                                //         cityId: Number(this.permanentareaDetails.cityId),
                                //         stateId: Number(this.permanentareaDetails.stateId),
                                //         countryId: Number(this.permanentareaDetails.countryId)
                                //     });
                                // }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                        let idData = this.selectedMappingFrom === "Pin Code" ? response.data?.pincodeId : id;
                        let building_url =
                            "/buildingmgmt/getBuildingMgmt?entityname=" +
                            this.selectedMappingFrom +
                            "&entityid=" +
                            idData;
                        this.savbillCommonBaseService.get(building_url).subscribe(
                            (response: any) => {
                                if (response.dataList?.length > 0) {
                                    this.newShiftbuildingListDD = response.dataList;
                                } else {
                                    this.newShiftbuildingListDD = [];
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                            }
                        );
                    }
                },
                (error: any) => {
                    console.log(error, "error");

                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
            const subAreaurl = "/subarea/getSubAreaFromArea?areaId=" + id;
            this.savbillCommonBaseService.get(subAreaurl).subscribe(
                (subarea: any) => {
                    // this.newShiftsubAreaListDD = subarea.dataList;
                    if (subarea.dataList) {
                        // Map the response to add '(UnderDeveloped)' for relevant items
                        this.newShiftsubAreaListDD = subarea.dataList.map((item: any) => ({
                            id: item.id,
                            name: item.name,
                            isUnderDevelopment: item.status === "UnderDevelopment"
                        }));
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    newShiftmodalOpenStaff(type) {
        // this.matdialog.open(this.selectStaffDialog, {
        //     width: '80%',
        //     maxHeight: '90vh',
        //     autoFocus: false,
        // });

        this.newShiftstaffSelectType = type;
        this.newShiftisSelectStaff = true;
        this.newShiftselectedStaff = [];
    }

    newShiftselectedStaffChange(selectedStaff) {
        this.newShiftstaffCustList.push({
            id: Number(selectedStaff.id),
            name: selectedStaff.firstname
        });
        this.newShiftisSelectStaff = false;
        if (this.newShiftstaffSelectType == "paymentCharge") {
            this.newShiftpaymentOwnerId = Number(selectedStaff.id);
            this.newShiftLocationChargeGroupForm.patchValue({
                paymentOwnerId: Number(selectedStaff.id)
            });
        } else if (this.newShiftstaffSelectType == "requestedBy")
            this.newShiftrequestedByID = Number(selectedStaff.id);
        this.newShiftstaffSelectType = "";
    }

    newShiftcloseStaff() {
        this.newShiftisSelectStaff = false;
        this.newShiftstaffSelectType = "";
    }

    newShiftremoveSelStaff(type) {
        if (type == "paymentCharge") {
            this.newShiftpaymentOwnerId = 0;
            this.newShiftLocationChargeGroupForm.patchValue({
                paymentOwnerId: ""
            });
        } else if (type == "requestedBy") this.newShiftrequestedByID = 0;
        this.newShiftstaffid = null;
    }

    newShiftmodalOpenParentCustomer(type) {
        this.newShiftparentCustomerDialogType = type;
        this.newShiftshowParentCustomerModel = true;
        this.newShiftcustomerSelectType = "Billable To";
        if (type === "parent") {
            this.newShiftcustomerSelectType = "Parent";
        }
        this.newShiftselectedParentCust = [];
    }

    async newShiftselectedCustChange(event) {
        this.newShiftshowParentCustomerModel = false;
        this.newShiftselectedParentCust = event;
        if (this.newShiftparentCustomerDialogType === "billable-shift-location") {
            this.newShiftbillableCustList = [
                {
                    id: this.newShiftselectedParentCust.id,
                    name: this.newShiftselectedParentCust.name
                }
            ];
            this.newShiftLocationChargeGroupForm.patchValue({
                billableCustomerId: this.newShiftselectedParentCust.id
            });
        }
    }

    newShiftcloseParentCust() {
        this.newShiftshowParentCustomerModel = false;
    }

    newShiftcloseParentCustt() {
        this.newShiftifModelIsShow = false;
    }

    newShiftremoveSelParentCust(type) {
        this.newShiftselectedParentCust = [];
        this.newShiftbillableCustList = [];
        this.newShiftLocationChargeGroupForm.patchValue({
            billableCustomerId: null
        });
        this.newShiftisBranchAvailable = false;
    }

    newShiftselectcharge(_event: any, type) {
        const chargeId = _event.value;
        let viewChargeData;
        let date;

        date = this.newShiftcurrentDate.toISOString();
        const format = "yyyy-MM-dd";
        const locale = "en-US";
        const myDate = date;
        const formattedDate = formatDate(myDate, format, locale);
        const url = "/charge/" + chargeId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            viewChargeData = response.chargebyid;
            this.newShiftselectchargeValueShow = true;
            this.newShiftLocationChargeGroupForm.patchValue({
                actualprice: Number(viewChargeData.actualprice),
                charge_date: formattedDate,
                type: "One-time"
            });
        });
    }

    newShiftselectTypecharge(e) {
        // this.chargeGroupForm.get("connection_no").reset();
        // this.chargeGroupForm.get("planid").reset();
        // this.chargeGroupForm.get("expiry").reset();
        // if (e.value == "Recurring") {
        //   // this.chargeGroupForm.get("billingCycle").setValidators([Validators.required]);
        //   // this.chargeGroupForm.get("billingCycle").updateValueAndValidity();
        // } else {
        //   this.chargeGroupForm.value.billingCycle = 0;
        //   // this.chargeGroupForm.get("billingCycle").clearValidators();
        //   // this.chargeGroupForm.get("billingCycle").updateValueAndValidity();
        // }
    }

    newShiftsaveShiftLocation() {
        this.matdialog.closeAll()
        this.newShiftsubmitted = true;
        this.newShiftifUpdateAddressSubmited = true;
        if (
            (this.newShiftshiftLocationDTO.shiftPartnerid === "" &&
                this.newShiftisBranchShiftLocation == false) ||
            (this.newShiftbranchID == 0 && this.newShiftisBranchShiftLocation) ||
            this.newShiftLocationChargeGroupForm.value.price <
            this.newShiftLocationChargeGroupForm.value.actualprice ||
            this.newShiftrequestedByID == 0 ||
            this.newShiftpresentGroupForm.invalid
        ) {
            return this;
        }

        if (this.newShiftLocationChargeGroupForm.valid) {
            if (this.newShiftLocationChargeGroupForm.value.type == "Recurring") {
                this.newShiftLocationChargeGroupForm.value.billingCycle = 1;
            }
            this.newShiftshiftLocationDTO.addressDetails = this.newShiftpresentGroupForm.getRawValue();
            this.newShiftshiftLocationDTO.custChargeOverrideDTO = {
                billableCustomerId: this.newShiftLocationChargeGroupForm.value.billableCustomerId,
                custChargeDetailsPojoList: [this.newShiftLocationChargeGroupForm.value],
                custid: this.customerId,
                paymentOwnerId: this.newShiftLocationChargeGroupForm.value.paymentOwnerId
            };
            this.newShiftshiftLocationDTO.popid = this.newShiftLocationPopId;
            this.newShiftshiftLocationDTO.oltid = this.newShiftLocationOltId;
            this.newShiftshiftLocationDTO.requestedById = this.newShiftrequestedByID;
            this.newShiftshiftLocationDTO.branchID = this.newShiftbranchID;
            if (this.newShiftshiftLocationDTO.shiftPartnerid === "") {
                this.newShiftshiftLocationDTO.shiftPartnerid = 1;
            }
            if (this.newShiftshiftLocationDTO.branchID == 0 || !this.newShiftisBranchShiftLocation) {
                this.newShiftshiftLocationDTO.branchID = null;
            }
            if (this.newShiftshiftLocationDTO.popid == 0) {
                this.newShiftshiftLocationDTO.popid = null;
            }

            // const url = "/balanceAndCommissionInfoForShiftLocation/" + this.customerId;
            // this.revenueManagementService.getMethod(url).subscribe(
            //     (response: any) => {
            // console.log("response ::::::::: ", response);
            this.newShiftshiftLocationDTO.isInvoiceCleared = true;
            this.newShiftshiftLocationDTO.transferableCommission = 0;
            this.newShiftshiftLocationDTO.transferableBalance = 0;
            const url = "/shiftCustomerLocation/" + this.customerId;
            this.commondropdownService.postMethod(url, this.newShiftshiftLocationDTO).subscribe(
                (response: any) => {
                    $("#openAddressForm").modal("hide");
                    this.toastr.success(`${response.message}`, 'hift customer location successfully!');


                    this.newShiftgetCustomersDetail(this.customerId);
                    this.newShiftgetNewCustomerAddressForCustomer();
                    this.newShiftcloseShiftLocation();
                },
                (error: any) => {
                    if (error.error.status == 417) {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                }
            );
        }
        // this.closeShiftLocation();
    }
    afterClosed() {
        throw new Error("Method not implemented.");
    }

    newShiftcloseShiftLocation() {
        this.matdialog.closeAll()
        this.newShiftsubmitted = false;
        this.newShiftifUpdateAddressSubmited = false;
        this.newShiftLocationChargeGroupForm.reset();
        this.newShiftifUpdateAddressSubmited = false;
        this.newShiftrequestedByID = 0;
        this.newShiftbranchID = 0;
        this.newShiftdisplayShiftLocationDetails = false;
    }

    newShiftpickModalOpen(data) {
        let name;
        let entityID;
        name = "SHIFT_LOCATION";
        entityID = data.id;
        let url = "/workflow/pickupworkflow?eventName=" + name + "&entityId=" + entityID;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.openCustomerAddress();
                this.newShiftgetNewCustomerAddressForCustomer();

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'success!');

                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    newShiftshiftLocationRejected(data) {
        this.matdialog.open(this.rejectDialog, {
            width: '60%',
            disableClose: true


        });

        this.newShiftapproveId = data.id;
        this.newShiftrejectApproveShiftLocationModal = true;
        this.newShiftassignShiftLocationData = data;
        this.newShiftshiftLocationFlagType = "Rejected";
        this.newShiftAppRjecHeader = "Reject";
        this.newShiftassignAppRejectShiftLocationForm.reset();
    }

    rejectPlan(dialogRef) {
        this.matdialog.closeAll();
    }
    newShiftshiftLocationApproved(data) {
        this.matdialog.open(this.approverDialog, {
            width: '60%',
            disableClose: true,
        });
        this.newShiftapproveId = data.id;
        this.newShiftrejectApproveShiftLocationModal = true;
        this.newShiftassignShiftLocationData = data;
        this.newShiftshiftLocationFlagType = "approved";
        this.newShiftAppRjecHeader = "Apporve ";
        this.newShiftassignAppRejectShiftLocationForm.reset();
    }

    newShiftcloseDisplayShiftLocationDetails() {
        this.newShiftrejectApproveShiftLocationModal = false;
    }

    newShiftassignShiftLocation1: boolean = false;
    newShiftassignAddressApprove() {
        this.matdialog.closeAll();
        this.newShiftassignShiftLocationsubmitted = true;
        if (this.newShiftassignAppRejectShiftLocationForm.valid) {
            let url = "/approveCustomerAddress";

            let assignCAFData = {
                addressId: this.newShiftassignShiftLocationData.id,
                flag: this.newShiftshiftLocationFlagType,
                nextStaffId: 0,
                remark: this.newShiftassignAppRejectShiftLocationForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };

            this.customerManagementService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    this.newShiftrejectApproveShiftLocationModal = false;
                    this.newShiftapproveInventoryData = null;
                    this.newShiftrejectInventoryData = null;
                    if (response.result.dataList) {
                        if (this.newShiftshiftLocationFlagType == "approved") {
                            this.newShiftapproved = true;
                            this.newShiftapproveInventoryData = response.result.dataList;
                            this.newShiftassignShiftLocation1 = true;
                            //   $("#assignCustomerInventoryModal").modal("show");
                        } else {
                            this.newShiftreject = true;
                            this.newShiftrejectInventoryData = response.result.dataList;
                            this.newShiftrejectCustomerInventoryModal = true;
                        }
                    } else {
                        this.newShiftgetNewCustomerAddressForCustomer();
                    }
                    this.newShiftassignAppRejectShiftLocationForm.reset();
                    this.newShiftassignShiftLocationsubmitted = false;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    newShiftassignToStaff(flag) {
        let url: any;
        let name: string;
        name = "SHIFT_LOCATION";
        if (!this.newShiftselectStaff && !this.newShiftselectStaffReject) {
            url = `/teamHierarchy/assignEveryStaff?entityId=${this.newShiftapproveId}&eventName=${name}&isApproveRequest=${flag}`;
        } else {
            if (flag) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.newShiftapproveId}&eventName=${name}&nextAssignStaff=${this.newShiftselectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.newShiftapproveId}&eventName=${name}&nextAssignStaff=${this.newShiftselectStaffReject}&isApproveRequest=${flag}`;
            }
        }

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (flag) {
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    } else {
                        this.toastr.success(`${response.message}`, 'Approved Successfully!');


                    }
                } else {
                    this.toastr.success(`${response.message}`, 'Rejected  Successfully!');



                }
                // $("#assignCustomerInventoryModal").modal("hide");
                this.newShiftassignShiftLocation1 = false;
                this.newShiftrejectCustomerInventoryModal = false;
                this.newShiftgetNewCustomerAddressForCustomer();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    newShiftsearchPrepaidValue() {
        this.newShiftprepaid = "";
        this.newShiftprepaidValue = 0;
        const now = new Date();
        let firstDay;
        let lastDay;
        firstDay = this.datePipe.transform(now, "yyyy-MM-dd");
        lastDay = this.datePipe.transform(new Date(now.setDate(now.getDate() + 1)), "yyyy-MM-dd");
        const url =
            "/getCustomer?custid=" + this.customerId + "&startdate=" + firstDay + "&endate=" + firstDay;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                response.customerDBRPojos.forEach(dbr => {
                    var DBRDate = moment(dbr.month, "DD/MM/YYYY").toDate();
                    var today = moment(new Date(), "DD/MM/YYYY").toDate();
                    if (moment(DBRDate.setHours(0, 0, 0, 0)).isSame(moment(today.setHours(0, 0, 0, 0)))) {
                        this.newShiftprepaidValue = this.newShiftprepaidValue + dbr.pendingamt;
                    }
                });
                this.newShiftprepaid = this.newShiftprepaidValue.toFixed(2);
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }
    newShiftshiftWorkflow(data) {

        this.newShiftifModelIsShow = true;
        this.PaymentamountService.show("custauditWorkflowModal");
        // this.matdialog.open(WorkflowAuditDetailsModalComponent, {
        //     width: '80%', disableClose: true,
        //     data: {
        //         auditcustid: this.auditcustid,
        //         checkHierachy: "SHIFT_LOCATION",
        //         planId: ""
        //     }
        // });
        this.newShiftauditcustid.next({
            auditcustid: data.id,
            checkHierachy: "SHIFT_LOCATION",
            planId: ""
        });
    }
    newShiftreassignWorkflow() {
        this.newShiftassignDocSubmitted = false;
        this.newShiftremark = this.newShiftassignDocForm.value.remark;
        let url: any;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.newShiftassignedShiftLocationid}&eventName=SHIFT_LOCATION&assignToStaffId=${this.newShiftselectStaff}&remark=${this.newShiftremark}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                $("#reAssignPLANModal").modal("hide");
                //  this.getAll();
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.toastr.success(`${response.message}`, 'Assigned to the next staff successfully!');


                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    newShiftcloseStaffModel(arg0: boolean) {
        this.newShiftassignShiftLocation1 = false;
    }

    getDemographicLabel(currentName: string): string {
        if (!this.demographicLabel || this.demographicLabel.length === 0) {
            return currentName;
        }

        const label = this.demographicLabel.find(item => item.currentName === currentName);
        return label ? label.newName : currentName;
    }

    openPaymentGateways(custId) {
        const url = "/generatePaymentLink/" + custId;
        this.customerManagementService.postMethod(url, null).subscribe(
            (response: any) => {
                let payData = response.data;
                if (response.data == null) {
                    this.toastr.info(`${response.responseMessage}`, 'No Unpaid Invoice Found for this Customer!');

                } else {
                    let isRenew = false;
                    window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
                    //   this.router.navigate(["/customer/payMethod/" + payData]);
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    addToWallet(orderId) {
        this.transModal = true;
        this.addToWalletOrderId = orderId;
    }

    paymentData: any;
    retryPayment(orderId) {
        this.paymentData = [];
        const url = "/ByOrderId?orderId=" + orderId;
        this.customerManagementService.getMethodForIntegration(url).subscribe(
            (response: any) => {
                // this.paymentData = response.onlineAuditData;
                this.getFailedPayments();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    searchStaffByName(searchText) {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage == 1) {
            this.itemsPerPage = this.pageITEM;
        } else {
            this.itemsPerPage = this.showItemPerPage;
        }

        const filterValue = this.searchDeatil.trim().toLowerCase();
        this.staffData.filter = filterValue;
        this.staffData.filter = this.searchDeatil.trim().toLowerCase();
        const trimmedSearchText = searchText.trim().replace(/\s+/g, " ");
        this.searchStaffDeatil = searchText;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
        this.staffData.filter = this.searchDeatil.trim().toLowerCase();
        const normalizedSearchText = trimmedSearchText.toLowerCase();

        if (normalizedSearchText) {
            this.approveCAFData = this.approveCAF.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(normalizedSearchText) ||
                    staff.username.toLowerCase().includes(normalizedSearchText)
            );
        } else {
            this.approveCAFData = this.approveCAF;
        }
        this.applyPagination(this.approveCAFData)
    }

    clearSearchForm() {
        this.searchOption = "";
        this.searchStaffDeatil = "";
        this.approveCAFData = this.approveCAF;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
        this.searchDeatil = '';
        this.staffData.filter = '';
        this.applyPagination(this.approveCAFData)
    }

    openFailureReason(data) {
        this.failureReason = data;
        this.failureReasonDialog = true;
    }
    closeFailureReason() {
        this.failureReasonDialog = false;
        this.failureReason = "";
    }
    async saveSelstaff(dialogRef) {
        this.staffList = [
            {
                id: Number(this.selectedStaffCust.id),
                name: this.selectedStaffCust.firstname
            }
        ];
        this.staffid = Number(this.selectedStaffCust.id);
        this.batchStaffid = Number(this.selectedStaffCust.id);
        dialogRef.close()
        this.modalCloseStaff();
    }
    modalCloseStaff() {
        throw new Error("Method not implemented.");
    }

    applyPagination(fullData: any[]) {
        this.totalStaffCount = fullData.length;
        const startIndex = this.currentPageStaffListIndex * this.approveStaffListdataitemsPerPageForStaff;
        const endIndex = startIndex + this.approveStaffListdataitemsPerPageForStaff;
        this.pagedStaffDataSource.data = fullData.slice(startIndex, endIndex);
    }


    paginateStaff(event: any) {
        this.approveStaffListdataitemsPerPageForStaff = event.pageSize;
        this.currentPageStaffListIndex = event.pageIndex;
        if (this.searchParentCustValue) {
            this.applyPagination(this.currentStaffData);
        } else {
            this.applyPagination(this.approveCAFData);
        }
    }

    getStaff() {
        throw new Error("Method not implemented.");
    }

    onCredentialMatchChange(event: any) {
        const isChecked = event.checked;
        this.isCredentialMatch = isChecked;

        this.isCredentialMatchWithAccountNumber(isChecked);
    }

    isCredentialMatchWithAccountNumber = (isChecked: boolean) => {
        if (isChecked) {

            this.customerGroupForm.get("username")?.setValue(null);
            this.customerGroupForm.get("password")?.setValue(null);

            this.customerGroupForm.get("username")?.disable();
            this.customerGroupForm.get("password")?.disable();

            this.customerGroupForm.get("username")?.clearValidators();
            this.customerGroupForm.get("password")?.clearValidators();

            this.customerGroupForm.get("username")?.updateValueAndValidity();
            this.customerGroupForm.get("password")?.updateValueAndValidity();
        } else {
            this.customerGroupForm.get("username")?.enable();
            this.customerGroupForm.get("password")?.enable();

            this.customerGroupForm.get("username")?.setValidators([Validators.required]);
            this.customerGroupForm.get("password")?.setValidators([Validators.required]);

            this.customerGroupForm.get("username")?.updateValueAndValidity();
            this.customerGroupForm.get("password")?.updateValueAndValidity();
        }
    };

    addToWalletAPI() {
        const url =
            "/addToWalletByOrderId?orderId=" +
            this.addToWalletOrderId +
            "&transactionId=" +
            this.transactionNo;
        this.recordPaymentService.postMethodForIntegration(url, null).subscribe(
            (response: any) => {
                if (response?.responseCode === 500) {
                    this.toastr.error(`${response?.data}`, 'Failed!');


                    return;
                }
                if ([405, 406, 417, 415].includes(response?.responseCode)) {
                    this.toastr.info(`${response?.data}`, 'Info!');

                    return;
                }
                this.customerData = response.customerList;
                this.toastr.success(`${response?.data}`, 'Success!');


                this.transModal = false;
                this.addToWalletOrderId = "";
                this.transactionNo = "";
                this.getFailedPayments();
            },
            (error: any) => {

                console.error("Error:", error);
                this.toastr.error(`${error?.error?.ERROR}`, 'Failed!');


            }
        );
    }

    ConfirmonTransactionNumber() {
        if (this.addToWalletOrderId) {
            this.confirmationService.confirm({
                message: "Do you want to confirm this transaction no?",
                header: "Transaction No Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.addToWalletAPI();
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    transactionModal() {
        this.transModal = false;
        this.addToWalletOrderId = "";
        this.transactionNo = "";
    }

    paymentModeData() {
        const url = "/commonList/paymentMode";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.paymentMode = response.dataList;
            },
            (error: any) => { }
        );
    }
    // openModal(custId) {

    //     this.dialog = true;
    //     this.customerid = custId;

    // }
    openModal(custId) {
        this.dialog = false;
        this.customerid = custId;

        setTimeout(() => {
            this.dialog = true;
        }, 0);
    }


    close() {
        this.customerid = null;
        this.dialog = false;
    }

    viewFileImages(files) {
        this.fileImages(files[0].customerCafImageMappingId, files[0].uniqueName);
    }

    fileImages(customerCafImageMappingId: any, uniqueName: string) {
        this.PreviewImagesDialog = true;
        this.matdialog.open(this.previewImagesDialog, {
            width: '80%',
        });

        // Build mappingId properly
        const mappingId =
            customerCafImageMappingId?.customerCafImageMappingId +
            "/" +
            customerCafImageMappingId?.uniqueName;

        this.customerManagementService.getMethodImagesFile(uniqueName, mappingId).subscribe({
            next: (data: any) => {
                if (data.status === 200) {
                    const fileName = uniqueName;
                    const fileType = fileName.split(".");
                    let type = "application/octet-stream"; // default type

                    // Convert response body → Uint8Array
                    const uint = new Uint8Array(data.body);
                    const magic = uint.subarray(0, 4);

                    // Detect file type by magic numbers
                    if (magic.every(b => b === 0xff)) {
                        type = "image/jpeg";
                    } else if (
                        magic[0] === 0x89 &&
                        magic[1] === 0x50 &&
                        magic[2] === 0x4e &&
                        magic[3] === 0x47
                    ) {
                        type = "image/png";
                    } else if (
                        magic[0] === 0x47 &&
                        magic[1] === 0x49 &&
                        magic[2] === 0x46 &&
                        magic[3] === 0x38
                    ) {
                        type = "image/gif";
                    } else if (
                        magic[0] === 0x25 &&
                        magic[1] === 0x50 &&
                        magic[2] === 0x44 &&
                        magic[3] === 0x46
                    ) {
                        type = "application/pdf";
                    }

                    // PDF Preview → open in new tab
                    if (fileType[fileType.length - 1] === "pdf") {
                        const blob = new Blob([data.body], { type: "application/pdf" });
                        const blobUrl = URL.createObjectURL(blob);
                        window.open(blobUrl, "_blank");
                    } else {
                        // Images (jpeg/png/gif)
                        const blob = new Blob([data.body], { type });
                        const blobUrl = URL.createObjectURL(blob);

                        this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                        this.documentPreview = true;

                        // Also push into array (if you want multiple previews in dialog)
                        this.imagesArray.push(this.previewUrl);
                    }
                } else if (data.status === 404) {
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'File Not Found!');
                    }


                } else {
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');
                    }


                }
            },
            error: (err: any) => {
                console.error("Error loading file:", err);
                this.toastr.error(`${err.error?.ERROR}`, 'Unable to load file!');

            }
        });
    }

    closeImagesDialog() {
        this.PreviewImagesDialog = false;
    }

    openPreview(files) {
        this.dialogDoc = true;
        this.matdialog.open(this.documentPreviewDialog, {
            width: '80%',
            maxHeight: '90vh',
            autoFocus: false,
        });
    }

    closepreviewDialog() {
        this.dialogDoc = false;
    }

    searchReassignStaffByName(searchText: string) {
        const trimmedSearchText = searchText.trim().replace(/\s+/g, " ");
        this.searchStaffDeatil = searchText;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
        const normalizedSearchText = trimmedSearchText.toLowerCase();

        if (trimmedSearchText) {
            this.approveCAF = this.reassigndata.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(normalizedSearchText) ||
                    staff.username.toLowerCase().includes(normalizedSearchText)
            );
        } else {
            this.approveCAF = this.reassigndata;
        }
    }

    clearReassignSearchForm() {
        this.searchReassignStaffDeatil = "";
        this.approveCAF = this.reassigndata;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
        this.NewStaffReasignList();
    }

    openMyCallDetails(id) {
        this.listView = false;
        this.isViewTicketMenu = false
        this.createView = false;
        this.selectAreaList = false;
        this.selectPincodeList = false;
        this.isCustomerDetailOpen = false;
        this.isCustomerLedgerOpen = false;
        this.customerPlanView = false;
        this.viewCustomerPaymentList = false;
        this.isCustomerDetailSubMenu = true;
        this.customerChangePlan = false;
        this.ifMyInvoice = false;
        this.isServiceOpen = false;
        this.ifShowDBRReport = false;
        this.ifChargeGetData = false;
        this.customerStatusView = false;
        this.ipManagementView = false;
        this.macManagementView = false;
        this.customerCafNotes = false;
        this.customerUpdateDiscount = false;
        this.ifWalletMenu = false;
        this.ifUpdateAddress = false;
        this.ifCafFollowUp = false;
        this.assignInventoryCustomerId = id;
        this.assignInventoryWithSerial = false;
        this.shiftLocationEvent = false;
        this.isCallDetails = true;
    }


    newShiftonChangeSubArea(_event: any, index: any) {
        if (_event.value) {
            const subAreaurl = "/subarea/getAreaIdFromSubAreaId?subAreaId=" + _event.value;
            this.savbillCommonBaseService.get(subAreaurl).subscribe(
                (subarea: any) => {
                    if (subarea.data) {
                        const url = "/area/" + subarea.data;
                        this.savbillCommonBaseService.get(url).subscribe(
                            (response: any) => {
                                if (response.data?.pincodeId) {
                                    const pincodeUrl =
                                        "/pincode/getServicAreaIdByPincode?pincodeid=" + response.data?.pincodeId;
                                    this.savbillCommonBaseService.get(pincodeUrl).subscribe(
                                        (res: any) => {
                                            if (res?.data) {
                                                if (!this.newShiftshiftLocationDTO.serviceareaid) {
                                                    let serviceAreaId = {
                                                        value: Number(res.data?.serviceAreaId)
                                                    };
                                                    this.newShiftshiftLocationDTO.serviceareaid = res.data?.serviceAreaId;
                                                    this.newShiftselServiceArea(serviceAreaId, false);
                                                }
                                            }
                                            if (index === "present") {
                                                this.newShiftareaDetails = response.data;

                                                this.newShiftselectPincodeList = true;

                                                this.newShiftpresentGroupForm.patchValue({
                                                    addressType: "Present",
                                                    areaId: Number(this.newShiftareaDetails.id),
                                                    pincodeId: Number(this.newShiftareaDetails.pincodeId),
                                                    cityId: Number(this.newShiftareaDetails.cityId),
                                                    stateId: Number(this.newShiftareaDetails.stateId),
                                                    countryId: Number(this.newShiftareaDetails.countryId)
                                                });
                                            }
                                        },
                                        (error: any) => {
                                            console.log(error, "error");
                                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                                        }
                                    );
                                    let idData;
                                    if (this.selectedMappingFrom === "Pin Code") {
                                        idData = response.data?.pincodeId;
                                    } else if (this.selectedMappingFrom === "Area") {
                                        idData = subarea?.data;
                                    } else {
                                        idData = _event?.value;
                                    }
                                    let building_url =
                                        "/buildingmgmt/getBuildingMgmt?entityname=" +
                                        this.selectedMappingFrom +
                                        "&entityid=" +
                                        idData;
                                    this.savbillCommonBaseService.get(building_url).subscribe(
                                        (response: any) => {
                                            if (response.dataList?.length > 0) {
                                                this.newShiftbuildingListDD = response.dataList;
                                                // if (this.iscustomerEdit) {
                                                let buildingEvent = {
                                                    value: Number(this.viewcustomerListData.building_mgmt_id)
                                                };
                                                this.newShiftonChangeBuildingArea(buildingEvent, "");
                                                // }
                                            } else {
                                                this.newShiftbuildingListDD = [];
                                            }
                                        },
                                        (error: any) => {
                                            console.log(error, "error");
                                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                                        }
                                    );
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    console.log(error, "error");

                }
            );
        }
    }

    newShiftonChangeBuildingArea(_event: any, index: any) {
        if (_event.value) {
            this.newShiftbuildingNoDD = [];
            const url = "/buildingmgmt/getBuildingMgmtNumbers?buildingMgmtId=" + _event.value;
            this.areaManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.newShiftbuildingNoDD = response.dataList.map(buildingNumber => ({ buildingNumber }));
                    // if (this.iscustomerEdit) {
                    this.newShiftpresentGroupForm.patchValue({
                        buildingNumber: this.viewcustomerListData.buildingNumber
                    });
                    // }
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
    }

    newShiftselServiceAreaByParent(id) {
        const serviceAreaId = id;
        this.newShiftpincodeDD = [];
        if (serviceAreaId) {
            const url = "/serviceArea/" + serviceAreaId;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    let serviceAreaData = response.data;
                    serviceAreaData.pincodes.forEach(element => {
                        this.commondropdownService.allpincodeNumber.forEach(e => {
                            if (e.pincodeid == element) {
                                this.newShiftpincodeDD.push(e);
                            }
                        });
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    searchStaffByNameReject(searchText: string) {
        const trimmedSearchText = searchText.trim().replace(/\s+/g, " ");
        this.searchStaffDeatil = searchText;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
        const normalizedSearchText = trimmedSearchText.toLowerCase();

        if (trimmedSearchText) {
            this.rejectCAF = this.rejectCafData.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(normalizedSearchText) ||
                    staff.username.toLowerCase().includes(normalizedSearchText)
            );
        } else {
            this.rejectCAF = this.rejectCafData;
        }
    }

    clearSearchFormReject() {
        this.searchStaffDeatil = "";
        this.rejectCAF = this.rejectCafData;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
    }

    closeApproveCustomer() {
        this.closeApprove.emit();
        this.selectStaff = null;
        this.matdialog.closeAll();
        this.assignCustomerCAFModal = false;
    }

    closeRejectCustomer() {
        this.matdialog.closeAll();
        this.closeReject.emit();
        this.rejectCustomerCAFModal = false;
    }

    closeReassignCustomer() {
        this.reAssignCustomerCAFModal = false;
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
                this.TrailPlanList = response.dataList;

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

    pageCustTrailPlanListData(event) {
        this.custShowTrailPlanShow = Number(event.pageSize);
        if (this.currentTrailPlanListdata > 1) {
            this.currentTrailPlanListdata = 1;
        }
        this.currentTrailPlanListdata = event.pageIndex + 1;
        this.getTrailPlanList(this.customerDetailData.id, this.custShowTrailPlanShow);
    }

    TotalTrailPlanItemPerPage(event) {
        this.custShowTrailPlanShow = Number(event.value);
        if (this.currentTrailPlanListdata > 1) {
            this.currentTrailPlanListdata = 1;
        }
        this.getTrailPlanList(this.customerDetailData.id, this.custShowTrailPlanShow);
    }

    buyKbzInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            isAdvancePayment: true,
            //   isBuyPlan: true,
            merchantName: "KBZPAY",
            customerUserName: this.customerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber:
                this.customerDetailData.countryCode.replace("+", "") +
                (this.customerDetailData.mobile ?? ""),
            invoiceId: invoice.id,
            partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerDetailData.buId
        };
        this.customerdetailsilsService.buyPlanUsingKbz(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                this.paymentConfirmationModal = false;
                this.isMpinFormSubmitted = false;
                this.mobileError = false;
                this.inputMobile = "";
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.exitBuy = false;
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                } else if (response.responseCode === 200 && response.data) {
                    const paymentLink = response.data;
                    this.toastr.info(`${response.responseMessage}`, 'Please open the payment link on your mobile device using the KBZPay app!');


                    //   const kbzurl = paymentLink.split("?kbzurl=")[1];
                    //   this.router.navigate(["/kbz-pay"], {
                    //     queryParams: { kbzurl: kbzurl }
                    //   });
                    //   window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Unexpected response received');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }

    buyTransacteasePayment(invoice) {
        const newTab = window.open("", "_blank");
        // this.getCustomerAddressDetails(this.customerDetailData.id);
        this.spinner.show();
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            //   amount: (this.amountsData + (this.amountsData * this.commissionPer) / 100).toString(),
            //   commission: (invoice.totalamount * this.commissionPer) / 100,
            billAddressLine1: this.customerAddressDetails?.landmark,
            billAddressLine2: this.customerAddressDetails?.landmark,
            billToAddressCity: this.customerAddressDetails?.cityName,
            billToAddressState: this.customerAddressDetails?.stateName,
            billToAddressZip: this.customerAddressDetails?.pincode,
            custServiceMappingId: this.customerDetailData.planMappingList[0].custServiceMappingId,
            email: this.customerDetailData?.email,
            isBuyPlan: true,
            isFromCaptive: true,
            actualAmount: invoice.totalamount.toString(),
            isAdvancePayment: true,
            merchantName: "TRANSACTEASE",
            customerUserName: this.customerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber:
                this.customerDetailData.countryCode.replace("+", "") +
                (this.customerDetailData.mobile ?? ""),
            payerMobileNumber:
                this.customerDetailData.countryCode.replace("+", "") +
                (this.customerDetailData.mobile ?? ""),
            partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerDetailData.buId
        };
        this.customerdetailsilsService.buyPlanUsingTransactease(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                this.paymentConfirmationModal = true;
                this.isMpinFormSubmitted = false;
                this.mobileError = false;
                this.inputMobile = "";
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.exitBuy = false;
                if (response) {
                    //   let paymentUrl = response.data;
                    //   window.open(paymentUrl, "_blank");
                    //   //   this.messageService.add({
                    //   //     severity: "info",
                    //   //     summary: "KBZPay Not Supported on Web",
                    //   //     detail: "Please open the payment link on your mobile device using the KBZPay app.",
                    //   //     icon: "pi pi-info-circle"
                    //   //   });
                    //   this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.data.message,
                    //     icon: "far fa-times-circle"
                    //   });
                    const htmlString = response;
                    if (typeof htmlString === "string" && htmlString.trim().startsWith("<!DOCTYPE html")) {
                        if (newTab) {
                            newTab.document.open();
                            newTab.document.write(htmlString);
                            newTab.document.close();
                        } else {
                            error: (error) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Please allow popups for this site!');
                            }


                        }
                    }
                } else {
                    this.toastr.info(`${response.responseMessage || "Unexpected response received"}`, 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }

    getCustomerAddressDetails(invoice?: any) {
        try {
            this.customerdetailsilsService
                .getCustomerAddressDetails(this.customerDetailData.id)
                .subscribe(
                    (result: any) => {
                        this.customerAddressDetails =
                            result.dataList && result.dataList?.length > 0 ? result.dataList[0] : [];
                        this.buyTransacteasePayment(this.invoice);
                    },
                    (error: any) => {
                        this.spinner.hide();
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
        } catch (error) {
            console.error("ERror in api", error);
        }
    }
    parentstaffListdatatotalRecords = this.staffData.length;; // total items in dataset
    parentStaffListdataitemsPerPage = 10; // items per page
    totalStaffCount: number = 0;

    handleMpesaPaymentOption(option: string) {
        this.spinner.hide();
        this.displayMpesaOptionsDialog = false;
        if (option === "Mpesa-Express") {
            this.buyMpesaExpressPlan(this.invoiceForMpesa);
        } else if (option === "Mpesa-B2C") {
            this.spinner.show();
            this.buyMpesaInvoicePayment(this.invoiceForMpesa);
        }
    }
    // Add method to close MPESA options dialog
    closeMpesaOptionsDialog() {
        this.displayMpesaOptionsDialog = false;
    }
    buyMpesaInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice?.totalamount - invoice?.adjustedAmount).toString(),
            isFromCaptive: false,
            customerUserName: this.customerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber:
                this.customerDetailData.countryCode.replace("+", "") +
                (this.customerDetailData.mobile ?? ""),
            invoiceId: invoice.id,
            partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            custServiceMappingId: this.customerDetailData.planMappingList[0].custServiceMappingId,
            buid: this.customerDetailData?.buId,
            orderId: "",
            planId: this.customerDetailData.planMappingList[0].planId
        };
        this.customerdetailsilsService.buyPlanUsingMpesa(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                if (response.responseCode == 200) {
                    this.paymentConfirmationModal = true;
                    this.toastr.success(`${response.data.ResponseDescription}`, 'Success!');


                } else {
                    this.toastr.info(`${response?.data?.errorMessage}`, 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');

            }
        );
    }
    buyMpesaExpressPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.id,
            amount: (invoice?.totalamount - invoice?.adjustedAmount).toString(),
            // isFromCaptive: true,
            customerUserName: this.customerDetailData.username,
            // customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            // mobileNumber:
            //     this.customerDetailData.countryCode.replace("+", "") +
            //     (this.customerDetailData.mobile ?? ""),
            payerMobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") +
                (this.mpinForm.value.mobileNumber ?? ""),
            // merchantName: null,
            // invoiceId: invoice.id,
            // partnerId: this.customerDetailData.partnerid,
            accountNumber: this.customerDetailData?.acctno ?? "",
            // custServiceMappingId: this.customerDetailData.planMappingList[0].custServiceMappingId,
            // buid: this.customerDetailData?.buId,
            // orderId: "",
            // planId: this.customerDetailData.planMappingList[0].planId,
            // hash: null,
            // isAdvancePayment: false,
            // isBuyPlan: true,
            // partnerPaymentId: this.customerDetailData.partnerid,
            // status: "PENDING"
        };
        this.customerdetailsilsService.buyPlanUsingMpesaExpress(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                if (response.responseCode == 200) {
                    this.paymentConfirmationModal = true;
                    this.toastr.success(`${response.data.ResponseDescription}`, 'Success!');


                } else {
                    this.toastr.info(`${response?.data?.errorMessage}`, 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }

    onCurrencyChange(event: any, invoice: any) {
        // invoice.selectedCurrency = event.value;
        // invoice.isDisplayConvertedAmount = event.value !== this.customerLedgerDetailData?.currency;
        this.isDisplayConvertedAmount =
            event.value !=
            (this.customerDetailData?.currency ? this.customerDetailData?.currency : this.currency);
    }

    onConvertedRateChange() {
        this.invoiceList.forEach(element => {
            element.convertedAmount = element.testamount * this.convertedExchangeRate;
        });
    }

    checkMobileLength() {
        this.isMobileNumberFocus = false;

        if (this.customerGroupForm.value.mobile.length >= this.commondropdownService.maxMobileLength) {
            this.isMobileNumberFocus = false;
        } else {
            this.isMobileNumberFocus = true;
        }
    }

    getaclEntries() {
        const aclvalue: any[] = JSON.parse(localStorage.getItem("aclEntries") || "[]");

        if (this.custType == "Prepaid") {
            var aclPreCust = aclvalue.find(item => item.code === "pre_cust_view_password");
        } else {
            if (this.custType == "Postpaid") {
                var aclPreCust = aclvalue.find(item => item.code === "post_cust_view_password");
            }
        }

        this.aclPreCustCode =
            aclPreCust?.code === "pre_cust_view_password" ||
                aclPreCust?.code === "post_cust_view_password"
                ? aclPreCust.code
                : null;
    }

    exportExcel() {
        const url = `/findAllCustomerNotes/${this.customerId}`;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            const exportData = response.customerNotesList;

            exportData.map((item: any) => {
                item['Customer Name'] = this.customerDetailData.custname;
                item['Account Number'] = this.customerDetailData.acctno;
                item['Notes'] = item.notes;
                item['Created By'] = item.createdByName;
                item['Id'] = item.id;
                item['Create Time'] = this.datepipe.transform(item.createdOn, 'dd-MM-yyyy HH:mm:ss');
                item['Account Status.'] = this.customerDetailData.customerServiceMappingList[0]?.status || '-';
                item['FAT'] = this.customerDetailData.areaName;
                item['Service Area'] = this.customerDetailData.serviceareaName;
                item['Olt Name'] = this.customerDetailData.oltName;
                delete item.notes;
                delete item.createdBy;
                delete item.id;
                delete item.createdOn;
                delete item.custId;
                delete item.createdByName;
            });
            const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(exportData);
            const workbook: XLSX.WorkBook = {
                Sheets: { 'data': worksheet },
                SheetNames: ['data'],
            };
            const excelBuffer: any = XLSX.write(workbook, {
                bookType: 'xlsx',
                type: 'array'
            });
            this.saveAsExcelFiles(excelBuffer, 'CustomerNotes');
        });
    }

    saveAsExcelFiles(buffer: any, fileName: string): void {
        const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
        const EXCEL_EXTENSION = '.xlsx';
        const data: Blob = new Blob([buffer], { type: EXCEL_TYPE });
        const timestamp = new Date().getTime();
        FileSaver.saveAs(data, `${fileName}_export_${timestamp}${EXCEL_EXTENSION}`);
    }

    onCancelDiscount() {
        this.rejectApproveDiscountDialogRef.close();
    }
    onCancelStaffAssign() {
        this.customerDiscountDialogRef.close();
    }
    openSubMenu(url) {
        this.router.navigate([url]);
    }
    getCurrentStaffPageIndex(): number {
        return Math.floor(this.newStaffFirst / this.approveStaffListdataitemsPerPageForStaff);
    }
    getTotalStaffCount(): number {
        return this.approveCAFData ? this.approveCAFData.length : 0;
    }
    handleBackToList(moduleType?: string) {
        if (moduleType === 'ticket') {
            this.isViewTicketMenu = false;
            this.isVisibleCAFHomeComponent = true;
        }
        if (moduleType === 'payment') {
            this.viewCustomerPaymentList = false;
            this.isVisibleCAFHomeComponent = true;
        }
        if (moduleType === 'wallet') {
            this.ifWalletMenu = false;
            this.isVisibleCAFHomeComponent = true;
        }
        if (moduleType === 'charge') {
            this.ifChargeGetData = false;
            this.isVisibleCAFHomeComponent = true;
        }
    }
}
