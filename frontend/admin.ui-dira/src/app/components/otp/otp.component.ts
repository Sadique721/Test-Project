import { Component, OnInit, ElementRef, ViewChild, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { OtpService } from "src/app/service/otp.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { NOTIFICATIONS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-otp",
    templateUrl: "./otp.component.html",
    styleUrls: ["./otp.component.css"],
    standalone: false
})
export class OtpComponent implements OnInit {
    displayedColumns: string[] = [
        "id",
        "profileName",
        "otpLength",
        "otpValidityInMin",
        "generationType",
        "mvnoName", "action"
    ];
    @ViewChild('otpDetailDialog') otpDetailDialog!: TemplateRef<any>;
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild('profileDialogTemplate') profileDialogTemplate!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    mvnoData: any;
    loggedInUser: any;
    otpProfiles: any = [];
    otpProfileList: any[] = [];
    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;

    profileName: string;
    selectedAllowedValues: [];
    searchForm: UntypedFormGroup;
    detailGroupForm: UntypedFormGroup;

    editId: any;
    submitted: boolean = false;
    searchSubmitted = false;

    allowedValues = [
        { label: "Upper Case", value: "UPPER_CASE" },
        { label: "Lower Case", value: "LOWER_CASE" },
        { label: "Symbol", value: "SYMBOL" },
        { label: "Number", value: "NUMBER" },
    ];
    generationTypes = [
        { label: "Always New", value: "ALWAYS_NEW" },
        { label: "Reuse", value: "REUSE" },
        { label: "Static", value: "STATIC" },
    ];

    @ViewChild("focusOnOTP") usernameRef: ElementRef;
    showDetailsDialogue: boolean;
    otpProfileData: any = [];

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: any = "";
    totalDataListLength = 0;

    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    editAccess: boolean = false;
    deleteAccess: boolean = false;
    sendAccess: boolean = false;
    public loginService: LoginService;
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private otpService: OtpService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private radiusUtility: RadiusUtility,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_OTP_CREATE);
        this.deleteAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_OTP_DELETE);
        this.editAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_OTP_EDIT);
        this.editId = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.getAll("");
        this.detailGroupForm = this.fb.group({
            name: ["", Validators.required],
            length: ["", [Validators.required, Validators.pattern("^[0-9]*$")]],
            validity: ["", [Validators.required, Validators.pattern("^[0-9]*$")]],
            selectedGenerationType: [[], Validators.required],
            selectedAllowedValues: ["", Validators.required],
            staticOtp: [""],
        });
        this.searchForm = this.fb.group({
            name: [""],
        });
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_OTP_EDIT)) {
            return ["id",
                "profileName",
                "otpLength",
                "otpValidityInMin",
                "generationType",
                "mvnoName", "action"];
        } else {
            return ["id",
                "profileName",
                "otpLength",
                "otpValidityInMin",
                "generationType",
                "mvnoName"];
        }
    }

    createNewOtp() {
        if (
            !this.loginService.hasOperationPermission(
                AclClassConstants.ACL_OTP,
                AclConstants.OPERATION_OTP_ADD,
                AclConstants.OPERATION_OTP_ALL
            )
        ) {
            this.toastr.error("Sorry you have not privilege to add or edit operation!", 'Failed!');

        } else {
            this.clearFormData();
            this.usernameRef.nativeElement.focus();
        }
    }


    getAll(list) {
        let size;
        this.searchkey = "";
        let page_list = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            if (this.showItemPerPage == 1) {
                this.itemsPerPage = this.pageITEM;
            } else {
                this.itemsPerPage = this.showItemPerPage;
            }
        }
        const requestData = {
            page: page_list,
            pageSize: size,
            sortBy: "createdate",
        };

        this.otpService.getAll(requestData).subscribe(
            (response: any) => {
                this.otpProfiles = response.otpProfileList;
                // this.totalRecords =  this.otpProfiles.length;
                this.dataSource.data = this.otpProfiles;
                if (this.showItemPerPage > this.itemsPerPage) {
                    this.totalDataListLength = this.otpProfiles.length % this.showItemPerPage;
                } else {
                    this.totalDataListLength = this.otpProfiles.length % this.showItemPerPage;
                }
            },
            (error: any) => {
                this.otpProfileList = [];
                this.dataSource.data = [];
                this.totalRecords = 0;
            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getAll(this.showItemPerPage);
        } else {
            this.search();
        }
    }

    getGenerationType(generationType: string) {
        return generationType.replace("_", " ");
    }

    search() {
        if (this.searchForm.valid) {
            // this.currentPage = 1;
            if (!this.searchkey || this.searchkey !== this.searchForm.controls.name.value.trim()) {
                this.currentPage = 1;
            }

            if (this.showItemPerPage == 1) {
                this.itemsPerPage = this.pageITEM;
            } else {
                this.itemsPerPage = this.showItemPerPage;
            }

            this.searchkey = this.searchForm.controls.name.value;

            this.searchSubmitted = true;

            this.otpProfileList = [];
            let profileName = this.searchForm.controls.name.value
                ? this.searchForm.controls.name.value
                : "";
            this.otpService.getByName(profileName).subscribe(
                (response: any) => {
                    this.otpProfiles = response.otpProfileList;
                    this.totalRecords = this.otpProfiles.length;
                    this.dataSource.data = this.otpProfiles;

                    if (this.showItemPerPage > this.itemsPerPage) {
                        this.totalDataListLength = this.otpProfiles.length % this.showItemPerPage;
                    } else {
                        this.totalDataListLength = this.otpProfiles.length % this.showItemPerPage;
                    }
                },
                (error: any) => {
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.msg}`, 'Info!');

                    } else if (error.error.status == 400) {
                        this.toastr.info("Data Not Found", 'Info!');

                    } else {
                        this.totalRecords = 0;
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                    this.totalRecords = 0;
                    this.otpProfileList = [];
                }
            );
        }
    }
    deleteConfirm(profile: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Profile',
                description: `Are you sure you want to delete "${profile.profileName}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.delete(profile.profileId, null);
            }
        });
    }

    delete(profileId, index) {
        this.otpService.deleteById(profileId).subscribe(
            (response: any) => {
                this.clearFormData();
                if (this.currentPage != 1 && this.totalDataListLength == 1) {
                    this.currentPage = this.currentPage - 1;
                }

                if (!this.searchkey) {
                    this.getAll("");
                } else {
                    this.search();
                }
                this.toastr.success("Successful Deleted", 'Success!');

            },
            error => {
                this.toastr.error(`${error.error.errorMessage || error.error.ERROR}`, 'Error!');

            }
        );
    }

    edit(profileId) {
        this.editId = profileId;

        this.otpService.getById(profileId).subscribe(
            (response: any) => {
                let otpProfileData = response.otpProfile;

                this.detailGroupForm.patchValue({
                    name: otpProfileData.profileName,
                    length: otpProfileData.otpLength,
                    validity: otpProfileData.otpValidityInMin,
                    selectedAllowedValues: otpProfileData.type,
                    selectedGenerationType: otpProfileData.generationType,
                    staticOtp: otpProfileData.staticOtp,
                });



                this.openprofileDialog(true, otpProfileData);
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    mvnoId: any;
    addOrUpdate() {
        this.submitted = true;
        // if (this.detailGroupForm.invalid) {
        //     return;
        // }
        this.detailGroupForm.markAllAsTouched();
        this.mvnoId = this.detailGroupForm.value.mvnoName;
        if (this.editId) {
            let otpProfileData = {
                profileId: this.editId,
                profileName: this.detailGroupForm.value.name,
                otpLength: this.detailGroupForm.value.length,
                otpValidityInMin: this.detailGroupForm.value.validity,
                generationType: this.detailGroupForm.value.selectedGenerationType,
                type: this.detailGroupForm.value.selectedAllowedValues,
                staticOtp: this.detailGroupForm.value.staticOtp,
            };

            this.update(otpProfileData);
        } else {
            let otpProfileData = {
                profileId: null,
                profileName: this.detailGroupForm.value.name,
                otpLength: this.detailGroupForm.value.length,
                otpValidityInMin: this.detailGroupForm.value.validity,
                generationType: this.detailGroupForm.value.selectedGenerationType,
                type: this.detailGroupForm.value.selectedAllowedValues,
                staticOtp: this.detailGroupForm.value.staticOtp,
            };
            this.add(otpProfileData);
        }
    }

    private add(otpProfileData) {
        this.otpService.add(otpProfileData).subscribe(
            (response: any) => {
                this.getAll("");
                this.dialogRef.close();
                this.toastr.success("Successful Created", 'Success!');

                this.clearFormData();
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    private update(data: any) {
        this.otpService.update(this.editId, data).subscribe(
            (response: any) => {
                if (!this.searchkey) {
                    this.getAll("");
                } else {
                    this.search();
                }
                this.dialogRef.close();
                this.toastr.success("Successful Updated", 'Success!');

                this.clearFormData();
            },
            error => {
                this.toastr.error(`${error.error.errorMessage || error.error.ERROR}`, 'Failed!');

            }
        );
    }

    clearFormData() {
        this.editId = null;
        this.submitted = false;
        this.detailGroupForm.reset();
        this.detailGroupForm.get("selectedAllowedValues").patchValue([]);
    }

    clearSearchForm() {
        this.clearFormData();
        this.searchSubmitted = false;
        this.searchForm.reset();
        this.currentPage = 1;
        this.getAll("");
        this.searchForm.controls.name.setValue("");
    }
    pageChanged(event: any) {
        this.itemsPerPage = event.pageSize;
        this.currentPage = event.pageIndex + 1;

        if (!this.searchkey) {
            this.getAll(this.itemsPerPage);
        } else {
            this.search();
        }
    }

    getOTPDetail(profileId: any) {
        this.otpService.getById(profileId).subscribe(
            (response: any) => {
                this.otpProfileData = response.otpProfile;
                this.dialog.open(this.otpDetailDialog, {
                    width: '500px'
                });
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    canExit() {
        if (!this.detailGroupForm.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                this.confirmationService.confirm({
                    header: "Alert",
                    message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                    icon: "pi pi-info-circle",
                    accept: () => {
                        observer.next(true);
                        observer.complete();
                    },
                    reject: () => {
                        observer.next(false);
                        observer.complete();
                    },
                });
                return false;
            });
        }
    }

    generationTypeChange(event: any) {
        let value = event.value;
        if (value === "STATIC") {
            this.detailGroupForm.get("staticOtp").setValidators([Validators.required]);
            this.detailGroupForm.get("staticOtp").updateValueAndValidity();
        } else {
            this.detailGroupForm.get("staticOtp").clearValidators();
            this.detailGroupForm.get("staticOtp").updateValueAndValidity();
        }
    }
    openprofileDialog(edit: boolean = false, profileData: any = null) {

        this.editId = edit && profileData ? profileData.profileId : null;

        if (!edit) {

            this.detailGroupForm.reset();
            this.detailGroupForm.get("selectedAllowedValues").patchValue([]);
        }
        this.dialogRef = this.dialog.open(this.profileDialogTemplate, {
            width: '800px'
        });



    }

    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }
}
