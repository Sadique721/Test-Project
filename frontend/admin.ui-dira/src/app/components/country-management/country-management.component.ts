import { Component, OnInit, TemplateRef, ViewChild, ViewEncapsulation } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CountryManagementService } from "src/app/service/country-management.service";
import { Regex } from "src/app/constants/regex";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY } from "src/app/RadiusUtils/RadiusConstants";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { Observable, Observer } from "rxjs";
import { resolve } from "dns";
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
import { AddEditCountryManagmentComponent } from "./add-edit-country-managment/add-edit-country-managment.component";
import { ToastrService } from "ngx-toastr";
interface Invoice {
    id: number;
    billFrom: string;
    billTo: string;
    totalCost: number;
    status: string;
    completed: boolean;
}

@Component({
    selector: "app-country-management",
    templateUrl: "./country-management.component.html",
    styleUrls: ["./country-management.component.css"],
    standalone: false,
    // encapsulation: ViewEncapsulation.None
})
export class CountryManagementComponent implements OnInit, IDeactivateGuard {
    title = COUNTRY;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    countryFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    countryData: CountryManagement;
    countryListData: any;
    isCountryEdit: boolean = false;
    viewCountryListData: any;
    currentPageCountrySlab = 1;
    countryitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    countrytotalRecords: any;
    searchCountryName: any = "";
    searchData: any;
    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    searchForm: FormGroup;
    public loginService: LoginService;
    dataSource = new MatTableDataSource<any>([]);
    countryName: string = ''

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private countryManagementService: CountryManagementService,
        private commondropdownService: CommondropdownService,
        loginService: LoginService,
        private dialog: MatDialog,
        private toastr: ToastrService,

    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(MASTERS.COUNTRY_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.COUNTRY_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.COUNTRY_EDIT);
        this.searchForm = this.fb.group({
            searchText: ['']
        });
    }


    ngOnInit(): void {
        this.countryFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
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
        this.getCountryListData("");
    }

    canExit() {
        if (!this.countryFormGroup.dirty) return true;
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

    addEditCountry(countryId) {
        this.submitted = true;
        if (this.countryFormGroup.valid) {
            if (countryId) {
                const url = "/country/" + countryId;
                this.countryData = this.countryFormGroup.value;
                this.countryData.delete = false;
                this.countryData.isDelete = false;
                this.countryManagementService.updateMethod(url, this.countryData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.countryFormGroup.reset();
                        this.countryFormGroup.controls.status.setValue("");
                        this.countryManagementService.clearCache("/country/all");
                        this.resetForm();
                        this.toastr.success(`${response.msg}`, 'Success!');
                        this.getCountryListData("");
                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchCountry();
                        } else {
                            this.getCountryListData("");
                        }
                    },
                    (error: any) => {
                        if (error.error.status == 417 || error.error.status == 406) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    }
                );
            } else {
                const url = "/country";
                this.countryData = this.countryFormGroup.value;
                this.countryData.delete = false;
                this.countryData.isDelete = false;

                this.countryManagementService.postMethod(url, this.countryData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isCountryEdit = false;
                        this.countryFormGroup.reset();
                        this.countryFormGroup.controls.status.setValue("");

                        this.countryManagementService.clearCache("/country/all");
                        this.resetForm();
                        this.toastr.success(`${response.msg}`, 'Success!');
                        this.getCountryListData("");
                        if (this.searchkey) {
                            this.searchCountry();
                        } else {
                            this.getCountryListData("");
                        }
                    },
                    (error: any) => {
                        if (error.error.status == 417 || error.error.status == 406) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    }
                );
            }
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageCountrySlab > 1) {
            this.currentPageCountrySlab = 1;
        }
        if (!this.searchkey) {
            this.getCountryListData(this.showItemPerPage);
        } else {
            this.searchCountry();
        }
    }

    getCountryListData(list) {
        const url = "/country/list";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageCountrySlab;

        if (list) {
            size = list;
            this.countryitemsPerPage = list;
        } else {
            size = this.countryitemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size
        };
        this.countryManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
                this.dataSource = new MatTableDataSource<any>(this.countryListData);

                this.countrytotalRecords = response.pageDetails.totalRecords;
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    editCountry(countryId) {
        if (countryId) {
            const url = "/country/" + countryId;
            this.countryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isCountryEdit = true;
                    this.viewCountryListData = response.countryData;
                    const dialogRef2 = this.dialog.open(AddEditCountryManagmentComponent, {
                        width: '800px',
                        data: {
                            isEdit: true,
                            title: 'Update ' + this.title,
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            countryData: this.viewCountryListData,
                            inputName: 'Enter ' + this.title + ' Name',
                            inputStatus: 'Select ' + this.title + ' Status'
                        }
                    });
                    dialogRef2.afterClosed().subscribe(result => {
                        if (result) {
                            this.countryFormGroup.patchValue({
                                name: result.name,
                                status: result.status
                            })
                            this.addEditCountry(countryId);
                        } else {
                        }
                    });

                    // this.countryFormGroup.patchValue(this.viewCountryListData);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    searchCountry() {
        if (!this.searchkey || this.searchkey !== this.searchCountryName) {
            this.currentPageCountrySlab = 1;
        }
        this.searchkey = this.searchCountryName;
        if (this.showItemPerPage) {
            this.countryitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchCountryName.trim();

        this.searchData.page = this.currentPageCountrySlab;
        this.searchData.pageSize = this.countryitemsPerPage;

        const url = "/country/search";
        this.countryManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
                this.countrytotalRecords = response.pageDetails.totalRecords;

                this.dataSource = new MatTableDataSource<any>(this.countryListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            (error: any) => {
                this.countrytotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.countryListData = [];
                } else {
                    this.toastr.error(`${error.response.ERROR}`, 'Error!');
                }
            }
        );
    }

    clearSearchCountry() {
        this.searchCountryName = "";
        this.searchkey = "";
        this.getCountryListData("");
        this.submitted = false;
        this.isCountryEdit = false;
        this.countryFormGroup.reset();
        this.countryFormGroup.controls.status.setValue("");
    }

    // deleteConfirmonCountry(countryId: number) {
    //     if (countryId) {
    //         this.confirmationService.confirm({
    //             message: "Do you want to delete this " + this.title + "?",
    //             header: "Delete Confirmation",
    //             icon: "pi pi-info-circle",
    //             accept: () => {
    //                 // this.deleteCountry(countryId);
    //             },
    //             reject: () => {
    //                 this.messageService.add({
    //                     severity: "info",
    //                     summary: "Rejected",
    //                     detail: "You have rejected"
    //                 });
    //             }
    //         });
    //     }
    // }

    deleteCountry(countryId) {
        this.resetForm();
        const url = "/country/" + countryId;

        this.countryManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                this.resetForm();
                if (this.currentPageCountrySlab != 1 && this.countryListData.length == 1) {
                    this.currentPageCountrySlab = this.currentPageCountrySlab - 1;
                }
                this.clearSearchCountry();
                this.resetForm();
                this.toastr.success(`${response.msg}`, 'Success!');
                if (this.searchkey) {
                    this.searchCountry();
                } else {
                    this.getCountryListData("");
                }
            },
            (error: any) => {
                if (error.error.status == 417 || error.error.status == 405 || error.error.status == 406) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }


    // After Adding Mat-Table Changed Data
    displayedColumns = ['id', 'Name', 'Status', 'Action'];

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    // onsubmit called
    onSubmit() {
        this.submitted = true;

        if (this.countryFormGroup.invalid) {
            return;
        }

        if (this.isCountryEdit) {
            this.addEditCountry(this.viewCountryListData.id);
        } else {
            this.addEditCountry('');
        }
    }

    private resetForm() {
        this.submitted = false;
        this.isCountryEdit = false;
        this.countryFormGroup.reset();
        this.countryFormGroup.controls.status.setValue('');
        this.countryFormGroup.reset({
            name: '',
            status: ''
        });
        this.countryFormGroup.markAsPristine();
        this.countryFormGroup.markAsUntouched();

        Object.keys(this.countryFormGroup.controls).forEach(key => {
            this.countryFormGroup.get(key)?.setErrors(null);
        });
    }

    pageChangedCountryList(event: PageEvent) {
        this.currentPageCountrySlab = event.pageIndex + 1;
        this.countryitemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.searchCountry();
        } else {
            this.getCountryListData("");
        }
    }

    deleteConfirmonCountryDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete ' + this.title,
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteCountry(item.id);
            } else {
            }
        });
    }

    addEditCountryDialog() {
        const dialogRef = this.dialog.open(AddEditCountryManagmentComponent, {
            width: '800px',
            data: {
                isEdit: false,
                title: 'Create ' + this.title,
                addLabel: true,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                inputName: 'Enter ' + this.title + ' Name',
                inputStatus: 'Select ' + this.title + ' Status'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.countryFormGroup = this.fb.group({
                    name: result.name,
                    status: result.status
                });
                this.addEditCountry("");

            } else {
            }
        });
    }


    selection = new Set<number>();

    toggleSelection(country: any): void {
        if (this.selection.has(country.id)) {
            this.selection.delete(country.id);
        } else {
            this.selection.add(country.id);
        }
    }

    isAllSelected(): boolean {
        return this.selection.size === this.dataSource.data.length;
    }

    isIndeterminate(): boolean {
        return this.selection.size > 0 && this.selection.size < this.dataSource.data.length;
    }

    masterToggle(event: any): void {
        if (event.checked) {
            this.selection = new Set(this.dataSource.data.map((country: any) => country.id));
        } else {
            this.selection.clear();
        }
    }


}
