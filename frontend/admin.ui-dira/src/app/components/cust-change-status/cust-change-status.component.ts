import { Component, Input, Output, OnInit, EventEmitter, Inject, ViewChild, TemplateRef } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { ToastrService } from 'ngx-toastr';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatDialog } from "@angular/material/dialog";


declare var $: any;
export interface DialogData {
    custId: any;
    custStatus: any;
    moduleType: any
}


@Component({
    selector: "app-cust-change-status",
    templateUrl: "./cust-change-status.component.html",
    styleUrls: ["./cust-change-status.component.css"],
    standalone: false
})
export class CustChangeStatusComponent implements OnInit {
    @Output() closeChangeStatusEvent = new EventEmitter();
    @ViewChild("ChangeStatus") ChangeStatus: TemplateRef<any>

    updatedStatus: any;
    remark: any;
    changeStatusModal: boolean = false;
    custId: any;
    custStatus: any;
    moduleType: any;
    datas: any;
    constructor(
        private dialog: MatDialog,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        public dialogRef: MatDialogRef<CustChangeStatusComponent>,
        @Inject(MAT_DIALOG_DATA) public data: DialogData
    ) {
        this.datas = data;
        this.custId = this.datas.custId;
        this.custStatus = this.datas.custStatus;
        this.moduleType = this.datas.moduleType;

    }

    ngOnInit(): void {
        this.commondropdownService.getCustomerStatus();
        this.changeStatusModal = true;
    }

    // ngAfterViewInit() {
    //     this.dialogRef = this.dialog.open(this.ChangeStatus, {
    //         width: '50%',
    //         maxWidth: '90vw',
    //         height: 'auto',
    //         autoFocus: false,
    //         disableClose: true
    //     });
    // }

    async changeStatus(updatedStatus, remark) {
        const data = {
            id: this.custId,
            rf: "bss",
            status: updatedStatus,
            remark: remark,
        };

        if (this.moduleType == "radius") {
            const url = "/updateStatus/" + this.custId + "?remark=" + remark + "&status=" + updatedStatus;
            this.customerManagementService.updateRadiusMethod(url, data).subscribe(
                (response: any) => {
                    this.dialogRef.close(true);

                    this.toastr.success(`${response.message}`, 'success');
                    this.updatedStatus = "";
                    this.closeChangeStatus(true);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    this.dialogRef.close(true);

                }
            );
        } else if (this.moduleType == "netConf") {
            const url =
                "/customer/updateStatus/" + this.custId + "?remark=" + remark + "&status=" + updatedStatus;
            this.customerManagementService.updateNetConf(url, data).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.message}`, 'Success!');

                    // this.getcustomerList("");
                    this.dialogRef.close(true);

                    this.updatedStatus = "";
                    this.closeChangeStatus(true);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    this.dialogRef.close(true);



                }
            );
        } else {
            const url = "/changeStatus/" + this.custId + "?remark=" + remark + "&status=" + updatedStatus;
            this.customerManagementService.updateMethod(url, data).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.customer}`, 'Success!');

                    // this.getcustomerList("");
                    this.updatedStatus = "";
                    // this.dialogRef.close(true);
                    this.dialogRef.close({ refresh: true });

                    this.closeChangeStatus(true);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    this.dialogRef.close(true);

                }
            );
        }
    }

    closeChangeStatus(isStatusChanged) {
        this.dialogRef.close(true);
        this.updatedStatus = "";
        this.remark = "";
        this.closeChangeStatusEvent.emit(isStatusChanged);
        this.changeStatusModal = false;
        this.dialogRef.close(isStatusChanged);
    }
}
