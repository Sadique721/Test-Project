import { Component, OnInit, TemplateRef, ViewChild, ViewEncapsulation } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CityManagementService } from "src/app/service/city-management.service";
import { Regex } from "src/app/constants/regex";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { StateManagementService } from "src/app/service/state-management.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { AddEditCityManagmentComponent } from "./add-edit-city-managment/add-edit-city-managment.component";
import { ToastrService } from "ngx-toastr";
import { error } from "console";

@Component({
    selector: "app-city-management",
    templateUrl: "./city-management.component.html",
    styleUrls: ["./city-management.component.css"],
    standalone: false,
    encapsulation: ViewEncapsulation.None
})
export class CityManagementComponent implements OnInit {
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    cityFormGroup: UntypedFormGroup;
    countryListData: any;
    stateListData: any;
    submitted: boolean = false;
    cityData: any = {};
    statePojo: any = {};
    countryPojo: any = {};
    cityListData: any;
    currentPageCityListdata = 1;
    cityListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    cityListdatatotalRecords: any;
    isCityEdit: boolean = false;
    viewCityListData: any;
    searchData: any;
    searchCityName: any = "";

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    statusOptions = RadiusConstants.status;
    countryselectshow = false;
    stateseclectData: any = [];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;

    // after change code
    cityTotalRecords: any;
    currentPageCitySlab = 1;
    cityName: string = '';
    dataSource = new MatTableDataSource<any>([]);
    page = 1;
    pageSize = 5;
    // totalRecords = 0;
    cityitemPerpage = RadiusConstants.ITEMS_PER_PAGE;


    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private cityManagementService: CityManagementService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService,
        private statemanagementService: StateManagementService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService

    ) {
        this.createAccess = loginService.hasPermission(MASTERS.CITY_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.CITY_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.CITY_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.cityFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            countryId: ["", Validators.required],
            stateId: ["", Validators.required],
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
        this.getStateList();
        this.getCityListData("");
        this.statePojo = {};
        this.countryPojo = {};
        this.cityData = {
            countryId: "",
        };
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);

        if (this.currentPageCitySlab > 1) {
            this.currentPageCitySlab = 1;
        }
        if (!this.searchkey) {
            this.getCityListData(this.showItemPerPage);
        } else {
            this.searchCity();
        }
    }

    getCountryList() {
        const url = "/country/all";
        this.cityManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
                this.dataSource = new MatTableDataSource<any>(this.countryListData);
                // this.cityTotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
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
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }


    canExit() {
        if (!this.cityFormGroup.dirty) return true;
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

    addEditCity(cityId) {
        this.submitted = true;
        if (this.cityFormGroup.valid) {
            const cityName = this.cityFormGroup.controls.name.value;
            const cityStatus = this.cityFormGroup.controls.status.value;
            const countryId = this.cityFormGroup.controls.countryId.value;
            const stateId = this.cityFormGroup.controls.stateId.value;

            const selectedCountry = this.countryListData?.find(c => c.id == countryId) || {};

            const selectedState = this.stateListData?.find(s => s.id == stateId) || {};

            this.cityData.name = cityName;
            this.cityData.status = cityStatus;
            this.cityData.countryPojo = selectedCountry;
            this.cityData.countryId = selectedCountry?.id || '';
            this.cityData.statePojo = selectedState;
            this.cityData.stateId = selectedState?.id || '';

            this.cityFormGroup.patchValue({
                countryId: selectedCountry?.id,
                stateId: selectedState?.id,
            });

            if (cityId) {
                const url = "/city/" + cityId;
                this.cityData.delete = false;
                this.cityData.isDelete = false;


                this.cityManagementService.updateMethod(url, this.cityData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isCityEdit = false;
                        this.cityFormGroup.reset();
                        this.commondropdownService.clearCache("/city/all");
                        this.resetForm();
                        this.toastr.success(`Successfully Updated`, 'Success!');
                        this.getCityListData("");
                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchCity();
                        } else {
                            this.getCityListData("");
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
                const url = "/city";
                this.cityData.name = this.cityFormGroup.controls.name.value;
                this.cityData.status = this.cityFormGroup.controls.status.value;

                this.cityData.statePojo = this.statePojo;
                this.cityData.statePojo.countryPojo = this.countryPojo;

                const countryId = this.cityFormGroup.controls.countryId.value;
                const stateId = this.cityFormGroup.controls.stateId.value;

                const selectedCountry = this.countryListData.find(c => c.id == countryId);
                const selectedState = this.stateListData.find(c => c.id == stateId);

                this.cityData.countryPojo = selectedCountry || {};
                this.cityData.countryId = selectedCountry?.id || '';
                this.cityData.statePojo = selectedState || {};
                this.cityData.stateId = selectedState?.id || '';

                this.cityManagementService.postMethod(url, this.cityData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isCityEdit = false;
                        this.cityFormGroup.reset();
                        this.cityFormGroup.controls.status.setValue("");

                        this.commondropdownService.clearCache("/city/all");
                        this.resetForm();
                        this.toastr.success(`Successfully Created`, 'Success!');
                        this.getCityListData("");
                        if (this.searchkey) {
                            this.searchCity();
                        } else {
                            this.getCityListData("");
                        }
                    },
                    (error: any) => {

                        if (error.error.status == 406) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    }
                );
            }
        }
    }

    getCountryById(countryId) {
        if (countryId) {
            //this.spinner.show();
            const url = "/country/" + countryId;
            this.cityManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.countryPojo.name = response.countryData.name;
                    this.countryPojo.id = response.countryData.id;
                    this.countryPojo.status = response.countryData.status;
                    this.cityData.countryId = response.countryData.name;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    getStateById(stateId) {
        if (stateId) {
            //
            const url = "/state/" + stateId;
            this.cityManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.statePojo.name = response.stateData.name;
                    this.statePojo.id = response.stateData.id;
                    this.statePojo.status = response.stateData.status;
                    this.cityData.countryId = response.stateData.countryId;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    getCityListData(list) {
        const url = "/city/list";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageCitySlab;
        if (list) {
            size = list;
            this.cityitemPerpage = list;
        } else {
            size = this.cityitemPerpage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
        };

        this.cityManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
                this.dataSource = new MatTableDataSource<any>(this.cityListData);
                this.cityTotalRecords = response.pageDetails.totalRecords;
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    editCity(cityId) {
        if (cityId) {
            const url = "/city/" + cityId;
            this.cityManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isCityEdit = true;
                    this.countryselectshow = true;
                    this.viewCityListData = response.cityList;

                    const dialogRef2 = this.dialog.open(AddEditCityManagmentComponent, {
                        width: '800px',
                        data: {
                            isEdit: true,
                            title: 'Update ' + this.cityTitle,
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            stateData: this.viewCityListData,
                            countryTitle: this.countryTitle,
                            subCountryTitle: this?.stateTitle,
                            cityTitle: this.cityTitle
                        }
                    });
                    dialogRef2.afterClosed().subscribe(result => {
                        if (result) {
                            this.cityFormGroup.patchValue({
                                name: result.name,
                                status: result.status,
                                countryId: result.countryId || null,
                                stateId: result.stateId || null
                            })
                            this.addEditCity(cityId);
                        } else {
                        }
                    });
                    // this.cityFormGroup.patchValue({
                    //     name: this.viewCityListData.name,
                    //     status: this.viewCityListData.status,
                    //     countryId: this.viewCityListData.countryId || null,
                    //     stateId: this.viewCityListData.statePojo?.id || null
                    // });

                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }


    searchCity() {
        if (!this.searchkey || this.searchkey !== this.searchCityName) {
            this.currentPageCitySlab = 1;
        }
        this.searchkey = this.searchCityName;
        if (this.showItemPerPage) {
            this.cityitemPerpage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchCityName.trim();

        this.searchData.page = this.currentPageCitySlab;
        this.searchData.pageSize = this.cityitemPerpage;

        const url = "/city/search";

        this.cityManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
                this.cityTotalRecords = response.pageDetails.totalRecords;

                this.dataSource = new MatTableDataSource<any>(this.cityListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            (error: any) => {
                this.cityTotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.cityListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }

    clearSearchCity() {
        this.searchCityName = "";
        this.searchkey = "";
        this.getCityListData("");
        this.submitted = false;
        this.isCityEdit = false;
        this.cityFormGroup.reset();
        this.cityFormGroup.controls.name.setValue("");
        this.cityFormGroup.controls.status.setValue("");
        this.cityFormGroup.controls.countryId.setValue("");
        this.cityFormGroup.controls.stateId.setValue("");
    }

    deleteCity(cityId) {
        this.resetForm();
        const url = "/city/" + cityId;
        this.cityManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                this.resetForm();
                if (this.currentPageCitySlab != 1 && this.cityListData.length == 1) {
                    this.currentPageCitySlab = this.currentPageCitySlab - 1;
                }
                this.clearSearchCity();
                this.resetForm();
                this.toastr.success(`Successfully Deleted`, 'Success!');
                if (this.searchkey) {
                    this.searchCity();
                } else {
                    this.getCityListData("");
                }
            },
            (error: any) => {
                if (error.error.status == 405 || error.error.status == 417) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }


    // after changes 


    displayedColumns = ['id', 'Olt', 'sub-Country', 'Country', 'Status', 'Action'];

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    onSubmit() {
        this.submitted = true;

        if (this.cityFormGroup.invalid) {
            return;
        }

        if (this.isCityEdit) {
            this.addEditCity(this.viewCityListData.id);
        } else {
            this.addEditCity('');
        }
    }

    private resetForm() {
        this.submitted = false;
        this.isCityEdit = false;
        this.cityFormGroup.reset();
        this.cityFormGroup.controls.status.setValue('');
        this.cityFormGroup.reset({
            name: '',
            countryId: '',
            stateId: '',
            status: ''
        });
        this.cityFormGroup.markAsPristine();
        this.cityFormGroup.markAsUntouched();

        Object.keys(this.cityFormGroup.controls).forEach(key => {
            this.cityFormGroup.get(key)?.setErrors(null);
        });
    }

    pageChangeCityList(event: PageEvent) {
        this.currentPageCitySlab = event.pageIndex + 1;
        this.cityitemPerpage = event.pageSize;
        if (this.searchkey) {
            this.searchCity();
        } else {
            this.getCityListData("");
        }
    }


    deleteConfirmonCityDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete OLT',
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteCity(item.id);
            } else {
            }
        });
    }

    confirmAndDeleteCity(cityId: number) {
        this.confirmationService.confirm({
            message: "Do you want to delete this " + this.cityTitle + "?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteCity(cityId);
            },
            reject: () => {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                }

            },
        });
    }

    addEditCityDialog() {
        const dialogRef = this.dialog.open(AddEditCityManagmentComponent, {
            width: '800px',
            data: {
                isEdit: false,
                title: 'Create ' + this.cityTitle,
                addLabel: true,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                countryTitle: this.countryTitle,
                subCountryTitle: this?.stateTitle,
                cityTitle: this.cityTitle
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.cityFormGroup = this.fb.group({
                    name: result.name,
                    status: result.status,
                    countryId: result.countryId,
                    stateId: result.stateId
                });
                this.addEditCity("");
            } else {
            }
        });
    }
}
