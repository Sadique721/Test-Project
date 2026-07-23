import { Component, EventEmitter, Input, OnInit, Output, ViewChild, AfterViewInit, OnDestroy, } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { UntypedFormBuilder, FormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-cust-dunning-management",
    templateUrl: "./cust-dunning-management.component.html",
    styleUrls: ["./cust-dunning-management.component.css"],
    standalone: false
})
export class CustDunningManagementComponent implements OnInit {
    custData: any = {};
    customerId = 0;
    custType: string = "";
    displayedColumns: string[] = [
        "dunningMessageDate",
        "eventName",
        "action",
    ];
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    DunningData: any = [];
    currentPageDunningSlab1 = 1;
    DunningitemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    DunningtotalRecords1 = 0;
    dunningList: any = [];
    dunningData: any;
    dunningStatusAccess: boolean = false;
    changeStatus: string;

    constructor(
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService, private toastr: ToastrService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router,
        loginService: LoginService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.dunningStatusAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_DUNNING_STATUS
                : POST_CUST_CONSTANTS.POST_CUST_DUNNING_STATUS
        );
    }

    ngOnInit(): void {
        if (history.state.data) this.custData = history.state.data;
        else this.getCustomersDetail(this.customerId);
        this.getDunningData("");
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

    getDunningData(size) {
        let page = this.currentPageDunningSlab1;
        let page_list;
        if (size) {
            page_list = size;
            this.DunningitemsPerPage1 = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.DunningitemsPerPage1 = 5;
            } else {
                this.DunningitemsPerPage1 = 5;
            }
        }
        this.DunningData = [];

        let data = {
            filters: [
                {
                    filterColumn: "customer",
                    filterValue: this.customerId,
                },
            ],

            page: page,
            pageSize: this.DunningitemsPerPage1,
        };
        const url = "/dunnninghistory/findByPartnerOrCustomerId";
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.DunningData = response.customerDunningHistory.content;
                this.DunningtotalRecords1 = response.customerDunningHistory.totalElements;
                this.dataSource.data = response.customerDunningHistory.content;
                if (this.paginator) {
                    this.paginator.pageIndex = this.currentPageDunningSlab1 - 1;
                }
                //this.auditList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    async getDunningDisableEnable(id, status) {
        // if (status == true) {
        //     this.changeStatus = "false";
        // }
        // if (status == false) {
        //     this.changeStatus = "true";
        // }
        this.changeStatus = status ? "true" : "false";
        const url =
            "/customer/changedunningenabalestatus?custId=" + id + "&dunningStatus=" + this.changeStatus;
        this.changeStatus = "";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.toastr.success(`${response.msg}`, 'Successful!');
                this.getCustomersDetail(id);
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
        setTimeout(async () => {
            await this.getCustomersDetail(id);
        }, 1000);
    }
    pageChangedMasterDunnningList(event: PageEvent) {
        this.currentPageDunningSlab1 = event.pageIndex + 1;
        this.DunningitemsPerPage1 = event.pageSize;
        this.getDunningData(this.DunningitemsPerPage1);
    }

    TotalItemPerPageDunningHistory(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageDunningSlab1 > 1) {
            this.currentPageDunningSlab1 = 1;
        }
        this.getDunningData(this.showItemPerPage);
    }
}
