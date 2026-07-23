import { status } from "./../../RadiusUtils/RadiusConstants";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { LocationService } from "src/app/service/location.service";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup, } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { ToastrService } from 'ngx-toastr'; // Replace MessageService import
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { LocationMaster } from "../model/location";
import { LoginService } from "src/app/service/login.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-location",
    templateUrl: "./location.component.html",
    styleUrls: ["./location.component.css"],
    standalone: false
})
export class LocationComponent implements OnInit {
    // Add these properties to your component class (after existing properties)
    displayedColumns: string[] = ['locationId', 'name', 'identifyValue', 'status', 'action'];
    locationMacDisplayedColumns: string[] = ['identity', 'mac', 'delete'];

    dataSource = new MatTableDataSource<any>();

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    locationMacDataSource = new MatTableDataSource<any>();
    @ViewChild('locationMacPaginator', { static: false }) locationMacPaginator: MatPaginator;
    overChargeListItemPerPage = 5;
    currentPageoverChargeList = 1;
    overChargeListtotalRecords = 0;

    locationMacDetailsDataSource = new MatTableDataSource<any>();
    @ViewChild('locationMacDetailsPaginator', { static: false }) locationMacDetailsPaginator: MatPaginator;
    locationMacDetailsDisplayedColumns: string[] = ['identity', 'mac', 'delete'];
    locationMacDetailsItemPerPage = 5;
    locationMacDetailsTotalRecords = 0;
    dialogRef3: any;

    @ViewChild("locationMacDetailsDialog") locationMacDetailsDialog!: TemplateRef<any>;

    locationMacDetailsCurrentPage = 0; // Add this new property
    currentPageLocationMacDetails = 0;

    // Add page size options
    pageSizeOptions = [5, 10, 20, 50, 100];
    dialogRef: any;
    dialogRef2: any;
    locationMaster: any = [];
    changeStatusData: any = [];
    locationData: any = [];
    createForm: UntypedFormGroup;
    searchByNameForm: UntypedFormGroup;
    submitted = false;
    searchSubmitted = false;
    //Used and required for pagination
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    totalRecords: number;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    editConcurrentId: number;
    editMode: boolean = false;
    status = [{ label: "Active" }, { label: "Inactive" }];
    mvnoData: any;
    name: string;
    loggedInUser: any;
    mvnoId: any;
    modalToggle: boolean = true;
    showItemPerPage: any;
    searchkey: string;
    showLocationDetails: LocationMaster = new LocationMaster();
    showDialogue: boolean;
    createAccess: boolean = false;
    //   deleteAccess: boolean = false;
    editAccess: boolean = false;
    showLocationMac: boolean = false;
    locationMacForm: UntypedFormGroup;
    overLocationMacArray = this.fb.array([]);
    locationMacsubmitted: boolean = false;
    locationMacData = [];
    showChargeDetails: boolean = false;
    isMacExist: boolean = false;
    userId: string;
    superAdminId: string;

    constructor(
        private toastr: ToastrService, // Replace MessageService with ToastrService
        private dialog: MatDialog,
        private locationService: LocationService,
        private radiusUtility: RadiusUtility,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.LOCATION_MASTER_CREATE);
        // this.deleteAccess = loginService.hasPermission(PRODUCTS.LOCATION_MASTER_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.LOCATION_MASTER_EDIT);
    }

    ngOnInit(): void {
        this.createForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            checkItem: [""],
            locationIdentifyAttribute: [""],
            locationIdentifyValue: [""],
            mvnoName: [""]
        });

        this.searchByNameForm = this.fb.group({
            name: [""]
        });

        this.locationMacForm = this.fb.group({
            identity: [""],
            mac: ["", Validators.required]
        });

        this.findAll("");
        // this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.getMvnoNameAnfId();

        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.superAdminId = RadiusConstants.SUPERADMINID;
        this.mvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
    }

    //Properties of Confirmation Popup
    popoverTitle: string = RadiusConstants.CONFIRM_DIALOG_TITLE;
    popoverMessage: string = RadiusConstants.DELETE_GROUP_CONFIRM_MESSAGE;
    confirmedClicked: boolean = false;
    cancelClicked: boolean = false;
    closeOnOutsideClick: boolean = true;

    async searchByName() {
        if (!this.searchkey || this.searchkey !== this.searchByNameForm.controls.name.value) {
            this.currentPage = 1;
        }
        this.searchSubmitted = true;
        if (this.searchByNameForm.valid) {
            this.locationData = [];

            let name = this.searchByNameForm.controls.name.value
                ? this.searchByNameForm.controls.name.value
                : "";

            if (this.showItemPerPage) {
                this.itemsPerPage = this.showItemPerPage;
            }

            this.searchkey = name;
            this.locationService.getAllLocation(this.currentPage, this.itemsPerPage, name).subscribe(
                (response: any) => {
                    this.locationData = response?.locationMasterList?.data;
                    this.dataSource = this.locationData;
                    this.dataSource.sort = this.sort;
                    this.totalRecords = response?.locationMasterList?.totalRecords;
                },
                (error: any) => {
                    this.locationData = [];
                    this.totalRecords = 0;
                    this.dataSource.data = [];
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                    }
                }
            );
        }
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.locationMacDataSource.data = this.overLocationMacArray.controls;
        this.locationMacDataSource.paginator = this.locationMacPaginator;
    }

    deleteLocationMacDetail(detailIndex: number) {
        let actualIndex = detailIndex;
        if (this.locationMacDetailsPaginator) {
            const currentPage = this.locationMacDetailsPaginator.pageIndex;
            const pageSize = this.locationMacDetailsPaginator.pageSize;
            actualIndex = (currentPage * pageSize) + detailIndex;
        }

        this.locationMacData.splice(actualIndex, 1);
        this.overLocationMacArray.removeAt(actualIndex);

        this.locationMacDetailsDataSource.data = [...this.locationMacData];
        this.locationMacDetailsTotalRecords = this.locationMacData.length;
        this.locationMacDataSource.data = [...this.overLocationMacArray.controls];
        this.overChargeListtotalRecords = this.overLocationMacArray.controls.length;

        if (this.locationMacDetailsPaginator) {
            const totalPages = Math.ceil(this.locationMacDetailsTotalRecords / this.locationMacDetailsPaginator.pageSize);
            if (this.locationMacDetailsPaginator.pageIndex >= totalPages && totalPages > 0) {
                this.locationMacDetailsPaginator.pageIndex = totalPages - 1;
                this.locationMacDetailsCurrentPage = totalPages - 1;
            }

            this.locationMacDetailsDataSource.paginator = this.locationMacDetailsPaginator;
        }
    }

    onPageChange(event: PageEvent) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.findAll("");
        } else {
            this.searchByName();
        }
    }

    async findAll(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.locationService.getAllLocation(page, size, (this.name = "")).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.message}`, 'Info!');
                    this.dataSource.data = [];
                } else {
                    this.locationData = response.locationMasterList.data;
                    this.dataSource = this.locationData;
                    this.dataSource.sort = this.sort;
                    this.totalRecords = response.locationMasterList.totalRecords;
                }
            },
            (error: any) => {
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                }
            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.itemsPerPage = this.showItemPerPage;
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAll(this.showItemPerPage);
        } else {
            this.searchByName();
        }
    }

    async deleteById(locationMasterId, selectedMvnoId, index) {
        this.locationService.deleteLocation(locationMasterId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0 && this.locationData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }

                if (!this.searchkey) {
                    this.findAll("");
                    this.locationData = [];
                } else {
                    this.searchByName();
                }
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed');
            }
        );
    }

    async addConcurrent() {
        this.submitted = true;
        this.userId = localStorage.getItem("userId");
        if (this.userId == RadiusConstants.SUPERADMINID) {
            this.createForm.get("mvnoName").setValidators([Validators.required]);
            this.createForm.get("mvnoName").updateValueAndValidity();
        }
        if (this.createForm.valid) {
            this.createForm.get("mvnoName").clearValidators();
            this.createForm.get("mvnoName").updateValueAndValidity();

            // Ensure locationMacData is current with FormArray values
            this.locationMacData = this.overLocationMacArray.value;

            if (this.editMode) {
                const updatedData = {
                    locationMasterId: this.editConcurrentId,
                    name: this.createForm.value.name,
                    status: this.createForm.value.status,
                    checkItem: this.createForm.value.checkItem,
                    locationIdentifyAttribute: this.createForm.value.locationIdentifyAttribute,
                    locationIdentifyValue: this.createForm.value.locationIdentifyValue,
                    mvnoName: this.createForm.value.mvnoName,
                    locationMasterMapping: this.locationMacData
                };
                this.locationService.updateLocation(updatedData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.editMode = false;
                        this.createForm.reset();
                        this.overLocationMacArray = this.fb.array([]);
                        this.locationMacData = [];
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }

                        if (!this.searchkey) {
                            this.findAll("");
                        } else {
                            this.searchByName();
                        }
                        this.editMode = false;
                        this.toastr.success(`${response.msg}`, 'Success!');
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                    }
                );
            } else {
                var addLocationrequest = this.createForm.value;
                this.userId = localStorage.getItem("userId");
                if (this.userId == RadiusConstants.SUPERADMINID) {
                }
                addLocationrequest.locationMasterMapping = this.locationMacData;
                this.locationService.addNewLocation(addLocationrequest).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.createForm.reset();
                        this.overLocationMacArray = this.fb.array([]);
                        this.locationMacData = [];

                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.findAll("");
                        this.toastr.success(`${response.msg}`, 'Success!');
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                    }
                );
            }
        }
    }

    async editById(locationMasterId, index, selectedMvnoId) {
        this.openlocationDialog(locationMasterId);
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.editMode = true;

            this.editConcurrentId = locationMasterId;
            let locationData: any;
            this.overLocationMacArray = this.fb.array([]);
            this.locationMacData = [];
            this.locationService.getLocationById(locationMasterId).subscribe(
                (response: any) => {
                    locationData = response.locationMaster;
                    this.createForm.patchValue({
                        name: locationData.name,
                        status: locationData.status,
                        checkItem: locationData.checkItem,
                        locationIdentifyAttribute: locationData.locationIdentifyAttribute,
                        locationIdentifyValue: locationData.locationIdentifyValue,
                        mvnoName: locationData.mvnoId
                    });

                    if (locationData.locationMasterMappings) {
                        locationData.locationMasterMappings.forEach(el => {
                            this.overLocationMacArray.push(
                                this.fb.group({
                                    identity: [el.identity],
                                    mac: [el.mac],
                                    isUsed: [el.isUsed]
                                })
                            );
                        });
                        this.locationMacData = this.overLocationMacArray.value;
                        this.locationMacDataSource.data = this.overLocationMacArray.controls;
                        this.overChargeListtotalRecords = this.overLocationMacArray.controls.length;
                    }
                },
                error => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                }
            );
        }
    }

    clearSearchForm() {
        this.editMode = false;
        this.searchSubmitted = false;
        this.currentPage = 1;
        this.searchByNameForm.reset();
        this.clearFormData(); // Use the new method
        this.findAll("");
    }

    async changeStatus(name, status, selectedMvnoId) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            if (status == "Active") {
                status = "Inactive";
            } else {
                status = "Active";
            }
            this.locationService.changeLocationSatus(name, status, selectedMvnoId).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.msg}`, 'Success!');
                    if (this.searchkey) {
                        this.searchByName();
                    } else {
                        this.findAll("");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                }
            );
        } else {
            if (!this.searchkey) {
                this.findAll("");
            } else {
                this.searchByName();
            }
        }
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber;

        if (!this.searchkey) {
            this.findAll("");
        } else {
            this.searchByName();
        }
    }

    deleteConfirm(locationMasterId, selectedMvnoId, index) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.confirmationService.confirm({
                message: "Do you want to delete this record?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteById(locationMasterId, selectedMvnoId, index);
                    //  this.locationData=[];
                },
                //  this.locationData=[];
                reject: () => {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    concurrent = {
        locationMasterId: 0,
        name: "",
        noOfConcurrentConnections: "",
        status: "",
        mvnoName: ""
    };
    editlocationmasterData: {
        locationMasterId: any;
        name: any;
        noOfConcurrentConnections: any;
        status: any;
        mvnoName: any;
        checkItem: any;
    };

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        if (this.userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            //  this.reset();
            this.toastr.info(`You are not authorized to do this operation. Please contact to the administrator`, 'Info!');
            this.modalToggle = false;
            return false;
        }
        return true;
    }

    async getLocationDetail(policyId) {
        this.openLocationDetailsDialog(policyId)
        if (this.validateUserToPerformOperations(this.mvnoId)) {
            this.showDialogue = true;
            this.locationService.getLocationById(policyId).subscribe(
                (response: any) => {
                    this.showLocationDetails = response.locationMaster;
                },
                error => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed');
                }
            );
        }
    }

    locationMacModelOpen() {
        this.showLocationMac = true;
        this.locationMacDataSource.data = this.overLocationMacArray.controls;
        this.overChargeListtotalRecords = this.overLocationMacArray.controls.length;
    }

    locationMacModelClose() {
        this.showLocationMac = false;
        this.locationMacForm.reset();
        this.locationMacsubmitted = false;
        this.isMacExist = false;
    }

    addLocationMacListField() {
        this.locationMacsubmitted = true;
        if (this.locationMacForm.valid) {
            var index = this.overLocationMacArray.value.findIndex(
                x => x.mac == this.locationMacForm.value.mac
            );
            if (index === -1) {
                this.overLocationMacArray.push(this.createLocationMacListFormGroup());
                this.locationMacForm.reset();
                this.locationMacsubmitted = false;
                this.locationMacDataSource.data = this.overLocationMacArray.controls;
                this.overChargeListtotalRecords = this.overLocationMacArray.controls.length;

                if (this.locationMacPaginator) {
                    this.locationMacDataSource.paginator = this.locationMacPaginator;
                }
            } else {
                this.isMacExist = true;
            }
        }
    }

    keypdown(event: any) {
        if (this.locationMacForm.value.mac != "") {
            this.isMacExist = false;
        } else {
            this.isMacExist = true;
        }
    }

    createLocationMacListFormGroup(): UntypedFormGroup {
        return this.fb.group({
            identity: [this.locationMacForm.value.identity],
            mac: [this.locationMacForm.value.mac]
        });
    }

    deleteLocationMapField(locationMapFieldIndex: number) {
        let actualIndex = locationMapFieldIndex;
        if (this.locationMacPaginator) {
            const currentPage = this.locationMacPaginator.pageIndex;
            const pageSize = this.locationMacPaginator.pageSize;
            actualIndex = (currentPage * pageSize) + locationMapFieldIndex;
        }

        this.overLocationMacArray.removeAt(actualIndex);
        if (this.locationMacData != null && this.locationMacData.length > actualIndex) {
            this.locationMacData.splice(actualIndex, 1);
        }
        this.locationMacDataSource.data = this.overLocationMacArray.controls;
        this.overChargeListtotalRecords = this.overLocationMacArray.controls.length;
        if (this.locationMacPaginator) {
            this.locationMacDataSource.paginator = this.locationMacPaginator;
        }
    }

    saveLocationMacData() {
        this.locationMacData = this.overLocationMacArray.value;
        this.showLocationMac = false;
        this.dialogRef2.close();
    }

    onLocationMacDetailsPageChange(event: any) {
        const pageIndex = event.pageIndex;
        const pageSize = event.pageSize;

        this.locationMacDetailsDataSource.paginator = this.locationMacDetailsPaginator;
    }

    locationMacDetailsModelOpen() {
        this.showChargeDetails = true;

        this.locationMacDetailsDataSource.data = this.locationMacData;
        this.locationMacDetailsTotalRecords = this.locationMacData.length;

        this.dialogRef3 = this.dialog.open(this.locationMacDetailsDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef3.afterClosed().subscribe(result => {
            this.locationMacDetailsModelClose();
        });

        if (this.locationMacDetailsPaginator) {
            this.locationMacDetailsDataSource.paginator = this.locationMacDetailsPaginator;
        }
    }

    locationMacDetailsModelClose() {
        this.showChargeDetails = false;
        // Clear the details data source
        this.locationMacDetailsDataSource.data = [];
        this.locationMacDetailsTotalRecords = 0;
    }

    getMvnoNameAnfId() {
        const url = "/mvno/getMvnoNameAndIds";
        this.locationService.getMvnoNameAndIds(url).subscribe(
            (response: any) => {
                this.mvnoData = response.dataList.filter(item => item.id !== 1);
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed');
            }
        );
    }

    @ViewChild("locationDialog") locationDialog!: TemplateRef<any>;
    @ViewChild("locationMacDialog") locationMacDialog!: TemplateRef<any>;

    openlocationDialog(locations: any = null) {
        this.submitted = false;
        this.editMode = false;
        this.createForm.reset();
        this.overLocationMacArray = this.fb.array([]);
        this.locationMacData = [];

        this.dialogRef = this.dialog.open(this.locationDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.clearFormData();
        });
    }

    clearFormData() {
        this.submitted = false;
        this.editMode = false;
        this.createForm.reset();
        this.overLocationMacArray = this.fb.array([]);
        this.locationMacData = [];
        this.editConcurrentId = null;
        // Add only these 2 lines
        this.locationMacDataSource.data = [];
        this.overChargeListtotalRecords = 0;
    }

    openlocationMacDialog(branchManagement: any) {
        this.dialogRef2 = this.dialog.open(this.locationMacDialog, {
            width: "900px",
            disableClose: true
        });

        this.locationMacDataSource.data = this.overLocationMacArray.controls;
        this.overChargeListtotalRecords = this.overLocationMacArray.controls.length;

        if (this.locationMacPaginator) {
            this.locationMacDataSource.paginator = this.locationMacPaginator;
        }

        this.dialogRef2.afterClosed().subscribe(result => {
            this.locationMacForm.reset();
            this.locationMacsubmitted = false;
            this.isMacExist = false;
        });
    }

    onLocationMacPageChange(event: any) {
        this.currentPageoverChargeList = event.pageIndex;
        this.overChargeListItemPerPage = event.pageSize;

        if (this.locationMacPaginator) {
            this.locationMacDataSource.paginator = this.locationMacPaginator;
        }
    }

    @ViewChild("locationDialogContent") locationDialogContent!: TemplateRef<any>;

    openLocationDetailsDialog(locationId: any) {
        const dialogRef = this.dialog.open(this.locationDialogContent, {
            width: "900px",
            disableClose: false
        });

        dialogRef.afterClosed().subscribe(result => {
        });
    }
}
