import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

export interface SubAreaDialogData {
    subAreaData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    areaTitle?: string;
    countryTitle?: string;
    cityTitle?: string;
    stateTitle?: string;
    pincodeTitle?: string;
    areaListData?: any[];
    cityListData?: any[];
    stateListData?: any[];
    countryListData?: any[];
    pincodeListData?: any[];
    subAreaTitle?: string;
}

@Component({
    selector: 'app-add-edit-subarea-management',
    templateUrl: './add-edit-subarea-management.component.html',
    styleUrls: ['./add-edit-subarea-management.component.css'],
    standalone: false
})
export class AddEditSubareaManagementComponent implements OnInit {

    subAreaFormGroup: UntypedFormGroup;
    statusOptions = [
        { label: "Active", value: "Y", val: "ACTIVE" },
        { label: "Inactive", value: "N", val: "INACTIVE" },
        { label: "UnderDevelopment", value: "U", val: "UNDERDEVELOPMENT" }
    ];
    submitted = false;
    createAccess = false;
    editAccess = false;
    isSubAreaEdit = false;
    areaTitle?: string;
    countryTitle?: string;
    cityTitle?: string;
    stateTitle?: string;
    pincodeTitle?: string;

    selectedFilePreviewd: any[] = [];
    cityListData: any[] = [];
    stateListData: any[] = [];
    countryListData: any[] = [];
    areaListData: any[] = [];
    pincodeListData: any[] = [];
    selectedCityList: any[] = [];
    selectedStateList: any[] = [];
    selectedCountryList: any[] = [];
    pincodeName: string = '';
    isAreaSelected = false;

    constructor(
        public dialogRef: MatDialogRef<AddEditSubareaManagementComponent>,
        @Inject(MAT_DIALOG_DATA) public data: SubAreaDialogData,
        private fb: UntypedFormBuilder,
    ) {
        this.createAccess = data.createAcS;
        this.editAccess = data.editAcs;
    }

    ngOnInit(): void {
        this.subAreaFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            areaId: ["", Validators.required],
            countryId: ["", Validators.required],
            cityId: ["", Validators.required],
            stateId: ["", Validators.required],
            file: [""]
        });

        this.areaTitle = this.data.areaTitle || 'Area';
        this.countryTitle = this.data.countryTitle || 'Country';
        this.cityTitle = this.data.cityTitle || 'City';
        this.stateTitle = this.data.stateTitle || 'State';
        this.pincodeTitle = this.data.pincodeTitle || 'Pincode';

        this.areaListData = this.data.areaListData || [];
        this.cityListData = this.data.cityListData || [];
        this.stateListData = this.data.stateListData || [];
        this.countryListData = this.data.countryListData || [];
        this.pincodeListData = this.data.pincodeListData || [];

        if (this.data.isEdit && this.data.subAreaData) {
            this.isSubAreaEdit = true;

            this.subAreaFormGroup.patchValue({
                name: this.data.subAreaData.name || '',
                status: this.data.subAreaData.status || '',
                areaId: this.data.subAreaData.areaId || '',
                countryId: this.data.subAreaData.countryId || '',
                cityId: this.data.subAreaData.cityId || '',
                stateId: this.data.subAreaData.stateId || ''
            });

            // Set selected lists to display current values
            this.selectedCityList = this.cityListData.filter(city => city.id === this.data.subAreaData.cityId);
            this.selectedStateList = this.stateListData.filter(state => state.id === this.data.subAreaData.stateId);
            this.selectedCountryList = this.countryListData.filter(country => country.id === this.data.subAreaData.countryId);

            // Call onAreaChange to set cascading dropdowns & pincode when area changes
            if (this.data.subAreaData.areaId) {
                this.onAreaChange({ value: this.data.subAreaData.areaId });
            }

            if (this.data.subAreaData.filename) {
                this.selectedFilePreviewd = this.data.subAreaData.filename
                    .split(',').map((name: string) => ({ name: name.trim(), status: 'old' }));
            }
        }

    }

    onAreaChange(event: any): void {
        const selectedAreaId = event.value;
        const selectedArea = this.areaListData.find(area => area.id === selectedAreaId);
        if (selectedArea) {

            this.selectedCityList = this.cityListData.filter(city => city.id === selectedArea.cityId);
            this.selectedStateList = this.stateListData.filter(state => state.id === selectedArea.stateId);
            this.selectedCountryList = this.countryListData.filter(country => country.id === selectedArea.countryId);

            const filterPincode = this.pincodeListData?.find(x => x?.pincodeid === selectedArea?.pincodeId);
            this.pincodeName = filterPincode?.pincode ?? '';

            this.subAreaFormGroup.patchValue({
                cityId: selectedArea.cityId,
                stateId: selectedArea.stateId,
                countryId: selectedArea.countryId
            });

            this.isAreaSelected = true;
        } else {
            this.selectedCityList = [];
            this.selectedStateList = [];
            this.selectedCountryList = [];
            this.pincodeName = '';

            this.subAreaFormGroup.patchValue({
                cityId: null,
                stateId: null,
                countryId: null
            });

            this.isAreaSelected = false;
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.subAreaFormGroup.valid) {
            this.dialogRef.close(this.subAreaFormGroup.value);
        } else {
            this.subAreaFormGroup.markAllAsTouched();
        }
    }
}
