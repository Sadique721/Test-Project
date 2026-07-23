import { Component, NgZone, OnInit, ViewChild } from "@angular/core";
import { UntypedFormBuilder, FormControl, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService, TreeNode } from "primeng/api";
import { Observable, Observer } from "rxjs";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { LoginService } from "src/app/service/login.service";
import { RoleService } from "src/app/service/role.service";
import { Acl } from "../generic-component/acl/acl-gerneric-component/model/acl";
import { AclOperationsList } from "../generic-component/acl/acl-gerneric-component/model/acl-operations-list";
import { AclSave } from "../generic-component/acl/acl-gerneric-component/model/acl-save";
import { Aclsaveoperationlist } from "../generic-component/acl/acl-gerneric-component/model/aclsaveoperationlist";
import { SETTINGS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog } from '@angular/material/dialog';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { CreateRoleComponent } from "./create-role/create-role.component";
import { ToastrService } from "ngx-toastr";
@Component({
    selector: "app-role-management",
    templateUrl: "./role-management.component.html",
    styleUrls: ["./role-management.component.css"],
    standalone: false
})
export class RoleManagementComponent implements OnInit {
    AclClassConstants;
    AclConstants;

    searchRoleForm: UntypedFormGroup;

    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: number = 0;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    submitted: boolean = false;
    searchSubmitted = false;
    isRoleList: boolean = true;
    isRoleCreateOrEdit: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    roleList: any = [];
    dataSource = new MatTableDataSource<any>([]);
    // displayedColumns = ['id', 'Name', 'Status', 'Action'];
    currentPageSize;

    searchData = {
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
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    customerGroupForm: any;
    planGroupForm: any;
    constructor(
        private roleService: RoleService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private radiusUtility: RadiusUtility, private toastr: ToastrService,
        private ngZone: NgZone,
        private dialog: MatDialog,
        public loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(SETTINGS.ROLE_CREATE);
        this.deleteAccess = loginService.hasPermission(SETTINGS.ROLE_DELETE);
        this.editAccess = loginService.hasPermission(SETTINGS.ROLE_EDIT);
    }


    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(SETTINGS.ROLE_EDIT) || this.loginService.hasPermission(SETTINGS.ROLE_DELETE)) {
            return ['id', 'Name', 'Status', 'Action'];
        } else {
            return ['id', 'Name', 'Status'];
        }
    }
    ngOnInit(): void {
        this.searchRoleForm = this.fb.group({
            name: ["", Validators.required],
        });
        this.getAll("");
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    openCreateRoleDialog(roleData = null) {
        const dialogRef = this.dialog.open(CreateRoleComponent, {
            width: '900px',
            data: {
                roleData: roleData,
                isEdit: roleData != null,
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                title: roleData ? 'Update Role' : 'Create Role',
                yesLabel: roleData ? 'Update' : 'Create',
                noLabel: 'Cancel',
                inputName: 'Enter Role Name',
                inputStatus: 'Select Status'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.roleSaveorUpdated();
            }
        });
    }

    openRoleCreateMenu() {
        this.isRoleCreateOrEdit = true;
        this.roleData = null;
        this.openCreateRoleDialog(null);
    }

    openRoleListMenu() {
        this.isRoleCreateOrEdit = false;
        this.isRoleList = true;
        this.roleData = null;
    }

    roleSaveorUpdated() {
        this.isRoleCreateOrEdit = false;
        this.isRoleList = true;
        this.roleData = null;
        this.searchRoleForm.reset();
        this.getAll(this.itemsPerPage);
    }



    roleData = null;
    roleId = null;

    deleteConfirm(role: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Role',
                description: `Do you want to delete "${role.rolename}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteRoleById(role);
            }
        });
    }

    editRoleById(roleId: number, index: number) {
        this.roleService.getRoleById(roleId).subscribe(
            (response: any) => {
                const roleData = response.data;
                if (roleData) {
                    this.openCreateRoleDialog(roleData);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getAll(list) {
        let size = list || this.itemsPerPage;
        this.itemsPerPage = size;

        let rolData = {
            page: this.currentPage,
            pageSize: this.itemsPerPage,
        };

        this.roleService.getDataPostAPIWithFlag(rolData, true).subscribe(
            (response: any) => {
                this.roleList = response.dataList;
                this.dataSource.data = this.roleList;
                this.totalRecords = response.totalRecords;

                if (response.currentPageNumber > response.totalPages) {
                    this.currentPage = 1;
                    this.getAll(this.itemsPerPage);
                }

                setTimeout(() => {
                    if (this.paginator) {
                        this.paginator.length = this.totalRecords;
                        this.paginator.pageIndex = this.currentPage - 1;
                        this.paginator.pageSize = this.itemsPerPage;
                    }
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    searchKey = ''

    searchRoleByName() {
        this.searchSubmitted = true;
        if (this.searchKey) {

            if (
                !this.searchData.filters[0].filterValue ||
                this.searchData.filters[0].filterValue !== this.searchKey
            ) {
                this.currentPage = 1;
                this.searchData.page = "1";
                this.searchData.pageSize = RadiusConstants.ITEMS_PER_PAGE.toString();
                this.currentPageSize = RadiusConstants.ITEMS_PER_PAGE.toString();
            } else {
                this.searchData.page = this.currentPage.toString();
                this.searchData.pageSize = this.itemsPerPage.toString();
                this.currentPageSize = this.itemsPerPage;
            }
            this.searchData.filters[0].filterValue = this.searchKey.trim();
            this.roleService.getByName(this.searchData).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');
                        this.roleList = [];
                        this.dataSource.data = [];
                        this.totalRecords = 0;
                    } else {
                        this.roleList = response.dataList;
                        this.dataSource.data = this.roleList;
                        this.totalRecords = response.totalRecords;
                        if (response.currentPageNumber > response.totalPages) {
                            this.currentPage = 1;
                            this.searchRoleByName();
                        }
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }


    deleteRoleById(role) {
        this.roleService.delete(role.id).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (this.searchData.filters[0].filterValue) {
                        this.searchRoleByName();
                    } else {
                        this.getAll("");
                    }
                    this.loginService.refreshToken();
                    this.toastr.success(`${response.responseMessage}`, "Successfully ");
                } else {
                    this.toastr.info(response.responseMessage, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChanged(event: PageEvent) {
        const pageSizeChanged = this.itemsPerPage !== event.pageSize;

        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (pageSizeChanged) {
            this.currentPage = 1;
        }

        if (this.searchKey && this.searchKey.trim() !== '') {
            this.searchRoleByName();
        } else {
            this.getAll(this.itemsPerPage);
        }
    }


    TotalItemPerPage(event) {
        this.itemsPerPage = Number(event.value);
        this.currentPageSize = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchKey) {
            this.getAll(this.itemsPerPage);
        } else {
            this.searchRoleByName();
        }
    }

    clearSearchForm() {
        this.searchRoleForm.reset();
        this.searchSubmitted = false;
        this.searchKey = ''
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
        this.currentPage = 1;
        this.getAll(this.itemsPerPage);
    }

    canExit(): Observable<boolean> {
        if (!this.searchRoleForm.dirty) {
            return new Observable(observer => {
                observer.next(true);
                observer.complete();
            });
        }

        return new Observable((observer: Observer<boolean>) => {
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
        });
    }
}
