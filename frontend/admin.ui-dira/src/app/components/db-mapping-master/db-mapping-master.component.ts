import { Component, OnInit, ElementRef, TemplateRef, AfterViewInit, ViewChild } from "@angular/core";
import { DBMappingMasterService } from "src/app/service/db-mapping-master.service";
import { UntypedFormBuilder, Validators, UntypedFormGroup, UntypedFormArray, FormArray } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { IDBMappingMaster } from "src/app/components/model/db-mapping-master";
import { DictionaryService } from "src/app/service/dictionary.service";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { element } from "protractor";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
import { ToastrService } from "ngx-toastr";


@Component({
    selector: "app-db-mapping-master",
    templateUrl: "./db-mapping-master.component.html",
    styleUrls: ["./db-mapping-master.component.css"],
    standalone: false
})
export class DBMappingMasterComponent implements OnInit, AfterViewInit {
    displayedColumns: string[] = ['name', 'status', 'action'];
    dataSource = new MatTableDataSource<any>([]);

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild('createEditDialog') createEditDialog!: TemplateRef<any>;
    @ViewChild('detailDialog') detailDialog!: TemplateRef<any>;

    createAccess = false;
    editAccess = false;
    deleteAccess = false;

    searchKey = '';

    AclClassConstants = AclClassConstants;
    AclConstants = AclConstants;
    changeStatusData: any = [];
    mappingMasterForm: UntypedFormGroup;
    searchForm: UntypedFormGroup;
    submitted = false;
    searchSubmitted = false;
    editDBMappingMasterId: number;
    attribute: UntypedFormArray;
    dbMappingMasterData: any = [];
    totalRecords: number;
    currentPage = 0;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    status = [{ label: "Active" }, { label: "Inactive" }];
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    createDBMappingMasterData: IDBMappingMaster;
    editDBMappingMasterData: IDBMappingMaster;
    mappingMasterData: IDBMappingMaster;
    editFormValues: any;
    editAttributeValues: any;
    update: boolean = true;
    editMode: boolean = false;
    dictionaryAttributeData: any = [];
    mappingMasterId: number;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    modalToggle: boolean = true;
    filtereDictionaryAttributeList: Array<any> = [];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    dialogRef: MatDialogRef<any>;
    @ViewChild("dbMappingName") usernameRef: ElementRef;

    public loginService: LoginService;
    userId: string;
    superAdminId = RadiusConstants.SUPERADMINID;

    constructor(
        private dialog: MatDialog,
        private dbMappingMasterService: DBMappingMasterService,
        private dictionaryService: DictionaryService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService, private toastr: ToastrService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private radiusUtility: RadiusUtility,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DB_MAPPING_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DB_MAPPING_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DB_MAPPING_EDIT);
        this.findAllDBMappingMasters("");
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DB_MAPPING_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DB_MAPPING_DELETE)) {
            return ['name', 'status', 'action'];
        } else {
            return ['name', 'status'];
        }
    }

    ngOnInit(): void {
        this.mappingMasterForm = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            mvnoName: [""]
        });
        this.attribute = this.fb.array([]);
        this.searchForm = this.fb.group({
            name: [null]
        });
        this.getAllDictionaryAttributes();

        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    onCancel() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    async searchByName() {
        if (!this.searchkey || this.searchkey !== this.searchForm.value.name) {
            this.currentPage = 0; // zero-based for paginator consistency
        }
        this.searchkey = this.searchForm.value.name;

        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchSubmitted = true;

        if (this.searchForm.value.name != null || this.searchForm.value.type != null) {
            let name = this.searchForm.value.name ? this.searchForm.value.name.trim() : "";
            this.dbMappingMasterData = [];
            // Pass pagination params to backend
            this.dbMappingMasterService.getDBMasterMappingByName(name).subscribe(
                (response: any) => {
                    this.dbMappingMasterData = response.dbMapingMasterList || [];
                    this.dataSource.data = this.dbMappingMasterData;
                    this.totalRecords = response.totalRecords || this.dbMappingMasterData.length;
                    // Update paginator state if needed
                    if (this.paginator) {
                        this.paginator.length = this.totalRecords;
                        this.paginator.pageSize = this.itemsPerPage;
                        this.paginator.pageIndex = this.currentPage;
                    }
                },
                error => {
                    this.dbMappingMasterData = [];
                    this.dataSource.data = [];
                    this.totalRecords = 0;
                    if (error.error.status == 404) {
                        this.toastr.info(`No Record found`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                }
            );
        }
    }



    // openCreateEditDialog1() {
    //     this.dialogRef = this.dialog.open(this.createEditDialog, { width: '900px' });
    //     this.dialogRef.afterClosed().subscribe((result) => {
    //         this.submitted = false;
    //         this.mappingMasterForm.reset();
    //         if (result) {
    //             this.clearSearchForm();
    //         }
    //     });
    // }

    clearSearchForm() {
        this.editMode = false;
        this.searchSubmitted = false;
        this.searchkey = '';
        this.searchForm.reset();
        this.currentPage = 0; // zero-based index
        this.itemsPerPage = RadiusConstants.ITEMS_PER_PAGE; // default page size

        // Reset the form and attributes array
        this.mappingMasterForm.reset();
        this.attribute = this.fb.array([]);
        this.onAddAttribute();

        // Fetch first page with default itemsPerPage and update paginator metadata explicitly
        this.findAllDBMappingMasters(this.itemsPerPage);
    }



    createDBMapping() {
        this.editMode = false;
        this.submitted = false;
        this.mappingMasterForm.reset();
        this.attribute = this.fb.array([]);
        this.onAddAttribute();
        this.usernameRef.nativeElement.focus();
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllDBMappingMasters(this.showItemPerPage);
        } else {
            this.searchByName();
        }
    }

    async findAllDBMappingMasters(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage || 0; // ensure zero-based page index
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage || RadiusConstants.ITEMS_PER_PAGE; // fallback
        }
        this.dbMappingMasterData = [];
        this.dbMappingMasterService.findAllDBMappingMasters(page, size).subscribe(
            (response: any) => {
                this.dbMappingMasterData = response.dbMapingMasterList || [];
                this.dataSource.data = this.dbMappingMasterData;
                this.totalRecords = response.totalRecords || this.dbMappingMasterData.length || 0;
                if (this.paginator) {
                    this.paginator.length = this.totalRecords;
                    this.paginator.pageSize = size;
                    this.paginator.pageIndex = page;
                    this.dataSource.paginator = this.paginator; // ensure paginator linked
                }
            },
            (error) => {
                this.totalRecords = 0;
                this.dataSource.data = [];
                if (this.paginator) {
                    this.paginator.length = 0;
                    this.paginator.pageIndex = 0;
                }
                if (error.error?.status === 404) {
                    this.toastr.info(error.message, "No records found");

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }


    async editDBMappingMasterById(dbMappingMasterId, selectedMvnoId, index) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.editMode = true;

            this.editDBMappingMasterId = dbMappingMasterId;
            this.dbMappingMasterService.findDbMappingMastersById(dbMappingMasterId).subscribe(
                (response: any) => {
                    this.editDBMappingMasterData = response.dbMapingMaster;
                    this.mappingMasterForm.patchValue({
                        name: this.editDBMappingMasterData.name,
                        status: this.editDBMappingMasterData.status,
                        mvnoName: this.editDBMappingMasterData.mvnoId
                    });
                    this.editFormValues = this.mappingMasterForm.value;
                    //
                    this.attribute = this.fb.array([]);
                    this.dbMappingMasterService.findDBMappingByDBMappingMasterId(dbMappingMasterId).subscribe(
                        (response: any) => {
                            let attributeList = response.DbMappingList;
                            attributeList.forEach(element => {
                                let isExist = this.filtereDictionaryAttributeList.some(
                                    attr => attr.name == element.radiusName
                                );
                                this.attribute.push(this.createAttributesArray(element, !isExist));
                            });
                            this.editAttributeValues = response.DbMappingList;
                            this.openCreateEditDialog();
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
            //
        }
    }
    dbMapingMaster = {
        mappingMasterId: 0,
        name: "",
        status: ""
    };
    async updateDbMappingMaster() {
        if (this.editDBMappingMasterData) this.editDBMappingMasterData = this.mappingMasterForm.value;
        this.editDBMappingMasterData.mappingMasterId = this.editDBMappingMasterId;
        this.dbMappingMasterService.updateDbMappingMaster(this.editDBMappingMasterData).subscribe(
            (response: any) => {
                this.editMode = false;
                this.submitted = false;
                if (!this.searchkey) {
                    this.findAllDBMappingMasters("");
                } else {
                    this.searchByName();
                }
                this.mappingMasterForm.reset();
                if (this.update) {
                    this.toastr.success(`${response.message}`, "Successfully ");
                }
                if (this.dialogRef) {
                    this.dialogRef.close(true);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    async updateDBMapping() {
        this.attribute.value.forEach(element => {
            element.mappingMasterId = this.editDBMappingMasterId;
        });
        this.dbMappingMasterService
            .updateDBMapping(
                this.attribute.value,
                this.editDBMappingMasterId,
                this.mappingMasterForm.value.mvnoName
            )
            .subscribe(
                (response: any) => {
                    this.editMode = false;
                    this.submitted = false;
                    this.mappingMasterForm.reset();
                    this.attribute = this.fb.array([]);
                    this.getDefaultAttributes();
                    if (this.update) {
                        this.toastr.success(`${response.message}`, "Successfully ");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        // }
    }

    async addNewDbMappingMaster() {
        this.submitted = true;
        if (this.mappingMasterForm.valid && this.attribute.valid) {
            if (this.editMode) {
                if (
                    this.mappingMasterForm.value == this.editFormValues &&
                    JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
                ) {
                    this.editMode = false;
                    this.submitted = false;
                    this.mappingMasterForm.reset();
                    this.attribute = this.fb.array([]);
                    this.getDefaultAttributes();
                    (response: any) => { this.toastr.success(`${response.message}`, "Profile data is same "); }
                    if (this.dbMappingDialogRef) {
                        this.dbMappingDialogRef.close(true);
                    }

                } else if (
                    this.mappingMasterForm.value != this.editFormValues &&
                    JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
                ) {
                    this.updateDbMappingMaster();
                } else if (
                    this.mappingMasterForm.value == this.editFormValues &&
                    JSON.stringify(this.attribute.value) !== JSON.stringify(this.editAttributeValues)
                ) {
                    this.updateDBMapping();
                } else {
                    this.update = false;
                    this.updateDbMappingMaster();
                    this.updateDBMapping();
                    this.editMode = false;
                    this.toastr.success("DB Mapping Master and Mapping Attributes has been updated successfully.");
                    this.dbMappingDialogRef.close();
                }
            } else {
                this.createDBMappingMasterData = this.mappingMasterForm.value;
                this.createDBMappingMasterData.dbMappingDtoList = this.attribute.value;
                this.dbMappingMasterService.addNewDbMappingMaster(this.createDBMappingMasterData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.findAllDBMappingMasters("");
                        this.mappingMasterForm.reset();
                        this.attribute = this.fb.array([]);
                        this.getDefaultAttributes();
                        this.toastr.success(`${response.message}`, "Successfully ");
                        if (this.dbMappingDialogRef) {
                            this.dbMappingDialogRef.close(true);
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            }
        }
    }
    deleteConfirm(dbMappingMasterId: number, selectedMvnoId: any, index: number) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Delete DB Mapping Master',
                    description: 'Do you want to delete this DB Mapping Master?',
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteDBMappingMasterById(dbMappingMasterId, selectedMvnoId, index);
                }
            });
        }
    }

    async deleteDBMappingMasterById(dbMapingMasterId, selectedMvnoId, index) {
        this.dbMappingMasterService
            .deleteDbMappingMasterById(dbMapingMasterId, selectedMvnoId)
            .subscribe(
                (response: any) => {
                    if (this.currentPage != 1 && index == 0 && this.dbMappingMasterData.length == 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    if (!this.searchkey) {
                        this.findAllDBMappingMasters("");
                    } else {
                        this.searchByName();
                    }
                    this.mappingMasterForm.reset();
                    this.attribute = this.fb.array([]);
                    this.onAddAttribute();
                    this.toastr.success(`${response.message}`, "Successfully ");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
    }

    onAddAttribute() {
        this.attribute.push(this.createAttributeFormGroup());
    }
    deleteConfirmAttribute(attributeIndex: number, mappingId: number, selectedMvnoId) {
        this.attribute.removeAt(attributeIndex);
    }

    createAttributeFormGroup(): UntypedFormGroup {
        return this.fb.group({
            dbColumnName: ["", Validators.required],
            radiusName: ["", Validators.required],
            mappingMasterId: [""],
            mappingId: [""]
        });
    }

    async getAllDictionaryAttributes() {
        this.dictionaryService.findAllAttributes().subscribe(
            (response: any) => {
                this.dictionaryAttributeData = response;
                this.getDetailsByMvno(JSON.parse(localStorage.getItem("mvnoId")));
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChanged(event: PageEvent) {
        this.currentPage = event.pageIndex; // zero-based page index expected by backend
        this.itemsPerPage = event.pageSize;
        if (!this.searchkey) {
            this.findAllDBMappingMasters(this.itemsPerPage);
        } else {
            this.searchByName();
        }
    }

    async changeStatusToActive(
        mappingMasterId: number | string,
        selectedMvnoId: number | string,
        event: MatSlideToggleChange
    ): Promise<void> {
        event.source.disabled = true; // Disable toggle during processing
        const previousChecked = !event.checked;
        this.modalToggle = true;

        const isAuthorized = this.validateUserToPerformOperations(selectedMvnoId);
        if (!isAuthorized) {
            // Revert toggle state if unauthorized
            setTimeout(() => {
                event.source.checked = previousChecked;
                event.source.disabled = false;
            });
            return;
        }

        this.dbMappingMasterService.changeDBMappingMasterStatus(
            mappingMasterId,
            RadiusConstants.ACTIVE,
            selectedMvnoId
        ).subscribe(
            (response: any) => {
                this.toastr.success(`${response.message}`, "Successfully ");
                if (!this.searchkey) {
                    this.findAllDBMappingMasters('');
                } else {
                    this.searchByName();
                }
                event.source.disabled = false; // Re-enable toggle
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                setTimeout(() => {
                    event.source.checked = previousChecked;
                    event.source.disabled = false;
                });
            }
        );
    }

    async changeStatusToInActive(
        mappingMasterId: number | string,
        selectedMvnoId: number | string,
        event: MatSlideToggleChange
    ): Promise<void> {
        event.source.disabled = true; // Disable toggle during processing
        const previousChecked = !event.checked;
        this.modalToggle = true;

        const isAuthorized = this.validateUserToPerformOperations(selectedMvnoId);
        if (!isAuthorized) {
            // Revert toggle state if unauthorized
            setTimeout(() => {
                event.source.checked = previousChecked;
                event.source.disabled = false;
            });
            return;
        }

        this.dbMappingMasterService.changeDBMappingMasterStatus(
            mappingMasterId,
            RadiusConstants.IN_ACTIVE,
            selectedMvnoId
        ).subscribe(
            (response: any) => {
                this.toastr.success(`${response.message}`, "Successfully ");
                if (!this.searchkey) {
                    this.findAllDBMappingMasters('');
                } else {
                    this.searchByName();
                }
                event.source.disabled = false; // Re-enable toggle
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                setTimeout(() => {
                    event.source.checked = previousChecked;
                    event.source.disabled = false;
                });
            }
        );
    }

    onToggleStatusChange(mappingMaster: any, event: MatSlideToggleChange) {
        if (event.checked) {
            this.changeStatusToActive(mappingMaster.mappingMasterId, mappingMaster.mvnoId, event);
        } else {
            this.changeStatusToInActive(mappingMaster.mappingMasterId, mappingMaster.mvnoId, event);
        }
    }

    mappingMasterDetails = {
        name: "",
        status: ""
    };

    showMappingMasterDetail(mappingMasterId, mvnoId) {
        this.modalToggle = true;
        this.dbMappingMasterService.findDbMappingMastersById(mappingMasterId).subscribe(
            (response: any) => {
                this.dbMappingMasterData = response;
                this.mappingMasterDetails = this.dbMappingMasterData.dbMapingMaster;
                this.dialogRef = this.dialog.open(this.detailDialog, {
                    width: '500px'
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        if (this.userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            this.toastr.info("You are not authorized to do this operation. Please contact to the administrator");
            return false;
        }
        return true;
    }

    getDetailsByMvno(event) {
        let mvnoId = event;
        let alldictionaryAttributeList = this.dictionaryAttributeData.dictionaryAttributeList
            ? this.dictionaryAttributeData.dictionaryAttributeList
            : [];
        this.filtereDictionaryAttributeList = alldictionaryAttributeList;
        this.getDefaultAttributes();
    }
    canExit() {
        if (!this.mappingMasterForm.dirty) return true;
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

    getDefaultAttributes() {
        let dbMappingMasterId = 1;
        this.dbMappingMasterService.findDBMappingByDBMappingMasterId(dbMappingMasterId).subscribe(
            (response: any) => {
                let attributeList = response.DbMappingList;
                attributeList.forEach(element => {
                    let isExist = this.filtereDictionaryAttributeList.some(
                        attr => attr.name == element.radiusName
                    );
                    this.attribute.push(this.createAttributesArray(element, !isExist));
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    createAttributesArray(element, isDesibled) {
        return this.fb.group({
            createdOn: [element?.createdOn],
            dbColumnName: [element?.dbColumnName],
            lastModifiedOn: [element?.lastModifiedOn],
            mappingId: [element?.mappingId],
            mappingMasterId: [element?.mappingMasterId],
            mvnoId: [element?.mvnoId],
            radiusName: [element?.radiusName],
            isDesibled: [isDesibled]
        });
    }
    @ViewChild('dbMappingDialog') dbMappingDialog!: TemplateRef<any>;
    dbMappingDialogRef!: MatDialogRef<any>;

    attributeDisplayedColumns = ['radiusName', 'dbColumnName'];
    openCreateEditDialog(): void {
        this.dbMappingDialogRef = this.dialog.open(this.dbMappingDialog, {
            width: '900px',
            disableClose: true
        });
    }
}
