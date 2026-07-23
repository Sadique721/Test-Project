import { Component, OnInit, ViewChild } from "@angular/core";
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
import { ObserversModule } from "@angular/cdk/observers";
import { DepartmentManagement } from "../model/department-management";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { AddEditDepartmentManagmentComponent } from "./add-edit-department-managment/add-edit-department-managment.component";
import { MatDialog } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-department-management",
    templateUrl: "./department-management.component.html",
    styleUrls: ["./department-management.component.css"],
    standalone: false
})
export class DepartmentManagementComponent implements OnInit, IDeactivateGuard {
    title = RadiusConstants.DEPARMENT;
    departmentFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    departmentData: DepartmentManagement;
    departmentListData: any;
    isDepartmentEdit: boolean = false;
    viewDepartmentListData: any;
    currentPageDepartment = 1;
    departmentitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    departmenttotalRecords: any;
    searchDepartmentName: any = "";
    searchData: any;
    AclClassConstants;
    AclConstants;

    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;
    planList: any;
    // after changes
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);


    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private countryManagementService: CountryManagementService,
        loginService: LoginService,
        private dialog: MatDialog,
        private toastr: ToastrService

    ) {
        this.createAccess = loginService.hasPermission(MASTERS.DEPARTMENT_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.DEPARTMENT_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.DEPARTMENT_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isDepartmentEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.departmentFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            planIds: ["", Validators.required],
            status: ["", Validators.required]
        });

        this.searchData = {
            filters: [
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
        this.getDepartmentListData("");
        this.getAllPlans();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    canExit() {
        if (!this.departmentFormGroup.dirty) return true;
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

    addEditDepartment(countryId) {
        this.submitted = true;
        if (this.departmentFormGroup.valid) {
            if (countryId) {
                const url = "/department/" + countryId;
                this.departmentData = this.departmentFormGroup.value;
                this.departmentData.delete = false;
                this.departmentData.isDelete = false;
                this.countryManagementService.updateMethod(url, this.departmentData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isDepartmentEdit = false;
                        this.departmentFormGroup.reset();
                        this.departmentFormGroup.controls.status.setValue("");
                        this.toastr.success(`${response.msg}`, 'Success!');
                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchDepartment();
                        } else {
                            this.getDepartmentListData("");
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        if (error.error.status == 406 || error.error.status == 417) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    }
                );
            } else {
                const url = "/department/save";
                this.departmentData = this.departmentFormGroup.value;
                this.departmentData.delete = false;
                this.departmentData.isDelete = false;
                // console.log("this.createChargeData", this.departmentData);
                this.countryManagementService.postMethod(url, this.departmentData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.departmentFormGroup.reset();
                        this.departmentFormGroup.controls.status.setValue("");
                        this.toastr.success(`${response.msg}`, 'Success!');
                        if (this.searchkey) {
                            this.searchDepartment();
                        } else {
                            this.getDepartmentListData("");
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        if (error.error.status == 406) {
                            this.toastr.info(`${error.error.ERRO}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERRO}`, 'Error!');
                        }
                    }
                );
            }
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageDepartment > 1) {
            this.currentPageDepartment = 1;
        }
        if (!this.searchkey) {
            this.getDepartmentListData(this.showItemPerPage);
        } else {
            this.searchDepartment();
        }
    }

    getDepartmentListData(list) {
        // const url = "/department/all"
        // this.countryManagementService.getMethod(url).subscribe((response: any) => {
        const url = "/department/list";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageDepartment;
        if (list) {
            size = list;
            this.departmentitemsPerPage = list;
        } else {
            size = this.departmentitemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size
        };
        this.countryManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.departmentListData = response.departmentList;

                this.dataSource = new MatTableDataSource<any>(this.departmentListData);
                this.departmenttotalRecords = response.pageDetails.totalRecords;


                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERRO}`, 'Error!');
            }
        );
    }

    editDepartment(countryId) {
        if (countryId) {
            const url = "/department/" + countryId;
            this.countryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isDepartmentEdit = true;
                    this.viewDepartmentListData = response.departmentData;

                    const dialogRef2 = this.dialog.open(AddEditDepartmentManagmentComponent, {
                        width: '800px',
                        data: {
                            isEdit: true,
                            title: 'Update ' + this.title,
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            depData: this.viewDepartmentListData,
                            planListData: this.planList
                        }
                    });
                    dialogRef2.afterClosed().subscribe(result => {
                        if (result) {
                            this.departmentFormGroup.patchValue({
                                name: result.name,
                                status: result.status,
                                planIds: result.planIds
                            })
                            this.addEditDepartment(countryId);
                        } else {
                        }
                    });

                    this.departmentFormGroup.patchValue(this.viewDepartmentListData);
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERRO}`, 'Error!');
                }
            );
        }
    }

    getAllPlans() {
        const url = "/postpaidplan/all";
        this.countryManagementService.getAllPlans(url).subscribe(
            (response: any) => {
                this.planList = response.postpaidplanList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERRO}`, 'Error!');
            }
        );
    }
    searchDepartment() {
        if (!this.searchkey || this.searchkey !== this.searchDepartmentName) {
            this.currentPageDepartment = 1;
        }
        this.searchkey = this.searchDepartmentName;
        if (this.showItemPerPage) {
            this.departmentitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchDepartmentName.trim();
        this.searchData.page = this.currentPageDepartment;
        this.searchData.pageSize = this.departmentitemsPerPage;

        const url = "/department/search";
        // console.log("this.searchData", this.searchData)
        this.countryManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.departmentListData = response.departmentList;
                this.departmenttotalRecords = response.pageDetails.totalRecords;

                this.dataSource = new MatTableDataSource<any>(this.departmentListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            (error: any) => {
                this.departmenttotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.departmentListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }

    clearSearchDepartment() {
        this.searchDepartmentName = "";
        this.searchkey = "";
        this.getDepartmentListData("");
        this.submitted = false;
        this.isDepartmentEdit = false;
        this.departmentFormGroup.reset();
        this.departmentFormGroup.controls.status.setValue("");
    }

    deleteConfirmonDepartment(countryId: number) {
        if (countryId) {
            this.confirmationService.confirm({
                message: "Do you want to delete this " + this.title + "?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteDepartment(countryId);
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });
        }
    }

    deleteDepartment(countryId) {
        const url = "/department/" + countryId;

        this.countryManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageDepartment != 1 && this.departmentListData.length == 1) {
                    this.currentPageDepartment = this.currentPageDepartment - 1;
                }
                this.clearSearchDepartment();
                this.toastr.success(`${response.msg}`, 'Success!');
                if (this.searchkey) {
                    this.searchDepartment();
                } else {
                    this.getDepartmentListData("");
                }
            },
            (error: any) => {
                // console.log(error, "error")
                if (error.error.status == 417) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }

    pageChangedDepartmentList(event: PageEvent) {
        this.currentPageDepartment = event.pageIndex + 1;
        this.departmentitemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.searchDepartment();
        } else {
            this.getDepartmentListData("");
        }
    }

    // after changes
    displayedColumns = ['id', 'Name', 'Status', 'Action'];


    addEditDepartmentDialog() {
        const dialogRef = this.dialog.open(AddEditDepartmentManagmentComponent, {
            width: '800px',
            data: {
                isEdit: false,
                title: 'Create ' + this.title,
                addLabel: true,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                planListData: this.planList
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.departmentFormGroup.patchValue({
                    name: result.name,
                    status: result.status,
                    planIds: result.planIds
                });
                this.addEditDepartment("");
            } else {
            }
        });
    }
    deleteConfirmonDepartmentDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: `Delete ${this.title}`,
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteDepartment(item.id);
            } else {
            }
        });
    }
}
