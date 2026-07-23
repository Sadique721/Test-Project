import { DatePipe } from "@angular/common";
import { Component, OnInit, ViewChild, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { CustomermanagementService } from "src/app/service/customermanagement.service";

import * as FileSaver from "file-saver";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { BehaviorSubject } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { InvoiceDetailsService } from "src/app/service/invoice-details.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { RecordPaymentService } from "src/app/service/record-payment.service";

import { LiveUserService } from "src/app/service/live-user.service";
import { InvoiceDetalisModelComponent } from "../invoice-detalis-model/invoice-detalis-model.component";
import { InvoicePaymentDetailsModalComponent } from "../invoice-payment-details-modal/invoice-payment-details-modal.component";

import { CustomerplanGroupDetailsModalComponent } from "src/app/components/customerplan-group-details-modal/customerplan-group-details-modal.component";
import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { WorkflowAuditDetailsModalComponent } from "../workflow-audit-details-modal/workflow-audit-details-modal.component";
import { SystemconfigService } from "../../service/systemconfig.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { PartnerService } from "src/app/service/partner.service";
import { SETTINGS } from "src/app/constants/aclConstants";

import { UntypedFormArray } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { MatTabChangeEvent } from "@angular/material/tabs";
declare var $: any;

@Component({
    selector: "app-myorganizationcustomer",
    templateUrl: "./myorganizationcustomer.component.html",
    styleUrls: ["./myorganizationcustomer.component.css"],
    standalone: false
})
export class MyorganizationcustomerComponent implements OnInit {
    @ViewChild('detailDialog') detailDialog: TemplateRef<any>;
    @ViewChild('walletDialog') walletDialog: TemplateRef<any>;
    @ViewChild('invoicePaymentDialog') invoicePaymentDialog: TemplateRef<any>;
    @ViewChild('assignApproveDialog') assignApproveDialog: TemplateRef<any>;
    @ViewChild('reAssignPlanDialog') reAssignPlanDialog: TemplateRef<any>;
    @ViewChild('addCreditNoteDialog') addCreditNoteDialog: TemplateRef<any>;
    @ViewChild('customerLedgerSearchTemplate') customerLedgerSearchTemplate: TemplateRef<any>;
    @ViewChild(InvoiceDetalisModelComponent) invoiceDetailModal: InvoiceDetalisModelComponent;
    dialogRef: MatDialogRef<any>;
    invoiceDisplayedColumns = [
        'select', 'customerName', 'custRefName', 'docnumber', 'createdByName', 'totalamount',
        'adjustedAmount', 'billrunstatus', 'billdate', 'status', 'actions'
    ];
    displayedColumns: string[] = [
        'name',
        'username',
        'serviceArea',
        'mobile',
        'acctno',
        'connectionStatus'
    ];
    ledgerDisplayedColumns: string[] = [
        'create_DATE',
        'receiptNo',
        'invoiceNo',
        'transcategory',
        'debit',
        'credit',
        'balAmount'
    ];

    selectedCustomer: any = null;
    selectedTabIndex: number = 0;
    public loginService: LoginService;
    AclClassConstants;
    AclConstants;
    remark: any;
    walletAccess: boolean = false;
    ledgerAccess: boolean = false;
    invoiceAccess: boolean = false;
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private customerManagementService: CustomermanagementService,
        public partnerService: PartnerService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private revenueManagementService: RevenueManagementService,
        public datepipe: DatePipe,
        private recordPaymentService: RecordPaymentService,
        private invoiceDetailsService: InvoiceDetailsService,
        private liveUserService: LiveUserService,
        public PaymentamountService: PaymentamountService,
        public invoicePaymentListService: InvoicePaymentListService,
        public systemService: SystemconfigService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        let staffID = localStorage.getItem("userId");
        this.staffID = Number(staffID);
        this.invoiceAccess = loginService.hasPermission(SETTINGS.ORGANIZATION__INVOICES);
        this.ledgerAccess = loginService.hasPermission(SETTINGS.ORGANIZATION__LEDGER);
        this.walletAccess = loginService.hasPermission(SETTINGS.ORGANIZATION__WALLET);
        this.loginService = loginService;
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
    }
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });
    assignPLANForm: UntypedFormGroup;
    @ViewChild(InvoiceDetalisModelComponent)
    InvoiceDetailModal: InvoiceDetalisModelComponent;

    @ViewChild(InvoicePaymentDetailsModalComponent)
    invoicePaymentDetailModal: InvoicePaymentDetailsModalComponent;

    @ViewChild(CustomerplanGroupDetailsModalComponent)
    custPlanGroupDataModal: CustomerplanGroupDetailsModalComponent;
    @ViewChild(WorkflowAuditDetailsModalComponent)
    workFlowAuditModal: WorkflowAuditDetailsModalComponent;

    paymappingItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    payMappinftotalRecords: String;
    currentPagePayMapping = 1;

    overChargeListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    overChargeListtotalRecords: String;
    currentPageoverChargeList = 1;

    uploadDocumentListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    uploadDocumentListtotalRecords: String;
    currentPageoverUploadDocumentList = 1;

    custMacMapppingListtemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacMapppingListtotalRecords: String;
    currentPagecustMacMapppingList = 1;

    custLedgerItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custLedgertotalRecords: String;
    currentPagecustLedgerList = 1;

    custChargeDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custChargeDeatiltotalRecords: String;
    currentPagecustChargeDeatilList = 1;

    custPlanDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custPlanDeatiltotalRecords: String;
    currentPagecustPlanDeatilList = 1;

    custMacAddItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacAddtotalRecords: String;
    currentPagecustMacAddList = 1;

    customerLedgerDetailData: any = [];
    customertotalRecords = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPage = 1;
    searchkey: string;
    searchData: any = [];
    listView = true;
    isInvoiceDetail = false;
    custID: number;

    custLedgerForm: UntypedFormGroup;
    custLedgerSubmitted = false;
    // fieldEnable = false;

    ifMyInvoice = false;
    searchInvoiceMasterFormGroup: UntypedFormGroup;
    currentPageinvoiceMasterSlab = 1;
    invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoiceMastertotalRecords: number;
    searchInvoiceData: any;
    invoiceMasterListData: any = [];
    isInvoiceSearch = false;

    showItemPerPageInvoice = 1;
    InvoiceDATA = new BehaviorSubject({
        InvoiceDATA: ""
    });

    invoiceId = new BehaviorSubject({
        invoiceId: ""
    });

    planGroupcustid = new BehaviorSubject({
        planGroupcustid: ""
    });

    partnerDATA: any = [];
    presentAdressDATA: any = [];
    permentAdressDATA: any = [];
    paymentAdressDATA: any = [];
    chargeDATA = [];
    dataPlan = [];
    postpaidplanData: any;
    serviceAreaDATA: any;
    paymentData: any;
    paymentDataamount: any;
    paymentDatareferenceno: any;
    paymentDatapaymentdate: any;
    paymentDatapaymentMode: any;
    FinalAmountList: any = [];
    paymentAddressData: any = [];
    permanentAddressData: any = [];

    customerBill: "";
    planGroupName: "";
    isInvoiceToOrg: any = false;
    ifIndividualPlan: boolean = false;
    ifModelIsShow: boolean = false;
    ifPlanGroup: boolean = false;
    dataChargePlan = [];
    custInvoiceToOrg: boolean;

    startDateCustLedger: any = "";
    endDateCustLedger: any = "";
    customerLedgerData: any = [];
    customerLedgerListData: any = [];
    legershowItemPerPage = 1;

    postdata: any = {
        CREATE_DATE: "",
        END_DATE: "",
        id: "",
        amount: "",
        balAmount: "",
        custId: "",
        description: "",
        refNo: "",
        transcategory: "",
        transtype: ""
    };
    customerLedgerSearchKey: string;
    isCustomerLedgerOpen = false;
    ifWalletMenu = false;
    getWallatData: any = [];
    isCustomerDetailOpen = false;
    isCustomerDetailSubMenu = false;

    searchDeatil = "";
    searchOption: "";
    customerListDataselector: any = [];

    currentPagecustomerListdata = 1;
    customerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerListdatatotalRecords: any;
    customerListData: MatTableDataSource<any> = new MatTableDataSource<any>()
    viewcustomerListData: any = [];
    searchkey2 = "";
    searchOptionSelect: any = [];
    staffList: any = [];
    approve = false;
    // reject = false;
    selectedStaff: number;
    assignStaffForm: UntypedFormGroup;
    currentPageAudit: any;
    itemsPerPageAudit: any;
    workflowAuditData: any[];
    MastertotalRecords: any;
    currency: string;
    customerData = [];
    custQuotaList: any = [];
    custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custQuotaListtotalRecords: String;
    currentPagecustQuotaList = 1;
    custFullAddress = "";

    invoicePaymentData = [];
    invoiceID = "";
    invoicePaymentItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPageinvoicePaymentList = 1;
    invoicePaymenttotalRecords: number;
    ifInvoicePayment = false;

    totaladjustedAmount = 0;
    allchakedPaymentData = [];
    ispaymentChecked = false;
    allIsChecked = false;
    isSinglepaymentChecked = false;

    //// ..........////
    isInvoiceChecked = false;
    allInvoiceChecked = false;
    chakedInvoiceData = [];
    isSingleInChecked = false;
    staffID: any;
    ngOnInit(): void {
        this.assignStaffForm = this.fb.group({
            staffId: [""],
            remark: ["", Validators.required],
            invoiceId: [""]
        });
        this.custLedgerForm = this.fb.group({
            startDateCustLedger: ["", Validators.required],
            endDateCustLedger: ["", Validators.required]
        });

        this.searchInvoiceMasterFormGroup = this.fb.group({
            billfromdate: [""],
            billrunid: [""],
            billtodate: [""],
            custMobile: ["", Validators.minLength(3)],
            custname: [""],
            docnumber: [""],
            customerid: [""],
            staffId: [""],
            branchId: [""],
            businessunit: [""],
            planId: [""],
            serviceId: [""]
        });
        this.assignPLANForm = this.fb.group({
            remark: [""]
        });

        this.paymentFormGroup = this.fb.group({
            amount: ["", [Validators.required, Validators.min(1)]],
            customerid: ["", Validators.required],
            // paymentdate: [""],
            paymentreferenceno: [""],
            paymode: ["Credit Note"],
            referenceno: ["", Validators.required],
            remark: ["", Validators.required],
            invoiceId: ["", Validators.required],
            type: ["creditnote"],
            paytype: ["creditnote"]
        });
        // customer get data
        this.getcustomerList("");
        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: "",
            pageSize: ""
        };
    }
    onTabChange(event: MatTabChangeEvent): void {
        if (event.tab.textLabel === "Invoices") {
            this.openMyInvoice(this.customerLedgerDetailData.id);
        }
        if (event.tab.textLabel === "Ledger") {
            this.getCustomersLedger(this.customerLedgerDetailData.id, '');
        }
        if (event.tab.textLabel === "Wallet") {
            this.addWalletIncustomer(this.customerLedgerDetailData.id);
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagecustomerListdata > 1) {
            this.currentPagecustomerListdata = 1;
        }
        if (!this.searchkey) {
            this.getcustomerList(this.showItemPerPage);
        } else {
            this.searchcustomer();
        }
    }

    pageChangedcustomerList(pageNumber) {
        this.currentPagecustomerListdata = pageNumber;
        if (this.searchkey) {
            this.searchcustomer();
        } else {
            this.getcustomerList("");
        }
    }
    customerList() {
        this.isCustomerDetailOpen = false;
        this.listView = true;
        // Reset or clear other flags if needed
    }

    searchcustomer(): void {
        if ((!this.searchkey && !this.searchkey2) || this.searchkey !== this.searchDeatil.trim() || this.searchkey2 !== this.searchOption.trim()) {
            this.currentPagecustomerListdata = 1;
        }
        this.searchkey = this.searchDeatil.trim();
        this.searchkey2 = this.searchOption.trim();

        if (this.showItemPerPage !== 1) {
            this.customerListdataitemsPerPage = this.showItemPerPage;
        }

        this.searchData.filters[0].filterValue = this.searchDeatil.trim();
        this.searchData.filters[0].filterColumn = this.searchOption.trim();
        this.searchData.page = this.currentPagecustomerListdata;
        this.searchData.pageSize = this.customerListdataitemsPerPage;

        const url = "/customers/search/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;

        this.customerManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.customerListData.data = response.customerList || [];
                const usernameList: string[] = this.customerListData.data.map(item => item.username);

                this.liveUserService.postMethod("/liveUser/isCustomersOnlineOrOffline", { users: usernameList }).subscribe((res: any) => {
                    const liveUsers: string[] = res.liveusers || [];
                    this.customerListData.data.forEach(element => {
                        element.connectionMode = liveUsers.includes(element.username) ? "Online" : "Offline";
                    });
                });

                this.customerListdatatotalRecords = response.pageDetails?.totalRecords || 0;
            },
            (error: any) => {
                this.customerListdatatotalRecords = 0;
                if (error.error.status === 404) {
                    this.messageService.add({
                        severity: "info",
                        summary: "Info",
                        detail: error.error.msg,
                        icon: "far fa-times-circle"
                    });
                    this.customerListData.data = [];
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

    closeParentCustt() {
        this.ifModelIsShow = false;
    }

    clearSearchcustomer() {
        this.getcustomerList("");
        this.searchDeatil = "";
        this.searchOption = "";
        // this.fieldEnable = false;
        this.currentPagecustomerListdata = 1;
    }

    getcustomerList(list): void {
        this.searchkey = "";
        this.searchkey2 = "";

        if (list) {
            this.customerListdataitemsPerPage = list;
        }

        this.customerListData.data = [];

        const postpaidUrl = `/customers/list/${RadiusConstants.CUSTOMER_TYPE.POSTPAID}?orgcusttype=true`;
        const prepaidUrl = `/customers/list/${RadiusConstants.CUSTOMER_TYPE.PREPAID}?orgcusttype=true`;
        const emptyPayload = {};

        this.customerManagementService.postMethod(postpaidUrl, emptyPayload).subscribe((postpaidResp: any) => {
            const postpaidList = postpaidResp.customerList || [];
            this.customerListData.data = [...this.customerListData.data, ...postpaidList];

            this.customerManagementService.postMethod(prepaidUrl, emptyPayload).subscribe((prepaidResp: any) => {
                const prepaidList = prepaidResp.customerList || [];
                this.customerListData.data = [...this.customerListData.data, ...prepaidList];

                if (this.customerListData.data.length > 0) {
                    const usernameList = this.customerListData.data.map(cust => cust.username);
                    this.liveUserService.postMethod("/liveUser/isCustomersOnlineOrOffline", { users: usernameList }).subscribe((liveUserRes: any) => {
                        const liveUsers = liveUserRes.liveusers || [];
                        this.customerListData.data.forEach(cust => {
                            cust.connectionMode = liveUsers.includes(cust.username) ? "Online" : "Offline";
                        });
                        this.customerListDataselector = [...this.customerListData.data];
                        this.customerListdatatotalRecords = this.customerListData.data.length;
                    });
                } else {
                    this.customerListDataselector = [];
                    this.customerListdatatotalRecords = 0;
                }
            });
        });
    }

    TotalLedgerItemPerPage(event) {
        this.legershowItemPerPage = Number(event.value);
        if (this.currentPagecustLedgerList > 1) {
            this.currentPagecustLedgerList = 1;
        }
        if (!this.customerLedgerSearchKey) {
            this.getCustomersLedger(this.customerLedgerDetailData.id, this.legershowItemPerPage);
        } else {
            this.searchCustomerLedger();
        }
    }

    pageChangedcustledgerList(pageNumber) {
        this.currentPagecustLedgerList = pageNumber;
        this.getCustomersLedger(this.customerLedgerDetailData.id, "");
    }

    listCustomer() {
        this.listView = true;
        this.isCustomerDetailOpen = false;
        this.isCustomerDetailSubMenu = false;
        this.isCustomerLedgerOpen = false;
        this.ifMyInvoice = false;
        this.ifWalletMenu = false;
    }

    customerDetailOpen(custId) {
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.isCustomerLedgerOpen = false;
        this.getCustomersDetail(custId);
        this.ifMyInvoice = false;
        this.ifWalletMenu = false;
        this.listView = false;
        // this.getCustQuotaList(custId)
    }

    getCustQuotaList(custId) {
        this.customerManagementService.getCustQuotaList(custId).subscribe(
            (response: any) => {
                this.custQuotaList = response.custQuotaList;
            },
            (error: any) => {
                // console.log(error, "error")
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

    pageChangedCustQuotaList(pageNumber) {
        this.currentPagecustQuotaList = pageNumber;
    }

    customerLedgerOpen() {
        this.isCustomerDetailOpen = true;
        this.isCustomerDetailSubMenu = true;
        this.isCustomerLedgerOpen = true;
        this.ifWalletMenu = false;
        this.listView = true;
        this.ifMyInvoice = false;
    }

    getCustomersLedger(custId, size) {
        let page_list;
        this.customerLedgerSearchKey = "";
        if (size) {
            page_list = size;
            this.custLedgerItemPerPage = size;
        } else {
            if (this.legershowItemPerPage == 1) {
                this.custLedgerItemPerPage = this.pageITEM;
            } else {
                this.custLedgerItemPerPage = this.legershowItemPerPage;
            }
        }
        const url = "/customerLedgers";
        this.postdata.custId = custId;
        this.revenueManagementService.postMethod(url, this.postdata).subscribe(
            (response: any) => {
                this.customerLedgerData = response.customerLedgerDtls;
                this.customerLedgerListData =
                    response.customerLedgerDtls.customerLedgerInfoPojo.debitCreditDetail;
                // this.customerLedgerData?.currency
                //   ? (this.currency = this.customerLedgerData?.currency)
                //   : this.systemService
                //       .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                //       .subscribe((res: any) => {
                //         this.currency = res.data.value;
                //       });
                // console.log("this.customerLedgerData", this.customerLedgerData);
                this.customerLedgerOpen();
            },
            (error: any) => {
                // console.log(error, "error")
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

    pageChangedcustChargeDetailList(pageNumber) {
        this.currentPagecustChargeDeatilList = pageNumber;
    }

    pageChangedcustPlanDetailList(pageNumber) {
        this.currentPagecustPlanDeatilList = pageNumber;
    }

    pageChangedcustMacAddDetailList(pageNumber) {
        this.currentPagecustMacAddList = pageNumber;
    }
    onSelectCustomer(customer) {
        this.selectedCustomer = customer;
        // Set flags to true if needed
        this.invoiceAccess = true;
        this.ledgerAccess = true;
        this.walletAccess = true;
    }

    getCustomersDetail(custId: number): void {
        // Initialize/reset variables
        this.presentAdressDATA = [];
        this.permentAdressDATA = [];
        this.paymentAdressDATA = [];
        this.partnerDATA = [];
        this.chargeDATA = [];
        this.paymentDataamount = "";
        this.paymentDatareferenceno = "";
        this.paymentDatapaymentdate = "";
        this.paymentDatapaymentMode = "";
        this.FinalAmountList = [];

        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerLedgerDetailData = response.customers || {};

                const creditDocuments = this.customerLedgerDetailData.creditDocuments || [];
                if (creditDocuments.length > 0) {
                    this.paymentDataamount = creditDocuments[0]?.amount || "";
                    this.paymentDatareferenceno = creditDocuments[0]?.referenceno || "";
                    this.paymentDatapaymentdate = creditDocuments[0]?.paymentdate || "";
                    this.paymentDatapaymentMode = creditDocuments[0]?.paymode || "";
                }

                const addressList = this.customerLedgerDetailData.addressList || [];
                if (addressList.length > 1) {
                    const paymentAddressType = addressList.filter(addr => addr.addressType === "Payment");
                    this.paymentAddressData = paymentAddressType.length ? paymentAddressType : { fullAddress: "" };

                    const permanentAddressType = addressList.filter(addr => addr.addressType === "Permanent");
                    this.permanentAddressData = permanentAddressType.length ? permanentAddressType : { fullAddress: "" };
                }

                // Fetch partner data if partnerid exists
                if (this.customerLedgerDetailData.partnerid) {
                    const partnerurl = "/partner/" + this.customerLedgerDetailData.partnerid;
                    this.partnerService.getMethodNew(partnerurl).subscribe(
                        (res: any) => {
                            this.partnerDATA = res.partnerlist?.name || "";
                        }
                    );
                }

                // Fetch service area data if serviceareaid exists
                if (this.customerLedgerDetailData.serviceareaid) {
                    const serviceareaurl = "/serviceArea/" + this.customerLedgerDetailData.serviceareaid;
                    this.savbillCommonBaseService.get(serviceareaurl).subscribe(
                        (res: any) => {
                            this.serviceAreaDATA = res.data?.name || "";
                        }
                    );
                }

                // Handle present address
                if (addressList.length > 0) {
                    this.custFullAddress = addressList[0]?.fullAddress || "";
                    if (addressList[0]?.addressType) {
                        const areaurl = "/area/" + addressList[0].areaId;
                        this.savbillCommonBaseService.get(areaurl).subscribe(
                            (res: any) => {
                                this.presentAdressDATA = res.data || [];
                            }
                        );
                    }
                }

                // Handle other addresses
                if (addressList.length > 1) {
                    addressList.forEach(addr => {
                        if (addr?.addressType) {
                            const areaurl = "/area/" + addr.areaId;
                            this.savbillCommonBaseService.get(areaurl).subscribe(
                                (res: any) => {
                                    if (addr.addressType === "Payment") {
                                        this.paymentAdressDATA = res.data || [];
                                    } else {
                                        this.permentAdressDATA = res.data || [];
                                    }
                                }
                            );
                        }
                    });
                }

                // Plan mapping list
                const planMappingList = this.customerLedgerDetailData.planMappingList || [];
                if (planMappingList.length > 0) {
                    planMappingList.reverse();
                    this.customerBill = planMappingList[0]?.billTo || "";
                    this.custInvoiceToOrg = planMappingList[0]?.isInvoiceToOrg || "";
                }

                // Handle Plan Group
                if (this.customerLedgerDetailData.plangroupid) {
                    this.ifIndividualPlan = false;
                    this.ifPlanGroup = true;
                    const planGroupurl = "/findPlanGroupById?planGroupId=" + this.customerLedgerDetailData.plangroupid;
                    this.customerManagementService.getMethod(planGroupurl).subscribe(
                        (res: any) => {
                            this.planGroupName = res.planGroup?.planGroupName || "";
                        }
                    );
                } else {
                    this.ifIndividualPlan = true;
                    this.ifPlanGroup = false;
                    let plandatalength = 0;
                    while (plandatalength < planMappingList.length) {
                        const planId = planMappingList[plandatalength]?.planId;
                        const discount = planMappingList[plandatalength]?.discount || 0;

                        const planurl = "/postpaidplan/" + planId;
                        this.customerManagementService.getMethod(planurl).subscribe(
                            (res: any) => {
                                this.dataPlan.push(res.postPaidPlan || {});
                            }
                        );

                        this.customerManagementService.getofferPriceWithTax(planId, discount).subscribe(
                            (res: any) => {
                                if (res.result?.finalAmount != null) {
                                    this.FinalAmountList.push(res.result.finalAmount);
                                } else {
                                    this.FinalAmountList.push(0);
                                }
                            }
                        );
                        plandatalength++;
                    }
                }

                // Charger data
                const indiChargeList = this.customerLedgerDetailData.indiChargeList || [];
                if (indiChargeList.length > 0) {
                    indiChargeList.forEach(element => {
                        if (element.planid) {
                            const url = "/postpaidplan/" + element.planid;
                            this.customerManagementService.getMethod(url).subscribe(
                                (res: any) => {
                                    this.dataChargePlan.push(res.postPaidPlan || {});
                                }
                            );
                        }
                    });
                }

                // Cust Quota List
                this.customerManagementService.getCustQuotaList(this.customerLedgerDetailData.id).subscribe(
                    (res: any) => {
                        this.custQuotaList = res.custQuotaList || [];
                    }
                );

            },
            (error: any) => {
                this.messageService.add({
                    severity: "error",
                    summary: "Error",
                    detail: error?.error?.ERROR || "An error occurred",
                    icon: "far fa-times-circle"
                });
            }
        );
    }


    getCustPlanGroupDataopen(id, planGroupcustid) {
        this.PaymentamountService.show(id);
        this.planGroupcustid.next({
            planGroupcustid
        });
    }

    searchCustomerLedger() {
        if (
            !this.customerLedgerSearchKey ||
            this.customerLedgerSearchKey !== this.custLedgerForm.value
        ) {
            this.currentPagecustLedgerList = 1;
        }
        this.customerLedgerSearchKey = this.custLedgerForm.value;

        if (this.legershowItemPerPage == 1) {
            this.custLedgerItemPerPage = this.pageITEM;
        } else {
            this.custLedgerItemPerPage = this.legershowItemPerPage;
        }

        this.custLedgerSubmitted = true;
        if (this.custLedgerForm.valid) {
            this.postdata.CREATE_DATE = this.custLedgerForm.controls.startDateCustLedger.value;
            this.postdata.END_DATE = this.custLedgerForm.controls.endDateCustLedger.value;
        }
        this.getCustomersLedger(this.customerLedgerData.custId, "");
    }

    clearSearchCustomerLedger() {
        this.postdata.CREATE_DATE = "";
        this.postdata.END_DATE = "";
        this.custLedgerForm.controls.startDateCustLedger.setValue("");
        this.custLedgerForm.controls.endDateCustLedger.setValue("");
        this.custLedgerSubmitted = false;
        this.getCustomersLedger(this.customerLedgerData.custId, "");
    }

    selSearchOption(event) {
        this.searchDeatil = "";
        // console.log("value", event.value);
        // if (event.value) {
        //   this.fieldEnable = true;
        // } else {
        //   this.fieldEnable = false;
        // }
    }

    openMyInvoice(id) {
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.isCustomerDetailSubMenu = true;
        this.ifMyInvoice = true;
        this.searchinvoiceMaster(id, "");
        this.commondropdownService.getAllActiveBranch();
        this.commondropdownService.getAllActiveStaff();
        this.commondropdownService.getplanservice();
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getBusinessUnitList();
        this.ifWalletMenu = false;
        this.listView = false;
    }

    openInvoiceModal(id, invoice) {
        // this.invoiceDetailsService.show(id);
        this.isInvoiceDetail = true;
        this.invoiceID = invoice.id;
        this.custID = invoice.custid;
    }
    closeInvoiceDetails() {
        this.isInvoiceDetail = false;
        this.invoiceID = "";
        this.custID = 0;
    }
    openInvoicePaymentModal(id, invoiceId) {
        this.invoicePaymentListService.show(id);
        this.invoiceId.next({
            invoiceId
        });
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

    searchInvoices() {
        this.currentPageinvoiceMasterSlab = 1;
        this.searchinvoiceMaster("", "");
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

        let url;

        // if (id) {
        //   this.searchInvoiceMasterFormGroup.value.billrunid = id
        //   this.searchInvoiceMasterFormGroup.patchValue({
        //     billrunid: Number(id),
        //   })
        // }

        const dtoData = {
            page: this.currentPageinvoiceMasterSlab,
            pageSize: this.invoiceMasteritemsPerPage
        };

        this.searchInvoiceMasterFormGroup.value.custMobile = "";
        this.searchInvoiceMasterFormGroup.value.customerid = this.customerLedgerDetailData.id;
        this.searchkey = "";
        Object.keys(this.searchInvoiceMasterFormGroup.value).forEach(key => {
            if (
                this.searchInvoiceMasterFormGroup.value[key] !== null ||
                this.searchInvoiceMasterFormGroup.value[key] !== ""
            ) {
                this.searchkey += `&${key}=${this.searchInvoiceMasterFormGroup.value[key]}`;
            }
        });

        url = "/invoice/search?isInvoiceVoid=true" + this.searchkey;
        this.customerManagementService.postMethod(url, dtoData).subscribe(
            (response: any) => {
                const invoiceMasterListData = response.invoicesearchlist;
                this.invoiceMasterListData = invoiceMasterListData;
                // this.invoiceMasterListData = response.invoicesearchlist;
                this.invoiceMastertotalRecords = response.pageDetails.totalRecords;
                this.isInvoiceChecked = false;
                this.allInvoiceChecked = false;
                this.isSingleInChecked = false;
                this.chakedInvoiceData = [];

                this.isInvoiceSearch = true;
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
    clearSearchinvoiceMaster() {
        this.isInvoiceSearch = false;
        this.searchInvoiceMasterFormGroup.reset();
        this.searchInvoiceMasterFormGroup.controls.billrunid.setValue("");
        this.searchInvoiceMasterFormGroup.controls.docnumber.setValue("");
        this.searchInvoiceMasterFormGroup.controls.custname.setValue("");
        this.searchInvoiceMasterFormGroup.controls.billfromdate.setValue("");
        this.searchInvoiceMasterFormGroup.controls.billtodate.setValue("");
        this.searchInvoiceMasterFormGroup.controls.customerid.setValue("");
        this.searchInvoiceMasterFormGroup.controls.staffId.setValue("");
        this.searchInvoiceMasterFormGroup.controls.branchId.setValue("");
        this.searchInvoiceMasterFormGroup.controls.businessunit.setValue("");
        this.searchInvoiceMasterFormGroup.controls.planId.setValue("");
        this.searchInvoiceMasterFormGroup.controls.serviceId.setValue("");

        this.invoiceMasterListData = [];
    }

    addWalletIncustomer(custID) {
        this.isCustomerDetailOpen = true;
        this.isCustomerLedgerOpen = false;
        this.isCustomerDetailSubMenu = true;
        this.ifMyInvoice = false;
        this.ifWalletMenu = true;
        this.listView = false;
        const data = {
            CREATE_DATE: "",
            END_DATE: "",
            amount: "",
            balAmount: "",
            custId: custID,
            description: "",
            id: "",
            refNo: "",
            transcategory: "",
            transtype: ""
        };
        const url = "/wallet";
        this.revenueManagementService.postMethod(url, data).subscribe((response: any) => {
            this.getWallatData = response;
        });
    }
    pageChangedInvoicePaymentList(pageNumber) {
        this.currentPageinvoicePaymentList = pageNumber;
    }
    invoicePaymentCloseModal() {
        this.ifInvoicePayment = false;
        this.ispaymentChecked = false;
        this.allIsChecked = false;
        this.isSinglepaymentChecked = false;
        this.invoicePaymentData = [];
        this.allchakedPaymentData = [];
    }

    invicePaymentList(invoice) {
        this.invoiceID = invoice.id;

        this.invoicePaymentData = [];
        if (invoice.adjustedAmount >= invoice.totalamount) {
            this.toastr.info(`Total payment is already adjusted`, 'Info!');
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Total payment is already adjusted",
            //     icon: "far fa-times-circle"
            // });
        } else {
            $("#invoicePayment").modal("show");
            const url = "/paymentmapping/" + this.invoiceID;
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.invoicePaymentData = response.Paymentlist;
                    this.invoicePaymenttotalRecords = this.invoicePaymentData.length;

                    this.invoicePaymentData.forEach((value, index) => {
                        this.invoicePaymentData[index].isSinglepaymentChecked = false;
                        this.totaladjustedAmount =
                            this.totaladjustedAmount + this.invoicePaymentData[index].adjustedAmount;
                    });
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

    checkInvoicePaymentAll(event) {
        if (event.checked == true) {
            this.allchakedPaymentData = [];
            const checkedData = this.invoicePaymentData;
            for (let i = 0; i < checkedData.length; i++) {
                this.allchakedPaymentData.push({
                    id: this.invoicePaymentData[i].id,
                    amount: this.invoicePaymentData[i].amount
                });
            }
            this.allchakedPaymentData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSinglepaymentChecked = true;
                    }
                });
            });
            this.ispaymentChecked = true;
            // console.log(this.allchakedPaymentData);
        }
        if (event.checked == false) {
            const checkedData = this.invoicePaymentData;
            this.allchakedPaymentData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSinglepaymentChecked = false;
                    }
                });
            });
            this.allchakedPaymentData = [];
            // console.log(this.allchakedPaymentData);
            this.ispaymentChecked = false;
            this.allIsChecked = false;
        }
    }

    addInvoicePaymentChecked(id, event) {
        if (event.checked) {
            this.invoicePaymentData.forEach((value, i) => {
                if (value.id == id) {
                    this.allchakedPaymentData.push({
                        id: value.id,
                        amount: value.amount
                    });
                }
            });

            if (this.invoicePaymentData.length === this.allchakedPaymentData.length) {
                this.ispaymentChecked = true;
                this.allIsChecked = true;
            }
            // console.log(this.allchakedPaymentData);
        } else {
            const checkedData = this.invoicePaymentData;
            checkedData.forEach(element => {
                if (element.id == id) {
                    element.isSinglepaymentChecked = false;
                }
            });
            this.allchakedPaymentData.forEach((value, index) => {
                if (value.id == id) {
                    this.allchakedPaymentData.splice(index, 1);
                    // console.log(this.allchakedPaymentData);
                }
            });

            if (
                this.allchakedPaymentData.length == 0 ||
                this.allchakedPaymentData.length !== this.invoicePaymentData.length
            ) {
                this.ispaymentChecked = false;
            }
        }
    }

    invoicePaymentAdjsment() {
        const data = {
            invoiceId: this.invoiceID,
            creditDocumentList: this.allchakedPaymentData
        };

        const url = "/invoicePaymentAdjust";
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                // this.closebutton.nativeElement.click();
                this.ifInvoicePayment = false;
                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.isSinglepaymentChecked = false;
                this.invoicePaymentData = [];
                this.allchakedPaymentData = [];
                this.searchinvoiceMaster(this.customerLedgerDetailData.id, "");
                this.toastr.success(`${response.message}`, 'Success!');
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
                //     detail: response.message,
                //     icon: "far fa-check-circle"
                // });
            },
            (error: any) => {
                // console.log(error, "error");
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

    downloadPDFINvoice(docNo, customerName) {
        if (docNo) {
            const downloadUrl = "/invoicePdf/download/" + docNo;
            this.customerManagementService.downloadPDFInvoice(downloadUrl).subscribe(
                (response: any) => {
                    const file = new Blob([response], { type: "application/pdf" });
                    // var fileURL = URL.createObjectURL(file,customerName + docNo);
                    // FileSaver.saveAs(file);
                    const fileURL = URL.createObjectURL(file);
                    FileSaver.saveAs(file, customerName + docNo);
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

    voidInvoice(invoice): void {
        if (invoice) {
            this.confirmationService.confirm({
                message: "Do you wish to VOID this invoice?",
                header: "VOID Invoice Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    const url = `/invoiceV2/voidInvoice?invoiceId=${invoice.id}`;
                    this.customerManagementService.getMethod(url).subscribe(
                        (response: any) => {
                            // this.closebutton.nativeElement.click();
                            this.ifInvoicePayment = false;
                            this.ispaymentChecked = false;
                            this.allIsChecked = false;
                            this.isSinglepaymentChecked = false;
                            this.invoicePaymentData = [];
                            this.allchakedPaymentData = [];
                            this.searchinvoiceMaster("", "");
                            if (response.responseCode == 417) {
                                this.toastr.info(`${response.responseMessage}`, 'Info!');
                                // this.messageService.add({
                                //     severity: "info",
                                //     summary: "Info",
                                //     detail: response.responseMessage,
                                //     icon: "far fa-check-circle"
                                // });
                            } else {
                                this.toastr.success(`${response.message}`, 'Success!');
                                // this.messageService.add({
                                //     severity: "success",
                                //     summary: "Successfully",
                                //     detail: response.message,
                                //     icon: "far fa-check-circle"
                                // });
                            }
                        },
                        (error: any) => {
                            // console.log(error, "error");
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
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                }
            });
        }
    }
    allSelectInvoice(event) {
        if (event.checked == true) {
            this.chakedInvoiceData = [];
            const checkedData = this.invoiceMasterListData;
            for (let i = 0; i < checkedData.length; i++) {
                // if (
                //   this.invoiceMasterListData[i].status !== "approved" &&
                //   this.invoiceMasterListData[i].status !== "rejected"
                // ) {
                this.chakedInvoiceData.push({
                    id: this.invoiceMasterListData[i].id
                });
                // }
            }
            this.chakedInvoiceData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSingleInChecked = true;
                    }
                });
            });

            this.isInvoiceChecked = true;
            // console.log(this.chakedInvoiceData);
        }
        if (event.checked == false) {
            const checkedData = this.invoiceMasterListData;
            this.chakedInvoiceData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSingleInChecked = false;
                    }
                });
            });
            this.chakedInvoiceData = [];
            // console.log(this.chakedInvoiceData);
            this.isInvoiceChecked = false;
            this.allInvoiceChecked = false;
        }
    }

    addInvoiceChecked(id, event) {
        if (event.checked) {
            this.invoiceMasterListData.forEach((value, i) => {
                if (value.id == id) {
                    this.chakedInvoiceData.push({
                        id: value.id
                    });
                }
            });

            if (this.invoiceMasterListData.length === this.chakedInvoiceData.length) {
                this.isInvoiceChecked = true;
                this.allInvoiceChecked = true;
            }
            // console.log(this.chakedInvoiceData);
        } else {
            const checkedData = this.invoiceMasterListData;
            checkedData.forEach(element => {
                if (element.id == id) {
                    element.isSingleInChecked = false;
                }
            });
            this.chakedInvoiceData.forEach((value, index) => {
                if (value.id == id) {
                    this.chakedInvoiceData.splice(index, 1);
                    // console.log(this.chakedInvoiceData);
                }
            });

            if (
                this.chakedInvoiceData.length == 0 ||
                this.chakedInvoiceData.length !== this.invoiceMasterListData.length
            ) {
                this.isInvoiceChecked = false;
            }
        }
    }

    approveRejectInvoice(invoiceID, isApproveRequest) {
        this.assignStaffForm.reset();
        const url = `/invoiceV2/approveDebitDoc?invoiceId=${invoiceID}&isApproveRequest=${isApproveRequest}&remark=${"approved"}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignStaffForm.controls.invoiceId.setValue(invoiceID);
                if (isApproveRequest) {
                    if (response.dataList != null) {
                        this.approve = true;
                        this.staffList = response.dataList;

                        $("#assignApproveModal").modal("show");
                    } else {
                        this.approve = false;
                        this.ifInvoicePayment = false;
                        this.ispaymentChecked = false;
                        this.allIsChecked = false;
                        this.isSinglepaymentChecked = false;
                        this.invoicePaymentData = [];
                        this.allchakedPaymentData = [];
                        this.searchinvoiceMaster("", "");
                    }
                } else {
                    if (response.dataList != null) {
                        // this.reject = true;
                        this.approve = false;
                        this.staffList = response.dataList;

                        $("#assignApproveModal").modal("show");
                    } else {
                        // this.reject = false;
                        this.ifInvoicePayment = false;
                        this.ispaymentChecked = false;
                        this.allIsChecked = false;
                        this.isSinglepaymentChecked = false;
                        this.invoicePaymentData = [];
                        this.allchakedPaymentData = [];
                        this.searchinvoiceMaster("", "");
                    }
                }
                // this.closebutton.nativeElement.click();

                if (response.responseCode === 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`${response.message}`, 'Success!');
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.message,
                    //     icon: "far fa-check-circle"
                    // });
                }
            },
            (error: any) => {
                // console.log(error, "error");
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

    assignToStaff() {
        const entityId = this.assignStaffForm.controls.invoiceId.value;
        const nextAssignStaff = this.assignStaffForm.controls.staffId.value;
        let url;
        if (nextAssignStaff) {
            url = `/teamHierarchy/assignFromStaffList?entityId=${entityId}&eventName=BILL_TO_ORGANIZATION&isApproveRequest=${this.approve}&nextAssignStaff=${nextAssignStaff}`;
        } else {
            url = `/teamHierarchy/assignEveryStaff?entityId=${entityId}&eventName=${"BILL_TO_ORGANIZATION"}&isApproveRequest=${this.approve}`;
        }
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                $("#assignApproveModal").modal("hide");
                this.searchinvoiceMaster("", "");
                this.toastr.success(`${response.message}`, 'Success!');
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
                //     detail: response.message,
                //     icon: "far fa-check-circle"
                // });
            },
            (error: any) => {
                // console.log(error, "error");
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

    getworkflowAuditDetails(size, id, name) {
        const page = this.currentPageAudit;
        let page_list;
        if (size) {
            page_list = size;
            this.itemsPerPageAudit = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.itemsPerPageAudit = 5;
            } else {
                this.itemsPerPageAudit = 5;
            }
        }

        this.workflowAuditData = [];

        const data = {
            page,
            pageSize: this.itemsPerPageAudit
        };

        const url = "/workflowaudit/list?entityId=" + id + "&eventName=" + name;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.workflowAuditData = response.dataList;
                this.MastertotalRecords = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.ERROR,
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
                console.log(error, "error");
            }
        );
    }
    pageChangedMasterList(pageNumber) {
        this.currentPageAudit = pageNumber;
        this.getworkflowAuditDetails("", this.workflowID, "PLAN");
    }
    workflowID(arg0: string, workflowID: any, arg2: string) {
        throw new Error("Method not implemented.");
    }
    TotalItemPerPageWorkFlow(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageAudit > 1) {
            this.currentPageAudit = 1;
        }
        this.getworkflowAuditDetails(this.showItemPerPage, this.workflowID, "PLAN");
    }

    openAuditWorkflow(id, modalId) {
        this.ifModelIsShow = true;
        this.PaymentamountService.show(modalId);
        this.auditcustid.next({
            auditcustid: id,
            checkHierachy: "BILL_TO_ORGANIZATION",
            planId: ""
        });
    }

    generatePDFInvoice(custId) {
        if (custId) {
            const url = "/generatePdfByInvoiceId/" + custId;
            this.customerManagementService.generateMethodInvoice(url).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
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
    pickModalOpen(data) {
        let url = "/workflow/pickupworkflow?eventName=BILL_TO_ORGANIZATION&entityId=" + data.id;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.searchinvoiceMaster("", "");

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                // console.log(error, "error");
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
    approvableStaff: any = [];
    assignedBillToOrganizationid: any;
    StaffReasignList1(data) {
        this.assignPLANForm.reset();
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.id}&eventName=BILL_TO_ORGANIZATION`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedBillToOrganizationid = data.id;
                this.approvableStaff = [];
                if (response.responseCode == 417) {
                    this.toastr.error(`Please Approve before assign`, 'Failed!');
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Please Approve before assign",
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
                if (response.dataList != null) {
                    this.approvableStaff = response.dataList;
                    this.approve = true;
                    $("#reAssignPLANModal").modal("show");
                } else {
                    $("#reAssignPLANModal").modal("hide");
                }
            },
            (error: any) => {
                // console.log(error, "error");
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
    selectStaff: any;
    reassignWorkflow() {
        let url: any;
        this.remark = this.assignPLANForm.value.remark;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.assignedBillToOrganizationid}&eventName=BILL_TO_ORGANIZATION&assignToStaffId=${this.selectStaff}&remark=${this.remark}`;
        if (this.assignedBillToOrganizationid === null) {
            this.toastr.error(`Please Approve before assign`, 'Failed!');
            // this.messageService.add({
            //     severity: "error",
            //     summary: "Error",
            //     detail: "Please Approve before assign",
            //     icon: "far fa-times-circle"
            // });
        } else {
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    $("#reAssignPLANModal").modal("hide");
                    this.searchinvoiceMaster("", "");
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-times-circle"
                        // });
                    } else {
                        this.toastr.success(`Assigned to the next staff successfully.`, 'Success!');
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: "Assigned to the next staff successfully.",
                        //     icon: "far fa-times-circle"
                        // });
                    }
                },
                error => {
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
        this.getcustomerList("");
    }

    createCreditNote(invoice) {

        this.custDropdownData = [
            {
                id: invoice.custid,
                name: invoice.customerName
            }
        ];

        this.invoiceDropdownData = [
            {
                id: invoice.id,
                docnumber: invoice.docnumber
            }
        ];

        this.paymentFormGroup.controls.paymentreferenceno.disable();
        this.paymentFormGroup.controls.customerid.disable();
        let creditNoteAmount = 0;
        if (invoice.creditDocumentList && invoice.creditDocumentList.length > 0) {
            let creditNoteinvoice = invoice.creditDocumentList.filter(inv => inv.type === "creditnote");
            creditNoteAmount = creditNoteinvoice
                .map(item => +item.amount)
                .reduce((sum, current) => sum + current);
        }
        this.paymentFormGroup.patchValue({
            paymode: "Credit Note",
            type: "creditnote",
            paytype: "creditnote",
            invoiceId: invoice.id,
            customerid: invoice.custid,
            amount: invoice.totalamount - creditNoteAmount
        });
        $("#addCreditNoteModal").modal("show");
    }

    createPaymentData: any;
    submitted = false;
    paymentFormGroup: UntypedFormGroup;
    custDropdownData = [];
    invoiceDropdownData = [];

    addPayment(paymentId): void {
        this.submitted = true;
        if (this.paymentFormGroup.valid) {
            this.createPaymentData = this.paymentFormGroup.getRawValue();
            this.paymentFormGroup.value.type = "creditnote";
            this.paymentFormGroup.value.paymode = "Credit Note";
            this.paymentFormGroup.value.paytype = "creditnote";
            // this.createPaymentData.paymentdate = new Date();
            let invoiceId = [];
            invoiceId.push(this.paymentFormGroup.controls.invoiceId.value);
            this.createPaymentData.invoiceId = invoiceId;
            delete this.createPaymentData.paymentreferenceno;

            const formData = new FormData();
            formData.append("spojo", JSON.stringify(this.createPaymentData));
            const url = "/record/payment";
            this.revenueManagementService.postMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.status == 200) {
                        this.submitted = false;
                        this.paymentFormGroup.reset();
                        this.submitted = false;
                        this.custDropdownData = [];
                        this.invoiceDropdownData = [];
                        $("#addCreditNoteModal").modal("hide");
                        this.searchinvoiceMaster("", "");
                        this.toastr.success(`${response.message}`, 'Success!');
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });
                    } else {
                        this.toastr.error(`${response.paymentdate}`, 'Failed!');
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.paymentdate,
                        //     icon: "far fa-times-circle"
                        // });
                    }
                },
                (error: any) => {
                    // console.log(error, "error")
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

    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }
}
