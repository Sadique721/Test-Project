import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerService } from "src/app/service/customer.service";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { MatDialog } from "@angular/material/dialog";
import { MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { MatTableDataSource } from '@angular/material/table';

declare var $: any;
@Component({
    selector: "app-cust-mac-management",
    templateUrl: "./cust-mac-management.component.html",
    styleUrls: ["./cust-mac-management.component.css"],
    standalone: false
})
export class CustmacManagementComponent implements OnInit {
    macMappingDataSource = new MatTableDataSource<any>();
    dialogRef!: MatDialogRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    @ViewChild('MacCreate') MacCreate!: TemplateRef<any>;
    displayedColumns: string[] = ['macAddress', 'service', 'actions'];
    displayedColumns1: string[] = ['macAddress', 'service'];
    custData: any = {};
    customerId = 0;
    custType: string = "";
    editmode: boolean = false;
    macSubmitted: boolean = false;
    displaymode: boolean = true;
    macManagementGroup: UntypedFormGroup;
    macMapppingListFromArray: UntypedFormArray;
    notificationusername: string;
    macData: any = [""];
    custId: any = [""];
    service: any[] = [];
    custPlanMapppingId: any = [""];
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    dropdownOptions: any[] = [];
    macListData: any = [];
    createMac: boolean = false;
    changeStatus: string;
    editingRecord: any = {};
    editingIndex: number | null = null;
    currentEditRecord: any;

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePmace: DatePipe,
        private spinner: NgxSpinnerService,
        private customerService: CustomerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router,
        loginService: LoginService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    ngOnInit(): void {
        this.macManagementGroup = this.fb.group({
            macAddress: ["", [Validators.required]],
            custid: ["", [Validators.required]],
            custsermappingid: [""],
            service: [""],
        });
        this.macMapppingListFromArray = this.fb.array([]);
        this.getService();
        this.getAllMac();
    }

    macListFormGroup(): UntypedFormGroup {
        const selectedService = this.dropdownOptions.find(
            option => option.value === this.macManagementGroup.value.custid
        );

        return this.fb.group({
            macAddress: [this.macManagementGroup.value.macAddress],
            custsermappingid: [this.macManagementGroup.value.custid],
            service: [selectedService.label],
            customer: {
                id: this.customerId,
            },
            isDeleted: false,
        });
    }

    onAddmacList() {
        this.macSubmitted = true;
        // this.macManagementGroup.markAllAsTouched();
        if (this.macManagementGroup.valid) {
            const formGroup = this.macListFormGroup();
            formGroup.addControl("isDeleted", new UntypedFormControl(false));
            this.macMapppingListFromArray.push(this.macListFormGroup());
            this.macMappingDataSource.data = this.macMapppingListFromArray.controls;
            this.macManagementGroup.reset();
            this.macSubmitted = false;
        }
        else {
            this.macManagementGroup.markAllAsTouched();
        }
    }
    // onAddmacList() {
    //     this.macSubmitted = true;
    //     this.markControlsTouched(this.macManagementGroup);

    //     if (this.macManagementGroup.valid) {
    //         const selectedService = this.dropdownOptions.find(
    //             option => option.value === this.macManagementGroup.value.custid
    //         );

    //         const newFormGroup = this.fb.group({
    //             macAddress: [this.macManagementGroup.value.macAddress, Validators.required],
    //             custsermappingid: [this.macManagementGroup.value.custid],
    //             service: [selectedService ? selectedService.label : ''],
    //             customer: this.fb.group({ id: this.customerId }),
    //             isDeleted: [false]
    //         });

    //         this.macMapppingListFromArray.push(newFormGroup);  // Add new form group once

    //         this.macManagementGroup.reset();  // Reset inputs for next entry
    //         this.macSubmitted = false;
    //     }
    // }

    addMac() {
        this.dialogRef = this.dialog.open(this.MacCreate);
        this.onAddmacList();
        this.createMac = true;
        this.macSubmitted = false;
    }

    closeaddMac() {
        this.dialog.closeAll();
        this.createMac = false;
        this.macMapppingListFromArray = this.fb.array([]);
        this.macManagementGroup.reset();
        this.macMappingDataSource.data = [];
    }
    flattenFormArray(formArray: UntypedFormArray): any[] {
        return formArray.controls.map((group: UntypedFormGroup) => {
            const formData = {};
            Object.keys(group.controls).forEach(key => {
                formData[key] = group.controls[key].value;
            });
            return formData;
        });
    }
    saveMac() {
        const url = "/customerMacManagement/save";
        const formArrayData = this.flattenFormArray(this.macMapppingListFromArray);
        this.customerService.saveMacs(url, formArrayData).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.macMapppingListFromArray = this.fb.array([]);
                } else {
                    this.createMac = false;
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.macMapppingListFromArray = this.fb.array([]);
                    this.macMappingDataSource.data = [];
                    this.createMac = false;
                    this.getAllMac();
                    this.dialogRef.close();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    editMacById(record, index: number) {
        this.editmode = true;
        this.displaymode = false;
        this.editingIndex = index;
        this.currentEditRecord = record;
        this.editingRecord = { ...this.macListData[index] };
    }

    saveChanges() {
        if (this.editingRecord) {
            const updatedRecords: { customer: any; macAddress: any; custsermappingid: any; id: any } = {
                id: this.editingRecord.id,
                macAddress: this.editingRecord.macAddress,
                custsermappingid: this.editingRecord.custsermappingid,
                customer: {
                    id: this.customerId,
                },
            };

            const url = "/customerMacManagement/update";
            this.customerService.updateMacs(url, updatedRecords).subscribe(
                (response: any) => {
                    if (response.responseCode == 417) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');
                    } else {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                        this.getAllMac();
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
            this.displaymode = true;
            this.editingIndex = null;
            this.getAllMac();
        }

        this.editmode = false;
        this.editingRecord = {};
    }
    cancelChanges() {
        this.displaymode = true;
        this.editingRecord = {};
        this.editingIndex = null;
    }

    getAllMac() {
        const url = "/customerMacManagement/findByCustId?custId=" + this.customerId;
        this.customerService.getAllMacs(url).subscribe(
            (response: any) => {
                this.macListData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    deleteConfirm(id) {
        if (id) {
            this.dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this MAC?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            this.dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteMac(id);
                } else {
                    (error: any) => {
                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this MAC?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteMac(id);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected",
            //         });
            //     },
            // });
        }
    }

    deleteMac(id) {
        const url = "/customerMacManagement/delete?custMacMapppingId=" + id;
        this.customerService.deleteMacs(url).subscribe(
            (response: any) => {
                this.toastr.success(`${response?.message || "Deleted successfully"}`, 'Success!');

                this.getAllMac();
                this.dialogRef.close(true);
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
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    onServiceSelected(serviceId: any) {
        const selectedService = this.dropdownOptions.find(option => option.value === serviceId);

        this.macMapppingListFromArray.controls.forEach(control => {
            control.get("service").setValue(selectedService.label);
        });
    }
}
