import { Component, OnInit, ViewChild, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CountryManagementService } from "../../service/country-management.service";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { LoginService } from "../../service/login.service";
import { AclClassConstants } from "../../constants/aclClassConstants";
import { AclConstants } from "../../constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-sub-buisness-unit",
    templateUrl: "./sub-buisness-unit.component.html",
    styleUrls: ["./sub-buisness-unit.component.css"],
    standalone: false
})
export class SubBuisnessUnitComponent implements OnInit {

    displayedColumns: string[] = ['id', 'Name', 'Code', 'Status'];
    dataSource = new MatTableDataSource<any>();

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    pageSizeOptions = [5,10,20,50,100];

    businessUnitFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    businessUnitData: any;
    businessUnitListData: any;
    isEdit: boolean = false;
    viewListData: any;
    dialogRef: MatDialogRef<any> | null = null;

    currentPageSlab = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;
    searchName: any = "";
    searchData: any;
    AclClassConstants;
    AclConstants;
    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    BUData: any = [];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService,
        private countryManagementService: CountryManagementService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.SUB_BUSINESS_UNIT_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.SUB_BUSINESS_UNIT_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.SUB_BUSINESS_UNIT_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.businessUnitFormGroup = this.fb.group({
            subbuname: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            subbucode: ["", Validators.required],
            businessunitid: ["", Validators.required],
            status: ["", Validators.required],
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
        };
        this.initializeDisplayedColumns();
        this.getBusinessUnit();
        this.getListData("");
    }

    private initializeDisplayedColumns(): void {
        this.displayedColumns = ['id', 'Name', 'Code', 'Status'];
        if (this.deleteAccess || this.editAccess) {
            this.displayedColumns.push('Action');
        }
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        this.paginator.page.subscribe((event: PageEvent) => {
            this.onPageChange(event);
        });
    }

    getBusinessUnit() {
        const url = "/businessUnit/all";
        this.countryManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.BUData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    addEdit(id) {
        this.submitted = true;
        if (this.businessUnitFormGroup.valid) {
            if (id) {
                const url = "/subbusinessunit/update";
                this.businessUnitData = this.businessUnitFormGroup.value;
                this.businessUnitData.id = id;
                this.countryManagementService.postMethod(url, this.businessUnitData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406 || response.responseCode == 417) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.isEdit = false;
                            this.businessUnitFormGroup.reset();
                            this.businessUnitFormGroup.controls.status.setValue("");
                            this.toastr.success(`Successfully updated`, 'Success!');
                            this.submitted = false;
                            if (this.dialogRef) {
                                this.dialogRef.close();
                                this.dialogRef = null;
                            }
                            if (this.searchkey) {
                                this.search();
                            } else {
                                this.getListData("");
                            }
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {
                const url = "/subbusinessunit/save";
                this.businessUnitData = this.businessUnitFormGroup.value;
                this.countryManagementService.postMethod(url, this.businessUnitData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.businessUnitFormGroup.reset();
                            this.businessUnitFormGroup.controls.status.setValue("");
                            this.toastr.success(`${response.responseMessage}`, 'Success!');

                            if (this.dialogRef) {
                                this.dialogRef.close();
                                this.dialogRef = null;
                            }

                            if (this.searchkey) {
                                this.search();
                            } else {
                                this.getListData("");
                            }
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            }
        }
    }

    TotalItemPerPage(event: any) {
        this.showItemPerPage = Number(event.value);
        this.itemsPerPage = this.showItemPerPage;

        if (this.currentPageSlab > 1) {
            this.currentPageSlab = 1;
        }

        if (!this.searchkey) {
            this.getListData(this.showItemPerPage);
        } else {
            this.search();
        }
    }

    getListData(list) {
        let size;
        this.searchkey = "";
        let pageList = this.currentPageSlab;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }

        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = "";
        const url = `/subbusinessunit/search?page=${this.currentPageSlab}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
        this.countryManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.businessUnitListData = response.dataList;
                this.totalRecords = response.totalRecords;
                this.dataSource = new MatTableDataSource(this.businessUnitListData);

                if (this.paginator) {
                    this.paginator.length = this.totalRecords;
                    this.paginator.pageSize = this.itemsPerPage;
                    this.paginator.pageIndex = this.currentPageSlab - 1;
                }

                this.searchkey = "";
            },
            (error: any) => {
                this.businessUnitListData = [];
                this.totalRecords = 0;
                this.dataSource = new MatTableDataSource([]);
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    edit(id, index) {
        if (id) {
            this.openSubBusinessDialog(id);
            const item = this.businessUnitListData.find(bu => bu.id === id);
            if (item) {
                this.viewListData = item;
                this.businessUnitFormGroup.patchValue(this.viewListData);
                this.isEdit = true;
            }
        }
    }

    getRowIndex(businessUnit: any): number {
        return this.businessUnitListData.indexOf(businessUnit);
    }

    search() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPageSlab = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName.trim();

        const url = `/subbusinessunit/search?page=${this.currentPageSlab}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
        this.countryManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.businessUnitListData = [];
                    this.totalRecords = response.totalRecords;
                    this.dataSource.data = [];
                } else {
                    this.businessUnitListData = response.dataList;
                    this.totalRecords = response.totalRecords;
                    this.dataSource.data = this.businessUnitListData;
                }
                if (this.paginator) {
                    this.paginator.length = this.totalRecords;
                    if (this.currentPageSlab === 1) {
                        this.paginator.firstPage();
                    }
                }
            },
            (error: any) => {
                this.totalRecords = 0;
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.businessUnitListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearSearch() {
        this.searchName = "";
        this.searchkey = "";
        this.currentPageSlab = 1;
        this.getListData("");
        this.submitted = false;
        this.isEdit = false;
        this.businessUnitFormGroup.reset();
        this.businessUnitFormGroup.controls.status.setValue("");

        if (this.paginator) {
            this.paginator.firstPage();
        }
    }

    onPageChange(event: PageEvent) {
        this.currentPageSlab = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.search();
        } else {
            this.getListData("");
        }
    }

    canExit() {
        if (!this.businessUnitFormGroup.dirty) return true;
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

    deleteConfirmon(data) {
        if (data.id) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${data.subbuname}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.delete(data);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    delete(data) {
        const url = "/subbusinessunit/delete";
        this.countryManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (this.currentPageSlab != 1 && this.businessUnitListData.length == 1) {
                    this.currentPageSlab = this.currentPageSlab - 1;
                }
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                }
                this.clearSearch();
                if (this.searchkey) {
                    this.search();
                } else {
                    this.getListData("");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedList(pageNumber) {
        this.currentPageSlab = pageNumber;
        if (this.searchkey) {
            this.search();
        } else {
            this.getListData("");
        }
    }

    @ViewChild("subBusinessDialog") subBusinessDialog!: TemplateRef<any>;

    openSubBusinessDialog(id?: any) {
        if (this.dialogRef) {
            return;
        }

        this.dialogRef = this.dialog.open(this.subBusinessDialog, {
            width: "900px",
            disableClose: true
        });

        if (id) {
            this.edit(id, null);
        } else {
            this.isEdit = false;
            this.businessUnitFormGroup.reset();
            this.businessUnitFormGroup.controls.status.setValue("");
            this.submitted = false;
        }

        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef = null;
            this.clearSearch();
        });
    }
}
