import { Component, EventEmitter, Input, OnInit, Output, ViewChild, TemplateRef } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { UntypedFormBuilder, FormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-cust-audit-details",
    templateUrl: "./cust-audit-details.component.html",
    styleUrls: ["./cust-audit-details.component.css"],
    standalone: false
})
export class CustAuditDetailsComponent implements OnInit {
    custData: any = {};
    customerId: number;
    custType: string = "";
    dataSource = new MatTableDataSource<any>([]);
    displayedColumns: string[] = ['auditDate', 'employeeName', 'module', 'operation', 'remark'];
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    auditData: any = [];
    currentPageAuditSlab1 = 1;
    AudititemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    AudittotalRecords1: 0;
    auditList: any = [];
    sortOrder = 0;
    showItemPerPage = 1;
    searchOption: string = "";
    searchData: any;
    searchInput: string = "";
    fromDate = "";
    toDate = "";
    searchOptions = [
        { label: "Employee Name", value: "employeename" },
        { label: "Username", value: "username" },
        { label: "Module", value: "module" },
        { label: "Operation", value: "operation" }

    ];
    remarkDialogVisible: boolean = false;
    selectedRemark: string = '';

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild('remarkDialog') remarkDialog!: TemplateRef<any>;

    constructor(
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router,
        private dialog: MatDialog,
        private toastr: ToastrService
    ) { }

    ngOnInit(): void {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;

        if (history.state.data) this.custData = history.state.data;
        else this.getCustomersDetail(this.customerId);
        this.getAuditData("");

        this.searchData = {
            filters: [
                {
                    filterColumn: "any",
                    filterValue: ""
                }
            ],
            page: "",
            pageSize: "",
            sortOrder: "",
            fromDate: "",
            toDate: "",
            sortBy: "id",
        };
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
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

    getAuditData(size) {
        let page = this.currentPageAuditSlab1;
        let page_list;
        if (size) {
            page_list = size;
            this.AudititemsPerPage1 = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.AudititemsPerPage1 = 5;
            } else {
                this.AudititemsPerPage1 = 5;
            }
        }
        this.auditData = [];

        let data = {
            page: page,
            pageSize: this.AudititemsPerPage1,
            sortBy: "id",
            sortOrder: 0,
        };
        const url = "/auditLog/getAuditList/" + this.customerId;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.auditData = response.dataList;
                this.AudittotalRecords1 = response.totalRecords;
                this.dataSource.data = this.auditData;
                setTimeout(() => {
                    if (this.paginator) {
                        this.paginator.length = this.AudittotalRecords1;
                        this.paginator.pageIndex = this.currentPageAuditSlab1 - 1;
                        this.paginator.pageSize = this.AudititemsPerPage1;
                    }
                });
                //this.auditList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onCancel() {
        this.dialog.closeAll();
    }

    pageChangedAuditList(event: PageEvent) {
        this.currentPageAuditSlab1 = event.pageIndex + 1;
        this.AudititemsPerPage1 = event.pageSize;

        if (this.searchOption || this.searchInput || this.fromDate || this.toDate) {
            this.searchAudit();
        } else {
            this.getAuditData(this.AudititemsPerPage1);
        }
    }

    TotalItemPerPageAudit(event: any) {
        this.AudititemsPerPage1 = Number(event.value);
        this.currentPageAuditSlab1 = 1;
        if (this.searchOption || this.searchInput || this.fromDate || this.toDate) {
            this.searchAudit();
        } else {
            this.getAuditData(this.AudititemsPerPage1);
        }
    }

    searchAudit() {
        if (this.searchOption && this.searchInput) {
            this.searchData.filters[0].filterColumn = this.searchOption;
            this.searchData.filters[0].filterValue = this.searchInput;
        } else {
            this.searchData.filters[0].filterColumn = "any";
            this.searchData.filters[0].filterValue = "";
        }

        this.searchData.fromDate = this.datePipe.transform(this.fromDate, "yyyy-MM-dd");
        this.searchData.toDate = this.datePipe.transform(this.toDate, "yyyy-MM-dd");
        this.searchData.page = this.currentPageAuditSlab1;
        this.searchData.pageSize = this.AudititemsPerPage1;
        this.searchData.sortBy = "id";
        this.searchData.status = "";

        const url = `/auditLog/getSearchAudit/${this.customerId}`;
        this.customerManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response?.auditListData?.length === 0) {
                    this.auditData = [];
                    this.AudittotalRecords1 = 0;
                } else {
                    this.auditData = response.dataList;
                    this.AudittotalRecords1 = response.totalRecords;
                }
                this.dataSource.data = this.auditData;
                setTimeout(() => {
                    if (this.paginator) {
                        this.paginator.length = this.AudittotalRecords1;
                        this.paginator.pageIndex = this.currentPageAuditSlab1 - 1;
                        this.paginator.pageSize = this.AudititemsPerPage1;
                    }
                });
            },
            (error: any) => {
                this.auditData = [];
                this.AudittotalRecords1 = 0;
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }


    clearAuditSearch() {
        this.searchOption = "";
        this.searchInput = "";
        this.searchData = {
            filters: [
                {
                    filterColumn: "any",
                    filterValue: ""
                }
            ],
            page: "",
            pageSize: "",
            sortOrder: "",
            sortBy: "",
            filterBy: "",
            fromDate: "",
            toDate: "",
            status: ""
        };
        this.currentPageAuditSlab1 = 1;
        this.showItemPerPage = 5;
        this.AudititemsPerPage1 = 5;
        this.getAuditData(this.AudititemsPerPage1);
    }

    openRemarkDialog(remark: string): void {
        this.selectedRemark = remark;
        this.remarkDialogVisible = true;
        this.dialog.open(this.remarkDialog, {
            width: '500px'
        });
    }
}

