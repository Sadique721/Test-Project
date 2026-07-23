import { filter } from "rxjs/operators";
import { Component, OnInit, SimpleChange, TemplateRef, ViewChild } from "@angular/core";
import { PlanGroupService } from "src/app/service/plan-group.service";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { PlanGroup } from "src/app/components/model/planGroup";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { BehaviorSubject, Observable, Observer, Subject } from "rxjs";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { DatePipe } from "@angular/common";
import { ProuctManagementService } from "src/app/service/prouct-management.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PARTNERS, PRODUCTS } from "src/app/constants/aclConstants";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { size } from "lodash";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { ToastrService } from 'ngx-toastr';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

declare var $: any;

@Component({
    selector: "app-plan-group",
    templateUrl: "./plan-group.component.html",
    styleUrls: ["./plan-group.component.css"],
    standalone: false
})
export class PlanGroupComponent implements OnInit {

    planDisplayedColumns: string[] = [
        'PlanBundle',
        'PlanType',
        'Status',
        'CreateDate',
        'CreatedBy',
        'ISP',
        'Amount',
        'Action'
    ];

    changePlanChargeClicked: Subject<any> = new Subject<any>();

    planGroupForm: UntypedFormGroup;
    ifModelIsShow: boolean = false;
    searchForm: UntypedFormGroup;
    submitted = false;
    searchSubmitted = false;
    editplanGroupId: number;
    planGroupData: any = [];
    planGroupDataList: any[] = [];

    status = [{ label: "Active" }, { label: "Inactive" }, { label: "NewActivation" }];
    totalRecords: any;
    searchData: any;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;

    currentPagePlanMapping = 0;
    PlanMappingitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    createplanGroupData: PlanGroup;
    editplanGroupData: PlanGroup;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    update: boolean = true;
    editMode: boolean = false;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    PlanMappingSubmitted: boolean = false;
    planProductMappingFromArray: UntypedFormArray;
    PlanMappingfromgroup: UntypedFormGroup;
    PlanMapping: UntypedFormArray;
    discountMappingtotalRecords: String;
    filterPlanData: any;
    plantypaSelectData: any[];
    creatPlangroup = false;
    ifPlangroupList = true;
    ifplanGroupDataShow = false;
    planGroupServiceAreaName: any = "";
    planTypeData: any;
    dataPlanAmount: any = [];
    totalOfferPrice: number = 0;
    planGroupMapingList: any = [];
    bindedProductPlanGroupMappingList: any = [];
    productDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productDeatiltotalRecords: String;
    productPageChargeDeatilList = 1;
    productPlanGroupMappingList: any = [];
    type = [{ label: "NORMAL" }, { label: "SPECIAL" }];
    planGroupDropDownData: any;
    searchOptionType = "";
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;

    assignPlanForm: UntypedFormGroup;
    rejectPlanForm: UntypedFormGroup;
    assignPlansubmitted: boolean = false;
    rejectPlanSubmitted: boolean = false;
    assignPlanGroupID: any;
    nextApproverId: any;
    selectStaff: any;
    remarks: any;
    selectStaffReject: any;
    approvePlanData = [];
    approved = false;
    rejectPlanData = [];
    reject = false;
    staffId: any;
    productplanmappingList: UntypedFormArray = this.fb.array([]);
    revisedChargeFlag: boolean = false;
    revisedChargeArray = [];
    productPlanDetailFlag: boolean = false;
    planDetails: any = [];
    workflowAuditData1: any = [];
    currentPageMasterSlab1 = 1;
    MasteritemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords1: String;
    workflowID: any;
    serviceAreaListPlanbundle: any;
    productName: any;
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: "",
    });

    planDiscount = [
        { label: "Yes", value: true },
        { label: "No", value: false },
    ];
    planProductfromgroup: UntypedFormGroup;
    productCatagorys: any = [];
    products: any = [];
    serviceData = [];
    dynamicDropdown: any = [];
    planValidity = "";
    planunitValidity = "";
    openPlanChargeDetails = false;
    selectedPlan = null;
    selectedPlanIndex = "";
    planFilterStatus = [
        { label: "New Activation", value: "planstatusnewactivation" },
        { label: "Active", value: "planstatusactive" },
        { label: "Inactive", value: "planstatusinactive" },
        { label: "Rejected", value: "planstatusrejected" },
        { label: "Expired", value: "planstatusexpired" },
    ];
    searchOption: any = "";
    searchkey;
    searchkey2;
    searchDeatil = "";
    searchDeatilFromDate = new Date();
    searchDeatilToDate = new Date();
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    searchStaffDeatil: any;
    approvePlan: any[];
    @ViewChild('planGroupDialogTemplate') planGroupDialogTemplate: TemplateRef<any>;
    @ViewChild('planMappingPaginator') planMappingPaginator!: MatPaginator;
    @ViewChild('viewPlanGroupDialogTemplate') viewPlanGroupDialogTemplate!: TemplateRef<any>;
    @ViewChild('serviceAreaDialogTemplate') serviceAreaDialogTemplate!: TemplateRef<any>;
    @ViewChild('approvePlanDialogTemplate') approvePlanDialogTemplate!: TemplateRef<any>;
    @ViewChild('rejectPlanDialogTemplate') rejectPlanDialogTemplate!: TemplateRef<any>;
    dialogRef: MatDialogRef<any>;
    planMappingDataSource = new MatTableDataSource<any>([]);
    dataSource = new MatTableDataSource<any>();
    PlanMappingtotalRecords: number = 0;
    planMappingData: any;

    constructor(
        private toastr: ToastrService,
        private planGroupService: PlanGroupService,
        private savbillcommonbssservice: SavbillCommonBaseService,
        private customermanagementservice: CustomermanagementService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        public PaymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService,
        private datePipe: DatePipe,
        loginService: LoginService,
        private productManagementService: ProuctManagementService,
        public statusCheckService: StatusCheckService,
        private dialog: MatDialog,
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.PLAN_GROUP_CREATE);
        // this.deleteAccess = loginService.hasPermission(PRODUCTS.PLAN_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.PLAN_GROUP_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.editMode = !this.createAccess && this.editAccess ? true : false;
    }
    staffID: any;
    ngOnInit(): void {
        let staffID = localStorage.getItem("userId");
        this.staffID = Number(staffID);
        this.planGroupForm = this.fb.group({
            // createdById: [''],
            // createdByName: [''],
            // createdate: [''],
            // lastModifiedById: [''],

            planMode: ["NORMAL"],
            mvnoId: [""],
            planGroupId: [""],
            planGroupName: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            serviceAreaId: ["", Validators.required],
            status: ["NewActivation", Validators.required],
            planType: ["", Validators.required],
            planGroupType: ["", Validators.required],
            category: ["", Validators.required],
            allowdiscount: ['', Validators.required],
            requiredApproval: [""],
            invoiceToOrg: [""],
            offerprice: [0],
        });
        this.PlanMapping = this.fb.array([]);
        this.searchForm = this.fb.group({
            name: [null],
        });
        this.planMode = "NORMAL";
        this.PlanMappingfromgroup = this.fb.group({
            service: ["", Validators.required],
            planId: ["", Validators.required],
            validity: ["", Validators.required],
            newOfferPrice: [""],
            planGroupId: [""],
            planGroupMappingId: [""],
            mvnoId: [""],
            amount: [""],
            validityUnit: [""],
            chargeList: [[]],
        });
        this.planProductfromgroup = this.fb.group({
            revisedCharge: ["", [Validators.required, Validators.minLength(0)]],
            ownershipType: ["", Validators.required],
            productCategoryId: ["", Validators.required],
            productCategoryName: [""],
            productName: [""],
            productId: [""],
            product_type: ["", Validators.required],
            name: [""],
            planGroupId: [""],
            id: [""],
            planName: [""],
            planId: [""],
        });
        this.assignPlanForm = this.fb.group({
            remark: ["", Validators.required],
        });
        this.rejectPlanForm = this.fb.group({
            remark: ["", Validators.required],
        });
        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.staffId = localStorage.getItem("userId");
        this.mvnoId = localStorage.getItem("mvnoId");

        this.findAllplanGroups("");
        this.getPlanType();
        this.getPlanCaegory();
        this.getAllProduct();
        this.getAllProductCategory();
        this.commondropdownService.getplanservice();
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getAllActiveStaff();
        const serviceArea = localStorage.getItem("serviceArea");
        const serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        this.commondropdownService.getPOSTpaidNormalPlan();
        this.commondropdownService.getPOSTpaidSpecialPlan();
        this.getPlanGroup();
        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
            page: "",
            pageSize: "",
        };
        this.planMappingData = new MatTableDataSource([]);
        this.planMappingData.paginator = this.planMappingPaginator;
    }

    ViewPlanCharges(currentPlanId, index) {
        if (currentPlanId !== "") {
            this.selectedPlan = this.PlanMapping.value.find(plan => plan.planId === currentPlanId);
        } else if (this.PlanMappingfromgroup.valid) {
            this.PlanMappingSubmitted = true;
            this.selectedPlan = this.PlanMappingfromgroup.value; //this.planDetails;
        }
        this.selectedPlanIndex = index;
        this.openPlanChargeDetails = true;
    }

    changePlanChargeData(event) {
        this.openPlanChargeDetails = false;
        this.selectedPlan = null;
        if (event) {
            if (this.selectedPlanIndex !== "") {
                this.PlanMapping.controls[this.selectedPlanIndex].patchValue({
                    newOfferPrice: event.newOfferPrice.toFixed(2),
                    chargeList: event.chargeList,
                });

                let calculatedofferprice = 0;
                this.PlanMapping.value.forEach(element => {
                    //edit..............
                    calculatedofferprice +=
                        Number(element.newOfferPrice) == 0
                            ? Number(element.amount)
                            : Number(element.newOfferPrice);
                });
                this.planGroupForm.patchValue({
                    offerprice: calculatedofferprice,
                });
            } else {
                this.PlanMappingfromgroup.patchValue({
                    newOfferPrice: event.newOfferPrice.toFixed(2),
                    chargeList: event.chargeList,
                });
            }
        }
    }

    createPlanGroup() {
        this.creatPlangroup = true;
        this.ifPlangroupList = false;
        this.ifplanGroupDataShow = false;

        this.editMode = false;
        this.searchSubmitted = false;
        this.productPlanDetailFlag = false;
        this.searchForm.reset();
        this.planGroupForm.reset();
        this.PlanMappingfromgroup.reset();
        this.productPlanGroupMappingList = [];
        this.currentPage = 1;
        this.PlanMapping = this.fb.array([]);
        // this.productplanmappingList = this.fb.array([]);
        this.planGroupForm.patchValue({
            status: "NewActivation",
            planMode: "NORMAL",
            allowdiscount: true,
            requiredApproval: true,
            invoiceToOrg: true,
        });
    }

    selSearchOption(event) {
        this.searchDeatil = "";
        this.searchOptionType = "";
        this.searchData.filters[0].filterDataType = "";
        this.currentPage = 1;
    }

    openPlanGroupDialog(edit: boolean = false, planData: any = null) {
        this.editMode = edit;
        this.PlanMapping.clear();
        this.planMappingData = [];
        if (edit && planData) {
            this.planGroupForm.patchValue(planData);
        } else {
            this.PlanMappingfromgroup.reset();
            this.planGroupForm.reset();
            this.planGroupForm.controls.planMode.setValue('NORMAL');
            this.planGroupForm.controls.status.setValue('NewActivation')
        }
        this.dialogRef = this.dialog.open(this.planGroupDialogTemplate, {
            width: '1200px'
        });
    }

    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    async searchByName(currentlist) {
        if (
            this.searchOption !== "plantype" &&
            this.searchOption !== "planname" &&
            this.searchOption !== "placurrentPagencreateddate"
        ) {
            //   if (
            //     !this.searchkey ||
            //     this.searchkey !== this.searchDeatil.trim() ||
            //     !this.searchkey2 ||
            //     this.searchkey2 !== this.searchOption.trim()
            //   ) {
            //     this.currentPage = 1;
            //   }
            this.searchkey = this.searchDeatil.trim();
            this.searchkey2 = this.searchOption.trim();

            this.searchData.filters[0].filterValue = this.searchDeatil.trim();
            this.searchData.filters[0].filterColumn = this.searchOption.trim();
        } else if (this.searchOption === "planname" || this.searchOption === "plantype") {
            //   if (
            //     !this.searchkey ||
            //     this.searchkey !== this.searchDeatil.trim() ||
            //     !this.searchkey2 ||
            //     this.searchkey2 !== this.searchOptionType.trim()
            //   ) {
            //     this.currentPage = 1;
            //   }
            this.searchkey = this.searchDeatil.trim();
            this.searchkey2 = this.searchOptionType.trim();

            this.searchData.filters[0].filterDataType = this.searchOptionType.trim();
            this.searchData.filters[0].filterValue =
                this.searchOption === "planname" ? this.searchDeatil.trim() : "";
            this.searchData.filters[0].filterColumn = "any";
        } else if (this.searchOption === "plancreateddate") {
            //   if (
            //     !this.searchkey ||
            //     this.searchkey !== this.searchDeatil ||
            //     !this.searchkey2 ||
            //     this.searchkey2 !== this.searchOption
            //   ) {
            //     this.currentPage = 1;
            //   }
            let searchDeatilToDate = this.datePipe.transform(this.searchDeatilToDate, "yyyy-MM-dd");
            let searchDeatilFromDate = this.datePipe.transform(this.searchDeatilFromDate, "yyyy-MM-dd");
            let searchDeatil = {
                from: searchDeatilFromDate,
                to: searchDeatilToDate,
            };
            this.searchkey2 = JSON.stringify(searchDeatil);
            this.searchkey2 = this.searchOption;

            this.searchData.filters[0].filterValue = JSON.stringify(searchDeatil);
            this.searchData.filters[0].filterColumn = this.searchOption;
        } else {
            //   if (
            //     !this.searchkey ||
            //     this.searchkey !== this.searchDeatil ||
            //     !this.searchkey2 ||
            //     this.searchkey2 !== this.searchOption
            //   ) {
            //     this.currentPage = 1;
            //   }
            let searchDeatil = this.datePipe.transform(this.searchDeatil, "yyyy-MM-dd");
            this.searchkey = searchDeatil;
            this.searchkey2 = this.searchOption;

            this.searchData.filters[0].filterValue = searchDeatil;
            this.searchData.filters[0].filterColumn = this.searchOption;
            if (!currentlist) {
                this.currentPage = 1;
            }
        }

        if (currentlist) {
            this.searchData.pageSize = currentlist;
            this.itemsPerPage = currentlist;
        } else {
            this.searchData.pageSize = this.itemsPerPage;
        }

        this.searchData.page = this.currentPage;
        // this.searchData.pageSize = currentlist;

        this.planGroupService.searchPlanGroup(this.searchData).subscribe(
            (response: any) => {
                this.planGroupDataList = response.planGroupList.content;
                this.totalRecords = response.pageDetails.totalRecords;
            },
            error => {
                this.totalRecords = 0;
                this.planGroupDataList = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Info",
                    //   detail: error.error.msg,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.errorMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        );
    }
    clearSearchForm() {
        this.editMode = false;
        this.searchSubmitted = false;
        this.creatPlangroup = false;
        this.ifPlangroupList = true;
        this.ifplanGroupDataShow = false;
        this.searchForm.reset();
        this.currentPage = 1;
        this.planGroupForm.reset();
        this.productPlanDetailFlag = false;
        this.planGroupForm.controls.status.setValue("NewActivation");
        this.PlanMappingfromgroup.reset();
        this.PlanMapping = this.fb.array([]);
        this.findAllplanGroups("");
        this.productPlanGroupMappingList = [];
        this.searchOption = "";
        this.searchDeatil = "";
        this.searchkey = "";
        this.searchkey2 = "";
        this.productplanmappingList = this.fb.array([]);
    }

    planCategoryData = [];
    getPlanCaegory() {
        const url = "/commonList/planCategory";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.planCategoryData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    getPlanValidity(event) {
        const planId = event.value;
        const url = "/postpaidplan/" + planId;
        this.planGroupService.getMethod(url).subscribe(
            (response: any) => {
                const planDetailData = response.postPaidPlan;
                // this.planDetails = response.postPaidPlan;
                if (planId) {
                    this.getProductPlanMappingDetails(planId);
                }
                let chargeList = [];
                planDetailData.chargeList.forEach(charge => {
                    chargeList.push({
                        id: charge.charge.id,
                        chargeName: charge.charge.name,
                        chargeprice: charge.chargeprice,
                        charge: charge.charge,
                    });
                });
                this.PlanMappingfromgroup.patchValue({
                    validity: Number(planDetailData.validity),
                    amount: planDetailData.offerprice,
                    newOfferPrice:
                        planDetailData.newOfferPrice > 0
                            ? planDetailData.newOfferPrice
                            : planDetailData.offerprice,
                    validityUnit: planDetailData.unitsOfValidity,
                    chargeList: chargeList,
                });
                // this.PlanMappingfromgroup.controls.validity.disable();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }
    createPlanMappingFormGroup(): UntypedFormGroup {
        if (this.editMode) {
            return this.fb.group({
                service: [this.PlanMappingfromgroup.value.service],
                planId: [this.PlanMappingfromgroup.value.planId],
                validity: [this.PlanMappingfromgroup.value.validity],
                planGroupId: [this.PlanMappingfromgroup.value.planGroupId],
                planGroupMappingId: [this.PlanMappingfromgroup.value.planGroupMappingId],
                mvnoId: [this.PlanMappingfromgroup.value.mvnoId],
                amount: [this.PlanMappingfromgroup.value.amount],
                validityUnit: [this.PlanMappingfromgroup.value.validityUnit],
                newOfferPrice: [this.PlanMappingfromgroup.value.newOfferPrice],
                chargeList: [this.PlanMappingfromgroup.value.chargeList],
                isNew: [true],
            });
        } else {
            return this.fb.group({
                service: [this.PlanMappingfromgroup.value.service],
                planId: [this.PlanMappingfromgroup.value.planId],
                validity: [this.PlanMappingfromgroup.value.validity],
                amount: [this.PlanMappingfromgroup.value.amount],
                validityUnit: [this.PlanMappingfromgroup.value.validityUnit],
                planGroupId: [""],
                planGroupMappingId: [""],
                newOfferPrice: [this.PlanMappingfromgroup.value.newOfferPrice],
                chargeList: [this.PlanMappingfromgroup.value.chargeList],
            });
        }
    }

    onAddPlanMappingField() {
        this.PlanMappingSubmitted = true;
        if (this.PlanMappingfromgroup.valid) {
            if (this.PlanMapping.controls.length == 0) {
                this.planValidity = this.PlanMappingfromgroup.value.validity;
                this.planunitValidity = this.PlanMappingfromgroup.value.validityUnit;
            }
            this.getProductPlanDetails();
            // this.PlanMapping.push(this.createPlanMappingFormGroup());
            // this.PlanMappingfromgroup.reset();
            const newPlanMapping = this.createPlanMappingFormGroup();


            this.PlanMapping.push(newPlanMapping);
            this.planMappingData = [...this.PlanMapping.controls];


            Object.keys(newPlanMapping.controls).forEach(key => {
                const control = newPlanMapping.get(key);
                if (control) {
                    control.disable();
                    control.markAsPristine();
                    control.markAsUntouched();
                    control.updateValueAndValidity({ onlySelf: true, emitEvent: false });
                }
            });
            this.PlanMappingfromgroup.reset();
            this.PlanMappingfromgroup.markAsPristine();
            this.PlanMappingfromgroup.markAsUntouched();
            this.PlanMappingSubmitted = false;
            this.planGroupForm.patchValue({
                offerprice: 0,
            });

            this.PlanMapping.value.forEach(element => {
                var offerPriceSum =
                    // this.planGroupForm.value.category == "Business Promotion"
                    // ?
                    Number(this.planGroupForm.value.offerprice) +
                    (Number(element.newOfferPrice) == 0
                        ? Number(element.amount)
                        : Number(element.newOfferPrice));
                // : Number(this.planGroupForm.value.offerprice) + Number(element.amount);

                this.planGroupForm.patchValue({
                    offerprice: offerPriceSum,
                });
            });
            this.updatePlanMappingTable();
        }
    }

    pageChangedPlanMapping(event: PageEvent) {
        this.currentPagePlanMapping = event.pageIndex;
        this.PlanMappingitemsPerPage = event.pageSize;

        this.updatePlanMappingTable();
    }

    updatePlanMappingTable() {

        this.planMappingData = new MatTableDataSource(this.PlanMapping.controls as UntypedFormGroup[]);
        this.planMappingData.paginator = this.planMappingPaginator;
        this.PlanMappingtotalRecords = this.PlanMapping.length;
    }

    canExit() {
        if (!this.planGroupForm.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue?`,
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });

                dialogRef.afterClosed().subscribe((result) => {
                    if (result === true) {
                        observer.next(true);
                        observer.complete();
                    }
                    else {
                        observer.next(false);
                        observer.complete();
                    }
                });
                return false;
            });
        }
    }
    

    deleteConfirmonPlanMappingField(
        PlanMappingFieldIndex: number,
        PlanMappingFieldId: number,
        PlanId: number,
        PlanGroupId: number
    ) {
        if (PlanMappingFieldIndex || PlanMappingFieldIndex === 0) {
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Plan Mapping ?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: async () => {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Delete Plan Mapping',
                    description: 'Do you want to delete this Plan Mapping?',
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(async (result) => {
                if (result) {
                    const deletePromise = this.onRemovePlanMapping(PlanMappingFieldIndex, PlanMappingFieldId);
                    if (!this.editMode) {
                        const removeAutoPlanPromise = this.onRemoveAutoPlanProductMap(PlanId);
                        await Promise.all([deletePromise, removeAutoPlanPromise]);
                    } else {
                        const removeProductPlanPromise = this.onRemoveProductPlanGroupMapping(
                            PlanGroupId,
                            PlanId
                        );
                        await Promise.all([deletePromise, removeProductPlanPromise]);
                    }
                    // this.planGroupForm.patchValue({
                    //     offerprice: 0,
                    // });
                    this.planGroupForm.patchValue({ offerprice: 0 });
                    this.PlanMapping.value.forEach(element => {
                        // var offerPriceSum =
                        //     Number(this.planGroupForm.value.offerprice) + Number(element.newOfferPrice);
                        // this.planGroupForm.patchValue({
                        //     offerprice: offerPriceSum,
                        // });
                        const offerPriceSum = Number(this.planGroupForm.value.offerprice) + Number(element.newOfferPrice);
                        this.planGroupForm.patchValue({ offerprice: offerPriceSum });
                    });
                    this.toastr.success("Plan Mapping has been deleted", 'Success!');


                } else {
                    this.toastr.info('You have cancelled deletion', 'Info!');
                    // User clicked Cancel

                }
            });
        }
    }

    async onRemovePlanMapping(PlanMappingFieldIndex: number, PlanMappingFieldId: number) {
        if (PlanMappingFieldId) {
            let url = "/deletePlanGroupMappingById?planGroupMappingId=" + PlanMappingFieldId;
            this.planGroupService.deleteMethod(url).subscribe(
                (response: any) => {
                    this.PlanMapping.removeAt(PlanMappingFieldIndex);
                    this.planMappingData = [...this.PlanMapping.controls];
                    if (this.PlanMapping.length == 0) {
                        this.planValidity = "";
                        this.planunitValidity = "";
                    }
                    this.editplanGroupById(this.editplanGroupData.planGroupId);
                    this.toastr.success(`${response.message}`, 'Success!');
                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Successfully",
                    //   detail: response.message,
                    //   icon: "far fa-check-circle",
                    // });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.errorMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
            );
        } else {
            this.PlanMapping.removeAt(PlanMappingFieldIndex);

            this.planMappingData = [...this.PlanMapping.controls];

            if (this.PlanMapping.length == 0) {
                this.planValidity = "";
                this.planunitValidity = "";
            }
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllplanGroups(this.showItemPerPage);
        } else {
            this.searchByName(this.showItemPerPage);
        }
    }

    async findAllplanGroups(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.planGroupDataList = [];
        //let url='/planGroupMappings'
        let data = {
            page: page,
            pageSize: size,
        };
        this.planGroupService.getPlanGroupList(data).subscribe(
            (response: any) => {
                this.planGroupDataList = response.planGroupList.content;
                this.totalRecords = response.pageDetails.totalRecords;
            },
            error => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Info",
                    //   detail: error.error.errorMessage,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.errorMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        );
    }

    async editplanGroupById(planGroupId) {
        this.editMode = true;

        this.editplanGroupId = planGroupId;
        let obj = [];
        this.creatPlangroup = true;
        this.ifPlangroupList = false;
        let url = "/findPlanGroupById?planGroupId=" + planGroupId;
        this.planGroupService.getMethod(url).subscribe(
            (response: any) => {
                let serviceAreaList = response.serviceareaList.serviceareaid;
                this.planGroupMapingList = response.planGroup.planMappingList;
                this.getProductDetails(planGroupId);
                // this.productPlanDetailFlag = true;
                this.editplanGroupData = response.planGroup;
                let editplanGroupData = response.planGroup;
                this.planGroupForm.patchValue({
                    planGroupName: editplanGroupData.planGroupName,
                    status: editplanGroupData.status,
                    mvnoId: editplanGroupData.mvnoId,
                    serviceAreaId: serviceAreaList,
                    planGroupId: editplanGroupData.planGroupId,
                    planType: editplanGroupData.plantype,
                    planMode: editplanGroupData.planMode,
                    planGroupType: editplanGroupData.planGroupType,
                    category: editplanGroupData.category,
                    allowdiscount: editplanGroupData.allowDiscount,
                    requiredApproval: editplanGroupData.requiredApproval,
                    invoiceToOrg: editplanGroupData.invoiceToOrg,
                });

                let plantype = {
                    value: editplanGroupData.plantype,
                };
                this.selPlanType(plantype);
                let serviceAreaId = {
                    value: serviceAreaList,
                };
                this.getServiceByServiceAreaID(serviceAreaId);
                if (editplanGroupData.status == "NewActivation") {
                    this.status = [{ label: "NewActivation" }];
                } else {
                    this.status = [{ label: "Active" }, { label: "Inactive" }];
                }
                // this.editPostpaidPLan(
                //   editplanGroupData.plantype,
                //   editplanGroupData.planMode,
                // )
                //
                this.PlanMapping = this.fb.array([]);

                let MappURL = "/findPlanGroupMappingByPlanGroupId?planGroupId=" + planGroupId;
                this.planGroupService.getMethod(MappURL).subscribe(
                    (response: any) => {
                        let attributeList = response.planGroupMappingList;
                        if (attributeList.length !== 0) {
                            attributeList.forEach(element => {
                                if (element.plan.id) {
                                    // let planAmount = "";
                                    // let validityUnit = "";
                                    // const url = "/postpaidplan/" + element.plan.id;
                                    // this.planGroupService.getMethod(url).subscribe((response: any) => {
                                    // planAmount = response.postPaidPlan.offerprice;
                                    // validityUnit = response.postPaidPlan.unitsOfValidity;

                                    let chargeList = [];
                                    element.plan.chargeList.forEach(charge => {
                                        chargeList.push({
                                            id: charge.charge.id,
                                            chargeName: charge.charge.name,
                                            chargeprice: charge.chargeprice,
                                            charge: charge.charge,
                                        });
                                    });
                                    // this.PlanMapping.push(
                                    //     this.fb.group({
                                    //         service: Number(element.plan.serviceId),
                                    //         mvnoId: element.mvnoId,
                                    //         planId: element.plan.id,
                                    //         validity: element.plan.validity,
                                    //         planGroupId: planGroupId,
                                    //         amount: element.plan.offerprice,
                                    //         validityUnit: element.plan.unitsOfValidity,
                                    //         newOfferPrice: element.newofferprice,
                                    //         planGroupMappingId: element.planGroupMappingId,
                                    //         chargeList: [chargeList],
                                    //         isNew: editplanGroupData.status == "NewActivation",
                                    //     })
                                    // );
                                    // });
                                    const fg = this.fb.group({
                                        service: Number(element.plan.serviceId),
                                        mvnoId: element.mvnoId,
                                        planId: element.plan.id,
                                        validity: element.plan.validity,
                                        planGroupId: planGroupId,
                                        amount: element.plan.offerprice,
                                        validityUnit: element.plan.unitsOfValidity,
                                        newOfferPrice: element.newofferprice,
                                        planGroupMappingId: element.planGroupMappingId,
                                        chargeList: [chargeList],
                                        isNew: editplanGroupData.status == "NewActivation",
                                    });


                                    Object.keys(fg.controls).forEach(key => {
                                        fg.get(key)?.disable();
                                    });

                                    this.PlanMapping.push(fg);
                                }
                            });
                            // } else {
                            // }
                            // setTimeout(() => {
                            // this.planGroupForm.patchValue({
                            // offerprice: 0,

                            this.planMappingData = [...this.PlanMapping.controls];

                            let offerPriceSum = 0;
                            this.PlanMapping.value.forEach(el => {
                                offerPriceSum += el.newOfferPrice || el.amount || 0;
                            });

                            // this.PlanMapping.value.forEach(element => {
                            //     this.planGroupForm.patchValue({
                            //         offerprice: this.planGroupForm.value.offerprice + element.newOfferPrice,
                            //     });
                            // });
                            this.planGroupForm.patchValue({ offerprice: offerPriceSum });


                            this.dialogRef = this.dialog.open(this.planGroupDialogTemplate, {
                                width: '1200px'
                            });
                            // }, 300);
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: error.error.errorMessage,
                        //   icon: "far fa-times-circle",
                        // });
                    }
                );
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.errorMessage,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    async updateplanGroup() {
        if (this.planGroupForm.value.planMode == "" || this.planGroupForm.value.planMode == null) {
            this.planGroupForm.value.planMode = "NORMAL";
        }

        let editplanGroupData = this.planGroupForm.value;
        editplanGroupData.planMappingList = this.PlanMapping.value;
        editplanGroupData.productPlanGroupMappingList = this.productplanmappingList.value;

        // if ( editplanGroupData.planMappingList.length > 0) {
        let url = "/updatePlanGroup?id=" + this.editplanGroupId;
        this.planGroupService.updateMethod(url, editplanGroupData).subscribe(
            (response: any) => {
                // this.updatePlanMapping(editplanGroupData.planMappingList);
                this.editMode = false;
                this.submitted = false;
                this.productplanmappingList = this.fb.array([]);
                this.creatPlangroup = false;
                this.ifPlangroupList = true;
                this.ifplanGroupDataShow = false;
                if (!this.searchkey) {
                    this.findAllplanGroups("");
                } else {
                    this.searchByName("");
                }
                this.productPlanGroupMappingList = [];
                this.planGroupForm.reset();
                this.planGroupForm.controls.status.setValue("NewActivation");
                this.dialogRef.close();
                if (this.update) {
                    this.toastr.success(`${response.message}`, 'Success!');
                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Successfully",
                    //   detail: response.message,
                    //   icon: "far fa-check-circle",
                    // });
                }
            },
            (error: any) => {
                if (error.error.status == 417) {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.errorMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        );
        // } else {
        //
        //   this.messageService.add({
        //     severity: "error",
        //     summary: "Required ",
        //     detail: "Minimum one plan Mapping Details need to add",
        //     icon: "far fa-times-circle",
        //   });
        // }
    }
    async updatePlanMapping(data) {
        let url = "/updatePlanGroupMapping?planGroupId=" + this.editplanGroupId;
        this.planGroupService.updateMethod(url, data).subscribe(
            (response: any) => {
                this.editMode = false;
                this.submitted = false;

                this.creatPlangroup = false;
                this.ifPlangroupList = true;
                this.ifplanGroupDataShow = false;
                this.planGroupForm.reset();
                this.planGroupForm.controls.status.setValue("NewActivation");
                this.PlanMapping = this.fb.array([]);
                this.PlanMappingfromgroup.reset();
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.errorMessage,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    async addNewplanGroup() {
        this.submitted = true;

        if (this.planGroupForm.value.planMode == "" || this.planGroupForm.value.planMode == null) {
            this.planGroupForm.value.planMode = "NORMAL";
        }

        if (this.planGroupForm.invalid || this.PlanMapping.invalid) {
            this.planGroupForm.markAllAsTouched();
            this.PlanMapping.markAllAsTouched();
            return;
        }

        if (this.planGroupForm.valid) {
            if (this.editMode) {
                this.updateplanGroup();
                //this.productPlanDetailFlag = false;
            } else {
                //this.productPlanDetailFlag = true;
                this.planGroupForm.value.mvnoId = this.mvnoId;
                this.createplanGroupData = this.planGroupForm.value;
                this.createplanGroupData.planMappingList = this.PlanMapping.value;
                this.createplanGroupData.productPlanGroupMappingList = this.productplanmappingList.value;
                this.createplanGroupData.status = "NewActivation";
                if (this.createplanGroupData.planMappingList.length > 1) {
                    let Url = "/addPlanGroup";
                    this.planGroupService.postMethod(Url, this.createplanGroupData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.findAllplanGroups("");
                            this.planGroupForm.reset();
                            this.planGroupForm.controls.status.setValue("NewActivation");
                            this.PlanMapping = this.fb.array([]);
                            this.productplanmappingList = this.fb.array([]);
                            this.productPlanGroupMappingList = [];
                            // this.onAddAttribute()
                            this.PlanMappingfromgroup.reset();
                            this.planunitValidity = "";
                            this.planValidity = "";
                            this.creatPlangroup = false;
                            this.ifPlangroupList = true;
                            this.ifplanGroupDataShow = false;
                            if (this.dialogRef) {
                                this.dialogRef.close();
                            }
                            this.toastr.success(`${response.message}`, 'Success!');
                            // this.messageService.add({
                            //   severity: "success",
                            //   summary: "Successfully",
                            //   detail: response.message,
                            //   icon: "far fa-check-circle",
                            // });
                        },
                        (error: any) => {
                            if (error.error.status == 406) {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                                // this.messageService.add({
                                //   severity: "error",
                                //   summary: "Error",
                                //   detail: error.error.ERROR,
                                //   icon: "far fa-times-circle",
                                // });
                            } else {
                                this.toastr.error(error.error.errorMessage || error.error.ERROR, 'Failed!');
                                // this.messageService.add({
                                //   severity: "error",
                                //   summary: "Error",
                                //   detail: error.error.errorMessage || error.error.ERROR,
                                //   icon: "far fa-times-circle",
                                // });
                            }
                        }
                    );
                } else {
                    this.toastr.error(`More than one plan Mapping Details need to add`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Required ",
                    //   detail: "More than one plan Mapping Details need to add",
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        }
    }
    deleteConfirm(id, index) {
        this.confirmationService.confirm({
            message: "Do you want to delete this Plan Group?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteplanGroupById(id, index);
            },
            reject: () => {
                this.toastr.info(`You have rejected`, 'Info!');
                // this.messageService.add({
                //   severity: "info",
                //   summary: "Rejected",
                //   detail: "You have rejected",
                // });
            },
        });
    }
    async deleteplanGroupById(id, index) {
        let url = "/deletePlanGroupById?planGroupId=" + id;
        this.planGroupService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0 && this.planGroupData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllplanGroups("");
                } else {
                    this.searchByName("");
                }
                this.planGroupForm.reset();
                this.planGroupForm.controls.status.setValue("NewActivation");
                this.PlanMapping = this.fb.array([]);
                // this.onAddAttribute()
                this.PlanMappingfromgroup.reset();
                this.toastr.success(`${response.message}`, 'Success!');
                // this.messageService.add({
                //   severity: "success",
                //   summary: "Successfully",
                //   detail: response.message,
                //   icon: "far fa-check-circle",
                // });
            },
            (error: any) => {
                if (error.error.status == 500) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Info",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.errorMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        );
    }

    pageChanged(event: PageEvent) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.findAllplanGroups("");
        } else {
            this.searchByName("");
        }
    }
    planType = "";
    planMode = "";

    selPlanType(event) {
        let custType = event.value;
        let obj: any = [];
        this.filterPlanData = [];
        this.planType = custType;
        if (this.planMode == "NORMAL") {
            obj = this.commondropdownService.NomalpostpaidplanData.filter(
                key => key.plantype == this.planType
            );
        } else {
            obj = this.commondropdownService.specialpostpaidplanData.filter(
                key => key.plantype == this.planType
            );
        }
        this.filterPlanData = obj;
        if (this.serviceId != null) {
            this.loadPlans();
        }
        this.PlanMapping = this.fb.array([]);
        this.PlanMappingfromgroup.reset();
    }

    selPlanMode(event) {
        let planMode = event.value;
        let obj: any = [];
        this.filterPlanData = [];
        this.planMode = planMode;
        let newObj: any = [];

        if (this.planMode == "NORMAL") {
            obj = this.commondropdownService.NomalpostpaidplanData.filter(
                key => key.plantype == this.planType,
                (this.dynamicDropdown = this.planGroupDropDownData)
            );
        } else {
            obj = this.commondropdownService.specialpostpaidplanData.filter(
                key => key.plantype == this.planType,
                (this.dynamicDropdown = this.planGroupDropDownData.filter(
                    key => key.text === "Renew" || key.text === "Registration and Renewal"
                ))
            );
        }
        this.filterPlanData = obj;
        this.PlanMapping = this.fb.array([]);
        this.PlanMappingfromgroup.reset();

        this.PlanMapping = this.fb.array([]);
        this.PlanMappingfromgroup.reset();
    }

    getPlanType() {
        const url = "/commonList/planType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.planTypeData = response.dataList.filter(type => type.value !== "Postpaid");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    serviceId = null;
    serviceBasePlanDATA(event) {
        let planserviceData;
        let planServiceID = "";
        let serviceId = event.value;
        if (this.planGroupForm.value.planType) {
            const planserviceurl = "/planservice/all";
            this.planGroupService.getMethod(planserviceurl).subscribe((response: any) => {
                planserviceData = response.serviceList.filter(service => service.id === serviceId);
                if (planserviceData.length > 0) {
                    planServiceID = planserviceData[0].id;
                    this.serviceId = planServiceID;
                    this.loadPlans();
                    // if (this.customerGroupForm.value.custtype) {
                    // this.plantypaSelectData = this.filterPlanData.filter(
                    //   (id) => id.serviceId === planServiceID && id.planGroup === "Renew"
                    // );
                    // if (this.plantypaSelectData.length === 0) {
                    //   this.messageService.add({
                    //     severity: "info",
                    //     summary: "Note ",
                    //     detail: "Plan not available for this Plan type and service ",
                    //   });
                    // }
                }
            });
        } else {
            this.toastr.info(`Please select a plan type`, 'Info!');
            // this.messageService.add({
            //   severity: "info",
            //   summary: "Note ",
            //   detail: "Please select a plan type",
            // });
        }
    }

    loadPlans() {
        let serviceArea = "";
        if (!this.planGroupForm.value.planType) {
            this.toastr.error(`Please Select Plan Type`, 'Failed!');
            // this.messageService.add({
            //   severity: "error",
            //   summary: "Error",
            //   detail: "Please Select Plan Type",
            //   icon: "far fa-times-circle",
            // });

            return;
        }
        if (this.planGroupForm.value.serviceAreaId.length == 0) {
            this.toastr.error(`Please Select Service Area`, 'Failed!');
            // this.messageService.add({
            //   severity: "error",
            //   summary: "Error",
            //   detail: "Please Select Service Area",
            //   icon: "far fa-times-circle",
            // });

            return;
        } else {
            this.planGroupForm.value.serviceAreaId.forEach((element, index) => {
                serviceArea = serviceArea + "&serviceAreaId=" + element;
                if (index == 0) {
                    serviceArea = serviceArea.trim();
                }
            });
        }

        if (!this.planGroupForm.value.planGroupType) {
            this.toastr.error(`Please Select Plan Group`, 'Failed!');
            // this.messageService.add({
            //   severity: "error",
            //   summary: "Error",
            //   detail: "Please Select Plan Group",
            //   icon: "far fa-times-circle",
            // });

            return;
        }

        const planserviceurl = "/plansByTypeServiceModeStatusAndMultipleServiceArea";
        this.planGroupService
            .getPlansByTypeServiceModeStatusAndServiceArea(
                planserviceurl,
                this.planGroupForm.value.planType,
                this.serviceId,
                serviceArea,
                this.planGroupForm.value.planMode,
                "Active",
                this.planGroupForm.value.planGroupType,
                this.planValidity,
                this.planunitValidity
            )
            .subscribe((response: any) => {
                if (response.status == 200 && response.postPaidPlan.length > 0) {
                    if (this.planGroupForm.value.category == "Business Promotion") {
                        this.plantypaSelectData = response.postPaidPlan.filter(
                            data => data.category == "Business Promotion"
                        );
                        if (this.plantypaSelectData.length == 0) {
                            this.plantypaSelectData = [];
                            this.toastr.info(`Plan not available for this Plan Category`, 'Info!');
                            // this.messageService.add({
                            //   severity: "info",
                            //   summary: "Note ",
                            //   detail: "Plan not available for this Plan Category",
                            // });
                        }
                    } else {
                        this.plantypaSelectData = response.postPaidPlan.filter(
                            data => data.category == "Normal"
                        );
                    }
                } else {
                    this.plantypaSelectData = [];
                    this.toastr.info(`Plan not available for this Plan type and service`, 'Info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Note ",
                    //   detail: "Plan not available for this Plan type and service ",
                    // });
                }
            });
    }

    listIfPlanGroupShow() {
        this.productplanmappingList = this.fb.array([]);
        this.creatPlangroup = false;
        this.ifPlangroupList = true;
        this.ifplanGroupDataShow = false;
    }

    planGroupDataById(planGroupId) {
        let plandatalength = 0;
        this.creatPlangroup = false;
        this.ifPlangroupList = false;
        this.ifplanGroupDataShow = true;
        this.dataPlanAmount = [];
        this.totalOfferPrice = 0;
        this.getworkflowAuditDetails("", planGroupId, "PLAN_GROUP");
        let url = "/findPlanGroupById?planGroupId=" + planGroupId;

        this.planGroupService.getMethod(url).subscribe((response: any) => {
            this.planGroupData = response.planGroup;
            this.serviceAreaListPlanbundle = response.serviceareaList.serviceareaName;
            this.planGroupMapingList = this.planGroupData.planMappingList;
            this.totalOfferPrice = this.planGroupData.offerprice;
            if (this.planGroupData.servicearea) {
                this.planGroupServiceAreaName = this.planGroupData.servicearea.name;
            }
            // this.bindedProductPlanGroupMappingList = this.planGroupData.productPlanGroupMappingList;
            this.getProductDetails(planGroupId);
            //   if (this.planGroupMapingList) {
            //     this.planGroupMapingList.forEach(element => {
            //       if (element.plan.id) {
            //         this.dataPlanAmount.push(element.newofferprice);
            //         this.totalOfferPrice = this.totalOfferPrice + Number(element.newofferprice);
            //       }
            //     });
            //   }
            this.showPlanGroupDialog();
        });
    }

    showPlanGroupDialog() {
        if (this.viewPlanGroupDialogTemplate) {
            this.dialogRef = this.dialog.open(this.viewPlanGroupDialogTemplate, {
                width: '75%',
                maxHeight: '90vh',
                autoFocus: false,
                disableClose: true,
            });
        } else {
            console.error('View Plan Group Template not found!');
        }
    }

    pageChangedProductPlanMappingDetailList(pageNumber) {
        this.productPageChargeDeatilList = pageNumber;
    }

    getPlanGroup() {
        const url = "/commonList/planGroup";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.planGroupDropDownData = response.dataList;
                this.dynamicDropdown = this.planGroupDropDownData;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    closeParentCustt() {
        this.ifModelIsShow = false;
    }

    rejectPlanOpen(planGroupId, nextApproverId) {
        this.reject = false;
        this.selectStaff = null;
        this.rejectPlanData = [];
        $("#rejectPlanModal").modal("show");
        this.assignPlanGroupID = planGroupId;
        this.nextApproverId = nextApproverId;
        this.dialogRef = this.dialog.open(this.rejectPlanDialogTemplate, {
            width: '35%',
            maxHeight: '70vh',
            autoFocus: false,
            disableClose: true,
        });
    }

    approvePlanOpen(planGroupId, nextApproverId) {
        this.approved = false;
        this.selectStaff = null;
        this.approvePlanData = [];
        $("#assignApporvePlanModal").modal("show");
        this.assignPlanGroupID = planGroupId;
        this.nextApproverId = nextApproverId;
        this.dialogRef = this.dialog.open(this.approvePlanDialogTemplate, {
            width: '35%',
            maxHeight: '70vh',
            autoFocus: false,
            disableClose: true,
        });
    }

    assignPlan() {
        this.assignPlansubmitted = true;
        this.approved = false;
        this.selectStaff = null;
        this.approvePlanData = [];
        if (this.assignPlanForm.valid) {
            let url = "/approvePlanGroup";
            let assignCAFData = {
                planGroupId: this.assignPlanGroupID,
                nextStaffId: "",
                flag: "approved",
                remark: this.assignPlanForm.controls.remark.value,
                staffId: localStorage.getItem("userId"),
            };

            this.planGroupService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    //$("#assignApporvePlanModal").modal("hide");

                    this.assignPlanForm.reset();
                    this.assignPlansubmitted = false;
                    if (response.result.dataList != null) {
                        this.approvePlanData = response.result.dataList;
                        this.approvePlan = this.approvePlanData
                        this.approved = true;
                    } else {
                        // $("#assignApporvePlanModal").modal("hide");
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.findAllplanGroups("");
                    }
                    this.toastr.success(`${response.message}`, 'Success!');

                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Successfully",
                    //   detail: response.message,
                    //   icon: "far fa-check-circle",
                    // });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            );
        }
    }


    rejectPlan() {
        this.rejectPlanSubmitted = true;
        if (this.rejectPlanForm.valid) {
            let assignCAFData = {
                planGroupId: this.assignPlanGroupID,
                nextStaffId: "",
                flag: "Rejected",
                remark: this.rejectPlanForm.controls.remark.value,
                staffId: localStorage.getItem("userId"),
            };

            let url = "/approvePlanGroup";
            this.planGroupService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    this.rejectPlanForm.reset();
                    this.rejectPlanSubmitted = false;
                    if (response.result.dataList != null) {
                        this.rejectPlanData = response.result.dataList;
                        this.reject = true;
                    } else {
                        // $("#rejectPlanModal").modal("hide");
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.findAllplanGroups("");
                    }
                    this.toastr.success(`${response.message}`, 'Success!');

                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Successfully",
                    //   detail: response.message,
                    //   icon: "far fa-check-circle",
                    // });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            );
        }
    }

    assignToStaff(flag) {
        let url: any;
        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.assignPlanGroupID
                    }&eventName=${"PLAN_GROUP"}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignPlanGroupID
                    }&eventName=${"PLAN_GROUP"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.assignPlanGroupID
                    }&eventName=${"PLAN_GROUP"}&nextAssignStaff=${this.selectStaffReject
                    }&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignPlanGroupID
                    }&eventName=${"PLAN_GROUP"}&isApproveRequest=${flag}`;
            }
        }

        this.planGroupService.getMethod(url).subscribe(
            response => {
                $("#assignApporvePlanModal").modal("hide");
                $("#rejectPlanModal").modal("hide");
                this.getPlanGroup();
                this.findAllplanGroups("");
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }
    getworkflowAuditDetails(size, id, name) {
        let page = this.currentPageMasterSlab1;
        let page_list;
        if (size) {
            page_list = size;
            this.MasteritemsPerPage1 = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.MasteritemsPerPage1 = 5;
            } else {
                this.MasteritemsPerPage1 = 5;
            }
        }

        this.workflowAuditData1 = [];

        let data = {
            page: page,
            pageSize: this.MasteritemsPerPage1,
        };

        let url = "/workflowaudit/list?entityId=" + id + "&eventName=" + name;

        this.planGroupService.postMethod(url, data).subscribe(
            (response: any) => {
                this.workflowAuditData1 = response.dataList;
                this.MastertotalRecords1 = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        );
    }
    pageChangedMasterList(pageNumber) {
        this.currentPageMasterSlab1 = pageNumber;
        this.getworkflowAuditDetails("", this.workflowID, "PLAN");
    }

    resetPlanMappingList(event) {
        this.planGroupForm.patchValue({
            offerprice: 0,
        });
        this.PlanMapping = this.fb.array([]);
        this.PlanMappingfromgroup.reset();
        if (this.planGroupForm.value.category !== "Business Promotion") {
            this.planGroupForm.patchValue({
                requiredApproval: false,
                invoiceToOrg: false,
            });
        } else if (this.planGroupForm.value.category == "Business Promotion") {
            this.planGroupForm.patchValue({
                requiredApproval: true,
                invoiceToOrg: true,
            });
        }
    }
    openPaymentWorkFlow(id, auditcustid) {
        this.ifModelIsShow = true;
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "PLAN_GROUP",
            planId: "",
        });
    }

    getServiceByServiceAreaID(event) {
        let data = [];
        data = event.value;
        let url = "/serviceArea/getAllServicesByServiceAreaId";
        this.customermanagementservice.postMethod(url, data).subscribe((response: any) => {
            this.serviceData = response.dataList;
        });
        this.resetPlanMappingList("");
    }

    pickModalOpen(data) {
        let url = "/workflow/pickupworkflow?eventName=PLAN_GROUP&entityId=" + data.planGroupId;
        this.planGroupService.getMethod(url).subscribe(
            (response: any) => {
                this.findAllplanGroups("");

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Info",
                    //   detail: response.responseMessage,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Success",
                    //   detail: response.responseMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }
    getAllProductCategory(): void {
        // let url = "";
        if (this.statusCheckService.isActiveInventoryService) {
            const url = "/productCategory/getAllActiveProductCategories";
            this.productManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.productCatagorys = response.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            );
        }
    }
    getAllProduct(): void {
        // let url = "";
        if (this.statusCheckService.isActiveInventoryService) {
            const url = "/product/getAllActiveProduct";
            this.productManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.products = response.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            );
        }
    }
    getProductPlanDetails() {
        if (
            this.planDetails.productplanmappingList != null &&
            this.planDetails.productplanmappingList.length > 0
        ) {
            this.productPlanDetailFlag = true;
            let attributeList = this.planDetails.productplanmappingList;
            if (attributeList.length !== 0) {
                attributeList.forEach(element => {
                    let productCategoryName = this.productCatagorys.find(
                        element1 => element1.id === element.productCategoryId
                    ).name;
                    if (element.productId != null) {
                        this.productName = this.products.find(
                            element2 => element2.id === element.productId
                        ).name;
                    } else {
                        this.productName = "";
                    }
                    if (element.ownershipType == "Sold") {
                        this.revisedChargeFlag = false;
                    } else {
                        this.revisedChargeFlag = true;
                    }
                    this.revisedChargeArray.push(this.revisedChargeFlag);
                    this.productplanmappingList.push(
                        this.fb.group({
                            planId: element.planId,
                            planName: element.planName,
                            revisedCharge: element.revisedCharge,
                            ownershipType: element.ownershipType,
                            productCategoryName: productCategoryName,
                            product_type: element.product_type,
                            productName: this.productName,
                            name: element.name,
                            productId: element.productId,
                            productCategoryId: element.productCategoryId,
                            id: "",
                            planGroupId: "",
                        })
                    );
                });
            }
        }
    }
    getProductPlanDetailsAtUpdate() {
        if (this.productPlanGroupMappingList.length > 0) {
            this.productPlanDetailFlag = true;
            let attributeList = this.productPlanGroupMappingList;
            if (attributeList.length !== 0) {
                attributeList.forEach(element => {
                    if (element.ownershipType == "Sold") {
                        this.revisedChargeFlag = false;
                    } else {
                        this.revisedChargeFlag = true;
                    }
                    this.revisedChargeArray.push(this.revisedChargeFlag);
                    this.productplanmappingList.push(
                        this.fb.group({
                            planId: element.planId,
                            planName: element.planName,
                            revisedCharge: element.revisedCharge,
                            ownershipType: element.ownershipType,
                            productCategoryName: element.productCategoryName,
                            product_type: element.product_type,
                            productName: element.productName,
                            name: element.name,
                            productId: element.productId,
                            productCategoryId: element.productCategoryId,
                            id: element.id,
                            planGroupId: element.planGroupId,
                        })
                    );
                });
            }
        }
    }
    async onRemovePlanProductMap(productFieldIndex: number, productFieldId: number) {
        this.productplanmappingList.removeAt(productFieldIndex);
        this.revisedChargeArray.splice(productFieldIndex, 1);

        if (this.productplanmappingList.length == 0) {
            this.planValidity = "";
            this.planunitValidity = "";
        }
    }
    deleteConfirmonPlanProductField(productFieldIndex: number, productFieldId: number) {
        if (productFieldIndex || productFieldIndex == 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Product?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.onRemovePlanProductMap(productFieldIndex, productFieldId);
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Rejected",
                    //   detail: "You have rejected",
                    // });
                },
            });
        }
    }
    async onRemoveAutoPlanProductMap(PlanId: number) {
        this.productplanmappingList.value.forEach(element => {
            if (PlanId == element.planId) {
                let index = this.productplanmappingList.value.findIndex(x => x.planId === element.planId);
                this.productplanmappingList.removeAt(index);
                if (this.productplanmappingList.length == 0) {
                    this.planValidity = "";
                    this.planunitValidity = "";
                }
            }
        });
    }

    async onRemoveProductPlanGroupMapping(PlanGroupId: number, PlanId: number) {
        // let url = "/deletePlanGroupMappingById?planGroupMappingId=" + PlanMappingFieldId;
        if (PlanGroupId != null && PlanId != null) {
            if (this.statusCheckService.isActiveInventoryService) {
                let url =
                    "/product_plan_mapping/deleteProductPlanGroupMapping?planGroupId=" +
                    PlanGroupId +
                    "&planId=" +
                    PlanId;
                this.productManagementService.deleteMethod(url).subscribe(
                    (response: any) => {
                        if (response.responseCode == 200) {
                            this.productplanmappingList = this.fb.array([]);
                            if (this.PlanMapping.length == 0) {
                                this.planValidity = "";
                                this.planunitValidity = "";
                            }
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: error.error.errorMessage,
                        //   icon: "far fa-times-circle",
                        // });
                    }
                );
            }
        } else {
            this.onRemoveAutoPlanProductMap(PlanId);
        }
    }
    revicedChargeValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }

    reasignPlanGroup(data) {
    }

    approvableStaff: any = [];
    assignedPlanGroupid: any;
    StaffReasignList(data) {
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.planGroupId}&eventName=PLAN_GROUP`;
        this.planGroupService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedPlanGroupid = data.id;
                this.approvableStaff = [];
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: response.responseMessage,
                    //   icon: "far fa-times-circle",
                    // });
                } else {
                    // this.messageService.add({
                    //   severity: "success",
                    //   summary: "Success",
                    //   detail: response.responseMessage,
                    //   icon: "far fa-times-circle",
                    // });
                }
                if (response.dataList != null) {
                    this.approvableStaff = response.dataList;
                    this.approved = true;
                    $("#reasignPlanGroup").modal("show");
                } else {
                    $("#reasignPlanGroup").modal("hide");
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }
    reassignWorkflow() {
        let url: any;
        this.remarks = this.assignPlanForm.controls.remark;
        if (this.assignPlanGroupID != null) {
            url = `/teamHierarchy/reassignWorkflow?entityId=${this.assignPlanGroupID}&eventName=PLAN_GROUP&assignToStaffId=${this.selectStaff}&remark=${this.remarks.value}`;
            if (this.assignPlanGroupID == null) {
                this.toastr.error(`Please Approve before reasign`, 'Failed!');

                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: "Please Approve before reasign",
                //   icon: "far fa-times-circle",
                // });
            } else {
                this.planGroupService.getMethod(url).subscribe(
                    (response: any) => {
                        $("#reasignPlanGroup").modal("hide");
                        this.findAllplanGroups("");

                        if (response.responseCode == 417) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                            // this.messageService.add({
                            //   severity: "error",
                            //   summary: "Error",
                            //   detail: response.responseMessage,
                            //   icon: "far fa-times-circle",
                            // });
                        } else {
                            this.findAllplanGroups("");
                            this.toastr.success(`Assigned to the next staff successfully.`, 'Success!');
                            // this.messageService.add({
                            //   severity: "success",
                            //   summary: "Successfully",
                            //   detail: "Assigned to the next staff successfully.",
                            //   icon: "far fa-times-circle",
                            // });
                        }
                    },
                    error => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: error.error.ERROR,
                        //   icon: "far fa-times-circle",
                        // });
                    }
                );
            }
        }

        // StaffReasignList(data) {
        //   console.log("model");
        //   let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.planGroupId}&eventName=PLAN_GROUP`;
        //   this.planGroupService.getMethod(url).subscribe(
        //     (response: any) => {
        //       if (response.responseCode == 417) {
        //         this.messageService.add({
        //           severity: "error",
        //           summary: "Error",
        //           detail: response.responseMessage,
        //           icon: "far fa-times-circle",
        //         });
        //       }
        //       console.log(response.dataList);
        //       if (response.dataList != null) {
        //         this.approvePlanData = response.dataList;
        //         this.approved = true;
        //         $("#reasignPlanGroup").modal("show");
        //       } else {
        //         $("#reasignPlanGroup").modal("hide");
        //       }
        //       console.log(response);
        //
        //     },
        //     (error: any) => {
        //
        //       // console.log(error, "error");
        //       this.messageService.add({
        //         severity: "error",
        //         summary: "Error",
        //         detail: error.error.ERROR,
        //         icon: "far fa-times-circle",
        //       });
        //     }
        //   );
    }

    getProductPlanMappingDetails(planId) {
        this.planProductMappingFromArray = this.fb.array([]);
        if (this.statusCheckService.isActiveInventoryService) {
            let url = `/product_plan_mapping/getProductPlanMappingDetails?planId=${planId}`;
            this.productManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planDetails.productplanmappingList = response.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            );
        }
    }
    getProductDetails(planGroupId) {
        let url = `/product_plan_group_mapping/getProductPlanGroupMappingDetails?plangroupid=${planGroupId}`;
        this.productManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.productPlanGroupMappingList = response.dataList;
                this.bindedProductPlanGroupMappingList = response.dataList;
                this.getProductPlanDetailsAtUpdate();
            },
            //       (error: any) => {
            //         this.messageService.add({
            //           severity: "error",
            //           summary: "Error",
            //           detail: error.error.ERROR,
            //           icon: "far fa-times-circle",
            //         });
            //       }
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

    clearForm() {
        this.searchStaffDeatil = "";
        this.approvePlanData = this.approvePlan;
    }

    openServiceAreaDialog() {
        this.dialog.open(this.serviceAreaDialogTemplate, {
            width: '35%',
            maxHeight: '70vh',
            autoFocus: false,
            disableClose: true
        });
    }
}
