import { Component, Inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { ServiceManagementService } from 'src/app/service/service-management.service';
import { FieldmappingService } from 'src/app/service/fieldmapping.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DeleteConfirmationDialogBoxComponent } from 'src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component';
import { ToastrService } from 'ngx-toastr';
// import { NgStyle } from "../../../../../node_modules/@angular/common/common_module.d";
@Component({
    selector: 'app-add-service-parameter-dialog',
    templateUrl: './add-service-parameter-dialog.component.html',
    styleUrl: './add-service-parameter-dialog.component.css',
    standalone: false
})
export class AddServiceParameterDialogComponent implements OnInit {
    addServiceParamForm!: FormGroup;
    serviceParamArray: FormArray;
    serviceParamData: any[] = []; // For mat-table dataSource
    parameterList: any;
    parameterOptionOriginalList: any;
    isEditService: any;
    submitted: boolean;
    isServiceEdit: boolean;
    defultUnitName: any;
    isMultipleFields: any;
    withEndpoint: any;
    defaultParamValues: any[];
    // dataSource: any;
    finalServiceParamList: any[];
    inputDataSource = [{}]; // Single row for input
    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>([]);
    constructor(
        private fb: FormBuilder,
        public dialogRef: MatDialogRef<AddServiceParameterDialogComponent>,
        private serviceManagementService: ServiceManagementService,
        private tempservice: FieldmappingService,
        private messageService: MessageService,
        private dialog: MatDialog,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private toastr: ToastrService
    ) {
        // this.addServiceParamForm = this.fb.group({
        //     serviceParamId: [''],
        //     isMandatory: [false],
        //     value: [''],
        // });
    }
    // ngOnInit(): void {
    //     this.getServiceParameter();
    //     this.addServiceParamForm = this.fb.group({
    //         serviceParamId: [''],
    //         isMandatory: [false],
    //         value: [''],
    //     });
    //     this.serviceParamArray = this.fb.array([]);
    //     // console.log("this.serviceParamData -> ", this.serviceParamData)
    //     // if (this.data.editAccess) {
    //     //     this.isEditService = true;
    //     //     this.dataSource = this.data.ServiceParameterData.serviceParamMappingList;
    //     //     console.log("object ->", this.data.ServiceParameterData.serviceParamMappingList);
    //     // } else {

    //     //     this.dataSource = new MatTableDataSource<any>(this.serviceParamData);
    //     // }
    //     this.dataSource = new MatTableDataSource<any>(this.serviceParamData);
    // }


    ngOnInit(): void {
        this.getServiceParameter();
        // Restore previously saved state


        this.addServiceParamForm = this.fb.group({
            // serviceParamId: ['',Validators.required],
            serviceParamId: [''],
            isMandatory: [false],
            value: [''],
        });
        this.serviceParamArray = this.fb.array([]);
        if (this.data?.existingParams?.length) {
            this.serviceParamArray.clear();
            this.serviceParamData = [];

            this.data.existingParams.forEach((param: any) => {
                const formGroup = this.fb.group({
                    serviceParamId: [param.serviceParamId],
                    serviceParamName: [param.serviceParamName],
                    isMandatory: [param.isMandatory],
                    value: [param.value]
                });
                this.serviceParamArray.push(formGroup);
                this.serviceParamData.push(param);
            });

            this.dataSource.data = [...this.serviceParamData];
        }
        if (this.data?.editAccess && this.data.ServiceParameterData) {
            this.isEditService = true;
            this.serviceParamData = [...this.data.ServiceParameterData.serviceParamMappingList];
            this.serviceParamData.forEach((el: any) => {
                this.serviceParamArray.push(
                    this.fb.group({
                        serviceParamId: [el.serviceParamId],
                        serviceParamName: [el.serviceParamName],
                        isMandatory: [el.isMandatory],
                        value: [el.value]
                    })
                );
            });

            // Ensure dataSource is updated with the initial data
            this.dataSource.data = [...this.serviceParamData];
        }
    };

    onParamSelect(e) {
        this.defaultParamValues = [];
        this.addServiceParamForm.get("value").setValue("");
        let filterdata = this.parameterOptionOriginalList.filter((el: any) => el.id == e.value);
        if (filterdata.length > 0) {
            let filterName = filterdata[0].name;
            if (
                filterName === "RAM" ||
                filterName === "Storage" ||
                filterName === "No of Additional Storage"
            ) {
                this.defultUnitName = "GB";
            } else if (filterName === "CPU") {
                this.defultUnitName = "Core";
            } else if (filterName === "Event per second") {
                this.defultUnitName = "EPS";
            } else if (filterName === "Distance") {
                this.defultUnitName = "Km";
            } else {
                this.defultUnitName = "";
            }
        }

        const url = "/fieldMapping/fieldDetailsByParam?paramId=" + e.value;
        this.serviceManagementService.getMethod(url).subscribe((response: any) => {
           this.defaultParamValues = response.dataList;
            if (this.defaultParamValues.length == 1) {
                this.isMultipleFields = false;
                if (
                    this.defaultParamValues[0].endpoint != null &&
                    this.defaultParamValues[0].endpoint.size != 0 &&
                    this.defaultParamValues[0].endpoint != ""
                ) {
                    this.withEndpoint = true;
                    this.tempservice
                        .getMethod2(this.defaultParamValues[0].endpoint)
                        .subscribe((response: any) => {
                            this.defaultParamValues = response.dataList;
                        });
                } else this.withEndpoint = false;
            } else this.isMultipleFields = true;
        });
    }

    parameterOptions: any = [];
    getServiceParameter() {
        if (this.parameterOptions.length == 0) {
            this.serviceManagementService.getMethod("/service_parameters/all").subscribe((res: any) => {
                this.parameterOptionOriginalList = res.dataList;
                this.parameterList = res["dataList"].map((el: any) => {
                    el["isSelected"] = false;
                    return el;
                });
                if (!this.isEditService) {
                    this.parameterOptions = res["dataList"];
                } else if (this.isEditService) {
                    this.parameterList = this.parameterList.map((el: any) => {
                        this.serviceParamArray.value.forEach((val: any) => {
                            if (val.serviceParamId == el.id) {
                                el.isSelected = true;
                            }
                        });
                        return el;
                    });
                    this.parameterOptions = this.parameterList.filter((el: any) => !el.isSelected);
                }
            });
        }
    }

    createServiceParamFormGroup(selectedParamName): UntypedFormGroup {
        return this.fb.group({
            // isBounded: [this.addServiceParamForm.value.isBounded],
            serviceParamName: selectedParamName != null ? selectedParamName[0].name : "",
            serviceParamId: [this.addServiceParamForm.value.serviceParamId],
            isMandatory: [this.addServiceParamForm.value.isMandatory],
            value: [this.addServiceParamForm.value.value]
        });
    }

    reserServiceGroupForm() {
        this.submitted = false;
        // this.serviceGroupForm.reset();
        this.parameterOptions = [];
        this.addServiceParamForm.reset();
        // this.finalServiceParamList = [];

        this.serviceParamArray.clear();
        this.isServiceEdit = false;
        this.isEditService = false;
    }
    displayedColumnsforModal: string[] = ['serviceParameter', 'mandatory', 'value', 'delete'];
    inputDisplayedColumns: string[] = ['parameter', 'Mandatory', 'defaultValue', 'action'];
    getFormControl(index: number, controlName: string) {
        return this.serviceParamArray.at(index).get(controlName);
    }
    addServiceParam(row?: FormGroup) {
        this.submitted = true;
        if (row) {
            Object.values(row.controls).forEach(control => {
                control.markAsTouched();
                control.updateValueAndValidity();
            });

            if (row.invalid) {
                return;
            }
        }

        if (this.addServiceParamForm.valid && this.addServiceParamForm.value.serviceParamId) {
            const selectedParamName = this.parameterOptions.find(
                item => item.id === this.addServiceParamForm.value.serviceParamId
            );
            this.parameterOptions = this.parameterList.filter(
                (el: any) => el.id !== this.addServiceParamForm.value.serviceParamId
            );
            const formGroup = this.fb.group({
                serviceParamId: [this.addServiceParamForm.value.serviceParamId],
                serviceParamName: [selectedParamName?.name || ''],
                isMandatory: [this.addServiceParamForm.value.isMandatory],
                value: [this.addServiceParamForm.value.value]
            });
            this.serviceParamArray.push(formGroup);
            this.serviceParamData.push({
                serviceParamId: this.addServiceParamForm.value.serviceParamId,
                serviceParamName: selectedParamName?.name || '',
                isMandatory: this.addServiceParamForm.value.isMandatory,
                value: this.addServiceParamForm.value.value
            });
            // Update MatTableDataSource explicitly
            // this.dataSource.data = this.serviceParamData;
            this.dataSource.data = [...this.serviceParamData];
            this.addServiceParamForm.reset();
            this.submitted = false;

        }

    }
    // addServiceParam() {
    //     this.submitted = true;

    //     if (this.addServiceParamForm.valid) {
    //         const selectedParamName = this.parameterOptions.find(
    //             item => item.id === this.addServiceParamForm.value.serviceParamId
    //         );
    //         this.parameterOptions = this.parameterList.filter(
    //             (el: any) => el.id !== this.addServiceParamForm.value.serviceParamId
    //         );
    //         const formGroup = this.fb.group({
    //             serviceParamId: [this.addServiceParamForm.value.serviceParamId],
    //             serviceParamName: [selectedParamName?.name || ''],
    //             isMandatory: [this.addServiceParamForm.value.isMandatory],
    //             value: [this.addServiceParamForm.value.value]
    //         });
    //         this.serviceParamArray.push(formGroup);
    //         this.serviceParamData.push({
    //             serviceParamId: this.addServiceParamForm.value.serviceParamId,
    //             serviceParamName: selectedParamName?.name || '',
    //             isMandatory: this.addServiceParamForm.value.isMandatory,
    //             value: this.addServiceParamForm.value.value
    //         });
    //         // Update MatTableDataSource explicitly
    //         // this.dataSource.data = this.serviceParamData;
    //         this.dataSource.data = [...this.serviceParamData];
    //         this.addServiceParamForm.reset();
    //         this.submitted = false;
    //     }
    // }

    // deleteConfirmonServiceParameter(index: number, serviceParamId: number) {
    //     this.serviceParamArray.removeAt(index);
    //     this.serviceParamData.splice(index, 1);
    //     this.dataSource.data = [...this.serviceParamData];

    //     const data = this.parameterOptionOriginalList.filter((el: any) => el.id == serviceParamId);
    //     this.parameterOptions = [...this.parameterOptions, ...data];
    // }
    onRemoveServiceParameter(index: number, serviceParamId: number) {
        // Remove form control from FormArray
        this.serviceParamArray.removeAt(index);
        // Remove from data array
        this.serviceParamData.splice(index, 1);
        // Update the MatTableDataSource
        this.dataSource.data = [...this.serviceParamData];
        // Restore the removed parameter back into available options
        const data = this.parameterOptionOriginalList.filter((el: any) => el.id == serviceParamId);
        this.parameterOptions = [...this.parameterOptions, ...data];
        // this.messageService.add({
        //     severity: "success",
        //     summary: "Deleted",
        //     detail: "Service Parameter deleted successfully."
        // });
        this.toastr.success('Service Parameter deleted successfully.', 'Success!');
    }

    deleteConfirmonServiceParameter(index: number, serviceParamId: number, serviceParamName: string) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Service Parameter',
                description: `Are you sure you want to delete "${serviceParamName}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.onRemoveServiceParameter(index, serviceParamId);
            } else {
            }
        });
    }
    // saveChanges() {
    //     this.finalServiceParamList = this.serviceParamArray.value;
    //     console.log("finalServiceParamList", this.finalServiceParamList);
    //     this.serviceModelFlag = false;
    // }
    saveChanges() {
        this.finalServiceParamList = this.serviceParamArray.getRawValue(); // Get plain form values
        this.dialogRef.close(this.finalServiceParamList); // Close dialog and send data back to parent
    }
    closeModal() {
        this.dialogRef.close(); // Simply closes the dialog without passing data
    }
}
