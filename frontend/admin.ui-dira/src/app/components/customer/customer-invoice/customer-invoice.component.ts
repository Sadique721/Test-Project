import { Component, OnInit, ViewChild, TemplateRef, OnDestroy, Input, EventEmitter, Output } from '@angular/core';
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ConfirmationService, MessageService } from "primeng/api";
import { InvoiceDetailsService } from "src/app/service/invoice-details.service";
import { BehaviorSubject, interval, Subscription } from "rxjs";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import * as FileSaver from "file-saver";
import { InvoiceMasterService } from "src/app/service/invoice-master.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import * as uuid from "uuid";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { countries } from "../../model/country";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { InvoiceDetalisModelComponent } from '../../invoice-detalis-model/invoice-detalis-model.component'; declare var $: any;
import { ToastrService } from 'ngx-toastr';
import { PageEvent } from '@angular/material/paginator';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
@Component({
    selector: "app-customer-invoice",
    templateUrl: "./customer-invoice.component.html",
    styleUrls: ["./customer-invoice.component.scss"],
    standalone: false
})
export class CustomerInvoiceComponent implements OnInit, OnDestroy {
    @ViewChild('writeoffDialog') writeoffDialog!: TemplateRef<any>;
    // @ViewChild('remark') remark!: TemplateRef<any>;
    @ViewChild('gracePeriodModal') gracePeriodModal!: TemplateRef<any>;
    @ViewChild('paymentDetailsModal') paymentDetailsModal!: TemplateRef<any>;
    // @ViewChild('invoiceDetailsModal') isInvoiceDetail!: TemplateRef<any>;
    @ViewChild('paymentMappingModal') displayPaymentDetails!: TemplateRef<any>;
    paymentDisplayedColumns: string[] = [
        'selectAll',
        'reciptNo',
        'paymentdate',
        'amount',
        'adjustedAmount',
        'remainingAmount',
        'paymode',
        'type'
    ];
    @ViewChild('remarkDialog') remarkDialog!: TemplateRef<any>;
    remarkDialogRef!: MatDialogRef<any>;
    dialogRef: MatDialogRef<any> | null = null;

    @ViewChild('paymentDetailsDialog') paymentDetailsDialog!: TemplateRef<any>;
    paymentDialogRef!: MatDialogRef<any>;
    @ViewChild('writeOffDialog') writeOffDialog!: TemplateRef<any>;
    writeOffDialogRef: any;
    @ViewChild('gracePeriodDialog') gracePeriodDialog!: TemplateRef<any>;
    gracePeriodDialogRef: any;
    @ViewChild('auditDialog') auditDialog!: TemplateRef<any>;
    auditDialogRef: any;
    auditDisplayedColumns: string[] = ['event', 'date', 'remark', 'modifiedBy'];

    customerDetailSubject: any;
    isInvoiceDetail = false;
    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    customerId: number;
    searchInvoiceMasterFormGroup: UntypedFormGroup;
    currentPageinvoiceMasterSlab = 1;
    pageLimitOptions = RadiusConstants.PAGE_SIZE_OPTIONS;
    invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoicePaymentItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageInvoice = 1;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    invoiceMasterListData: any = [];
    customerLedgerDetailData: any;
    invoiceMastertotalRecords: String;
    searchInvoiceData: any;
    isInvoiceSearch = false;
    invoiceID = "";
    custID = 0;
    invoicePaymentData = [];
    invoicePaymenttotalRecords: number;
    totaladjustedAmount = 0;
    invoiceCancelRemarks = null;
    invoiceCancelRemarksType = null;
    ifInvoicePayment = false;
    ispaymentChecked = false;
    allIsChecked = false;
    isSinglepaymentChecked = false;
    allchakedPaymentData = [];
    showdata: any = [];
    planNotes = false;
    currentPageinvoicePaymentList = 1;
    AclClassConstants;
    AclConstants;
    StopId = ""
    startDate: any;
    today: any;

    InvoiceDATA = new BehaviorSubject({
        InvoiceDATA: ""
    });
    invoiceId = new BehaviorSubject({
        invoiceId: ""
    });
    currency: string;
    Remark: boolean = false;
    generateAccess: boolean = false;
    viewInvoiceAccess: boolean = false;
    invoicePaymentListAccess: boolean = false;
    voidInvoiceAcces: boolean = false;
    reprintInvoiceAccess: boolean = false;
    cancelAndRegenerateAccess: boolean = false;
    displayInvoicePaymentDialog: boolean;
    savedConfig: any;
    invoice: any;
    exitBuy: boolean = true;
    paymentstatusCount = RadiusConstants.TIMER_COUNT;
    paymentConfirmationModal: boolean = false;
    subscription2: Subscription;
    obs1$ = interval(1000);
    transactionStatus: boolean = false;
    customerLedgerData: any = [];
    paymentSucessModel: boolean = false;
    presentAdressDATA: any = [];
    isPaymentGatewayConfigured: boolean = false;
    sendTraInvoiceAccess: boolean = false;
    writeOffAccess: boolean = false;
    gracePeriodAccess: boolean = false;
    paymentGateway: any;
    paymentkeyValuePairs: { [key: string]: any } = {};
    presentFullAddress: any;
    isTraEnable: boolean = false;
    mpinModal: boolean = false;
    mpinForm: UntypedFormGroup;
    momoPayinvoice: any;
    isMpinFormSubmitted: boolean = false;
    inputMobile: string = "";
    countries: any = countries;
    payMethod: any;
    isWriteOffModel: boolean = false;
    writeOffAmount: any = "";
    writeOffAmountFirst: any = "";
    writeOffInvoice: any;
    writeOffRemark: any = "";
    holdDays: any;
    isGracePeriodModel: boolean;
    gracePeriod: string;
    gracePeriodData: any;
    auditListModal: boolean = false;
    auditListData: any;
    searchkey: string;
    currentPageAuditListSlab = 1;
    auditListitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    auditTotalRecords: any;
    showItemPerPage = 1;
    searchOption: string = "";
    searchInput: string = ""; // New variable for user-entered text
    serviceStopBulkFlag: boolean = false;
    serviceStopId = [];
    servicePerticularData: any = [];
    selectDeactivateReason: string = "";
    serviceStropRemarks: string = "";
    serviceMappingId: any

    displayStopServiceModal: boolean = false;
    stopServiceReason: any = '';

    searchOptions = [
        { label: "Username", value: "username" },
        { label: "Invoice Number", value: "invoicenumber" }
    ];
    searchData: any;
    customerAddressDetails: any;
    displayMpesaOptionsDialog: boolean;
    selectedMpesaOption: string = "";
    invoiceForMpesa: any;
    displayedColumns: string[] = [
        'customer',
        'invoiceNumber',
        'purchaseBy',
        'billableTo',
        'totalAmount',
        'adjustedAmount',
        'unpaidAmount',
        'billRunStatus',
        'billDate',
        'paymentStatus',
        'paymentOwner',
        'action'
    ];
    todayDate: any;

    invoiceRemarkTitle1 = 'Void Invoice Action'
    invoiceRemarkTitle2 = 'Cancel & Regenerate Action'
    invoiceRemarkSubTitle1 = "This action will permanently void the invoice. Please provide a reason for voiding."
    invoiceRemarkSubTitle2 = "'This action will cancel the current invoice and regenerate a new one. Please provide a reason."
    @ViewChild('stopServiceDialog') stopServiceDialog!: TemplateRef<any>;
    stopServiceDialogRef!: MatDialogRef<any>;
    @ViewChild('paymentDialog') paymentDialog!: TemplateRef<any>;
    countryCode: string = ''

    constructor(
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        private route: ActivatedRoute,
        private router: Router,
        private fb: UntypedFormBuilder,
        private messageService: MessageService,
        private invoiceDetailsService: InvoiceDetailsService,
        public invoicePaymentListService: InvoicePaymentListService,
        private confirmationService: ConfirmationService,
        private invoiceMasterService: InvoiceMasterService,
        public loginService: LoginService,
        private recordPaymentService: RecordPaymentService,
        private systemService: SystemconfigService,
        public commondropdownService: CommondropdownService,
        public customerdetailsilsService: CustomerdetailsilsService,
        private dialog: MatDialog,

    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.generateAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_GENERATE
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_GENERATE
        );
        this.invoicePaymentListAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_LIST
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_PAYMENT_LIST
        );
        this.voidInvoiceAcces = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_VOID
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_VOID
        );
        this.reprintInvoiceAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_REPRINT
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_REPRINT
        );
        this.cancelAndRegenerateAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_CANCEL_REGENERATE
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_CANCEL_REGENERATE
        );
        this.viewInvoiceAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_VIEW
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_VIEW
        );
        this.sendTraInvoiceAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVOICES_SEND_TRA_INVOICE
                : POST_CUST_CONSTANTS.POST_CUST_INVOICES_VIEW
        );
        this.writeOffAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_WRITE_OFF
                : POST_CUST_CONSTANTS.POST_CUST_WRITE_OFF
        );
        this.gracePeriodAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_GRACE_PERIOD
                : POST_CUST_CONSTANTS.POST_CUST_GRACE_PERIOD
        );
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    }

    ngOnDestroy(): void {
        if (this.customerDetailSubject) {
            this.customerDetailSubject.unsubscribe();
        }
    }

    async ngOnInit() {
        // this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.searchInvoiceMasterFormGroup = this.fb.group({
            billfromdate: [""],
            billrunid: [""],
            billtodate: [""],
            custMobile: [""],
            custname: [""],
            docnumber: [""],
            customerid: [""]
        });

        this.getCustomersDetail(this.customerId);
        this.searchinvoiceMaster(this.customerId, "");

        this.mpinForm = this.fb.group({
            countryCode: [''],
            mobileNumber: ["", [Validators.required]]
        });
        this.checkPaymentGatewayConfiguration();
        this.checkInvoiceIntigration();
        this.commondropdownService.getsystemconfigList();
        this.commondropdownService.commonCountryCodeSubject$.subscribe((code: string) => {
            this.mpinForm.get("countryCode")?.setValue(code);
            this.countryCode = code;
        })
        this.searchData = {
            filters: [
                {
                    filterColumn: "any",
                    filterValue: ""
                }
            ],
            page: "",
            pageSize: "",
            sortOrder: "",
            sortBy: this.customerId,
            filterBy: ""
        };
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

    getCustomersDetail(custId) {
        this.customerDetailSubject = this.customerManagementService.customerDetail$.subscribe((response: any) => {
            if (response) {
                this.customerLedgerDetailData = response.customers;

                //Address
                if (this.customerLedgerDetailData.addressList.length > 0) {
                    if (this.customerLedgerDetailData.addressList[0].addressType) {
                        this.presentFullAddress = this.customerLedgerDetailData.addressList[0].fullAddress;
                        let areaurl = "/area/" + this.customerLedgerDetailData.addressList[0].areaId;

                        this.customerdetailsilsService.commonGetMethod(areaurl).subscribe((response: any) => {
                            this.presentAdressDATA = response.data;
                        });
                    }
                }
                this.customerLedgerDetailData?.currency
                    ? (this.currency = this.customerLedgerDetailData?.currency)
                    : this.systemService
                        .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                        .subscribe((res: any) => {
                            this.currency = res.data.value;
                        });
            }
        });
    }
    @Input() isFromCaf;
    @Output() backToList: EventEmitter<any> = new EventEmitter<any>();
    customerDetailOpen() {
        if (!this.isFromCaf) {
            this.backToList?.emit();
            return;
        }
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    searchInvoices() {
        this.currentPageinvoiceMasterSlab = 1;
        this.searchinvoiceMaster("", "");
    }
    filteredData: any
    // serviceMappingId: any;
    searchinvoiceMaster(id: any, size) {
        if (size) {
            this.invoiceMasteritemsPerPage = size;
        } else {
            this.invoiceMasteritemsPerPage = this.pageITEM; // default page size
        }

        const dtoData = {
            page: this.currentPageinvoiceMasterSlab,
            pageSize: this.invoiceMasteritemsPerPage
        };

        this.searchInvoiceMasterFormGroup.value.custMobile = "";
        this.searchInvoiceMasterFormGroup.value.customerid = this.customerId;

        const url =
            "/invoice/search?billrunid=" + this.searchInvoiceMasterFormGroup.value.billrunid +
            "&docnumber=" + this.searchInvoiceMasterFormGroup.value.docnumber.trim() +
            "&customerid=" + this.searchInvoiceMasterFormGroup.value.customerid +
            "&billfromdate=" + this.searchInvoiceMasterFormGroup.value.billfromdate +
            "&billtodate=" + this.searchInvoiceMasterFormGroup.value.billtodate +
            "&custmobile=" + this.searchInvoiceMasterFormGroup.value.custMobile.trim() +
            "&isInvoiceVoid=true";

        this.revenueManagementService.postMethod(url, dtoData).subscribe(
            (response: any) => {
                this.spinner.hide();
                // Applying custType filter if needed
                const fullList = response.invoicesearchlist.filter(invoice => invoice.custType == this.custType);
                // this.serviceMappingId = response?.invoicesearchlist?.custServiceMappingId;
                // this.serviceStopId = fullList.flatMap(invoice => invoice.custServiceMappingId);
                // this.serviceStopId = response.invoicesearchlist.filter(invoice => invoice.custServiceMappingId == this.serviceMappingId);
                this.filteredData = fullList.map(invoice => ({
                    custServiceMappingId: invoice.custServiceMappingId,
                    remarks: invoice.remarks,
                    reasonId: invoice.reasonId
                }));

                // Total records for paginator
                this.invoiceMastertotalRecords = response.pageDetails.totalRecords || fullList.length;

                // Calculate start and end index for client-side slicing based on current page and page size
                const startIndex = (this.currentPageinvoiceMasterSlab - 1) * this.invoiceMasteritemsPerPage;
                const endIndex = startIndex + this.invoiceMasteritemsPerPage;

                // Slice full list to current page's data for display
                this.invoiceMasterListData = fullList.slice(startIndex, endIndex);

                this.isInvoiceSearch = true;
                this.today = new Date();
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`No Record Found`, 'Failed!');
            }
        );
    }


    isTodayBeforeOrEqual(startDate: string | Date): boolean {
        return this.today <= new Date(startDate);
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
        // this.searchInvoiceMasterFormGroup.controls.staffid.setValue("");
        this.invoiceMasterListData = [];
        this.searchinvoiceMaster("", "");
    }

    // openInvoiceModal(id, invoice) {
    //     this.dialogRef = this.dialog.open(this.isInvoiceDetail, {
    //         width: '600px',
    //         disableClose: false
    //     });
    //     this.invoiceID = invoice.id;
    //     this.custID = invoice.custid;
    // }
    openInvoiceModal(id, invoice) {
        this.isInvoiceDetail = true;
        this.dialogRef = this.dialog.open(InvoiceDetalisModelComponent, {
            width: '1000px',
            disableClose: false,
            data: {
                dialogId: 'InvoiceDetailModal',
                invoiceID: invoice.id,
                custID: invoice.custid,
                sourceType: 'customer',
            },
        });
        this.dialogRef.afterClosed().subscribe(() => {
            this.closeInvoiceDetails();
        });
        this.invoiceID = invoice.id;
        this.custID = invoice.custid;
    }

    closeInvoiceDetails() {
        this.dialogRef?.close();
        this.invoiceID = "";
        this.custID = 0;
    }

    openInvoicePaymentModal(id, invoiceId) {
        this.invoicePaymentListService.show(id);
        this.invoiceId.next({
            invoiceId
        });
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
                    this.toastr.success(`Downloaded Successfully`, 'Success!');
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    generatePDFInvoice(custId) {
        if (custId) {
            const url = "/generatePdfByInvoiceId/" + custId;
            this.customerManagementService.generateMethodInvoice(url).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');
                        this.searchInvoiceData("", "");
                    } else {
                        response.responseCode == 417;
                    }
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                },

                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    invoicePaymentList(invoice) {
        this.invoiceID = invoice.id;

        this.invoicePaymentData = [];
        if (invoice.adjustedAmount >= invoice.totalamount) {
            this.toastr.info(`Total payment is already adjusted`, 'Info!');

        } else {
            // this.dialogRef = this.dialog.open(this.displayPaymentDetails, {
            //     width: '600px',
            //     disableClose: false
            // });
            this.paymentDialogRef = this.dialog.open(this.paymentDetailsDialog, {
                width: '70%',
                disableClose: true
            });
            const url = "/paymentmapping/" + this.invoiceID;
            this.revenueManagementService.getMethod(url).subscribe(
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

                }
            );
        }
    }

    invoiceRemarks(invoice, type) {

        this.invoiceID = invoice.id;
        this.invoiceCancelRemarksType = type;
        this.Remark = true;
        // this.dialogRef = this.dialog.open(this.remark, {
        //     width: '600px',
        //     disableClose: false
        // });
        this.remarkDialogRef = this.dialog.open(this.remarkDialog, {
            width: '35%',
            disableClose: true,
        });
    }

    addInvoiceRemarks() {
        if (this.invoiceCancelRemarksType === "void") {
            this.voidInvoice();
        } else if (this.invoiceCancelRemarksType === "cancelRegenerate") {
            this.cancelRegenrateInvoice();
        }
    }

    // voidInvoice(): void {
    //     // if (invoice) {
    //     this.confirmationService.confirm({
    //         message: "Do you wish to VOID this invoice?",
    //         header: "VOID Invoice Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //             const url = `/voidInvoice?invoiceId=${this.invoiceID}&invoiceCancelRemarks=${this.invoiceCancelRemarks}`;
    //             this.revenueManagementService.getMethod(url).subscribe(
    //                 (response: any) => {
    //                     // this.closebutton.nativeElement.click();
    //                     this.ifInvoicePayment = false;
    //                     this.ispaymentChecked = false;
    //                     this.allIsChecked = false;
    //                     this.isSinglepaymentChecked = false;
    //                     this.invoiceCancelRemarks = null;
    //                     this.invoiceCancelRemarksType = null;
    //                     this.invoicePaymentData = [];
    //                     this.allchakedPaymentData = [];
    //                     this.searchinvoiceMaster("", "");
    //                     this.Remark = false;
    //                     this.dialogRef?.close();
    //                     if (response.responseCode == 417) {
    //                         this.toastr.info(`${response.responseMessage}`, 'Info!');

    //                     } else {
    //                          this.toastr.success(`${response.message}`, 'Success!');

    //                         this.remarkDialogRef.close();
    //                     }
    //                 },
    //                 (error: any) => {
    //                     // console.log(error, "error");
    //                      this.toastr.error(`${error.error.ERROR}`, 'Failed!');

    //                 }
    //             );
    //         },
    //         reject: () => {
    //             this.toastr.info(`You have rejected`, 'Info!');

    //         }
    //     });
    //     // }
    // }
    voidInvoice(): void {
        // if (invoice) {
        const url = `/voidInvoice?invoiceId=${this.invoiceID}&invoiceCancelRemarks=${this.invoiceCancelRemarks}`;
        // this.confirmationService.confirm({
        //     message: "Do you wish to VOID this invoice?",
        //     header: "VOID Invoice Confirmation",
        //     icon: "pi pi-info-circle",
        //     accept: () => {
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.closebutton.nativeElement.click();
                this.ifInvoicePayment = false;
                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.isSinglepaymentChecked = false;
                this.invoiceCancelRemarks = null;
                this.invoiceCancelRemarksType = null;
                this.invoicePaymentData = [];
                this.allchakedPaymentData = [];
                this.searchinvoiceMaster("", "");
                this.Remark = false;
                this.dialogRef?.close();
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.remarkDialogRef.close();

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.remarkDialogRef.close();
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        )
        // },
        // reject: () => {
        //     this.toastr.info(`You have rejected`, 'Info!');

        // }
        // });
        // }
    }

    cancelRegenrateInvoice() {
        const data = {};

        const url =
            "/cancelAndRegenerate/" +
            this.invoiceID +
            "?isCaf=false&invoiceCancelRemarks=" +
            this.invoiceCancelRemarks;
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                // this.closebutton.nativeElement.click();
                this.ifInvoicePayment = false;
                this.ispaymentChecked = false;
                this.allIsChecked = false;
                this.isSinglepaymentChecked = false;
                this.invoiceCancelRemarks = null;
                this.invoiceCancelRemarksType = null;
                this.invoicePaymentData = [];
                this.allchakedPaymentData = [];
                this.searchinvoiceMaster("", "");
                this.Remark = false;
                this.dialogRef?.close();
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.remarkDialogRef.close();
                }
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    InvoiceReprint(docnumber, custname) {
        const url = "/regeneratepdfsub/" + docnumber;
        this.invoiceMasterService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, custname);
                this.toastr.success(response.message || "Downloaded Successfully", 'Success!');

            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    viewInvoice(docnumber, custname) {
        const url = "/regeneratepdfsub/" + docnumber;
        this.invoiceMasterService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                window.open(fileURL, "_blank");
                // this.toastr.success(`${response.message}`, 'Success!');
                this.toastr.success('PDF opened successfully!', 'Success!');

            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                this.toastr.error('Failed to load PDF', 'Error!');

            }
        );
    }


    displayNote(type) {
        if (type === "invoice") {
            this.planNotes = false;
            this.showdata = this.invoiceMasterListData.filter(
                invoice => invoice.billrunstatus === "Cancelled" || invoice.billrunstatus === "VOID"
            );
            // Open Angular Material Dialog
            this.auditDialogRef = this.dialog.open(this.auditDialog, {
                width: '75%',
                disableClose: true,
            });
        }
    }

    pageChangedinvoiceMasterList(event: PageEvent): void {
        // Update pagination variables with paginator event data
        this.currentPageinvoiceMasterSlab = event.pageIndex + 1;  // paginator index is 0-based
        this.invoiceMasteritemsPerPage = event.pageSize;

        // Fetch the data for the current page with updated page size
        this.searchinvoiceMaster(this.currentPageinvoiceMasterSlab, this.invoiceMasteritemsPerPage);
    }


    TotalItemPerPageInvoice(event) {
        this.showItemPerPageInvoice = Number(event.value);
        if (this.currentPageinvoiceMasterSlab > 1) {
            this.currentPageinvoiceMasterSlab = 1;
        }
        this.searchinvoiceMaster("", this.showItemPerPageInvoice);
    }

    closeInvoiceCancelremark() {
        this.invoiceCancelRemarks = "";
        this.Remark = false;
        this.dialogRef?.close();
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

    pageChangedInvoicePaymentList(pageNumber) {
        this.currentPageinvoicePaymentList = pageNumber;
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


                // this.dialogRef?.close();
                this.paymentDialogRef.close();
            },
            (error: any) => {
                // console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    invoicePaymentCloseModal() {
        this.ifInvoicePayment = false;
        this.ispaymentChecked = false;
        this.allIsChecked = false;
        this.isSinglepaymentChecked = false;
        this.invoicePaymentData = [];
        this.allchakedPaymentData = [];

        // this.dialogRef?.close();
        this.paymentDialogRef.close();

    }

    checkPaymentGatewayConfiguration() {
        this.spinner.show();
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

    openPaymentGatewaysforInvoicePayment(invoice: any) {
        this.displayInvoicePaymentDialog = false;
        this.dialogRef?.close();
        if (this.savedConfig.length === 0) {
            this.toastr.info(`Payment Gateway Configuration Not Found!!!`, 'Info!');

        } else if (this.savedConfig.length === 1) {
            if (this.savedConfig[0].paymentConfigName === "MoMo Pay") {
                this.spinner.show();
                this.buyMomoInvoicePayment(invoice);
            } else if (this.savedConfig[0].paymentConfigName === "AIRTEL") {
                this.spinner.show();
                this.airtelPayPlan(invoice);
            } else if (this.savedConfig[0].paymentConfigName === "SELCOM") {
                this.spinner.show();
                this.selcomPayPlan(invoice);
            } else if (this.savedConfig[0].paymentConfigName === "Wave Pay") {
                this.spinner.show();
                this.buyWaveMoneyPayPlan(invoice);
            } else if (this.savedConfig[0].paymentConfigName == "ONEPAY") {
                this.spinner.show();
                this.buyOnePayInvoicePayment(this.invoice);
            } else if (this.savedConfig[0].paymentConfigName == "TRANSACTEASE") {
                this.spinner.show();
                this.getCustomerAddressDetails(this.invoice);
            } else if (this.savedConfig[0].paymentConfigName == "MPESA") {
                this.payMethod = this.savedConfig[0].paymentConfigName;
                this.displayMpesaOptionsDialog = true;
                // this.invoiceForMpesa = invoice;
                this.invoice = invoice;
                this.showMpinModal()
            } else {
                this.toastr.info(`Invoice payment is not available for this gateway.`, 'Info!');

            }
        } else if (this.savedConfig.length >= 1) {
            this.invoice = invoice;
            this.displayInvoicePaymentDialog = true;
            this.dialog.open(this.payamentGetwayMethode, {
                width: '600px',
                disableClose: false
            });
        }
    }

    onKeymobilelength(event) {
        const str = this.mpinForm.value.mobileNumber.toString();
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

    showMpinModal() {
        this.displayInvoicePaymentDialog = false;
        this.dialogRef?.close();
        this.mpinModal = true;
        this.dialog.open(this.mpinModalDailog, {
            width: '550px',
            disableClose: false
        });

        // this.momoPayinvoice = invoice;
        this.mpinForm.controls.countryCode.setValue(this.countryCode);
        this.mpinForm.controls.mobileNumber.setValue(this.customerLedgerDetailData.mobile);
        // this.mpinForm.controls.mobileNumber.reset();
    }

    hideMpinModal() {
        this.isMpinFormSubmitted = false;
        this.mpinForm.reset();
        this.mpinForm.controls.countryCode.setValue("");
        this.mpinForm.controls.mobileNumber.setValue("");
        // this.mpinForm.updateValueAndValidity();
        this.mpinModal = false;
        this.mobileError = false;
        this.inputMobile = "";
    }

    keypressId(event: any) {
        const pattern = /^[0-9]+$/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    invoicePayment(savedConfig: any) {
        this.invoicePaymentpaymentGateway(savedConfig, '');
    }

    invoicePaymentpaymentGateway(selectedConfig: any, dialogRef: any) {
        this.payMethod = selectedConfig.paymentConfigName;
        if (this.payMethod === "Wave Pay") {
            this.spinner.show();
            this.buyWaveMoneyPayPlan(this.invoice);
        } else if (this.payMethod === "KBZPAY") {
            this.spinner.show();
            this.buyKbzInvoicePayment(this.invoice);
        } else if (this.payMethod == "ONEPAY") {
            this.showMpinModal();
            //   this.buyOnePayInvoicePayment(this.invoice);
        } else if (this.payMethod == "TRANSACTEASE") {
            this.getCustomerAddressDetails();
        } else {
            this.showMpinModal();
        }
        dialogRef.close()
    }
    buyOnePayInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            isAdvancePayment: true,
            //   isBuyPlan: true,
            merchantName: "ONEPAY",
            customerUserName: this.customerLedgerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            payerMobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerLedgerDetailData.buId
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
                    this.toastr.info(response.responseMessage || "Unexpected response received.", 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }

    invoicePaymentGateway(dialogRef: any) {
        if (this.payMethod === "MoMo Pay") {
            this.spinner.show();
            this.buyMomoInvoicePayment(this.invoice);
            dialogRef.close()
        } else if (this.payMethod === "AIRTEL") {
            this.spinner.show();
            this.airtelPayPlan(this.invoice);
            dialogRef.close()
        } else if (this.payMethod === "SELCOM") {
            this.spinner.show();
            this.selcomPayPlan(this.invoice);
            dialogRef.close()
        } else if (this.payMethod === "MPESA") {
            this.displayMpesaOptionsDialog = true;
            this.invoiceForMpesa = this.invoice;
            this.buyMpesaExpressPlan(this.invoiceForMpesa);
            dialogRef.close()
        } else if (this.payMethod === "ONEPAY") {
            this.spinner.show();
            this.buyOnePayInvoicePayment(this.invoice);
            dialogRef.close()
        } else {
            this.toastr.info(`Invoice payment is not available for this gateway.`, 'Info!');

        }

    }

    buyMomoInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            merchantName: "MoMo Pay",
            customerUserName: this.customerLedgerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            invoiceId: invoice.id,
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? ""
        };
        this.customerdetailsilsService.buyPlanUsingMomoInvoice(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                (localStorage.setItem("transactionId", response.data.data.orderId),
                    console.log("in response of momo buy"));
                this.paymentConfirmationModal = true;
                this.isMpinFormSubmitted = false;
                this.mobileError = false;
                this.inputMobile = "";
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                this.exitBuy = false;

                this.dialog.open(this.paymentDialog, {
                    width: '400px',
                    disableClose: true
                });

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
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }

    getStatusSuccessByMomo(status) {
        this.spinner.hide();
        let data = {
            orderId: localStorage.getItem("transactionId"),
            status: status
        };
        this.customerdetailsilsService.getIntigrationTransactionstatusInvoice(data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data.istransactionsuccess === "true") {
                        this.transactionStatus = response.istransactionsuccess;
                        let data = {
                            userName: this.customerLedgerData.username,
                            password: this.customerLedgerData.password
                        };
                        // this.getDevice(data);
                        this.paymentConfirmationModal = false;
                        this.subscription2.unsubscribe();
                        this.paymentSucessModel = true;
                    }
                }
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    hidepaymentConfirmDialog() {
        this.paymentConfirmationModal = false;
        this.displayInvoicePaymentDialog = false;
        this.dialogRef?.close();
    }

    hidepaymentSucessDialog() {
        this.paymentSucessModel = false;
    }

    airtelPayPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            merchantName: "AIRTEL",
            customerUserName: this.customerLedgerDetailData.username,
            mvnoId: this.customerLedgerDetailData.mvnoId,
            mobileNumber: this.mpinForm.value.mobileNumber ?? "",
            invoiceId: invoice.id,
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? ""
        };
        this.customerdetailsilsService.buyPlanUsingAirtelInvoice(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                this.isMpinFormSubmitted = false;
                this.mpinForm.reset();
                this.mpinForm.controls.countryCode.setValue("");
                this.mpinForm.controls.mobileNumber.setValue("");
                //localStorage.setItem("transactionId"),
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                }
                (localStorage.setItem("transactionId", response.data.data.transaction.id),
                    console.log("in response of AIrtel buy"));
                this.exitBuy = false;

                this.paymentConfirmationModal = true;
                this.mobileError = false;
                this.inputMobile = "";
                this.dialog.open(this.paymentDialog, {
                    width: '400px',
                    disableClose: true
                });
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
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }

    selcomPayPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;

        let customerPaymentDTO = {
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            buid: this.customerLedgerDetailData.buId,
            custServiceMappingId: this.customerLedgerDetailData.planMappingList[0].custServiceMappingId,
            customerId: this.customerLedgerDetailData.id,
            customerUUID: uuid.v4(),
            customerUserName: this.customerLedgerDetailData.username,
            invoiceId: invoice.id,
            isBuyPlan: true,
            isFromCaptive: true,
            merchantName: "SELCOM",
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            orderId: null,
            partnerId: this.customerLedgerDetailData.partnerid,
            partnerPaymentId: this.customerLedgerDetailData.partnerPaymentId ?? null,
            planId: this.customerLedgerDetailData.planMappingList[0].planId,
            requestFor: this.customerLedgerDetailData.requestFor ?? null,
            status: this.customerLedgerDetailData.status
        };

        let selcomPayPayment = {
            vendor: "",
            order_id: null,
            buyer_email: this.customerLedgerDetailData.email,
            buyer_name: this.customerLedgerDetailData.username,
            buyer_phone:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            gateway_buyer_uuid: "",
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            currency: "",
            payment_methods: "",
            "billing.firstname": this.customerLedgerDetailData.firstname ?? "",
            "billing.lastname": this.customerLedgerDetailData.lastname ?? "",
            "billing.address_1": this.customerLedgerDetailData?.addressList[0]?.landmark ?? "",
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
                this.inputMobile = "";
                //localStorage.setItem("transactionId"),
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                } else if (response.responseCode === 200 && response.data && response.data.data) {
                    const paymentLink = response.data.data;
                    window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(response.responseMessage || "Unexpected response received.", 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }
    checkInvoiceIntigration() {
        this.customerdetailsilsService
            .checkInvoiceIntigration("Invoice Creation")
            .subscribe((response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                if (response.status === 417) {
                    this.toastr.info(`${response.ERROR}`, 'Info!');

                    return;
                } else if (response.status === 200) {
                    if (this.isClientPresent(response.thirdPartyIntegrationMenuData, "TRA Integration")) {
                        this.isTraEnable = true;
                    }
                }
                this.spinner.hide();
            });
    }

    isClientPresent(data, clientName: string): boolean {
        return data.some(item => item.clientName === clientName);
    }

    sendInvoiceTraIntigration(invoice) {
        this.customerdetailsilsService
            .sendTraInvoiceIntigration(invoice.id)
            .subscribe((response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                if (response.status === 417) {
                    this.toastr.info(`${response.ERROR}`, 'Info!');

                    return;
                } else if (response.status === 200) {
                    this.toastr.success(`${response.message}`, 'Success!');

                    this.spinner.show();
                    setTimeout(() => {
                        this.clearSearchinvoiceMaster();
                    }, 3000);
                } else if (response.status === 204) {
                    this.toastr.info(`${response.message}`, 'Info!');

                } else {
                    this.toastr.info(`${response.ERROR}`, 'Info!');

                }
                this.spinner.hide();
            });
    }

    openPaymentGateways(invoice) {
        const url = "/generatePaymentLink/" + invoice.custid;
        this.customerManagementService.postMethod(url, null).subscribe(
            (response: any) => {
                let payData = response.data;
                if (response.data == null) {
                    this.toastr.info(`No Unpaid Invoice Found for this Customer`, 'Info!');

                } else {
                    window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
                    //   this.router.navigate(["/customer/payMethod/" + payData]);
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    sendemailinvoice(docNo) {
        if (docNo) {
            const downloadUrl = "/invoice/send/" + docNo;
            this.customerdetailsilsService.getmethodforrevenue(downloadUrl).subscribe(
                (response: any) => {
                    this.spinner.hide();
                    this.toastr.success(`${response.msg}`, 'Success!');

                },
                (error: any) => {
                    this.spinner.hide();
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    keypress(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }
    openWriteOff(invoice) {
        this.isWriteOffModel = true;
        // this.dialogRef = this.dialog.open(this.writeoffDialog, {
        //     width: '600px',
        //     disableClose: false
        // });
        this.writeOffDialogRef = this.dialog.open(this.writeOffDialog, {
            width: '30%',
            disableClose: true,
        });
        this.writeOffAmountFirst = invoice?.totalamount - invoice?.adjustedAmount;
        this.writeOffAmount = +(invoice?.totalamount - invoice?.adjustedAmount).toFixed(2);
        this.writeOffAmountFirst = this.writeOffAmount;
        this.writeOffInvoice = invoice;
        this.writeOffRemark = "";
    }
    closeWriteOff() {
        this.isWriteOffModel = false;
        this.dialogRef?.close();
        this.writeOffAmountFirst = "";
        this.writeOffAmount = "";
        this.writeOffInvoice = "";
        this.writeOffRemark = "";
        this.writeOffDialogRef.close()
    }
    confirmWriteOff() {
        if (this.writeOffAmount) {
            this.spinner.show();
            const url = "/writeOffByDebitDocId";
            let obj = {
                debitDocId: this.writeOffInvoice.id,
                writeOffAmount: this.writeOffAmount,
                remarks: this.writeOffRemark
            };
            this.customerdetailsilsService.postRevenueMethod(url, obj).subscribe(
                (response: any) => {
                    this.spinner.hide();
                    this.toastr.success(`Successfully`, 'Success!');


                    this.closeWriteOff();
                    this.invoiceMasterListData = [];
                    this.searchinvoiceMaster("", "");
                    this.writeOffDialogRef.close();
                },
                (error: any) => {
                    this.spinner.hide();
                    this.closeWriteOff();
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    checkWriteOff() {
        if (this.writeOffAmountFirst === this.writeOffAmount) {
            this.confirmWriteOff();
        } else {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Confirmation",
                    description:
                        "The provided amount is insufficient to clear the ledger balance.Do you want to continue?",
                    yesLabel: "Continue",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result === true) {
                    this.confirmWriteOff();
                }
                // No need for reject handler, empty/no action if Cancel is pressed
            });
            //   this.confirmationService.confirm({
            //     message:
            //       "The provided amount is insufficient to clear the ledger balance.<br>Do you want to continue?",
            //     header: "Confirmation",
            //     icon: "pi pi-exclamation-triangle",
            //     accept: () => {
            //       this.confirmWriteOff();
            //     },
            //     reject: () => {}
            //   });
        }
    }

    buyWaveMoneyPayPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            isAdvancePayment: true,
            isBuyPlan: true,
            merchantName: "Wave Pay",
            customerUserName: this.customerLedgerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            custServiceMappingId: this.customerLedgerDetailData.planMappingList[0].custServiceMappingId,
            mobileNumber:
                this.customerLedgerDetailData.countryCode.replace("+", "") +
                (this.customerLedgerDetailData.mobile ?? ""),
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerLedgerDetailData.buId,
            planId: null
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
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                    return;
                } else if (response.responseCode === 200 && response.data) {
                    const paymentLink = response.data;
                    window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(response.responseMessage || "Unexpected response received.", 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }
    openGracePeriod(invoice) {
        this.isGracePeriodModel = true;
        this.gracePeriodDialogRef = this.dialog.open(this.gracePeriodDialog, {
            width: '30%',
            disableClose: true,
        });
        this.gracePeriod = invoice.debitDocGraceDays;
        this.gracePeriodData = invoice;
    }
    closeGracePeriod() {
        this.isGracePeriodModel = false;
        this.gracePeriodDialogRef.close();
        this.dialogRef?.close();
        this.gracePeriodData = "";
        this.gracePeriod = "";
    }
    saveGracePeriod() {
        if (this.gracePeriod) {
            this.spinner.show();
            let gracedata = {
                debitDocId: this.gracePeriodData.id,
                debitDocGraceDays: Number(this.gracePeriod)
            };
            const url = "/duedaywithgracdays";
            this.customerdetailsilsService.postRevenueMethod(url, gracedata).subscribe(
                (response: any) => {
                    this.spinner.hide();
                    if (response.responseCode === 417) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');

                    } else {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                        this.closeGracePeriod();
                        this.gracePeriodData = [];
                        this.searchinvoiceMaster("", "");
                        this.gracePeriodDialogRef.close();
                    }
                },
                (error: any) => {
                    this.spinner.hide();
                    this.closeGracePeriod();
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

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
    buyKbzInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            isFromCaptive: false,
            isAdvancePayment: true,
            //   isBuyPlan: true,
            merchantName: "KBZPAY",
            customerUserName: this.customerLedgerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            mobileNumber:
                this.customerLedgerDetailData.countryCode.replace("+", "") +
                (this.customerLedgerDetailData.mobile ?? ""),
            invoiceId: invoice.id,
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerLedgerDetailData.buId
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
                    this.toastr.info(`Please open the payment link on your mobile device using the KBZPay app.`, 'Info!');

                    //   const kbzurl = paymentLink.split("?kbzurl=")[1];
                    //   this.router.navigate(["/kbz-pay"], {
                    //     queryParams: { kbzurl: kbzurl }
                    //   });
                    //   window.open(paymentLink, "_blank");
                } else {
                    this.toastr.info(response.responseMessage || "Unexpected response received.", 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }
    getAuditData(size) {
        let page = this.currentPageAuditListSlab;
        // let page_list;
        // if (size) {
        //     page_list = size;
        //     this.auditListitemsPerPage = size;
        // } else {
        //     if (this.showItemPerPage == 0) {
        //         this.auditListitemsPerPage = 5;
        //     } else {
        //         this.auditListitemsPerPage = 5;
        //     }
        // }
        this.auditListData = [];
        let data = {
            page: page,
            pageSize: this.auditListitemsPerPage,
            sortBy: "id",
            sortOrder: 0
        };
        const url = "/auditLog/getAuditList/" + this.customerId;
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.auditListData = response.dataList;
                this.auditTotalRecords = response.totalRecords;
                this.auditListModal = true;
                this.dialog.open(this.auditdetailsDialog, {
                    width: '80%',
                    disableClose: true
                });
                //this.auditList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    closeAuditListData() {
        this.auditListModal = false;
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageAuditListSlab > 1) {
            this.currentPageAuditListSlab = 1;
        }
        if (!this.searchkey) {
            this.getAuditData(this.showItemPerPage);
        } else {
            this.searchAudit();
        }
    }
    pageChangedList(pageNumber) {
        this.currentPageAuditListSlab = pageNumber.pageIndex + 1;
        this.auditListitemsPerPage = pageNumber.pageSize
        if (this.searchkey) {
            this.searchAudit();
        } else {
            this.getAuditData("");
        }
    }
    searchAudit() {
        if (this.searchOption && this.searchInput) {
            this.searchData.filters[0].filterColumn = this.searchOption;
            this.searchData.filters[0].filterValue = this.searchInput;
        } else {
            this.searchData.filters[0].filterColumn = "any";
            this.searchData.filters[0].filterValue = "";
        }
        this.searchData.page = this.currentPageAuditListSlab || 1;
        this.searchData.pageSize = this.auditListitemsPerPage || 10;
        this.searchData.sortBy = "entityRefId";
        this.searchData.sortOrder = 1;
        this.searchData.status = "";
        this.searchData.filterBy = "";
        const url = `/auditLog/getSearchAudit/${this.customerId}`;
        this.revenueManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response?.auditListData?.length === 0) {
                    this.auditListModal = true;
                    this.auditListData = [];
                    this.auditTotalRecords = 0;
                } else {
                    this.auditListData = response.dataList;
                    this.auditTotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.auditListData = [];
                this.auditTotalRecords = 0;
                this.toastr.error(error.error?.ERROR || "Something went wrong.", 'Failed!');
            }
        );
    }
    clearAuditSearch() {
        this.searchOption = "";
        this.searchInput = "";
        this.searchData = {
            filters: [
                {
                    filterColumn: "any",
                    filterValue: ""
                }
            ],
            page: "",
            pageSize: "",
            sortOrder: "",
            sortBy: "entityRefId",
            filterBy: "",
            status: ""
        };
        this.getAuditData("");
    }
    buyTransacteasePayment(invoice) {
        const newTab = window.open("", "_blank");
        // this.getCustomerAddressDetails(this.customerId)
        this.spinner.show();
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
            //   amount: (this.amountsData + (this.amountsData * this.commissionPer) / 100).toString(),
            //   commission: (invoice.totalamount * this.commissionPer) / 100,
            billAddressLine1: this.customerAddressDetails?.landmark,
            billAddressLine2: this.customerAddressDetails?.landmark,
            billToAddressCity: this.customerAddressDetails?.cityName,
            billToAddressState: this.customerAddressDetails?.stateName,
            billToAddressZip: this.customerAddressDetails?.pincode,
            custServiceMappingId: this.customerLedgerDetailData.planMappingList[0].custServiceMappingId,
            email: this.customerLedgerDetailData?.email,
            isBuyPlan: true,
            isFromCaptive: true,
            actualAmount: invoice.totalamount.toString(),
            isAdvancePayment: true,
            merchantName: "TRANSACTEASE",
            customerUserName: this.customerLedgerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            mobileNumber:
                this.customerLedgerDetailData.countryCode.replace("+", "") +
                (this.customerLedgerDetailData.mobile ?? ""),
            payerMobileNumber:
                this.customerLedgerDetailData.countryCode.replace("+", "") +
                (this.customerLedgerDetailData.mobile ?? ""),
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? "",
            hash: "",
            buid: this.customerLedgerDetailData.buId
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
                            this.toastr.error(`Please allow popups for this site.`, 'Failed!');

                        }
                    }
                } else {
                    this.toastr.info(response.responseMessage || "Unexpected response received.", 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }
    getCustomerAddressDetails(invoice?: any) {
        try {
            this.customerdetailsilsService
                .getCustomerAddressDetails(this.customerLedgerDetailData.id)
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
    handleMpesaPaymentOption(option: string) {
        this.spinner.hide();
        this.displayMpesaOptionsDialog = false;
        if (option === "Mpesa-Express") {
            this.buyMpesaExpressPlan(this.invoiceForMpesa);
        } else if (option === "Mpesa-B2C") {
            this.spinner.show();
            this.buyMpesaInvoicePayment(this.invoiceForMpesa);
        }
    }
    // Add method to close MPESA options dialog
    closeMpesaOptionsDialog() {
        this.displayMpesaOptionsDialog = false;
    }

    buyMpesaInvoicePayment(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice?.totalamount - invoice?.adjustedAmount).toString(),
            isFromCaptive: false,
            customerUserName: this.customerLedgerDetailData.username,
            customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            mobileNumber:
                this.customerLedgerDetailData.countryCode.replace("+", "") +
                (this.customerLedgerDetailData.mobile ?? ""),
            invoiceId: invoice.id,
            partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? "",
            custServiceMappingId: this.customerLedgerDetailData.planMappingList[0].custServiceMappingId,
            buid: this.customerLedgerDetailData?.buId,
            orderId: "",
            planId: this.customerLedgerDetailData.planMappingList[0].planId
        };
        this.customerdetailsilsService.buyPlanUsingMpesa(data).subscribe(
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
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }
    buyMpesaExpressPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerLedgerDetailData.id,
            amount: (invoice?.totalamount - invoice?.adjustedAmount).toString(),
            // isFromCaptive: true,
            customerUserName: this.customerLedgerDetailData.username,
            // customerUUID: uuid.v4(),
            mvnoId: this.customerLedgerDetailData.mvnoId,
            // mobileNumber:
            //     this.customerLedgerDetailData.countryCode.replace("+", "") +
            //     (this.customerLedgerDetailData.mobile ?? ""),
            payerMobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") +
                (this.mpinForm.value.mobileNumber ?? ""),
            // merchantName: null,
            // invoiceId: invoice.id,
            // partnerId: this.customerLedgerDetailData.partnerid,
            accountNumber: this.customerLedgerDetailData?.acctno ?? "",
            // custServiceMappingId: this.customerLedgerDetailData.planMappingList[0].custServiceMappingId,
            // buid: this.customerLedgerDetailData?.buId,
            // orderId: "",
            // planId: this.customerLedgerDetailData.planMappingList[0].planId,
            // hash: null,
            // isAdvancePayment: false,
            // isBuyPlan: true,
            // partnerPaymentId: this.customerLedgerDetailData.partnerid,
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
                this.toastr.error(`Something went wrong`, 'Failed!');

            }
        );
    }

    openPaushSearviceMedel(id: any, serviceMappingId: any, actionType: string) {
        this.serviceMappingId = serviceMappingId;
        const stopUrl = `/getCNAmount/${id}`;
        this.revenueManagementService.getMethod(stopUrl, true)
            .subscribe({
                next: (response: string) => {
                    this.stopServiceReason = response;
                    // this.displayStopServiceModal = true;
                    this.stopServiceDialogRef = this.dialog.open(this.stopServiceDialog, {
                        width: '1000px',
                        maxWidth: '70vw',
                        height: 'auto',
                        autoFocus: false,
                        disableClose: true
                    });

                    this.stopServiceDialogRef.afterClosed().subscribe(result => {
                        this.stopServiceDialogRef = null;
                    });
                    if (response) {

                    } else {
                    }

                },
                error: (error: any) => {
                    console.error("API call error:", error);
                    this.toastr.error(error?.error?.ERROR || "Unknown error", 'Failed!');

                },
                complete: () => {
                }
            });


    }
    ClosePaushSearviceMedel() {
        // this.displayStopServiceModal = false;
        this.stopServiceDialogRef.close();
    }
    isButtonDisabled(dueDate, srtartDate): boolean {
        return (new Date(dueDate) > new Date(srtartDate));
    }


    StopService(serviceStopIds: any) {
        // const deactivatePlanReqModels = (this.serviceStopBulkFlag
        //     ? this.serviceStopId.map(e => ({
        //         custServiceMappingId: e.custServiceMappingId,
        //         remarks: this.filteredData.find(item => item.custServiceMappingId === (Array.isArray(e.custServiceMappingId) ? e.custServiceMappingId[0] : e.custServiceMappingId))?.remarks || '',
        //         reasonId: this.selectDeactivateReason
        //     }))
        //     : [{
        //         custServiceMappingId: this.serviceMappingId,
        //         remarks: this.serviceStropRemarks,
        //         reasonId: this.selectDeactivateReason
        //     }]
        // );

        const deactivatePlanReqModels = this.serviceMappingId.map(e => ({
            custServiceMappingId: e,
            remarks: e.remarks || '',
            reasonId: ''
        }));
        const data = {
            custId: this.customerLedgerDetailData.id,
            serviceStopBulkFlag: this.serviceStopBulkFlag,
            deactivatePlanReqModels: deactivatePlanReqModels
        };


        const url = "/subscriber/stopServiceInBulk";
        this.customerManagementService.postMethod(url, data).subscribe({
            next: (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.serviceStopBulkFlag = false;
                    this.serviceStopId = [];
                    // this.displayStopServiceModal = false;
                    this.toastr.success(`Service Stopped Successfully`, 'Success!')
                    this.stopServiceDialogRef.close();

                }
            },
            error: (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        });
    }

    @ViewChild('payamentGetwayMethode') payamentGetwayMethode!: TemplateRef<any>;
    @ViewChild('auditdetailsDialog') auditdetailsDialog!: TemplateRef<any>;
    @ViewChild('mpinModalDailog') mpinModalDailog!: TemplateRef<any>;
    displayedAuditColumns = ['userName', 'auditDate', 'invoiceNumber', 'debitDocGraceDays']
}
