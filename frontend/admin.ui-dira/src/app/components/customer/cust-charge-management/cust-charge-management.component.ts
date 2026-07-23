import { Component, OnInit, Input, ElementRef, EventEmitter, Output, ViewChild, TemplateRef } from "@angular/core";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { formatDate, DatePipe } from "@angular/common";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import moment from "moment";
import { ActivatedRoute, Router } from "@angular/router";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { filter } from "lodash";
// import { StaffSelectModelComponent } from 'StaffSelectModelComponent';
import { StaffSelectModelComponent } from "./../../staff-select-model/staff-select-model.component";
import { StaffService } from "../../radius-staff/staff.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { CustomerService } from "src/app/service/customer.service";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { LoginService } from "src/app/service/login.service";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTable } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
import { PageEvent } from "@angular/material/paginator";

declare var $: any;

@Component({
    selector: "app-cust-charge-management",
    templateUrl: "./cust-charge-management.component.html",
    styleUrls: ["./cust-charge-management.component.css"],
    standalone: false
})
export class CustChargeManagementComponent implements OnInit {
    @Input() cafCustomerID: any
    @ViewChild('addChargeDialog') addChargeDialog: TemplateRef<any>;
    @ViewChild('selectCustomerDialog') selectCustomerDialog: TemplateRef<any>;
    @ViewChild('selectStaffDialog') selectStaffDialog: TemplateRef<any>;
    @ViewChild('updateStaticIPDialog') updateStaticIPDialog: TemplateRef<any>;
    private currentDialogRef: MatDialogRef<any>;
    parentCustomerColumns: string[] = ['select', 'name', 'username'];
    staffColumns: string[] = ['select', 'name', 'username', 'partnerName'];

    custid = 0;
    custData: any = {};
    customerId = 0;
    custType: string = "";
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    currentCustChargePageSlab = 1;
    itemsCustChargePerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalCustChargeRecords: any;
    showItemCustChargePerPage = 0;
    ChargeCustList = [];
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    overChargeListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    overChargeListtotalRecords: String;
    currentPageoverChargeList = 1;
    overChargeListFromArray: FormArray;
    chargeGroupForm: FormGroup;
    currentDate = new Date();
    billingCycle: any = [];
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    custmerType: any = "";
    viewcustomerListData: any = [];
    filterPlanData = [];
    planDropdownInChageData: any = [];
    selectchargeValueShow: boolean = false;
    chargesubmitted = false;
    endData: any = "";
    deleteChargeID: any = "";
    planChageData: any;
    dateTime = new Date();
    todayDate: any;
    selectedParentCust: any = [];
    billableCusList: any = [];
    searchOptionSelect = this.commondropdownService.customerSearchOptionBill;
    newFirst = 0;
    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    prepaidParentCustomerList: any;
    parentCustomerListdatatotalRecords: any;
    parentFieldEnable = false;
    searchParentCustValue = "";
    searchParentCustOption = "";
    billableCustomerId = null;
    planMappingList: any = [];
    paymentOwnerId = null;
    customerPlanMappingList: any = [];
    currency: string;
    searchkey: string;
    searchkey2: string;
    searchDeatil: string;
    searchData: any;
    staffDataList: any = [];
    requestedByList: any = [];
    serviceAreaId: any;
    data: any = [];
    staffData: any = [];
    serviceAreaDisable = false;
    showItemPerPage = 1;
    selectedStaffCust: any = [];
    staffCustList: any = [];
    staffid: any = "";
    searchOption = "";
    selectCustomerDialogVisible: boolean = false;
    addChargeAccess: boolean = false;
    isInstallemnt: boolean = false;
    totalInstallments: any = [];
    totalInstallmentsLength: number;
    isDisabled: boolean = true;
    planGroup: any = [];
    displayedColumns: string[] = [
        'serialNo',
        'chargeName',
        'chargeAmount',
        'staticIP',
        'planName',
        'validity',
        'newPrice',
        'startDate',
        'expiryDate',
        'status',
        'installmentFrequency',
        'nextInstallmentDate',
        'lastInstallmentDate',
        'installmentDetails',
        'action'
    ];
    constructor(
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        // public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        public serviceAreaService: ServiceAreaService,
        private fb: FormBuilder,
        private datePipe: DatePipe,
        private staffService: StaffService,
        private route: ActivatedRoute,
        private router: Router,
        private systemService: SystemconfigService,
        private liveUserService: LiveUserService,
        public invoicePaymentListService: InvoicePaymentListService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private customerService: CustomerService,
        public loginService: LoginService, private toastr: ToastrService,
        private dialog: MatDialog
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custid = this.customerId;
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.addChargeAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CHARGE_CREATE
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
    }
    ngOnInit(): void {
        if (this.cafCustomerID) {
            this.customerId = this.cafCustomerID
            this.custid = this.customerId;
        }

        this.getCustomersDetail(this.customerId);
        this.chargeGroupForm = this.fb.group({
            chargeid: ["", Validators.required],
            validity: [null],
            price: ["", Validators.required],
            actualprice: ["", Validators.required],
            originalActualPrice: [""],
            charge_date: ["", Validators.required],
            type: ["Recurring", Validators.required],
            staticIPAdrress: [""],
            planid: [null],
            unitsOfValidity: [null],
            billingCycle: [null],
            connection_no: [null],
            paymentOwnerId: ["", Validators.required],
            discount: [null],
            expiry: [null],
            expiryDate: [null],
            installmentFrequency: [null],
            totalInstallments: [null],
            installment_no: [null]
        });
        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ]
        };
        this.overChargeListFromArray = this.fb.array([]);
        // this.customerId = this.custid;
        this.getSingleCustomerData(this.custid);
        this.getserviceData();
        this.dateTime.setDate(this.dateTime.getDate());
        this.todayDate = this.dateTime.getTime();
        this.commondropdownService.getInstallmentTypeData();

        this.systemService.getConfigurationByName("TOTAL_INSTALLMENTS").subscribe((res: any) => {
            this.totalInstallmentsLength = +res.data.value;
            for (let i = 0; i < this.totalInstallmentsLength; i++) {
                this.totalInstallments.push({ text: i + 1, value: i + 1 });
            }
        });
        this.commondropdownService.findAllplanGroups();
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
    }
    currentPageStaff = 1;
    parentStaffListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentstaffListdatatotalRecords = 0;

    onStaffPageChange(event: any): void {
        this.currentPageStaff = event.pageIndex + 1;
        this.parentStaffListdataitemsPerPage = event.pageSize;

        // Re-fetch staff data with pagination
        if (this.searchDeatil) {
            this.searchStaffByName();
        } else {
            this.getStaffDetailById(this.custData.serviceareaid);
        }
    }
    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            this.custData?.currency
                ? (this.currency = this.custData?.currency)
                : this.systemService
                    .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                    .subscribe((res: any) => {
                        this.currency = res.data.value;
                    });
            this.getCustChargeDetails("", this.custid);
        });
    }
    displayDialog: boolean = false;

    showDialog() {
        this.getServiceSerialNumber();
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
        this.staffSelectList = [
            {
                id: Number(userId),
                name: loginUserName
            }
        ];
        this.paymentOwnerId = Number(userId);
        const url = "/charge/ByType/CUSTOMER_DIRECT";

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.commondropdownService.chargeByTypeData = response.chargelist || [];
            },
            (error: any) => {
                console.error("Failed to load initial charges", error);
            }
        );

        // Open Material Dialog
        this.currentDialogRef = this.dialog.open(this.addChargeDialog, {
            width: '90vw',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });
    }
    @Output() backToList = new EventEmitter<void>();
    @Input() isFromCaf?: boolean = false;
    customerDetailOpen() {
        if (this.isFromCaf) {
            this.backToList.emit();
            return;
        }
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    TotalItemCustChargePerPage(event) {
        this.showItemCustChargePerPage = Number(event.value);
        if (this.currentCustChargePageSlab > 1) {
            this.currentCustChargePageSlab = 1;
        }
        this.getCustChargeDetails(this.showItemCustChargePerPage, this.custid);
    }
    onChargePageChange(event: any): void {
        this.currentCustChargePageSlab = event.pageIndex + 1;
        this.itemsCustChargePerPage = event.pageSize;

        // Re-fetch charge data with new page
        this.getCustChargeDetails(this.itemsCustChargePerPage, this.custid);
    }
    getCustChargeDetails(size, id) {
        this.planChageData = [];
        let page_list;

        if (size) {
            page_list = size;
            this.itemsCustChargePerPage = size;
        } else {
            if (this.showItemCustChargePerPage == 0) {
                this.itemsCustChargePerPage = this.pageITEM;
            } else {
                this.itemsCustChargePerPage = this.showItemCustChargePerPage;
            }
        }

        let data = [];
        let url = "/getAllCustomerDirectChargeByCustomer/" + id;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                const chargeList = response.custChargeOverrideList || [];

                // Load plan data for charges
                response.custChargeOverrideList.forEach(element => {
                    if (element.planid) {
                        const url = "/postpaidplan/" + element.planid;
                        this.customerManagementService.getMethod(url).subscribe((response: any) => {
                            this.planChageData.push(response.postPaidPlan);
                        });
                    }
                });

                // Filter deleted items FIRST
                const filteredList = chargeList.filter(value => value.isDeleted == false);

                // IMPORTANT: Set total records BEFORE pagination
                this.totalCustChargeRecords = filteredList.length;

                // For client-side pagination: Show all data, let mat-paginator handle display
                // MatTableDataSource with MatPaginator handles pagination automatically
                this.ChargeCustList = filteredList;


            },
            (error: any) => {
                this.ChargeCustList = [];
                this.totalCustChargeRecords = 0;
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

    pageChangedList(pageNumber) {
        this.currentCustChargePageSlab = pageNumber;
        this.getCustChargeDetails("", this.customerId);
    }

    // add charge
    billingSequence() {
        for (let i = 0; i < 12; i++) {
            this.billingCycle.push({ label: i + 1 });
            // console.log(this.billingCycle)
        }
    }

    getSingleCustomerData(id) {
        this.planDropdownInChageData = [];
        this.customerPlanMappingList = [];
        let url = "/customers/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custmerType = response.customers.custtype;

            //   this.getStaffDetailById(response.customers.serviceareaid);
            // this.chargeGroupForm.get("billingCycle").clearValidators();
            // this.chargeGroupForm.get("billingCycle").updateValueAndValidity();
            this.chargeGroupForm.patchValue({
                type: "Recurring"
            });
            this.billingSequence();
            // if (response.customers.plangroupid) {
            //   this.getPlangroupByPlan(response.customers.plangroupid);
            // } else {
            this.customerPlanMappingList = response.customers.planMappingList;
            // response.customers.planMappingList.forEach(element => {
            //   if (element.planId) {
            //     const url = "/postpaidplan/" + element.planId;
            //     this.customerManagementService.getMethod(url).subscribe((response: any) => {
            //       this.planDropdownInChageData.push(response.postPaidPlan);
            //     });
            //   }
            // });
            // }
            const url = "/subscriber/fetchCustomerDiscountDetailServiceLevel/" + id;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planMappingList = response.discountDetails;
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
        });
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

    onBillingCycleChange(e) {
        this.chargeGroupForm.get("connection_no").reset();
        this.chargeGroupForm.get("planid").reset();
        this.chargeGroupForm.get("expiry").reset();
    }

    deleteConfirmonChargeField(chargeFieldIndex: number, name: string) {
        if (chargeFieldIndex || chargeFieldIndex == 0) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: "Do you want to delete this " + name + "?",
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.onRemoveChargelist(chargeFieldIndex);
                } else {
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });

        }
    }

    onRemoveChargelist(index: number) {
        this.overChargeListFromArray.removeAt(index);
        this.chargeCustTable.renderRows()
    }

    pageChangedOverChargeList(pageNumber) {
        this.currentPageoverChargeList = pageNumber;
    }

    createoverChargeListFormGroup(): FormGroup {
        let billingCycle = this.chargeGroupForm.value.type === "Recurring" ? 1 : null;
        let planName = null;
        let planidVal = null;

        // Safely check if planid exists before trying to split it
        if (this.chargeGroupForm.value.planid) {
            planidVal = String(this.chargeGroupForm.value.planid).split("-")[0];

            // Safely check el.planId before splitting
            let matchedPlan = this.planByService.find(el =>
                el.planId && String(el.planId).split("-")[0] === planidVal
            );

            if (matchedPlan && matchedPlan.planName) {
                planName = String(matchedPlan.planName).split("(")[0];
            }
        }

        let expiryDateFormatted = null;
        if (this.chargeGroupForm.value.expiry) {
            expiryDateFormatted = moment(this.chargeGroupForm.value.expiry).format("DD-MM-YYYY HH:mm").toString();
        }

        return this.fb.group({
            type: [this.chargeGroupForm.value.type ? this.chargeGroupForm.value.type : "Recurring"],
            chargeid: [this.chargeGroupForm.value.chargeid],
            validity: [this.chargeGroupForm.value.validity || null],
            price: [this.chargeGroupForm.value.price],
            actualprice: [this.chargeGroupForm.value.actualprice],
            originalActualPrice: [this.chargeGroupForm.value.originalActualPrice],
            charge_date: [this.chargeGroupForm.value.charge_date],
            planid: [planidVal],
            planName: [planName],
            unitsOfValidity: [this.chargeGroupForm.value.unitsOfValidity || null],
            billingCycle: [billingCycle],
            paymentOwnerId: [this.paymentOwnerId],
            discount: [this.chargeGroupForm.value.discount || null],
            staticIPAdrress: [this.chargeGroupForm.value.staticIPAdrress || null],
            expiry: [this.chargeGroupForm.value.expiry || null],
            expiryDate: [expiryDateFormatted],
            connection_no: [this.chargeGroupForm.value.connection_no || null],
            installment_no: [null],
            installmentFrequency: [this.chargeGroupForm.value.installmentFrequency || null],
            totalInstallments: [this.chargeGroupForm.value.totalInstallments || null]
        });
    }

    staticIPCharge = null;
    staticIPExpiryDate = null;
    selectedStaff: any = [];
    selectStaffType = "";
    staffSelectList: any = [];
    selectStaff: boolean = false;
    showSelectStaffModel: boolean = false;
    parentCustomerDialogType = "";
    editStaticIP(charge) {
        this.staticIPCharge = charge;
        this.staticIPCharge.enddate = moment(charge.enddate).toDate();
        this.staticIPCharge.startdate = moment(charge.startdate).toDate();
        this.staticIPExpiryDate = moment(charge.enddate).toDate();
        this.currentDialogRef = this.dialog.open(this.updateStaticIPDialog, {
            width: '500px',
            disableClose: false,
            autoFocus: false
        });
    }
    closeUpdateStaticIPModal(): void {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.staticIPCharge = null;
    }
    updateStaticIPAddress(): void {
        const url = `/updateStaticIpAddress?custChargeId=${this.staticIPCharge.id}&staticIPAddress=${this.staticIPCharge.staticIPAdrress
            }&staticIPExpiryDate=${this.datePipe.transform(this.staticIPExpiryDate, "yyyy-MM-dd")}`;

        this.customerManagementService.updateMethod(url, {}).subscribe(
            (response: any) => {
                this.toastr.success(response.message || "Edited Successfully", 'Success!')
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
                //     detail: response.message,
                //     icon: "far fa-check-circle"
                // });

                this.closeUpdateStaticIPModal();
                this.staticIPCharge = null;
                this.staticIPExpiryDate = null;
                this.getCustChargeDetails("", this.custid);
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

    onAddoverChargeListField() {
        this.chargesubmitted = true;
        this.chargeGroupForm.patchValue({
            paymentOwnerId: this.paymentOwnerId,
            type: "Recurring"
        });
        if (this.chargeGroupForm.valid) {
            if (this.chargeGroupForm.value.price >= this.chargeGroupForm.value.actualprice) {
                this.overChargeListFromArray.push(this.createoverChargeListFormGroup());
                this.chargeGroupForm.reset();
                this.chargeGroupForm.patchValue({
                    type: "Recurring"
                });
                this.chargesubmitted = false;
                this.selectchargeValueShow = false;
                this.planByService = [];
            }
        }

        this.chargeCustTable.renderRows()
    }

    getPlangroupByPlan(planGroupId) {
        // this.planDropdownInChageData = [];
        this.customerPlanMappingList = [];
        let MappURL = "/findPlanGroupMappingByPlanGroupId?planGroupId=" + planGroupId;
        this.customerManagementService.getMethod(MappURL).subscribe((response: any) => {
            let attributeList = response.planGroupMappingList;
            attributeList.forEach(element => {
                this.customerPlanMappingList.push(element.plan);
            });
        });
    }

    getPlanValidityForChagre(event) {
        const planId = event.value.split("-")[0];
        const id = event.value.split("-")[1];
        let customerPlanMappingListPlanId = this.customerPlanMappingList.find(
            plan => Number(plan.planId) == Number(planId)
        );
        let expiry = this.planByService.find(
            plan =>
                Number(plan.planId.split("-")[0]) == Number(planId) &&
                Number(plan.planId.split("-")[1] == id)
        ).expiryDate;

        // let expiryDate = moment(expiry).format("DD-MM-YYYY HH:mm").toString();

        this.chargeGroupForm.patchValue({
            validity: Number(customerPlanMappingListPlanId.validity),
            unitsOfValidity: customerPlanMappingListPlanId.unitsOfValidity,
            expiry: expiry
        });
        let planData = this.planMappingList.find(
            element => element.connectionNo === this.chargeGroupForm.value.connection_no
        );
        if (
            planData.discountType === "Recurring" &&
            new Date(planData.discountExpiryDate) > this.dateTime &&
            planData.discount > 0
        ) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Change Discount Confirmation',
                    description: `Do you want to apply " + planData.discount + " % of  Discount?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.chargeGroupForm.patchValue({
                        discount: planData.discount
                    });
                } else {
                    this.chargeGroupForm.patchValue({
                        discount: 0
                    });
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });

        } else if (
            planData.discountType === "Recurring" &&
            new Date(planData.discountExpiryDate) > this.dateTime &&
            planData.discount < 0
        ) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Change Discount Confirmation',
                    description: `Do you want to over charge customer " + planData.discount + " % ?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.chargeGroupForm.patchValue({
                        discount: planData.discount
                    });
                } else {
                    this.chargeGroupForm.patchValue({
                        discount: 0
                    });
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });


        }
        //
        // });
    }

    selectcharge(_event: any) {
        let chargeId = _event.value;
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
            if (viewChargeData.chargecategory === "IP") {
                this.chargeGroupForm.get("staticIPAdrress").setValidators([Validators.required]);
                this.chargeGroupForm.get("staticIPAdrress").updateValueAndValidity();
            } else {
                this.chargeGroupForm.get("staticIPAdrress").clearValidators();
                this.chargeGroupForm.get("staticIPAdrress").updateValueAndValidity();
            }

            const basePrice = Number(viewChargeData.actualprice) || 0;
            const taxAmount = Number(viewChargeData.taxamount) || 0;
            const totalAmountWithTax = basePrice + taxAmount;

            this.chargeGroupForm.patchValue({
                actualprice: totalAmountWithTax,
                originalActualPrice: basePrice,
                price: totalAmountWithTax,
                charge_date: formattedDate
            });
        });
    }

    closeChargeModal() {
        this.chargeGroupForm.reset();
        this.overChargeListFromArray = this.fb.array([]);
        this.removeSelParentCust();
        this.removeSelectStaff();
        this.displayDialog = false;

        // Close Material Dialog
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
    }


    saveChargeData(dialogRef) {
        const url = "/createCustChargeOverride";
        var request = [];
        request = JSON.parse(JSON.stringify(this.overChargeListFromArray.value));

        request.forEach(charge => {
            if (charge.expiry) {
                const parsedDate = moment(charge.expiry);
                charge.expiry = parsedDate.isValid() ? parsedDate.format("YYYY-MM-DD") : null;
            } else {
                charge.expiry = null;
            }

            if (charge.originalActualPrice !== undefined && charge.originalActualPrice !== null) {
                charge.actualprice = charge.originalActualPrice;
            }
            delete charge.originalActualPrice;
        });
        let chargeDta = {
            custChargeDetailsPojoList: request,
            custid: this.custid,
            billableCustomerId: this.billableCustomerId,
            paymentOwnerId: this.paymentOwnerId
        };
        this.customerManagementService.postMethod(url, chargeDta).subscribe(
            (response: any) => {
                this.getCustChargeDetails("", this.custid);
                this.closeChargeModal();
                dialogRef.close()
                this.toastr.success(`Successfully Created`, 'Success!')
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
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

    deleteConfirmCharge(id, startdate, enddate) {
        this.deleteChargeID = id;

        if (this.todayDate < enddate && this.todayDate > startdate) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Charge Confirmation',
                    description: `Do you want to delete this Charge?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    $("#deleteChargeId").modal("show");
                } else {
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });

        } else if (this.todayDate < enddate && this.todayDate < startdate) {
            this.deletechargeData("softDel");
        }
    }

    deletecloseModel() {
        this.deleteChargeID = "";
    }

    deletechargeData(SID) {
        let data;
        if (SID == "softDel") {
            data = {
                endDate: "",
                id: this.deleteChargeID,
                softDelete: true
            };
        } else {
            data = {
                endDate: this.endData,
                id: this.deleteChargeID,
                softDelete: false
            };
        }

        const url = "/deleteCustomerDirectCharge";
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.getCustChargeDetails("", this.custid);
                this.toastr.success(`${response.message}`, 'Success!')
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
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
    custServiceData: any = [];
    planByService: any = [];
    isShowConnection = true;
    serviceSerialNumbers = [];
    async getserviceData() {
        // const url = "/subscriber/getPlanByCustService/" + this.custid;
        // this.customerManagementService.getMethod(url).subscribe(
        //   (response: any) => {
        //     this.custServiceData = response.dataList;
        //
        //   },
        //   (error: any) => {
        //     this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //       detail: error.error.ERROR,
        //       icon: "far fa-times-circle",
        //     });
        //
        //   }
        // );
        // const url = "/subscriber/getFuturePlanList/" + this.custid;
        // await this.customerManagementService.getMethod(url).subscribe(
        //   (response: any) => {
        //     if (response.dataList != null) {
        //       response.dataList.forEach(data => {
        //         if (this.custServiceData.length > 0) {
        //           let isElementAlreadyExist = this.custServiceData.find(
        //             el => el.serviceId === data.serviceId
        //           )
        //             ? true
        //             : false;
        //           if (!isElementAlreadyExist) {
        //             this.custServiceData.push(data);
        //           }
        //         } else {
        //           this.custServiceData.push(data);
        //         }
        //       });
        //     }
        //
        //   },
        //   (error: any) => {
        //     // console.log(error, "error")
        //     this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //       detail: error.error.ERROR,
        //       icon: "far fa-times-circle",
        //     });
        //
        //   }
        // );

        const url1 = "/subscriber/getActivePlanList/" + this.custid + "?isNotChangePlan=true";
        await this.customerManagementService.getMethod(url1).subscribe(
            (response: any) => {
                if (response.dataList != null) {
                    this.custServiceData = response.dataList.filter(
                        item =>
                            item.plangroup !== "Volume Booster" &&
                            item.plangroup !== "Bandwidthbooster" &&
                            item.plangroup !== "DTV Addon"
                    );
                }
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
    getServiceSerialNumber() {
        var keepGping = false;
        this.serviceSerialNumbers = [];
        this.overChargeListFromArray = this.fb.array([]);
        this.chargeGroupForm.reset();
        if (this.custServiceData.length > 0) {
            this.custServiceData.forEach(item => {
                if (!keepGping) {
                    var filteredItem = item.customerInventorySerialnumberDtos.filter(item => item.primary);
                    if (filteredItem.length > 0) {
                        this.isShowConnection = false;
                        this.serviceSerialNumbers.push({
                            serialNumber: filteredItem[0].serialNumber,
                            custPlanMapppingId: item.custPlanMapppingId,
                            connection_no: item.connection_no
                        });
                    } else {
                        this.isShowConnection = true;
                        this.serviceSerialNumbers = [];
                        keepGping = true;
                    }
                }
            });
        }
    }

    filterPlan(e) {
        this.planByService = [];
        let expiryDate;
        this.customerPlanMappingList.filter(element => {
            let isElementAlreadyExist = this.planByService.filter(el => el.planId === element.planId);
            if (
                element.serviceId ==
                this.planMappingList.find(plan => plan.connectionNo === e.value).serviceId &&
                (element.plangroup === "Renew" || element.plangroup === "Registration and Renewal") &&
                element.custPlanStatus.toLowerCase() === "active"
            ) {
                expiryDate = new Date(element.expiryDate);
                // let date = new Date(expiryDate);
                // let extendValue = 1 * element.validity;
                // if (this.chargeGroupForm.value.type === this.chargeType[1].label) {
                // var unitsOfValidity = element.unitsOfValidity;
                // switch (unitsOfValidity) {
                //   case "Days": {
                //     date.setDate(date.getDate() + extendValue);
                //     break;
                //   }

                //   case "Hours": {
                //     date.setHours(date.getHours() + extendValue);
                //     break;
                //   }

                //   case "Months": {
                //     date.setMonth(date.getMonth() + extendValue);
                //     break;
                //   }

                //   case "Years": {
                //     date.setFullYear(date.getFullYear() + extendValue);
                //     break;
                //   }
                // }
                // expiryDate = date;
                // }
                if (isElementAlreadyExist.length > 0 && isElementAlreadyExist.id !== element.id) {
                    this.planByService.push({
                        planId: element.planId,
                        planName: element.planName,
                        expiryDate: expiryDate
                    });
                } else {
                    this.planByService.push({
                        planId: element.planId,
                        planName: element.planName,
                        expiryDate: expiryDate
                    });
                }
            }
        });
        this.planByService.map(plan => {
            plan.planId = plan.planId + "-" + plan.id;
            plan.planName =
                plan.planName + "(" + moment(plan.expiryDate).format("DD-MM-YYYY").toString() + ")";
        });
        this.commondropdownService.getChargeTypeByList(
            this.planMappingList.find(plan => plan.connectionNo === e.value).serviceId,
            this.currency
        );
    }
    isStaticIPAdrress(chargeid) {
        if (chargeid !== null && chargeid !== undefined && chargeid !== "") {
            if (!this.commondropdownService.chargeByTypeData ||
                !Array.isArray(this.commondropdownService.chargeByTypeData)) {
                return false;
            }

            return (
                this.commondropdownService.chargeByTypeData.filter(
                    charge => charge.id === chargeid && charge.chargecategory === "IP"
                ).length > 0
            );
        } else {
            return false;
        }
    }

    modalOpenParentCustomer() {
        this.selectCustomerDialogVisible = true;
        this.currentDialogRef = this.dialog.open(this.selectCustomerDialog, {
            width: '80%',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });
        this.newFirst = 0;
        this.getParentCustomerData();
        this.selectedParentCust = [];
    }

    getParentCustomerData() {
        const currentPage = this.currentPageParentCustomerListdata;
        const pageSize = this.parentCustomerListdataitemsPerPage;

        const data = {
            page: currentPage,
            pageSize: pageSize
        };

        const url = "/parentCustomers/list/" + this.custType;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                const list = response.parentCustomerList || [];
                // Filter out current customer id if needed
                this.prepaidParentCustomerList = list.filter(cust => cust.id !== this.custid);

                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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

    getSearchCustomerByService() {
        const url =
            "/getByCustomerService?page=" +
            this.currentPageParentCustomerListdata +
            "&pageSize=" +
            this.parentCustomerListdataitemsPerPage +
            "&service=" +
            this.searchDeatil +
            "&customerType=" +
            this.custType;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.customers.content;
                const usernameList: string[] = [];
                this.prepaidParentCustomerList.forEach(element => {
                    usernameList.push(element.username);
                });

                this.liveUserService
                    .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                        users: usernameList
                    })
                    .subscribe((res: any) => {
                        const liveUsers: string[] = res.liveusers;
                        this.prepaidParentCustomerList.forEach(element => {
                            if (liveUsers.findIndex(e => e == element.username) < 0) {
                                element.connectionMode = "Offline";
                            } else {
                                element.connectionMode = "Online";
                            }
                        });
                    });
                this.parentCustomerListdatatotalRecords = response.customers.totalElements;

                this.parentCustomerListdataitemsPerPage = response.pageDetails.totalRecordsPerPage;
                this.currentPageParentCustomerListdata = response.pageDetails.currentPageNumber;
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
                    this.prepaidParentCustomerList = [];
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

    searchParentCustomer() {
        const searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "id",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage,
            sortBy: "id",
            sortOrder: 0
        };
        if (this.searchParentCustOption !== "UserName" && this.searchParentCustOption !== "Name") {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchParentCustValue.trim() ||
                !this.searchkey2 ||
                this.searchkey2 !== this.searchParentCustOption.trim()
            ) {
                this.currentPageParentCustomerListdata = 1;
            }
            this.searchkey = this.searchParentCustValue.trim();
            this.searchkey2 = this.searchParentCustOption.trim();
            searchData.filters[0].filterValue = this.searchParentCustValue.trim();
            searchData.filters[0].filterColumn = this.searchParentCustOption.trim();
        } else {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchParentCustValue ||
                !this.searchkey2 ||
                this.searchkey2 !== this.searchParentCustOption
            ) {
                this.currentPageParentCustomerListdata = 1;
            }
            let searchParentCustValue = this.datePipe.transform(this.searchParentCustValue, "yyyy-MM-dd");
            this.searchkey = searchParentCustValue;
            this.searchkey2 = this.searchParentCustOption;
            this.searchData.filters[0].filterValue = searchParentCustValue;
            this.searchData.filters[0].filterColumn = this.searchParentCustOption;
        }
        if (this.searchParentCustOption == "UserName") {
            this.getSearchCustomerByService();
        } else {
            this.searchData.page = this.currentPageParentCustomerListdata;
            this.searchData.pageSize = this.parentCustomerListdataitemsPerPage;
            const url = "/customers/search/" + this.custType;
            this.customerManagementService.postMethod(url, searchData).subscribe(
                (response: any) => {
                    this.prepaidParentCustomerList = response.customerList;
                    const usernameList: string[] = [];
                    this.prepaidParentCustomerList.forEach(element => {
                        usernameList.push(element.username);
                    });
                    this.liveUserService
                        .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                            users: usernameList
                        })
                        .subscribe((res: any) => {
                            const liveUsers: string[] = res.liveusers;
                            this.prepaidParentCustomerList.forEach(element => {
                                if (liveUsers.findIndex(e => e == element.username) < 0) {
                                    element.connectionMode = "Offline";
                                } else {
                                    element.connectionMode = "Online";
                                }
                            });
                        });
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
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
                        this.prepaidParentCustomerList = [];
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
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData();
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    paginate(event: PageEvent) {

        this.currentPageParentCustomerListdata = event.pageIndex + 1; // pageIndex is 0-based
        this.parentCustomerListdataitemsPerPage = event.pageSize;

        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }


    async saveSelCustomer() {
        this.billableCusList = [
            {
                id: this.selectedParentCust.id,
                name: this.selectedParentCust.name
            }
        ];
        this.billableCustomerId = this.selectedParentCust.id;
        this.modalCloseParentCustomer();
        this.selectCustomerDialogVisible = false;
    }

    modalCloseParentCustomer() {
        if (this.currentDialogRef) this.currentDialogRef.close();
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.selectCustomerDialogVisible = false;
    }

    removeSelParentCust() {
        this.selectedParentCust = [];
        this.billableCusList = [];
        this.billableCustomerId = null;
    }

    getStaffDetailById(serviceAreaId: any): void {
        const url = "/getstaffuserbyserviceareaid/" + serviceAreaId;
        this.serviceAreaService.getMethod(url).subscribe(
            (response: any) => {
                const fullList = response.dataList || [];

                this.staffData = fullList;
                this.staffDataList = fullList;

                this.staffData.forEach((element) => {
                    element.displayLabel = `${element.fullName} (Ph: ${element.phone})`;
                });

                this.parentstaffListdatatotalRecords = fullList.length;

                // Calculate start and end index for current page
                const startIndex = (this.currentPageStaff - 1) * this.parentStaffListdataitemsPerPage;
                const endIndex = startIndex + this.parentStaffListdataitemsPerPage;

                // Slice full list to get current page's data
                this.staffDataList = fullList.slice(startIndex, endIndex);
            },
            (error: any) => {
                console.error('Error loading staff:', error);
                this.staffData = [];
                this.staffDataList = [];
            }
        );
    }

    modalOpenSelectStaff(type) {
        this.selectStaff = true;
        this.parentCustomerDialogType = type;
        this.selectedStaff = [];
        this.selectStaffType = type;

        // Load staff data based on service area
        if (this.custData && this.custData.serviceareaid) {
            this.getStaffDetailById(this.custData.serviceareaid);
        }

        // Open dialog
        // this.currentDialogRef = this.dialog.open(this.selectStaffDialog, {
        //     width: '80%',
        //     maxHeight: '90vh',
        //     disableClose: false,
        //     autoFocus: false
        // });
    }


    saveSelstaff(): void {
        if (this.selectedStaffCust) {
            this.staffSelectList = [
                {
                    id: Number(this.selectedStaffCust.id),
                    name: this.selectedStaffCust.firstname
                }
            ];
            this.paymentOwnerId = Number(this.selectedStaffCust.id);
        }
        this.modalCloseStaff();
        this.selectStaff = false;
    }
    searchStaffByName(): void {
        const url = `/getByCustomerService?page=${this.currentPageStaff}&pageSize=${this.parentStaffListdataitemsPerPage}&service=${this.searchDeatil}&customerType=${this.custType}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.staffData = response.customers?.content || response.customers || [];
                this.parentstaffListdatatotalRecords = response.customers?.totalElements || response.pageDetails?.totalRecords || 0;

                const usernameList: string[] = [];
                this.staffData.forEach((element) => {
                    usernameList.push(element.username);
                });

                this.liveUserService
                    .postMethod("liveUser/isCustomersOnlineOrOffline", { users: usernameList })
                    .subscribe((res: any) => {
                        const liveUsers: string[] = res.liveusers;
                        this.staffData.forEach((element) => {
                            if (liveUsers.findIndex((e) => e == element.username) < 0) {
                                element.connectionMode = "Offline";
                            } else {
                                element.connectionMode = "Online";
                            }
                        });
                    });
            },
            (error: any) => {
                this.parentstaffListdatatotalRecords = 0;
                if (error.error.status === 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    this.staffData = [];
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


    /**
     * Clear search form
     */
    clearSearchForm(): void {
        this.searchDeatil = "";
        // Re-fetch all staff data by calling getStaffDetail with default service area
        if (this.custData && this.custData.serviceareaid) {
            this.getStaffDetailById(this.custData.serviceareaid);
        }
    }
    selectedStaffChange(event): void {
        this.selectStaff = false;
        let data = event;
        this.staffSelectList = [
            {
                id: Number(data.id),
                name: data.firstname
            }
        ];
        this.paymentOwnerId = Number(data.id);
    }
    modalCloseStaff() {
        if (this.currentDialogRef) this.currentDialogRef.close();
        this.selectStaff = false;
    }

    closeSelectStaff() {
        this.selectStaff = false;
    }

    removeSelectStaff() {
        this.staffSelectList = [];
        this.paymentOwnerId = null;
    }

    onChangeInstallmentType() {
        if (this.isInstallemnt === true) {
            this.chargeGroupForm.get("installmentFrequency").setValidators([Validators.required]);
            this.chargeGroupForm.get("installmentFrequency").updateValueAndValidity();
            this.chargeGroupForm.get("totalInstallments").setValidators([Validators.required]);
            this.chargeGroupForm.get("totalInstallments").updateValueAndValidity();
        } else {
            this.chargeGroupForm.get("installmentFrequency").clearValidators();
            this.chargeGroupForm.get("installmentFrequency").updateValueAndValidity();
            this.chargeGroupForm.get("totalInstallments").clearValidators();
            this.chargeGroupForm.get("totalInstallments").updateValueAndValidity();
        }
    }
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    @ViewChild('chargeCustTable') chargeCustTable!: MatTable<any>;
    displayedColumnsCharge: string[] = [
        'connection_no',
        'chargeid',
        'actualprice',
        'staticIPAdrress',
        'planName',
        'expiryDate',
        'price',
        'discount',
        'delete'
    ];

}
