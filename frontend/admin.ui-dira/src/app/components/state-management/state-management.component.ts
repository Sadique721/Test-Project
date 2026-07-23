import { Component, OnInit, TemplateRef, ViewChild, ViewEncapsulation } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { StateManagementService } from "src/app/service/state-management.service";
import { Regex } from "src/app/constants/regex";
import { StateManagement } from "src/app/components/model/state-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CountryManagement } from "src/app/components/model/country-management";
import { CountryManagementComponent } from "../country-management/country-management.component";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { AddEditStateManagmentComponent } from "./add-edit-state-managment/add-edit-state-managment.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-state-management",
    templateUrl: "./state-management.component.html",
    styleUrls: ["./state-management.component.css"],
    standalone: false,
    encapsulation: ViewEncapsulation.None
})
export class StateManagementComponent implements OnInit {
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    stateFormGroup: UntypedFormGroup;
    // countryFormArray: FormArray;
    submitted: boolean = false;
    stateData: any = {};
    countryListData: any;
    currentPageStateListdata = 1;
    stateitemPerpage = RadiusConstants.ITEMS_PER_PAGE;
    statetotalRecord: any;
    countryPojo: any = {};
    stateListData: any;
    viewCountryListData: any;
    viewStateListData: any;
    isStateEdit: boolean = false;
    searchData: any;
    searchStateName: any = "";
    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    statusOptions = RadiusConstants.status;
    public loginService: LoginService;

    // after changed code
    subCountryTotalRecords: any;
    currentPageStateSlab = 1;
    stateName: string = '';
    dataSource = new MatTableDataSource<any>([]);
    page = 1;
    pageSize = 5;
    totalRecords = 0;




    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private stateManagementService: StateManagementService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService

    ) {
        this.createAccess = loginService.hasPermission(MASTERS.STATE_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.STATE_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.STATE_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.stateFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            countryName: ["", Validators.required],
        });

        this.searchData = {
            filters: [
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

        this.getCountryList();
        this.getStateListData("");
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);

        if (this.currentPageStateSlab > 1) {
            this.currentPageStateSlab = 1;
        }
        if (!this.searchkey) {
            this.getStateListData(this.showItemPerPage);
        } else {
            this.searchState();
        }
    }



    canExit() {
        if (!this.stateFormGroup.dirty) return true;
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

    addEditState(stateId) {
        this.submitted = true;
        if (this.stateFormGroup.valid) {
            const stateName = this.stateFormGroup.controls.name.value;
            const stateStatus = this.stateFormGroup.controls.status.value;
            const countryId = this.stateFormGroup.controls.countryName.value;

            const selectedCountry = this.countryListData?.find(c => c.id == countryId) || {};

            this.stateData.name = stateName;
            this.stateData.status = stateStatus;
            this.stateData.countryPojo = selectedCountry;
            this.stateData.countryName = selectedCountry?.name || '';

            this.stateFormGroup.patchValue({
                countryName: selectedCountry?.id
            });

            if (stateId) {
                const url = "/state/" + stateId;
                this.stateData.delete = false;
                this.stateData.isDelete = false;

                this.stateManagementService.updateMethod(url, this.stateData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isStateEdit = false;
                        this.stateFormGroup.reset();
                        this.commondropdownService.clearCache("/state/all");
                        this.resetForm();
                        this.toastr.success(`Successfully Updated`, 'Success!');
                        this.getStateListData("");
                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchState();
                        } else {
                            this.getStateListData("");
                        }
                    },
                    (error: any) => {
                        if (error.error.status == 406) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else if (error.error.status == 417) {
                            this.toastr.info(`${error.error.msg}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    }
                );
            } else {
                const url = "/state";
                this.stateData.name = this.stateFormGroup.controls.name.value;
                this.stateData.status = this.stateFormGroup.controls.status.value;
                this.stateData.countryPojo = this.countryPojo;

                const countryId = this.stateFormGroup.controls.countryName.value;

                const selectedCountry = this.countryListData.find(c => c.id == countryId);

                this.stateData.countryPojo = selectedCountry || {};
                this.stateData.countryName = selectedCountry?.name || '';

                this.stateManagementService.postMethod(url, this.stateData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isStateEdit = false;
                        this.stateFormGroup.reset();
                        this.stateFormGroup.controls.status.setValue("");

                        this.commondropdownService.clearCache("/state/all");
                        this.resetForm();
                        if (response.ERROR) {
                            this.toastr.info(`${response.ERROR}`, 'Info!');
                        } else {
                            this.toastr.success(`${response.msg}`, 'Success!');
                        }
                        this.getStateListData("");
                        if (this.searchkey) {
                            this.searchState();
                        } else {
                            this.getStateListData("");
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            }
        }
    }


    getCountryList() {
        const url = "/country/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    getStateListData(list) {
        const url = "/state/list";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageStateSlab;
        if (list) {
            size = list;
            this.stateitemPerpage = list;
        } else {
            size = this.stateitemPerpage;
        }
        let plandata = {
            page: pageList,
            pageSize: size
        };

        this.stateManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {

                this.stateListData = response.stateList;
                this.dataSource = new MatTableDataSource<any>(this.stateListData);

                this.statetotalRecord = response.pageDetails.totalRecords;
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    editState(stateId) {
        if (stateId) {
            const url = "/state/" + stateId;
            this.stateManagementService.getMethod(url).subscribe(
                (response: any) => {

                    this.isStateEdit = true;
                    this.viewStateListData = response.stateData;

                    const dialogRef2 = this.dialog.open(AddEditStateManagmentComponent, {
                        width: '800px',
                        data: {
                            isEdit: true,
                            title: 'Update ' + this.stateTitle,
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            dynamicCountryTitle: this.countryTitle,
                            dynamicSubCountryName: this.stateTitle,
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            stateData: this.viewStateListData
                        }
                    });
                    dialogRef2.afterClosed().subscribe(result => {
                        if (result) {
                            this.stateFormGroup.patchValue({
                                name: result.name,
                                status: result.status,
                                countryName: result.countryId
                            })
                            this.addEditState(stateId);
                        } else {
                        }
                    });

                    this.stateFormGroup.patchValue({
                        name: this.viewStateListData.name,
                        status: this.viewStateListData.status,
                        countryName: this.viewStateListData.countryPojo?.id || null
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    searchState() {
        if (!this.searchkey || this.searchkey !== this.searchStateName) {
            this.currentPageStateSlab = 1;
        }
        this.searchkey = this.searchStateName;
        if (this.showItemPerPage) {
            this.stateitemPerpage = this.showItemPerPage;
        }

        this.searchData.filters[0].filterValue = this.searchStateName.trim();

        this.searchData.page = this.currentPageStateSlab;
        this.searchData.pageSize = this.stateitemPerpage;

        const url = "/state/search";
        this.stateManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
                this.statetotalRecord = response.pageDetails.totalRecords;

                this.dataSource = new MatTableDataSource<any>(this.stateListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            (error: any) => {
                this.statetotalRecord = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.stateListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }


    clearSearchState() {
        this.searchStateName = "";
        this.searchkey = "";
        this.getStateListData("");
        this.submitted = false;
        this.isStateEdit = false;
        this.stateFormGroup.reset();
        this.stateFormGroup.controls.status.setValue("");
    }

    deleteConfirmonState(stateId: number) {
        if (stateId) {
            this.confirmAndDeleteState(stateId);
        }
    }


    handleDeleteResponse(response: any) {
        if (response.responseCode == 200) {
            this.toastr.success(`${response.error.message}`, 'Success!');
        } else if (
            response.responseCode == 405 ||
            response.responseCode == 406 ||
            response.responseCode == 417
        ) {
            this.toastr.error(`${response.responseMessage}`, 'Error!');
        }
    }

    handleDeleteError(error: any) {
        if (error.error.status == 405 || error.error.status == 417) {
            this.toastr.info(`${error.error.ERROR}`, 'Info!');
        } else {
            this.toastr.error(`${error.error.ERROR}`, 'Error!');
        }
    }

    deleteState(stateId: number) {
        this.resetForm();
        const url = "/state/" + stateId;
        this.stateManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                this.resetForm();
                if (this.currentPageStateSlab != 1 && this.stateListData.length == 1) {
                    this.currentPageStateSlab = this.currentPageStateSlab - 1;
                }
                this.clearSearchState();
                this.resetForm();
                this.toastr.success(`Successfully Deleted`, 'Success!');
                if (this.searchkey) {
                    this.searchState();
                } else {
                    this.getStateListData("");
                }
            },
            (error: any) => {
                this.handleDeleteError(error);
            }
        );
    }


    // after change code

    displayedColumns = ['id', 'Name', "CountryName", 'Status', 'Action'];

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    onSubmit() {
        this.submitted = true;

        if (this.stateFormGroup.invalid) {
            return;
        }

        if (this.isStateEdit) {
            this.addEditState(this.viewStateListData.id);
        } else {
            this.addEditState('');
        }
    }


    private resetForm() {
        this.submitted = false;
        this.isStateEdit = false;
        this.stateFormGroup.reset();
        this.stateFormGroup.controls.status.setValue('');
        this.stateFormGroup.reset({
            name: '',
            countryName: '',
            status: ''
        });
        this.stateFormGroup.markAsPristine();
        this.stateFormGroup.markAsUntouched();

        Object.keys(this.stateFormGroup.controls).forEach(key => {
            this.stateFormGroup.get(key)?.setErrors(null);
        });
    }

    pageChangedStateList(event: PageEvent) {
        this.currentPageStateSlab = event.pageIndex + 1;
        this.stateitemPerpage = event.pageSize;
        if (this.searchkey) {
            this.searchState();
        } else {
            this.getStateListData("");
        }
    }

    deleteConfirmonStateDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete State',
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteState(item.id);
            } else {
            }
        });
    }

    confirmAndDeleteState(stateId: number) {
        this.confirmationService.confirm({
            message: "Do you want to delete this " + this.stateTitle + "?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteState(stateId);
            },
            reject: () => {
                this.toastr.info(`You have Rejected`, 'Info!')
                // this.messageService.add({
                //     severity: "info",
                //     summary: "Rejected",
                //     detail: "You have rejected",
                // });
            },
        });
    }

    addEditStateDialog() {
        const dialogRef = this.dialog.open(AddEditStateManagmentComponent, {
            width: '800px',
            data: {
                isEdit: false,
                title: 'Create ' + this.stateTitle,
                addLabel: true,
                dynamicCountryTitle: this.countryTitle,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                dynamicSubCountryName: this.stateTitle,
                createAcS: this.createAccess,
                editAcs: this.editAccess
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.stateFormGroup.patchValue({
                    name: result.name,
                    status: result.status,
                    countryName: result.countryId
                });
                this.addEditState("");

            } else {
            }
        });
    }

}
