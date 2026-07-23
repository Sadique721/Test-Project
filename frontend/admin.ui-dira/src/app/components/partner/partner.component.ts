import { BranchManagementService } from "./../branch-management/branch-management.service";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgbDateStruct } from "@ng-bootstrap/ng-bootstrap";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { partnerManagement } from "src/app/components/model/partner";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomerDetailsService } from "src/app/service/customer-details.service";
import { LoginService } from "src/app/service/login.service";
import { PartnerService } from "src/app/service/partner.service";
import { CustomerDetailsComponent } from "../common/customer-details/customer-details.component";
import { countries } from "src/app/components/model/country";
import { Router, ActivatedRoute } from "@angular/router";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { SystemconfigService } from "../../service/systemconfig.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RegionManagementService } from "../region-management/region-management.service";
import { PARTNERS } from "src/app/constants/aclConstants";
import { HttpClient } from "@angular/common/http";
import { TreeNode } from "primeng/api";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';

declare var $: any;


declare module '@angular/forms' {
    interface FormGroup {
        pick(keys: string[]): FormGroup;
    }
}

FormGroup.prototype.pick = function (keys: string[]): FormGroup {
    const group: { [key: string]: any } = {};

    keys.forEach(k => {
        const control = this.get(k);
        if (control) {
            group[k] = new (control.constructor as any)(control.value, control.validator, control.asyncValidator);
        }
    });

    return new FormGroup(group);
};
@Component({
    selector: "app-partner",
    templateUrl: "./partner.component.html",
    styleUrls: ["./partner.component.css"],
    standalone: false
})
export class PartnerComponent implements OnInit {
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    KraTitle = RadiusConstants.KRA_PIN;
    @ViewChild(CustomerDetailsComponent)
    customerDetailModal: CustomerDetailsComponent;

    custId = new BehaviorSubject({
        custId: ""
    });

    countries: any = countries;
    partnerGroupForm: UntypedFormGroup;
    partnerCategoryList: any;
    submitted: boolean = false;
    taxListData: any;
    createpartnerData: partnerManagement;
    currentPagepartnerListdata = 1;
    partnerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    partnerListdatatotalRecords: any;
    partnerListData: any = [];
    parenetpartnerList: any = [];
    viewpartnerListData: any = [];
    ispartnerEdit: boolean = false;
    partnertype = "";
    partnercategory = "";
    searchpartnerUrl: any;
    dayArray: any = [];
    serviceData: any;
    qosPolicyData: any;
    quotaData: any;
    quotaTypeData: any;
    areaNameCategoryList: any;
    isPlanEdit: boolean = false;
    viewPlanListData: any;

    areaIdFromArray: UntypedFormArray;
    areaNameFromArray: UntypedFormArray;
    areaNameitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    areaNametotalRecords: String;
    currentPageareaName = 1;
    selectvalue = "";

    temp = [];
    partnerListData1: any;
    partnerListDataselector: any;
    partnerRulelength = 0;

    searchData: any;
    searchName: any = "";
    searchAddressType: any = "";
    searchServiceAreaName: any = "";

    myselectedArea = "";

    listView: boolean = true;
    createView: boolean = false;
    // partnerDetailsView: boolean = false;

    date1 = new Date();

    ngbBirthcal: NgbDateStruct | any;
    pincodeDeatils: any;

    selectPartnerView: boolean = false;
    partnerrALLDeatilsShow: boolean = false;
    balanceDetailsView = false;
    isBalanceSubMenu = false;
    partnerDATA: any = [];
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    filterPartnerListData: any = [];
    partnerLedgerInfoPojo: any = [];
    pageIndex = 0
    pageSize = RadiusConstants.ITEMS_PER_PAGE
    pageIndex1 = 0
    pageSize1 = RadiusConstants.ITEMS_PER_PAGE

    balanceLedgerItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    balanceLedgertotalRecords: String;
    currentPagebalanceLedgerList = 1;
    balanceData: any = [];
    showDialogue: boolean = false;
    BalanceAllData: any;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: string;
    totalDataListLength = 0;
    showItemBalance = 1;
    partnerIDSelectBalance: any;
    statusOptions = RadiusConstants.status;
    commissionShareType = [
        { label: "Balance", value: "Balance" },
        { label: "Revenue", value: "Revenue" }
    ];
    celendarTypeData = [{ label: "English" }, { label: "Nepali" }];
    viewPincodeNumber: any = "";
    filterPincodeData: any = [];
    allpincodeNumber: any = [];
    loginuser: any;
    partnerTypeData: any = [];
    currency: string;
    CommissionLabel = "Commission";
    PartnerPayableCommission = "Partner Payable commission";
    PartnerCommission = "Partner Commission";
    inputMobile: string;
    inputMobileSec = "";
    partnerFlag = false;
    isInvoiceSubMenu: boolean = false;
    isPaymentSubMenu: boolean = false;

    PaymentdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    PaymentdatatotalRecords: String;
    currentPagePaymentdata = 1;
    paymentShowItemPerPage = 1;
    viewPaymentData: any = [];

    currentPageinvoiceMasterSlab = 1;
    invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoiceMastertotalRecords: String;
    showItemPerPageInvoice = 1;
    invoiceMasterListData: any = [];
    partnerServiceDropdownData: any = [];
    partnerServiceDropdownMasterData: any = [];

    editServiceAreaList: any = [];
    ifmobileValidation: boolean = false;

    serviceBranches: any = [];
    branchRegions: any = [];
    regionVerticals: any = [];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    manageBalanceAccess: boolean = false;
    uploadAccess: boolean = false;
    shiftAccess: boolean = false;
    balanceDataAccess: boolean = false;
    invoiceAccess: boolean = false;
    paymentAccess: boolean = false;
    serviceareaModal: boolean = false;
    dialogId: boolean = false;
    viewShiftPartnerModel: boolean = false;
    activePriceBookList: any = [];
    activePriceBookListMaster: any = [];
    partnerHierarchyModal: boolean = false;
    partnerHierarchy: TreeNode[];
    isCustomerCountDetailsModel: boolean = false;
    customerLedgerDetailsList: any[] = [];
    customerDetailsCurrentPage = 0;
    customerDetailsTotalRecords: any;
    customerDetailsItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    isCustomerRevenueDetailsModel: boolean = false;
    isCustomerCommissionDetailsModel: boolean = false;
    isCustomerTotalCommissionDetailsModelForServiceLevel: boolean = false;
    isNetTotalCommissionOfServiceDetailsModelForServiceLevel: boolean = false;
    isCustomerNetCommissionDetailsModel: boolean = false;
    planCommissionDetailsList: any[] = [];
    customerCommissionDetailsList: any[] = [];
    netCommissionDetailsListForService = {};
    commissionIntervalOptionData = [
        { label: "Monthly" },
        { label: "Quarterly" },
        { label: "Half-Yearly" },
        { label: "Yearly" }
    ];
    loginPartner: any;
    customerServiceDetailsCurrentPage = 0;
    customerServiceDetailsTotalRecords: any;
    customerServiceDetailsItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    isOnlinePaymentAudit: boolean = false;
    partnerOnlinePaymentAuditListData: any[] = [];
    partnerOnlinePaymentdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    partnerpaymentCurrentPagepartnerListdata = 0;
    partnerOnlinePaymentListdatatotalRecords: any;
    searchOption: string = "name";
    searchOptions = [
        { label: "Name", value: "name" },
        { label: "Email", value: "email" },
        { label: "Commission Type", value: "commtype" },
        { label: "Country Name", value: "country" },
        { label: "Status", value: "status" }
    ];
    statusOption = [{ label: "Active" }, { label: "Inactive" }];
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private pincodeManagementService: PincodeManagementService,
        private messageService: MessageService,
        private partnerService: PartnerService,
        private regionManagementService: RegionManagementService,
        private branchManagementService: BranchManagementService,
        private savbillCommonBaseService: SavbillCommonBaseService,
        loginService: LoginService,
        private router: Router,
        private customerDetailsService: CustomerDetailsService,
        private systemService: SystemconfigService,
        private http: HttpClient,
        private revenueService: RevenueManagementService,
        private customerManagementService: CustomermanagementService,
        private dialog: MatDialog,
    ) {
        this.createAccess = loginService.hasPermission(PARTNERS.PARTNER_CREATE);
        this.deleteAccess = loginService.hasPermission(PARTNERS.PARTNER_DELETE);
        this.editAccess = loginService.hasPermission(PARTNERS.PARTNER_EDIT);
        this.manageBalanceAccess = loginService.hasPermission(PARTNERS.PARTNER_MANAGE_BALANCE);
        this.uploadAccess = loginService.hasPermission(PARTNERS.PARTNER_UPLOAD);
        this.shiftAccess = loginService.hasPermission(PARTNERS.PARTNER_SHIFT_PARTNER);
        this.balanceDataAccess = loginService.hasPermission(PARTNERS.PARTNER_BALANCE_DATA);
        this.invoiceAccess = loginService.hasPermission(PARTNERS.PARTNER_INVOICE);
        this.paymentAccess = loginService.hasPermission(PARTNERS.PARTNER_PAYMENT_DATA);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.ispartnerEdit = !this.createAccess && this.editAccess ? true : false;
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PARTNERS.PARTNER_EDIT) || this.loginService.hasPermission(PARTNERS.PARTNER_DELETE)) {
            return [
        'id',
        'name',
        'email',
        'commtype',
        'countryName',
        'status', 'action'];
        } else {
            return [
        'id',
        'name',
        'email',
        'commtype',
        'countryName',
        'status'];
        }
    }

    ngOnInit(): void {
        this.loginPartner = localStorage.getItem("partnerId");
        this.commondropdownService.getsystemconfigList();
        this.getAllPincodeList();
        this.partnerGroupForm = this.fb.group({
            address1: ["", Validators.required],
            address2: ["", Validators.required],
            addresstype: ["Permanent"],
            city: ["", Validators.required],
            cityName: [""],
            commdueday: ["", Validators.required],
            commrelvalue: [""],
            //  commtype: ['', Validators.required],
            commissionShareType: ["", Validators.required],
            id: [""],
            country: ["", Validators.required],
            countryCode: [this.commondropdownService.commonCountryCode],
            countryName: [""],
            createdById: [""],
            createdByName: [""],
            createdate: [""],
            credit: ["", Validators.required],
            name: ["", Validators.required],
            email: ["", Validators.email],
            isDelete: [""],
            lastModifiedById: [""],
            lastModifiedByName: [""],
            lastbilldate: [""],
            mobile: ["", [Validators.required]],
            nextbilldate: [""],
            outcomeBalance: ["", Validators.required],
            parentPartnerName: [""],
            parentpartnerid: [""],
            pincode: ["", Validators.required],
            pricebookId: ["", Validators.required],
            pricebookname: [""],
            state: ["", Validators.required],
            stateName: [""],
            status: ["", Validators.required],
            taxName: [""],
            taxid: ["", Validators.required],
            updatedate: [""],
            calendarType: ["English", Validators.required],
            prcode: [""],
            serviceAreaIds: [""],
            branch: [""],
            region: [""],
            bussinessvertical: [""],
            serviceAreaNameList: [],
            partnerType: ["", Validators.required],
            panName: ["", [Validators.minLength(9), Validators.maxLength(9)]],
            cpName: [""],
            cname: [""],
            commissionInterval: [""],
            isVisibleToIsp: [""]
        });

        this.partnerGroupForm.controls.commrelvalue.disable();
        //  this.partnerGroupForm.controls.pricebookId.disable();
        this.searchData = {
            currentPageNumber: this.currentPagepartnerListdata,
            dataList: [{}]
        };
        //dropdown
        // this.commondropdownService.getALLArea();
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        // this.commondropdownService.getAllPinCodeNumber();
        // // this.commondropdownService.getAllPinCodeData();
        this.commondropdownService.getTaxAllListAll();
        this.getActivePriceBookListAll();
        this.loginuser = localStorage.getItem("userId");
        const serviceArea = localStorage.getItem("serviceArea");
        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        this.daySequence();
        this.getpartnerList("");
        // this.getParenetPartnerList();
        this.getPartnerTypeData();
        window.scroll(0, 0);

        this.searchData = {
            filters: [
                {
                    filterColumn: "any",
                    filterCondition: "and",
                    filterDataType: "",
                    filterOperator: "equalto",
                    filterValue: "",
                    serviceArea: ""
                }
            ],
            page: "",
            pageSize: ""
        };
        this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
            if (lengthObj) {
                this.partnerGroupForm
                    .get("mobile")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(lengthObj.min),
                        Validators.maxLength(lengthObj.max)
                    ]);
                this.partnerGroupForm.get("mobile")?.updateValueAndValidity();
            }
        });
    }

    selectSearchOption() {
        this.searchName = "";
    }

    get cityName() {
        return this.partnerGroupForm.get("serviceAreaIds");
    }

    onKeymobilelength(event) {
        const str = this.partnerGroupForm.value.mobile.toString();
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
        const inputElement = event.target as HTMLInputElement;
        const inputValue = inputElement.value;

        // Check if the input starts with 0
        if (inputValue.startsWith("0")) {
            this.mobileError = true;
        } else {
            this.mobileError = false;
        }
    }

    checkEmail() {
        const enteredEmail = this.partnerGroupForm.get("email").value;
        if (enteredEmail) {
            const url = `/email?emailId=${enteredEmail}`;
            this.partnerService.getMethodNew(url).subscribe(
                (response: any) => {
                    if (response.responseCode == 409) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-times-circle"
                        // });
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`Email already exists`, 'Failed!');
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Email already exists",
                    //     icon: "far fa-times-circle"
                    // });
                }
            );
        }
    }

    createPlan(tempPartnerId) {
        this.getParenetPartnerList();
        // this.serviceAreaListWherePartnerIsNotBind(tempPartnerId);
        this.listView = false;
        this.createView = true;
        // this.partnerDetailsView = false;
        this.partnerrALLDeatilsShow = false;

        this.submitted = false;
        this.partnerGroupForm.reset();
        this.ispartnerEdit = false;
        this.selectPartnerView = false;
        this.balanceDetailsView = false;
        this.viewpartnerListData = [];
        this.isBalanceSubMenu = false;
        this.isInvoiceSubMenu = false;
        this.isPaymentSubMenu = false;
        this.isOnlinePaymentAudit = false;
        this.partnerGroupForm.controls.calendarType.setValue("English");
        this.partnerGroupForm.controls.countryCode.setValue("");
        this.partnerGroupForm.patchValue({
            countryCode: this.commondropdownService.commonCountryCode,
            commissionInterval: "Monthly"
        });
    }
    get step1Group(): FormGroup {
        return this.partnerGroupForm.pick(['name', 'prcode', 'partnerType', 'taxid', 'outcomeBalance', 'credit', 'parentpartnerid', 'calendarType', 'status', 'isVisibleToIsp']);
    }

    get step2Group(): FormGroup {
        return this.partnerGroupForm.pick(['countryCode', 'mobile', 'email', 'panName', 'cname', 'serviceAreaIds', 'branch', 'region', 'bussinessvertical', 'cpName']);
    }

    get step3Group(): FormGroup {
        return this.partnerGroupForm.pick(['pricebookId', 'commdueday', 'commissionInterval', 'commrelvalue', 'commissionShareType']);
    }
    serviceAreaListWherePartnerNotBind: any = [];

    serviceAreaListWherePartnerIsNotBind(tempPartnerId) {
        let url = " ";
        if (tempPartnerId) url = "/serviceAreaListWherePartnerIsNotBind?tempPartnerId=" + tempPartnerId;
        else url = "/serviceAreaListWherePartnerIsNotBind";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.serviceAreaListWherePartnerNotBind = response.serviceAreaList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    listPlan() {
        this.listView = true;
        this.createView = false;
        // this.partnerDetailsView = false;
        this.partnerrALLDeatilsShow = false;
        this.balanceDetailsView = false;
        this.isBalanceSubMenu = false;
        this.isInvoiceSubMenu = false;
        this.isPaymentSubMenu = false;
        this.isOnlinePaymentAudit = false;
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagepartnerListdata > 1) {
            this.currentPagepartnerListdata = 1;
        }
        if (!this.searchkey) {
            this.getpartnerList(this.showItemPerPage);
        } else {
            this.searchpartner();
        }
    }

    getpartnerList(list) {
        let size;
        let page_list = this.currentPagepartnerListdata;
        this.searchkey = "";
        if (list) {
            size = list;
            this.partnerListdataitemsPerPage = list;
        } else {
            // if (this.showItemPerPage == 1) {
            //   this.partnerListdataitemsPerPage = this.pageITEM
            // } else {
            //   this.partnerListdataitemsPerPage = this.showItemPerPage
            // }
            size = this.partnerListdataitemsPerPage;
        }

        const url = "/partner/list";
        let partnerdata = {
            page: page_list,
            pageSize: size
        };
        this.partnerListData = [];
        this.partnerListDataselector = [];
        this.filterPartnerListData = [];
        this.partnerService.postMethodNew(url, partnerdata).subscribe(
            (response: any) => {
                let partnerListData = response.partnerlist;
                this.partnerListData = partnerListData;
                this.partnerListDataselector = partnerListData;
                this.partnerListdatatotalRecords = response.pageDetails.totalRecords;

                if (this.showItemPerPage > this.partnerListdataitemsPerPage) {
                    this.totalDataListLength = this.partnerListData.length % this.showItemPerPage;
                } else {
                    this.totalDataListLength = this.partnerListData.length % this.partnerListdataitemsPerPage;
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getParenetPartnerList() {
        const url = "/partner/all";
        this.parenetpartnerList = [];
        this.partnerService.getMethodNew(url).subscribe((response: any) => {
            this.parenetpartnerList = response.partnerlist.filter(
                statusActive => statusActive.status === "ACTIVE" || statusActive.status === "ACTIVE"
            );
        });
    }

    particularALLDatashow(data: any) {
        if (data != "") {
            this.partnerDATA = data;
            if (this.partnerDATA.partnerType == "LCO") {
                this.partnerFlag = true;
                this.CommissionLabel = "Revenue Share to Operator";
                this.PartnerPayableCommission = "Payable Revenue Share to Operator";
                this.PartnerCommission = "Revenue Share to Operator";
            } else {
                this.partnerFlag = false;
                this.CommissionLabel = "Commission";
                this.PartnerPayableCommission = "Partner Payable commission";
                this.PartnerCommission = "Partner Commission";
            }
            if (data.pincode) {
                const url = "/pincode/" + data.pincode;

                this.savbillCommonBaseService.get(url).subscribe((response: any) => {
                    this.viewPincodeNumber = response.data.pincode;
                });
            }
        }

        // this.listView = false;
        this.createView = false;
        // this.partnerrALLDeatilsShow = true;
        this.dialog.open(this.partnerrALLDeatilsShowDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

        this.balanceDetailsView = false;
        this.isBalanceSubMenu = true;
        this.isInvoiceSubMenu = false;
        this.isPaymentSubMenu = false;
    }

    selectPINCODEChange(_event: any) {
        //
        this.getpincodeData(_event.value);
    }

    getpincodeData(id: any) {
        const url = "/pincode/" + id;
        this.savbillCommonBaseService.get(url).subscribe((response: any) => {
            this.selectPartnerView = true;
            this.pincodeDeatils = response.data;
            this.partnerGroupForm.patchValue({
                city: Number(this.pincodeDeatils.cityId),
                state: Number(this.pincodeDeatils.stateId),
                country: Number(this.pincodeDeatils.countryId)
            });
        });
    }

    tempPartnerId: any;

    addEditpartner(partnerId) {
        this.submitted = true;
        if (this.partnerGroupForm.valid && this.ifmobileValidation == false) {
            if (partnerId) {
                const url = "/partner/" + partnerId;
                if (
                    this.partnerGroupForm.value.countryCode == "" ||
                    this.partnerGroupForm.value.countryCode == null
                ) {
                    this.partnerGroupForm.value.countryCode = this.commondropdownService.commonCountryCode;
                }

                this.createpartnerData = this.partnerGroupForm.value;
                this.createpartnerData.isDelete = false;
                this.createpartnerData.addresstype = "Permanent";
                this.createpartnerData.commtype = "PRICEBOOK";
                if (
                    this.partnerGroupForm.value.calendarType == "" ||
                    this.partnerGroupForm.value.calendarType == null
                ) {
                    this.partnerGroupForm.value.calendarType = "English";
                }
                this.createpartnerData.totalCustomerCount = this.viewpartnerListData.totalCustomerCount;
                this.createpartnerData.renewCustomerCount = this.viewpartnerListData.renewCustomerCount;
                this.createpartnerData.newCustomerCount = this.viewpartnerListData.newCustomerCount;

                this.partnerService.updateMethodNew(url, this.createpartnerData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.partnerGroupForm.reset();
                        this.ispartnerEdit = false;
                        this.selectPartnerView = false;
                        this.viewpartnerListData = [];
                         this.toastr.success(`${response.message}`, 'Success!');
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });

                        this.listPlan();
                        if (!this.searchkey) {
                            this.getpartnerList("");
                            //   this.getParenetPartnerList();
                        } else {
                            this.searchpartner();
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: error.error.ERROR,
                        //     icon: "far fa-times-circle"
                        // });
                    }
                );
            } else {
                const url = "/partner";

                if (
                    this.partnerGroupForm.value.countryCode == "" ||
                    this.partnerGroupForm.value.countryCode == null
                ) {
                    this.partnerGroupForm.value.countryCode = this.commondropdownService.commonCountryCode;
                }
                this.createpartnerData = this.partnerGroupForm.value;

                this.createpartnerData.isDelete = false;
                this.createpartnerData.addresstype = "Permanent";
                this.createpartnerData.city = Number(this.createpartnerData.city);
                this.createpartnerData.commdueday = Number(this.createpartnerData.commdueday);
                this.createpartnerData.commrelvalue = Number(this.createpartnerData.commrelvalue);
                this.createpartnerData.country = Number(this.createpartnerData.country);
                this.createpartnerData.outcomeBalance = Number(this.createpartnerData.outcomeBalance);
                this.createpartnerData.commtype = "PRICEBOOK";

                this.createpartnerData.createdById = this.loginuser;
                this.partnerService.postMethodNew(url, this.createpartnerData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.partnerGroupForm.reset();
                        this.toastr.success(`${response.message}`, 'Success!');
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });
                        this.selectPartnerView = false;
                        this.listPlan();
                        if (!this.searchkey) {
                            this.getpartnerList("");
                            //   this.getParenetPartnerList();
                        } else {
                            this.searchpartner();
                        }
                    },
                    (error: any) => {
                        if (error.error.status == 400) {
                             this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            // this.messageService.add({
                            //     severity: "error",
                            //     summary: "Error",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });
                        } else if (error.error.status == 406) {
                             this.toastr.info(`${error.error.ERROR}`, 'info!');
                            // this.messageService.add({
                            //     severity: "info",
                            //     summary: "Info",
                            //     detail: error.error.ERROR,
                            //     icon: "pi pi-info-circle"
                            // });
                        } else {
                             this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            // this.messageService.add({
                            //     severity: "error",
                            //     summary: "Error",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });
                        }
                        console.log(error, "error");
                    }
                );
            }
        }
    }

    editpartner(PartnerId: any) {
        this.getParenetPartnerList();
        let date1;
        let date2;
        let editServiceAreaId: any = [];
        let editServiceAreaNameList: any = [];
        this.partnerServiceDropdownData = [];
        if (PartnerId) {
            this.createPlan(PartnerId);

            this.viewpartnerListData = [];
            const url = "/partner/" + PartnerId;
            this.partnerService.getMethodNew(url).subscribe(
                (response: any) => {
                    this.ispartnerEdit = true;
                    this.viewpartnerListData = response.partnerlist;

                    editServiceAreaId = this.viewpartnerListData.serviceAreaIds;
                    editServiceAreaNameList = this.viewpartnerListData.serviceAreaNameList;

                    editServiceAreaId.forEach((element1, i) => {
                        editServiceAreaNameList.forEach((elemnt2, j) => {
                            if (i == j) {
                                this.editServiceAreaList.push({ name: elemnt2, id: element1 });
                            }
                        });
                    });
                    // if (this.viewpartnerListData.lastbilldate) {
                    //   let a = moment(this.viewpartnerListData.lastbilldate);
                    //   date2= a.valueOf();
                    //   this.viewpartnerListData.lastbilldate = date2;
                    // }

                    if (this.viewpartnerListData.commtype == "PERCUSTFLAT") {
                        this.partnerGroupForm.controls.commrelvalue.enable();
                        this.partnerGroupForm.controls.pricebookId.disable();
                    } else if (this.viewpartnerListData.commtype == "PERCUSTPERC") {
                        this.partnerGroupForm.controls.commrelvalue.enable();
                        this.partnerGroupForm.controls.pricebookId.disable();
                    } else if (this.viewpartnerListData.commtype == "PRICEBOOK") {
                        this.partnerGroupForm.controls.commrelvalue.disable();
                        this.partnerGroupForm.controls.pricebookId.enable();
                    } else {
                        this.partnerGroupForm.controls.commrelvalue.disable();
                        //this.partnerGroupForm.controls.pricebookId.disable();
                    }

                    this.selectPartnerView = true;
                    let partnerrType = {
                        value: this.viewpartnerListData.partnerType
                    };
                    this.selectPartnerType(partnerrType, true);
                    let serviceData = {
                        value: this.viewpartnerListData.serviceAreaIds
                    };
                    this.selServiceArea(serviceData);
                    let branch = {
                        value: this.viewpartnerListData.branch
                    };
                    this.onBranchChange(branch);
                    let region = {
                        value: this.viewpartnerListData.region
                    };
                    this.onRegionChange(region);
                    this.partnerGroupForm.patchValue(this.viewpartnerListData);
                    this.partnerGroupForm.patchValue({
                        pincode: Number(this.viewpartnerListData.pincode),
                        serviceAreaIds: this.viewpartnerListData.serviceAreaIds,
                        branch: this.viewpartnerListData.branch,
                        region: this.viewpartnerListData.region,
                        bussinessvertical: this.viewpartnerListData.bussinessvertical
                    });
                    this.setPlanBundleAtEdit();
                },
                (error: any) => {
                     this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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

    canExit() {
        if (!this.partnerGroupForm.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
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
                // this.confirmationService.confirm({
                //     header: "Alert",
                //     message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                //     icon: "pi pi-info-circle",
                //     accept: () => {
                //         observer.next(true);
                //         observer.complete();
                //     },
                //     reject: () => {
                //         observer.next(false);
                //         observer.complete();
                //     }
                // });
                return false;
            });
        }
    }

    deleteConfirmonpartner(partnerId: number) {
        if (partnerId) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this partner?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deletepartner(partnerId);
                } else {
                    this.toastr.info(`You have rejected`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this partner?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deletepartner(partnerId);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }

    deletepartner(partnerId) {
        const url = "/partner/" + partnerId;
        this.partnerService.deleteMethodNew(url).subscribe(
            (response: any) => {
                if (this.currentPagepartnerListdata != 1 && this.totalDataListLength == 1) {
                    this.currentPagepartnerListdata = this.currentPagepartnerListdata - 1;
                }
                this.toastr.success(`${response.message}`, 'Success!');
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
                //     detail: response.message,
                //     icon: "far fa-check-circle"
                // });
                if (!this.searchkey) {
                    this.getpartnerList("");
                    //   this.getParenetPartnerList();
                } else {
                    this.searchpartner();
                }
            },
            (error: any) => {
                console.log(error, "error");
                if (error.error.status == 417) {
                    this.toastr.info(`${error.error.ERROR}`, 'info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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

    pageChangedpartnerList(pageNumber) {
        this.currentPagepartnerListdata = pageNumber.pageIndex + 1;
        this.partnerListdataitemsPerPage = pageNumber.pageSize
        if (!this.searchkey) {
            this.getpartnerList("");
        } else {
            this.searchpartner();
        }
    }

    pageChangedareaName(pageNumber) {
        this.currentPageareaName = pageNumber;
    }

    deleteConfirmonareaNameField(areaNameFieldIndex: number, areaNameFieldId: number) {
        if (areaNameFieldIndex || areaNameFieldIndex == 0) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this areaName?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.onRemoveareaName(areaNameFieldIndex, areaNameFieldId);
                } else {
                    this.toastr.info(`You have rejected`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this areaName?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.onRemoveareaName(areaNameFieldIndex, areaNameFieldId);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }

    async onRemoveareaName(areaNameFieldIndex: number, areaNameFieldId: number) {
        this.areaNameFromArray.removeAt(areaNameFieldIndex);
    }

    searchpartner() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPagepartnerListdata = 1;
        }

        if (this.showItemPerPage == 1) {
            this.partnerListdataitemsPerPage = this.pageITEM;
        } else {
            this.partnerListdataitemsPerPage = this.showItemPerPage;
        }
        let url = "/partner/search/byColumns";
        this.searchData.filters[0].filterColumn = this.searchOption;
        this.searchData.filters[0].filterValue = this.searchName.trim();
        this.searchData.filters[0].serviceArea = Number(this.searchServiceAreaName);

        this.searchData.page = this.currentPagepartnerListdata;
        this.searchData.pageSize = this.partnerListdataitemsPerPage;
        this.searchkey = this.searchName;
        this.partnerListData = [];
        this.filterPartnerListData = [];
        this.partnerListDataselector = [];
        this.partnerService.searchTax(url, this.searchData).subscribe(
            (response: any) => {
                this.partnerListData = response.partnerlist;

                this.partnerListdatatotalRecords = response.pageDetails.totalRecords;

                if (this.showItemPerPage > this.partnerListdataitemsPerPage) {
                    this.totalDataListLength = this.partnerListData.length % this.showItemPerPage;
                } else {
                    this.totalDataListLength = this.partnerListData.length % this.partnerListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.partnerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    this.partnerListData = [];
                } else {
                     this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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

    clearSearchpartner() {
        this.getpartnerList("");
        this.searchName = "";
        this.searchAddressType = "";
        this.searchServiceAreaName = "";
    }

    selCommType(event) {
        const commType = event.target.value;
        if (commType == "PERCUSTFLAT") {
            this.partnerGroupForm.controls.commrelvalue.enable();
            this.partnerGroupForm.controls.pricebookId.disable();
            this.partnerGroupForm.controls.commrelvalue.clearValidators();
            this.partnerGroupForm.controls.commrelvalue.setValidators(Validators.required);
            this.partnerGroupForm.controls.commrelvalue.updateValueAndValidity();
        } else if (commType == "PERCUSTPERC") {
            this.partnerGroupForm.controls.commrelvalue.enable();
            this.partnerGroupForm.controls.pricebookId.disable();
            this.partnerGroupForm.controls.commrelvalue.setValidators([
                Validators.required,
                Validators.max(100)
            ]);
            this.partnerGroupForm.controls.commrelvalue.updateValueAndValidity();
        } else if (commType == "PRICEBOOK") {
            this.partnerGroupForm.controls.commrelvalue.disable();
            this.partnerGroupForm.controls.pricebookId.enable();
        } else {
            this.partnerGroupForm.controls.commrelvalue.disable();
            this.partnerGroupForm.controls.pricebookId.disable();
        }
    }

    daySequence() {
        for (let i = 0; i < 31; i++) {
            this.dayArray.push({ label: i + 1 });
        }
    }

    TotalItemPerPageBalnce(event) {
        this.showItemBalance = Number(event.value);
        if (this.currentPagebalanceLedgerList > 1) {
            this.currentPagebalanceLedgerList = 1;
        }
        this.PartnerBalnceData(this.partnerIDSelectBalance, this.showItemBalance);
    }

    closeSelectStaff() {
        this.dialogId = false;
    }

    PartnerBalnceData(PartnerId, size) {
        let page_list;
        this.searchkey = "";
        this.partnerIDSelectBalance = PartnerId;
        if (size) {
            page_list = size;
            this.balanceLedgerItemPerPage = size;
        } else {
            if (this.showItemBalance == 1) {
                this.balanceLedgerItemPerPage = this.pageITEM;
            } else {
                this.balanceLedgerItemPerPage = this.showItemBalance;
            }
        }

        this.listView = false;
        this.createView = false;
        // this.partnerDetailsView = false;
        this.partnerrALLDeatilsShow = false;
        // this.balanceDetailsView = true;
        this.dialog.open(this.balanceDetailsViewDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.isInvoiceSubMenu = false;
        this.isPaymentSubMenu = false;
        this.isOnlinePaymentAudit = false;

        let data = {
            END_DATE: "",
            START_DATE: "",
            end_DATE: "",
            partner_id: PartnerId,
            start_DATE: ""
        };
        this.partnerService.balanaceData(data).subscribe(
            (response: any) => {
                this.BalanceAllData = response.partnerLedgerDtls;
                this.partnerLedgerInfoPojo = response.partnerLedgerDtls.partnerLedgerInfoPojo;
            },
            (error: any) => {
                 this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    pageChangedbalanceLedgerList(pageNumber) {
        this.currentPagebalanceLedgerList = pageNumber;
    }

    getAllPincodeList() {
        const url = "/pincode/getAll";
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.filterPincodeData = response.dataList;
                this.allpincodeNumber = response.dataList;
            },
            (error: any) => {
                 this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    catch(error) {
        console.error("Error in api", error);
    }

    async balanceAllDetails(data) {
        // if (data.transcategory == 'Commision') {
        this.balanceData = data;
        // this.showDialogue = true;

        this.dialog.open(this.showDialogueDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

    }

    balanceDetailsColse() {
        this.showDialogue = false;
    }

    openModal(id, custId) {
        // this.customerDetailsService.show(id);
        this.dialogId = true;
        // this.dialog.open(CustomerDetailsComponent, {
        //     width: '80%', disableClose: true,
        //     data: {
        //         custId: custId
        //     }
        // });
        this.custId.next({
            custId: custId
        });
    }

    gotoManageBalance(partnerid) {
        this.router.navigate(["/home/manageBalance"], {
            queryParams: { id: partnerid }
        });
    }

    selServiceArea(event) {
        this.serviceBranches = [];
        this.branchRegions = [];
        this.regionVerticals = [];
        this.partnerGroupForm.controls.branch.reset();
        this.partnerGroupForm.controls.region.reset();
        this.partnerGroupForm.controls.bussinessvertical.reset();
        const selServiceAreaId = event.value;
       
        if (selServiceAreaId.length > 0) {
            const getpincodebyserviceArea: any = [];
            selServiceAreaId.forEach(element => {
                const url = "/serviceArea/" + element;
                this.savbillCommonBaseService.get(url).subscribe(
                    (response: any) => {
                        const pincodesbyServiceArea = response.data.pincodes;
                        if (pincodesbyServiceArea.length > 0) {
                            pincodesbyServiceArea.forEach(element => {
                                if (!getpincodebyserviceArea.includes(element)) {
                                    getpincodebyserviceArea.push(element);
                                }
                            });
                        }

                        this.filterPincodeData = [];
                        this.allpincodeNumber.forEach(element => {
                            getpincodebyserviceArea.forEach(e => {
                                if (
                                    element.pincodeid == e &&
                                    !this.filterPincodeData.some(e => e.pincodeid == element.pincodeid)
                                ) {
                                    this.filterPincodeData.push(element);
                                }
                            });
                        });
                    },
                    (error: any) => {
                        console.log(error, "error");
                         this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: error.error.ERROR,
                        //     icon: "far fa-times-circle"
                        // });
                    }
                );
            });
            this.getBranchesByServiceArea(selServiceAreaId);
        }
    }

    onBranchChange(event) {

        const selectedBranch = [event.value];
        if (selectedBranch.length > 0) {
            this.getRegionByBranch(selectedBranch);
        }
    }

    onRegionChange(event) {

        const selectedRegion = [event.value];
        if (selectedRegion.length > 0) {
            this.getVerticalsByRegion(selectedRegion);
        }
    }

    getBranchesByServiceArea(selServiceAreaId) {
        const url = "/branchManagement/getAllBranchesByforPartnerServiceAreaId/";

        this.branchManagementService.postMethod(url, selServiceAreaId).subscribe(
            (response: any) => {
                this.serviceBranches = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                 this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getRegionByBranch(selectedBranch) {
        const url = "/region/getAllRegionByBranchId/";

        this.regionManagementService.postMethod(url, selectedBranch).subscribe(
            (response: any) => {
                this.branchRegions = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                 this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    getVerticalsByRegion(selectedRegion) {
        const url = "/businessverticals/getAllVerticalsByRegion/";

        this.branchManagementService.postMethod(url, selectedRegion).subscribe(
            (response: any) => {
                this.regionVerticals = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                 this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getPartnerTypeData() {
        const url = `/commonList/partnerType`;
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.partnerTypeData = response.dataList;
        });
    }

    selectPartnerType(event, isEdit) {
        if (event.value === "LCO") {
            this.partnerGroupForm.controls.serviceAreaIds.clearValidators();
            this.partnerGroupForm.controls.serviceAreaIds.updateValueAndValidity();
            this.getPartnerServiceListOfLCO(event.value, isEdit);
            this.getAllPincodeList();
            this.celendarTypeData = [{ label: "English" }];
            this.partnerGroupForm.patchValue({
                calendarType: "English"
            });

            if (!this.ispartnerEdit) {
                const currentDate = new Date();
                const lastDayOfMonth = new Date(
                    currentDate.getFullYear(),
                    currentDate.getMonth() + 2,
                    0
                ).getDate();

                this.partnerGroupForm.patchValue({
                    commissionShareType: "Revenue",
                    commdueday: lastDayOfMonth
                });
            }

            this.CommissionLabel = "Revenue Share to Operator";
        } else {
            this.partnerGroupForm.controls.serviceAreaIds.clearValidators();
            this.partnerGroupForm.controls.serviceAreaIds.setValidators(Validators.required);
            this.partnerGroupForm.controls.serviceAreaIds.updateValueAndValidity();
            this.getPartnerServiceList(event.value, isEdit);
            this.celendarTypeData = [{ label: "English" }, { label: "Nepali" }];
            this.partnerGroupForm.patchValue({
                commissionShareType: "",
                commdueday: "",
                calendarType: null
            });

            this.CommissionLabel = "Commission";
        }
    }

    getPartnerServiceListOfLCO(partnerType, isEdit) {
        let data = [];
        let filterData = [];
        this.partnerServiceDropdownData = [];
        this.partnerServiceDropdownMasterData = [];
        let type = partnerType.toUpperCase();
        // const url = `/serviceArea/All`;
        const url = "/serviceArea/dropdown/all";
        this.partnerService.getserviceAreaList(url).subscribe((response: any) => {
            this.partnerServiceDropdownData = response.dataList;
            this.partnerServiceDropdownMasterData = response.dataList;
            /*const serviceArea = localStorage.getItem("serviceArea");
                  if (serviceArea != null && serviceArea.length > 0) {
                    this.partnerServiceDropdownData = this.partnerServiceDropdownData.filter(item =>
                      serviceArea.includes(item.id)
                    );
                  }*/
            if (isEdit) {
                this.setServiceAreaDataAtEdit();
            }
        });
    }

    getPartnerServiceList(partnerType, isEdit) {
        let data = [];
        let filterData = [];
        this.partnerServiceDropdownData = [];
        this.partnerServiceDropdownMasterData = [];
        let type = partnerType.toUpperCase();
        const url = `/serviceAreaListWherePartnerIsNotBind?partnerType=` + type;
        this.partnerService.getMethod(url).subscribe((response: any) => {
            this.partnerServiceDropdownData = response.serviceAreaList;
            this.partnerServiceDropdownMasterData = response.serviceAreaList;
            /*const serviceArea = localStorage.getItem("serviceArea");
                  if (serviceArea != null && serviceArea.length > 0) {
                    this.partnerServiceDropdownData = this.partnerServiceDropdownData.filter(item =>
                      serviceArea.includes(item.id)
                    );
                  }*/
            if (isEdit) {
                this.setServiceAreaDataAtEdit();
            }
        });
    }

    setServiceAreaDataAtEdit() {
        if (this.partnerGroupForm.value.parentpartnerid) {
            var selectedParentPartner = this.parenetpartnerList.find(
                parent => parent.id == this.partnerGroupForm.value.parentpartnerid
            );

            this.partnerServiceDropdownData = selectedParentPartner.serviceAreaIds.map((id, index) => ({
                id: id,
                name: selectedParentPartner.serviceAreaNameList[index]
            }));
        } else {
            this.partnerServiceDropdownData = [
                ...this.partnerServiceDropdownData,
                ...this.editServiceAreaList
            ];

            this.partnerServiceDropdownMasterData = [
                ...this.partnerServiceDropdownData,
                ...this.editServiceAreaList
            ];
        }
    }

    showListPaymentData() {
        this.listView = false;
        this.createView = false;
        this.partnerrALLDeatilsShow = false;
        this.balanceDetailsView = false;
        this.isBalanceSubMenu = true;
        this.isInvoiceSubMenu = false;
        // this.isPaymentSubMenu = true;
        this.dialog.open(this.isPaymentSubMenuDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.isOnlinePaymentAudit = false;
        this.openCustomersPaymentData(this.partnerDATA.id, "");
    }

    openServiceareaModal() {
        // this.serviceareaModal = true;
        this.dialog.open(this.serviceareaModalDailog, {
            width: '450px',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
    }

    closeServiceareaModal() {
        this.serviceareaModal = false;
    }
    showListInvoiceData() {
        this.listView = false;
        this.createView = false;
        this.partnerrALLDeatilsShow = false;
        this.balanceDetailsView = false;
        this.isBalanceSubMenu = true;
        // this.isInvoiceSubMenu = true;
        this.isPaymentSubMenu = false;
        this.isOnlinePaymentAudit = false;
        this.dialog.open(this.isInvoiceSubMenuDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

        this.searchinvoiceMaster(this.partnerDATA.id, "");
    }

    pageChangedPaymentList(pageNumber) {
        this.currentPagePaymentdata = pageNumber.pageIndex + 1;
        this.PaymentdataitemsPerPage = pageNumber.pageSize;
        this.openCustomersPaymentData(this.partnerDATA.id, "");
    }

    TotalPaymentItemPerPage(event) {
        this.paymentShowItemPerPage = Number(event.value);
        if (this.currentPagePaymentdata > 1) {
            this.currentPagePaymentdata = 1;
        }
        this.openCustomersPaymentData(this.partnerDATA.id, this.paymentShowItemPerPage);
    }

    openCustomersPaymentData(id, size) {
        if (size) {
            this.PaymentdataitemsPerPage = size;
        } else {
            if (this.paymentShowItemPerPage == 1) {
                this.PaymentdataitemsPerPage = this.pageITEM;
            } else {
                this.PaymentdataitemsPerPage = this.paymentShowItemPerPage;
            }
        }

        const url = "/partnerDoc/partnerPaymentHistory/" + id;
        this.partnerService.getMethod(url).subscribe((response: any) => {
            this.viewPaymentData = response.dataList;
            this.PaymentdatatotalRecords = this.viewPaymentData.length;
        });
    }

    pageChangedinvoiceMasterList(pageNumber) {
        this.currentPageinvoiceMasterSlab = pageNumber.pageIndex + 1;
        this.invoiceMasteritemsPerPage = pageNumber.pageSize;
        this.searchinvoiceMaster(this.partnerDATA.id, "");
    }

    TotalItemPerPageInvoice(event) {
        this.showItemPerPageInvoice = Number(event.value);
        if (this.currentPageinvoiceMasterSlab > 1) {
            this.currentPageinvoiceMasterSlab = 1;
        }
        this.searchinvoiceMaster(this.partnerDATA.id, this.showItemPerPageInvoice);
    }

    searchinvoiceMaster(id, size) {
        if (size) {
            this.invoiceMasteritemsPerPage = size;
        } else {
            if (this.showItemPerPageInvoice == 1) {
                this.invoiceMasteritemsPerPage = this.pageITEM;
            } else {
                this.invoiceMasteritemsPerPage = this.showItemPerPageInvoice;
            }
        }

        let url = "/partnerDoc/partnerInvoiceHistory/" + id;
        this.partnerService.getMethod(url).subscribe(
            (response: any) => {
                this.invoiceMasterListData = response.dataList;
                this.invoiceMastertotalRecords = response.dataList.length;
            },
            (error: any) => {
                console.log(error, "error");
                 this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    partnerDATAServiceAreaList = [];
    selectedShiftPartnerServiceAreaList = [];
    selectedServiceAreaIds = [];
    selectedShiftPartner = null;
    frenchiseDropdownData = [];
    newPartnerId = null;
    partnerShifted = false;

    async shiftPartnerModel(partner) {
        if (partner != "") {
            this.newPartnerId = null;
            this.selectedShiftPartner = null;
            this.selectedShiftPartnerServiceAreaList = [];
            this.partnerDATAServiceAreaList = [];
            this.selectedServiceAreaIds = [];
            this.partnerShifted = false;
            await this.getFrenchiseData();
            this.partnerDATA = partner;
            // this.viewShiftPartnerModel = true;
            this.dialog.open(this.viewShiftPartnerModelDailog, {
                width: '80%',
                disableClose: true // same as data-backdrop="static" data-keyboard="false"
            });
            await this.commondropdownService.getserviceAreaList();

            //   this.partnerDATA.serviceAreaIds.forEach(serviceAreaId => {
            //     let serviceArea = this.commondropdownService.serviceAreaList.find(
            //       serviceArea => serviceArea.id === serviceAreaId
            //     );
            //     if (serviceArea) {
            //       this.partnerDATAServiceAreaList.push(serviceArea);
            //     }
            //   });

            //   this.partnerDATAServiceAreaList = [];

            this.partnerDATA.serviceAreaIds.forEach(serviceAreaId => {
                let serviceArea = this.commondropdownService.serviceAreaList.find(
                    serviceArea => serviceArea.id === serviceAreaId
                );

                if (serviceArea) {
                    // Check if the service area is not already present in this.partnerDATAServiceAreaList
                    if (
                        !this.partnerDATAServiceAreaList.some(
                            existingServiceArea => existingServiceArea.id === serviceArea.id
                        )
                    ) {
                        this.partnerDATAServiceAreaList.push(serviceArea);
                    }
                }
            });
        }
    }

    closeShiftPartnerModel() {
        this.viewShiftPartnerModel = false;
    }

    shiftPartnerSave(dialogRef: any) {
        if ((this.partnerShifted = true)) {
            const url = "/partner/";
            this.partnerDATA.serviceAreaIds = this.partnerDATAServiceAreaList.map(
                serviceArea => serviceArea.id
            );
            this.selectedShiftPartner.serviceAreaIds = this.selectedShiftPartnerServiceAreaList.map(
                serviceArea => serviceArea.id
            );
            this.partnerDATA.commtype = "PRICEBOOK";
            this.selectedShiftPartner.commtype = "PRICEBOOK";
            this.partnerDATA.isShitPartner = true;
            this.selectedShiftPartner.isShitPartner = false;
            this.partnerDATA.status =
                this.partnerDATA.serviceAreaIds.length > 0 ? this.partnerDATA.status : "INACTIVE";
            this.partnerService.updateMethodNew(url + this.partnerDATA.id, this.partnerDATA).subscribe(
                (response: any) => {
                    this.partnerService
                        .updateMethodNew(url + this.newPartnerId, this.selectedShiftPartner)
                        .subscribe(
                            (response: any) => {
                                this.newPartnerId = null;
                                this.selectedShiftPartner = null;
                                this.selectedShiftPartnerServiceAreaList = [];
                                this.partnerDATAServiceAreaList = [];
                                this.selectedServiceAreaIds = [];
                                dialogRef.close()
                                this.toastr.success(`${response.message}`, 'Success!');
                                // this.messageService.add({
                                //     severity: "success",
                                //     summary: "Successfully",
                                //     detail: response.message,
                                //     icon: "far fa-check-circle"
                                // });
                                this.viewShiftPartnerModel = false;
                                this.listPlan();
                                if (!this.searchkey) {
                                    this.getpartnerList("");
                                    this.getParenetPartnerList();
                                } else {
                                    this.searchpartner();
                                }
                            },
                            (error: any) => {
                                console.log(error, "error");
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                                // this.messageService.add({
                                //     severity: "error",
                                //     summary: "Error",
                                //     detail: error.error.ERROR,
                                //     icon: "far fa-times-circle"
                                // });
                            }
                        );
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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

    getFrenchiseData() {
        const url = "/partner/type/FRANCHISE";
        this.partnerService.getMethodNew(url).subscribe((response: any) => {
            this.frenchiseDropdownData = response.partnerlist.filter(
                partner => partner.name !== "Default"
            );
        });
    }

    getPartnerData() {
        this.selectedShiftPartner = this.frenchiseDropdownData.find(
            partner => partner.id === this.newPartnerId
        );
        this.selectedShiftPartnerServiceAreaList = [];

        this.selectedShiftPartner.serviceAreaIds.forEach(serviceAreaId => {
            const existingServiceArea = this.selectedShiftPartnerServiceAreaList.find(
                serviceArea => serviceArea.id === serviceAreaId
            );

            if (!existingServiceArea) {
                const newServiceArea = this.commondropdownService.serviceAreaList.find(
                    serviceArea => serviceArea.id === serviceAreaId
                );

                if (newServiceArea) {
                    this.selectedShiftPartnerServiceAreaList.push(newServiceArea);
                }
            }
        });
    }

    shiftPartner() {
        if (this.newPartnerId !== null) {
            this.partnerShifted = true;
            this.selectedShiftPartnerServiceAreaList = [
                ...this.selectedShiftPartnerServiceAreaList,
                ...this.commondropdownService.serviceAreaList.filter(serviceArea =>
                    this.selectedServiceAreaIds.includes(serviceArea.id)
                )
            ];
            this.partnerDATAServiceAreaList = this.partnerDATAServiceAreaList.filter(
                item => !this.selectedServiceAreaIds.includes(item.id)
            );
            this.selectedServiceAreaIds = [];
        }
    }

    getActivePriceBookListAll() {
        const url = "/priceBook/active";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.activePriceBookList = response.dataList;
                this.activePriceBookListMaster = response.dataList;
            },
            (error: any) => { }
        );
    }

    onChangePartner(event: any, dd: any) {

        // let selectedParentPartner = dd.selectedOption;

        const selectedParentPartner = this.parenetpartnerList.find(p => p.id === event.value);

        if (this.partnerGroupForm.value.partnerType === "Franchise") {
            this.partnerServiceDropdownData = selectedParentPartner.serviceAreaIds.map((id, index) => ({
                id: id,
                name: selectedParentPartner.serviceAreaNameList[index]
            }));

            this.activePriceBookList = this.activePriceBookListMaster.filter(
                priceBook =>
                    priceBook.partnerId == selectedParentPartner.id ||
                    priceBook.id == selectedParentPartner.pricebookId
            );
        } else {
            this.partnerServiceDropdownData = this.partnerServiceDropdownMasterData;
            this.activePriceBookList = this.activePriceBookListMaster;
        }
    }

    setPlanBundleAtEdit() {
        if (this.partnerGroupForm.value.parentpartnerid) {
            var selectedParentPartner = this.parenetpartnerList.find(
                parent => parent.id == this.partnerGroupForm.value.parentpartnerid
            );
            this.activePriceBookList = this.activePriceBookListMaster.filter(
                priceBook =>
                    priceBook.partnerId == selectedParentPartner.id ||
                    priceBook.id == selectedParentPartner.pricebookId
            );
        }
    }

    openPartnerHierarchyModal(parentpartnerid) {
        // this.partnerHierarchyModal = true;
        this.dialog.open(this.partnerHierarchyModalDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.getPartnerHierarchy(parentpartnerid);
    }

    closePartnerHierarchyModal() {
        this.partnerHierarchyModal = false;
    }

    getPartnerHierarchy(parentpartnerid) {
        this.partnerHierarchy = [];

        const url = "/getPartnerHierarchyByChildPartnerId/" + parentpartnerid;
        this.partnerService.getMethodNew(url).subscribe((response: any) => {
            this.partnerHierarchy = response.partnerHierarchyList;
        });
    }

    handleKeyDown(event: KeyboardEvent) {
        const inputElement = event.target as HTMLInputElement;

        if (
            event.keyCode === 8 ||
            (event.key >= "0" && event.key <= "9") ||
            (event.key === "." && inputElement.value.indexOf(".") === -1) // Allow only one decimal point
        ) {
        } else {
            return false; // Prevent the input for other keys
        }
    }

    openCustomerCountDetailsModel(isPlanGroup, planOrPlanGroupId, transcategory, transType) {
        // this.isCustomerCountDetailsModel = true;

        this.dialog.open(this.isCustomerCountDetailsModelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.customerLedgerDetailsList = [];
        if (isPlanGroup) {
            this.customerLedgerDetailsList =
                transcategory == "Commision"
                    ? this.partnerLedgerInfoPojo.debitCreditDetail.filter(
                        debit =>
                            debit.transcategory == transcategory &&
                            debit.planGroupId == planOrPlanGroupId &&
                            debit.transtype == transType
                    )
                    : this.partnerLedgerInfoPojo.debitCreditDetail.filter(
                        debit => debit.transcategory == transcategory && debit.transtype == transType
                    );
        } else {
            this.customerLedgerDetailsList =
                transcategory == "Commision"
                    ? this.partnerLedgerInfoPojo.debitCreditDetail.filter(
                        debit =>
                            debit.transcategory == transcategory &&
                            debit.planid == planOrPlanGroupId &&
                            debit.planGroupId == null &&
                            debit.transtype == transType
                    )
                    : this.partnerLedgerInfoPojo.debitCreditDetail.filter(
                        debit => debit.transcategory == transcategory && debit.transtype == transType
                    );
        }
        this.customerDetailsTotalRecords = this.customerLedgerDetailsList.length;
    }

    closeCustomerCountDetailsModel() {
        this.isCustomerCountDetailsModel = false;
    }

    customerDetailsPageChanged(pageNumber) {
        this.customerDetailsCurrentPage = Number(pageNumber);
    }

    customerDetailsTotalItemPerPage(event) {
        this.showItemBalance = Number(event.value);
        if (this.customerDetailsCurrentPage > 1) {
            this.customerDetailsCurrentPage = 1;
        }
    }

    openCustomerRevenueDetailsModel(ledger) {
        // this.isCustomerRevenueDetailsModel = true;
        this.dialog.open(this.isCustomerRevenueDetailsModelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

        this.getPlanCommissionDetails(ledger);
    }

    closeCustomerRevenueDetailsModel() {
        this.isCustomerRevenueDetailsModel = false;
    }

    openCustomerCommissionDetailsModel(ledger) {
        // this.isCustomerCommissionDetailsModel = true;
        this.dialog.open(this.isCustomerCommissionDetailsModelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.getPlanCommissionDetails(ledger);
    }

    closeCustomerCommissionDetailsModel() {
        this.isCustomerCommissionDetailsModel = false;
    }

    openCustomerNetCommissionDetailsModel(ledger) {
        // this.isCustomerNetCommissionDetailsModel = true;
        this.dialog.open(this.isCustomerNetCommissionDetailsModelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.getPlanCommissionDetails(ledger);
    }

    closeCustomerNetCommissionDetailsModel() {
        this.isCustomerNetCommissionDetailsModel = false;
    }

    getPlanCommissionDetails(ledger) {
        this.planCommissionDetailsList = [];
        let request = {
            id: ledger.planOrPlanGroupId,
            isPlanGroup: ledger.isPlanGroup,
            agrPercentage: ledger.agrPercentage,
            revenueSharePercentage: ledger.commissionSharePercentage,
            partnerTaxId: ledger.partnerTaxId,
            tdsPercentage: ledger.tdsPercentage
        };
        let url = "/getPlanCommissionDetailList";
        this.revenueService.postMethod(url, request).subscribe(
            (response: any) => {
                this.planCommissionDetailsList = response.planCommissionDetails.planCommissionDetailList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getTotal(data) {
        return data.reduce(
            (item, { baseOfferPriceExcludeAgr }) => (item += +(baseOfferPriceExcludeAgr || 0)),
            0
        );
    }

    openCustomerCountDetailsModelForServiceLevel(serviceId, transcategory, transType) {
        // this.isCustomerCountDetailsModel = true;
        this.dialog.open(this.isCustomerCountDetailsModelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.customerLedgerDetailsList = [];
        this.customerLedgerDetailsList =
            transcategory == "Commision"
                ? this.partnerLedgerInfoPojo.debitCreditDetail.filter(
                    debit =>
                        debit.transcategory == transcategory &&
                        debit.serviceId == serviceId &&
                        debit.transtype == transType
                )
                : this.partnerLedgerInfoPojo.debitCreditDetail.filter(
                    debit => debit.transcategory == transcategory && debit.transtype == transType
                );
        this.customerDetailsTotalRecords = this.customerLedgerDetailsList.length;
    }

    openCustomerTotalCommissionDetailsModel(ledger) {
        // this.isCustomerTotalCommissionDetailsModelForServiceLevel = true;
        this.dialog.open(this.isCustomerTotalCommissionDetailsModelForServiceLevelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

        this.getCustomerTotalCommissionDetails(ledger);
    }

    closeCustomerTotalCommissionDetailsModelForServiceLevel() {
        this.isCustomerTotalCommissionDetailsModelForServiceLevel = false;
    }

    getCustomerTotalCommissionDetails(ledger) {
        this.customerCommissionDetailsList = [];

        this.customerCommissionDetailsList = ledger.serviceLevelCommissions;

        this.customerServiceDetailsTotalRecords = this.customerCommissionDetailsList.length;
    }

    openNetTotalCommissionOfServiceDetailsModelForServiceLevel(ledger) {
        // this.isNetTotalCommissionOfServiceDetailsModelForServiceLevel = true;
        this.dialog.open(this.isNetTotalCommissionLevelDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        let totalBaseRevenue = ledger.commissionDetailList.planCommissionDetailList.reduce(
            (total, revenue) => total + revenue.baseOfferPriceExcludeAgr * revenue.customerCount,
            0
        );
        let totalCommission = ledger.commissionDetailList.planCommissionDetailList.reduce(
            (total, revenue) => total + revenue.netCommission * revenue.customerCount,
            0
        );

        this.netCommissionDetailsListForService = {
            serviceName: ledger.serviceName,
            totalBaseRevenue: totalBaseRevenue,
            commission: totalCommission
        };
    }

    closeNetTotalCommissionOfServiceDetailsModelForServiceLevel() {
        this.isNetTotalCommissionOfServiceDetailsModelForServiceLevel = false;
    }

    customerServiceDetailsPageChanged(pageNumber) {
        this.customerServiceDetailsCurrentPage = Number(pageNumber);
    }

    customerServiceDetailsTotalItemPerPage(event) {
        this.showItemBalance = Number(event.value);
        if (this.customerServiceDetailsCurrentPage > 1) {
            this.customerServiceDetailsCurrentPage = 1;
        }
    }

    getPartnerOnlinePaymentAudit(id) {
        this.listView = false;
        this.createView = false;
        this.partnerrALLDeatilsShow = false;
        this.balanceDetailsView = false;
        this.isInvoiceSubMenu = false;
        this.isPaymentSubMenu = false;
        // this.isOnlinePaymentAudit = true;
        this.dialog.open(this.isOnlinePaymentAuditDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        const url = "/onlinePayAudit/allByPartnerId?partnerId=" + id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerOnlinePaymentAuditListData = response.onlineAuditData;
                if (response?.onlineAuditData && response?.onlineAuditData.length == 0) {
                    this.toastr.info(`No Payment Found !!`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "No Payment Found !! ",
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    partnerPaymentPageChanged(pageNumber) {
        this.partnerpaymentCurrentPagepartnerListdata = pageNumber;
    }

    partnerPaymentTotalItemPerPage(event) {
        if (this.partnerpaymentCurrentPagepartnerListdata > 1) {
            this.partnerpaymentCurrentPagepartnerListdata = 1;
        }
    }
    displayedColumns: string[] = [
        'id',
        'name',
        'email',
        'commtype',
        'countryName',
        'status', 'action']

    displayedColumnsisInvoiceSubMenu: string[] = ['docnumber', 'totalamount', 'adjustedamount', 'billrunstatus', 'billdate', 'duedate'];
    displayedColumnspayment = ['paydetails4', 'receiptNo', 'paymode', 'type', 'amount', 'paymentdate', 'status'];
    isCustomerRevenuedisplayedColumns: string[] = [
        'planName',
        'basePrice',
        'planTax',
        'totalPrice',
        'agr',
        'baseRevenue'
    ];
    isCustomerCountdisplayedColumns: string[] = [
        'createDate',
        'customer_name',
        'invoiceNo',
        'creditDocNo',
        'planname',
        'offerprice',
        'commission',
        'transcategory',
        'debit',
        'credit',
        'partnerInvoiceId'
    ];
    customterCommissiondisplayedColumns: string[] = [
        'planName',
        'netCommission',
        'partnerTaxAmount',
        'tdsAmount'
    ];
    customterNETCommissiondisplayedColumns = ['planName',
        'baseOfferPriceExcludeAgr',
        'netCommission',]

    CustomerTotalCommissiondisplayedColumns: string[] = [
        'planName',
        'partnerCommission',
        'royalty',
        'partnerTax',
        'tds',
        'netCommission',
        'payableCommission'
    ];

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    @ViewChild('partnerrALLDeatilsShowDailog') partnerrALLDeatilsShowDailog!: TemplateRef<any>;
    @ViewChild('balanceDetailsViewDailog') balanceDetailsViewDailog!: TemplateRef<any>;
    @ViewChild('isOnlinePaymentAuditDailog') isOnlinePaymentAuditDailog!: TemplateRef<any>;
    @ViewChild('showDialogueDailog') showDialogueDailog!: TemplateRef<any>;
    @ViewChild('serviceareaModalDailog') serviceareaModalDailog!: TemplateRef<any>;

    @ViewChild('isInvoiceSubMenuDailog') isInvoiceSubMenuDailog!: TemplateRef<any>;
    @ViewChild('isPaymentSubMenuDailog') isPaymentSubMenuDailog!: TemplateRef<any>;
    @ViewChild('viewShiftPartnerModelDailog') viewShiftPartnerModelDailog!: TemplateRef<any>;
    @ViewChild('partnerHierarchyModalDailog') partnerHierarchyModalDailog!: TemplateRef<any>;
    @ViewChild('isCustomerCountDetailsModelDailog') isCustomerCountDetailsModelDailog!: TemplateRef<any>;
    @ViewChild('isCustomerRevenueDetailsModelDailog') isCustomerRevenueDetailsModelDailog!: TemplateRef<any>;
    @ViewChild('isCustomerCommissionDetailsModelDailog') isCustomerCommissionDetailsModelDailog!: TemplateRef<any>;
    @ViewChild('isCustomerNetCommissionDetailsModelDailog') isCustomerNetCommissionDetailsModelDailog!: TemplateRef<any>;

    @ViewChild('isCustomerTotalCommissionDetailsModelForServiceLevelDailog') isCustomerTotalCommissionDetailsModelForServiceLevelDailog!: TemplateRef<any>;
    @ViewChild('isNetTotalCommissionLevelDailog') isNetTotalCommissionLevelDailog!: TemplateRef<any>;






}
