import { Component, EventEmitter, Inject, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { BillRunMasterService } from "src/app/service/bill-run-master.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { InvoiceDetailsService } from "src/app/service/invoice-details.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import moment from "moment";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';

declare var $: any;
export interface fetchData {
    dialogId: any,
    invoiceID: any,
    custID: any,
    sourceType: any,
    InvoiceDATA: any,
}

@Component({
    selector: "app-invoice-detalis-model",
    templateUrl: "./invoice-detalis-model.component.html",
    styleUrls: ["./invoice-detalis-model.component.css"],
    standalone: false
})


export class InvoiceDetalisModelComponent implements OnInit {
    @ViewChild("InvoiceDetails") InvoiceDetails!: TemplateRef<any>;
    @Input() dialogId: string;
    @Input() invoiceID: any;
    @Input() custID: any;
    @Input() sourceType: any;
    @Input() InvoiceDATA: Observable<any>;
    @Output() closeInvoiceDetails = new EventEmitter();
    invoiceDialogRef: MatDialogRef<any>;
    taxDialogRef: MatDialogRef<any>;
    viewbillInvoiceListData: any = {};
    documentDetailId: any = [];
    viewbillInvoiceInventoryListData: any = [];
    debitDocDetails: any = [];
    debitDocumentTAXRels: any = [];
    debitDocumentTAXRelDtos: any = [];
    taxData: any = [];
    taxtype: string = "";
    showInventory: boolean;
    promiseToPay: boolean = false;
    displayInvoiceMasterDetails: boolean = false;
    displayTaxDetails: boolean = false;
    installmentInterestExists: boolean = false;

    constructor(
        private toastr: ToastrService,
        private customerManagementService: CustomermanagementService,
        private invoiceDetailsService: InvoiceDetailsService,
        private revenueManagementService: RevenueManagementService,
        private messageService: MessageService,
        private spinner: NgxSpinnerService, private dialog: MatDialog,
        private billRunMasterService: BillRunMasterService, public dialogRef: MatDialogRef<InvoiceDetalisModelComponent>,
        @Inject(MAT_DIALOG_DATA) public data: fetchData,
    ) { }

    ngOnInit(): void {
        if (this.data) {
            this.dialogId = this.data.dialogId;
            this.invoiceID = this.data.invoiceID;
            this.custID = this.data.custID;
            this.sourceType = this.data.sourceType;

            // this.invoiceDetailsService.show("InvoiceDetailModal");
            this.displayInvoiceMasterDetails = true;
            const url = "/invoiceDetails/" + this.invoiceID + "/" + this.custID;
            this.revenueManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.viewbillInvoiceListData = response.invoiceDetails;
                    this.debitDocDetails = response.debitDocDetails;
                    this.installmentInterestExists = this.debitDocDetails?.some(
                        (item) => item.installmentInterest != null
                    );

                    this.debitDocumentTAXRels = response.debitDocumentTAXRels;
                    this.documentDetailId = this.debitDocumentTAXRels.map(item => item.documentDetailId);
                    this.debitDocumentTAXRelDtos = response.debitDocumentTAXRelDtos;
                    this.viewbillInvoiceInventoryListData =
                        this.viewbillInvoiceListData.debitDocumentInventoryRels;
                    this.viewbillInvoiceListData.dueDateWithGrace = this.calculateDueDateWithGrace(
                        this.viewbillInvoiceListData.duedate,
                        this.viewbillInvoiceListData.debitDocGraceDays
                    );
                    if (this.viewbillInvoiceInventoryListData != null) this.showInventory = true;
                    else this.showInventory = false;
                    if (this.viewbillInvoiceListData.ispromiseToPayInOldCPR) this.promiseToPay = true;
                    else this.promiseToPay = false;
                },
                error => {
                    console.error('Error loading invoice details', error);
                }
            );
        }
    }
    closeDialog() {
        this.closeInvoiceDetails.emit();
        this.dialogRef.close();
    }
    ngAfterViewInit() {
        // this.invoiceDialogRef = this.dialog.open(this.InvoiceDetails, {
        //     width: '80%',
        //     disableClose: true
        // });
    }
    openTaxModal(documentDetailId: number, type: string): void {
        this.taxtype = type;
        this.taxData = [];

        const specificDetail = this.debitDocumentTAXRels.filter(
            detail => detail.documentDetailId === documentDetailId
        );
        if (this.taxtype === "charge") {
            this.taxData = specificDetail;
        } else {
            this.taxData = this.debitDocumentTAXRels;
        }
        if (this.taxData.length > 0) {
            this.displayTaxDetails = true;

            this.taxDialogRef = this.dialog.open(this.displayTaxDetailsDialog, {
                width: '50%',
                disableClose: true // same as data-backdrop="static" data-keyboard="false"
            });
        } else {
            this.toastr.info(`Tax Data Not Found!`, 'Info!');
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Tax Data Not Found!",
            //     icon: "far fa-times-circle"
            // });
        }
    }

    closeDisplayTaxDetails() {
        if (this.taxDialogRef) {
            this.taxDialogRef.close();
        }
        this.displayTaxDetails = false;
    }

    openTotalTaxModal(id, type): void {
        this.taxtype = type;

        this.taxData = this.debitDocumentTAXRelDtos;

        if (this.taxData.length > 0) {
            this.displayTaxDetails = true;
            this.taxDialogRef = this.dialog.open(this.displayTaxDetailsDialog, {
                width: '50%',
                disableClose: true // same as data-backdrop="static" data-keyboard="false"
            });
        } else {
            this.toastr.info(`Tax Data Not Found!`, 'Info!');
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Tax Data Not Found!",
            //     icon: "far fa-times-circle"
            // });
        }
    }

    close() {
        this.closeInvoiceDetails.emit();
        this.displayInvoiceMasterDetails = false;

    }

    calculateDueDateWithGrace(duedate: string, graceday: number): string {
        if (graceday > 0) {
            return moment(duedate).add(graceday, "days").format("YYYY-MM-DD");
        }
        return moment(duedate).format("YYYY-MM-DD");
    }

    @ViewChild('displayTaxDetailsDialog') displayTaxDetailsDialog!: TemplateRef<any>;

    displayedColumns: string[] = [
        'chargename',
        'subtotal',
        'discount',
        'tax',
        ...(this.installmentInterestExists ? ['installmentInterest'] : []),
        'totalamount',
        'description'
    ];

    inventoryColumns: string[] = [
        'productName',
        'productType',
        'serialNo',
        'mac',
        'assignedDate'
    ];

    get taxDisplayedColumns(): string[] {
        return this.taxtype === 'charge'
            ? ['taxname', 'taxlevel', 'percentage', 'amount']
            : ['taxname', 'percentage', 'amount'];
    }
}
