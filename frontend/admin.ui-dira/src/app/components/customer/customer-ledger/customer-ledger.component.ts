import { Component, OnInit, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { BehaviorSubject } from "rxjs";
import { FormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";

import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-customer-ledger",
    templateUrl: "./customer-ledger.component.html",
    styleUrls: ["./customer-ledger.component.scss"],
    standalone: false
})
export class CustomerLedgerComponent implements OnInit {
    @ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;
    @ViewChild(MatSort, { static: false }) sort!: MatSort;

    custType: string = "";
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    customerId: number;

    ledgerDataSource = new MatTableDataSource<any>([]);
    ledgerDisplayedColumns: string[] = [
        'createDate',
        'receiptNo',
        'invoiceNo',
        'category',
        'debit',
        'credit',
        'balAmount',
        'remarks'
    ];

    isCollapsed = false;

    custLedgerForm: UntypedFormGroup;
    customerLedgerSearchKey: string;
    currentPagecustLedgerList = 1;
    legershowItemPerPage = 1;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    custLedgerItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custLedgertotalRecords = 0;

    pageSizeOptions = [5,10,20,50,100];
    pageLimitOptions = RadiusConstants.pageLimitOptions;

    custLedgerSubmitted = false;
    customerLedgerListData: any[] = [];
    customerLedgerDetailData: any;

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

    customerLedgerData: any = {
        title: "",
        firstname: "",
        lastname: "",
        plan: "",
        status: "",
        username: "",
        address: "",
        customerLedgerInfoPojo: {
            openingAmount: "",
            closingBalance: ""
        }
    };

    currency: string = 'USD';

    constructor(
        private spinner: NgxSpinnerService,
        private toastr: ToastrService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        private route: ActivatedRoute,
        private router: Router,
        private fb: UntypedFormBuilder,
        private systemService: SystemconfigService,
        private messageService: MessageService
    ) {
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    async ngOnInit() {
        this.custLedgerForm = this.fb.group({
            startDateCustLedger: ["", Validators.required],
            endDateCustLedger: ["", Validators.required]
        });

        this.getCustomersLedger(this.customerId, "");
        this.getCustomersDetail(this.customerId);
    }

    /**
     * Helper method to format date to yyyy-MM-dd format
     * @param date - Date object to format
     * @returns string in yyyy-MM-dd format
     */
    private formatDateToString(date: Date): string {
        if (!date) return '';

        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');

        return `${year}-${month}-${day}`;
    }

    /**
     * Helper method to convert date input to yyyy-MM-dd format
     * @param dateInput - Date object, string, or null
     * @returns string in yyyy-MM-dd format
     */
    private convertToDateString(dateInput: any): string {
        if (!dateInput) return '';

        let date: Date;

        if (dateInput instanceof Date) {
            date = dateInput;
        } else if (typeof dateInput === 'string') {
            date = new Date(dateInput);
        } else {
            return '';
        }

        // Check if date is valid
        if (isNaN(date.getTime())) {
            return '';
        }

        return this.formatDateToString(date);
    }

    private initializeMatTable() {
        setTimeout(() => {
            if (this.paginator && this.sort) {
                this.ledgerDataSource.paginator = this.paginator;
                this.ledgerDataSource.sort = this.sort;

                this.sort.active = 'createDate';
                this.sort.direction = 'desc';
                this.ledgerDataSource.sort = this.sort;
            }
        });
    }

    toggleCollapse() {
        this.isCollapsed = !this.isCollapsed;
    }

    getCustomersDetail(custId: number) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.customerLedgerDetailData = response.customers;
            this.customerLedgerDetailData?.currency
                ? (this.currency = this.customerLedgerDetailData?.currency)
                : this.systemService
                    .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                    .subscribe((res: any) => {
                        this.currency = res.data.value;
                    });
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    searchCustomerLedger() {
        if (
            !this.customerLedgerSearchKey ||
            this.customerLedgerSearchKey !== this.custLedgerForm.value
        ) {
            this.currentPagecustLedgerList = 1;
        }
        this.customerLedgerSearchKey = this.custLedgerForm.value;

            this.custLedgerItemPerPage = this.custLedgerItemPerPage;
        

        this.custLedgerSubmitted = true;
        if (this.custLedgerForm.valid) {
            // Convert date values to yyyy-MM-dd format before sending to API
            const startDate = this.custLedgerForm.controls.startDateCustLedger.value;
            const endDate = this.custLedgerForm.controls.endDateCustLedger.value;

            this.postdata.CREATE_DATE = this.convertToDateString(startDate);
            this.postdata.END_DATE = this.convertToDateString(endDate);
        }
        this.getCustomersLedger(this.customerId, "");
    }

    clearSearchCustomerLedger() {
        this.postdata.CREATE_DATE = "";
        this.postdata.END_DATE = "";
        this.custLedgerForm.controls.startDateCustLedger.setValue("");
        this.custLedgerForm.controls.endDateCustLedger.setValue("");
        this.custLedgerSubmitted = false;
        this.getCustomersLedger(this.customerId, "");
    }

    getCustomersLedger(custId: number, size: any) {
        let page_list;
        this.customerLedgerSearchKey = "";

        if (size) {
            page_list = size;
            this.custLedgerItemPerPage = size;
        } else {
            
                this.custLedgerItemPerPage = this.custLedgerItemPerPage;
        }

        const url = "/customerLedgers";
        this.postdata.custId = custId;

        this.revenueManagementService.postMethod(url, this.postdata).subscribe(
            (response: any) => {
                this.customerLedgerData = response.customerLedgerDtls;
                this.customerLedgerListData = response.customerLedgerDtls.customerLedgerInfoPojo.debitCreditDetail.map((item: any) => {
                    // Format the date to yyyy-MM-dd format for display
                    let formattedDate = '';
                    if (item.create_DATE) {
                        const date = new Date(item.create_DATE);
                        if (!isNaN(date.getTime())) {
                            formattedDate = this.formatDateToString(date);
                        } else {
                            formattedDate = item.create_DATE; // fallback to original if conversion fails
                        }
                    }

                    return { ...item, create_DATE: formattedDate };
                });

                this.ledgerDataSource.data = this.customerLedgerListData;
                this.custLedgertotalRecords = this.customerLedgerListData.length;

                this.initializeMatTable();
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');

                this.ledgerDataSource.data = [];
                this.custLedgertotalRecords = 0;
            }
        );
    }

    // Material Paginator event handler
    onLedgerPageChange(event: PageEvent) {
        this.custLedgerItemPerPage = event.pageSize;
        this.currentPagecustLedgerList = event.pageIndex + 1;
         this.getCustomersLedger(this.customerId, this.custLedgerItemPerPage);
    }

    // Legacy pagination methods (kept for compatibility)
    pageChangedcustledgerList(pageNumber: number) {
        this.currentPagecustLedgerList = pageNumber;
        this.getCustomersLedger(this.customerId, "");
    }

    TotalLedgerItemPerPage(event: any) {
        this.legershowItemPerPage = Number(event.value);
        if (this.currentPagecustLedgerList > 1) {
            this.currentPagecustLedgerList = 1;
        }
        if (!this.customerLedgerSearchKey) {
            this.getCustomersLedger(this.customerId, this.custLedgerItemPerPage);
        } else {
            this.searchCustomerLedger();
        }
    }

    // Helper methods for feedback functionality (kept for compatibility)
    getStars(rating: number, maxStars: number): number[] {
        return Array(maxStars).fill(0).map((x, i) => i);
    }

    getFeedbackColorClass(rating: number): string {
        if (rating >= 4) return 'feedback-excellent';
        if (rating >= 3) return 'feedback-good';
        if (rating >= 2) return 'feedback-neutral';
        if (rating >= 1) return 'feedback-poor';
        return 'feedback-none';
    }
}
