import { Component, Input, Output, OnInit, EventEmitter, ViewChild, TemplateRef, viewChild, SimpleChanges } from "@angular/core";
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
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { eventNames } from "process";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
declare var $: any;

@Component({
    selector: "app-cust-service-management",
    templateUrl: "./cust-service-management.component.html",
    styleUrls: ["./cust-service-management.component.css"],
    standalone: false
})
export class CustServiceManagementComponent implements OnInit {
    @ViewChild('approverDialog') approverDialog!: TemplateRef<any>;
    @ViewChild(WorkflowAuditDetailsModalComponent)
    custauditWorkflowModal: WorkflowAuditDetailsModalComponent;
    // MappingDataSource = new MatTableDataSource<any>();
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    dataSource!: MatTableDataSource<any>;
    displayedCustomerColumns: string[] = ['select', 'Name', 'username'];
    displayedstaffRejectColumns: string[] = ['select', 'fullName', 'username'];
    displayApprovableStaff: string[] = ['select', 'fullName', 'username'];
    displayedColumns2: string[] = ['serviceStopTime', 'action', 'staffName', 'reasonCategory', 'remarks'];
    displayedColumns1: string[] = [
        'service',
        'plan',
        'validity',
        'offerPrice',
        'discountType',
        'discount',
        'discountExpiryDate',
        // 'invoicetype',
        'istrialplan',
        'delete'
    ];
    displayedColumns: string[] = ['select', 'service', 'serialNo', 'invoiceType', 'currentPlan', 'expiryDate',
        'serviceHoldDate', 'serviceResumeDate', 'status', 'holdBy', 'resumeBy', 'remarks', 'nickname']

    @Input() custData: any;
    @Input() isLeadMaster: boolean = false;
    @Input() isThroughLead: boolean = true;
    @Input() ifcustCaf: boolean = false;
    @Output() custPlanMappping = new EventEmitter();
    @Output() backButton = new EventEmitter();
    @ViewChild("CreateService") CreateService: TemplateRef<any>;
    @ViewChild("AuditDetails") AuditDetails: TemplateRef<any>;
    @ViewChild("ServiceTerminal") ServiceTerminal: TemplateRef<any>;
    @ViewChild("customerSelect") customerSelect: TemplateRef<any>;

    // Individual dialog refs
    approveDialogRef!: MatDialogRef<any>;
    CreateDialogRef: MatDialogRef<any>;
    auditDialogRef!: MatDialogRef<any>;
    serviceTerminalDialogRef!: MatDialogRef<any>;
    customerSelectDialogRef!: MatDialogRef<any>;
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: "",
    });
    custCurrentPlanList = [];
    serviceForm: UntypedFormGroup;
    planGroupForm: UntypedFormGroup;
    servicePlanFormArray: UntypedFormArray;
    planDetailsCategory = [
        { label: "Individual", value: "individual" },
        { label: "Plan Group", value: "groupPlan" },
    ];
    isInvoiceData = [
        { label: "YES", value: true },
        { label: "NO", value: false },
    ];
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    filterPlanData: any = [];
    planByServiceArea: any = [];
    plantypaSelectData: any = [];
    servicePlanItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    servicePlantotalRecords: number;
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
    ifModelIsShow: boolean = false;
    // selectedParentCust: any = [];
    selectedParentCust: any = null;
    isServiceThroughLead: boolean = false;
    billableCusList: any;
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
        { label: "Independent", value: "Independent" },
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
    showPlanConnectionNo: boolean = false;
    planForConnection;
    custCurrentPlanListLength: number;
    displaySelectParentCustomer: boolean = false;

    addServiceAccess: boolean = false;
    searchStaffDeatil: any;
    approvePlan: any[];
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private fb: UntypedFormBuilder,
        private datePipe: DatePipe,
        private planManagementService: PlanManagementService,
        public PaymentamountService: PaymentamountService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public loginService: LoginService
    ) {
        this.commondropdownService.findAllplanGroups();
    }

    ngOnInit(): void {
        this.addServiceAccess = this.loginService.hasPermission(
            this.custData.custtype == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_SERVICE_CREATE
                : POST_CUST_CONSTANTS.POST_CUST_CAF_SERVICE_CREATE
        );
        this.serviceForm = this.fb.group({
            parentCustomerId: [""],
            planCategory: [{ value: '', disabled: true }],
            billTo: [{ value: '', disabled: true }],
            billableCustomerId: [{ value: '', disabled: true }],
            isInvoiceToOrg: [false],
            discount: ["", [Validators.max(99)]],
            plangroupid: [""],
            istrialplan: [""],
            planMappingList: (this.servicePlanFormArray = this.fb.array([]))
        });

        this.planGroupForm = this.fb.group({
            discount: ["", [Validators.max(99)]],
            planId: ["", Validators.required],
            service: ["", Validators.required],
            validity: ["", Validators.required],
            offerprice: [""],
            validityUnit: [{ value: '', disabled: true }],
            newAmount: [""],
            istrialplan: [""],
            discountType: [""],
            discountExpiryDate: [""],
            invoiceType: [""],
        });

        this.assignPlanForm = this.fb.group({
            remark: ["", Validators.required],
        });

        this.rejectPlanForm = this.fb.group({
            remark: ["", Validators.required],
        });

        // this.servicePlanFormArray = this.fb.array([this.createServiceFormGroup()]);
        this.commondropdownService.getBillToData();
        this.commondropdownService.getplanservice();
        this.commondropdownService.planCreationType();
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
        this.getServiceByServiceAreaID(this.custData.serviceareaid);
        this.getPlanbyServiceArea(this.custData.serviceareaid);
        this.dataSource = new MatTableDataSource(this.servicePlanFormArray.controls);
        // this.dataSource.data = this.servicePlanFormArray.value;
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.custData && this.custData) {
            this.updateDisplayedColumns();
        }
    }

    updateDisplayedColumns() {
        // Base columns in your table, excluding Action column
        this.displayedColumns = [
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
            'resumeBy',
            'remarks',
            'nickname'
        ];

        // Add Action column only if status NOT 'newactivation'
        if (this.custData.status?.trim().toLowerCase() !== 'newactivation') {
            this.displayedColumns.push('action');
        }
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
    }
    pageChange(event: PageEvent) {
        this.currentPageServicePlan = event.pageIndex + 1;
        this.servicePlanItemPerPage = event.pageSize;
        this.getActivePlanDetails();
    }

    getLoggedinUserData() {
        let staffId = localStorage.getItem("userId");
        this.staffUserId = localStorage.getItem("userId");
        this.loggedInUser = localStorage.getItem("loggedInUser");
    }

    openAddServiceModal() {
        this.CreateDialogRef = this.dialog.open(this.CreateService, {
            width: '1200px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
        });
        if (!this.isLeadMaster && this.isThroughLead) {
            this.confirmationService.confirm({
                header: "Alert",
                message:
                    "You need to create lead first in order to add service. Do you want to create lead? (Yes/No)",
                icon: "pi pi-info-circle",
                accept: () => {
                    if (this.commondropdownService.isPlanOnDemand) {
                        this.router.navigate(["/home/enterprise-lead"], {
                            queryParams: { id: this.custData.id },
                        });
                    } else {
                        this.router.navigate(["/home/lead-management"], {
                            queryParams: { id: this.custData.id },
                        });
                    }
                },
                reject: () => {
                    return false;
                },
            });
        } else {
            this.planGroupForm.reset();
            this.serviceForm.reset();
            this.servicePlanFormArray = this.fb.array([]);
            this.filterPlanData = [];
            // this.dataSource.data = this.servicePlanFormArray.contrgols
            var planCategory = "";
            if (this.custData.plangroupid) {
                this.isPlanCategoryGroup = false;
                planCategory = "groupPlan";
                // this.serviceForm.controls.plangroupid.patchValue(this.custData.plangroupid);
                // this.serviceForm.controls.discount.patchValue(this.custData.planMappingList[0].discount);
                // this.serviceForm.controls.isInvoiceToOrg.patchValue(
                //   this.custData.planMappingList[0].isInvoiceToOrg
                // );
            } else {
                this.isPlanCategoryGroup = false;
                planCategory = "individual";
                this.serviceForm.controls.plangroupid.disable();
                this.serviceForm.controls.discount.disable();
                this.serviceForm.controls.isInvoiceToOrg.disable();
            }

            if (this.custData.planMappingList.length > 0) {
                this.serviceForm.patchValue({
                    parentCustomerId: this.custData.parentCustomerId,
                    planCategory: planCategory,
                    billTo:
                        this.custData.planMappingList.length > 0 ? this.custData.planMappingList[0].billTo : "",
                    billableCustomerId:
                        this.custData.planMappingList.length > 0
                            ? this.custData.planMappingList[0].billableCustomerId
                            : "",
                    isInvoiceToOrg:
                        this.custData.planMappingList.length > 0
                            ? this.custData.planMappingList[0].isInvoiceToOrg
                            : "",
                });
            }

            $("#addServiceModal").modal("show");
        }
    }

    createServiceCloseDialog() {
        this.CreateDialogRef.close();
        this.submitted = false;
        this.plansubmitted = false;
        this.servicePlanFormArray = this.fb.array([]);
        this.dataSource.data = this.servicePlanFormArray.controls;
    }

    getServiceByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/serviceArea/getAllServicesByServiceAreaId";
        this.customerManagementService.postMethod(url, data).subscribe((response: any) => {
            this.serviceAreaBYserviceList = response.dataList;
            if (response.dataList.size > 0) {
            }
        });
    }

    createServiceFormGroup(): UntypedFormGroup {
        return this.fb.group({
            discount: [this.planGroupForm.value.discount, [Validators.max(99)]],
            planId: [this.planGroupForm.value.planId, Validators.required],
            service: [this.planGroupForm.value.service, Validators.required],
            validity: [this.planGroupForm.value.validity, Validators.required],
            offerprice: [this.planGroupForm.value.offerprice],
            validityUnit: [this.planGroupForm.value.validityUnit],
            istrialplan: [this.planGroupForm.value.istrialplan],
            discountType: [this.planGroupForm.value.discountType],
            discountExpiryDate: [this.planGroupForm.value.discountExpiryDate],
            invoiceType: [this.planGroupForm.value.invoiceType],
        });
    }

    planValidity = "";
    planunitValidity = "";
    onAddPlanServiceField() {
        this.plansubmitted = true;
        if (this.planGroupForm.valid) {
            this.servicePlanFormArray.push(this.createServiceFormGroup());
            this.dataSource.data = this.servicePlanFormArray.controls;
            this.planGroupForm.reset();
            this.plansubmitted = false;
            this.discountType = "One-time";
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
                    this.plantypaSelectData = response.postPaidPlan;
                } else {
                    this.plantypaSelectData = [];
                    this.toastr.info(`${response.responseMessage}`, 'Plan not available for this Plan type and service!');

                }
            },
            response => {

                this.plantypaSelectData = [];

                this.toastr.info(`${response.responseMessage}`, 'Plan not available for this Plan type and service!');

            }
        );
    }

    pageChangedPlanService(pageNumber) {
        this.currentPageServicePlan = pageNumber;
    }

    deleteConfirmonChargeField(planFieldIndex: number) {
        if (planFieldIndex || planFieldIndex === 0) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.onRemovePayMapping(planFieldIndex);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
        // if (planFieldIndex || planFieldIndex === 0) {
        //     this.confirmationService.confirm({
        //         message: "Do you want to delete this Plan?",
        //         header: "Delete Confirmation",
        //         icon: "pi pi-info-circle",
        //         accept: () => {
        //             this.onRemovePayMapping(planFieldIndex);
        //         },
        //         reject: () => {
        //             error: (error) => {
        //                 this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
        //             }
        //         },
        //     });
        // }
    }

    async onRemovePayMapping(planFieldIndex: number) {
        this.servicePlanFormArray.removeAt(planFieldIndex);
        this.dataSource.data = this.servicePlanFormArray.controls;
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
                    validityUnit: planDetailData.unitsOfValidity,
                });
                this.planGroupForm.get('offerprice')?.updateValueAndValidity();
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    addPlanService() {
        if (this.serviceForm.valid) {
            this.addServicePlanData = this.custData;
            this.addServicePlanData.planMappingList = [];
            // if (this.serviceForm.controls.planCategory.value == "groupPlan") {
            //   //this.addServicePlanData = this.serviceForm.getRawValue();
            //   this.addServicePlanData.id = this.custData.id;
            //   // this.addServicePlanData.custtype = "Prepaid";
            //   // this.addServicePlanData.serviceareaid = this.custData.serviceareaid;
            //   this.addServicePlanData.planMappingList = [];
            //   console.log("this.addServicePlanData", this.addServicePlanData);
            //   this.planServiceAdd(this.addServicePlanData);
            // } else {
            //this.addServicePlanData = this.serviceForm.getRawValue();
            this.addServicePlanData.id = this.custData.id;
            // this.addServicePlanData.custtype = "Prepaid";
            // this.addServicePlanData.serviceareaid = this.custData.serviceareaid;
            this.addServicePlanData.planMappingList = this.servicePlanFormArray.value;

            this.addServicePlanData.planMappingList.forEach(plan => {
                plan.planCategory = this.serviceForm.controls.planCategory.value;
                plan.billTo = this.serviceForm.controls.billTo.value;
                plan.billableCustomerId = this.serviceForm.controls.billableCustomerId.value;
                plan.newAmount = plan.offerprice;
                plan.isInvoiceToOrg = this.serviceForm.controls.isInvoiceToOrg.value;
            });

            if (this.addServicePlanData.planMappingList.length <= 0) {
                error: (error) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Please add atleast one service and plan!');
                }


            } else {
                this.planServiceAdd(this.data);
                this.CreateDialogRef.close();
            }
        }
        // }
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
            dunningCategory: this.dunningCategory,
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
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.serviceForm.reset();
                    this.planGroupForm.reset();
                    this.submitted = false;
                    this.plansubmitted = false;
                    this.servicePlanFormArray.controls = [];
                    this.dataSource.data = this.servicePlanFormArray.controls
                    $("#addServiceModal").modal("hide");
                    this.getActivePlanDetails();
                    this.toastr.success(`${response.responseMessage || "Service Added Successfully"}`, 'Success!');


                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, "error");

            }
        );
    }

    getActivePlanDetails() {
        let url: any;

        if (this.ifcustCaf) {
            url =
                "/subscriber/getPlanByCustService/" +
                this.custData.id +
                "?status=NewActivation" +
                "&isNotChangePlan=true";
        } else {
            url =
                "/subscriber/getPlanByCustService/" +
                this.custData.id +
                "?status=Active" +
                "?isAllRequired=true" +
                "&isNotChangePlan=true";
        }
        this.serviceStopBulkFlag = false;
        this.serviceStopId = [];
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // if (response.responseCode == 200) {
                this.custCurrentPlanList = response.dataList;
                this.servicePlantotalRecords = response.totalRecords || this.custCurrentPlanList.length;
                this.dataSource.data = this.custCurrentPlanList;
                if (this.custCurrentPlanList == null) {
                    this.custCurrentPlanList = [];
                }
                this.servicePlanFormArray.clear();
        this.custCurrentPlanList.forEach(data => {
            this.servicePlanFormArray.push(this.fb.group({
                service: [data.service || data.serviceName],
                planId: [data.planId],
                validity: [data.validity],
                validityUnit: [data.unitsOfValidity || data.validityUnit],
                offerprice: [data.offerprice],
                discountType: [data.discountType || 'One-time'],
                discount: [data.discount || 0],
                istrialplan: [data.istrialplan || false],
                discountExpiryDate: [data.discountExpiryDate]
            }));
        });

        this.dataSource.data = this.servicePlanFormArray.controls;
        this.servicePlantotalRecords = response.totalRecords || this.servicePlanFormArray.length;
                // this.custCurrentPlanList = this.custCurrentPlanList.filter(
                //   data => data.custPlanStatus == "Active" || data.custPlanStatus == "ACTIVE"
                // );

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
                });

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
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


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

    closeParentCustt() {
        this.ifModelIsShow = false;
    }

    deleteServicePlanData() {
        let planMapId = this.servicePerticularData.planmapid;
        let custId = this.servicePerticularData.custId;
        let planId = this.servicePerticularData.planId;

        let data1 = {};

        if (!this.serviceStopBulkFlag) {
            const url =
                "/subscriber/deleteService/" +
                planId +
                "?custId=" +
                custId +
                "&planMapId=" +
                planMapId +
                "&reasonId=" +
                this.selectDeactivateReason;
            this.customerManagementService.postMethod(url, data1).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    } else {
                        this.getActivePlanDetails();
                        // $("#StopServiceModal").modal("show");
                        $("#StopServiceModal").modal("hide");
                        this.toastr.success(`${response.message}`, 'Success!');


                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        } else {
            let data;
            let terminateService = [];
            this.serviceStopId.map(e => {
                terminateService.push({
                    custPlanMappingId: e.planmapid,
                });
            });
            data = {
                customerId: this.serviceStopId[0].custId,
                reason: this.selectDeactivateReason,
                terminateService: terminateService,
            };
            const url = "/subscriber/terminateServiceInBulk";
            this.customerManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    } else {
                        $("#StopServiceModal").modal("hide");
                        this.getActivePlanDetails();
                        this.serviceStopBulkFlag = false;
                        this.serviceStopId = [];
                        this.custPlanMappping.emit();
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    console.log(error, "error");

                }
            );
        }
    }

    pauseService() {
        let custId = this.servicePerticularData.custId;
        let data;
        if (!this.serviceStopBulkFlag) {
            data = {
                custId: custId,
                deactivatePlanReqModels: [
                    {
                        custServiceMappingId: this.servicePerticularData.customerServiceMappingId,
                        remarks: this.serviceStropRemarks,
                    },
                ],
            };
        } else {
            let deactivatePlanReqModels = [];
            this.serviceStopId.map(e => {
                deactivatePlanReqModels.push({
                    custServiceMappingId: e.customerServiceMappingId,
                    remarks: this.serviceStropRemarks,
                });
            });
            data = {
                custId: this.custData.id,
                deactivatePlanReqModels: deactivatePlanReqModels,
            };
        }
        const url = "/subscriber/holdServiceInBulk";
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    $("#StopServiceModal").modal("hide");
                    this.serviceStopBulkFlag = false;
                    this.serviceStopId = [];
                    this.getActivePlanDetails();
                    this.custPlanMappping.emit();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, "error");

            }
        );
    }
    playService() {
        let data;
        let custId = this.servicePerticularData.custId;
        if (!this.serviceStopBulkFlag) {
            data = {
                custId: custId,
                deactivatePlanReqModels: [
                    {
                        custServiceMappingId: this.servicePerticularData.customerServiceMappingId,
                        remarks: this.serviceStropRemarks,
                    },
                ],
            };
        } else {
            let deactivatePlanReqModels = [];
            this.serviceStopId.map(e => {
                deactivatePlanReqModels.push({
                    custServiceMappingId: e.customerServiceMappingId,
                    remarks: this.serviceStropRemarks,
                });
            });
            data = {
                custId: this.custData.id,
                deactivatePlanReqModels: deactivatePlanReqModels,
            };
        }
        const url = "/subscriber/resumeServiceInBulk";
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    $("#StopServiceModal").modal("hide");
                    this.getActivePlanDetails();
                    this.serviceStopBulkFlag = false;
                    this.serviceStopId = [];
                    this.custPlanMappping.emit();
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                $("#StopServiceModal").modal("hide");
                this.getActivePlanDetails();
                this.serviceStopBulkFlag = false;
                this.serviceStopId = [];
            }
        );
    }

    saveEditNickName(serviceMappingID, nickName) {
        let data = {};
        const url = `/subscriber/nickName?custServiceMappingId=${serviceMappingID}&name=${nickName}`;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success(`Successfully`, 'Success!');

                    this.getActivePlanDetails();

                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openPaushSearviceMedel(data, type) {
        this.serviceTerminalDialogRef = this.dialog.open(this.ServiceTerminal, {
            width: '580px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
        });
        if (type === "Delete" && data.isChildExists) {
            this.confirmationService.confirm({
                message: "Child customer service also terminate, Do you want to continue ?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.ifselecResonType = type;
                    this.servicePerticularData = data;
                    this.selectDeactivateReason = "";
                    this.serviceStropRemarks = "";
                    $("#StopServiceModal").modal("show");
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                },
            });
        } else {
            this.ifselecResonType = type;
            this.servicePerticularData = data;
            this.selectDeactivateReason = "";
            this.serviceStropRemarks = "";
            $("#StopServiceModal").modal("show");
        }
    }

    // openStartSearviceMedel(data, type) {
    //   this.ifselecResonType = type;
    //   this.servicePerticularData = data;
    //   this.selectDeactivateReason = "";
    //   this.serviceStropRemarks ="";
    //   if (type === "Pause") {
    //     $("#StopServiceModal").modal("show");
    //   } else {
    //     this.playService();
    //   }
    // }

    getDectivateData() {
        let url = `/commonList/generic/DEACTIVATE_REASON_EZ_BILL`;
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.deactiveDataList = response.dataList;
        });
    }

    modalOpenParentCustomer() {
        this.customerSelectDialogRef = this.dialog.open(this.customerSelect, {
            width: '900px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
        });
        this.displaySelectParentCustomer = true;
        this.newFirst = 0;
        this.getParentCustomerData();
        this.selectedParentCust = [];
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
            pageSize: this.parentCustomerListdataitemsPerPage,
        };
        const url = "/parentCustomers/list/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                const list = this.prepaidParentCustomerList;
                const filterList = list.filter(cust => cust.id !== this.custData.id);

                this.prepaidParentCustomerList = filterList;

                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                this.parentCustomerListdataitemsPerPage = this.parentCustomerListdataitemsPerPage;  // or a default value

            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


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
        this.currentPageParentCustomerListdata = 1;
        const searchParentData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage,
        };

        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();

        const url = "/parentCustomers/search/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
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
                    const filterList = list.filter(cust => cust.id !== this.custData.id);
                    this.prepaidParentCustomerList = filterList;
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                    this.parentCustomerListdataitemsPerPage = this.parentCustomerListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');


                } else {

                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


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

    paginate(event: PageEvent) {
        this.currentPageParentCustomerListdata = event.pageIndex + 1; // MatPaginator pageIndex starts from 0
        this.parentCustomerListdataitemsPerPage = event.pageSize;

        // this.currentPageParentCustomerListdata = event.page + 1;
        // this.first = event.first;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }
    async saveSelCustomer() {
        this.billableCusList = [
            {
                id: Number(this.selectedParentCust.id),
                name: this.selectedParentCust.name,
            },
        ];

        this.serviceForm.patchValue({
            billableCustomerId: Number(this.selectedParentCust.id),
        });

        let customerId = {
            value: this.selectedParentCust.id,
        };
        // this.changeCustomer(customerId);
        this.modalCloseParentCustomer();
    }
    // async saveSelCustomer() {
    //     console.log("page event", this.selectedParentCust);

    //     this.billableCusList = [
    //         {
    //             id: this.selectedParentCust.id,
    //             name: this.selectedParentCust.name,
    //         },
    //     ];
    //     this.serviceForm.patchValue({
    //         billableCustomerId: this.selectedParentCust.id,
    //     });

    //     this.modalCloseParentCustomer();
    // }

    modalCloseParentCustomer() {
        this.customerSelectDialogRef.close();
        this.displaySelectParentCustomer = false;
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    removeSelParentCust() {
        this.selectedParentCust = [];
        this.billableCusList = [];
        this.serviceForm.patchValue({
            billableCustomerId: null,
        });
    }
    newActivationFlag: boolean = false;
    approvePlanOpen(planId, nextApproverId, serviceMappingId, status) {
        this.approveDialogRef = this.dialog.open(this.approverDialog, {
            width: '50%',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        if (status === "NewActivation") this.newActivationFlag = true;
        else this.newActivationFlag = false;
        this.approved = false;
        this.selectStaff = null;
        this.approvePlanData = [];
        $("#assignApporvePlanModal").modal("show");
        this.assignPlanID = planId;
        this.nextApproverId = nextApproverId;
        this.customerServiceMappingId = serviceMappingId;
        // this.rejectPlanForm.reset();
        this.rejectPlanSubmitted = false;
    }
    eventName: any = "";
    pickModalOpen(data) {
        if (data.custServMappingStatus == "NewActivation") {
            this.eventName = "CUSTOMER_SERVICE_ADD";
        } else {
            this.eventName = "CUSTOMER_SERVICE_TERMINATION";
        }
        let url =
            "/workflow/pickupworkflow?eventName=" +
            this.eventName +
            "&entityId=" +
            data?.customerServiceMappingId;
        this.planManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.getPostoaidPlan("");
                this.getActivePlanDetails();

                if (response.responseCode == 417) {

                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {

                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    @ViewChild('rejectDialog') rejectDialog!: TemplateRef<any>;
    rejectDialogRef!: MatDialogRef<any>;
    rejectPlanOpen(planId, nextApproverId, mappingId, status) {
        this.rejectDialogRef = this.dialog.open(this.rejectDialog, {
            width: '50%',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        if (status === "NewActivation") this.newActivationFlag = true;
        else this.newActivationFlag = false;
        this.reject = false;
        this.selectStaff = null;
        this.rejectPlanData = [];
        $("#rejectPlanModal").modal("show");
        this.assignPlanID = planId;
        this.nextApproverId = nextApproverId;
        this.customerServiceMappingId = mappingId;
        // this.rejectPlanForm.reset();
        this.rejectPlanSubmitted = false;
    }

    StaffReasignList(data) {
        this.customerServiceMappingId = data.customerServiceMappingId;
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.customerServiceMappingId}&eventName=CUSTOMER_SERVICE_TERMINATION`;
        this.planManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedPlanid = data.id;
                this.approvableStaff = [];
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');


                }
                if (response.dataList != null) {
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

    reassignWorkflow() {
        let url: any;
        // this.remarks = this.assignPlanForm.controls.remark;
        if (this.customerServiceMappingId != null) {
            url = `/teamHierarchy/reassignWorkflow?entityId=${this.customerServiceMappingId}&eventName=CUSTOMER_SERVICE_TERMINATION&assignToStaffId=${this.selectStaff}&remark=${this.assignPlanForm.value.remark}`;

            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    $("#reAssignPLANModal").modal("hide");
                    this.getActivePlanDetails();
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
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please Aprove Before Reassigne!');
            }


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
            // customerServiceMappingId=11&isApproveRequest=true&remarks=11
            let assignCAFData = {
                planId: this.assignPlanID,
                nextStaffId: "",
                flag: "approved",
                remark: this.assignPlanForm.controls.remark.value,
                staffId: localStorage.getItem("userId"),
            };

            this.planManagementService.getMethod(url).subscribe(
                (response: any) => {
                    // $("#assignApporvePlanModal").modal("hide");
                    if (!this.searchkey && !this.searchKeyType) {
                        // this.getPostoaidPlan("");
                        this.getActivePlanDetails();
                    } else {
                        // this.searchPlan();
                    }
                    if (response.responseCode === 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    } else {
                        this.toastr.success(`${response.message}`, 'Success!');
                        this.assignPlanForm.reset();
                        this.assignPlansubmitted = false;
                        if (response.dataList != null) {
                            this.approvePlanData = response.dataList;
                            this.approvePlan = this.approvePlanData
                            this.approved = true;
                        } else {
                            $("#assignApporvePlanModal").modal("hide");
                        }
                        this.approveDialogRef.close();
                    }
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    rejectPlan() {
        this.rejectPlanSubmitted = true;
        if (this.rejectPlanForm.valid) {
            let assignCAFData = {
                planId: this.assignPlanID,
                nextStaffId: "",
                flag: "Rejected",
                remark: this.rejectPlanForm.controls.remark.value,
                staffId: localStorage.getItem("userId"),
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
                    this.toastr.success(`${response.message}`, 'Success!');
                    this.rejectPlanForm.reset();
                    this.rejectPlanSubmitted = false;
                    if (response.dataList != null) {
                        this.rejectPlanData = response.dataList;
                        this.reject = true;
                    } else {
                        $("#rejectPlanModal").modal("hide");
                    }
                    this.rejectDialogRef.close();
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

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
                $("#assignApporvePlanModal").modal("hide");
                $("#rejectPlanModal").modal("hide");
                // this.getPostoaidPlan("");
                this.approveDialogRef.close();  // Close approve dialog if open
                this.rejectDialogRef.close();   // Close reject dialog if open
                this.getActivePlanDetails();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    openAddWorkFlow(id, auditcustid) {
        this.ifModelIsShow = true;
        // this.dialog.open(WorkflowAuditDetailsModalComponent, {
        //     width: '80%', maxWidth: '90vw',
        //     height: 'auto',
        //     autoFocus: false, disableClose: true,
        //     data: {
        //         auditcustid: auditcustid,
        //         checkHierachy: "CUSTOMER_SERVICE_ADD",
        //         planId: ""
        //     }
        // });
        // console.log("id, auditcustid ", id, auditcustid);
        // this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "CUSTOMER_SERVICE_ADD",
            planId: "",
        });
    }

    openEditWorkFlow(id, auditcustid) {
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "CUSTOMER_SERVICE_TERMINATION",
            planId: "",
        });
    }
    auditData: any = [];
    auditItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPageAuditList = 1;
    audittotalRecords: number = 0;

    getAuditData(auditcustid: any, page?: number, pageSize?: number) {
        const currentPage = page ?? this.currentPageAuditList;
        const itemsPerPage = pageSize ?? this.auditItemPerPage;

        const data = {
            page: currentPage,
            pageSize: itemsPerPage,
        };

        const url = "/subscriber/servicestatusAudit/" + auditcustid;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.auditData = response.data.content;
                this.audittotalRecords = response.data.totalElements;
                // Update current pagination state after successful fetch
                this.currentPageAuditList = currentPage;
                this.auditItemPerPage = itemsPerPage;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed to fetch audit data!');
            }
        );
    }

    openAudit(auditcustid) {
        this.auditDialogRef = this.dialog.open(this.AuditDetails, {
            width: '1200px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
        });
        $("#auditDetails").modal("show");
        this.currentAuditCustId = auditcustid;
        this.currentPageAuditList = 1;
        this.getAuditData(auditcustid);
    }
    currentAuditCustId: any;

    pageChangedauditList(event: PageEvent) {
        const newPage = event.pageIndex + 1;
        const newPageSize = event.pageSize;
        if (this.currentAuditCustId) {
            this.getAuditData(this.currentAuditCustId, newPage, newPageSize);
        }
    }

    auditCloseModal() {
        $("#auditDetails").modal("hide");
        this.auditData = [];
    }
    serviceStopBulkFlag: boolean = false;

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
                    //     detail: "Please select service with same status!",
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
    serviceStop() {
        this.confirmationService.confirm({
            message: "If Child customer is there child service also Stop, Do you want to continue ?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                let deactivatePlanReqModels = [];
                let data: any;
                if (this.serviceStopBulkFlag) {
                    this.serviceStopId.map(e => {
                        deactivatePlanReqModels.push({
                            custServiceMappingId: e.custPlanMapppingId,
                            remarks: this.serviceStropRemarks,
                            reasonId: this.selectDeactivateReason,
                        });
                    });
                    data = {
                        custId: this.serviceStopId[0].custId,
                        deactivatePlanReqModels: deactivatePlanReqModels,
                    };
                } else {
                    deactivatePlanReqModels.push({
                        custServiceMappingId: this.servicePerticularData.custPlanMapppingId,
                        remarks: this.serviceStropRemarks,
                        reasonId: this.selectDeactivateReason,
                    });
                    data = {
                        custId: this.servicePerticularData.custId,
                        deactivatePlanReqModels: deactivatePlanReqModels,
                    };
                }

                const url = "/subscriber/stopServiceInBulk";
                this.customerManagementService.postMethod(url, data).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                        } else {
                            $("#StopServiceModal").modal("hide");
                            this.getActivePlanDetails();
                            this.serviceStopBulkFlag = false;
                            this.serviceStopId = [];
                            this.custPlanMappping.emit();
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            },
            reject: () => {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                }

                this.serviceStopBulkFlag = false;
                this.serviceStopId = [];
                this.selectDeactivateReason = "";
                this.serviceStropRemarks = "";
                $("#StopServiceModal").modal("show");
            },
        });
    }
    chekcPlanGroup(plan, planList) {
        if (this.custData.plangroupid !== null) {
            let groupPlanList = planList.filter(item => item.plangroupid == plan.plangroupid);
            return groupPlanList[0] === plan;
        }
        return true;
    }

    getSerialNumber(plan) {
        return plan.customerInventorySerialnumberDtos.filter(item => item.primary).length > 0
            ? plan.customerInventorySerialnumberDtos.filter(item => item.primary)[0].serialNumber
            : "";
    }

    openPlanConnectionModal(plan) {
        // this.planForConnection = plan;
        // this.showPlanConnectionNo = true;

        this.planForConnection = null;   // Clear previous plan
        this.showPlanConnectionNo = false; // Reset visibility first

        setTimeout(() => {
            this.planForConnection = plan;  // Assign new plan after reset
            this.showPlanConnectionNo = true;  // Show dialog
        }, 0);  // Minimal delay to ensure change detection triggers
    }
    closeDialog() {
        this.planForConnection = null;
        this.showPlanConnectionNo = false;
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
        let status = planStatus.toLowerCase();
        let statusWorkflow = workflowStatus.toLowerCase();

        if (statusWorkflow == "newactivation") {
            if (statusWorkflow == "newactivation") this.badgeTypeForStatus = "green";
            else if (statusWorkflow == "rejected") this.badgeTypeForStatus == "red";
            this.displayStatus = workflowStatus.toUpperCase();
        } else {
            this.displayStatus = planStatus.toUpperCase();
            switch (status) {
                case "active":
                case "ingrace":
                case "newactivation":
                case "ActivationPending":
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

    reActivate(plan) {
        const url = `/reactivateService?custId=${plan.custId}&custServiceId=${plan.customerServiceMappingId}`;
        let data = {};
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data) {
                        this.toastr.success(`${response.message}`, 'Re-activate Sucessfully!');


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

    searchStaffByName() {
        if (this.searchStaffDeatil) {
            this.approvePlanData = this.approvePlan.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(this.searchStaffDeatil.toLowerCase()) ||
                    staff.username.toLowerCase().includes(this.searchStaffDeatil.toLowerCase())
            );
        } else {
            this.approvePlanData = this.approvePlan;
        }
    }

    clearSearchForm() {
        this.searchStaffDeatil = "";
        this.approvePlanData = this.approvePlan;
    }
}
