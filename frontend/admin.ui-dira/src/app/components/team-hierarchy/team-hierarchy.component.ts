import { Component, OnInit, ViewChild, Output, EventEmitter, TemplateRef } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService, TreeNode } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { TeamsService } from "src/app/components/teams/teams.service";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { TeamHierarchyService } from "src/app/service/team-hierarchy.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer } from "rxjs";
import { WORKFLOWS } from "src/app/constants/aclConstants";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { MatSelect } from "@angular/material/select";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from 'ngx-toastr';

//option code
interface Invoice {
    id: number;
    billFrom: string;
    billTo: string;
    totalCost: number;
    status: string;
    completed: boolean;
}

declare var $: any;

@Component({
    selector: "app-team-hierarchy",
    templateUrl: "./team-hierarchy.component.html",
    styleUrls: ["./team-hierarchy.component.css"],
    standalone: false
})
export class TeamHierarchyComponent implements OnInit {

    @ViewChild('teamConditiontable') teamConditiontable!: MatTable<any>;
    @ViewChild('teamConditionDialog') teamConditionDialog!: TemplateRef<any>;

    @Output() closeWorkflow = new EventEmitter();
    teamHierarchyModal: boolean = false;
    teamHierarchyData: any[] = [];

    mockData = [{ value: "Team A" }, { value: "Team B" }, { value: "Team C" }];

    workflowName = "";
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;

    assignParentTeamForm: UntypedFormGroup;
    teamListData: any;
    parentTeamData: any;
    teamhierarchyData: any;
    submitted: boolean = false;
    teamConditionModal: boolean = false;

    // currentPageTeamListdata = 1;
    // teamListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    // teamListdatatotalRecords: String;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    currentPage: number = 0;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: number;
    parentTeamListData: any;
    assignparentTeamData: any;

    ItemPerPageteamCondition = RadiusConstants.ITEMS_PER_PAGE;
    currentPageteamCondition = 1;
    totalRecordsteamCondition: number;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;

    totalRecordsTeams = 0;
    ItemPerPageTeams = RadiusConstants.ITEMS_PER_PAGE;
    currentPageTeams: any = 1;

    createView: boolean = false;
    hierarchyListShow: boolean = true;
    hierarchyDeatilsShow: boolean = false;
    searchView: boolean = true;
    dataSource = new MatTableDataSource<any>([]);
    isEdit: boolean = false;
    teamHierarchyForm: UntypedFormGroup;
    teams: UntypedFormArray;
    status = [];
    submittedTH: boolean = false;
    teamSubmitted: boolean = false;
    searchName: string;
    teamValue: any = "";
    teamcondition: any = "";
    searchData: any;
    teamHerAction = "";
    teamHirerarchyDetailData: any = {
        teamHierarchyMappingList: []
    };
    displayedColumns = ["id", "workflow_name", "Action"];
    title: any;

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        setTimeout(() => this.fieldDropdown.open(), 0);
    }

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild("fieldDropdown") fieldDropdown!: MatSelect;
    @ViewChild("workflowManagement") workflowManagement;
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
    //this add condition page
    teamConditionArray: UntypedFormArray;
    teamConditionData: any = [];
    coditionArray: any[];
    evenCatagorytData: any = []; //{ value: 'field1', text: 'Field 1' }, { value: 'field2', text: 'Field 2' }
    eventActionData: any = [];
    teamQueryFieldListINDEX: string;
    teamMappingTeamID: any = "";
    errormsgCondition: string;
    showTeamConditionData: any = [];
    eventData: any;
    hierarchyId: number;
    tatMatricesID = "";
    conditionDataEnableDisble: boolean[] = [true, true];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isAutoApprove: any;
    isAutoAssign: any;

    constructor(
        private toastr: ToastrService,

        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private teamsService: TeamsService,
        private radiusUtility: RadiusUtility,
        private teamHierarchyService: TeamHierarchyService,
        public commondropdownService: CommondropdownService,
        loginService: LoginService
    ) {
        this.teamConditionArray = this.fb.array([]);
        this.createAccess = loginService.hasPermission(WORKFLOWS.WORKFLOW_CREATE);
        this.deleteAccess = loginService.hasPermission(WORKFLOWS.WORKFLOW_DELETE);
        this.editAccess = loginService.hasPermission(WORKFLOWS.WORKFLOW_EDIT);
        this.loginService = loginService;
        this.isEdit = !this.createAccess && this.editAccess ? true : false;
        this.getTeamHierarchy("");
        this.getAllParenetTeam();
        this.commondropdownService.getActiveMatrixList();
        this.getTeamData();
        this.getAllEvent();
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(WORKFLOWS.WORKFLOW_DELETE) || this.loginService.hasPermission(WORKFLOWS.WORKFLOW_EDIT)) {
            return ["id", "workflow_name", "Action"];
        } else {
            return ["id", "workflow_name"];
        }
    }
    ngOnInit(): void {
        this.teams = this.fb.array([]);
        this.teamHierarchyForm = this.fb.group({
            hierarchyName: ["", Validators.required],
            eventName: ["", Validators.required],
            id: [],
            teamHierarchyMappingList: [...this.teams.value],
            isAutoAssign: Boolean,
            isAutoApprove: Boolean
        });
        this.assignParentTeamForm = this.fb.group({
            parentteamid: ["", Validators.required]
        });

        this.teamConditionArray = this.fb.array([]);
        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "hierarchyname",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ]
        };
    }

    createMapping(templateRef: any) {
        this.workflowName = "";
        this.dialog.open(templateRef, {
            width: "80%", //dialog box width
            //   height: '50vh',
            panelClass: "fullscreen-dialog"
        });

        this.searchView = false;
        this.createView = true;
        this.hierarchyListShow = false;
        this.submitted = false;
        this.isEdit = false;
        this.hierarchyDeatilsShow = false;
        this.teamHierarchyForm.reset();
        this.teams = this.fb.array([]);
        this.teamValue = "";
        this.teamHerAction = "";
        this.teamcondition = "";
        this.coditionArray = [];
        this.teamConditionArray = this.fb.array([]);
        this.getTeamevent("");
        this.conditionDataEnableDisble = [];
    }

    listMapping() {
        this.searchView = true;
        this.createView = false;
        this.hierarchyListShow = true;
        this.hierarchyDeatilsShow = false;
        this.itemsPerPage = 5;
        this.currentPage = 0;
        this.searchName = "";
        this.searchkey = "";
        this.getTeamHierarchy("");
    }

    detailMapping(id, eventName) {
        this.searchView = false;
        this.createView = false;
        this.hierarchyListShow = false;
        this.hierarchyDeatilsShow = true;
        this.getTeamhierarchyDetail(id, eventName);
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.itemsPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getTeamHierarchy(this.showItemPerPage);
        } else {
            this.search();
        }
    }

    getAllEvent() {
        const url = "/commonList/hierarchy_event";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.eventData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getAllParenetTeam() {
        this.parentTeamData = [];
        this.teamsService.getAllParentTeam().subscribe(
            (response: any) => {
                this.parentTeamData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getTeamHierarchy(list) {
        // let size;

        // let page_list = this.currentPage;
        // if (list) {
        //     size = list;
        //     this.itemsPerPage = list;
        // } else {
        //     // if (this.showItemPerPage == 0) {
        //     //   this.itemsPerPage = this.pageITEM
        //     // } else {
        //     //   this.itemsPerPage = this.showItemPerPage
        //     // }
        //     size = this.itemsPerPage;
        // }
        // let teamdata = {
        //     page: page_list,
        //     pageSize: size
        // };
        this.teamhierarchyData = [];
        const url = "/teamHierarchy/hierarchy/all";
        this.teamHierarchyService.getMethod(url).subscribe(
            (response: any) => {
                this.teamhierarchyData = response.dataList;
                this.totalRecords = response.totalRecords;
                // let eventData = [];

                // this.teamhierarchyData.forEach((element) => {
                //   eventData.push(element.eventName);
                // });

                // this.eventData = this.eventData.filter(function (el) {
                //   return eventData.indexOf(el.id) < 0;
                // });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getTeamData() {
        this.teamsService.getAllParentTeam().subscribe(
            (response: any) => {
                this.teamListData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    assignParentTeamModalOpen(data) {
        $("#assignParentTeamModal").modal("show");
        this.assignparentTeamData = data;
        this.parentTeamListData = this.parentTeamData.filter(team => team.id != data.id);
    }

    checkAssignparentTeam() {
        this.submitted = true;
        if (this.assignParentTeamForm.valid) {
            const url =
                "/teams/checkTeamIsAlreadyParentTeam/" +
                this.assignParentTeamForm.controls.parentteamid.value;
            this.teamsService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.data == false) {
                        this.assignParentTeam();
                        //console.log(true);

                        this.submitted = false;
                    } else {
                        this.submitted = false;

                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Selected parent team is already parent team of other team. Please select other team!');
                        }

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            );
        }
    }

    addEditTeamHierarchy(dialogRef) {
        this.submittedTH = true;
        if (this.teamHierarchyForm.valid) {
            if (this.isEdit) {
                const url = "/teamHierarchy/update";
                let teamHierarchy = this.teamHierarchyForm.value;
                teamHierarchy.id = this.hierarchyId;

                this.teams.value.forEach(element => {
                    element.teamAction = element.teamAction == null ? 0 : element.teamAction;
                });
                teamHierarchy.teamHierarchyMappingList = this.teams.value;
                this.teamHierarchyService.postMethod(url, teamHierarchy).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        } else {
                            this.submittedTH = false;
                            this.isEdit = false;
                            this.ifTATMetricsShow = false;
                            this.teamHierarchyForm.reset();
                            this.conditionDataEnableDisble = [];
                            this.toastr.success(`${response.message}`, 'Success!');

                            dialogRef.close()

                            this.submittedTH = false;
                            if (this.searchkey) {
                                this.search();
                            } else {
                                this.getTeamHierarchy("");
                            }
                            this.listMapping();
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            } else {
                let data = [];
                const url = "/teamHierarchy/save";

                this.teams.value.forEach(element => {
                    element.teamAction = element.teamAction == null ? 0 : element.teamAction;
                });
                this.teamHierarchyForm.value.teamHierarchyMappingList = this.teams.value;
                this.teamHierarchyService.postMethod(url, this.teamHierarchyForm.value).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                        } else {
                            if (response) {
                                this.submittedTH = false;
                                this.ifTATMetricsShow = false;
                                this.teamHierarchyForm.reset();
                                this.toastr.success(`Successfully`, 'Success!');

                                dialogRef.close()
                                if (this.searchkey) {
                                    this.search();
                                } else {
                                    this.getTeamHierarchy("");
                                }
                                this.isEdit = false;
                                this.conditionDataEnableDisble = [];
                                this.listMapping();
                            } else {
                                (error: any) => {
                                    this.toastr.error(`${error.error.ERROR}`, 'Workflow with same Event is already exist!');
                                }


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

    editById(id: number, templateRef: any) {

        this.isEdit = true; // update mode
        this.hierarchyListShow = false;
        this.createView = true;
        this.hierarchyId = id;
        this.teams = this.fb.array([]);
        this.submitted = false;

        const url = "/teamHierarchy/" + id;
        this.teamHierarchyService.getMethod(url).subscribe(
            (response: any) => {
                // Patch the form with existing data
                this.teamHierarchyForm.patchValue(response.data);

                // Load event actions
                const data = { value: response.data.eventName };
                this.getTeamevent(data);

                // Populate the teams FormArray
                response.data.teamHierarchyMappingList.forEach(element => {
                    let teamcondition = "";
                    if (element.queryFieldList.length > 0) {
                        const queryDataLength = element.queryFieldList.length - 1;
                        element.queryFieldList.forEach((qElement, index) => {
                            if (index > 0) this.conditionDataEnableDisble.push(true);
                            if (queryDataLength !== index) {
                                teamcondition += ` ${qElement.queryField} ${qElement.queryOperator} ${qElement.queryValue} ${qElement.queryCondition}`;
                            } else {
                                teamcondition += ` ${qElement.queryField} ${qElement.queryOperator} ${qElement.queryValue}`;
                            }
                        });
                    }

                    this.teams.push(
                        this.fb.group({
                            hierarchyId: [response.data.id],
                            id: [element.id],
                            teamId: [element.teamId, Validators.required],
                            teamAction: [element.teamAction],
                            teamcondition: [teamcondition],
                            tat_id: [element.tat_id],
                            queryFieldList: [element.queryFieldList],
                            isAutoApprove: [element.isAutoApprove],
                            isAutoAssign: [element.isAutoAssign]
                        })
                    );
                });

                // Open the same dialog as create
                this.dialog.open(templateRef, {
                    width: "80%", // same as create
                    panelClass: "fullscreen-dialog"
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    deleteConfirmonSubReasonDialog(source: any): void {
        // if (id) return;
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: `Delete ${this.title}`,
                description: `Are you sure you want to delete "${source.hierarchyName}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteTeam(source?.id);
            }
        });
    }

    search() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName ? this.searchName.trim() : "";

        const url = `/teamHierarchy/search?page=${this.currentPage}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
        this.teamHierarchyService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.teamhierarchyData = response.dataList;
                this.totalRecords = response.totalRecords;
            },
            (error: any) => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.teamhierarchyData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            }
        );
    }

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    deleteConfirmon(team) {
        if (team) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this TAT?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteTeam(team);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }

        // if (team) {
        //     this.confirmationService.confirm({
        //         message: "Do you want to delete this Workflow?",
        //         header: "Delete Confirmation",
        //         icon: "pi pi-info-circle",
        //         accept: () => {
        //             this.deleteTeam(team);
        //         },
        //         reject: () => {

        //             (response: any) => {
        //                 this.toastr.info(`${response.responseMessage}`, 'You have rejected!');
        //             }
        //         }
        //     });
        // }
    }

    deleteTeam(team) {
        const url1 = "/teamHierarchy/" + team.id;
        this.teamHierarchyService.getMethod(url1).subscribe((response: any) => {
            const url = "/teamHierarchy/delete";
            this.teamHierarchyService.postMethod(url, response.data).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    } else {
                        if (this.currentPage != 1 && this.totalAreaListLength == 1) {
                            this.currentPage = this.currentPage - 1;
                        }
                        if (this.searchkey) {
                            this.search();
                        } else {
                            this.getTeamHierarchy("");
                        }
                        this.toastr.error(`${response.message}`, 'Delete Successfully!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        });
    }

    onAddAttribute() {
        this.teamSubmitted = true;
        this.teamConditionData = [];
        // let teamTotalData = 5
        // if(this.teams.length == teamTotalData){
        //   teamTotalData = 5 + teamTotalData
        //   this.currentPageTeams = this.currentPageTeams+1
        // }
        if (this.teamValue) {
            this.teams.push(
                this.createAttributeFormGroup(
                    this.teamValue,
                    this.teamHerAction,
                    this.coditionArray,
                    this.teamcondition,
                    this.tatMatricesID,
                    this.isAutoApprove,
                    this.isAutoAssign
                )
            );
        }
        this.isAutoApprove = false;
        this.isAutoAssign = false;
        this.teamsTable.renderRows()
    }

    createAttributeFormGroup(
        name,
        tActin,
        conditionData,
        condition,
        tatMatricesID,
        isAutoApprove,
        isAutoAssign
    ): UntypedFormGroup {
        // this.teamValue = "";
        this.teamValue = "";
        this.teamHerAction = "";
        this.teamcondition = "";
        this.tatMatricesID = "";
        this.coditionArray = [];
        this.teamSubmitted = false;

        return this.fb.group({
            teamId: [name, Validators.required],
            teamAction: [tActin],
            teamcondition: [condition],
            queryFieldList: [conditionData],
            tat_id: [tatMatricesID],
            isAutoAssign: [isAutoAssign],
            isAutoApprove: [isAutoApprove]
        });
    }

    deleteConfirmAttribute(i, data) {
        let teamConData = this.teams.value;
        let teamTotalData = teamConData.length;
        teamConData.forEach((element, i) => {
            if (element == data) {
                this.teams.removeAt(i);
                if (i % 5 == 0 && teamTotalData == i + 1) {
                    this.currentPageTeams = this.currentPageTeams - 1;
                }
            }
        });

        this.teamsTable.renderRows()

        // this.teams.removeAt(i);
    }

    assignParentTeam() {
        this.submitted = true;
        if (this.assignParentTeamForm.valid) {
            let team = {
                id: "",
                name: "",
                status: "",
                parentteamid: ""
            };
            team.id = this.assignparentTeamData.id;
            team.name = this.assignparentTeamData.name;
            team.status = this.assignparentTeamData.status;
            team.parentteamid = this.assignParentTeamForm.controls.parentteamid.value;
            this.teamsService.updateTeam(team).subscribe(
                (response: any) => {
                    this.assignparentTeamModalClose();
                    this.getTeamHierarchy("");
                    this.toastr.success(`${response.responseMessage}`, 'Successfully!');

                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
        }
    }

    assignparentTeamModalClose() {
        this.assignParentTeamForm.reset();
        this.submitted = false;
        $("#assignParentTeamModal").modal("hide");
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber;
        if (!this.searchkey) {
            //   this.getTeamHierarchy("");
        } else {
            this.search();
        }
    }

    // teamHierarchyModalOpen(data: any[]) {
    //     this.dialog.open(this.workflowManagement);
    //     this.teamHierarchyData = Array.isArray(data) ? data : [];
    //     this.teamHierarchyModal = true;
    // }

    teamHierarchyModalOpen(data) {
        this.dialog.open(this.workflowManagement);
        this.teamHierarchyModal = true;
        // this.teamHierarchyData = data.reverse();
        this.teamHierarchyData = data;
        // var newList = [];
        // var parentId = null;
        // for (var i = 0; i < this.teamListData.length; i++) {
        //   this.teamListData[i].label = this.teamListData[i].name;
        //   this.teamListData[i].expanded = true;
        //   this.teamListData[i].children = [];
        // }
        // for (var i = 0; i < this.teamListData.length; i++) {
        //   for (var j = 0; j < this.teamListData.length; j++) {
        //     if (parentId == this.teamListData[j].parentteamid) {
        //       newList.push(this.teamListData[j]);
        //       parentId = this.teamListData[j].id;
        //     }
        //   }
        // }

        // this.teamHierarchyData = this.list_to_tree(this.teamListData);
    }


    closeTeamHierarchy() {
        this.dialog.closeAll();
        this.closeWorkflow.emit();
        this.teamHierarchyModal = false;
    }

    // var newList = [];
    // var parentId = null;
    // for (var i = 0; i < this.teamListData.length; i++) {
    //   this.teamListData[i].label = this.teamListData[i].name;
    //   this.teamListData[i].expanded = true;
    //   this.teamListData[i].children = [];
    // }
    // for (var i = 0; i < this.teamListData.length; i++) {
    //   for (var j = 0; j < this.teamListData.length; j++) {
    //     if (parentId == this.teamListData[j].parentteamid) {
    //       newList.push(this.teamListData[j]);
    //       parentId = this.teamListData[j].id;
    //     }
    //   }
    // }

    // this.teamHierarchyData = this.list_to_tree(this.teamListData);

    clearSearch() {
        this.searchName = "";
        this.getTeamHierarchy("");
        this.getAllParenetTeam();
        this.listMapping();
        this.searchkey = ""; //new
        this.submitted = false; //nwe
        this.isEdit = false;
        //this.createAttributeFormGroup.reset();
        //this.FormGroup.controls.status.setValue("");
    }

    list_to_tree(list) {
        var map = {},
            node,
            roots = [],
            i;

        for (i = 0; i < list.length; i += 1) {
            map[list[i].id] = i; // initialize the map
            list[i].children = []; // initialize the children
        }

        for (i = 0; i < list.length; i += 1) {
            node = list[i];
            if (node.parentteamid != null) {
                // if you have dangling branches check that map[node.parentId] exists
                list[map[node.parentteamid]].children.push(node);
            } else {
                roots.push(node);
            }
        }
        return roots;
    }

    pageChangedTeams(pageNumber) {
        this.currentPageTeams = pageNumber;
    }

    openConditionModel() {
        this.dialog.open(this.teamConditionDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.teamConditionModal = true;
        this.teamQueryFieldListINDEX = "";
        if (this.teamcondition) {
            this.coditionArray.forEach((element, index) => {
                this.teamConditionArray.push(
                    this.fb.group({
                        id: element.id,
                        field: element.queryField,
                        operator: element.queryOperator,
                        value: element.queryValue,
                        condition: element.queryCondition,
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

        this.teamConditionModal = false;
        this.teamConditionArray.reset();
        this.teamConditionArray = this.fb.array([]);
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

    onkeyCondition(e) {
        if (e.value != "") {
            this.errormsgCondition = "";
        }
    }

    createteamConditionForm(): UntypedFormGroup {
        return this.fb.group({
            field: ["", Validators.required],
            operator: ["", Validators.required],
            value: ["", Validators.required],
            condition: [""]
        });
    }

    deleteTeamCondition(index: number): void {
        if (index >= 0 && index < this.teamConditionArray.length) {
            this.teamConditionArray.removeAt(index);

            // Optional pagination adjustment
            const remainingItems = this.teamConditionArray.length;
            if (index % 5 === 0 && remainingItems === index) {
                this.currentPageteamCondition = this.currentPageteamCondition - 1;
            }
        }

        setTimeout(() => {
            this.teamConditiontable?.renderRows();
        });
    }

    pageChangedTeamMappingList(page) {
        this.currentPageteamCondition = page;
    }
    onCancel(dialogRef) {
        let quaryFieldCondition = "";
        let quaryConditionArray = [];
        this.closeConditionModel();
        dialogRef.close()
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
                teamHirMappingId: this.teamMappingTeamID ? this.teamMappingTeamID : this.teamValue
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
            this.teams.value[this.teamQueryFieldListINDEX].queryFieldList = quaryConditionArray;
            this.teams.value[this.teamQueryFieldListINDEX].teamcondition = quaryFieldCondition;
            // hierarchyMappingList = this.teams.value
            // hierarchyMappingList.forEach((element , i) => {
            //   if(this.teamQueryFieldListINDEX == i){
            // this.teams.push(
            this.teams.patchValue(this.teams.value);
            this.closeConditionModel();
            this.teamcondition = "";
            this.coditionArray = [];
            this.teamMappingTeamID = "";
            // );
            //   }
            // })
            this.teamcondition = JSON.stringify(this.teamConditionArray.value); // optional, store as string
            this.closeConditionModel();
        }

        if (this.teamcondition || quaryFieldCondition) {
            this.closeConditionModel();
            this.conditionDataEnableDisble.push(true);
        }
        dialogRef.close()
    }

    ifTATMetricsShow = false;
    isWorkFlowAction = true;
    isAutoAssignShow = false;
    getTeamevent(event) {
        let eventD = [];
        eventD = this.eventData.filter(data => data.value == event.value);
        let value;
        if (eventD.length > 0) {
            value = eventD[0].value;
        }
        this.isAutoAssignShow = false;
        if (value == "CASE") {
            this.ifTATMetricsShow = false;
        }
        if (value == "CUSTOMER_INVENTORY_ASSIGN") {
            this.isWorkFlowAction = false;
        }
        if (value == "LEAD" || value == "CAF" || value == "TERMINATION") {
            this.ifTATMetricsShow = true;
        }
        if (value == "CAF") {
            this.isAutoAssignShow = true;
        }
        let actionUrl = `/commonList/generic/${value}_ACTION`;
        let conditionUrl = `/commonList/generic/${value}_CONDITION`;
        //TODO need to check and then use cache method for below two api calls
        this.getWorlFlowAction(actionUrl);
        this.getWorlFlowCatagory(conditionUrl);
        // let actionUrl = "";
        // // const url = "/commonList/hierarchy_event";
        // let catagoryUrl = "";
        // if (eventD.length > 0) {
        //   let selectevent = "";
        //   if (eventD[0].value == "Termination") {
        //     selectevent = "Termination";
        //     actionUrl = "/commonList/generic" + selectevent + "_ACTION";
        //     this.getWorlFlowAction(actionUrl);
        //   } else {
        //     selectevent = eventD[0].value;
        //     catagoryUrl = "/commonList/generic/" + selectevent + "_CONDITION";
        //     selectevent = eventD[0].value.toLowerCase();
        //     actionUrl = "/commonList/generic/" + selectevent + "_ACTION";
        //     this.getWorlFlowAction(actionUrl);
        //     this.getWorlFlowCatagory(catagoryUrl);
        //   }
        // }
    }

    getWorlFlowAction(url) {
        this.eventActionData = [];
        this.teamHierarchyService.getMethod(url).subscribe((response: any) => {
            this.eventActionData = response.dataList;
        });
    }

    getWorlFlowCatagory(url) {
        this.evenCatagorytData = [];
        this.teamHierarchyService.getMethod(url).subscribe((response: any) => {
            response.dataList.forEach(element => {
                switch (element.text) {
                    case "State":
                        element.text = this.stateTitle;
                        break;
                    case "Country":
                        element.text = this.countryTitle;
                        break;
                    case "City":
                        element.text = this.cityTitle;
                        break;
                    case "Area":
                        element.text = this.areaTitle;
                        break;
                    case "Pincode":
                        element.text = this.pincodeTitle;
                        break;
                    default:
                        break;
                }
            });
            this.evenCatagorytData = response.dataList;
        });

    }

    defultAddTeamCondition(index, teamId) {
        this.teamConditionModal = true;
        this.teamConditionArray.reset();
        this.teamConditionArray = this.fb.array([]);
        let hierarchyMappingList = [];
        this.conditionDataEnableDisble = [];
        this.teamMappingTeamID = teamId;
        this.teamQueryFieldListINDEX = index;
        if (this.teams.length > 0) {
            hierarchyMappingList = this.teams.value[index].queryFieldList;
        } this.dialog.open(this.teamConditionDialog, {
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
        }

        setTimeout(() => {
            this.teamConditiontable?.renderRows();
        });
    }

    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    getTeamhierarchyDetail(id, eventName) {
        let teameventId = {
            value: eventName
        };

        this.getTeamevent(teameventId);
        this.showTeamConditionData = [];

        const url = "/teamHierarchy/" + id;
        this.teamHierarchyService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.teamHirerarchyDetailData = response.data;
                    const eventName = "eventName";
                    const teamName = "teamName";
                    const teamActionName = "teamActionName";
                    const teamTATMatrics = "teamTATMatrics";

                    this.eventData.forEach(element => {
                        if (element.id == this.teamHirerarchyDetailData.eventName) {
                            this.teamHirerarchyDetailData[eventName] = element.text;
                            if (element.text == "Customer Ticket") {
                                this.ifTATMetricsShow = true;
                            } else {
                                this.ifTATMetricsShow = false;
                            }
                        }
                    });

                    this.teamListData.forEach(element => {
                        this.teamHirerarchyDetailData.teamHierarchyMappingList.forEach(e => {
                            if (element.id == e.teamId) {
                                e[teamName] = element.name;
                            }
                        });
                    });

                    this.eventActionData.forEach(element => {
                        this.teamHirerarchyDetailData.teamHierarchyMappingList.forEach(e => {
                            if (element.value == e.teamAction) {
                                e[teamActionName] = element.text;
                            }
                        });
                    });

                    this.commondropdownService.tatMatricsData.forEach(element => {
                        this.teamHirerarchyDetailData.teamHierarchyMappingList.forEach(e => {
                            if (element.id == e.tat_id) {
                                e[teamTATMatrics] = element.name;
                            }
                        });
                    });

                    response.data.teamHierarchyMappingList.forEach(element => {
                        let queryData = [];
                        let teamcondition = "";
                        if (element.queryFieldList.length > 0) {
                            let queryDataLength = element.queryFieldList.length - 1;
                            queryData = element.queryFieldList;
                            queryData.forEach((element, index) => {
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
                            this.showTeamConditionData.push({ condition: teamcondition });
                        } else {
                            this.showTeamConditionData.push({ condition: "" });
                        }
                    });
                    this.dialog.open(this.detailsDialog, {
                        width: '80%',
                        disableClose: true
                    });
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }


            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    canExit() {
        if (
            !this.teamHierarchyForm.dirty &&
            !this.assignParentTeamForm.dirty &&
            !this.teamConditionArray.dirty
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
    saveWorkflow(dialogRef: any) {
        dialogRef.close();
    }


    @ViewChild('teamsTable') teamsTable!: MatTable<any>;

    dispayviewTat = ['field', 'operator', 'value', 'condition', 'delete']
    isplayedColumns: string[] = [
        'approval',
        'team',
        'action',
        ...(this.ifTATMetricsShow ? ['tat'] : []),
        'condition',
        ...(this.isAutoAssignShow ? ['autoAssign', 'autoApprove'] : []),
        'delete'
    ];

}

//my code

// import { Component, OnInit } from "@angular/core";
// import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
// import { ConfirmationService, MessageService, TreeNode } from "primeng/api";
// import { NgxSpinnerService } from "ngx-spinner";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
// import { TeamsService } from "src/app/components/teams/teams.service";
// import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
// import { LoginService } from "src/app/service/login.service";
// import { AclClassConstants } from "src/app/constants/aclClassConstants";
// import { AclConstants } from "src/app/constants/aclOperationConstants";
// import { TeamHierarchyService } from "src/app/service/team-hierarchy.service";
// import { CommondropdownService } from "src/app/service/commondropdown.service";
// import { Observable, Observer } from "rxjs";
// import { WORKFLOWS } from "src/app/constants/aclConstants";
// import { MatTableModule } from '@angular/material/table';
// import { MatTableDataSource } from '@angular/material/table';
// import { MatPaginator } from '@angular/material/paginator';
// import { MatSort } from '@angular/material/sort';

// declare var $: any;

// @Component({

//     selector: "app-team-hierarchy",
//     templateUrl: "./team-hierarchy.component.html",
//     styleUrls: ["./team-hierarchy.component.css"],
//     standalone: false
// })
// export class TeamHierarchyComponent implements OnInit {

//   displayedColumns: string[] = ['id', 'WorkflowName', 'Action'];
//   dataSource = new MatTableDataSource<any>([]);

//   pageSizeOptions = [5, 10,  20, 50, 100];countryTitle = COUNTRY;
//   cityTitle = CITY;
//   stateTitle = STATE;
//   pincodeTitle = PINCODE;
//   areaTitle = AREA;
//   assignParentTeamForm: UntypedFormGroup;
//   teamListData: any;
//   parentTeamData: any;
//   teamhierarchyData: any;
//   submitted: boolean = false;
//   teamConditionModal: boolean = false;
//   // currentPageTeamListdata = 1;
//   // teamListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   // teamListdatatotalRecords: String;
//   AclClassConstants;
//   AclConstants;
//   public loginService: LoginService;
//   currentPage: number = 1;
//   itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
//   totalRecords: number;
//   parentTeamListData: any;
//   assignparentTeamData: any;

//   ItemPerPageteamCondition = RadiusConstants.ITEMS_PER_PAGE;
//   currentPageteamCondition = 1;
//   totalRecordsteamCondition: number;

//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   showItemPerPage = 0;
//   searchkey: string;
//   totalAreaListLength = 0;
//   pageITEM = RadiusConstants.ITEMS_PER_PAGE;
//   teamHierarchyData: TreeNode[] = [];

//   totalRecordsTeams = 0;
//   ItemPerPageTeams = RadiusConstants.ITEMS_PER_PAGE;
//   currentPageTeams: any = 1;

//   createView: boolean = false;
//   hierarchyListShow: boolean = true;
//   hierarchyDeatilsShow: boolean = false;
//   searchView: boolean = true;

//   isEdit: boolean = false;
//   teamHierarchyForm: UntypedFormGroup;
//   teams: UntypedFormArray;
//   status = [];
//   submittedTH: boolean = false;
//   teamSubmitted: boolean = false;
//   searchName: string;
//   teamValue: any = "";
//   teamcondition: any = "";
//   searchData: any;
//   teamHerAction = "";
//   teamHirerarchyDetailData: any = {
//     teamHierarchyMappingList: [],
//   };
//   operatorList = [
//     { label: "Equal to", value: "==" },
//     { label: "Less than or equal to", value: "<=" },
//     { label: "Greater than or equal to", value: ">=" },
//     { label: "Less than ", value: "<" },
//     { label: "Greater than", value: ">" },
//     { label: "Not equal to", value: "!=" },
//   ];
//   AndOrDropdown = [
//     { label: "AND", value: "and" },
//     { label: "OR", value: "or" },
//   ];

//   teamConditionArray: UntypedFormArray;
//   teamConditionData: any = [];
//   coditionArray: any[];
//   evenCatagorytData: any = [];
//   eventActionData: any = [];
//   teamQueryFieldListINDEX: string;
//   teamMappingTeamID: any = "";
//   errormsgCondition: string;
//   showTeamConditionData: any = [];
//   eventData: any;
//   hierarchyId: number;
//   tatMatricesID = "";
//   conditionDataEnableDisble = [];
//   createAccess: boolean = false;
//   deleteAccess: boolean = false;
//   editAccess: boolean = false;
// isAutoApprove: any;
// isAutoAssign: any;
// teamHierarchyModal : boolean = false;
//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private teamsService: TeamsService,
//     private radiusUtility: RadiusUtility,
//     private teamHierarchyService: TeamHierarchyService,
//     public commondropdownService: CommondropdownService,
//     loginService: LoginService
//   ) {
//     this.createAccess = loginService.hasPermission(WORKFLOWS.WORKFLOW_CREATE);
//     this.deleteAccess = loginService.hasPermission(WORKFLOWS.WORKFLOW_DELETE);
//     this.editAccess = loginService.hasPermission(WORKFLOWS.WORKFLOW_EDIT);
//     this.loginService = loginService;
//     this.isEdit = !this.createAccess && this.editAccess ? true : false;
//     this.getTeamHierarchy("");
//     this.getAllParenetTeam();
//     this.commondropdownService.getActiveMatrixList();
//     this.getTeamData();
//     this.getAllEvent();
//   }

//   ngOnInit(): void {
//     this.teams = this.fb.array([]);
//     this.teamHierarchyForm = this.fb.group({
//       hierarchyName: ["", Validators.required],
//       eventName: ["", Validators.required],
//       id: [],
//       teamHierarchyMappingList: [...this.teams.value],
//       isAutoAssign:Boolean,
//       isAutoApprove:Boolean,

//     });
//     this.assignParentTeamForm = this.fb.group({
//       parentteamid: ["", Validators.required],
//     });

//     this.teamConditionArray = this.fb.array([]);
//     this.searchData = {
//       filter: [
//         {
//           filterDataType: "",
//           filterValue: "",
//           filterColumn: "hierarchyname",
//           filterOperator: "equalto",
//           filterCondition: "and",
//         },
//       ],
//     };
//   }

//   createMapping() {
//     this.searchView = false;
//     this.createView = true;
//     this.hierarchyListShow = false;
//     this.submitted = false;
//     this.isEdit = false;
//     this.hierarchyDeatilsShow = false;
//     this.teamHierarchyForm.reset();
//     this.teams = this.fb.array([]);
//     this.teamValue = "";
//     this.teamHerAction = "";
//     this.teamcondition = "";
//     this.coditionArray = [];
//     this.teamConditionArray = this.fb.array([]);
//     this.getTeamevent("");
//     this.conditionDataEnableDisble = [];
//   }

//   listMapping() {
//     this.searchView = true;
//     this.createView = false;
//     this.hierarchyListShow = true;
//     this.hierarchyDeatilsShow = false;
//     this.itemsPerPage = 5;
//     this.currentPage = 1;
//     this.searchName = "";
//     this.searchkey = "";
//     this.getTeamHierarchy("");
//   }

//   detailMapping(id, eventName) {
//     this.searchView = false;
//     this.createView = false;
//     this.hierarchyListShow = false;
//     this.hierarchyDeatilsShow = true;
//     this.getTeamhierarchyDetail(id, eventName);
//   }

//   TotalItemPerPage(event) {
//     this.showItemPerPage = Number(event.value);
//     this.itemsPerPage = Number(event.value);
//     if (this.currentPage > 1) {
//       this.currentPage = 1;
//     }
//     if (!this.searchkey) {
//       this.getTeamHierarchy(this.showItemPerPage);
//     } else {
//       this.search();
//     }
//   }

//   getAllEvent() {
//     const url = "/commonList/hierarchy_event";
//     this.commondropdownService.getMethodWithCache(url).subscribe(
//       (response: any) => {
//         this.eventData = response.dataList;
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   getAllParenetTeam() {
//     this.parentTeamData = [];
//     this.teamsService.getAllParentTeam().subscribe(
//       (response: any) => {
//         this.parentTeamData = response.dataList;
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }
//   getTeamHierarchy(): void {
//     // Replace with your service call
//     const mockData = [
//       { id: 1, hierarchyName: 'Workflow A', eventName: 'Create' },
//       { id: 2, hierarchyName: 'Workflow B', eventName: 'Update' },
//       { id: 3, hierarchyName: 'Workflow C', eventName: 'Delete' }
//     ];

//     this.dataSource.data = mockData;
//     this.totalRecords = mockData.length;
//   }

// //   getTeamHierarchy(list) {
// //     let size;

// //     let page_list = this.currentPage;
// //     if (list) {
// //       size = list;
// //       this.itemsPerPage = list;
// //     } else {
// //       // if (this.showItemPerPage == 0) {
// //       //   this.itemsPerPage = this.pageITEM
// //       // } else {
// //       //   this.itemsPerPage = this.showItemPerPage
// //       // }
// //       size = this.itemsPerPage;
// //     }
// //     let teamdata = {
// //       page: page_list,
// //       pageSize: size,
// //     };
// //     this.teamhierarchyData = [];
// //     const url = "/teamHierarchy/hierarchy/all";
// //     this.teamHierarchyService.getMethod(url).subscribe(
// //       (response: any) => {
// //         this.teamhierarchyData = response.dataList;
// //         this.totalRecords = response.totalRecords;
// //         // let eventData = [];

// //         // this.teamhierarchyData.forEach((element) => {
// //         //   eventData.push(element.eventName);
// //         // });

// //         // this.eventData = this.eventData.filter(function (el) {
// //         //   return eventData.indexOf(el.id) < 0;
// //         // });
// //       },
// //       (error: any) => {
// //         this.messageService.add({
// //           severity: "error",
// //           summary: "Error",
// //           detail: error.error.ERROR,
// //           icon: "far fa-times-circle",
// //         });
// //       }
// //     );
// //   }

//   getTeamData() {
//     this.teamsService.getAllParentTeam().subscribe(
//       (response: any) => {
//         this.teamListData = response.dataList;
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   assignParentTeamModalOpen(data) {
//     console.log(data);
//     $("#assignParentTeamModal").modal("show");
//     this.assignparentTeamData = data;
//     this.parentTeamListData = this.parentTeamData.filter(team => team.id != data.id);
//   }

//   checkAssignparentTeam() {
//     this.submitted = true;
//     if (this.assignParentTeamForm.valid) {
//       const url =
//         "/teams/checkTeamIsAlreadyParentTeam/" +
//         this.assignParentTeamForm.controls.parentteamid.value;
//       this.teamsService.getMethod(url).subscribe(
//         (response: any) => {
//           if (response.data == false) {
//             this.assignParentTeam();
//             //console.log(true);

//             this.submitted = false;
//           } else {
//             this.submitted = false;

//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail:
//                 "Selected parent team is already parent team of other team. Please select other team",
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.errorMessage,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     }
//   }

//   addEditTeamHierarchy() {
//     this.submittedTH = true;
//     if (this.teamHierarchyForm.valid) {
//       if (this.isEdit) {
//         const url = "/teamHierarchy/update";
//         let teamHierarchy = this.teamHierarchyForm.value;
//         teamHierarchy.id = this.hierarchyId;

//         this.teams.value.forEach(element => {
//           element.teamAction = element.teamAction == null ? 0 : element.teamAction;
//         });
//         teamHierarchy.teamHierarchyMappingList = this.teams.value;
//         this.teamHierarchyService.postMethod(url, teamHierarchy).subscribe(
//           (response: any) => {
//             if (response.responseCode == 406) {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: response.responseMessage,
//                 icon: "far fa-times-circle",
//               });
//             } else {
//               this.submittedTH = false;
//               this.isEdit = false;
//               this.ifTATMetricsShow = false;
//               this.teamHierarchyForm.reset();
//               this.conditionDataEnableDisble = [];
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.message,
//                 icon: "far fa-check-circle",
//               });
//               this.submittedTH = false;
//               if (this.searchkey) {
//                 this.search();
//               } else {
//                 this.getTeamHierarchy("");
//               }
//               this.listMapping();
//             }
//           },
//           (error: any) => {
//             // console.log(error, "error")

//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.ERROR,
//               icon: "far fa-times-circle",
//             });
//           }
//         );
//       } else {
//         let data = [];
//         const url = "/teamHierarchy/save";

//         this.teams.value.forEach(element => {
//           element.teamAction = element.teamAction == null ? 0 : element.teamAction;
//         });
//         this.teamHierarchyForm.value.teamHierarchyMappingList = this.teams.value;
//         this.teamHierarchyService.postMethod(url, this.teamHierarchyForm.value).subscribe(
//           (response: any) => {
//             if (response.responseCode == 406) {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: response.responseMessage,
//                 icon: "far fa-check-circle",
//               });
//             } else {
//               if (response) {
//                 this.submittedTH = false;
//                 this.ifTATMetricsShow = false;
//                 this.teamHierarchyForm.reset();
//                 this.messageService.add({
//                   severity: "success",
//                   summary: "Successfully",
//                   detail: response.message,
//                   icon: "far fa-check-circle",
//                 });
//                 if (this.searchkey) {
//                   this.search();
//                 } else {
//                   this.getTeamHierarchy("");
//                 }
//                 this.isEdit = false;
//                 this.conditionDataEnableDisble = [];
//                 this.listMapping();
//               } else {
//                 this.messageService.add({
//                   severity: "error",
//                   summary: "Error",
//                   detail: "Workflow with same Event is already exist!",
//                   icon: "far fa-times-circle",
//                 });
//               }
//             }
//           },
//           (error: any) => {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.ERROR,
//               icon: "far fa-times-circle",
//             });
//           }
//         );
//       }
//     }
//   }

//   editById(id: number) {
//       void {
//     console.log('Edit clicked for ID:', id);
//   }

//     this.isEdit = true;
//     this.hierarchyListShow = false;
//     this.createView = true;
//     this.hierarchyId = id;
//     this.teams = this.fb.array([]);
//     const url = "/teamHierarchy/" + id;
//     this.teamHierarchyService.getMethod(url).subscribe(
//       (response: any) => {
//         this.teamHierarchyForm.patchValue(response.data);
//         let data = {
//           value: response.data.eventName,
//         };
//         this.getTeamevent(data);
//         response.data.teamHierarchyMappingList.forEach(element => {
//             console.log(element);
//           let queryData = [];
//           let teamcondition = "";
//           if (element.queryFieldList.length > 0) {
//             let queryDataLength = element.queryFieldList.length - 1;
//             queryData = element.queryFieldList;
//             queryData.forEach((element, index) => {
//               if (index > 0) {
//                 this.conditionDataEnableDisble.push(true);
//               }
//               if (queryDataLength != index) {
//                 teamcondition =
//                   teamcondition +
//                   " " +
//                   element.queryField +
//                   " " +
//                   element.queryOperator +
//                   " " +
//                   element.queryValue +
//                   " " +
//                   element.queryCondition;
//               } else {
//                 teamcondition =
//                   teamcondition +
//                   " " +
//                   element.queryField +
//                   " " +
//                   element.queryOperator +
//                   element.queryValue;
//               }
//             });
//           }

//           this.teams.push(
//             this.fb.group({
//               hierarchyId: [response.data.id],
//               id: [element.id],
//               teamId: [element.teamId, Validators.required],
//               teamAction: [element.teamAction],
//               teamcondition: [teamcondition],
//               tat_id: [element.tat_id],
//               queryFieldList: [element.queryFieldList],
//               isAutoApprove:[element.isAutoApprove],
//               isAutoAssign:[element.isAutoAssign],
//             })
//           );
//         });
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   search() {
//     if (!this.searchkey || this.searchkey !== this.searchName) {
//       this.currentPage = 1;
//     }
//     this.searchkey = this.searchName;
//     if (this.showItemPerPage) {
//       this.itemsPerPage = this.showItemPerPage;
//     }
//     this.searchData.filter[0].filterValue = this.searchName ? this.searchName.trim() : "";

//     const url = `/teamHierarchy/search?page=${this.currentPage}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
//     this.teamHierarchyService.postMethod(url, this.searchData).subscribe(
//       (response: any) => {
//         this.teamhierarchyData = response.dataList;
//         this.totalRecords = response.totalRecords;
//       },
//       (error: any) => {
//         this.totalRecords = 0;
//         if (error.error.status == 404) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: error.error.msg,
//             icon: "far fa-times-circle",
//           });
//           this.teamhierarchyData = [];
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       }
//     );
//   }

//   deleteConfirmon(team) {
//     if (team) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this Workflow?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.deleteTeam(team);
//         },
//         reject: () => {
//           this.messageService.add({
//             severity: "info",
//             summary: "Rejected",
//             detail: "You have rejected",
//           });
//         },
//       });
//     }
//   }

//   deleteTeam(team) {
//     const url1 = "/teamHierarchy/" + team.id;
//     this.teamHierarchyService.getMethod(url1).subscribe((response: any) => {
//       const url = "/teamHierarchy/delete";
//       this.teamHierarchyService.postMethod(url, response.data).subscribe(
//         (response: any) => {
//           if (response.responseCode == 406) {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             if (this.currentPage != 1 && this.totalAreaListLength == 1) {
//               this.currentPage = this.currentPage - 1;
//             }
//             if (this.searchkey) {
//               this.search();
//             } else {
//               this.getTeamHierarchy("");
//             }

//             this.messageService.add({
//               severity: "success",
//               summary: "Delete Successfully",
//               detail: response.message,
//               icon: "far fa-check-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     });
//   }

//   onAddAttribute() {
//     this.teamSubmitted = true;
//     this.teamConditionData = [];
//     // let teamTotalData = 5
//     // if(this.teams.length == teamTotalData){
//     //   teamTotalData = 5 + teamTotalData
//     //   this.currentPageTeams = this.currentPageTeams+1
//     // }
//     if (this.teamValue) {
//       this.teams.push(
//         this.createAttributeFormGroup(
//           this.teamValue,
//           this.teamHerAction,
//           this.coditionArray,
//           this.teamcondition,
//           this.tatMatricesID,
//           this.isAutoApprove,
//           this.isAutoAssign

//         )
//       );
//     }
//     this.isAutoApprove=false;
//     this.isAutoAssign=false;

//   }

//   createAttributeFormGroup(name, tActin, conditionData, condition, tatMatricesID,isAutoApprove,isAutoAssign): UntypedFormGroup {
//     // this.teamValue = "";
//     this.teamValue = "";
//     this.teamHerAction = "";
//     this.teamcondition = "";
//     this.tatMatricesID = "";
//     this.coditionArray = [];
//     this.teamSubmitted = false;

//     return this.fb.group({
//       teamId: [name, Validators.required],
//       teamAction: [tActin],
//       teamcondition: [condition],
//       queryFieldList: [conditionData],
//       tat_id: [tatMatricesID],
//       isAutoAssign:[isAutoAssign],
//       isAutoApprove:[isAutoApprove]

//     });
//   }

//   deleteConfirmAttribute(i, data) {
//     let teamConData = this.teams.value;
//     let teamTotalData = teamConData.length;
//     teamConData.forEach((element, i) => {
//       if (element == data) {
//         this.teams.removeAt(i);
//         if (i % 5 == 0 && teamTotalData == i + 1) {
//           this.currentPageTeams = this.currentPageTeams - 1;
//         }
//       }
//     });

//     // this.teams.removeAt(i);
//   }

//   assignParentTeam() {
//     this.submitted = true;
//     if (this.assignParentTeamForm.valid) {
//       let team = {
//         id: "",
//         name: "",
//         status: "",
//         parentteamid: "",
//       };
//       team.id = this.assignparentTeamData.id;
//       team.name = this.assignparentTeamData.name;
//       team.status = this.assignparentTeamData.status;
//       team.parentteamid = this.assignParentTeamForm.controls.parentteamid.value;
//       console.log(team);
//       this.teamsService.updateTeam(team).subscribe(
//         (response: any) => {
//           this.assignparentTeamModalClose();
//           this.getTeamHierarchy("");
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.responseMessage,
//             icon: "far fa-check-circle",
//           });
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.errorMessage,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     }
//   }

//   assignparentTeamModalClose() {
//     this.assignParentTeamForm.reset();
//     this.submitted = false;
//     $("#assignParentTeamModal").modal("hide");
//   }

//   pageChanged(pageNumber) {
//     this.currentPage = pageNumber;
//     if (!this.searchkey) {
//       //   this.getTeamHierarchy("");
//     } else {
//       this.search();
//     }
//   }

//   teamHierarchyModalOpen(data) {
//     console.log("data", data);
//     this.teamHierarchyModal = true;
//     // this.teamHierarchyData = data.reverse();
//     this.teamHierarchyData = data;
//     // var newList = [];
//     // var parentId = null;
//     // for (var i = 0; i < this.teamListData.length; i++) {
//     //   this.teamListData[i].label = this.teamListData[i].name;
//     //   this.teamListData[i].expanded = true;
//     //   this.teamListData[i].children = [];
//     // }
//     // for (var i = 0; i < this.teamListData.length; i++) {
//     //   for (var j = 0; j < this.teamListData.length; j++) {
//     //     if (parentId == this.teamListData[j].parentteamid) {
//     //       newList.push(this.teamListData[j]);
//     //       parentId = this.teamListData[j].id;
//     //     }
//     //   }
//     // }

//     // this.teamHierarchyData = this.list_to_tree(this.teamListData);
//   }
//  clearSearch()
//    {
//     this.searchName = "";
//     this.getTeamHierarchy("");
//     this.getAllParenetTeam();
//     this.listMapping();
//   }

//   list_to_tree(list) {
//     var map = {},
//       node,
//       roots = [],
//       i;

//     for (i = 0; i < list.length; i += 1) {
//       map[list[i].id] = i; // initialize the map
//       list[i].children = []; // initialize the children
//     }

//     for (i = 0; i < list.length; i += 1) {
//       node = list[i];
//       if (node.parentteamid != null) {
//         // if you have dangling branches check that map[node.parentId] exists
//         list[map[node.parentteamid]].children.push(node);
//       } else {
//         roots.push(node);
//       }
//     }
//     return roots;
//   }

//   pageChangedTeams(pageNumber) {
//     this.currentPageTeams = pageNumber;
//   }

//   openConditionModel() {
//     this.teamConditionModal = true;
//     this.teamQueryFieldListINDEX = "";
//     if (this.teamcondition) {
//       this.coditionArray.forEach((element, index) => {
//         this.teamConditionArray.push(
//           this.fb.group({
//             id: element.id,
//             field: element.queryField,
//             operator: element.queryOperator,
//             value: element.queryValue,
//             condition: element.queryCondition,
//           })
//         );
//         if (index > 0) {
//           this.conditionDataEnableDisble.push(true);
//         }
//       });
//     } else {
//       this.onConditionAddAttribute();
//     }
//   }

//   closeConditionModel() {
//     this.teamConditionModal = false;
//     this.teamConditionArray.reset();
//     this.teamConditionArray = this.fb.array([]);
//   }

//   onConditionAddAttribute() {
//     if (this.teamConditionArray.value.length >= 1) {
//       let index = this.teamConditionArray.value.length - 1;
//       if (this.teamConditionArray.value[index].condition == "" && this.teamConditionArray.valid) {
//         this.errormsgCondition = "Please add condition first";
//         this.conditionDataEnableDisble.push(true);
//         this.teamConditionArray.push(this.createteamConditionForm());
//       } else if (!this.teamConditionArray.valid) {
//         this.errormsgCondition = "";
//       } else {
//         this.errormsgCondition = "";
//         this.conditionDataEnableDisble.push(true);
//         this.teamConditionArray.push(this.createteamConditionForm());
//       }
//     } else {
//       this.teamConditionArray.push(this.createteamConditionForm());
//     }
//   }

//   onkeyCondition(e) {
//     if (e.value != "") {
//       this.errormsgCondition = "";
//     }
//   }

//   createteamConditionForm(): UntypedFormGroup {
//     return this.fb.group({
//       field: ["", Validators.required],
//       operator: ["", Validators.required],
//       value: ["", Validators.required],
//       condition: [""],
//     });
//   }

//   deleteTeamCondition(index, data) {
//     let teamConData = this.teamConditionArray.value;
//     let teamTotalData = teamConData.length;

//     teamConData.forEach((element, i) => {
//       if (element == data) {
//         this.teamConditionArray.removeAt(i);
//         if (i % 5 == 0 && teamTotalData == i + 1) {
//           this.currentPageteamCondition = this.currentPageteamCondition - 1;
//         }
//       }
//     });
//   }

//   pageChangedTeamMappingList(page) {
//     this.currentPageteamCondition = page;
//   }

//   saveConditionData() {
//     this.teamcondition = "";
//     this.coditionArray = [];
//     let quaryFieldCondition = "";
//     let quaryConditionArray = [];
//     let detailsFormValue = this.teamConditionArray.value;
//     this.teamConditionData = detailsFormValue;
//     let detailsLength = detailsFormValue.length - 1;
//     detailsFormValue.forEach((element, index) => {
//       this.coditionArray.push({
//         id: element.id ? element.id : null,
//         queryField: element.field,
//         queryOperator: element.operator,
//         queryValue: element.value,
//         queryCondition: element.condition,
//         teamHirMappingId: this.teamMappingTeamID ? this.teamMappingTeamID : this.teamValue,
//       });

//       if (detailsLength != index) {
//         this.teamcondition =
//           this.teamcondition +
//           " " +
//           element.field +
//           " " +
//           element.operator +
//           " " +
//           element.value +
//           " " +
//           element.condition;
//       } else {
//         this.teamcondition =
//           this.teamcondition + " " + element.field + " " + element.operator + element.value;
//       }
//     });
//     quaryConditionArray = this.coditionArray;
//     quaryFieldCondition = this.teamcondition;
//     if (this.teamQueryFieldListINDEX !== "") {
//       this.teams.value[this.teamQueryFieldListINDEX].queryFieldList = quaryConditionArray;
//       this.teams.value[this.teamQueryFieldListINDEX].teamcondition = quaryFieldCondition;
//       // hierarchyMappingList = this.teams.value
//       // hierarchyMappingList.forEach((element , i) => {
//       //   if(this.teamQueryFieldListINDEX == i){
//       // this.teams.push(
//       this.teams.patchValue(this.teams.value);
//       this.closeConditionModel();
//       this.teamcondition = "";
//       this.coditionArray = [];
//       this.teamMappingTeamID = "";
//       // );
//       //   }
//       // })
//     }

//     if (this.teamcondition || quaryFieldCondition) {
//       this.closeConditionModel();
//       this.conditionDataEnableDisble.push(true);
//     }
//   }

//   ifTATMetricsShow = false;
//   isWorkFlowAction = true;
//   isAutoAssignShow=false;
//   getTeamevent(event) {
//     let eventD = [];
//     eventD = this.eventData.filter(data => data.value == event.value);
//     console.log(eventD);
//     let value;
//     if (eventD.length > 0) {
//       value = eventD[0].value;
//     }
//      this.isAutoAssignShow=false;
//     if (value == "CASE") {
//       this.ifTATMetricsShow = false;
//     }
//     if (value == "CUSTOMER_INVENTORY_ASSIGN") {
//       this.isWorkFlowAction = false;
//     }
//     if (value == "LEAD" || value == "CAF" || value == "TERMINATION") {
//       this.ifTATMetricsShow = true;
//     }
//      if ( value == "CAF" ) {
//       this.isAutoAssignShow = true;
//     }
//     let actionUrl = `/commonList/generic/${value}_ACTION`;
//     let conditionUrl = `/commonList/generic/${value}_CONDITION`;
//     //TODO need to check and then use cache method for below two api calls
//     this.getWorlFlowAction(actionUrl);
//     this.getWorlFlowCatagory(conditionUrl);
//     // let actionUrl = "";
//     // // const url = "/commonList/hierarchy_event";
//     // let catagoryUrl = "";
//     // if (eventD.length > 0) {
//     //   let selectevent = "";
//     //   if (eventD[0].value == "Termination") {
//     //     selectevent = "Termination";
//     //     actionUrl = "/commonList/generic" + selectevent + "_ACTION";
//     //     this.getWorlFlowAction(actionUrl);
//     //   } else {
//     //     selectevent = eventD[0].value;
//     //     catagoryUrl = "/commonList/generic/" + selectevent + "_CONDITION";
//     //     selectevent = eventD[0].value.toLowerCase();
//     //     actionUrl = "/commonList/generic/" + selectevent + "_ACTION";
//     //     this.getWorlFlowAction(actionUrl);
//     //     this.getWorlFlowCatagory(catagoryUrl);
//     //   }
//     // }
//   }

//   getWorlFlowAction(url) {
//     this.eventActionData = [];
//     this.teamHierarchyService.getMethod(url).subscribe((response: any) => {
//       this.eventActionData = response.dataList;
//     });
//   }

//   getWorlFlowCatagory(url) {
//     this.evenCatagorytData = [];
//     this.teamHierarchyService.getMethod(url).subscribe((response: any) => {
//       response.dataList.forEach(element => {
//         switch (element.text) {
//           case "State":
//             element.text = this.stateTitle;
//             break;
//           case "Country":
//             element.text = this.countryTitle;
//             break;
//           case "City":
//             element.text = this.cityTitle;
//             break;
//           case "Area":
//             element.text = this.areaTitle;
//             break;
//           case "Pincode":
//             element.text = this.pincodeTitle;
//             break;
//           default:
//             break;
//         }
//       });
//       this.evenCatagorytData = response.dataList;
//       console.log(response.dataList);
//     });

//     console.log(this.evenCatagorytData);
//   }

//   defultAddTeamCondition(index, teamId) {
//     this.teamConditionModal = true;
//     this.teamConditionArray.reset();
//     this.teamConditionArray = this.fb.array([]);
//     let hierarchyMappingList = [];
//     this.conditionDataEnableDisble = [];
//     this.teamMappingTeamID = teamId;
//     this.teamQueryFieldListINDEX = index;
//     if (this.teams.length > 0) {
//       hierarchyMappingList = this.teams.value[index].queryFieldList;
//     }
//     if (hierarchyMappingList.length > 0) {
//       hierarchyMappingList.forEach((element, index) => {
//         if (index > 0) {
//           this.conditionDataEnableDisble.push(true);
//         }
//         this.teamConditionArray.push(
//           this.fb.group({
//             id: element.id,
//             field: element.queryField,
//             operator: element.queryOperator,
//             value: element.queryValue,
//             condition: element.queryCondition,
//           })
//         );
//       });
//     }
//   }

//   getTeamhierarchyDetail(id, eventName) {
//     let teameventId = {
//       value: eventName,
//     };

//     this.getTeamevent(teameventId);
//     this.showTeamConditionData = [];

//     const url = "/teamHierarchy/" + id;
//     this.teamHierarchyService.getMethod(url).subscribe(
//       (response: any) => {
//         if (response.responseCode == 200) {
//           this.teamHirerarchyDetailData = response.data;
//           const eventName = "eventName";
//           const teamName = "teamName";
//           const teamActionName = "teamActionName";
//           const teamTATMatrics = "teamTATMatrics";

//           this.eventData.forEach(element => {
//             if (element.id == this.teamHirerarchyDetailData.eventName) {
//               this.teamHirerarchyDetailData[eventName] = element.text;
//               if (element.text == "Customer Ticket") {
//                 this.ifTATMetricsShow = true;
//               } else {
//                 this.ifTATMetricsShow = false;
//               }
//             }
//           });

//           this.teamListData.forEach(element => {
//             this.teamHirerarchyDetailData.teamHierarchyMappingList.forEach(e => {
//               if (element.id == e.teamId) {
//                 e[teamName] = element.name;
//               }
//             });
//           });

//           this.eventActionData.forEach(element => {
//             this.teamHirerarchyDetailData.teamHierarchyMappingList.forEach(e => {
//               if (element.value == e.teamAction) {
//                 e[teamActionName] = element.text;
//               }
//             });
//           });

//           this.commondropdownService.tatMatricsData.forEach(element => {
//             this.teamHirerarchyDetailData.teamHierarchyMappingList.forEach(e => {
//               if (element.id == e.tat_id) {
//                 e[teamTATMatrics] = element.name;
//               }
//             });
//           });

//           response.data.teamHierarchyMappingList.forEach(element => {
//             let queryData = [];
//             let teamcondition = "";
//             if (element.queryFieldList.length > 0) {
//               let queryDataLength = element.queryFieldList.length - 1;
//               queryData = element.queryFieldList;
//               queryData.forEach((element, index) => {
//                 if (queryDataLength != index) {
//                   teamcondition =
//                     teamcondition +
//                     " " +
//                     element.queryField +
//                     " " +
//                     element.queryOperator +
//                     " " +
//                     element.queryValue +
//                     " " +
//                     element.queryCondition;
//                 } else {
//                   teamcondition =
//                     teamcondition +
//                     " " +
//                     element.queryField +
//                     " " +
//                     element.queryOperator +
//                     element.queryValue;
//                 }
//               });
//               this.showTeamConditionData.push({ condition: teamcondition });
//             } else {
//               this.showTeamConditionData.push({ condition: "" });
//             }
//           });

//           console.log("this.teamHirerarchyDetailData", this.teamHirerarchyDetailData);
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle",
//           });
//         }
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   canExit() {
//     if (
//       !this.teamHierarchyForm.dirty &&
//       !this.assignParentTeamForm.dirty &&
//       !this.teamConditionArray.dirty
//     )
//       return true;
//     {
//       return Observable.create((observer: Observer<boolean>) => {
//         this.confirmationService.confirm({
//           header: "Alert",
//           message: "The filled data will be lost. Do you want to continue? (Yes/No)",
//           icon: "pi pi-info-circle",
//           accept: () => {
//             observer.next(true);
//             observer.complete();
//           },
//           reject: () => {
//             observer.next(false);
//             observer.complete();
//           },
//         });
//         return false;
//       });
//     }
//   }
// }
