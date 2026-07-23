import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import {
    UntypedFormBuilder,
    Validators,
    UntypedFormGroup,
    UntypedFormArray,
    FormArray,
    FormControl
} from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AREA, BUILDING, CITY, COUNTRY, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { BuildingManagementService } from "src/app/service/building-management.service";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
import { SelectBuildingDialogComponent } from "../common/select-building-dialog/select-building-dialog.component";

@Component({
    selector: "app-building-management",
    templateUrl: "./building-management.component.html",
    styleUrls: ["./building-management.component.css"],
    standalone: false
})
export class BuildingManagementComponent implements OnInit, IDeactivateGuard {
    @ViewChild('homePassDialog') homePassDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    selectBuildingDialogRef: MatDialogRef<SelectBuildingDialogComponent>;

    title = BUILDING;
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    areaTitle = AREA;
    mvnoId: any;
    inputshowSelsctData: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    buildingFormGroup: UntypedFormGroup;
    uploadDocForm: UntypedFormGroup;
    selectedMappingFrom: string = "";
    selectedFilePreview: File[] = [];

    submitted: boolean = false;
    countryData: CountryManagement;
    buildingMgmt: any;
    countryListData: any;
    isBuildingEdit: boolean = false;
    buildingData: any;
    currentPageBuildingSlab = 1;
    buildingItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    buildingTotalRecords: any;
    searchBuildingName: any = "";
    searchData: any;
    selectedFile: any;

    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    currentPageAreaListdata = 1;
    areaListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    areaListdatatotalRecords: any;
    showItemPerPage: any;
    buildingId;
    searchkey: string;

    public loginService: LoginService;
    cityListData: any;
    stateListData: any;
    areaListData: any[];
    cityDetail: any;
    deletedata: any = {
        id: "",
        cityId: "",
        countryId: "",
        name: "",
        stateId: "",
        status: "",
        mvnoId: "",
        buId: "",
        isDeleted: "",
        areaId: ""
    };
    methodTypeData = [{ label: "Range" }, { label: "CSV" }, { label: "Manual" }];
    selectedMethodType: string = "";
    manualEntries: number[] = [];
    dunningData: any;
    areaList: any;
    pinCodeList: any;
    areaDropdownList: any;
    pinCodeDropdownList: any;
    pincodeListData: any;
    pincodeListdatatotalRecords: any;
    pincodeListdataitemsPerPage: any;
    currentPagePincodeListdata: any;
    methodOptions = [
        { label: "Range", value: "Range" },
        { label: "CSV", value: "CSV" },
        { label: "Manual", value: "Manual" }
    ];
    buildingMappings: any;
    buildingNameListData: any[];
    buildingTypeData: any[];
    displayedColumns = ["Name", "Action"];
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private buildingMangementService: BuildingManagementService,
        private commondropdownService: CommondropdownService,
        loginService: LoginService,
        private dialog: MatDialog,
        private toastr: ToastrService,
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(MASTERS.BUILDING_MGMT_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.BUILDING_MGMT_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.COUNTRY_EDIT);
    }

    ngOnInit(): void {
        this.buildingFormGroup = this.fb.group({
            buildingName: ["", Validators.required],
            methodType: [""],
            pincode: [""],
            areaId: [""],
            subAreaId: [""],
            selectedMethod: ["", Validators.required],
            buildingType: [""],
            rangeStart: [""],
            rangeEnd: [""],
            manualControls: this.fb.array([]),
            file: [""]
        });
        this.uploadDocForm = this.fb.group({
            file: ["", Validators.required]
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
        this.getMappingFrom();
        this.getBuildingList("");
        this.getBuildingType();
        this.mvnoId = Number(localStorage.getItem("mvnoId"));
    }

    canExit() {
        if (!this.buildingFormGroup.dirty) return true;
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

    getBuildingType() {
        const url = "/commonList/buildingType";
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                this.buildingTypeData = response.dataList;
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onChnage(event) {
        this.selectedMappingFrom = this.dunningData[0].mappingFrom;
        if (this.selectedMappingFrom === "Pin Code") {
            let data = this.pincodeListData.find(x => x.pincodeid == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.pincode);
        } else if (this.selectedMappingFrom === "Area") {
            let data = this.areaListData.find(x => x.id == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.name);
        }
    }
    addOrUpdateBuilding(id?: number) {
        this.submitted = true;
        const formData = new FormData();
        let buildingMappings = [];

        if (
            this.selectedFile &&
            this.selectedFile.type === "text/csv" &&
            this.buildingMappings.length > 0
        ) {
            buildingMappings = this.buildingMappings;
        } else {
            if (this.buildingFormGroup.value.selectedMethod === "Range") {
                const rangeStart = this.buildingFormGroup.value.rangeStart;
                const rangeEnd = this.buildingFormGroup.value.rangeEnd;
                buildingMappings = this.generateBuildingNumbers(rangeStart, rangeEnd);
            } else if (this.buildingFormGroup.value.selectedMethod === "Manual") {
                const manualInputs = this.buildingFormGroup.value.manualControls || [];
                buildingMappings = manualInputs.map(num => ({ buildingNumber: num }));
            }
        }

        if (id) {
            buildingMappings = buildingMappings.map(mapping => ({
                ...mapping,
                buildingMgmtId: id,
                isDeleted: false
            }));
        }

        const entityDTO = {
            buildingName: this.buildingFormGroup.value.buildingName,
            buildingType: this.buildingFormGroup.value?.buildingType,
            pincodeId: this.buildingFormGroup.value.pincode,
            areaId: this.buildingFormGroup.value.areaId || null,
            subAreaId: this.buildingFormGroup.value.subAreaId || null,
            mvnoId: this.mvnoId,
            buid: null,
            isDeleted: false,
            buildingMappings
        };

        if (id) {
            entityDTO["buildingMgmtId"] = id; // Ensure buildingMgmtId is added for update
        }

        formData.append("entityDTO", JSON.stringify(entityDTO));

        const fileArray: FileList = this.buildingFormGroup.controls.file.value;
        if (fileArray && fileArray.length > 0) {
            Array.from(fileArray).forEach(file => {
                formData.append("file", file);
            });
        }

        const url = id ? "/buildingmgmt/update" : "/buildingmgmt/save";
        this.buildingMangementService.postMethod(url, formData).subscribe(
            (response: any) => {
                this.submitted = false;
                // this.messageService.add({
                //   severity: "success",
                //   summary: "Successfully Saved",
                //   detail: response.msg,
                //   icon: "far fa-check-circle"
                // });
                this.toastr.success(`${response.responseMessage}`, 'Success!');

                this.selectedFile = null;
                this.buildingMappings = [];
                this.buildingFormGroup.patchValue({ file: null });

                this.buildingFormGroup.reset();
                this.buildingMangementService.clearCache("/subarea/all");
                this.getBuildingList("");
                if (id) {
                    this.isBuildingEdit = false;
                }
                this.onCancel();
            },
            (error: any) => {
                this.submitted = false;
                // this.messageService.add({
                //   severity:
                //     error.error.responseCode == 417 || error.error.responseCode == 406 ? "info" : "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    deletUploadedFile(event: any) {
        var temp: File[] = this.selectedFile?.filter((item: File) => item?.name != event);
        this.selectedFile = temp;
        this.uploadDocForm.patchValue({
            file: temp
        });
    }

    // Method to generate building numbers from the range
    generateBuildingNumbers(rangeStart: string, rangeEnd: string): Array<any> {
        // If either rangeStart or rangeEnd is missing, return an empty array
        if (!rangeStart || !rangeEnd) {
            return [];
        }

        const prefixStart = rangeStart.replace(/[0-9]+/g, ""); // Extract prefix (e.g., 'E-')
        const prefixEnd = rangeEnd.replace(/[0-9]+/g, ""); // Extract prefix (e.g., 'E-')

        // If prefixes are not the same, return an empty array
        if (prefixStart !== prefixEnd) {
            return [];
        }

        const startNum = parseInt(rangeStart.replace(/[^\d]/g, ""), 10); // Get number from rangeStart
        const endNum = parseInt(rangeEnd.replace(/[^\d]/g, ""), 10); // Get number from rangeEnd

        const buildingMappings = [];

        // Loop through the numbers from startNum to endNum
        for (let i = startNum; i <= endNum; i++) {
            const buildingNumber = `${prefixStart}${i}`; // Combine prefix with the number
            buildingMappings.push({
                buildingNumber: buildingNumber,
                isDeleted: false
            });
        }

        return buildingMappings;
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageBuildingSlab > 1) {
            this.currentPageBuildingSlab = 1;
        }
        if (!this.searchkey) {
            this.getBuildingList(this.showItemPerPage);
        } else {
            this.searchBuilding();
        }
    }

    editBuilding(id: number) {
        if (id) {
            this.isBuildingEdit = true;
            const url = "/buildingmgmt/" + id;
            this.buildingMangementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isBuildingEdit = true;
                    this.buildingData = response.data;

                    this.dialogRef = this.dialog.open(this.homePassDialog, {
                        width: '1000px'
                    });
                    if (this.selectedMappingFrom === "Pin Code") {
                        const selectedPincode = this.pincodeListData.find(
                            pincode => pincode.pincodeid === this.buildingData.pincodeId
                        );
                        if (selectedPincode) {
                            this.buildingFormGroup.patchValue({
                                pincode: selectedPincode.pincodeid
                            });
                        }
                    } else if (this.selectedMappingFrom === "Area") {
                        const selectedArea = this.areaListData.find(
                            area => area.id === this.buildingData.areaId
                        );
                        if (selectedArea) {
                            this.buildingFormGroup.patchValue({
                                areaId: selectedArea.id
                            });
                        }
                    }

                    this.buildingFormGroup.patchValue(this.buildingData);


                    let mappingMethod = this.buildingData.mappingMethod || "Manual";
                    this.buildingFormGroup.patchValue({ selectedMethod: mappingMethod });

                    if (mappingMethod === "Manual") {
                        this.manualControls.clear();
                        if (
                            this.buildingData.buildingMappings &&
                            this.buildingData.buildingMappings.length > 0
                        ) {
                            this.buildingData.buildingMappings.forEach(mapping => {
                                this.manualControls.push(this.fb.control(mapping.buildingNumber));
                            });
                        }
                    } else if (mappingMethod === "Range") {
                        if (
                            this.buildingData.buildingMappings &&
                            this.buildingData.buildingMappings.length > 0
                        ) {
                            const numbers = this.buildingData.buildingMappings.map(mapping => {
                                const parts = mapping.buildingNumber.split("-");
                                return parts.length > 1 ? parseInt(parts[1], 10) : 0;
                            });
                            const min = Math.min(...numbers);
                            const max = Math.max(...numbers);
                            this.buildingFormGroup.patchValue({
                                rangeStart: min,
                                rangeEnd: max
                            });
                        }
                    } else if (mappingMethod === "CSV") {
                        this.buildingMappings = this.buildingData.buildingMappings;
                    }















                    //   const dialogRef = this.dialog.open(AddEditHomePassComponent, {
                    //     width: "800px",
                    //     data: {
                    //       isEdit: true,
                    //       title: "Update " + this.title,
                    //       yesLabel: "Update",
                    //       noLabel: "Cancel",
                    //       createAcS: this.createAccess,
                    //       editAcs: this.editAccess,
                    //       buildingData: this.buildingData,
                    //       inputName: "Enter " + this.title + " Name",
                    //       inputStatus: "Select " + this.title + " Status",
                    //       selectedMappingFrom: this.selectedMappingFrom,
                    //       pincodeListData: this.pincodeListData,
                    //       areaListData: this.areaListData,
                    //       subAreaListData: this.subAreaListData,
                    //       buildingTypeData: this.buildingTypeData,
                    //       methodOptions: this.methodOptions
                    //     }
                    //   });

                    //   dialogRef.afterClosed().subscribe(result => {
                    //     if (result) {
                    //       // Patch basic fields
                    //       this.buildingFormGroup.patchValue({
                    //         buildingName: result.buildingName,
                    //         status: result.status,
                    //         selectedMethod: result.selectedMethod,
                    //         pincode: result.pincode,
                    //         areaId: result.areaId,
                    //         subAreaId: result.subAreaId,
                    //         buildingType: result.buildingType,
                    //         rangeStart: result.rangeStart,
                    //         rangeEnd: result.rangeEnd,
                    //         file: result.file
                    //       });

                    //       // Patch manualControls if Method is Manual
                    //       if (result.selectedMethod === "Manual" && result.manualControls?.length) {
                    //         this.manualControls.clear();
                    //         result.manualControls.forEach(controlValue => {
                    //           this.manualControls.push(this.fb.control(controlValue));
                    //         });
                    //       }

                    //       // Patch buildingMappings if Method is CSV
                    //       if (result.selectedMethod === "CSV") {
                    //         this.buildingMappings = result.buildingMappings;
                    //       }

                    //       this.addOrUpdateBuilding(id);
                    //     }
                    //   });
                },
                (error: any) => {
                    //   this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    //   });
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    openSelectBuildingDialogComponent = (): void => {
        const dialogRef = this.dialog?.open(SelectBuildingDialogComponent, {
            width: "80%",
            disableClose: true,
            data: {
                dialogref: this.selectBuildingDialogRef
            }
        });
        dialogRef?.afterClosed().subscribe(res => {
            this.buildingId = res?.id;
            this.buildingFormGroup?.get("subAreaId").setValue(this.buildingId);
            this.buildingFormGroup?.get("buildingName").setValue(res?.name);
        })
    }

    searchBuilding() {
        if (!this.searchkey || this.searchkey !== this.searchBuildingName) {
            this.currentPageBuildingSlab = 1;
        }
        this.searchkey = this.searchBuildingName;
        if (this.showItemPerPage) {
            this.buildingItemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchBuildingName.trim();
        this.searchData.page = this.currentPageBuildingSlab;
        this.searchData.pageSize = this.buildingItemsPerPage;

        const url =
            "/buildingmgmt/search?page=" +
            this.currentPageBuildingSlab +
            "&pageSize=" +
            this.buildingItemsPerPage +
            "&sortBy=buildingMgmtId&sortOrder=0";
        // console.log("this.searchData", this.searchData)
        this.buildingMangementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.buildingNameListData = response.dataList;
                this.buildingTotalRecords = response.totalRecords;
                this.dataSource = new MatTableDataSource<any>(this.buildingNameListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            (error: any) => {
                this.buildingTotalRecords = 0;
                if (error.error.status == 404) {
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.info(`${error.error.msg}`);

                    this.buildingNameListData = [];
                } else {
                    //   this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.response.ERROR,
                    //     icon: "far fa-times-circle"
                    //   });
                    this.toastr.error(`${error.response.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearBuildingCountry() {
        this.searchBuildingName = "";
        this.searchkey = "";
        this.getBuildingList("");
        this.submitted = false;
        this.isBuildingEdit = false;
        this.buildingFormGroup.reset();
    }

    deleteBuilding(buildingMgmt) {
        const url = "/buildingmgmt/delete";
        let obj = {
            buildingMgmtId: buildingMgmt?.buildingMgmtId,
            buildingName: buildingMgmt?.buildingName,
            pincodeId: buildingMgmt?.pincodeId,
            areaId: buildingMgmt?.areaId,
            subAreaId: buildingMgmt?.subAreaId,
            mvnoId: buildingMgmt?.mvnoId,
            buid: buildingMgmt?.buid,
            isDeleted: buildingMgmt?.isDeleted,
            buildingMappings: buildingMgmt?.buildingMappings
        };

        this.buildingMangementService.postMethod(url, obj).subscribe(
            (response: any) => {
                // this.messageService.add({
                //   severity: "success",
                //   summary: "Successfully Deleted",
                //   detail: response.responseMessage,
                //   icon: "far fa-check-circle"
                // });
                this.toastr.success(`${response.responseMessage}`, 'Success!');
                this.getBuildingList("");
            },
            (error: any) => {
                // Handle error response
                console.error(error);
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error?.responseMessage || "Something went wrong!",
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error?.responseMessage}`, 'Failed!');
            }
        );
    }
    deleteConfirmonCountryDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: "Delete " + this.title,
                description: `Are you sure you want to delete "${item.buildingName}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteBuilding(item);
            } else {
            }
        });
    }

    pageChangedCountryList(event: PageEvent) {
        this.currentPageBuildingSlab = event.pageIndex + 1;
        this.buildingItemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.searchBuilding();
        } else {
            this.getBuildingList("");
        }
    }
    getCountryList() {
        const url = "/country/all";
        this.buildingMangementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
            },
            (error: any) => {
                console.log(error, "error");
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getCityList() {
        const url = "/city/all";
        this.buildingMangementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
            },
            (error: any) => {
                console.log(error, "error");
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getStateList() {
        const url = "/state/all";
        this.buildingMangementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
            },
            (error: any) => {
                console.log(error, "error");
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getSelCity(event) {
        const selCity = event.value;
        this.getCityDetailbyd(selCity);
        this.getStateList();
        this.getCountryList();
    }

    getCityDetailbyd(cityId) {
        const url = "/city/" + cityId;
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                this.cityDetail = response.cityList;
                // return
                this.inputshowSelsctData = true;
                this.buildingFormGroup.controls.countryId.patchValue(this.cityDetail.countryId);
                this.buildingFormGroup.controls.stateId.patchValue(this.cityDetail.statePojo.id);
            },
            (error: any) => {
                console.log(error, "error");
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    addManualEntry() {
        this.manualEntries.push(0);
    }

    removeEntry(index: number) {
        this.manualEntries.splice(index, 1);
    }

    onMethodTypeChange(value: string) {
        this.selectedMethodType = value;
        this.manualEntries = []; // Reset manual entries when switching
    }

    getBuildingList(list) {
        const url = "/buildingmgmt";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageBuildingSlab;

        if (list) {
            size = list;
            this.buildingItemsPerPage = list;
        } else {
            size = this.buildingItemsPerPage;
        }

        let plandata = {
            page: pageList,
            pageSize: size
        };

        this.buildingMangementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                if (response && response.dataList) {
                    this.buildingNameListData = response.dataList;
                    this.dataSource = new MatTableDataSource<any>(this.buildingNameListData);

                    this.buildingTotalRecords = response.totalRecords;
                    this.searchkey = "";
                } else {
                    this.buildingNameListData = [];
                }

                this.searchkey = "";
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error?.ERROR || "Unknown error",
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error?.ERROR}`, 'Failed!');
            }
        );
    }

    getMappingFrom() {
        const url = "/buildingRefrence/all";
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                this.dunningData = response.dataList;
                if (this.dunningData?.length > 0) {
                    this.selectedMappingFrom = this.dunningData[0].mappingFrom;
                    if (this.selectedMappingFrom === "Pin Code") {
                        this.getAllPinCodeData();
                    } else if (this.selectedMappingFrom === "Area") {
                        this.getALLAreaData();
                    }
                } else {
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "Please Select First Building Reference Management.",
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.info('Please Select First Building Reference Management.');
                }
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getAllPinCodeData() {
        this.pincodeListData = [];
        const url = "/pincode/getAll";
        this.commondropdownService.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.pincodeListData = response.dataList;
                // console.log("allpincodeNumber", this.allpincodeNumber);
            },
            (error: any) => { }
        );
    }

    getALLAreaData() {
        this.areaListData = [];
        const url = "/area/all";
        this.commondropdownService.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.areaListData = response.dataList;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onMethodChange(event: any) {
        const selectedValue = event.value;
        this.buildingFormGroup.patchValue({ selectedMethod: selectedValue });

        this.buildingMappings = [];

        if (selectedValue === "Manual") {
            this.selectedFile = null;
            this.manualControls.clear();
            this.buildingFormGroup.patchValue({ file: null });
            this.addManualInput();
            this.buildingFormGroup.controls.file.clearValidators();
            this.buildingFormGroup.controls.file.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeStart.clearValidators();
            this.buildingFormGroup.controls.rangeStart.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeEnd.clearValidators();
            this.buildingFormGroup.controls.rangeEnd.updateValueAndValidity();
        } else if (selectedValue === "CSV") {
            this.selectedFile = null;
            this.manualControls.clear();
            this.buildingFormGroup.controls.file.setValidators([Validators.required]);
            this.buildingFormGroup.controls.file.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeStart.clearValidators();
            this.buildingFormGroup.controls.rangeStart.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeEnd.clearValidators();
            this.buildingFormGroup.controls.rangeEnd.updateValueAndValidity();
        } else if (selectedValue === "Range") {
            this.selectedFile = null;
            this.manualControls.clear();
            this.buildingFormGroup.patchValue({ file: null });
            this.buildingFormGroup.controls.file.clearValidators();
            this.buildingFormGroup.controls.file.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeStart.setValidators([Validators.required]);
            this.buildingFormGroup.controls.rangeStart.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeEnd.setValidators([Validators.required]);
            this.buildingFormGroup.controls.rangeEnd.updateValueAndValidity();
        }
    }

    //   onFileUpload(event: any) {
    //     const file = event.target.files[0];
    //     console.log("File uploaded:", file);
    //   }
    onFileChange(event: any) {
        this.selectedFilePreview = [];
        if (event.target.files.length > 0) {
            this.selectedFile = event.target.files[0];
            const files: FileList = event.target.files;
            for (let i = 0; i < files.length; i++) {
                this.selectedFilePreview.push(files.item(i));
            }
            if (this.selectedFile.type != "text/csv") {
                this.buildingFormGroup.controls.file.reset();
                alert("File type must be csv");
            } else {
                const file = event.target.files;
                this.buildingFormGroup.patchValue({
                    file: file
                });
            }
        }
    }

    get manualControls(): UntypedFormArray {
        return this.buildingFormGroup.get("manualControls") as UntypedFormArray;
    }

    addManualInput(): void {
        this.manualControls.push(this.fb.control("", Validators.required)); // Add new FormControl
    }

    removeManualInput(index: number): void {
        this.manualControls.removeAt(index);
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
    //   addEditCountryDialog() {
    //     const dialogRef = this.dialog.open(AddEditHomePassComponent, {
    //       width: "800px",
    //       data: {
    //         isEdit: false,
    //         title: "Create " + this.title,
    //         addLabel: true,
    //         yesLabel: "Create",
    //         noLabel: "Cancel",
    //         createAcS: this.createAccess,
    //         editAcs: this.editAccess,
    //         inputName: "Enter " + this.title + " Name",
    //         inputStatus: "Select " + this.title + " Status",
    //         buildingTypeData: this.buildingTypeData,
    //         dunningData: this.dunningData,
    //         selectedMappingFrom: this.selectedMappingFrom,
    //         subAreaListData: this.subAreaListData,
    //         pincodeListData: this.pincodeListData,
    //         areaListData: this.areaListData
    //       }
    //     });

    //     dialogRef.afterClosed().subscribe(result => {
    //       if (result) {
    //         this.buildingFormGroup.patchValue(result);
    //         // this.buildingFormGroup.get('')
    //         this.patchManualControls(result.manualControls);
    //         this.addOrUpdateBuilding();
    //       } else {
    //       }
    //     });
    //   }
    heading: any;
    addEditCountryDialog() {
        this.isBuildingEdit = false;
        this.heading = "Create Home Pass";
        this.dialogRef = this.dialog.open(this.homePassDialog, {
            width: '1000px'
        });
    }
    onCancel() {
        this.buildingFormGroup.reset();
        this.dialogRef.close();
    }
    onChange(event) {
        this.selectedMappingFrom = this.dunningData[0].mappingFrom;
        if (this.selectedMappingFrom === "Pin Code") {
            let data = this.pincodeListData.find(x => x.pincodeid == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.pincode);
        } else if (this.selectedMappingFrom === "Area") {
            let data = this.areaListData.find(x => x.id == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.name);
        }
    }
    patchManualControls(values: string[]) {
        const controlArray = this.buildingFormGroup.get("manualControls") as FormArray;
        controlArray.clear(); // clear old
        values.forEach(val => controlArray.push(new FormControl(val)));
    }
}
