import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { StateManagementService } from 'src/app/service/state-management.service';
import { MessageService } from 'primeng/api';

export interface createDialogData {
    inputStatus: any;
    inputName: any;
    stateData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;
    planListData: any;
    depData: any;
}

@Component({
    selector: 'app-add-edit-department-managment',
    standalone: false,
    templateUrl: './add-edit-department-managment.component.html',
    styleUrl: './add-edit-department-managment.component.css'
})
export class AddEditDepartmentManagmentComponent {

    departmentFormGroup: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isDepartmentEdit: boolean = false;
    planList: any;
    inputName: any;
    inputStatus: any;
    dName: any;
    dStatus: any;




    constructor(
        public dialogRef: MatDialogRef<AddEditDepartmentManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
        private stateManagementService: StateManagementService,
        private messageService: MessageService,

    ) { }

    ngOnInit(): void {
        this.departmentFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            planIds: ["", Validators.required],
        });
        this.planList = this.data.planListData;
        let parts = this.data.title.split(' ');
        parts.shift();
        this.dName = parts.join(' ');

        if (this.data.isEdit === true) {
            this.isDepartmentEdit = true;
            this.patchValueFormData(this.data.depData);
        }
    }


    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;
        if (this.departmentFormGroup.valid) {
            const payload = {
                name: this.departmentFormGroup.value.name,
                status: this.departmentFormGroup.value.status,
                planIds: this.departmentFormGroup.value.planIds,
            };
            this.dialogRef.close(payload);
        } else {
            this.departmentFormGroup.markAllAsTouched();
        }
    }

    patchValueFormData(item: any) {
        let planIdsArray = [];

        if (Array.isArray(item.planIds)) {
            planIdsArray = item.planIds.map((id: any) => Number(id));
        } else if (typeof item.planIds === 'string') {
            planIdsArray = item.planIds.split(',').map((id: string) => Number(id.trim()));
        } else if (item.planIds != null) {
            planIdsArray = [Number(item.planIds)];
        }

        this.departmentFormGroup.patchValue({
            name: item.name,
            status: item.status,
            planIds: planIdsArray,
        });

    }



}
