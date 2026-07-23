import { Component, EventEmitter, OnInit, Output, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { RecordPaymentService } from "src/app/service/record-payment.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

import { CommondropdownService } from "src/app/service/commondropdown.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatSelectChange } from "@angular/material/select";
import { PageEvent } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
declare var $: any;

@Component({
    selector: "app-invoice-record-payment",
    templateUrl: "./invoice-record-payment.component.html",
    styleUrls: ["./invoice-record-payment.component.css"],
    standalone: false
})
export class InvoiceRecordPaymentComponent implements OnInit {
    displayedColumns = ['select', 'Name', 'l-name', 'username'];
    displayedColumns1 = [
        'select',
        'docnumber',
        'createdByName',
        'tax',
        'totalamount',
        'pendingAmount',
        'refundAbleAmount'
    ];
    @Output() CloseParentCustomer = new EventEmitter();
    @ViewChild("ParentCustomerList") ParentCustomerList;
    dialogRef!: MatDialogRef<any>;
    dialogRefInvoice!: MatDialogRef<any>;
    @ViewChild("CustomerInvoice") CustomerInvoice;
    constructor(
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private recordPaymentService: RecordPaymentService,
        public commondropdownService: CommondropdownService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public revenueManagementService: RevenueManagementService,
        loginService: LoginService,
        private toastr: ToastrService,

    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }
    paymentFormGroup: UntypedFormGroup;
    submitted = false;
    customerData: any;
    createPaymentData: any;
    AclClassConstants;
    AclConstants;
    invoiceList: any = [];
    bankDataList: any;
    public loginService: LoginService;
    selectedcustInvoice: any = [];
    taxData: any = [];
    ngOnInit(): void {
        this.paymentFormGroup = this.fb.group({
            amount: ["", [Validators.required]],
            customerid: ["", Validators.required],
            paymentdate: [""],
            paymentreferenceno: [""],
            paymode: ["Credit Note"],
            referenceno: ["", Validators.required],
            remark: ["", Validators.required],
            invoiceId: ["", Validators.required],
            type: ["creditnote"],
            paytype: ["creditnote"],
        });
        // this.paymentFormGroup.controls.branch.disable();
        // this.paymentFormGroup.controls.chequedate.disable();
        // this.paymentFormGroup.controls.bank.disable();
        // this.paymentFormGroup.controls.bankManagement.disable();
        // this.paymentFormGroup.controls.chequeno.disable();
        this.paymentFormGroup.controls.paymentreferenceno.disable();
        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.getPostpaidplanData();
        // this.getCustomer();this api will remove by shivam
        this.getBankDetail();
        const serviceArea = localStorage.getItem("serviceArea");

        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }

        this.paymentFormGroup.controls.customerid.disable();
        this.paymentFormGroup.controls.invoiceId.disable();
    }

    changeCustomer(event) {
        const url = "/invoiceListForCreditNote/byCustomer/";
        this.invoiceList = [];

        this.revenueManagementService.getAllInvoiceByCustomer(url + event.value).subscribe(
            (response: any) => {
                this.invoiceList = response.invoiceList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }


    addPayment(paymentId): void {
        this.submitted = true;
        this.paymentFormGroup.controls.invoiceId.enable();
        this.paymentFormGroup.controls.customerid.enable();




        if (this.paymentFormGroup.invalid) {
            Object.values(this.paymentFormGroup.controls).forEach(control => {
                if (control.invalid) {
                    control.updateValueAndValidity();
                }
            });
            return;
        }
        if (this.paymentFormGroup.valid) {
            this.paymentFormGroup.value.type = "creditnote";
            this.paymentFormGroup.value.paymode = "Credit Note";
            this.paymentFormGroup.value.paytype = "creditnote";
            this.paymentFormGroup.value.paymentdate = new Date();
            this.createPaymentData = this.paymentFormGroup.value;
            let invoiceId = [];
            invoiceId.push(this.paymentFormGroup.controls.invoiceId.value);

            this.createPaymentData.invoiceId = invoiceId;
            this.selectedcustInvoice.refundAbleAmount;

            const formData = new FormData();

            formData.append("spojo", JSON.stringify(this.createPaymentData));

            const url = "/record/payment";

            this.revenueManagementService.postMethod(url, formData).subscribe(
                (response: any) => {
                    this.submitted = false;
                    this.paymentFormGroup.reset();
                    this.toastr.success(`${response.message}`, 'Success!');
                },
                (error: any) => {
                    if (error.error.status == 417) {
                        this.toastr.info(`${error.error.ERROR}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                }
            );
        }

    }

    selectCustomerInvoice: boolean = false;
    modalOpenCustomerInvoice() {
        this.selectCustomerInvoice = true;
        this.newFirst = 0;
        this.selectedcustInvoice = [];

        this.dialogRefInvoice = this.dialog.open(this.CustomerInvoice, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

    }
    modalCloseCustomerInvoice() {
        this.dialogRefInvoice.close();
        this.selectCustomerInvoice = false;
        this.newFirst = 0;
    }
    saveSelCustomerInvoice() {
        this.paymentFormGroup.patchValue({
            invoiceId: this.selectedcustInvoice.id,
            amount: this.selectedcustInvoice.refundAbleAmount,
            // includeTds: this.selectedcustInvoice.includeTds,
            // includeAbbs: this.selectedcustInvoice.includeAbbs,
        });
        this.dialogRefInvoice.close();
        this.selectCustomerInvoice = false;
        this.newFirst = 0;
    }
    taxDetails: boolean = false;
    openTaxModal(id) {
        this.invoiceList.forEach(element => {
            if (element.id == id) this.taxData = element.debitDocumentTAXRels;
        });
        if (this.taxData.length > 0) {
            this.taxDetails = true;
        } else {
            this.toastr.info(`Tax Data Not Found!`, 'Info!');
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Tax Data Not Found!",
            //     icon: "far fa-times-circle",
            // });
        }
    }
    // paginate(event) {
    //   console.log("page event", this.selectedcustInvoice);
    //   this.currentPageParentCustomerListdata = event.page + 1;
    //   // this.first = event.first;
    //   this.changeCustomer()
    // }

    canExit() {
        if (!this.paymentFormGroup.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                this.confirmationService.confirm({
                    header: "Alert",
                    message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                    icon: "pi pi-info-circle",
                    accept: () => {
                        observer.next(true);
                        observer.complete();
                    },
                    reject: () => {
                        observer.next(false);
                        observer.complete();
                    },
                });
                return false;
            });
        }
    }

    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentCustomerListdatatotalRecords: any;
    selectedParentCust: any = [];
    selectedParentCustId: any;
    parentCustList: any;
    newFirst = 0;
    searchParentCustOption: any = "";
    searchParentCustValue = "";
    parentFieldEnable = false;
    customerList = [];
    searchOptionSelect = this.commondropdownService.customerSearchOption;

    // customer dropdown

    getParentCustomerData() {
        let currentPage;
        currentPage = this.currentPageParentCustomerListdata;
        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage,
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
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }
    selectParentCustomer: boolean = false;
    async modalOpenParentCustomer() {

        this.selectParentCustomer = true;
        this.getParentCustomerData();
        this.newFirst = 1;
        this.selectedParentCust = [];
        this.dialogRef = this.dialog.open(this.ParentCustomerList, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

    }

    modalCloseParentCustomer() {
        this.dialogRef.close();
        this.CloseParentCustomer.emit();
        this.selectParentCustomer = false;
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.customerList = [];

        if (this.selectedParentCust) {
            this.paymentFormGroup.patchValue({ customerid: this.selectedParentCust.id });
        }

    }

    async saveSelCustomer() {
        this.parentCustList = [
            {
                id: Number(this.selectedParentCust.id),
                name: this.selectedParentCust.name,
            },
        ];

        this.paymentFormGroup.patchValue({
            customerid: Number(this.selectedParentCust.id),
        });

        let customerId = {
            value: this.selectedParentCust.id,
        };
        this.changeCustomer(customerId);
        this.modalCloseParentCustomer();
    }

    paginate(event: PageEvent) {

        this.currentPageParentCustomerListdata = event.pageIndex + 1;

        this.parentCustomerListdataitemsPerPage = event.pageSize;

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
                    filterCondition: "and",
                },
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage,
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
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle",
                    // });
                    // this.customerListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle",
                    // });
                }
            }
        );
    }

    selParentSearchOption(event: MatSelectChange) {
        this.parentFieldEnable = !event.value;
        this.searchParentCustOption = event;
    }

    newOfferPriceValidation(input) {
        var num = String.fromCharCode(input.which);
        const charStr = String.fromCharCode(input.which);

        if (
            !/^\d$/.test(charStr) &&
            charStr !== "0" &&
            charStr !== "1" &&
            charStr !== "2" &&
            charStr !== "3" &&
            charStr !== "4" &&
            charStr !== "5" &&
            charStr !== "6" &&
            charStr !== "7" &&
            charStr !== "8" &&
            charStr !== "9"
        ) {
            event.preventDefault();
        }
    }
}
