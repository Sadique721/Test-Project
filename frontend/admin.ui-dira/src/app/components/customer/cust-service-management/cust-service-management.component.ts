import { Component, Input, Output, OnInit, EventEmitter, ViewChild } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { DatePipe } from "@angular/common";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PlanManagementService } from "src/app/service/plan-management.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { WorkflowAuditDetailsModalComponent } from "src/app/components/workflow-audit-details-modal/workflow-audit-details-modal.component";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";

import { ToastrService } from 'ngx-toastr';
import { error } from "console";


import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TemplateRef } from '@angular/core';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { PageEvent } from "@angular/material/paginator";
declare var $: any;

@Component({
    selector: "app-cust-service-management",
    templateUrl: "./cust-service-management.component.html",
    styleUrls: ["./cust-service-management.component.css"],
    standalone: false
})
export class CustServiceManagementComponent implements OnInit {
    @ViewChild(WorkflowAuditDetailsModalComponent) custauditWorkflowModal: TemplateRef<any>;
    @ViewChild('serviceActionDialog') serviceActionDialog: TemplateRef<any>;
    @ViewChild('stopServiceDialog') stopServiceDialog!: TemplateRef<any>;
    @ViewChild('approveServiceDialog') approveServiceDialog!: TemplateRef<any>;
    @ViewChild('rejectServiceDialog') rejectServiceDialog!: TemplateRef<any>;
    @ViewChild('reassignServiceDialog') reassignServiceDialog!: TemplateRef<any>;
    @ViewChild('pickServiceDialog') pickServiceDialog!: TemplateRef<any>;
    @ViewChild('auditDetailsDialog') auditDetailsDialog!: TemplateRef<any>;
    @ViewChild('gracePeriodDialog') gracePeriodDialog!: TemplateRef<any>;
    @ViewChild('addServiceDialog') addServiceDialog!: TemplateRef<any>;
    @ViewChild('leadConfirmationDialog') leadConfirmationDialog!: TemplateRef<any>;
    private currentDialogRef: MatDialogRef<any>;
    customerId: number = 0;
    custType: String = "";
    custData: any = {};
    today: string;
    isLeadMaster: any = false;
    isServiceThroughLead: number = 1;
    ifcustCaf: boolean = false;
    @Output() custPlanMappping = new EventEmitter();
    @Output() backButton = new EventEmitter();
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });
    custCurrentPlanList = [];
    serviceForm: UntypedFormGroup;
    planGroupForm: UntypedFormGroup;
    servicePlanFormArray: UntypedFormArray;
    planDetailsCategory = [
        { label: "Individual", value: "individual" },
        { label: "Plan Group", value: "groupPlan" }
    ];
    isInvoiceData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    filterPlanData: any = [];
    planByServiceArea: any = [];
    plantypaSelectData: any = [];
    servicePlanItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    servicePlantotalRecords: any;
    currentPageServicePlan = 1;
    submitted: boolean = false;
    plansubmitted: boolean = false;
    isPlanCategoryGroup: boolean = false;
    addServicePlanData: any = [];
    planGroup: any = [];
    customercurrenrCustListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customercurrenrCustListdatatotalRecords: String;
    currentPagecustomercurrenrCustListdata = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pausePlatbtnCondition = "";
    selectDeactivateReason: string = "";
    deactiveDataList: any = [];
    ifselecResonType: any;
    servicePerticularData: any = [];
    iscustomerEdit = false;
    ifcustomerDiscountField: boolean = false;
    selectedParentCust: any = [];
    billableCusList: any = [];
    newFirst = 0;
    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    prepaidParentCustomerList: any;
    parentCustomerListdatatotalRecords: any;
    parentFieldEnable = false;
    searchParentCustValue = "";
    searchParentCustOption = "";
    dateTime = new Date();
    discountType: any = "One-time";
    serviceStropRemarks: string = "";
    invoiceTypes = [
        { label: "Group", value: "Group" },
        { label: "Independent", value: "Independent" }
    ];
    nextApproverId: any;
    rejectPlanSubmitted: boolean = false;
    assignedPlanid: any;
    assignPlansubmitted: boolean = false;
    assignPlanForm: UntypedFormGroup;
    rejectPlanForm: UntypedFormGroup;
    staffUserId: any;
    loggedInUser: any;
    searchkey: string;
    searchKeyType: any;
    customerServiceMappingId: any;
    // assignPLANForm: FormGroup;
    rejectCustomerCAFForm: UntypedFormGroup;
    setplanMode = "";
    setplanGroupType = "";
    setplanCategory = "";
    serviceStartPuase: boolean = false;
    custServMappingStatus: any;
    data: any = [];
    failcount: any;
    custtype: any;
    countryCode: any;
    cafno: any;
    calendarType: any;
    partnerid: any;
    serviceareaid: any;
    status: any;
    billTo: any;
    billableCustomerId: any;
    isInvoiceToOrg: any;
    planMappingList: any = [];
    addressList: any = [];
    paymentDetails: any;
    dunningCategory: any;
    assignPlanID: any;
    approvableStaff: any = [];
    selectStaff: any;
    selectStaffReject: any;
    approvePlanData = [];
    approved = false;
    rejectPlanData = [];
    reject = false;
    serviceAreaBYserviceList = [];
    planList = [];
    //   planNameOpen: boolean = false;
    isSerialNumberShow: boolean = false;
    planForConnection;
    custCurrentPlanListLength: number;
    serialNumber: any;
    displaySelectParentCustomer: boolean = false;
    customerSelectType: any;
    displayDeleteReason: boolean = false;
    displayAuditDetails: boolean = false;
    // displayShiftLocationDetails: boolean = false;
    ifModelIsShow: boolean = false;
    displayApprovePlan: boolean = false;
    assignApporvePlanModal: boolean = false;
    createServiceAccess: boolean = false;
    serviceTerminationAccess: boolean = false;
    showPlanConnectionNo: boolean = false;
    rejectPlanModal: boolean = false;
    currentPagedunningListdata = 1;
    serviceHoldAccess: boolean = false;
    serviceStopAccess: boolean = false;
    serviceResumeDate: any;
    holdDays: any;
    isGracePeriodModel: boolean;
    gracePeriod: string;
    storedServiceAreaId: any = null;
    gracePeriodData: any;
    holdDaysOptions: any[];
    // Format: YYYY-MM-DD
    // isSelectStaff:boolean = false;
    displayedColumnsAddService: string[] = ['service', 'plan', 'validity', 'offerprice', 'delete'];
    baseColumnsAddService: string[] = ['service', 'plan', 'validity', 'offerprice'];

    servicePlanCurrentPage: number = 0;
    totalRecords: any;
    constructor(
        private toastr: ToastrService,

        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private fb: UntypedFormBuilder,
        private datePipe: DatePipe,
        private planManagementService: PlanManagementService,
        public PaymentamountService: PaymentamountService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private router: Router,
        private systemService: SystemconfigService,
        private route: ActivatedRoute,
        loginService: LoginService,
        private dialog: MatDialog
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.createServiceAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_CREATE
                : POST_CUST_CONSTANTS.POST_CUST_SERVICE_CREATE
        );
        this.serviceTerminationAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_TERMINATION
                : POST_CUST_CONSTANTS.POST_CUST_SERVICE_TERMINATION
        );
        this.serviceHoldAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_HOLD_RESUME
                : POST_CUST_CONSTANTS.POST_CUST_SERVICE_HOLD_RESUME
        );

        this.serviceStopAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_STOP
                : POST_CUST_CONSTANTS.POST_CUST_SERVICE_STOP
        );
        this.commondropdownService.findAllplanGroups();
    }

    ngOnInit(): void {
        const currentDate = new Date();
        this.today = currentDate.toISOString().split("T")[0];
        this.systemService.getConfigurationByName("ServiceThroughLead").subscribe((res: any) => {
            this.isServiceThroughLead = res.data.value;
        });

        this.getCustomersDetail(this.customerId);
        this.serviceForm = this.fb.group({
            parentCustomerId: [""],
            planCategory: [""],
            billTo: [""],
            billableCustomerId: [""],
            isInvoiceToOrg: [false],
            discount: ["", [Validators.max(99)]],
            plangroupid: [""],
            istrialplan: [""]
        });

        this.planGroupForm = this.fb.group({
            discount: ["", [Validators.max(99)]],
            planId: ["", Validators.required],
            service: ["", Validators.required],
            serviceId: [""],
            validity: ["", Validators.required],
            offerprice: [""],
            validityUnit: [""],
            newAmount: [""],
            istrialplan: [""],
            discountType: [""],
            discountExpiryDate: [""],
            invoiceType: [""],
            serialNumber: [""]
        });

        this.assignPlanForm = this.fb.group({
            remark: ["", Validators.required]
        });

        this.rejectPlanForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.serviceForm.get('billTo')?.valueChanges.subscribe(() => {
            this.updateDisplayedColumns();
        });
        this.serviceForm.get('parentCustomerId')?.valueChanges.subscribe(() => {
            this.updateDisplayedColumns();
        });
        this.servicePlanFormArray = this.fb.array([]);
        this.commondropdownService.getBillToData();
        this.commondropdownService.getplanservice();
        this.commondropdownService.planCreationType();
        this.commondropdownService.getPostpaidplanData();

        if (this.custData.custtype == "Prepaid") {
            this.planGroup = this.commondropdownService.PrepaidPlanGroupDetails.filter(
                planGroup => planGroup.servicearea.id == this.custData.serviceareaid
            );
        } else {
            this.planGroup = this.commondropdownService.postPlanGroupDetails.filter(
                planGroup => planGroup.servicearea.id == this.custData.serviceareaid
            );
        }

        this.getDectivateData();
        this.getLoggedinUserData();
        this.getActivePlanDetails();
    }
    updateDisplayedColumns(): void {
        this.displayedColumnsAddService = this.getDisplayedColumns();
    }
    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            if (this.custData.isLeadMaster != null) {
                this.isLeadMaster = this.custData.isLeadMaster;
            }

            // ⭐ FIX: Check if isServiceThroughLead comes from customer details
            if (this.custData.isServiceThroughLead != null) {
                this.isServiceThroughLead = this.custData.isServiceThroughLead;
            }
            this.getServiceByServiceAreaID(this.custData.serviceareaid);
            this.getPlanbyServiceArea(this.custData.serviceareaid);
        });
    }
    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }
    getLoggedinUserData() {
        let staffId = localStorage.getItem("userId");
        this.staffUserId = localStorage.getItem("userId");
        this.loggedInUser = localStorage.getItem("loggedInUser");
    }
    openAddServiceModal(): void {


        // ⭐ FIX: Convert to proper boolean
        const serviceThroughLead = Number(this.isServiceThroughLead) === 1;
        const leadMasterExists = this.isLeadMaster === true || this.isLeadMaster === 'true';



        // ⭐ CHECK: Lead validation with proper boolean check
        if (serviceThroughLead && !leadMasterExists) {
            this.showLeadConfirmationDialog();
            return; // Stop here - don't open service modal
        }

        // ⭐ IF LEAD CHECK PASSED - OPEN SERVICE MODAL
        this.proceedToAddService();
    }
    showLeadConfirmationDialog(): void {
        this.currentDialogRef = this.dialog.open(this.leadConfirmationDialog, {
            width: '500px',
            disableClose: true, // User must choose Yes or No
            autoFocus: false,
            panelClass: 'lead-confirmation-dialog',
            hasBackdrop: true
        });
    }

    // ============================================
    // CONFIRM LEAD CREATION (YES BUTTON)
    // ============================================

    confirmLeadCreation(): void {

        // Close the dialog
        this.closeDialog();

        // Navigate to lead creation page
        if (this.commondropdownService.isPlanOnDemand) {
            this.router.navigate(['/home/enterprise-lead'], {
                queryParams: { id: this.custData.id }
            });
        } else {
            this.router.navigate(['/home/lead-management'], {
                queryParams: { id: this.custData.id }
            });
        }
    }

    // ============================================
    // CANCEL LEAD CREATION (NO BUTTON)
    // ============================================

    cancelLeadCreation(): void {

        // Close the dialog
        this.closeDialog();

        // Show info message
        this.toastr.info('Service addition cancelled. Please create a lead first.', 'Info');
    }

    // ============================================
    // PROCEED TO ADD SERVICE (AFTER VALIDATION)
    // ============================================

    proceedToAddService(): void {

        // Reset forms
        this.planGroupForm.reset({
            discountType: 'One-time',
            discount: 0,
            istrialplan: false
        });

        this.serviceForm.reset();
        this.servicePlanFormArray = this.fb.array([]);
        this.filterPlanData = [];

        // Initialize displayed columns
        this.displayedColumnsAddService = [...this.baseColumnsAddService, 'delete'];
        this.servicePlantotalRecords = 0;
        this.servicePlanCurrentPage = 0;

        // Set plan category based on customer data
        let planCategory: any;

        if (this.custData.plangroupid) {
            this.isPlanCategoryGroup = false;
            planCategory = 'groupPlan';
            this.serviceForm.controls['plangroupid'].patchValue(this.custData.plangroupid);

            if (this.custData.planMappingList && this.custData.planMappingList.length > 0) {
                this.serviceForm.controls['discount'].patchValue(this.custData.planMappingList[0].discount);
                this.serviceForm.controls['isInvoiceToOrg'].patchValue(this.custData.planMappingList[0].isInvoiceToOrg);
            }
        } else {
            this.isPlanCategoryGroup = false;
            planCategory = 'individual';
            this.serviceForm.controls['plangroupid'].disable();
            this.serviceForm.controls['discount'].disable();
            this.serviceForm.controls['isInvoiceToOrg'].disable();
        }

        // Patch form values
        if (this.custData.planMappingList && this.custData.planMappingList.length > 0) {
            this.serviceForm.patchValue({
                parentCustomerId: this.custData.parentCustomerId || null,
                planCategory: planCategory,
                billTo: this.custData.planMappingList[0]?.billTo || '',
                billableCustomerId: this.custData.planMappingList[0]?.billableCustomerId || null,
                isInvoiceToOrg: this.custData.planMappingList[0]?.isInvoiceToOrg || false
            });
        } else {
            // If no existing plan mappings, set defaults
            this.serviceForm.patchValue({
                parentCustomerId: this.custData.parentCustomerId || null,
                planCategory: planCategory,
                billTo: '',
                billableCustomerId: null,
                isInvoiceToOrg: false
            });
        }

        // Update displayed columns
        this.updateDisplayedColumns();

        //  OPEN ADD SERVICE DIALOG
        setTimeout(() => {
            try {
                this.currentDialogRef = this.dialog.open(this.addServiceDialog, {
                    width: '90%',
                    maxWidth: '1400px',
                    maxHeight: '90vh',
                    disableClose: false,
                    autoFocus: false,
                    panelClass: 'add-service-dialog',
                    hasBackdrop: true
                });

            } catch (error) {
                console.error('Error opening Add Service dialog:', error);
                this.toastr.error('Failed to open Add Service dialog', 'Error!');
            }
        }, 100);
    }
    getPlanName(planId: number): string {
        if (!this.plantypaSelectData || this.plantypaSelectData.length === 0) {
            return 'N/A';
        }

        const plan = this.plantypaSelectData.find((p: any) => p.id === planId);
        return plan ? plan.name : 'N/A';
    }

    getDisplayedColumns(): string[] {
        const columns: string[] = [...this.baseColumnsAddService];

        // Add discount columns if NOT organization
        if (this.serviceForm?.value?.billTo !== 'ORGANIZATION') {
            columns.push('discountType', 'discount', 'discountExpiryDate', 'istrialplan');
        }

        // Add invoice type if parent customer exists
        if (this.serviceForm?.value?.parentCustomerId !== null &&
            this.serviceForm?.value?.parentCustomerId !== '') {
            columns.push('invoiceType');
        }

        // Add serial number if required
        if (this.isSerialNumberShow) {
            columns.push('serialNumber');
        }

        // Always add delete at the end
        columns.push('delete');

        return columns;
    }

    getServiceByServiceAreaID(ids: any) {
        this.storedServiceAreaId = ids;

        let data = [];
        data.push(ids);

        this.customerManagementService.postMethod("/serviceArea/getAllServicesByServiceAreaId", data)
            .subscribe((response: any) => {
                this.serviceAreaBYserviceList = response.dataList || [];

                // Update total record count for paginator
                this.servicePlantotalRecords = response.totalRecords || this.custCurrentPlanList.length;

                // Client-side pagination if backend does not support page & size
                const startIndex = (this.currentPageServicePlan - 1) * this.servicePlanItemPerPage;
                this.serviceAreaBYserviceList = this.serviceAreaBYserviceList.slice(startIndex, startIndex + this.servicePlanItemPerPage);
            });
    }

    createServiceFormGroup(): UntypedFormGroup {
        return this.fb.group({
            discount: [this.planGroupForm.value.discount, [Validators.max(99)]],
            planId: [this.planGroupForm.value.planId, Validators.required],
            service: [this.planGroupForm.value.service, Validators.required],
            serviceId: [
                this.serviceAreaBYserviceList
                    .filter(data => this.planGroupForm.value.service.includes(data.name))
                    .map(data => data.id)[0],
                Validators.required
            ],
            validity: [this.planGroupForm.value.validity, Validators.required],
            offerprice: [this.planGroupForm.value.offerprice],
            validityUnit: [this.planGroupForm.value.validityUnit],
            istrialplan: [this.planGroupForm.value.istrialplan],
            discountType: [this.planGroupForm.value.discountType],
            discountExpiryDate: [this.planGroupForm.value.discountExpiryDate],
            invoiceType: [this.planGroupForm.value.invoiceType],
            serialNumber: [this.planGroupForm.value.serialNumber]
        });
    }
    planValidity = "";
    planunitValidity = "";
    onAddPlanServiceField(): void {
        this.plansubmitted = true;

        if (this.planGroupForm.valid) {
            const formValue = this.planGroupForm.getRawValue();

            // Create FormGroup for the plan
            const servicePlanGroup = this.fb.group({
                service: [formValue.service],
                planId: [formValue.planId],
                validity: [formValue.validity || ''],
                validityUnit: [formValue.validityUnit || ''],
                offerprice: [formValue.offerprice || 0],
                discountType: [formValue.discountType || 'One-time'],
                discount: [formValue.discount || 0],
                discountExpiryDate: [formValue.discountExpiryDate || null],
                invoiceType: [formValue.invoiceType || ''],
                istrialplan: [formValue.istrialplan || false],
                serialNumber: [formValue.serialNumber || ''],
                serviceId: [formValue.serviceId || ''],
                newAmount: [formValue.offerprice || 0]
            });

            // Add to array
            this.servicePlanFormArray.push(servicePlanGroup);

            // Update total records
            this.servicePlantotalRecords = this.servicePlanFormArray.length;

            // Reset form
            this.planGroupForm.reset({
                discountType: 'One-time',
                discount: 0,
                istrialplan: false
            });

            this.plansubmitted = false;

            // Update columns
            this.updateDisplayedColumns();

            this.toastr.success('Service added to list', 'Success!');
        } else {
            this.toastr.error('Please fill all required fields', 'Error!');

            // Mark all fields as touched to show errors
            Object.keys(this.planGroupForm.controls).forEach(key => {
                this.planGroupForm.get(key)?.markAsTouched();
            });
        }
    }

    getPlanbyServiceArea(serviceAreaId) {
        if (serviceAreaId) {
            this.filterPlanData = [];
            const url = "/plans/serviceArea?planmode=ALL&serviceAreaId=" + serviceAreaId;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planByServiceArea = response.postpaidplanList;
                    this.filterPlanData = this.planByServiceArea.filter(
                        plan => plan.plantype == this.custData.custtype
                    );
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    serviceBasePlanDATA(event) {

        let planserviceData;
        let planServiceID = "";
        let planType = "";
        const servicename = event.value;
        const planserviceurl = "/planservice/all";
        this.customerManagementService.getMethod(planserviceurl).subscribe((response: any) => {
            //
            planserviceData = response.serviceList.filter(service => service.name === servicename);
            this.isSerialNumberShow = planserviceData[0].serviceParamMappingList.some(
                item => item.serviceParamName !== null && item.serviceParamName === "Product Required"
            );
            if (planserviceData.length > 0) {
                planServiceID = planserviceData[0].id;
                planType = this.custData.custtype;
                this.plantypaSelectData = [];
                this.postpaidplanByService(planServiceID, planType);
                // const planserviceurl = "/plansByTypeServiceModeStatusAndServiceArea";
                // this.customerManagementService
                //   .getPlansByTypeServiceModeStatusAndServiceAreaWithoutService(
                //     planserviceurl,
                //     this.custData.custtype,
                //     planServiceID,
                //     this.custData.serviceareaid,
                //     this.setplanMode,
                //     "Active",
                //     this.setplanGroupType
                //   )
                //   .subscribe((response: any) => {
                //     if (response.status == 200 && response.postPaidPlan.length > 0) {
                //       this.plantypaSelectData = response.postPaidPlan;
                //     } else {
                //       this.plantypaSelectData = [];
                //       this.messageService.add({
                //         severity: "info",
                //         summary: "Note ",
                //         detail: "Plan not available for this Plan type and service ",
                //       });
                //     }
                //   });

                //     // if (this.customerGroupForm.value.custtype) {
                //     console.log("this.filterPlanData", this.filterPlanData);
                //     this.plantypaSelectData = this.filterPlanData.filter(
                //       id =>
                //         id.serviceId === planServiceID &&
                //         (id.planGroup === "Registration" || id.planGroup === "Registration and Renewal")
                //     );
                //     if (this.plantypaSelectData.length === 0) {
                //       this.messageService.add({
                //         severity: "info",
                //         summary: "Note ",
                //         detail:
                //           this.custData.custtype + " Plan not available for this customer type and service ",
                //         icon: "far fa-times-circle",
                //       });
                //     }
                //     // }
                //     // else {
                //     //   this.messageService.add({
                //     //     severity: 'info',
                //     //     summary: 'Required ',
                //     //     detail: 'Customer Type Field Required',
                //     //     icon: 'far fa-times-circle',
                //     //   });
                //     // }
            }
        });
    }

    postpaidplanByService(serviceId, planType) {
        let url = `/postpaidplanByService/${serviceId}/${planType}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.status == 200 && response.postPaidPlan.length > 0) {
                    this.plantypaSelectData = response.postPaidPlan.filter(
                        key => key.plantype === this.custType
                    );
                } else {
                    this.plantypaSelectData = [];
                    this.toastr.info(`Plan not available for this Plan type and service`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Note ",
                    //     detail: "Plan not available for this Plan type and service "
                    // });
                }
            },
            error => {
                this.plantypaSelectData = [];
                this.toastr.info(`Plan not available for this Plan type and service`, 'Info!')

                // this.messageService.add({
                //     severity: "info",
                //     summary: "Note ",
                //     detail: "Plan not available for this Plan type and service "
                // });
            }
        );
    }
    pageChangedPlanService(event: any): void {
        this.servicePlanCurrentPage = event.pageIndex;
        this.servicePlanItemPerPage = event.pageSize;
    }

    deleteConfirmonChargeField(planFieldIndex: number) {
        if (planFieldIndex || planFieldIndex === 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Plan?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.onRemovePayMapping(planFieldIndex);
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    async onRemovePayMapping(planFieldIndex: number) {
        this.servicePlanFormArray.removeAt(planFieldIndex);
    }

    getPlanValidity(event) {
        const planId = event.value;
        const url = "/postpaidplan/" + planId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                const planDetailData = response.postPaidPlan;
                this.planGroupForm.patchValue({
                    validity: Number(planDetailData.validity),
                    offerprice: Number(planDetailData.offerprice),
                    newAmount: Number(planDetailData.offerprice),
                    validityUnit: planDetailData.unitsOfValidity
                });
            },
            (error: any) => {
                console.log(error, "error");
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
    addPlanService(): void {

        if (this.serviceForm.valid) {
            this.addServicePlanData = this.custData;
            this.addServicePlanData.planMappingList = [];

            if (this.serviceForm.controls['planCategory'].value === 'groupPlan') {
                this.addServicePlanData = this.serviceForm.getRawValue();
                this.addServicePlanData.id = this.custData.id;
                this.addServicePlanData.custtype = 'Prepaid';
                this.addServicePlanData.serviceareaid = this.custData.serviceareaid;
                this.addServicePlanData.planMappingList = [];
                this.planServiceAdd(this.addServicePlanData);
            } else {
                this.addServicePlanData = this.serviceForm.getRawValue();
                this.addServicePlanData.id = this.custData.id;
                this.addServicePlanData.custtype = 'Prepaid';
                this.addServicePlanData.serviceareaid = this.custData.serviceareaid;
                this.addServicePlanData.planMappingList = this.servicePlanFormArray.value;

                this.addServicePlanData.planMappingList.forEach((plan: any) => {
                    plan.planCategory = this.serviceForm.controls['planCategory'].value;
                    plan.billTo = this.serviceForm.controls['billTo'].value;
                    plan.billableCustomerId = this.serviceForm.controls['billableCustomerId'].value;
                    plan.newAmount = plan.offerprice;
                    plan.isInvoiceToOrg = this.serviceForm.controls['isInvoiceToOrg'].value;
                });

                if (this.addServicePlanData.planMappingList.length === 0) {
                    this.toastr.error('Please add atleast one service and plan', 'Error!');
                } else {
                    console.log(this.addServicePlanData, this.addServicePlanData);
                    this.planServiceAdd(this.data);
                }
            }
        }
    }
    planServiceAdd(data) {
        let id = this.addServicePlanData.id;
        this.failcount = this.addServicePlanData.failcount;
        this.custtype = this.addServicePlanData.custtype;
        this.countryCode = this.addServicePlanData.countryCode;
        this.cafno = this.addServicePlanData.cafno;
        this.calendarType = this.addServicePlanData.calendarType;
        this.partnerid = this.addServicePlanData.partnerid;
        this.serviceareaid = this.addServicePlanData.serviceareaid;
        this.status = this.addServicePlanData.status;
        this.billTo = this.addServicePlanData.billTo;

        this.billableCustomerId = this.addServicePlanData.billableCustomerId;
        this.isInvoiceToOrg = this.addServicePlanData.isInvoiceToOrg;
        this.planMappingList = this.addServicePlanData.planMappingList;
        this.addressList = this.addServicePlanData.addressList;
        this.paymentDetails = this.addServicePlanData.paymentDetails;
        this.dunningCategory = this.addServicePlanData.dunningCategory;
        this.data = {
            id: id,
            failcount: this.failcount,
            custtype: this.custtype,
            countryCode: this.countryCode,
            cafno: this.cafno,
            calendarType: this.calendarType,
            partnerid: this.partnerid,
            serviceareaid: this.serviceareaid,
            status: this.status,
            billTo: this.billTo,
            billableCustomerId: this.billableCustomerId,
            isInvoiceToOrg: this.isInvoiceToOrg,
            planMappingList: this.planMappingList,
            addressList: this.addressList,
            paymentDetails: this.paymentDetails,
            dunningCategory: this.dunningCategory
        };
        //console.log("data",this.data)

        let url: any = "";
        if (this.isLeadMaster) {
            url = "/SavbillSalesCrmsBss/leadMaster/addNewService";
        } else {
            url = "/subscriber/addNewService";
        }

        this.customerManagementService.postMethod(url, this.data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(response.responseMessage, 'Error!');
                } else {
                    this.serviceForm.reset();
                    this.planGroupForm.reset();
                    this.submitted = false;
                    this.plansubmitted = false;
                    this.servicePlanFormArray.controls = [];

                    //  CLOSE DIALOG
                    this.closeDialog();

                    this.router.navigate(['/home/customer-caf', this.custType]);
                    this.getActivePlanDetails();
                    this.toastr.success('Service added successfully. You can check it in Customer CAF.', 'Success!');
                }
            },
            (error: any) => {
                console.log(error, error);
                this.toastr.error(error.error.ERROR, 'Error!');
            }
        );
    }

    getActivePlanDetails() {
        let url: any;

        if (this.ifcustCaf) {
            url =
                "/subscriber/getPlanByCustService/" +
                this.customerId +
                "?status=NewActivation" +
                "&isNotChangePlan=true";
        } else {
            url =
                "/subscriber/getPlanByCustService/" +
                this.customerId +
                "?isAllRequired=true" +
                "&isNotChangePlan=true";
        }
        this.serviceStopBulkFlag = false;
        this.serviceStopId = [];
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // if (response.responseCode == 200) {
                this.custCurrentPlanList = response.dataList;
                // this.custCurrentPlanList = this.custCurrentPlanList.sort((a, b) => a.custId - b.custId);
                if (this.custCurrentPlanList == null) {
                    this.custCurrentPlanList = [];
                }
                this.custCurrentPlanList = this.custCurrentPlanList.filter(
                    data => data.planstage === "ACTIVE" || !data.planstage || data.planstage === "EXPIRED" || data.planstage === "Hold"
                );
                if (this.custCurrentPlanList.length > 0) {
                    this.pausePlatbtnCondition = this.custCurrentPlanList[0].custPlanStatus;
                    this.SelectplanDataValue(this.custCurrentPlanList[0].planId);
                }
                this.custCurrentPlanList.forEach(e => {
                    if (e.custPlanStatus == "ACTIVE") {
                        this.serviceStartPuase = true;
                    } else {
                        if (e.stopServiceDate) this.serviceStartPuase = false;
                        else this.serviceStartPuase = true;
                    }
                    if (e.isServiceThroughLead != null) {
                        this.isServiceThroughLead = e.isServiceThroughLead;
                    }

                    // ⭐ FIX: Set isLeadMaster flag (THIS WAS MISSING!)
                    if (e.isLeadMaster != null) {
                        this.isLeadMaster = e.isLeadMaster;
                    }
                });
                // this.custCurrentPlanList = this.custCurrentPlanList.filter(
                //   element =>
                //     element.custServMappingStatus !== "ActivationPending" &&
                //     element.custServMappingStatus !== "NewActivation" 
                // );


                // } else {
                //
                //   this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: response.responseMessage,
                //     icon: "far fa-times-circle",
                //   });
                // }
            },
            (error: any) => {
                // console.log(error, "error")
                this.custCurrentPlanList = [];
                this.planList = [];
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

    SelectplanDataValue(planId) {
        const url = "/postpaidplan/" + planId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            let viewPlanListData = response.postPaidPlan;
            this.setplanMode = viewPlanListData.mode;
            this.setplanGroupType = viewPlanListData.planGroup;
            this.setplanCategory = viewPlanListData.category;
            this.planValidity = viewPlanListData.validity;
            this.planunitValidity = viewPlanListData.unitsOfValidity;
        });
    }
    pageChangedcustomercurrenrCustListData(pageNumber) {
        this.currentPagecustomercurrenrCustListdata = pageNumber;
        //this.getActivePlanDetails();
    }

    TotalCurrentPlanItemPerPage(event) {
        this.customercurrenrCustListdataitemsPerPage = Number(event.value);
        if (this.currentPagecustomercurrenrCustListdata > 1) {
            this.currentPagecustomercurrenrCustListdata = 1;
        }
        // this.getActivePlanDetails();
    }

    deleteServicePlanData(): void {
        let planMapId = this.servicePerticularData.planmapid;
        let custId = this.servicePerticularData.custId;
        let planId = this.servicePerticularData.planId;
        let data1: any;

        if (!this.serviceStopBulkFlag) {
            const url = `/subscriber/deleteService/${planId}?custId=${custId}&planMapId=${planMapId}&reasonId=${this.selectDeactivateReason}`;

            this.customerManagementService.postMethod(url, data1).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        this.toastr.error(response.responseMessage, 'Error!');
                    } else {
                        this.getActivePlanDetails();
                        this.closeDialog();  //  CLOSE DIALOG
                        this.toastr.success(response.message, 'Success!');
                    }
                },
                (error: any) => {
                    console.log(error, error);
                    this.toastr.error(error.error.ERROR, 'Error!');
                }
            );
        } else {
            let data: any;
            let terminateService: any[] = [];

            this.serviceStopId.map((e: any) => {
                terminateService.push({
                    custPlanMappingId: e.planmapid
                });
            });

            data = {
                customerId: this.serviceStopId[0].custId,
                serviceStopBulkFlag: this.serviceStopBulkFlag,
                reason: this.selectDeactivateReason,
                terminateService: terminateService
            };

            const url = `subscriber/terminateServiceInBulk`;

            this.customerManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        this.toastr.error(response.responseMessage, 'Error!');
                    } else {
                        this.closeDialog();  //  CLOSE DIALOG
                        this.getActivePlanDetails();
                        this.serviceStopBulkFlag = false;
                        this.serviceStopId = [];
                        this.custPlanMappping.emit();
                        this.toastr.success(response.message, 'Success!');
                    }
                },
                (error: any) => {
                    console.log(error, error);
                    this.toastr.error(error.error.ERROR, 'Error!');
                }
            );
        }
    }
    pauseService(): void {
        let custId = this.servicePerticularData.custId;
        let data: any;


        if (!this.serviceStopBulkFlag) {
            data = {
                custId: custId,
                deactivatePlanReqModels: [{
                    custServiceMappingId: this.servicePerticularData.customerServiceMappingId,
                    remarks: this.serviceStropRemarks,
                    reasonId: this.selectDeactivateReason,
                    serviceResumeDate: this.serviceResumeDate
                }],
                serviceResumeDate: this.serviceResumeDate,
                holdDays: this.holdDays
            };
        } else {
            let deactivatePlanReqModels: any[] = [];

            this.serviceStopId.map((e: any) => {
                deactivatePlanReqModels.push({
                    custServiceMappingId: e.customerServiceMappingId,
                    remarks: this.serviceStropRemarks,
                    reasonId: this.selectDeactivateReason
                });
            });

            data = {
                custId: custId,
                serviceStopBulkFlag: this.serviceStopBulkFlag,
                deactivatePlanReqModels: deactivatePlanReqModels
            };
        }

        const url = `/subscriber/holdServiceInBulk`;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(response.responseMessage, 'Error!');
                } else {
                    this.closeDialog();  //  CLOSE DIALOG
                    this.serviceStopBulkFlag = false;
                    this.serviceStopId = [];
                    this.getActivePlanDetails();
                    this.custPlanMappping.emit();
                    this.toastr.success(response.message, 'Success!');
                }
            },
            (error: any) => {
                console.log(error, error);
                this.toastr.error(error?.error?.ERROR || 'Service hold cannot be done when future plan is present', 'Error!');
            }
        );
    }

    playService(): void {
        let data: any;
        let custId = this.servicePerticularData.custId;

        if (!this.serviceStopBulkFlag) {
            data = {
                custId: custId,
                deactivatePlanReqModels: [{
                    custServiceMappingId: this.servicePerticularData.customerServiceMappingId,
                    remarks: this.serviceStropRemarks
                }]
            };
        } else {
            let deactivatePlanReqModels: any[] = [];

            this.serviceStopId.map((e: any) => {
                deactivatePlanReqModels.push({
                    custServiceMappingId: e.customerServiceMappingId,
                    remarks: this.serviceStropRemarks
                });
            });

            data = {
                custId: this.custData.id,
                serviceStopBulkFlag: this.serviceStopBulkFlag,
                deactivatePlanReqModels: deactivatePlanReqModels
            };
        }

        const url = `/subscriber/resumeServiceInBulk`;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(response.responseMessage, 'Error!');
                } else {
                    this.closeDialog();  //  CLOSE DIALOG
                    this.getActivePlanDetails();
                    this.serviceStopBulkFlag = false;
                    this.serviceStopId = [];
                    this.custPlanMappping.emit();
                    this.toastr.success(response.message, 'Success!');
                }
            },
            (error: any) => {
                console.log(error, error);
                this.toastr.error(error.error.ERROR, 'Error!');
            }
        );
    }
    pageChange(event: PageEvent): void {
        this.currentPageServicePlan = event.pageIndex + 1; // paginator is 0-based index
        this.servicePlanItemPerPage = event.pageSize;

        // If you don't yet have stored ID, get it from your data source or input context
        if (!this.storedServiceAreaId && this.custData?.serviceareaid) {
            this.storedServiceAreaId = this.custData.serviceareaid;
        }

        // Call your fetching method using the stored id, using internal pagination variables
        if (this.storedServiceAreaId) {
            this.getServiceByServiceAreaID(this.storedServiceAreaId);
        }
    }

    saveEditNickName(serviceMappingID, nickName) {
        let data = {};
        const url = `/subscriber/nickName?custServiceMappingId=${serviceMappingID}&name=${nickName}`;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.getActivePlanDetails();
                    this.toastr.success(`Successfully`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: "Successfully",
                    //     icon: "far fa-check-circle"
                    // });
                }
            },
            (error: any) => {
                console.log(error, "error");
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

    openPaushSearviceModel(data: any, type: string): void {
        // Set data
        this.ifselecResonType = type;
        this.servicePerticularData = data;
        this.selectDeactivateReason = '';
        this.serviceStropRemarks = '';
        this.serviceResumeDate = null;
        this.holdDays = null;

        // Handle Delete confirmation for child services
        if (type === 'Delete' && data.isChildExists) {
            this.confirmationService.confirm({
                message: 'Child customer service also terminate, Do you want to continue?',
                header: 'Delete Confirmation',
                icon: 'pi pi-info-circle',
                accept: () => {
                    this.openStopServiceDialog();
                },
                reject: () => {
                    this.toastr.info('You have rejected', 'Rejected');
                }
            });
        } else {
            this.openStopServiceDialog();
        }
        // Set hold days options for Pause
        if (type === 'Pause') {
            this.holdDaysOptions = this.getHoldDaysOptions();
        }
    }
    openStopServiceDialog(): void {
        this.currentDialogRef = this.dialog.open(this.stopServiceDialog, {
            width: '600px',
            disableClose: false,
            autoFocus: false,
            panelClass: 'stop-service-dialog'
        });
    }
    getServiceActionIcon(): string {
        const icons: { [key: string]: string } = {
            'Delete': 'delete_forever',
            'Stop': 'stop_circle',
            'Pause': 'pause_circle',
            'Start': 'play_circle',
            'Resume': 'play_circle',
            'Enable': 'power_settings_new',
            'Hold': 'pause_circle'
        };
        return icons[this.ifselecResonType] || 'info';
    }
    getDectivateData() {
        let url = `/commonList/generic/DEACTIVATE_REASON_EZ_BILL`;
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.deactiveDataList = response.dataList;
        });
    }

    modalOpenParentCustomer() {
        this.displaySelectParentCustomer = true;
        this.customerSelectType = "Billable To";
        this.selectedParentCust = [];

        // this.newFirst = 0;
        // this.getParentCustomerData();
        // this.selectedParentCust = [];
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
        const url = "/parentCustomers/list/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                const list = this.prepaidParentCustomerList;
                const filterList = list.filter(cust => cust.id !== this.custData.id);

                this.prepaidParentCustomerList = filterList;

                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
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

        const url = "/parentCustomers/search/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
        // console.log("this.searchData", this.searchData)
        this.customerManagementService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.msg}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    // this.customerListData = [];
                    this.parentCustomerListdatatotalRecords = 0;
                } else {
                    this.prepaidParentCustomerList = response.parentCustomerList;
                    const list = this.prepaidParentCustomerList;
                    const filterList = list.filter(cust => cust.id !== this.custData.id);
                    this.prepaidParentCustomerList = filterList;
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
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

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData();
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    paginate(event) {
        this.currentPageParentCustomerListdata = event.page + 1;
        // this.first = event.first;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }

    selectedCustChange(event) {

        this.displaySelectParentCustomer = false;
        this.selectedParentCust = event;

        this.billableCusList = [
            {
                id: this.selectedParentCust.id,
                name: this.selectedParentCust.name
            }
        ];
        this.serviceForm.patchValue({
            billableCustomerId: this.selectedParentCust.id
        });
        this.serviceForm.updateValueAndValidity();
    }

    closeParentCust() {
        this.displaySelectParentCustomer = false;
    }

    removeSelParentCust() {
        this.selectedParentCust = [];
        this.billableCusList = [];
        this.serviceForm.patchValue({
            billableCustomerId: null
        });
    }
    newActivationFlag: boolean = false;
    approvePlanOpen(planId: any, nextApproverId: any, serviceMappingId: any, status: any): void {
        if (status === 'NewActivation') {
            this.newActivationFlag = true;
        } else {
            this.newActivationFlag = false;
        }

        this.approved = false;
        this.selectStaff = null;
        this.approvePlanData = [];
        this.assignPlanID = planId;
        this.nextApproverId = nextApproverId;
        this.customerServiceMappingId = serviceMappingId;
        this.assignPlanForm.reset();
        this.assignPlansubmitted = false;

        //  OPEN MATERIAL DIALOG INSTEAD
        this.currentDialogRef = this.dialog.open(this.approveServiceDialog, {
            width: '700px',
            disableClose: false,
            autoFocus: false,
            panelClass: 'approve-service-dialog'
        });
    }
    selectedPlan: any = null;

    pickModalOpen(data: any): void {
        this.selectedPlan = data;

        //  OPEN CONFIRMATION DIALOG
        this.currentDialogRef = this.dialog.open(this.pickServiceDialog, {
            width: '500px',
            disableClose: false,
            autoFocus: false,
            panelClass: 'pick-service-dialog'
        });
    }

    //  ADD confirmPickService METHOD
    confirmPickService(): void {
        let url = `workflow/pickupworkflow?eventName=CUSTOMERSERVICETERMINATION&entityId=${this.selectedPlan?.customerServiceMappingId}`;

        this.planManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getActivePlanDetails();
                this.closeDialog();

                if (response.responseCode == 417) {
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.toastr.success(response.responseMessage, 'Success!');
                }
            },
            (error: any) => {
                this.toastr.error(error.error.ERROR, 'Error!');
            }
        );
    }


    rejectPlanOpen(planId: any, nextApproverId: any, mappingId: any, status: any): void {
        if (status === 'NewActivation') {
            this.newActivationFlag = true;
        } else {
            this.newActivationFlag = false;
        }

        this.reject = false;
        this.selectStaffReject = null;
        this.rejectPlanData = [];
        this.assignPlanID = planId;
        this.nextApproverId = nextApproverId;
        this.customerServiceMappingId = mappingId;
        this.rejectPlanForm.reset();
        this.rejectPlanSubmitted = false;
        this.currentDialogRef = this.dialog.open(this.rejectServiceDialog, {
            width: '700px',
            disableClose: false,
            autoFocus: false,
            panelClass: 'reject-service-dialog'
        });
    }

    StaffReasignList(data: any): void {
        this.customerServiceMappingId = data.customerServiceMappingId;

        let url = `teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.customerServiceMappingId}&eventName=CUSTOMERSERVICETERMINATION`;

        this.planManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedPlanid = data.id;
                this.approvableStaff = [];

                if (response.responseCode == 417) {
                    this.toastr.error(response.responseMessage, 'Error!');
                } else {
                    this.toastr.success(response.responseMessage, 'Success!');

                    if (response.dataList != null) {
                        this.approvableStaff = response.dataList;
                        this.approved = true;

                        //  OPEN MATERIAL DIALOG INSTEAD
                        this.currentDialogRef = this.dialog.open(this.reassignServiceDialog, {
                            width: '700px',
                            disableClose: false,
                            autoFocus: false,
                            panelClass: 'reassign-service-dialog'
                        });
                    }
                }
            },
            (error: any) => {
                console.log(error, error);
                this.toastr.error(error.error.ERROR, 'Error!');
            }
        );
    }

    reassignWorkflow() {
        let url: any;
        // this.remarks = this.assignPlanForm.controls.remark;
        if (this.customerServiceMappingId != null) {
            url = `/teamHierarchy/reassignWorkflow?entityId=${this.customerServiceMappingId}&eventName=CUSTOMER_SERVICE_TERMINATION&assignToStaffId=${this.selectStaff}&remark=${this.assignPlanForm.value.remark}`;

            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.displayApprovePlan = false;
                    this.getActivePlanDetails();
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-times-circle"
                        // });
                    } else {
                        this.toastr.success(`Assigned to the next staff successfully`, 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: "Assigned to the next staff successfully.",
                        //     icon: "far fa-times-circle"
                        // });
                    }
                },
                error => {
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
            this.toastr.error(`Please Aprove Before Reassigne`, 'Failed!')
            // this.messageService.add({
            //     severity: "error",
            //     summary: "Error",
            //     detail: "Please Aprove Before Reassigne",
            //     icon: "far fa-times-circle"
            // });
        }
    }

    assignPlan() {
        this.assignPlansubmitted = true;
        this.approved = false;
        this.selectStaff = null;
        this.approvePlanData = [];
        if (this.assignPlanForm.valid) {
            let url;
            if (this.newActivationFlag)
                url = `/subscriber/approveCustomerServiceAdd?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=true&remarks=${this.assignPlanForm.controls.remark.value}`;
            else
                url = `/subscriber/approveCustomerServiceTermination?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=true&remarks=${this.assignPlanForm.controls.remark.value}`;
            let assignCAFData = {
                planId: this.assignPlanID,
                nextStaffId: "",
                flag: "approved",
                remark: this.assignPlanForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };

            this.planManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (!this.searchkey && !this.searchKeyType) {
                        // this.getPostoaidPlan("");
                        this.getActivePlanDetails();
                    } else {
                        // this.searchPlan();
                    }
                    if (response.responseCode === 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-times-circle"
                        // });
                    } else {
                        this.closeDialog();
                        this.toastr.success("Successfully", 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });

                        this.assignPlanForm.reset();
                        this.assignPlansubmitted = false;
                        if (response.dataList != null) {
                            this.approvePlanData = response.dataList;
                            this.approved = true;
                        } else {
                            this.assignApporvePlanModal = false;
                        }
                    }
                    //   this.assignApporvePlanModal = false;
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
    }

    closeRejectPlanModal(): void {
        this.closeDialog();
    }

    closereassignWorkflow() {
        this.reassignWorkflow;
    }
    rejectPlan() {
        this.rejectPlanSubmitted = true;
        if (this.rejectPlanForm.valid) {
            let assignCAFData = {
                planId: this.assignPlanID,
                nextStaffId: "",
                flag: "Rejected",
                remark: this.rejectPlanForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };
            let url;
            if (this.newActivationFlag)
                url = `/subscriber/approveCustomerServiceAdd?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=false&remarks=${this.assignPlanForm.controls.remark.value}`;
            else
                url = `/subscriber/approveCustomerServiceTermination?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=false&remarks=${this.assignPlanForm.controls.remark.value}`;
            this.planManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (!this.searchkey && !this.searchKeyType) {
                        // this.getPostoaidPlan("");
                        this.getActivePlanDetails();
                    } else {
                        // this.searchPlan();
                    }
                    this.toastr.success(`${response.message}`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.message,
                    //     icon: "far fa-check-circle"
                    // });

                    this.rejectPlanForm.reset();
                    this.rejectPlanSubmitted = false;
                    if (response.dataList != null) {
                        this.rejectPlanData = response.dataList;
                        this.reject = true;
                    } else {
                        this.rejectPlanModal = false;
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
    }

    assignToStaff(flag) {
        let url: any;
        let event;
        if (this.newActivationFlag) event = "CUSTOMER_SERVICE_ADD";
        else event = "CUSTOMER_SERVICE_TERMINATION";
        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.customerServiceMappingId}&eventName=${event}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.customerServiceMappingId}&eventName=${event}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.customerServiceMappingId}&eventName=${event}&nextAssignStaff=${this.selectStaffReject}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.customerServiceMappingId}&eventName=${event}&isApproveRequest=${flag}`;
            }
        }

        this.planManagementService.getMethod(url).subscribe(
            response => {
                this.assignApporvePlanModal = false;
                this.rejectPlanModal = false;
                // this.getPostoaidPlan("");
                this.getActivePlanDetails();

                this.assignApporvePlanModal = false;
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
        this.assignApporvePlanModal = false;
    }

    close(): void {
        this.closeDialog();
    }

    openAddWorkFlow(id, auditcustid) {
        // this.displayShiftLocationDetails = true;
        this.ifModelIsShow = true;
        this.showPlanConnectionNo = true;
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "CUSTOMER_SERVICE_ADD",
            planId: ""
        });
    }

    closeParentCustt() {
        this.ifModelIsShow = false;
        // this.displayShiftLocationDetails = false;
    }

    openEditWorkFlow(id, auditcustid) {
        // this.displayShiftLocationDetails = true;
        this.ifModelIsShow = true;
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "CUSTOMER_SERVICE_TERMINATION",
            planId: ""
        });
    }
    auditData: any = [];
    auditItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPageAuditList = 1;
    audittotalRecords: String;

    openAudit(auditcustid: any, pageData?: any): void {
        let currentPage: any;

        if (pageData) {
            currentPage = pageData + 1;
        } else {
            currentPage = this.currentPageAuditList;
        }

        const data = {
            page: currentPage,
            pageSize: this.auditItemPerPage
        };

        const url = `/subscriber/servicestatusAudit/${auditcustid}`;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.auditData = response.data.content;
                this.audittotalRecords = response.data.totalElements;

                //  OPEN MATERIAL DIALOG
                this.currentDialogRef = this.dialog.open(this.auditDetailsDialog, {
                    width: '1100px',
                    maxHeight: '90vh',
                    disableClose: false,
                    autoFocus: false,
                    panelClass: 'audit-details-dialog'
                });
            },
            (error: any) => {
                console.log(error, error);
                this.toastr.error(error.error.ERROR, 'Error!');
            }
        );
    }

    pageChangedauditList(pageNumber) {
        this.currentPageAuditList = pageNumber;
    }

    auditCloseModal(): void {
        this.closeDialog();
        this.auditData = [];
    }
    serviceStopBulkFlag: boolean = false;

    closedisplayApprovePlan() {
        this.closeDialog();
    }

    serviceStopId = [];

    isServiceResumeValid() {
        if (this.serviceStopBulkFlag) {
            return this.serviceStopId.some(x => x.custPlanStatus === "Hold");
        }
    }

    seviceStopBulk(data, e) {
        if (e.checked) {
            this.serviceStopBulkFlag = true;
            this.serviceStopId.push(data);
            if (this.serviceStopId.length > 0) {
                if (
                    this.serviceStopId[0].custPlanStatus.toLowerCase() !== data.custPlanStatus.toLowerCase()
                ) {
                    this.toastr.info(`Different Service Selected`, 'Please select service with same status!')
                    // this.messageService.add({
                    //     severity: "warn",
                    //     summary: "Diffrent Service Selected",
                    //     detail: "Please select service with same status!"
                    // });
                    this.serviceStopBulkFlag = false;
                }
            }
        } else {
            let requiredIndex;
            this.serviceStopId.forEach((element, i) => {
                if (element.custPlanMapppingId == data.custPlanMapppingId) requiredIndex = i;
            });
            if (requiredIndex >= 0) this.serviceStopId.splice(requiredIndex);
            if (this.serviceStopId.length > 0) {
                let check = [];
                this.serviceStopId.forEach(element => {
                    if (this.serviceStopId[0].custPlanStatus != element.custPlanStatus) check.push(element);
                });
                if (check.length == 0) this.serviceStopBulkFlag = true;
            }
            if (this.serviceStopId.length <= 0) this.serviceStopBulkFlag = false;
        }
    }
    serviceStop(): void {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `If Child customer is there child service also Stop, Do you want to continue?`,
                yesLabel: 'Yes',
                noLabel: 'No'
            }
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                let deactivatePlanReqModels: any[] = [];
                let data: any;

                if (this.serviceStopBulkFlag) {
                    this.serviceStopId.map((e: any) => {
                        deactivatePlanReqModels.push({
                            custServiceMappingId: e.custPlanMapppingId,
                            remarks: this.serviceStropRemarks,
                            reasonId: this.selectDeactivateReason
                        });
                    });

                    data = {
                        custId: this.serviceStopId[0].custId,
                        serviceStopBulkFlag: this.serviceStopBulkFlag,
                        deactivatePlanReqModels: deactivatePlanReqModels
                    };
                } else {
                    deactivatePlanReqModels.push({
                        custServiceMappingId: this.servicePerticularData.custPlanMapppingId,
                        remarks: this.serviceStropRemarks,
                        reasonId: this.selectDeactivateReason
                    });

                    data = {
                        custId: this.servicePerticularData.custId,
                        serviceStopBulkFlag: this.serviceStopBulkFlag,
                        deactivatePlanReqModels: deactivatePlanReqModels
                    };
                }

                const url = `/subscriber/stopServiceInBulk`;

                this.customerManagementService.postMethod(url, data).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(response.responseMessage, 'Error!');
                        } else {
                            this.closeDialog();
                            this.getActivePlanDetails();
                            this.serviceStopBulkFlag = false;
                            this.serviceStopId = [];
                            this.custPlanMappping.emit();
                            this.toastr.success(response.message, 'Success!');
                        }
                    },
                    (error: any) => {
                        console.log(error, error);
                        this.toastr.error(error.error.ERROR, 'Error!');
                    }
                );
            }
            else {
                this.toastr.info('You have rejected', 'Rejected');
                this.serviceStopBulkFlag = false;
                this.serviceStopId = [];
                this.selectDeactivateReason = '';
                this.serviceStropRemarks = '';
            }
        });

    }
    closeDialog(): void {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
            this.currentDialogRef = null;
        }

        // Reset all dialog data
        this.ifselecResonType = '';
        this.selectDeactivateReason = '';
        this.serviceStropRemarks = '';
        this.servicePerticularData = null;
        this.holdDays = null;
    }
    chekcPlanGroup(plan, planList) {
        if (this.custData.plangroupid !== null) {
            let groupPlanList = planList.filter(item => item.plangroupid == plan.plangroupid);
            return groupPlanList[0] === plan;
        }
        return true;
    }

    getSerialNumber(plan) {
        return plan.customerInventorySerialnumberDtos?.filter(item => item.primary).length > 0
            ? plan.customerInventorySerialnumberDtos?.filter(item => item.primary)[0].serialNumber
            : "";
    }

    closeServicePlanData() {
        this.displayDeleteReason = false;
        this.ifselecResonType = "";
        this.selectDeactivateReason = "";
        this.serviceStropRemarks = "";
        this.servicePerticularData = null;
        this.holdDays = "";
    }

    openPlanConnectionModal(plan) {
        this.planForConnection = plan;
        this.showPlanConnectionNo = true;
    }

    currentDate = new Date();
    serviceTerminationCheck(serviceEndDate, custPlanStatus) {
        if ((custPlanStatus == "Stop" || custPlanStatus == "STOP") && serviceEndDate) {
            serviceEndDate = new Date(serviceEndDate);
            if (serviceEndDate.getTime() < this.currentDate.getTime()) return false;
        }
        return true;
    }
    badgeTypeForStatus: any;
    displayStatus: any;
    checkStatus(planStatus, workflowStatus) {
        const status = planStatus?.toLowerCase().replace(/\s+/g, "") || "";
        const statusWorkflow = workflowStatus?.toLowerCase() || "";

        if (statusWorkflow === "new activation") {
            this.badgeTypeForStatus = "green";
            this.displayStatus = workflowStatus.toUpperCase();
        } else if (statusWorkflow === "rejected") {
            this.badgeTypeForStatus = "red";
            this.displayStatus = workflowStatus.toUpperCase();
        } else {
            this.displayStatus = planStatus.toUpperCase();

            switch (status) {
                case "active":
                case "ingrace":
                case "inprogress":
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

    openReactivateModel(data, type) {
        if (type === "Start") {
            this.confirmationService.confirm({
                message: "Do You want To Change Service Status from Disable to Active",
                header: "Active Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.reactivateService(data);
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

    reactivateService(serviceId) {
        const url = "/subscriber/activateServiceFromHold?serviceId=" + serviceId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getActivePlanDetails();
                this.toastr.success(`${response.message}`, 'Success!')
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Service Activated Successfully",
                //     detail: response.message,
                //     icon: "far fa-check-circle"
                // });
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

    keypress(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    openGracePeriod(service) {
        this.isGracePeriodModel = true;
        this.gracePeriod = "";
        this.gracePeriodData = service;
    }

    closeGracePeriod(): void {
        this.closeDialog();
        this.gracePeriodData = null;
        this.gracePeriod = '';
    }

    saveGracePeriod(): void {
        if (this.gracePeriod) {
            this.spinner.show();

            let gracedata = {
                custId: this.customerId,
                graceDays: Number(this.gracePeriod)
            };

            const url = `extendGraceDays`;

            this.customerManagementService.postMethod(url, gracedata).subscribe(
                (response: any) => {
                    this.spinner.hide();
                    this.toastr.success(response.msg, 'Success!');
                    this.closeDialog();  //  CLOSE DIALOG
                    this.gracePeriodData = null;
                    this.getActivePlanDetails();
                },
                (error: any) => {
                    this.spinner.hide();
                    this.closeDialog();
                    console.log(error, error);
                    this.toastr.error(error.error.ERROR, 'Error!');
                }
            );
        }
    }

    validateHoldDays(event: any) {
        const value = parseInt(event.target.value, 10);
        if (value < 1) {
            event.target.value = 1;
            this.holdDays = 1;
        } else if (value > 31) {
            event.target.value = 31;
            this.holdDays = 31;
        }
    }

    getHoldDaysOptions() {
        const options = [];
        const maxAttempts = this.servicePerticularData?.remainingPauseDays || 0;

        for (let i = 1; i <= maxAttempts; i++) {
            options.push({
                label: `${i}`,
                value: i
            });
        }

        return options;
    }
    // Add these properties to your existing component class

    // Modal flags
    showServiceActionModal: boolean = false;
    showGracePeriodModal: boolean = false;
    showApproveServiceModal: boolean = false;
    showAddServiceModal: boolean = false;

    // Modal data
    serviceActionTitle: string = '';
    serviceActionType: string = '';
    serviceActionRemarks: string = '';
    serviceActionDate: Date = new Date();
    selectedServiceForAction: any = null;

    gracePeriodDays: number = 0;
    gracePeriodStartDate: Date = new Date();
    gracePeriodRemarks: string = '';
    selectedServiceForGracePeriod: any = null;

    approveServiceRemarks: string = '';
    selectedServiceForApproval: any = null;

    // Table columns
    displayedColumns: string[] = [
        'select',
        'service',
        'serialNo',
        'invoiceType',
        'currentPlan',
        'expiryDate',
        'serviceHoldDate',
        'serviceResumeDate',
        'status',
        'holdBy',
        'remarks',
        'action'
    ];

    // Add these methods

    // openPaushSearviceMedel(plan: any, actionType: string) {
    //     this.selectedServiceForAction = plan;
    //     this.serviceActionType = actionType;
    //     this.serviceActionRemarks = '';

    //     switch (actionType) {
    //         case 'Delete':
    //             this.serviceActionTitle = 'Service Termination';
    //             break;
    //         case 'Stop':
    //             this.serviceActionTitle = 'Service Stop';
    //             break;
    //         case 'Pause':
    //             this.serviceActionTitle = 'Service Hold';
    //             break;
    //         case 'Start':
    //             this.serviceActionTitle = 'Service Resume';
    //             break;
    //         case 'Enable':
    //             this.serviceActionTitle = 'Enable Service';
    //             break;
    //     }

    //     this.showServiceActionModal = true;
    // }
    openServiceActionModal(actionType: string, title: string): void {
        this.serviceActionType = actionType;
        this.serviceActionTitle = title;
        this.serviceActionRemarks = '';
        this.serviceActionDate = null;

        this.currentDialogRef = this.dialog.open(this.serviceActionDialog, {
            width: '600px',
            disableClose: false,
            autoFocus: false
        });
    }
    closeServiceActionModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.showServiceActionModal = false;
        this.selectedServiceForAction = null;
        this.serviceActionRemarks = '';
    }

    submitServiceAction(): void {
        // Based on serviceActionType, call appropriate method
        switch (this.serviceActionType) {
            case 'Delete':
                this.deleteServicePlanData();
                break;
            case 'Stop':
                this.serviceStop();
                break;
            case 'Pause':
                this.pauseService();
                break;
            case 'Start':
                this.playService();
                break;
            case 'Enable':
                this.reactivateService(this.servicePerticularData.custServiceMappingId);
                break;
        }

        this.closeServiceActionModal();
    }

    openGracePeriodModal(plan: any): void {
        this.selectedServiceForGracePeriod = plan;
        this.gracePeriod = '';

        //  OPEN MATERIAL DIALOG
        this.currentDialogRef = this.dialog.open(this.gracePeriodDialog, {
            width: '500px',
            disableClose: false,
            autoFocus: false,
            panelClass: 'grace-period-dialog'
        });
    }

    closeGracePeriodModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.showGracePeriodModal = false;
        this.selectedServiceForGracePeriod = null;
    }

    submitGracePeriod(): void {
        if (this.gracePeriodDays) {
            this.spinner.show();
            let gracedata = {
                custId: this.customerId,
                graceDays: Number(this.gracePeriodDays)
            };

            const url = '/extendGraceDays';
            this.customerManagementService.postMethod(url, gracedata).subscribe(
                (response: any) => {
                    this.spinner.hide();
                    this.toastr.success(`${response.msg}`, 'Success!')
                    // this.messageService.add({
                    //     severity: 'success',
                    //     summary: 'Successfully',
                    //     detail: response.msg,
                    //     icon: 'far fa-check-circle'
                    // });
                    this.closeGracePeriodModal();
                    this.gracePeriodData = null;
                    this.getActivePlanDetails();
                },
                (error: any) => {
                    this.spinner.hide();
                    this.closeGracePeriodModal();
                    console.log(error, 'error');
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: 'error',
                    //     summary: 'Error',
                    //     detail: error.error.ERROR,
                    //     icon: 'far fa-times-circle'
                    // });
                }
            );
        }
    }

    getApproveServiceDetails(plan: any) {
        this.selectedServiceForApproval = plan;
        this.approveServiceRemarks = '';
        this.showApproveServiceModal = true;
    }
    openApproveServiceModal(planId: any, nextApproverId: any, serviceMappingId: any, status: any): void {
        if (status === 'NewActivation') {
            this.newActivationFlag = true;
        } else {
            this.newActivationFlag = false;
        }

        this.approved = false;
        this.selectStaff = null;
        this.approvePlanData = [];
        this.assignApporvePlanModal = true;
        this.assignPlanID = planId;
        this.nextApproverId = nextApproverId;
        this.customerServiceMappingId = serviceMappingId;
        this.approveServiceRemarks = '';

        // Find the service details
        this.selectedServiceForApproval = this.custCurrentPlanList.find(
            service => service.customerServiceMappingId === serviceMappingId
        );

        this.currentDialogRef = this.dialog.open(this.approveServiceDialog, {
            width: '700px',
            disableClose: false,
            autoFocus: false
        });
    }
    closeApproveServiceModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.showApproveServiceModal = false;
        this.selectedServiceForApproval = null;
    }
    approveService(): void {
        if (this.approveServiceRemarks) {
            let url: any;
            if (this.newActivationFlag) {
                url = `/subscriber/approveCustomerServiceAdd?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=true&remarks=${this.approveServiceRemarks}`;
            } else {
                url = `/subscriber/approveCustomerServiceTermination?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=true&remarks=${this.approveServiceRemarks}`;
            }

            this.planManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.getActivePlanDetails();

                    if (response.responseCode === 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!')
                        // this.messageService.add({
                        //     severity: 'error',
                        //     summary: 'Error',
                        //     detail: response.responseMessage,
                        //     icon: 'far fa-times-circle'
                        // });
                    } else {
                        this.toastr.success(`${response.message}`, 'Success!')
                        // this.messageService.add({
                        //     severity: 'success',
                        //     summary: 'Successfully',
                        //     detail: response.message,
                        //     icon: 'far fa-check-circle'
                        // });

                        if (response.dataList != null) {
                            this.approvePlanData = response.dataList;
                            this.approved = true;
                        } else {
                            this.closeApproveServiceModal();
                        }
                    }
                },
                (error: any) => {
                    console.log(error, 'error');
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: 'error',
                    //     summary: 'Error',
                    //     detail: error.error.ERROR,
                    //     icon: 'far fa-times-circle'
                    // });
                }
            );
        }
    }

    rejectService(): void {
        if (this.approveServiceRemarks) {
            let url: any;
            if (this.newActivationFlag) {
                url = `/subscriber/approveCustomerServiceAdd?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=false&remarks=${this.approveServiceRemarks}`;
            } else {
                url = `/subscriber/approveCustomerServiceTermination?customerServiceMappingId=${this.customerServiceMappingId}&isApproveRequest=false&remarks=${this.approveServiceRemarks}`;
            }

            this.planManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.getActivePlanDetails();
                    this.toastr.success(`${response.message}`, 'Success!')
                    // this.messageService.add({
                    //     severity: 'success',
                    //     summary: 'Successfully',
                    //     detail: response.message,
                    //     icon: 'far fa-check-circle'
                    // });

                    if (response.dataList != null) {
                        this.rejectPlanData = response.dataList;
                        this.reject = true;
                    } else {
                        this.closeApproveServiceModal();
                    }
                },
                (error: any) => {
                    console.log(error, 'error');
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: 'error',
                    //     summary: 'Error',
                    //     detail: error.error.ERROR,
                    //     icon: 'far fa-times-circle'
                    // });
                }
            );
        }
    }

    // openAddServiceModal() {
    //     this.showAddServiceModal = true;
    // }

    closeAddServiceModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.showAddServiceModal = false;
    }

    submitAddService(): void {

        if (this.serviceForm.valid) {
            this.addServicePlanData = { ...this.custData };
            this.addServicePlanData.planMappingList = [];

            if (this.serviceForm.controls.planCategory.value === 'groupPlan') {
                // For group plan
                this.addServicePlanData = this.serviceForm.getRawValue();
                this.addServicePlanData.id = this.custData.id;
                this.addServicePlanData.custtype = 'Prepaid';
                this.addServicePlanData.serviceareaid = this.custData.serviceareaid;
                this.addServicePlanData.planMappingList = [];

                this.planServiceAdd(this.addServicePlanData);
            } else {
                // For individual plan
                this.addServicePlanData = this.serviceForm.getRawValue();
                this.addServicePlanData.id = this.custData.id;
                this.addServicePlanData.custtype = 'Prepaid';
                this.addServicePlanData.serviceareaid = this.custData.serviceareaid;
                this.addServicePlanData.planMappingList = this.servicePlanFormArray.value;

                // Add additional fields to each plan mapping
                this.addServicePlanData.planMappingList.forEach((plan) => {
                    plan.planCategory = this.serviceForm.controls.planCategory.value;
                    plan.billTo = this.serviceForm.controls.billTo.value;
                    plan.billableCustomerId = this.serviceForm.controls.billableCustomerId.value;
                    plan.newAmount = plan.offerprice;
                    plan.isInvoiceToOrg = this.serviceForm.controls.isInvoiceToOrg.value;
                });

                if (this.addServicePlanData.planMappingList.length === 0) {
                    this.toastr.error(`Please add atleast one service and plan`, 'Failed!')
                    // this.messageService.add({
                    //     severity: 'error',
                    //     summary: 'Error',
                    //     detail: 'Please add atleast one service and plan',
                    //     icon: 'far fa-times-circle'
                    // });
                } else {
                    this.planServiceAdd(this.addServicePlanData);
                }
            }
        }
    }

    getStatusDisplay(plan: any): string {
        // Check workflow status first
        if (plan.custServMappingStatus) {
            const workflowStatus = plan.custServMappingStatus.toLowerCase();

            if (workflowStatus === 'newactivation' || workflowStatus === 'new activation') {
                return 'NEW ACTIVATION';
            } else if (workflowStatus === 'rejected') {
                return 'REJECTED';
            } else if (workflowStatus === 'pending') {
                return 'PENDING';
            }
        }

        // Return plan status
        if (plan.custPlanStatus) {
            return plan.custPlanStatus.toUpperCase();
        }

        return 'UNKNOWN';
    }

    openWorkflowAuditModal(plan: any) {
        this.auditcustid = plan.id;
        this.ifModelIsShow = true;
    }
    // Add these properties to your class (after existing access properties)
    approveServiceAccess: boolean = false;
    pickServiceAccess: boolean = false;
    reassignServiceAccess: boolean = false;
    gracePeriodAccess: boolean = false;
    serviceEnableAccess: boolean = false;

    // // Then in your constructor (after existing permission checks), add these:
    // constructor(
    //     // ... existing parameters
    //     loginService: LoginService
    // ) {
    //     // ... existing code ...

    //     this.approveServiceAccess = loginService.hasPermission(
    //         this.custType == "Prepaid"
    //             ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_APPROVE
    //             : POST_CUST_CONSTANTS.POST_CUST_SERVICE_APPROVE
    //     );

    //     this.pickServiceAccess = loginService.hasPermission(
    //         this.custType == "Prepaid"
    //             ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_PICK
    //             : POST_CUST_CONSTANTS.POST_CUST_SERVICE_PICK
    //     );

    //     this.reassignServiceAccess = loginService.hasPermission(
    //         this.custType == "Prepaid"
    //             ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_REASSIGN
    //             : POST_CUST_CONSTANTS.POST_CUST_SERVICE_REASSIGN
    //     );

    //     this.gracePeriodAccess = loginService.hasPermission(
    //         this.custType == "Prepaid"
    //             ? PRE_CUST_CONSTANTS.PRE_CUST_GRACE_PERIOD
    //             : POST_CUST_CONSTANTS.POST_CUST_GRACE_PERIOD
    //     );

    //     this.serviceEnableAccess = loginService.hasPermission(
    //         this.custType == "Prepaid"
    //             ? PRE_CUST_CONSTANTS.PRE_CUST_SERVICE_ENABLE
    //             : POST_CUST_CONSTANTS.POST_CUST_SERVICE_ENABLE
    //     );

    //     // ... rest of existing constructor code
    // }

}
