import { Component, OnInit, TemplateRef, ViewChild, ViewEncapsulation } from "@angular/core";
import {
    UntypedFormBuilder,
    Validators,
    UntypedFormGroup,
    FormControl,
    FormArray,
    FormGroup
} from "@angular/forms";
import { ToastrService } from "ngx-toastr"; // Changed from MessageService to ToastrService
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { AreaManagementService } from "src/app/service/area-management.service";
import { Regex } from "src/app/constants/regex";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CityManagementService } from "src/app/service/city-management.service";
import { StateManagementService } from "src/app/service/state-management.service";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { MaterialModule } from "src/app/material.module";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
// import { TablerIconsModule } from 'angular-tabler-icons';
import { RouterModule } from "@angular/router";
// import { AppConfirmDeleteDialogComponent } from './confirm-delete-dialog.component';
// import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from "@angular/material/snack-bar";

// import { Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";


@Component({
    selector: "app-area-management",
    templateUrl: "./area-management.component.html",
    styleUrls: ["./area-management.component.css"],
    standalone: false,
    encapsulation: ViewEncapsulation.None
})
export class AreaManagementComponent implements OnInit {
    @ViewChild("areaDialog") areaDialog!: TemplateRef<any>;
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    areaFormGroup: UntypedFormGroup;
    areaListData: any;
    dialogRef2: any;
    pincodeListData: any;
    pincodeSelected: boolean = false;
    cityListData: any;
    countryListData: any;
    areaModal: boolean = false;
    stateListData: any;
    submitted: boolean = false;
    createAreaData: any;
    dataSource = new MatTableDataSource<any>([]);
    currentPageAreaListdata = 1;
    areaListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    areaListdatatotalRecords: any;
    viewAreaListData: any;
    isAreaEdit: boolean = false;
    pincode: any = {};
    pincodeDetail: any;
    deletedata: any = {
        id: "",
        cityId: "",
        cityName: "",
        code: "",
        countryId: "",
        countryName: "",
        name: "",
        pincodeId: "",
        stateId: "",
        stateName: "",
        status: "",
        pincode: ""
    };
    areaInputview: boolean = false;
    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    statusOptions = RadiusConstants.status;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    searchAreaName: any = "";
    areaparticularData: any = [];
    public loginService: LoginService;
    searchData: any;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService, // Changed from messageService to toastr
        private areaManagementService: AreaManagementService,
        private commondropdownService: CommondropdownService,
        private pincodemanagemnetService: PincodeManagementService,
        private statemanagementService: StateManagementService,
        private citymanagementservice: CityManagementService,
        private dialog: MatDialog,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.AREA_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.AREA_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.AREA_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isAreaEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.areaFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            countryId: ["", Validators.required],
            stateId: ["", Validators.required],
            cityId: ["", Validators.required],
            pincodeId: ["", Validators.required],
            status: ["", Validators.required]
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
            //  page: '',
            // pageSize: '',
        };
        // if (areaListData.id) {

        // }
        this.pincode = {};
        this.getStateList();
        this.getCityList();
        this.getCountryList();
        this.getPincodeList();
        this.getAreaList("");
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageAreaListdata > 1) {
            this.currentPageAreaListdata = 1;
        }
        if (!this.searchkey) {
            this.getAreaList(this.showItemPerPage);
        } else {
            this.searchArea();
        }
    }

    getAreaList(list) {
        let size;
        this.searchkey = "";
        let page_list = this.currentPageAreaListdata;
        if (list) {
            size = list;
            this.areaListdataitemsPerPage = list;
        } else {
            size = this.areaListdataitemsPerPage;
        }
        const url = "/area";
        let areadata = {
            page: page_list,
            pageSize: size
        };
        this.areaListData = [];
        this.areaManagementService.postMethod(url, areadata).subscribe(
            (response: any) => {
                if (response.responseCode == 204) {
                    this.dataSource = new MatTableDataSource<any>([]); // Empty array instead of wrong data
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.areaListData = response.dataList;
                    this.dataSource = new MatTableDataSource<any>(response.dataList); // Add this line
                    this.areaListdatatotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    searchArea() {
        if (!this.searchkey || this.searchkey !== this.searchAreaName) {
            this.currentPageAreaListdata = 1;
        }
        this.searchkey = this.searchAreaName;
        if (this.showItemPerPage) {
            this.pageITEM = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchAreaName.trim();
        //   this.searchData.page = this.currentPageAreaListdata;
        //   this.searchData.pageSize = this.areaListdataitemsPerPage;
        const url =
            "/area/search?page=" +
            this.currentPageAreaListdata +
            "&pageSize=" +
            this.pageITEM +
            "&sortBy=id&sortOrder=0";

        this.areaManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.dataSource = new MatTableDataSource<any>([]); // Empty array instead of wrong data
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.areaListData = [];
                    this.areaListdatatotalRecords = 0;
                } else {
                    this.areaListData = response.dataList;
                    this.dataSource = new MatTableDataSource<any>(response.dataList); // Add this line
                    this.areaListdatatotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    clearSearchArea() {
        this.searchAreaName = "";
        this.getAreaList("");
        this.submitted = false;
        this.isAreaEdit = false;
        this.areaInputview = false;
        this.areaFormGroup.reset();
    }

    getPincodeList() {
        // const url = "/pincode/all";
        const url = "/pincode/getAll";
        this.pincodemanagemnetService.getMethod(url).subscribe(
            (response: any) => {
                this.pincodeListData = response.dataList.filter(pincode => pincode.status == "Active");
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getCityList() {
        const url = "/city/all";
        this.citymanagementservice.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getCountryList() {
        const url = "/country/all";
        this.areaManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getStateList() {
        const url = "/state/all";
        this.statemanagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    addEditArea(areaId) {
        this.submitted = true;
        const rawForm = this.areaFormGroup.getRawValue();
        if (this.areaFormGroup.valid) {
            if (areaId) {
                // this.getPincodeById(this.areaFormGroup.controls.pincodeId.value);
                // setTimeout(() => {
                this.areaFormGroup.get('pincodeId')?.enable();
                this.areaFormGroup.get('cityId')?.disable();
                this.areaFormGroup.get('stateId')?.disable();
                this.areaFormGroup.get('countryId')?.disable();

                this.createAreaData = this.areaFormGroup.value;
                this.createAreaData = { ...rawForm };
                this.createAreaData.id = areaId;
                this.createAreaData.pincode = this.pincode;

                const url = "/area/update";
                this.areaManagementService.postMethod(url, this.createAreaData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406 || response.responseCode == 417) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.isAreaEdit = false;
                            this.areaInputview = false;
                            this.areaFormGroup.reset();
                            // this.commondropdownService.clearCache("/area/all");
                            this.toastr.success(`Successfully Updated`, 'Success!');
                            if (this.dialogRef2) {
                                this.dialogRef2.close();
                            }
                            if (!this.searchkey) {
                                this.getAreaList("");
                            } else {
                                this.searchArea();
                            }
                            this.clearSearchArea();
                        }
                        this.clearSearchArea();
                        // this.dialogRef2().afterClosed();
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
                if (!this.isAreaEdit) {
                    this.areaFormGroup.reset();
                    // this.areaFormGroup.get('cityId')?.enable();
                    // this.areaFormGroup.get('stateId')?.enable();
                    // this.areaFormGroup.get('countryId')?.enable();
                }

                // }, 3000);
            } else {
                // this.getPincodeById(this.areaFormGroup.controls.pincodeId.value);
                // setTimeout(() => {

                this.createAreaData = this.areaFormGroup.value;
                this.createAreaData = { ...rawForm };
                this.createAreaData.pincode = this.pincode;
                // emove  this.createAreaData.pincodeId
                const url = "/area/save";
                this.areaManagementService.postMethod(url, this.createAreaData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.areaInputview = false;
                            this.areaFormGroup.reset();
                            // this.commondropdownService.clearCache("/area/all");
                            this.toastr.success(`${response.responseMessage}`, 'Success!');

                            if (this.dialogRef2) {
                                this.dialogRef2.close();
                            }

                            if (!this.searchkey) {
                                this.getAreaList("");
                            } else {
                                this.searchArea();
                            }
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
                // }, 3000);
            }
        }
    }

    editArea(areaId) {
        this.areaFormGroup.get('cityId')?.disable();
        this.areaFormGroup.get('stateId')?.disable();
        this.areaFormGroup.get('countryId')?.disable();

        if (areaId) {
            this.isAreaEdit = true;
            this.pincodeSelected = true;
            this.areaInputview = true;
            // this.getAreaById(areaId);
            // setTimeout(() => {
            //   this.areaFormGroup.patchValue(this.viewAreaListData);
            // }, 1000);

            const url = "/area/" + areaId;
            this.areaManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.viewAreaListData = response.data;
                    this.deletedata = this.viewAreaListData;
                    this.areaFormGroup.patchValue(this.viewAreaListData);
                    this.getPincodeById(this.areaFormGroup.controls.pincodeId.value);
                    this.openAddEditDialog(areaId);
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    async getAreaById(areaId) {
        const url = "/area/" + areaId;
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.viewAreaListData = response.data;
                this.deletedata = this.viewAreaListData;
                this.areaInputview = true;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getPincodeById(pincodeId) {
        if (pincodeId) {
            //
            const url = "/pincode/" + pincodeId;

            this.areaManagementService.getMethod(url).subscribe(
                (response: any) => {
                    // console.log("pinresponse", response)
                    this.pincode.pincodeid = response.data.pincodeid;
                    this.pincode.pincode = response.data.pincode;
                    this.pincode.status = response.data.status;
                    this.pincode.isDeleted = response.data.isDeleted;
                    //
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    getSelPincode(event) {
        const selPincode = event.value;
        this.getPincodeDetailbyId(selPincode);
    }

    getPincodeDetailbyId(pincodeId) {
        const url = "/pincode/" + pincodeId;
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.pincodeDetail = response.data;
                // return
                this.areaInputview = true;
                this.areaFormGroup.controls["countryId"].disable();
                this.areaFormGroup.controls["stateId"].disable();
                this.areaFormGroup.controls["cityId"].disable();

                this.areaFormGroup.controls.countryId.patchValue(this.pincodeDetail.countryId);
                this.areaFormGroup.controls.stateId.patchValue(this.pincodeDetail.stateId);
                this.areaFormGroup.controls.pincodeId.patchValue(this.pincodeDetail.pincodeid);
                this.areaFormGroup.controls.cityId.patchValue(this.pincodeDetail.cityId);

                // (Optional) keep pincode disabled if you don't want to edit it
                this.areaFormGroup.controls["pincodeId"].enable();

                this.pincode = {
                    pincodeid: response.data.pincodeid,
                    pincode: response.data.pincode,
                    status: response.data.status,
                    isDeleted: response.data.isDeleted
                };
                // this.pincode.pincodeid = response.data.pincodeid;
                // this.pincode.pincode = response.data.pincode;
                // this.pincode.status = response.data.status;
                // this.pincode.isDeleted = response.data.isDeleted;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    canExit() {
        if (!this.areaFormGroup.dirty) return true;
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

    @ViewChild("deleteDialog") deleteDialog!: TemplateRef<any>;
    //   dialogRef!: MatDialogRef<any>;
    private dialogRef!: MatDialogRef<any>;
    private deleteAreaId: number | null = null;

    onCancel(): void {
        this.dialogRef.close(false);
    }

    // Confirm button click
    onConfirm(): void {
        this.dialogRef.close(true);
    }

    //     deleteConfirmonAreaDialog(item: any) { 
    //     const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
    //       width: "400px",
    //       data: {
    //         title: "Delete State",
    //         description: `Are you sure you want to delete "${item.name}"?`,
    //         yesLabel: "Delete",
    //         noLabel: "Cancel"
    //       }
    //     });

    //     dialogRef.afterClosed().subscribe(result => {
    //       if (result) {
    //         this.deleteArea(item.id);
    //       } else {
    //         console.log("Delete cancelled");
    //       }
    //     });
    //   }

    deleteConfirmonArea(area) {
        this.getAreaById(area.id);
        if (area) {

            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                data: {
                    title: "Delete State",
                    description: `Are you sure you want to delete "${area.name}"?`,
                    yesLabel: "Delete",
                    noLabel: "Cancel"
                }
            });
            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteArea(area.id);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    filteredPincodeList: any[] = [];

    filterPincode(searchText: string) {
        const value = searchText.toLowerCase();
        this.filteredPincodeList = this.pincodeListData.filter((pin: any) =>
            pin.pincode.toLowerCase().includes(value)
        );
    }

    deleteArea(areaId) {
        const url = "/area/delete";
        //console.log("this.createQosPolicyData", this.deletedata);
        this.areaManagementService.postMethod(url, this.deletedata).subscribe(
            (response: any) => {
                if (this.currentPageAreaListdata != 1 && this.totalAreaListLength == 1) {
                    this.currentPageAreaListdata = this.currentPageAreaListdata - 1;
                }
                if (response.responseCode == 405 || response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
                this.clearSearchArea();
                if (!this.searchkey) {
                    //   this.getAreaList("");
                } else {
                    this.searchArea();
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.responseMessage}`, 'Failed!');
            }
        );
    }

    pageChangedAreaList(event: PageEvent) {
        this.currentPageAreaListdata = event.pageIndex + 1;
        this.areaListdataitemsPerPage = event.pageSize;
        if (!this.searchkey) {
            this.getAreaList("");
        } else {
            this.searchArea();
        }
    }

    areaDataOpenModel(data) {
        this.areaparticularData = data;
        this.areaModal = true;
    }

    displayedColumns = ["id", "Name", "Pincode", "OLT", "Sub-County", "Status", "Action"];

    @ViewChild("areaDetailsDialog") areaDetailsDialog!: TemplateRef<any>;
    @ViewChild("areaFormDialog") areaFormDialog!: TemplateRef<any>;
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    closeAreaModal() {
        this.areaModal = false;
    }

    openAddEditDialog(area: any): void {
        this.pincodeSelected = false;
        this.areaparticularData = area;

        this.dialogRef2 = this.dialog.open(this.areaFormDialog, {
            width: "900px",
            disableClose: true
        });

        if (!this.isAreaEdit) {
            this.areaFormGroup.reset();
            this.areaFormGroup.get('pincodeId')?.enable();
            this.areaFormGroup.get('cityId')?.enable();
            this.areaFormGroup.get('stateId')?.enable();
            this.areaFormGroup.get('countryId')?.enable();
        }

        this.dialogRef2.afterClosed().subscribe(result => {
            this.clearSearchArea();
        });
    }

    deleteConfirmonAreaDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: "Delete State",
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteArea(item.id);
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });
    }

    openAreaDetailsDialog(area: any): void {
        this.areaparticularData = area;
        this.dialog.open(this.areaDetailsDialog, {
            width: "800px",
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
    }

    getDropdownHeight(): string {
        if (!this.pincodeListData || this.pincodeListData.length === 0) {
            return "0px";
        }

        const itemSize = 30;
        const maxItemsToShow = 6;

        return this.pincodeListData.length > maxItemsToShow
            ? `${maxItemsToShow * itemSize}px`
            : `${this.pincodeListData.length * itemSize}px`;
    }

    onPincodeSelectionChange(event: any): void {
        this.getSelPincode(event); // Keep your existing functionality
        this.pincodeSelected = !!event.value;

        // Auto-populate location fields when pincode is selected in add mode
        if (!this.isAreaEdit && this.pincodeSelected) {
            const selectedPincode = this.pincodeListData.find(p => p.pincodeid === event.value);
            if (selectedPincode) {
                this.areaFormGroup.patchValue({
                    cityId: selectedPincode.cityId,
                    stateId: selectedPincode.stateId,
                    countryId: selectedPincode.countryId
                });
            }
        }
    }

    shouldDisablePincodeField(): boolean {
        return this.isAreaEdit && this.areaFormGroup.get('pincodeId')?.value;
    }

    shouldDisableLocationFields(): boolean {
        if (this.isAreaEdit) {
            return !!this.areaFormGroup.get('pincodeId')?.value;
        }
        return this.pincodeSelected;
    }
}
