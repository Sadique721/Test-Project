import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { PageEvent } from "@angular/material/paginator";


declare var $: any;

@Component({
    selector: "app-cust-credit-note",
    templateUrl: "./cust-credit-note.component.html",
    styleUrls: ["./cust-credit-note.component.css"],
    standalone: false
})
export class CustCreditNoteComponent implements OnInit {
    @ViewChild('addCreditNoteDialog') addCreditNoteDialog: TemplateRef<any>;
    @ViewChild('selectInvoiceDialog') selectInvoiceDialog: TemplateRef<any>;
    @ViewChild('invoiceDetailsDialog') invoiceDetailsDialog: TemplateRef<any>;

    invoiceDetailRef: MatDialogRef<any>;
    addcreditnoteDialogRef: MatDialogRef<any>;
    selectinvoiceDialogRef: MatDialogRef<any>;

    custData: any = {};
    serstatus: any = {};
    customerId = 0;
    custType: string = "";
    paymentFormGroup: UntypedFormGroup;
    displayInvoiceDetails: boolean = false;
    addCreditNoteBtn: boolean = false;
    submitted = false;
    customerData: any;
    invoiceList: any = [];
    createPaymentData: any;
    selectedInvoice: any = null;
    newFirst: number;
    currentPagePaymentSlab = 1;
    paymentitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymenttotalRecords = 0;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.PAGE_SIZE_OPTIONS;
    showItemPerPage = 0;
    searchPaymentData: any = [];
    totalPaymentListLength = 0;
    paymentId = new BehaviorSubject({
        paymentId: ""
    });
    currency: string;
    displayAddCreditNote: boolean = false;
    creditNoteAccess: boolean = false;
    displaySelectInvoice: boolean;
    PaymentList: any = [];
    totaladjustedAmount = 0;
    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: number;
    displayedColumns: string[] = [
        'customer',
        'amount',
        'invoiceNo',
        'creditNoteNo',
        'creditDate',
        'generatedBy',
        'remark',
        'status'
    ];

    invoiceDisplayedColumns: string[] = [
        'select',
        'docNumber',
        'createdBy',
        'taxAmount',
        'totalInvoice',
        'pendingAmount',
        'refundableAmount'
    ];
    invoiceDetailsColumns: string[] = [
        'docNumber',
        'invoiceNumber',
        'billAmount',
        'adjustedAmount',
        'billDate'
    ];
    selectedInvoiceDetails: any = null;
    customerNameSetForAddCreditNote: string;

    constructor(
        private toastr: ToastrService,

        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        public revenueManagementService: RevenueManagementService,
        private route: ActivatedRoute,
        private router: Router,
        private systemService: SystemconfigService,
        loginService: LoginService,
        private dialog: MatDialog
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.creditNoteAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CREDIT_NOTE
                : POST_CUST_CONSTANTS.POST_CUST_CREDIT_NOTE
        );
    }

    ngOnInit(): void {
        this.getCustomersDetail(this.customerId);
        this.paymentFormGroup = this.fb.group({
            amount: ["", [Validators.required, Validators.min(1)]],
            customerid: ["", Validators.required],
            paymentreferenceno: [""],
            paymode: ["Credit Note"],
            referenceno: ["", Validators.required],
            remark: ["", Validators.required],
            invoiceId: ["", Validators.required],
            type: ["creditnote"],
            paytype: ["creditnote"]
        });
    }

    // ========== MODAL METHODS ==========

    // Open Add Credit Note Modal
    openAddCreditNoteModal(): void {
        this.submitted = false;

        this.paymentFormGroup.controls.paymentreferenceno.disable();
        this.paymentFormGroup.controls.customerid.disable();
        this.paymentFormGroup.controls.invoiceId.disable();

        this.customerNameSetForAddCreditNote = this.custData.firstname + " " + this.custData.lastname;

        this.addcreditnoteDialogRef = this.dialog.open(this.addCreditNoteDialog, {
            width: '1000px',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });

        const currentInvoiceId = this.paymentFormGroup.get('invoiceId')?.value;
        const currentAmount = this.paymentFormGroup.get('amount')?.value;

        this.paymentFormGroup.reset();

        this.paymentFormGroup.patchValue({
            customerid: this.customerId,
            paymode: 'Credit Note',
            type: 'creditnote',
            paytype: 'creditnote'
        });

        if (currentInvoiceId) {
            this.paymentFormGroup.patchValue({
                invoiceId: currentInvoiceId,
                amount: currentAmount
            });
        }



    }

    closeDisplayAddCreditNote(): void {
        if (this.addcreditnoteDialogRef) {
            this.paymentFormGroup?.reset();
            this.addcreditnoteDialogRef.close();
        }
    }

    modalOpenInvoiceList(): void {

        this.selectedInvoice = null;

        this.selectinvoiceDialogRef = this.dialog.open(this.selectInvoiceDialog, {
            width: '1200px',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });
    }

    modalCloseInvoiceList(): void {
        if (this.selectinvoiceDialogRef) {
            this.selectinvoiceDialogRef.close();
        }
        this.displaySelectInvoice = false;
        this.newFirst = 0;
    }
    // Save Selected Invoice and Return to Add Credit Note Modal
    saveSelInvoice(): void {
        if (this.selectedInvoice) {

            if (this.selectinvoiceDialogRef) {
                this.selectinvoiceDialogRef.close();
            }

            this.paymentFormGroup.patchValue({
                invoiceId: this.selectedInvoice.invoiceNumber || this.selectedInvoice.id,
                amount: this.selectedInvoice.refundAbleAmount || this.selectedInvoice.invoiceAmount
            });

        }
    }

    // Open Invoice Details Modal
    openInvoiceDetailsModal(invoiceData: any): void {
        this.selectedInvoiceDetails = invoiceData;

        this.invoiceDetailRef = this.dialog.open(this.invoiceDetailsDialog, {
            width: '1000px',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });
    }

    // Close Invoice Details Modal
    closeParentCustt(): void {
        if (this.invoiceDetailRef) {
            this.invoiceDetailRef.close();
        }
        this.PaymentList = [];
        this.totaladjustedAmount = 0;
    }

    // ========== DATA METHODS ==========

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
            this.searchPayment("");
            this.getInvoiceByCustomer();
            this.addCreditNoteBtn = true;
            this.custData.planMappingList.forEach(element => {
                if (element.custPlanStatus != "Hold") {
                    this.addCreditNoteBtn = false;
                    return;
                }
            });
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    addNewCreditNote() {
        this.openAddCreditNoteModal();
    }

    getInvoiceByCustomer() {
        const url = "/invoiceListForCreditNote/byCustomer/";
        this.invoiceList = [];
        this.revenueManagementService.getMethod(url + this.custData.id).subscribe(
            (response: any) => {
                response.invoiceList.forEach(element => {
                    if (element.billrunstatus != "VOID") {
                        this.invoiceList.push(element);
                    }
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    addPayment(paymentId): void {
        this.submitted = true;
        if (this.paymentFormGroup.valid) {
            this.createPaymentData = this.paymentFormGroup.getRawValue();
            this.paymentFormGroup.value.type = "creditnote";
            this.paymentFormGroup.value.paymode = "Credit Note";
            this.paymentFormGroup.value.paytype = "creditnote";

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
                        this.closeDisplayAddCreditNote();
                        this.searchPayment("");
                        this.toastr.success(`Successfully Created`, 'Success!');

                    } else {
                        this.toastr.info(`${response.paymentdate}`, 'Info!');
                        this.closeDisplayAddCreditNote();

                    }
                },
                (error: any) => {
                    if (error.error.status == 417) {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        this.closeDisplayAddCreditNote();

                    } else {
                        this.closeDisplayAddCreditNote();
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        this.closeDisplayAddCreditNote();

                    }
                }
            );
        } else {
            this.paymentFormGroup.markAllAsTouched();
            return;
        }
    }

    searchPayment(size) {
        if (size) {
            this.paymentitemsPerPage = size;
        }

        // Construct the URL, you can still pass page and pageSize params for backend filtering if supported
        const url =
            "/payment/search?type=CreditNote&page=" +
            this.currentPagePaymentSlab +
            "&pageSize=" +
            this.paymentitemsPerPage +
            "&customerid=" +
            this.custData.id +
            "&paystatus=&paytodate=&payfromdate=";

        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                // Assume response.creditDocumentPojoList contains full list or filtered by backend

                // Force slicing client-side
                const startIndex = (this.currentPagePaymentSlab - 1) * this.paymentitemsPerPage;
                const endIndex = startIndex + this.paymentitemsPerPage;

                this.paymenttotalRecords = response.pageDetails.totalRecords || response.creditDocumentPojoList.length;

                // Slice data manually to show only itemsPerPage entries for current page
                this.searchPaymentData = response.creditDocumentPojoList.slice(startIndex, endIndex);

                // You may update total count or other UI data here
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }



    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagePaymentSlab > 1) {
            this.currentPagePaymentSlab = 1;
        }
        this.searchPayment(this.showItemPerPage);
    }

    pageChangedPaymentList(event: PageEvent) {
        this.currentPagePaymentSlab = event.pageIndex + 1;
        this.paymentitemsPerPage = event.pageSize;
        this.searchPayment(this.paymentitemsPerPage);
    }


    openPaymentInvoiceModal(paymentId: number): void {
        this.getpaymentDetail(paymentId);

        this.invoiceDetailRef = this.dialog.open(this.invoiceDetailsDialog, {
            width: '1000px',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });
    }
    getpaymentDetail(paymentId: number): void {
        this.totaladjustedAmount = 0;
        this.PaymentList = [];

        const url = `/invoicemapping/${paymentId}`;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {

                this.PaymentList = response.Invoicelist || [];

                // Calculate total adjusted amount
                this.PaymentList.forEach((item: any) => {
                    this.totaladjustedAmount += Number(item.adjustedAmount || 0);
                });
            },
            (error: any) => {
                console.error('Error fetching invoice details:', error);
                this.toastr.error(`${error.error?.ERROR || "Failed to fetch invoice details"}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error?.ERROR || "Failed to fetch invoice details",
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }


    keypressId(event: any) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }
}
