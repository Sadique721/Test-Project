import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Regex } from "src/app/constants/regex";
import { TicketReasonSubCategoryService } from "src/app/service/ticket-reason-sub-category.service";
import { TicketReasonSubCategory } from "src/app/components/model/ticket-reason-sub-category";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer } from "rxjs";
import { TICKETING_SYSTEMS } from "src/app/constants/aclConstants";
import { MatDialog } from "@angular/material/dialog";
import { MatTable } from "@angular/material/table";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-ticket-reason-sub-category",
    templateUrl: "./ticket-reason-sub-category.component.html",
    styleUrls: ["./ticket-reason-sub-category.component.css"],
    standalone: false
})
export class TicketReasonSubCategoryComponent implements OnInit {
    ticketReasonSubCatFormGroup: UntypedFormGroup;
    ticketReasonMapingForm: UntypedFormGroup;
    ticketReasonMaping: UntypedFormArray;
    submitted = false;
    ticketReasonMapingSubmitted = false;
    parentTRCData: any;
    statusOptions = RadiusConstants.status;
    currentPageReasonMapping = 1;
    reasonMappingitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    reasonMappingtotalRecords: number;
    isTicketReasonSubCategoryEdit = false;
    createTicketReasonSubCategoryData: TicketReasonSubCategory = {
        subCategoryName: "",
        ticketSubCategoryGroupReasonMappingList: [],
        ticketSubCategoryTatMappingList: [],
        ticketSubCategoryReasonCategoryMappingList: [],
        status: "",
        id: "",
        isDefaultSubProblemDomain: false
    };
    currentPageTicketReasonSubCategoryListdata = 1;
    ticketReasonSubCategoryListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ticketReasonSubCategoryListDatatotalRecords: any;
    ticketReasonSubCategoryListData: any;
    showItemPerPage: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    searchkey: string;
    editTicketReasonSubCategoryData: TicketReasonSubCategory;
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
    ticketReasonSubCatdialoge: boolean = false;
    ticketReasonSubCatModal: boolean = false;
    coditionArrayShow = [];
    pageItem;
    constructor(
        private toastr: ToastrService,

        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private ticketReasonSubCategoryService: TicketReasonSubCategoryService,
        public commondropdownService: CommondropdownService,
        public loginService: LoginService, private dialog: MatDialog,
    ) {
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(TICKETING_SYSTEMS.SUB_PB_DOMAIN_CREATE);
        this.deleteAccess = loginService.hasPermission(TICKETING_SYSTEMS.SUB_PB_DOMAIN_DELETE);
        this.editAccess = loginService.hasPermission(TICKETING_SYSTEMS.SUB_PB_DOMAIN_EDIT);

        // this.isTicketReasonSubCategoryEdit = !createAccess && editAccess ? true : false;
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(TICKETING_SYSTEMS.SUB_PB_DOMAIN_EDIT) || this.loginService.hasPermission(TICKETING_SYSTEMS.SUB_PB_DOMAIN_DELETE)) {
            return ['id', 'subCategoryName', 'status', 'action'];
        } else {
            return ['id', 'subCategoryName', 'status'];
        }
    }
    ngOnInit(): void {
        this.ticketReasonSubCatFormGroup = this.fb.group({
            subCategoryName: ["", Validators.required],
            status: ["", Validators.required],
            parentCategory: ["", Validators.required],
            isDefaultSubProblemDomain: [false]
        });
        this.ticketReasonMapingForm = this.fb.group({
            reason: ["", Validators.required],
            ticketReasonSubCategoryId: [""]
        });
        this.ticketReasonMaping = this.fb.array([]);
        this.getTicketReasonCategoryDataList();
        this.getTicketReasonSubCategoryDataList("");
        this.getFildDropdownValue();
        this.getTATForTicketList();
        this.viewTrscData = {
            status: "",
            ticketSubCategoryGroupReasonMappingList: [],
            ticketSubCategoryTatMappingList: [],
            parentCategory: {
                categoryName: ""
            },
            subCategoryName: ""
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
                },
                {
                    filterValue: "",
                    filterColumn: "parentCategoryName"
                }
            ],
            page: "",
            pageSize: "",
            sortBy: "createdate",
            sortOrder: 0
        };
    }

    createTicketFun(): void {
        this.listTicket = false;
        this.createTicket = true;
        this.submitted = false;
        this.isTicketReasonSubCategoryEdit = false;
        this.ticketReasonSubCatFormGroup.reset();
        this.submitted = false;
        this.ticketReasonMaping.controls = [];
        this.orderid = 0;
        this.teamConditionArray.reset();
        this.ticketReasonSubCatFormGroup.reset();
        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);
        this.ticketReasonMaping = this.fb.array([]);
        this.tatForTicketID = "";
        this.teamcondition = "";
        this.ticketReasonSubCatFormGroup.get("isDefaultSubProblemDomain").enable();
    }

    searchTicketFun(): void {
        this.listTicket = true;
        this.createTicket = false;
        this.isTicketReasonSubCategoryEdit = false;
        this.ticketReasonSubCatFormGroup.reset();
        this.ticketReasonMaping.controls = [];
        this.ticketReasonSubCatFormGroup.get("isDefaultSubProblemDomain").enable();
        this.pageItem = this.ticketReasonSubCategoryListdataitemsPerPage;
        this.getTicketReasonSubCategoryDataList("");
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
        const url = "/ticketReasonSubCategory";
        this.ticketReasonSubCategoryService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.ticketReasonSubCategoryListData = response.dataList;
                this.ticketReasonSubCategoryListDatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getTicketReasonCategoryDataList() {
        const url = "/ticketReasonCategory/getAllActiveReasonCatgory";
        this.ticketReasonSubCategoryService.getMethod(url).subscribe(
            (response: any) => {
                this.parentTRCData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    ticketReasonMappingFormGroup(): UntypedFormGroup {
        return this.fb.group({
            reason: [this.ticketReasonMapingForm.value.reason, [Validators.required]],
            ticketReasonSubCategoryId: [""]
        });
    }

    onAddReasonMappingField() {
        this.ticketReasonMapingSubmitted = true;
        if (this.ticketReasonMapingForm.valid) {
            this.ticketReasonMaping.push(this.ticketReasonMappingFormGroup());
            this.ticketReasonMapingForm.reset();
            this.ticketReasonMapingSubmitted = false;
            this.resonmappingtable.renderRows()
        }
    }

    addEditTicketReasonSubCat(id) {
        let TatMappingList: any = [];
        let quaryArray: any = [];
        let ticketCatogary: any = [];
        let catogaryData = [];
        this.submitted = true;

        if (this.ticketReasonSubCatFormGroup.value.isDefaultSubProblemDomain == null) {
            this.ticketReasonSubCatFormGroup.controls.isDefaultSubProblemDomain.setValue(false);
        }
        if (this.ticketReasonSubCatFormGroup.valid) {
            if (this.TatMappingArray.value.length > 0) {
                if (id) {
                    const url = "/ticketReasonSubCategory/update";

                    if (this.TatMappingArray.value.length > 0) {
                        this.TatMappingArray.value.forEach((element, i) => {
                            if (element.tatQueryFieldMappingList.length > 0) {
                                quaryArray = element.tatQueryFieldMappingList;
                            } else {
                                quaryArray = [];
                            }
                            TatMappingList.push({
                                id: element.id,
                                orderid: element.orderid,
                                tatQueryFieldMappingList: quaryArray,
                                ticketTatMatrix: {
                                    id: element.tatForTicketID
                                },
                                ticketReasonSubCategoryId: id
                            });
                        });
                    }
                    //this.createTicketReasonSubCategoryData = this.ticketReasonSubCatFormGroup.value;
                    this.createTicketReasonSubCategoryData.id = id;
                    this.createTicketReasonSubCategoryData.ticketSubCategoryGroupReasonMappingList =
                        this.ticketReasonMaping.value;
                    catogaryData = this.ticketReasonSubCatFormGroup.value.parentCategory;
                    if (catogaryData.length > 0) {
                        catogaryData.forEach((element, i) => {
                            let n = i + 1;
                            ticketCatogary.push({
                                ticketReasonCategoryId: element,
                                ticketReasonSubCategoryId: this.createTicketReasonSubCategoryData.id
                            });
                            if (n == catogaryData.length) {
                                this.createTicketReasonSubCategoryData.ticketSubCategoryReasonCategoryMappingList =
                                    ticketCatogary;
                            }
                        });
                    }
                    this.createTicketReasonSubCategoryData.ticketSubCategoryTatMappingList = TatMappingList;
                    this.createTicketReasonSubCategoryData.status =
                        this.ticketReasonSubCatFormGroup.value.status;

                    this.createTicketReasonSubCategoryData.subCategoryName =
                        this.ticketReasonSubCatFormGroup.value.subCategoryName;

                    this.createTicketReasonSubCategoryData.isDefaultSubProblemDomain =
                        this.ticketReasonSubCatFormGroup.value.isDefaultSubProblemDomain;

                    // this.createTicketReasonSubCategoryData.parentCategory = parentCatId;

                    // if (
                    //   this.createTicketReasonSubCategoryData.ticketSubCategoryGroupReasonMappingList.length < 1
                    // ) {
                    //   this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Please add Reason details",
                    //     icon: "far fa-times-circle",
                    //   });
                    //
                    // } else {
                    this.ticketReasonSubCategoryService
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


                                    this.clearTicketReasonSubCategory();
                                    this.isTicketReasonSubCategoryEdit = false;
                                }
                            },
                            (error: any) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        );
                    // }
                } else {
                    const url = "/ticketReasonSubCategory/save";

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
                    this.createTicketReasonSubCategoryData.id = "";
                    this.createTicketReasonSubCategoryData.ticketSubCategoryGroupReasonMappingList =
                        this.ticketReasonMaping.value;
                    catogaryData = this.ticketReasonSubCatFormGroup.value.parentCategory;
                    if (catogaryData.length > 0) {
                        catogaryData.forEach((element, i) => {
                            let n = i + 1;
                            ticketCatogary.push({
                                ticketReasonCategoryId: element,
                                ticketReasonSubCategoryId: this.createTicketReasonSubCategoryData.id
                            });
                            if (n == catogaryData.length) {
                                this.createTicketReasonSubCategoryData.ticketSubCategoryReasonCategoryMappingList =
                                    ticketCatogary;
                            }
                        });
                    }

                    this.createTicketReasonSubCategoryData.ticketSubCategoryTatMappingList = TatMappingList;
                    this.createTicketReasonSubCategoryData.status =
                        this.ticketReasonSubCatFormGroup.value.status;

                    this.createTicketReasonSubCategoryData.subCategoryName =
                        this.ticketReasonSubCatFormGroup.value.subCategoryName;
                    // // console.log(" this.createTicketReasonCategoryData", this.createTicketReasonSubCategoryData);
                    // if (
                    //   this.createTicketReasonSubCategoryData.ticketSubCategoryGroupReasonMappingList.length < 1
                    // ) {
                    //   this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Please add Reason details",
                    //     icon: "far fa-times-circle",
                    //   });
                    //
                    // } else {
                    this.createTicketReasonSubCategoryData.isDefaultSubProblemDomain =
                        this.ticketReasonSubCatFormGroup.value.isDefaultSubProblemDomain;
                    this.ticketReasonSubCategoryService
                        .postMethod(url, this.createTicketReasonSubCategoryData)
                        .subscribe(
                            (response: any) => {
                                if (response.responseCode == 406) {
                                    this.toastr.info(`${response.responseMessage}`, 'Info!');


                                } else {
                                    this.toastr.success(`Successfully Added`, 'Success!');


                                    this.clearTicketReasonSubCategory();
                                }
                            },
                            (error: any) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                            }
                        );
                    // }
                }
            } else {
                (error: any) => {
                    this.toastr.info(`${error.error.ERROR}`, 'Please add TAT Mapping!');

                }


            }
        }
    }

    getTATForTicketList() {
        const url = "/tickettatmatrix/searchByStatus";
        this.ticketReasonSubCategoryService.getMethod(url).subscribe(
            (response: any) => {
                this.TATForTicketData = response.dataList;
            },
            (error: any) => { }
        );
    }

    clearTicketReasonSubCategory() {
        this.submitted = false;
        this.getTicketReasonSubCategoryDataList("");
        this.listTicket = true;
        this.createTicket = false;
        this.teamConditionArray.reset();
        this.ticketReasonSubCatFormGroup.reset();
        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);
        this.ticketReasonMaping = this.fb.array([]);
        this.tatForTicketID = "";
        this.teamcondition = "";
    }

    pageChangedReasonMappingData(pageNumber) {
        this.currentPageReasonMapping = pageNumber;
    }

    deleteConfirmonReasonMappingField(reasonMappingFieldIndex: number, reasonMappingFieldId: number) {
        if (reasonMappingFieldIndex || reasonMappingFieldIndex == 0) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: "Do you want to delete this Reason?",
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.onRemoveReasonMapping(reasonMappingFieldIndex, reasonMappingFieldId);
                } else {
                    (error: any) => {
                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');

                    }

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Reason?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.onRemoveReasonMapping(reasonMappingFieldIndex, reasonMappingFieldId);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }

    async onRemoveReasonMapping(reasonMappingFieldIndex: number, reasonMappingFieldId: number) {
        this.ticketReasonMaping.removeAt(reasonMappingFieldIndex);
        this.resonmappingtable.renderRows()
    }

    TotalItemPerPage(event) {
        this.ticketReasonSubCategoryListdataitemsPerPage = Number(event.value);
        if (this.currentPageTicketReasonSubCategoryListdata > 1) {
            this.currentPageTicketReasonSubCategoryListdata = 1;
        }
        this.getTicketReasonSubCategoryDataList(this.showItemPerPage);
    }

    pageChangedTrscList(event) {
        this.currentPageTicketReasonSubCategoryListdata = event.pageIndex + 1;
        this.ticketReasonSubCategoryListdataitemsPerPage = event.pageSize;
        this.getTicketReasonSubCategoryDataList("");
    }

    editTicketReasonSubCategory(id) {
        this.tatForTicketID = "";
        this.teamcondition = "";

        this.ticketReasonSubCatFormGroup.reset();
        this.ticketReasonMapingForm.reset();
        this.isTicketReasonSubCategoryEdit = true;

        this.listTicket = false;
        this.createTicket = true;

        this.ticketReasonMaping = this.fb.array([]);
        this.teamConditionArray = this.fb.array([]);
        this.TatMappingArray = this.fb.array([]);
        this.orderid = 0;
        this.ticketCatogryData = [];
        let catogaryData = [];

        const url = "/ticketReasonSubCategory/" + id;
        this.ticketReasonSubCategoryService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.editTicketReasonSubCategoryData = response.data;
                    this.ticketReasonSubCatFormGroup.patchValue({
                        subCategoryName: this.editTicketReasonSubCategoryData.subCategoryName,
                        status: this.editTicketReasonSubCategoryData.status,
                        // parentCategory: this.editTicketReasonSubCategoryData.parentCategory.id,
                        isDefaultSubProblemDomain:
                            this.editTicketReasonSubCategoryData.isDefaultSubProblemDomain
                    });

                    this.ticketReasonMaping = this.fb.array([]);
                    this.editTicketReasonSubCategoryData.ticketSubCategoryGroupReasonMappingList.forEach(
                        element => {
                            this.ticketReasonMaping.push(this.fb.group(element));
                        }
                    );
                    this.ticketReasonMaping.patchValue(
                        this.editTicketReasonSubCategoryData.ticketSubCategoryGroupReasonMappingList
                    );
                    catogaryData =
                        this.editTicketReasonSubCategoryData.ticketSubCategoryReasonCategoryMappingList;
                    if (catogaryData.length > 0) {
                        catogaryData.forEach((element, i) => {
                            let n = i + 1;
                            this.ticketCatogryData.push(element.ticketReasonCategoryId);
                            if (n == catogaryData.length) {
                                this.ticketReasonSubCatFormGroup.patchValue({
                                    parentCategory: this.ticketCatogryData
                                });
                            }
                        });
                    }

                    let data = [];
                    response.data.ticketSubCategoryTatMappingList.forEach((element, i) => {
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
                                tatQueryFieldMappingList: [element.tatQueryFieldMappingList],
                                ticketReasonSubCategoryId: [id]
                            })
                        );

                        this.tatMapping.renderRows()
                        this.resonmappingtable.renderRows()
                    });
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }
            },
            (error: any) => {
                // console.log(error, "error")
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
            this.searchData.filters[0].filterColumn = "parentCategoryName";
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

        // console.log("this.searchData", this.searchData)
        const url = "/ticketReasonSubCategory/searchAll";
        this.ticketReasonSubCategoryService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response?.dataList?.length > 0) {
                    this.ticketReasonSubCategoryListData = response.dataList;
                    this.ticketReasonSubCategoryListDatatotalRecords = response.totalRecords;
                } else {
                    this.ticketReasonSubCategoryListData = [];
                    this.ticketReasonSubCategoryListDatatotalRecords = 0;
                    this.toastr.info(`${response.responseMessage}`, 'No Record Found!');


                }
            },
            (error: any) => {
                this.ticketReasonSubCategoryListDatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');


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
    }

    deleteConfirmonTicketReasonSubCat(TrscData) {
        if (TrscData) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: "Do you want to delete this Ticket Sub Problem Domain?",
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteTrsc(TrscData);
                } else {
                    (error: any) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                }
            });
            // this.confirmationService.confirm({this.toastr.info(`${response.responseMessage}`, 'Info!');

            //     message: "Do you want to delete this Ticket Sub Problem Domain?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteTrsc(TrscData);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }

    deleteTrsc(data) {
        const url = "/ticketReasonSubCategory/delete";
        this.ticketReasonSubCategoryService.postMethod(url, data).subscribe(
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
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    trscAllDetails(data) {
        this.ticketReasonSubCatdialoge = true;
        let data1 = [];
        this.coditionArrayShow = [];
        this.viewTrscData = data;
        this.dialog.open(this.detailsDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        let teamcondition = "";
        data1 = this.viewTrscData.ticketSubCategoryTatMappingList;
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
        });
    }

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
            this.tatMapping.renderRows()
        }
    }

    createAttributeFormGroup(orderid, condition, tatForTicketID, coditionArray): UntypedFormGroup {
        this.teamcondition = "";
        this.tatForTicketID = "";
        this.teamSubmitted = false;
        this.coditionArray = [];

        return this.fb.group({
            orderid: [orderid],
            teamcondition: [condition],
            tatForTicketID: [tatForTicketID],
            tatQueryFieldMappingList: [coditionArray]
        });
    }

    deleteConfirmAttribute(i, data) {
        let teamConData = this.TatMappingArray.value;
        // let teamTotalData = teamConData.length;
        // teamConData.forEach((element, i) => {
        //   if (element == data) {
        //     this.TatMappingArray.removeAt(i);
        //     if (i % 5 == 0 && teamTotalData == i + 1) {
        //       this.currentPageTeams = this.currentPageTeams - 1;
        //     }
        //   }
        // });

        this.TatMappingArray.removeAt(i);
        this.orderid = this.orderid - 1;
        this.TatMappingArray.value.forEach((element, i) => {
            let n = i + 1;
            element.orderid = n;
            if (this.TatMappingArray.value.length == n) {
                this.TatMappingArray.patchValue(this.TatMappingArray.value);
            }
        });
        this.tatMapping.renderRows()
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
        this.teamConditiontable.renderRows()
    }

    pageChangedTeamMappingList(page) {
        this.currentPageteamCondition = page;
    }

    saveConditionData(dialogRef) {
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
            // hierarchyMappingList = this.TatMappingArray.value

            // hierarchyMappingList.forEach((element , i) => {
            //   if(this.teamQueryFieldListINDEX == i){
            // this.TatMappingArray.push(
            this.TatMappingArray.patchValue(this.TatMappingArray.value);
            this.closeConditionModel();
            this.teamcondition = "";
            this.coditionArray = [];
            this.teamMappingTeamID = "";
            // );
            //   }
            // })
        }

        if (this.teamcondition || quaryFieldCondition) {
            this.closeConditionModel();
            this.conditionDataEnableDisble.push(true);
        }
        dialogRef.close()
    }

    openConditionModel() {
        this.ticketReasonSubCatModal = true;
        this.dialog.open(this.teamConditionDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

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
            this.teamConditiontable.renderRows()
        } else {
            this.teamConditionArray.push(this.createteamConditionForm());
            this.teamConditiontable.renderRows()
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
        this.dialog.open(this.teamConditionDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
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
            this.teamConditiontable.renderRows()


        }
    }
    canExit() {
        if (
            !this.ticketReasonSubCatFormGroup.dirty &&
            !this.ticketReasonMapingForm.dirty &&
            !this.ticketReasonMaping.dirty
        )
            return true;
        {


            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: "The filled data will be lost. Do you want to continue? (Yes/No)",
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });

                dialogRef.afterClosed().subscribe((result) => {
                    if (result === true) {
                        observer.next(true);
                        observer.complete();
                    } else {
                        observer.next(false);
                        observer.complete();
                    }
                });

                // this.confirmationService.confirm({
                //     header: "Alert",
                //     message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                //     icon: "pi pi-info-circle",
                //     accept: () => {
                //         observer.next(true);
                //         observer.complete();
                //     },
                //     reject: () => {
                //         observer.next(false);
                //         observer.complete();
                //     }
                // });
                return false;
            });
        }
    }

    onParentChange(event: any) {
        var parentIds = event.value;

        const url = "/ticketReasonSubCategory/isReasonSubCategoryDefault";

        if (parentIds.length > 0) {
            this.ticketReasonSubCategoryService.postMethod(url, parentIds).subscribe(
                (response: any) => {
                    if (response.data) {
                        let isProblemDomain = response.data ? response.data : false;
                        if (this.isTicketReasonSubCategoryEdit) {
                            if (JSON.stringify(parentIds) === JSON.stringify(this.ticketCatogryData)) {
                                this.ticketReasonSubCatFormGroup.get("isDefaultSubProblemDomain").enable();
                                this.subProbleDomainPatchValue(isProblemDomain);
                            } else {
                                this.ticketReasonSubCatFormGroup.get("isDefaultSubProblemDomain").disable();
                                this.subProbleDomainPatchValue(false);
                            }
                        } else {
                            this.ticketReasonSubCatFormGroup.get("isDefaultSubProblemDomain").disable();
                            this.subProbleDomainPatchValue(false);
                        }
                    } else {
                        this.ticketReasonSubCatFormGroup.get("isDefaultSubProblemDomain").enable();
                        this.subProbleDomainPatchValue(false);
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    subProbleDomainPatchValue(isdefault: boolean) {
        this.ticketReasonSubCatFormGroup.patchValue({
            isDefaultSubProblemDomain: isdefault
        });
    }

    closeSubProblemDomainDetailsModel() {
        this.ticketReasonSubCatdialoge = false;
    }

    onNextStep1(stepper: any) {
        this.submitted = true
        this.ticketReasonSubCatFormGroup.markAllAsTouched();
        if (this.ticketReasonSubCatFormGroup.valid) {
            stepper.next();
        }
    }

    dataSourceData = [{}];
    displayedTATMappingColumns: string[] = ['orderid', 'tatName', 'condition'];
    @ViewChild('tatMapping') tatMapping!: MatTable<any>;
    tatMappingTableDispaly = ['order', 'tatForTicket', 'condition', 'delete']
    @ViewChild('resonmappingtable') resonmappingtable!: MatTable<any>;
    displayresonmapping = ['reason', 'delete']
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    displayedColumns: string[] = ['id', 'subCategoryName', 'status', 'action'];

    displayShowresonmapping = ['reason']
    @ViewChild('teamConditiontable') teamConditiontable!: MatTable<any>;
    @ViewChild('teamConditionDialog') teamConditionDialog!: TemplateRef<any>;
    dispayviewTat = ['field', 'operator', 'value', 'condition', 'delete']

    tatMappingTableDispalyADD = ['tatForTicket', 'condition', 'delete']
}
