import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';

export interface IntegrationConfigDialogData {
    configData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description?: string;
    yesLabel?: string;
    noLabel?: string;
    inputName?: string;
    inputStatus?: string;
}

@Component({
    selector: 'app-add-edit-integration-configuration',
    templateUrl: './add-edit-integration-configuration.component.html',
    styleUrls: ['./add-edit-integration-configuration.component.css'],
    standalone: false,
})
export class AddEditIntegrationConfigurationComponent implements OnInit {
    integrationConfigFormGroup: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted = false;
    createAccess = true;
    editAccess = false;
    isEditMode = false;
    inputName?: string;
    inputStatus?: string;
    cName?: string;
    cStatus?: string;

    constructor(
        public dialogRef: MatDialogRef<AddEditIntegrationConfigurationComponent>,
        @Inject(MAT_DIALOG_DATA) public data: IntegrationConfigDialogData,
        private fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this.integrationConfigFormGroup = this.fb.group({
            id: [''],
            name: ['', [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            baseurl: ['', Validators.required],
            port: ['', Validators.required],
            username: [''],
            password: [''],
        });

        this.inputName = this.data.inputName;
        this.inputStatus = this.data.inputStatus;

        if (this.inputName) {
            const parts = this.inputName.split(' ');
            parts.shift();
            this.cName = parts.join(' ');
        }

        if (this.inputStatus) {
            const parts2 = this.inputStatus.split(' ');
            parts2.shift();
            this.cStatus = parts2.join(' ');
        }

        if (this.data.isEdit === true) {
            this.isEditMode = true;
            this.editAccess = this.data.editAcs;
            this.integrationConfigFormGroup.patchValue({
                id: this.data.configData.id || '',
                name: this.data.configData.name || '',
                baseurl: this.data.configData.baseurl || '',
                port: this.data.configData.port || '',
                username: this.data.configData.username || '',
                password: this.data.configData.password || '',
                status: this.data.configData.status || '',
            });
        }

        this.createAccess = this.data.createAcS;
    }
    keypressId(event: any) {
        const pattern = /^[0-9]+$/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }
    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.integrationConfigFormGroup.valid) {
            this.dialogRef.close(this.integrationConfigFormGroup.value);
        } else {
            this.integrationConfigFormGroup.markAllAsTouched();
        }
    }
}
