import { Component, EventEmitter, Input, OnInit, Output, AfterViewInit } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { UntypedFormBuilder, FormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
declare var $: any;
@Component({
    selector: "app-cust-dbr-report",
    templateUrl: "./cust-dbr-report.component.html",
    styleUrls: ["./cust-dbr-report.component.css"],
    standalone: false
})
export class CustDBRReportComponent implements OnInit, AfterViewInit {
    displayedColumns: string[] = [
        'date',
        'custTypeRevenue',
        'revenue',
        'cumulativeRevenue',
        'remark'
    ];
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    dataSource = new MatTableDataSource<any>();
    custData: any = {};
    customerId = 0;
    custType: string = "";
    searchDBRFormDate: any = "";
    searchDBREndDate: any = "";
    dbrListData: any = [];
    multiServiceData: any = [];
    outStandingData: any;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    currentPageDBRListdata = 1;
    DBRListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    showItemDBRPerPage = 0;
    DBRListdatatotalRecords: any;

    constructor(
        private toastr: ToastrService,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;

        const now = new Date();
        this.searchDBRFormDate = this.datePipe.transform(now, "yyyy-MM-dd");
        this.searchDBREndDate = this.datePipe.transform(
            new Date(now.setDate(now.getDate() + 30)),
            "yyyy-MM-dd"
        );
        if (history.state.data) this.custData = history.state.data;
        else this.getCustomersDetail(this.customerId);
        this.searchDBR();
    }
    ngAfterViewInit(): void {
        setTimeout(() => {
            this.currentPageDBRListdata = 0;  // start at first page
            this.searchDBR();                 // fetch data for first page automatically
            if (this.paginator) {
                this.paginator.pageIndex = this.currentPageDBRListdata; // keep paginator UI in sync
            }
        }, 0);
    }
    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
        });
    }
    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    searchDBR() {
        let page_list;
        let size = this.showItemDBRPerPage;
        if (size != 0) {
            page_list = size;
            this.DBRListdataitemsPerPage = size;
        } else {
            if (this.showItemDBRPerPage == 0) {
                this.DBRListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
            } else {
                this.DBRListdataitemsPerPage = this.showItemDBRPerPage;
            }
        }

        // this.currentPageDBRListdata = 1;
        let firstDay = this.datePipe.transform(this.searchDBRFormDate, 'yyyy-MM-dd');
        let lastDay = this.datePipe.transform(this.searchDBREndDate, 'yyyy-MM-dd');
        // const url =
        //     "/getCustomer?custid=" + this.customerId + "&startdate=" + firstDay + "&endate=" + lastDay;
        const url =
            `/getCustomer?custid=${this.customerId}&startdate=${firstDay}&endate=${lastDay}&page=${this.currentPageDBRListdata + 1}&size=${this.DBRListdataitemsPerPage}`;

        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.dataSource.data = response.customerDBRPojos;
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;
                this.DBRListdatatotalRecords = response.customerDBRPojos.length;
                //this.searchDBRFormDate = ''
                // this.searchDBREndDate = ''
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
                if (this.sort) {
                    this.dataSource.sort = this.sort;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    searchClearDBR() {
        this.searchDBRFormDate = "";
        this.searchDBREndDate = "";

        const now = new Date();
        this.searchDBRFormDate = this.datePipe.transform(now, "yyyy-MM-dd");
        this.searchDBREndDate = this.datePipe.transform(
            new Date(now.setDate(now.getDate() + 30)),
            "yyyy-MM-dd"
        );

        // Reset paginator page index and current page variable
        this.currentPageDBRListdata = 0;
        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }

        this.searchDBR();
    }

    pageChangedDbrList(event: PageEvent) {
        this.currentPageDBRListdata = event.pageIndex + 1;
        this.DBRListdataitemsPerPage = event.pageSize;
        this.searchDBR(); // Re-fetch data with new pagination
    }

    multiService(date: any) {
        let parts_of_date = date.split("-");

        let output =
            parts_of_date[2] +
            "-" +
            (parts_of_date[1].length <= 1 ? "0" + parts_of_date[1] : parts_of_date[1]) +
            "-" +
            (parts_of_date[0].length <= 1 ? "0" + parts_of_date[0] : parts_of_date[0]);

        let url = `/getDbrByCustomerIdAndDate?custid=${this.customerId}&startdate=${output}`;
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.multiServiceData = response;
                $("#multiService").modal("show");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    TotalItemDBRPerPage(event) {
        this.showItemDBRPerPage = Number(event.value);
        if (this.currentPageDBRListdata > 1) {
            this.currentPageDBRListdata = 1;
        }
        this.searchDBR();
    }
}
