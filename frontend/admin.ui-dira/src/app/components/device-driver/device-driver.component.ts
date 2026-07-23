import { Component, OnInit, ElementRef, ViewChild, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { IDBMappingMaster } from "src/app/components/model/db-mapping-master";
import { DictionaryService } from "src/app/service/dictionary.service";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { DeviceDriverService } from "src/app/service/device-driver.service";
import { DeviceDriver } from "../model/device-driver";

import { ObserversModule } from "@angular/cdk/observers";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-device-driver",
    templateUrl: "./device-driver.component.html",
    styleUrls: ["./device-driver.component.css"],
    standalone: false
})
export class DeviceDriverComponent implements OnInit {

    displayedColumns: string[] = ['id', 'Name', 'Address', 'Action'];
    dataSource = new MatTableDataSource<any>();

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    pageSize = 5;
    pageSizeOptions: number[] = [5, 10, 20, 50, 100];
    dialogRef: any;

    changeStatusData: any = [];
    deviceDriverForm: UntypedFormGroup;
    searchForm: UntypedFormGroup;
    submitted = false;
    searchSubmitted = false;
    editDeviceDriverId: number;
    deviceDriverData: any = [];
    status = [{ label: "Active" }, { label: "Inactive" }];
    //Used and required for pagination
    totalRecords: number;
    currentPage = 0;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    createDeviceDriverData: DeviceDriver;
    editDeviceDriverData: DeviceDriver;
    mappingMasterData: IDBMappingMaster;
    editFormValues: any;
    editAttributeValues: any;
    update: boolean = true;
    editMode: boolean = false;
    dictionaryAttributeData: any = [];
    mappingMasterId: number;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    filtereDictionaryAttributeList: Array<any> = [];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));

    @ViewChild("dbMappingName") usernameRef: ElementRef;

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    createAccess: any;
    editAccess: any;
    deleteAccess: any;
    userId: string;
    superAdminId: string;

    constructor(
        private deviceDriverService: DeviceDriverService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DRIVER_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DRIVER_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DRIVER_EDIT);
        this.findAllDeviceDrivers("");
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DRIVER_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DRIVER_DELETE)) {
            return ['id', 'Name', 'Address', 'Action'];
        } else {
            return ['id', 'Name', 'Address'];
        }
    }

    ngOnInit(): void {
        this.deviceDriverForm = this.fb.group({
            name: ["", Validators.required],
            address: ["", Validators.required],
            userName: ["", Validators.required],
            password: ["", Validators.required],
            userDn: ["", Validators.required],
            passwordAttribute: ["", Validators.required],
            userNameAttribute: ["", Validators.required]
        });
        this.searchForm = this.fb.group({
            name: [null]
        });

        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
        this.dataSource = new MatTableDataSource(this.deviceDriverData);
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    async searchByName() {
        if (!this.searchkey || this.searchkey !== this.searchForm.value.name) {
            this.currentPage = 0;
        }
        this.searchkey = this.searchForm.value.name;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchSubmitted = true;

        if (this.searchForm.value.name != null || this.searchForm.value.type != null) {
            let name = this.searchForm.value.name.trim() ? this.searchForm.value.name.trim() : "";
            this.deviceDriverData = [];
            let requestData = {
                name: name
            };
            this.deviceDriverService.getDeviceDriverByName(requestData).subscribe(
                (response: any) => {
                    if (response.deviceList.length > 0) {
                        // Sort by deviceDriverId in descending order
                        this.deviceDriverData = response.deviceList.sort((a, b) => b.deviceDriverId - a.deviceDriverId);
                        this.dataSource.data = this.deviceDriverData;
                        if (response.totalRecords) {
                            this.totalRecords = response.totalRecords;
                        }
                    } else {
                        this.deviceDriverData = [];
                        this.dataSource.data = [];
                        this.totalRecords = 0;
                        this.toastr.info("No records found", 'Info!');
                    }
                },
                error => {
                    this.deviceDriverData = [];
                    this.dataSource.data = [];
                    this.totalRecords = 0;
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                }
            );
        }
    }

    clearSearchForm() {
        this.editMode = false;
        this.searchSubmitted = false;
        this.submitted = false;

        // Reset search form
        this.searchForm.reset();

        // Reset device driver form completely
        this.deviceDriverForm.reset();
        this.deviceDriverForm.markAsPristine();
        this.deviceDriverForm.markAsUntouched();

        // Reset all form controls
        Object.keys(this.deviceDriverForm.controls).forEach(key => {
            this.deviceDriverForm.get(key)?.setErrors(null);
            this.deviceDriverForm.get(key)?.markAsUntouched();
            this.deviceDriverForm.get(key)?.markAsPristine();
        });

        this.currentPage = 0;
        this.findAllDeviceDrivers("");
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllDeviceDrivers(this.showItemPerPage);
        } else {
            this.searchByName();
        }
    }

    // async findAllDeviceDrivers(list) {
    //     let size;
    //     this.searchkey = "";
    //     let page = this.currentPage;
    //     if (list) {
    //         size = list;
    //         this.itemsPerPage = list;
    //     } else {
    //         size = this.itemsPerPage;
    //     }
    //     this.deviceDriverData = [];
    //     this.deviceDriverService.findAllDeviceDrivers(page, size).subscribe(
    //         (response: any) => {
    //             this.deviceDriverData = response.deviceList;
    //             this.dataSource.data = this.deviceDriverData;
    //             //this.totalRecords = response.dbMapingMasterList.totalRecords;

    //         },
    //         error => {
    //             this.totalRecords = 0;
    //             if (error.error.status == 404) {
    //                 this.toastr.info(`${error.error.errorMessage}`, 'Info!');
    //             } else {
    //                 this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
    //             }
    //         }
    //     );
    // }

    async findAllDeviceDrivers(list) {
        this.searchkey = "";
        this.deviceDriverService.findAllDeviceDrivers(this.currentPage + 1, this.pageSize).subscribe(
            (response: any) => {
                if (response.deviceList && response.deviceList.length > 0) {
                    // Sort by deviceDriverId in descending order
                    this.deviceDriverData = response.deviceList.sort((a, b) => b.deviceDriverId - a.deviceDriverId);
                    this.dataSource.data = this.deviceDriverData;

                    // Update total records if available in response
                    if (response.totalRecords) {
                        this.totalRecords = response.totalRecords;
                    } else if (response.deviceList.length === this.pageSize) {
                        // If we got a full page, assume there are more records
                        this.totalRecords = this.deviceDriverData.length + 1;
                    } else {
                        this.totalRecords = this.deviceDriverData.length;
                    }
                } else {
                    this.deviceDriverData = [];
                    this.dataSource.data = [];
                    this.totalRecords = 0;
                }
            },
            error => {
                this.deviceDriverData = [];
                this.dataSource.data = [];
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                }
            }
        );
    }

    onPageChange(event: PageEvent) {
        this.currentPage = event.pageIndex;
        this.pageSize = event.pageSize;

        if (!this.searchkey) {
            this.findAllDeviceDrivers("");
        } else {
            this.searchByName();
        }
    }

    async editDeviceDriverById(dbMappingMasterId, index) {
        this.editMode = true;
        this.editDeviceDriverId = dbMappingMasterId;

        this.deviceDriverService.findDeviceDriverById(dbMappingMasterId).subscribe(
            (response: any) => {
                this.editDeviceDriverData = response.deviceDriver;
                this.openDeviceDrivernDialog(dbMappingMasterId);
                setTimeout(() => {
                    this.deviceDriverForm.patchValue(this.editDeviceDriverData);
                    this.editFormValues = this.deviceDriverForm.value;
                }, 0);
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    async updateDeviceDriver() {
        if (this.editDeviceDriverData) {
            this.editDeviceDriverData.name = this.deviceDriverForm.value.name;
            this.editDeviceDriverData.address = this.deviceDriverForm.value.address;
            this.editDeviceDriverData.userName = this.deviceDriverForm.value.userName;
            this.editDeviceDriverData.password = this.deviceDriverForm.value.password;
            this.editDeviceDriverData.userDn = this.deviceDriverForm.value.userDn;
            this.editDeviceDriverData.userNameAttribute = this.deviceDriverForm.value.userNameAttribute;
            this.editDeviceDriverData.passwordAttribute = this.deviceDriverForm.value.passwordAttribute;
        }
        this.deviceDriverService.updateDeviceDriver(this.editDeviceDriverData).subscribe(
            (response: any) => {
                this.resetFormAndCloseDialog();
                this.editMode = false;
                this.submitted = false;
                if (!this.searchkey) {
                    this.findAllDeviceDrivers("");
                } else {
                    this.searchByName();
                }
                // this.deviceDriverForm.reset();
                if (this.update) {
                    this.toastr.success(`${response.message}`, 'Success!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    private resetFormAndCloseDialog() {
        // Manual form reset
        this.deviceDriverForm.reset();

        // Reset form state
        this.submitted = false;
        this.editMode = false;

        // Mark form as pristine and untouched
        this.deviceDriverForm.markAsPristine();
        this.deviceDriverForm.markAsUntouched();

        // Reset all form controls
        Object.keys(this.deviceDriverForm.controls).forEach(key => {
            this.deviceDriverForm.get(key)?.setErrors(null);
            this.deviceDriverForm.get(key)?.markAsUntouched();
            this.deviceDriverForm.get(key)?.markAsPristine();
        });

        // Close dialog
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    async addDeviceDriver() {
        this.submitted = true;
        this.markFormGroupTouched(this.deviceDriverForm);

        if (this.deviceDriverForm.valid) {
            if (this.editMode) {
                this.updateDeviceDriver();
            } else {
                this.createDeviceDriverData = this.deviceDriverForm.value;
                this.deviceDriverService.addNewDeviceDriver(this.createDeviceDriverData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        // Close dialog and reset form after successful add
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.findAllDeviceDrivers("");
                        this.deviceDriverForm.reset();
                        this.toastr.success(`${response.message}`, 'Success!');
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                );
            }
        }
    }

    deleteConfirm(dbMapingMasterId, index) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            disableClose: true,
            data: {
                title: "Delete Confirmation",
                description: `Are you sure you want to delete?`,
                yesLabel: "Confirm",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteDeviceDriverById(dbMapingMasterId, index);
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });
        // this.confirmationService.confirm({
        //     message: "Do you want to delete this Device Driver?",
        //     header: "Delete Confirmation",
        //     icon: "pi pi-info-circle",
        //     accept: () => {
        //         this.deleteDeviceDriverById(dbMapingMasterId, index);
        //     },
        //     reject: () => {
        //         this.toastr.info("You have rejected", 'Info!');
        //     }
        // });
        // }
    }

    async deleteDeviceDriverById(dbMapingMasterId, index) {
        this.deviceDriverService.deleteDeviceDriverById(dbMapingMasterId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0 && this.deviceDriverData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllDeviceDrivers("");
                } else {
                    this.searchByName();
                }
                this.deviceDriverForm.reset();
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber;
        if (!this.searchkey) {
            this.findAllDeviceDrivers("");
        } else {
            this.searchByName();
        }
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        let userId = localStorage.getItem("userId");
        if (userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            //  this.reset();
            this.toastr.info("You are not authorized to do this operation. Please contact to the administrator", 'Info!');
            return false;
        }
        return true;
    }

    canExit() {
        if (!this.deviceDriverForm.dirty) return true;
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
                    }
                });
                return false;
            });
        }
    }

    testDeviceDriverConnection() {
        this.submitted = true;
        this.markFormGroupTouched(this.deviceDriverForm);
        if (this.deviceDriverForm.valid) {
            this.createDeviceDriverData = this.deviceDriverForm.value;
            this.deviceDriverService.testADConnection(this.createDeviceDriverData).subscribe(
                (response: any) => {
                    if (response.status === 200) {
                        this.toastr.success(`${response.msg}`, 'Success!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');
                }
            );
        }
    }

    private markFormGroupTouched(formGroup: UntypedFormGroup) {
        Object.keys(formGroup.controls).forEach(field => {
            const control = formGroup.get(field);
            control?.markAsTouched({ onlySelf: true });

            if (control instanceof UntypedFormGroup) {
                this.markFormGroupTouched(control);
            }
        });
    }

    @ViewChild("deviceDriverDialog") deviceDriverDialog!: TemplateRef<any>;

    openDeviceDrivernDialog(driven?: any) {
        // Only reset form state if NOT in edit mode
        if (!this.editMode) {
            this.resetFormState();
        }

        this.dialogRef = this.dialog.open(this.deviceDriverDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            // Always reset form when dialog closes
            this.resetFormState();
            // Refresh data if needed
            if (!this.searchkey) {
                this.findAllDeviceDrivers("");
            } else {
                this.searchByName();
            }
        });
    }

    private resetFormState() {
        this.submitted = false;
        this.editMode = false;

        // Complete form reset
        this.deviceDriverForm.reset();
        this.deviceDriverForm.markAsPristine();
        this.deviceDriverForm.markAsUntouched();

        // Reset individual controls
        Object.keys(this.deviceDriverForm.controls).forEach(key => {
            const control = this.deviceDriverForm.get(key);
            control?.setErrors(null);
            control?.markAsUntouched();
            control?.markAsPristine();
            control?.setValue('');
        });
    }
}
