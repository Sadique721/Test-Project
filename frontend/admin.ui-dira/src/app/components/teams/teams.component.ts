import { Component, OnInit, ViewChild, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, UntypedFormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TeamsService } from "./teams.service";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { TreeNode } from "primeng/api";
import { WORKFLOWS } from "src/app/constants/aclConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from "ngx-toastr";

declare var $: any;

@Component({
    selector: "app-teams",
    templateUrl: "./teams.component.html",
    styleUrls: ["./teams.component.css"],
    standalone: false
})
export class TeamsComponent implements OnInit {
    @ViewChild('teamDialogTemplate') teamDialogTemplate!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    @ViewChild('teamHierarchyDialog') teamHierarchyDialog: TemplateRef<any>;
    hierarchyDialogRef: MatDialogRef<any>;

    teamHierarchyData: TreeNode[];
    selectedNode: TreeNode;

    teamFormGroup: UntypedFormGroup;
    teamListData: any;
    submitted: boolean = false;
    currentPageTeamListdata = 1;
    teamListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    teamListdatatotalRecords: any;
    searchData: any;
    teamId: any;
    team: any;
    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any = 5;
    searchkey: string;
    totalAreaListLength = 0;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    listView: boolean = false;
    teamtypedata: any;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    statusList: any[] = [
        { value_field: "active", display_field: "Active" },
        { value_field: "inactive", display_field: "InActive" },
    ];
    searchTeamName: any = "";
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    hirearchyAccess: boolean = false;
    pageSize;
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private teamsService: TeamsService,
        private radiusUtility: RadiusUtility,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(WORKFLOWS.TEAMS_CREATE);
        this.deleteAccess = loginService.hasPermission(WORKFLOWS.TEAMS_DELETE);
        this.editAccess = loginService.hasPermission(WORKFLOWS.TEAMS_EDIT);
        // this.hirearchyAccess = loginService.hasPermission(WORKFLOWS.TEAMS_HIERARCHY);
        this.loginService = loginService;
        this.editMode = !this.createAccess && this.editAccess ? true : false;
    }

    editMode: boolean = false;
    isTeamList: boolean = true;
    isTeamCreateOrEdit: boolean = false;
    openTeamListMenu() {
        this.isTeamCreateOrEdit = false;
        this.isTeamList = true;
        this.currentPage = 1;
        this.itemsPerPage = 5;
        this.getTeamList("");
    }

    openTeamCreateMenu() {
        this.editMode = false;
        this.isTeamList = false;
        this.isTeamCreateOrEdit = true;
        this.teamFormGroup.reset();
        this.teamFormGroup.controls.product.setValue("BSS");
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(WORKFLOWS.TEAMS_EDIT) || this.loginService.hasPermission(WORKFLOWS.TEAMS_DELETE)) {
            return ['id', 'name', 'status', 'action'];
        } else {
            return ['id', 'name', 'status'];
        }
    }
    ngOnInit(): void {
        this.teamFormGroup = new UntypedFormGroup({
            name: new UntypedFormControl("", [Validators.required]),
            status: new UntypedFormControl(null, [Validators.required]),
            teamType: new UntypedFormControl(""),
            product: new UntypedFormControl("BSS"),
        });

        this.searchData = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "any",
                },
            ],
            page: "",
            pageSize: "",
            sortBy: "createdate",
            sortOrder: 0,
        };
        this.listView = true;
        this.getTeamList("");
        this.getTeamType();
    }

    clearFormData() {
        this.teamFormGroup.reset();
        this.teamFormGroup.controls.product.setValue("BSS");
        this.editMode = false;
        this.submitted = false;
    }
    /**
     * Total Item Per Page
     * @param event
     */
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getTeamList(this.showItemPerPage);
        } else {
            this.searchTrc();
        }
    }

    displayedColumns: string[] = ['id', 'name', 'status', 'action'];
    getTeamList(list) {
        let size;
        this.searchkey = "";
        let page_list = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            // if (this.showItemPerPage == 0) {
            //   this.itemsPerPage = this.pageITEM
            // } else {
            //   this.itemsPerPage = this.showItemPerPage
            // }
            size = this.itemsPerPage;
        }
        let teamdata = {
            page: page_list,
            pageSize: size,
            sortBy: "createdate",
        };
        this.teamsService.getAllTeam(teamdata).subscribe(
            (response: any) => {
                this.teamListData = response.dataList;
                // if (this.showItemPerPage > this.itemsPerPage) {
                //   this.totalAreaListLength =
                //     this.teamListData.length % this.showItemPerPage
                // } else {
                //   this.totalAreaListLength =
                //     this.teamListData.length % this.itemsPerPage
                // }
                this.totalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    addUpdateTeam() {

        this.submitted = true;
        if (this.teamFormGroup.valid) {
            if (this.editMode) {
                this.updateTeam();
            } else {
                this.addNewTeam();
            }
        }
    }

    private addNewTeam() {
        if (this.teamFormGroup.valid) {
            var request = this.teamFormGroup.value;
            request.product = "BSS";
            this.teamsService.createTeam(this.teamFormGroup.value).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    } else {
                        this.getTeamList("");
                        this.clearFormData();
                        this.openTeamListMenu();
                        this.dialogRef?.close();
                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
        }
    }

    private updateTeam() {
        if (this.teamFormGroup.valid) {
            var request = this.teamFormGroup.value;
            request.product = "BSS";

            this.team = this.teamFormGroup.value;
            this.team.id = this.teamId;
            this.teamsService.updateTeam(this.team).subscribe(
                (response: any) => {
                    if (response.responseCode == 406 || response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    } else {
                        this.getTeamList("");
                        this.clearFormData();
                        this.openTeamListMenu();
                        this.dialogRef?.close();
                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
        }
    }

    editTeamById(teamId, index) {
        this.editMode = true;
        this.isTeamList = false;
        this.isTeamCreateOrEdit = true;
        this.teamId = teamId;
        // index = this.radiusUtility.getIndexOfSelectedRecord(
        //   index,
        //   this.currentPage,
        //   this.itemsPerPage,
        // )
        // this.team = this.teamListData[index]

        this.teamsService.getTeamById(teamId).subscribe(
            (response: any) => {
                let teamListData = response.data;
                this.teamFormGroup.patchValue(teamListData);
                this.openTeamDialog(true, teamListData);

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    deleteConfirmonTeam(team) {
        if (team) {
            this.confirmationService.confirm({
                message: "Do you want to delete this team?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteTeam(team);
                },
                reject: () => {
                    this.toastr.info("You have rejected", 'Info!');

                },
            });
        }
    }

    deleteTeam(team) {
        this.team = team;
        this.teamsService.deleteTeam(this.team).subscribe(
            (response: any) => {
                if (response.responseCode == 406 || response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    if (this.currentPage != 1 && this.totalAreaListLength == 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    this.getTeamList("");
                    this.openTeamListMenu();
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    /**
     * Page Changed
     * @param pageNumber
     */
    pageChanged(event: any) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getTeamList("");
        } else {
            this.searchTrc();
        }
    }


    /**
     * Search Team
     */
    searchTrc() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPage = 1;
            // this.itemsPerPage = 5;
            // this.pageSize = 5;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }

        this.searchData.filters[0].filterColumn = "any";
        this.searchData.filters[0].filterValue = this.searchTeamName.trim();
        this.searchData.page = this.currentPage;
        this.searchData.pageSize = this.itemsPerPage;
        const url = "/teams/searchAll";
        this.teamsService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response?.responseCode == 200 && response?.dataList?.length > 0) {
                    this.teamListData = response.dataList;
                    this.totalRecords = response.totalRecords;
                } else {
                    this.toastr.info(response.responseMessage == "OK" ? "No Record Found." : response.responseMessage, 'Info!');


                    this.teamListData = [];
                    this.totalRecords = 0;
                }
            },
            (error: any) => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.teamListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    clearSearchTrc() {
        this.searchTeamName = "";
        this.searchkey = "";
        this.getTeamList("");
        this.teamFormGroup.reset();
        this.teamFormGroup.controls.product.setValue("BSS");
    }

    canExit() {
        if (!this.teamFormGroup.dirty) return true;
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

    teamHierarchyModalOpen(data) {
        let teamData: any = [];
        let staffHierarchy = [];
        $("#teamHierarchyModal").modal("show");

        teamData = data;

        teamData.staffNameList.forEach(element => {
            staffHierarchy.push({
                label: element,
                type: "person",
                styleClass: "p-person",
            });
        });
        this.teamHierarchyData = [
            {
                label: data.name,
                type: "person",
                styleClass: "p-person",
                expanded: true,
                children: staffHierarchy,
            },
        ];

        this.hierarchyDialogRef = this.dialog.open(this.teamHierarchyDialog, {
            width: '800px',
            data: { name: data.name, hierarchy: this.teamHierarchyData }
        });
    }
    closeHierarchyDialog() {
        if (this.hierarchyDialogRef) {
            this.hierarchyDialogRef.close();
        }
    }

    getTeamType() {
        const url = "/commonList/teamType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.teamtypedata = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    openTeamDialog(edit: boolean = false, teamData: any = null) {
        this.editMode = edit;

        if (edit && teamData) {
            this.teamFormGroup.patchValue(teamData);
        } else {
            this.teamFormGroup.reset();
            this.teamFormGroup.controls.product.setValue('BSS');
        }

        this.dialogRef = this.dialog.open(this.teamDialogTemplate, {
            width: '800px'
        });
    }
    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }
    deleteConfirmonTeamDialog(team: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Team',
                description: `Are you sure you want to delete "${team.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteTeam(team);
            }
        });
    }


}
