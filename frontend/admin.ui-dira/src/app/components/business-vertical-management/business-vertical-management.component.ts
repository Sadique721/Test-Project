import { Component, OnInit, TemplateRef, ViewChild, AfterViewInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { Regex } from "src/app/constants/regex";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY } from "src/app/RadiusUtils/RadiusConstants";
import { BranchManagementService } from "src/app/components/branch-management/branch-management.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { StateManagementService } from "src/app/service/state-management.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { ToastrService } from 'ngx-toastr';

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count, error } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";


@Component({
    selector: "app-business-vertical-management",
    templateUrl: "./business-vertical-management.component.html",
    styleUrls: ["./business-vertical-management.component.css"],
    standalone: false
})
export class BusinessVerticalManagementComponent implements OnInit {

    displayedColumns: string[] = ['id', 'Name', 'Status', 'Action'];
    dataSource = new MatTableDataSource<any>([]);
    pageSizeOptions = [5,10,20,50,100];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    title = "Business Vertical";
    dialogRef: any;
    businessVerticalFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    countryData: CountryManagement;
    regionListData: any;
    isBusinessVEdit: boolean = false;
    viewBusinessVListData: any;
    currentPageBusinessVSlab = 1;
    businessVitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    businessVtotalRecords: any;
    searchCountryName: any = "";
    searchBusinessVName: any = "";
    searchData: any;
    regionSector = "";
    businessVisData = "";
    businessVData: any = [];
    AclClassConstants;
    AclConstants;
    businessData: any;
    isDeleted: boolean;
    isEdit: boolean;
    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private branchManagementService: BranchManagementService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService,
        private stateManagementService: StateManagementService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(MASTERS.BUSINESS_VERTICALS_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.BUSINESS_VERTICALS_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.BUSINESS_VERTICALS_EDIT);
    }

    ngOnInit(): void {
        this.businessVerticalFormGroup = this.fb.group({
            vname: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            region_id: [],
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
            ],
            page: "",
            pageSize: ""
        };

        this.getRegionList();
        this.getAllBussiesVerticalData(""); //   this.getCountryListData("");
    }

    ngAfterViewInit() {
        // this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
    getSelectCustomerSector(id: any) {
        const idtem = id;
    }
    getRegionList() {
        const url = "/region/all";
        const custerlist = {};
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.regionSector = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    addEdit(id) {
        this.submitted = true;
        if (this.businessVerticalFormGroup.valid) {
            //console.log("2 " , this.businessVerticalFormGroup.value)

            if (id) {
                //console.log("3 ")

                const url = "/businessverticals/update";
                this.businessVData = this.businessVerticalFormGroup.value;
                //console.log(this.businessData,"businessData")
                this.businessVData.id = id;
                this.branchManagementService.postMethod(url, this.businessVData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');

                        } else {
                            this.submitted = false;
                            this.dialogRef.close();
                            this.resetFormState();
                            this.isBusinessVEdit = false;
                            this.businessVerticalFormGroup.reset();
                            this.businessVerticalFormGroup.controls.status.setValue("");
                            this.getAllBussiesVerticalData("");
                            this.commondropdownService.clearCache("/businessverticals/all");
                            this.toastr.success(`Successfully updated`, 'Success!');


                            this.submitted = false;
                            if (this.searchkey) {
                                //this.search();
                            } else {
                                //this.getListData("");
                            }
                        }
                    },
                    (error: any) => {
                        console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                    }
                );
            } else {

                const url = "/businessverticals/save";
                this.businessData = this.businessVerticalFormGroup.value;
                this.branchManagementService.postMethod(url, this.businessData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');

                        } else {
                            this.submitted = false;
                            this.dialogRef.close();
                            this.resetFormState();
                            this.businessVerticalFormGroup.reset();
                            this.businessVerticalFormGroup.controls.status.setValue("");
                            this.getAllBussiesVerticalData("");
                            this.toastr.success(`Successfully Created`, 'Success!');


                            this.getRegionList();
                            if (this.searchkey) {
                                //this.search();
                            } else {
                                //this.getListData("");
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

    resetFormState() {
        this.submitted = false;
        this.isBusinessVEdit = false;
        this.businessVerticalFormGroup.reset();
        this.businessVerticalFormGroup.controls.status.setValue("");
        this.viewBusinessVListData = null;
        this.businessVData = [];
    }


    deleteConfirmBusinessV(id: number) {
        if (id) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete this Business Vertical?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    // User confirmed - get data and delete
                    const url1 = "/businessverticals/" + id;
                    this.branchManagementService.getMethod(url1).subscribe(
                        (response: any) => {
                            const data = response.data;
                            this.delete(data);
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    );
                } else {
                    // User cancelled
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'Delete operation was cancelled!');
                    }

                }
            });
        }
    }

    delete(data) {
        const url = "/businessverticals/delete";
        this.branchManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 405 || response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.getAllBussiesVerticalData("");

                    this.toastr.success(`Successfully Deleted`, 'Success!');

                }
                if (this.searchkey) {
                    // this.search();
                } else {
                    // this.getListData("");
                    this.getAllBussiesVerticalData("");
                }
            },
            (error: any) => {
                console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
        // location.reload();
    }
    searchBusinessV() {
        if (!this.searchkey || this.searchkey !== this.searchBusinessVName) {
            this.currentPageBusinessVSlab = 1;
            if (this.paginator) {
                this.paginator.firstPage();
            }
        }
        this.searchkey = this.searchBusinessVName;
        if (this.showItemPerPage) {
            this.businessVitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchBusinessVName.trim();
        this.searchData.page = this.currentPageBusinessVSlab;
        this.searchData.pageSize = this.businessVitemsPerPage;

        const url = `/businessverticals/search?page=${this.currentPageBusinessVSlab}&pageSize=${this.businessVitemsPerPage}&sortBy=id&sortOrder=0`;
        this.branchManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.businessVisData = response.dataList;
                this.dataSource = new MatTableDataSource(response.dataList);
                this.dataSource.sort = this.sort;
                this.businessVtotalRecords = response.totalRecords;
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`Record fetched successfully`, 'Success!');

                }
            },
            (error: any) => {
                this.businessVtotalRecords = 0;
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.regionListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    canExit() {
        if (!this.businessVerticalFormGroup.dirty) return true;
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
    clearSearchCountry() {
        this.searchBusinessVName = "";
        this.searchkey = "";
        this.currentPageBusinessVSlab = 1;

        if (this.paginator) {
            this.paginator.firstPage();
        }

        this.getAllBussiesVerticalData("");
        this.submitted = false;
        this.isBusinessVEdit = false;
        this.businessVerticalFormGroup.reset();
        this.businessVerticalFormGroup.controls.status.setValue("");
        this.dataSource = new MatTableDataSource([]);
        this.dataSource.sort = this.sort;
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.businessVitemsPerPage = this.showItemPerPage;
        if (this.currentPageBusinessVSlab > 1) {
            this.currentPageBusinessVSlab = 1;
        }

        // Reset Material paginator to first page
        if (this.paginator) {
            this.paginator.firstPage();
        }

        if (!this.searchkey) {
            this.getAllBussiesVerticalData(this.showItemPerPage);
        } else {
            this.searchBusinessV();
        }
    }
    getAllBussiesVerticalData(list) {
        const url = "/businessverticals";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageBusinessVSlab;
        if (list) {
            size = list;
            this.businessVitemsPerPage = list;
        } else {
            size = this.businessVitemsPerPage;
        }
        let plandata = {
            page: this.currentPageBusinessVSlab,
            pageSize: size
        };
        this.branchManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                var data = response.dataList;
                const result = data.filter(word => word.isDeleted === false);
                this.businessVisData = result;
                this.dataSource = new MatTableDataSource(result);
                this.dataSource.sort = this.sort;
                this.businessVtotalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }


    editRegion(countryId) {
        if (countryId) {
            const url = "/businessverticals/" + countryId;
            this.openbusinessDialog(countryId)
            this.branchManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isBusinessVEdit = true;
                    this.viewBusinessVListData = response.data;
                    this.businessVerticalFormGroup.patchValue(this.viewBusinessVListData);
                    this.businessVData.id = countryId;
                },
                (error: any) => {
                    console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                }
            );
        }
    }

    pageChangedCountryList(event: PageEvent) {
        this.currentPageBusinessVSlab = event.pageIndex + 1;
        this.businessVitemsPerPage = event.pageSize;
        this.dataSource.data = [];
        if (this.searchkey) {
            this.searchBusinessV();
        } else {
            this.getAllBussiesVerticalData("");
        }
    }

    @ViewChild("businessVerticalDialog") businessVerticalDialog!: TemplateRef<any>;

    openbusinessDialog(branchManagement?: any) {
        // Close any existing dialog first
        if (this.dialogRef) {
            this.dialogRef.close();
        }

        // Reset form state before opening
        this.resetFormState();

        this.dialogRef = this.dialog.open(this.businessVerticalDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            // Reset form state when dialog closes for any reason
            this.resetFormState();
            this.dialogRef = null;
        });
    }


}
