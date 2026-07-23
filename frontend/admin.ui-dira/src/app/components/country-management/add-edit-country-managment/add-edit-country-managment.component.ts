import { Component, Inject, input, Output } from '@angular/core';
import { DeleteDialogData } from 'src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { MatCard } from "@angular/material/card";
import { MaterialModule } from "src/app/material.module";
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormGroup } from '@angular/forms';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { EventEmitter } from 'stream';

export interface createDialogData {
    countryData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;
    inputName?: string;
    inputStatus?: string;
}


@Component({
    selector: 'app-add-edit-country-managment',
    templateUrl: './add-edit-country-managment.component.html',
    styleUrl: './add-edit-country-managment.component.css',
    standalone: false
})
export class AddEditCountryManagmentComponent {

    countryFormGroup: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isCountryEdit: boolean = false;
    inputName?: string;
    inputStatus?: string;
    cName?: string;
    cStatus?: string;


    constructor(
        public dialogRef: MatDialogRef<AddEditCountryManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
    ) { }

    ngOnInit(): void {
        this.countryFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required]
        });
        this.inputName = this.data.inputName;
        this.inputStatus = this.data.inputStatus;

        let parts = this.inputName.split(' ');
        let parts2 = this.inputStatus.split(' ');
        parts.shift();
        parts2.shift();
        this.cName = parts.join(' ');
        this.cStatus = parts2.join(' ');


        if (this.data.isEdit === true) {
            this.isCountryEdit = true;
            this.editAccess = this.data.editAcs;
            this.countryFormGroup.patchValue({
                name: this.data.countryData.name || '',
                status: this.data.countryData.status || ''
            });
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;
        if (this.countryFormGroup.valid) {
            this.dialogRef.close(this.countryFormGroup.value);
        } else {
            this.countryFormGroup.markAllAsTouched();
        }
    }


}
