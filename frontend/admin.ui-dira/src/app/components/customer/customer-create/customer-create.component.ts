import { ADDRESS } from "./../../../RadiusUtils/RadiusConstants";
import { DatePipe, formatDate } from "@angular/common";
import { Component, ElementRef, Input, OnInit, TemplateRef, ViewChild } from "@angular/core";
import {
    AbstractControl,
    FormArray,
    FormBuilder,
    FormGroup,
    ValidationErrors,
    Validators
} from "@angular/forms";
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
import { ExternalItemManagementService } from "src/app/service/external-item-management.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { PaymentAmountModelComponent } from "src/app/components/payment-amount-model/payment-amount-model.component";
import { WorkflowAuditDetailsModalComponent } from "src/app/components/workflow-audit-details-modal/workflow-audit-details-modal.component";
import { CustomerplanGroupDetailsModalComponent } from "src/app/components/customerplan-group-details-modal/customerplan-group-details-modal.component";
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
import { ToastrService } from 'ngx-toastr';

import { CountryManagementService } from "src/app/service/country-management.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { DeactivateService } from "src/app/service/deactivate.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { LocationService } from "src/app/service/location.service";
import { Console, error } from "console";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { AreaManagementService } from "src/app/service/area-management.service";
import { BuildingManagementService } from "src/app/service/building-management.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { MatStepper } from "@angular/material/stepper";
import { SelectBuildingDialogComponent } from "../../common/select-building-dialog/select-building-dialog.component";
import { SelectorDialogComponent } from "../../common/selector-dialog/selector-dialog.component";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { PageEvent } from "@angular/material/paginator";
import { CustomerSelectComponent } from "../../customer-select/customer-select.component";

declare var $: any;

@Component({
    selector: "app-customer-create",
    templateUrl: "./customer-create.component.html",
    styleUrls: ["./customer-create.component.scss"],
    standalone: false
})
export class CustomerCreateComponent implements OnInit {
    customerVrn = RadiusConstants.CUSTOMER_VRN;
    customerNid = RadiusConstants.CUSTOMER_NID;
    custType: any;
    editCustId: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    AclClassConstants;
    AclConstants;
    custData: any = {};
    custLocationData: any = [];
    dateOfBirth: String;
    customerGroupForm: FormGroup;
    planCategoryForm: FormGroup;
    planGroupForm: FormGroup;
    presentGroupForm: FormGroup;
    paymentGroupForm: FormGroup;
    permanentGroupForm: FormGroup;
    planDataForm: FormGroup;
    chargeGroupForm: FormGroup;
    validityUnitFormGroup: FormGroup;
    payMappingListFromArray: FormArray;
    addressListFromArray: FormArray;
    paymentDetailsFromArray: FormArray;
    overChargeListFromArray: FormArray;
    custMacMapppingListFromArray: FormArray;
    ipMapppingListFromArray: FormArray;
    plansArray: FormArray;
    validityUnitFormArray: FormArray;
    locationDataByPlan: any = [];
    iscustomerEdit = false;
    submitted = false;
    showPassword = false;
    calTypwDisable = false;
    isCustSubTypeCon = false;
    showParentCustomerModel = false;
    serviceareaCheck = true;
    serviceAreaDisable = false;
    parentFieldEnable = false;
    isBranchAvailable = false;
    isParantExpirenceEdit = false;
    selectAreaList = false;
    selectPincodeList = false;
    selectPincodeListPermanent = false;
    selectPincodeListPayment = false;
    iflocationFill = false;
    ifsearchLocationModal = false;
    ifPlanGroup = false;
    ifcustomerDiscountField = false;
    ifplanisSubisuSelect = false;
    ifIndividualPlan = true;
    isTrialCheckDisable = false;
    plansubmitted = false;
    ifdiscounAllow = true;
    isSerialNumberShow: boolean = false;
    serialNumber: any;
    isProductRequired = false;
    parentBillday: number;
    KraTitle = RadiusConstants.KRA_PIN;

    currentPagesearchLocationList = 1;
    planTotalOffetPrice = 0;
    discountValue: any = 0;

    _passwordType = "password";
    department = RadiusConstants.DEPARMENT;
    countries: any = countries;
    pincodeTitle = PINCODE;
    addressTitle = ADDRESS;
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    areaTitle = AREA;
    subareaTitle = RadiusConstants.SUBAREA;
    buildingTitle = RadiusConstants.BUILDING;
    selectBuildingDialogRef: MatDialogRef<SelectBuildingDialogComponent>;
    areaId;

    serviceAreaSelectorDialogRef: MatDialogRef<SelectorDialogComponent>;
    selectedServiceAreaName: string = "";

    paymappingItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    payMappinftotalRecords = 0;
    currentPagePayMapping = 1;

    dunningRules: any;
    serviceAreaData: any;
    selectedParentCustId: any;
    departmentListData: any;
    planByServiceArea: any;
    planByPartner: any;
    areaDetails: any;
    pincodeDeatils: any;
    paymentareaDetails: any;
    permanentareaDetails: any;
    planGroupSelectedSubisu: any;
    planGroupSelected: any;
    plantypaSelectData: any;
    filterPlan: any;
    areaAvailableList: any;
    newSubisuPrice: any;
    finalOfferPrice: number;

    inputMobile = "";
    inputMobileSec = "";
    extendDays: any = "";
    trailbtnTypeSelect = "";
    customerSector = "";
    parentCustomerDialogType = "";
    customerSelectType = "";
    ipSubmitted = false;
    customerType: any[];
    customerSubType: any[];
    days = [];
    earlydays = [];
    staffList = [];
    parentCustList = [];
    billableCustList = [];
    pincodeDD: any = [];
    areaListDD: any;
    selectedParentCust: any = [];
    partnerListByServiceArea: any = [];
    serviceData: any = [];
    branchData: any = [];
    filterPlanData: any = [];
    planDropdownInChageData = [];
    discountValueStore: any = [];
    filterNormalPlanGroup = [];
    filterPartnerPlanGroup = [];
    isPartnerSelected: boolean = false;
    isMobileNumberFocus = false;
    planListSubisu = [];
    planIds = [];
    planGroupMapingList: any = [];
    oltDevices = [];
    spliterDevices = [];
    masterDbDevices = [];

    dateTime = new Date();

    createcustomerData: CustomerManagements;
    searchLocationForm: FormGroup;
    ipManagementGroup: FormGroup;

    //   selectTitile = [
    //     { label: "Mr" },
    //     { label: "Ms" },
    //     { label: "Mrs" },
    //     { label: "Miss" },
    //     { label: "M/S" },
    //     { label: "Dear" }
    //   ];
    celendarTypeData = [{ label: "English" }, { label: "Nepali" }];
    planDetailsCategory = [
        { label: "Individual", value: "individual" },
        { label: "Plan Group", value: "groupPlan" }
    ];
    customerTypeValue = [
        { label: "Customer", value: "customer" },
        { label: "Organization", value: "organization" }
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
    invoiceData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];

    quotaSharableData = [
        { label: "shareable", value: "shareable" },
        { label: "individual", value: "individual" }
    ];
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    discountType: any = "One-time";
    serviceAreaList: any = [];
    defualtServiceArea: any = [];
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    showLocationMac: boolean = false;
    locationMacForm: FormGroup;
    planMappingDataSource = new MatTableDataSource<AbstractControl>();
    overLocationMacArray = this.fb.array([]);
    macData: any = [];
    locationMacData: any = [];
    searchLocationData: any;
    searchLocationItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    searchLocationtotalRecords: String;
    billToData: any = [];
    isInvoiceTypeAlreadySelected: boolean = false;
    @ViewChild("closebutton") closebutton;
    customerMacCount: number = 0;
    selectMacRetentionUnit: any = [
        { label: "Hours", value: "HOURS" },
        { label: "Days", value: "DAY" }
    ];
    blockNoOptions: number[];
    isMobileAndEmailRequired: boolean = true;
    subAreaListDD: any[];
    buildingListDD: any[];
    buildingNoDD: any[];
    selectedMappingFrom: any;
    isCredentialMatch: boolean = false;
    demographicLabel: any;
    showLoginPassword = false;
    _loginPasswordType = "password";
    currency: any;
    servicePlanId: any;
    displayedColumns: string[] = [];
    constructor(
        private toastr: ToastrService,

        private fb: FormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService,
        public datepipe: DatePipe,
        public loginService: LoginService,
        public invoicePaymentListService: InvoicePaymentListService,
        private datePipe: DatePipe,
        private route: ActivatedRoute,
        private router: Router,
        private networkdeviceService: NetworkdeviceService,
        private liveUserService: LiveUserService,
        private countryManagementService: CountryManagementService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private deactivateService: DeactivateService,
        private locationService: LocationService,
        public statusCheckService: StatusCheckService,
        private systemService: SystemconfigService,
        private pincodeManagementService: PincodeManagementService,
        private areaManagementService: AreaManagementService,
        private buildingMangementService: BuildingManagementService,
        private dialog: MatDialog,
    ) {
        this.custType = this.route.snapshot.paramMap.get("custType")!;
        this.editCustId = this.route.snapshot.paramMap.get("custId")!;
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    async ngOnInit() {
        this.demographicLabel = RadiusConstants.DEMOGRAPHICDATA || [];
        if (this.editCustId != null) {
            this.iscustomerEdit = true;
        }
        let staffID = localStorage.getItem("userId");
        let loggedInUser = localStorage.getItem("loggedInUser");
        this.partnerId = Number(localStorage.getItem("partnerId"));
        if (this.custType == "Postpaid") {
            this.planDetailsCategory = this.planDetailsCategory.filter(cat => cat.value != "groupPlan");
        }
        this.initData();
        this.createStepGroups();
        this.updateDisplayedColumns();
        this.planMappingDataSource.data = this.payMappingListFromArray.controls;
        // const url = "/getlocationbyplanid";
        this.locationService.getAllActiveLocation().subscribe((response: any) => {
            this.locationDataByPlan = response.locationMasterList.map(location => ({
                name: location.name,
                locationMasterId: location.locationMasterId
            }));

        });
        const today = new Date();
        this.dateOfBirth = today.toISOString().split("T")[0];
    }

    openSelectorDialogComponent() {
        this.serviceAreaSelectorDialogRef = this.dialog?.open(SelectorDialogComponent, {
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
            this.customerGroupForm?.get("serviceAreaName").setValue(res?.name);
            this.selectedServiceAreaName = res?.name;
            this.planGroupForm.get("serviceId").setValue("");
            this.planGroupForm.get("planId").setValue("");
            this.selServiceArea({ value: res?.id }, true);
        })
    }

    canExit() {
        if (!this.customerGroupForm.dirty || this.customerGroupForm.pristine) {
            return true;
        }

        const hasActualData = this.checkIfFormHasData();

        if (!hasActualData) {
            return true;
        }

        return Observable.create((observer: Observer<boolean>) => {

            const dialogRef2 = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
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
                    observer.next(true);
                    observer.complete();
                } else {
                    observer.next(false);
                    observer.complete();
                }
            });
        });
    }

    checkIfFormHasData(): boolean {
        const formValue = this.customerGroupForm.value;

        // Check if critical fields are filled
        const hasCriticalData =
            (formValue.username && formValue.username.trim() !== '') ||
            (formValue.firstname && formValue.firstname.trim() !== '') ||
            (formValue.email && formValue.email.trim() !== '') ||
            (formValue.mobile && formValue.mobile.trim() !== '') ||
            (this.payMappingListFromArray && this.payMappingListFromArray.length > 0);

        return hasCriticalData;
    }
    getMappingFrom() {
        const url = "/buildingRefrence/all";
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                let dunningData = response.dataList;
                if (dunningData?.length > 0) {
                    this.selectedMappingFrom = dunningData[0].mappingFrom;
                } else {
                    //   this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "Please Select First Building Reference Management.",
                    //     icon: "far fa-times-circle"
                    //   });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


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
        this.areaListDD = [];
        const url = "/area/all";
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.areaListDD = response.dataList;
            },
            (error: any) => { }
        );
    }
    // getDisplayedPlanDetailsColumns(): Array<string> {
    //     if (this.customerGroupForm.value.billTo !== 'ORGANIZATION' &&
    //         this.planGroupForm.value.discountType === 'Recurring') {
    //         return ['service', 'plan', 'validity', 'd-type', 'discount', 'discountExpiryDate', 'action'];
    //     } else {
    //         return ['service', 'plan', 'validity', 'd-type', 'discount', 'action'];
    //     }
    // }
    getDisplayedPlanDetailsColumns(): string[] {
        const columns: string[] = ['service', 'plan'];

        if (

            // this.planCategoryForm.value.planCategory === 'individual'
            this.customerGroupForm.get("parentCustomerId").value &&
            this.planCategoryForm.get('planCategory').value === 'individual'
        ) {
            columns.push('invoiceType');
        }

        if (this.custType?.toLowerCase() === 'prepaid') {
            columns.push('validity');
        }

        if (this.customerGroupForm.value.billTo !== 'ORGANIZATION') {
            columns.push('d-type', 'discount');

            if (this.planGroupForm.value.discountType === 'Recurring') {
                columns.push('discountExpiryDate');
            }
        }

        columns.push('action');
        return columns;
    }
    listView() {

        this.router.navigate(['/home/customer/list/' + this.custType])
    }
    setMobileLength(min: number, max: number) {
        this.mobileMinLength = min;
        this.mobileMaxLength = max;

        this.customerGroupForm.get("mobile")?.setValidators([
            Validators.required,
            Validators.minLength(min),
            Validators.maxLength(max)
        ]);

        this.customerGroupForm.get("altmobile")?.setValidators([
            Validators.minLength(min),
            Validators.maxLength(max)
        ]);

        this.customerGroupForm.get("mobile")?.updateValueAndValidity();
        this.customerGroupForm.get("altmobile")?.updateValueAndValidity();

    }
    initData() {
        this.getAllPinCodeData();
        this.getALLAreaData();
        this.getMappingFrom();
        this.searchLocationForm = this.fb.group({
            searchLocationname: ["", Validators.required]
        });

        this.planCategoryForm = this.fb.group({
            planCategory: ["individual"]
        });
        this.planGroupForm = this.fb.group({
            planId: ["", Validators.required],
            service: ["", Validators.required],
            serviceId: ["", Validators.required],
            validity: ["", Validators.required],
            offerprice: [""],
            newAmount: [""],
            discountType: [""],
            discountExpiryDate: [""],
            discount: ["", [Validators.min(-99), Validators.max(99)]],
            istrialplan: [false],
            serialNumber: [""],
            invoiceType: ["", Validators.required],
            skipQuotaUpdate: [false],
            currency: [""],
            plangroupid: ['']
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

        this.presentGroupForm = this.fb.group({
            latitude: [""],
            longitude: [""],
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
            landmark1: [""]
        });
        this.paymentGroupForm = this.fb.group({
            addressType: ["", Validators.required],
            landmark: [""],
            areaId: [""],
            pincodeId: [""],
            cityId: [""],
            stateId: [""],
            countryId: [""],
            subareaId: [""],
            building_mgmt_id: [""],
            buildingNumber: [""],
            landmark1: [""]
        });
        this.permanentGroupForm = this.fb.group({
            addressType: [""],
            landmark: [""],
            areaId: [""],
            pincodeId: [""],
            cityId: [""],
            stateId: [""],
            countryId: [""],
            subareaId: [""],
            building_mgmt_id: [""],
            buildingNumber: [""],
            landmark1: [""]
        });
        this.planDataForm = this.fb.group({
            offerPrice: [""],
            discountPrice: [0],
            serialNumber: [""]
        });
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
            serialNumber: [""],
            expiry: ["", Validators.required]
        });
        this.ipManagementGroup = this.fb.group({
            ipAddress: [
                "",
                [Validators.required, Validators.pattern("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")]
            ],
            ipType: ["", Validators.required]
        });
        this.customerGroupForm = this.fb.group({
            username: ["", Validators.required],
            password: ["", [Validators.required, this.noSpaceValidator]],
            firstname: ["", Validators.required],
            lastname: ["", Validators.required],
            email: ["", [Validators.email]],
            title: [""],
            pan: [""],
            gst: [""],
            aadhar: [""],
            passportNo: [""],
            tinNo: ["", [Validators.minLength(9), Validators.maxLength(9)]],
            contactperson: ["", Validators.required],
            failcount: ["0"],
            //   acctno: [true, Validators.required],
            custtype: [this.custType],
            custlabel: ["customer"],
            //   phone: [""],
            mobile: [""],
            altmobile: [""],
            //   fax: [""],
            birthDate: [""],
            countryCode: [this.commondropdownService.commonBefaultCountryCode],
            customerType: ["Customer"],
            //   customerSubType: [""],
            //   customerSector: [""],
            //   customerSubSector: [""],
            //   cafno: [""],
            voicesrvtype: [""],
            didno: [""],
            calendarType: ["English", Validators.required],
            partnerid: [this.partnerId],
            //   salesremark: [""],
            servicetype: [""],
            serviceareaid: ["", Validators.required],
            serviceAreaName: [""],
            status: ["Active", Validators.required],
            parentCustomerId: [""],
            invoiceType: ["", Validators.required],
            parentExperience: ["Actual", Validators.required],
            locations: [],
            //   id: [],
            billTo: ["CUSTOMER"],
            billableCustomerId: [""],
            isInvoiceToOrg: [false],
            istrialplan: [false],
            popid: [""],
            //   staffId: [""],
            discount: ["", [Validators.min(-99), Validators.max(99)]],
            flatAmount: [""],
            plangroupid: [""],
            discountType: [""],
            discountExpiryDate: [""],
            planMappingList: (this.payMappingListFromArray = this.fb.array([], Validators.minLength(1))),
            addressList: (this.addressListFromArray = this.fb.array([])),
            overChargeList: (this.overChargeListFromArray = this.fb.array([])),
            custMacMapppingList: (this.custMacMapppingListFromArray = this.fb.array([])),
            custIpMappingList: (this.ipMapppingListFromArray = this.fb.array([])),
            branch: [""],
            oltid: [""],
            masterdbid: [""],
            splitterid: [""],
            //   framedIpBind: [""],
            //   ipPoolNameBind: [""],
            //   valleyType: [""],
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
            departmentId: [""],
            parentQuotaType: [""],
            isParentLocation: [""],
            //   framedIpv6Address: [""],
            //   vlan_id: [""],
            //   nasIpAddress: [""],
            //   nasPort: [""],
            //   nasPortId: [""],
            //   framedIp: [""],
            //   maxconcurrentsession: [""],
            earlybillday: [""],
            //   delegatedprefix: [""],
            //   framedroute: [""],
            mac_provision: [true],
            mac_auth_enable: [true],
            addparam1: [""],
            addparam2: [""],
            addparam3: [""],
            addparam4: [""],
            earlybilldate: [""],
            //   framedIPNetmask: [""],
            //   framedIPv6Prefix: [""],
            //   gatewayIP: [""],
            //   primaryDNS: [""],
            //   primaryIPv6DNS: [""],
            //   secondaryIPv6DNS: [""],
            //   secondaryDNS: [""],
            macRetentionPeriod: [""],
            macRetentionUnit: [""],
            skipQuotaUpdate: [false],
            blockNo: [""],
            //   drivingLicence: [""],
            customerVrn: [""],
            customerNid: [""],
            isEmailAndMobileRequired: [true],
            renewPlanLimit: [""],
            //   graceDay: [{ value: 0, disabled: this.iscustomerEdit }, [Validators.max(30)]],
            //   graceDay: [""],
            isCredentialMatchWithAccountNo: [true],
            loginUsername: ["", Validators.required],
            loginPassword: ["", [Validators.required, this.noSpaceValidator]],
            currency: [""]
        });

        this.customerGroupForm.get('countryCode')?.valueChanges.subscribe(code => {
            if (code === "+91") {
                this.setMobileLength(10, 10);
            }
        })

        this.isCredentialMatch = true;
        this.isCredentialMatchWithAccountNumber(true);

        if (this.custType == "Postpaid") {
            this.customerGroupForm.controls.billday.setValidators(Validators.required);
            this.customerGroupForm.updateValueAndValidity();
            this.customerGroupForm.controls.earlybillday.setValidators(Validators.required);
            this.customerGroupForm.updateValueAndValidity();
        }

        this.locationMacForm = this.fb.group({
            location: [""],
            mac: [""]
        });

        this.validityUnitFormGroup = this.fb.group({
            validityUnit: [""]
        });
        this.validityUnitFormArray = this.fb.array([]);
        this.plansArray = this.fb.array([]);

        this.customerGroupForm.controls.invoiceType.disable();
        this.customerGroupForm.controls.parentExperience.disable();
        if (this.custType === RadiusConstants.CUSTOMER_TYPE.POSTPAID) {
            this.daySequence();
            this.earlyDaySequence();
        }
        if (!this.iscustomerEdit) {
            // this.presentGroupForm.get('countryId').disable();
            // this.presentGroupForm.get('stateId').disable();
            // this.presentGroupForm.get('cityId').disable();

            this.permanentGroupForm.get('countryId').disable();
            this.permanentGroupForm.get('stateId').disable();
            this.permanentGroupForm.get('cityId').disable();

            this.paymentGroupForm.get('countryId').disable();
            this.paymentGroupForm.get('stateId').disable();
            this.paymentGroupForm.get('cityId').disable();
        }

        this.makeEmailAndMobileMandatoryOrNot();
        const serviceArea = localStorage.getItem("serviceArea");
        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        // this.commondropdownService.getAllPinCodeNumber();
        this.commondropdownService.getAllPinCodeData();
        // this.commondropdownService.getALLAreaData();
        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        // this.commondropdownService.getValleyTypee();
        this.commondropdownService.getInsideValley();
        this.commondropdownService.getOutsideValley();
        // this.commondropdownService.getBillToData();
        this.commondropdownService.getplanservice();
        // this.setDefualtServiceArea();
        // this.getsystemconfigListByName("DUNNING_CATEGORY");
        this.commondropdownService.getCustomerCategory();
        this.commondropdownService.getsystemconfigList();
        // this.getCustomerType();
        // this.getCustomerSector();
        this.getDepartmentList();
        this.getBillToData();

        if (this.statusCheckService.isActiveInventoryService) {
            this.commondropdownService.getPOPList();
        }
        this.commondropdownService.findAllplanGroups();
        this.commondropdownService.getPostpaidplanData();
        this.getNetworkDevicesByType("OLT");
        this.getNetworkDevicesByType("Splitter");
        this.getNetworkDevicesByType("Master DB/DB");
        if (this.editCustId != null) {
            this.iscustomerEdit = true;
            this.editCustomer();
            this.getCustomerMacCount();
        }
        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.panNumberLength$.subscribe(panLength => {
            if (panLength) {
                this.customerGroupForm
                    .get("pan")
                    ?.setValidators([Validators.minLength(panLength), Validators.maxLength(panLength)]);
                this.customerGroupForm.get("pan")?.updateValueAndValidity();
            }
        });
        this.commondropdownService.mobileNumberLengthSubject$.subscribe(len => {
            if (len) {
                this.customerGroupForm
                    .get("mobile")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(len.min),
                        Validators.maxLength(len.max)
                    ]);
                this.customerGroupForm
                    .get("altmobile")
                    ?.setValidators([Validators.minLength(len.min), Validators.maxLength(len.max)]);
                this.customerGroupForm.get("mobile")?.updateValueAndValidity();
                this.customerGroupForm.get("altmobile")?.updateValueAndValidity();
            }
        });
        this.customerGroupForm.get('countryCode')?.valueChanges.subscribe(code => {
            if (code === "+91") {
                // this.setMobileLength(9, 9);
                this.customerGroupForm.get("mobile")?.setValidators([
                    Validators.required,
                    Validators.minLength(10),
                    Validators.maxLength(10)
                ]);
            }
            this.customerGroupForm.get("mobile")?.updateValueAndValidity();
        })
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
        this.systemService.getConfigurationByName("DEFAULT_CUSTOMER_CATEGORY").subscribe((res: any) => {
            if (res?.data?.value) {
                this.customerGroupForm.controls.dunningCategory.setValue(res?.data?.value);
            }
        });
    }
    //   = 9;
    mobileMinLength = 9;
    mobileMaxLength = 9;

    step1Group: FormGroup;
    step2Group: FormGroup;
    step3Group: FormGroup;
    step4Group: FormGroup;
    createStepGroups() {
        // Step 1: Basic Details - Reference controls from customerGroupForm
        this.step1Group = this.fb.group({
            firstname: this.customerGroupForm.get('firstname'),
            lastname: this.customerGroupForm.get('lastname'),
            loginUsername: this.customerGroupForm.get('loginUsername'),
            loginPassword: this.customerGroupForm.get('loginPassword'),
            mobile: this.customerGroupForm.get('mobile'),
            contactperson: this.customerGroupForm.get('contactperson'),
            calendarType: this.customerGroupForm.get('calendarType'),
            dunningCategory: this.customerGroupForm.get('dunningCategory'),
            status: this.customerGroupForm.get('status'),
            customerType: this.customerGroupForm.get('customerType'),
            billday: this.customerGroupForm.get('billday')
        });
        this.step1Group?.get("customerType")?.setValue("Customer");
        this.customerGroupForm?.get("customerType")?.setValue("Customer")

        // Step 2: Service Area Details
        this.step2Group = this.fb.group({
            serviceareaid: this.customerGroupForm.get('serviceareaid'),
            landmark: this.presentGroupForm.get('landmark'),
            pincodeId: this.presentGroupForm.get('pincodeId'),
            areaId: this.presentGroupForm.get('areaId'),
            cityId: this.presentGroupForm.get('cityId'),
            stateId: this.presentGroupForm.get('stateId'),
            countryId: this.presentGroupForm.get('countryId')
        });

        // Add branch or partner depending on availability
        if (this.isBranchAvailable) {
            this.step2Group.addControl('branch', this.customerGroupForm.get('branch')!);
        } else {
            this.step2Group.addControl('partnerid', this.customerGroupForm.get('partnerid')!);
        }

        // Add blockNo if needed
        if (this.serviceAreaData?.serviceAreaType === 'private' && !this.iscustomerEdit) {
            this.step2Group.addControl('blockNo', this.customerGroupForm.get('blockNo')!);
        }


        // Create new group
        // this.step2Group = this.fb.group(step2Controls);



        // Step 3: Plan Details 
        this.updateStep3Group();

        // Step 4: Payment Details (Optional)
        this.step4Group = this.fb.group({
            addparam1: this.customerGroupForm.get('addparam1'),
            addparam2: this.customerGroupForm.get('addparam2'),
            addparam3: this.customerGroupForm.get('addparam3'),
            addparam4: this.customerGroupForm.get('addparam4')
        });
    }
    updateStep3Group() {
        const step3Controls: any = {
            billTo: this.customerGroupForm.get('billTo')
        };

        if (this.planCategoryForm) {
            step3Controls.planCategory = this.planCategoryForm.get('planCategory');
        }

        // Get the controls we need to toggle
        const planListArray = this.payMappingListFromArray;
        const planGroupControl = this.customerGroupForm.get('plangroupid');

        if (this.ifIndividualPlan) {
            // --- THIS IS THE FIX ---
            // 1. ALWAYS add the FormArray to this step's controls.
            //    This REMOVES the 'else' block that added the temporary fields.
            step3Controls.planMappingList = planListArray;

            // 2. Set validation: The FormArray must have at least 1 item.
            planListArray.setValidators(Validators.minLength(1));

            // 3. Clear validation for the *other* plan type (Plan Group).
            planGroupControl?.clearValidators();

        } else if (this.ifPlanGroup) {
            // This is for your "Plan Group" logic
            // 1. Add the plan group control to this step.
            step3Controls.plangroupid = planGroupControl;

            // 2. Set validation: The plan group dropdown is required.
            planGroupControl?.setValidators(Validators.required);

            // 3. Clear validation for the *other* plan type (Individual Plan).
            planListArray.clearValidators();
        } else {
            // Fallback case: clear all validation
            step3Controls.serviceId = this.planGroupForm.get('serviceId');
            step3Controls.planId = this.planGroupForm.get('planId');
            planListArray.clearValidators();
            planGroupControl?.clearValidators();
        }

        // Update validity on the controls themselves *before* creating the group
        planListArray.updateValueAndValidity();
        planGroupControl?.updateValueAndValidity();

        // Re-create the step3Group with the correct, validated controls
        this.step3Group = this.fb.group(step3Controls);
    }
    updateDisplayedColumns() {
        this.displayedColumns = [];

        if (this.isSerialNumberShow) {
            this.displayedColumns.push('serialNumber');
        }

        this.displayedColumns.push('service', 'plan', 'validity', 'currency');

        if (this.customerGroupForm.value.billTo == 'ORGANIZATION') {
            this.displayedColumns.push('offerPrice', 'newAmount');
        } else {
            this.displayedColumns.push('discountType', 'discount', 'discountExpiryDate');
        }

        if (this.customerGroupForm.value.parentCustomerId) {
            this.displayedColumns.push('invoiceType');
        }

        // if (this.customerGroupForm.value.billTo !== 'ORGANIZATION') {
        //     this.displayedColumns.push('trialPlan');
        // }

        if (!this.iscustomerEdit) {
            this.displayedColumns.push('delete');
        }
    }
    onNext = (stepper: MatStepper) => {

        if (this.step3Group?.valid && this.planDropdownInChageData.length > 0) {
            stepper.next();
        }
    }

    openSelectBuildingDialogComponent = (): void => {
        this.selectBuildingDialogRef = this.dialog?.open(SelectBuildingDialogComponent, {
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

    onSubmitCustomer() {
        this.checkUsernme(null);
    }

    updateCustomer() {
        this.checkUsernme(this.editCustId);
    }
    ipListFormGroup(): FormGroup {
        return this.fb.group({
            ipAddress: [this.ipManagementGroup.value.ipAddress],
            ipType: [this.ipManagementGroup.value.ipType]
        });
    }
    noSpaceValidator(control: AbstractControl): ValidationErrors | null {
        if (control.value && control.value.includes(" ")) {
            return { noSpace: true };
        }
        return null;
    }
    onAddIPList() {
        this.ipSubmitted = true;
        if (this.ipManagementGroup.valid) {
            this.ipMapppingListFromArray.push(this.ipListFormGroup());
            this.ipManagementGroup.reset();
            this.ipSubmitted = false;
        } else {
        }
    }
    markStepGroupsAsValid() {
        // Mark step1Group controls as touched and valid
        if (this.step1Group) {
            Object.keys(this.step1Group.controls).forEach(key => {
                const control = this.step1Group.get(key);
                control?.markAsTouched();
                control?.updateValueAndValidity();
            });
            this.step1Group.updateValueAndValidity();
        }

        // Mark step2Group controls as touched and valid
        if (this.step2Group) {
            Object.keys(this.step2Group.controls).forEach(key => {
                const control = this.step2Group.get(key);
                control?.markAsTouched();
                control?.updateValueAndValidity();
            });
            this.step2Group.updateValueAndValidity();
        }

        // Mark step3Group controls as touched and valid
        if (this.step3Group) {
            Object.keys(this.step3Group.controls).forEach(key => {
                const control = this.step3Group.get(key);
                control?.markAsTouched();
                control?.updateValueAndValidity();
            });
            this.step3Group.updateValueAndValidity();
        }

        // Mark step4Group controls as touched and valid
        if (this.step4Group) {
            Object.keys(this.step4Group.controls).forEach(key => {
                const control = this.step4Group.get(key);
                control?.markAsTouched();
                control?.updateValueAndValidity();
            });
            this.step4Group.updateValueAndValidity();
        }
    }
    editCustomer() {
        this.customerMacCount = 0;
        const url = "/customers/" + this.editCustId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.iscustomerEdit = true;
                this.custData = response.customers;
                // 1. First patch Service Area and trigger its change
                if (this.custData.serviceareaid) {
                    this.customerGroupForm.patchValue({
                        serviceareaid: this.custData.serviceareaid
                    });

                    this.customerGroupForm.patchValue({
                        serviceAreaName: this.custData.serviceareaName
                    });

                    // Trigger service area change to load dependent dropdowns
                    const serviceAreaEvent = { value: this.custData.serviceareaid };
                    this.selServiceArea(serviceAreaEvent, false); // This loads branch, road, fat, olt, etc.
                }

                // 2. Wait for dropdowns to load, then patch presentGroupForm
                setTimeout(() => {
                    // Patch Branch/Partner
                    if (this.isBranchAvailable && this.custData.branchId) {
                        this.customerGroupForm.patchValue({
                            branch: this.custData.branchId
                        });
                    } else if (this.custData.partnerId) {
                        this.customerGroupForm.patchValue({
                            partnerid: this.custData.partnerId
                        });
                    }

                    // Patch Address fields from addressList[0]
                    if (this.custData?.addressList && this.custData.addressList.length > 0) {
                        const presentAddress = this.custData.addressList[0];

                        // Load dependent data first
                        if (presentAddress.pincodeId) {
                            this.getTempPincodeData(presentAddress.pincodeId, 'present');
                        }

                        if (presentAddress.areaId) {
                            this.getAreaData(presentAddress.areaId, 'present');
                        }

                        // Patch presentGroupForm with delay to allow dropdowns to populate
                        setTimeout(() => {

                            this.presentGroupForm.patchValue({
                                roadId: presentAddress.roadId,
                                fatId: presentAddress.fatId,
                                building_mgmt_id: presentAddress.building_mgmt_id,
                                homePassId: presentAddress.homePassId,
                                buildingNumber: presentAddress.buildingNumber,
                                oltId: presentAddress.oltId,
                                subCountyId: presentAddress.subCountyId,
                                countyId: presentAddress.countyId,
                                landmark: presentAddress.landmark || '',
                                landmark1: presentAddress.landmark1 || '',
                                latitude: presentAddress.latitude,
                                longitude: presentAddress.longitude
                            });

                            this.presentGroupForm.patchValue({
                                roadId: presentAddress.roadId,
                                fatId: presentAddress.fatId,
                                building_mgmt_id: presentAddress.building_mgmt_id,
                                homePassId: presentAddress.homePassId,
                                buildingNumber: presentAddress.buildingNumber,
                                oltId: presentAddress.oltId,
                                subCountyId: presentAddress.subCountyId,
                                countyId: presentAddress.countyId,
                                landmark: presentAddress.landmark || '',
                                landmark1: presentAddress.landmark1 || '',
                                latitude: presentAddress.latitude,
                                longitude: presentAddress.longitude
                            });

                            // Mark step groups as valid after patching
                            this.createStepGroups();
                            this.markStepGroupsAsValid();
                        }, 800);
                    }
                    if (this.custData.birthDate)
                        this.custData.birthDate = moment(this.custData.birthDate).format("YYYY-MM-DD");
                    this.customerGroupForm.patchValue(this.custData);
                    this.getBillableCust(this.custData.billableCustomerId);
                    let serviceAreaId = {
                        value: Number(this.custData.serviceareaid)
                    };
                    this.selServiceArea(serviceAreaId, false);

                    //this.customerGroupForm.controls.username.disable();

                    this.customerGroupForm.get("parentQuotaType").setValue(this.custData.parentQuotaType);
                    if (this.custData.isCredentialMatchWithAccountNo) {
                        this.customerGroupForm.controls.username.disable();
                        this.customerGroupForm.controls.isCredentialMatchWithAccountNo.disable();
                    } else {
                        this.customerGroupForm.controls.username.enable();
                    }

                    if (this.custData.customerLocations.length > 0) {
                        this.customerGroupForm
                            .get("isParentLocation")
                            .setValue(this.custData.customerLocations[0].isParentLocation);

                        var selectedLocation = [];
                        this.custLocationData = [];
                        this.custLocationData = [...this.custData.customerLocations];

                        this.custData.customerLocations.forEach(location => {
                            if (selectedLocation.indexOf(location.locationId) === -1) {
                                selectedLocation.push(location.locationId);
                            }

                            this.overLocationMacArray.push(
                                this.fb.group({
                                    name: location.locationName,
                                    mac: location.mac,
                                    locationId: location.locationId,
                                    isAlreadyAvailable: true,
                                    isParentLocation: location.isParentLocation
                                }) as any
                            );
                        });
                        if (this.overLocationMacArray.value.length > 0) {
                            this.locationMacData = this.overLocationMacArray.value.map((location: any) => ({
                                locationId: location.locationId, //location.locationId
                                mac: location.mac,
                                isParentLocation: location.isParentLocation
                            }));
                        }
                    }
                    this.locationChange(selectedLocation);
                    this.locationMacForm.get("location").setValue(selectedLocation);

                    if (this.custData.planMappingList && this.custData.planMappingList.length > 0) {
                        this.customerGroupForm.get("billTo").setValue(this.custData.planMappingList[0].billTo);
                        this.customerGroupForm
                            .get("isInvoiceToOrg")
                            .setValue(this.custData.planMappingList[0].isInvoiceToOrg);
                    }

                    this.customerGroupForm.get("isCustCaf").setValue("no");

                    this.custData.custtype;
                    if (this.custData.custtype == this.custType) {
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

                    if (this.custData.creditDocuments?.length > 0) {
                        this.customerGroupForm.controls.paymentDetails.patchValue(
                            this.custData.creditDocuments[0]
                        );
                    }
                    if (this.custData.parentCustomerId != null) {
                        this.isParantExpirenceEdit = true;
                        this.customerGroupForm.controls.parentExperience.enable();
                        this.customerGroupForm.controls.parentExperience.patchValue(
                            this.custData.parentExperience
                        );
                    } else {
                        this.customerGroupForm.controls.parentExperience.disable();
                    }

                    if (this.custData.parentCustomerId) {
                        this.parentCustList = [
                            {
                                id: this.custData.parentCustomerId,
                                name: this.custData.parentCustomerName
                            }
                        ];
                    } else {
                        this.parentCustList = [];
                    }

                    if (this.custData.parentCustomerId && this.custData.plangroupid) {
                        this.customerGroupForm.controls.invoiceType.enable();
                        this.planGroupForm.controls.invoiceType.disable();
                    } else {
                        this.customerGroupForm.controls.invoiceType.disable();
                        this.planGroupForm.controls.invoiceType.enable();
                    }

                    if (this.custData.plangroupid) {
                        this.ifIndividualPlan = false;
                        this.ifPlanGroup = true;
                        this.planCategoryForm.patchValue({
                            planCategory: "groupPlan"
                        });
                        this.getPlangroupByPlan(this.custData.plangroupid);
                        this.customerGroupForm.patchValue({
                            plangroupid: this.custData.plangroupid
                        });
                    } else {
                        this.ifIndividualPlan = true;
                        this.ifPlanGroup = false;

                        this.planCategoryForm.patchValue({
                            planCategory: "individual"
                        });

                        // plan deatils
                        this.payMappingListFromArray.clear();
                        this.validityUnitFormArray.clear();
                        this.planDropdownInChageData = [];
                        this.discountValueStore = [];
                        this.planTotalOffetPrice = 0;

                        let totalDiscountedPrice = 0;

                        this.custData.planMappingList.forEach((element: any, i: number) => {
                            const fullPlan = this.filterPlanData.find(p => p.id === element.planId);

                            // **FIX 1: Retrieve Plan details (or set sensible defaults)**
                            const planName = fullPlan ? fullPlan.name : 'N/A';
                            const unitsOfValidity = fullPlan ? fullPlan.unitsOfValidity : element.unitsOfValidity;
                            const finalCurrency = element.currency
                                ? element.currency
                                : (fullPlan?.currency || this.customerGroupForm.value.currency || 'N/A');
                            const planOfferPrice = fullPlan ? Number(fullPlan.offerprice) : Number(element.offerPrice);
                            // ----------------------------------------------------

                            const discountPercentage = element.discount ? Number(element.discount) : 0;
                            const finalPrice = Number(element.newAmount); // Using stored final price

                            totalDiscountedPrice += finalPrice;

                            // 2. Create Form Group for the FormArray
                            const planMappingGroup = this.fb.group({
                                service: [element.service],
                                planId: [element.planId],
                                name: [planName],
                                unitsOfValidity: [unitsOfValidity],
                                validity: [Number(element.validity)],
                                offerPrice: [planOfferPrice],
                                newAmount: [finalPrice],
                                discount: [element.discount ? element.discount : 0],
                                istrialplan: [element.istrialplan],
                                invoiceType: [element.invoiceType],
                                isInvoiceToOrg: [element.isInvoiceToOrg],
                                discountType: [element.discountType],
                                serialNumber: [element.serialNumber],
                                discountExpiryDate: [
                                    element.discountExpiryDate
                                        ? moment(element.discountExpiryDate).utc(true).toDate()
                                        : null
                                ],
                                skipQuotaUpdate: [element.skipQuotaUpdate],
                                currency: [finalCurrency],
                            });

                            this.payMappingListFromArray.push(planMappingGroup);

                            // 3. Populate validity unit array (needed for UI logic/display)
                            this.validityUnitFormArray.push(
                                this.fb.group({
                                    validityUnit: [unitsOfValidity]
                                })
                            );

                            // 4. Populate data for dropdown reference and discount logic
                            this.discountValueStore.push({ value: finalPrice });
                            this.planTotalOffetPrice += planOfferPrice;

                            const planDetail = this.filterPlanData.find(p => p.id === element.planId);
                            if (planDetail) {
                                this.planDropdownInChageData.push(planDetail);
                            } else {
                                // Fallback: Fetch full plan details explicitly if not pre-loaded
                                this.getChargeUsePlanList(element.planId);
                            }
                        });

                        // 5. Update summary form
                        this.planDataForm.patchValue({
                            offerPrice: this.planTotalOffetPrice, // Sum of original offer prices
                            discountPrice: Number(this.custData.flatAmount ? this.custData.flatAmount : 0).toFixed(2) // Total discounted amount/flat amount
                        });

                        // Update the MatTableDataSource
                        this.planMappingDataSource.data = this.payMappingListFromArray.controls;

                        // Update overall currency in main form if present
                        if (this.custData.planMappingList.length > 0 && this.payMappingListFromArray.value[0].currency) {
                            this.customerGroupForm.get("currency").setValue(this.payMappingListFromArray.value[0].currency);
                        }
                        // --- END FIX: PATCH INDIVIDUAL PLAN DATA ---
                    }

                    // Address
                    if (this.custData?.addressList[0]?.addressType) {
                        //   this.getTempPincodeData(this.custData.addressList[0].pincodeId, "present");
                        this.getAreaData(this.custData.addressList[0].areaId, "present");
                        this.presentGroupForm.patchValue(this.custData.addressList[0]);

                        this.selServiceAreaByParent(Number(this.custData.serviceareaid));
                        const data = {
                            value: Number(this.custData.addressList[0].pincodeId)
                        };
                        this.onChnagePincode(data, "");
                        this.presentGroupForm.patchValue({
                            pincodeId: Number(this.custData.addressList[0].pincodeId)
                        });
                    }
                    // if (this.viewcustomerListData.addressList != null) {
                    //   this.viewcustomerListData.addressList.forEach(element => {
                    //     // console.log("element", element);
                    //     if ("Payment" == element.addressType) {
                    //       this.getTempPincodeData(element.pincodeId, "payment");
                    //       this.getAreaData(element.areaId, "payment");
                    //       this.paymentGroupForm.patchValue(element);
                    //       this.selectAreaListPayment = true;
                    //       this.selectPincodeListPayment = true;
                    //     } else if ("Permanent" == element.addressType || "permanent" == element.addressType) {
                    //       this.getTempPincodeData(element.pincodeId, "permanent");
                    //       this.getAreaData(element.areaId, "permanent");
                    //       this.permanentGroupForm.patchValue(element);
                    //       this.selectAreaListPermanent = true;
                    //       this.selectPincodeListPermanent = true;
                    //     }
                    //   });
                    // }

                    this.custData.overChargeList = this.custData.indiChargeList;
                    // charge
                    let k = 0;
                    while (k < this.custData.indiChargeList.length) {
                        if (this.custData.indiChargeList[k].charge_date) {
                            const format = "yyyy-MM-dd";
                            const locale = "en-US";
                            const myDate = this.custData.indiChargeList[k].charge_date;
                            const formattedDate = formatDate(myDate, format, locale);
                            this.custData.indiChargeList[k].charge_date = formattedDate;

                            const date = this.custData.indiChargeList[k].charge_date.split("-");
                            // this.ngbBirthcal = {
                            //   year: Number(date[0]),
                            //   month: Number(date[1]),
                            //   day: Number(date[2]),
                            // };
                            this.overChargeListFromArray.patchValue([
                                {
                                    charge_date: this.custData.indiChargeList[k].charge_date
                                }
                            ]);
                            // console.log(this.viewcustomerListData.indiChargeList[k].charge_date)
                        }

                        this.chargeGroupForm.patchValue(this.custData.indiChargeList[k]);
                        // this.onAddoverChargeListField();

                        this.overChargeListFromArray.patchValue(this.custData.indiChargeList);
                        k++;
                    }

                    // MAc
                    let macNumber = 0;
                    // while (this.viewcustomerListData.custMacMapppingList.length > macNumber) {
                    //   this.macGroupForm.patchValue(this.viewcustomerListData.custMacMapppingList[macNumber]);
                    //   this.onAddMACList();
                    //   this.custMacMapppingListFromArray.patchValue(
                    //     this.viewcustomerListData.custMacMapppingList
                    //   );
                    //   macNumber++;
                    // }
                    this.selectAreaList = true;
                    this.selectPincodeList = true;
                    if (this.custData.customerType != null) {
                        const data = {
                            value: this.custData.customerType
                        };
                        this.customerGroupForm.controls.customerSubType.enable();
                    } else {
                        this.customerGroupForm.controls.customerSubType.disable();
                    }

                    if (this.custData.customerSector != null) {
                        this.customerGroupForm.controls.customerSubSector.enable();
                    } else {
                        this.customerGroupForm.controls.customerSubSector.disable();
                    }
                    this.createStepGroups();
                    this.markStepGroupsAsValid();
                }, 500);
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getTempPincodeData(id: any, index: any) {
        const url = "/pincode/" + id;

        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            if (index === "present") {
                this.pincodeDeatils = response.data;
                if (response.data.areaList.length !== 0) {
                    this.areaAvailableList = response.data.areaList;
                }
            }
            // if (index === "payment") {
            //   this.PyamentpincodeDeatils = response.data;
            //   if (response.data.areaList.length !== 0) {
            //     this.paymentareaAvailableList = response.data.areaList;
            //   }
            //
            // }
            // if (index === "permanent") {
            //   this.permanentpincodeDeatils = response.data;
            //   if (response.data.areaList.length !== 0) {
            //     this.permanentareaAvailableList = response.data.areaList;
            //   }
            //
            // }
        });
    }

    getBillableCust(billableCustomerId) {
        const url = "/customers/" + billableCustomerId;
        if (billableCustomerId) {
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
        this.customerGroupForm.patchValue({
            earlybillday: this.earlydays[0].label
        });
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

    //   getStaffUserByServiceArea(ids) {
    //     let data = [];
    //     data.push(ids);
    //     let url = "/staffsByServiceAreaId/" + ids;
    //     this.savbillCommonBaseService.get(url).subscribe((response: any) => {
    //       this.staffList = response.dataList;
    //     });
    //   }

    //   getCustomerType() {
    //     const url = "/commonList/Customer_Type";
    //     this.commondropdownService.getMethodWithCache(url).subscribe(
    //       (response: any) => {
    //         this.customerType = response.dataList;
    //         // console.log("this.customerType ::::: ", this.customerType);
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

    //   getcustType(event) {
    //     let value = event.value;
    //     this.customerGroupForm.controls.customerSubType.enable();
    //     let actionUrl = `/commonList/${value}`;
    //     if (event.value == "Barter") {
    //       this.isCustSubTypeCon = false;
    //     } else {
    //       this.isCustSubTypeCon = true;
    //       this.getCustomerTypeFlow(actionUrl);
    //     }
    //   }

    //   getCustomerTypeFlow(url) {
    //     this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
    //       this.customerSubType = response.dataList;
    //     });
    //   }

    //   getCustomerSector() {
    //     const url = "/commonList/Customer_Sector";
    //     const custerlist = {};
    //     this.commondropdownService.getMethodWithCache(url).subscribe(
    //       (response: any) => {
    //         this.customerSector = response.dataList;
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

    getDepartmentList() {
        const url = "/department/all";
        this.countryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.departmentListData = res.departmentList;
            },
            (err: any) => {
                this.toastr.error(`${err.error.ERROR}`, 'Something went wrong while fetching lead origin types!');


            }
        );
    }

    getNetworkDevicesByType(deviceType) {
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

    getSelectCustomerSector(event) {
        const value = event.value;
        if (value) {
            this.customerGroupForm.controls.customerSubSector.enable();
        } else {
            this.customerGroupForm.controls.customerSubSector.disable();
        }
    }
    onCustomerPageChange(event: PageEvent): void {
        this.currentPageParentCustomerListdata = event.pageIndex + 1;
        this.parentCustomerListdataitemsPerPage = event.pageSize;
        this.getParentCustomerList();
    }
    onSearchParentCustomer() {
        // Reset to the first page whenever the search query changes
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerList();
    }

    async selectedCustChange(customer: any) {
        this.selectedParentCust = customer;

        // Construct the full name for display in the input field
        const customerName = `${customer.title ? customer.title + ' ' : ''}${customer.firstname} ${customer.lastname}`;

        if (this.parentCustomerDialogType === "billable") {
            // Logic for Billable Customer (Step 3: Plan Details)
            this.billableCustList = [
                {
                    id: customer.id,
                    name: customerName // Use the formatted name
                }
            ];
            // Patch the main form control
            this.customerGroupForm.patchValue({
                billableCustomerId: customer.id
            });
            // Update step 3 group control (important for validation/state)
            this.step3Group.patchValue({
                billableCustomerId: customer.id
            });

        } else if (this.parentCustomerDialogType === "parent") {
            // Logic for Parent Customer (Step 1: Basic Details)
            this.parentCustList = [
                {
                    id: customer.id,
                    name: customerName // Use the formatted name
                }
            ];
            // Patch the main form control
            this.customerGroupForm.patchValue({
                parentCustomerId: customer.id
            });

            // Fetch parent customer's details to get service area ID and bill day
            const url = "/customers/" + customer.id;
            await this.customerManagementService.getMethod(url).subscribe((response: any) => {
                const parentCustData = response.customers;

                // Update properties used for validation/logic
                this.parentBillday = parentCustData.billday;
                this.serviceareaCheck = false;

                // Patch Service Area ID and Name (Step 2 controls)
                this.customerGroupForm.patchValue({
                    serviceareaid: parentCustData.serviceareaid,
                    serviceAreaName: parentCustData.serviceareaName
                });

                // Update Parent Experience and Invoice Type based on parent setup
                if (parentCustData.serviceareaid) {
                    this.selServiceAreaByParent(parentCustData.serviceareaid);
                    this.serviceAreaDisable = true; // Disable service area selection
                }

                // Enable and set parent experience to 'Actual' (or other default based on logic)
                this.customerGroupForm.controls.parentExperience.setValue("Actual");
                this.customerGroupForm.controls.parentExperience.enable();

                // Handle Invoice Type enablement based on Plan Category
                if (this.planCategoryForm.value.planCategory === "groupPlan") {
                    this.customerGroupForm.controls.invoiceType.enable();
                    this.planGroupForm.controls.invoiceType.disable();
                    if (this.customerGroupForm.value.parentExperience === "Single" || "Actual") {
                        this.customerGroupForm.patchValue({ invoiceType: "Group" });
                        this.planGroupForm.patchValue({ invoiceType: "Group" });
                    } else {
                        this.customerGroupForm.patchValue({ invoiceType: "" });
                    }
                } else if (this.planCategoryForm.value.planCategory === "individual") {
                    this.customerGroupForm.controls.invoiceType.enable();
                    this.planGroupForm.controls.invoiceType.enable();
                    if (this.customerGroupForm.value.parentExperience === "Single" || "Actual") {
                        this.planGroupForm.patchValue({ invoiceType: "Group" });
                        this.customerGroupForm.patchValue({ invoiceType: "Group" });
                    } else {
                        this.planGroupForm.patchValue({ invoiceType: "" });
                    }
                }
            });
        }
    }

    removeSelParentCust(type) {
        this.selectedParentCust = [];
        if (type === "billable") {
            this.billableCustList = [];
            this.customerGroupForm.patchValue({
                billableCustomerId: null
            });
        } else {
            this.customerGroupForm.patchValue({
                parentCustomerId: ""
            });
            this.customerGroupForm.controls.invoiceType.setValue("");
            this.customerGroupForm.controls.invoiceType.disable();
            this.planGroupForm.controls.invoiceType.setValue("");
            this.planGroupForm.controls.invoiceType.disable();
            this.customerGroupForm.controls.parentExperience.setValue("");
            this.customerGroupForm.controls.parentExperience.disable();
            this.customerGroupForm.controls.billday.setValue("");
            this.customerGroupForm.controls.billday.enable();
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
                    this.getPlanbyServiceArea(serviceAreaId);
                    this.getBranchByServiceAreaID(serviceAreaId);
                    //   this.getStaffUserByServiceArea(serviceAreaId);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    selServiceArea(event, isFromUI) {
        this.isPartnerSelected = false;
        if (isFromUI) {
            this.pincodeDD = [];
        }
        const serviceAreaId = event.value;
        this.planGroupForm.patchValue({
            service: "",
            planId: ""
        });

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
                    this.plantypaSelectData = [];
                    this.filterPlan = [];
                    if (isFromUI) {
                        this.serviceAreaData.pincodes.forEach(element => {
                            this.commondropdownService.allpincodeNumber.forEach(e => {
                                if (e.pincodeid == element) {
                                    this.pincodeDD.push(e);
                                }
                            });
                        });
                    }

                    this.getPlanbyServiceArea(serviceAreaId);
                    if (!this.iscustomerEdit) {
                        if (isFromUI) {
                            this.presentGroupForm.reset();
                        }
                    }
                },
                (error: any) => { }
            );
            this.getPartnerAllByServiceArea(serviceAreaId);
            this.getServiceByServiceAreaID(serviceAreaId);
            if (this.partnerId == 1) this.getBranchByServiceAreaID(serviceAreaId);
            //   this.getStaffUserByServiceArea(serviceAreaId);
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

    getPartnerAllByServiceArea(serviceAreaId) {
        const url = "/getPartnerByServiceAreaId/" + serviceAreaId;
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerListByServiceArea = response.partnerList.filter(item => item.id != 1);
            },
            (error: any) => { }
        );
    }

    getServiceByServiceAreaID(ids) {
        let data = [];
        data.push(ids);
        let url = "/serviceArea/getAllServicesByServiceAreaId";
        this.customerManagementService.postMethod(url, data).subscribe((response: any) => {
            this.serviceData = response.dataList;
        });
    }

    onPartnerCategoryChange(event: any) { }

    getPlanbyPartner(serviceAreaId, partnerId) {
        this.isPartnerSelected = true;
        if (serviceAreaId) {
            this.filterPlanData = [];
            const custType = this.custType;
            const url = `/partnerplans/serviceArea?planmode=NORMAL&serviceAreaId=${serviceAreaId}&partnerId=${partnerId}`;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planByServiceArea = response.partnerpostpaidplanList;
                    this.filterPlanData = response.partnerpostpaidplanList.filter(
                        plan => plan.plantype == custType
                    );
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

    getPlanbyServiceArea(serviceAreaId) {
        if (serviceAreaId) {
            this.filterPlanData = [];
            const custType = this.custType;
            const url = "/plans/serviceArea?planmode=NORMAL&serviceAreaId=" + serviceAreaId;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planByServiceArea = response.postpaidplanList;
                    this.filterPlanData = this.planByServiceArea.filter(plan => plan.plantype == custType);
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
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
                                        this.customerGroupForm.controls.serviceAreaName.setValue(res.data?.serviceAreaName);

                                        // this.getBranchByServiceAreaID(res.data);
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

                                    this.areaDetails = response.data;
                                    this.selectPincodeList = true;

                                    const patch = {
                                        addressType: "Present",
                                        areaId: Number(this.areaDetails.id),
                                        pincodeId: Number(this.areaDetails.pincodeId),
                                        cityId: Number(this.areaDetails.cityId),
                                        stateId: Number(this.areaDetails.stateId),
                                        countryId: Number(this.areaDetails.countryId)
                                    };

                                    this.presentGroupForm.patchValue(patch);
                                    this.step2Group.patchValue(patch); // ✅ Keep both forms in sync
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
            this.areaId = id;
            this.presentGroupForm?.get("subareaId").reset();
            this.presentGroupForm?.get("subareaName").reset();
        }

        // this.areaTitle
    }

    onKey(event) {
        if (event.key == "Tab") {
            const url =
                "/customer/customerUsernameIsAlreadyExists/" +
                this.customerGroupForm.controls.username.value;
            this.customerManagementService.getMethod(url).subscribe((response: any) => {
                if (response.isAlreadyExists == true) {
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Username already exists!!!');
                    }


                }
            });
        }
    }

    onKeymobilelength(event) {
        const str = this.customerGroupForm.value.mobile.toString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
        if (mobilenumberlength === 0 || mobilenumberlength === null) {
            mobilenumberlength = 10;
        }
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobile = `${mobilenumberlength} character required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobile = "";
        } else {
            this.inputMobile = `${mobilenumberlength} character required.`;
        }
    }

    mobileError: boolean = false;

    onInputMobile(event: any) {
        this.checkMobileLength();
        const inputElement = event.target as HTMLInputElement;
        const inputValue = inputElement.value;

        // Check if the input starts with 0
        if (inputValue.startsWith("0")) {
            this.mobileError = true;
        } else {
            this.mobileError = false;
        }
    }

    onKeymobilelengthsec(event) {
        const str = this.customerGroupForm.value.altmobile.toLocaleString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobileSec = `Mobile Number minimum ${mobilenumberlength} character is required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobileSec = "";
        } else {
            this.inputMobileSec = `Mobile Number minimum ${mobilenumberlength} character is required.`;
        }
    }

    discountvaluesetPercentage(event: KeyboardEvent) {
        const inputElement = event.target as HTMLInputElement;
        if (Number(inputElement.value) > 0) {
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
            return;
        }
    }

    preventNegativeInput(event: KeyboardEvent) {
        if (event.key === "-") {
            event.preventDefault();
        }
    }

    mylocation() {
        //
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
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

    parentExperienceSelect(e) {
        this.planGroupForm.value.invoiceType = "Group";
    }

    onChnagePincode(_event: any, index: any) {
        if (_event.value) {
            const url = "/area/pincode?pincodeId=" + _event.value;
            this.savbillCommonBaseService.get(url).subscribe(
                (response: any) => {
                    this.areaListDD = response.areaList;
                    if (_event.value) {
                        let url = "/pincode/getServicAreaIdByPincode?pincodeid=" + _event.value;
                        this.savbillCommonBaseService.get(url).subscribe(
                            (res: any) => {
                                if (res.data != null) {
                                    // this.getBranchByServiceAreaID(response.data);
                                    // this.getPlanbyServiceArea(response.data);
                                    let serviceAreaId = {
                                        value: Number(res.data?.serviceAreaId)
                                    };

                                    if (!this.customerGroupForm.controls.serviceareaid.value) {
                                        this.customerGroupForm.controls.serviceareaid.setValue(res.data?.serviceAreaId);
                                        this.customerGroupForm.controls.serviceAreaName.setValue(res.data?.serviceAreaName);

                                        this.selServiceArea(serviceAreaId, false);
                                    }
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
    }

    planSelectType(event) {
        this.planDropdownInChageData = [];
        this.discountValueStore = [];
        this.discountValue = 0;
        this.planTotalOffetPrice = 0;
        const planaddDetailType = event.value;
        this.ifplanisSubisuSelect = false;
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
                if (this.customerGroupForm.value.parentExperience == "Single" || "Actual")
                    this.customerGroupForm.patchValue({ invoiceType: "Group" });
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
                        if (
                            element.planMode == "NORMAL" &&
                            (element.planGroupType === "Registration" ||
                                element.planGroupType === "Registration and Renewal")
                        ) {
                            this.filterNormalPlanGroup.push(element);
                        }
                    });
                }
                if (this.custType == "Postpaid") {
                    this.commondropdownService.postPlanGroupDetails.forEach(element => {
                        if (
                            element.planMode == "NORMAL" &&
                            (element.planGroupType === "Registration" ||
                                element.planGroupType === "Registration and Renewal")
                        ) {
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
                if (
                    this.customerGroupForm.value.parentCustomerId != null &&
                    this.customerGroupForm.value.parentCustomerId != ""
                ) {
                    this.customerGroupForm.controls.invoiceType.enable();
                    this.planGroupForm.controls.invoiceType.disable();
                    if (this.customerGroupForm.value.parentExperience == "Single" || "Actual")
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

    billtoSelectValue(e) {
        this.payMappingListFromArray.controls = [];
        this.planGroupForm.reset();
        this.planGroupForm.controls.skipQuotaUpdate.setValue(false);
        this.plansArray = this.fb.array([]);
        this.customerGroupForm.patchValue({
            plangroupid: ""
        });
    }

    onChangeArea(_event: any, index: any) {
        this.getAreaData(_event.value, index);
    }

    onChangeSubArea(_event: any, index: any) {
        if (_event?.value) {
            const subAreaurl = "/subarea/getAreaIdFromSubAreaId?subAreaId=" + _event.value;
            this.savbillCommonBaseService.get(subAreaurl).subscribe(
                (subarea: any) => {
                    if (subarea?.data) {
                        const url = "/area/" + subarea.data;
                        this.savbillCommonBaseService.get(url).subscribe(
                            (response: any) => {
                                if (response.data?.pincodeId) {
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
                                            } else {
                                                this.buildingListDD = [];
                                            }
                                        },
                                        (error: any) => {
                                            console.log(error, "error");
                                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                                        }
                                    );
                                    const pincodeUrl =
                                        "/pincode/getServicAreaIdByPincode?pincodeid=" + response.data?.pincodeId;

                                    this.savbillCommonBaseService.get(pincodeUrl).subscribe(
                                        (res: any) => {
                                            if (!this.customerGroupForm.controls.serviceareaid.value) {
                                                this.customerGroupForm.controls.serviceareaid.setValue(res.data?.serviceAreaId);
                                                this.customerGroupForm.controls.serviceAreaName.setValue(res.data?.serviceAreaName);

                                                let serviceAreaId = {
                                                    value: Number(res.data?.serviceAreaId)
                                                };
                                                this.selServiceArea(serviceAreaId, false);
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

                                                this.areaDetails = response.data;
                                                this.selectPincodeList = true;

                                                const patch = {
                                                    addressType: "Present",
                                                    areaId: Number(this.areaDetails.id),
                                                    pincodeId: Number(this.areaDetails.pincodeId),
                                                    cityId: Number(this.areaDetails.cityId),
                                                    stateId: Number(this.areaDetails.stateId),
                                                    countryId: Number(this.areaDetails.countryId)
                                                };

                                                this.presentGroupForm.patchValue(patch);
                                                this.step2Group.patchValue(patch);
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
                    //   this.buildingNoDD = response.dataList;
                    this.buildingNoDD = response.dataList.map(buildingNumber => ({ buildingNumber }));
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

    onChangeInvoiceToOrg(e) {
        if (!this.ifPlanGroup) {
            this.plansArray.value.forEach(element => {
                element.isInvoiceToOrg = e.value;
            });
        }
    }

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
            this.planGroupSelected = e.value;
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
                        // this.plansArray.controls = response.planGroup
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

                        // if (this.customerChangePlan) {
                        //   $("#selectPlanGroup").modal("show");
                        //   this.planGroupSelectedSubisu = e.value;
                        //   console.log(this.planGroupSelectedSubisu);
                        //   this.getPlanListByGroupIdSubisu();
                        // } else {
                        this.planGroupSelectedSubisu = e.value;
                        // }
                    }
                    let newAmount = 0;
                    let totalAmount = 0;
                    this.planIds = [];
                    planDetailData.planMappingList.forEach((element, i) => {
                        let n = i + 1;
                        newAmount =
                            element.newofferprice != null && element.newofferprice != 0
                                ? element.newofferprice
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
        }
        // else if (this.customerChangePlan) {
        //   $("#selectPlanGroup").modal("show");
        //   this.planGroupSelectedSubisu = e;
        //   console.log(this.planGroupSelectedSubisu);
        //   this.getPlanListByGroupIdSubisu();
        // }

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
        this.customerGroupForm.get('plangroupid').setValue(e.value)
    }

    planGroupDataById(planGroupId) {
        let url = "/findPlanGroupById?planGroupId=" + planGroupId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.planGroupMapingList = response.planGroup.planMappingList;
        });
    }

    subisuPrice(e) {
        this.newSubisuPrice = e.target.value;
    }
    modalClosePlanChangeSubisu() {
        $("#selectPlanGroup").modal("hide");
    }

    discountKeyDown(event: KeyboardEvent) {
        const inputElement = event.target as HTMLInputElement;
        const currentValue = inputElement.value;
        let maxValue: number = Number(99.99);
        let minValue: number = Number(-99.99);
        if (
            event.keyCode === 8 ||
            (event.key >= "0" && event.key <= "9") ||
            event.key === "-" ||
            (event.key === "." && inputElement.value.indexOf(".") === -1) // Allow only one decimal point
        ) {
            if (event.key !== "-" && event.keyCode !== 8) {
                let value = inputElement.value + event.key;

                if (parseFloat(value) <= maxValue && parseFloat(value) >= minValue) {
                    this.discountPercentage(value);
                    return true;
                } else {
                    return false;
                }
            } else if (event.keyCode === 8) {
                const updatedValue = currentValue.slice(0, -1);

                if (parseFloat(updatedValue) <= maxValue && parseFloat(updatedValue) >= minValue) {
                    this.discountPercentage(updatedValue);
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    discountPercentage(value) {
        // let rawValue = e.target.value.replace(/,/g, "");
        // let newValue = parseFloat(rawValue);

        // if (rawValue.includes("-")) {
        //   if (Math.abs(newValue) > 99) {
        //     e.target.value = "-99";
        //   }
        // } else {
        //   if (newValue > 99) {
        //     e.target.value = "99";
        //   } else if (newValue < -99) {
        //     e.target.value = "-99";
        //   }
        // }

        this.previousValue = value;
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
                .getofferPriceWithTax(this.planGroupForm.value.planId, value)
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
                            isInvoiceToOrg: this.customerGroupForm.value.isInvoiceToOrg,
                            skipQuotaUpdate:
                                this.customerGroupForm.value.skipQuotaUpdate == null
                                    ? false
                                    : this.customerGroupForm.value.skipQuotaUpdate
                        })
                    );

                    this.planTotalOffetPrice = this.planTotalOffetPrice + Number(newAmount);
                });

                // console.log(this.planListSubisu);

                this.planDataForm.patchValue({
                    offerPrice: this.planTotalOffetPrice
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    checkIfDiscountPlanGroup(plangroupid) {
        if (plangroupid !== null && plangroupid !== undefined && plangroupid !== "") {
            // console.log(
            //   plangroupid,
            //   this.filterNormalPlanGroup.find(planGroup => planGroup.planGroupId === plangroupid)
            // );
            return !this.filterNormalPlanGroup.find(planGroup => planGroup.planGroupId === plangroupid)
                .allowDiscount;
        } else {
            return false;
        }
    }

    serviceBasePlanDATA(event) {
        this.servicePlanId = event.value;
        const serviceId = event.value;
        const servicename = this.serviceData.find(item => item.id == serviceId).name;
        this.planGroupForm.patchValue({ service: servicename });
        this.planGroupForm.controls.istrialplan.reset();
        if (!this.isBranchAvailable) {
            this.plantypaSelectData = this.filterPlanData.filter(
                id =>
                    id.serviceId === this.planGroupForm.controls.serviceId.value &&
                    (id.planGroup === "Registration" || id.planGroup === "Registration and Renewal")
            );
            this.filterPlan = this.filterPlanData.filter(
                id => id.planGroup === "Registration" || id.planGroup === "Registration and Renewal"
            );
        } else {
            let planserviceData;
            let planServiceID = "";
            this.changeTrialCheck();
            const planserviceurl = "/planservice/all";
            this.customerManagementService.getMethod(planserviceurl).subscribe((response: any) => {
                planserviceData = response.serviceList.filter(service => service.id === serviceId);
                // console.log("planserviceData", planserviceData);
                this.isSerialNumberShow = planserviceData[0].serviceParamMappingList.some(
                    item => item.serviceParamName !== null && item.serviceParamName === "Product Required"
                );
                // console.log("isNull ::::: ", this.isSerialNumberShow);
                if (planserviceData.length > 0) {
                    planServiceID = planserviceData[0].id;

                    // if (this.customerGroupForm.value.custtype) {
                    this.plantypaSelectData = this.filterPlanData.filter(
                        id =>
                            id.serviceId === planServiceID &&
                            (id.planGroup === "Registration" || id.planGroup === "Registration and Renewal")
                    );
                    this.filterPlan = this.filterPlanData.filter(
                        id => id.planGroup === "Registration" || id.planGroup === "Registration and Renewal"
                    );
                    // if (this.payMappingListFromArray?.length > 0) {
                    //     let selectedCurrency = this.payMappingListFromArray?.value[0]?.currency;
                    //     this.plantypaSelectData = this.plantypaSelectData.filter(plan => {
                    //         const chargeCurrency = plan?.currency ?? this.currency;
                    //         return chargeCurrency === selectedCurrency;
                    //     });
                    // }
                    //console.log("this.plantypaSelectData", this.plantypaSelectData);
                    if (this.plantypaSelectData.length === 0) {
                        this.toastr.info(`${response.responseMessage}`, 'Plan not available for this customer type and service!');

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
                    offerprice: Number(planDetailData.offerprice),
                    currency: planDetailData.currency
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

    onAddplanMappingList() {
        this.plansubmitted = true;
        let offerP = 0;
        let disValue = 0;

        if (this.planGroupForm.valid) {
            this.discountValueStore.push({ value: this.discountValue });
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
            this.serialNumber = this.planGroupForm.value.serialNumber;
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
            this.planMappingDataSource.data = this.payMappingListFromArray.controls;
            this.payMappingListFromArray.updateValueAndValidity(); // This line is new
            this.validityUnitFormGroup.reset();

            if (this.payMappingListFromArray?.length > 0) {
                this.customerGroupForm
                    .get("currency")
                    .setValue(this.payMappingListFromArray?.value[0]?.currency);
            }
            //this.filterChargesByCurrency(this.planGroupForm.value);

            this.planGroupForm.reset();
            this.planGroupForm.controls.skipQuotaUpdate.setValue(false);
            this.planGroupForm.controls.validity.enable();
            this.plansubmitted = false;
            this.discountType = "One-time";
            this.discountValue = 0;
            if (this.customerGroupForm.value.parentExperience == "Single")
                this.planGroupForm.patchValue({ invoiceType: "Group" });
            else this.planGroupForm.patchValue({ invoiceType: "" });

        } else {
        }
    }


    filterChargesByCurrency(plan) {
        const selectedCurrency = plan?.currency;


        this.plantypaSelectData = this.plantypaSelectData.filter(plan => {
            const chargeCurrency = plan?.currency ?? this.currency;
            return chargeCurrency === selectedCurrency;
        });
    }

    validityUnitListFormGroup(): FormGroup {
        return this.fb.group({
            validityUnit: [this.validityUnitFormGroup.value.validityUnit]
        });
    }

    getChargeUsePlanList(id) {
        const url = "/postpaidplan/" + id;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            const data = response.postPaidPlan;
            this.planDropdownInChageData.push(data);
        });
    }


    planMappingListFormGroup(): FormGroup {
        // --- CHANGED ---
        // Use getRawValue() to include values from disabled controls (like 'validity')
        const formValue = this.planGroupForm.getRawValue();
        const selectedPlan = this.plantypaSelectData.find(
            plan => plan.id === formValue.planId
        );
        // Use a fallback name if not found, though it should be.
        const planName = selectedPlan ? selectedPlan.name : 'N/A';
        const formGroup = this.fb.group({
            planId: [formValue.planId, Validators.required],
            service: [formValue.service, Validators.required],
            validity: [formValue.validity, Validators.required],
            name: [planName],
            // --- SIMPLIFIED THIS LOGIC ---
            serviceId: [formValue.serviceId, Validators.required],

            discount: [formValue.discount ? formValue.discount : 0],
            billTo: [this.customerGroupForm.value.billTo],
            billableCustomerId: [this.customerGroupForm.value.billableCustomerId],
            newAmount: [formValue.newAmount],
            invoiceType: [formValue.invoiceType],
            offerPrice: [formValue.offerprice],
            isInvoiceToOrg: [this.customerGroupForm.value.isInvoiceToOrg],
            istrialplan: [formValue.istrialplan],
            discountType: [formValue.discountType],
            serialNumber: [formValue.serialNumber],
            discountExpiryDate: [
                formValue.discountExpiryDate
                    ? moment(formValue.discountExpiryDate).utc(true).toDate()
                    : null
            ],
            skipQuotaUpdate: [formValue.skipQuotaUpdate],
            currency: [formValue.currency]
        });

        return formGroup;
    }

    previousValue: number;
    discountChange(e, index) {
        let newValue = parseFloat(e.target.value);

        if (newValue > 99.99) {
            e.target.value = "99";
        } else if (newValue < -99.99) {
            e.target.value = "-99";
        } else {
            this.previousValue = newValue;

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
                            this.planDataForm.value.discountPrice -
                            this.discountValueStore[index].value +
                            lastvalue
                        ).toFixed(2)
                    });

                    this.discountValueStore[index].value = lastvalue;
                });
        }
    }

    deleteConfirmonChargeField(chargeFieldIndex: number, name: string) {
        if (chargeFieldIndex || chargeFieldIndex == 0) {
            // We ensure the logic from the original (incorrect) msgTxt assignment is preserved:
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

            // Use a LOCAL variable 'dialogRef' to ensure this instance is separate 
            // from any other dialogs managed by this class (like this.dialogRef).
            const deleteConfirmationDialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                disableClose: true,
                data: {
                    title: 'Delete Confirmation',
                    description: `Are you sure you want to delete this ${name}?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            // The subscription uses the local 'dialogRef' to handle the closure specific to this action.
            deleteConfirmationDialogRef.afterClosed().subscribe(result => {
                if (result) {
                    // Logic for deletion (original 'accept' handler)
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
                        // Other cases...
                    }
                } else {
                    // Logic for cancellation (original 'reject' handler intent)
                    this.toastr.info('Delete operation was cancelled', 'Info!');
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
                this.planDataForm.value.discountPrice - this.discountValueStore[chargeFieldIndex].value
            ).toFixed(2)
        });

        this.payMappingListFromArray.removeAt(chargeFieldIndex);
        this.validityUnitFormArray.removeAt(chargeFieldIndex);
        this.planMappingDataSource.data = this.payMappingListFromArray.controls;
        this.payMappingListFromArray.updateValueAndValidity();
        let obj = {
            value: this.servicePlanId
        };
        this.serviceBasePlanDATA(obj);
        this.discountValueStore.splice(chargeFieldIndex, 1);
        if (this.payMappingListFromArray.value.length == 0) {
            this.discountValueStore = [];
            this.planTotalOffetPrice = 0;
            this.planDataForm.patchValue({
                discountPrice: 0,
                offerPrice: 0
            });
        }
        this.changeTrialCheck();
    }

    pageChangedpayMapping(pageNumber) {
        this.currentPagePayMapping = pageNumber;
    }
    @ViewChild('parentCustomerDialog') parentCustomerDialog: TemplateRef<any>;
    parentCustomerDialogRef: MatDialogRef<any>;

    // modalOpenParentCustomer(type: string) {
    //     this.parentCustomerDialogType = type;
    //     this.selectedParentCust = null;

    //     this.customerSelectType = type === "parent" ? "Parent" : "Billable To";
    //     this.searchParentCustValue = '';
    //     this.currentPageParentCustomerListdata = 1;
    //     this.prepaidParentCustomerListDataSource.filter = '';
    //     this.getParentCustomerList();

    //     this.parentCustomerDialogRef = this.dialog.open(this.parentCustomerDialog, {
    //         width: '900px',
    //         disableClose: true
    //     });
    // }
    modalOpenParentCustomer(type) {
        this.parentCustomerDialogType = type;
        // this.showParentCustomerModel = true;
        this.customerSelectType = "Billable To";
        if (type === "parent") {
            this.customerSelectType = "Parent";
        }
        this.selectedParentCust = [];

        const dialogRef = this.dialog.open(CustomerSelectComponent, {
            width: '900px',
            disableClose: true,
            maxWidth: '90vw',
            autoFocus: false,
            data: {
                type: this.custType,
                selectedCust: this.selectedParentCust
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.selectedCustChange(result);
            }
        });
    }

    closeParentCust() {
        if (this.parentCustomerDialogRef) {
            this.parentCustomerDialogRef.close();
        }
        this.selectedParentCust = null;
        this.showParentCustomerModel = false;
    }
    searchParentCustValue: string = '';
    customerColumns: string[] = ['select', 'name', 'mobile', 'acctno'];
    prepaidParentCustomerListDataSource = new MatTableDataSource<any>();
    parentCustomerListdataitemsPerPage: number = 5;
    parentCustomerListdatatotalRecords: number = 0;
    currentPageParentCustomerListdata: number = 1;
    prepaidParentCustomerList: any[] = [];
    getParentCustomerList() {
        const data = {
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage,
            searchKey: this.searchParentCustValue,
            custType: this.custType
        };
        const url = `/parentCustomers/list/${this.custType}`;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                this.prepaidParentCustomerListDataSource.data = this.prepaidParentCustomerList;
                if (this.searchParentCustValue && this.searchParentCustValue.trim() !== '') {
                    this.prepaidParentCustomerListDataSource.filter = this.searchParentCustValue.trim().toLowerCase();
                    this.setupCustomFilterPredicate();
                    this.parentCustomerListdatatotalRecords = this.prepaidParentCustomerListDataSource.filteredData.length;
                } else {
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords || this.prepaidParentCustomerList.length;
                    this.prepaidParentCustomerListDataSource.filter = '';
                }
            },
            (error: any) => {
                console.error('Error fetching customer list', error);
                this.prepaidParentCustomerList = [];
                this.prepaidParentCustomerListDataSource.data = [];
                this.parentCustomerListdatatotalRecords = 0;
            }
        );
    }
    setupCustomFilterPredicate() {
        this.prepaidParentCustomerListDataSource.filterPredicate = (data: any, filter: string) => {
            const dataStr = (data.firstname + ' ' + data.lastname + ' ' + data.mobile + ' ' + data.acctno).toLowerCase();
            return dataStr.indexOf(filter) !== -1;
        };
    }
    openSearchModel() {
        this.ifsearchLocationModal = true;
        this.currentPagesearchLocationList = 1;
    }
    onCustomerSelect(customer: any) {
        this.selectedParentCust = customer;
    }
    confirmCustomerSelection() {
        if (!this.selectedParentCust) {
            this.toastr.info('Please select a customer.');
            return;
        }

        // Use the common handler to process the selection and update the main form
        this.selectedCustChange(this.selectedParentCust);

        // Close the dialog after successful selection and processing
        this.closeParentCustomerModal();
    }
    closeParentCustomerModal() {
        if (this.parentCustomerDialogRef) {
            this.parentCustomerDialogRef.close();
        }
        this.selectedParentCust = null;
        this.searchParentCustValue = '';
        this.currentPageParentCustomerListdata = 1;
        this.prepaidParentCustomerListDataSource.filter = '';
    }
    checkUsernme(customerId) {
        this.submitted = true;
        if (this.customerGroupForm.valid) {
            const isCredentialMatch =
                this.customerGroupForm.controls.isCredentialMatchWithAccountNo.value;
            if (isCredentialMatch) {
                this.addEditcustomer(customerId);
            } else {
                const url =
                    "/customer/customerUsernameIsAlreadyExists/" +
                    this.customerGroupForm.controls.username.value;
                this.customerManagementService.getMethod(url).subscribe((response: any) => {
                    if (response.isAlreadyExists) {
                        this.toastr.error(`${response.error.ERROR}`, 'Username already exists!!');

                    } else {
                        this.addEditcustomer(customerId);
                    }
                });
            }
        } else {

            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Fields are Mandatory or Invalid. Please fill or update those fields!');
            }

            this.scrollToError();
        }
    }

    scrollToError(): void {
        const firstElementWithError = document.querySelector(".ng-invalid[formControlName]");
        this.scrollTo(firstElementWithError);
    }

    scrollTo(el: Element): void {
        if (el) {
            el.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    }

    deleteConfirmip(index: number, name: string) {
        if (index || index === 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this " + name + "?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    switch (name) {
                        case "ipAddress":
                            this.ipMapppingListFromArray.removeAt(index);
                            break;
                        default:
                            break;
                    }
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'You have rejected');
                    }

                }
            });
        }
    }
    addEditcustomer(customerId) {
        this.submitted = true;
        this.customerGroupForm.markAllAsTouched();
        this.presentGroupForm.markAllAsTouched();
        if (this.payMappingListFromArray) {
            this.payMappingListFromArray.controls.forEach(control => control.markAsTouched());
            this.payMappingListFromArray.updateValueAndValidity();
        }

        // Debug log plan array length
        const hasIndividualPlans = this.payMappingListFromArray && this.payMappingListFromArray.length > 0;
        const hasPlanGroupSelected = !!this.customerGroupForm.get('plangroupid')?.value;

        if (!hasIndividualPlans && !hasPlanGroupSelected) {
            this.toastr.error("Minimum one Plan Details need to add!");
            this.scrollToError();
            return;
        }

        if (!this.customerGroupForm.valid || !this.presentGroupForm.valid) {
            this.toastr.error("Fields are Mandatory or Invalid. Please fill or update those fields!");
            this.scrollToError();
            return;
        }
        let i = 0;
        let j = 0;
        let K = 0;
        let x = 0;
        const l = 0;
        let a = 0;
        let b = 0;
        let c = 0;
        let addressListData: any = [];
        if (this.customerGroupForm.valid && this.presentGroupForm.valid) {
            //   if (
            //     this.customerGroupForm.value.planMappingList.length > 0 ||
            //     this.customerGroupForm.value.plangroupid ||
            //     this.customerGroupForm.value.custlabel === "organization"
            //   ) {

            if (customerId) {
                this.customerGroupForm.value.pan = this.customerGroupForm.value.pan
                    ? this.customerGroupForm.value.pan.trim()
                    : "";
                if (this.customerGroupForm.value.maxconcurrentsession < this.customerMacCount) {


                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You can not set max concurrent session less then customer mac!');
                    }

                    return;
                }
                const url = "/customers/" + customerId;
                this.customerGroupForm.value.flatAmount = this.planDataForm.value.discountPrice;
                this.customerGroupForm.value.discount = this.customerGroupForm.value.discount
                    ? this.customerGroupForm.value.discount
                    : 0;
                if (this.presentGroupForm.value.addressType) {
                    addressListData.push(this.presentGroupForm.value);
                    // this.addressListData [0].addressType = "Present";
                }
                if (this.paymentGroupForm.value.addressType) {
                    addressListData.push(this.paymentGroupForm.value);
                    // this.addressListData[1].addressType = "Payment";
                }
                if (this.permanentGroupForm.value.addressType) {
                    addressListData.push(this.permanentGroupForm.value);
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
                // this.createcustomerData = this.customerGroupForm.value;
                this.createcustomerData = this.customerGroupForm.getRawValue();
                this.createcustomerData.customerLocations = this.locationMacData;
                addressListData.forEach(item => {
                    delete item.subareaName;
                });
                this.createcustomerData.addressList = addressListData;

                this.createcustomerData.failcount = Number(this.createcustomerData.failcount);
                if (
                    this.customerGroupForm.controls.partnerid.value == null ||
                    this.customerGroupForm.controls.partnerid.value == ""
                ) {
                    this.createcustomerData.partnerid = 1;
                } else {
                    this.createcustomerData.partnerid =
                        this.partnerId !== 1 ? this.partnerId : this.customerGroupForm.controls.partnerid.value;
                }
                // this.createcustomerData.partnerid = Number(this.createcustomerData.partnerid);
                this.createcustomerData.paymentDetails.amount = Number(
                    this.createcustomerData.paymentDetails.amount
                );
                // if (this.viewcustomerListData.parentExperience != null) {
                //   this.customerGroupForm.controls.parentExperience.enable();
                // }
                while (a < this.createcustomerData.addressList.length) {
                    this.createcustomerData.addressList[a].areaId = this.createcustomerData.addressList[a].areaId ? Number(
                        this.createcustomerData.addressList[a].areaId
                    ) : Number(this.step2Group.value.areaId);

                    this.createcustomerData.addressList[a].pincodeId = this.createcustomerData.addressList[a].pincodeId ? Number(
                        this.createcustomerData.addressList[a].pincodeId
                    ) : Number(this.step2Group.value.pincodeId);

                    this.createcustomerData.addressList[a].cityId = this.createcustomerData.addressList[a].cityId ? Number(
                        this.createcustomerData.addressList[a].cityId
                    ) : Number(this.step2Group.value.cityId);

                    this.createcustomerData.addressList[a].stateId = this.createcustomerData.addressList[a].stateId ? Number(
                        this.createcustomerData.addressList[a].stateId
                    ) : Number(this.step2Group.value.stateId);

                    this.createcustomerData.addressList[a].countryId = this.createcustomerData.addressList[a].countryId ? Number(
                        this.createcustomerData.addressList[a].countryId
                    ) : Number(this.step2Group.value.countryId);

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
                // this.createcustomerData.acctno = this.viewcustomerListData.acctno;
                // this.createcustomerData.isDunningEnable = this.viewcustomerListData.isDunningEnable;
                // this.createcustomerData.isNotificationEnable =
                //   this.viewcustomerListData.isNotificationEnable;
                this.createcustomerData.username = this.customerGroupForm.controls.username.value;

                if (this.customerGroupForm.value.plangroupid) {
                    // this.createcustomerData.planMappingList = this.planGroupMapingList.value;
                    this.createcustomerData.planMappingList = this.plansArray.value;
                }
                this.createcustomerData.planPurchaseType = this.customerGroupForm.value.planCategory;

                this.createcustomerData.parentQuotaType = this.customerGroupForm.value.parentQuotaType;

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
                    this.createcustomerData.birthDate = new Date(this.customerGroupForm.value.birthDate);
                } else {
                    this.createcustomerData.birthDate = this.customerGroupForm.value.birthDate;
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
                this.customerManagementService.updateMethod(url, this.createcustomerData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.iscustomerEdit = false;
                        this.toastr.success(`Succesfully Updated`, 'Success!');

                        this.deactivateService.setShouldCheckCanExit(false);

                        this.router.navigate(["/home/customer/list/" + this.custType]);
                        // this.customerID = "";
                        // this.payMappingListFromArray.controls = [];
                        // this.overChargeListFromArray.controls = [];
                        // this.custMacMapppingListFromArray.controls = [];
                        // //   this.uploadDocumentListFromArray.controls = [];

                        // this.customerFormReset();
                        // this.customerGroupForm.controls.parentExperience.disable();
                        // //  this.uploadDocumentGroupForm.reset();
                        // this.viewcustomerListData = [];
                        // this.planCategoryForm.reset();
                        // this.addressListData = [];

                        // this.listView = true;
                        // this.createView = false;
                        // this.selectAreaList = false;
                        // this.selectchargeValueShow = false;
                        // this.ifIndividualPlan = false;
                        // this.ifPlanGroup = false;
                        // //    this.listSearchView = false;
                        // if (this.searchkey) {
                        //   this.searchcustomer();
                        // } else {
                        //   this.getcustomerList("");
                        // }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                this.customerGroupForm.value.pan = this.customerGroupForm.value.pan.trim();
                if (
                    this.customerGroupForm.value.planMappingList.length > 0 ||
                    this.customerGroupForm.value.plangroupid ||
                    this.customerGroupForm.value.custlabel === "organization"
                ) {
                    // if (this.presentGroupForm.value.addressType) {
                    addressListData.push(this.presentGroupForm.value);
                    addressListData[0].version = "NEW";
                    // }
                    if (this.paymentGroupForm.value.addressType) {
                        addressListData.push(this.paymentGroupForm.value);
                        // this.addressListData[1].addressType = "Payment";
                    }
                    if (this.permanentGroupForm.value.addressType) {
                        addressListData.push(this.permanentGroupForm.value);
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

                    this.customerGroupForm.get("billday").enable();
                    this.createcustomerData = this.customerGroupForm.value;
                    this.createcustomerData.customerLocations = this.locationMacData;
                    this.createcustomerData.birthDate = new Date(this.customerGroupForm.value.birthDate);
                    addressListData.forEach(item => {
                        delete item.subareaName;
                    });
                    this.createcustomerData.addressList = addressListData;

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
                        // this.createcustomerData.planMappingList = this.planGroupMapingList.value;
                    }
                    if (
                        this.createcustomerData.plangroupid == null ||
                        this.createcustomerData.plangroupid == ""
                    )
                        this.createcustomerData.invoiceType = null;
                    this.createcustomerData.planPurchaseType = this.planCategoryForm.value.planCategory;

                    this.createcustomerData.parentQuotaType = this.customerGroupForm.value.parentQuotaType;

                    while (x < this.createcustomerData.customerLocations.length) {
                        this.createcustomerData.customerLocations[x].locationId = Number(
                            this.locationMacData[x].locationId
                        );
                        this.createcustomerData.customerLocations[x].mac = this.locationMacData[x].mac;
                        this.createcustomerData.customerLocations[x].isParentLocation =
                            this.locationMacData[x].isParentLocation;
                        x++;
                    }
                    if (
                        this.createcustomerData?.mac_provision == null ||
                        this.createcustomerData?.mac_provision == undefined
                    ) {
                        this.createcustomerData.mac_provision = false;
                    }
                    // console.log("this.createcustomerData :::::::: ", this.createcustomerData);
                    //
                    // return;
                    let departmentId = this.customerGroupForm.value?.departmentId;
                    if (departmentId) {
                        let departmentData = this.departmentListData?.find(x => x?.id === departmentId);
                        this.createcustomerData.department = departmentData?.name;
                    }

                    const trimData = {
                        ...this.createcustomerData,
                        firstname: this.createcustomerData.firstname.trim(),
                        email: this.createcustomerData.email?.trim() || "",
                        latitude: this.presentGroupForm.get('latitude').value,
                        longitude: this.presentGroupForm.get('longitude').value,
                        mobile: this.createcustomerData.mobile.trim(),
                        lastname: this.createcustomerData.lastname.trim()
                    };

                    this.customerManagementService.postMethod(url, trimData).subscribe(
                        (response: any) => {
                            if (response.status == 406) {
                                this.toastr.info(`${response.responseMessage}`, 'Info!');

                            } else if (response.status == 400) {
                                this.toastr.info(`${response.ERROR.mobile}`, 'Info!');

                            } else {
                                this.toastr.success('Successfully Created', 'Success!');

                                this.deactivateService.setShouldCheckCanExit(false);
                                this.submitted = false;
                                this.router.navigate(["/home/customer/list/" + this.custType]);

                                // this.payMappingListFromArray.controls = [];
                                // this.overChargeListFromArray.controls = [];
                                // this.custMacMapppingListFromArray.controls = [];
                                // // this.uploadDocumentListFromArray.controls = [];
                                // this.addressListData = [];
                                // this.customerGroupForm.controls.parentExperience.disable();
                                // this.customerFormReset();
                                // //  this.uploadDocumentGroupForm.reset();
                                // this.selectchargeValueShow = false;

                                // this.listView = true;
                                // this.createView = false;
                                // this.ifIndividualPlan = false;
                                // this.ifPlanGroup = false;
                                // // this.listSearchView = false;

                                // this.selectAreaList = false;
                                // if (this.searchkey) {
                                //   this.searchcustomer();
                                // } else {
                                //   this.getcustomerList("");
                                // }
                            }
                        },
                        (error: any) => {
                            // console.log(error, "error")
                            if (error.status == 500) {
                                this.toastr.info(`${error.error.ERROR}`, 'Info!');

                            } else {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                            }
                        }
                    );
                } else {
                    // error: (error) => {
                    this.toastr.error(`Error!`, 'Minimum one Plan Details need to add!');
                    // }


                }
            }
        } else {
            // error: (error) => {
            this.toastr.error(`Error!`, 'Fields are Mandatory or Invalid. Please fill or update those field.!');
            // }


            this.scrollToError();
        }
    }

    setDefualtServiceArea() {
        this.serviceAreaList = this.commondropdownService.serviceAreaList;
        const anyMatch = this.serviceAreaList.some(obj => this.anyMatchString(obj, "Default"));
        if (anyMatch === true) {
            this.serviceAreaList.filter((el: any) => {
                if (el.name === "Default") {
                    this.pincodeDD = [];
                    const serviceAreaId = el.id;
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

                                this.getPlanbyServiceArea(serviceAreaId);
                            },
                            (error: any) => { }
                        );
                        this.getServiceByServiceAreaID(serviceAreaId);
                        let data = [];
                        data.push(serviceAreaId);
                        let url3 = "/branchManagement/getAllBranchesByServiceAreaId";
                        this.savbillCommonBaseService.postMethod(url3, data).subscribe((response: any) => {
                            this.branchData = response.dataList;
                            if (this.branchData.length > 0) {
                                this.customerGroupForm.patchValue({
                                    branch: this.branchData[0].id
                                });
                            }
                        });
                        // this.shiftLocationDTO.shiftPartnerid = "";
                    }
                    this.isBranchAvailable = true;
                    this.customerGroupForm.patchValue({
                        serviceareaid: el.id
                    });
                    this.presentGroupForm.controls.landmark.setValue(el.name);

                    const url = "/area/pincode?pincodeId=" + el.pincodes[el.pincodes.length - 1];
                    this.savbillCommonBaseService.get(url).subscribe(
                        (response: any) => {
                            this.areaListDD = response.areaList;

                            setTimeout(() => {
                                this.presentGroupForm.patchValue({
                                    addressType: "Present",
                                    pincodeId: Number(el.pincodes[el.pincodes.length - 1]),
                                    cityId: Number(this.areaListDD[0].cityId),
                                    stateId: Number(this.areaListDD[0].stateId),
                                    countryId: Number(this.areaListDD[0].countryId)
                                });
                            }, 500);
                            const url4 = "/pincode/" + this.areaListDD[0].id;

                            setTimeout(() => {
                                this.savbillCommonBaseService.get(url4).subscribe((response: any) => {
                                    this.presentGroupForm.patchValue({
                                        areaId: this.areaListDD[0].id
                                    });
                                });
                            }, 500);
                        },
                        (error: any) => {
                            console.log(error);
                        }
                    );
                }
            });
        }
    }

    anyMatchString(servicearea: any, string: any) {
        const serviceareanameLower = servicearea.name.toLowerCase();
        const searchStringLower = string.toLowerCase();
        return serviceareanameLower.includes(searchStringLower);
    }

    locationMacModelOpen() {
        this.showLocationMac = true;
    }

    locationMacModelClose() {
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
                        }) as any
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
                    }) as any
                );
            });
        }
    }

    addMacAtEdit() { }

    saveLocationMacData() {
        this.locationMacData = this.overLocationMacArray.value.map((location: any) => ({
            locationId: location.locationId, //location.locationId
            mac: location.mac,
            isParentLocation: this.customerGroupForm.value.isParentLocation
        }));
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
                locationId: location.locationId, //location.locationId
                mac: location.mac,
                isParentLocation: false
            }));
        }
    }

    deleteLocationMapField(locationMapField: any, index: number) {
        const existingIndex = this.custLocationData.findIndex(
            x => x.locationId === locationMapField.value.locationId
        );
        this.custLocationData.splice(existingIndex);
        this.overLocationMacArray.removeAt(index);
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

        this.custLocationData = [...this.custData.customerLocations];

        this.custData.customerLocations.forEach(location => {
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
                }) as any
            );
        });
        if (this.overLocationMacArray.value.length > 0) {
            this.locationMacData = this.overLocationMacArray.value.map((location: any) => ({
                locationId: location.locationId, //location.locationId
                mac: location.mac,
                isParentLocation: location.isParentLocation
            }));
        }
        this.locationChange(selectedLocation);
        this.locationMacForm.get("location").setValue(selectedLocation);
        this.showLocationMac = false;
    }

    searchLocation() {
        if (this.searchLocationForm.valid) {
            const url =
                "/serviceArea/getPlaceId?query=" + this.searchLocationForm.value.searchLocationname;
            this.savbillCommonBaseService.get(url).subscribe(
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
    hidePassword = true;
    hideLoginPassword = true;
    pageChangedSearchLocationList(currentPage) {
        this.currentPagesearchLocationList = currentPage;
    }

    clearsearchLocationData() {
        this.searchLocationData = [];
        this.ifsearchLocationModal = false;
        this.searchLocationForm.reset();
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

    insertBillDay(event, invoiceType) {
        if (event.value == "Group") {
            this.customerGroupForm.controls.billday.setValue(this.parentBillday);
            this.customerGroupForm.get("billday").disable();
        } else if (event.value == "Independent" && this.isInvoiceTypeAlreadySelected) {
            this.customerGroupForm.controls.billday.setValue("");
            this.customerGroupForm.controls.billday.enable();
        }
    }

    invoiceTypeClick() {
        let invoiceType = this.planGroupForm.value.invoiceType;
        if (invoiceType) {
            this.isInvoiceTypeAlreadySelected = true;
        } else {
            this.isInvoiceTypeAlreadySelected = false;
        }
    }

    getCustomerMacCount() {
        const url = "/customerMacManagement/getMacCount?custId=" + this.editCustId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerMacCount = response.data;
            },
            (error: any) => {
                this.customerMacCount = 0;
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    makeEmailAndMobileMandatoryOrNot() {
        this.systemService
            .getConfigurationByName("IS_MOBILE_AND_EMAIL_REQUIRED")
            .subscribe((res: any) => {
                this.isMobileAndEmailRequired = res.data.value == "true" ? true : false;
                if (this.isMobileAndEmailRequired) {
                    this.customerGroupForm.get("mobile").setValidators([Validators.required]);
                    this.customerGroupForm.get("mobile").updateValueAndValidity();
                    this.customerGroupForm.get("email");
                    this.customerGroupForm.get("email").updateValueAndValidity();
                    this.customerGroupForm.controls.isEmailAndMobileRequired.patchValue(true);
                } else {
                    this.customerGroupForm.get("mobile").clearValidators();
                    this.customerGroupForm.get("mobile").updateValueAndValidity();
                    this.customerGroupForm.get("email").clearValidators();
                    this.customerGroupForm.get("email").updateValueAndValidity();
                    this.customerGroupForm.controls.isEmailAndMobileRequired.patchValue(false);
                }
            });
    }
    keypressSession(event: any) {
        const pattern = /[0-9]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    onCredentialMatchChange(event: any) {
        const isChecked = event.checked;
        this.isCredentialMatch = isChecked;

        this.isCredentialMatchWithAccountNumber(isChecked);
    }

    isCredentialMatchWithAccountNumber = (isChecked: boolean) => {
        if (isChecked) {
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

    getDemographicLabel(currentName: string): string {
        if (!this.demographicLabel || this.demographicLabel.length === 0) {
            return currentName;
        }

        const label = this.demographicLabel.find(item => item.currentName === currentName);
        return label ? label.newName : currentName;
    }

    checkMobileLength() {
        this.isMobileNumberFocus = false;

        if (this.customerGroupForm.value.mobile.length >= this.commondropdownService.maxMobileLength) {
            this.isMobileNumberFocus = false;
        } else {
            this.isMobileNumberFocus = true;
        }
    }
}
