import { Component, OnInit, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { Regex } from "src/app/constants/regex";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { COUNTRY, CITY, STATE, PINCODE, AREA, REGEX } from "src/app/RadiusUtils/RadiusConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CityManagementComponent } from "../city-management/city-management.component";
import { CityManagementService } from "src/app/service/city-management.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatDialog } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { AddEditPincodeManagmentComponent } from "./add-edit-pincode-managment/add-edit-pincode-managment.component";
import { StateManagementService } from "src/app/service/state-management.service";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-pincode-management",
    templateUrl: "./pincode-management.component.html",
    styleUrls: ["./pincode-management.component.css"],
    standalone: false
})
export class PincodeManagementComponent implements OnInit {
    regex = "String";
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    pincodeFormGroup: UntypedFormGroup;
    pincodeListData: any = [];
    cityListData: any = [];
    countryListData: any = [];
    stateListData: any;
    submitted: boolean = false;
    createPincodeData: any;
    currentPagePincodeListdata = 1;
    pincodeListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    pincodeListdatatotalRecords: any;
    isPincodeEdit: boolean = false;
    viewPincodeListData: any = [];
    searchPincodeName = "";
    cityDetail: any;
    deletedata: any = {
        id: "",
        cityId: "",
        cityName: "",
        code: "",
        countryId: "",
        countryName: "",
        name: "",
        pincodeId: "",
        stateId: "",
        stateName: "",
        status: ""
    };

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: string;
    totalDataListLength = 0;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    inputshowSelsctData: boolean = false;
    statusOptions = RadiusConstants.status;

    // after change code
    pincodetotalRecords: any;
    currentPagePincodeSlab = 1;
    pincodeName: string = '';
    dataSource = new MatTableDataSource<any>([]);
    page = 1;
    pageSize = 5;
    totalRecords = 0;
    searchData: any;
    statePojo: any = {};
    countryPojo: any = {};
    cityPojo: any = {};
    countryselectshow = false;
    pincodeData: any = {};
    deletedDatas: any = [];



    pincodeitemPerpage = RadiusConstants.ITEMS_PER_PAGE;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private pincodeManagementService: PincodeManagementService,
        private stateManagmentService: StateManagementService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService,
        private cityManagementService: CityManagementService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.createAccess = loginService.hasPermission(MASTERS.PINCODE_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.PINCODE_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.PINCODE_EDIT);
        // this.isPincodeEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.pincodeFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            countryId: ["", Validators.required],
            stateId: ["", Validators.required],
            cityId: ["", Validators.required],
            status: ["", Validators.required]
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
        this.getStateList();
        this.getCountryList();
        this.getCityList();
        this.getPincodeListData("");
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);

        if (this.currentPagePincodeSlab > 1) {
            this.currentPagePincodeSlab = 1;
        }
        if (!this.searchkey) {
            this.getPincodeListData(this.showItemPerPage);
        } else {
            this.searchPincode();
        }
    }

    getPincodeListData(list) {
        const url = "/pincode";
        let size = this.pincodeitemPerpage;
        this.searchkey = "";
        let pageList = this.currentPagePincodeSlab;
        if (list) {
            size = list;
            this.pincodeitemPerpage = list;
        } else {
            size = this.pincodeitemPerpage;
        }
        let pincodedata = {
            page: this.currentPagePincodeSlab,
            pageSize: size
        };
        this.pincodeManagementService.postMethod(url, pincodedata).subscribe(
            (response: any) => {
                this.pincodeListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.pincodeListData);
                this.pincodetotalRecords = response.totalRecords;
                this.searchkey = "";

                if (response.responseCode == 204) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    getCityList() {
        const url = "/city/all";
        this.cityManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    getCountryList() {
        const url = "/country/all";
        this.pincodeManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    getStateList() {
        const url = "/state/all";
        this.pincodeManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    // addEditPincode(pincodeId: any) {
    //     this.submitted = true;
    //     const countryId = this.pincodeFormGroup.controls.countryId.value;
    //     const stateId = this.pincodeFormGroup.controls.stateId.value;
    //     const cityId = this.pincodeFormGroup.controls.cityId.value;

    //     const selectedCountry = this.countryListData?.find(c => c.id == countryId) || {};
    //     const selectedState = this.stateListData?.find(s => s.id == stateId) || {};
    //     const selectedCity = this.cityListData?.find(c => c.id == cityId) || {};


    //     if (pincodeId) {
    //         const url = "/pincode/update";
    //         this.pincodeData.delete = false;
    //         this.pincodeData.isDelete = false;
    //         this.viewPincodeListData = this.pincodeFormGroup.value;

    //         const payload = {
    //             name: this.pincodeFormGroup.controls.name.value,
    //             status: this.pincodeFormGroup.controls.status.value,
    //             countryId: countryId,
    //             countryPojo: selectedCountry,
    //             stateId: stateId,
    //             statePojo: selectedState,
    //             cityId: cityId,
    //             cityPojo: selectedCity,
    //         };
    //         console.log("payload edit=>", payload)

    //         this.pincodeManagementService.postMethod(url, payload).subscribe(
    //             (response: any) => {
    //                 this.submitted = false;
    //                 this.isPincodeEdit = false;
    //                 this.pincodeFormGroup.reset();
    //                 this.commondropdownService.clearCache("/city/all");
    //                 this.resetForm();

    //                 if (response.responseCode == 406 || response.responseCode == 417) {
    //                     this.messageService.add({
    //                         severity: "info",
    //                         summary: "Info",
    //                         detail: response.responseMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 } else {
    //                     this.clearPincode();
    //                     //   this.commondropdownService.clearCache("/pincode/all");
    //                     this.commondropdownService.getAllPinCodeData();
    //                     this.messageService.add({
    //                         severity: "success",
    //                         summary: "Successfully",
    //                         detail: response.responseMessage,
    //                         icon: "far fa-check-circle"
    //                     });
    //                     this.getPincodeListData("");
    //                     this.submitted = false;
    //                     if (this.searchkey) {
    //                         this.searchPincode();
    //                     } else {
    //                         this.getPincodeListData("");
    //                     }
    //                 }
    //             },
    //             (error: any) => {
    //                 this.messageService.add({
    //                     severity: "error",
    //                     summary: "Error",
    //                     detail: error.error.ERROR,
    //                     icon: "far fa-times-circle"
    //                 });
    //             }
    //         );
    //     } else {
    //         const url = "/pincode/save";
    //         this.viewPincodeListData.name = this.pincodeFormGroup.controls.name.value;
    //         this.viewPincodeListData.status = this.pincodeFormGroup.controls.status.value;

    //         this.viewPincodeListData.statePojo = this.statePojo;
    //         this.viewPincodeListData.statePojo.countryPojo = this.countryPojo;
    //         this.viewPincodeListData.countryPojo = this.cityPojo;

    //         const countryId = this.pincodeFormGroup.controls.countryId.value;
    //         const stateId = this.pincodeFormGroup.controls.stateId.value;
    //         const cityId = this.pincodeFormGroup.controls.cityId.value;


    //         const selectedCountry = this.countryListData?.find(c => c.id == countryId) || {};
    //         const selectedState = this.stateListData?.find(s => s.id == stateId) || {};
    //         const selectedCity = this.cityListData?.find(c => c.id == cityId) || {};

    //         this.viewPincodeListData.countryPojo = selectedCountry || {};
    //         this.viewPincodeListData.countryId = selectedCountry?.id || '';

    //         this.viewPincodeListData.statePojo = selectedState || {};
    //         this.viewPincodeListData.stateId = selectedState?.id || '';

    //         this.viewPincodeListData.cityPojo = selectedCity || {};
    //         this.viewPincodeListData.cityId = selectedCity?.id || '';

    //         this.createPincodeData = this.pincodeFormGroup.value;
    //         const payload = {
    //             name: this.pincodeFormGroup.controls.name.value,
    //             status: this.pincodeFormGroup.controls.status.value,
    //             countryId: countryId,
    //             countryPojo: selectedCountry || {},
    //             stateId: stateId,
    //             statePojo: selectedState || {},
    //             cityId: cityId,
    //             cityPojo: selectedCity || {},
    //         };

    //         console.log("created", this.createPincodeData)
    //         this.pincodeManagementService.postMethod(url, this.createPincodeData).subscribe(
    //             (response: any) => {
    //                 this.submitted = false;
    //                 this.isPincodeEdit = false;
    //                 this.pincodeFormGroup.reset();
    //                 this.pincodeFormGroup.controls.status.setValue("");
    //                 this.commondropdownService.clearCache("/pincode/all");
    //                 this.resetForm();

    //                 if (response.responseCode == 406) {
    //                     this.messageService.add({
    //                         severity: "info",
    //                         summary: "Info",
    //                         detail: response.responseMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 } else {
    //                     this.clearPincode();
    //                     this.commondropdownService.getAllPinCodeData();
    //                     this.messageService.add({
    //                         severity: "success",
    //                         summary: "Successfully",
    //                         detail: response.responseMessage,
    //                         icon: "far fa-check-circle"
    //                     });
    //                     this.getPincodeListData("");
    //                     if (this.searchkey) {
    //                         this.searchPincode();
    //                     } else {
    //                         this.getPincodeListData("");
    //                     }
    //                     this.countryListData = "";
    //                     this.stateListData = "";
    //                 }
    //             },
    //             (error: any) => {
    //                 this.messageService.add({
    //                     severity: "error",
    //                     summary: "Error",
    //                     detail: error.error.ERROR,
    //                     icon: "far fa-times-circle"
    //                 });
    //             }
    //         );
    //     }
    // }
    addEditPincode(pincodeId) {
        this.submitted = true;
        if (this.pincodeFormGroup.valid) {

            if (pincodeId) {
                const url = "/pincode/update";
                this.pincodeData.delete = false;
                this.pincodeData.isDelete = false;

                const payload = {
                    cityId: this.pincodeFormGroup.controls.cityId.value,
                    countryId: this.pincodeFormGroup.controls.countryId.value,
                    pincode: this.pincodeFormGroup.controls.name.value,
                    pincodeid: pincodeId,
                    stateId: this.pincodeFormGroup.controls.stateId.value,
                    status: this.pincodeFormGroup.controls.status.value,
                };


                this.pincodeManagementService.postMethod(url, payload).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isPincodeEdit = false;
                        this.pincodeFormGroup.reset();
                        this.commondropdownService.clearCache("/city/all");
                        this.resetForm();

                        if (response.responseCode == 406 || response.responseCode == 417) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.clearPincode();
                            //   this.commondropdownService.clearCache("/pincode/all");
                            this.commondropdownService.getAllPinCodeData();
                            this.toastr.success(`Successfully Updated`, 'Success!');
                            this.getPincodeListData("");
                            this.submitted = false;
                            if (this.searchkey) {
                                this.searchPincode();
                            } else {
                                this.getPincodeListData("");
                            }
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            } else {
                const url = "/pincode/save";
                this.viewPincodeListData.name = this.pincodeFormGroup.controls.name.value;
                this.viewPincodeListData.status = this.pincodeFormGroup.controls.status.value;

                this.viewPincodeListData.statePojo = this.statePojo;
                this.viewPincodeListData.statePojo.countryPojo = this.countryPojo;
                this.viewPincodeListData.countryPojo = this.cityPojo;

                const countryId = this.pincodeFormGroup.controls.countryId.value;
                const stateId = this.pincodeFormGroup.controls.stateId.value;
                const cityId = this.pincodeFormGroup.controls.cityId.value;

                const selectedCountry = this.countryListData.find(c => c.id == countryId);
                const selectedState = this.stateListData.find(c => c.id == stateId);
                const selectedCity = this.cityListData.find(c => c.id == cityId);

                this.viewPincodeListData.countryPojo = selectedCountry || {};
                this.viewPincodeListData.countryId = selectedCountry?.id || '';

                this.viewPincodeListData.statePojo = selectedState || {};
                this.viewPincodeListData.stateId = selectedState?.id || '';

                this.viewPincodeListData.cityPojo = selectedCity || {};
                this.viewPincodeListData.cityId = selectedCity?.id || '';

                const payload = {
                    cityId: this.pincodeFormGroup.controls.cityId.value,
                    countryId: this.pincodeFormGroup.controls.countryId.value,
                    pincode: this.pincodeFormGroup.controls.name.value,
                    stateId: this.pincodeFormGroup.controls.stateId.value,
                    status: this.pincodeFormGroup.controls.status.value,

                };
                this.pincodeManagementService.postMethod(url, payload).subscribe(
                    (response: any) => {

                        this.submitted = false;
                        this.isPincodeEdit = false;
                        this.pincodeFormGroup.reset();
                        this.pincodeFormGroup.controls.status.setValue("");
                        // this.commondropdownService.clearCache("/pincode/all");
                        this.resetForm();

                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            this.clearPincode();
                            this.commondropdownService.getAllPinCodeData();
                            this.toastr.success(`${response.responseMessage}`, 'Success!');
                            this.getPincodeListData("");
                            if (this.searchkey) {
                                this.searchPincode();
                            } else {
                                this.getPincodeListData("");
                            }
                            this.countryListData = [];
                            this.stateListData = [];
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            }
        }
    }

    async getPincodeById(pincodeId) {
        const url = "/pincode/" + pincodeId;
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.viewPincodeListData = response.data;
                this.deletedata = this.viewPincodeListData;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }






    canExit() {
        if (!this.pincodeFormGroup.dirty) return true;
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

    clearPincode() {
        this.submitted = false;
        this.isPincodeEdit = false;
        this.inputshowSelsctData = false;
        this.searchPincodeName = "";
        this.searchkey = "";
        this.getPincodeListData("");
        this.submitted = false;
        this.isPincodeEdit = false;
        this.pincodeFormGroup.reset();
        this.pincodeFormGroup.controls.name.setValue("");
        this.pincodeFormGroup.controls.countryId.setValue("");
        this.pincodeFormGroup.controls.stateId.setValue("");
        this.pincodeFormGroup.controls.cityId.setValue("");
        this.pincodeFormGroup.controls.status.setValue("");
    }

    editPincode(pincodeId: Number) {
        if (pincodeId) {
            this.getStateList();
            this.getCountryList();
            const url = "/pincode/" + pincodeId;
            this.pincodeManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isPincodeEdit = true;
                    this.countryselectshow = true;
                    this.viewPincodeListData = response.data;

                    const dialogRef2 = this.dialog.open(AddEditPincodeManagmentComponent, {
                        width: '1000px',
                        data: {
                            isEdit: true,
                            title: 'Update ' + this.pincodeTitle,
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            pincodeData: this.viewPincodeListData,
                            pincodeTitle: this.pincodeTitle,
                            countryTitle: this.countryTitle,
                            subCountryTitle: this?.stateTitle,
                            cityTitle: this.cityTitle
                        }
                    });
                    dialogRef2.afterClosed().subscribe(result => {
                        if (result) {
                            this.pincodeFormGroup.patchValue({
                                name: result.name,
                                status: result.status,
                                countryId: result.countryId || null,
                                stateId: result.stateId || null,
                                cityId: result.cityId || null,
                            })
                            this.addEditPincode(pincodeId);
                        } else {
                        }
                    });

                    // for auto selecting country and state
                    // this.pincodeFormGroup.patchValue({
                    //     name: this.viewPincodeListData.pincode,
                    //     status: this.viewPincodeListData.status,
                    //     countryId: this.viewPincodeListData.countryId || null,
                    //     stateId: this.viewPincodeListData.stateId || null,
                    //     cityId: this.viewPincodeListData.cityId || null,
                    // });

                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    searchPincode() {
        this.searchkey = this.searchPincodeName.trim();
        if (this.showItemPerPage == 1) {
            this.pincodeListdataitemsPerPage = this.pageITEM;
        } else {
            this.pincodeListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchPincodeName = this.searchPincodeName.trim().replace(/\\/g, "");
        const url = "/pincode/search?s=" + this.searchPincodeName;
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.pincodeListData = response.dataList || [];
                this.pincodetotalRecords = response.totalRecords || 0;

                this.dataSource = new MatTableDataSource<any>(this.pincodeListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                }
            },
            (error: any) => {
                this.pincodetotalRecords = 0;
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    clearSearchPincode() {
        this.searchPincodeName = "";
        this.searchkey = "";

        this.currentPagePincodeSlab = 1;
        this.pincodeitemPerpage = this.pageITEM;

        this.submitted = false;
        this.isPincodeEdit = false;
        this.pincodeFormGroup.reset();
        this.pincodeFormGroup.controls.name.setValue("");
        this.pincodeFormGroup.controls.status.setValue("");
        this.pincodeFormGroup.controls.countryId.setValue("");
        this.pincodeFormGroup.controls.stateId.setValue("");
        this.pincodeFormGroup.controls.cityId.setValue("");

        this.getPincodeListData(this.pincodeitemPerpage);
    }

    deletePincode(item: any) {
        this.resetForm();
        const url = "/pincode/delete";
        this.pincodeManagementService.postMethod(url, item).subscribe(
            (response: any) => {
                this.resetForm();

                if (this.currentPagePincodeSlab != 1 && this.pincodeListData.length == 1) {
                    this.currentPagePincodeSlab = this.currentPagePincodeSlab - 1;
                }
                this.clearSearchPincode();
                this.resetForm();
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.error(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
                if (this.searchkey) {
                    this.searchPincode();
                } else {
                    this.getPincodeListData("");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.responseMessage}`, 'Error!');
            }
        );
    }

    // after changes 

    displayedColumns = ['id', 'Road', 'OLT', 'sub-County', 'County', 'Status', 'Action'];

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    onSubmit() {
        this.submitted = true;

        if (this.pincodeFormGroup.invalid) {
            return;
        }

        if (this.isPincodeEdit) {
            this.addEditPincode(this.viewPincodeListData.id);
        } else {
            this.addEditPincode('');
        }
    }

    private resetForm() {
        this.submitted = false;
        this.isPincodeEdit = false;
        this.pincodeFormGroup.reset();
        this.pincodeFormGroup.controls.status.setValue('');
        this.pincodeFormGroup.reset({
            name: '',
            countryId: '',
            stateId: '',
            cityId: '',
            status: ''
        });
        this.pincodeFormGroup.markAsPristine();
        this.pincodeFormGroup.markAsUntouched();

        Object.keys(this.pincodeFormGroup.controls).forEach(key => {
            this.pincodeFormGroup.get(key)?.setErrors(null);
        });
    }

    pageChangePincodeList(event: PageEvent) {
        this.currentPagePincodeSlab = event.pageIndex + 1;
        this.pincodeitemPerpage = event.pageSize;
        if (this.searchkey) {
            this.searchPincode();
        } else {
            this.getPincodeListData("");
        }
    }

    deleteConfirmonPincodeDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Road',
                description: `Are you sure you want to delete "${item.pincode}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deletePincode(item);
            } else {
            }
        });
    }

    confirmAndDeleteCity(pincodeId: number) {
        this.confirmationService.confirm({
            message: "Do you want to delete this " + this.pincodeTitle + "?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deletePincode(pincodeId);
            },
            reject: () => {
                this.toastr.info(`You have rejected`, 'Info!');
                // this.messageService.add({
                //     severity: "info",
                //     summary: "Rejected",
                //     detail: "You have rejected",
                // });
            },
        });
    }
    addEditPincodeDialog() {
        const dialogRef = this.dialog.open(AddEditPincodeManagmentComponent, {
            width: '1000px',
            data: {
                isEdit: false,
                title: 'Create ' + this.pincodeTitle,
                addLabel: true,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                pincodeTitle: this.pincodeTitle,
                countryTitle: this.countryTitle,
                subCountryTitle: this?.stateTitle,
                cityTitle: this.cityTitle
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.pincodeFormGroup.patchValue({
                    name: result.name,
                    status: result.status,
                    countryId: result.countryId || null,
                    stateId: result.stateId || null,
                    cityId: result.cityId || null,
                })
                this.addEditPincode('');
            } else {
            }
        });
    }
}
