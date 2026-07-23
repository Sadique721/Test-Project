import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { StateManagementService } from 'src/app/service/state-management.service';
import { MessageService } from 'primeng/api';
import { MatCardContent } from "@angular/material/card";
import { CityManagementService } from 'src/app/service/city-management.service';
import { PincodeManagementService } from 'src/app/service/pincode-management.service';
import { ToastrService } from 'ngx-toastr';

export interface createDialogData {
    pincodeData: any;
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;
    pincodeTitle: string;
    countryTitle: string;
    subCountryTitle: string;
    cityTitle: string;
}
@Component({
    selector: 'app-add-edit-pincode-managment',
    standalone: false,
    templateUrl: './add-edit-pincode-managment.component.html',
    styleUrl: './add-edit-pincode-managment.component.css'
})
export class AddEditPincodeManagmentComponent {
    pincodeFormGroup: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isPincodeEdit: boolean = false;
    countryListData: any;
    stateListData: any;
    cityListData: any;
    cityDetail: any;
    inputshowSelsctData: boolean = false;

    constructor(
        private toastr: ToastrService,
        public dialogRef: MatDialogRef<AddEditPincodeManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
        private stateManagementService: StateManagementService,
        private cityManagementService: CityManagementService,
        private messageService: MessageService,
        private pincodeManagementService: PincodeManagementService,

    ) { }

    ngOnInit(): void {
        this.pincodeFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            countryId: ["", Validators.required],
            stateId: ["", Validators.required],
            cityId: ["", Validators.required],
        });

        this.getCountryList();
        this.getStateList();
        this.getCityList();
    }

    getCountryList() {
        const url = "/country/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
                this.tryToAddFormPathValue();
            },
            (error: any) => { /* handle error */ }
        );
    }

    getStateList() {
        const url = "/state/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
                this.tryToAddFormPathValue();
            },
            (error: any) => { /* handle error */ }
        );
    }

    getCityList() {
        const url = "/city/all";
        this.cityManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
                this.tryToAddFormPathValue();
            },
            (error: any) => { /* handle error */ }
        );
    }


    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;
        const countryId = this.pincodeFormGroup.controls.countryId.value;
        const stateId = this.pincodeFormGroup.controls.stateId.value;
        const cityId = this.pincodeFormGroup.controls.cityId.value;
        if (this.isPincodeEdit && this.data.editAcs) {
            if (this.pincodeFormGroup.valid) {
                const payload = {
                    name: this.pincodeFormGroup.controls.name.value,
                    status: this.pincodeFormGroup.controls.status.value,
                    pincodeid: this.data.pincodeData.pincodeid,
                    countryId: countryId,
                    stateId: stateId,
                    cityId: cityId,
                };
                this.dialogRef.close(payload);
            } else {
                this.pincodeFormGroup.markAllAsTouched();
            }
        } else if (this.data.createAcS) {
            if (this.pincodeFormGroup.valid) {
                const payload = {
                    name: this.pincodeFormGroup.controls.name.value,
                    status: this.pincodeFormGroup.controls.status.value,
                    countryId: countryId,
                    stateId: stateId,
                    cityId: cityId,
                };
                this.dialogRef.close(payload);
            } else {
                this.pincodeFormGroup.markAllAsTouched();
            }
        }
    }

    selectCityChange(event) {
        const selCity = event.value;
        this.getCityDetailbyd(selCity);
        // this.getStateList();
        // this.getCountryList();

    }

    getCityDetailbyd(cityId) {
        const url = "/city/" + cityId;
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.cityDetail = response.cityList;
                // return
                this.inputshowSelsctData = true;
                this.pincodeFormGroup.controls.countryId.patchValue(this.cityDetail.countryId);
                this.pincodeFormGroup.controls.stateId.patchValue(this.cityDetail.statePojo.id);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }


    tryToAddFormPathValue() {
        if (this.data.isEdit === true &&
            this.countryListData && this.stateListData && this.cityListData) {

            this.isPincodeEdit = true;
            this.editAccess = this.data.editAcs;

            const selectedCountry = this.countryListData.find(
                c => c.name === this.data.pincodeData.countryName
            );

            const selectedState = this.stateListData.find(
                s => s.name === this.data.pincodeData.stateName
            );

            const selectedCity = this.cityListData.find(
                city => city.name === this.data.pincodeData.cityName
            );

            this.pincodeFormGroup.patchValue({
                name: this.data.pincodeData.pincode || '',
                status: this.data.pincodeData.status || '',
                countryId: selectedCountry ? selectedCountry.id : null,
                stateId: selectedState ? selectedState.id : null,
                cityId: selectedCity ? selectedCity.id : null,
            });
        }
    }

}
