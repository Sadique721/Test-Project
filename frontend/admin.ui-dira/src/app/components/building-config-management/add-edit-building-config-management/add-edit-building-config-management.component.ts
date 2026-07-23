import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { BuildingConfigManagementService } from 'src/app/service/building-config-management.service';
import { ToastrService } from 'ngx-toastr';

export interface CreateDialogData {
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
    selector: 'app-add-edit-building-config-management',
    templateUrl: './add-edit-building-config-management.component.html',
    styleUrls: ['./add-edit-building-config-management.component.css'],
    standalone: false
})
export class AddEditBuildingConfigManagementComponent implements OnInit {
    statusOptions = RadiusConstants.status;
    submitted = false;
    dunningData: any;
    createAccess = false;
    editAccess = false;
    isCountryEdit = false;

    countryListData: any[] = []; // This is used to disable controls based on length

    buildingconfFormGroup: UntypedFormGroup;
    messageService: any;
    searchkey: string;

    get title(): string {
        return this.data.title || 'Country';
    }

    constructor(
        public dialogRef: MatDialogRef<AddEditBuildingConfigManagementComponent>,
        @Inject(MAT_DIALOG_DATA) public data: CreateDialogData,
        private fb: UntypedFormBuilder,
        private toastr: ToastrService,

        private buidingConfigManagement: BuildingConfigManagementService,
    ) { }

    ngOnInit(): void {
        this.getmappingFrom();
        // Set accesses from injected data
        this.createAccess = this.data.createAcS;
        this.editAccess = this.data.editAcs;

        this.isCountryEdit = this.data.isEdit;

        // Initialize the form group according to template bindings
        this.buildingconfFormGroup = this.fb.group({
            name: ['', [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            mappingFrom: ['', Validators.required]
        });

        // Patch form values if editing
        if (this.isCountryEdit && this.data.countryData) {
            this.buildingconfFormGroup.patchValue({
                name: this.data.countryData.name || '',
                mappingFrom: this.data.countryData.mappingFrom || ''
            });
        }

       this.countryListData = this.data.countryData || [];
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }
    getmappingFrom() {
        const url = "/commonList/buildingRefrence";
        this.buidingConfigManagement.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.dunningData = response.dataList;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
            }
        );
    }
    addEditCountry(flag: string): void {
        this.submitted = true;
        if (this.buildingconfFormGroup.valid) {
            this.dialogRef.close(this.buildingconfFormGroup.value);
        } else {
            this.buildingconfFormGroup.markAllAsTouched();
        }
    }
}
