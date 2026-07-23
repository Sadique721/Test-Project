import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import moment from "moment";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { BehaviorSubject } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { DatePipe, formatDate } from "@angular/common";
import { element } from "protractor";
import { Regex } from "src/app/constants/regex";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { CustomerService } from "src/app/service/customer.service";
import { ToastrService } from 'ngx-toastr';
import { MatTableDataSource } from "@angular/material/table";
import { LoginService } from "src/app/service/login.service";
import { PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";

declare var $: any;

@Component({
    selector: "app-cust-change_plan",
    templateUrl: "./cust-change_plan.component.html",
    styleUrls: ["./cust-change_plan.component.scss"],
    standalone: false
})
export class CustChangePlanComponent implements OnInit {
    @ViewChild('planDetailsDialog') planDetailsDialog: TemplateRef<any>;
    @ViewChild('selectStaffDialog') selectStaffDialog: TemplateRef<any>;
    @ViewChild('parentCustomerDialog') parentCustomerDialog: TemplateRef<any>;
    @ViewChild('addChargeDialog') addChargeDialog: TemplateRef<any>;
    @ViewChild('recordPaymentDialog') recordPaymentDialog: TemplateRef<any>;
    @ViewChild('invoiceDialog') invoiceDialog: TemplateRef<any>;
    @ViewChild('planConnectionDialog') planConnectionDialog: TemplateRef<any>;
    @ViewChild('quotaPlanDialog') quotaPlanDialog: TemplateRef<any>;
    @ViewChild('amountDialog') amountDialog: TemplateRef<any>;
    @ViewChild('amountCheckDialogModal') amountCheckDialogModal: TemplateRef<any>;


    custType: any;
    customerId: number;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));

    changePlanNewForm: FormGroup;
    // changenewPlanForm: FormGroup;
    changePlanForm: FormGroup;
    plansArray: FormArray;
    customerChangePlanDueAmount: any = null;
    currency: string = 'INR';
    custDetails: any = {};
    planForConnection: any;
    changePlanTypeValue: any;
    changePlanTypeSelection: any;
    ChangePLanDateSelection: any;
    childCustList: any = [];
    childPlanGroup: any = [];
    newPlanSelection: any;
    planSelected: any;
    custPlanMapppingId: any;
    planGroupSelected: any;
    planDetails: any;
    newPlanGroupId: any;
    isAddCharge: boolean = false;
    planGroupChanges: any;
    newPlanGroupIdChild: any;
    selectedPlanCategory: any;
    billableCustList: any;
    parentCustomerDialogType: any = "";
    customerSelectType: any = "";
    selectedParentCust: any = [];
    currentIndex: number;
    planDiscount: number;

    changePlanSelect: boolean = false;
    selectedPlanType: any;
    selectedBillingCycle: any;
    planUpgradeDowngrade: any[];
    planBillingCycle: any[];
    planByService: any = [];
    selectedType: any;
    serviceURL: any;

    finalOfferPrice: number;

    custServiceData = [];
    serviceSerialNumbers = [];
    promiseToPayData = [];
    selectedChangePlan = [];
    newPlanData: any = {};
    planDropdownInChageData = [];

    custCustDiscountList: any = [];
    selectPlanListIDs = [];
    planMappingListData: any = [];
    groupOfferPrices = {};
    offerPrice = 0;

    isShowConnection = true;
    showPlanConnectionNo = false;
    isPromiseToPayModelOpen: boolean = false;
    changePlanSubmitted = false;
    ifPlanSelectChanePlan = false;
    ifPlanGroup = false;
    subisuChange = false;
    showParentCustomerModel = false;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    customerCurrentPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCurrentPlanListdatatotalRecords = 0;
    currentPagecustomerCurrentPlanListdata = 1;


    planDetailsCategory = [
        { label: "Individual", value: "individual" },
        { label: "Plan Group", value: "groupPlan" }
    ];
    displayPlanDetails: boolean = false;
    visibleQuotaDetails: boolean = false;
    showAddDirectCharge: boolean = false;
    showChargeDetails: boolean = false;
    selectchargeValueShow: boolean = false;
    chargesubmitted: boolean = false;

    overChargeListFromArray = this.fb.array([]);
    chargeGroupForm: FormGroup;

    overChargeListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    overChargeListtotalRecords: String;
    currentPageoverChargeList = 1;

    custPlansForCharge = {};
    plansForChargeByCust = [];
    plansForCharge = [];
    currentDate = new Date();
    chargeData = [];
    addedChargeList = [];
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];

    displayRecordPaymentDialog: boolean = false;
    displaySelectInvoiceDialog: boolean = false;
    submitted: boolean = false;
    masterSelected: boolean = false;
    isShowInvoiceList: boolean = false;
    destinationbank: boolean = false;
    paymentFormGroup: FormGroup;
    invoiceList: any = [{ docnumber: "Advance", id: 0, isSelected: false }];
    //   invoicedropdownValue = [{ docnumber: "Advance", id: 0 }];
    chequeDateName = "Cheque Date";
    selectedInvoice: any = [];
    onlineSourceData = [];
    tdsPercent: number;
    abbsPercent: number;
    expiryDate: Date;

    Amount: any = 0;
    paymentMode = [];
    bankDataList: any;
    bankDestination: any;
    paymentOwnerRequiredValue: any;
    paymentOwnerRequired: boolean = true;
    changePlanType: any[] = [];
    dateType: any[] = [];
    addOnEndDate: Date;
    addOnStartDate: Date;
    dateTime: Date = new Date();
    isoDateString: string;
    //   expiryDate: string;
    endDate: Date;
    skipQuotaUpdate: boolean = false;
    renewalForBooster: boolean = false;
    isoStartDateString: string;
    displayedColumns: string[] = [
        'serviceName',
        'serialNo',
        'planName',
        'planGroup',
        'validity',
        'planStatus',
        'startDate',
        'serviceExpiryDate',
        'billingEndDate',
        'remainingDays',
        'promiseToPay'
    ];

    parentPlanColumns: string[] = [
        'changePlan',
        'connection',
        'service',
        'currentPlan',
        'newPlan',
        'viewDetails'
    ];

    childPlanColumns: string[] = [
        'changePlan',
        'connection',
        'service',
        'currentPlan',
        'newPlan',
        'viewDetails'
    ];
    // Add these column definitions
    chargeListColumns: string[] = [
        'chargeName',
        'chargeAmount',
        'chargeType',
        'planName',
        'validity',
        'newPrice',
        'discount',
        'delete'
    ];

    invoiceColumns: string[] = [
        'select',
        'docNumber',
        'createdBy',
        'taxAmount',
        'totalInvoice',
        'pendingAmount',
        'refundableAmount',
        'amount',
        'tds',
        'abbs'
    ];

    planQuota = new BehaviorSubject({
        custid: "",
        PlanData: ""
    });

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
        finalAmount: "",
        downloadSpeed: "",
        uploadSpeed: "",
        downloadSpeedUnit: "",
        uploadSpeedUnit: "",
        burst_limit: "",
        burst_limit_unit: "",
        name: "",
        description: ""
    };
    planDetailsDialogRef: MatDialogRef<any>;
    amountDialogRef: MatDialogRef<any>;
    chargeDialogRef: MatDialogRef<any>;
    paymentDialogRef: MatDialogRef<any>;
    invoiceDialogRef: MatDialogRef<any>;
    parentCustomerDialogRef: MatDialogRef<any>;
    planConnectionDialogRef: MatDialogRef<any>;
    quotaPlanDialogRef: MatDialogRef<any>;
    selectStaffDialogRef: MatDialogRef<any>;

    staffSelectType: string = 'Payment Owner';
    selectedStaffCust: any = null;
    searchStaffDeatil: string = '';
    staffColumns: string[] = ['select', 'name', 'username'];
    staffListDatasource = new MatTableDataSource<any>();
    approveStaffListdataitemsPerPageForStaff: number = 5;
    approvestaffListdatatotalRecords: number = 0;
    currentPageApproveStaffListdata: number = 1;
    staffCustList: any[] = [];


    searchParentCustValue: string = '';
    customerColumns: string[] = ['select', 'name', 'mobile', 'acctno'];
    prepaidParentCustomerListDataSource = new MatTableDataSource<any>();
    parentCustomerListdataitemsPerPage: number = 5;
    parentCustomerListdatatotalRecords: number = 0;
    currentPageParentCustomerListdata: number = 1;
    prepaidParentCustomerList: any[] = [];
    amountCheckDialogRef: MatDialogRef<any>;
    customerInsufficientBalance: any;
    invoiceTableColumns: string[] = [
        'docnumber', 'createdBy', 'taxAmount', 'totalInvoice',
        'pendingAmount', 'refundableAmount', 'amount', 'tds', 'abbs'
    ];
    filteredPaymentModes: any[] = [];
    // planType: any[] = [
    //     { label: 'All', value: 'All' },
    //     { label: 'Downgrade', value: 'Downgrade' },
    //     { label: 'Upgrade', value: 'Upgrade' },
    // ];
    isAllowDowngradeChangePlan: boolean = false;
    constructor(
        private fb: FormBuilder,
        private datePipe: DatePipe,
        private customerService: CustomerService,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private messageService: MessageService,
        private customerManagementService: CustomermanagementService,
        private paymentamountService: PaymentamountService,
        public commonDropDownService: CommondropdownService,
        private route: ActivatedRoute,
        private router: Router,
        private confirmationService: ConfirmationService,
        private systemService: SystemconfigService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private dialog: MatDialog,
        private loginService: LoginService
    ) {
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    }

    async ngOnInit() {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;

        this.initData();
        this.commonDropDownService.getsystemconfigList()
        this.filteredPaymentModes = this.paymentMode;
        this.isAllowDowngradeChangePlan = this.custType === 'Prepaid' ? this.loginService.hasPermission(PRE_CUST_CONSTANTS.PRE_CUST_ALLOW_DOWNGRADE_CHANGE_PLAN) : false;
    }
    onPageChange(event: any) {
        this.currentPagecustomerCurrentPlanListdata = event.pageIndex + 1;
        this.customerCurrentPlanListdataitemsPerPage = event.pageSize;
        this.getserviceData();
    }
    getSelectedPaymentModeText(): string {
        const selected = this.paymentMode.find(
            m => m.value === this.paymentFormGroup.get('paymode')?.value
        );
        return selected ? selected.text : 'Select Payment Mode';
    }

    filterPaymentModes(event: any) {
        const search = event.target.value.toLowerCase();
        this.filteredPaymentModes = this.paymentMode.filter(mode =>
            mode.text.toLowerCase().includes(search)
        );
    }
    keypressId(event: KeyboardEvent) {
        const charCode = event.which ? event.which : event.keyCode;
        if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode !== 46) {
            return false;
        }
        return true;
    }
    getChangePlanType() {
        this.customerService.getChangePlanTypeList().subscribe((response: any) => {
            this.planUpgradeDowngrade = response.dataList;
            let plan = this.planUpgradeDowngrade.find(x => x.text == "Upgrade");
            this.selectedPlanType = plan.text;
            // this.changePlanForm.controls["planType"].setValue(this.planUpgradeDowngrade[0].value);
        });
    }

    onPlanTypeChange() {
        this.custServiceData.forEach(element => {
            element.changeFlag = false;
            element.newPlanSelection = null;
        });
    }

    getBillingCycle() {
        this.customerService.getBillingCycleList().subscribe((response: any) => {

            this.planBillingCycle = response.dataList;
        });
    }

    changeBillingCycle(event) {
        this.selectedBillingCycle = event.value;
    }

    initData() {
        this.changePlanNewForm = this.fb.group({
            isPaymentReceived: [false],
            remarks: ["", Validators.required],
            paymentOwnerId: ["", Validators.required],
            billableCustomerId: [""],
            externalRemark: [""],
            planType: ["upgrade"],
            isTriggerCoaDm: [true],
            billCycle: ["New billing cycle"]
        });
        this.changePlanForm = this.fb.group({
            connectionNo: [null, Validators.required],
            serviceName: [null],
            serviceNickName: [null],
            purchaseType: ["", Validators.required],
            planId: ["", Validators.required],
            planGroupId: ["", Validators.required],
            planList: [""],
            isPaymentReceived: [false],
            remarks: ["", Validators.required],
            paymentOwnerId: ["", Validators.required],
            billableCustomerId: [""],
            // addonStartDate: [this.currentData],
            ChangePlanCategory: [""],
            billCycle: ["New billing cycle"]
        });
        this.chargeGroupForm = this.fb.group({
            chargeid: ["", Validators.required],
            custId: [""],
            validity: ["", Validators.required],
            price: ["", Validators.required],
            actualprice: ["", Validators.required],
            charge_date: ["", Validators.required],
            type: ["One-time", Validators.required],
            staticIPAdrress: [""],
            planid: ["", Validators.required],
            unitsOfValidity: ["", Validators.required],
            discount: [""],
            billingCycle: [""]
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
            tdsAmount: [0],
            abbsAmount: [0],
            invoiceId: ["", Validators.required],
            onlinesource: [""],

        });
        this.getCustomersDetail(this.customerId);
        this.getserviceData();
        this.getcustDiscountDetails(this.customerId, "");
        this.getChildCustomersForChangePlan();

        this.getPlanPurchaseType();
        this.getChangePlanDate();
        // this.commonDropDownService.getChargeTypeByList();
        this.commonDropDownService.getPostpaidplanData();
        this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
            this.tdsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
            this.abbsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("PAYMENTOWNERREQUIRED").subscribe((res: any) => {
            this.paymentOwnerRequiredValue = res.data != null ? res.data.value : "true";
            if (this.paymentOwnerRequiredValue === "false") {
                this.paymentOwnerRequired = false;
            }
            if (this.paymentOwnerRequired === false) {
                // this logic is make payment owner mandetory or non mandetory with setting parameter
                this.changePlanNewForm.controls["paymentOwnerId"].clearValidators();
            } else {
                this.changePlanNewForm.controls["paymentOwnerId"].setValidators(Validators.required);
            }
            this.changePlanNewForm.controls["paymentOwnerId"].updateValueAndValidity();
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    refreshChangePlan() {
        this.getserviceData();
    }

    getCustomersDetail(custId: number) {
        const url = `/customers/${custId}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custDetails = response.customers;

                // ✅ SET BILLABLE CUSTOMER LIST
                this.billableCustList = [
                    {
                        id: this.custDetails.id,
                        name: `${this.custDetails.title} ${this.custDetails.custname}`
                    }
                ];


                // ✅ SET DEFAULT BILLABLE CUSTOMER IN FORM
                this.changePlanNewForm.patchValue({
                    billableCustomerId: this.custDetails.id
                });

                // Handle currency
                let currency;
                if (this.custDetails?.currency) {
                    currency = this.custDetails.currency;
                    this.commonDropDownService.getChargeTypeByList('', currency);
                } else {
                    this.systemService.getConfigurationByName('CURRENCY_FOR_PAYMENT')
                        .subscribe((res: any) => {
                            currency = res.data.value;
                            this.commonDropDownService.getChargeTypeByList('', currency);
                        });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: 'error',
                //     summary: 'Error',
                //     detail: error.error.ERROR,
                //     icon: 'far fa-times-circle'
                // });
            }
        );

        // ✅ SET PAYMENT OWNER (STAFF) FROM LOCALSTORAGE
        const loginUserName = localStorage.getItem('loginUserName');
        const userId = localStorage.getItem('userId');

        if (!Array.isArray(this.staffCustList) || this.staffCustList.length === 0) {
            this.staffCustList = [
                {
                    id: Number(userId),
                    name: loginUserName
                }
            ];
        }

        // ✅ SET DEFAULT PAYMENT OWNER IN FORM
        this.changePlanNewForm.patchValue({
            paymentOwnerId: Number(userId)
        });
    }

    getserviceData() {
        let services = [];
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerId +
            "?isAllRequired=true" +
            "&isNotChangePlan=false";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custServiceData = [];
                var keepGping = false;
                response.dataList.forEach(service => {
                    if (
                        !this.custServiceData.find(srv => srv.connection_no === service.connection_no) &&
                        service.custPlanStatus.toLowerCase() !== "newactivation" &&
                        (service.invoiceType == null ||
                            service.invoiceType == "" ||
                            service.invoiceType === "Independent")
                    ) {
                        this.custServiceData.push(service);
                    }
                });

                // this.selectedCustService.invoiceType === "Independent";

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
                    if (this.custServiceData[0]?.custServMappingStatus?.toLowerCase() === "hold") {
                        this.changePlanType = this.changePlanType.filter(
                            item => item.text !== "Change Plan"
                        );
                    }
                }
                let data = this.custServiceData;
                this.custServiceData = [];
                data.forEach(element => {
                    if (element.custPlanStatus.toLowerCase() != "terminate") {
                        this.custServiceData.push(element);
                    }
                });
                this.customerCurrentPlanListdatatotalRecords = this.custServiceData.length;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getChildCustomersForChangePlan() {
        let chargeAvailable: Boolean = false;
        const url = `/getAllChildCustomer?customerId=${this.customerId}&invoiceType=Group`;
        const data = {
            page: 1,
            pageSize: 5
        };
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                var childCustomerDataListForChangePlan = response.customerList;
                this.childCustList = childCustomerDataListForChangePlan;
                // this.childPlanGroup = childCustomerDataListForChangePlan.filter(e => e.plangroupid);
                childCustomerDataListForChangePlan.forEach((element, i) => {
                    // this.getplanChangeforplanGroup(element.id);
                    if (element.indiChargeList.length == 0) {
                        chargeAvailable = false;
                    } else {
                        chargeAvailable = true;
                    }
                    this.getcustDiscountDetails(element.id, "");
                    // const url = "/subscriber/getActivePlanList/" + element.id + "?isNotChangePlan=false";
                    const url =
                        "/subscriber/getPlanByCustService/" +
                        element.id +
                        "?isAllRequired=true" +
                        "&isNotChangePlan=false";
                    this.customerManagementService.getMethod(url).subscribe(
                        (response: any) => {
                            let childActivePlans = [];
                            let childActivePlanGroup = [];
                            response.dataList.forEach(item => {
                                if (
                                    item.invoiceType == "Group" &&
                                    item.plangroup !== "Volume Booster" &&
                                    item.plangroup !== "Bandwidthbooster" &&
                                    item.plangroup !== "DTV Addon" &&
                                    item.custPlanStatus.toLowerCase() !== "newactivation"
                                )
                                    childActivePlans.push(item);
                                // else if (
                                //   item.invoiceType == "Group" &&
                                //   item.plangroup !== "Volume Booster" &&
                                //   item.plangroup !== "Bandwidthbooster" &&
                                //   item.plangroup !== "DTV Addon" &&
                                //   item.custPlanStatus.toLowerCase() !== "newactivation"
                                // )
                                //   childActivePlanGroup.push(item);
                            });

                            // if (childActivePlanGroup.length > 0)
                            //   this.childPlanGroup[i].serviceMappingData = childActivePlanGroup;
                            if (childActivePlans.length > 0)
                                this.childCustList[i].serviceMappingData = childActivePlans;
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.msg}`, 'Failed!')
                            // this.messageService.add({
                            //     severity: "error",
                            //     summary: "Error",
                            //     detail: error.error.msg,
                            //     icon: "far fa-times-circle"
                            // });
                        }
                    );
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.msg,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    modalOpenDetails(newPlanId, connection_no, custId, selectedPlanCategory) {
        if (selectedPlanCategory == "groupPlan") {
            this.planGroupData[custId].forEach(e => {
                if (e.plan.id == newPlanId) this.planDetails = e.plan;
            });
        } else {
            this.newPlanData[connection_no].forEach(e => {
                if (e.id == newPlanId) this.planDetails = e;
            });
        }

        this.planDetailsDialogRef = this.dialog.open(this.planDetailsDialog, {
            width: '600px',
            disableClose: false
        });
        this.calculateExpiry();
    }

    closeDisplayPlanDetails() {
        if (this.planDetailsDialogRef) {
            this.planDetailsDialogRef.close();
        }
    }

    onChangePlanType(event) {
        this.changePlanSubmitted = false;

        this.newPlanGroupId = null;
        this.isAddCharge = false;
        this.selectedBillCycle = 'Current';
        this.newCycleDate = null;
        this.changePlanNewForm.reset();
        const userId = localStorage.getItem("userId");
        this.changePlanNewForm.patchValue({
            paymentOwnerId: Number(userId)
        });
        this.changePlanNewForm.patchValue({
            isPaymentReceived: false,
            billableCustomerId: this.custDetails.id,
            isTriggerCoaDm: true,
            billCycle: "New billing cycle",
            planType: 'Upgrade'
        });
        this.childCustList.forEach(element => {
            if (
                element.serviceMappingData != null &&
                element.serviceMappingData.length == 1
            ) {
                element.selectedPlanCategory = "individual";
            } else {
                element.selectedPlanCategory = null;
            }
            element.newPlanGroupId = null;
            element.isAddCharge = false;
            element.serviceMappingData.forEach(item => {
                item.changeFlag = false;
                item.newPlanSelection = null;
            });
        });
        if (
            this.custServiceData != null &&
            this.custServiceData.length == 1
        ) {
            this.selectedPlanCategory = "individual";
        } else {
            this.selectedPlanCategory = null;
        }
        this.custServiceData.forEach(element => {
            element.changeFlag = false;
            element.newPlanSelection = null;
        });
        this.getBillingCycle();
        if (event.value == "Changeplan") {
            this.changePlanSelect = true;
            this.getChangePlanType();
        } else {
            this.changePlanSelect = false;
        }
    }

    selectNewPlan(i, event, custServiceMapping, custId?, selectedPlanCategory?) {
        this.currentIndex = i;
        this.addEndDate(custServiceMapping.newPlanSelection, custServiceMapping.connection_no);
        this.getPlanDetailById(event, custServiceMapping);
    }

    addEndDate(newPlanId: any, connection_no: any) {
        // Locate the plan details based on the provided IDs
        this.newPlanData[connection_no].forEach(e => {
            if (e.id === newPlanId) {
                this.planDetails = e;
            }
        });


        if (this.planDetails && this.planDetails.validity) {
            const currentDate = new Date();
            const validity = this.planDetails.validity;
            const calculatedEndDate = new Date(currentDate);

            if (this.planDetails.unitsOfValidity.toLowerCase() === "years") {
                calculatedEndDate.setFullYear(currentDate.getFullYear() + validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "months") {
                const newMonth = currentDate.getMonth() + validity;
                calculatedEndDate.setMonth(newMonth);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "days") {
                calculatedEndDate.setDate(currentDate.getDate() + validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "hours") {
                calculatedEndDate.setHours(currentDate.getHours() + validity);
            } else {
                calculatedEndDate.setDate(currentDate.getDate() + validity);
            }

            this.addOnEndDate = calculatedEndDate;
            this.addOnStartDate = new Date();
            // this.isoStartDateString = moment(this.addOnStartDate).format('YYYY-MM-DDTHH:mm:ss.SSSZ');
            // this.isoDateString = moment(this.addOnEndDate).format('YYYY-MM-DDTHH:mm:ss.SSSZ');
            this.isoStartDateString = moment(this.addOnStartDate)
                .local()
                .format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
            this.isoDateString = moment(this.addOnEndDate).local().format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
        } else {
            this.addOnEndDate = null;
            this.addOnStartDate = null;
        }
    }

    onDateSelectStartDate(event) {
        const selectedDate = new Date(event);
        const currentTime = new Date();
        this.addOnStartDate = new Date(
            selectedDate.getFullYear(),
            selectedDate.getMonth(),
            selectedDate.getDate(),
            currentTime.getHours(),
            currentTime.getMinutes(),
            currentTime.getSeconds()
        );
        // this.isoStartDateString = moment(this.addOnStartDate).local().format('YYYY-MM-DDTHH:mm:ss.SSS[Z]');
        if (this.planDetails && this.planDetails.validity) {
            const currentDate = this.addOnStartDate;
            const validity = this.planDetails.validity;
            const calculatedEndDate = new Date(currentDate);

            if (this.planDetails.unitsOfValidity.toLowerCase() === "years") {
                calculatedEndDate.setFullYear(currentDate.getFullYear() + validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "months") {
                const newMonth = currentDate.getMonth() + validity;
                calculatedEndDate.setMonth(newMonth);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "days") {
                calculatedEndDate.setDate(currentDate.getDate() + validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "hours") {
                calculatedEndDate.setHours(currentDate.getHours() + validity);
            } else {
                calculatedEndDate.setDate(currentDate.getDate() + validity);
            }

            this.addOnEndDate = calculatedEndDate;
            this.isoStartDateString = moment(this.addOnStartDate)
                .local()
                .format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
            this.isoDateString = moment(this.addOnEndDate).local().format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
        }
    }

    onDateSelect(event) {
        const selectedDate = new Date(event);
        const currentTime = new Date();
        this.addOnEndDate = new Date(
            selectedDate.getFullYear(),
            selectedDate.getMonth(),
            selectedDate.getDate(),
            currentTime.getHours(),
            currentTime.getMinutes(),
            currentTime.getSeconds()
        );
        this.isoDateString = moment(this.addOnEndDate).local().format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
    }

    pageChangedcustomerCurrentPlanListData(pageNumber) {
        this.currentPagecustomerCurrentPlanListdata = pageNumber;
        this.getserviceData();
    }

    TotalCurrentPlanItemPerPage(event) {
        this.customerCurrentPlanListdataitemsPerPage = Number(event.value);
        if (this.currentPagecustomerCurrentPlanListdata > 1) {
            this.currentPagecustomerCurrentPlanListdata = 1;
        }
        this.getserviceData();
    }

    getSerialNumber(plan) {
        return plan.customerInventorySerialnumberDtos.filter(item => item.primary).length > 0
            ? plan.customerInventorySerialnumberDtos.filter(item => item.primary)[0].serialNumber
            : "";
    }

    openPlanConnectionModal(plan) {
        this.planForConnection = plan;
        this.planConnectionDialogRef = this.dialog.open(this.planConnectionDialog, {
            width: '600px',
            disableClose: false
        });
    }

    closeDialog() {
        this.planForConnection = null;
        if (this.planConnectionDialogRef) {
            this.planConnectionDialogRef.close();
        }
    }

    closeModel() {
        this.visibleQuotaDetails = false;
        this.planQuota = new BehaviorSubject({
            custid: "",
            PlanData: ""
        });
    }

    findDuration(expiryDate: Date) {
        var start = moment(new Date(new Date().setHours(0, 0, 0, 0)), "DD/MM/YYYY"); //todays date
        var end = moment(new Date(expiryDate), "DD/MM/YYYY"); // another date
        var duration = moment.duration(end.diff(start));

        var days = duration.asDays();
        return Math.trunc(days);
    }

    findDurationFromStartDate(startDate: Date, expiryDate: Date) {
        var start = moment(new Date(startDate), "DD/MM/YYYY"); //start date
        var currentDate = moment(new Date(new Date().setHours(0, 0, 0, 0)), "DD/MM/YYYY");
        if (currentDate <= start) start = currentDate;
        var end = moment(new Date(expiryDate), "DD/MM/YYYY"); // another date
        var duration = moment.duration(end.diff(start));

        var days = duration.asDays();
        return Math.trunc(days);
    }

    promiseToPayDetailsClick(id, startDate, endDate, days) {
        this.promiseToPayData = [{ startDate: startDate, endDate: endDate, days: days }];
        this.isPromiseToPayModelOpen = true;
        this.paymentamountService.show(id);
    }

    quotaPlanDetailsModel(modelID, custid, PlanData) {
        this.selPlanData = PlanData;
        this.paymentamountService.show(modelID);

        this.planQuota.next({
            custid,
            PlanData
        });
        this.quotaPlanDialogRef = this.dialog.open(this.quotaPlanDialog, {
            width: '800px',
            maxHeight: '90vh',
            disableClose: false
        });
    }

    closeQuotaPlanModal() {
        if (this.quotaPlanDialogRef) {
            this.quotaPlanDialogRef.close();
        }
    }
    getcustDiscountDetails(custId, discountType) {
        let custDiscountdatalength = 0;
        let url =
            "/subscriber/fetchCustomerDiscountDetailServiceLevel/" +
            custId +
            "?isExpiredRequired=" +
            (discountType === "changeDiscount");
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custCustDiscountList = [...this.custCustDiscountList, ...response.discountDetails];

                while (custDiscountdatalength < this.custCustDiscountList.length) {
                    // const planurl =
                    //   "/postpaidplan/" +
                    //   this.custCustDiscountList[custDiscountdatalength].planId;
                    // this.customerManagementService
                    //   .getMethod(planurl)
                    //   .subscribe((response: any) => {
                    //     this.dataDiscountPlan.push(response.postPaidPlan);
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
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getPlanChangeGroup(custData) {
        // this.newPlanGroupId = null;
        let url = "/getPlanGroupByFilters";
        let data = {
            changePlanType: this.changePlanTypeSelection.toLowerCase(),
            custId: custData.id,
            planGroupId: custData.plangroupid,
            customerServiceMappingID: custData.planMappingList[0].custServiceMappingId
        };
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.planGroupChanges = response;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    changePlanSelection(e, data, i, isChildPlan, childIdx) {
        if (e.checked) {
            let url = "/getPlansByFilters";
            var payload = {
                changePlanType: this.changePlanTypeSelection.toLowerCase(),
                currPlanId: data.planId,
                custId: data.custId,
                serviceId: data.serviceId,
                customerServiceMappingID: data.customerServiceMappingId,
                plantype: this.changePlanNewForm.value.planType.toLowerCase()
            };
            this.customerManagementService.postMethod(url, payload).subscribe(
                (response: any) => {
                    this.newPlanData[data.connection_no] = response.filter(
                        item => item.plantype == this.custType
                    );
                    this.newPlanData[data.connection_no].forEach(e => {
                        if (e.plantype == "Postpaid") {
                            e.label = e.name;
                        } else {
                            // if (e.planGroup !== "Bandwidthbooster") {
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
                            // } else e.label = e.name;
                        }
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            );
        } else {
            if (isChildPlan) {
                this.childCustList[childIdx].serviceMappingData[i].changeFlag = false;
                this.childCustList[childIdx].serviceMappingData[i].newPlanSelection = null;
            } else {
                this.custServiceData[i].changeFlag = false;
                this.custServiceData[i].newPlanSelection = null;
            }
        }
    }

    getPlanDetailById(event, custServiceMapping) {
        this.planDiscount = 0;
        // this.planDropdownInChageData = [];
        this.plansArray = this.fb.array([]);
        this.ifPlanSelectChanePlan = true;

        this.planSelected = event.value;
        const url = "/postpaidplan/" + this.planSelected;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.selPlanData = response.postPaidPlan;

                const date = new Date();
                this.selPlanData.activationDate = this.datePipe.transform(date, "dd-MM-yyyy");
                this.selPlanData.expiryDate = date.setDate(date.getDate() + this.selPlanData.validity);
                this.selPlanData.expiryDate = this.datePipe.transform(
                    this.selPlanData.expiryDate,
                    "dd-MM-yyyy"
                );
                this.selPlanData.finalAmount = this.selPlanData.offerprice + this.selPlanData.taxamount;
                let discountData = this.custCustDiscountList.find(
                    element => element.custId === custServiceMapping.custId
                );

                if (
                    discountData &&
                    discountData.discountType === "Recurring" &&
                    moment(discountData.discountExpiryDate).isSameOrAfter(moment(), "day") &&
                    (discountData.discount > 0 || discountData.discount < 0)
                ) {
                    this.confirmationService.confirm({
                        message: "Do you want to apply " + custServiceMapping.discount + " % of  Discount?",
                        header: "Change Discount Confirmation",
                        icon: "pi pi-info-circle",
                        accept: () => {
                            this.planDiscount = custServiceMapping.discount;
                            this.updateDiscountFromService(event.value, event.index);
                            //   this.custServiceData[this.currentIndex].newDiscount = discountData.discount;
                            custServiceMapping.newDiscount = custServiceMapping.discount;
                        },
                        reject: () => {
                            this.toastr.info(`You have rejected`, 'Info!')
                            // this.messageService.add({
                            //     severity: "info",
                            //     summary: "Rejected",
                            //     detail: "You have rejected"
                            // });
                            this.planDiscount = 0;
                            //   this.custServiceData[this.currentIndex].newDiscount = 0;
                            custServiceMapping.newDiscount = 0;
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
                    //   this.custServiceData[this.currentIndex].newDiscount = 0;
                    custServiceMapping.newDiscount = 0;
                    this.updateDiscountFromService(event.value, event.index);
                } else {
                    this.planDiscount = 0;
                    //   this.custServiceData[this.currentIndex].newDiscount = 0;
                    custServiceMapping.newDiscount = 0;
                    this.updateDiscountFromService(event.value, event.index);
                }
                if (this.plansForChargeByCust != null && this.plansForChargeByCust.length > 0) {
                    let index = this.plansForChargeByCust.findIndex(
                        item => item.connection_no == custServiceMapping.connection_no
                    );
                    if (index != -1) this.plansForChargeByCust.splice(index);
                }
                this.plansForChargeByCust.push({
                    connection_no: custServiceMapping.connection_no,
                    custId: custServiceMapping.custId,
                    planId: this.selPlanData.id,
                    planName: this.selPlanData.name,
                    unitsOfValidity: this.selPlanData.unitsOfValidity,
                    validity: this.selPlanData.validity,
                    discount: custServiceMapping.discount,
                    discountExpiryDate: custServiceMapping.discountExpiryDate,
                    discountType: custServiceMapping.discountType
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    updateDiscountFromService(id, index) {
        if (this.ifPlanGroup && this.changePlanForm.value.purchaseType !== "Addon") {
            this.custServiceData.find(serviceData => serviceData.newplan === id).discount =
                this.planDiscount;
            this.finalOfferPrice = 0;
            this.custServiceData.forEach(custChild => {
                if (index !== "") {
                    this.groupOfferPrices[index] = Number(this.selPlanData.offerprice);
                }
                if (custChild.newplan) {
                    this.customerManagementService
                        .getofferPriceWithTax(custChild.newplan, custChild.discount, this.planGroupSelected)
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
            this.custDetails.planMappingList[0].billTo == "ORGANIZATION" ||
            this.custDetails.planMappingList[0].billTo == "Organization"
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
                            isInvoiceToOrg: this.custDetails.isInvoiceToOrg,
                            istrialplan: this.custDetails.istrialplan
                            // invoiceType: this.customerGroupForm.value.invoiceType,
                        })
                    );
                    // }
                    // $("#selectPlanGroup").modal("show");
                },
                reject: () => {
                    this.subisuChange = false;
                    this.toastr.info(`You have rejected`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                    // $("#selectPlanChange").modal("show");
                }
            });
        }
        // }
    }
    planGroupData: any = {};
    selectPlanGroup(e, childIdx) {
        let planMappingListData = [];
        let data: any;

        this.planGroupChanges.forEach(element => {
            if (e.value == element.planGroupId) data = element;
        });
        data.planMappingList.forEach(d => {
            planMappingListData.push(d);
        });
        if (childIdx != -1) {
            this.planGroupData[this.childCustList[childIdx].id] = planMappingListData;
        } else {
            this.planGroupData[this.custDetails.id] = planMappingListData;
        }
    }

    selectPlanCategory(event, childIdx, custData) {
        if (event.value === "individual") {
            if (childIdx != -1) {
                this.childCustList[childIdx].newPlanGroupId = null;
                this.childCustList[childIdx].isAddCharge = false;
            } else {
                this.newPlanGroupId = null;
                this.isAddCharge = false;
            }
        }
        if (childIdx != -1) {
            this.childCustList[childIdx].serviceMappingData.map(item => {
                item.changeFlag = false;
                item.newPlanSelection = null;
            });
        } else {
            this.custServiceData.map(item => {
                item.changeFlag = false;
                item.newPlanSelection = null;
            });
        }
        if (event.value === "groupPlan") {
            this.getPlanChangeGroup(custData);
        }
        // this.planMappingListData = [];
        // let data: any;
        // this.planGroupChanges.forEach(element => {
        //   if (e.value == element.planGroupId) data = element;
        // });
        // data.planMappingList.forEach(d => {
        //   this.planMappingListData.push(d);
        // });
    }

    filterPlanGroup(service, childIdx) {
        if (childIdx != -1) {
            const planGroup = this.planGroupData[this.childCustList[childIdx]?.id];
            if (planGroup) {
                planGroup.forEach(element => {
                    if (element.service == service) element.inactive = false;
                    else element.inactive = true;
                });
            }
        } else {
            const planGroup = this.planGroupData[this.custDetails?.id];
            if (planGroup) {
                planGroup.forEach(element => {
                    if (element.service == service) element.inactive = false;
                    else element.inactive = true;
                });
            }
        }
    }

    getStatusClass(planStatus, workflowStatus) {
        let status = planStatus.toLowerCase();
        let statusWorkflow = workflowStatus ? workflowStatus.toLowerCase() : "";

        if (statusWorkflow == "new activation" || statusWorkflow == "rejected") {
            if (statusWorkflow == "new activation") return "status-success";
            else return "status-danger";
        } else {
            switch (status) {
                case "active":
                case "ingrace":
                    return "status-success";
                case "terminate":
                case "stop":
                case "inactive":
                case "expired":
                    return "status-danger";
                case "hold":
                case "disable":
                    return "status-primary";
                default:
                    return "";
            }
        }
    }


    getStatus(planStatus, workflowStatus) {
        let statusWorkflow = workflowStatus ? workflowStatus.toLowerCase() : "";
        if (statusWorkflow == "new activation" || statusWorkflow == "rejected") {
            return workflowStatus.toUpperCase();
        } else {
            return planStatus.toUpperCase();
        }
    }

    resetFormType() {
        this.changePlanSubmitted = false;
        // this.changePlanNewForm.reset();
        this.changePlanNewForm.patchValue({
            isPaymentReceived: false,
            billableCustomerId: this.custDetails.id,
            isTriggerCoaDm: true,
            billCycle: "New billing cycle",
            remarks: ''
        });
        this.custServiceData.forEach(element => {
            element.changeFlag = false;
            element.newPlanSelection = null;
        });
        this.childCustList.forEach(element => {
            element.serviceMappingData.forEach(item => {
                item.changeFlag = false;
                item.newPlanSelection = null;
            });
        });
        this.childPlanGroup.forEach(element => {
            element.serviceMappingData.forEach(item => {
                item.changeFlag = false;
                item.newPlanSelection = null;
            });
        });
    }



    async selectedCustChange(event) {
        this.showParentCustomerModel = false;
        this.selectedParentCust = event;

        this.billableCustList = [
            {
                id: this.selectedParentCust.id,
                name: this.selectedParentCust.name
            }
        ];
        // this.changePlanForm.patchValue({
        //   billableCustomerId: this.selectedParentCust.id,
        // });
        this.changePlanNewForm.patchValue({
            billableCustomerId: this.selectedParentCust.id
        });
    }
    closeParentCust() {
        this.showParentCustomerModel = false;
    }

    removeSelParentCust(type) {
        this.selectedParentCust = [];
        this.billableCustList = [];
        this.changePlanForm.patchValue({
            billableCustomerId: null
        });
    }
    isChequeModeSelected(): boolean {
        const paymode = this.paymentFormGroup?.get('paymode')?.value;
        if (!paymode) {
            return false;
        }

        // Check if payment mode requires cheque fields
        const chequeModes = ['Cheque', 'cheque', 'NEFT', 'neft', 'RTGS', 'rtgs', 'DD', 'dd'];
        return chequeModes.includes(paymode);
    }
    paymentFlagToggle(e) {
        if (e.value) {
            this.changePlanNewForm.controls["paymentOwnerId"].setValidators(Validators.required);
            if (this.paymentOwnerRequired === false) {
                // this logic is make payment owner mandetory or non mandetory with setting parameter
                this.changePlanNewForm.controls["paymentOwnerId"].clearValidators();
            } else {
                this.changePlanNewForm.controls["paymentOwnerId"].setValidators(Validators.required);
            }
            this.changePlanNewForm.controls["paymentOwnerId"].updateValueAndValidity();
        } else {
            this.changePlanNewForm.controls["paymentOwnerId"].clearValidators();
        }
        this.changePlanNewForm.controls["paymentOwnerId"].updateValueAndValidity();
        this.changePlanNewForm.patchValue({
            isPaymentReceived: e.value
        });
    }



    selectedStaff: any = [];
    paymentOwnerId;
    displayDTVHistory = false;
    staffid;
    displayAmountModel = false;
    custPackRelId: any;
    oldPlanId: any;
    // Add these properties to your component class
    selectedBillCycle: string = 'Current'; // Default value
    newCycleDate: Date | null = null;


    // Open Staff Modal
    modalOpenStaff(type: string) {
        this.staffSelectType = type === 'payment' ? 'Payment Owner' : 'Staff';
        this.searchStaffDeatil = '';
        this.selectedStaffCust = null;

        // Fetch staff list
        this.getStaffList();

        this.selectStaffDialogRef = this.dialog.open(this.selectStaffDialog, {
            width: '800px',
            disableClose: false
        });
    }

    // Get Staff List
    getStaffList() {
        const data = {
            page: this.currentPageApproveStaffListdata,
            pageSize: this.approveStaffListdataitemsPerPageForStaff,
            searchKey: this.searchStaffDeatil
        };

        const url = '/staffuser/Activestaff?product=BSS';
        this.savbillCommonBaseService.post(url, data).subscribe(
            (response: any) => {
                this.staffCustList = response.staffUserlist;
                this.approvestaffListdatatotalRecords = response?.pageDetails.totalRecords;
                this.staffListDatasource.data = this.staffCustList;
            },
            (error: any) => {
                console.error('Error fetching staff list', error);
            }
        );
    }

    // Search Staff
    onSearchStaff() {
        this.currentPageApproveStaffListdata = 1;
        this.getStaffList();
    }

    // Staff Selection
    onStaffSelect(staff: any) {
        this.selectedStaffCust = staff;
    }

    // Confirm Staff Selection
    confirmStaffSelection() {
        if (this.selectedStaffCust) {
            // Update form value
            this.changePlanNewForm.patchValue({
                paymentOwnerId: this.selectedStaffCust.id
            });

            // ✅ UPDATE staffCustList to include selected staff
            const existingStaff = this.staffCustList.find(s => s.id === this.selectedStaffCust.id);
            if (!existingStaff) {
                this.staffCustList = [
                    {
                        id: this.selectedStaffCust.id,
                        name: this.selectedStaffCust.name
                    }
                ];
            }

            this.closeStaffModal();
        }
    }

    // Close Staff Modal
    closeStaffModal() {
        if (this.selectStaffDialogRef) {
            this.selectStaffDialogRef.close();
        }
        this.selectedStaffCust = null;
    }

    // Staff Page Change
    onStaffPageChange(event: any) {
        this.currentPageApproveStaffListdata = event.pageIndex + 1;
        this.approveStaffListdataitemsPerPageForStaff = event.pageSize;
        this.getStaffList();
    }

    // Get Display Name for Staff
    getStaffDisplayName(): string {
        const paymentOwnerId = this.changePlanNewForm.value.paymentOwnerId;

        if (paymentOwnerId && this.staffListDatasource.data && this.staffListDatasource.data.length > 0) {
            const staff = this.staffListDatasource.data.find(s => s.id === paymentOwnerId);
            return staff ? staff.displayName : '';
        }
        return '';
    }

    // Open Parent/Billable Customer Modal
    modalOpenParentCustomer(type: string) {
        this.parentCustomerDialogType = type;
        this.customerSelectType = type === 'parent' ? 'Parent' : 'Billable';
        this.searchParentCustValue = '';
        this.selectedParentCust = null;

        // Fetch customer list
        this.getParentCustomerList();

        this.parentCustomerDialogRef = this.dialog.open(this.parentCustomerDialog, {
            width: '900px',
            disableClose: false
        });
    }

    // Get Parent Customer List
    getParentCustomerList() {
        const data = {
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage,
            searchKey: this.searchParentCustValue,
            custType: this.custType // Prepaid or Postpaid
        };

        const url = '/parentCustomers/list/Prepaid';
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                this.parentCustomerListdatatotalRecords = response.totalRecords;
                this.prepaidParentCustomerListDataSource.data = this.prepaidParentCustomerList;
            },
            (error: any) => {
                console.error('Error fetching customer list', error);
            }
        );
    }

    // Search Parent Customer
    onSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerList();
    }

    // Customer Selection
    onCustomerSelect(customer: any) {
        this.selectedParentCust = customer;
    }

    // Confirm Customer Selection
    confirmCustomerSelection() {
        if (this.selectedParentCust) {
            if (this.parentCustomerDialogType === 'billable') {
                // Update form value
                this.changePlanNewForm.patchValue({
                    billableCustomerId: this.selectedParentCust.id
                });

                // ✅ UPDATE billableCustList
                this.billableCustList = [
                    {
                        id: this.selectedParentCust.id,
                        name: `${this.selectedParentCust.title} ${this.selectedParentCust.firstname} ${this.selectedParentCust.lastname}`
                    }
                ];
            }
            this.closeParentCustomerModal();
        }
    }

    // Close Parent Customer Modal
    closeParentCustomerModal() {
        if (this.parentCustomerDialogRef) {
            this.parentCustomerDialogRef.close();
        }
        this.selectedParentCust = null;
    }

    // Customer Page Change
    onCustomerPageChange(event: any) {
        this.currentPageParentCustomerListdata = event.pageIndex + 1;
        this.parentCustomerListdataitemsPerPage = event.pageSize;
        this.getParentCustomerList();
    }

    // Get Display Name for Billable Customer
    getBillableCustomerDisplayName(): string {
        const billableCustomerId = this.changePlanNewForm.value.billableCustomerId;
        if (billableCustomerId && this.billableCustList && this.billableCustList.length > 0) {
            const customer = this.billableCustList.find(c => c.id === billableCustomerId);
            return customer ? customer.name : '';
        }
        return '';
    }

    modalOpenAmount() {
        this.getCustomerChangePlanDueAmount();
        this.amountDialogRef = this.dialog.open(this.amountDialog, {
            width: '500px',
            disableClose: false
        });
    }
    closeDisplayPlanAmountDetails() {
        if (this.amountDialogRef) {
            this.amountDialogRef.close();
        }
    }
    modalOpenAddCharge() {
        // OLD: this.showAddDirectCharge = true;
        // NEW:
        this.chargeDialogRef = this.dialog.open(this.addChargeDialog, {
            width: '900px',
            disableClose: false
        });
    }

    closeAddChargeModal() {
        if (this.chargeDialogRef) {
            this.chargeDialogRef.close();
        }
    }
    modalOpenRecordPayment() {
        // OLD: this.displayRecordPaymentDialog = true;
        // NEW:
        this.paymentDialogRef = this.dialog.open(this.recordPaymentDialog, {
            width: '90%',
            maxWidth: '1200px',
            disableClose: false
        });
    }
    getCustomerChangePlanDueAmount(isCheckAmount?) {
        this.custServiceData.forEach(element => {
            this.custPackRelId = element.planmapid;
            this.oldPlanId = element.planId;
        });

        let pojo = {
            custId: this.custDetails.id,
            custPackRelId: this.custPackRelId,
            oldPlanId: this.oldPlanId,
            newPlanId: this.selPlanData.id,
            changePlanBillingCycle: this.changePlanNewForm.value.billCycle,
            purchaseType: this.changePlanTypeSelection == 'Changeplan' ? 'Change Plan' : 'Renew'
        };
        const url = "/customers/getCustomerChangePlanDueAmount";
        this.customerManagementService
            .getCustomerChangePlanDueAmount(url, pojo)
            .subscribe((response: any) => {
                this.customerChangePlanDueAmount = response;
                if (isCheckAmount) {
                    if (!this.customerChangePlanDueAmount.Amount) {
                        this.prepareChangePlanPayload(null);
                    }
                    else {
                        this.amountCheckDialog();
                    }
                }
            });
    }

    amountCheckDialog() {
        this.amountCheckDialogRef = this.dialog.open(this.amountCheckDialogModal, {
            width: '500px',
            disableClose: false
        });
    }

    onConfirmAmount() {
        this.router.navigate([`/home/customer/details/Prepaid/x/${this.custDetails.id}`]);
        this.amountCheckDialogRef.close();
    }

    onCancelAmount() {
        this.amountCheckDialogRef.close();
    }

    selectedStaffChange(selectedStaff) {
        this.staffCustList = [
            {
                id: Number(selectedStaff.id),
                name: selectedStaff.firstname
            }
        ];
        this.changePlanForm.patchValue({
            paymentOwnerId: selectedStaff.id
        });
        this.changePlanNewForm.patchValue({
            paymentOwnerId: selectedStaff.id
        });
        this.paymentOwnerId = selectedStaff.id;
        this.displayDTVHistory = false;
        // this.closeStaff();
    }
    closeStaff() {
        this.displayDTVHistory = false;
        // $("#selectStaff").modal("hide");
    }
    removeSelStaff() {
        this.staffCustList = [];
        this.staffid = null;
    }

    private hasSelectedPlanCategory(planCategory: string): boolean {
        return this.childCustList.some(item => item.selectedPlanCategory === planCategory);
    }
    // Helper function to check if any item in the array has a null value for the specified field
    private hasNullValue(items: any[], fieldName: string): boolean {
        return items.some(item => item[fieldName] == null);
    }

    // Helper function to check if any item in the array has a non-null value for the specified field
    private hasNonNullValue(items: any[], fieldCheckbox: string, fieldName: string): boolean {
        return items.some(item => item[fieldName] != null);
    }

    changePlanGroupBulk() {
        this.changePlanSubmitted = true;
        let isOnePlanSelected = true;
        let isAnyFieldNull = false;

        // ✅ DEBUG: Check form values

        if (this.changePlanNewForm.value.isTriggerCoaDm == null) {
            this.changePlanNewForm.patchValue({
                isTriggerCoaDm: true
            });
        }

        if (!this.changePlanNewForm.valid) {
            return;
        }

        const isAddon = this.changePlanTypeSelection === "Addon";
        const hasIndividualSelected = this.hasSelectedPlanCategory("individual");
        const hasGroupPlanSelected = this.hasSelectedPlanCategory("groupPlan");

        if (this.changePlanTypeSelection == null || this.changePlanTypeSelection == "") {
            this.toastr.error(`Please select Change Plan Type.`, 'Failed!')
            // this.messageService.add({
            //     severity: "error",
            //     summary: "Required Details",
            //     detail: "Please select Change Plan Type.",
            //     icon: "far fa-times-circle"
            // });
            return;
        }

        if (!isAddon && this.childCustList.length === 0) {
            if (this.selectedPlanCategory === "individual") {
                isOnePlanSelected = this.hasNonNullValue(
                    this.custServiceData,
                    "changeFlag",
                    "newPlanSelection"
                );
            } else if (this.selectedPlanCategory === "groupPlan") {
                isAnyFieldNull = this.hasNullValue(this.custServiceData, "newPlanSelection");
            }
        } else if (
            !isAddon &&
            (this.selectedPlanCategory == null || this.selectedPlanCategory === "")
        ) {
            for (const item of this.childCustList) {
                if (item.selectedPlanCategory === "individual") {
                    isOnePlanSelected = this.hasNonNullValue(
                        item.serviceMappingData,
                        "changeFlag",
                        "newPlanSelection"
                    );
                } else if (item.selectedPlanCategory === "groupPlan") {
                    isAnyFieldNull = this.hasNullValue(item.serviceMappingData, "newPlanSelection");
                    if (isAnyFieldNull) {
                        this.errorMsg();
                        return;
                    }
                }
            }
        } else if (isAddon) {
            isOnePlanSelected = this.hasNonNullValue(
                this.custServiceData,
                "changeFlag",
                "newPlanSelection"
            );
            if (!isOnePlanSelected) {
                for (const item of this.childCustList) {
                    if (this.hasNonNullValue(item.serviceMappingData, "changeFlag", "newPlanSelection")) {
                        isOnePlanSelected = true;
                        break;
                    } else {
                        isOnePlanSelected = true;
                        isAnyFieldNull = false;
                    }
                }
            }
        } else {
            if (this.selectedPlanCategory === "individual") {
                isOnePlanSelected = this.hasNonNullValue(
                    this.custServiceData,
                    "changeFlag",
                    "newPlanSelection"
                );
                if (!isOnePlanSelected) {
                    for (const item of this.childCustList) {
                        if (hasIndividualSelected) {
                            isOnePlanSelected = this.hasNonNullValue(
                                item.serviceMappingData,
                                "changeFlag",
                                "newPlanSelection"
                            );
                            if (isOnePlanSelected) break;
                        } else if (hasGroupPlanSelected) {
                            isAnyFieldNull = this.hasNullValue(item.serviceMappingData, "newPlanSelection");
                            break;
                        }
                    }
                }
            } else if (this.selectedPlanCategory === "groupPlan") {
                isAnyFieldNull = this.hasNullValue(this.custServiceData, "newPlanSelection");
                if (isAnyFieldNull) {
                    for (const item of this.childCustList) {
                        if (hasIndividualSelected) {
                            isOnePlanSelected = this.hasNonNullValue(
                                item.serviceMappingData,
                                "changeFlag",
                                "newPlanSelection"
                            );
                            break;
                        } else if (hasGroupPlanSelected) {
                            isAnyFieldNull = this.hasNullValue(item.serviceMappingData, "newPlanSelection");
                            break;
                        }
                    }
                }
            }
        }

        if (!isOnePlanSelected || isAnyFieldNull) {
            this.errorMsg();
            return;
        }


        // ✅ KEY PAYMENT LOGIC FIX - Handle both string and boolean
        const isPaymentReceived = this.changePlanNewForm.value.isPaymentReceived;

        // ✅ Check for both "true" (string) and true (boolean)
        if (!isPaymentReceived) {
            if (this.commonDropDownService.AllowChangePlanWhenEnoughBalance === 'Yes') {
                if (this.changePlanTypeSelection == "Renew") {
                    this.getCustomerInsufficientBalance()
                }
                else if (this.changePlanTypeSelection == "Changeplan") {
                    this.getCustomerChangePlanDueAmount(true)
                }
                else {
                    this.prepareChangePlanPayload(null);
                }
            }
            else {
                this.prepareChangePlanPayload(null);
            }
        } else {
            this.openRecordPayment();
        }
    }

    getCustomerInsufficientBalance() {
        const url = "/customers/getCustomerChangePlanDueAmount";
        this.custServiceData.forEach(element => {
            this.custPackRelId = element.planmapid;
            this.oldPlanId = element.planId;
        });
        let data = {
            custId: this.custDetails.id,
            custPackRelId: this.custPackRelId,
            oldPlanId: this.oldPlanId,
            newPlanId: this.selPlanData.id,
            changePlanBillingCycle: this.changePlanNewForm.value.billCycle,
            purchaseType: this.changePlanTypeSelection == 'Changeplan' ? 'Change Plan' : 'Renew'
        };
        this.customerManagementService.getCustomerInsufficientBalance(url, data).subscribe(
            (response: any) => {
                this.customerInsufficientBalance = response;
                if (this.customerInsufficientBalance.Amount == 0) this.prepareChangePlanPayload(null);
                else if (this.customerInsufficientBalance.Amount != 0 || (this.customerInsufficientBalance.responseCode === 400 && this.customerInsufficientBalance.responseMessage === 'Insufficient wallet balance')) {
                    this.amountCheckDialog();
                }
            }
        );
    }


    prepareChangePlanPayload(recordPaymentPojo) {
        if (this.changePlanTypeSelection == "Renew") {
            let planBindWithOldPlans = [];
            let planList = [];
            let changePlanRequestDTOList = [];
            let pojo = {};
            if (this.selectedPlanCategory == "groupPlan") {
                let pojo = {
                    purchaseType: "Renew",
                    isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                    remarks: this.changePlanNewForm.value.remarks,
                    paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                    billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                    addonStartDate: null,
                    addonEndDate: null,
                    ChangePlanCategory: "",
                    isAdvRenewal: false,
                    custId: this.custDetails.id,
                    recordPaymentDTO: {},
                    isRefund: false,
                    planBindWithOldPlans: planBindWithOldPlans,
                    newPlanList: planList,
                    planMappingList: null,
                    isParent: true,
                    renewalForBooster: this.renewalForBooster || false
                };
                this.custServiceData.forEach(element => {
                    if (element.newPlanSelection != null) {
                        let data = {
                            newPlanId: element.newPlanSelection,
                            custServiceMappingId: element.customerServiceMappingId,
                            oldPlanId: element.planId,
                            discount: element.newDiscount
                        };
                        planList.push(element.newPlanSelection);
                        planBindWithOldPlans.push(data);
                        pojo["planGroupId"] = this.newPlanGroupId;
                        pojo["planBindWithOldPlans"] = planBindWithOldPlans;
                        pojo["custServiceMappingId"] = element.customerServiceMappingId;
                        pojo["newPlanList"] = planList;
                        pojo["planId"] = element.newPlanSelection;
                    }
                });
                changePlanRequestDTOList.push(pojo);
            } else if (this.selectedPlanCategory == "individual") {
                this.custServiceData.forEach(element => {
                    if (element.newPlanSelection != null) {
                        let pojo = {
                            purchaseType: "Renew",
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            remarks: this.changePlanNewForm.value.remarks,
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            addonStartDate: null,
                            addonEndDate: null,
                            ChangePlanCategory: "",
                            isAdvRenewal: false,
                            custId: this.custDetails.id,
                            recordPaymentDTO: {},
                            isRefund: false,
                            planBindWithOldPlans: planBindWithOldPlans,
                            newPlanList: null,
                            planMappingList: null,
                            isParent: true,
                            renewalForBooster: this.renewalForBooster || false
                        };

                        pojo["discount"] = element.newDiscount;
                        pojo["planId"] = element.newPlanSelection;
                        pojo["custServiceMappingId"] = element.customerServiceMappingId;
                        changePlanRequestDTOList.push(pojo);
                    }
                });
            }

            if (this.childCustList.length > 0) {
                this.childCustList.forEach(childCust => {
                    let childPlanBindWithOldPlans = [];
                    let childPlanList = [];
                    if (childCust.serviceMappingData.length > 0) {
                        if (childCust.selectedPlanCategory != null && childCust.selectedPlanCategory != "") {
                            if (childCust.selectedPlanCategory == "groupPlan") {
                                let pojo = {
                                    purchaseType: "Renew",
                                    isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                                    remarks: this.changePlanNewForm.value.remarks,
                                    paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                                    billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                                    addonStartDate: null,
                                    addonEndDate: null,
                                    ChangePlanCategory: "",
                                    isAdvRenewal: false,
                                    custId: childCust.id,
                                    recordPaymentDTO: {},
                                    isRefund: false,
                                    planBindWithOldPlans: childPlanBindWithOldPlans,
                                    newPlanList: childPlanList,
                                    planMappingList: null,
                                    isParent: false
                                };
                                childCust.serviceMappingData.forEach(element => {
                                    if (element.newPlanSelection != null) {
                                        let data = {
                                            newPlanId: element.newPlanSelection,
                                            custServiceMappingId: element.customerServiceMappingId,
                                            oldPlanId: element.planId,
                                            discount: element.newDiscount
                                        };
                                        childPlanList.push(element.newPlanSelection);
                                        childPlanBindWithOldPlans.push(data);
                                        pojo["planGroupId"] = childCust.newPlanGroupId;
                                        pojo["planBindWithOldPlans"] = childPlanBindWithOldPlans;
                                        pojo["custServiceMappingId"] = element.customerServiceMappingId;
                                        pojo["newPlanList"] = childPlanList;
                                        pojo["planId"] = element.newPlanSelection;
                                    }
                                });
                                changePlanRequestDTOList.push(pojo);
                            } else if (childCust.selectedPlanCategory == "individual") {
                                childCust.serviceMappingData.forEach(element => {
                                    if (element.newPlanSelection != null) {
                                        let pojo = {
                                            purchaseType: "Renew",
                                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                                            remarks: this.changePlanNewForm.value.remarks,
                                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                                            addonStartDate: null,
                                            addonEndDate: null,
                                            ChangePlanCategory: "",
                                            isAdvRenewal: false,
                                            custId: childCust.id,
                                            recordPaymentDTO: {},
                                            isRefund: false,
                                            planBindWithOldPlans: childPlanBindWithOldPlans,
                                            newPlanList: null,
                                            planMappingList: null,
                                            isParent: false
                                        };
                                        pojo["discount"] = element.newDiscount;
                                        pojo["planId"] = element.newPlanSelection;
                                        pojo["custServiceMappingId"] = element.customerServiceMappingId;
                                        changePlanRequestDTOList.push(pojo);
                                    }
                                });
                            }
                        }
                    }
                });
            }
            let finalRenewData: any = {
                changePlanRequestDTOList: changePlanRequestDTOList,
                recordPayment: null
            };
            if (recordPaymentPojo != null) {
                finalRenewData.recordPayment = recordPaymentPojo;
            }
            this.addChargeDataInRenew(finalRenewData);
        } else if (this.changePlanTypeSelection == "Addon") {
            let changePlanRequestDTOList = [];
            this.custServiceData.forEach(element => {
                if (element.newPlanSelection != null) {
                    let addonPojo = {
                        connectionNo: element.connectionNo,
                        serviceName: element.serviceName,
                        serviceNickName: element.serviceName,
                        purchaseType: "Addon",
                        planId: element.newPlanSelection,
                        // planGroupId: this.newPlanGroupId,
                        isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                        remarks: this.changePlanNewForm.value.remarks,
                        paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                        billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                        addonStartDate: this.isoStartDateString,
                        addonEndDate: this.isoDateString,
                        ChangePlanCategory: "",
                        isAdvRenewal: false,
                        custId: this.custDetails.id,
                        recordPaymentDTO: {},
                        isRefund: false,
                        discount: element.newDiscount,
                        planBindWithOldPlans: [],
                        newPlanList: null,
                        planMappingList: null,
                        custServiceMappingId: element.customerServiceMappingId,
                        isParent: true,
                        renewalForBooster: this.renewalForBooster || false
                    };
                    if (this.custDetails.plangroupid != null) {
                        addonPojo["planGroupId"] = this.custDetails.plangroupid;
                    }
                    changePlanRequestDTOList.push(addonPojo);
                }
            });
            this.childCustList.forEach(childCust => {
                childCust.serviceMappingData.forEach(element => {
                    if (element.newPlanSelection != null) {
                        let addonPojo = {
                            purchaseType: "Addon",
                            planId: element.newPlanSelection,
                            // planGroupId: childCust.newPlanGroupId,
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            remarks: this.changePlanNewForm.value.remarks,
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            addonStartDate: this.isoStartDateString,
                            addonEndDate: this.isoDateString,
                            ChangePlanCategory: "",
                            isAdvRenewal: false,
                            custId: childCust.id,
                            recordPaymentDTO: {},
                            isRefund: false,
                            discount: element.newDiscount,
                            planBindWithOldPlans: [],
                            newPlanList: null,
                            planMappingList: null,
                            custServiceMappingId: element.customerServiceMappingId,
                            isParent: false,
                            renewalForBooster: this.renewalForBooster || false
                        };
                        if (childCust.plangroupid != null) {
                            addonPojo["planGroupId"] = childCust.plangroupid;
                        }
                        changePlanRequestDTOList.push(addonPojo);
                    }
                });
            });

            changePlanRequestDTOList.forEach(
                obj => (obj.renewalForBooster = this.renewalForBooster || false)
            );

            let finalAddonData: any = {
                changePlanRequestDTOList: changePlanRequestDTOList,
                recordPayment: null,
                isTriggerCoaDm: this.changePlanNewForm.value.isTriggerCoaDm,
                renewalForBooster: this.renewalForBooster || false
            };
            if (recordPaymentPojo != null) {
                finalAddonData.recordPayment = recordPaymentPojo;
            }
            this.addOnPlans(finalAddonData);
        } else {
            let deactivatePlanReqModels = [];
            let deactivatePlanReqModelsChild = [];
            let finalData = {
                deactivatePlanReqDTOS: [],
                recordPayment: null,
                skipQuotaUpdate:
                    this.skipQuotaUpdate === null || this.skipQuotaUpdate === undefined
                        ? false
                        : this.skipQuotaUpdate,

            };
            if (
                this.selectedPlanCategory != null &&
                this.selectedPlanCategory !== undefined &&
                this.selectedPlanCategory != ""
            ) {
                this.custServiceData.forEach(element => {
                    if (element.newPlanSelection != null) {
                        let data = {
                            billToOrg: false,
                            newPlanGroupId: this.newPlanGroupId,
                            planGroupId: this.custDetails.plangroupid,
                            newPlanId: element.newPlanSelection,
                            custServiceMappingId: element.customerServiceMappingId,
                            discount: element.newDiscount
                        };
                        deactivatePlanReqModels.push(data);
                    }
                });
                if (deactivatePlanReqModels.length > 0) {
                    finalData.deactivatePlanReqDTOS.push({
                        custId: this.custDetails.id,
                        deactivatePlanReqModels: deactivatePlanReqModels,
                        isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                        planGroupChange: this.selectedPlanCategory === "groupPlan",
                        planGroupFullyChanged: this.selectedPlanCategory === "groupPlan",
                        paymentOwner: "yogesh Patil",
                        paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                        billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                        isParent: true,
                        remark: this.changePlanNewForm.value.remarks,
                        changePlanDate: this.ChangePLanDateSelection,
                        changePlanBillingCycle: this.changePlanNewForm.value.billCycle

                    });
                }
            }

            this.childCustList.forEach(childCust => {
                if (
                    childCust.selectedPlanCategory != null &&
                    childCust.selectedPlanCategory !== undefined &&
                    childCust.selectedPlanCategory != ""
                ) {
                    deactivatePlanReqModelsChild = [];
                    childCust.serviceMappingData.forEach(element => {
                        if (element.newPlanSelection) {
                            let changeDetails = {
                                billToOrg: false,
                                newPlanGroupId: childCust.newPlanGroupId,
                                planGroupId: childCust.plangroupid,
                                newPlanId: element.newPlanSelection,
                                custServiceMappingId: element.customerServiceMappingId,
                                discount: element.newDiscount
                            };
                            deactivatePlanReqModelsChild.push(changeDetails);
                        }
                    });
                    if (deactivatePlanReqModelsChild.length > 0) {
                        let childPojo = {
                            custId: childCust.id,
                            deactivatePlanReqModels: deactivatePlanReqModelsChild,
                            planGroupChange: childCust.selectedPlanCategory === "groupPlan",
                            isPaymentReceived: this.changePlanNewForm.value.isPaymentReceived,
                            planGroupFullyChanged: childCust.selectedPlanCategory === "groupPlan",
                            paymentOwner: "yogesh Patil",
                            paymentOwnerId: this.changePlanNewForm.value.paymentOwnerId,
                            billableCustomerId: this.changePlanNewForm.value.billableCustomerId,
                            isParent: false,
                            remark: this.changePlanNewForm.value.remarks,

                        };
                        finalData.deactivatePlanReqDTOS.push(childPojo);
                    }
                }
            });
            if (recordPaymentPojo != null) {
                finalData.recordPayment = recordPaymentPojo;
            }

            this.changePlans(finalData);
        }
    }

    addChargeDataInRenew(finalRenewData) {
        finalRenewData.custChargeDetailsList = this.chargeData;
        this.renewPlans(finalRenewData);
    }

    renewPlans(finalRenewData) {
        let url = "/subscriber/changePlan01";
        this.customerManagementService.postMethod(url, finalRenewData).subscribe(
            (response: any) => {
                if (response.responseCode == 417 || response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`Successfull`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: "",
                    //     icon: "far fa-check-circle"
                    // });
                }
                this.resetForm();
                this.initData();
            },
            (error: any) => {
                console.log(error);
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    addOnPlans(finalAddonData) {
        let url = "/subscriber/changePlan01";
        this.customerManagementService.postMethod(url, finalAddonData).subscribe(
            (response: any) => {
                if (response.responseCode == 417 || response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`Successfully`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: "",
                    //     icon: "far fa-check-circle"
                    // });
                }
                this.resetForm();
                this.initData();
            },
            (error: any) => {
                console.log(error);
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }



    changePlans(finalData) {
        let url = "/subscriber/deactivatePlanInBulk";
        this.customerManagementService.postMethod(url, finalData).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`Successfully`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: "",
                    //     icon: "far fa-check-circle"
                    // });
                }
                this.resetForm();
                this.initData();
            },
            (error: any) => {
                if (error.error.status == 417) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            }
        );
    }

    errorMsg() {
        this.toastr.error(`Please select at least one new plan.`, 'Failed!')
        // this.messageService.add({
        //     severity: "error",
        //     summary: "Required Details",
        //     detail: "Please select at least one new plan.",
        //     icon: "far fa-times-circle"
        // });
    }

    resetForm() {
        this.changePlanSubmitted = false;
        this.changePlanTypeSelection = null;
        this.selectedPlanCategory = null;
        this.newPlanGroupId = null;
        this.isAddCharge = false;
        this.changePlanNewForm.reset();
        this.changePlanNewForm.patchValue({
            isPaymentReceived: false,
            billableCustomerId: this.custDetails.id,
            isTriggerCoaDm: true
        });
        this.childCustList.forEach(element => {
            element.changePlanTypeSelection = null;
            element.newPlanGroupId = null;
            element.isAddCharge = false;
            element.serviceMappingData.forEach(item => {
                item.changeFlag = false;
                item.newPlanSelection = null;
            });
        });
        this.custServiceData.forEach(element => {
            element.changeFlag = false;
            element.newPlanSelection = null;
        });
    }

    //Direct Charge developement

    selectcharge(_event: any) {
        const chargeId = _event.value;
        let viewChargeData;
        let date;

        date = this.currentDate.toISOString();
        const format = "yyyy-MM-dd";
        const locale = "en-US";
        const myDate = date;
        const formattedDate = formatDate(myDate, format, locale);
        //
        const url = "/charge/" + chargeId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            viewChargeData = response.chargebyid;
            this.selectchargeValueShow = true;
            this.chargeGroupForm.patchValue({
                actualprice: Number(viewChargeData.actualprice),
                charge_date: formattedDate,
                type: "One-time"
            });
        });
    }

    isStaticIPAdrress(chargeid) {
        if (chargeid !== null && chargeid !== undefined && chargeid !== "") {
            return (
                this.commonDropDownService.chargeByTypeData.filter(
                    charge => charge.id === chargeid && charge.chargecategory === "IP"
                ).length > 0
            );
        } else {
            return false;
        }
    }

    getPlanValidityForChagre(event) {
        const planId = event.value;

        // const url = "/postpaidplan/" + planId;
        // this.customerManagementService.getMethod(url).subscribe((response: any) => {
        //   const planDetailData = response.postPaidPlan;
        let selectedPlan = this.plansForCharge.find(item => item.planId == event.value);

        this.chargeGroupForm.patchValue({
            validity: Number(selectedPlan.validity),
            unitsOfValidity: selectedPlan.unitsOfValidity
        });

        if (
            selectedPlan.discountType === "Recurring" &&
            new Date(selectedPlan.discountExpiryDate) > new Date() &&
            selectedPlan.discount > 0
        ) {
            this.confirmationService.confirm({
                message: "Do you want to apply " + selectedPlan.discount + " % of  Discount?",
                header: "Change Discount Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.chargeGroupForm.patchValue({
                        discount: selectedPlan.discount,
                        discountType: selectedPlan.discountType
                    });
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                    this.chargeGroupForm.patchValue({
                        discount: 0
                    });
                }
            });
        } else if (
            selectedPlan.discountType === "Recurring" &&
            new Date(selectedPlan.discountExpiryDate) > new Date() &&
            selectedPlan.discount < 0
        ) {
            this.confirmationService.confirm({
                message: "Do you want to over charge customer " + selectedPlan.discount + " % ?",
                header: "Change Discount Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.chargeGroupForm.patchValue({
                        discount: selectedPlan.discount,
                        discountType: selectedPlan.discountType
                    });
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                    this.chargeGroupForm.patchValue({
                        discount: 0
                    });
                }
            });
        }

        //
        // });
    }

    onAddoverChargeListField() {
        this.chargesubmitted = true;
        if (this.chargeGroupForm.valid) {
            if (this.chargeGroupForm.value.price >= this.chargeGroupForm.value.actualprice) {
                this.overChargeListFromArray.push(this.overChargeListFromArray as any);
                this.chargeGroupForm.reset();
                this.chargesubmitted = false;
                this.selectchargeValueShow = false;
            }
        } else {
        }
    }

    createoverChargeListFormGroup(): FormGroup {
        let billingCycle = this.chargeGroupForm.value.type === "Recurring" ? 1 : "";
        let planName = this.plansForCharge.find(
            plan => plan.planId == this.chargeGroupForm.value.planid
        ).planName;
        return this.fb.group({
            // chargeid: [''],
            type: [this.chargeGroupForm.value.type ? this.chargeGroupForm.value.type : "Recurring"],
            custid: [this.chargeGroupForm.value.custId],
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
            staticIPAdrress: [this.chargeGroupForm.value.staticIPAdrress]
            //   expiry: [this.chargeGroupForm.value.expiry],
            //   expiryDate: [moment(this.chargeGroupForm.value.expiry).format("DD-MM-YYYY HH:mm").toString()],
        });
    }

    deleteConfirmonChargeField(chargeFieldIndex: number, custId) {
        if (chargeFieldIndex || chargeFieldIndex == 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Charge ?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    // console.log(name);
                    this.chargeData.splice(this.chargeData.findIndex(item => item.custid == custId));
                    this.overChargeListFromArray.removeAt(chargeFieldIndex);
                    if (this.addedChargeList != null) {
                        this.addedChargeList.splice(chargeFieldIndex);
                    }
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                }
            });
        }
    }

    pageChangedOverChargeList(pageNumber) {
        this.currentPageoverChargeList = pageNumber;
    }

    isPlanSelected(custId) {
        var plans = this.plansForChargeByCust.filter(item => item.custId == custId);
        if (plans != null && plans.length > 0) {
            return false;
        }
        return true;
    }

    getChargeName(chargeid) {
        return this.commonDropDownService.chargeByTypeData.filter(item => item.id == chargeid)[0].name;
    }

    onDirectChargeChange(event, custId) {
        this.plansForCharge = this.plansForChargeByCust.filter(item => item.custId == custId);
        if (event.checked) {
            this.showAddDirectCharge = true;
            this.chargeGroupForm.patchValue({
                custId: custId
            });
            this.overChargeListFromArray = this.fb.array([]);
            var filteredCharge: any = this.chargeData.find(item => item.custid == custId);

            if (filteredCharge != null) {
                filteredCharge.custChargeDetailsPojoList.forEach(item => {
                    (this.overChargeListFromArray as any).push(
                        this.fb.group({
                            type: [item.type],
                            custid: [item.custId],
                            chargeid: [item.chargeid],
                            validity: [item.validity],
                            price: [item.price],
                            actualprice: [item.actualprice],
                            charge_date: [item.charge_date],
                            planid: [item.planid],
                            planName: [item.planName],
                            unitsOfValidity: [item.unitsOfValidity],
                            billingCycle: [item.billingCycle],
                            discount: [item.discount],
                            staticIPAdrress: [item.staticIPAdrress]
                        })
                    );
                });
            }
        } else {
            var index: any = this.chargeData.findIndex(item => item.custid == custId);
            if (index != -1) this.chargeData.splice(index);
            this.addedChargeList = [];
        }
    }

    saveChargeData() {
        const url = "/createCustChargeOverride";
        var chargeList = [];
        chargeList = this.overChargeListFromArray.value;

        this.chargeData.push({
            custChargeDetailsPojoList: chargeList,
            custid: chargeList[0].custid,
            parentId: this.custDetails.id,
            billableCustomerId: this.custDetails.id,
            paymentOwnerId: this.paymentOwnerId
        });

        this.showAddDirectCharge = false;
    }

    closeAddCharge() {
        this.showAddDirectCharge = false;
        this.chargeGroupForm.reset();
    }

    closeChargeDetaills() {
        this.showChargeDetails = false;
    }

    openChargeDetails(custId) {
        var filteredCharge: any = this.chargeData.find(item => item.custid == custId);
        if (filteredCharge != null) {
            this.addedChargeList = filteredCharge.custChargeDetailsPojoList;
        }
        this.showChargeDetails = true;
    }

    // Record advance payment

    openRecordPayment() {
        this.getPaymentMode();
        this.displayRecordPaymentDialog = true;
        this.paymentFormGroup.patchValue({
            customerid: this.custDetails.id
        });
        if (this.recordPaymentDialog) {
            this.paymentDialogRef = this.dialog.open(this.recordPaymentDialog, {
                width: '90%',
                maxWidth: '1400px',
                disableClose: false
            });

        } else {
        }
    }
    // Submit payment from modal
    submitPaymentRecord() {
        this.submitted = true;

        if (!this.paymentFormGroup.valid) {
            return;
        }

        // Check if at least one invoice is selected
        if (this.selectedInvoice.length < 1) {
            this.toastr.info(`Please select at least one invoice or advance mode`, 'Info!')
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Please select at least one invoice or advance mode.",
            //     icon: "far fa-check-circle"
            // });
            return;
        }

        // Build payment list
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

        // Create payment data object
        const createPaymentData: any = this.paymentFormGroup.value;
        createPaymentData.onlinesource = this.paymentFormGroup.controls.onlinesource.value;
        createPaymentData.paymentListPojos = paymentListPojos;

        // Close payment modal
        this.closePaymentModal();

        // Now proceed with change plan WITH payment data
        this.prepareChangePlanPayload(createPaymentData);
    }

    // Close payment modal
    closePaymentModal() {
        if (this.paymentDialogRef) {
            this.paymentDialogRef.close();
        }
        this.submitted = false;
    }

    modalOpenInvoice(id) {
        this.invoiceDialogRef = this.dialog.open(this.invoiceDialog, {
            width: '75%',
            disableClose: false
        });
        this.masterSelected = false;
        this.invoiceDialogRef.afterClosed().subscribe(result => {
            if (result && result.selectedInvoices) {
                // Extract invoice IDs
                const invoiceIds = result.selectedInvoices.map((inv: any) => inv.id);

                // ✅ Update form control
                this.paymentFormGroup.patchValue({
                    invoiceId: invoiceIds
                });

                // ✅ Store selected invoices for display
                this.selectedInvoice = result.selectedInvoices;
                this.isShowInvoiceList = true;

                // ✅ Enable the dropdown to show selected values
                this.paymentFormGroup.get('invoiceId')?.enable();

            }
        });
    }
    getPaymentMode() {
        const url = "/commonList/paymentMode";
        this.commonDropDownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.paymentMode = response.dataList;
            },
            (error: any) => { }
        );
    }

    async selPayModeRecord(event) {
        this.resetPayMode();
        const payMode = event.value.toLowerCase();
        if (payMode == "POS".toLowerCase() || payMode == "VatReceiveable".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.updateValueAndValidity();
            this.chequeDateName = "Transaction date";
        } else if (payMode == "Online".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.referenceno.setValidators([Validators.required]);
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.chequeDateName = "Transaction date";
        } else if (payMode == "Direct Deposit".toLowerCase()) {
            this.paymentFormGroup.controls.branch.enable();
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.disable();
            this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
            this.chequeDateName = "Transaction date";
        } else if (payMode == "NEFT_RTGS".toLowerCase()) {
            this.paymentFormGroup.controls.bankManagement.enable();
            this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
            this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.enable();
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
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.branch.enable();
            this.paymentFormGroup.controls.chequeno.updateValueAndValidity();
        }
        // await this.commondropdownService.getOnlineSourceData(payMode.toLowerCase());

        const url = "/commonList/generic/" + payMode;
        this.commonDropDownService.getMethodWithCache(url).subscribe(
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
        this.onChangeOFAmountTest(this.selectedInvoice);
        // let isAbbsTdsMode = this.checkPaymentMode(payMode);
        // if (isAbbsTdsMode) {
        //   this.paymentFormGroup.patchValue({
        //     tdsAmount: 0,
        //     abbsAmount: 0,
        //   });
        //   if (this.selectedInvoice.length > 0) {
        //     this.selectedInvoice.map(element => {
        //       element.tds = 0;
        //       element.abbs = 0;
        //     });
        //   }
        // }
    }

    resetPayMode() {
        this.paymentFormGroup.controls.chequeno.disable();
        this.paymentFormGroup.controls.chequedate.disable();
        this.paymentFormGroup.controls.bankManagement.disable();
        this.paymentFormGroup.controls.branch.disable();
        this.paymentFormGroup.controls.destinationBank.disable();
        this.paymentFormGroup.controls.reciptNo.enable();
        this.chequeDateName = "Cheque Date";
        this.paymentFormGroup.controls.referenceno.clearValidators();
        this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
        this.paymentFormGroup.controls.chequedate.setValidators([]);
        this.paymentFormGroup.controls.destinationBank.setValidators([]);
        this.paymentFormGroup.controls.bankManagement.setValidators([]);
        this.paymentFormGroup.controls.chequeno.setValidators([]);
        this.paymentFormGroup.controls.onlinesource.setValidators([]);
        this.paymentFormGroup.updateValueAndValidity();
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
        }
    }

    checkUncheckAllInvoice() {
        for (let i = 0; i < this.invoiceList.length; i++) {
            this.invoiceList[i].isSelected = this.masterSelected;
        }
        this.getCheckedItemListInvoice();
    }

    isAllSelectedInvoice() {
        this.masterSelected = this.invoiceList.every(function (item: any) {
            return item.isSelected == true;
        });
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

    onSelectedInvoice(event, data, isTDS, isABBS) {
        if (event > 0) {
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
        // data.includeTds = event.checked;
        if (event.checked && data.testamount) {
            data.includeTds = true;
            data.tdsCheck = ((data.testamount * this.tdsPercent) / 100).toFixed(2);
            data.tds = ((data.testamount * this.tdsPercent) / 100).toFixed(2);
        } else {
            data.includeTds = false;
            data.tdsCheck = 0;
            data.tds = 0;
        }
    }

    onChangeOFABBSTest(event, data) {
        if (event.checked && data.testamount) {
            data.includeAbbs = true;
            data.abbsCheck = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
            data.abbs = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
        } else {
            data.includeAbbs = false;
            data.abbsCheck = 0;
            data.abbs = 0;
        }
    }

    bindInvoice() {
        if (this.selectedInvoice.length >= 1) {
            this.isShowInvoiceList = true;
            this.Amount = 0;
            this.selectedInvoice.forEach(element => {
                if (element.testamount !== null) {
                    this.Amount += parseFloat(element.testamount);
                }
            });
            if (!this.Amount || this.Amount < 1) {
                this.toastr.info(`Please enter amount greater than 1`, 'Info!')
                // this.messageService.add({
                //     severity: "info",
                //     summary: "Info",
                //     detail: "Please enter amount greater than 1",
                //     icon: "far fa-check-circle"
                // });
                return;
            }
            this.paymentFormGroup.patchValue({
                invoiceId: this.selectedInvoice.map(item => item.id),
                amount: this.Amount.toFixed(2)
            });

            this.onChangeOFAmountTest(this.selectedInvoice);
            this.destinationbank = true;
        } else {
            this.toastr.info(`Please select at least one invoice or advance mode.`, 'Info!')
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Please select at least one invoice or advance mode.",
            //     icon: "far fa-check-circle"
            // });
            return;
        }
        if (this.selectedInvoice.length == 2) {
            this.selectedInvoice.forEach(element => {
                if (element.docnumber == "Advance") {
                    this.selectedInvoice = [];
                    this.invoiceList.forEach(element => {
                        element.isSelected = false;
                    });
                    this.masterSelected = false;
                    this.toastr.info(`Please select advance mode value only.`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "Please select advance mode value only.",
                    //     icon: "far fa-check-circle"
                    // });
                }
            });
        }
        this.displaySelectInvoiceDialog = false;
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
                        // this.isTdsFlag = true;
                    }
                }
                if (element.includeAbbs) {
                    if (element.includeAbbs === true) {
                        abbs = Number(element.abbsCheck);
                        totalabbsAmount = Number(element.abbsCheck) + Number(totalabbsAmount);
                        // this.isAbbsFlag = true;
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


    modalCloseInvoiceList() {
        let invoiceId = []
        let refundAbleAmount: number = 0
        this.selectedInvoice.map(item => {
            invoiceId.push(item.id)
            refundAbleAmount = refundAbleAmount + item.refundAbleAmount
        })
        this.paymentFormGroup.patchValue({
            invoiceId: invoiceId,
            amount: refundAbleAmount
        });

        if (this.invoiceDialogRef) {
            this.invoiceDialogRef.close();
        }
    }


    // keypressId(event: any) {
    //     const pattern = /[0-9\.]/;
    //     let inputChar = String.fromCharCode(event.charCode);
    //     if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
    //         event.preventDefault();
    //     }
    // }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus?banktype=other";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
                // this.bankDestination = response.dataList.banktype
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
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
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    addPayment(paymentId) {
        this.submitted = true;

        if (this.paymentFormGroup.valid) {
            //
            //   if (this.paymentFormGroup.value.invoiceId == 0) {
            //     this.paymentFormGroup.value.paytype = "advance";
            //   } else {
            //     this.paymentFormGroup.value.paytype = "invoice";
            //   }

            if (this.selectedInvoice.length == 0) {
                this.toastr.error(`Please select atleat one invoice or advance mode.`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: "Please select atleat one invoice or advance mode.",
                //     icon: "far fa-check-circle"
                // });
                return;
            } else {
                var createPaymentData: any = {};
                const url = "/record/payment";
                this.paymentFormGroup.value.customerid = this.custDetails.id;
                this.paymentFormGroup.value.type = "Payment";
                createPaymentData = this.paymentFormGroup.value;
                createPaymentData.onlinesource = this.paymentFormGroup.controls.onlinesource.value;
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
                createPaymentData.paymentListPojos = paymentListPojos;
                this.closePaymentModal()
                this.prepareChangePlanPayload(createPaymentData);
            }
            this.displayRecordPaymentDialog = false;
        }
    }

    closePaymentForm() {
        this.paymentFormGroup.reset();
        this.displayRecordPaymentDialog = false;
        this.submitted = false;
        this.isShowInvoiceList = false;
        this.selectedInvoice = [];
    }

    getPlanPurchaseType() {
        const url = "/commonList/generic/planPurchaseType";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                if (this.custType === "Postpaid") {
                    this.changePlanType = response.dataList.filter(
                        type => type.text !== "New" && type.text !== "Upgrade" && type.text !== "Renew"
                    );
                } else {
                    this.changePlanType = response.dataList.filter(
                        type => type.text !== "New" && type.text !== "Upgrade"
                    );
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

    getChangePlanDate() {
        const url = "/commonList/generic/changePlanDate";
        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            this.dateType = response.dataList;
            if (this.custType == "Postpaid") {
                this.ChangePLanDateSelection = this.dateType[1].value;
            }
        });
    }

    calculateExpiry() {
        if (this.planDetails?.validity) {
            const currentDate = new Date();
            const expiry = new Date(currentDate);
            if (this.planDetails.unitsOfValidity.toLowerCase() === "years") {
                expiry.setFullYear(currentDate.getFullYear() + this.planDetails.validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "months") {
                expiry.setMonth(currentDate.getMonth() + this.planDetails.validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "days") {
                expiry.setDate(currentDate.getDate() + this.planDetails.validity);
            } else if (this.planDetails.unitsOfValidity.toLowerCase() === "hours") {
                expiry.setHours(currentDate.getHours() + this.planDetails.validity);
            } else {
                expiry.setDate(currentDate.getDate() + this.planDetails.validity);
            }

            this.expiryDate = expiry;
        } else {
            this.expiryDate = null;
        }
    }
}
