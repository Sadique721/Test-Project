import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { map, Observable, Observer } from "rxjs";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
declare var $: any;

@Component({
    selector: "app-record-payment",
    templateUrl: "./record-payment.component.html",
    styleUrls: ["./record-payment.component.css"],
    standalone: false
})
export class RecordPaymentComponent implements OnInit {
    @ViewChild("dt", { static: false }) dt: any;
    paymentFormGroup: UntypedFormGroup;
    submitted = false;
    customerData: any;
    createPaymentData: any;
    AclClassConstants;
    AclConstants;
    invoiceList: any = [];
    onlineSourceData = [];

    paymentMode = [
        // { label: "Cash", value: "Cash" },
        // { label: "Cheque", value: "Cheque" },
        // { label: "Online", value: "Online" },
        // { label: "EFTs", value: "EFTs" },
        // { label: "Barter", value: "barter" },
        // { label: "Direct Deposit", value: "Direct Deposit" },
        // { label: "VAT Receiveable", value: "VAT Receiveable" },
        // { label: "Non Cash Adjustment", value: "Non Cash Adjustment" },
        // { label: "POS Adjustmnet", value: "POS Adjustmnet" },
        // { label: "QR", value: "QR" },
        // { label: "OPG Adjustment", value: "OPG Adjustment" },
    ];
    invoicedropdownValue = [{ docnumber: "Advance", id: 0 }];
    customerList: any;

    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentCustomerListdatatotalRecords: any;
    selectedParentCust: any = [];
    selectedParentCustId: any;
    parentCustList: any;
    editCustomerId: any;
    newFirst = 0;
    searchParentCustOption = "";
    searchParentCustValue = "";
    serviceAreaDisable = false;
    parentFieldEnable = false;
    public loginService: LoginService;
    bankDataList: any;
    bankDestination: any;
    searchOptionSelect = this.commondropdownService.customerSearchOption;
    fileName: any;
    file: any = "";
    taxData: any = [];
    selectedInvoices: any[];
    isShowInvoiceList: boolean = false;
    recordPaymentAccess: boolean = false;
    masterSelected: boolean;
    checklist: any;
    checkedList: any[] = [];
    tdsInclude = false;
    abbsInclude = false;
    tdsPercent: number;
    abbsPercent: number;
    chequeDateName = "Transaction Date";
    datePlaceholder = "Select transaction date";
    todaysDateString = new Date().toLocaleDateString('en-US');
    custType: string;
    collectedCurrency: string;
    isDisplayConvertedAmount: boolean = false;
    convertedExchangeRate: any;
    currency: string;
    systemConfigCurrency: string;

    constructor(
        private fb: UntypedFormBuilder,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private revenueManagementService: RevenueManagementService,
        private recordPaymentService: RecordPaymentService,
        public commondropdownService: CommondropdownService, private dialog: MatDialog,
        public savbillCommonBaseService: SavbillCommonBaseService,
        loginService: LoginService,
        private systemService: SystemconfigService
    ) {
        this.recordPaymentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_PAYMENT_RECORD
                : POST_CUST_CONSTANTS.POST_CUST_PAYMENT_RECORD
        );
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.systemService.getConfigurationByName("TDS").subscribe((res: any) => {
            this.tdsPercent = res.data.value;
        });
        this.systemService.getConfigurationByName("ABBS").subscribe((res: any) => {
            this.abbsPercent = res.data.value;
        });
        const url = "/commonList/paymentMode";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.paymentMode = response.dataList;
            },
            (error: any) => { }
        );
    }

    ngOnInit(): void {
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
        // this.paymentFormGroup.controls.bank.disable();
        // this.paymentFormGroup.controls.branch.disable();
        // this.paymentFormGroup.controls.chequedate.disable();
        // this.paymentFormGroup.controls.bankManagement.disable();
        // this.paymentFormGroup.controls.chequeno.disable();
        // this.paymentFormGroup.controls.onlinesource.disable();
        this.resetPayMode();
        // this.getCustomer();this api will remove by shivam
        this.getBankDetail();
        this.getBankDestinationDetail();

        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getsystemconfigList();
        const serviceArea = localStorage.getItem("serviceArea");

        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        this.commondropdownService.getAllCurrencyData();
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
        this.systemService.getConfigurationByName("CONVERTED_EXCHANGE_RATE").subscribe((res: any) => {
            this.convertedExchangeRate = parseFloat(res?.data?.value.replace(/,/g, "")) || 1;
        });
    }

    changeCustomer(custId) {
        const url = `/invoiceList/byCustomer/${custId}`;
        this.invoiceList = [];
        this.masterSelected = false;

        this.revenueManagementService.getAllInvoiceByCustomer(url).subscribe(
            (response: any) => {
                const invoiceList = response.invoiceList;
                if (response.invoiceList == null || response.invoiceList.length === 0) {
                    this.invoiceList.push(...this.invoicedropdownValue);
                } else {
                    this.invoiceList.push(...invoiceList);
                }
                this.invoiceList.forEach(element => {
                    element.tdsCheck = 0;
                    element.abbsCheck = 0;
                    element.includeTds = false;
                    element.includeAbbs = false;
                    element.isSelected = false;
                    if (element.adjustedAmount) {
                        element.testamount = (element.totalamount - element.adjustedAmount).toFixed(2);
                    } else {
                        element.testamount = element.totalamount?.toFixed(2);
                    }
                    element.convertedAmount = element.testamount * this.convertedExchangeRate;
                    element.currency = this.selectedParentCust?.currency
                        ? this.selectedParentCust?.currency
                        : this.currency;
                });


                this.dialog.open(this.selectInvoiceDailog, {
                    width: '80%',
                    disableClose: true // same as data-backdrop="static" data-keyboard="false"
                });
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

    getCustomer() {
        // const url = "/customers/list";
        // const custerlist = {
        //     page: 1,
        //     pageSize: 10000
        // };
        // this.recordPaymentService.postMethod(url, custerlist).subscribe(
        //     (response: any) => {
        //         this.customerData = response.customerList;
        //         // console.log("this.customerData", this.customerData);
        //     },
        //     (error: any) => {
        //         // console.log(error, "error")
        //         this.toastr.error(`${error.error.ERROR}`, 'Failed!')
        //         // this.messageService.add({
        //         //     severity: "error",
        //         //     summary: "Error",
        //         //     detail: error.error.ERROR,
        //         //     icon: "far fa-times-circle"
        //         // });
        //     }
        // );
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

    getBankDestinationDetail() {
        const url = "/bankManagement/searchByStatus?banktype=operator";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                // this.bankDataList = response.dataList.banktype;
                this.bankDestination = response.dataList;
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

    addPayment(paymentId) {
        this.submitted = true;
        if (this.paymentFormGroup.valid) {
            if (this.paymentFormGroup.value.invoiceId == 0) {
                this.paymentFormGroup.value.paytype = "advance";
            } else {
                this.paymentFormGroup.value.paytype = "invoice";
            }
            if (this.checkedList.length == 0) {
                this.toastr.error(`Please select atleat one invoice or advance mode.`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: "Please select atleat one invoice or advance mode.",
                //     icon: "far fa-check-circle"
                // });
                return;
            } else {
                const url = "/record/payment";
                this.paymentFormGroup.value.type = "Payment";
                this.createPaymentData = this.paymentFormGroup.value;
                this.createPaymentData.onlinesource = this.paymentFormGroup.controls.onlinesource.value;
                this.createPaymentData.filename = this.fileName;
                this.selectedParentCust.id = "";
                this.resetPayMode();
                const invoiceId = [];
                // const invoices = [];
                this.checkedList.forEach(element => {
                    invoiceId.push(element.id);
                    // invoices.push({
                    //   id: element.id,
                    //   amount: element.paymentAmount,
                    //   includeTds: element.includeTds,
                    //   includeAbbs: element.includeAbbs,
                    // });
                });
                this.createPaymentData.invoiceId = invoiceId;
                // this.createPaymentData.invoices = invoices;
                delete this.createPaymentData.file;
                const formData = new FormData();
                let fileArray: FileList;
                var paymentListPojos = [];

                this.checkedList.forEach(element => {
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
                        this.submitted = false;
                        this.paymentFormGroup.reset();
                        this.paymentFormGroup.get('chequedate').setValue(new Date());
                        this.isShowInvoiceList = false;
                        this.paymentFormGroup.get("type").setValue("Payment");
                        this.parentCustList = [];
                        this.invoiceList = [];
                        this.checkedList = [];
                        this.toastr.success(`Payment Created Successfully`, 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Payment Created Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });
                    },
                    (error: any) => {
                        if (error.error.status == 500) {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                            // this.messageService.add({
                            //     severity: "error",
                            //     summary: "Attachment size too large",
                            //     detail: error.error.ERROR,
                            //     icon: "pi pi-info-circle"
                            // });
                        } else {
                            // console.log(error, "error")
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
    }

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
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.updateValueAndValidity();
            //  this.chequeDateName = "Transaction date";
        } else if (payMode == "Online".toLowerCase()) {
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.referenceno.setValidators([Validators.required]);
            this.paymentFormGroup.controls.reciptNo.enable();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            //  this.chequeDateName = "Transaction date";
        } else if (payMode == "Direct Deposit".toLowerCase()) {
            this.paymentFormGroup.controls.branch.enable();
            this.paymentFormGroup.controls.chequedate.enable();
            this.paymentFormGroup.controls.chequedate.setValidators([Validators.required]);
            this.paymentFormGroup.controls.chequedate.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
            this.paymentFormGroup.controls.reciptNo.disable();
            this.paymentFormGroup.controls.destinationBank.updateValueAndValidity();
            //  this.chequeDateName = "Transaction date";
        } else if (payMode == "NEFT_RTGS".toLowerCase()) {
            this.paymentFormGroup.controls.bankManagement.enable();
            this.paymentFormGroup.controls.bankManagement.setValidators([Validators.required]);
            this.paymentFormGroup.controls.bankManagement.updateValueAndValidity();
            this.paymentFormGroup.controls.destinationBank.enable();
            this.paymentFormGroup.controls.destinationBank.setValidators([Validators.required]);
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
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
            this.paymentFormGroup.controls.referenceno.clearValidators();
            this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
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
                console.log(error, "error");
            }
        );
        this.paymentFormGroup.updateValueAndValidity();
        let isAbbsTdsMode = this.checkPaymentMode(payMode);
        if (isAbbsTdsMode) {
            this.paymentFormGroup.patchValue({
                tdsAmount: 0,
                abbsAmount: 0
            });
            if (this.checkedList.length > 0) {
                this.checkedList.map(element => {
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
        this.paymentFormGroup.controls.referenceno.clearValidators();
        this.paymentFormGroup.controls.referenceno.updateValueAndValidity();
        this.paymentFormGroup.controls.chequedate.setValidators([]);
        this.paymentFormGroup.controls.destinationBank.setValidators([]);
        this.paymentFormGroup.controls.bankManagement.setValidators([]);
        this.paymentFormGroup.controls.chequeno.setValidators([]);
        this.paymentFormGroup.controls.onlinesource.setValidators([]);
        this.paymentFormGroup.updateValueAndValidity();
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
        }
    }

    getParentCustomerData() {
        let currentPage;
        currentPage = this.currentPageParentCustomerListdata;
        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const url = "/customers/list";
        this.recordPaymentService.postMethod(url, data).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                this.newFirst = 1;
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
    selectParentCustomer: boolean = false;
    async modalOpenParentCustomer() {
        this.selectParentCustomer = true;
        this.dialog.open(this.selectParentCustomerDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        await this.getParentCustomerData();
        this.newFirst = 1;
        this.selectedParentCust = [];
        //  console.log("this.newFirst2", this.newFirst)
    }

    modalCloseParentCustomer() {
        this.selectParentCustomer = false;
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.customerList = [];

        // console.log("this.newFirst1", this.newFirst)
    }

    async saveSelCustomer(dialogRef) {
        this.parentCustList = [
            {
                id: Number(this.selectedParentCust.id),
                name: this.selectedParentCust.name
            }
        ];

        this.paymentFormGroup.patchValue({
            customerid: Number(this.selectedParentCust.id)
        });
        dialogRef.close()
        this.modalCloseParentCustomer();
        // if (this.selectedParentCust.id) {
        //     this.changeCustomer(this.selectedParentCust.id);
        // }
    }

    paginate(event) {
        this.currentPageParentCustomerListdata = event.pageIndex + 1;
        this.parentCustomerListdataitemsPerPage = event.pageSize
        // this.first = event.first;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData();
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
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
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage
        };

        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();

        const url = "/subscriber/getByInvoiceType/search/Group";
        // console.log("this.searchData", this.searchData)
        this.recordPaymentService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 400 || error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    // this.customerListData = [];
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

    selParentSearchOption(event) {
        // console.log("value", event.value);
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    onFileChange(event) {
        if (event.target.files.length > 0) {
            this.file = "";
            this.fileName = event.target.files[0].name;
            this.file = event.target.files[0];
            // this.paymentFormGroup.patchValue({
            //   file: file,
            // });
        }
    }

    closeDialog() {
        this.dialog.closeAll();
        this.isShowInvoiceList = false;
    }

    selectInvoice: boolean = false;
    modalOpenInvoice() {

        this.isShowInvoiceList = true;
        this.selectInvoice = true;
        this.newFirst = 0;
        this.isDisplayConvertedAmount = false;
        this.collectedCurrency = this.selectedParentCust?.currency
            ? this.selectedParentCust?.currency
            : this.currency;

        if (this.selectedParentCust.id) {
            this.changeCustomer(this.selectedParentCust.id);
        } else {
            this.dialog.open(this.selectInvoiceDailog, {
                width: '1000px',
                disableClose: true
            })
        }

    }

    // The master checkbox will check/ uncheck all items
    checkUncheckAll() {
        for (let i = 0; i < this.invoiceList.length; i++) {
            this.invoiceList[i].isSelected = this.masterSelected;
        }
        this.getCheckedItemList();
    }

    // Check All Checkbox Checked
    isAllSelected() {
        this.masterSelected = this.invoiceList.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemList();
    }

    // Get List of Checked Items
    getCheckedItemList() {
        this.checkedList = [];
        for (let i = 0; i < this.invoiceList.length; i++) {
            if (this.invoiceList[i].isSelected) {
                this.checkedList.push(this.invoiceList[i]);
            }
        }
    }

    // bindInvoice() {
    //   if (this.checkedList.length == 1) {
    //     this.paymentFormGroup.patchValue({
    //       invoiceId: this.checkedList[0].id,
    //       amount: this.checkedList[0].totalamount - this.checkedList[0].adjustedAmount,
    //     });
    //   }
    //   this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
    //   if (this.checkedList.length == 0) {
    //     this.messageService.add({
    //       severity: "error",
    //       summary: "Error",
    //       detail: "Please select atleat one invoice or advance mode.",
    //       icon: "far fa-check-circle",
    //     });
    //   } else if (this.checkedList.length == 2) {
    //     this.checkedList.forEach(element => {
    //       if (element.docnumber == "Advance") {
    //         this.checkedList = [];
    //         this.invoiceList.forEach(element => {
    //           element.isSelected = false;
    //         });
    //         this.masterSelected = false;
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: "Please select advance mode value only.",
    //           icon: "far fa-check-circle",
    //         });
    //       }
    //     });
    //   }
    // }

    Amount: any = 0;
    bindInvoice(dialogRef) {
        if (this.checkedList.length >= 1) {
            this.selectInvoice = false;
            this.isShowInvoiceList = true;
            this.Amount = 0;
            this.checkedList.forEach(element => {
                if (element.testamount && element.totalamount !== null) {
                    this.Amount += Number(element.testamount);
                }
            });
            this.paymentFormGroup.patchValue({
                invoiceId: this.checkedList[0].id,
                amount: parseFloat(this.Amount).toFixed(2)
            });
            dialogRef.close()
            this.onChangeOFAmountTest(this.checkedList);
        }
        if (this.checkedList.length == 0) {
            this.toastr.error(`Please select atleast one invoice or advance mode.`, 'Failed!')
            // this.messageService.add({
            //     severity: "error",
            //     summary: "Error",
            //     detail: "Please select atleast one invoice or advance mode.",
            //     icon: "far fa-check-circle"
            // });
        } else if (this.checkedList.length == 2) {
            this.checkedList.forEach(element => {
                if (element.docnumber == "Advance") {
                    this.checkedList = [];
                    this.invoiceList.forEach(element => {
                        element.isSelected = false;
                    });
                    this.masterSelected = false;
                    this.toastr.error(`Please select advance mode value only.`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Please select advance mode value only.",
                    //     icon: "far fa-check-circle"
                    // });
                }
                dialogRef.close()
            });
        }
    }

    openTaxModal(id) {
        this.invoiceList.forEach(element => {
            if (element.id == id) {
                this.taxData = element.debitDocumentTAXRels;
            }
        });
        if (this.taxData.length > 0) {
            $("#taxDetails").modal("show");
        } else {
            this.toastr.info(`Tax Data Not Found!`, 'Info!')
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Tax Data Not Found!",
            //     icon: "far fa-times-circle"
            // });
        }
    }

    // calculateTDS(event) {
    //   if (!event.target.checked) {
    //     this.tdsInclude = false;
    //     this.paymentFormGroup.controls.tdsAmount.disable();
    //     this.paymentFormGroup.controls.tdsAmount.setValue(0);
    //   } else {
    //     this.tdsInclude = true;
    //     this.paymentFormGroup.controls.tdsAmount.enable();
    //     this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
    //   }
    // }

    // calculateABBS(event) {
    //   if (!event.target.checked) {
    //     this.abbsInclude = false;
    //     this.paymentFormGroup.controls.abbsAmount.disable();
    //     this.paymentFormGroup.controls.abbsAmount.setValue(0);
    //   } else {
    //     this.abbsInclude = true;
    //     this.paymentFormGroup.controls.abbsAmount.enable();
    //     this.onChangeOFAmount(this.paymentFormGroup.controls.amount.value);
    //   }
    // }

    onChangeOFAmount(event) {
        const tdsAmount = (event * this.tdsPercent) / 100;
        const abbsAmount = (event * this.abbsPercent) / 100;

        // let tdsAmount = 0;
        // let abbsAmount = 0;
        // this.checkedList.forEach(element => {
        //   tdsAmount += element.includeTds ? (element.totalamount * this.tdsPercent) / 100 : 0;
        //   abbsAmount += element.includeAbbs ? (element.totalamount * this.abbsPercent) / 100 : 0;
        // });
        if (!this.paymentFormGroup.controls.abbsAmount.disabled && this.abbsInclude) {
            this.paymentFormGroup.controls.abbsAmount.setValue(abbsAmount);
        }
        if (!this.paymentFormGroup.controls.tdsAmount.disabled && this.tdsInclude) {
            this.paymentFormGroup.controls.tdsAmount.setValue(tdsAmount);
        }
    }

    // onChangeOFTDS(event) {
    //   const tdsAmount = event;
    //   const abbsAmount = this.paymentFormGroup.controls.abbsAmount.value;
    //   const totalAmount = this.paymentFormGroup.controls.amount.value;
    //   const diff = totalAmount - abbsAmount - tdsAmount;

    //   if (diff < 0 && tdsAmount != 0) {
    //     this.paymentFormGroup.controls.tdsAmount.setValue(0);
    //   }
    // }

    // onChangeOFABBS(event) {
    //   const abbsAmount = event;
    //   const tdsAmount = this.paymentFormGroup.controls.tdsAmount.value;
    //   const totalAmount = this.paymentFormGroup.controls.amount.value;
    //   const diff = totalAmount - abbsAmount - tdsAmount;

    //   if (diff < 0 && abbsAmount != 0) {
    //     this.paymentFormGroup.controls.abbsAmount.setValue(0);
    //   }
    // }
    canExit() {
        if (!this.paymentFormGroup.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                    width: "400px",
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });
                dialogRef.afterClosed().subscribe(result => {
                    if (result) {
                        observer.next(true);
                        observer.complete();
                    } else {
                        observer.next(false);
                        observer.complete();
                    }
                });
                return false;
            }
            )
        }
    }

    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    onChangeOFTDSTest(event, data) {
        if (event && data.testamount) {
            data.tdsCheck = ((data.totalamount * this.tdsPercent) / 100).toFixed(2);
        } else {
            data.tdsCheck = 0;
        }
    }

    onChangeOFABBSTest(event, data) {
        if (event && data.testamount) {
            data.abbsCheck = ((data.totalamount * this.abbsPercent) / 100).toFixed(2);
        } else {
            data.abbsCheck = 0;
        }
    }
    isTdsFlag: boolean = false;
    isAbbsFlag: boolean = false;
    testamount: number = 0;
    onChangeOFAmountTest(event) {
        if (this.checkedList.length >= 1) {
            let formPayModeValue = this.paymentFormGroup.controls.paymode.value.toLowerCase();
            let isAbbsTdsMode = this.checkPaymentMode(formPayModeValue);
            let totaltdsAmount = 0;
            let totalabbsAmount = 0;
            this.checkedList.forEach(element => {
                let tds = 0;
                let abbs = 0;
                if (element.includeTds && element.testamount != null && element.testamount > 0) {
                    if (element.includeTds === true) {
                        tds = Number(element.tdsCheck);
                        totaltdsAmount = Number(element.tdsCheck) + Number(totaltdsAmount);
                        this.isTdsFlag = true;
                    }
                }
                if (element.includeAbbs && element.testamount != null && element.testamount > 0) {
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
                if (this.isAbbsFlag) {
                    this.paymentFormGroup.controls.abbsAmount.setValue(abbsAmount);
                }
                if (this.isTdsFlag) {
                    this.paymentFormGroup.controls.tdsAmount.setValue(tdsAmount);
                }
            }
        }
    }
    isSelectedInvoice = true;
    onSelectedInvoice(event, data) {
        if (event > 0) {
            // this.isSelectedInvoice = false;
            // if (data.includeTds) {
            //   data.tdsCheck = (data.testamount * this.tdsPercent) / 100;
            // }
            // if (data.includeAbbs) {
            //   data.abbsCheck = (data.testamount * this.abbsPercent) / 100;
            // }
        } else {
            data.includeTds = false;
            data.includeAbbs = false;
            data.tdsCheck = 0;
            data.abbsCheck = 0;
        }
        data.convertedAmount = data.testamount * this.convertedExchangeRate;
    }

    closeInvoiceListModel() {
        this.selectInvoice = false;
        this.masterSelected = false;
    }

    checkPaymentMode(formPayModeValue) {
        if (
            formPayModeValue == "vatreceiveable" ||
            formPayModeValue == "tds" ||
            formPayModeValue == "abbs"
        ) {
            return true;
        } else {
            return false;
        }
    }

    onCurrencyChange(event: any, invoice: any) {
        // invoice.selectedCurrency = event.value;
        // invoice.isDisplayConvertedAmount = event.value !== this.customerLedgerDetailData?.currency;
        this.isDisplayConvertedAmount =
            event.value !=
            (this.selectedParentCust?.currency ? this.selectedParentCust?.currency : this.currency);
    }

    onConvertedAmountChange(event, data) {
        data.testamount = event / this.convertedExchangeRate;
        // data.convertedAmount = event;
    }

    onConvertedRateChange() {
        this.invoiceList.forEach(element => {
            element.convertedAmount = element.testamount * this.convertedExchangeRate;
        });
    }
    displayedColumns: string[] = [
        'docnumber',
        'createdByName',
        'tax',
        'totalamount',
        'pendingAmount',
        'refundAbleAmount',
        ...(this.isDisplayConvertedAmount ? ['convertedAmount'] : []),
        'testamount',
        'tds',
        'abbs'
    ];
    displayedInvoiceColumns = ['select', 'docnumber', 'createdByName', 'tax', 'totalamount', 'pendingAmount', 'refundAbleAmount',
        'testamount', 'tds', 'abbs']

    displayedColumnsCutomer: string[] = ['select', 'name', 'username'];

    @ViewChild('selectInvoiceDailog') selectInvoiceDailog!: TemplateRef<any>;
    @ViewChild('selectParentCustomerDialog') selectParentCustomerDialog!: TemplateRef<any>
}
