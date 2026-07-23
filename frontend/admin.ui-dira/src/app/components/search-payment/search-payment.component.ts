import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { SearchPaymentService } from "src/app/service/search-payment.service";
import { CustomerDetailsService } from "src/app/service/customer-details.service";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerDetailsComponent } from "../common/customer-details/customer-details.component";
import { BehaviorSubject } from "rxjs";
import * as FileSaver from "file-saver";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { StaffService } from "src/app/service/staff.service";
import { PaymentAmountModelComponent } from "src/app/components/payment-amount-model/payment-amount-model.component";
import { WorkflowAuditDetailsModalComponent } from "src/app/components/workflow-audit-details-modal/workflow-audit-details-modal.component";
declare var $: any;
import { ToastrService } from 'ngx-toastr';
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { CountryManagementService } from "src/app/service/country-management.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { PAYMENT_SYSTEMS } from "src/app/constants/aclConstants";
import { CustomerService } from "src/app/service/customer.service";
import { DatePipe } from "@angular/common";
import * as XLSX from "xlsx";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: "app-search-payment",
    templateUrl: "./search-payment.component.html",
    styleUrls: ["./search-payment.component.css"],
    standalone: false
})
export class SearchPaymentComponent implements OnInit {
    private currentDialogRef: MatDialogRef<any>;

    @ViewChild(CustomerDetailsComponent)
    customerDetailModal: CustomerDetailsComponent;

    @ViewChild(PaymentAmountModelComponent)
    PaymentDetailModal: PaymentAmountModelComponent;

    @ViewChild(WorkflowAuditDetailsModalComponent)
    custauditWorkflowModal: WorkflowAuditDetailsModalComponent;
    assignPaymentStaffForm: UntypedFormGroup;
    searchPaymentFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    ifModelIsShow: boolean = false;
    customerData: any;
    searchPaymentData: any;
    currentPagePaymentSlab = 1;
    paymentitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymenttotalRecords = 0;
    isPaymentSearch: boolean = false;
    staffsubmmitted: boolean = false;
    displayInvoiceDetails: boolean = false;
    customerid: any = "";
    staffid: any = "";
    batchStaffid: any = "";
    loginStaffid: any = "";
    approveId: any = "";
    payfromdate = "";
    paytodate = "";
    batchPayfromdate = "";
    batchPaytodate = "";
    paystatus = "";
    recepit: any;
    searchData: any;
    bankDataList: any;
    bankDestination: any;
    masterSelected: any;
    checkedList: any = [];
    selectedInvoiceIdList: any = [];
    custId = new BehaviorSubject({
        custId: ""
    });
    paymentId = new BehaviorSubject({
        paymentId: ""
    });
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    payStatus = [
        { label: "Pending (Collected/Submitted)", value: "Pending" },
        { label: "Verified", value: "Approved" },
        { label: "Rejected", value: "Rejected" }
    ];

    invoiceStatusList = [
        { label: "Unpaid", value: "Unpaid" },
        { label: "Clear", value: "Clear" },
        { label: "Cancelled", value: "Cancelled" },
        { label: "Partial Pending", value: "Partial Pending" },
        { label: "Pending", value: "Pending" }
    ];

    batchStatus = [
        { label: "Pending", value: "Pending" },
        { label: "Approved", value: "Approved" },
        { label: "Rejected", value: "Rejected" }
    ];

    auditSearchOption = [
        { label: "Customer Name", value: "customerUsername" },
        { label: "Reference", value: "orderid" },
        { label: "Status", value: "status" },
        { label: "Merchant Name", value: "merchantName" },
        { label: "Transaction No", value: "pgTransactionId" },
        { label: "Account No", value: "accountNumber" },
        { label: "Transaction Date", value: "transactionDate" },
        { label: "Payer Mobile Number", value: "payerMobileNumber" }
    ];

    selectedAuditSearchOption: any = "";
    selectedAuditSearchValue: any = "";

    searchStaffOptionSelect = [{ label: "Global Search Filter", value: "globalsearch" }];
    searchOption: any = "";
    searchDeatil: any = "";

    ifPaymentList = true;
    ifOnlinePaymentAuditList = false;
    ifBatchList = false;
    chakedPaymentData = [];
    ispaymentChecked: boolean = false;
    allIsChecked: boolean = false;
    isSinglepaymentChecked = false;

    batchitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPagebatch = 1;
    batchtotalRecords: number;
    showItemPerPageBatch = 0;

    batchMappingitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPagebatchMapping = 0;
    batchMappingtotalRecords: number;
    batchMappingData: any = [];

    showItemPerPageBatchAudit = 0;
    batchAudititemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPagebatchAudit = 1;
    batchAudittotalRecords: number;

    onlinePayAudititemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPageOnlinePayAudit = 1;
    onlinePayAuditotalRecords: number;
    showItemPerPageOnlinePayAudit = 0;

    onlinePaymentAudititemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    currentPageOnlinePaymentAudit = 1;
    onlinePaymentAuditotalRecords: number;
    showItemPerPageOnlinePaymentAudit = 0;

    newBatchName = "";
    batchPaymentList = [];
    batchPaymentAuditList = [];
    staffList = [];
    chequeDetail = [];
    staffID = 0;
    approveID = 0;
    AssignbatchId = "";
    batchApporve = false;
    batchReject = false;
    batchAssignStaff = false;
    ifAddbatchData = false;
    batchId = "";

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    BatchName: any = "";
    batchSingleData: any = [];
    paymentMode: any = [];
    reject = false;
    rejectCAF = [];
    selectStaffReject: any;
    approved = false;
    approveCAF = [];
    selectStaff: any;
    workflowApproveId: any;
    chequeNumber = "";
    invoiceNumber = "";
    staff = "";
    paymode = "";
    branchname = "";
    buid = "";
    referenceno = "";
    branchName = "";
    remark: any;
    selectApprove: any;
    receiptNo = "";
    chequedate = "";
    paydetails1 = "";
    destinationBank = "";
    batchDestinationBank = "";
    displayedColumnsOnlinePaymentAudit = [
        'orderId',
        'pgTransactionId',
        'accountNumber',
        'customerUsername',
        'payment',
        'status',
        'gatewayStatus',
        'failureDescription',
        'paymentDate',
        'merchantName',
        'transactionDate',
        'payerMobileNumber',
        'autoPaymentInitiator',
        'action'
    ];

    partnerName = "";
    serviceAreaId = "";

    onlinePaymentAuditList: any;
    loggedInUserObj;
    partnerId = 1;
    status: any = [];
    staffId: number;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    assignAccess: boolean = false;
    batchAuditDetailsAccess: boolean = false;
    createBatchAccess: boolean = false;
    batchPaymentAccess: boolean = false;
    downloadAccess: boolean = false;
    reassignAccess: boolean = false;
    currency: string;
    dialogId: boolean = false;
    isBatchNameModelVisible: boolean = false;
    isSelectTeamModelVisible: boolean = false;
    isAssignbatchModelVisible: boolean = false;
    paymentData: any[];
    failureReasonDialog: boolean = false;
    selectedFailureDescription: string;
    transModal: boolean = false;
    transactionNo: any;
    addToWalletOrderId: any;
    searchFromDate: any;
    searchTodate: any
    cols = [
        {
            field: "orderId",
            header: "Reference No",
            customExportHeader: "Reference No"
        },
        {
            field: "pgTransactionId",
            header: "Transaction No",
            customExportHeader: "Transaction No"
        },
        {
            field: "accountNumber",
            header: "Account Number",
            customExportHeader: "Account Number"
        },
        {
            field: "customerUsername",
            header: "Customer Username",
            customExportHeader: "Customer Username"
        },
        {
            field: "payment",
            header: "Payment Amount",
            customExportHeader: "Payment Amount"
        },
        {
            field: "status",
            header: "Status",
            customExportHeader: "Status"
        },
        {
            field: "gatewayStatus",
            header: "Gateway Status",
            customExportHeader: "Gateway Status"
        },
        {
            field: "failureDescription",
            header: "Failure reason",
            customExportHeader: "Failure reason"
        },
        {
            field: "paymentDate",
            header: "Payment Date",
            customExportHeader: "Payment Date"
        },
        {
            field: "merchantName",
            header: "Merchant Name",
            customExportHeader: "Merchant Name"
        },
        {
            field: "transactionDate",
            header: "Transaction Date",
            customExportHeader: "Transaction Date"
        },
        {
            field: "payerMobileNumber",
            header: "Payer Mobile Number",
            customExportHeader: "Payer Mobile Number"
        },
        {
            field: "autoPaymentInitiator",
            header: "Auto Payment Initiator",
            customExportHeader: "Auto Payment Initiator"
        }

    ];
    approveCAFData: any[];
    searchStaffDeatil: any;

    displayedColumns: string[] = [
        'select', 'customerName', 'paytype', 'paymode', 'paymentreferenceno', 'status', 'amount', 'adjustedAmount', 'createdate', 'createbyname', 'actions'
    ];
    displayedColumnsBatch: string[] = [
        'batchName',
        'invoiceCount',
        'totalAmount',
        'tds',
        'abbs',
        'assignee',
        'batchStatus',
        'file',
        'approveReject',
        'action'
    ];
    panelTitle: string = 'Search Payment';
    searchPanelExpanded: boolean = true;
    batchPanelExpanded: boolean = true;
    auditPanelExpanded: boolean = true;
    approveDisplay = ['select', 'firstname', 'username']

    approveRejectDisplay = ['amount', 'chequedate', 'chequeNo']
    rejectDisplay = ['select', 'fullName', 'username']
    custCafDisplay = ['select', 'fullName', 'username']
    displayedColumnsCutomer: string[] = ['select', 'name', 'username'];
    batchmappingDisplay = ['select', 'customerName', 'amount', 'tdsAmount', 'abbsAmount', 'paymode', 'status', 'date', 'delete']
    displayStaff = ['select', 'name', 'username', 'partner']
    payamentDisply = ['batchName', 'staffName', 'teamName', 'status', 'remark']
    @ViewChild('createBatchDialog') createBatchDialog!: TemplateRef<any>;
    @ViewChild('assignBatchDialog') assignBatchDialog!: TemplateRef<any>;
    @ViewChild('selectTeamDialog') selectTeamDialog!: TemplateRef<any>;

    @ViewChild('batchPaymentAuditDialog') batchPaymentAuditDialog!: TemplateRef<any>;

    @ViewChild('batchMappingDialog') batchMappingDialog!: TemplateRef<any>;
    @ViewChild('assignCustomerCAFDialog') assignCustomerCAFDialog!: TemplateRef<any>;
    @ViewChild('rejectCustomerCAFDialog') rejectCustomerCAFDialog!: TemplateRef<any>;

    @ViewChild('approveRejectDialog') approveRejectDialog!: TemplateRef<any>;
    private approveCustomerDialogRef: MatDialogRef<any>;
    @ViewChild('selectParentCustomerDialog') selectParentCustomerDialog!: TemplateRef<any>;
    @ViewChild('approveCustomerDialog') approveCustomerDialog!: TemplateRef<any>;
    @ViewChild('failureReasonDialogTemplate') failureReasonDialogTemplate!: TemplateRef<any>;
    @ViewChild('addTransactionDialogTemplate') addTransactionDialogTemplate!: TemplateRef<any>;

    @ViewChild('selectStaffDialog') selectStaffDialog!: TemplateRef<any>;
    @ViewChild('chequeDetailsDialog') chequeDetailsDialog!: TemplateRef<any>;
    @ViewChild('selectApproveDialog') selectApproveDialog!: TemplateRef<any>;


    selectParentCustomerDialogRef: MatDialogRef<any>;
    createBatchDialogRef: MatDialogRef<any>;
    PaymentDetailModalRef: MatDialogRef<any>;
    custauditWorkflowModalRef: MatDialogRef<any>;


    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private toastr: ToastrService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private searchPaymentService: SearchPaymentService,
        private customerService: CustomerService,
        private revenueManagementService: RevenueManagementService,
        private customerDetailsService: CustomerDetailsService,
        public PaymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService,
        private recordPaymentService: RecordPaymentService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private staffService: StaffService,
        loginService: LoginService,
        private customerManagementService: CustomermanagementService,
        private systemService: SystemconfigService,
        private countryManagementService: CountryManagementService,
        public datePipe: DatePipe, private dialog: MatDialog,
    ) {
        this.batchStaffid = Number(localStorage.getItem("userId"));
        this.loginStaffid = this.batchStaffid;
        this.partnerId = Number(localStorage.getItem("partnerId"));
        this.createAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAY_BATCH_PAY_CREATE);
        this.deleteAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAY_BATCH_PAY_DELETE);
        this.assignAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAY_BATCH_PAY_ASSIGN);
        this.batchAuditDetailsAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAYMENT_BATCH_AUDIT);
        this.createBatchAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAYMENT_CREATE_BATCH);
        this.batchPaymentAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAY_BATCH_PAYMENT);
        this.downloadAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAYMENT_DOWNLOAD);
        this.reassignAccess = loginService.hasPermission(PAYMENT_SYSTEMS.PAYMENT_REASSIGN);
        this.loginService = loginService;
    }
    assignPaymentForm: UntypedFormGroup;
    ngOnInit(): void {
        this.initializeVariables();
        let staffID = localStorage.getItem("userId");
        let loggedInUser = localStorage.getItem("loggedInUser");
        this.staffCustList = [
            {
                id: Number(staffID),
                name: loggedInUser
            }
        ];
        this.selectApproveList = [
            {
                id: Number(staffID),
                name: loggedInUser
            }
        ];
        this.searchPanelExpanded = true;
        this.batchPanelExpanded = true;
        this.auditPanelExpanded = true;
        if (this.batchPaymentAccess) {
            this.activeTabIndex = 1;
            this.panelTitle = "Search Payment";
            // Ensure Search is expanded
            this.searchPanelExpanded = true;
            this.SearchPayment();
        } else {
            this.activeTabIndex = 0;
            this.panelTitle = "Search Payment";
            this.searchPanelExpanded = true;
            this.SearchPayment();
        }
        // this.approveId = Number(staffID);
        // this.staffid = Number(staffID);
        this.staffID = Number(staffID);
        this.getPaymentMode();
        this.getBusinessUnit();
        this.getBankDetail();
        this.getBankDestinationDetail();
        this.commondropdownService.filterserviceAreaList();
        this.commondropdownService.getpartnerAll();
        this.assignPaymentStaffForm = this.fb.group({
            batchId: [""],
            nextStaffId: [""],
            remark: [""],
            staffId: [""],
            approveId: [""]
        });

        this.searchData = {
            filters: [
                {
                    filterDataType: "string",
                    filterValue: "string",
                    filterColumn: "any",
                    filterOperator: "string",
                    filterCondition: "string"
                }
            ],
            page: "",
            pageSize: ""
        };
        this.commondropdownService.getAllActiveBranch();
        this.ifPaymentList = true;
        this.ifBatchList = false;
        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getAllActiveStaff();
        const serviceArea = localStorage.getItem("serviceArea");

        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        this.assignPaymentForm = this.fb.group({
            remark: [""]
        });

        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
    }

    initializeVariables() {
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        this.payfromdate = yesterday.toISOString();
        this.paytodate = today.toISOString();
        this.paystatus = "Pending";
    }

    clearPayStatus() {
        this.paystatus = "";
    }

    selSearchOption() {
        this.selectedAuditSearchValue = "";
    }

    openPaymentInvoiceModal(id, paymentId) {
        this.dialog.open(PaymentAmountModelComponent, {
            width: '80%', disableClose: true,
            data: {
                paymentId: paymentId
            }
        });
    }

    getCustomer() {
        // let customerListData = [];
        // const url = "/customers/list";
        // let custerlist = {};
        // this.searchPaymentService.postMethod(url, custerlist).subscribe(
        //     (response: any) => {
        //         const serviceArea = localStorage.getItem("serviceArea");
        //         if (serviceArea != "null") {
        //             let customerListData = response.customerList.filter(
        //                 cust => cust.networkDetails.serviceareaid == localStorage.getItem("serviceArea")
        //             );
        //             this.customerData = customerListData;
        //         } else {
        //             this.customerData = response.customerList;
        //         }
        //     },
        //     (error: any) => {
        //         console.log(error, "error");
        //         this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        //     }
        // );
    }

    openModal(id, custId) {
        this.dialog.open(CustomerDetailsComponent, {
            width: '80%', disableClose: true,
            data: {
                custId: custId
            }
        });

        // this.dialogId = true;
        this.custId.next({
            custId: custId,
        });
    }

    closeSelectStaff() {
        this.dialogId = false;
    }

    closeParentCustt() {
        this.dialog.closeAll();
        this.ifModelIsShow = false;
    }

    closeParentCust() {
        this.displayInvoiceDetails = false;
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagePaymentSlab > 1) {
            this.currentPagePaymentSlab = 1;
        }
        if (!this.searchkey) {
            this.searchPayment(this.showItemPerPage);
        }
    }
    manualSearch() {
        //this.searchPanelExpanded = false;
        this.searchPayment('');
    }
    searchPayment(size) {
        if (this.payfromdate) {
            const date = new Date(this.payfromdate);
            const payfromdateFormatted =
                date.getFullYear() +
                "-" +
                String(date.getMonth() + 1).padStart(2, "0") +
                "-" +
                String(date.getDate()).padStart(2, "0");
            this.payfromdate = payfromdateFormatted;
        }
        if (this.paytodate) {
            const date = new Date(this.paytodate);
            const paytodateFormatted =
                date.getFullYear() +
                "-" +
                String(date.getMonth() + 1).padStart(2, "0") +
                "-" +
                String(date.getDate()).padStart(2, "0");
            this.paytodate = paytodateFormatted;
        }
        if (this.chequedate) {
            const date = new Date(this.chequedate);
            const chequedateFormatted =
                date.getFullYear() +
                "-" +
                String(date.getMonth() + 1).padStart(2, "0") +
                "-" +
                String(date.getDate()).padStart(2, "0");
            this.chequedate = chequedateFormatted;
        }

        this.totalCheckedPayments = {
            totalSelPayments: 0,
            totalAmount: 0
        };

        let page_list;

        if (size) {
            page_list = size;
            this.paymentitemsPerPage = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.paymentitemsPerPage = this.pageITEM;
            } else {
                this.paymentitemsPerPage = this.showItemPerPage;
            }
        }

        let url;
        if (this.batchSingleData.length !== 0) {
            this.paystatus = "Pending";
            url =
                "/paymentGateway/payment/search?customerid=" +
                this.customerid +
                "&paystatus=" +
                this.paystatus +
                "&paytodate=" +
                this.paytodate +
                "&payfromdate=" +
                this.payfromdate +
                "&type=Payment" +
                "&invoiceNumber=" +
                this.invoiceNumber +
                "&chequeNo=" +
                this.chequeNumber +
                "&staff=" +
                this.staffid +
                "&paymode=" +
                this.paymode +
                "&branchname=" +
                this.branchname +
                "&buID=" +
                this.buid +
                "&referenceno=" +
                this.referenceno +
                "&approveId =" +
                this.approveId +
                "&receiptNo=" +
                this.receiptNo +
                "&chequedate=" +
                this.chequedate
            "& paydetails1=" +
                this.paydetails1 +
                "&destinationBank=" +
                this.destinationBank +
                "&partnerName=" +
                this.partnerName +
                "&serviceAreaId=" +
                this.serviceAreaId +
                "&page=" +
                this.currentPagePaymentSlab +
                "&pageSize=" +
                this.paymentitemsPerPage;
        } else {
            url =
                "/paymentGateway/payment/search?customerid=" +
                this.customerid +
                "&paystatus=" +
                this.paystatus +
                "&paytodate=" +
                this.paytodate +
                "&payfromdate=" +
                this.payfromdate +
                "&type=Payment" +
                "&invoiceNumber=" +
                this.invoiceNumber +
                "&chequeNo=" +
                this.chequeNumber +
                "&staff=" +
                this.staffid +
                "&paymode=" +
                this.paymode +
                "&branchname=" +
                this.branchName +
                "&buID=" +
                this.buid +
                "&referenceno=" +
                this.referenceno +
                "&approveId=" +
                this.approveId +
                "&receiptNo=" +
                this.receiptNo +
                "&chequedate=" +
                this.chequedate +
                "&paydetails1=" +
                this.paydetails1 +
                "&destinationBank=" +
                this.destinationBank +
                "&partnerName=" +
                this.partnerName +
                "&serviceAreaId=" +
                this.serviceAreaId +
                "&page=" +
                this.currentPagePaymentSlab +
                "&pageSize=" +
                this.paymentitemsPerPage;
        }
        this.payfromdate;
        this.searchPaymentData = [];
        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                if (response.creditDocumentPojoList.length > 0) {
                    let serviceArea: any = [];
                    let searchPaymentData: any;
                    serviceArea = JSON.parse(localStorage.getItem("serviceArea"));
                    this.searchPaymentData = response.creditDocumentPojoList;
                    if (response.pageDetails) {
                        this.paymenttotalRecords = response.pageDetails.totalRecords;
                        if (this.showItemPerPage > this.paymentitemsPerPage) {
                            this.totalAreaListLength = this.searchPaymentData.length % this.showItemPerPage;
                        } else {
                            this.totalAreaListLength = this.searchPaymentData.length % this.paymentitemsPerPage;
                        }
                    }
                    this.isPaymentSearch = true;
                    this.ispaymentChecked = false;
                    this.allIsChecked = false;
                    this.isSinglepaymentChecked = false;
                    this.chakedPaymentData = [];
                    if (this.batchSingleData.length !== 0) {
                        this.searchPaymentData.forEach((element, index) => {
                            this.batchSingleData.forEach(data => {
                                if (element.id == data.creditDocumentId || element.batchAssigned == true) {
                                    this.searchPaymentData.splice(index, 1);
                                }
                            });
                        });
                    }
                    this.searchPanelExpanded = false;
                    this.toastr.success(`Records fetched successfully`, 'Success!');
                } else {
                    this.isPaymentSearch = false;
                    this.searchPanelExpanded = true;
                    this.toastr.info(`No records found`, 'Info!');
                }
            },
            (error: any) => {
                this.searchPanelExpanded = true;
                this.isPaymentSearch = false;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                } else {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }
    getPaymentMode() {
        const url = "/commonList/paymentMode";
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.paymentMode = response.dataList;
        });
    }
    businessUnit: any = [];
    businessUnitList: any = [];
    getBusinessUnit() {
        const url = "/businessUnit/all";
        this.countryManagementService.getMethodWithCache(url).subscribe((response: any) => {
            this.businessUnit = response.dataList;
        });
    }
    clearPayment() {
        this.isPaymentSearch = false;
        this.customerid = "";
        this.payfromdate = "";
        this.paytodate = "";
        this.paystatus = "";
        this.chequeNumber = "";
        this.invoiceNumber = "";
        this.paymode = "";
        this.branchName = "";
        this.referenceno = "";
        this.buid = "";
        this.staffid = "";
        this.approveId = "";
        this.searchPaymentData = [];
        this.batchSingleData = [];
        this.ifAddbatchData = false;
        this.receiptNo = "";
        this.chequedate = "";
        this.paydetails1 = "";
        this.destinationBank = "";
        this.serviceAreaId = "";
        this.partnerName = "";
        this.totalCheckedPayments = {
            totalSelPayments: 0,
            totalAmount: 0
        };
        this.searchPanelExpanded = false;
        this.searchPayment('');
    }

    clearAuditSearch() {
        this.selectedAuditSearchOption = "";
        this.selectedAuditSearchValue = "";
        this.searchFromDate = "";
        this.searchTodate = "";
        this.searchOnlineAuditPayment(false);
        this.auditPanelExpanded = false;
    }

    clearBatch() {
        this.status = "";
        this.branchName = "";
        this.batchStaffid = "";
        this.batchDestinationBank = "";
        this.serviceAreaId = "";
        this.partnerName = "";
        this.batchPayfromdate = "";
        this.batchPaytodate = "";
        this.batchPanelExpanded = false;
    }

    pageChangedPaymentList(pageNumber) {
        this.currentPagePaymentSlab = pageNumber.pageIndex + 1;
        this.paymentitemsPerPage = pageNumber.pageSize;
        this.searchPayment(this.paymentitemsPerPage);
    }

    downloadreceipt(id: any) {
        const url = "/payment/generatereceipt/" + id;
        this.searchPaymentService.downloadPDF(url).subscribe(
            (response: any) => {
                var file = new Blob([response], { type: "application/pdf" });
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, "bill.pdf");
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    ifApproveStatus = false;
    approveHeader: any;
    approveRejectRemark = "";
    ticketApprRejectData: any = [];
    ApproveRejectModal: boolean = false;
    approveModalOpen(data) {

        this.approveRejectRemark = "";
        this.ifApproveStatus = true;
        this.approveHeader = "Approve Payment";
        this.ticketApprRejectData = data;
        this.ApproveRejectModal = true;

        this.approveCustomerDialogRef = this.dialog.open(this.approveRejectDialog, {
            width: '450px',
            disableClose: true
        });


    }

    rejectModalOpen(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = false;
        this.approveHeader = "Reject Payment";
        this.ticketApprRejectData = data;
        this.ApproveRejectModal = true;
        this.approveCustomerDialogRef = this.dialog.open(this.approveRejectDialog, {
            width: '450px',
            disableClose: true
        });

    }

    rejectCustomerCAFModal: boolean = false;
    statusRejected() {
        this.workflowApproveId = this.ticketApprRejectData.id;
        this.reject = false;
        this.selectStaffReject = null;
        this.rejectCAF = [];
        let rejectdata = {
            customerid: this.ticketApprRejectData.custId,
            idlist: Number(this.ticketApprRejectData.id),
            paymode: this.ticketApprRejectData.paymode,
            paystatus: this.ticketApprRejectData.status,
            paytodate: this.ticketApprRejectData.paymentdate,
            referenceno: this.ticketApprRejectData.referenceno,
            remarks: this.approveRejectRemark
        };
        const url = "/payment/reject";
        this.searchPaymentService.postMethod(url, rejectdata).subscribe(
            (response: any) => {
                this.ApproveRejectModal = false;
                if (this.approveCustomerDialogRef) {
                    this.approveCustomerDialogRef.close();
                    this.approveCustomerDialogRef = null;
                }
                if (response.payment.dataList) {
                    this.reject = true;
                    this.rejectCAF = response.payment.dataList;
                    this.currentDialogRef.close();
                    this.dialog.open(this.rejectCustomerCAFDialog, {
                        width: '80%',
                        disableClose: true
                    });
                    this.rejectCustomerCAFModal = true;
                } else {
                    this.searchPayment("");
                }
                this.ifApproveStatus = false;
                this.ticketApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    assignCustomerCAFModal: boolean = false;
    statusApporeved() {
        this.workflowApproveId = this.ticketApprRejectData.id;
        this.approved = false;
        this.approveCAF = [];
        this.selectStaff = null;
        let approvedData = {
            customerid: this.ticketApprRejectData.custId,
            idlist: Number(this.ticketApprRejectData.id),
            invoiceNumber: this.ticketApprRejectData.invoiceNumber,
            paymode: this.ticketApprRejectData.paymode,
            paystatus: this.ticketApprRejectData.status,
            paytodate: this.ticketApprRejectData.paymentdate,
            referenceno: this.ticketApprRejectData.referenceno,
            remarks: this.approveRejectRemark
        };
        const url = "/payment/approve";

        this.searchPaymentService.postMethod(url, approvedData).subscribe(
            (response: any) => {
                this.ApproveRejectModal = false;
                if (this.approveCustomerDialogRef) {
                    this.approveCustomerDialogRef.close();
                    this.approveCustomerDialogRef = null;
                }
                if (response.payment.dataList) {
                    this.approved = true;
                    this.approveCAF = response.payment.dataList;
                    this.approveCAFData = this.approveCAF
                    this.assignCustomerCAFModal = true;
                    this.dialog.open(this.assignCustomerCAFDialog, {
                        width: '80%',
                        disableClose: true
                    });
                } else {
                    this.searchPayment("");
                }
                this.ifApproveStatus = false;
                this.ticketApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    assignToStaff(flag, dialogRef) {
        let url: any;
        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.workflowApproveId
                    }&eventName=${"PAYMENT"}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.workflowApproveId
                    }&eventName=${"PAYMENT"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.workflowApproveId
                    }&eventName=${"PAYMENT"}&nextAssignStaff=${this.selectStaffReject
                    }&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.workflowApproveId
                    }&eventName=${"PAYMENT"}&isApproveRequest=${flag}`;
            }
        }

        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                this.assignCustomerCAFModal = false;
                this.rejectCustomerCAFModal = false;
                this.searchPayment("");
                dialogRef.close()
                if (response.status == 200) {
                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    batchList() {
        this.ifOnlinePaymentAuditList = false;
        this.ifPaymentList = false;
        this.ifBatchList = true;
        this.ifAddbatchData = false;
        this.ispaymentChecked = false;
        this.allIsChecked = false;
        this.isSinglepaymentChecked = false;
        this.chakedPaymentData = [];
        this.searchBatch("");
        this.batchId = "";
        this.BatchName = "";
        this.newBatchName = "";
        this.batchSingleData = [];
        this.isPaymentSearch = false;
    }

    SearchPayment() {
        this.ifOnlinePaymentAuditList = false;
        this.ifPaymentList = true;
        this.ifBatchList = false;
        this.ifAddbatchData = false;
        this.ispaymentChecked = false;
        this.allIsChecked = false;
        this.isSinglepaymentChecked = false;
        this.chakedPaymentData = [];
        this.batchId = "";
        this.BatchName = "";
        this.newBatchName = "";
        this.batchSingleData = [];
        this.searchPayment("");
    }

    batchSaveOnly(dialogRef) {
        let data = {
            assignedStatus: "Assigned",
            batchPaymentMappingList: this.chakedPaymentData,
            batchName: this.newBatchName,
            id: ""
        };

        const url = "/createBatchPayment";
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.newBatchName = "";
                dialogRef.close()
                let checkedData = this.searchPaymentData;
                this.chakedPaymentData.forEach((value, index) => {
                    checkedData.forEach(element => {
                        if (element.id == value.credit_doc_id) {
                            element.isSinglepaymentChecked = false;
                        }
                    });
                });
                this.chakedPaymentData = [];

                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.newBatchName = "";
                this.toastr.success(`${response.msg}`, 'Success!');
                this.isBatchNameModelVisible = false;
                setTimeout(() => {
                    this.searchPayment("");
                }, 100);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    clearBatchName() {
        this.newBatchName = "";
        this.isBatchNameModelVisible = true;
        this.dialog.open(this.createBatchDialog, {
            width: '50%',
            disableClose: true
        });
    }

    closeBatchName() {
        this.newBatchName = "";
        this.isBatchNameModelVisible = false;
    }

    TotalItemPerPageBatchList(event) {
        this.showItemPerPageBatch = Number(event.value);
        if (this.currentPagebatch > 1) {
            this.currentPagebatch = 1;
        }
        this.searchBatch(this.showItemPerPageBatch);
    }

    TotalItemPerPageAuditList(event) {
        this.onlinePaymentAudititemsPerPage = Number(event.value);
        if (this.currentPageOnlinePaymentAudit > 1) {
            this.currentPageOnlinePaymentAudit = 1;
        }
        this.searchOnlineAuditPayment(false);
    }

    batchPaymentDetailsList(size) {
        let staffId = this.staffID;

        let page_list;

        if (size) {
            page_list = size;
            this.batchitemsPerPage = size;
        } else {
            if (this.showItemPerPageBatch == 0) {
                this.batchitemsPerPage = this.pageITEM;
            } else {
                this.batchitemsPerPage = this.showItemPerPageBatch;
            }
        }

        this.batchPaymentList = [];
        const url = "/batchPaymentDetailList?staffId=" + staffId;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.batchPaymentList = response.batchPaymentDetailList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    manualSearchBatch() {
        this.batchPanelExpanded = false;
        this.searchBatch('');
    }
    searchBatch(size) {
        let staffId = this.staffID;
        let page_list;

        if (size) {
            page_list = size;
            this.batchitemsPerPage = size;
        }

        const pagedata = {
            page: this.currentPagebatch,
            pageSize: this.batchitemsPerPage
        };

        const url =
            "/batchPayment/search?" +
            "status=" +
            this.status +
            "&staff=" +
            this.batchStaffid +
            "&serviceArea=" +
            this.serviceAreaId +
            "&branch=" +
            this.branchName +
            "&partner=" +
            this.partnerName +
            "&destinationBank=" +
            this.batchDestinationBank +
            "&fromDate=" +
            this.batchPayfromdate +
            "&toDate=" +
            this.batchPaytodate +
            "&type=Prepaid" +
            "&isInvoiceVoid=false";
        this.batchPaymentList = [];
        this.revenueManagementService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.batchPaymentList = response.batchPaymentList.content;
                this.batchtotalRecords = response.batchPaymentList.totalElements;
                // this.toastr.success(`Record Founded Successfully`, 'Success!')
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }

    searchOnlinePayAudit(size) {
        let page_list;

        if (size) {
            page_list = size;
            this.onlinePayAudititemsPerPage = size;
        } else {
            if (this.showItemPerPageOnlinePayAudit == 0) {
                this.onlinePayAudititemsPerPage = this.pageITEM;
            } else {
                this.onlinePayAudititemsPerPage = this.showItemPerPageBatch;
            }
        }

        const pagedata = {
            page: this.currentPageOnlinePayAudit,
            pageSize: this.onlinePayAudititemsPerPage,
            sortBy: "orderid"
        };

        let url = "/onlinePayAudit/all";
        this.customerManagementService.postMethodForIntegration(url, pagedata).subscribe(
            (response: any) => {
                this.batchPaymentList = response.batchPaymentList.content;
                this.batchtotalRecords = response.batchPaymentList.totalElements;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedPaymentBatchList(pageNumber) {
        this.currentPagebatch = pageNumber.pageIndex + 1;
        this.batchitemsPerPage = pageNumber.pageSize;
        this.searchBatch("");
    }

    pageChangedOnlinePayAuditList(pageNumber) {
        this.currentPageOnlinePaymentAudit = pageNumber.pageIndex + 1;
        this.onlinePaymentAudititemsPerPage = pageNumber.pageSize;
        this.searchOnlineAuditPayment(false);
    }

    pageChangedPaymentBatchAuditList(page) {
        this.currentPagebatchAudit = page;
        this.batchPaymentDetailsList("");
    }

    TotalItemPerPageBatchAuditList(event) {
        this.showItemPerPageBatchAudit = Number(event.value);
        if (this.currentPagebatchAudit > 1) {
            this.currentPagebatchAudit = 1;
        }
        this.batchPaymentAuditDetails(this.batchId, this.showItemPerPageBatchAudit);
    }
    batchPaymentAudit: boolean = false;
    batchPaymentAuditDetails(id, size) {
        this.batchPaymentAudit = true;
        this.dialog.open(this.batchPaymentAuditDialog, {
            width: '80%',
            disableClose: true
        });
        let page_list;
        this.batchId = id;
        if (size) {
            page_list = size;
            this.batchAudititemsPerPage = size;
        } else {
            if (this.showItemPerPageBatchAudit == 0) {
                this.batchAudititemsPerPage = this.pageITEM;
            } else {
                this.batchAudititemsPerPage = this.showItemPerPageBatchAudit;
            }
        }

        const url = "/batchPaymentAuditDetail?batchId=" + id;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.batchPaymentAuditList = response.batchPaymentAuditDetails;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeBatchpaymentAuditModel() {
        this.batchPaymentAudit = false;
    }

    batchMappingPersonalData = [];
    batchMapping: boolean = false;
    batchMappingList(id) {
        this.batchMapping = true;
        this.dialog.open(this.batchMappingDialog, {
            width: '80%',
            disableClose: true
        });
        let mappingData = [];
        this.batchId = id;

        mappingData = this.batchPaymentList.filter(data => data.batchId == this.batchId);
        if (mappingData.length > 0) {
            this.batchMappingPersonalData = mappingData[0];
        }

        const url = "/batchPaymentMappingList?batchId=" + id;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.batchMappingData = response.mappingList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedPaymentBatchMappingtList(page) {
        this.currentPagebatchMapping = page;
    }

    newADDbatch(data) {
        this.ifPaymentList = true;
        this.ifBatchList = false;
        this.ifAddbatchData = true;
        this.batchSingleData = data.creditDocumentList;
        this.batchId = data.batchId;
        this.BatchName = data.batchName;
        this.searchPayment("");
    }

    addBatchPaymentMapping() {
        if (this.chakedPaymentData.length == 0) {
            this.toastr.info(`Please select a Mapping Data`, 'Info!');
        } else {
            let data = {
                assignedStatus: "Assigned",
                batchPaymentMappingList: this.chakedPaymentData,
                batchname: this.BatchName,
                id: this.batchId
            };

            this.ifPaymentList = false;
            this.ifBatchList = true;
            this.ifAddbatchData = false;

            const url = "/addBatchPaymentMappingInExistingBatch";
            this.revenueManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.newBatchName = "";
                    this.BatchName = "";
                    this.ifAddbatchData = true;
                    this.batchSingleData = [];
                    this.searchBatch("");
                    this.toastr.success(`${response.message}`, 'Success!');
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    deleteBatchPaymentMapping(id) {
        const url = "/deleteBatchPaymentMappingById?id=" + id;
        this.searchPaymentService.deleteMethod(url).subscribe(
            (response: any) => {
                this.batchMappingList(this.batchId);
                this.batchPaymentDetailsList("");
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    deleteBatchPayment(id) {
        const url = "/deleteBatchPaymentById?batchId=" + id;
        this.searchPaymentService.deleteMethod(url).subscribe(
            (response: any) => {
                this.batchPaymentDetailsList("");
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    openBatchAssignStaff(id, staffName) {
        this.AssignbatchId = id;
        this.assignPaymentStaffForm.reset();
        this.batchApporve = false;
        this.batchReject = false;
        this.batchAssignStaff = true;
        this.staffUserList(id);
    }

    openBatchReject(id) {
        this.getAllTeams();
        this.AssignbatchId = id;
        this.assignPaymentStaffForm.reset();
        this.batchApporve = false;
        this.batchReject = true;
        this.batchAssignStaff = false;
        this.isAssignbatchModelVisible = true;
    }

    openBatchApporve(id, batchdata) {
        this.getAllTeams();
        this.AssignbatchId = id;
        this.assignPaymentStaffForm.reset();
        this.assignPaymentStaffForm.controls.nextStaffId.setValue("");
        this.batchApporve = true;
        this.batchReject = false;
        this.batchAssignStaff = false;
        if (batchdata.nextStaffId) {
            this.isSelectTeamModelVisible = true;
            this.dialog.open(this.selectTeamDialog, {
                width: '80%',
                disableClose: true
            });
        } else {
            this.isAssignbatchModelVisible = true;
            this.dialog.open(this.assignBatchDialog, {
                width: '80%',
                disableClose: true
            });
        }
        this.dialog.afterAllClosed.subscribe(() => {
            this.teamselected = null;
            this.staffselected = null;
            this.teamToggle = false;
            this.isSelectTeamModelVisible = false;
            this.isAssignbatchModelVisible = false;
        });
    }

    assignBatchModelVisible(dialogRef) {
        this.isAssignbatchModelVisible = true;
        dialogRef.close()
        this.dialog.open(this.assignBatchDialog, {
            width: '80%',
            disableClose: true
        });
    }

    batchModelVisibleClose() {
        this.isSelectTeamModelVisible = false;
        this.teamToggle = false;
        this.teamselected = null;
        this.staffselected = null;
        this.isAssignbatchModelVisible = false;
    }

    batchPaymentAssignStaff(dialogRef) {
        let data = {
            batchId: this.AssignbatchId,
            nextStaffId: this.assignPaymentStaffForm.value.nextStaffId,
            remark: this.assignPaymentStaffForm.value.remark,
            staffId: this.staffID
        };

        const url = "/batchPaymentAssignByStaffId";
        this.searchPaymentService.postMethod(url, data).subscribe(
            (response: any) => {
                dialogRef.close()
                this.assignPaymentStaffForm.reset();
                this.batchPaymentDetailsList("");

                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    AssignApporveStaff(dialogRef) {
        let data = {
            batchId: this.AssignbatchId,
            nextStaffId: this.staffselected ? this.staffselected.id : null,
            remark: this.assignPaymentStaffForm.value.remark,
            staffId: this.staffID
        };

        const url = "/batchPaymentApprove";
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                dialogRef.close()
                this.assignPaymentStaffForm.reset();
                this.batchPaymentDetailsList("");
                this.isSelectTeamModelVisible = false;
                this.isAssignbatchModelVisible = false;

                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    AssignRejectedStaff(dialogRef) {
        let data = {
            batchId: this.AssignbatchId,
            nextStaffId: this.staffselected ? this.staffselected.id : null,
            remark: this.assignPaymentStaffForm.value.remark,
            staffId: this.staffID
        };
        const url = "/batchPaymentReject";
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                dialogRef.close()
                this.assignPaymentStaffForm.reset();
                this.batchPaymentDetailsList("");
                this.isAssignbatchModelVisible = false;

                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    staffUserList(id) {
        let url = "/nextStaffListByBatchId?batchId=" + id;
        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                this.staffList = response.nextStaffList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    totalCheckedPayments = {
        totalSelPayments: 0,
        totalAmount: 0
    };
    allSelectBatch(event) {
        if (event.checked == true) {
            this.chakedPaymentData = [];
            let checkedData = this.searchPaymentData;
            for (let i = 0; i < checkedData.length; i++) {
                if (
                    this.searchPaymentData[i].status !== "approved" &&
                    this.searchPaymentData[i].status !== "rejected"
                ) {
                    this.chakedPaymentData.push({
                        credit_doc_id: this.searchPaymentData[i].id
                    });
                }
            }
            this.chakedPaymentData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.credit_doc_id) {
                        element.isSinglepaymentChecked = true;
                    }
                });
            });

            this.ispaymentChecked = true;
        }
        if (event.checked == false) {
            let checkedData = this.searchPaymentData;
            this.chakedPaymentData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.credit_doc_id) {
                        element.isSinglepaymentChecked = false;
                    }
                });
            });
            this.chakedPaymentData = [];
            this.ispaymentChecked = false;
            this.allIsChecked = false;
        }
        this.totalCheckedPayments.totalSelPayments = this.chakedPaymentData.length;
        const commonObjects = this.searchPaymentData.filter(obj1 =>
            this.chakedPaymentData.some(obj2 => obj1.id === obj2.credit_doc_id)
        );
        this.totalCheckedPayments.totalAmount = 0;
        commonObjects.forEach(item => {
            this.totalCheckedPayments.totalAmount += item.amount;
        });
    }

    addbatchChecked(id, event) {
        if (event.checked) {
            this.searchPaymentData.forEach((value, i) => {
                if (value.id == id) {
                    this.chakedPaymentData.push({
                        credit_doc_id: value.id
                    });
                }
            });

            if (this.searchPaymentData.length === this.chakedPaymentData.length) {
                this.ispaymentChecked = true;
                this.allIsChecked = true;
            }
        } else {
            let checkedData = this.searchPaymentData;
            checkedData.forEach(element => {
                if (element.id == id) {
                    element.isSinglepaymentChecked = false;
                }
            });
            this.chakedPaymentData.forEach((value, index) => {
                if (value.credit_doc_id == id) {
                    this.chakedPaymentData.splice(index, 1);
                }
            });

            if (
                this.chakedPaymentData.length == 0 ||
                this.chakedPaymentData.length !== this.searchPaymentData.length
            ) {
                this.ispaymentChecked = false;
            }
        }
        this.totalCheckedPayments.totalSelPayments = this.chakedPaymentData.length;
        const commonObjects = this.searchPaymentData.filter(obj1 =>
            this.chakedPaymentData.some(obj2 => obj1.id === obj2.credit_doc_id)
        );
        this.totalCheckedPayments.totalAmount = 0;
        commonObjects.forEach(item => {
            this.totalCheckedPayments.totalAmount += item.amount;
        });
    }

    openPaymentWorkFlow(id, auditcustid) {
        this.ifModelIsShow = true;

        this.dialog.open(WorkflowAuditDetailsModalComponent, {
            width: '80%', disableClose: true,
            data: {
                auditcustid: auditcustid,
                checkHierachy: "PAYMENT",
                planId: ""
            }
        });
    }

    paymentModal: boolean = false;
    openPaymentModal(id) {
        this.paymentModal = true;
        this.dialog.open(this.chequeDetailsDialog, {
            width: '80%',
            disableClose: true
        });
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

    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentCustomerListdatatotalRecords: any;
    selectedParentCust: any = [];
    selectedParentCustId: any;
    parentCustList: any;
    newFirst = 1;
    searchParentCustOption = "";
    searchParentCustValue = "";
    parentFieldEnable = false;
    customerList = [];

    currentPage = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: number;
    searchOptionSelect = this.commondropdownService.customerSearchOption;

    getParentCustomerData() {
        let currentPage;
        currentPage = this.currentPageParentCustomerListdata;
        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const url = "/customers/list";
        this.searchPaymentService.postMethod(url, data).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                this.newFirst = 1;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    selectParentCustomer: boolean = false;
    async modalOpenParentCustomer() {
        this.selectParentCustomer = true;
        this.dialog.open(this.selectParentCustomerDialog, {
            width: '80%',
            disableClose: true
        });
        await this.getParentCustomerData();
        this.newFirst = 0;
        this.selectedParentCust = [];
    }


    removeSelParentCust() {
        this.parentCustList = [];

        this.customerid = null;
    }

    modalCloseParentCustomer() {
        this.selectParentCustomer = false;
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 1;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.customerList = [];
    }
    async saveSelCustomer(dialogRef) {
        this.parentCustList = [
            {
                id: Number(this.selectedParentCust.id),
                name: this.selectedParentCust.name
            }
        ];

        this.customerid = Number(this.selectedParentCust.id);
        dialogRef.close()
        this.modalCloseParentCustomer();
    }


    paginate(pageNumber) {
        this.currentPageParentCustomerListdata = pageNumber.pageIndex + 1;
        this.parentCustomerListdataitemsPerPage = pageNumber.pageSize;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData();
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    searchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
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

        const url = "/subscriber/getByInvoiceType/search/Group";
        this.searchPaymentService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 400 || error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    selParentSearchOption(event) {
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    staffData = [];
    staffCustList: any = [];
    selectApproveList: any = [];
    selectedStaffCust: any = [];
    selectedApprove: any = [];
    parentstaffListdatatotalRecords: any;
    parentStaffListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPageParentStaffListdata = 1;
    getStaff() {
        let currentPage;
        currentPage = this.currentPageParentStaffListdata;
        const data = {
            page: currentPage,
            pageSize: this.parentStaffListdataitemsPerPage
        };
        const url = "/staffuser/list?product=BSS";
        this.savbillCommonBaseService.post(url, data).subscribe(
            (response: any) => {
                this.staffData = response.staffUserlist;
                this.parentstaffListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    selectStaffModal: boolean = false;
    async modalOpenStaff() {
        this.selectStaffModal = true;
        this.dialog.open(this.selectStaffDialog, {
            width: '80%',
            disableClose: true
        });
        await this.getStaff();
        this.newFirst = 1;
        this.selectedStaffCust = [];
    }

    removeSelStaff() {
        this.staffCustList = [];
        if (this.ifBatchList) {
            this.batchStaffid = "";
        } else {
            this.staffid = "";
        }
    }

    removeSelAssigned() {
        this.selectApproveList = [];
        this.approveId = "";
    }

    modalCloseStaff() {
        this.selectStaffModal = false;
        this.currentPageParentStaffListdata = 1;
        this.newFirst = 1;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.customerList = [];
    }

    async saveSelstaff(dialogRef) {
        this.staffCustList = [
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
    paginateStaff(pageNumber) {
        this.currentPageParentStaffListdata = pageNumber.pageIndex + 1;
        this.parentStaffListdataitemsPerPage = pageNumber.pageSize;
        if (this.searchParentCustValue) {
            this.searchStaffByName();
        } else {
            this.getStaff();
        }
    }
    clearSearchForm() {
        this.searchDeatil = "";
        this.searchOption = "";
    }

    searchStaffByName() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage == 1) {
            this.itemsPerPage = this.pageITEM;
        } else {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchDeatil.trim();
        this.staffService.staffSearch(this.searchData).subscribe(
            (response: any) => {
                this.staffData = response.dataList;
                this.totalRecords = response.totalRecords;
            },
            (error: any) => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.staffData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    pickModalOpen(data) {
        let url = "/workflow/pickupworkflow?eventName=PAYMENT&entityId=" + data.id;
        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
                this.searchPayment("");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    teams: any[];
    teamToggle: boolean = false;
    teamselected: any;
    staffDataList: any;
    staffselected: any;
    getAllTeams() {
        let url = "/teams/getAllFinanceTeam";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.teams = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    selectedTeam(dialogRef) {
        this.staffDataList = [];
        this.teamToggle = true;
        const staffName = this.teamselected.staffNameList;
        const staffId = this.teamselected.staffUserIds;
        staffId.forEach((e: any, i: any) => {
            this.staffDataList.push({ id: e, name: staffName[i] });
        });

    }

    onRadioChange(event) {
        this.teamselected = event.value
    }

    tdsAmount(data) {
        let total = 0;
        for (let datas of data.creditDocumentList) {
            total += datas.tdsAmount;
        }
        return total;
    }
    abbsAmount(data) {
        let total = 0;
        for (let datas of data.creditDocumentList) {
            total += datas.abbsAmount;
        }
        return total;
    }
    reassignWorkflow(dialogRef) {
        let url: any;
        this.remark = this.assignPaymentForm.value.remark;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.paymentIdforAssigned}&eventName=PAYMENT&assignToStaffId=${this.selectStaff}&remark=${this.remark}`;

        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                dialogRef.close()
                this.batchPaymentDetailsList("");

                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else {
                    this.searchPayment("");

                    this.reasignpayment = false;
                    this.toastr.success(`Assigned to the next staff successfully`, 'Success!');
                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                this.reasignpayment = false;
            }
        );
    }
    paymentIdforAssigned: any;
    reasignpayment: boolean = false;
    StaffReasignList(id) {
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${id}&eventName=PAYMENT`;
        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                this.paymentIdforAssigned = id;
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                }

                if (response.dataList != null) {
                    this.staffDataList = response.dataList;
                    this.approved = true;

                    this.reasignpayment = true;
                    this.dialog.open(this.approveCustomerDialog, {
                        width: '80%',
                        disableClose: true
                    });
                } else {
                    this.reasignpayment = false;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    selectApproveModal: boolean = false;
    async modalOpenApprove() {
        this.selectApproveModal = true;
        this.dialog.open(this.selectApproveDialog, {
            width: '80%',
            disableClose: true
        });
        await this.getStaff();
        this.newFirst = 1;
        this.selectedApprove = [];
    }

    modalCloseApprove() {
        this.selectApproveModal = false;
        this.currentPageParentStaffListdata = 1;
        this.newFirst = 1;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.customerList = [];
    }
    async saveSelstaffApprove(dialogRef) {
        this.selectApproveList = [
            {
                id: Number(this.selectedApprove.id),
                name: this.selectedApprove.firstname
            }
        ];
        this.approveId = Number(this.selectedApprove.id);
        dialogRef.close()
        this.modalCloseApprove();
    }
    getBankDestinationDetail() {
        const url = "/bankManagement/searchByStatus?banktype=operator";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDestination = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    getBankDetail() {
        const url = "/bankManagement/searchByStatus?banktype=other";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    checkUncheckAllInvoice() {
        for (let i = 0; i < this.batchMappingData.length; i++) {
            this.batchMappingData[i].isSelected = this.masterSelected;
        }
        this.getCheckedItemListInvoice();
    }

    isAllSelectedInvoice() {
        this.masterSelected = this.batchMappingData.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemListInvoice();
    }

    getCheckedItemListInvoice() {
        this.checkedList = [];
        for (let i = 0; i < this.batchMappingData.length; i++) {
            if (this.batchMappingData[i].isSelected) {
                this.checkedList.push(this.batchMappingData[i]);
                if (this.selectedInvoiceIdList.indexOf(this.batchMappingData[i]) === -1) {
                    this.selectedInvoiceIdList.push(this.batchMappingData[i]);
                }
            } else {
                let isElementAlreadyExist = this.selectedInvoiceIdList.find(
                    obj => obj.id === this.batchMappingData[i].id
                );
                if (
                    isElementAlreadyExist != undefined &&
                    isElementAlreadyExist &&
                    !this.batchMappingData[i].isSelected
                ) {
                    const index: number = this.selectedInvoiceIdList.findIndex(
                        obj => obj.id === this.batchMappingData[i].id
                    );
                    this.selectedInvoiceIdList.splice(index, 1);
                }
            }
        }
    }

    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    updateBatch(dialogRef) {
        var request = [];
        this.checkedList.forEach(invoice => {
            let data = {
                amount: invoice.amount,
                creditDocId: invoice.creditDocumentId
            };
            request.push(data);
        });
        this.masterSelected = false;
        const url = "/paymentGateway/editbatchpayment";
        this.searchPaymentService.postMethod(url, request).subscribe(
            (response: any) => {
                dialogRef.close()
                this.closeBatchDetailsDialog();
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    downloadInvoice(docId, custId, fileName) {

        const url = "/documentForInvoice/download/" + docId + "/" + custId;
        this.revenueManagementService.downloadInvoice(url).subscribe(
            (response: any) => {
                var fileType = "";
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

    checkPendingStatus(assignmentStatus, batchStatus) {
        var pendingStatusValue = batchStatus;
        if (assignmentStatus.toLowerCase() == "assignedtootherteam") pendingStatusValue = "Submitted";
        else if (assignmentStatus.toLowerCase() == "pending") pendingStatusValue = "Collected";
        return pendingStatusValue;
    }

    downloadFile(filename, docid, custId) {

        const url = "/documentForInvoice/download/" + docid + "/" + custId;
        this.revenueManagementService.downloadInvoice(url).subscribe(
            (response: any) => {
                var fileType = "";
                var file = new Blob([response], { type: "application/pdf" });
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, filename);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    approveRejectModelClose() {
        this.ApproveRejectModal = false;
    }

    SearchOnlinePaymentAudit() {
        this.ifPaymentList = false;
        this.ifOnlinePaymentAuditList = true;
        this.ifBatchList = false;
        this.ifAddbatchData = false;
        this.ispaymentChecked = false;
        this.allIsChecked = false;
        this.isSinglepaymentChecked = false;
        this.chakedPaymentData = [];
        this.batchId = "";
        this.BatchName = "";
        this.newBatchName = "";
        this.batchSingleData = [];
        this.searchOnlineAuditPayment(false);
    }
    manualSearchAudit() {
        this.auditPanelExpanded = false;
        this.searchOnlineAuditPayment(true);
    }
    searchOnlineAuditPayment(isSerach) {
        let data;
        data = {
            page: this.currentPageOnlinePaymentAudit,
            pageSize: this.onlinePaymentAudititemsPerPage,
            sortBy: "id",
            filters: [
                {
                    filterValue: this.selectedAuditSearchValue,
                    filterColumn: "any",
                    filterCondition: "and",
                    filterDataType: this.selectedAuditSearchOption,
                    filterOperator: "equalto",
                    fromDate: "",
                    toDate: ""
                }
            ]
        };

        if (
            this.selectedAuditSearchOption !== "transactionDate"
        ) {
            this.searchkey = this.selectedAuditSearchValue.trim();
            data.filters[0].filterValue = this.selectedAuditSearchValue.trim();
            data.filters[0].filterDataType = this.selectedAuditSearchOption.trim();
        } else {

            let searchDeatil = this.datePipe.transform(this.selectedAuditSearchValue, "yyyy-MM-dd");
            this.searchkey = searchDeatil;

            data.filters[0].filterValue = searchDeatil;
            data.filters[0].filterDataType = this.selectedAuditSearchOption;
        }
        data.filters[0].fromDate = this.datePipe.transform(this.searchFromDate, "yyyy-MM-dd");
        data.filters[0].toDate = this.datePipe.transform(this.searchTodate, "yyyy-MM-dd");
        let url;
        url = "/onlinePayAudit/all";

        this.customerManagementService.postMethodForIntegration(url, data).subscribe(
            (response: any) => {
                this.onlinePaymentAuditList = response.onlineAuditData;
                this.onlinePaymentAuditotalRecords = response.totalRecords;
                this.toastr.success(`Records fetched successfully`, 'Success!');
            },
            (error: any) => {
                if (error.error.status == "Failed") {
                    this.toastr.info(`${error.error.message}`, 'Info!');
                } else {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    retryPayment(orderId) {
        this.paymentData = [];
        const url = "/ByOrderId?orderId=" + orderId;
        this.customerManagementService.getMethodForIntegration(url).subscribe(
            (response: any) => {
                this.searchOnlineAuditPayment(false);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeBatchDetailsDialog() {
        this.batchMapping = false;
    }

    closeAssignCustomerModel() {
        this.assignCustomerCAFModal = false;
    }

    closeReassignModel() {
        this.reasignpayment = false;
    }

    closePaymentModal() {
        this.paymentModal = false;
    }

    addToWallet(orderId: number) {
        this.transModal = true;
        this.dialog.open(this.addTransactionDialogTemplate, {
            width: '80%',
            disableClose: true
        });
        this.addToWalletOrderId = orderId;
    }
    openFailureReason(data: any) {
        this.dialog.open(this.failureReasonDialogTemplate, {
            width: '80%',
            disableClose: true
        });
        this.selectedFailureDescription = data.failureDescription;
        this.failureReasonDialog = true;
    }

    closeFailureReason() {
        this.failureReasonDialog = false;
        this.selectedFailureDescription = "";
    }

    addToWalletAPI(dialogRef) {
        const url = "/addToWalletByOrderId?orderId=" + this.addToWalletOrderId + "&transactionId=" + this.transactionNo;
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
                dialogRef.close()
                this.customerData = response.customerList;
                this.toastr.success(`${response?.data}`, 'Success!');
                this.transModal = false;
                this.addToWalletOrderId = '';
                this.transactionNo = '';
                this.searchOnlineAuditPayment(false);
            },
            (error: any) => {
                console.error("Error:", error);
                this.toastr.error(`${error?.error?.ERROR}`, 'Failed!');
            }
        );
    }

    ConfirmonTransactionNumber(dialogRef) {
        if (this.addToWalletOrderId) {
            this.confirmationService.confirm({
                message: "Do you want to confirm this transaction no?",
                header: "Transaction No Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.addToWalletAPI(dialogRef);
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });
        }
    }

    transactionModal() {
        this.transModal = false;
        this.addToWalletOrderId = "";
        this.transactionNo = "";
    }

    async exportExcel() {
        let searchFromDate = this.searchFromDate
            ? this.datePipe.transform(this.searchFromDate, "yyyy-MM-dd")
            : null;
        let searchTodate = this.searchTodate
            ? this.datePipe.transform(this.searchTodate, "yyyy-MM-dd")
            : null;
        let selectedAuditSearchValue;
        if (this.selectedAuditSearchOption === 'transactionDate') {
            selectedAuditSearchValue = this.selectedAuditSearchValue
                ? this.datePipe.transform(this.selectedAuditSearchValue, "yyyy-MM-dd")
                : null;
        } else {
            selectedAuditSearchValue = this.selectedAuditSearchValue;
        }
        let obj =
            [{
                filterColumn: "any",
                filterDataType: this.selectedAuditSearchOption,
                filterValue: selectedAuditSearchValue,
                fromDate: searchFromDate,
                toDate: searchTodate
            }]

        this.recordPaymentService.getDataTOExport(obj).subscribe(
            (res: any) => {
                if (res.status == 404) {
                    this.toastr.info(`${res.msg}`, 'Info!');
                } else {
                    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(res.dataToExport);
                    const wb: XLSX.WorkBook = XLSX.utils.book_new();
                    XLSX.utils.book_append_sheet(wb, ws, "PaymentAudit");
                    const fileName = `Payment audit${searchFromDate ? " " + searchFromDate : ""}${searchTodate ? " to " + searchTodate : ""}.xlsx`;
                    XLSX.writeFile(wb, fileName);
                }
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                } else {
                    this.toastr.error(`${error?.error?.ERROR}`, 'Failed!');
                }
            }
        );
    }
    searchStaffName() {
        if (this.searchStaffDeatil) {
            this.approveCAF = this.approveCAFData.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(this.searchStaffDeatil.toLowerCase()) ||
                    staff.username.toLowerCase().includes(this.searchStaffDeatil.toLowerCase())
            );
        } else {
            this.approveCAF = this.approveCAFData;
        }
    }

    clearSearch() {
        this.searchStaffDeatil = "";
        this.approveCAF = this.approveCAFData;
    }

    activeTabIndex = 1;

    onTabChange(event: any) {
        this.panelTitle = event.tab.textLabel;
        this.searchPanelExpanded = true;
        this.batchPanelExpanded = true;
        this.auditPanelExpanded = true;
        switch (event.index) {
            case 0:
                if (this.batchPaymentAccess) {
                    this.batchList();
                } else {
                    this.SearchPayment();
                }
                break;
            case 1:
                if (this.batchPaymentAccess) {
                    this.SearchPayment();
                } else {
                    this.SearchOnlinePaymentAudit();
                }
                break;
            case 2:
                this.SearchOnlinePaymentAudit();
                break;
        }
    }


}
