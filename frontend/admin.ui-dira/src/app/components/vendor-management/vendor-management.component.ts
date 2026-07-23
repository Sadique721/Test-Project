import { error } from "console";
import { Component, OnInit, TemplateRef, ViewChild, ViewEncapsulation } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CountryManagementService } from "src/app/service/country-management.service";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY } from "src/app/RadiusUtils/RadiusConstants";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { Observable, Observer } from "rxjs";
import { resolve } from "dns";
//import { ObserversModule } from "@angular/cdk/observers";
//simport { SectorManagement } from "../model/SectorManagement";
import { VendorManagment } from "../model/vendorManagment";
import { VendorManagementService } from "src/app/service/vendor-management.service";
import { INVENTORYS } from "src/app/constants/aclConstants";

import { ObserversModule } from "@angular/cdk/observers";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-vendor-management",
    templateUrl: "./vendor-management.component.html",
    styleUrls: ["./vendor-management.component.css"],
    standalone: false
})
export class VendorManagementComponent implements OnInit {
    title = "Manufacturer";
    title1 = "Manufacturer";
    vendorFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    vendorData: VendorManagment;
    vendorListData: any;
    isVendorEdit: boolean = false;
    viewVendorListData: any;
    currentPageVendorSlab = 1;
    vendoritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    vendortotalRecords: any;
    searchVendorName: any = "";
    searchData: any;
    AclClassConstants;
    AclConstants;
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any = 5;
    dataSource = new MatTableDataSource<any>();
    areaModal: boolean = false;

    pageSizeOptions = [5,10,20,50,100]; // Fixed array for page size options
    areaparticularData: any = [];
    dialogRef: any;

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    displayedColumns = ['id', 'Name', 'Status', 'Action'];

    searchkey: string;
    public loginService: LoginService;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private vendorManagementService: VendorManagementService,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(INVENTORYS.MANUFACTURER_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.MANUFACTURER_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.MANUFACTURER_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.pageLimitOptions = RadiusConstants.pageLimitOptions;
        // this.isVendorEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.vendorFormGroup = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            id: [""],
            mvnoId: [""],
        });

        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
            //   page: "",
            //   pageSize: "",
        };
        this.getVendorListData("");
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.MANUFACTURER_EDIT) || this.loginService.hasPermission(INVENTORYS.MANUFACTURER_DELETE)) {
            return ['id', 'Name', 'Status', 'Action'];
        } else {
            return ['id', 'Name', 'Status'];
        }
    }

    areaDataOpenModel(data) {
        this.areaparticularData = data;
        this.areaModal = true;
    }
    closeAreaModal() {
        this.areaModal = false;
    }

    canExit() {
        if (!this.vendorFormGroup.dirty) return true;
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

    onPageChange(event: PageEvent) {
        this.vendoritemsPerPage = event.pageSize;
        this.currentPageVendorSlab = event.pageIndex + 1; // MatPaginator uses 0-based index

        if (this.searchkey) {
            this.searchVendor();
        } else {
            this.getVendorListData("");
        }
    }

    /** Create and Edit Vendor */
    addEditVendor(id) {
        this.submitted = true;
        if (this.vendorFormGroup.valid) {
            if (id) {
                const url = "/vendor/update";
                this.vendorData = this.vendorFormGroup.value;
                this.vendorData.id = id;
                this.vendorData.isDelete = false;
                this.vendorManagementService.postMethod(url, this.vendorData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isVendorEdit = false;
                        this.vendorFormGroup.reset();
                        this.vendorFormGroup.controls.status.setValue("");
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');
                            if (this.dialogRef) {
                                this.clearSearchVendor();
                                this.dialogRef.close();
                            }
                        }
                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchVendor();
                        } else {
                            this.getVendorListData("");
                        }
                    },

                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }

                );
            } else {
                const url = "/vendor/save";
                this.vendorData = this.vendorFormGroup.value;
                // this.vendorData.delete = false;
                this.vendorData.isDelete = false;
                this.vendorManagementService.postMethod(url, this.vendorData).subscribe((response: any) => {
                    this.submitted = false;
                    this.vendorFormGroup.reset();
                    this.vendorFormGroup.controls.status.setValue("");
                    if (this.searchkey) {
                        this.searchVendor();
                    } else {
                        this.getVendorListData("");
                    }

                    if (response.responseCode == 406) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');
                    } else if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');
                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                    }
                });
                if (this.dialogRef) {
                    this.dialogRef.close();
                }
            }
        }
    }

    /** Total Item Per Page */
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageVendorSlab > 1) {
            this.currentPageVendorSlab = 1;
        }
        if (!this.searchkey) {
            this.getVendorListData(this.showItemPerPage);
        } else {
            this.searchVendor();
        }
    }

    /** Get All Vendor with Pagination */
    getVendorListData(list) {
        const url = "/vendor/getAllVendor";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageVendorSlab;
        if (list) {
            size = list;
            this.vendoritemsPerPage = list;
        } else {
            size = this.vendoritemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
        };
        this.vendorManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.vendorListData = response.dataList;
                this.vendortotalRecords = response.totalRecords;
                // console.log( "sectortotalRecords",this.sectortotalRecords);

                // Update the data source with the new data
                this.dataSource.data = this.vendorListData;
                this.searchkey = "";
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    /** Get Vendor by Id */
    editVendor(sectorId) {
        if (sectorId) {
            this.isVendorEdit = true;
            this.openvendorManagementDialog(sectorId);
            const url = "/vendor/getById?id=" + sectorId;
            this.vendorManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.viewVendorListData = response.data;
                    this.vendorFormGroup.patchValue(this.viewVendorListData);
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    /** Serch Vendor */
    searchVendor() {
        if (!this.searchkey || this.searchkey !== this.searchVendorName) {
            this.currentPageVendorSlab = 1;
        }
        this.searchkey = this.searchVendorName;
        if (this.showItemPerPage) {
            this.vendoritemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchVendorName.trim();
        const page = {
            page: this.currentPageVendorSlab,
            pageSize: this.vendoritemsPerPage,
        };
        this.vendorManagementService.searchvendor(page, this.searchData).subscribe(
            (response: any) => {
                this.vendorListData = response.dataList;

                if (response.responseCode == 200) {
                    this.vendorListData = response.dataList;
                    this.vendortotalRecords = response.totalRecords;
                    this.dataSource.data = this.vendorListData;
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.vendorListData = [];
                    this.vendortotalRecords = 0;
                    this.dataSource.data = [];
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    /** Clear */
    clearSearchVendor() {
        this.searchVendorName = "";
        this.searchkey = "";
        this.getVendorListData("");
        this.submitted = false;
        this.isVendorEdit = false;
        this.vendorFormGroup.reset();
        this.vendorFormGroup.controls.status.setValue("");
    }

    /** Delete Conformation */
    //   deleteConfirmonSector(id: number) {
    //     if (id) {
    //       this.confirmationService.confirm({
    //         message: "Do you want to delete this " + this.title + "?",
    //         header: "Delete Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //           this.deleteSector(id);
    //         },
    //         reject: () => {
    //           this.toastr.info("You have rejected", 'Info!');
    //         },
    //       });
    //     }
    //   }
    /** Delete Vendor */

    deleteConfirmonSector(id) {
        if (id) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${id.name}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteSector(id);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    deleteSector(data) {
        const url = "/vendor/delete/" + data.id;
        this.vendorManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
                this.getVendorListData("");
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');
            }
        );
    }

    /** Page Change */
    pageChangedCasList(pageNumber) {
        this.currentPageVendorSlab = pageNumber;
        if (this.searchkey) {
            this.searchVendor();
        } else {
            this.getVendorListData("");
        }
    }

    //   @ViewChild(MatSort) sort: MatSort = Object.create(null);
    //   @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    @ViewChild("vendorManagementDialog") vendorManagementDialog!: TemplateRef<any>;

    openvendorManagementDialog(branchManagement: any) {
        this.areaparticularData = branchManagement;
        this.dialogRef = this.dialog.open(this.vendorManagementDialog, {
            width: "900px",
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(result => {
            this.clearSearchVendor();
        });
    }
}
