import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { StateManagementService } from 'src/app/service/state-management.service';
import { MessageService } from 'primeng/api';
import { MatCardContent } from "@angular/material/card";
import { MatInputModule } from "@angular/material/input";
import { SharedModule } from "src/app/shared/shared.module";
import { Regex } from 'src/app/constants/regex';

export interface createDialogData {
    gatewayAtrribute: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;

}

@Component({
    selector: 'app-add-edit-qos-policy-managment',
    standalone: false,
    templateUrl: './add-edit-qos-policy-managment.component.html',
    styleUrl: './add-edit-qos-policy-managment.component.css'
})
export class AddEditQosPolicyManagmentComponent {

    qosPolicyGroupForm: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isStateEdit: boolean = false;
    gatewayAtrribute: any;
    gatewaytableColumns = ['gatewayName', "downloadSpeed", 'uploadSpeed', 'baseDownloadSpeed', 'baseUploadSpeed', 'throttleDownloadSpeed', 'throttleUploadSpeed', 'delete'];

    constructor(
        public dialogRef: MatDialogRef<AddEditQosPolicyManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
    ) { }

    ngOnInit(): void {
        this.qosPolicyGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            thpolicyname: ["", Validators.required],
            thparam1: ["", Validators.required],
            thparam2: ["", Validators.required],
            thparam3: ["", Validators.required],
            description: ["", [Validators.required, Validators.pattern(Regex.characterlength100)]],
            basepolicyname: ["", Validators.required],
            baseparam1: ["", Validators.required],
            baseparam2: ["", Validators.required],
            baseparam3: ["", Validators.required],
            type: [""],
            qosspeed: ["", Validators.required],
            status: ["", Validators.required],
            upstreamprofileuid: [""],
            downstreamprofileuid: [""],
            upstreamprofileName: [""],
            downstreamprofileName: [""],
        });
        this.gatewayAtrribute = this.data.gatewayAtrribute;

    }

    onKeymobilelength(event) { }



    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;
        if (this.qosPolicyGroupForm.valid) {
            const payload = {
                name: this.qosPolicyGroupForm.value.name,
                // status: this.qosPolicyGroupForm.value.status,
                countryId: this.qosPolicyGroupForm.value.countryName,
            };
            this.dialogRef.close(payload);
        } else {
            this.qosPolicyGroupForm.markAllAsTouched();
        }
    }

    deleteConfirmInActiveAttribute() { }

}
