import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormControl, FormArray, FormGroup } from "@angular/forms";
import { CountryManagement } from "../model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { Observable, Observer } from "rxjs";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { subBusinessVerticalService } from "./sub-business-vertical.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { StateManagementService } from "src/app/service/state-management.service";
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
    selector: "app-sub-business-vertical",
    templateUrl: "./sub-business-vertical.component.html",
    styleUrls: ["./sub-business-vertical.component.css"],
    standalone: false
})
export class SubBusinessVerticalComponent implements OnInit {

    displayedColumns: string[] = ['id', 'sbvName', 'status', 'action'];
    dataSource = new MatTableDataSource<any>();
    // Add this property with your other properties
    pageSizeOptions = [5, 10, 20, 50, 100];
    dialogRef: any;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    title = "Sub Business Vertical";
    subbusinessVerticalFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    // countryData: CountryManagement;
    businessverticalListData: any;
    isSubBusinessVEdit: boolean = false;
    viewSubBusinessVListData: any;
    currentPageSubBusinessVSlab = 1;
    subbusinessVitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    subbusinessVtotalRecords: any;
    searchCountryName: any = "";
    searchName: any = "";
    searchData: any;
    businessverticalSector = "";
    subbusinessVisData = "";
    subbusinessVData: any = [];
    AclClassConstants;
    AclConstants;
    subbusinessData: any;
    isDeleted: boolean;
    isEdit: boolean = false;
    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    deletedata: any = {
        id: "",
    };
    editMode: boolean;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private subBusinessVerticalService: subBusinessVerticalService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService,
        private stateManagementService: StateManagementService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.SUB_BUSINESS_VERTICALS_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.SUB_BUSINESS_VERTICALS_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.SUB_BUSINESS_VERTICALS_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isEdit = !createAccess && editAccess ? true : false;
    }

    ngOnInit(): void {
        this.subbusinessVerticalFormGroup = this.fb.group({
            sbvname: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            buVerticalsId: ["", Validators.required],
            status: ["", Validators.required],
            // id: [""],
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

        this.getBusinessVertical();
        this.getAllSubBussiesVerticalData("");
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
    onSubBusinessVPageChange(event: PageEvent) {
        this.currentPageSubBusinessVSlab = event.pageIndex + 1;
        this.subbusinessVitemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchSubBusinessV();
        } else {
            this.getAllSubBussiesVerticalData("");
        }
    }

    getSelectCustomerSector(id: any) {
        const idtem = id;
    }
    getBusinessVertical() {
        const url = "/businessverticals/all";
        const custerlist = {};
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.businessverticalSector = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    addEdit(id) {
        this.submitted = true;
        if (this.subbusinessVerticalFormGroup.valid) {
            if (id) {
                const url = "/subbusinessvertical/update";
                this.subbusinessVData = this.subbusinessVerticalFormGroup.value;
                //console.log(this.subbusinessData,"subbusinessData")
                this.subbusinessVData.id = id;
                this.subBusinessVerticalService.postMethod(url, this.subbusinessVData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406 || response.responseCode == 417) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.isEdit = false;
                            this.subbusinessVerticalFormGroup.reset();
                            this.subbusinessVerticalFormGroup.controls.status.setValue("");
                            this.dialogRef.close();
                            this.getAllSubBussiesVerticalData("");

                            this.toastr.success(`Successfully Updated`, 'Success!');
                            this.submitted = false;
                            if (this.searchkey) {
                                this.searchSubBusinessV();
                            } else {
                                this.getAllSubBussiesVerticalData("");
                            }
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")

                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {

                const url = "/subbusinessvertical/save";
                this.subbusinessData = this.subbusinessVerticalFormGroup.value;
                this.subBusinessVerticalService.postMethod(url, this.subbusinessData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.submitted = false;
                            this.subbusinessVerticalFormGroup.reset();
                            this.subbusinessVerticalFormGroup.controls.status.setValue("");
                            this.dialogRef.close();
                            this.getAllSubBussiesVerticalData("");

                            this.toastr.success(`Successfully Created`, 'Success!');
                            this.getBusinessVertical();
                            if (this.searchkey) {
                                this.searchSubBusinessV();
                            } else {
                                this.getAllSubBussiesVerticalData("");
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
    deleteConfirmSubBusinessV(data): void {
        if (data.id) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${data.sbvname}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.delete(data.id);

                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Sub Business Vertical?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.delete(data.id);
            //         //   let deletedata: any;
            //         //
            //         //   const url = "/subbusinessvertical/" + id;
            //         //   this.subBusinessVerticalService.getMethod(url).subscribe(
            //         //     (response: any) => {
            //         //       deletedata = response.deletedata;
            //         //       this.delete(deletedata);
            //         //     },
            //         //     (error: any) => {
            //         //       this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            //         //
            //         //     }
            //         //   );
            //     },
            //     reject: () => {
            //         this.toastr.info("You have rejected", 'Info!');
            //     },
            // });
        }
    }
    delete(id) {
        const url = "/subbusinessvertical/delete?id=" + id;
        this.subBusinessVerticalService.deleteMethod(url).subscribe(
            (response: any) => {
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.getAllSubBussiesVerticalData("");
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                }
                if (this.searchkey) {
                    this.searchSubBusinessV();
                } else {
                    this.getAllSubBussiesVerticalData("");
                    this.subbusinessVerticalFormGroup.reset();
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    searchSubBusinessV() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPageSubBusinessVSlab = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.subbusinessVitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName.trim();
        this.searchData.page = this.currentPageSubBusinessVSlab;
        this.searchData.pageSize = this.subbusinessVitemsPerPage;

        const url = `/subbusinessvertical/search?page=${this.currentPageSubBusinessVSlab}&pageSize=${this.subbusinessVitemsPerPage}&sortBy=id&sortOrder=0`;

        this.subBusinessVerticalService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.subbusinessVisData = response.dataList;
                    this.dataSource = response.dataList;
                    this.dataSource.sort = this.sort;
                    this.subbusinessVtotalRecords = response.totalRecords;
                } else if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.subbusinessVisData = response.dataList;
                    this.subbusinessVtotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.subbusinessVtotalRecords = 0;
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.businessverticalListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    canExit() {
        if (!this.subbusinessVerticalFormGroup.dirty) return true;
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
    clearSearchCountry() {
        this.searchName = "";
        this.searchkey = "";
        this.getAllSubBussiesVerticalData("");
        this.submitted = false;
        this.isEdit = false;
        this.subbusinessVerticalFormGroup.reset();
        this.subbusinessVerticalFormGroup.controls.status.setValue("");
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.subbusinessVitemsPerPage = this.showItemPerPage; // Update items per page
        if (this.currentPageSubBusinessVSlab > 1) {
            this.currentPageSubBusinessVSlab = 1;
        }
        if (!this.searchkey) {
            this.getAllSubBussiesVerticalData(this.showItemPerPage);
        } else {
            this.searchSubBusinessV();
        }
    }

    getAllSubBussiesVerticalData(list) {
        const url = "/subbusinessvertical";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageSubBusinessVSlab;
        if (list) {
            size = list;
            this.subbusinessVitemsPerPage = list;
        } else {
            size = this.subbusinessVitemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
            // SortBy: "id",
            // sortOrder: 0,
        };
        this.subBusinessVerticalService.postMethod(url, plandata).subscribe(
            (response: any) => {
                var data = response.dataList;
                const result = data.filter(word => word.isDeleted === false);
                this.subbusinessVisData = result;
                this.dataSource = new MatTableDataSource(result);
                this.dataSource.sort = this.sort;
                this.subbusinessVtotalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    editSubBusinessVertical(id) {
        if (id) {
            const url = "/subbusinessvertical/" + id;
            this.openSubBusinessVerticalDialog(id);
            this.subBusinessVerticalService.getMethod(url).subscribe(
                (response: any) => {
                    this.isEdit = true;
                    this.viewSubBusinessVListData = response.data;
                    this.subbusinessVerticalFormGroup.patchValue(this.viewSubBusinessVListData);
                    this.subbusinessVData.id = id;
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    pageChangedList(pageNumber) {
        this.currentPageSubBusinessVSlab = pageNumber;
        if (this.searchkey) {
            this.searchSubBusinessV();
        } else {
            this.getAllSubBussiesVerticalData("");
        }
    }

    @ViewChild("subBusinessVerticalDialog") subBusinessVerticalDialog!: TemplateRef<any>;

    openSubBusinessVerticalDialog(subbusiness: any = null) {
        this.submitted = false;
        this.isEdit = false;
        this.subbusinessVerticalFormGroup.reset();
        this.subbusinessVerticalFormGroup.controls.status.setValue("");

        this.dialogRef = this.dialog.open(this.subBusinessVerticalDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.clearSearchCountry();
        });
    }
}
