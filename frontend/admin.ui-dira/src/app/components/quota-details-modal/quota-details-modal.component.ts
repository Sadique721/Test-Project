import { Component, OnInit, Input, Output, EventEmitter, Inject } from "@angular/core";
import { Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-quota-details-modal",
    templateUrl: "./quota-details-modal.component.html",
    styleUrls: ["./quota-details-modal.component.css"],
    standalone: false
})
export class QuotaDetailsModalComponent implements OnInit {
    @Input() dialogId: string;
    @Input() PlanQuota: Observable<any>;
    @Output() closeDialogg = new EventEmitter();
    custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custQuotaListtotalRecords: String;
    currentPagecustQuotaList = 1;
    custQuotaList: any = [];
    planMappingId: any;
    planData: any;
    custid: any;
    visibleQuotaDetails: boolean = false;
    quotaDisplayedColumns: string[] = [
        'planName', 'quotaType', 'totalQuota', 'usedQuota', 'currentSessionVolume',
        'quotaUnit', 'timeTotalQuota', 'timeQuotaUsed', 'timeQuotaUnit',
        'currentSessionTime', 'parentQuotaType', 'usageQuotaType'
    ];

    constructor(
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private toastr: ToastrService,
        private customerManagementService: CustomermanagementService,
        private paymentamountService: PaymentamountService,
        public dialogRef: MatDialogRef<QuotaDetailsModalComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
    ) {
    }

    // ngOnInit(): void {
    //     this.visibleQuotaDetails = true;
    //     this.PlanQuota.subscribe(value => {
    //         this.custid = value.custid;
    //         this.planData = value.PlanData;
    //         if (this.custid) {
    //             this.getCustQuotaList(this.custid);
    //         }
    //     });
    // }
    ngOnInit(): void {
        if (this.data) {
            this.custid = this.data.custid;
            this.planData = this.data.PlanData;
            if (this.custid) {
                this.getCustQuotaList(this.custid);
            }
        }
    }

    getCustQuotaList(custId) {
        this.customerManagementService.getCustQuotaList(custId).subscribe(
            (response: any) => {
                let data = response.custQuotaList;
                this.custQuotaList = data.filter(e => e.cprId == this.planData.planmapid);
                this.custQuotaListtotalRecords = this.custQuotaList.length;

            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    pageChangedCustQuotaList(pageNumber) {
        this.currentPagecustQuotaList = pageNumber;
    }

    closeModel() {
        this.closeDialogg.emit();
        this.visibleQuotaDetails = false;
    }
    onCancel() {
        this.dialogRef.close();
    }
}
