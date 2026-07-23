import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { StateManagementService } from 'src/app/service/state-management.service';
import { MessageService } from 'primeng/api';
import { MatCardContent } from "@angular/material/card";
import { CityManagementService } from 'src/app/service/city-management.service';
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
    inputStatus?: string;
    inputName?: string;
    countryTitle?: string;
    subCountryTitle?: string;
    cityTitle?: string;
}
@Component({
    selector: 'app-add-edit-city-managment',
    standalone: false,
    templateUrl: './add-edit-city-managment.component.html',
    styleUrl: './add-edit-city-managment.component.css'
})
export class AddEditCityManagmentComponent {
    cityFormGroup: UntypedFormGroup;
    statusOptions = RadiusConstants.status;
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isCityEdit: boolean = false;
    countryListData: any;
    stateListData: any;
    statePojo: any = {};
    countryselectshow = false;
    stateseclectData: any = [];
    constructor(
        private toastr: ToastrService,
        public dialogRef: MatDialogRef<AddEditCityManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
        private stateManagementService: StateManagementService,
        private cityManagementService: CityManagementService,
        private messageService: MessageService,

    ) { }

    ngOnInit(): void {
        this.cityFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            countryId: ["", Validators.required],
            stateId: ["", Validators.required],
        });
        this.getCountryList();
        this.getStateList();
    }


    getStateList() {
        const url = "/state/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;

                this.tryToAddFormPathValue();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    selectStateChange(event: any) {
        let id = Number(event.value);
        const url = "/state/" + id;
        this.cityManagementService.getMethod(url).subscribe((response: any) => {
            this.countryselectshow = true;
            this.stateseclectData = response.stateData.countryPojo.id;
            this.cityFormGroup.patchValue({
                countryId: response.stateData.countryPojo.id,
            });
            this.statePojo.name = response.stateData.name;
            this.statePojo.id = response.stateData.id;
            this.statePojo.status = response.stateData.status;
            // this.cityData.countryId = response.stateData.countryId;
            // this.getCountryById(this.cityFormGroup.controls.countryId.value);
        });
    }



    getCountryList() {
        const url = "/country/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;

                this.tryToAddFormPathValue();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;

        this.cityFormGroup.markAllAsTouched();

        if (this.cityFormGroup.invalid) {
            return;
        }
        this.dialogRef.close(this.cityFormGroup.value);
    }



    tryToAddFormPathValue() {
        if (this.data.isEdit === true && this.countryListData && this.stateListData) {
            this.isCityEdit = true;
            this.editAccess = this.data.editAcs;

            const selectedCountry = this.countryListData.find(
                c => c.name === this.data.stateData.countryName
            );

            const selectedState = this.stateListData.find(
                s => s.name === this.data.stateData.stateName
            );

            this.cityFormGroup.patchValue({
                name: this.data.stateData.name || '',
                status: this.data.stateData.status || '',
                countryId: selectedCountry ? selectedCountry.id : null,
                stateId: selectedState ? selectedState.id : null,
            });
        }

    }
}
