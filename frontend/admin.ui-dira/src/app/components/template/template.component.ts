import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, UntypedFormArray } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { ClientGroupService } from "src/app/service/client-group.service";
import { SmsNotificationService } from "src/app/service/sms-notification.service";
import { TemplateService } from "src/app/service/template.service";
import { eventNames } from "process";
import { MatTableDataSource } from "@angular/material/table";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { SETTINGS } from "src/app/constants/aclConstants";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { MatCheckboxChange } from '@angular/material/checkbox';
import { Observable } from 'rxjs';
import { ToastrService } from "ngx-toastr";
@Component({
    selector: "app-template",
    templateUrl: "./template.component.html",
    styleUrls: ["./template.component.css"],
    standalone: false
})
export class TemplateComponent implements OnInit {
    changeStatusData: any = [];
    groupData: any = [];
    displayedColumns = [
        "templateName",
        "appendUrl",
        "smsEventConfigured",
        "smsTemplateData",
        "emailEventConfigured",
        "emailTemplateData",
        "action",
    ];
    dataSource = new MatTableDataSource<any>([]);
    updateTemplateData: any = [];
    createGroupForm: UntypedFormGroup;
    saveTemplateForm: UntypedFormGroup;
    submitted = false;
    searchSubmitted = false;
    totalRecords: String;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    smsTemplateData: string;
    emailTemplateData: string;
    attribute: UntypedFormArray;
    editFormValues: any;
    editAttributeValues: any = [];

    editClientGroupId: number;
    editMode: boolean = false;
    smsChecked: boolean = false;
    emailChecked: boolean = false;
    status = [{ label: "Active" }, { label: "Inactive" }];
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    allIEmailDs = [];
    isEmailChecked: boolean = false;
    allEmailChecked: boolean = false;

    allIDs = [];
    issmsChecked: boolean = false;
    saveAccess: boolean = false;
    allIsChecked: boolean = false;
    temeplatedata: any = [];
    Wifitemeplatedata: any = [];
    loggedInUser: string;
    groupID: any;
    groupEmailID: any;
    groupAppednURL: any;
    SearchName = "";
    searchKey: string = "";
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    viewAccess: any;
    createAccess: any;
    editAccess: any;
    deleteAccess: any;
    constructor(
        private messageService: MessageService,
        private clientGroupService: ClientGroupService,
        private TemplateService: TemplateService,
        private radiusUtility: RadiusUtility,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService, private toastr: ToastrService,
        private confirmationService: ConfirmationService,
        private smsNotificationService: SmsNotificationService,
        loginService: LoginService
    ) {
        this.saveAccess = loginService.hasPermission(SETTINGS.TEMPLATE_SAVE);
        this.loginService = loginService;
        this.findAll();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    ngOnInit(): void {
        this.saveTemplateForm = this.fb.group({
            templateId: [""],
            smsEventConfigured: [""],
            smsTemplateData: [""],
            emailEventConfigured: [""],
            emailTemplateData: [""],
            templateName: [""],
        });
        this.createGroupForm = this.fb.group({
            smsTemplate: ["", Validators.required],
            emailTemplate: ["", Validators.required],
        });

        this.loggedInUser = localStorage.getItem("loggedInUser");
    }
    popoverTitle: string = RadiusConstants.CONFIRM_DIALOG_TITLE;
    popoverMessage: string = RadiusConstants.DELETE_GROUP_CONFIRM_MESSAGE;
    confirmedClicked: boolean = false;
    cancelClicked: boolean = false;
    closeOnOutsideClick: boolean = true;

    async searchGroupByName(): Promise<void> {
        this.searchSubmitted = true;
        const trimmedName = this.SearchName.trim();
        this.searchKey = trimmedName;

        this.TemplateService.getEventByName(trimmedName).subscribe(
            (response: any) => {
                if (response.responseCode === 404) {
                    this.temeplatedata = response.dataList || [];
                    this.dataSource.data = this.temeplatedata;
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.temeplatedata = response.templateList || [];
                    this.dataSource.data = this.temeplatedata;
                    this.refreshCheckboxStates();
                }
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;

                // Reset to first page after search
                if (this.paginator) {
                    this.paginator.firstPage();
                }
            },
            (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            },
        );
    }

    async findAll() {
        this.searchKey = "";

        this.attribute = this.fb.array([]);
        this.TemplateService.grtTemplate().subscribe(
            (response: any) => {
                this.groupData = response;
                this.temeplatedata = response.templateList;

                if (this.temeplatedata != null && this.temeplatedata.length > 0) {
                    this.dataSource.data = this.temeplatedata; // Update data only
                    this.dataSource.paginator = this.paginator; // Reassign paginator
                    this.dataSource.sort = this.sort;
                    let attributeList = this.groupData.templateList;
                    attributeList.forEach(element => {
                        this.attribute.push(this.fb.group(element));
                    });

                    for (let index = 0; index < this.groupData.templateList.length; index++) {
                        if (!this.groupData.templateList[index]) {
                            this.saveTemplateForm.patchValue({
                                templateId: this.groupData.templateList[index].templateId,
                                templateName: this.groupData.templateList[index].templateName,
                                smsEventConfigured: false,
                                smsTemplateData: "",
                                emailEventConfigured: false,
                                emailTemplateData: "",
                            });
                        }
                        this.groupData.templateList[index].templateId =
                            this.groupData.templateList[index].templateId;
                    }
                    this.allIDs = [];
                    this.allIEmailDs = [];
                    this.totalRecords = this.groupData.templateList.length;

                    for (let i = 0; i < this.temeplatedata.length; i++) {
                        if (this.temeplatedata[i].smsEventConfigured == true) {
                            this.allIDs.push(this.temeplatedata[i].templateId);

                            if (this.temeplatedata.length == this.allIDs.length) {
                                this.issmsChecked = true;
                                this.allIsChecked = true;
                            }
                        }
                        if (this.temeplatedata[i].emailEventConfigured == true) {
                            this.allIEmailDs.push(this.temeplatedata[i].templateId);
                            if (this.temeplatedata.length === this.allIEmailDs.length) {
                                this.isEmailChecked = true;
                                this.allEmailChecked = true;
                            }
                        }
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    async updateTemplate() {
        let addTemplateData = [];
        for (let index = 0; index < this.groupData.dataList.length; index++) {
            addTemplateData.push({
                eventId: this.groupData.dataList[index].templateId,
                smsEventConfigured: this.groupData.dataList[index].smsEventConfigured,
                smsTemplateData: this.groupData.dataList[index].smsTemplateData,
                emailEventConfigured: this.groupData.dataList[index].emailEventConfigured,
                emailTemplateData: this.groupData.dataList[index].emailTemplateData,
                appendUrl: this.groupData.dataList[index].appendUrl,
                status: this.groupData.dataList[index].status,
                templateName: this.groupData.dataList[index].templateName,
                mvnoId: this.groupData.dataList[index].mvnoId,
                buId: this.groupData.dataList[index].buId,
            });
        }

        this.groupAppednURL = "";
        this.groupID = "";
        this.groupEmailID = "";
        if (addTemplateData.length != 0) {
            this.TemplateService.updateTemplate(addTemplateData).subscribe(
                (response: any) => {
                    if (this.searchKey) {
                        this.searchGroupByName();
                    } else {
                        this.findAll();
                    }
                    this.toastr.success(`${response.message}`, "Successfully ");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    clearSearchForm() {
        this.searchSubmitted = false;
        this.SearchName = "";
        if (this.paginator) {
            this.paginator.firstPage();
        }
        this.findAll();
    }

    refreshCheckboxStates(): void {
        this.allIDs = this.temeplatedata.filter(d => d.smsEventConfigured).map(d => d.templateId);
        this.allIEmailDs = this.temeplatedata.filter(d => d.emailEventConfigured).map(d => d.templateId);

        this.issmsChecked = this.temeplatedata.length > 0 && this.temeplatedata.every(i => i.smsEventConfigured);
        this.allIsChecked = this.issmsChecked;

        this.isEmailChecked = this.temeplatedata.length > 0 && this.temeplatedata.every(i => i.emailEventConfigured);
        this.allEmailChecked = this.isEmailChecked;
    }

    checkAllEmail(event: MatCheckboxChange): void {
        const checked = event.checked;
        this.temeplatedata.forEach(i => i.emailEventConfigured = checked);
        this.allIEmailDs = checked ? this.temeplatedata.map(i => i.templateId) : [];
        this.isEmailChecked = checked;
        this.allEmailChecked = checked;
        this.dataSource.data = [...this.temeplatedata];
        this.dataSource.paginator = this.paginator;
    }

    addEmailChecked(id: number, event: MatCheckboxChange): void {
        const checked = event.checked;
        const index = this.allIEmailDs.indexOf(id);
        if (checked && index === -1) {
            this.allIEmailDs.push(id);
        } else if (!checked && index !== -1) {
            this.allIEmailDs.splice(index, 1);
            const item = this.temeplatedata.find(x => x.templateId === id);
            if (item) item.emailEventConfigured = false;
        }
        this.isEmailChecked = this.temeplatedata.length > 0 && this.temeplatedata.every(x => x.emailEventConfigured);
        this.allEmailChecked = this.isEmailChecked;
    }

    checkSMSAll(event: MatCheckboxChange): void {
        const checked = event.checked;
        this.temeplatedata.forEach(i => i.smsEventConfigured = checked);
        this.allIDs = checked ? this.temeplatedata.map(i => i.templateId) : [];
        this.issmsChecked = checked;
        this.allIsChecked = checked;
        this.dataSource.data = [...this.temeplatedata];
        this.dataSource.paginator = this.paginator;
    }

    addsmsChecked(id: number, event: MatCheckboxChange): void {
        const checked = event.checked;
        const index = this.allIDs.indexOf(id);
        if (checked && index === -1) {
            this.allIDs.push(id);
        } else if (!checked && index !== -1) {
            this.allIDs.splice(index, 1);
            const item = this.temeplatedata.find(x => x.templateId === id);
            if (item) item.smsEventConfigured = false;
        }
        this.issmsChecked = this.temeplatedata.length > 0 && this.temeplatedata.every(x => x.smsEventConfigured);
        this.allIsChecked = this.issmsChecked;
    }

    ifsmsTemplateData = true;
    smsTemplateDataEdit(id) {
        this.groupID = id;
        this.groupEmailID = "";
        this.groupAppednURL = "";
    }
    EmailTemplateDataEdit(id) {
        this.groupEmailID = id;
        this.groupAppednURL = "";
        this.groupID = "";
    }
    appendUrlTemplateDataEdit(id) {
        this.groupAppednURL = id;
        this.groupEmailID = "";
        this.groupID = "";
    }

    async editTemplate(groupData) {
        let addTemplateData = [];
        groupData.templateId = groupData.templateId;
        addTemplateData.push({
            eventId: groupData.templateId,
            templateId: groupData.templateId,
            smsEventConfigured: groupData.smsEventConfigured,
            smsTemplateData: groupData.smsTemplateData,
            emailEventConfigured: groupData.emailEventConfigured,
            emailTemplateData: groupData.emailTemplateData,
            appendUrl: groupData.appendUrl,
            status: groupData.status,
            templateName: groupData.templateName,
            mvnoId: groupData.mvnoId,
            buId: groupData.buId,
        });

        this.groupAppednURL = "";
        this.groupID = "";
        this.groupEmailID = "";

        if (addTemplateData.length != 0) {
            this.TemplateService.updateTemplate(groupData).subscribe(
                (response: any) => {
                    if (this.searchKey) {
                        this.searchGroupByName();
                    } else {
                        this.findAll();
                    }
                    this.toastr.success(`${response.message}`, "Successfully ");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }
    canExit(): Observable<boolean> | boolean {
        if (!this.saveTemplateForm.dirty) {
            return true;
        }

        return new Observable<boolean>(observer => {
            this.confirmationService.confirm({
                message: 'You have unsaved changes. Do you really want to leave?',
                header: 'Confirm Exit',
                icon: 'pi pi-exclamation-triangle',
                accept: () => {
                    observer.next(true);
                    observer.complete();
                },
                reject: () => {
                    observer.next(false);
                    observer.complete();
                }
            });
        });
    }
}
