import { Component, Input, OnInit, ViewChild } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { PartnerService } from "src/app/service/partner.service";
import { CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { Output, EventEmitter } from '@angular/core';
import { CustomerService } from "src/app/service/customer.service";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import * as uuid from "uuid";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { BehaviorSubject } from "rxjs";
import moment from "moment";
import FileSaver from "file-saver";
import { InvoiceMasterService } from "src/app/service/invoice-master.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { InvoiceDetalisModelComponent } from "../../invoice-detalis-model/invoice-detalis-model.component";
import { countries } from "../../model/country";
import { NgxSpinnerService } from "ngx-spinner";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { ActivatedRoute } from "@angular/router";

@Component({
    selector: "app-customer-caf-home",
    templateUrl: "./customer-caf-home.component.html",
    styleUrl: './customer-caf-home.component.css',
    standalone: false
})
export class CustomerCAFHomeComponent implements OnInit {
    getWallatData: any = [];
    WalletAmount: any = "";
    @Input() customerId;
    @Output() onEditProfile = new EventEmitter<any>();
    @ViewChild(InvoiceDetalisModelComponent)
    @ViewChild("Mobilenumber") Mobilenumber;

    InvoiceDetailModal: InvoiceDetalisModelComponent;
    invoicedialogRef: MatDialogRef<any> | null = null;
    demographicLabel: any;
    payMethod: string;
    invoiceMasterListData: any = [];
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
    customeroverviewDetails: any = {
        customerInformation: {},
        planInformation: {},
        ticketsList: new MatTableDataSource([]),
        ticketsListClosed: new MatTableDataSource([]),
        paymentsHistory: new MatTableDataSource([]),
        outstandingAmount: new MatTableDataSource([])
    };
    customerNetworkLocationDetailData: any;
    pincodeDD: any = [];
    custId: any = [""];
    selectedTabIndex: number = 0;
    PRE_CUST_CONSTANTS = PRE_CUST_CONSTANTS;
    POST_CUST_CONSTANTS = POST_CUST_CONSTANTS;
    sendPaymentLinkAccessCaf: boolean = false;
    custType: string = "";

    constructor(
        private customerManagementService: CustomermanagementService,
        private liveUserService: LiveUserService,
        private ticketManagementService: TicketManagementService,
        private revenueService: RevenueManagementService,
        private toastr: ToastrService,
        public statusCheckService: StatusCheckService,
        private fb: UntypedFormBuilder,
        public customerdetailsilsService: CustomerdetailsilsService,
        public serviceAreaService: ServiceAreaService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public commondropdownService: CommondropdownService,
        private invoiceMasterService: InvoiceMasterService,
        private systemService: SystemconfigService,
        public matdialog: MatDialog,
        public partnerService: PartnerService,
        public invoicePaymentListService: InvoicePaymentListService,
        private spinner: NgxSpinnerService,
        private loginService: LoginService,
        private route: ActivatedRoute,

    ) {
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
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
        this.invoicesPaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CAF_INVOICES_PAYMENT
                : POST_CUST_CONSTANTS.POST_CUST_CHARGE_CREATE
        );
        this.sendPaymentLinkAccessCaf = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PAYMENT_LINK_PRE_CUST_CAF
                : POST_CUST_CONSTANTS.PAYMENT_LINK_POST_CUST_CAF
        );
    }


    isOverviewShow: boolean = true;
    isProfileShow: boolean = false;
    isTicketsShow: boolean = false;
    isInvoicesShow: boolean = false;
    isPaymentsShow: boolean = false;
    isPlansShow: boolean = false;
    customerGroupForm: UntypedFormGroup;
    presentGroupForm: UntypedFormGroup;
    countries: any = countries;

    @ViewChild("PaymentConfirm") PaymentConfirm;
    paymentGroupForm: UntypedFormGroup;
    mpinForm: UntypedFormGroup;
    permanentGroupForm: UntypedFormGroup;
    iscustomerEdit = false;
    paymentConfirmationModal: boolean = false;
    step1Group: UntypedFormGroup;
    paymentstatusCount = RadiusConstants.TIMER_COUNT;
    step2Group: UntypedFormGroup;
    step3Group: UntypedFormGroup;
    step4Group: UntypedFormGroup;
    serviceareaCheck = true;
    serviceAreaData: any;
    inputMobileNumber: string = "";
    serviceAreaDATA: any;
    aclPreCustCode: any;
    presentAdressDATA: any = [];
    permentAdressDATA: any = [];
    paymentAdressDATA: any = [];
    partnerDATA: any = [];
    chargeDATA: any = [];
    isMpinFormSubmitted: boolean = false;
    FinalAmountList: any = [];
    dataChargePlan: any = [];
    paymentDataamount: any;
    paymentDatareferenceno: any;
    paymentDatapaymentdate: any;
    paymentDatapaymentMode: any;
    currency: string;
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    cutomerId;
    areaTitle = RadiusConstants.AREA;
    department = RadiusConstants.DEPARMENT;
    OlddiscountData = [];
    KraTitle = RadiusConstants.KRA_PIN;
    systemConfigCurrency: string;
    isDisplayConvertedAmount: boolean = false;
    customerPopName: any = "";
    isInvoiceToOrg: any = false;
    customerBill: "";
    customerCustDiscountListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCustDiscountListdatatotalRecords: String;
    currentPagecustomerCustDiscountListdata = 1;
    CustDiscountShowItemPerPage = 1;
    custCustDiscountList: any = [];
    custInvoiceToOrg: boolean;
    ifIndividualPlan = false;
    ifPlanGroup = false;
    planGroupName: any = "";
    activePlanNames: string = "";
    dataPlan = [];
    addChargeForm: UntypedFormGroup;
    planMappingList = [];
    savedConfig: any;
    searchInvoiceMasterFormGroup: UntypedFormGroup;
    currentPageinvoiceMasterSlab = 1;
    invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoiceMastertotalRecords: String;
    searchInvoiceData: any;
    customerAddressDetails: any;
    isInvoiceSearch = false;
    showItemPerPageInvoice = 1;
    @ViewChild("PaymentGateway") PaymentGateway;
    isPaymentGatewayConfigured: boolean = false;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    paymentGateway: any;
    paymentkeyValuePairs: { [key: string]: any } = {};
    isInvoiceDetail = false;
    custTrailPlanItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TrailPlanList = [];
    custShowTrailPlanShow = 1;
    currentTrailPlanListdata = 1;
    custCurrentPlanList;
    custFuturePlanList: any;
    invoiceId = new BehaviorSubject({
        invoiceId: ""
    });
    custExpiredPlanList: any;
    invoiceID = "";
    custID: number;
    exitBuy: boolean = true;
    mpinModal: boolean = false;
    masterSelected: boolean;

    _CWSCpasswordType = "password";
    CWSCshowPassword = false;

    invoiceList = [];
    displaySelectInvoiceDialog: boolean = false;
    CurrentPlanShowItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    futurePlanShowItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    expiredShowItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerFuturePlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerFuturePlanListdatatotalRecords: String;
    currentPagecustomerFuturePlanListdata = 1;
    customerExpiryPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerExpiryPlanListdatatotalRecords: number = 0;
    currentPagecustomerExpiryPlanListdata = 1;
    customerCurrentPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerCurrentPlanListdatatotalRecords: String;

    inputMobile = "";
    inputMobileSec = "";
    currentDiscountDataSource = new MatTableDataSource<any>([]);
    displayMpesaOptionsDialog: boolean;
    currentPagecustomerCurrentPlanListdata = 1;

    selectedMpesaOption: string = "";
    invoiceForMpesa: any;
    istrialplan: boolean = false;
    ngOnInit(): void {
        console.log("customer type", this.custType);
        this._CWSCpasswordType = "password";
        this.CWSCshowPassword = false;
        this.demographicLabel = RadiusConstants.DEMOGRAPHICDATA || [];
        this.mpinForm = this.fb.group({
            countryCode: [""],
            mobileNumber: ["", [Validators.required, Validators.maxLength(10)]]
        });
        this.getPlanDetails();
        this.commondropdownService.getsystemconfigList();
        this.getPaymentsHistory();
        this.getWalletAmount();
        this.outstandingAmount();
        this.getCustomersDetail(this.custId);
        this.getCustomerNetworkLocationDetail(this.custId);
        this.searchInvoiceMasterFormGroup = this.fb.group({
            billfromdate: [""],
            billrunid: [""],
            billtodate: [""],
            custMobile: ["", Validators.minLength(3)],
            custname: [""],
            docnumber: [""],
            customerid: [""]
        });
        this.openMyInvoice(this.custId);
        this.openCustomersPlan(this.customerId);

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
    }
    openCustomersPlan(id) {
        this.getcustFuturePlan(id, "");
        this.getcustExpiredPlan(id, "");
        this.getcustCurrentPlan(id, "");
        this.getTrailPlanList(this.customerId, "");
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

    openMyInvoice(id) {
        this.checkPaymentGatewayConfiguration();
        this.searchinvoiceMaster(id, "");
    }
    checkPaymentGatewayConfiguration() {
        // this.spinner.show();
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
        this.searchInvoiceMasterFormGroup.value.customerid = this.customerId;

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
            "&isInvoiceVoid=true"; ''
        this.revenueService.postMethod(url, dtoData).subscribe(
            (response: any) => {
                // const invoiceMasterListData = response.invoicesearchlist;
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
        const url = "/customers/" + this.customerId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerDetailData = response.customers;

                this.getLiveUser(response.customers.username);
                this.getTickets(response.customers.username);
                this.getClosedTickets(response.customers.username);

                this.customeroverviewDetails.customerInformation = {
                    name: `${response.customers.firstname} ${response.customers.lastname}`,
                    id: response.customers?.id,
                    account: response.customers?.acctno ?? "N/A",
                    status: response.customers?.status,
                    wallet: response.customers?.walletbalance ?? "N/A",
                    currency: response.customers?.currency ?? "N/A"
                };

                // this.customerId = response.customers?.id;
                // this.customerGroupForm.controls.calendarType.setValue('English');
                if (response.customers?.creditDocuments?.length) {
                    this.paymentDataamount = response.customers.creditDocuments[0].amount;
                    this.paymentDatareferenceno = response.customers.creditDocuments[0].referenceno;
                    this.paymentDatapaymentdate = response.customers.creditDocuments[0].paymentdate;
                    this.paymentDatapaymentMode = response.customers.creditDocuments[0].paymode;
                }
                // const paymentaddressType = response.customers.addressList.filter(
                //     key => key.addressType === "Payment"
                // );
                // if (paymentaddressType) {
                //     this.paymentAddressData = paymentaddressType;
                // } else {
                //     this.paymentAddressData = {
                //         fullAddress: ""
                //     };
                // }
                // const permanentaddressType = response.customers.addressList.filter(
                //     key => key.addressType === "Permanent"
                // );
                // console.log("permanentaddressType ======>>>>", permanentaddressType);
                // if (permanentaddressType) {
                //     this.permanentAddressData = permanentaddressType;
                // } else {
                //     this.permanentAddressData = {
                //         fullAddress: ""
                //     };
                // }

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
                // if (this.customerDetailData.popid) {
                //     if (this.statusCheckService.isActiveInventoryService) {
                //         let partnerurl = "/popmanagement/" + this.customerDetailData.popid;
                //         this.customerManagementService.getMethod(partnerurl).subscribe((response: any) => {
                //             this.customerPopName = response.data.name;

                //         });
                //     }
                // }

                // partner Name
                if (this.customerDetailData?.partnerid) {
                    const partnerurl = "/partner/" + this.customerDetailData.partnerid;
                    this.partnerService.getMethodNew(partnerurl).subscribe((response: any) => {
                        this.partnerDATA = response.partnerlist.name;

                        // console.log("partnerDATA", this.partnerDATA);
                    });
                }

                // serviceArea Name
                if (this.customerDetailData?.serviceareaid) {
                    const serviceareaurl = "/serviceArea/" + this.customerDetailData.serviceareaid;
                    this.savbillCommonBaseService.get(serviceareaurl).subscribe((response: any) => {
                        this.serviceAreaDATA = response.data.name;

                        // console.log("partnerDATA", this.serviceAreaDATA);
                    });
                }

                // Address
                if (
                    this.customerDetailData?.addressList?.length > 0 &&
                    this.customerDetailData?.addressList[0]?.addressType
                ) {
                    const areaurl = "/area/" + this.customerDetailData?.addressList[0]?.areaId;

                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                        this.presentAdressDATA = response.data;
                        this.presentAdressDATA.buildingNumber =
                            this.customerDetailData.addressList[0]?.buildingNumber;
                        this.serviceAreaAndBuildingNameFromCustomerId();
                        // console.log("presentAdressDATA", this.presentAdressDATA);
                    });
                }
                if (this.customerDetailData?.addressList?.length > 1) {
                    let j = 0;
                    while (j < this.customerDetailData?.addressList?.length) {
                        const addres1 = this.customerDetailData?.addressList[j]?.addressType;
                        if (addres1) {
                            if ("Payment" == addres1) {
                                const areaurl = "/area/" + this.customerDetailData?.addressList[j]?.areaId;
                                this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                    this.paymentAdressDATA = response.data;

                                    // console.log("paymentAdressDATA", this.paymentAdressDATA);
                                });
                            } else {
                                const areaurl = "/area/" + this.customerDetailData?.addressList[j]?.areaId;
                                this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                    this.permentAdressDATA = response.data;

                                    // console.log("permentAdressDATA", this.permentAdressDATA);
                                });
                            }
                        }
                        j++;
                    }
                }

                // if (this.customerDetailData?.planMappingList?.length > 0) {
                //     this.customerBill = this.customerDetailData.planMappingList[0].billTo;
                //     this.custInvoiceToOrg = this.customerDetailData.planMappingList[0].isInvoiceToOrg;
                // }

                // if (this.customerDetailData?.plangroupid) {
                //     this.ifIndividualPlan = false;
                //     this.ifPlanGroup = true;
                //     const planGroupurl =
                //         "/findPlanGroupById?planGroupId=" + this.customerDetailData.plangroupid;

                //     this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                //         this.planGroupName = response.planGroup.planGroupName;
                //     });
                // } else {
                //     this.ifIndividualPlan = true;
                //     this.ifPlanGroup = false;
                //     this.customerDetailData.planMappingList = this.customerDetailData?.planMappingList?.filter(
                //         data => data.custPlanStatus == "Active"
                //     );

                //     this.planMappingList = this.customerDetailData.planMappingList;
                //     while (plandatalength < this.customerDetailData.planMappingList.length) {
                //         const planId = this.customerDetailData.planMappingList[plandatalength].planId;
                //         let discount;
                //         if (
                //             this.customerDetailData.planMappingList[plandatalength].discount == null ||
                //             this.customerDetailData.planMappingList[plandatalength].discount == ""
                //         ) {
                //             discount = 0;
                //         } else {
                //             discount = this.customerDetailData.planMappingList[plandatalength].discount;
                //         }
                //         this.activePlanNames = "";
                //         if (
                //             this.customerDetailData.planMappingList[plandatalength].plangroup !=
                //             "Volume Booster" &&
                //             this.customerDetailData.planMappingList[plandatalength].plangroup !=
                //             "Bandwidth Booster"
                //         )
                //             this.activePlanNames =
                //                 this.activePlanNames +
                //                 this.customerDetailData.planMappingList[plandatalength].planName +
                //                 ",";

                //         const planurl = "/postpaidplan/" + planId;
                //         this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                //             this.dataPlan.push(response.postPaidPlan);
                //             // console.log("dataPlan", this.dataPlan);
                //         });

                //         this.customerManagementService
                //             .getofferPriceWithTax(planId, discount)
                //             .subscribe((response: any) => {
                //                 if (response.result.finalAmount) {
                //                     this.FinalAmountList.push(response.result.finalAmount);
                //                 } else {
                //                     this.FinalAmountList.push(0);
                //                 }
                //             });
                //         plandatalength++;
                //     }
                //     if (this.customerDetailData.indiChargeList.length > 0) {
                //         this.addChargeForm.patchValue({
                //             chargeAdd: true
                //         });
                //     }
                // }
                if (this.customerDetailData?.indiChargeList?.length > 0) {
                    this.customerDetailData.indiChargeList.forEach(element => {
                        if (element.planid) {
                            const url = "/postpaidplan/" + element.planid;
                            this.customerManagementService.getMethod(url).subscribe((response: any) => {
                                this.dataChargePlan.push(response.postPaidPlan);
                            });
                        }
                    });
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

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
    mobileError: boolean = false;

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

    showMpinModal(invoice) {
        this.spinner.hide();
        this.mpinModal = true;
        this.mpinForm.controls.countryCode.setValue(this.commondropdownService.commonCountryCode);
        this.mpinForm.controls.mobileNumber.setValue(this.customerDetailData.mobile);
        // this.mpinForm.controls.mobileNumber.reset();
    }

    createStepGroups() {
        // Step 1: Basic Details - All Required Fields
        const step1Controls: any = {
            // Required fields
            firstname: this.customerGroupForm.get('firstname'),
            lastname: this.customerGroupForm.get('lastname'),
            contactperson: this.customerGroupForm.get('contactperson'),
            calendarType: this.customerGroupForm.get('calendarType'),
            customerType: this.customerGroupForm.get('customerType'),
            customerCategory: this.customerGroupForm.get('customerCategory'),

            // Conditional required - AAA Username/Password (required if not credential match)
            username: this.customerGroupForm.get('username'),
            password: this.customerGroupForm.get('password')
        };

        // Add login credentials only if not in edit mode
        if (!this.iscustomerEdit) {
            step1Controls.loginUsername = this.customerGroupForm.get('loginUsername');
            step1Controls.loginPassword = this.customerGroupForm.get('loginPassword');
        }

        this.step1Group = this.fb.group(step1Controls);
        // Step 2: Contact Details
        this.step2Group = this.fb.group({
            mobile: this.customerGroupForm.get('mobile'),
            countryCode: this.customerGroupForm.get('countryCode')
        });

        // Step 3: Subscriber Location Details - Required Fields (from presentGroupForm)
        this.step3Group = this.fb.group({
            serviceareaid: this.presentGroupForm.get('serviceareaid'),
            partnerid: this.presentGroupForm.get('partnerid'),
            landmark: this.presentGroupForm.get('landmark'),
            pincodeId: this.presentGroupForm.get('pincodeId'),
            areaId: this.presentGroupForm.get('areaId')
            // Other fields like building_mgmt_id, subareaId, buildingNumber, etc. are optional
        });
        this.step4Group = this.fb.group({});
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
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }
    getDemographicLabel(currentName: string): string {
        if (!this.demographicLabel || this.demographicLabel.length === 0) {
            return currentName;
        }

        const label = this.demographicLabel.find(item => item.currentName === currentName);
        return label ? label.newName : currentName;
    }

    getLiveUser(username: string) {
        this.liveUserService
            .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                users: [username]
            })
            .subscribe({
                next: (res: any) => {
                    if (res.liveUsers && res.liveUsers.length > 0) {
                        this.customeroverviewDetails.customerInformation.connectionStatus = "Oneline";
                    } else {
                        this.customeroverviewDetails.customerInformation.connectionStatus = "Offline";
                    }
                },
                error: (err: any) => {
                    console.log("Error", err);
                }
            });
    }

    getPlanDetails() {
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerId +
            "?isAllRequired=true&isNotChangePlan=true";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.custCurrentPlanList = response.dataList;
                this.customeroverviewDetails.planInformation = {
                    name:
                        response.dataList && response.dataList.length > 0
                            ? response.dataList[0].planName
                            : "N/A",
                    renewalDate:
                        response.dataList && response.dataList.length > 0 ? response.dataList[0].endDate : "N/A"
                };

            },
            (error: any) => { }
        );
    }


    getClosedTickets(username: string) {
        const url = "/case/case/search";

        const data = {
            filters: [
                {
                    filterColumn: "TICKET_STATUS",
                    filterListValues: [
                        "Raise and Close",
                        "Closed"
                    ]
                },
                {
                    filterValue: username,
                    filterColumn: "CUSTOMER_USERNAME"
                }
            ],
            page: 1,
            pageSize: 3,
            sortBy: "createdate",
            sortOrder: 0
        };

        this.ticketManagementService.postMethod(url, data).subscribe({
            next: (res: any) => {

                const allTickets = res.dataList || [];


                this.customeroverviewDetails.ticketsListClosed.data = allTickets
                    .filter(t =>
                        ["Raise and Close", "Closed"].includes(t.caseStatus)
                    )
                    .slice(-3);

                this.customeroverviewDetails.ticketsListClosed._updateChangeSubscription?.();
            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }
    getTickets(username: string) {
        const url = "/case/case/search";
        const data = {
            filters: [
                {
                    filterColumn: "TICKET_STATUS",
                    filterListValues: [
                        "In Progress",
                        "Open",
                        "Resolved"
                    ]
                },
                {
                    filterValue: username,
                    filterColumn: "CUSTOMER_USERNAME"
                }
            ],
            page: 1,
            pageSize: 20,
            sortBy: "createdate",
            sortOrder: 0
        };
        this.ticketManagementService.postMethod(url, data).subscribe({
            next: (res: any) => {
                const allTickets = res.dataList || [];
                this.customeroverviewDetails.ticketsList.data = allTickets.filter(t =>
                    ["Open", "In Progress", "Resolved"].includes(t.caseStatus)
                );


                // If using table render
                this.customeroverviewDetails.ticketsList._updateChangeSubscription?.();
                this.customeroverviewDetails.ticketsListClosed._updateChangeSubscription?.();
                // console.log("Tickets", res);
                // if (status === "open" || status === "In Progress" || status === 'Resolved') this.customeroverviewDetails.ticketsList.data = res.dataList;
                // else this.customeroverviewDetails.ticketsListClosed.data = res.dataList;
            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }
    getWalletAmount() {
        const data = {
            CREATE_DATE: "",
            END_DATE: "",
            amount: "",
            balAmount: "",
            custId: this.customerId,
            description: "",
            id: "",
            refNo: "",
            transcategory: "",
            transtype: ""
        };
        const url = "/wallet";
        this.revenueService.postMethod(url, data).subscribe((response: any) => {
            this.getWallatData = response;
            this.WalletAmount = response.customerWalletDetails;



        });
    }
    formatDate(dateString: string): string {
        if (!dateString) return "";

        const [date] = dateString.split(" ");

        const [day, month, year] = date.split("-");
        return `${day}-${month}-${year}`;
    }
    get firstThreePayments() {
        return this.customeroverviewDetails?.paymentsHistory?.data.slice(0, 3) || [];
    }

    getPaymentsHistory() {
        const url = '/paymentHistory/' + this.customerId;
        this.revenueService.getMethod(url).subscribe({
            next: (res: any) => {
                // this.customeroverviewDetails.ticketsList.data = res.dataList;
                this.customeroverviewDetails.paymentsHistory.data = res.dataList;
            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }


    sendPaymentLink() {
        const url = "/generatePaymentLink/" + this.customerId;
        this.customerManagementService.postMethod(url, null).subscribe(
            (response: any) => {
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Into!');

                } else {
                    let payData = response.data;
                    if (response.data == null) {
                        this.toastr.info(`${response.responseMessage}`, 'Something went wrong!');

                    } else {
                        window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
                    }
                }
            },
            (error: any) => {
                console.log(error, "error");
                if (error.responseCode === 417) {
                    this.toastr.info(`${error.responseMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                }
            }
        );
    }

    raiseTicket() {
        // this.router.navigate([`/home/customer/details/Prepaid/tickets/${this.customerId}`]);
    }

    outstandingAmount() {

        const url =
            "/trial/invoice/search?billrunid=" +
            "&docnumber=" +
            "&customerid=" +
            this.customerId +
            "&billfromdate=" +
            "&billtodate=" +
            "&custmobile=" +
            "&isInvoiceVoid=" +
            "&isOutstandingDue=true"
            ;

        this.revenueService.postMethod(url, { page: 1, pageSize: 1000 }).subscribe({
            next: (res: any) => {

                this.customeroverviewDetails.outstandingAmount = res.invoicesearchlist;



            }
        });
    }

    getConnectionStatusClass(status: string): string {
        switch (status.toLowerCase()) {
            case "online":
                return "Online";
            case "offline":
                return "Offline";
            case "active":
                return "Active";
            case "inactive":
                return "Inactive";
            case "Rejected":
                return "Rejected";
            case "NewActivation":
                return "NewActivation";
            default:
                return "Expired";
        }
    }
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
                this.payMethod = 'MPESA';
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

    hidepaymentConfirmDialog() {
        // this.closePaymentConfirm.emit();
        this.matdialog.closeAll();
        this.paymentConfirmationModal = false;
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

    hideMpinModal() {
        // this.closeMobilenumber.emit();
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

    editProfile() {
        this.onEditProfile.emit(this.customeroverviewDetails?.customerInformation?.id);
        // this.router.navigate([`/home/customer/edit/${this.custType}/${this.customerId}`]);
    }

    customerData = {
        name: "Lemor Nova",
        id: "8303789",
        account: "27-628267",
        status: "Active",
        connectionStatus: "Online"
    };

    planData = {
        name: "Family 70 GB",
        remainingDays: 9
    };

    walletData = {
        currency: "KES",
        amount: 6300
    };

    usageData = {
        used: 47,
        total: 70
    };

    outstandingData = {
        currency: "UGX",
        amount: 15000
    };

    paymentsData = [
        {
            payment: "0000465",
            dueDate: "01-Jun-2025",
            amount: "UGX 3000",
            amountClass: "amount-green"
        },
        {
            payment: "0000575",
            dueDate: "25-Jun-2025",
            amount: "UGX 2500",
            amountClass: "amount-orange"
        }
    ];

    lastPayments = [
        {
            payment: "0000365",
            dueDate: "30-Jun-2025",
            amount: "UGX 3000",
            amountClass: "amount-green"
        }
    ];

    openTickets = [
        {
            ticketNo: "2578",
            type: "Inquiry",
            started: "07-July-2025",
            call: "Received"
        }
    ];

    closedTickets = [
        {
            ticketNo: "2888",
            type: "Inquiry",
            started: "25-July-2025",
            call: "Received"
        }
    ];
    generatePdfAccess: boolean = false;
    reprintAccess: boolean = false;
    viewInvoiceAccess: boolean = false;
    invoicesPaymentAccess: boolean = false;
    invoice: any;
    paymentColumns = ["paymentdate", "paymode", "paymentreferenceno", "amount", "adjustedAmount", "status"];
    outstandingColumns = ["docnumber", "createdate", "totalamount", "adjustedAmount"];
    ticketColumns = ["ticketNo", "type", "started", "call"];
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
    onTabChange(event: any) {
        this.isOverviewShow = event.tab.textLabel === "Overview";
        this.isProfileShow = event.tab.textLabel === "Profile";
        this.isTicketsShow = event.tab.textLabel === "Tickets";
        this.isInvoicesShow = event.tab.textLabel === "Invoices";
        this.isPaymentsShow = event.tab.textLabel === "Payments";
        this.isPlansShow = event.tab.textLabel === "Plans";
    }

    getUsagePercentage(): number {
        if (this.usageData.total === 0) return 0;
        return (this.usageData.used / this.usageData.total) * 100;
    }

    handleBackToList() {
        this.selectedTabIndex = 0;
        this.onTabChange({ tab: { textLabel: "Overview" } });
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
}
