import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { StateManagementService } from 'src/app/service/state-management.service';
import { MessageService } from 'primeng/api';
import { ToastrService } from 'ngx-toastr';

export interface createDialogData {
    stateData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;
    dynamicSubCountryName?: string;
    dynamicCountryTitle?: string;
}
@Component({
    selector: 'app-add-edit-state-managment',
    standalone: false,
    templateUrl: './add-edit-state-managment.component.html',
    styleUrl: './add-edit-state-managment.component.css'
})
export class AddEditStateManagmentComponent {
    stateFormGroup: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isStateEdit: boolean = false;
    countryListData: any;
    countryPojo: any = {};
    stateData: any;
    constructor(
        public dialogRef: MatDialogRef<AddEditStateManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
        private toastr: ToastrService,
        private stateManagementService: StateManagementService,
        private messageService: MessageService,

    ) { }

    ngOnInit(): void {

        this.stateFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            countryName: ["", Validators.required],

        });
        this.getCountryList();
    }

    getCountryList() {
        const url = "/country/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;

                if (this.data.isEdit === true) {
                    this.isStateEdit = true;
                    this.editAccess = this.data.editAcs;
                    const selectedCountry = this.countryListData.find(
                        c => c.name === this.data.stateData.countryName
                    );
                    this.stateFormGroup.patchValue({
                        name: this.data.stateData.name || '',
                        status: this.data.stateData.status || '',
                        countryName: selectedCountry ? selectedCountry.id : null,
                    });
                }
            },
            (error: any) => {
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

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;
        if (this.stateFormGroup.valid) {
            const payload = {
                name: this.stateFormGroup.value.name,
                status: this.stateFormGroup.value.status,
                countryId: this.stateFormGroup.value.countryName,
            };
            this.dialogRef.close(payload);
        } else {
            this.stateFormGroup.markAllAsTouched();
        }
    }

    getCountryById(countryId) {
        if (countryId) {
            const url = "/country/" + countryId;
            this.stateManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.countryPojo.name = response.countryData.name;
                    this.countryPojo.id = response.countryData.id;
                    this.countryPojo.status = response.countryData.status;
                },
                (error: any) => {
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
    }

    countryChange(event: any) {
        const countryId = this.stateFormGroup.controls.countryName.value;
        if (countryId) {
            this.getCountryById(countryId);
        }
    }
}
