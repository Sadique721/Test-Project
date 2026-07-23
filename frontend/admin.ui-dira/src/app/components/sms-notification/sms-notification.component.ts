import { Component, OnInit, ViewChild, ElementRef, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, NgForm, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { SmsNotificationService } from "src/app/service/sms-notification.service";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { eventNames } from "process";
import { countries } from "src/app/components/model/country";
import { LoginService } from "src/app/service/login.service";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer } from "rxjs";
import { NOTIFICATIONS } from "src/app/constants/aclConstants";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { MaterialModule } from "src/app/material.module";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { map, startWith } from "rxjs/operators";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-sms-notification",
    templateUrl: "./sms-notification.component.html",
    styleUrls: ["./sms-notification.component.css"],
    standalone: false
})
export class SmsNotificationComponent implements OnInit {
    public loginService: LoginService;
    AclClassConstants;
    AclConstants;

    displayedColumns = ['id', 'eventType', 'mobileNo', 'eventTime', 'status', 'action'];

    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    // Keep dataSource for potential future use, but don't use it for server-side pagination
    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>([]);

    countries: any = countries;
    searchSubmitted = false;
    submitted = false;
    searchForm: UntypedFormGroup;
    smsDetailsForm: UntypedFormGroup;
    smsId: number;
    dialogRef: any;
    sourceName = "";
    statusMsg = "";

    newGroupData = {
        sourceName: "",
        countryCode: "",
        mobileNo: "",
        message: "",
        eventName: ""
    };

    editGroupData = {
        smsId: "",
        sourceName: "",
        countryCode: "",
        mobileNo: "",
        message: "",
        eventName: ""
    };

    searchGroupData = {
        sourceName: ""
    };

    changeStatusData: any = [];
    groupData: any = []; // This will hold the current page data
    sourceNameValue = [
        { label: "Savbill Radius" },
        { label: "Savbill Wifi" },
        { label: "Savbill Common" },
        { label: "Savbill BSS API GATEWAY" },
        { label: "Notification Schedular" },
        { label: "Sales Crms BSS API" }
    ];
    eventTypeValue: any = [];

    // Server-side pagination properties
    totalRecords: number = 0;
    currentPage = 0; // 0-based for Material paginator
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    errorData: any = [];
    errorMsg = "";

    @ViewChild("mobileNumberFocus") el: ElementRef;

    alert_success: boolean = false;
    alert_update_success: boolean = false;
    alert_delete_success: boolean = false;
    alert_send_success: boolean = false;
    alert_InActive_success: boolean = false;
    alert_Active_success: boolean = false;
    alert_error_message: boolean = false;
    alert_search_error_message: boolean = false;
    editMode: boolean = false;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: string;
    totalDataListLength = 0;
    inputMobile: string;

    createAccess: boolean = false;
    editAccess: boolean = false;
    deleteAccess: boolean = false;
    sendAccess: boolean = false;
    emailAccess: boolean = false;

    constructor(
        private smsNotificationService: SmsNotificationService,
        private radiusUtility: RadiusUtility,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private dialog: MatDialog,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS_CREATE);
        this.deleteAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS_DELETE);
        this.sendAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS_SEND);
        this.editAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS_EDIT);
        this.emailAccess = loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS);
        this.loginService = loginService;
        this.editMode = !this.createAccess && this.editAccess ? true : false;
        this.findAllSms("");
    }

    ngOnInit(): void {
        this.getEventType();
        this.commondropdownService.getsystemconfigList();

        this.smsDetailsForm = this.fb.group({
            sourceName: ["", Validators.required],
            countryCode: [this.commondropdownService.commonCountryCode],
            mobileNo: ["", Validators.required],
            message: ["", Validators.required],
            eventId: ["", Validators.required]
        });

        this.searchForm = this.fb.group({
            mobileNo: [""]
        });

        this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
            if (lengthObj) {
                this.smsDetailsForm
                    .get("mobileNo")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(lengthObj.min),
                        Validators.maxLength(lengthObj.max)
                    ]);
                this.smsDetailsForm.get("mobileNo")?.updateValueAndValidity();
            }
        });

        this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
            if (lengthObj) {
                this.searchForm
                    .get("mobileNo")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(lengthObj.min),
                        Validators.maxLength(lengthObj.max)
                    ]);
                this.searchForm.get("mobileNo")?.updateValueAndValidity();
            }
        });
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS_DELETE) || this.loginService.hasPermission(NOTIFICATIONS.NOTIFICATION_SMS_EDIT)) {
            return ['id', 'eventType', 'mobileNo', 'eventTime', 'status', 'action'];
        } else {
            return ['id', 'eventType', 'mobileNo', 'eventTime', 'status'];
        }
    }
    onPageChange(event: PageEvent): void {
        this.currentPage = event.pageIndex;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.findAllSms("");
        } else {
            this.searchBySourceName();
        }
    }

    async searchBySourceName() {
        this.clearMessageAlert();
        if (this.searchForm.value.sourceName == null) {
            this.searchForm.value.sourceName = "";
        }

        if (!this.searchkey || this.searchkey !== this.searchForm.value.mobileNo) {
            this.currentPage = 0;
        }
        this.searchkey = this.searchForm.value.mobileNo;

        this.searchSubmitted = true;
        let name = this.searchForm.value.mobileNo.trim() ? this.searchForm.value.mobileNo.trim() : "";

        if (this.searchForm.valid || name === "") {
            this.smsNotificationService.getSmsDataBySourceName(name).subscribe(
                (response: any) => {
                    this.reset();
                    // For search results, use MatTableDataSource for client-side pagination
                    this.dataSource.data = response.smsList || [];
                    this.totalRecords = this.dataSource.data.length;

                    // Connect paginator to dataSource for search results
                    this.dataSource.paginator = this.paginator;

                    // Set groupData to empty since we're using dataSource for search
                    this.groupData = [];

                    if (this.paginator) {
                        this.paginator.pageIndex = this.currentPage;
                        this.paginator.length = this.totalRecords;
                    }
                },
                (error: any) => {
                    this.handleError(error);
                }
            );
        }
    }


    async findAllSms(size) {
        this.searchkey = "";

        if (size) {
            this.itemsPerPage = size;
        }

        const apiPage = this.currentPage + 1;


        this.smsNotificationService.findAllSmsData(this.itemsPerPage, apiPage).subscribe(
            (response: any) => {
                if (response.message) {
                    this.toastr.info(`${response.message}`, 'Info!');
                } else {
                    this.groupData = response.smsList.data || [];
                    this.totalRecords = response.smsList.totalRecords || 0;

                }
            },
            (error: any) => {
                this.handleError(error);
            }
        );
    }

    handleError(error: any) {
        if (error.error.status == 404) {
            this.groupData = [];
            this.totalRecords = 0;
            this.toastr.info(`${error.error.message || "Data Not Found"}`, 'Info!');
        } else if (error.error.status == 400) {
            this.toastr.info("Data Not Found", 'Info!');
        } else {
            this.toastr.error(`${error.error.errorMessage || "An error occurred"}`, 'Failed!');
        }
    }

    TotalItemPerPage(event) {
        this.itemsPerPage = Number(event.value);
        this.currentPage = 0; // Reset to first page

        if (this.paginator) {
            this.paginator.pageSize = this.itemsPerPage;
            this.paginator.firstPage();
        }

        if (!this.searchkey) {
            this.findAllSms(this.showItemPerPage);
        } else {
            // this.searchBySourceName();
            this.dataSource.paginator = this.paginator;
        }
    }

    async deleteSmsById(smsId) {
        this.smsNotificationService.deleteSmsById(smsId).subscribe(
            (response: any) => {
                this.reset();
                // Check if we need to go to previous page
                if (this.currentPage > 0 && this.groupData.length === 1) {
                    this.currentPage = this.currentPage - 1;
                }

                if (!this.searchkey) {
                    this.findAllSms("");
                } else {
                    this.searchBySourceName();
                }
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.handleError(error);
            }
        );
    }

    clearSearchForm() {
        this.searchSubmitted = false;
        this.reset();
        this.currentPage = 0;
        this.searchkey = "";
        this.searchForm.reset();

        this.dataSource.data = [];
        this.dataSource.paginator = null;

        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }

        this.findAllSms("");
        this.smsDetailsForm.reset();
    }


    reset() {
        this.clearMessageAlert();
        this.clearFormData();
    }

    // Keep all other existing methods unchanged...
    async addSmsDetails() {
        this.submitted = true;
        if (this.smsDetailsForm.valid) {
            if (this.editMode) {
                const updatedGroupData = {
                    smsId: this.editGroupData.smsId,
                    sourceName: this.smsDetailsForm.value.sourceName,
                    countryCode: this.smsDetailsForm.value.countryCode,
                    mobileNo: this.smsDetailsForm.value.mobileNo,
                    message: this.smsDetailsForm.value.message,
                    eventId: this.smsDetailsForm.value.eventId
                };
                this.smsNotificationService.updateSmsDetails(updatedGroupData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.smsDetailsForm.reset();
                        if (!this.searchkey) {
                            this.findAllSms("");
                        } else {
                            this.searchBySourceName();
                        }
                        this.editMode = false;
                        this.dialogRef.close(response);
                        this.toastr.success(`${response.message}`, 'Success!');
                        this.clearFormData();
                    },
                    (error: any) => {
                        this.handleError(error);
                    }
                )
            } else {
                const saveData = {
                    sourceName: this.smsDetailsForm.value.sourceName,
                    countryCode: this.smsDetailsForm.value.countryCode,
                    mobileNo: this.smsDetailsForm.value.mobileNo,
                    message: this.smsDetailsForm.value.message,
                    eventId: this.smsDetailsForm.value.eventId
                };
                this.smsNotificationService.addSmsDetails(saveData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.smsDetailsForm.reset();
                        if (!this.searchkey) {
                            this.findAllSms("");
                        } else {
                            this.searchBySourceName();
                        }
                        this.dialogRef.close(response);
                        this.toastr.success(`${response.message}`, 'Success!');
                        this.clearFormData();
                    },
                    (error: any) => {
                        this.handleError(error);
                    }
                );
            }
        }
    }

    async sendSmsById(smsId) {
        this.smsNotificationService.sendSmsById(smsId).subscribe(
            (response: any) => {
                this.reset();
                this.findAllSms("");
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.handleError(error);
            }
        );
    }

    getEventType() {
        this.smsNotificationService.getEvents().subscribe(
            (response: any) => {
                this.eventTypeValue = response;
            },
            error => {
                this.handleError(error);
            }
        );
    }

    editSmsById(smsId, index) {
        this.editMode = true;

        this.smsDetailsForm.patchValue({
            sourceName: this.groupData[index].sourceName,
            countryCode: this.groupData[index].countryCode,
            mobileNo: this.groupData[index].mobileNo,
            message: this.groupData[index].message,
            eventId: this.groupData[index].eventId
        });

        this.editGroupData = {
            smsId: this.groupData[index].smsId,
            sourceName: "",
            countryCode: "",
            mobileNo: "",
            message: "",
            eventName: ""
        };
        this.opensmsDialog(smsId);
    }

    clearFormData() {
        this.editMode = false;
        this.smsDetailsForm.setValue({
            sourceName: "",
            countryCode: "",
            mobileNo: "",
            message: "",
            eventId: ""
        });
    }

    deleteConfirm(sms) {

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: "Delete State",
                description: `Are you sure you want to delete ?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteSmsById(sms.smsId);
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
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

    canExit() {
        if (!this.smsDetailsForm.dirty) return true;
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

    onKeymobilelength(event) {
        const str = this.smsDetailsForm.value.mobileNo.toLocaleString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobile = `${mobilenumberlength} character required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobile = "";
        } else {
            this.inputMobile = `${mobilenumberlength} character required.`;
        }
    }

    popoverTitle: string = RadiusConstants.CONFIRM_DIALOG_TITLE;
    popoverMessage: string = RadiusConstants.DELETE_GROUP_CONFIRM_MESSAGE;
    confirmedClicked: boolean = false;
    cancelClicked: boolean = false;
    closeOnOutsideClick: boolean = true;
    message: string;

    onInput(event: any) {
        const pattern = /^[0-9]+$/;
        let inputValue = event.target.value;
        let mobilenumberlength = this.commondropdownService.commonMoNumberLength;

        inputValue = inputValue.replace(/[^0-9]/g, "");
        inputValue = inputValue.slice(0, mobilenumberlength);

        if (event.target.value.length <= mobilenumberlength) {
            event.target.value = inputValue;
        }

        const mobileNo = inputValue;
    }

    @ViewChild("smsDialog") smsDialog!: TemplateRef<any>;
    opensmsDialog(area: any): void {
        this.dialogRef = this.dialog.open(this.smsDialog, {
            width: "800px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result) {
            }
            this.submitted = false;
            this.editMode = false;
            this.clearFormData()
        });
    }
}
