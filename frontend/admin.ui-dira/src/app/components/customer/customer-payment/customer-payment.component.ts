import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { BehaviorSubject } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import { ConfirmationService, MessageService } from "primeng/api";
import { FormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { Regex } from "src/app/constants/regex";
import { SearchPaymentService } from "src/app/service/search-payment.service";
import * as FileSaver from "file-saver";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { sortBy } from "lodash";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DatePipe } from "@angular/common";
import { PaymentAmountModelComponent } from "../../payment-amount-model/payment-amount-model.component";
import { PageEvent } from "@angular/material/paginator";


declare var $: any;

@Component({
    selector: "app-customer-payment",
    templateUrl: "./customer-payment.component.html",
    styleUrls: ["./customer-payment.component.scss"],
    standalone: false
})
export class CustomerPaymentComponent implements OnInit {
    @Input() cafCustomerID: any
    @Input() cafCustomerType: any
    @Input() isFromCaf?: boolean = false;
    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    customerId: number;
    showError: boolean = false;
    customerLedgerDetailData: any;
    isDisable: boolean = false;
    customerPaymentdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymentShowItemPerPage = 1;
    viewcustomerPaymentData: any;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    invoiceList = [];
    masterSelected: boolean;
    invoicedropdownValue = [{ docnumber: "Advance", id: 0 }];
    customerData: any;
    paymentFormGroup: UntypedFormGroup;
    searchData: any = {};
    chequeDetail = [];
    showChequeDetails: boolean = false;
    AclClassConstants;
    AclConstants;
    currentPagecustomerPaymentdata = 0;
    newFirst = 0;
    selectedInvoice: any = [];
    isSelectedInvoice = true;
    tdsPercent: number;
    abbsPercent: number;
    isShowInvoiceList: boolean = false;
    destinationbank: boolean = false;
    Amount: any = 0;
    isTdsFlag: boolean = false;
    isAbbsFlag: boolean = false;
    chequeDateName = "Transaction Date";
    datePlaceholder = "Select transaction date";
    todaysDateString = new Date().toLocaleDateString('en-US');
    paymentMode = [];
    test: any = "true";
    fileName: any;
    file: any = "";
    submitted = false;
    createPaymentData: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    paymentId = new BehaviorSubject({
        paymentId: ""
    });
    displayInvoiceDetails: boolean = false;
    currency: string;
    systemConfigCurrency: string;
    collectedCurrency: string;
    displayRecordPaymentDialog: boolean = false;
    displayFailedPaymentDialog: boolean = false;
    displaySelectInvoiceDialog: boolean = false;
    recordPaymentAccess: boolean = false;
    selectedCheckboxStates: boolean[] = [];
    viewcustomerFailedPaymentData: any = [];

    bankDataList: any;
    bankDestination: any;
    failureReasonDialog: boolean = false;
    transModal: boolean = false;
    transactionNo: any;
    addToWalletOrderId: any;
    failureReason: string = "";
    retryPaymentAccess: boolean = false;
    manuallySettlement: boolean = false;

    isDisplayConvertedAmount: boolean = false;
    convertedExchangeRate: any;

    @ViewChild('confirmationDialog') confirmationDialog!: TemplateRef<any>;
    @ViewChild('onlinePaymentDialog') onlinePaymentDialog!: TemplateRef<any>;
    @Output() backToList = new EventEmitter<void>();
    dialogRef!: MatDialogRef<any>;
    dialogRef2!: MatDialogRef<any>;
    dialogRef3!: MatDialogRef<any>;
    dialogRef4!: MatDialogRef<any>;
    customerPaymentDataTotalRecords: any;



    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        private route: ActivatedRoute,
        private router: Router,
        private recordPaymentService: RecordPaymentService,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        private searchPaymentService: SearchPaymentService,
        public loginService: LoginService,
        public commondropdownService: CommondropdownService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private systemService: SystemconfigService, private datePipe: DatePipe,
        private confirmationService: ConfirmationService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;

        this.recordPaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_PAYMENT_RECORD
                : POST_CUST_CONSTANTS.POST_CUST_PAYMENT_RECORD
        );
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;

        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
            this.tdsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
            this.abbsPercent = res.data.value;
        });
        this.retryPaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.RETRY_PAYMENTSTATUS
                : POST_CUST_CONSTANTS.POST_RETRY_PAYMENTSTATUS
        );
        this.manuallySettlement = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.MANUALLY_SETTLEMENT
                : POST_CUST_CONSTANTS.POST_MANUALLY_SETTLEMENT
        );
    }

    async ngOnInit() {

        if (this.cafCustomerID) {
            this.customerId = this.cafCustomerID
            this.custType = this.cafCustomerType;
        }
        this.selectedCheckboxStates = this.invoiceList.map(invoice => invoice.isSelected);
        this.paymentFormGroup = this.fb.group({
            amount: [0, [Validators.required, Validators.min(1)]],
            bank: [""],
            branch: [""],
            chequedate: [new Date(), Validators.required],
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
            file: [""],
            tdsAmount: [0],
            abbsAmount: [0],
            invoiceId: ["", Validators.required],
            onlinesource: [""]
        });

        this.getCustomersDetail(this.customerId);
        this.getPaymentMode();
        this.resetPayMode();
        this.getBankDetail();
        this.getBankDestinationDetail();
        this.commondropdownService.getAllCurrencyData();
        this.systemService.getConfigurationByName("CONVERTED_EXCHANGE_RATE").subscribe((res: any) => {
            this.convertedExchangeRate = parseFloat(res?.data?.value.replace(/,/g, "")) || 1;
        });
    }
    paymentData: any;
    retryPayment(orderId) {
        this.paymentData = [];
        const url = "/ByOrderId?orderId=" + orderId;
        this.customerManagementService.getMethodForIntegration(url).subscribe(
            (response: any) => {
                // this.paymentData = response.onlineAuditData;
                this.getFailedPayments();

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    customerDetailOpen() {
        if (this.isFromCaf) {
            this.backToList.emit();
            return;
        }
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.customerLedgerDetailData = response.customers;
            this.openCustomersPaymentData(this.customerId, "");
            this.customerLedgerDetailData?.currency
                ? (this.currency = this.customerLedgerDetailData?.currency)
                : this.systemService
                    .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                    .subscribe((res: any) => {
                        this.currency = res.data.value;
                    });
            this.paymentFormGroup.patchValue({
                customerid: this.customerLedgerDetailData.id
            });
        });
    }
    closeRecordPaymentDialog(dialogRef: any) {
        this.paymentFormGroup.reset();
        dialogRef.close();
    }

    openCustomersPaymentData(id: number, size) {
        this.currentPagecustomerPaymentdata = 0;
        if (
            id === 0 ||
            this.customerLedgerDetailData.invoiceType === "Group"
        ) {
            this.isDisable = true;
        }

        if (!size) {
            size = this.customerPaymentdataitemsPerPage || this.pageITEM;
            this.customerPaymentdataitemsPerPage = size;
        }



        const url = "/paymentHistory/" + id;
        this.revenueManagementService.paymentData(url).subscribe((response: any) => {
            const fullList = response.dataList || [];

            this.customerPaymentDataTotalRecords = fullList.length;

            const startIndex = this.currentPagecustomerPaymentdata * this.customerPaymentdataitemsPerPage;
            const endIndex = startIndex + this.customerPaymentdataitemsPerPage;




            this.viewcustomerPaymentData = fullList.slice(startIndex, endIndex);
            // this.viewcustomerPaymentData = fullList;

        });
    }



    pageChangedCustomerPaymentData(event: PageEvent) {
        this.currentPagecustomerPaymentdata = event.pageIndex;
        this.customerPaymentdataitemsPerPage = event.pageSize;

        this.openCustomersPaymentData(this.customerId, this.customerPaymentdataitemsPerPage);
    }




    InvoiceListByCustomer(id) {
        const url = "/invoiceList/byCustomer/" + id;
        this.invoiceList = [];
        const Data = [];
        this.masterSelected = false;

        this.revenueManagementService.getAllInvoiceByCustomer(url).subscribe(
            (response: any) => {
                const invoicedata = [];
                if (response.invoiceList != null && response.invoiceList.length != 0) {
                    this.invoiceList.push(...response.invoiceList);
                } else {
                    this.invoiceList.push(...this.invoicedropdownValue);
                }
                // this.invoiceList = Data;
                this.invoiceList.forEach(item => {
                    item.tdsCheck = 0;
                    item.abbsCheck = 0;
                    item.tds = 0;
                    item.abbs = 0;
                    item.includeTds = false;
                    item.includeAbbs = false;
                    item.testamount = this.getPendingAmount(item);
                    item.convertedAmount = item.testamount * this.convertedExchangeRate;
                    item.currency = this.customerLedgerDetailData?.currency
                        ? this.customerLedgerDetailData?.currency
                        : this.currency;
                });

                this.dialog.open(this.selectInvoiceDailog, {
                    width: '80%',
                    disableClose: true
                });

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    onCurrencyChange(event: any, invoice: any) {
        // invoice.selectedCurrency = event.value;
        // invoice.isDisplayConvertedAmount = event.value !== this.customerLedgerDetailData?.currency;
        this.isDisplayConvertedAmount =
            event.value !=
            (this.customerLedgerDetailData?.currency
                ? this.customerLedgerDetailData?.currency
                : this.currency);
    }

    //   get shouldShowCollectedAmountColumnonInvoice(): boolean {
    //     return this.selectedInvoice?.some(row => row.isDisplayConvertedAmount);
    //   }

    //   get shouldShowCollectedAmountColumn():boolean{
    //     return this.invoiceList?.some(row=>row.isDisplayConvertedAmount);
    //   }

    getPendingAmount(item) {
        var amount = 0;
        if (item.adjustedAmount) {
            amount = item.totalamount - item.adjustedAmount;
        } else if (item.pendingAmt) {
            amount = item.totalamount - item.pendingAmt;
        } else if (item.adjustedAmount) {
            amount = item.totalamount - item.adjustedAmount;
        } else {
            amount = item.totalamount;
        }
        if (amount) return amount.toFixed(2);
        else return 0;
    }

    getCustomer() {
        // this.displayRecordPaymentDialog = true;
        this.dialog.open(this.recordPaymentDailog, {
            width: '80%',
            disableClose: true
        });
        // this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
        //   this.systemConfigCurrency = res.data.value;
        //   this.isDisplayConvertedAmount =
        //     this.systemConfigCurrency != this.customerLedgerDetailData?.currency;
        // });
        // const url = "/customers/list";
        // const custerlist = {};
        // this.recordPaymentService.postMethod(url, custerlist).subscribe(
        //     (response: any) => {
        //         this.customerData = response.customerList;
        //         this.paymentFormGroup.patchValue({
        //             customerid: this.customerLedgerDetailData.id
        //         });
        //     },
        //     (error: any) => {
        //         this.toastr.error(`${error.error.ERROR}`, 'Failed!');


        //     }
        // );
    }

    addToWalletClose() {
        this.dialogRef3.close();
    }

    addToWallet(orderId) {
        this.transModal = true;
        this.addToWalletOrderId = orderId;
        this.dialogRef3 = this.dialog.open(this.addTransactionDialog, {
            width: '700px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

    }
    openPaymentModal(id) {
        if (this.searchData.filters) {
            this.searchData.filters[0].filterValue = "";
            this.searchData.filters[0].filterColumn = "";
            this.searchData.page = "";
            this.searchData.pageSize = "";
        }

        let url = "/getChequeDetail/" + id;
        this.searchPaymentService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.chequeDetail = response.dataList;
                this.showChequeDetails = true;
                this.dialog.open(this.chequeDetailsDialog, {
                    width: '70%',
                    panelClass: 'cheque-dialog'
                });
            },
            (error: any) => {

                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    openPaymentInvoiceModal(id, paymentId) {


        this.dialog.open(PaymentAmountModelComponent, {
            width: '80%', disableClose: true,
            data: {
                paymentId: paymentId
            }
        });
        this.PaymentamountService.show(id);
        // this.displayInvoiceDetails = true;
        // this.paymentId.next({
        //     paymentId
        // });
    }

    downloadInvoice(docId, custId, fileName) {
        const url = "/documentForInvoice/download/" + docId + "/" + custId;
        this.revenueManagementService.downloadInvoice(url).subscribe(
            (response: any) => {
                var fileType = "";
                var file = new Blob([response], { type: "application/pdf" });
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, fileName);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedcustomerPaymentList(pageNumber) {
        this.currentPagecustomerPaymentdata = pageNumber.pageIndex + 1;
        this.customerPaymentdataitemsPerPage = pageNumber.pageSize;
        this.openCustomersPaymentData(this.customerId, "");
    }

    TotalPaymentItemPerPage(event) {
        this.paymentShowItemPerPage = Number(event.value);
        if (this.currentPagecustomerPaymentdata > 1) {
            this.currentPagecustomerPaymentdata = 1;
        }
        this.openCustomersPaymentData(this.customerLedgerDetailData.id, this.paymentShowItemPerPage);
    }

    @ViewChild('selectInvoiceDailog') selectInvoiceDailog!: TemplateRef<any>;
    modalOpenInvoice(id) {

        this.displaySelectInvoiceDialog = true;
        this.isDisplayConvertedAmount = false;
        this.collectedCurrency = this.customerLedgerDetailData?.currency
            ? this.customerLedgerDetailData?.currency
            : this.currency;
        if (id) {
            this.InvoiceListByCustomer(id);
        }
        this.newFirst = 0;
    }

    checkUncheckAllInvoice() {
        for (let i = 0; i < this.invoiceList.length; i++) {
            this.invoiceList[i].isSelected = this.masterSelected;
        }
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

    isAllSelectedInvoice() {
        this.masterSelected = this.invoiceList.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemListInvoice();
    }

    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    onSelectedInvoice(event, data, isTDS, isABBS) {
        if (event > 0) {
            this.isSelectedInvoice = false;
            if (isTDS) {
                data.tdsCheck = ((data.testamount * this.tdsPercent) / 100).toFixed(2);
            }
            if (isABBS) {
                data.abbsCheck = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
            }
        } else {
            //   data.includeTds = false;
            //   data.includeAbbs = false;
            data.tdsCheck = 0;
            data.abbsCheck = 0;
        }
        data.convertedAmount = data.testamount * this.convertedExchangeRate;
    }

    onConvertedAmountChange(event, data) {
        data.testamount = event / this.convertedExchangeRate;
        // data.convertedAmount = event;
    }

    onChangeOFTDSTest(event, data) {


        if (event.checked && data.totalamount) {
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
        if (event.checked && data.totalamount) {
            data.includeAbbs = true;
            data.abbsCheck = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
            data.abbs = ((data.testamount * this.abbsPercent) / 100).toFixed(2);
        } else {
            data.includeAbbs = false;
            data.abbsCheck = 0;
            data.abbs = 0;
        }
    }

    modalCloseInvoiceList() {
        this.paymentFormGroup.patchValue({
            invoiceId: this.selectedInvoice.id,
            amount: this.selectedInvoice.refundAbleAmount
        });
        this.isShowInvoiceList = true;
        this.displaySelectInvoiceDialog = false;
        this.newFirst = 0;
    }

    saveSelInvoice() {
        this.modalCloseInvoiceList();
    }

    bindInvoice(dialogRef) {
        if (this.selectedInvoice.length >= 1) {
            this.isShowInvoiceList = true;
            this.Amount = 0;
            this.selectedInvoice.forEach(element => {
                if (element.testamount !== null) {
                    this.Amount += parseFloat(element.testamount);
                }
            });
            this.paymentFormGroup.patchValue({
                invoiceId: this.selectedInvoice.map(item => item.id),
                amount: this.Amount.toFixed(2)
            });
            this.onChangeOFAmountTest(this.selectedInvoice);
            this.destinationbank = true;
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please select at least one invoice or advance mode!');
            }


        }
        if (this.selectedInvoice.length == 2) {
            this.selectedInvoice.forEach(element => {
                if (element.docnumber == "Advance") {
                    this.selectedInvoice = [];
                    this.invoiceList.forEach(element => {
                        element.isSelected = false;
                    });
                    this.masterSelected = false;
                    error: (error) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Please select advance mode value only!');
                    }


                }
            });
        }
        this.displaySelectInvoiceDialog = false;
        dialogRef.close();
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
                        this.isTdsFlag = true;
                    }
                }
                if (element.includeAbbs) {
                    if (element.includeAbbs === true) {
                        abbs = Number(element.abbsCheck);
                        totalabbsAmount = Number(element.abbsCheck) + Number(totalabbsAmount);
                        this.isAbbsFlag = true;
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

    closeInvoiceModel() {
        this.invoiceList = [];
        this.masterSelected = false;
        this.displaySelectInvoiceDialog = false;
    }

    onlineSourceData = [];
    async selPayModeRecord(event) {
        this.resetPayMode();
        const payMode = event.value.toLowerCase();
        if (payMode === "cheque") {
            this.chequeDateName = "Cheque Date";
            this.datePlaceholder = "Select cheque date";
        } else {
            this.chequeDateName = "Transaction Date";
            this.datePlaceholder = "Select transaction date";
        }
        if (payMode == "POS".toLowerCase() || payMode == "VatReceiveable".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            //   this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            //   this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.updateValueAndValidity();
            // this.chequeDateName = "Transaction date";
        } else if (payMode == "Online".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            //   this.paymentFormGroup.controls.referenceno.setValidators([Validators.required]);
            this.paymentFormGroup.controls.reciptNo.enable();
            //   this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            // this.chequeDateName = "Transaction date";
        } else if (payMode == "Direct Deposit".toLowerCase()) {
            this.paymentFormGroup.controls.branch.enable();
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            //   this.paymentFormGroup.controls.referenceno.clearValidators();
            //   this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.disable();
            this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
            // this.chequeDateName = "Transaction date";
        } else if (payMode == "NEFT_RTGS".toLowerCase()) {
            this.paymentFormGroup.controls.bankManagement.enable();
            this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
            this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            //   this.paymentFormGroup.controls.referenceno.clearValidators();
            //   this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
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
            //   this.paymentFormGroup.controls.referenceno.clearValidators();
            //   this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.branch.enable();
            this.paymentFormGroup.controls.chequeno.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
        }
        // await this.commondropdownService.getOnlineSourceData(payMode.toLowerCase());

        const url = "/commonList/generic/" + payMode;
        this.commondropdownService.getMethodWithCache(url).subscribe(
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

            }
        );
        this.paymentFormGroup.updateValueAndValidity();
        let isAbbsTdsMode = this.checkPaymentMode(payMode);
        if (isAbbsTdsMode) {
            this.paymentFormGroup.patchValue({
                tdsAmount: 0,
                abbsAmount: 0
            });
            if (this.selectedInvoice.length > 0) {
                this.selectedInvoice.map(element => {
                    element.tds = 0;
                    element.abbs = 0;
                });
            }
        }
    }

    resetPayMode() {
        this.paymentFormGroup.controls.chequeno.disable();
        this.paymentFormGroup.controls.bankManagement.disable();
        this.paymentFormGroup.controls.branch.disable();
        this.paymentFormGroup.controls.destinationBank.disable();
        this.paymentFormGroup.controls.reciptNo.enable();
        this.chequeDateName = "Transaction Date";
        this.datePlaceholder = "Select transaction date";
        // this.paymentFormGroup.controls.referenceno.clearValidators();
        // this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
        this.paymentFormGroup.controls.chequedate.setValidators([]);
        this.paymentFormGroup.controls.destinationBank.setValidators([]);
        this.paymentFormGroup.controls.bankManagement.setValidators([]);
        this.paymentFormGroup.controls.chequeno.setValidators([]);
        this.paymentFormGroup.controls.onlinesource.setValidators([]);
        this.paymentFormGroup.updateValueAndValidity();
    }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus?banktype=other";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
                // this.bankDestination = response.dataList.banktype
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


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
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getPaymentMode() {
        const url = "/commonList/paymentMode";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.paymentMode = response.dataList;
            },
            (error: any) => { }
        );
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
            case "cheque_in_hand":
                this.paymentFormGroup.controls.chequedate.enable();
                this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
                this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
                this.paymentFormGroup.controls.bankManagement.enable();
                this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
                this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
                this.paymentFormGroup.controls.chequeno.enable();
                this.paymentFormGroup.controls.chequeno.setValidators([Validators.required]);
                // this.paymentFormGroup.controls.referenceno.clearValidators();
                // this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
                this.paymentFormGroup.controls.reciptNo.enable();
                this.paymentFormGroup.controls.branch.enable();
                this.paymentFormGroup.controls.chequeno.updateValueAndValidity();
                break;
        }
    }

    onFileChange(event) {
        if (event.target.files.length > 0) {
            this.file = "";
            this.fileName = event.target.files[0].name;
            this.file = event.target.files[0];
        }
    }

    formatDate(dateStr: string): string | null {
        return this.datePipe.transform(dateStr, 'yyyy-MM-dd');
    }

    addPayment(paymentId, dialogRef) {

        this.submitted = true;

        this.paymentFormGroup.get('customerid')?.setValue(this.customerLedgerDetailData.id);
        // this.paymentFormGroup.patchValue({
        //     customerid: this.customerLedgerDetailData.id
        // });

        this.paymentFormGroup.markAllAsTouched();
        this.paymentFormGroup.updateValueAndValidity();

        if (this.paymentFormGroup.valid) {
            if (this.paymentFormGroup.value.invoiceId == 0) {
                this.paymentFormGroup.value.paytype = "advance";
            } else {
                this.paymentFormGroup.value.paytype = "invoice";
            }

            if (this.selectedInvoice.length == 0) {
                error: (error) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Please select atleat one invoice or advance mode!');
                }


                return;
            }
            const maxSize = 1048576; // 1MB
            if (this.file && this.file.size > maxSize) {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'File size cannot exceed 1MB.!');
                }

                return;
            } else {
                const url = "/record/payment";
                // this.paymentFormGroup.value.customerid = this.customerLedgerDetailData.id;


                this.paymentFormGroup.value.type = "Payment";
                this.createPaymentData = this.paymentFormGroup.value;
                this.createPaymentData.onlinesource = this.paymentFormGroup.controls.onlinesource.value;
                this.createPaymentData.bank = this.paymentFormGroup.value.bank ? this.paymentFormGroup.value.bank : null;
                if (this.paymentFormGroup.controls.chequedate.value) {
                    this.createPaymentData.chequedate = this.paymentFormGroup.controls.chequedate.value ? this.formatDate(this.paymentFormGroup.controls.chequedate.value) : null;
                    this.createPaymentData.chequedatestr = this.paymentFormGroup.controls.chequedate.value ? this.formatDate(this.paymentFormGroup.controls.chequedate.value) : null;
                }
                this.createPaymentData.filename = this.fileName ? this.fileName : null;
                let invoiceId = [];
                this.selectedInvoice.forEach(element => {
                    invoiceId.push(element.id);
                });
                this.createPaymentData.invoiceId = invoiceId;
                // this.createPaymentData.invoices = invoices;
                delete this.createPaymentData.file;
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
                this.createPaymentData.paymentListPojos = paymentListPojos;
                formData.append("file", this.file);
                formData.append("spojo", JSON.stringify(this.createPaymentData));

                this.revenueManagementService.postMethod(url, formData).subscribe(
                    (response: any) => {

                        this.toastr.success('Payment Created Successfully!', 'Success!');

                        this.paymentFormGroup.reset();
                        this.paymentFormGroup.get('chequedate')?.setValue(new Date());
                        dialogRef.close();

                        this.submitted = false;
                        this.destinationbank = false;
                        this.currentPagecustomerPaymentdata = 1;
                        this.invoiceList = [];

                        this.paymentFormGroup.reset();
                        this.paymentFormGroup.markAsPristine();
                        this.paymentFormGroup.markAsUntouched();
                        this.paymentFormGroup.updateValueAndValidity();

                        this.file = "";
                        this.fileName = null;
                        this.isShowInvoiceList = false;

                        this.openCustomersPaymentData(this.customerId, "");

                        // this.paymentFormGroup.reset();
                        // this.paymentFormGroup.markAsPristine();
                        // this.paymentFormGroup.markAsUntouched();
                        // this.paymentFormGroup.updateValueAndValidity();

                        // this.displayrDialog = false;
                        this.selectedInvoice = [];
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        this.submitted = false;
                    }
                );
                this.paymentFormGroup.reset();
            }
        }
        // this.displayRecordPaymentDialog = false;
    }

    closePaymentForm() {
        this.paymentFormGroup.reset();
        this.displayRecordPaymentDialog = false;
        this.submitted = false;
        this.isShowInvoiceList = false;
        this.selectedInvoice = [];
        this.file = "";
        this.fileName = null;
    }

    getFailedPaymentsClose() {
        this.dialog.closeAll();
        this.dialogRef2.close();
    }

    getFailedPayments() {
        this.viewcustomerFailedPaymentData = [];
        const url = "/onlinePayAudit/allByCustId?custId=" + this.customerId;
        this.customerManagementService.getMethodForIntegration(url).subscribe(
            (response: any) => {
                this.viewcustomerFailedPaymentData = response.onlineAuditData;
                if (this.viewcustomerFailedPaymentData.length !== 0) {
                    // this.displayFailedPaymentDialog = true;
                    this.dialogRef2 = this.dialog.open(this.onlinePaymentDialog, {
                        width: '1200px',
                        maxWidth: '90vw',
                        height: 'auto',
                        autoFocus: false,
                        disableClose: true
                    });
                } else {
                    this.toastr.info('No Payment Found !! !');

                }

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    closeFailedPaymentForm() {
        this.displayFailedPaymentDialog = false;
    }

    openFailureReason(data) {
        this.failureReason = data;
        this.failureReasonDialog = true;
        this.dialog.open(this.failureReasonsDialog, { width: '50%' });
    }
    closeFailureReason() {
        this.failureReasonDialog = false;
        this.failureReason = "";
    }

    addToWalletAPI() {
        const url =
            "/addToWalletByOrderId?orderId=" +
            this.addToWalletOrderId +
            "&transactionId=" +
            this.transactionNo;
        this.recordPaymentService.postMethodForIntegration(url, null).subscribe(
            (response: any) => {
                this.dialogRef.close();
                this.dialogRef2.close();
                if (response?.responseCode === 500) {
                    this.toastr.error(`${response?.data}`, 'Failed!');
                    this.dialogRef.close();
                    this.dialogRef2.close();
                    this.dialogRef3.close();

                    return;
                }
                if ([405, 406, 417, 415].includes(response?.responseCode)) {
                    this.toastr.info(`${response?.data}`, 'Info!');
                    this.dialogRef.close();
                    this.dialogRef2.close();
                    this.dialogRef3.close();

                    return;
                }
                this.customerData = response.customerList;
                this.toastr.success(`${response?.data}`, 'Success!');
                this.dialogRef.close();
                this.dialogRef2.close();
                this.dialogRef3.close();

                this.transModal = false;
                this.addToWalletOrderId = "";
                this.transactionNo = "";
                // this.getFailedPayments();
            },
            (error: any) => {
                console.error("Error:", error);
                this.toastr.error(`${error?.error?.ERROR}`, 'Failed!');

            }
        );
    }

    cancelConfirm() {
        this.dialogRef.close();
    }

    ConfirmonTransactionNumber() {
        if (this.addToWalletOrderId) {

            this.dialogRef = this.dialog.open(this.confirmationDialog, {
                width: '700px',
                maxWidth: '70vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to confirm this transaction no?",
            //     header: "Transaction No Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.addToWalletAPI();
            //     },
            //     reject: () => {
            //         error: (error) => {
            //             this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
            //         }

            //     }
            // });
        }
    }

    transactionModal() {
        this.transModal = false;
        this.addToWalletOrderId = "";
        this.transactionNo = "";
    }

    onConvertedRateChange() {
        this.invoiceList.forEach(element => {
            element.convertedAmount = element.testamount * this.convertedExchangeRate;
        });
    }


    @ViewChild('chequeDetailsDialog') chequeDetailsDialog!: TemplateRef<any>;

    displayedChequeColumns: string[] = ['amount', 'chequedate', 'chequeNo'];

    @ViewChild('recordPaymentDailog') recordPaymentDailog!: TemplateRef<any>;
    displayedInvoiceColumns = ['select', 'docnumber', 'createdByName', 'tax', 'totalamount', 'pendingAmount', 'refundAbleAmount',
        'totalamount', 'pendingAmount', 'refundAbleAmount', 'testamount', 'tds', 'abbs']
    displayedColumns: string[] = [
        'custName',
        'paytype',
        'paymode',
        'paymentreferenceno',
        'status',
        'amount',
        'adjustedAmount',
        'createdate',
        'createdByName'
    ];

    displayedColumnsInvoice: string[] = [
        'docnumber',
        'createdByName',
        'tax',
        'totalamount',
        'pendingAmount',
        'refundAbleAmount',
        ...(this.isDisplayConvertedAmount ? ['convertedAmount'] : []),
        'testamount',
        'tdsCheck',
        'abbsCheck'
    ];


    displayedSelectInvoiceColumns = [
        'select',
        'docnumber',
        'createdBy',
        'tax',
        'totalamount',
        'pending',
        'refund',
        'amount',
        'tds',
        'abbs'
    ];
    displayedColumnsOnlinePayment = ['orderId', 'pgTransactionId', 'accountNumber', 'customerUsername', 'payment', 'status', 'gatewayStatus', 'failureDescription', 'paymentDate', 'merchantName', 'transactionDate', 'payerMobileNumber', 'autoPaymentInitiator', 'action']


    @ViewChild('failureReasonsDialog') failureReasonsDialog!: TemplateRef<any>;
    @ViewChild('addTransactionDialog') addTransactionDialog!: TemplateRef<any>;
}
