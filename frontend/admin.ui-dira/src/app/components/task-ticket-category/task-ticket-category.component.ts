import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray } from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Regex } from "src/app/constants/regex";
import { TicketReasonSubCategory } from "src/app/components/model/ticket-reason-sub-category";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer } from "rxjs";
import { TASK_SYSTEMS, TICKETING_SYSTEMS } from "src/app/constants/aclConstants";
import { TaskCategoryService } from "src/app/service/task-category.service";
import { TicketReasonSubCategoryService } from "src/app/service/ticket-reason-sub-category.service";

import { TemplateRef, ViewChild, ViewEncapsulation } from "@angular/core";
import { FormArray, FormGroup } from "@angular/forms";

import { CountryManagementService } from "src/app/service/country-management.service";
import { CountryManagement } from "src/app/components/model/country-management";

import { COUNTRY } from "src/app/RadiusUtils/RadiusConstants";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { resolve } from "dns";
import { ObserversModule } from "@angular/cdk/observers";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-task-ticket-category",
    templateUrl: "./task-ticket-category.component.html",
    styleUrls: ["./task-ticket-category.component.css"],
    standalone: false
})
export class TaskTicketCategoryComponent implements OnInit {
    ticketReasonSubCatFormGroup: UntypedFormGroup;
    dataSource = new MatTableDataSource<any>([]);
    tatMappingDataSource = new MatTableDataSource<any>([]);
    dialogRef2: any;

    submitted = false;
    parentTRCData: any;
    statusOptions = RadiusConstants.status;
    isTicketReasonSubCategoryEdit = false;
    createTicketReasonSubCategoryData: any = {
        categoryName: "",
        caseCategoryTatMappingList: [],
        status: "",
        categoryId: "",
        isDefaultCaseCategory: false,
        isDeleted: false
    };
    currentPageTicketReasonSubCategoryListdata = 1;
    ticketReasonSubCategoryListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ticketReasonSubCategoryListDatatotalRecords: any;
    areaparticularData: any = [];
    areaModal: boolean = false;
    ticketReasonSubCategoryListData: any;
    showItemPerPage: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    searchkey: string;
    editTicketReasonSubCategoryData: any;
    viewTrscData: any = [];
    currentPageViewReasonListdata = 1;
    viewReasonListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    viewReasonListDatatotalRecords: any;
    searchTrscName: any = "";
    searchParentTrcName: any = "";
    searchData: any;
    searchAllData: any;

    listTicket = true;
    createTicket = false;
    TATForTicketData: any = [];
    teamConditionArray: UntypedFormArray;
    teamConditionData: any = [];
    coditionArray: any = [];
    teamcondition: any = "";
    conditionDataEnableDisble = [];
    teamQueryFieldListINDEX: string;
    teamMappingTeamID: any = "";
    teamValue: any = "";
    errormsgCondition: string;
    teamSubmitted: boolean = false;
    tatForTicketID = "";
    TatMappingArray: UntypedFormArray;
    ItemPerPageteamCondition = RadiusConstants.ITEMS_PER_PAGE;
    currentPageteamCondition = 1;
    totalRecordsteamCondition: number;
    totalRecordsTeams = 0;
    ItemPerPageTeams = RadiusConstants.ITEMS_PER_PAGE;
    currentPageTeams: any = 1;

    operatorList = [
        { label: "Equal to", value: "==" },
        { label: "Less than or equal to", value: "<=" },
        { label: "Greater than or equal to", value: ">=" },
        { label: "Less than ", value: "<" },
        { label: "Greater than", value: ">" },
        { label: "Not equal to", value: "!=" }
    ];
    AndOrDropdown = [
        { label: "AND", value: "and" },
        { label: "OR", value: "or" }
    ];

    currentPageViewTATListdata = 1;
    viewTATListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    viewTATListDatatotalRecords: any;

    orderid = 1;
    AclClassConstants;
    AclConstants;
    ticketCatogryData = [];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isDeleted: boolean = false;
    ticketReasonSubCatdialoge: boolean = false;
    ticketReasonSubCatModal: boolean = false;
    coditionArrayShow = [];
    pageItem;
    mvnoId: any;
    this: any;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService,
        private taskCategoryService: TaskCategoryService,
        public commondropdownService: CommondropdownService,
        public loginService: LoginService,
        private dialog: MatDialog,
        private ticketReasonSubCategoryService: TicketReasonSubCategoryService,
    ) {
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(TASK_SYSTEMS.TASK_CATEGORY_DOMAIN_CREATE);
        this.deleteAccess = loginService.hasPermission(TASK_SYSTEMS.TASK_CATEGORY_DOMAIN_DELETE);
        this.editAccess = loginService.hasPermission(TASK_SYSTEMS.TASK_CATEGORY_DOMAIN_EDIT);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(TASK_SYSTEMS.TASK_CATEGORY_DOMAIN_EDIT) || this.loginService.hasPermission(TASK_SYSTEMS.TASK_CATEGORY_DOMAIN_DELETE)) {
            return ['id', 'Name', 'Status', 'Action'];
        } else {
            return ['id', 'Name', 'Status'];
        }
    }

    ngOnInit(): void {
        this.ticketReasonSubCatFormGroup = this.fb.group({
            categoryName: ["", Validators.required],
            status: ["", Validators.required],
            isDeleted: [false],
            isDefaultCaseCategory: [false]
        });
        this.mvnoId = localStorage.getItem("mvnoId");

        this.getTicketReasonCategoryDataList();
        this.getTicketReasonSubCategoryDataList("");
        this.getFildDropdownValue();
        this.getTATForTicketList();
        this.tatMappingDataSource = new MatTableDataSource<any>([]);
        this.viewTrscData = {
            status: "",
            caseCategoryTatMappingList: [],
            categoryName: ""
        };

        this.searchData = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "name"
                }
            ],
            page: "",
            pageSize: "",
            sortBy: "createdate",
            sortOrder: 0
        };

        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);
        this.searchAllData = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "name"
                }
            ],
            page: "",
            pageSize: "",
            sortBy: "createdate",
            sortOrder: 0
        };
        this.TatMappingArray = this.fb.array([]);
        this.updateTatMappingDataSource();
    }

    createTicketFun(): void {
        this.listTicket = false;
        this.createTicket = true;
        this.submitted = false;
        this.isTicketReasonSubCategoryEdit = false;
        this.ticketReasonSubCatFormGroup.reset();
        this.submitted = false;
        this.orderid = 0;
        this.teamConditionArray.reset();
        this.ticketReasonSubCatFormGroup.reset();
        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);
        this.tatForTicketID = "";
        this.teamcondition = "";
        this.createTicketReasonSubCategoryData.categoryId = "";
        this.ticketReasonSubCatFormGroup.get("isDefaultCaseCategory").enable();
    }

    searchTicketFun(): void {
        this.listTicket = true;
        this.createTicket = false;
        this.isTicketReasonSubCategoryEdit = false;
        this.ticketReasonSubCatFormGroup.reset();
        this.ticketReasonSubCatFormGroup.get("isDefaultCaseCategory").enable();
        this.pageItem = this.ticketReasonSubCategoryListdataitemsPerPage;
        this.getTicketReasonSubCategoryDataList("");
    }

    SearchTicketReasonSubCategoryDataList(list) {
        let size;
        this.searchkey = "";
        const page = this.currentPageTicketReasonSubCategoryListdata;
        if (list) {
            size = list;
            this.currentPageTicketReasonSubCategoryListdata = list;
        } else {
            size = this.ticketReasonSubCategoryListdataitemsPerPage;
        }

        let pagedata = {
            page: page,
            pageSize: size
        };
        const url = "/CaseCategory/searchAll";
        this.taskCategoryService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.ticketReasonSubCategoryListData = response.dataList;
                this.ticketReasonSubCategoryListDatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getTicketReasonSubCategoryDataList(list) {
        let size;
        this.searchkey = "";
        const page = this.currentPageTicketReasonSubCategoryListdata;
        if (list) {
            size = list;
            this.currentPageTicketReasonSubCategoryListdata = list;
        } else {
            size = this.ticketReasonSubCategoryListdataitemsPerPage;
        }

        const pagedata = {
            page,
            pageSize: size
        };
        const url = "/CaseCategory";
        this.taskCategoryService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.ticketReasonSubCategoryListData = response.dataList;
                this.ticketReasonSubCategoryListDatatotalRecords = response.totalRecords;
                this.dataSource.data = this.ticketReasonSubCategoryListData;
                if (this.paginator) {
                    this.paginator.length = this.ticketReasonSubCategoryListDatatotalRecords;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getTicketReasonCategoryDataList() {
        const url = "/CaseCategory/getAllActiveReasonCatgory";
        this.taskCategoryService.getMethod(url).subscribe(
            (response: any) => {
                this.parentTRCData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onSubmit() {
        this.submitted = true;

        if (this.ticketReasonSubCatFormGroup.valid && this.TatMappingArray.value.length > 0) {
            if (this.isTicketReasonSubCategoryEdit) {
                this.addEditTicketReasonSubCat(this.editTicketReasonSubCategoryData.categoryId);
            } else {
                this.addEditTicketReasonSubCat('');
            }
        } else {
            if (this.TatMappingArray.value.length === 0) {
                this.toastr.info(`Please add TAT Mapping`, 'Required!');
            }
        }
    }

    addEditTicketReasonSubCat(id) {
        let TatMappingList: any = [];
        let quaryArray: any = [];
        let ticketCatogary: any = [];
        let catogaryData = [];
        this.submitted = true;

        if (this.ticketReasonSubCatFormGroup.value.isDefaultCaseCategory == null) {
            this.ticketReasonSubCatFormGroup.controls.isDefaultCaseCategory.setValue(false);
        }
        if (this.ticketReasonSubCatFormGroup.valid) {
            if (this.TatMappingArray.value.length > 0) {
                if (id) {
                    const url = "/CaseCategory/update";

                    if (this.TatMappingArray.value.length > 0) {
                        this.TatMappingArray.value.forEach((element, i) => {
                            if (element.tatQueryFieldMappingList.length > 0) {
                                quaryArray = element.tatQueryFieldMappingList;
                            } else {
                                quaryArray = [];
                            }
                            TatMappingList.push({
                                caseCategoryId: element.caseCategoryId,
                                id: element.id,
                                orderid: element.orderid,
                                tatQueryFieldMappingList: quaryArray,
                                ticketTatMatrix: {
                                    id: element.tatForTicketID
                                }
                            });
                        });
                    }
                    this.createTicketReasonSubCategoryData = this.ticketReasonSubCatFormGroup.value;
                    this.createTicketReasonSubCategoryData.categoryId = id;
                    this.createTicketReasonSubCategoryData.status =
                        this.ticketReasonSubCatFormGroup.value.status;

                    this.createTicketReasonSubCategoryData.categoryName =
                        this.ticketReasonSubCatFormGroup.value.categoryName;

                    this.createTicketReasonSubCategoryData.mvnoId = Number(this.mvnoId);

                    this.createTicketReasonSubCategoryData.isDefaultCaseCategory =
                        this.ticketReasonSubCatFormGroup.value.isDefaultCaseCategory;

                    this.createTicketReasonSubCategoryData.isDeleted = this.isDeleted;
                    this.createTicketReasonSubCategoryData = {
                        ...this.createTicketReasonSubCategoryData,
                        caseCategoryTatMappingList: TatMappingList
                    };

                    this.taskCategoryService
                        .postMethod(url, this.createTicketReasonSubCategoryData)
                        .subscribe(
                            (response: any) => {
                                if (
                                    response.responseCode == 406 ||
                                    response.responseCode == 417 ||
                                    response.responseCode == 500
                                ) {
                                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                                } else {
                                    this.toastr.success(`Successfully Updated`, 'Success!');
                                    this.submitted = false;
                                    this.teamSubmitted = false;
                                    this.dialog.closeAll();
                                    this.clearTicketReasonSubCategory();
                                    this.isTicketReasonSubCategoryEdit = false;
                                }
                            },
                            (error: any) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            }
                        );
                    this.dialog.closeAll();
                    this.clearTicketReasonSubCategory();
                } else {
                    const url = "/CaseCategory/save";

                    if (this.TatMappingArray.value.length > 0) {
                        this.TatMappingArray.value.forEach((element, i) => {
                            if (element.tatQueryFieldMappingList.length > 0) {
                                quaryArray = element.tatQueryFieldMappingList;
                            } else {
                                quaryArray = [];
                            }
                            TatMappingList.push({
                                id: "",
                                orderid: element.orderid,
                                tatQueryFieldMappingList: quaryArray,
                                ticketTatMatrix: {
                                    id: element.tatForTicketID
                                }
                            });
                        });
                    }

                    this.createTicketReasonSubCategoryData.caseCategoryTatMappingList = TatMappingList;
                    this.createTicketReasonSubCategoryData.status =
                        this.ticketReasonSubCatFormGroup.value.status;

                    this.createTicketReasonSubCategoryData.categoryName =
                        this.ticketReasonSubCatFormGroup.value.categoryName;

                    this.createTicketReasonSubCategoryData.mvnoId = Number(this.mvnoId);

                    this.createTicketReasonSubCategoryData.isDefaultCaseCategory =
                        this.ticketReasonSubCatFormGroup.value.isDefaultCaseCategory;

                    this.createTicketReasonSubCategoryData.isDeleted = this.isDeleted;

                    this.taskCategoryService
                        .postMethod(url, this.createTicketReasonSubCategoryData)
                        .subscribe(
                            (response: any) => {
                                if (response.responseCode == 406) {
                                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                                } else {
                                    this.toastr.success(`Successfully Created`, 'Success!');
                                    this.clearTicketReasonSubCategory();
                                }
                            },
                            (error: any) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            }
                        );
                    this.dialog.closeAll();
                    this.clearTicketReasonSubCategory();
                }
            } else {
                this.toastr.info(`Please add TAT Mapping`, 'Required!');
            }
        }
    }

    private isFormValidForSubmission(): boolean {
        const isBasicFormValid = this.ticketReasonSubCatFormGroup.valid;
        const hasTatMapping = this.TatMappingArray.value.length > 0;

        return isBasicFormValid && hasTatMapping;
    }

    private prepareTatMappingList(categoryId: string): any[] {
        let TatMappingList: any = [];

        this.TatMappingArray.value.forEach((element, i) => {
            let quaryArray = element.tatQueryFieldMappingList || [];

            TatMappingList.push({
                caseCategoryId: categoryId || element.caseCategoryId,
                id: element.id || "",
                orderid: element.orderid,
                tatQueryFieldMappingList: quaryArray,
                ticketTatMatrix: {
                    id: element.tatForTicketID
                }
            });
        });

        return TatMappingList;
    }

    getTATForTicketList() {
        const url = "/tasktatmatrix/searchByStatus";
        this.taskCategoryService.getMethod(url).subscribe(
            (response: any) => {
                this.TATForTicketData = response.dataList;
            },
            (error: any) => { }
        );
    }

    clearTicketReasonSubCategory() {
        this.submitted = false;
        this.teamSubmitted = false;
        this.isTicketReasonSubCategoryEdit = false;

        this.ticketReasonSubCatFormGroup.reset();
        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);

        this.updateTatMappingDataSource();

        this.tatForTicketID = "";
        this.teamcondition = "";
        this.orderid = 1;

        this.getTicketReasonSubCategoryDataList("");
        this.listTicket = true;
        this.createTicket = false;
        this.dialog.closeAll();
    }

    TotalItemPerPage(event) {
        this.ticketReasonSubCategoryListdataitemsPerPage = Number(event.value);
        if (this.currentPageTicketReasonSubCategoryListdata > 1) {
            this.currentPageTicketReasonSubCategoryListdata = 1;
        }
        if (!this.searchkey) {
            this.getTicketReasonSubCategoryDataList(this.showItemPerPage);
        } else {
            this.searchTrsc();
        }
    }

    onPageChange(event: PageEvent) {
        this.currentPageTicketReasonSubCategoryListdata = event.pageIndex + 1;
        this.ticketReasonSubCategoryListdataitemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchTrsc();
        } else {
            this.getTicketReasonSubCategoryDataList("");
        }
    }

    pageChangedTrscList(pageNumber) {
        this.currentPageTicketReasonSubCategoryListdata = pageNumber;
        if (this.searchkey) {
            this.searchTrsc();
        } else {
            this.getTicketReasonSubCategoryDataList("");
        }
    }

    editTicketReasonSubCategory(id) {
        this.tatForTicketID = "";
        this.teamcondition = "";

        this.ticketReasonSubCatFormGroup.reset();
        this.isTicketReasonSubCategoryEdit = true;

        this.createTicket = true;

        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);
        this.orderid = 0;
        this.ticketCatogryData = [];
        let catogaryData = [];
        const url = "/CaseCategory/" + id;

        this.taskCategoryService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.editTicketReasonSubCategoryData = response.data;

                    this.ticketReasonSubCatFormGroup.patchValue({
                        categoryName: this.editTicketReasonSubCategoryData.categoryName,
                        status: this.editTicketReasonSubCategoryData.status,
                        isDefaultCaseCategory: this.editTicketReasonSubCategoryData.isDefaultCaseCategory
                    });

                    if (this.editTicketReasonSubCategoryData.parentCategory) {
                        this.ticketReasonSubCatFormGroup.patchValue({
                            parentCategory: this.editTicketReasonSubCategoryData.parentCategory.id
                        });
                    }

                    let data = [];
                    response.data.caseCategoryTatMappingList.forEach((element, i) => {
                        let queryData = [];
                        this.orderid = i + 1;
                        let teamcondition = "";
                        if (element.tatQueryFieldMappingList.length > 0) {
                            let queryDataLength = element.tatQueryFieldMappingList.length - 1;
                            queryData = element.tatQueryFieldMappingList;

                            queryData.forEach((element, index) => {
                                if (index > 0) {
                                    this.conditionDataEnableDisble.push(true);
                                }
                                if (queryDataLength != index) {
                                    teamcondition =
                                        teamcondition +
                                        " " +
                                        element.queryField +
                                        " " +
                                        element.queryOperator +
                                        " " +
                                        element.queryValue +
                                        " " +
                                        element.queryCondition;
                                } else {
                                    teamcondition =
                                        teamcondition +
                                        " " +
                                        element.queryField +
                                        " " +
                                        element.queryOperator +
                                        element.queryValue;
                                }
                            });
                        }

                        this.TatMappingArray.push(
                            this.fb.group({
                                id: [element.id],
                                orderid: [element.orderid],
                                teamcondition: [teamcondition],
                                tatForTicketID: [element.ticketTatMatrix.id],
                                tatForTicketData: [element.ticketTatMatrix],
                                tatQueryFieldMappingList: [element.tatQueryFieldMappingList],
                                caseCategoryId: [element.caseCategoryId]
                            })
                        );
                    });
                    this.updateTatMappingDataSource();
                    this.openEditCategoryDetails(id);
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    searchTrsc() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPageTicketReasonSubCategoryListdata = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage) {
            this.ticketReasonSubCategoryListdataitemsPerPage = this.showItemPerPage;
        }
        let data: any = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "name"
                }
            ],
            page: "",
            pageSize: ""
        };
        if (this.searchTrscName && !this.searchParentTrcName) {
            this.searchData.filters[0].filterColumn = "name";
            this.searchData.filters[0].filterValue = this.searchTrscName.trim();
            this.searchData.page = this.currentPageTicketReasonSubCategoryListdata;
            this.searchData.pageSize = this.ticketReasonSubCategoryListdataitemsPerPage;
            data = this.searchData;
        } else if (!this.searchTrscName && this.searchParentTrcName) {
            this.searchData.filters[0].filterValue = this.searchParentTrcName.categoryName;
            this.searchData.page = this.currentPageTicketReasonSubCategoryListdata;
            this.searchData.pageSize = this.ticketReasonSubCategoryListdataitemsPerPage;
            data = this.searchData;
        } else if (this.searchTrscName && this.searchParentTrcName) {
            this.searchAllData.filters[0].filterValue = this.searchTrscName.trim();
            this.searchAllData.filters[1].filterValue = this.searchParentTrcName.categoryName;
            this.searchAllData.page = this.currentPageTicketReasonSubCategoryListdata;
            this.searchAllData.pageSize = this.ticketReasonSubCategoryListdataitemsPerPage;
            data = this.searchAllData;
        }

        const url = "/CaseCategory/searchAll";
        this.taskCategoryService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response?.dataList?.length > 0) {
                    this.ticketReasonSubCategoryListData = response.dataList;
                    this.ticketReasonSubCategoryListDatatotalRecords = response.totalRecords;

                    this.dataSource.data = this.ticketReasonSubCategoryListData;

                    if (this.paginator) {
                        this.paginator.length = this.ticketReasonSubCategoryListDatatotalRecords;
                    }
                } else {
                    this.ticketReasonSubCategoryListData = [];
                    this.ticketReasonSubCategoryListDatatotalRecords = 0;
                    this.dataSource.data = [];
                    this.toastr.info(`No Record Found`, 'Info!');
                }
            },
            (error: any) => {
                this.ticketReasonSubCategoryListDatatotalRecords = 0;
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.ticketReasonSubCategoryListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearSearchTrsc() {
        this.searchTrscName = "";
        this.searchParentTrcName = "";
        this.getTicketReasonSubCategoryDataList("");
        this.listTicket = true;
        this.createTicket = false;
        this.currentPageTicketReasonSubCategoryListdata = 1;
    }

    deleteTrsc(data) {
        const url = "/CaseCategory/delete";
        this.taskCategoryService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406 || response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else if (response.responseCode == 304) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else {
                    if (
                        this.currentPageTicketReasonSubCategoryListdata != 1 &&
                        this.ticketReasonSubCategoryListData.length == 1
                    ) {
                        this.currentPageTicketReasonSubCategoryListdata =
                            this.ticketReasonSubCategoryListData - 1;
                    }
                    if (!this.searchkey) {
                        this.getTicketReasonSubCategoryDataList("");
                    } else {
                        this.searchTrsc();
                    }
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    trscAllDetails(data) {
        this.ticketReasonSubCatdialoge = true;
        let data1 = [];
        this.coditionArrayShow = [];
        this.viewTrscData = data;

        let teamcondition = "";
        data1 = this.viewTrscData.caseCategoryTatMappingList;
        data1.forEach(element => {
            let queryData = [];
            let teamcondition = "";
            if (element.tatQueryFieldMappingList.length > 0) {
                let queryDataLength = element.tatQueryFieldMappingList.length - 1;
                queryData = element.tatQueryFieldMappingList;
                queryData.forEach((element, index) => {
                    if (index > 0) {
                        this.conditionDataEnableDisble.push(true);
                    }
                    if (queryDataLength != index) {
                        teamcondition =
                            teamcondition +
                            " " +
                            element.queryField +
                            " " +
                            element.queryOperator +
                            " " +
                            element.queryValue +
                            " " +
                            element.queryCondition;
                    } else {
                        teamcondition =
                            teamcondition +
                            " " +
                            element.queryField +
                            " " +
                            element.queryOperator +
                            element.queryValue;
                    }
                });
            }

            this.coditionArrayShow.push(teamcondition);
            this.openCategoryDetails(data);
        });
    }

    displayedColumns = ['id', 'Name', 'Status', 'Action'];
    tatDisplayedColumns: string[] = ['Order', 'TAT For Task', 'Delete'];

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('addCategoryDetails', { static: true }) addCategoryDetails!: TemplateRef<any>;
    @ViewChild('viewCategoryDetails', { static: true }) viewCategoryDetails!: TemplateRef<any>;

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    @ViewChild('deleteDialog') deleteDialog!: TemplateRef<any>;

    changeTATPageData(pageNumber) {
        this.currentPageViewTATListdata = pageNumber;
    }

    pageChangedViewReasonData(pageNumber) {
        this.currentPageViewReasonListdata = pageNumber;
    }

    FildDropdownData = [];

    getFildDropdownValue() {
        this.FildDropdownData = [];
        let url = `/commonList/generic/CASE_CONDITION`;
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.FildDropdownData = response.dataList;
        });
    }

    createteamConditionForm(): UntypedFormGroup {
        return this.fb.group({
            field: ["", Validators.required],
            operator: ["", Validators.required],
            value: ["", Validators.required],
            condition: [""]
        });
    }

    onAddAttribute() {
        this.teamSubmitted = true;
        this.teamConditionData = [];
        if (this.tatForTicketID) {
            this.orderid = this.orderid + 1;
            this.TatMappingArray.push(
                this.createAttributeFormGroup(
                    this.orderid,
                    this.teamcondition,
                    this.tatForTicketID,
                    this.coditionArray
                )
            );
            this.updateTatMappingDataSource();
            // Reset the form inputs
            this.tatForTicketID = "";
            this.teamcondition = "";
        }
    }

    createAttributeFormGroup(orderid, condition, tatForTicketID, coditionArray): UntypedFormGroup {
        this.teamcondition = "";
        this.tatForTicketID = "";
        this.teamSubmitted = false;
        this.coditionArray = [];

        return this.fb.group({
            id: [''],
            orderid: [orderid],
            teamcondition: [condition],
            tatForTicketID: [tatForTicketID],
            tatQueryFieldMappingList: [coditionArray],
            caseCategoryId: ['']
        });
    }

    deleteConfirmAttribute(i, data) {
        let teamConData = this.TatMappingArray.value;

        this.TatMappingArray.removeAt(i);
        this.orderid = this.orderid - 1;
        this.TatMappingArray.value.forEach((element, i) => {
            let n = i + 1;
            element.orderid = n;
            if (this.TatMappingArray.value.length == n) {
                this.TatMappingArray.patchValue(this.TatMappingArray.value);
            }
        });
        this.updateTatMappingDataSource();
    }

    deleteTeamCondition(index, data) {
        let teamConData = this.teamConditionArray.value;
        let teamTotalData = teamConData.length;

        teamConData.forEach((element, i) => {
            if (element == data) {
                this.teamConditionArray.removeAt(i);
                if (i % 5 == 0 && teamTotalData == i + 1) {
                    this.currentPageteamCondition = this.currentPageteamCondition - 1;
                }
            }
        });
    }

    pageChangedTeamMappingList(page) {
        this.currentPageteamCondition = page;
    }

    saveConditionData() {
        this.teamcondition = "";
        this.coditionArray = [];
        let quaryFieldCondition = "";
        let quaryConditionArray = [];
        let detailsFormValue = this.teamConditionArray.value;
        this.teamConditionData = detailsFormValue;

        let detailsLength = detailsFormValue.length - 1;
        detailsFormValue.forEach((element, index) => {
            this.coditionArray.push({
                id: element.id ? element.id : null,
                queryField: element.field,
                queryOperator: element.operator,
                queryValue: element.value,
                queryCondition: element.condition,
                tatMappingId: this.teamMappingTeamID ? this.teamMappingTeamID : this.teamValue
            });

            if (detailsLength != index) {
                this.teamcondition =
                    this.teamcondition +
                    " " +
                    element.field +
                    " " +
                    element.operator +
                    " " +
                    element.value +
                    " " +
                    element.condition;
            } else {
                this.teamcondition =
                    this.teamcondition + " " + element.field + " " + element.operator + element.value;
            }
        });
        quaryConditionArray = this.coditionArray;
        quaryFieldCondition = this.teamcondition;

        if (this.teamQueryFieldListINDEX !== "") {
            this.TatMappingArray.value[this.teamQueryFieldListINDEX].tatQueryFieldMappingList =
                quaryConditionArray;
            this.TatMappingArray.value[this.teamQueryFieldListINDEX].teamcondition = quaryFieldCondition;

            this.TatMappingArray.patchValue(this.TatMappingArray.value);
            this.closeConditionModel();
            this.teamcondition = "";
            this.coditionArray = [];
            this.teamMappingTeamID = "";
        }

        if (this.teamcondition || quaryFieldCondition) {
            this.closeConditionModel();
            this.conditionDataEnableDisble.push(true);
        }
    }

    openConditionModel() {
        this.ticketReasonSubCatModal = true;
        this.teamQueryFieldListINDEX = "";
        if (this.teamcondition) {
            this.coditionArray.forEach((element, index) => {
                this.teamConditionArray.push(
                    this.fb.group({
                        id: element.id,
                        field: element.queryField,
                        operator: element.queryOperator,
                        value: element.queryValue,
                        condition: element.queryCondition
                    })
                );
                if (index > 0) {
                    this.conditionDataEnableDisble.push(true);
                }
            });
        } else {
            this.onConditionAddAttribute();
        }
    }

    closeConditionModel() {
        this.teamConditionArray.reset();
        this.teamConditionArray = this.fb.array([]);
        this.ticketReasonSubCatModal = false;
    }

    onkeyCondition(e) {
        if (e.value != "") {
            this.errormsgCondition = "";
        }
    }

    onConditionAddAttribute() {
        if (this.teamConditionArray.value.length >= 1) {
            let index = this.teamConditionArray.value.length - 1;
            if (this.teamConditionArray.value[index].condition == "" && this.teamConditionArray.valid) {
                this.errormsgCondition = "Please add condition first";
                this.conditionDataEnableDisble.push(true);
                this.teamConditionArray.push(this.createteamConditionForm());
            } else if (!this.teamConditionArray.valid) {
                this.errormsgCondition = "";
            } else {
                this.errormsgCondition = "";
                this.conditionDataEnableDisble.push(true);
                this.teamConditionArray.push(this.createteamConditionForm());
            }
        } else {
            this.teamConditionArray.push(this.createteamConditionForm());
            this.conditionDataEnableDisble.push(true);
        }
    }

    defultAddTeamCondition(index, teamId) {
        this.teamConditionArray.reset();
        this.teamConditionArray = this.fb.array([]);
        let hierarchyMappingList = [];
        this.conditionDataEnableDisble = [];
        this.teamMappingTeamID = teamId;
        this.teamQueryFieldListINDEX = index;
        if (this.TatMappingArray.length > 0) {
            hierarchyMappingList = this.TatMappingArray.value[index].tatQueryFieldMappingList;
        }
        if (hierarchyMappingList.length > 0) {
            hierarchyMappingList.forEach((element, index) => {
                if (index > 0) {
                    this.conditionDataEnableDisble.push(true);
                }
                this.teamConditionArray.push(
                    this.fb.group({
                        id: element.id,
                        field: element.queryField,
                        operator: element.queryOperator,
                        value: element.queryValue,
                        condition: element.queryCondition
                    })
                );
            });
        }
    }

    canExit() {
        if (
            !this.ticketReasonSubCatFormGroup.dirty
        )
            return true;
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

    closeSubProblemDomainDetailsModel() {
        this.ticketReasonSubCatdialoge = false;
    }

    areaDataOpenModel(data) {
        this.areaparticularData = data;
        this.areaModal = true;
    }

    closeAreaModal() {
        this.areaModal = false;
    }

    openAddCategoryDetails(): void {
        this.isTicketReasonSubCategoryEdit = false;
        this.ticketReasonSubCatFormGroup.reset();
        this.TatMappingArray = this.fb.array([]);
        this.updateTatMappingDataSource();
        this.orderid = 0;
        const dialogRef = this.dialog.open(this.addCategoryDetails, {
            width: '1200px',
            disableClose: true,
            data: {
                isEdit: false,
                editData: null,
                categoryId: null
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            this.createTicket = false;
            this.clearSearchTrsc();
        });
    }

    openEditCategoryDetails(id: any): void {
        this.isTicketReasonSubCategoryEdit = true;
        const dialogRef = this.dialog.open(this.addCategoryDetails, {
            width: '1200px',
            disableClose: true,
            data: {
                isEdit: this.isTicketReasonSubCategoryEdit,
                editData: this.editTicketReasonSubCategoryData,
                categoryId: id
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            this.createTicket = false;
            this.clearSearchTrsc();
        });
    }

    openCategoryDetails(id: any): void {
        const dialogRef = this.dialog.open(this.viewCategoryDetails, {
            width: '900px',
            disableClose: true,
        });

        dialogRef.afterClosed().subscribe(result => {
            this.clearSearchTrsc();
        });
    }

    deleteConfirmonTicketReasonSubCat(id) {
        if (id) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Do you want to delete the "${id.categoryName}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteTrsc(id);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    updateTatMappingDataSource() {
        this.tatMappingDataSource.data = this.TatMappingArray.controls;
    }
}
