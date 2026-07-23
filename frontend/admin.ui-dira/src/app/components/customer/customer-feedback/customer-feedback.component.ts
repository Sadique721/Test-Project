import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";


import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
@Component({
    selector: "app-customer-feedback",
    templateUrl: "./customer-feedback.component.html",
    styleUrls: ["./customer-feedback.component.css"],
    standalone: false
})
export class CustomerFeedbackComponent implements OnInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    custType: any;

    displayedColumns: string[] = ['event', 'rating', 'action'];
    dataSource: MatTableDataSource<any> = new MatTableDataSource();

    dialogRef: any;
    // Pagination properties
    feedbackTotalRecords: number = 0;
    itemsPerPage: number = 5;
    pageSizeOptions: number[] = [5,10,20,50,100];
    currentPage: number = 0;

    ifcustCaf: boolean = false;
    customerId: any;
    custCurrentPlanList: any;
    serviceStartPuase: boolean = false;
    custData: any;
    badgeTypeForStatus: any;
    displayStatus: any;
    ifselecResonType: any;
    displayDeleteReason: boolean = false;
    deactiveDataList: any;
    selectDeactivateReason: string = "";
    serviceStropRemarks: string = "";
    servicePerticularData: any;
    serviceStopBulkFlag: boolean = false;
    serviceStopId = [];
    planForConnection: any;
    showPlanConnectionNo: boolean;
    feedbackListData: any;
    viewFeedbackModel: boolean = false;
    feedbackViewData: any;

    constructor(
        private messageService: MessageService,
        private dialog: MatDialog,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private route: ActivatedRoute,
        private router: Router,
        public customerService: CustomermanagementService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    ngOnInit() {
        this.getFeedbackList();
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    getFeedbackList() {
        // const url = "/customerfeedback/getFeedBackDetails?custid=" + this.customerId;
        const url = `/customerfeedback/findByCustomerId?customerId=${this.customerId}`;
        this.customerService.getMethod(url).subscribe(
            (response: any) => {
                // this.feedbackListData = response.feedBackDetails;
                // this.feedbackListData = [response.customerFeedback];
                // this.initializeDataSource(this.feedbackListData);
                const feedback = response.customerFeedback;

                // MUST wrap object in array for table
                const feedbackArray = [feedback];

                // Set dataSource properly
                this.dataSource = new MatTableDataSource(feedbackArray);

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                console.log(error, "error");

            }
        );
    }

    initializeDataSource(data: any[]) {
        this.dataSource = new MatTableDataSource(data);
        this.feedbackTotalRecords = data ? data.length : 0;

        setTimeout(() => {
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    onPageChange(event: PageEvent) {
        this.currentPage = event.pageIndex;
        this.itemsPerPage = event.pageSize;
        this.getFeedbackList();
    }

    getStars(rating: number, max: number): number[] {
        return Array.from({ length: max }, (_, i) => i);
    }

    getFeedbackColorClass(rating: number | null): string {
        if (rating === 5) return "feedback-excellent";
        if (rating === 4) return "feedback-good";
        if (rating === 3) return "feedback-neutral";
        if (rating === 2) return "feedback-poor";
        if (rating === 1) return "feedback-bad";
        return "feedback-none";
    }

    showknowledgetDocData(data) {
        this.feedbackViewData = data;
        this.viewFeedbackModel = true;
        this.openCustomerFeedbackDialog(data);
    }

    closeModal() {
        this.viewFeedbackModel = false;
    }

    applyFilter(event: Event) {
        const filterValue = (event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();

        if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
        }
    }

    @ViewChild("customerfeedback") customerfeedback!: TemplateRef<any>;

    openCustomerFeedbackDialog(branchManagement: any) {
        this.dialogRef = this.dialog.open(this.customerfeedback, {
            width: "900px",
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(result => {
        });
    }
}
