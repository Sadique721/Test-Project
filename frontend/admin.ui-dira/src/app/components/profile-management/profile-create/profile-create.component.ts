import { Component, Inject, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { ToastrService } from "ngx-toastr"; // Replace MessageService with ToastrService
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { ProfileService } from "src/app/service/profile.service";

export interface createDialogData {
    dataid: any;
    isProfileEdit: boolean;
    editData: any;
}

@Component({
    selector: "app-profile-create",
    templateUrl: "./profile-create.component.html",
    styleUrls: ["./profile-create.component.scss"],
    standalone: false
})
export class ProfileCreateComponent implements OnInit {
    profileTitle = RadiusConstants.PROFILE;
    isProfileEdit: boolean = false;
    profileFormGroup: UntypedFormGroup;
    searchData: {
        filter: {
            filterDataType: string;
            filterValue: string;
            filterColumn: string;
            filterOperator: string;
            filterCondition: string;
        }[];
    };
    public loginService: LoginService;
    editProfileId: any;
    statusOptions = RadiusConstants.status;
    typeList: any[] = [
        { label: "Number", value: "number" },
        { label: "Timestamp", value: "timestamp" }
    ];
    submitted: boolean = false;
    selectedType: string;
    viewProfileData: any;

    constructor(
        private fb: UntypedFormBuilder,
        loginService: LoginService,
        private route: ActivatedRoute,
        private toastr: ToastrService, // Replace MessageService with ToastrService
        private profileService: ProfileService,
        private router: Router,
        public dialogRef: MatDialogRef<ProfileCreateComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
    ) {
        this.loginService = loginService;
        // this.editProfileId = this.route.snapshot.paramMap.get("profileId")!;
    }

    async ngOnInit() {
        this.isProfileEdit = this.data.isProfileEdit;
        this.editProfileId = this.data.dataid;
        if (this.editProfileId != null) {
            this.isProfileEdit = true;
            this.getProfileById(this.editProfileId);
        }
        this.profileFormGroup = this.fb.group({
            id: [""],
            name: ["", Validators.required],
            prefix: ["", Validators.required],
            status: ["", Validators.required],
            type: ["", Validators.required],
            startFrom: [""],
            year: [false],
            month: [false],
            day: [false],
            mvnoId: [""]
        });
        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ]
        };
    }

    onTypeChange(selectedValue: any): void {
        this.selectedType = selectedValue.toLowerCase();

        if (selectedValue === "number") {
            this.profileFormGroup.controls["startFrom"].setValidators([Validators.required]);
            this.profileFormGroup.controls["startFrom"].updateValueAndValidity();
            this.clearTimestampValidators();
            this.resetTimestampCheckboxes();
        } else if (selectedValue === "timestamp") {
            this.profileFormGroup.controls["startFrom"].clearValidators();
            this.profileFormGroup.controls["startFrom"].updateValueAndValidity();
            this.profileFormGroup.controls["startFrom"].reset();
        }
    }

    onlyNumberKey(event: KeyboardEvent) {
        let specialKeys: Array<string> = [
            "Backspace",
            "Tab",
            "End",
            "Home",
            "ArrowLeft",
            "ArrowRight",
            "ArrowUp",
            "ArrowDown"
        ];

        if (specialKeys.indexOf(event.key) !== -1) {
            return;
        }
        let current: string = event.currentTarget["value"];
        let next: string = current.concat(event.key);
        if (next && !String(next).match(/^[\d]*$/)) {
            event.preventDefault();
        }
    }

    clearTimestampValidators(): void {
        const timestampFields = ["year", "month", "day"];
        timestampFields.forEach(field => {
            if (this.profileFormGroup.controls[field]) {
                this.profileFormGroup.controls[field].clearValidators();
                this.profileFormGroup.controls[field].updateValueAndValidity();
            }
        });
    }

    resetTimestampCheckboxes(): void {
        const timestampFields = ["year", "month", "day"];
        timestampFields.forEach(field => {
            if (this.profileFormGroup.controls[field]) {
                this.profileFormGroup.controls[field].setValue(false);
            }
        });
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.profileFormGroup.invalid) {
            this.profileFormGroup.markAllAsTouched();
            return;
        }
        const formData = this.profileFormGroup.value;
        if (this.isProfileEdit) {
            this.updateProfile(formData);
        } else {
            this.addProfile(formData);
        }
    }

    addProfile(formData: any): void {
        const url = "/custAccountProfile/create";
        this.profileService.postMethod(url, formData).subscribe(
            (response: any) => {
                if (response.status === 200) {
                    this.submitted = false;
                    this.profileFormGroup.reset();
                    this.dialogRef.close(null);

                    this.toastr.success(`Successfully Created`, 'Success!');
                    this.router.navigate(["/home/profile/list"]);
                } else {
                    this.toastr.info(`${response.message}`, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    updateProfile(formData: any): void {
        const id = formData.id;
        const url = "/custAccountProfile/update/" + this.editProfileId;
        this.profileService.updateMethod(url, formData).subscribe(
            (response: any) => {
                this.submitted = false;
                this.profileFormGroup.reset();
                this.dialogRef.close(null);
                if (response.status === 200) {
                    this.submitted = false;
                    this.profileFormGroup.reset();
                    this.toastr.success(`Successfully Updated`, 'Success!');

                    if (formData.status === "Inactive") {
                        this.setDefaultProfile(this.editProfileId);
                    }
                    this.router.navigate(["/home/profile/list"]);
                } else {
                    this.toastr.info(`${response.message}`, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    setDefaultProfile(id) {
        if (id) {
            const url = "/mvno/setDefaultProfile/" + id;
            this.profileService.updateMethod(url, id).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');
                    } else {
                        this.toastr.info(`${response.message}`, 'Info!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    getProfileById(editProfileId) {
        if (editProfileId) {
            const url = "/custAccountProfile/get/" + editProfileId;
            this.profileService.getMethod(url).subscribe(
                (response: any) => {
                    this.isProfileEdit = true;
                    this.viewProfileData = response.custAccountProfilesList;
                    this.selectedType = this.viewProfileData.type;
                    if (this.selectedType) {
                        this.onTypeChange(this.selectedType);
                    }
                    this.profileFormGroup.patchValue(this.viewProfileData);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    closeDialog() {
        this.dialogRef.close(null);
    }
}
