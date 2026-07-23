import { Component, OnInit, ViewChild, ViewEncapsulation, TemplateRef } from "@angular/core";
import {
    UntypedFormBuilder,
    Validators,
    UntypedFormGroup,
    FormControl,
    UntypedFormArray,
    FormArray,
    AbstractControl
} from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { LoginService } from "../../service/login.service";
import { AclClassConstants } from "../../constants/aclClassConstants";
import { AclConstants } from "../../constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { BranchManagementService } from "./branch-management.service";
import { Observable, Observer } from "rxjs";
import { CustomerService } from "src/app/service/customer.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatSelectModule } from "@angular/material/select";
import { MatCheckboxModule } from "@angular/material/checkbox";

import { map, startWith } from "rxjs/operators";

import { StateManagementService } from "src/app/service/state-management.service";
import { Regex } from "src/app/constants/regex";
import { StateManagement } from "src/app/components/model/state-management";
import { CountryManagement } from "src/app/components/model/country-management";
import { CountryManagementComponent } from "../country-management/country-management.component";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

import { AsyncPipe } from "@angular/common";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { CityManagementService } from "src/app/service/city-management.service";

import { RouterModule } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
    selector: "app-branch-management",
    templateUrl: "./branch-management.component.html",
    styleUrls: ["./branch-management.component.css"],
    standalone: false
})
export class BranchManagementComponent implements OnInit {
    @ViewChild("servicePaginator") servicePaginator!: MatPaginator;
    @ViewChild("areaDialog") areaDialog!: TemplateRef<any>;

    branchFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    dialogRef2: any;
    dialogRef3: any;
    branchData: any = [];
    branchDataDetailsShow: boolean = false;
    branchListData: any;
    isEdit: boolean = false;
    viewListData: any;
    editServiceAreaList: any = [];
    currentPageSlab = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;
    serviceData: any;
    searchName: any = "";
    searchData: any;

    // Add these properties
    currentServicePage = 0;
    servicePageSize = 5;
    pagedServiceData: any[] = [];

    AclClassConstants;
    AclConstants;
    areaModal: boolean = false;
    dataSource = new MatTableDataSource<any>([]);
    serviceCommissionDataSource = new MatTableDataSource<any>([]);
    serviceCommonFromGroup: UntypedFormGroup;
    serviceCommisionSubmitted: boolean = false;
    serviceCommonListFromArray: UntypedFormArray = this.fb.array([]);
    serviceSelectList: any = [];
    planserviceData: any = [];
    planserviceCopyData: any = [];

    serviceitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    servicetotalRecords: any;
    currentPageservice = 1;
    serviListName: any = [];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    viewBranchListData: any = [];
    branchDataDetails: any;
    serviceArea = [];
    serviceAreaDropdownList: any = [];
    serviceAreaSearchCtrl = new FormControl("");
    filteredServiceAreas: Observable<any[]>;
    serviceDisplayedColumns: string[] = ["Service", "Revenue Share Percentage", "Delete"];
    serviceDataSource = new MatTableDataSource<any>([]);
    selectedAreas: { id: any; name: string }[] = [];
    areaparticularData: any = [];
    revenueSharingData = [
        { label: "Yes", value: true },
        { label: "No", value: false }
    ];
    public loginService: LoginService;
    dunningData: any;
    serviceAreaModal: boolean = false;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService,
        private branchManagementService: BranchManagementService,
        private customerServiceManagement: CustomerService,
        public commondropdownService: CommondropdownService,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.BRANCH_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.BRANCH_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.BRANCH_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.branchFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            serviceAreaIdsList: ["", Validators.required],
            status: ["", Validators.required],
            branch_code: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            revenue_sharing: ["", Validators.required],
            dunningDays: [""],
            branchServiceMappingEntityList: (this.serviceCommonListFromArray = this.fb.array([]))
        });

        this.serviceCommonFromGroup = this.fb.group({
            branchId: [""],
            serviceId: ["", Validators.required],
            revenueShareper: ["", [Validators.required, Validators.min(0), Validators.max(100)]]
        });

        // this.filteredServiceAreas = this.serviceAreaSearchCtrl.valueChanges.pipe(
        //     startWith(""),
        //     map(searchText => {
        //         if (!searchText) {
        //             return this.serviceAreaDropdownList;
        //         }
        //         return this.serviceAreaDropdownList.filter(area =>
        //             area.name.toLowerCase().includes(searchText.toLowerCase())
        //         );
        //     })
        // );
        this.serviceCommonListFromArray.controls.forEach(control => {
            control.get("serviceId").disable();
            control.get("revenueShareper").disable();
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
            ]
        };

        const serviceArea = localStorage.getItem("serviceArea");
        let serviceAreaArray = JSON.parse(serviceArea);
        this.commondropdownService.getserviceAreaList();
        this.getListData("");
        this.getDunningDays();
        this.branchDataDetailsShow = false;
        this.getserviceArea();
        this.commondropdownService.getplanservice();
        this.getplanservice();
        this.serviceCommonListFromArray.controls.forEach(control => {
            control.get("serviceId")?.disable();
            control.get("revenueShareper")?.disable();
        });
    }

    isSelected(id: any): boolean {
        return this.selectedAreas.some(a => a.id === id);
    }

    branchAllDetails(branch) {
        this.branchDataDetails = branch;
        this.branchDataDetails.serviceAreaNameList.forEach((element, index) => {
            this.serviListName.push(element);
        });
        this.openBranchDetailsDialog(branch);
    }

    openModel() {
        this.serviceAreaModal = true;
    }

    closeModal() {
        this.serviceAreaModal = false;
    }

    syncServiceDataSource(): void {
        this.serviceDataSource.data = this.serviceCommonListFromArray.controls;

        if (this.servicePaginator) {
            this.serviceDataSource.paginator = this.servicePaginator;
        }
        this.serviceDataSource._updateChangeSubscription();
    }

    updateServicePagination(): void {
        const startIndex = this.currentServicePage * this.servicePageSize;
        const endIndex = startIndex + this.servicePageSize;
        this.pagedServiceData = this.serviceCommonListFromArray.controls.slice(startIndex, endIndex);
        this.serviceDataSource.data = this.pagedServiceData;
    }

    revenueSharingEvent(e) {
        if (e.value == true) {
            this.branchFormGroup
                .get("branchServiceMappingEntityList")
                .setValidators([Validators.required]);
            this.branchFormGroup.get("branchServiceMappingEntityList").updateValueAndValidity();
        } else {
            this.serviceCommonFromGroup.reset();
            this.branchFormGroup.get("branchServiceMappingEntityList").clearValidators();
            this.branchFormGroup.get("branchServiceMappingEntityList").updateValueAndValidity();
        }
    }

    private filterServiceAreas(searchTerm: string): any[] {
        if (!searchTerm) {
            return this.serviceAreaDropdownList;
        }
        searchTerm = searchTerm.toLowerCase();
        return this.serviceAreaDropdownList.filter(area =>
            area.name.toLowerCase().includes(searchTerm)
        );
    }

    getserviceArea() {
        let data = [];
        const url = "/serviceArea/serviceAreaListWhereBranchIsNotBind";
        this.serviceAreaDropdownList = [];
        this.branchManagementService.getMethod(url).subscribe((response: any) => {
            if (response.dataList) {
                const processedData = response.dataList.map((item: any) => ({
                    id: item.id,
                    name: item.name,
                    isUnderDevelopment: item.status === "UnderDevelopment"
                }));
                this.serviceAreaDropdownList.push(...processedData);
            }
        });
    }

    addEdit(id) {
        this.submitted = true;
        let allowSubmit = false;

        if (this.branchFormGroup.value.revenue_sharing) {
            if (this.serviceCommonListFromArray.length > 0) {
                // We use .length on the array directly to check existence
                this.branchFormGroup.patchValue({
                    branchServiceMappingEntityList: this.serviceCommonListFromArray.value
                });
                allowSubmit = true;
            } else {
                this.toastr.error("Atleast one service should be added", 'Required!');
            }
        } else {
            this.branchFormGroup.patchValue({
                branchServiceMappingEntityList: null
            });
            allowSubmit = true;
        }

        if (this.branchFormGroup.valid && allowSubmit === true) {
            if (id) {
                // UPDATE OPERATION
                const url = "/branchManagement/update";

                // FIX: Use getRawValue() to include disabled form controls (serviceId, revenueShareper)
                this.branchData = this.branchFormGroup.getRawValue();
                this.branchData.id = id;

                this.branchManagementService.postMethod(url, this.branchData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406 || response.responseCode == 405) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            // Reset form states
                            this.submitted = false;
                            this.isEdit = false;
                            this.serviceCommisionSubmitted = false;

                            // Clear cache and refresh data
                            this.commondropdownService.clearCache("/branchManagement/findAll");
                            this.getserviceArea();

                            // Close dialog first
                            if (this.dialogRef2) {
                                this.dialogRef2.close();
                            }

                            // Clear forms
                            this.branchFormGroup.reset();
                            this.serviceCommonFromGroup.reset();

                            // Clear service commission data
                            this.serviceCommonListFromArray.controls = [];
                            this.serviceSelectList = [];

                            // Clear service area selections
                            this.serviceAreaSearchCtrl.setValue("");
                            this.selectedAreas = [];

                            // Restore plan service data
                            this.planserviceData = [...this.planserviceCopyData];

                            this.branchFormGroup.controls.status.setValue("");

                            this.toastr.success("Successfully Updated", 'Success!');

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
                // CREATE OPERATION
                const url = "/branchManagement/save";

                // FIX: Use getRawValue() to include disabled form controls
                this.branchData = this.branchFormGroup.getRawValue();

                this.branchManagementService.postMethod(url, this.branchData).subscribe(
                    (response: any) => {
                        if (
                            response.responseCode == 406 ||
                            response.responseCode == 405 ||
                            response.responseCode == 417
                        ) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            // Reset form states
                            this.submitted = false;
                            this.serviceCommisionSubmitted = false;

                            // Clear cache and refresh data
                            this.commondropdownService.clearCache("/branchManagement/findAll");
                            this.getserviceArea();

                            // Clear forms
                            this.branchFormGroup.reset();
                            this.serviceCommonFromGroup.reset();

                            // Clear service commission data
                            this.serviceCommonListFromArray.controls = [];
                            this.serviceSelectList = [];

                            // Clear service area selections
                            this.serviceAreaSearchCtrl.setValue("");
                            this.selectedAreas = [];

                            // Restore plan service data
                            this.planserviceData = [...this.planserviceCopyData];

                            this.branchFormGroup.controls.status.setValue("");

                            this.toastr.success(`${response.responseMessage}`, 'Success!');

                            if (this.searchkey) {
                                this.search();
                            } else {
                                this.getListData("");
                            }

                            // Close dialog after success
                            if (this.dialogRef2) {
                                this.dialogRef2.close();
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

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageSlab > 1) {
            this.currentPageSlab = 1;
        }
        if (!this.searchkey) {
            this.getListData(this.showItemPerPage);
        } else {
            this.search();
        }
    }

    onServicePageChange(event: PageEvent): void {
        this.currentServicePage = event.pageIndex;
        this.servicePageSize = event.pageSize;
        this.updateServicePagination();
    }

    getListData(list) {
        const url = "/branchManagement";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageSlab;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size
        };

        this.branchManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                if (response.responseCode == 204) {
                    this.dataSource = new MatTableDataSource<any>([]);
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.branchListData = response.dataList;
                    this.dataSource = new MatTableDataSource<any>(this.branchListData);
                    this.totalRecords = response.totalRecords;
                    this.searchkey = "";
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    edit(id) {
        let date1;
        let date2;
        var list = "";
        let editServiceAreaId: any = [];
        let editServiceAreaNameList: any = [];
        this.editServiceAreaList = [];
        this.serviceArea = [];
        this.serviceAreaDropdownList = [];
        this.serviceCommonFromGroup.reset();
        this.serviceCommisionSubmitted = false;
        this.serviceCommonListFromArray.controls = [];

        this.isEdit = true;

        this.openbranchManagementDialog(id);
        this.getserviceArea();
        if (id) {
            this.viewBranchListData = [];
            const url = "/branchManagement/" + id;
            this.branchManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.viewListData = response.data;
                    let servicAreaId = [];
                    editServiceAreaId = this.viewListData.serviceAreaIdsList;
                    editServiceAreaNameList = this.viewListData.serviceAreaNameList;
                    this.viewListData.serviceAreaIdsList.forEach((id, i) => {
                        this.viewListData.serviceAreaNameList.forEach((name, j) => {
                            if (i == j) {
                                servicAreaId.push({ name: name, id: id });
                            }
                        });
                    });
                    this.serviceAreaDropdownList.push(...servicAreaId);

                    this.branchFormGroup.patchValue(this.viewListData);

                    this.viewListData.branchServiceMappingEntityList.forEach(element => {
                        this.serviceCommonListFromArray.push(
                            this.fb.group({
                                serviceId: [element.serviceId],
                                revenueShareper: [element.revenueShareper],
                                id: [element.id],
                                branchId: [element.branchId]
                            })
                        );

                        this.serviceSelectList.push(element.serviceId);
                        this.planserviceData.forEach((ele, index) => {
                            if (ele.id == element.serviceId) {
                                this.planserviceData.splice(index, 1);
                            }
                        });
                    });
                    this.serviceDataSource.data = this.serviceCommonListFromArray.controls;

                    this.updateServicePagination();

                    if (this.servicePaginator) {
                        this.serviceDataSource.paginator = this.servicePaginator;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    search() {
        this.branchDataDetailsShow = false;

        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPageSlab = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName.trim();
        this.searchData.page = this.currentPageSlab;
        this.searchData.pageSize = this.itemsPerPage;
        const url = `/branchManagement/search?page=${this.currentPageSlab}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
        this.branchManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.dataSource = new MatTableDataSource<any>([]);
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.branchListData = [];
                    this.totalRecords = 0;
                } else {
                    this.branchListData = response.dataList;
                    this.dataSource = new MatTableDataSource<any>(response.dataList);
                    this.totalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.branchListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    areaDataOpenModel(data) {
        this.areaparticularData = data;
        this.areaModal = true;
    }

    displayedColumns = ["id", "Name", "Status", "Action"];
    @ViewChild("areaDetailsDialog") areaDetailsDialog!: TemplateRef<any>;
    @ViewChild("branchManagementDialog") branchManagementDialog!: TemplateRef<any>;
    @ViewChild("branchDetailsDialog") branchDetailsDialog!: TemplateRef<any>;
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        this.updateServicePagination();

        this.serviceDataSource.paginator = this.servicePaginator;
    }

    updateServiceDataSource(): void {
        this.serviceDataSource.data = this.serviceCommonListFromArray.controls;
        this.serviceDataSource.paginator = this.servicePaginator;
    }

    openAreaDetailsDialog(area: any): void {
        this.areaparticularData = area;
        this.dialog.open(this.areaDetailsDialog, {
            width: "600px",
            disableClose: true
        });
    }

    openbranchManagementDialog(branchManagement: any) {
        this.areaparticularData = branchManagement;
        this.dialogRef2 = this.dialog.open(this.branchManagementDialog, {
            width: "900px"

        });

        this.dialogRef2.afterClosed().subscribe(result => {
            this.isEdit = false;
            this.branchFormGroup.reset();
            this.serviceCommonFromGroup.reset();
            this.serviceCommisionSubmitted = false;

            this.clearFormArray(this.serviceCommonListFromArray);

            this.serviceDataSource.data = [];

            this.planserviceData = [...this.planserviceCopyData];

            this.getserviceArea();
        });
    }

    clearFormArray(formArray: UntypedFormArray): void {
        while (formArray.length !== 0) {
            formArray.removeAt(0);
        }

        this.serviceDataSource.data = [];
        this.serviceSelectList = [];
    }

    openBranchDetailsDialog(branchManagement: any) {
        this.areaparticularData = branchManagement;
        this.dialogRef3 = this.dialog.open(this.branchDetailsDialog, {
            width: "900px"
        });
        this.dialogRef3.afterClosed().subscribe(result => {
        });
    }

    closeAreaModal() {
        this.areaModal = false;
    }

    clearSearch() {
        this.branchDataDetailsShow = false;
        this.searchName = "";
        this.searchkey = "";
        this.getListData("");
        this.submitted = false;
        this.isEdit = false;
        this.branchFormGroup.reset();
        this.serviceCommonFromGroup.reset();
        this.serviceCommisionSubmitted = false;
        this.serviceCommonListFromArray.controls = [];
        this.branchFormGroup.controls.status.setValue("");
        this.getserviceArea();
    }

    canExit() {
        if (!this.branchFormGroup.dirty) return true;
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

    private performDelete(id: number) {
        const url1 = "/branchManagement/" + id;
        this.branchManagementService.getMethod(url1).subscribe(
            (response: any) => {
                const data = response.data;
                this.delete(data);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    deleteConfirmonBranchDialog(item: any) {
        const dialogRef2 = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: "Delete State",
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef2.afterClosed().subscribe(result => {
            if (result) {
                this.performDelete(item.id);
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });
    }

    delete(data) {
        const url = "/branchManagement/delete";
        this.branchManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (this.currentPageSlab != 1 && this.branchListData.length == 1) {
                    this.currentPageSlab = this.currentPageSlab - 1;
                }
                this.clearSearch();
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.getserviceArea();
                    this.toastr.success(`${response.message}`, 'Success!');
                }
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

    deleteConfirmonServiceCommisiionField(index: number, row: AbstractControl) {
        const serviceId = row.get('serviceId')?.value;
        const globalIndex = (this.currentServicePage * this.servicePageSize) + index;

        // Add the service back to available options
        const deletedService = this.planserviceCopyData.find((service: any) => service.id === serviceId);
        if (deletedService) {
            this.planserviceData.push(deletedService);
        }

        // Remove from form array
        this.serviceCommonListFromArray.removeAt(globalIndex);
        this.serviceSelectList.splice(globalIndex, 1);

        // Update the pagination
        this.updateServicePagination();
    }

    pageChangedList(event: PageEvent) {
        this.currentPageSlab = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.search();
        } else {
            this.getListData("");
        }
    }

    getServiceAreaNameFromId(serviceAreaId) {
        var filterData = this.commondropdownService.serviceAreaList.filter(
            serviceArea => serviceArea.id == serviceAreaId
        );
        if (filterData != null && filterData.length > 0) return filterData[0].name;
        else return "";
    }

    getDunningDays() {
        const url = "/commonList/dunningDays";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.dunningData = response.dataList;
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    sharingPercentageValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }

    createServiceCommissionFormGroup(): UntypedFormGroup {
        return this.fb.group({
            id: [""],
            serviceId: [this.serviceCommonFromGroup.value.serviceId],
            revenueShareper: [this.serviceCommonFromGroup.value.revenueShareper]
        });
    }

    onAddServiceCommissionField() {
        this.serviceCommisionSubmitted = true;

        // Check if the form is valid
        if (this.serviceCommonFromGroup.invalid) {
            // Mark all fields as touched to show validation errors
            Object.keys(this.serviceCommonFromGroup.controls).forEach(key => {
                const control = this.serviceCommonFromGroup.get(key);
                control.markAsTouched();
            });
            return;
        }
        this.serviceSelectList.push(this.serviceCommonFromGroup.value.serviceId);

        // Add the service to the form array
        const newControl = this.createServiceCommissionFormGroup();
        newControl.get("serviceId").disable();
        newControl.get("revenueShareper").disable();

        this.serviceCommonListFromArray.push(newControl);

        this.updateServicePagination();
        this.updateServiceDataSource();

        // Update the data source
        this.syncServiceDataSource();

        // Remove the service from available options
        this.planserviceData = this.planserviceData.filter(
            service => service.id !== this.serviceCommonFromGroup.value.serviceId
        );

        // Reset the form
        this.serviceCommonFromGroup.reset();
        this.serviceCommisionSubmitted = false;
    }

    onOptionClick(event: MouseEvent, area: any) {
        event.stopPropagation();
        event.preventDefault();

        if (this.isSelected(area.id)) {
            // remove
            this.selectedAreas = this.selectedAreas.filter(a => a.id !== area.id);
        } else {
            // add
            this.selectedAreas.push({ id: area.id, name: area.name });
        }

        // update form control with array of ids
        this.branchFormGroup.patchValue({
            serviceAreaIdsList: this.selectedAreas.map(a => a.id)
        });

        // clear text input so user can continue searching
        this.serviceAreaSearchCtrl.setValue("");
    }

    onInputKeydown(event: KeyboardEvent) {
        if (event.key === "Enter") {
            event.preventDefault();
        }
    }

    getplanservice() {
        const url = "/planservice/all";
        this.customerServiceManagement.getMethod(url).subscribe(
            (response: any) => {
                this.planserviceData = response.serviceList;
                this.planserviceCopyData = response.serviceList;
                this.serviceDataSource = new MatTableDataSource<any>(response.planserviceData);
            },
            (error: any) => { }
        );
    }

    pageChangedServiceCommission(event: PageEvent) {
        this.serviceitemsPerPage = event.pageSize;
    }

    loadServiceCommissionData() {
        const url = "/branchManagement/serviceCommission";
        const payload = {
            page: this.currentPageservice,
            pageSize: this.serviceitemsPerPage
        };

        this.branchManagementService.postMethod(url, payload).subscribe((response: any) => {
            this.serviceDataSource.data = response.dataList;
            this.servicetotalRecords = response.totalRecords;
        });
    }
    serviceAreaDropdownList1 = [
        { id: 1, name: "North Zone", isUnderDevelopment: false },
        { id: 2, name: "South Zone", isUnderDevelopment: true },
        { id: 3, name: "East Zone", isUnderDevelopment: false },
        { id: 4, name: "West Zone", isUnderDevelopment: true },
        { id: 5, name: "Central City", isUnderDevelopment: false },
        { id: 6, name: "Urban Extension", isUnderDevelopment: true }
    ];

    // // Select All - Service Areas
    toggleSelectAllServiceAreas() {
        const allIds = this.serviceAreaDropdownList.map(item => item.id);
        const control = this.branchFormGroup.get('serviceAreaIdsList');

        if (this.isAllServiceAreaSelected()) {
            control?.setValue([]); // Unselect all
        } else {
            control?.setValue(allIds); // Select all
        }
    }

    // Check if all selected
    isAllServiceAreaSelected(): boolean {
        const controlValue = this.branchFormGroup.get('serviceAreaIdsList')?.value;
        return controlValue?.length === this.serviceAreaDropdownList?.length;
    }

    // Indeterminate state
    isServiceAreaIndeterminate(): boolean {
        const controlValue = this.branchFormGroup.get('serviceAreaIdsList')?.value || [];
        return controlValue?.length > 0 && controlValue?.length < this.serviceAreaDropdownList?.length;
    }

    onServiceAreaSelectionChange(event: any) {
        // Filter out undefined values if Select All option gets selected
        const controlValue = this.branchFormGroup.get('serviceAreaIdsList');
        const currentValue = controlValue?.value || [];
        // Remove any undefined or null values
        const filteredValue = currentValue.filter((val: any) => val !== undefined && val !== null);
        if (filteredValue.length !== currentValue?.length) {
            controlValue?.setValue(filteredValue, { emitEvent: false });
        }
    }
}
