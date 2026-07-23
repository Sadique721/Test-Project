import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { EmailConfigService } from "src/app/service/email-config.service";
import { UntypedFormBuilder, Validators, UntypedFormGroup, NgForm } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
// Remove MessageService import since we're replacing it with toastr
import { ConfirmationService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { MASTERS, NOTIFICATIONS } from "src/app/constants/aclConstants";
// import { ConfirmationService } from 'primeng/api';
import { resolve } from "dns";
import { ObserversModule } from "@angular/cdk/observers";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-email-config",
    templateUrl: "./email-config.component.html",
    styleUrls: ["./email-config.component.css"],
    standalone: false
})
export class EmailConfigComponent implements OnInit {
    AclClassConstants;
    AclConstants;
    groupName = "";
    statusMsg = "";
    newGroupData = {
        name: "",
        cgStatus: ""
    };
    editGroupData = {
        emailConfigId: "",
        userName: "",
        password: "",
        authParam: "",
        authValue: "",
        //starttlsParam: '',
        authType: "",
        hostParam: "",
        hostValue: "",
        portParam: "",
        portValue: ""
    };

    changeStatusData: any = [];
    groupData: any = [];
    serviceType: any;
    editForm: UntypedFormGroup;
    submitted = false;
    isEmailConfigEdit = true;
    private emailConfigDialogRef: MatDialogRef<any> | null = null;
    //Used and required for pagination
    totalRecords: String;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    //Used to store error data and error message
    errorData: any = [];
    errorMsg = "";
    dialogRef: any;
    dialogMode: 'add' | 'edit' = 'add'; // Add this property
    dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['index', 'userName', 'hostServer', 'port', 'mvnoName', 'action'];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    selectedConfigIndex: number = -1;

    editMode: boolean = false;
    smtpAuth = [
        { label: "true", value: true },
        { label: "false", value: false }
    ];
    authType = [
        { label: "StartTLS", value: "StartTLS" },
        { label: "SSL", value: "SSL" }
    ];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    loginmvnoid: any = JSON.parse(localStorage.getItem("mvnoId"));

    //hostServer = [{ label: 'smtp.gmail.com' }, { label: 'smtp.live.com' }, { label: 'smtp.office365.com ' }, { label: 'smtp.mail.yahoo.com' }, { label: 'plus.smtp.mail.yahoo' }];
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    loggedInUser: string;
    isDialogOpen: boolean = false;
    showPassword = false;
    _passwordType = "password";

    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    mvnoId: any;

    constructor(
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private dialog: MatDialog,
        private emailConfigService: EmailConfigService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private confirmationService: ConfirmationService,
        private radiusUtility: RadiusUtility,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        public loginService: LoginService // private confirmationService: ConfirmationService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.createAccess = loginService.hasPermission(NOTIFICATIONS.EMAIL_CONFIG_CREATE);
        this.editAccess = loginService.hasPermission(NOTIFICATIONS.EMAIL_CONFIG_EDIT);
        this.dataSource = new MatTableDataSource();
    }

    ngOnInit(): void {
        this.initializeForm();
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.getCurrentStaffBUId();
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(NOTIFICATIONS.EMAIL_CONFIG_EDIT)) {
            return ['index', 'userName', 'hostServer', 'port', 'mvnoName', 'action'];
        } else {
            return ['index', 'userName', 'hostServer', 'port', 'mvnoName'];
        }
    }
    initializeForm() {
        this.editForm = this.fb.group({
            userName: ["", Validators.required],
            password: ["", Validators.required],
            authValue: ["", Validators.required],
            authType: ["", Validators.required],
            hostValue: ["", Validators.required],
            portValue: ["", Validators.required],
            mvnoName: [""],
            createdBy: [""],
            lastModifiedBy: [""]
        });
    }

    //Properties of Confirmation Popup
    popoverTitle: string = RadiusConstants.CONFIRM_DIALOG_TITLE;
    popoverMessage: string = RadiusConstants.DELETE_GROUP_CONFIRM_MESSAGE;
    confirmedClicked: boolean = false;
    cancelClicked: boolean = false;
    closeOnOutsideClick: boolean = true;

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        this.findAll(this.showItemPerPage);
    }


    ngAfterViewInit() {
        // Connect paginator and sort to data source after view init
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    async findAll(size) {
        this.serviceType = "BSS";
        let page_list;
        if (size) {
            page_list = size;
            this.itemsPerPage = size;
        } else {
            if (this.showItemPerPage == 1) {
                this.itemsPerPage = this.pageITEM;
            } else {
                this.itemsPerPage = this.showItemPerPage;
            }
        }

        this.emailConfigService.findAll(this.currentStaffBuid, this.serviceType).subscribe(
            (response: any) => {
                this.groupData = response.emailConfigList;
                this.totalRecords = this.groupData.length;

                this.dataSource.data = this.groupData;
                // if (this.paginator) {
                //   this.paginator.length = this.totalRecords;
                //   this.paginator.pageSize = this.itemsPerPage; 
                // }
                // const hasMVNOIdOne = this.groupData.some(item => item.mvnoId === 1);
                // if (this.loginmvnoid == 1) {
                //   if (hasMVNOIdOne && this.groupData !== null && this.groupData.length > 0) {
                //     this.editMode = true;
                //   }
                // } else {
                //   if (hasMVNOIdOne) {
                //     if (this.groupData !== null && this.groupData.length > 1) {
                //       this.editMode = false;
                //     }
                //   } else {
                //     if (this.groupData !== null && this.groupData.length > 0) {
                //       this.editMode = false;
                //     }
                //   }
                // }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    handlePageEvent(event: PageEvent) {
        this.itemsPerPage = event.pageSize;
        this.currentPage = event.pageIndex + 1;
        // If you need to fetch new data based on pagination, do it here
    }

    async getEdit() {
        if (this.groupData.size > 1) {
            this.editMode = true;
        }
    }

    async addeditEmailConfig(id) {
        this.serviceType = "BSS";
        this.submitted = true;

        if (this.editForm.invalid) {
            return;
        }

        const updatedGroupData = {
            emailConfigId: this.editMode ? this.editGroupData.emailConfigId : "",
            userName: this.editForm.value.userName,
            password: this.editForm.value.password,
            smtpAuth: this.editForm.value.authValue,
            authType: this.editForm.value.authType,
            hostServer: this.editForm.value.hostValue,
            port: this.editForm.value.portValue,
            mvnoId: this.editForm.value.mvnoName,
            createdBy: this.editForm.value.createdBy,
            lastModifiedBy: this.loggedInUser,
            serviceType: this.serviceType
        };

        if (this.editMode) {
            this.emailConfigService
                .updateEmailConfig(updatedGroupData, this.currentStaffBuid)
                .subscribe(
                    (response: any) => {
                        this.handleSuccessResponse("Updated Successfully");
                    },
                    (error: any) => {
                        this.handleErrorResponse(error);
                    }
                );
        } else {
            this.emailConfigService.addEmailConfig(updatedGroupData, this.currentStaffBuid).subscribe(
                (response: any) => {
                    this.handleSuccessResponse("Added Successfully");
                },
                (error: any) => {
                    this.handleErrorResponse(error);
                }
            );
        }
    }

    private handleSuccessResponse(message: string) {
        this.submitted = false;
        this.editForm.reset();
        this.findAll("");
        this.toastr.success(`${message}`, 'Success!');
        this.closeDialog();
        // this.editMode = false;
        // this.isEmailConfigEdit = true;
    }

    closeDialog() {
        if (this.emailConfigDialogRef) {
            this.emailConfigDialogRef.close();
            this.emailConfigDialogRef = null;
        }
        this.resetDialogState();
        this.isDialogOpen = false;
    }

    private handleErrorResponse(error: any) {
        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
    }

    editConfigById(emailConfigId, index) {
        this.selectedConfigIndex = index;
        this.editMode = true;
        this.isEmailConfigEdit = false;

        const actualIndex = this.dataSource.data.findIndex(item => item.emailConfigId === emailConfigId);

        if (actualIndex !== -1) {
            this.editForm.patchValue({
                userName: this.dataSource.data[actualIndex].userName,
                password: this.dataSource.data[actualIndex].password,
                authValue: this.dataSource.data[actualIndex].smtpAuth,
                authType: this.dataSource.data[actualIndex].authType,
                hostValue: this.dataSource.data[actualIndex].hostServer,
                portValue: this.dataSource.data[actualIndex].port,
                mvnoName: this.dataSource.data[actualIndex].mvnoId
            });

            this.editGroupData.emailConfigId = this.dataSource.data[actualIndex].emailConfigId;
        }

        this.dialogRef = this.dialog.open(this.EmailConfigDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.editForm.reset();
            this.editMode = false;
        });
    }

    async clearSearchForm() {
        this.currentPage = 1;
        this.findAll("");
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber;
    }

    canExit() {
        if (!this.editForm.dirty) return true;
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

    currentLoginStaffData: any;
    currentStaffBuid: any;
    async getCurrentStaffBUId() {

        const url = "/staffuser/" + localStorage.getItem("userId");
        this.savbillCommonBaseService.get(url).subscribe(
            async (response: any) => {
                if (response.status == 200) {
                    this.currentLoginStaffData = await response.Staff;
                    this.currentStaffBuid = this.currentLoginStaffData?.businessUnitIdsList || [];
                    if (!Array.isArray(this.currentStaffBuid)) {
                        this.currentStaffBuid = [];
                    }
                    if (this.currentStaffBuid.length === 1) {
                        this.currentStaffBuid = this.currentStaffBuid[0];
                        this.findAll("");
                    } else if (this.currentStaffBuid.length === 0) {
                        this.currentStaffBuid = 0;
                        this.findAll("");
                    } else if (this.currentStaffBuid.length > 1) {
                        this.currentStaffBuid = 0;
                        this.findAll("");
                        this.toastr.info("Multiple BU found in given staff", 'Info!');
                    }
                } else {
                    this.toastr.error(`${response.errorMessage}`, 'Failed!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || "An unexpected error occurred"}`, 'Failed!');
            }
        );
    }

    @ViewChild("EmailConfigDialog") EmailConfigDialog!: TemplateRef<any>;

    openEmailConfigDialog() {
        if (this.isDialogOpen) {
            return;
        }

        this.resetDialogState();
        this.editMode = false;
        this.isDialogOpen = true; // Set flag to true

        this.emailConfigDialogRef = this.dialog.open(this.EmailConfigDialog, {
            width: "900px",
            disableClose: true
        });

        this.emailConfigDialogRef.afterClosed().subscribe(() => {
            this.resetDialogState();
            this.isDialogOpen = false; // Reset flag when closed
        });
    }

    private populateEditForm(emailConfigId, index) {
        const actualIndex = this.dataSource.data.findIndex(item => item.emailConfigId === emailConfigId);

        if (actualIndex !== -1) {
            this.editForm.patchValue({
                userName: this.dataSource.data[actualIndex].userName,
                password: this.dataSource.data[actualIndex].password,
                authValue: this.dataSource.data[actualIndex].smtpAuth,
                authType: this.dataSource.data[actualIndex].authType,
                hostValue: this.dataSource.data[actualIndex].hostServer,
                portValue: this.dataSource.data[actualIndex].port,
                mvnoName: this.dataSource.data[actualIndex].mvnoId
            });

            this.editGroupData.emailConfigId = this.dataSource.data[actualIndex].emailConfigId;
        }
    }

    openEditDialog(emailConfigId, index) {
        if (this.isDialogOpen) {
            return;
        }

        this.resetDialogState();
        this.editMode = true;
        this.isEmailConfigEdit = false;
        this.selectedConfigIndex = index;
        this.isDialogOpen = true;

        this.populateEditForm(emailConfigId, index);

        this.emailConfigDialogRef = this.dialog.open(this.EmailConfigDialog, {
            width: "900px",
            disableClose: true
        });

        this.emailConfigDialogRef.afterClosed().subscribe(() => {
            this.resetDialogState();
            this.isDialogOpen = false;
        });
    }

    resetDialogState() {
        this.editForm.reset();
        this.editMode = false;
        this.submitted = false;
        this.selectedConfigIndex = -1;
        this.isEmailConfigEdit = true;
    }
}
