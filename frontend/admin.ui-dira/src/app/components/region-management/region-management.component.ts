import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { StateManagementService } from "src/app/service/state-management.service";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer } from "rxjs";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-region-management",
    templateUrl: "./region-management.component.html",
    styleUrls: ["./region-management.component.css"],
    standalone: false
})
export class RegionManagementComponent implements OnInit {

    displayedColumns: string[] = ['id', 'Name', 'Status', 'Action'];
    dataSource = new MatTableDataSource<any>();
    pageSizeOptions = [5,10,20,50,100];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    dialogRef: any;

    reginFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    regionListData: any = [];
    viewRegionListData: any = [];
    currentPageReginListdata = 1;
    ReginListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ReginListdatatotalRecords: any;
    isReginEdit: boolean = false;
    searchData: any;
    searchReginName: any = "";
    AclClassConstants;
    AclConstants;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    statusOptions = RadiusConstants.status;
    public loginService: LoginService;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService,
        private stateManagementService: StateManagementService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.REGION_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.REGION_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.REGION_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.reginFormGroup = this.fb.group({
            rname: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            branchid: [""],
            id: [""],
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

        this.getRegionList("");
        this.commondropdownService.getAllActiveBranch();
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.ReginListdataitemsPerPage = this.showItemPerPage;
        if (this.currentPageReginListdata > 1) {
            this.currentPageReginListdata = 1;
        }
        if (!this.searchkey) {
            this.getRegionList(this.showItemPerPage);
        } else {
            this.searchRegion();
        }

        if (this.paginator) {
            this.paginator.pageSize = this.ReginListdataitemsPerPage;
            this.paginator.pageIndex = 0;
        }
    }

    getRegionList(list) {
        let size;
        this.searchkey = "";
        let List = this.currentPageReginListdata;
        if (list) {
            size = list;
            this.ReginListdataitemsPerPage = list;
        } else {
            size = this.ReginListdataitemsPerPage;
        }
        const url = "/region";
        let plandata = {
            page: List,
            pageSize: size,
        };
        this.stateManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.regionListData = response.dataList;
                this.ReginListdatatotalRecords = response.totalRecords;
                this.dataSource.data = this.regionListData;

                if (this.paginator) {
                    this.paginator.length = this.ReginListdatatotalRecords;
                    this.paginator.pageIndex = this.currentPageReginListdata - 1;
                    this.paginator.pageSize = this.ReginListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                this.dataSource.data = [];
                this.ReginListdatatotalRecords = 0;
            }
        );
    }

    addEditRegion(id) {
        this.submitted = true;
        if (this.reginFormGroup.valid) {
            if (id) {
                const url = "/region/update";

                if (this.reginFormGroup.value.branchid.length == 0) {
                    this.reginFormGroup.value.branchid = null;
                }
                let regionListData = this.reginFormGroup.value;

                this.stateManagementService.postMethod(url, regionListData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.isReginEdit = false;
                            this.reginFormGroup.reset();
                            this.reginFormGroup.controls.branchid.setValue("");
                            this.stateManagementService.clearCache("/region/all");
                            if (this.dialogRef) {
                                this.dialogRef.close();
                            }

                            if (!this.searchkey) {
                                this.getRegionList("");
                            } else {
                                this.searchRegion();
                            }

                            this.toastr.success(`Successfully updated`, 'Success!');
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {
                const url = "/region/save";

                if (this.reginFormGroup.value.branchid.length == 0) {
                    this.reginFormGroup.value.branchid = null;
                }
                let regionListData = this.reginFormGroup.value;

                this.stateManagementService.postMethod(url, regionListData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.reginFormGroup.reset();
                            this.stateManagementService.clearCache("/region/all");
                            if (this.dialogRef) {
                                this.dialogRef.close();
                            }
                            if (!this.searchkey) {
                                this.getRegionList("");
                            } else {
                                this.searchRegion();
                            }
                            this.reginFormGroup.controls.branchid.setValue("");

                            this.toastr.success(`Successfully Created`, 'Success!');
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            }
        }
    }

    editregion(id) {
        if (id) {
            const url = "/region/" + id;
            this.openRegionDialog(id);
            this.stateManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isReginEdit = true;
                    this.viewRegionListData = response.data;
                    this.reginFormGroup.patchValue(this.viewRegionListData);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    searchRegion() {
        if (!this.searchkey || this.searchkey !== this.searchReginName) {
            this.currentPageReginListdata = 1;
        }
        this.searchkey = this.searchReginName;
        if (this.showItemPerPage) {
            this.ReginListdataitemsPerPage = this.showItemPerPage;
        }

        this.searchData.filter[0].filterValue = this.searchReginName.trim();

        const url =
            "/region/search?page=" +
            this.currentPageReginListdata +
            "&pageSize=" +
            this.ReginListdataitemsPerPage +
            "&sortBy=id&sortOrder=0";

        this.stateManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.regionListData = response.dataList;
                    this.ReginListdatatotalRecords = response.totalRecords;
                } else {
                    this.regionListData = response.dataList;
                    this.ReginListdatatotalRecords = response.totalRecords;
                }

                this.dataSource.data = this.regionListData;

                if (this.paginator) {
                    this.paginator.length = this.ReginListdatatotalRecords;
                    this.paginator.pageIndex = this.currentPageReginListdata - 1;
                    this.paginator.pageSize = this.ReginListdataitemsPerPage;
                }
            },
            (error: any) => {
                this.ReginListdatatotalRecords = 0;
                this.dataSource.data = [];
                if (this.paginator) {
                    this.paginator.length = 0;
                    this.paginator.pageIndex = 0;
                }
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.regionListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearRegionData() {
        this.searchReginName = "";
        this.searchkey = "";
        this.currentPageReginListdata = 1;
        this.getRegionList("");
        this.resetForm();
    }

    // Add this new method to reset form properly
    resetForm() {
        this.reginFormGroup.reset();
        this.submitted = false;
        this.isReginEdit = false;
        this.reginFormGroup.controls.branchid.setValue("");
    }

    // Update the closeDialog method
    closeDialog() {
        this.resetForm();
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    deleteConfirmon(rdata: any) {
        if (rdata) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${rdata.rname}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteregion(rdata);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    deleteregion(rdata) {
        let data = rdata;

        const url = "/region/delete";
        this.stateManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (this.currentPageReginListdata != 1 && this.regionListData.length == 1) {
                        this.currentPageReginListdata = this.currentPageReginListdata - 1;
                    }
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                    if (!this.searchkey) {
                        this.getRegionList("");
                    } else {
                        this.searchRegion();
                    }
                    this.searchReginName = "";
                    this.reginFormGroup.reset();
                    this.reginFormGroup.controls.branchid.setValue("");
                    this.submitted = false;
                    this.isReginEdit = false;
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedRegionList(event: PageEvent) {
        this.currentPageReginListdata = event.pageIndex + 1; // Material paginator is 0-based
        this.ReginListdataitemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getRegionList("");
        } else {
            this.searchRegion();
        }
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;

        if (this.paginator) {
            this.paginator.length = this.ReginListdatatotalRecords;
            this.paginator.pageIndex = this.currentPageReginListdata - 1;
            this.paginator.pageSize = this.ReginListdataitemsPerPage;
        }

        this.paginator.page.subscribe((event: PageEvent) => {
            this.pageChangedRegionList(event);
        });
    }

    canExit() {
        if (!this.reginFormGroup.dirty) return true;
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

    @ViewChild("regionDialog") regionDialog!: TemplateRef<any>;

    openRegionDialog(region: any) {
        this.dialogRef = this.dialog.open(this.regionDialog, {
            width: "900px",
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(result => {
            this.resetForm();
        });
    }
}
