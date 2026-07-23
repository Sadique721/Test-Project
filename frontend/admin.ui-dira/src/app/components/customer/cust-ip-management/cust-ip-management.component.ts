import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerService } from "src/app/service/customer.service";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-cust-ip-management",
    templateUrl: "./cust-ip-management.component.html",
    styleUrls: ["./cust-ip-management.component.css"],
    standalone: false
})
export class CustipManagementComponent implements OnInit {

    @ViewChild('addIpDialog') addIpDialog!: TemplateRef<any>;

    custData: any = {};
    customerId = 0;
    custType: string = "";
    editmode: boolean = false;
    ipSubmitted: boolean = false;
    displaymode: boolean = true;
    ipManagementGroup: UntypedFormGroup;
    ipMapppingListFromArray: UntypedFormArray;
    notificationusername: string;
    ipData: any = [""];
    custId: any = [""];
    service: any[] = [];
    custPlanMapppingId: any = [""];
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    dropdownOptions: any[] = [];
    ipListData: any = [];
    ipListDataMaster: any = [];
    createIp: boolean = false;
    changeStatus: string;
    editingRecord: any = {};
    editingIndex: number | null = null;
    currentEditRecord: any;
    totalRecord: any;

    constructor(
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private customerService: CustomerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router,
        loginService: LoginService,
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    ngOnInit(): void {
        this.ipManagementGroup = this.fb.group({
            ipAddress: ["", Validators.required],
            ipType: ["", Validators.required],
            custid: [""],
            custsermappingid: [""],
            service: [""],
        });
        this.ipMapppingListFromArray = this.fb.array([]);
        // this.ipMappingDataSource.data = this.ipMapppingListFromArray.controls;
        this.getService();
        this.getAllIp();
    }
    ipListFormGroup(): UntypedFormGroup {
        const selectedService = this.dropdownOptions.find(
            option => option.value === this.ipManagementGroup.value.custid
        );

        return this.fb.group({
            ipAddress: [this.ipManagementGroup.value.ipAddress],
            ipType: [this.ipManagementGroup.value.ipType],
            custsermappingid: [this.ipManagementGroup.value.custid],
            custid: [this.customerId],
            service: [selectedService.label],
        });
    }
    tableDataRefresh: any;
    ipMappingDataSource = new MatTableDataSource<any>();
    onAddIPList() {
        this.ipSubmitted = true;
        if (this.ipManagementGroup.valid) {
            this.ipMapppingListFromArray.push(this.ipListFormGroup());
            this.ipMappingDataSource.data = this.ipMapppingListFromArray.controls;
            this.ipMapppingListFromArray.controls.forEach((group: any) => {
                group.get('ipAddress')?.disable();
                group.get('ipType')?.disable();
                group.get('service')?.disable();
            });
            this.ipManagementGroup.reset();
            this.ipSubmitted = false;
            // this.refreshTableData();
        } else {
            this.ipManagementGroup.markAllAsTouched();
        }
    }


    addIp() {
        // this.onAddIPList();
        this.createIp = true;
        this.ipSubmitted = false;
        this.dialog.open(this.addIpDialog, {
            width: '60%',
            disableClose: true,
        });
    }
    //   closeaddIp() {
    //     this.createIp = false;
    //     this.ipManagementGroup.reset();
    //     this.ipMapppingListFromArray = this.fb.array([]);
    //   }
    flattenFormArray(formArray: UntypedFormArray): any[] {
        return formArray.controls.map((group: UntypedFormGroup) => {
            const formData = {};
            Object.keys(group.controls).forEach(key => {
                formData[key] = group.controls[key].value;
            });
            return formData;
        });
    }
    saveIp() {
        this.createIp = false;
        const url = "/customerIpManagement/save";
        const formArrayData = this.flattenFormArray(this.ipMapppingListFromArray);
        this.customerService.saveIps(url, formArrayData).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.ipMapppingListFromArray = this.fb.array([]);
                    this.createIp = true;
                } else {
                    this.toastr.success(`Successfully Added`, 'Success!');
                    this.dialog.closeAll();
                    this.ipMapppingListFromArray = this.fb.array([]);
                    this.ipMappingDataSource.data = [];
                    this.getAllIp();

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    editIpById(record, index: number) {
        this.editmode = true;
        this.displaymode = false;
        this.editingIndex = index;
        this.currentEditRecord = record;
        this.editingRecord = { ...this.ipListData[index] };
    }

    saveChanges() {
        if (this.editingRecord) {
            const url = "/customerIpManagement/update";

            const payload = this.ipListData.map(record => ({
                custid: record.custid,
                ipAddress: record.ipAddress,
                ipType: record.ipType,
                custsermappingid: record.custsermappingid,
                service: record.service,
            }));
            this.customerService.updateIps(url, payload).subscribe(
                (response: any) => {
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: "IP Address updated successfully",
                    //     icon: "far fa-check-circle",
                    // });
                    this.toastr.success(`Succesfully Updated`, 'Success!');
                    this.getAllIp();
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                }
            );
            this.displaymode = true;
            this.editingIndex = null;
        }

        this.editmode = false;
        this.editingRecord = {};
    }
    cancelChanges() {
        this.displaymode = true;
        this.editingRecord = {};
        this.editingIndex = null;
        this.ipListData = this.ipListDataMaster.map(obj => JSON.parse(JSON.stringify(obj)));
    }
    dataSource = new MatTableDataSource<any>([]);
    getAllIp() {
        const url = "/customerIpManagement/getIpsByCustId?custId=" + this.customerId;
        this.customerService.getAllIps(url).subscribe(
            (response: any) => {
                this.ipListData = response.customerIps;
                this.ipListTotalRecords = response.customerIps.length;
                this.ipListDataMaster = this.ipListData.map(obj => JSON.parse(JSON.stringify(obj)));
                this.dataSource = new MatTableDataSource<any>(this.ipListData);

            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    // deleteConfirm(id) {
    //     this.confirmationService.confirm({
    //         message: "Do you want to delete this IP?",
    //         header: "Delete Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //             this.deleteIp(id);
    //         },
    //         reject: () => {
    //             this.messageService.add({
    //                 severity: "info",
    //                 summary: "Rejected",
    //                 detail: "You have rejected",
    //             });
    //         },
    //     });
    // }
    deleteConfirm(data, id: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Are you sure you want to delete "${data.ipType}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteIp(id);
            } else {
            }
        });
    }


    deleteIp(id) {
        const url = "/customerIpManagement/delete?id=" + id;
        this.customerService.deleteIps(url).subscribe(
            (response: any) => {
                this.toastr.success(`Successfully Deleted`, 'Success!');
                this.getAllIp();
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    getService() {
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerId +
            "?isAllRequired=true&isNotChangePlan=true";
        this.customerService.getMethod(url).subscribe(
            (response: any) => {
                this.custId = response.dataList;
                this.service = response.dataList.map(item => item.service);
                this.custPlanMapppingId = response.dataList[0].custPlanMapppingId;
                this.dropdownOptions = response.dataList.map(item => ({
                    label: item.service,
                    value: item.customerServiceMappingId,
                }));
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    ipTypeChange(event, selectedOption) {
        let selected = selectedOption.selectedOption.value;
        if (selected == "Ipv6") {
            this.ipManagementGroup
                .get("ipAddress")
                .setValidators([
                    Validators.pattern(
                        "^((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4}))*::((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4}))*|((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4})){7}$"
                    ),
                ]);
        } else {
            this.ipManagementGroup
                .get("ipAddress")
                .setValidators([Validators.pattern("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")]);
        }
    }
    closeaddIp(): void {
        this.dialog.closeAll();
        this.ipManagementGroup.reset();
        // this.ipMapppingListFromArray = this.fb.array([]);
        this.ipMappingDataSource.data = [];
    }
    ipListTotalRecords = 0;
    ipListItemsPerPage = 10;
    pageChangedIpList(event: any) {
        // this.currentPageProductListdata = event.pageIndex + 1;
        this.ipListItemsPerPage = event.pageSize;

    }
}
