import { Component, OnInit, ViewChild, TemplateRef } from "@angular/core";
import { DatePipe } from "@angular/common";
import { MessageService } from "primeng/api";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ActivatedRoute, Router } from "@angular/router";
import { TaskManagementService } from "src/app/service/task-management.service";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
@Component({
    selector: "app-cust-task-audit",
    templateUrl: "./cust-task-audit.component.html",
    styleUrls: ["./cust-task-audit.component.css"],
    standalone: false
})
export class CustTaskAuditComponent implements OnInit {
    customerId = 0;
    custType: string = "";
    pageNumberForTaskAuditPage = 1;
    pageSizeForTaskAuditPage = RadiusConstants.ITEMS_PER_PAGE;
    taskAuditList: any = [];
    taskAuditTotalRecords = 0;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    custData: any = {};
    taskDetailsData: any = {};
    isTaskDetail: boolean = false;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('detailDialog') detailDialog: TemplateRef<any>;
    dialogRef: MatDialogRef<any>;
    displayedColumns: string[] = [
        'caseNumber',
        'caseTitle',
        'assigneeName',
        'caseStatus',
        'priority',
        'startDate',
        'endDate',
        'viewTaskHistory'
    ];
    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();

    constructor(
        private messageService: MessageService,
        public datePipe: DatePipe,
        private taskManagementService: TaskManagementService,
        private customerManagementService: CustomermanagementService,
        private route: ActivatedRoute,
        private toastr: ToastrService,
        private router: Router,
        private dialog: MatDialog
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    ngOnInit(): void {
        this.getCustomersDetail(this.customerId);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            this.getChildCustomers();
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    getChildCustomers() {
        const url = `/case/casehistory?customerId=${this.customerId}`;
        const data = {
            page: this.pageNumberForTaskAuditPage,
            pageSize: this.pageSizeForTaskAuditPage
        };
        this.taskManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.dataSource.data = response.dataList;
                this.taskAuditTotalRecords = response.totalRecords;
                if (this.paginator) {
                    this.paginator.pageIndex = this.pageNumberForTaskAuditPage - 1;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.msg,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    pageChangeEventForChildCustomers(event: PageEvent) {
        this.pageNumberForTaskAuditPage = event.pageIndex + 1;
        this.pageSizeForTaskAuditPage = event.pageSize;
        this.getChildCustomers();
    }

    itemPerPageChangeEvent(event) {
        this.pageSizeForTaskAuditPage = Number(event.value);
        this.pageNumberForTaskAuditPage = 1;
        this.getChildCustomers();
    }

    getTaskDetails(ticketId) {
        const url = "/case/" + ticketId;
        this.taskManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.taskDetailsData = response.data;
                this.dialogRef = this.dialog.open(this.detailDialog, {
                    width: '600px',
                    data: { taskDetails: this.taskDetailsData }
                });
                this.dialogRef.afterClosed().subscribe(() => {
                    this.taskDetailsData = {};
                });
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

    closeTaskDetail() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }
}
