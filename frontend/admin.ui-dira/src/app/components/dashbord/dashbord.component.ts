import { ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService } from "primeng/api";
import { ToastrService } from "ngx-toastr";
import { DashboardService } from "src/app/service/dashboard.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { LoginService } from "../../service/login.service";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { CustomerDocumentService } from "../customer-documents/customer-document.service";
import { DatePipe, formatDate } from "@angular/common";
import { LeadManagementService } from "src/app/service/lead-management-service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import moment from "moment";
import { DASHBOARDS } from "src/app/constants/aclConstants";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import { Router } from "@angular/router";
import { InwardService } from "./../../service/inward.service";
import { CustomerInventoryDetailsService } from "src/app/service/customer-inventory-details.service";
import { BehaviorSubject } from "rxjs/internal/BehaviorSubject";
import { CustomerInventoryManagementService } from "src/app/service/customer-inventory-management.service";
import { TaskManagementService } from "src/app/service/task-management.service";
import { CountryManagementService } from "src/app/service/country-management.service";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTabChangeEvent } from "@angular/material/tabs";
import { MatDialog } from "@angular/material/dialog";
import { error } from "console";
import { StaffService } from "src/app/service/staff.service";

declare var $: any;

export interface Country {
    name?: string;
    code?: string;
}

export interface Representative {
    name?: string;
    image?: string;
}

export interface Customer {
    id?: number;
    name?: string;
    country?: Country;
    company?: string;
    date?: string;
    status?: string;
    representative?: Representative;
    activity?: any;
    itemAssemblyName?: any;
    itemAssemblyId?: any;
    custInventoryListId?: any;
}
@Component({
    selector: "app-dashbord",
    templateUrl: "./dashbord.component.html",
    styleUrls: ["./dashbord.component.css"],
    standalone: false
})
export class DashbordComponent implements OnInit {
    //   public loginService: LoginService;
    dateTime = new Date();
    AclClassConstants;
    AclConstants;
    showCustomerGraphs: boolean;
    showPaymentGraphs: boolean;
    showTicketGraphs: boolean;
    radiusGraph: boolean;
    customerTypeWiseData: any;
    customerTypeWiseDataOptions: any;
    customerStatusWiseData: any;
    newCustomerTypeWiseData: any;
    newCustomerPlanWiseData: any;
    partnerwisePayment: any;
    paymentMonthWiseData: any;
    pendingData: any = {};
    nextReceiveable: any;
    monthwiseTicketCount: any;
    monthwiseTicketCountOptions: any;
    staffwiseTicketCount: any;
    teamwiseTicketCount: any;
    nextTenDaysRenewableCustomerArray = [];
    date10 = new Date().getFullYear();
    totalOpenTicket: string;
    connecteduser: string;
    monthWisevolumeUsages: any;
    monthWisetimeUsages: any;
    date101 = new Date().getFullYear();
    currency: string;
    showLoader: boolean = true;
    showTicketLoader: boolean = true;
    overDueticketList = [];
    commissionGraph: boolean;
    monthWiseAGRDetails: {
        labels: string[];
        datasets: {
            label: string;
            data: unknown[];
            backgroundColor: string;
            hoverBackgroundColor: string;
        }[];
    };
    monthWiseTDSDetails: any;
    partnerWiseTDS: any;
    stackedData: {
        labels: string[];
        datasets: { type: string; label: string; backgroundColor: string; data: number[] }[];
    };
    stackedOptions: {
        tooltips: { mode: string; intersect: boolean };
        responsive: boolean;
        scales: { xAxes: { stacked: boolean }[]; yAxes: { stacked: boolean }[] };
    };
    topFivePartnerCommissionWiseData: any;
    inventoryGraph: boolean;
    staffAndProductWiseInventoryList: any[];
    wareHouseAndProductWiseInventory: any[];
    inventoryAlertList: any[];
    availableInventoryProductWise: any[];
    showInventoryLoader: boolean;
    showApprovalData: boolean;
    showSalseData: boolean;
    showProductQtyData: boolean;
    currentPagecustomerListdata = 1;
    customerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerListFollowUpDataItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerListdatatotalRecords: any;
    customerListFollowUpDataTotalRecords: any;
    customerListData: any = new MatTableDataSource<any>([]);
    customerListFollowUPData: any = [];
    customerListFollowUpDataselector: any;
    customerListFollowUpDatalength: any;
    currentPagecustomerListFollowUpData = 1;
    salseDetatilListData: any = [];
    childStaffData: any = [];
    childStaffDataSelector: any = [];
    customerListDataselector: any;
    salseDetatilListDataSelector: any = [];
    currentPagespecialPlanMappingdata = 1;
    specialPlanMappingdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    specialPlanMappingdatatotalRecords: any;
    specialPlanMappingData: any = [];
    specialPlanMappingDataselector: any;
    specialPlanMappingListDatalength = 0;
    pageLimitOptionsForMapping = RadiusConstants.pageLimitOptions;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPage = 1;
    customerListDatalength = 0;
    currentPagePlanListdata = 1;
    planListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    planListdatatotalRecords: any;
    planListData: any = [];
    planListDataselector: any;
    pageLimitOptionsForPlan = RadiusConstants.pageLimitOptions;
    pageITEMForPlan = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageForPlan = 1;
    planDatalength = 0;
    currentPagePlanGroupListdata = 1;
    planGroupListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    planGroupListdatatotalRecords: any;
    planGroupListData: any = [];
    planGroupListDataselector: any;
    pageLimitOptionsForPlanGroup = RadiusConstants.pageLimitOptions;
    pageITEMForPlanGroup = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageForPlanGroup = 1;
    planGroupDatalength = 0;
    currentPagePaymentListdata = 1;
    paymentListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymentListdatatotalRecords: any;
    paymentListData: any = [];
    paymentListDataselector: any;
    pageLimitOptionsForPayment = RadiusConstants.pageLimitOptions;
    pageITEMForPayment = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageForPayment = 1;
    paymentDatalength = 0;
    currentPagecustomerTerminationListdata = 1;
    customerTerminationListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerTerminationListdatatotalRecords: any;
    customerTerminationListData: any = [];
    customerTerminationListDataselector: any;
    pageLimitOptionsTermination = RadiusConstants.pageLimitOptions;
    pageITEMTermination = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageTermination = 1;
    customerTerminationListDatalength = 0;
    currentPageCaseListdata = 1;
    caseListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    caseListdatatotalRecords: any;
    caseListData: any = [];
    caseListDataselector: any;
    pageLimitOptionsCase = RadiusConstants.pageLimitOptions;
    pageITEMCase = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageCase = 1;
    caseListDatalength = 0;
    currentPageChangeDiscountListdata = 1;
    changeDiscountListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    changeDiscountListdatatotalRecords: any;
    changeDiscountListData: any = [];
    changeDiscountListDataselector: any;
    pageLimitOptionsChangeDiscount = RadiusConstants.pageLimitOptions;
    pageITEMChangeDiscount = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageChangeDiscount = 1;
    changeDiscountListDatalength = 0;
    currentPageInvoiceListdata = 1;
    currentPageProductQtyByStaffdata = 1;
    currentPageProductQtyByWarehousedata = 1;
    invoiceListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productQtyListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productQtyListdataitemsbywarehousePerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoiceListdatatotalRecords: any;
    productQtytotalRecords: any;
    productQtyByWarehousetotalRecords: any;
    invoiceListData: any = [];
    productQTy: any = [];
    productQtyByWarehouse: any = [];
    invoiceListDataselector: any;
    pageLimitOptionsInvoice = RadiusConstants.pageLimitOptions;
    pageITEMInvoice = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageInvoice = 1;
    showItemPerPageProductQty = 1;
    showItemPerPageProducyQtyByWarehouse = 1;
    invoiceListDatalength = 0;
    productListDatalength = 0;
    currentPagePartnerPaymentListdata = 1;
    partnerPaymentListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    partnerPaymentListdatatotalRecords: any;
    partnerPaymentListData: any = [];
    partnerPaymentListDataselector: any;
    pageLimitOptionsPartnerPayment = RadiusConstants.pageLimitOptions;
    pageITEMPartnerPayment = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPagePartnerPayment = 1;
    partnerPaymentListDatalength = 0;
    showFollowUpPendingData: boolean = false;

    searchkey: string;

    assignCustomerCAFForm: UntypedFormGroup;
    rejectCustomerCAFForm: UntypedFormGroup;
    assignAppRejectDiscountForm: UntypedFormGroup;

    approveId: any;
    workflowID: number;
    reject = false;
    rejectCAF = [];
    selectStaffReject: any;
    approved = false;
    approveCAF = [];
    selectStaff: any;
    assignCustomerCAFsubmitted = false;
    rejectCustomerCAFsubmitted = false;
    assignCustomerCAFId: any;
    nextApproverId: any;

    selectedFileUploadPreview: any[] = [];

    approve = false;
    staffList: any = [];
    assignStaffForm: UntypedFormGroup;
    allIsChecked = false;
    productPlanMappingId: any;
    currentPageCustomerDocListdata = 1;
    currentPageInventoryPendingListdata = 1;
    customerDocListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    inventoryPendingListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerDocListdatatotalRecords: any;
    inventoryPendingListdatatotalRecords: any;
    customerDocListData: any = [];
    inventoryPendingListData: any = [];
    customerDocListDataselector: any;
    pageLimitOptionsCustomerDoc = RadiusConstants.pageLimitOptions;
    pageITEMCustomerDoc = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageCustomerDoc = 1;
    showItemPerPageInventoryPending = 1;
    customerDocListDatalength = 0;
    inventoryPendingListDatalength = 0;
    inventoryData = new BehaviorSubject({
        inventoryData: ""
    });
    currentPageLeadListdata = 1;
    leadListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    leadListdatatotalRecords: any;
    leadListData: any = [];
    leadListDataselector: any;
    pageLimitOptionsLead = RadiusConstants.pageLimitOptions;
    pageITEMLead = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageLead = 1;
    leadListDatalength = 0;
    viewAccess: any;
    staffid: any;
    loggedInUser: any;
    mvnoid: any;
    leadDashboardView: boolean = true;
    leadListFlag: boolean = true;
    leadFollowupFlag: boolean = false;
    leadListForUserAndTeamFlag: boolean = false;

    assignedLeadListPageData = RadiusConstants.ITEMS_PER_PAGE;
    currentPageAssignedLeadList = 1;
    assignedLeadListdatatotalRecords: any;
    leadApproveRejectDto: any = {
        approveRequest: true,
        buId: null,
        currentLoggedInStaffId: 0,
        firstname: "",
        id: 0,
        mvnoId: 0,
        remark: "",
        serviceareaid: null,
        flag: "",
        nextTeamMappingId: null,
        status: "",
        teamName: "",
        username: ""
    };

    leadApproveRejectForm: UntypedFormGroup;
    leadApproveRejectFormsubmitted: boolean = false;

    closeFollowupForm: UntypedFormGroup;
    closeFollowupFormsubmitted: boolean = false;

    remarkFollowupForm: UntypedFormGroup;
    remarkFollowupFormsubmitted: boolean = false;
    isShowConnection = true;
    reFollowupScheduleForm: UntypedFormGroup;
    reFollowupFormsubmitted: boolean = false;
    inventoryAccess: boolean = false;
    salseCrmAccess: boolean = false;
    pendingApprovalAccess: boolean = false;
    salseDashboardAccess: boolean = false;
    customerPendingForApprovals: boolean = false;
    customerPendingForTerminationApprovals: boolean = false;
    plansPendingForApprovals: boolean = false;
    planGroupPendingForApprovals: boolean = false;
    paymentPendingForApprovals: boolean = false;
    ticketPendingForApprovals: boolean = false;
    changeDiscountPendingForApprovals: boolean = false;
    invoicesPendingForApprovals: boolean = false;
    partnerPaymentForApprovals: boolean = false;
    customerDocumentForApprovals: boolean = false;
    specialPlanForApprovals: boolean = false;
    productQuantityOfStaff: boolean = false;
    productQuantityByWarehouse: boolean = false;
    assignedLeadList: boolean = false;
    teamLeadApprovalList: boolean = false;
    leadFollowupList: boolean = false;
    teamLeadFollowupList: boolean = false;
    inventoryPendingForApprovals: boolean = false;
    selectAssignInventoryApproveStaff: any;
    approveAssignInventoryData = [];
    assignInventoryId: any;
    customerInventoryId: any;
    rejectAssignInventorySubmitted: boolean = false;
    assignAssignInventorysubmitted: boolean = false;
    assignedInventoryList: any = [];
    approveAssignInventoryForm: UntypedFormGroup;
    rejectAssignInventoryForm: UntypedFormGroup;
    selectAssignInventoryRejectStaff: any;
    rejectAssignInventoryData = [];
    rowGroupMetadata: any = {};
    customerId: number = 0;
    rejectRemove = false;
    selectRemoveInventoryRejectStaff: any;
    rejectRemoveInventoryData = [];
    approveRemove = false;
    selectRemoveInventoryApproveStaff: any;
    approveRemoveInventoryForm: UntypedFormGroup;
    approveRemoveInventoryData = [];
    assignRemoveInventoryId: any;
    custInventoryId: any;
    macMappingId: any;
    rejectRemoveInventoryForm: UntypedFormGroup;
    rejectRemoveInventorySubmitted: boolean = false;
    assignRemoveInventorysubmitted: boolean = false;
    staffUserId: any;
    custData: any = {};
    public userName: string = '';
    public userEmailId: string = '';

    currentPageTaskListData = 1;
    taskListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    // taskListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    taskListData: any = [];
    taskListSelector: any;
    taskListTotalRecords: any;
    showItemPerPageTask = 1;
    taskListLength = 0;
    selectedFilter: any;
    startdate!: any;
    enddate!: any;
    pageSizeOptions = RadiusConstants.PAGE_SIZE_OPTIONS;

    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild("assignCustomerCAFModal") assignCustomerCAFModal: TemplateRef<any>;
    @ViewChild("rejectCustomerCAFModal") rejectCustomerCAFModal: TemplateRef<any>;
    @ViewChild("assignApproveOtherInventoryOpen") assignApproveOtherInventoryOpen: TemplateRef<any>;
    @ViewChild("approveOrRejectLeadPopupRef") approveOrRejectLeadPopupRef: TemplateRef<any>;
    @ViewChild("reScheduleFollowupModal") reScheduleFollowupModal: TemplateRef<any>;
    @ViewChild("closeFollowupModal") closeFollowupModal: TemplateRef<any>;
    @ViewChild("assignCustomerDocumentForApproval")
    assignCustomerDocumentForApproval: TemplateRef<any>;
    @ViewChild("ApproveRejectRemarkModal")
    ApproveRejectRemarkModal: TemplateRef<any>;
    @ViewChild("assignCustomerInventoryModal") assignCustomerInventoryModal: TemplateRef<any>;
    @ViewChild("rejectCustomerInventoryModal") rejectCustomerInventoryModal: TemplateRef<any>;
    @ViewChild("rejectApproveDiscountModal") rejectApproveDiscountModal: TemplateRef<any>;
    @ViewChild("ApproveRejectModal") ApproveRejectModal: TemplateRef<any>;
    @ViewChild("assignRejectOtherInventoryOpen") assignRejectOtherInventoryOpen: TemplateRef<any>;
    @ViewChild("rescheduleFollowUpsModal") rescheduleFollowUpsModal: TemplateRef<any>;
    @ViewChild("closeFollowUpsModal") closeFollowUpsModal: TemplateRef<any>;
    @ViewChild("remarkScheduleFollowupModal") remarkScheduleFollowupModal: TemplateRef<any>;
    userId: string;

    constructor(
        private fb: UntypedFormBuilder,
        private dashboardService: DashboardService,
        private inwardService: InwardService,
        private messageService: MessageService,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private configService: SystemconfigService,
        private customerDocumentService: CustomerDocumentService,
        private leadManagementService: LeadManagementService,
        private customerManagementService: CustomermanagementService,
        private ticketManagementService: TicketManagementService,
        private taskManagementService: TaskManagementService,
        public loginService: LoginService,
        public statusCheckService: StatusCheckService,
        public datePipe: DatePipe,
        private router: Router,
        public CustomerInventoryDetailsService: CustomerInventoryDetailsService,
        private customerInventoryManagementService: CustomerInventoryManagementService,
        private gateWayCommonService: CountryManagementService,
        public dialog: MatDialog,
        private cd: ChangeDetectorRef,
        private staffService: StaffService
    ) {
        this.staffid = Number(localStorage.getItem("userId"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoid = Number(localStorage.getItem("mvnoId"));
        this.selectedFilter = Number(localStorage.getItem("userId"));

        this.pendingApprovalAccess = loginService.hasPermission(DASHBOARDS.DASHBOARD_APPROVAL);
        this.salseDashboardAccess = loginService.hasPermission(DASHBOARDS.SALSE_DARSHBOARD); //change this after acl
        this.inventoryAccess = loginService.hasPermission(DASHBOARDS.DASHBOARD_INVENTORY);
        this.salseCrmAccess = loginService.hasPermission(DASHBOARDS.DASHBOARD_SALES_CRM);
        this.pendingApprovalAccess = loginService.hasPermission(DASHBOARDS.DASHBOARD_APPROVAL);
        this.salseDashboardAccess = loginService.hasPermission(DASHBOARDS.SALSE_DARSHBOARD); //change this after acl
        this.inventoryAccess = loginService.hasPermission(DASHBOARDS.DASHBOARD_INVENTORY);
        this.salseCrmAccess = loginService.hasPermission(DASHBOARDS.DASHBOARD_SALES_CRM);

        this.customerPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.CUSTOMER_PENDING_FOR_APPROVALS
        );
        this.customerPendingForTerminationApprovals = loginService.hasPermission(
            DASHBOARDS.CUSTOMER_PENDING_FOR_TERMINATION_APPROVALS
        );
        this.plansPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.PLANS_PENDING_FOR_APPROVALS
        );
        this.planGroupPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.PLAN_GROUP_PENDING_FOR_APPROVALS
        );
        this.paymentPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.PAYMENT_PENDING_FOR_APPROVALS
        );
        this.ticketPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.TICKET_PENDING_FOR_APPROVALS
        );
        this.changeDiscountPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.CHANGE_DISCOUNT_PENDING_FOR_APPROVALS
        );
        this.invoicesPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.INVOICES_PENDING_FOR_APPROVALS
        );
        this.partnerPaymentForApprovals = loginService.hasPermission(
            DASHBOARDS.PARTNER_PAYMENT_FOR_APPROVALS
        );
        this.customerDocumentForApprovals = loginService.hasPermission(
            DASHBOARDS.CUSTOMER_DOCUMENT_FOR_APPROVALS
        );
        this.inventoryPendingForApprovals = loginService.hasPermission(
            DASHBOARDS.INVENTORY_PENDING_FOR_APPROVALS
        );
        this.specialPlanForApprovals = loginService.hasPermission(
            DASHBOARDS.SPECIAL_PLAN_FOR_APPROVALS
        );

        this.productQuantityOfStaff = loginService.hasPermission(DASHBOARDS.PRODUCT_QUANTITY_OF_STAFF);
        this.productQuantityByWarehouse = loginService.hasPermission(
            DASHBOARDS.PRODUCT_QUANTITY_BY_WAREHOUSE
        );
        this.assignedLeadList = loginService.hasPermission(DASHBOARDS.ASSIGNED_LEAD_LIST);
        this.teamLeadApprovalList = loginService.hasPermission(DASHBOARDS.TEAM_LEAD_APPROVAL_LIST);
        this.leadFollowupList = loginService.hasPermission(DASHBOARDS.LEAD_FOLLOWUP_LIST);
        this.teamLeadFollowupList = loginService.hasPermission(DASHBOARDS.TEAM_LEAD_FOLLOWUP_LIST);

        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.configService.getConfigurationByName("CURRENCY_SYMBOL").subscribe((res: any) => {
            this.currency = res.data?.value;
        });
        this.customerTypeWiseDataOptions = {
            scales: {
                yAxes: [
                    {
                        ticks: {
                            beginAtZero: true
                        }
                    }
                ]
            }
        };
        this.closeFollowupForm = this.fb.group({
            followUpId: [""],
            remarks: ["", Validators.required]
        });
    }

    ngOnInit(): void {
        // this.getCustomeGraph();
        this.userId = localStorage.getItem("userId");
        this.staffService.getStaffUserData(this.userId).subscribe((response: any) => {
            this.userName = response?.Staff.username || "User";
            this.userEmailId = response?.Staff?.email || "Email";
        });
        //     this.getApprovalData();
        //     this.viewInventoryDashboard();
        this.loadAllTabsData();
        this.getCustomerFollowUpPendingApprovals("");
        this.inventoryAccess = this.loginService.hasPermission(DASHBOARDS.DASHBOARD_INVENTORY);
        this.salseCrmAccess = this.loginService.hasPermission(DASHBOARDS.DASHBOARD_SALES_CRM);
        this.assignCustomerCAFForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectCustomerCAFForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.assignAppRejectDiscountForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.leadApproveRejectForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.reFollowupScheduleForm = this.fb.group({
            id: [""],
            followUpName: ["", Validators.required],
            followUpDatetime: ["", Validators.required],
            remarks: [""],
            isMissed: [true],
            leadMasterId: [],
            remarksTemp: ["", Validators.required]
        });
        // this.closeFollowupForm = this.fb.group({
        //   followUpId: [""],
        //   remarks: ["", Validators.required]
        // });
        this.approveAssignInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectAssignInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        // this.getCustomerAssignedList();
        this.approveRemoveInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectRemoveInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.remarkFollowupForm = this.fb.group({
            cafFollowUpId: [""],
            remark: ["", Validators.required]
        });

        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);

        this.startdate = yesterday;
        this.enddate = today;

        this.getSalseDashboardDataDetails();
    }

    ngAfterViewInit() {
        this.customerTerminationListData.paginator = this.paginator;
    }

    invenoryDetails: {
        oldId: string;
        oldSerialNumber: string;
        oldMacAddress: string;
        newId: string;
        newSerialNumber: string;
        newMacAddress: string;
        currentApproveId: string;
    }[] = [];

    getCustomeGraph() {
        this.showCustomerGraphs = true;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        if (this.statusCheckService.isActiveCMS) {
            this.getTypeWiseUserCountData();
            this.getStatusWiseUserCount();
            this.getNewlyActivatedCustomer();
            this.getPlanWiseCustomer();
        }
    }

    getPaymentGraph() {
        this.showCustomerGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.showPaymentGraphs = true;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        if (this.statusCheckService.isActiveCMS) {
            this.getMonthWiseCollection(new Date().getFullYear());
            this.pendingApprovalPayments();
            this.nextTenDaysReceivablePayment();
            this.partnerWisePayment();
            this.nextTenDaysRenewableCustomer();
        }
    }

    getTicketsGraph() {
        this.showCustomerGraphs = false;
        this.showPaymentGraphs = false;
        this.radiusGraph = false;
        this.showTicketGraphs = true;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        if (this.statusCheckService.isActiveCMS) {
            this.totalOpenTickets();
            this.monthWiseTicketCount(new Date().getFullYear());
            this.staffWiseTicketCount();
            this.teamWiseTicketCount();
            this.overDueTicketList();
        }
    }

    getRadiusGraph() {
        this.showCustomerGraphs = false;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = true;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        if (this.statusCheckService.isActiveCMS) {
            this.connectedUser();
            this.monthWiseVolumeUsages(new Date().getFullYear());
            this.monthWiseTimeUsages(new Date().getFullYear());
        }
    }

    getTypeWiseUserCountData() {
        this.dashboardService.getTypeWiseCustomerCount().subscribe(
            (res: any) => {
                if (res.data != null) {
                    this.customerTypeWiseData = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Number of customers",
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getStatusWiseUserCount() {
        this.dashboardService.getStatusWiseCount().subscribe(
            (res: any) => {
                if (res.data != null) {
                    this.customerStatusWiseData = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Number of customers",
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060",
                                data: Object.values(res.data)
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getNewlyActivatedCustomer() {
        this.dashboardService.getNewlyActivatedCustomer().subscribe(
            (res: any) => {
                if (res.data != null) {
                    this.newCustomerTypeWiseData = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: Object.keys(res.data),
                                backgroundColor: ["#4ea365", "#b3760c", "#b3360c", "#a50cb3", "#b30c3b"],
                                data: Object.values(res.data)
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getPlanWiseCustomer() {
        this.dashboardService.getPlanWiseCustomer().subscribe(
            (res: any) => {
                if (res.data != null) {
                    this.newCustomerPlanWiseData = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Number of customers",
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060",
                                data: Object.values(res.data)
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getMonthWiseCollection(year) {
        this.dashboardService.getMonthWiseCollection(year).subscribe(
            (res: any) => {
                this.paymentMonthWiseData = null;
                if (res.data != null) {
                    this.paymentMonthWiseData = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Amount in " + this.currency,
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060",
                                data: Object.values(res.data)
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    pendingApprovalPayments() {
        this.dashboardService.pendingApprovalPayments().subscribe(
            (res: any) => {
                if (res.data != null) {
                    this.pendingData = res.data;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    nextTenDaysReceivablePayment() {
        this.dashboardService.nextTenDaysReceivablePayment().subscribe(
            (res: any) => {
                this.nextReceiveable = res.data.data;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    monthWiseTicketCount(year) {
        this.dashboardService.monthWiseTicketCount(year).subscribe(
            (res: any) => {
                this.monthwiseTicketCount = null;
                let cerated;
                if (res.data != null) {
                    if (res.data["Created"] != null) {
                        cerated = res.data["Created"];
                        this.monthwiseTicketCount = {
                            labels: Object.keys(cerated),
                            datasets: [
                                {
                                    type: "bar",
                                    label: "Created",
                                    backgroundColor: "#606060",
                                    data: Object.values(cerated)
                                }
                            ]
                        };
                    }
                    if (res.data["Resolved"] != null) {
                        let resolved = res.data["Resolved"];
                        if (cerated) {
                            this.monthwiseTicketCount = {
                                labels: Object.keys(cerated),
                                datasets: [
                                    {
                                        type: "bar",
                                        label: "Created",
                                        backgroundColor: "#606060",
                                        data: Object.values(cerated)
                                    },
                                    {
                                        type: "bar",
                                        label: "Resolved",
                                        backgroundColor: "#4ea364",
                                        data: Object.values(resolved)
                                    }
                                ]
                            };
                        } else {
                            this.monthwiseTicketCount = {
                                labels: Object.keys(resolved),
                                datasets: [
                                    {
                                        type: "bar",
                                        label: "Resolved",
                                        backgroundColor: "#4ea364",
                                        data: Object.values(resolved)
                                    }
                                ]
                            };
                        }
                    }

                    this.monthwiseTicketCountOptions = {
                        tooltips: {
                            mode: "index",
                            intersect: false
                        },
                        responsive: true,
                        scales: {
                            xAxes: [
                                {
                                    stacked: true
                                }
                            ],
                            yAxes: [
                                {
                                    stacked: true
                                }
                            ]
                        }
                    };
                }

                // this.nextReceiveable = res.data.data;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    staffWiseTicketCount() {
        this.dashboardService.staffWiseTicketCount().subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    let resolved = [];
                    let assigned = [];
                    this.staffwiseTicketCount = null;
                    if (res.data["Resolved"] != null) {
                        resolved = res.data["Resolved"];
                        this.staffwiseTicketCount = {
                            labels: Object.keys(resolved),
                            datasets: [
                                {
                                    type: "bar",
                                    label: "Resolved",
                                    backgroundColor: "#4ea364",
                                    data: Object.values(resolved)
                                }
                            ]
                        };
                    }
                    if (res.data["Assigned"] != null) {
                        assigned = res.data["Assigned"];
                        if (resolved) {
                            this.staffwiseTicketCount = {
                                labels:
                                    Object.keys(resolved).length > 0 ? Object.keys(resolved) : Object.keys(assigned),
                                datasets: [
                                    {
                                        type: "bar",
                                        label: "Assigned",
                                        backgroundColor: "#606060",
                                        data: Object.values(assigned)
                                    },
                                    {
                                        type: "bar",
                                        label: "Resolved",
                                        backgroundColor: "#4ea364",
                                        data: Object.values(resolved)
                                    }
                                ]
                            };
                        } else {
                            this.staffwiseTicketCount = {
                                labels: Object.keys(assigned),
                                datasets: [
                                    {
                                        type: "bar",
                                        label: "Assigned",
                                        backgroundColor: "#606060",
                                        data: Object.values(assigned)
                                    }
                                ]
                            };
                        }
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    teamWiseTicketCount() {
        this.dashboardService.teamWiseTicketCount().subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    let resolved = [];
                    let assigned = [];
                    if (res.data["Resolved"] != null) {
                        resolved = res.data["Resolved"];
                        this.teamwiseTicketCount = {
                            labels: Object.keys(resolved),
                            datasets: [
                                {
                                    type: "bar",
                                    label: "Resolved",
                                    backgroundColor: "#4ea364",
                                    data: Object.values(resolved)
                                }
                            ]
                        };
                    }
                    if (res.data["Assigned"] != null) {
                        assigned = res.data["Assigned"];
                        if (resolved) {
                            this.teamwiseTicketCount = {
                                labels: Object.keys(assigned) ? Object.keys(assigned) : Object.keys(res),
                                datasets: [
                                    {
                                        type: "bar",
                                        label: "Resolved",
                                        backgroundColor: "#4ea364",
                                        data: Object.values(resolved) ? Object.values(resolved) : [0]
                                    },
                                    {
                                        type: "bar",
                                        label: "Assigned",
                                        backgroundColor: "#606060",
                                        data: Object.values(assigned) ? Object.values(assigned) : []
                                    }
                                ]
                            };
                        } else {
                            this.teamwiseTicketCount = {
                                labels: Object.keys(assigned),
                                datasets: [
                                    {
                                        type: "bar",
                                        label: "Assigned",
                                        backgroundColor: "#606060",
                                        data: Object.values(assigned) ? Object.values(assigned) : []
                                    }
                                ]
                            };
                        }
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    nextTenDaysRenewableCustomer() {
        this.dashboardService.nextTenDaysRenewableCustomer().subscribe(
            (res: any) => {
                this.nextTenDaysRenewableCustomerArray = [];
                if (res.dataList != null) {
                    this.nextTenDaysRenewableCustomerArray = res.dataList;
                }
                this.showLoader = false;
            },
            (error: any) => {
                this.showLoader = false;
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    partnerWisePayment() {
        this.partnerwisePayment = null;
        this.dashboardService.partnerWisePayment().subscribe(
            (res: any) => {
                this.partnerwisePayment = {
                    labels: Object.keys(res.data),
                    datasets: [
                        {
                            label: "Amount in " + this.currency,
                            backgroundColor: "#4ea364",
                            hoverBackgroundColor: "#606060",
                            data: Object.values(res.data)
                        }
                    ]
                };
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getDataAccordingtoYear() {
        if (this.statusCheckService.isActiveCMS) {
            this.getMonthWiseCollection(this.date10);
        }
    }

    getDataAccordingtoYearForTicket() {
        if (this.statusCheckService.isActiveCMS) {
            this.monthWiseTicketCount(this.date10);
        }
    }

    totalOpenTickets() {
        this.dashboardService.totalOpenTickets().subscribe(
            (res: any) => {
                this.totalOpenTicket = res.data != null ? res.data.data : "0";
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    monthWiseVolumeUsages(year) {
        this.dashboardService.monthWiseVolumeUsages(year).subscribe(
            (res: any) => {
                this.monthWisevolumeUsages = null;
                if (res.data != null) {
                    this.monthWisevolumeUsages = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Volumes in MB",
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
                // this.connecteduser = res.data != null ? res.data : "0";
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    monthWiseTimeUsages(year) {
        this.dashboardService.monthWiseTimeUsages(year).subscribe(
            (res: any) => {
                this.monthWisetimeUsages = null;
                if (res.data != null) {
                    this.monthWisetimeUsages = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Time in Minute",
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    connectedUser() {
        this.dashboardService.connectedUser().subscribe(
            (res: any) => {
                this.connecteduser = res.data != null ? res.data : "0";
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    overDueTicketList() {
        this.dashboardService.overDueTicketList().subscribe(
            (res: any) => {
                this.overDueticketList = [];
                if (res.dataList != null) {
                    this.overDueticketList = res.dataList;
                }
                this.showTicketLoader = false;
            },
            (error: any) => {
                this.showTicketLoader = false;
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getCommissionGraph() {
        this.showCustomerGraphs = false;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.commissionGraph = true;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        if (this.statusCheckService.isActiveCMS) {
            this.monthWiseAGRPayable(new Date().getFullYear());
            this.monthWiseTDSPayable(new Date().getFullYear());
            this.partnerWiseTDSDetails(new Date().getFullYear());
            this.monthWiseTotalDetails(new Date().getFullYear());
            this.topFivePartnerCommissionWise(new Date().getFullYear());
        }
    }

    monthWiseAGRPayable(year) {
        this.dashboardService.monthWiseAGRPayable(year).subscribe(
            (res: any) => {
                this.monthWiseAGRDetails = null;
                if (res.data != null) {
                    this.monthWiseAGRDetails = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Amount in " + this.currency,
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    monthWiseTDSPayable(year) {
        this.dashboardService.monthWiseTDSPayable(year).subscribe(
            (res: any) => {
                this.monthWiseTDSDetails = null;
                if (res.data != null) {
                    this.monthWiseTDSDetails = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Amount in " + this.currency,
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    partnerWiseTDSDetails(year) {
        this.dashboardService.partnerWiseTDSDetails(year).subscribe(
            (res: any) => {
                this.partnerWiseTDS = null;
                if (res.data != null) {
                    this.partnerWiseTDS = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Amount in " + this.currency,
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "!");
            }
        );
    }

    monthWiseTotalDetails(year) {
        this.dashboardService.monthWiseTotalDetails(year).subscribe(
            (res: any) => {
                this.stackedData = null;
                if (res.data != null) {
                    let tds = res.data["TDS"];
                    let agr = res.data["AGR"];
                    let commission = res.data["COMMISSION"];
                    this.stackedData = {
                        labels: Object.keys(commission),
                        datasets: [
                            {
                                type: "bar",
                                label: "TDS amount in " + this.currency,
                                backgroundColor: "#4ea364",
                                data: Object.values(tds)
                            },
                            {
                                type: "bar",
                                label: "AGR amount in " + this.currency,
                                backgroundColor: "#EFD30A",
                                data: Object.values(agr)
                            },
                            {
                                type: "bar",
                                label: "Commission amount in " + this.currency,
                                backgroundColor: "#606060",
                                data: Object.values(commission)
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
        this.stackedOptions = {
            tooltips: {
                mode: "index",
                intersect: false
            },
            responsive: true,
            scales: {
                xAxes: [
                    {
                        stacked: true
                    }
                ],
                yAxes: [
                    {
                        stacked: true
                    }
                ]
            }
        };
    }

    topFivePartnerCommissionWise(year) {
        this.dashboardService.topFivePartnerCommissionWise(year).subscribe(
            (res: any) => {
                this.topFivePartnerCommissionWiseData = null;
                if (res.data != null) {
                    this.topFivePartnerCommissionWiseData = {
                        labels: Object.keys(res.data),
                        datasets: [
                            {
                                label: "Amount in " + this.currency,
                                data: Object.values(res.data),
                                backgroundColor: "#4ea364",
                                hoverBackgroundColor: "#606060"
                            }
                        ]
                    };
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getInventoryGraph() {
        this.showInventoryLoader = true;
        this.showCustomerGraphs = false;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.commissionGraph = false;
        this.inventoryGraph = true;
        this.showApprovalData = false;
        if (this.statusCheckService.isActiveCMS) {
            this.getStaffAndProductWiseInventory();
            this.getWareHouseAndProductWiseInventory();
            this.getInventoryAlert();
            this.getAvailableInventoryProductWise();
        }

        this.showInventoryLoader = false;
    }

    getStaffAndProductWiseInventory() {
        this.dashboardService.getStaffAndProductWiseInventory().subscribe(
            (res: any) => {
                this.staffAndProductWiseInventoryList = [];
                if (res.data != null) {
                    this.staffAndProductWiseInventoryList = res.data;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getWareHouseAndProductWiseInventory() {
        this.dashboardService.getWareHouseAndProductWiseInventory().subscribe(
            (res: any) => {
                this.wareHouseAndProductWiseInventory = [];
                if (res.data != null) {
                    this.wareHouseAndProductWiseInventory = res.data;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getInventoryAlert() {
        this.inventoryAlertList = [];
        this.dashboardService.getInventoryAlert().subscribe(
            (res: any) => {
                if (res.data != null) {
                    this.inventoryAlertList = res.data;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getAvailableInventoryProductWise() {
        this.dashboardService.getAvailableInventoryProductWise().subscribe(
            (res: any) => {
                this.availableInventoryProductWise = [];
                if (res.data != null) {
                    this.availableInventoryProductWise = res.data;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, "Failed!");
            }
        );
    }

    getApprovalData(): void {
        this.showCustomerGraphs = false;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = true;
        this.leadListFlag = false;
        this.leadFollowupFlag = false;
        this.leadDashboardView = false;
        this.leadListForUserAndTeamFlag = false;
        this.showProductQtyData = false;
        this.showSalseData = false;
        this.showFollowUpPendingData = true;

        let url = "/serviceStatus";
        this.dashboardService.getMethod(url).subscribe((response: any) => {
            if (this.statusCheckService.isActiveCMS) {
                this.getCustomerPendingApprovals("");
                this.getPlanPendingApprovals("");
                this.getPlanGroupPendingApprovals("");
                this.getPaymentPendingApprovals("");
                this.getCustomerTerminationPendingApprovals("");
                this.getChangeDiscountPendingApprovals("");
                this.getInvoicePendingApprovals("");
                this.getPartnerPaymentApprovals("");
                this.getCustomerDocPendingApprovals("");
                this.getSpecialPlanMappingApprovals("");
            }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getPlanPendingApprovals("");

            // }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getPlanGroupPendingApprovals("");
            // }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getPaymentPendingApprovals("");
            // }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getCustomerTerminationPendingApprovals("");
            // }
            if (this.statusCheckService.isActiveTicketService) {
                this.getCasePendingApprovals("");
            }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getChangeDiscountPendingApprovals("");
            // }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getInvoicePendingApprovals("");
            // }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getPartnerPaymentApprovals("");
            // }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getCustomerDocPendingApprovals("");
            // }
            if (this.statusCheckService.isActiveInventoryService) {
                this.getInventoryPendingApprovals("");
            }
            if (this.statusCheckService.isActiveSalesCrm) {
                this.getLeadList("");
            }
            // if (this.statusCheckService.isActiveCMS) {
            //     this.getSpecialPlanMappingApprovals("");
            // }
            if (this.statusCheckService.isActiveTaskManagementService) {
                // this.getTaskPendingApprovals("");
            }
            setTimeout(() => {
                this.showApprovalData = true;
                this.cd.detectChanges();
            });
        });
    }

    getSalseDashboardData(): void {
        this.showCustomerGraphs = false;
        this.showFollowUpPendingData = false;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        this.leadListFlag = false;
        this.leadFollowupFlag = false;
        this.leadDashboardView = false;
        this.leadListForUserAndTeamFlag = false;
        this.showProductQtyData = false;
        this.showSalseData = true;
        // let url = "/serviceStatus";
        // this.gateWayCommonService.getMethod(url).subscribe((response: any) => {
        //   console.log("::::statuschceck:::::::");
        //   if (this.statusCheckService.isActiveCommon) {
        this.getSalseDashboardDataDetails();
        this.getChildStaffList();
        setTimeout(() => {
            this.cd.detectChanges();
        });
        // }
        // });
    }

    getSpecialPlanMappingApprovals(list): void {
        let size;
        const page = this.currentPagespecialPlanMappingdata;
        if (list) {
            size = list;
            this.specialPlanMappingdataitemsPerPage = list;
        } else {
            size = this.specialPlanMappingdataitemsPerPage;
        }

        const url = `/dashboard/approval/getSpecialPlanMappingApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.specialPlanMappingData = response.dataList;
                this.specialPlanMappingDataselector = response.dataList;
                this.specialPlanMappingdatatotalRecords = response.totalRecords;
                if (this.showItemPerPage > this.specialPlanMappingdataitemsPerPage) {
                    this.specialPlanMappingListDatalength =
                        this.customerListData.length % this.showItemPerPage;
                } else {
                    this.specialPlanMappingListDatalength =
                        this.customerListData.length % this.specialPlanMappingdataitemsPerPage;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    totalItemPerPageForSpecialPlanMapping(event): void {
        this.showItemPerPageForPayment = Number(event.value);
        if (this.currentPagePaymentListdata > 1) {
            this.currentPagePaymentListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getSpecialPlanMappingApprovals(this.showItemPerPageForPayment);
        }
    }

    pageChangedForSpecialPlanMapping(pageNumber): void {
        this.currentPagePaymentListdata = pageNumber;
        if (this.statusCheckService.isActiveCMS) {
            this.getSpecialPlanMappingApprovals("");
        }
    }

    getSalseDashboardDataDetails(): void {
        let size;
        const page = this.currentPagecustomerListdata;
        const startDate = this.formatToTimestamp(this.startdate);
        const endDate = this.formatToTimestamp(this.enddate);
        const url =
            `/staff-sales/created-by/` +
            this.selectedFilter +
            "?startdate=" +
            startDate +
            "&enddate=" +
            endDate;
        // const custerlist = {
        //   page,
        //   pageSize: size
        // };
        this.gateWayCommonService.getMethod(url).subscribe(
            (response: any) => {
                this.salseDetatilListData = response;
                this.salseDetatilListDataSelector = response;
                // this.customerListdatatotalRecords = response.totalRecords;
                // if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                //   this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                // } else {
                //   this.customerListDatalength =
                //     this.customerListData.length % this.customerListdataitemsPerPage;
                // }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    getChildStaffList() {
        let id = localStorage.getItem("userId");
        let username = localStorage.getItem("loginUserName");

        const url = `/staffList/` + id;
        // const custerlist = {
        //   page,
        //   pageSize: size
        // };
        this.gateWayCommonService.getMethod(url).subscribe(
            (response: any) => {
                this.childStaffData = response.dataList;
                this.childStaffDataSelector = response.dataList;
                var loginuser = {
                    id: Number(id),
                    username: username,
                    firstname: "Kingsley",
                    fullName: "Kingsley Okafor",
                    lastname: "Okafor",
                    phone: null
                };
                this.childStaffData.push(loginuser);
                this.selectedFilter = Number(id);
                // this.customerListdatatotalRecords = response.totalRecords;
                // if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                //   this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                // } else {
                //   this.customerListDatalength =
                //     this.customerListData.length % this.customerListdataitemsPerPage;
                // }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    getCustomerPendingApprovals(list): void {
        let size;
        const page = this.currentPagecustomerListdata;
        if (list) {
            size = list;
            this.customerListdataitemsPerPage = list;
        } else {
            size = this.customerListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getCustomersApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerListData = response.dataList;
                this.customerListDataselector = response.dataList;
                this.customerListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                    this.customerListDatalength = this.customerListData.length % this.showItemPerPage;
                } else {
                    this.customerListDatalength =
                        this.customerListData.length % this.customerListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    rescheduleFollowupRemarks = [
        "Confirm Later",
        "Do Not Call",
        "Expensive Package",
        "Call rejected by Client"
    ];

    followUpDetails: any;
    followUpCustomerId: any;
    rescheduleFollowUps(followUpDetails) {
        this.followUpCustomerId = followUpDetails;
        this.followUpId = followUpDetails.id;
        this.followUpDetails = followUpDetails;
        this.generatedNameOfTheReFollowUps(followUpDetails.customersId);
        this.reFollowupFormsubmitted = false;
        this.dialog.open(this.rescheduleFollowUpsModal, {
            width: "600px",
            disableClose: true
        });
    }
    saveReFollowUps() {
        this.followupData = {};
        this.reFollowupFormsubmitted = true;
        if (this.reFollowupScheduleForm.valid) {
            this.followupData = this.reFollowupScheduleForm.value;
            this.followupData.customersId = this.followUpCustomerId.customersId;
            this.followupData.staffUserId = this.staffid;
            this.followupData.mvnoId = this.mvnoid;
            this.followupData.isSend = false;
            this.followupData.status = "Pending";
            const myFormattedDate = this.datePipe.transform(
                this.followupData.followUpDatetime,
                "dd-MM-yyyy HH:mm:ss"
            );
            this.followupData.followUpDatetime = myFormattedDate;
            const url =
                "/cafFollowUp/reSchedulefollowup?followUpId=" +
                this.followUpId +
                "&remarks=" +
                this.followupData.remarksTemp;
            this.customerManagementService.postMethod(url, this.followupData).subscribe(
                (response: any) => {
                    this.reFollowupFormsubmitted = false;
                    this.reFollowupScheduleForm.reset();
                    this.toastr.success(`${response.message}`, "Success!");
                    this.dialog?.closeAll();
                    this.reFollowupFormsubmitted = false;
                    this.getCustomerFollowUpPendingApprovals("");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
            this.reFollowupFormsubmitted = false;
        }
    }
    generateNameOfReFollowUp: any;
    generatedNameOfTheReFollowUps(customersId) {
        const url = "/cafFollowUp/generateNameOfTheCafFollowUp/" + customersId;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.generateNameOfReFollowUp = response.data;
                this.generateNameOfReFollowUp
                    ? this.reFollowupScheduleForm.controls["followUpName"].setValue(
                        this.generateNameOfReFollowUp
                    )
                    : "";
            },
            (error: any) => {
                this.toastr.error(
                    `${error.error.errorMessage}`,
                    ' "Something went wrong with "followup name" Generation"!'
                );
            }
        );
    }

    getCustomerFollowUpPendingApprovals(list): void {
        let size;
        const page =
            this.currentPagecustomerListFollowUpData["pageIndex"] ||
            this.currentPagecustomerListFollowUpData;
        if (list) {
            size = list;
            this.customerListFollowUpDataItemsPerPage = list;
        } else {
            size = this.customerListFollowUpDataItemsPerPage;
        }

        const url = `/cafFollowUp/findAllByStaff?staffId=${this.staffid}&page=${page}&pageSize=${size}`;
        this.dashboardService.getMethodForFollowUp(url).subscribe(
            (response: any) => {
                this.customerListFollowUPData = response.dataList;
                this.customerListFollowUpDataselector = response.dataList;
                this.customerListFollowUpDataTotalRecords = response.totalRecords;
                if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                    this.customerListFollowUpDatalength =
                        this.customerListFollowUPData.length % this.showItemPerPage;
                } else {
                    this.customerListFollowUpDatalength =
                        this.customerListFollowUPData.length % this.customerListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.toastr.error(error.error.ERROR || "Something Went Wrong", "Failed!");
            }
        );
    }

    totalItemPerPageForCustomerFollowUpApprovals(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagecustomerListFollowUpData > 1) {
            this.currentPagecustomerListFollowUpData = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getCustomerFollowUpPendingApprovals(this.showItemPerPage);
        }
    }

    pageChangedForCustomerFollowUpApprovals(pageEvent: PageEvent): void {
        this.currentPagecustomerListFollowUpData = pageEvent.pageIndex + 1;
        this.customerListFollowUpDataItemsPerPage = pageEvent.pageSize;
        if (this.statusCheckService.isActiveCMS) {
            this.getCustomerFollowUpPendingApprovals(this.customerListFollowUpDataItemsPerPage);
        }
    }
    saveCloseFollowUps() {
        this.closeFollowupFormsubmitted = true;
        if (this.closeFollowupForm.valid) {
            const url =
                "/cafFollowUp/closefollowup?followUpId=" +
                this.followUpId +
                "&remarks=" +
                this.closeFollowupForm.get("remarks").value;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.closeDialog();
                    this.closeFollowupForm.reset();

                    this.toastr.success(`${response.responseMessage}`, "Success!");
                    this.getCustomerFollowUpPendingApprovals("");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
            this.closeFollowupFormsubmitted = false;
        }
    }
    closeFollowUps(followUpDetails) {
        this.closeFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        this.dialog.open(this.closeFollowUpsModal, {
            width: "600px",
            disableClose: true
        });
    }

    closeActionFolloupPopUps() {
        this.closeFollowupForm.reset();
        this.dialog.closeAll();
    }

    followUpDetailsObj: any;
    remarkFollowUp(followUpDetails) {
        this.followUpDetailsObj = followUpDetails;
        this.remarkFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        this.getfollowUpRemarkList(this.followUpId);
        this.dialog.open(this.remarkScheduleFollowupModal, {
            width: "600px",
            disableClose: true
        });
    }

    closeRemarkPopup() {
        this.remarkFollowupForm.reset();
        this.remarkFollowupFormsubmitted = false;
        this.closeDialog();
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
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
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
                (response: any) => {
                    this.closeDialog();
                    this.remarkFollowupForm.reset();
                    this.toastr.success(`${response.responseMessage}`, "Success!");
                    this.getCustomerFollowUpPendingApprovals("");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
            this.remarkFollowupFormsubmitted = false;
        }
    }

    customerDocumentId: any;
    totalItemPerPageForCustomerApprovals(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagecustomerListdata > 1) {
            this.currentPagecustomerListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getCustomerPendingApprovals(this.showItemPerPage);
        }
    }

    pageChangedForCustomerApprovals(pageNumber): void {
        this.currentPagecustomerListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getCustomerPendingApprovals(pageNumber.pageSize);
        }
    }

    getPlanPendingApprovals(list): void {
        let size;
        const page = this.currentPagePlanListdata;
        if (list) {
            size = list;
            this.planListdataitemsPerPage = list;
        } else {
            size = this.planListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getPlanApprovalsList`;
        const planList = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, planList).subscribe(
            (response: any) => {
                this.planListData = response.dataList;
                this.planListDataselector = response.dataList;
                this.planListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPage > this.customerListdataitemsPerPage) {
                    this.planDatalength = this.planListData.length % this.showItemPerPage;
                } else {
                    this.planDatalength = this.planListData.length % this.planListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForPlanApprovals(event): void {
        this.showItemPerPageForPlan = Number(event.value);
        if (this.currentPagePlanListdata > 1) {
            this.currentPagePlanListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getPlanPendingApprovals(this.showItemPerPageForPlan);
        }
    }

    pageChangedForPlanApprovals(pageNumber): void {
        this.currentPagePlanListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getPlanPendingApprovals(pageNumber.pageSize);
        }
    }

    getPlanGroupPendingApprovals(list): void {
        let size;
        const page = this.currentPagePlanGroupListdata;
        if (list) {
            size = list;
            this.planGroupListdataitemsPerPage = list;
        } else {
            size = this.planGroupListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getPlanGroupApprovalsList`;
        const planList = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, planList).subscribe(
            (response: any) => {
                this.planGroupListData = response.dataList;
                this.planGroupListDataselector = response.dataList;
                this.planGroupListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageForPlanGroup > this.planGroupListdataitemsPerPage) {
                    this.planGroupDatalength =
                        this.planGroupListData.length % this.showItemPerPageForPlanGroup;
                } else {
                    this.planGroupDatalength =
                        this.planGroupListData.length % this.planGroupListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForPlanGroupApprovals(event): void {
        this.showItemPerPageForPlanGroup = Number(event.value);
        if (this.currentPagePlanGroupListdata > 1) {
            this.currentPagePlanGroupListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getPlanGroupPendingApprovals(this.showItemPerPageForPlanGroup);
        }
    }

    pageChangedForPlanGroupApprovals(pageNumber): void {
        this.currentPagePlanGroupListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getPlanGroupPendingApprovals(pageNumber.pageSize);
        }
    }
    getPaymentPendingApprovals(list): void {
        let size;
        const page = this.currentPagePaymentListdata;
        if (list) {
            size = list;
            this.paymentListdataitemsPerPage = list;
        } else {
            size = this.paymentListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getPaymentApprovalsList`;
        const planList = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, planList).subscribe(
            (response: any) => {
                if (response.dataList?.length > 0) {
                    this.paymentListData = response.dataList;
                    this.paymentListDataselector = response.dataList;
                    this.paymentListdatatotalRecords = response.totalRecords;
                    if (this.showItemPerPageForPayment > this.paymentListdataitemsPerPage) {
                        this.paymentDatalength = this.paymentListData.length % this.showItemPerPageForPayment;
                    } else {
                        this.paymentDatalength = this.paymentListData.length % this.paymentListdataitemsPerPage;
                    }
                } else {
                    this.paymentListData = [];
                    this.paymentListDataselector = [];
                    this.paymentDatalength = 0;
                    this.paymentListdatatotalRecords = 0;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForPaymentApprovals(event): void {
        this.showItemPerPageForPayment = Number(event.value);
        if (this.currentPagePaymentListdata > 1) {
            this.currentPagePaymentListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getPlanGroupPendingApprovals(this.showItemPerPageForPayment);
        }
    }

    pageChangedForPaymentApprovals(pageNumber): void {
        this.currentPagePaymentListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getPaymentPendingApprovals(pageNumber.pageSize);
        }
    }

    getCustomerTerminationPendingApprovals(list): void {
        let size;
        const page = this.currentPagecustomerTerminationListdata;
        if (list) {
            size = list;
            this.customerTerminationListdataitemsPerPage = list;
        } else {
            size = this.customerTerminationListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getCustomersApprovalsForTermination`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerTerminationListData = response.dataList;
                this.customerTerminationListDataselector = response.dataList;
                this.customerTerminationListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageTermination > this.customerTerminationListdataitemsPerPage) {
                    this.customerTerminationListDatalength =
                        this.customerTerminationListData.length % this.showItemPerPageTermination;
                } else {
                    this.customerTerminationListDatalength =
                        this.customerTerminationListData.length % this.customerTerminationListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForCustomerTerminationApprovals(event): void {
        this.showItemPerPageTermination = Number(event.value);
        if (this.currentPagecustomerTerminationListdata > 1) {
            this.currentPagecustomerTerminationListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getCustomerPendingApprovals(this.showItemPerPageTermination);
        }
    }

    pageChangedForCustomerTerminationApprovals(pageNumber): void {
        this.currentPagecustomerTerminationListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getCustomerTerminationPendingApprovals(pageNumber.pageSize);
        }
    }

    getCasePendingApprovals(list): void {
        let size;
        const page = this.currentPageCaseListdata;
        if (list) {
            size = list;
            this.caseListdataitemsPerPage = list;
        } else {
            size = this.caseListdataitemsPerPage;
        }

        const url = `/case/approval/getTicketApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.ticketManagementService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.caseListData = response.dataList;
                this.caseListDataselector = response.dataList;
                this.caseListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageCase > this.caseListdataitemsPerPage) {
                    this.caseListDatalength = this.caseListData.length % this.showItemPerPageCase;
                } else {
                    this.caseListDatalength = this.caseListData?.length % this.caseListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForCaseApprovals(event): void {
        this.showItemPerPageCase = Number(event.value);
        if (this.currentPageCaseListdata > 1) {
            this.currentPageCaseListdata = 1;
        }
        if (this.statusCheckService.isActiveTicketService) {
            this.getCasePendingApprovals(this.showItemPerPageCase);
        }
    }

    pageChangedForCaseApprovals(pageNumber): void {
        this.currentPageCaseListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveTicketService) {
            this.getCasePendingApprovals(pageNumber.pageSize);
        }
    }

    getTaskPendingApprovals(list): void {
        // let size;
        // const page = this.currentPageTaskListData;
        // if (list) {
        //     size = list;
        //     this.taskListItemsPerPage = list;
        // } else {
        //     size = this.taskListItemsPerPage;
        // }
        // const url = "/case/approval/getTaskApprovals";
        // const requestPayload = {
        //     page,
        //     pageSize: size
        // };
        // this.taskManagementService.postMethod(url, requestPayload).subscribe(
        //     (response: any) => {
        //         this.taskListData = response.dataList;
        //         this.taskListSelector = response.dataList;
        //         this.taskListTotalRecords = response.totalRecords;
        //         if (this.showItemPerPageTask > this.taskListItemsPerPage) {
        //             this.taskListLength = this.taskListData.length % this.showItemPerPageTask;
        //         } else {
        //             this.taskListLength = this.taskListData?.length % this.taskListItemsPerPage;
        //         }
        //     },
        //     (error: any) => {
        //         this.toastr.error(`${error.error?.ERROR ? error.error?.ERROR : "Something went wrong!"}`, 'Failed!');
        //     }
        // );
    }

    totalItemPerPageForTaskApprovals(event): void {
        this.showItemPerPageTask = Number(event.value);
        if (this.currentPageTaskListData > 1) {
            this.currentPageTaskListData = 1;
        }
        if (this.statusCheckService.isActiveTaskManagementService) {
            // this.getTaskPendingApprovals(this.showItemPerPageTask);
        }
    }

    pageChangedForTaskApprovals(pageNumber): void {
        this.currentPageTaskListData = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveTaskManagementService) {
            // this.getTaskPendingApprovals(pageNumber.pageSize);
        }
    }

    getChangeDiscountPendingApprovals(list): void {
        let size;
        const page = this.currentPageChangeDiscountListdata;
        if (list) {
            size = list;
            this.changeDiscountListdataitemsPerPage = list;
        } else {
            size = this.changeDiscountListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getChangeDiscountApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.changeDiscountListData = response.dataList;
                this.changeDiscountListDataselector = response.dataList;
                this.changeDiscountListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageChangeDiscount > this.changeDiscountListdataitemsPerPage) {
                    this.changeDiscountListDatalength =
                        this.changeDiscountListData.length % this.showItemPerPageChangeDiscount;
                } else {
                    this.changeDiscountListDatalength =
                        this.changeDiscountListData.length % this.changeDiscountListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForChangeDiscountApprovals(event): void {
        this.showItemPerPageChangeDiscount = Number(event.value);
        if (this.currentPageChangeDiscountListdata > 1) {
            this.currentPageChangeDiscountListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getChangeDiscountPendingApprovals(this.showItemPerPageChangeDiscount);
        }
    }

    pageChangedForChangeDiscountApprovals(pageNumber): void {
        this.currentPageChangeDiscountListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getChangeDiscountPendingApprovals(pageNumber.pageSize);
        }
    }

    getInvoicePendingApprovals(list): void {
        let size;
        const page = this.currentPageInvoiceListdata;
        if (list) {
            size = list;
            this.invoiceListdataitemsPerPage = list;
        } else {
            size = this.invoiceListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getBillToOrgApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.invoiceListData = response.dataList;
                this.invoiceListDataselector = response.dataList;
                this.invoiceListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageInvoice > this.invoiceListdataitemsPerPage) {
                    this.invoiceListDatalength = this.invoiceListData.length % this.showItemPerPageInvoice;
                } else if (this.invoiceListData != null) {
                    this.invoiceListDatalength =
                        this.invoiceListData.length % this.invoiceListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForInvoiceApprovals(event): void {
        this.showItemPerPageInvoice = Number(event.value);
        if (this.currentPageInvoiceListdata > 1) {
            this.currentPageInvoiceListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getInvoicePendingApprovals(this.showItemPerPageInvoice);
        }
    }

    pageChangedForInvoiceApprovals(pageNumber): void {
        this.currentPageInvoiceListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getInvoicePendingApprovals(pageNumber.pageSize);
        }
    }
    getPartnerPaymentApprovals(list): void {
        let size;
        const page = this.currentPagePartnerPaymentListdata;
        if (list) {
            size = list;
            this.partnerPaymentListdataitemsPerPage = list;
        } else {
            size = this.partnerPaymentListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getPartnerPaymentApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.partnerPaymentListData = response.dataList;
                this.partnerPaymentListDataselector = response.dataList;
                this.partnerPaymentListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPagePartnerPayment > this.partnerPaymentListdataitemsPerPage) {
                    this.partnerPaymentListDatalength =
                        this.partnerPaymentListData.length % this.showItemPerPagePartnerPayment;
                } else {
                    this.partnerPaymentListDatalength =
                        this.partnerPaymentListData.length % this.partnerPaymentListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    totalItemPerPageForPartnerPaymentApprovals(event): void {
        this.showItemPerPagePartnerPayment = Number(event.value);
        if (this.currentPagePartnerPaymentListdata > 1) {
            this.currentPagePartnerPaymentListdata = 1;
        }
        if (this.statusCheckService.isActiveCMS) {
            this.getPartnerPaymentApprovals(this.showItemPerPagePartnerPayment);
        }
    }

    pageChangedForPartnerPaymentApprovals(pageNumber): void {
        this.currentPagePartnerPaymentListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveCMS) {
            this.getPartnerPaymentApprovals(pageNumber.pageSize);
        }
    }
    //Customer Approve/Reject
    isCustDocPending(cafId, nextApproverId) {
        // this.customerDocumentService.isCustDocPending(cafId).subscribe(
        //   (response: any) => {
        //     if (response.data) {
        //       this.messageService.add({
        //         severity: "error",
        //         summary: "Error",
        //         detail: "Customer cannot activate. Document Verification Pending",
        //         icon: "far fa-times-circle"
        //       });
        //     } else {
        this.approved = false;
        this.selectStaff = null;
        this.dialog.open(this.assignCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
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
    rejectCustomerCAFOpen(cafId, nextApproverId) {
        this.reject = false;
        this.dialog.open(this.rejectCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
        this.assignCustomerCAFId = cafId;
        this.nextApproverId = nextApproverId;
    }

    onFileChangeUpload(event: any): void {
        this.selectedFileUploadPreview = [];
        const inputElement = event.target as HTMLInputElement;
        if (inputElement.files && inputElement.files.length > 0) {
            const files: FileList = inputElement.files;

            // Validate all files
            for (let i = 0; i < files.length; i++) {
                const file = files.item(i);
                if (
                    file &&
                    (file.type === "image/png" ||
                        file.type === "image/jpg" ||
                        file.type === "image/jpeg" ||
                        file.type === "application/pdf")
                ) {
                    this.selectedFileUploadPreview.push(file);
                } else {
                    this.toastr.error(`${file?.name}`, "Info!");
                }
            }
        }
    }

    assignToStaff(flag) {
        let url: any;
        let name: any;
        let id: any;
        if (this.planID) {
            id = this.planID;
            name = "PLAN";
        } else if (this.planGroupID) {
            id = this.planGroupID;
            name = "PLAN_GROUP";
        } else if (this.partnerID) {
            id = this.partnerID;
            name = "PARTNER_BALANCE";
        } else {
            id = this.assignCustomerCAFId;
            name = "CAF";
        }
        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${id}&eventName=${name}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${id}&eventName=${name}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${id}&eventName=${name}&nextAssignStaff=${this.selectStaffReject}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${id}&eventName=${name}&isApproveRequest=${flag}`;
            }
        }
        this.dashboardService.getMethod(url).subscribe(
            response => {
                this.dialog?.closeAll();
                // this.selectStaffReject = null;
                this.getApprovalData();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    assignStaffListData = [];

    assignCustomerCAF() {
        this.assignCustomerCAFsubmitted = true;

        if (this.assignCustomerCAFForm.valid) {
            let url: any;
            let payload: any;

            if (this.planID) {
                url = "/approvePlan";
                payload = {
                    planId: this.planID,
                    nextStaffId: "",
                    flag: "approved",
                    remark: this.assignCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else if (this.planGroupID) {
                url = "/approvePlanGroup";
                payload = {
                    planGroupId: this.planGroupID,
                    nextStaffId: "",
                    flag: "approved",
                    remark: this.assignCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else if (this.partnerID) {
                url = "/approvePartnerBalance";
                payload = {
                    partnerPaymentId: this.partnerID,
                    nextStaffId: "",
                    flag: "approved",
                    remark: this.assignCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else if (this.customerDocumentId) {
                url =
                    "/custDoc/approveUploadCustomerDoc?docId=" +
                    this.customerDocumentId +
                    "&remarks=" +
                    this.assignCustomerCAFForm.controls.remark.value +
                    "&isApproveRequest=true";
                payload = {
                    custcafId: this.customerDocumentId,
                    nextStaffId: "",
                    flag: "approved",
                    remark: this.assignCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else {
                url = "/approveCaf";

                const formData = new FormData();
                formData.append("custcafId", String(this.assignCustomerCAFId));
                formData.append("nextStaffId", "");
                formData.append("flag", "approved");
                formData.append("remark", this.assignCustomerCAFForm.controls.remark.value ?? "");
                formData.append("staffId", String(localStorage.getItem("userId")));

                payload = formData;
            }

            this.dashboardService.updateMethod(url, payload).subscribe(
                (response: any) => {
                    if (response.dataList != null && response.dataList.length > 0) {
                        this.assignStaffListDataSPM = response.dataList;
                        this.dialog.open(this.assignCustomerDocumentForApproval, {
                            width: "600px",
                            disableClose: true
                        });
                    } else {
                        this.getCustomerDocPendingApprovals("");
                    }

                    this.getApprovalData();
                    this.toastr.success(`${response.message}`, "Success!");

                    this.assignCustomerCAFForm.reset();
                    this.assignCustomerCAFsubmitted = false;

                    if (response.result != null && response.result.dataList != null) {
                        this.approveCAF = response.result.dataList;
                        this.approved = true;
                    } else {
                        this.dialog?.closeAll();
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }

    rejectCustomerCAF() {
        this.rejectCustomerCAFsubmitted = true;
        if (this.rejectCustomerCAFForm.valid) {
            let url: any;
            let assignCAFData: any;
            if (this.planID) {
                url = "/approvePlan";
                assignCAFData = {
                    planId: this.planID,
                    nextStaffId: "",
                    flag: "Rejected",
                    remark: this.rejectCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else if (this.planGroupID) {
                url = "/approvePlanGroup";
                assignCAFData = {
                    planGroupId: this.planGroupID,
                    nextStaffId: "",
                    flag: "Rejected",
                    remark: this.rejectCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else if (this.partnerID) {
                url = "/approvePartnerBalance";
                assignCAFData = {
                    partnerPaymentId: this.partnerID,
                    nextStaffId: "",
                    flag: "Rejected",
                    remark: this.rejectCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };
            } else {
                url = "/approveCaf";
                assignCAFData = {
                    custcafId: this.assignCustomerCAFId,
                    nextStaffId: "",
                    flag: "rejected",
                    remark: this.rejectCustomerCAFForm.controls.remark.value,
                    staffId: localStorage.getItem("userId")
                };

                const formData = new FormData();
                formData.append("custcafId", assignCAFData.custcafId.toString());

                formData.append(
                    "nextStaffId",
                    assignCAFData.nextStaffId == null ? "" : assignCAFData.nextStaffId.toString()
                );
                formData.append("flag", assignCAFData.flag);
                formData.append("remark", assignCAFData.remark);
                formData.append("staffId", assignCAFData.staffId.toString());
                this.selectedFileUploadPreview.forEach((file: File, index: number) => {
                    formData.append("files", file, file.name);
                    //   formData.append(`files[${index}]`, file);
                });

                this.dashboardService.updateMethod(url, formData).subscribe(
                    (response: any) => {
                        this.getApprovalData();
                        this.toastr.success(`${response?.message}`, "Success!");

                        this.rejectCustomerCAFForm.reset();
                        this.rejectCustomerCAFsubmitted = false;
                        if (response.result.dataList != null) {
                            this.rejectCAF = response.result.dataList;
                            this.reject = true;
                        } else {
                            this.dialog?.closeAll();
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, "Failed!");
                    }
                );
            }
        }
    }

    ifApproveStatus = false;
    approveRejectRemark = "";
    ifShowRemarkMoedl = "";
    apprRejectCustID = "";
    approveInventoryData: any;
    rejectInventoryData: any;

    approveCutomerstatusModalOpen(custId) {
        this.ifApproveStatus = true;
        this.apprRejectCustID = custId;
        this.approveRejectRemark = "";
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Customer";
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }

    rejectCustomerstatusModalOpen(custId) {
        this.ifApproveStatus = false;
        this.apprRejectCustID = custId;
        this.approveRejectRemark = "";
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Customer";
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
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
        this.dashboardService.updateMethod(url, data).subscribe(
            (response: any) => {
                if (response.dataList != null && response.dataList.length > 0) {
                    this.assignStaffListDataSPM = response.dataList;
                    //  this.ApproveRejectModal = false;

                    this.dialog.open(this.assignCustomerDocumentForApproval, {
                        width: "600px",
                        disableClose: true
                    });
                } else {
                    //   this.ApproveRejectModal = false;
                    this.getCustomerTerminationPendingApprovals("");
                }
                this.dialog?.closeAll();
                this.ifShowRemarkMoedl = "";
                if (this.ifApproveStatus == true) {
                    if (response.result.dataList != null) {
                        this.approved = true;
                        this.approveInventoryData = response.result.dataList;
                        this.dialog.open(this.assignCustomerInventoryModal, {
                            width: "600px",
                            disableClose: true
                        });
                    } else {
                        this.toastr.success(`${response?.message}`, "Approved Successfully.!");

                        this.getApprovalData();
                    }
                } else {
                    if (response.result.dataList) {
                        this.reject = true;
                        this.rejectInventoryData = response.result.dataList;
                        this.dialog.open(this.rejectCustomerInventoryModal, {
                            width: "600px",
                            disableClose: true
                        });
                    } else {
                        this.toastr.success(`${response?.message}`, "Rejected Successfully.!");

                        this.getApprovalData();
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    assignToStaffCAF(flag) {
        let url: any;
        let id: any;
        let name: any;
        if (this.paymentID) {
            id = this.paymentID;
            name = "PAYMENT";
        } else if (this.caseId) {
            id = this.caseId;
            name = "CASE";
        } else if (this.discountID) {
            id = this.discountID;
            name = "CUSTOMER_DISCOUNT";
        } else {
            id = this.approveId;
            name = "TERMINATION";
        }
        if (flag) {
            url = `/teamHierarchy/assignFromStaffList?entityId=${id}&eventName=${name}&nextAssignStaff=${this.selectStaff.id}&isApproveRequest=${flag}`;
        } else {
            url = `/teamHierarchy/assignFromStaffList?entityId=${id}&eventName=${name}&nextAssignStaff=${this.selectStaffReject.id}&isApproveRequest=${flag}`;
        }
        const api = (this.ifShowRemarkMoedl == "Ticket") ? this.ticketManagementService.getMethod(url) : this.dashboardService?.getMethod(url);

        api.subscribe(
            response => {
                if (flag) {
                    this.toastr.success(`${flag}`, "Approved Successfully.!");
                } else {
                    this.toastr.success(`${flag}`, "Rejected Successfully.!");
                }
                this.dialog?.closeAll();
                this.getApprovalData();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    //Plan
    planID: any;
    rejectPlanOpen(planId, nextApproverId) {
        this.reject = false;
        this.selectStaff = null;
        this.rejectCAF = [];
        this.dialog.open(this.rejectCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
        this.planID = planId;
        this.nextApproverId = nextApproverId;
        this.rejectCustomerCAFForm.reset();
        this.rejectCustomerCAFsubmitted = false;
    }

    approvePlanOpen(planId, nextApproverId) {
        this.approved = false;
        this.selectStaff = null;
        this.approveCAF = [];
        this.dialog.open(this.assignCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
        this.planID = planId;
        this.nextApproverId = nextApproverId;
        this.assignCustomerCAFForm.reset();
        this.assignCustomerCAFsubmitted = false;
    }

    //Plan Group
    planGroupID: any;
    rejectPlanGroupOpen(planGroupId, nextApproverId) {
        this.reject = false;
        this.selectStaff = null;
        this.approveCAF = [];
        this.dialog.open(this.rejectCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
        this.planGroupID = planGroupId;
        this.nextApproverId = nextApproverId;
        this.assignCustomerCAFForm.reset();
        this.assignCustomerCAFsubmitted = false;
    }

    approvePlanGroupOpen(planGroupId, nextApproverId) {
        this.approved = false;
        this.selectStaff = null;
        this.rejectCAF = [];
        this.dialog.open(this.assignCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
        this.planGroupID = planGroupId;
        this.nextApproverId = nextApproverId;
        this.assignCustomerCAFForm.reset();
        this.assignCustomerCAFsubmitted = false;
    }

    approveRejectInvoice(invoiceID, isApproveRequest) {
        // this.assignStaffForm.reset();
        const url = `/invoiceV2/approveDebitDoc?invoiceId=${invoiceID}&isApproveRequest=${isApproveRequest}&remark=${"approved"}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.assignStaffForm.controls.invoiceId.setValue(invoiceID);
                if (isApproveRequest) {
                    if (response.dataList != null) {
                        this.approve = true;
                        this.staffList = response.dataList;

                        $("#assignApproveModal").modal("show");
                    } else {
                        this.approve = false;
                        this.allIsChecked = false;
                    }
                } else {
                    if (response.dataList != null) {
                        this.approve = false;
                        this.staffList = response.dataList;

                        $("#assignApproveModal").modal("show");
                    } else {
                        this.approve = true;
                        this.allIsChecked = false;
                    }
                }
                // this.closebutton.nativeElement.click();

                if (response.responseCode === 417) {
                    this.toastr.success(`${response.responseMessage}`, "Failed!");
                } else {
                    this.toastr.success(`${response.message}`, "Success!");
                }

                this.getApprovalData();
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    //Partner Payment
    partnerID: any;
    rejectPartnerBalanceOpen(partnerBalanceId, nextApproverId) {
        this.reject = false;
        this.selectStaff = null;
        this.rejectCAF = [];
        this.dialog.open(this.rejectCustomerCAFModal, {
            width: "695px",
            disableClose: true
        });
        this.partnerID = partnerBalanceId;
        this.nextApproverId = nextApproverId;
    }

    approvePartnerBalanceOpen(partnerBalanceId, nextApproverId) {
        this.approved = false;
        this.selectStaff = null;
        this.approveCAF = [];
        this.dialog.open(this.assignCustomerCAFModal, {
            width: "600px",
            disableClose: true
        });
        this.partnerID = partnerBalanceId;
        this.nextApproverId = nextApproverId;
    }
    //Payment
    paymentID: any;
    ApprRejectData: any = [];

    approvePaymentModalOpen(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = true;
        this.ApprRejectData = data;
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Payment";
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }

    rejectPaymentModalOpen(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = false;
        this.ApprRejectData = data;
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Payment";
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }

    statusApporeved() {
        this.paymentID = this.ApprRejectData.id;
        this.approveId = this.ApprRejectData.id;
        this.approved = false;
        this.approveInventoryData = [];
        this.selectStaff = null;

        const format = "yyyy-MM-dd";
        const locale = "en-US";
        const myDate = moment(this.ApprRejectData.paymentdate, "DD-MM-YYYY").toDate();
        const formattedDate = formatDate(myDate, format, locale);

        let approvedData = {
            customerid: this.ApprRejectData.custId,
            idlist: Number(this.ApprRejectData.id),
            paymode: this.ApprRejectData.paymode,
            paystatus: this.ApprRejectData.status,
            paytodate: formattedDate,
            referenceno: this.ApprRejectData.receiptNo,
            remarks: this.approveRejectRemark
        };
        const url = "/payment/approve";
        this.dashboardService.postMethod(url, approvedData).subscribe(
            (response: any) => {
                // this.recepit = response.data;
                this.dialog?.closeAll();
                if (response.payment.dataList) {
                    this.approved = true;
                    this.dialog.open(this.assignCustomerInventoryModal, {
                        width: "600px",
                        disableClose: true
                    });
                    this.approveInventoryData = response.payment.dataList;
                } else {
                    this.toastr.success(response.responseMessage || "Approved Successfully!", 'Success!');

                    this.getApprovalData();
                }
                this.ifApproveStatus = false;
                this.ApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    statusRejected() {
        this.paymentID = this.ApprRejectData.id;
        this.approveId = this.ApprRejectData.id;
        this.reject = false;
        this.selectStaffReject = null;
        this.rejectInventoryData = [];
        const format = "yyyy-MM-dd";
        const locale = "en-US";
        const myDate = moment(this.ApprRejectData.paymentdate, "YYYY-MM-DD").toDate();
        const formattedDate = formatDate(myDate, format, locale);

        let rejectdata = {
            customerid: this.ApprRejectData.custId,
            idlist: Number(this.ApprRejectData.id),
            paymode: this.ApprRejectData.paymode,
            paystatus: this.ApprRejectData.status,
            paytodate: formattedDate,
            referenceno: this.ApprRejectData.receiptNo,
            remarks: this.approveRejectRemark
        };
        const url = "/payment/reject";
        this.dashboardService.postMethod(url, rejectdata).subscribe(
            (response: any) => {
                this.dialog?.closeAll();
                if (response.payment.dataList) {
                    this.reject = true;
                    this.rejectInventoryData = response.payment.dataList;
                    this.dialog.open(this.rejectCustomerInventoryModal, {
                        width: "600px",
                        disableClose: true
                    });
                } else {
                    this.toastr.success(`${response.responseMessage || "Rejected Successfully!"}`, "Success!");

                    this.getApprovalData();
                }
                this.ifApproveStatus = false;
                this.ApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    //Case
    caseId: any;

    approveTicket(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = true;
        this.ApprRejectData = data;
        this.ifShowRemarkMoedl = "Ticket";

        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }

    rejectTicket(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = false;
        this.ApprRejectData = data;
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Ticket";

        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }

    approveTask(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = true;
        this.ApprRejectData = data;
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Task";
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }

    rejectTask(data) {
        this.approveRejectRemark = "";
        this.ifApproveStatus = false;
        this.ApprRejectData = data;
        this.dialog.open(this.ApproveRejectRemarkModal, {
            width: "600px",
            disableClose: true
        });
        this.ifShowRemarkMoedl = "Task";
        this.paymentID = "";
        this.approveId = "";
        this.caseId = "";
    }
    statusApprovedTask() {
        // TODO: Add API call or logic to approve the task
        this.dialog?.closeAll();
    }

    statusRejectedTask() {
        // TODO: Add API call or logic to reject the task
        this.dialog?.closeAll();
    }

    statusApporevedTicket() {
        if (this.statusCheckService.isActiveTicketService) {
            this.caseId = this.ApprRejectData.caseId;
            this.approveId = this.ApprRejectData.caseId;
            this.approved = false;
            this.approveInventoryData = [];
            this.selectStaff = null;
            const url =
                "/case/approveTicket?caseId=" +
                this.caseId +
                "&isApproveRequest=true&remarks=" +
                this.approveRejectRemark;
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.dialog?.closeAll();
                    if (response.dataList) {
                        this.approved = true;
                        this.approveInventoryData = response.dataList;
                        this.dialog.open(this.assignCustomerInventoryModal, {
                            width: "600px",
                            disableClose: true
                        });
                    } else {
                        this.toastr.success(`${response.responseMessage}`, "Success!");

                        this.getApprovalData();
                    }
                    this.ifApproveStatus = false;
                    this.ApprRejectData = [];
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }
    statusRejectedTicket() {
        if (this.statusCheckService.isActiveTicketService) {
            this.caseId = this.ApprRejectData.caseId;
            this.approveId = this.ApprRejectData.caseId;
            this.reject = false;
            this.selectStaffReject = null;
            this.rejectInventoryData = [];
            const url =
                "/case/approveTicket?caseId=" +
                this.caseId +
                "&isApproveRequest=false&remarks=" +
                this.approveRejectRemark;
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.dialog?.closeAll();
                    if (response.dataList) {
                        this.reject = true;
                        this.rejectInventoryData = response.dataList;
                        this.dialog.open(this.rejectCustomerInventoryModal, {
                            width: "600px",
                            disableClose: true
                        });
                    } else {
                        this.toastr.success(`Rejected Successfully.`, "Success!");

                        this.getApprovalData();
                    }
                    this.ifApproveStatus = false;
                    this.ApprRejectData = [];
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }
    //Change Discount
    discountID: any;
    AppRjecHeader: string;
    discountFlageType: string;
    assignDiscountData: any;
    assignDiscounsubmitted: boolean = false;
    discountRejected(data) {
        this.discountID = data.id;
        this.dialog.open(this.rejectApproveDiscountModal, {
            width: "600px",
            disableClose: true
        });
        this.assignDiscountData = data;
        this.discountFlageType = "Rejected";
        this.AppRjecHeader = "Reject";
        this.assignAppRejectDiscountForm.reset();
    }

    discountApporeved(data) {
        this.discountID = data.id;
        this.dialog.open(this.rejectApproveDiscountModal, {
            width: "600px",
            disableClose: true
        });
        this.assignDiscountData = data;
        this.discountFlageType = "approved";
        this.AppRjecHeader = "Apporve ";
        this.assignAppRejectDiscountForm.reset();
    }
    assignDiscountApprove() {
        this.assignDiscounsubmitted = true;
        if (this.assignAppRejectDiscountForm.valid) {
            let url = "/approveChangeDiscount";
            let assignCAFData = {
                custPackageId: this.assignDiscountData.id,
                flag: this.discountFlageType,
                nextStaffId: 0,
                planId: this.assignDiscountData.planId,
                remark: this.assignAppRejectDiscountForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };

            this.dashboardService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    this.dialog?.closeAll();
                    if (response.dataList) {
                        if (this.discountFlageType == "approved") {
                            this.approved = true;
                            this.approveInventoryData = response.dataList;
                            this.dialog.open(this.assignCustomerInventoryModal, {
                                width: "600px",
                                disableClose: true
                            });
                        } else {
                            this.reject = true;
                            this.rejectInventoryData = response.dataList;
                            this.dialog.open(this.rejectCustomerInventoryModal, {
                                width: "600px",
                                disableClose: true
                            });
                        }
                    } else {
                        if (this.discountFlageType == "approved") {
                            this.toastr.success(`${response.message}`, "Approved Successfully!");
                        } else {
                            this.toastr.success(`${response.message}`, "Success!");
                        }
                        this.getApprovalData();
                    }
                    this.assignAppRejectDiscountForm.reset();
                    this.assignDiscounsubmitted = false;
                },
                (error: any) => {
                    // console.log(error, "error")

                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }

    pageChangedLeadList(pageNumber) {
        this.currentPageLeadListdata = pageNumber.pageIndex + 1;
        if (this.searchkey) {
        } else {
            if (this.statusCheckService.isActiveSalesCrm) {
                this.getLeadList(pageNumber.pageSize);
            }
        }
    }

    TotalItemPerPage(event) {
        this.currentPageLeadListdata = 1;
        this.showItemPerPage = Number(event.value);
        // if (this.currentPageLeadListdata > 1) {
        //   this.currentPageLeadListdata = 1;
        // }
        if (!this.searchkey) {
            if (this.statusCheckService.isActiveSalesCrm) {
                this.getLeadList(this.showItemPerPage);
            }
        } else {
        }
    }

    myStaffs: any;
    getLeadList(list) {
        if (this.statusCheckService.isActiveSalesCrm) {
            let size;
            this.searchkey = "";
            let page = this.currentPageLeadListdata;
            if (list) {
                size = list;
                this.leadListdataitemsPerPage = list;
            } else {
                size = this.leadListdataitemsPerPage;
            }

            const url =
                "/leadMaster/findAllByCurrentUserTeamLead?page=" +
                page +
                "&pageSize=" +
                size +
                "&mvnoId=" +
                localStorage.getItem("mvnoId");
            this.leadManagementService.getMethod(url).subscribe(
                async (response: any) => {
                    // await response?.leadMasterList?.content.forEach((leadItem: any) =>
                    //   Number(leadItem.createdBy)
                    // );

                    this.leadListData = await response?.leadMasterList?.content;

                    this.leadListdatatotalRecords = await response?.leadMasterList?.totalElements;

                    if (this.showItemPerPage > this.leadListdataitemsPerPage) {
                        this.leadListDatalength = this.leadListData?.length % this.showItemPerPage;
                    } else {
                        this.leadListDatalength = this.leadListData?.length % this.leadListdataitemsPerPage;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }

    totalItemPerPageForProductQtyByStaff(event): void {
        this.showItemPerPageProductQty = Number(event.value);
        if (this.currentPageProductQtyByStaffdata > 1) {
            this.currentPageProductQtyByStaffdata = 1;
        }
        if (this.statusCheckService.isActiveInventoryService) {
            this.getProductQtyByStaff(this.showItemPerPageProductQty);
        }
    }

    pageChangedForProductQtyByStaff(pageNumber): void {
        this.currentPageProductQtyByStaffdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveInventoryService) {
            this.getProductQtyByStaff(pageNumber.pageSize);
        }
    }

    totalItemPerPageForProductQtyByWarehouse(event): void {
        this.showItemPerPageProducyQtyByWarehouse = Number(event.value);
        if (this.currentPageProductQtyByWarehousedata > 1) {
            this.currentPageProductQtyByWarehousedata = 1;
        }
        if (this.statusCheckService.isActiveInventoryService) {
            this.getProductQtyByWarehouse(this.showItemPerPageProducyQtyByWarehouse);
        }
    }

    pageChangedForProductQtyByWarehouse(pageNumber): void {
        this.currentPageProductQtyByWarehousedata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveInventoryService) {
            this.getProductQtyByWarehouse(pageNumber.pageSize);
        }
    }

    getProductQtyByStaff(list): void {
        let size;
        const page = this.currentPageProductQtyByStaffdata;
        if (list) {
            size = list;
            this.productQtyListdataitemsPerPage = list;
        } else {
            size = this.productQtyListdataitemsPerPage;
        }

        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.getProductQtyByStaff(custerlist).subscribe(
            (response: any) => {
                this.productQTy = response.dataList;
                this.productQtytotalRecords = response.totalRecords;
                if (this.showItemPerPageProductQty > this.productQtyListdataitemsPerPage) {
                    this.productListDatalength = this.productQTy.length % this.showItemPerPageProductQty;
                } else {
                    this.productListDatalength = this.productQTy.length % this.productQtyListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    getProductQtyByWarehouse(list): void {
        let size;
        const page = this.currentPageProductQtyByWarehousedata;
        if (list) {
            size = list;
            this.productQtyListdataitemsbywarehousePerPage = list;
        } else {
            size = this.productQtyListdataitemsbywarehousePerPage;
        }

        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.getProductQtyByWarehouse(custerlist).subscribe(
            (response: any) => {
                this.productQtyByWarehouse = response.dataList;
                this.productQtyByWarehousetotalRecords = response.totalRecords;
                if (
                    this.showItemPerPageProducyQtyByWarehouse > this.productQtyListdataitemsbywarehousePerPage
                ) {
                    this.productListDatalength =
                        this.productQtyByWarehouse.length % this.showItemPerPageProducyQtyByWarehouse;
                } else {
                    this.productListDatalength =
                        this.productQtyByWarehouse.length % this.productQtyListdataitemsbywarehousePerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    getCustomerDocPendingApprovals(list): void {
        let size;
        const page = this.currentPageCustomerDocListdata;
        if (list) {
            size = list;
            this.customerDocListdataitemsPerPage = list;
        } else {
            size = this.customerDocListdataitemsPerPage;
        }

        const url = `/dashboard/approval/getCustomerDocForApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.dashboardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerDocListData = response.dataList;
                this.customerDocListDataselector = response.dataList;
                this.customerDocListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageCustomerDoc > this.customerDocListdataitemsPerPage) {
                    this.customerDocListDatalength =
                        this.customerDocListData.length % this.showItemPerPageCustomerDoc;
                } else {
                    this.customerDocListDatalength =
                        this.customerDocListData.length % this.customerDocListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    getInventoryPendingApprovals(list): void {
        let size;
        const page = this.currentPageInventoryPendingListdata;
        if (list) {
            size = list;
            this.inventoryPendingListdataitemsPerPage = list;
        } else {
            size = this.inventoryPendingListdataitemsPerPage;
        }

        const url = `/inwards/getInventoryApprovals`;
        const custerlist = {
            page,
            pageSize: size
        };
        this.inwardService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.inventoryPendingListData = response.dataList;
                // this.customerDocListDataselector = response.dataList;
                this.inventoryPendingListdatatotalRecords = response.totalRecords;
                if (this.showItemPerPageInventoryPending > this.inventoryPendingListdataitemsPerPage) {
                    this.inventoryPendingListDatalength =
                        this.inventoryPendingListData.length % this.showItemPerPageInventoryPending;
                } else {
                    this.inventoryPendingListDatalength =
                        this.inventoryPendingListData.length % this.inventoryPendingListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    totalItemPerPageForInventoryApprovals(event): void {
        this.showItemPerPageInventoryPending = Number(event.value);
        if (this.currentPageInventoryPendingListdata > 1) {
            this.currentPageInventoryPendingListdata = 1;
        }
        if (this.statusCheckService.isActiveInventoryService) {
            this.getInventoryPendingApprovals(this.showItemPerPageInventoryPending);
        }
    }

    pageChangedForInventoryApprovals(pageNumber): void {
        this.currentPageInventoryPendingListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveInventoryService) {
            this.getInventoryPendingApprovals(pageNumber.pageSize);
        }
    }
    totalItemPerPageForCustomerDocApprovals(event): void {
        this.showItemPerPageCustomerDoc = Number(event.value);
        if (this.currentPageCustomerDocListdata > 1) {
            this.currentPageCustomerDocListdata = 1;
        }
        this.getCustomerDocPendingApprovals(this.showItemPerPageCustomerDoc);
    }

    pageChangedForCustomerDocApprovals(pageNumber): void {
        this.currentPageCustomerDocListdata = pageNumber.pageIndex + 1;
        this.getCustomerDocPendingApprovals(pageNumber.pageSize);
    }
    viewLeadDashboard() {
        this.leadDashboardView = true;
        this.showFollowUpPendingData = false;
        this.showApprovalData = false;
        this.leadListFlag = true;
        this.leadFollowupFlag = true;
        this.leadListForUserAndTeamFlag = true;
        this.showProductQtyData = false;
        this.showSalseData = false;
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAssignedLeadList("");
            this.getFollowupLeadList("");
            this.getAllLeadsByCurrentUserTeamLead("");
            this.getAllfollowupsByCurrentUserTeamfollowup("");
            setTimeout(() => {
                this.cd.detectChanges();
            });
        }
    }
    viewInventoryDashboard() {
        this.showCustomerGraphs = false;
        this.showFollowUpPendingData = false;
        this.showPaymentGraphs = false;
        this.showTicketGraphs = false;
        this.radiusGraph = false;
        this.commissionGraph = false;
        this.inventoryGraph = false;
        this.showApprovalData = false;
        this.leadListFlag = false;
        this.leadFollowupFlag = false;
        this.leadDashboardView = false;
        this.leadListForUserAndTeamFlag = false;
        this.showProductQtyData = true;
        this.showSalseData = false;
        if (this.statusCheckService.isActiveInventoryService) {
            this.getProductQtyByStaff("");
            this.getProductQtyByWarehouse("");
            setTimeout(() => {
                this.cd.detectChanges();
            });
        }
    }
    //   CentralDairyDashboard() {
    //     this.showCustomerGraphs = false;
    //     this.showPaymentGraphs = false;
    //     this.showTicketGraphs = false;
    //     this.radiusGraph = false;
    //     this.commissionGraph = false;
    //     this.inventoryGraph = false;
    //     this.showApprovalData = false;
    //     this.leadListFlag = false;
    //     this.leadFollowupFlag = false;
    //     this.leadDashboardView = false;
    //     this.leadListForUserAndTeamFlag = false;
    //     this.showProductQtyData = true;
    //     if (this.statusCheckService.isActiveInventoryService) {
    //       this.getProductQtyByStaff("");
    //       this.getProductQtyByWarehouse("");
    //     }
    //   }
    assignedLeadListData: any;
    getAssignedLeadList(list) {
        let size;
        let page = this.currentPageAssignedLeadList;
        if (list) {
            size = list;
            this.assignedLeadListPageData = list;
        } else {
            size = this.assignedLeadListPageData;
        }
        const url =
            "/leadMaster/findAllByCurrentUser?page=" +
            page +
            "&pageSize=" +
            size +
            "&mvnoId=" +
            localStorage.getItem("mvnoId");

        this.leadManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.assignedLeadListData = await response?.leadMasterList?.content;

                this.assignedLeadListdatatotalRecords = await response?.leadMasterList?.totalElements;

                if (this.showItemPerPage > this.assignedLeadListPageData) {
                    this.leadListDatalength = this.assignedLeadListData?.length % this.showItemPerPage;
                } else {
                    this.leadListDatalength =
                        this.assignedLeadListData?.length % this.assignedLeadListPageData;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    pageChanged(pageNumber): void {
        this.currentPageAssignedLeadList = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAssignedLeadList(pageNumber.pageSize);
        }
    }

    TotalItems(event): void {
        this.showItemPerPage = Number(event.value);
        // if (this.currentPageAssignedLeadList > 1) {
        //   this.currentPageAssignedLeadList = 1;
        // }
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAssignedLeadList(this.showItemPerPage);
        }
    }

    labelFlag: any;
    leadObj: any;
    approveOrRejectLeadPopup(lead, flag) {
        if (lead.finalApproved) {
            if (flag === "Reject") {
                setTimeout(() => {
                    if (this.statusCheckService.isActiveSalesCrm) {
                        this.getAssignedLeadList("");
                    }
                }, 1000);
                error: error => {
                    this.toastr.success(`${error.message}`, "Assigned to the next staff!");
                };
            } else {
                // this.editLead(lead.id, lead.finalApproved);
                error: error => {
                    this.toastr.info(
                        `${error.message}`,
                        "Lead has been already prepared for 'Convert To CAF' operation. Go to 'Lead Management' screen for this.!"
                    );
                };
            }
        } else {
            this.approvedForLead = false;
            this.labelFlag = flag;
            this.leadObj = lead;
            if (flag === "Approve") this.leadApproveRejectDto.approveRequest = true;
            if (flag === "Reject") this.leadApproveRejectDto.approveRequest = false;

            if (this.staffid) this.leadApproveRejectDto.currentLoggedInStaffId = Number(this.staffid);
            this.leadApproveRejectDto.firstname = lead.firstname;
            this.leadApproveRejectDto.username = lead.username;
            this.leadApproveRejectDto.flag = flag;
            this.leadApproveRejectDto.status = lead.leadStatus;
            if (this.mvnoid) this.leadApproveRejectDto.mvnoId = Number(this.mvnoid);
            if (lead.serviceareaid) this.leadApproveRejectDto.serviceareaid = Number(lead.serviceareaid);
            if (lead.id) this.leadApproveRejectDto.id = Number(lead.id);
            if (lead.buId) this.leadApproveRejectDto.buId = Number(lead.buId);
            if (lead.nextTeamMappingId)
                this.leadApproveRejectDto.nextTeamMappingId = Number(lead.nextTeamMappingId);
            this.leadApproveRejectFormsubmitted = false;
            this.dialog.open(this.approveOrRejectLeadPopupRef, {
                width: "600px",
                disableClose: true
            });
        }
    }

    closeApproveOrRejectLeadPopup() {
        this.leadApproveRejectForm.reset();
        this.leadApproveRejectFormsubmitted = false;
        this.dialog?.closeAll();
    }

    isFinalApproved: boolean = false;
    approveOrRejectLead(leadObject: any) {
        if (leadObject?.finalApproved) this.isFinalApproved = true;

        this.leadApproveRejectFormsubmitted = true;
        let url = "/teamHierarchy/approveLead";

        if (this.leadApproveRejectForm.valid) {
            this.leadApproveRejectDto.remark = this.leadApproveRejectForm.controls.remark.value;

            this.customerManagementService.updateMethod(url, this.leadApproveRejectDto).subscribe(
                async (response: any) => {
                    this.leadApproveRejectFormsubmitted = false;
                    this.leadApproveRejectForm.reset();

                    if ((await response.dataList) && (await response.dataList.length) > 0) {
                        this.approveLeadList = await response.dataList;
                        this.approvedForLead = true;
                    } else {
                        this.dialog?.closeAll();

                        if (this.leadApproveRejectDto.approveRequest) {
                            if (response.data === "FINAL_APPROVED") {
                                // this.editLead(this.leadApproveRejectDto.id, true);
                                this.toastr.info(
                                    this.leadApproveRejectDto.id,
                                    "Lead has been already prepared for Convert To CAF operation. Go to Lead Management screen for this.!"
                                );
                            } else {
                                if (response.responseMessage === "Assigned to next staff") {
                                    if (this.statusCheckService.isActiveSalesCrm) {
                                        this.getAssignedLeadList("");
                                    }
                                    this.getAllLeadsByCurrentUserTeamLead("");
                                    this.toastr.success(`${response.message}`, "Success!");
                                }
                            }
                        } else {
                            if (this.statusCheckService.isActiveSalesCrm) {
                                this.getAssignedLeadList("");
                                this.getAllLeadsByCurrentUserTeamLead("");

                            }
                            this.toastr.success(`${response.responseMessage}`, "Success!");
                        }
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }

    approvedForLead = false;
    approveLeadList = [];
    selectStaffForLead: any = null;
    selectStaffRejectForLead: any = null;
    assignToStaffForLead(flag) {
        let url: any;

        if (flag == "Approve") {
            url = `/teamHierarchy/assignFromStaffListForLead?eventName=${"LEAD"}&nextAssignStaff=${this.selectStaffForLead
                }`;
        } else {
            url = `/teamHierarchy/assignFromStaffListForLead?eventName=${"LEAD"}&nextAssignStaff=${this.selectStaffRejectForLead
                }`;
        }

        this.customerManagementService.postMethod(url, this.leadApproveRejectDto).subscribe(
            async (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.error.responseMessage}`, "Failed!");

                    this.dialog?.closeAll();
                    if (this.statusCheckService.isActiveSalesCrm) {
                        this.getAssignedLeadList("");
                    }
                    this.toastr.success(`${response.responseMessage}`, "Success!");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
                this.dialog?.closeAll();
                if (this.statusCheckService.isActiveSalesCrm) {
                    this.getAssignedLeadList("");
                }
            }
        );
    }

    followupCurrentPageLeadListdata = 1;
    followupLeadListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    followupLeadListdatatotalRecords: any;
    followupLeadListData: any;

    getFollowupLeadList(list) {
        let size;
        let page = this.followupCurrentPageLeadListdata;
        if (list) {
            size = list;
            this.followupLeadListdataitemsPerPage = list;
        } else {
            size = this.followupLeadListdataitemsPerPage;
        }

        const url = "/followUp/findAllByCurruntUser?page=" + page + "&pageSize=" + size;

        this.leadManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.followupLeadListData = await response?.followUpList?.content;

                this.followupLeadListdatatotalRecords = await response?.followUpList?.totalElements;

                if (this.showItemPerPage > this.followupLeadListdataitemsPerPage) {
                    this.leadListDatalength = this.followupLeadListData?.length % this.showItemPerPage;
                } else {
                    this.leadListDatalength =
                        this.followupLeadListData?.length % this.followupLeadListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    pageFollowupChanged(pageNumber): void {
        this.followupCurrentPageLeadListdata = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getFollowupLeadList(pageNumber.pageSize);
        }
    }

    TotalFollowupItems(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.followupCurrentPageLeadListdata > 1) {
            this.followupCurrentPageLeadListdata = 1;
        }
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getFollowupLeadList(this.showItemPerPage);
        }
    }

    followupData: any;
    requiredFollowupInfo: any;
    tempLeadId: any;
    followUpId: any;
    rescheduleRemarks: any = [];
    rescheduleFollowUp(followUpDetails) {
        this.followupData = followUpDetails;
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getReScheduleFollowUpRemarksList();
            this.generateNameOfTheReFollowUp(followUpDetails.leadMasterId);
        }
        this.tempLeadId = followUpDetails.leadMasterId;
        this.followUpId = followUpDetails.id;
        this.reFollowupFormsubmitted = false;
        this.requiredFollowupInfo = {
            mvnoId: this.mvnoid,
            staffId: this.staffid,
            leadId: followUpDetails.leadMasterId
        };
        this.dialog.open(this.reScheduleFollowupModal, {
            width: "600px",
            disableClose: true
        });
    }

    saveReFollowup() {
        if (this.statusCheckService.isActiveSalesCrm) {
            this.followupData = {};
            this.reFollowupFormsubmitted = true;
            if (this.reFollowupScheduleForm.valid) {
                this.followupData.leadMasterId = this.tempLeadId;

                this.followupData = this.reFollowupScheduleForm.value;
                this.followupData.leadMasterId = this.tempLeadId;
                const myFormattedDate = this.datePipe.transform(
                    this.followupData.followUpDatetime,
                    "yyyy-MM-dd HH:mm:ss"
                );
                this.followupData.followUpDatetime = myFormattedDate;
                const url =
                    "/followUp/reSchedulefollowup?followUpId=" +
                    this.followUpId +
                    "&remarks=" +
                    this.followupData.remarksTemp;
                this.leadManagementService
                    .postMethod(url, this.followupData, this.mvnoid, this.staffid)
                    .subscribe(
                        (response: any) => {
                            this.reFollowupFormsubmitted = false;
                            this.reFollowupScheduleForm.reset();
                            this.toastr.success(`${response.message}`, "Success!");

                            this.dialog?.closeAll();
                            this.reFollowupFormsubmitted = false;
                            this.getFollowupLeadList("");
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, "Failed!");
                        }
                    );
                this.reFollowupFormsubmitted = false;
            }
        }
    }

    closeReFolloupPopup() {
        this.reFollowupFormsubmitted = false;
        this.dialog?.closeAll();
        this.reFollowupScheduleForm.reset();
    }

    generatedNameOfTheReFollowUp: any;
    generateNameOfTheReFollowUp(leadId) {
        const url = "/followUp/generateNameOfTheFollowUp/" + leadId;

        this.leadManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.generatedNameOfTheReFollowUp = await response.generatedNameOfTheFollowUp;
                this.generatedNameOfTheReFollowUp
                    ? this.reFollowupScheduleForm.controls["followUpName"].setValue(
                        this.generatedNameOfTheReFollowUp
                    )
                    : "";
            },
            async (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    makeACall() {
        this.toastr.info(`Sorry! Please configure call client first..`, "Info!");
        // this.messageService.add({
        //     severity: "info",
        //     summary: "Call configure",
        //     detail: "Sorry! Please configure call client first..",
        //     icon: ""
        // });
    }

    closeFollowUp(followUpDetails) {
        this.closeFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        this.dialog.open(this.closeFollowupModal, {
            width: "600px",
            disableClose: true
        });
    }

    closeActionFolloupPopup() {
        this.dialog.closeAll();
    }

    saveCloseFollowUp() {
        if (this.statusCheckService.isActiveSalesCrm) {
            this.closeFollowupFormsubmitted = true;
            if (this.closeFollowupForm.valid) {
                var closeData = this.closeFollowupForm.value;

                const url =
                    "/followUp/closefollowup?followUpId=" +
                    this.followUpId +
                    "&remarks=" +
                    this.closeFollowupForm.get("remarks").value;
                this.leadManagementService.getMethod(url).subscribe(
                    (response: any) => {
                        this.dialog?.closeAll();
                        this.closeFollowupForm.reset();

                        this.toastr.success(`${response?.message}`, "Success!");
                        this.getFollowupLeadList("");
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, "Failed!");
                    }
                );
                this.closeFollowupFormsubmitted = false;
            }
        }
    }

    getReScheduleFollowUpRemarksList() {
        const url = "/findAll/reScheduleFollowUpRemarks";
        this.leadManagementService.getMethodCMS(url).subscribe(
            async (response: any) => {
                this.rescheduleRemarks = await response.rescheduleFollowupRemarkList[0].split(",");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    leadListPageForUserAndTeam = 1;
    leadListItemsPerPageForUserAndTeam = RadiusConstants.ITEMS_PER_PAGE;
    leadListTotalRecordsForUserAndTeam: any;
    leadListForUserAndTeam: any;

    getAllLeadsByCurrentUserTeamLead(list) {
        let size;
        let page = this.leadListPageForUserAndTeam;
        if (list) {
            size = list;
            this.leadListItemsPerPageForUserAndTeam = list;
        } else {
            size = this.leadListItemsPerPageForUserAndTeam;
        }

        const url =
            "/leadMaster/findAllByCurrentUserTeamLead?page=" +
            page +
            "&pageSize=" +
            size +
            "&mvnoId=" +
            localStorage.getItem("mvnoId");

        this.leadManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.leadListForUserAndTeam = await response?.leadMasterList?.content;

                this.leadListTotalRecordsForUserAndTeam = await response?.leadMasterList?.totalElements;

                if (this.showItemPerPage > this.leadListItemsPerPageForUserAndTeam) {
                    this.leadListDatalength = this.leadListForUserAndTeam?.length % this.showItemPerPage;
                } else {
                    this.leadListDatalength =
                        this.leadListForUserAndTeam?.length % this.leadListItemsPerPageForUserAndTeam;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    pageChangedLeadListForUserAndTeam(pageNumber): void {
        this.leadListPageForUserAndTeam = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAllLeadsByCurrentUserTeamLead(pageNumber.pageSize);
        }
    }

    totalLeadListForUserAndTeamItems(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.leadListPageForUserAndTeam > 1) {
            this.leadListPageForUserAndTeam = 1;
        }
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAllLeadsByCurrentUserTeamLead(this.showItemPerPage);
        }
    }

    followupListPageForUserAndTeam = 1;
    followupListItemsPerPageForUserAndTeam = RadiusConstants.ITEMS_PER_PAGE;
    followupListTotalRecordsForUserAndTeam: any;
    followupListForUserAndTeam: any;

    getAllfollowupsByCurrentUserTeamfollowup(list) {
        let size;
        let page = this.followupListPageForUserAndTeam;
        if (list) {
            size = list;
            this.followupListItemsPerPageForUserAndTeam = list;
        } else {
            size = this.followupListItemsPerPageForUserAndTeam;
        }

        const url = "/followUp/findAllByCurruntUserAndTeam?page=" + page + "&pageSize=" + size;

        this.leadManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.followupListForUserAndTeam = await response?.followUpList?.content;

                this.followupListTotalRecordsForUserAndTeam = await response?.followUpList?.totalElements;

                if (this.showItemPerPage > this.followupListItemsPerPageForUserAndTeam) {
                    this.leadListDatalength = this.followupListForUserAndTeam?.length % this.showItemPerPage;
                } else {
                    this.leadListDatalength =
                        this.followupListForUserAndTeam?.length % this.followupListItemsPerPageForUserAndTeam;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    pageChangedfollowupListForUserAndTeam(pageNumber): void {
        this.followupListPageForUserAndTeam = pageNumber.pageIndex + 1;
        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAllfollowupsByCurrentUserTeamfollowup(pageNumber.pageSize);
        }
    }

    totalfollowupListForUserAndTeamItems(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.followupListPageForUserAndTeam > 1) {
            this.followupListPageForUserAndTeam = 1;
        }

        if (this.statusCheckService.isActiveSalesCrm) {
            this.getAllfollowupsByCurrentUserTeamfollowup(this.showItemPerPage);
        }
    }

    rejectCustomerDocumentOpen(partnerBalanceId, nextApproverId) {
        this.reject = false;
        this.selectStaff = null;
        this.rejectCAF = [];
        this.dialog.open(this.rejectCustomerCAFModal, {
            width: "600px",
            disableClose: true
        });
        this.customerDocumentId = partnerBalanceId;
        this.nextApproverId = nextApproverId;
    }

    approveCustomerDocumentOpen(partnerBalanceId, nextApproverId) {
        this.approved = false;
        this.selectStaff = null;
        this.approveCAF = [];
        this.dialog.open(this.assignCustomerCAFModal, {
            width: "600px",
            disableClose: true
        });
        this.customerDocumentId = partnerBalanceId;
        this.nextApproverId = nextApproverId;
    }
    ifApproveSPMStatus = false;
    approveRejectSPMRemark = "";
    apprRejectSPMData: any = [];
    assignStaffListDataSPM = [];
    assignedStaffSPM: any;
    staffIDSPM: number;
    mvnoIdSPM: number;
    viewPlanMappingData: any;
    approvestatusSPMModalOpen(data) {
        this.ifApproveSPMStatus = true;
        this.apprRejectSPMData = data;
        this.approveRejectSPMRemark = "";
        this.dialog.open(this.ApproveRejectModal, {
            width: "600px",
            disableClose: true
        });
    }

    rejectstatusSPMModalOpen(data) {
        this.ifApproveSPMStatus = false;
        this.apprRejectSPMData = data;
        this.approveRejectSPMRemark = "";
        this.dialog.open(this.ApproveRejectModal, {
            width: "600px",
            disableClose: true
        });
    }
    statusApporevedRejectedSPM() {
        const url1 = "/custspecialplanrelmapping/" + this.apprRejectSPMData.id;
        this.dashboardService.getMethod(url1).subscribe((response: any) => {
            this.viewPlanMappingData = response.mapping;
        });
        setTimeout(() => {
            let mappingData;
            if (this.viewPlanMappingData.planGroupMapping) {
                this.viewPlanMappingData.planGroupMapping.map(e => {
                    e.nextStaff = this.apprRejectSPMData.nextStaff;
                });
                mappingData = {
                    id: this.apprRejectSPMData.id,
                    name: this.apprRejectSPMData.name,
                    mvnoIdSPM: this.mvnoIdSPM,
                    planGroupMapping: this.viewPlanMappingData.planGroupMapping,
                    status: this.apprRejectSPMData.status,
                    flag: this.ifApproveSPMStatus ? "approved" : "rejected",
                    remarks: this.approveRejectSPMRemark,
                    nextStaff: this.apprRejectSPMData.nextStaff
                };
            } else {
                this.viewPlanMappingData.planMapping.map(e => {
                    e.nextStaff = this.apprRejectSPMData.nextStaff;
                });
                mappingData = {
                    id: this.apprRejectSPMData.id,
                    name: this.apprRejectSPMData.name,
                    mvnoIdSPM: this.mvnoIdSPM,
                    planMapping: this.viewPlanMappingData.planMapping,
                    status: this.apprRejectSPMData.status,
                    flag: this.ifApproveSPMStatus ? "approved" : "rejected",
                    remarks: this.approveRejectSPMRemark,
                    nextStaff: this.apprRejectSPMData.nextStaff
                };
            }
            const url = `/approveSpecialPlan`;
            this.dashboardService.updateMethod(url, mappingData).subscribe(
                (response: any) => {
                    if (response.dataList != null && response.dataList.length > 0) {
                        this.assignStaffListDataSPM = response.dataList;
                        this.dialog?.closeAll();
                        this.dialog.open(this.assignCustomerDocumentForApproval, {
                            width: "600px",
                            disableClose: true
                        });
                    } else {
                        this.dialog?.closeAll();
                        this.getSpecialPlanMappingApprovals("");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }, 1000);
    }
    assignToStaffSPMapping() {
        let url = "";
        if (this.assignedStaffSPM) {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.apprRejectSPMData.id}&eventName=SPACIAL_PLAN_MAPPING&nextAssignStaff=${this.assignedStaffSPM}&isApproveRequest=${this.ifApproveSPMStatus}`;
        } else {
            url = `/teamHierarchy/assignEveryStaff?entityId=${this.apprRejectSPMData.id
                }&eventName=${"SPACIAL_PLAN_MAPPING"}&isApproveRequest=${this.ifApproveSPMStatus}`;
        }
        this.dashboardService.getMethod(url).subscribe(
            response => {
                this.dialog?.closeAll();
                if (this.ifApproveSPMStatus) {
                    this.toastr.success(`${this.apprRejectSPMData}`, "Approved Successfully!");
                } else {
                    this.toastr.success(`${this.apprRejectSPMData}`, "Rejected Successfully!");
                }
                this.getSpecialPlanMappingApprovals("");
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    openInventoryDetailModal(modalId, data) {
        this.CustomerInventoryDetailsService.show(this.inventoryData);
        this.inventoryData.next({
            inventoryData: data
        });
    }

    approveAssignInventoryOpen(mappingId, nextApproverId, id) {
        this.approved = false;
        this.selectAssignInventoryApproveStaff = null;
        this.approveAssignInventoryData = [];
        this.dialog.open(this.assignApproveOtherInventoryOpen, {
            width: "695px",
            disableClose: true
        });
        this.approveAssignInventoryForm.reset();
        this.assignInventoryId = mappingId;
        this.customerInventoryId = id;
        this.nextApproverId = nextApproverId;
        this.rejectAssignInventoryForm.reset();
        this.rejectAssignInventorySubmitted = false;
    }
    rejectAssignInventoryOpen(mappingId, nextApproverId, id) {
        this.reject = false;
        this.selectAssignInventoryRejectStaff = null;
        this.rejectAssignInventoryData = [];
        this.dialog.open(this.assignRejectOtherInventoryOpen, {
            width: "695px",
            disableClose: true
        });
        this.rejectAssignInventoryForm.reset();
        this.assignInventoryId = mappingId;
        this.nextApproverId = nextApproverId;
        this.customerInventoryId = id;
        this.rejectAssignInventoryForm.reset();
        this.rejectAssignInventorySubmitted = false;
    }

    approveInventory(): void {
        if (this.statusCheckService.isActiveInventoryService) {
            let itemAssemblyId = this.assignInventoryId;
            const approveId = [];
            this.assignAssignInventorysubmitted = true;
            const selInventory = this.inventoryPendingListData.filter(
                data => data.custInventoryListId === itemAssemblyId
            );
            selInventory.forEach(inOutWardMACMapping => approveId.push(inOutWardMACMapping.id));
            const remarkAssign = this.approveAssignInventoryForm.value;
            let staffId = localStorage.getItem("userId");
            // const url = `/inwards/approveInventory?isApproveRequest=true&customerInventoryMappingId=${id}`;
            const url =
                "/inwards/approveInventory?isApproveRequest=true&nextstaff=" +
                staffId +
                "&remark=" +
                remarkAssign.remark;
            this.customerInventoryManagementService.postMethod(url, approveId).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.assignAssignInventorysubmitted = false;
                        this.approveAssignInventoryForm.reset();
                        if (response.dataList != null) {
                            this.approveAssignInventoryData = response.dataList;
                            this.approved = true;
                        } else {
                            this.dialog?.closeAll();
                            // this.getCustomerAssignedList();
                            this.getInventoryPendingApprovals("");
                        }
                    } else {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, "Failed!");
                }
            );
        }
    }

    rejectInventory(): void {
        if (this.statusCheckService.isActiveInventoryService) {
            const rejectId = [];
            let itemAssemblyId = this.assignInventoryId;
            const selInventory = this.inventoryPendingListData.filter(
                inventory => inventory.custInventoryListId === itemAssemblyId
            );
            selInventory.forEach(inOutWardMACMapping => rejectId.push(inOutWardMACMapping.id));
            const remarkReject = this.rejectAssignInventoryForm.value;
            let staffId = localStorage.getItem("userId");
            //const url = `/inwards/approveInventory?isApproveRequest=false&customerInventoryMappingId=${id}`;
            const url =
                "/inwards/approveInventory?isApproveRequest=false&nextstaff=" +
                staffId +
                "&remark=" +
                remarkReject.remark;

            this.customerInventoryManagementService.postMethod(url, rejectId).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.rejectAssignInventorySubmitted = false;
                        this.rejectAssignInventoryForm.reset();
                        if (response.dataList != null) {
                            this.rejectAssignInventoryData = response.dataList;
                            this.reject = true;
                        } else {
                            this.dialog?.closeAll();
                            this.getInventoryPendingApprovals("");
                        }
                    } else {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, "Failed!");
                }
            );
        }
    }

    assignToStaff2(flag) {
        if (this.statusCheckService.isActiveInventoryService) {
            let url: any;
            if (flag == true) {
                if (this.selectAssignInventoryApproveStaff) {
                    if (this.assignInventoryId != this.customerInventoryId) {
                        url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                            }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryApproveStaff
                            }&isApproveRequest=${flag}&isAssignPairItem=true`;
                    } else {
                        url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                            }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryApproveStaff
                            }&isApproveRequest=${flag}&isAssignPairItem=false`;
                    }
                } else {
                    url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
                }
            } else {
                if (this.selectAssignInventoryRejectStaff) {
                    if (this.assignInventoryId != this.customerInventoryId) {
                        url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                            }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryRejectStaff
                            }&isApproveRequest=${flag}&isAssignPairItem=true`;
                    } else {
                        url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                            }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryRejectStaff
                            }&isApproveRequest=${flag}&isAssignPairItem=false`;
                    }
                } else {
                    url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
                }
            }

            this.customerInventoryManagementService.getMethod(url).subscribe(
                response => {
                    this.dialog?.closeAll();
                    this.getInventoryPendingApprovals("");
                },
                error => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }
    clearassignToStaff() {
        this.rejectAssignInventoryForm.reset();
    }

    updateRowGroupMetaData() {
        this.rowGroupMetadata = {};

        if (this.assignedInventoryList) {
            for (let i = 0; i < this.assignedInventoryList.length; i++) {
                let rowData = this.assignedInventoryList[i];
                let representativeName = rowData.custInventoryListId ? rowData.custInventoryListId : null;

                if (i == 0) {
                    this.rowGroupMetadata[representativeName] = { index: 0, size: 1 };
                } else {
                    let previousRowData = this.assignedInventoryList[i - 1];
                    let previousRowGroup = previousRowData.custInventoryListId
                        ? previousRowData.custInventoryListId
                        : null;
                    if (representativeName === previousRowGroup) {
                        this.rowGroupMetadata[representativeName].size++;
                    } else {
                        this.rowGroupMetadata[representativeName] = { index: i, size: 1 };
                    }
                }
            }
        }
    }

    approveRemoveInventory(): void {
        if (this.statusCheckService.isActiveInventoryService) {
            this.assignRemoveInventorysubmitted = true;
            let mappingId = this.macMappingId;
            let custInventoryId = this.custInventoryId;
            // const ownershipFlag = this.ownershipForm.value;
            const removeRemark = this.approveRemoveInventoryForm.value;
            let staffId = localStorage.getItem("userId");
            // const url = `/inwards/approveInventory?isApproveRequest=true&customerInventoryMappingId=${id}`;
            const url = `/inoutWardMacMapping/removeInventory?&macMappingId=${mappingId}&customerInventoryId=${custInventoryId}&customerId=${this.custData.id}&isApprove=true&nextstaff=${staffId}&remark=${removeRemark.remark}`;

            this.customerInventoryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.responseCode == 200 || response.responseCode == 0) {
                        this.assignRemoveInventorysubmitted = false;
                        this.approveRemoveInventoryForm.reset();
                        if (response.dataList != null) {
                            this.approveRemoveInventoryData = response.dataList;
                            this.approveRemove = true;
                        } else {
                            this.dialog?.closeAll();
                        }

                        this.toastr.success(`${response.responseMessage}`, "Success!");

                        this.getInventoryPendingApprovals("");
                    } else {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, "Failed!");
                }
            );
        }
    }
    assignRemoveInventoryToStaff(flag) {
        if (this.statusCheckService.isActiveInventoryService) {
            let url: any;
            if (flag == true) {
                if (this.selectRemoveInventoryApproveStaff) {
                    url = `/inwards/assignFromStaffList?entityId=${this.assignRemoveInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectRemoveInventoryApproveStaff
                        }&isApproveRequest=${flag}&isAssignPairItem=false`;
                } else {
                    url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignRemoveInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
                }
            } else {
                if (this.selectRemoveInventoryRejectStaff) {
                    url = `/inwards/assignFromStaffList?entityId=${this.assignRemoveInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectRemoveInventoryRejectStaff
                        }&isApproveRequest=${flag}&isAssignPairItem=false`;
                } else {
                    url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignRemoveInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
                }
            }

            this.customerInventoryManagementService.getMethod(url).subscribe(
                response => {
                    this.dialog?.closeAll();
                    this.getInventoryPendingApprovals("");
                },
                error => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }

    clearapproveremoveInventory() {
        this.approveRemoveInventoryForm.reset();
    }

    formatToTimestamp(date: Date | null | undefined): string {
        if (!date) return "";

        const pad = (n: number) => (n < 10 ? "0" + n : n);
        const yyyy = date.getFullYear();
        const MM = pad(date.getMonth() + 1);
        const dd = pad(date.getDate());
        const HH = pad(date.getHours());
        const mm = pad(date.getMinutes());
        const ss = pad(date.getSeconds());

        return `${yyyy}-${MM}-${dd} ${HH}:${mm}:${ss}`;
    }

    onTabChange(event: MatTabChangeEvent) {
        // this.selectedTab = event.tab;
        if (event.tab.textLabel === "Pending Approvals") {
            this.getApprovalData();
        }
        if (event.tab.textLabel === "Sales Dashboard") {
            this.getSalseDashboardData();
        }
        if (event.tab.textLabel === "Lead Dashboard") {
            this.viewLeadDashboard();
        }
        if (event.tab.textLabel === "Inventory Dashboard") {
            this.viewInventoryDashboard();
        }
    }
    loadAllTabsData(): void {
        if (this.pendingApprovalAccess) {
            this.getApprovalData();
        } else if (this.salseDashboardAccess) {
            this.getSalseDashboardData();
        } else if (this.salseCrmAccess && this.statusCheckService.isActiveSalesCrm) {
            this.viewLeadDashboard();
        } else if (this.inventoryAccess && this.statusCheckService.isActiveInventoryService) {
            this.viewInventoryDashboard();
        }
    }
    closeDialog() {
        this.dialog.closeAll();
    }
}
