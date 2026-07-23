import { Component, OnInit } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { BehaviorSubject } from "rxjs";
import { FormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { DatePipe } from "@angular/common";
import * as FileSaver from "file-saver";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { LoginService } from "src/app/service/login.service";
import { ToastrService } from 'ngx-toastr';
import { ViewChild, TemplateRef } from "@angular/core";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatDialog } from "@angular/material/dialog";
@Component({
    selector: "app-cust-session-history",
    templateUrl: "./cust-session-history.component.html",
    styleUrls: ["./cust-session-history.component.scss"],
    standalone: false
})
export class CustSessionHistoryComponent implements OnInit {
    dataSource = new MatTableDataSource<any>([]);
    displayedColumnsCDR: string[] = [
        'userName',
        'nasIpAddress',
        'framedIpAddress',
        'uploadMB',
        'downloadMB',
        'sessionTime',
        'createDate',
        'lastModificationDate',
    ];
    custType: any;
    // loggedInStaffId = localStorage.getItem("userId");
    // partnerId = Number(localStorage.getItem("partnerId"));
    customerId: number;
    custData: any = {};

    searchAcctCdrForm: UntypedFormGroup;

    groupDataCDR: any[] = [];

    totalCDRRecords: number = 0;
    currentPageCDR = 1;
    itemsPerPageCDR = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPageCDR = 1;
    searchCDRSubmitted: boolean = false;
    exportToExcelAccess: boolean = false;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    constructor(
        private toastr: ToastrService,

        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private route: ActivatedRoute,
        private router: Router,
        private fb: UntypedFormBuilder,
        private messageService: MessageService,
        private datePipe: DatePipe,
        loginService: LoginService,
        private dialog: MatDialog,
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.exportToExcelAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_SESSION_HISTORY_EXPORT
                : POST_CUST_CONSTANTS.POST_CUST_SESSION_HISTORY_EXPORT
        );
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    }

    async ngOnInit() {
        this.getCustomersDetail(this.customerId);
        this.searchAcctCdrForm = this.fb.group({
            userName: [this.custData.custname],
            framedIpAddress: [""],
            fromDate: [""],
            toDate: [""]
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            this.searchGroupByNameCDR("");
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    async searchGroupByNameCDR(list) {
        let size;
        // this.searchkey = "";
        // this.searchkey2 = "";
        const page = this.currentPageCDR;
        if (list) {
            size = list;
            this.itemsPerPageCDR = list;
        } else {
            size = this.itemsPerPageCDR;
        }
        let f = "";
        let t = "";

        if (this.searchAcctCdrForm.value.fromDate) {
            f = this.datePipe.transform(this.searchAcctCdrForm.controls.fromDate.value, "yyyy-MM-dd");
        }
        if (this.searchAcctCdrForm.value.toDate) {
            t = this.datePipe.transform(this.searchAcctCdrForm.controls.toDate.value, "yyyy-MM-dd");
        }

        // this.currentPage = 1;
        this.searchCDRSubmitted = true;
        if (this.searchAcctCdrForm.valid) {
            const userNameForSearch = this.custData.username;
            const framedIpAddress = this.searchAcctCdrForm.value.framedIpAddress
                ? this.searchAcctCdrForm.value.framedIpAddress.trim()
                : "";
            this.groupDataCDR = [];

            this.customerManagementService
                .getAcctCdrDataByUsernameAndcustId(
                    userNameForSearch,
                    framedIpAddress,
                    this.custData.id,
                    f,
                    t,
                    this.currentPageCDR,
                    this.itemsPerPageCDR
                )
                .subscribe(
                    (response: any) => {
                        // this.groupDataCDR = response.acctCdr.content;
                        if (!response.infomsg) {
                            const groupDataCDR = response.acctCdr.content.filter(
                                name => name.userName == this.custData.username
                            );
                            this.groupDataCDR = groupDataCDR;
                            this.totalCDRRecords = response.acctCdr.totalElements;
                            this.dataSource.data = groupDataCDR;
                        } else {
                            this.toastr.info(`${response.infomsg}`, 'Info!');
                            this.dataSource.data = [];

                        }
                    },
                    (error: any) => {
                        this.totalCDRRecords = 0;
                        if (error.error.status == 404) {
                            this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                        } else {

                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    }
                );
        }
    }

    async exportExcel() {
        this.groupDataCDR = [];
        let data = {
            userName: this.custData.username,
            fromDate: this.searchAcctCdrForm.value.fromDate,
            custId: this.custData.id,
            toDate: this.searchAcctCdrForm.value.toDate,
            page: this.currentPageCDR,
            size: this.itemsPerPageCDR
        };
        this.customerManagementService.getAllCDRExportWithCustId(data).subscribe((response: any) => {
            const file = new Blob([response], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            });
            const fileURL = URL.createObjectURL(file);
            FileSaver.saveAs(file, "Sheet");
            // if (response.acctCdrList.length > 0) {
            //   const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.groupDataCDR);
            //   const wb: XLSX.WorkBook = XLSX.utils.book_new();
            //   XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
            //   XLSX.writeFile(wb, this.fileNameCDR);

            // } else {
            //
            //   this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "No record found for export.",
            //     icon: "far fa-times-circle",
            //   });
            // }
        });
    }
    onEnterSearch() {
        this.currentPageCDR = 1; // Reset to page 1 if needed
        this.searchGroupByNameCDR('');
    }

    pageCDRChanged(event: PageEvent): void {
        this.currentPageCDR = event.pageIndex + 1;
        this.itemsPerPageCDR = event.pageSize;
        this.searchGroupByNameCDR(this.itemsPerPageCDR);
    }

    TotalItemPerCDRPage(event: any): void {
        this.itemsPerPageCDR = Number(event.value);
        this.currentPageCDR = 1;
        this.searchGroupByNameCDR(this.itemsPerPageCDR);
    }

    clearSearchCDRForm() {
        this.searchCDRSubmitted = false;
        this.currentPageCDR = 1;
        this.searchAcctCdrForm.reset();
        this.searchGroupByNameCDR("");
    }
}
