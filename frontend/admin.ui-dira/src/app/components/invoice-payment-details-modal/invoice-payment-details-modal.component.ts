import { Component, OnInit, Input, ViewChild, TemplateRef, Inject } from "@angular/core";
import { Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BillRunMasterService } from "src/app/service/bill-run-master.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
export interface fetchData {
    invoiceId: any
}
@Component({
    selector: "app-invoice-payment-details-modal",
    templateUrl: "./invoice-payment-details-modal.component.html",
    styleUrls: ["./invoice-payment-details-modal.component.css"],
    standalone: false
})
export class InvoicePaymentDetailsModalComponent implements OnInit {
    @Input() dialogId: string;
    @Input() invoiceId: Observable<any>;
    viewInvoicePaymentListData: any;

    currentPageinvoiceMasterSlab = 1;
    invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    invoiceMastertotalRecords: String;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    InvoicePaymentList: any = [];
    iNVOICEID: any = "";
    totaladjustedAmount = 0;

    constructor(
        private toastr: ToastrService,
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private revenuemanagementservice: RevenueManagementService, private dialog: MatDialog,
        private billRunMasterService: BillRunMasterService, public dialogRef: MatDialogRef<InvoicePaymentDetailsModalComponent>,
        @Inject(MAT_DIALOG_DATA) public data: fetchData,
    ) { }

    ngOnInit(): void {
        if (this.data) {
            this.iNVOICEID = this.data.invoiceId;
            this.getpaymentDetail("");
        }
        // this.invoiceId.subscribe(value => {
        //     if (value.invoiceId) {
        //         this.iNVOICEID = value.invoiceId;
        //         this.getpaymentDetail("");
        //     }
        // });
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageinvoiceMasterSlab > 1) {
            this.currentPageinvoiceMasterSlab = 1;
        }
        this.getpaymentDetail(this.showItemPerPage);
    }

    getpaymentDetail(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.invoiceMasteritemsPerPage = size;
        } else {
            size = this.invoiceMasteritemsPerPage
            // if (this.showItemPerPage == 0) {
            //     this.invoiceMasteritemsPerPage = this.pageITEM;
            // } else {
            //     this.invoiceMasteritemsPerPage = this.showItemPerPage;
            // }
        }
        this.totaladjustedAmount = 0;
        this.InvoicePaymentList = [];

        // /api/v1/AdjustedPaymentAgainstInvoice/{invoiceId}

        let url = "/AdjustedPaymentAgainstInvoice/" + this.iNVOICEID;
        this.revenuemanagementservice.getMethod(url).subscribe(
            (response: any) => {
                this.InvoicePaymentList = response.Paymentlist;

                this.InvoicePaymentList.forEach((value, index) => {
                    this.totaladjustedAmount =
                        this.totaladjustedAmount + Number(this.InvoicePaymentList[index].adjustedAmount);
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

    pageChangedinvoiceMasterList(pageNumber) {
        this.currentPageinvoiceMasterSlab = pageNumber;
        this.getpaymentDetail("");
    }

    displayedColumns: string[] = [
        'referenceNumber',
        'paymode',
        'type',
        'status',
        'amount',
        'adjustedAmount',
        'paymentdate'
    ];

    @ViewChild('partnerrALLDeatilsShowDailog') partnerrALLDeatilsShowDailog!: TemplateRef<any>;

}
