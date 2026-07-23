import { Component, OnInit, Input, Output, EventEmitter, Inject, TemplateRef, ViewChild } from "@angular/core";
import { Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BillRunMasterService } from "src/app/service/bill-run-master.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';

export interface fetchData {
    paymentId: any,
}


@Component({
    selector: "app-payment-amount-model",
    templateUrl: "./payment-amount-model.component.html",
    styleUrls: ["./payment-amount-model.component.css"],
    standalone: false
})


export class PaymentAmountModelComponent implements OnInit {
    @Input() dialogId: string;
    @Input() paymentId: Observable<any>;
    @Output() closeParentCustt = new EventEmitter();
    @ViewChild("customerPayment") customerPayment: TemplateRef<any>;
    customerPaymentdialogRef!: MatDialogRef<any>;
    viewPaymentListData: any;

    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: number;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    PaymentList: any = [];
    paymentID: any = "";
    totaladjustedAmount = 0;
    displayInvoiceDetails: boolean = false;

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private revenueManagementService: RevenueManagementService,
        private billRunMasterService: BillRunMasterService, public dialogRef: MatDialogRef<PaymentAmountModelComponent>,
        @Inject(MAT_DIALOG_DATA) public data: fetchData,

    ) { }

    ngOnInit(): void {
        this.displayInvoiceDetails = true;
        if (this.data) {
            this.paymentID = this.data.paymentId;
            this.getpaymentDetail("");
        }
        // this.paymentId.subscribe(value => {
        //   if (value.paymentId) {
        //     this.paymentID = value.paymentId;
        //     this.getpaymentDetail("");
        //   }
        // });
    }
    ngAfterViewInit() {
        // this.customerPaymentdialogRef = this.dialog.open(this.customerPayment, {
        //     width: '50%',
        //     maxWidth: '90vw',
        //     height: 'auto',
        //     autoFocus: false,
        //     disableClose: true // same as data-backdrop="static" data-keyboard="false"
        // });
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMasterSlab > 1) {
            this.currentPageMasterSlab = 1;
        }
        this.getpaymentDetail(this.showItemPerPage);
    }

    getpaymentDetail(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.MasteritemsPerPage = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.MasteritemsPerPage = this.pageITEM;
            } else {
                this.MasteritemsPerPage = this.showItemPerPage;
            }
        }
        this.totaladjustedAmount = 0;
        this.PaymentList = [];

        // this.paymentId.subscribe({
        //     next: (data) => {
        //         this.paymentID = data?.paymentId
        //     },
        //     error: (err) => {
        //     },
        //     complete: () => {
        //     }
        // });
        let url = "/invoicemapping/" + this.paymentID;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.PaymentList = response.Invoicelist;

                this.PaymentList.forEach((value, index) => {
                    this.totaladjustedAmount =
                        this.totaladjustedAmount + Number(this.PaymentList[index].adjustedAmount);
                });
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

    pageChangedMasterList(pageNumber) {
        this.currentPageMasterSlab = pageNumber;
        this.getpaymentDetail("");
    }

    closeDisplayInvoiceDetails() {
        this.dialog.closeAll();
        this.closeParentCustt.emit();
        this.displayInvoiceDetails = false;
    }


    displayedColumns: string[] = [
        'docnumber',
        'invoiceNumber',
        'totalamount',
        'adjustedAmount',
        'billdate'
    ];
}
