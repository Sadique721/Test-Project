import { Component, OnInit, ViewChild } from "@angular/core";
import { EmailNotificationService } from "src/app/service/email-notification.service";
import { UntypedFormBuilder, UntypedFormGroup, NgForm, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { Observable, Observer } from "rxjs";
import { NOTIFICATIONS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TemplateRef } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-email-notification",
    templateUrl: "./email-notification.component.html",
    styleUrls: ["./email-notification.component.css"],
    standalone: false
})
export class EmailNotificationComponent implements OnInit {
    displayedColumns: string[] = ['id', 'eventName', 'emailAddress', 'date', 'status', 'action'];
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild('emailDialogTemplate') emailDialogTemplate!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
    }
    //@ViewChild('emailDetailsForm') emailDetailsForm: NgForm;
    // @ViewChild('searchForm') searchForm: NgForm;
    public loginService: LoginService;
    AclClassConstants;
    AclConstants;
    searchSubmitted = false;
    submitted = false;
    searchForm: UntypedFormGroup;
    emailDetailsForm: UntypedFormGroup;
    message: string;
    emailAddress: string;
    sourceName = "";
    statusMsg = "";
    newGroupData = {
        sourceName: "",
        emailAddress: "",
        message: "",
        eventName: "",
    };
    editGroupData = {
        emailId: "",
        sourceName: "",
        emailAddress: "",
        message: "",
        eventName: "",
    };
    searchGroupData = {
        sourceName: "",
    };

    changeStatusData: any = [];
    groupData = [];
    sourceNameValue = [
        { label: "Savbill Radius" },
        { label: "Savbill Wifi" },
        { label: "Savbill BSS API GATEWAY" },
        { label: "Savbill Common" },
        { label: "SAVBILL Ticket" },
    ];
    eventTypeValue: any = [];
    eventList: any = [];
    //sourceNameValue: String[] = ["Savbill Radius", "Savbill Wifi", "Savbill Common"];
    //Used and required for pagination
    totalRecords: number;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    //Used to store error data and error message
    errorData: any = [];
    errorMsg = "";

    //Used for alert message.
    alert_success: boolean = false;
    alert_update_success: boolean = false;
    alert_delete_success: boolean = false;
    alert_send_success: boolean = false;
    alert_InActive_success: boolean = false;
    alert_Active_success: boolean = false;
    alert_error_message: boolean = false;
    alert_search_error_message: boolean = false;
    editMode: boolean = false;
    filteredSearchResults: any[] = [];

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: string;
    totalDataListLength = 0;
    createAccess: boolean = false;
    editAccess: boolean = false;
    deleteAccess: boolean = false;
    sendAccess: boolean = false;

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private emailNotificationService: EmailNotificationService,
        private radiusUtility: RadiusUtility,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_EMAIL_CREATE);
        this.deleteAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_EMAIL_DELETE);
        this.sendAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_EMAIL_SEND);
        this.editAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_EMAIL_EDIT);
        this.editMode = !this.createAccess && this.editAccess ? true : false;
        this.findAllEmail("");
    }

    ngOnInit(): void {

        this.getEventType();
        this.emailDetailsForm = this.fb.group({
            sourceName: ["", Validators.required],
            emailAddress: ["", Validators.required],
            message: ["", Validators.required],
            eventId: ["", Validators.required],
        });
        this.searchForm = this.fb.group({
            emailAddress: [""],
        });
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_EMAIL_EDIT) || this.loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_EMAIL_DELETE)) {
            return ['id', 'eventName', 'emailAddress', 'date', 'status', 'action'];
        } else {
            return ['id', 'eventName', 'emailAddress', 'date', 'status'];
        }
    }

    //Properties of Confirmation Popup
    popoverTitle: string = RadiusConstants.CONFIRM_DIALOG_TITLE;
    popoverMessage: string = RadiusConstants.DELETE_GROUP_CONFIRM_MESSAGE;
    confirmedClicked: boolean = false;
    cancelClicked: boolean = false;
    closeOnOutsideClick: boolean = true;
    async searchBySourceName() {
        this.clearMessageAlert();

        let name = this.searchForm.value.emailAddress?.trim() || "";

        if (!this.searchkey || this.searchkey !== name) {
            this.currentPage = 1; // Reset current page when search key changes
        }
        this.searchkey = name;

        this.itemsPerPage = this.showItemPerPage === 1 ? this.pageITEM : this.showItemPerPage;
        this.searchSubmitted = true;

        if (this.searchForm.valid) {
            this.emailNotificationService.getEmailDataBySourceName(name).subscribe(
                (response: any) => {
                    this.reset();

                    const allData = response.emailList || [];

                    // Cache filtered results
                    this.filteredSearchResults = allData;

                    this.totalRecords = this.filteredSearchResults.length;

                    // Calculate max pages and verify currentPage
                    const maxPages = Math.ceil(this.totalRecords / this.itemsPerPage);
                    if (this.currentPage > maxPages && maxPages > 0) {
                        this.currentPage = maxPages;
                    }
                    if (this.currentPage < 1) {
                        this.currentPage = 1;
                    }

                    this.updatePagedList();

                },
                (error: any) => {
                    this.groupData = [];
                    this.totalRecords = 0;
                    if (error.error.status === 404 || error.error.status === 400) {
                        this.toastr.error(`${error.error.message || "Data Not Found"}`, 'Failed!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage || "Server Error"}`, 'Failed!');

                    }
                }
            );
        }
    }
    updatePagedList() {
        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;

        const pageData = this.filteredSearchResults.slice(startIndex, endIndex);

        const totalPages = Math.ceil(this.totalRecords / this.itemsPerPage);
        this.groupData = pageData;
    }



    getEventById(id) {
        this.emailNotificationService.getEventById(id).subscribe(
            (response: any) => {
                this.eventList = response;
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    getEventType() {
        this.emailNotificationService.getEvents().subscribe(
            (response: any) => {
                this.eventTypeValue = response;
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllEmail(this.showItemPerPage);
        } else {
            this.searchBySourceName();
        }
    }
    async findAllEmail(size) {
        let pageSize;
        this.searchkey = "";
        if (size) {
            pageSize = size;
            this.itemsPerPage = size;
        } else {
            if (this.showItemPerPage == 1) {
                this.itemsPerPage = this.pageITEM;
            } else {
                this.itemsPerPage = this.showItemPerPage;
            }
        }
        let pageData;

        this.emailNotificationService.findAllEmailData(this.itemsPerPage, this.currentPage).subscribe(
            (response: any) => {
                // this.groupData = response.emailList.data;
                // this.totalRecords = response.emailList.totalRecords;
                //console.log(this.totalRecords);
                // if (this.showItemPerPage > this.itemsPerPage) {
                //   this.totalDataListLength = this.groupData.length % this.showItemPerPage;
                // } else {
                //   this.totalDataListLength = this.groupData.length % this.itemsPerPage;
                if (response.message) {
                    this.toastr.info(`${response.message}`, 'Info!');

                } else {
                    this.groupData = response.emailList.data;
                    this.totalRecords = response.emailList.totalRecords;
                }
                // }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    async deleteEmailById(emailId) {
        this.emailNotificationService.deleteEmailById(emailId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && this.totalDataListLength == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                this.reset();
                if (!this.searchkey) {
                    this.findAllEmail("");
                } else {
                    this.searchBySourceName();
                }
                this.toastr.success("Successfull Deleted", 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    async addEmailDetails() {
        this.submitted = true;
        if (this.emailDetailsForm.valid) {
            if (this.editMode) {
                const updatedGroupData = {
                    emailId: this.editGroupData.emailId,
                    sourceName: this.emailDetailsForm.value.sourceName,
                    emailAddress: this.emailDetailsForm.value.emailAddress,
                    message: this.emailDetailsForm.value.message,
                    eventId: this.emailDetailsForm.value.eventId,
                };
                this.emailNotificationService.updateEmailDetails(updatedGroupData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.emailDetailsForm.reset();
                        //this.reset();
                        if (!this.searchkey) {
                            this.findAllEmail("");
                        } else {
                            this.searchBySourceName();
                        }
                        this.editMode = false;
                        this.dialogRef?.close();
                        this.toastr.success("Successful Updated", 'Success!');

                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                );
            } else {
                //  this.newGroupData = data;
                // this.getEventById(this.emailDetailsForm.value.eventId);
                const saveData = {
                    sourceName: this.emailDetailsForm.value.sourceName,
                    emailAddress: this.emailDetailsForm.value.emailAddress,
                    message: this.emailDetailsForm.value.message,
                    eventId: this.emailDetailsForm.value.eventId,
                };
                this.emailNotificationService.addEmailDetails(saveData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.emailDetailsForm.reset();
                        //this.reset();
                        if (!this.searchkey) {
                            this.findAllEmail("");
                        } else {
                            this.searchBySourceName();
                        }
                        this.dialogRef?.close();
                        this.toastr.success("Successfull Created", 'Success!');

                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                );
            }
        }
    }
    async sendEmailById(emailId) {
        -this.emailNotificationService.sendEmailById(emailId).subscribe(
            (response: any) => {
                this.reset();
                this.findAllEmail("");
                this.dialogRef?.close();
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                this.clearFormData();
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    editEmailById(emailId, index) {
        this.editMode = true;
        index = this.radiusUtility.getIndexOfSelectedRecord(index, this.currentPage, this.itemsPerPage);
        this.emailNotificationService.getFindEmailById(emailId).subscribe(
            (response: any) => {
                this.emailDetailsForm.patchValue({
                    sourceName: response.email?.sourceName ?? "Savbill BSS API GATEWAY",
                    emailAddress: response.email.emailAddress,
                    message: response.email.message,
                    eventId: response.email.event.eventId,
                });
                this.dialogRef = this.dialog.open(this.emailDialogTemplate, {
                    width: '800px',
                    height: 'auto',
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
        this.editGroupData = {
            emailId: emailId,
            sourceName: "",
            emailAddress: "",
            message: "",
            eventName: "",
        };
    }

    clearFormData() {
        this.editMode = false;
        this.emailDetailsForm.setValue({
            sourceName: "",
            emailAddress: "",
            message: "",
            eventId: "",
        });
    }
    deleteConfirm(emailId) {
        this.confirmationService.confirm({
            message: "Do you want to delete this record?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteEmailById(emailId);
            },
            reject: () => {
                this.toastr.info("You have rejected", 'Info!');

            },
        });
    }
    clearMessageAlert() {
        this.alert_success = false;
        this.alert_update_success = false;
        this.alert_delete_success = false;
        this.alert_send_success = false;
        this.alert_Active_success = false;
        this.alert_InActive_success = false;
        this.alert_error_message = false;
        this.alert_search_error_message = false;
    }
    createEmailNotification() {
        if (
            !this.loginService.hasOperationPermission(
                AclClassConstants.ACL_EMAIL,
                AclConstants.OPERATION_EMAIL_ADD,
                AclConstants.OPERATION_EMAIL_ALL
            )
        ) {
            this.toastr.error("Sorry you have not privilege to add operation!", 'Failed!');

        } else {
            this.submitted = false;
            this.editMode = false;
            this.emailDetailsForm.reset();
        }
    }
    clearSearchForm() {
        this.searchSubmitted = false;
        this.reset();
        this.currentPage = 1;
        this.searchForm.reset();
        this.findAllEmail("");
        this.emailDetailsForm.reset();
    }

    reset() {
        this.clearMessageAlert();
        this.clearFormData();
    }

    pageChanged(event: any) {
        this.itemsPerPage = event.pageSize;
        this.currentPage = event.pageIndex + 1;

        if (this.searchkey) {
            this.updatePagedList(); // Just update paged list from cached filtered data
        } else {
            this.findAllEmail(this.itemsPerPage); // Fetch new list when no search active
        }
    }



    canExit() {
        if (!this.emailDetailsForm.dirty) return true;
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
    openEmailDialog(): void {
        this.editMode = false;
        this.emailDetailsForm.reset();

        this.dialogRef = this.dialog.open(this.emailDialogTemplate, {
            width: '800px',
            height: 'auto',
        });
    }
    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }
    deleteConfirmonEmailDialog(email: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Email',
                description: `Are you sure you want to delete "${email.emailAddress}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === true) {
                this.deleteEmailById(email.emailId);
            }
        });
    }

}
