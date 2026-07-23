import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ServiceManagement } from '../../model/service-management';
import { CountryManagementService } from 'src/app/service/country-management.service';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { ProductCategoryManagementService } from 'src/app/service/product-category-management.service';
import { CommondropdownService } from 'src/app/service/commondropdown.service';
import { StatusCheckService } from 'src/app/service/status-check-service.service';
import { AddServiceParameterDialogComponent } from '../add-service-parameter-dialog/add-service-parameter-dialog.component';
import { ServiceManagementService } from 'src/app/service/service-management.service';
import { MessageService } from 'primeng/api';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-add-edit-service-dialog',
    templateUrl: './add-edit-service-dialog.component.html',
    styleUrl: './add-edit-service-dialog.component.css',
    standalone: false
})
export class AddEditServiceDialogComponent implements OnInit {
    @Output() serviceList = new EventEmitter<void>();
    @Input() dataFromParent: any;
    serviceGroupForm: FormGroup;
    submitted: boolean = false;
    expiryFlag: boolean = false;
    createServiceData: ServiceManagement;
    finalServiceParamList: any = [];
    parameterOptions: any = [];
    addServiceParamForm: UntypedFormGroup;
    serviceParamArray: UntypedFormArray;
    serviceSelectExpire: UntypedFormGroup;
    isServiceEdit: boolean = false;
    isEditService: boolean = false;
    ICListdata: any = [];
    Data: any;
    createAccess: boolean = false;
    editAccess: boolean = false
    eventExpireData: any = [];
    isDTV = [
        { label: "TRUE", value: true },
        { label: "FALSE", value: false }
    ];
    selectExpireType = [
        { label: "Midnight", value: "at_midnight" },
        { label: "Actual time", value: "actual_time" }
    ];
    isreadonlyfield: boolean = false;
    constructor(
        private fb: UntypedFormBuilder,
        public productCategoryManagementService: ProductCategoryManagementService,
        private countrymanagemntservice: CountryManagementService,
        public commondropdownService: CommondropdownService,
        public statusCheckService: StatusCheckService,
        private dialog: MatDialog,
        public dialogRef: MatDialogRef<AddEditServiceDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private serviceManagementService: ServiceManagementService,
        private messageService: MessageService,
        private toastr: ToastrService,
    ) { }
    ngOnInit(): void {
        this.serviceGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            displayName: [""],
            icname: [""],
            iccode: [""],
            investmentid: [""],
            installation: [false],
            feasibility: [false],
            poc: [false],
            isServiceThroughLead: [""],
            isPriceEditable: [false],
            provisioning: [false],
            ledgerId: [""],
            expiry: [""],
            pcategoryId: [],
            serviceParamMappingList: [[]],
            is_dtv: [false]

        });
        this.getSelIcName("");
        if (this.data.isEdit === true) {
            this.isServiceEdit = true;
            this.editAccess = this.data.editAcs;
            this.serviceGroupForm.patchValue(this.data.serviceData);
            this.serviceGroupForm.get('provisioning')?.disable();
            this.serviceGroupForm.get('isPriceEditable')?.disable();
            this.serviceGroupForm.get('poc')?.disable();
            this.serviceGroupForm.get('installation')?.disable();
            this.serviceGroupForm.get('feasibility')?.disable();
            if (this.data.serviceData.icname !== null && this.data.serviceData.icname !== "") {
                this.ICListdata = [{ icname: this.data.serviceData.icname }];
            } else {
                this.getSelIcName("");
            }
        } else {
            this.serviceGroupForm.patchValue({
                expiry: 'at_midnight'
            })

        }




    }

    addEditService(serviceId) {
        this.submitted = true;
        this.expiryFlag = false;
        if (this.serviceGroupForm.valid) {
            if (serviceId) {
                const url = "/planservice/" + serviceId;
                this.createServiceData = this.serviceGroupForm.value;
                this.createServiceData["serviceParamMappingList"] = this.finalServiceParamList;
                this.serviceManagementService.updateMethod(url, this.createServiceData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.messageService.add({
                                severity: "error",
                                summary: "Error",
                                detail: response.responseMessage,
                                icon: "far fa-times-circle"
                            });
                            this.dialogRef.close(null);

                        } else {
                            this.reserServiceGroupForm();
                            this.messageService.add({
                                severity: "success",
                                summary: "Successfully Updated",
                                detail: response.msg,
                                icon: "far fa-check-circle"
                            });
                            this.dialogRef.close(null);
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        if (error.error.status == 417 || error.error.status == 406) {
                            this.messageService.add({
                                severity: "info",
                                summary: "Info",
                                detail: error.error.ERROR,
                                icon: "far fa-times-circle"
                            });
                            this.dialogRef.close(null);
                        } else {
                            this.messageService.add({
                                severity: "error",
                                summary: "Error",
                                detail: error.error.ERROR,
                                icon: "far fa-times-circle"
                            });
                            this.dialogRef.close(null);
                        }
                    }
                );
            } else {
                const url = "/planservice";
                this.createServiceData = this.serviceGroupForm.value;
                this.createServiceData["serviceParamMappingList"] = this.finalServiceParamList;
                this.serviceManagementService.postMethod(url, this.createServiceData).subscribe(
                    (response: any) => {
                        this.toastr.success(
                            response.msg, "success",
                        );
                        this.serviceList.emit()
                        this.dialogRef.close()
                        this.reserServiceGroupForm();


                    },
                    (error: any) => {
                        if (error.error.status == 406) {
                            // this.messageService.add({
                            //     severity: "info",
                            //     summary: "Info",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });

                            this.toastr.info(
                                error.ERROR || "Something happened",
                                "Info"
                            );
                            this.dialogRef.close();
                        } else {
                            this.messageService.add({
                                severity: "error",
                                summary: "Error",
                                detail: error.error.ERROR,
                                icon: "far fa-times-circle"
                            });
                            this.dialogRef.close();
                        }
                    }
                );
            }
        }
    }

    onSubmit() {
        this.submitted = true;
        if (this.serviceGroupForm.valid) {
            this.dialogRef.close(this.serviceGroupForm.value);
        } else {
            this.serviceGroupForm.markAllAsTouched();
        }
    }
    closeModal() {
        this.dialogRef.close(); // Simply closes the dialog without passing data
    }
    onchangeEventForDTV(value: any) {
        if (value) {
            this.serviceGroupForm.controls.expiry.setValue("at_midnight");
            this.expiryFlag = true;
            this.isreadonlyfield = true;
            //this.serviceGroupForm.controls.expiry.disable();
        } else {
            this.serviceGroupForm.controls.expiry.setValue("");
            this.expiryFlag = false;
            this.isreadonlyfield = false;
            //this.serviceGroupForm.controls.expiry.enable();
        }
    }
    getExpireTypeType(e) {
        if (e.value == "at_midnight") {
            this.eventExpireData = [];
            let midNighturl = `/commonList/at_midnight`;
            this.commondropdownService.getMethodWithCache(midNighturl).subscribe((response: any) => {
                this.eventExpireData = response.dataList;
            });
        } else if (e.value == "actual_time") {
            this.eventExpireData = [];
            let actualTime = `/commonList/actual_time`;
            this.commondropdownService.getMethodWithCache(actualTime).subscribe((response: any) => {
                this.eventExpireData = response.dataList;
            });
        }
    }
    addServiceParameter() {
        if (this.editAccess) {
            const dialogRef = this.dialog.open(AddServiceParameterDialogComponent, {
                width: '80%',
                data: {
                    ServiceParameterData: this.data.serviceData,
                    editAccess: true
                }
            });

            dialogRef.afterClosed().subscribe(finalServiceParamList => {
                if (finalServiceParamList) {
                    this.finalServiceParamList = finalServiceParamList;
                    this.serviceGroupForm.patchValue({
                        serviceParamMappingList: finalServiceParamList
                    });
                }
            });
        } else {
            const dialogRef = this.dialog.open(AddServiceParameterDialogComponent, {
                width: '80%',
                data: {
                    existingParams: this.finalServiceParamList
                }
            });
            dialogRef.afterClosed().subscribe(finalServiceParamList => {
                if (finalServiceParamList) {
                    this.finalServiceParamList = finalServiceParamList;
                    this.serviceGroupForm.patchValue({
                        serviceParamMappingList: finalServiceParamList
                    });
                }
            });
        }
    }
    getSelIcName(event) {
        const elist = event.value;
        let icData = this.ICListdata.find(item => item.icname == elist);
        if (icData) {
            this.Data = icData.iccode;
            this.serviceGroupForm.controls.investmentid.setValue(icData.id);
            this.serviceGroupForm.controls.iccode.patchValue(this.Data);
        }
        const url = "/investmentCode/getIcNames/";
        this.countrymanagemntservice.getMethod(url).subscribe((response: any) => {
            this.ICListdata = response;
        });
    }
    reserServiceGroupForm() {
        this.submitted = false;
        this.serviceGroupForm.reset();
        this.serviceGroupForm.markAsPristine();
        this.serviceGroupForm.markAsUntouched();
        this.serviceGroupForm.updateValueAndValidity();
        this.parameterOptions = [];
        this.addServiceParamForm.reset();
        this.finalServiceParamList = [];
        Object.keys(this.serviceGroupForm.controls).forEach(key => {
            this.serviceGroupForm.get(key)?.setErrors(null);
        });

        this.serviceParamArray.clear();
        this.serviceSelectExpire.reset();
        this.serviceGroupForm.controls.is_dtv.setValue(false);
        this.isServiceEdit = false;
        this.isEditService = false;
        // this.getServiceDataList("");
        this.getSelIcName("");
    }
    onCancel(): void {
        this.dialogRef.close(null);

    }


}
