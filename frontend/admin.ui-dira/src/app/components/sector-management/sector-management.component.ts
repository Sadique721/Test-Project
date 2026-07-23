import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
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
import { SectorManagement } from "../model/SectorManagement";
import { SectorManagementService } from "src/app/service/sector-management.service";
import { DTVS } from "src/app/constants/aclConstants";
import { ToastrService } from 'ngx-toastr';
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";


@Component({
    selector: "app-sector-management",
    templateUrl: "./sector-management.component.html",
    styleUrls: ["./sector-management.component.css"],
    standalone: false
})
export class SectorManagementComponent implements OnInit, IDeactivateGuard {
    sectorDataSource = new MatTableDataSource<any>();
    displayedColumns: string[] = ['Id', 'Name', 'Status', 'Action'];
    pageSizeOptions = [5, 10, 25, 50, 100];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    dialogRef: any;
    title = "Sector";
    sectorFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    sectorData: SectorManagement;
    sectorListData: any;
    isSectorEdit: boolean = false;
    viewSectorListData: any;
    currentPageSectorSlab = 1;
    sectoritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    sectortotalRecords: any;
    searchSectorName: any = "";
    searchData: any;
    AclClassConstants;
    AclConstants;

    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    createSectorAccess = false;
    editSectorAccess = false;
    deleteSectorAccess = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;

    public loginService: LoginService;
    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private toastr: ToastrService,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private countryManagementService: CountryManagementService,
        private sectorManagementService: SectorManagementService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.createAccess = loginService.hasPermission(DTVS.SECTOR_CREATE);
        this.deleteAccess = loginService.hasPermission(DTVS.SECTOR_DELETE);
        this.editAccess = loginService.hasPermission(DTVS.SECTOR_EDIT);

        // this.isSectorEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.sectorFormGroup = this.fb.group({
            sname: ["", Validators.required],
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
            page: "",
            pageSize: "",
        };
        this.getSectorListData("");
    }

    ngAfterViewInit() {
        // this.sectorDataSource.paginator = this.paginator;
        // this.sectorDataSource.sort = this.sort;
        if (this.paginator) {
            this.paginator.page.subscribe((pageEvent: PageEvent) => {
                this.onSectorPageChange(pageEvent);
            });
        }
    }
    canExit() {
        if (!this.sectorFormGroup.dirty) return true;
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

    addEditSector(id) {
        this.submitted = true;
        if (this.sectorFormGroup.valid) {
            if (id) {
                const url = "/sectormaster/update";
                this.sectorData = this.sectorFormGroup.value;
                this.sectorData.id = id;
                this.sectorData.isDelete = false;
                this.sectorManagementService.updateMethod(url, this.sectorData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isSectorEdit = false;
                        this.sectorFormGroup.reset();
                        this.sectorFormGroup.controls.status.setValue("");
                        this.toastr.success(`Successfully Updated`, 'Success!')
                        // this.messageService.add({
                        //   severity: "success",
                        //   summary: "Successfully",
                        //   detail: response.message,
                        //   icon: "far fa-check-circle",
                        // });
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchSector();
                        } else {
                            this.getSectorListData("");
                        }
                    },

                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: error.error.ERROR,
                        //   icon: "far fa-times-circle",
                        // });
                    }
                );
            } else {
                const url = "/sectormaster/save";
                this.sectorData = this.sectorFormGroup.value;
                this.sectorData.delete = false;
                this.sectorData.isDelete = false;
                this.sectorManagementService.postMethod(url, this.sectorData).subscribe((response: any) => {
                    this.submitted = false;
                    this.sectorFormGroup.reset();
                    this.sectorFormGroup.controls.status.setValue("");
                    if (this.dialogRef) {
                        this.dialogRef.close();
                    }
                    if (this.searchkey) {
                        this.searchSector();
                    } else {
                        this.getSectorListData("");
                    }

                    if (response.responseCode !== 200) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: response.responseMessage,
                        //   icon: "far fa-times-circle",
                        // });
                    } else {
                        this.toastr.success(`Successfully Created`, 'Success!')
                        // this.messageService.add({
                        //   severity: "success",
                        //   summary: "Successfully",
                        //   detail: response.message,
                        //   icon: "far fa-check-circle",
                        // });
                    }
                });
            }
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.currentPageSectorSlab = 1; // Reset to first page
        this.sectoritemsPerPage = this.showItemPerPage;

        if (!this.searchkey) {
            this.getSectorListData(this.showItemPerPage);
        } else {
            this.searchSector();
        }
    }

    onSectorPageChange(event: PageEvent) {
        this.currentPageSectorSlab = event.pageIndex + 1;
        this.sectoritemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchSector();
        } else {
            this.getSectorListData("");
        }
    }

    getSectorListData(list) {
        const url = "/sectormaster";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageSectorSlab;
        if (list) {
            size = list;
            this.sectoritemsPerPage = list;
        } else {
            size = this.sectoritemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
        };
        this.sectorManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.sectorDataSource.data = response.dataList;
                this.sectorListData = response.dataList;
                this.sectortotalRecords = response.totalRecords;
                // console.log( "sectortotalRecords",this.sectortotalRecords);
                if (this.paginator) {
                    this.paginator.length = this.sectortotalRecords;
                    this.paginator.pageIndex = this.currentPageSectorSlab - 1;
                }
                this.searchkey = "";
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    editSector(sectorId) {
        if (sectorId) {
            const url = "/sectormaster/" + sectorId;
            this.sectorManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isSectorEdit = true;
                    this.viewSectorListData = response.data;
                    // console.log(" this.viewCountryListData", this.viewCountryListData);
                    this.sectorFormGroup.patchValue(this.viewSectorListData);
                    this.openSectorManagementDialog(sectorId);

                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    //   this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle",
                    //   });
                }
            );
        }
    }

    searchSector() {
        if (!this.searchkey || this.searchkey !== this.searchSectorName) {
            this.currentPageSectorSlab = 1;
        }
        this.searchkey = this.searchSectorName;
        if (this.showItemPerPage) {
            this.sectoritemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchSectorName.trim();
        this.searchData.page = this.currentPageSectorSlab;
        this.searchData.pageSize = this.sectoritemsPerPage;
        const url =
            "/sectormaster/search?page=" +
            this.currentPageSectorSlab +
            "&pageSize=" +
            this.sectoritemsPerPage +
            "&sortBy=id&sortOrder=0";
        this.sectorManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.sectorDataSource.data = response.dataList
                this.sectorListData = response.dataList;
                this.sectortotalRecords = response.totalRecords;
                if (this.paginator) {
                    this.paginator.length = this.sectortotalRecords;
                }
            },
            (error: any) => {
                this.sectortotalRecords = 0;
                this.sectorDataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!')
                    //   this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle",
                    //   });
                    this.sectorListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    //   this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle",
                    //   });
                }
            }
        );
    }

    clearSearchSector() {
        this.searchSectorName = "";
        this.searchkey = "";
        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }
        this.currentPageSectorSlab = 1;
        this.getSectorListData("");
        this.submitted = false;
        this.isSectorEdit = false;
        this.sectorFormGroup.reset();
        this.sectorFormGroup.controls.status.setValue("");
    }

    deleteConfirmonSector(sector: any) {
        if (sector) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${sector.sname}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteSector(sector);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }
    deleteSector(data) {
        const url = "/sectormaster/delete";
        this.sectorManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                // UPDATE THIS LOGIC
                if (this.currentPageSectorSlab > 0 && this.sectorListData.length === 1) {
                    this.currentPageSectorSlab = this.currentPageSectorSlab - 1;
                    if (this.paginator) {
                        this.paginator.pageIndex = this.currentPageSectorSlab;
                    }
                }

                if (this.searchkey) {
                    this.searchSector();
                } else {
                    this.getSectorListData("");
                }

                this.toastr.success(`Successfully Deleted`, 'Success!');
            },
            // ... error handling
        );
    }
    pageChangedCasList(pageNumber) {
        this.currentPageSectorSlab = pageNumber;
        if (this.searchkey) {
            this.searchSector();
        } else {
            this.getSectorListData("");
        }
    }
    @ViewChild("SectorManagementDialog") SectorManagementDialog!: TemplateRef<any>;

    openSectorManagementDialog(SectorManagement: any) {
        this.dialogRef = this.dialog.open(this.SectorManagementDialog, {
            width: "900px",
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(result => {
            this.clearSearchSector();
        });
    }

}
