import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { UntypedFormBuilder, FormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { ToastrService } from 'ngx-toastr';

declare var $: any;
@Component({
    selector: "app-cust-workflow-audit",
    templateUrl: "./cust-workflow-audit.component.html",
    styleUrls: ["./cust-workflow-audit.component.css"],
    standalone: false
})
export class CustWorkflowAuditComponent implements OnInit {
    custData: any = {};
    customerId = 0;
    custType: string = "";

    customerStatusDetail: any;
    workflowAuditData: any = [];

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: String;
    showItemPerPage = 5;

    constructor(
        private toastr: ToastrService,

        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    ngOnInit(): void {
        if (history.state.data) this.custData = history.state.data;
        else this.getCustomersDetail(this.customerId);
        this.getCustomerTeamHierarchy(this.customerId);
        this.getworkflowAuditDetails("", "CAF");
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

    getCustomerTeamHierarchy(custId) {
        const url = `/teamHierarchy/getApprovalProgress?entityId=${custId}&eventName=CAF`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerStatusDetail = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    getworkflowAuditDetails(size, name) {
        let page = this.currentPageMasterSlab;
        let page_list;
       if (size) {
        this.MasteritemsPerPage = size;
    } else {
        this.MasteritemsPerPage = this.showItemPerPage > 0 ? this.showItemPerPage : 5;
    }

    this.workflowAuditData = [];
    let data = {
        page: this.currentPageMasterSlab, 
        pageSize: this.MasteritemsPerPage,
    };

    let url = "/workflowaudit/list?entityId=" + this.customerId + "&eventName=" + name;

    this.customerManagementService.postMethod(url, data).subscribe(
        (response: any) => {
            this.workflowAuditData = response.dataList;
            this.MastertotalRecords = response.totalRecords;
        },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
                console.log(error, "error");
            }
        );
    }
pageChangedMasterList(event) {
    this.currentPageMasterSlab = event.pageIndex + 1;
    this.showItemPerPage = Number(event.pageSize);
    this.MasteritemsPerPage = this.showItemPerPage; 
    this.getworkflowAuditDetails(this.showItemPerPage, "CAF");
}

    TotalItemPerPageWorkFlow(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMasterSlab > 1) {
            this.currentPageMasterSlab = 1;
        }
        this.getworkflowAuditDetails(this.showItemPerPage, "CAF");
    }
}
