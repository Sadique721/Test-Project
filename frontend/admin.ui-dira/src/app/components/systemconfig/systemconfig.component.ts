import { Component, OnInit, Input } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { Regex } from "src/app/constants/regex";
import { SystemConfig } from "src/app/components/model/systemcofig";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import * as _ from "lodash";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Data } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { SETTINGS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TemplateRef } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-systemconfig",
    templateUrl: "./systemconfig.component.html",
    styleUrls: ["./systemconfig.component.css"],
    standalone: false
})
export class SystemconfigComponent implements OnInit {
    @ViewChild('systemDialogTemplate') systemDialogTemplate: TemplateRef<any>;
    dialogRef: MatDialogRef<any>;
    editMode: boolean = false;

    displayedColumns: string[] = ['ID', 'Name', 'Value', 'Action']
    @ViewChild(MatPaginator) paginator: MatPaginator;
    systemconfigListDataSource: MatTableDataSource<any>;

    systemconfigListdatatotalRecords: number = 0;

    @Input() systemname: string;
    systemconfigGroupForm: UntypedFormGroup;
    systemconfigCategoryList: any;
    submitted: boolean = false;
    taxListData: any;
    createsystemconfigData: SystemConfig;
    currentPagesystemconfigListdata = 1;
    systemconfigListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    systemconfigListData: any = [];
    viewsystemconfigListData: any = [];
    issystemconfigEdit: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    searchSysConfigName: any = "";
    AclClassConstants;
    AclConstants;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: string;
    totalDataListLength = 0;
    public loginService: LoginService;
    constructor(
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private toastr: ToastrService,
        private systemconfigService: SystemconfigService,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(SETTINGS.SYSTEM_CONFIGURATION_CREATE);
        this.editAccess = loginService.hasPermission(SETTINGS.SYSTEM_CONFIGURATION_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.issystemconfigEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.systemconfigGroupForm = this.fb.group({
            id: [""],
            name: ["", Validators.required],
            value: ["", Validators.required],
        });

        this.getsystemconfigList("");
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(SETTINGS.SYSTEM_CONFIGURATION_EDIT)) {
            return ['ID', 'Name', 'Value', 'Action'];
        } else {
            return ['ID', 'Name', 'Value'];
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.currentPagesystemconfigListdata = 1;

        if (!this.searchkey || this.searchkey === "") {
            this.getsystemconfigList(this.showItemPerPage);
        } else {
            this.searchSysConfig();
        }
    }


    getsystemconfigList(size) {
        this.searchkey = "";

        if (size) {
            this.systemconfigListdataitemsPerPage = size;
        } else {
            this.systemconfigListdataitemsPerPage = this.showItemPerPage === 1 ? this.pageITEM : this.showItemPerPage;
        }

        const url = "/system/configuration/";
        this.systemconfigService.getMethod(url).subscribe(
            (response: any) => {
                this.systemconfigListData = response.clientlist || [];


                const paytmlinksms_Data = this.systemconfigListData.filter(data => data.name === "paytmlinksms_enable");
                this.commondropdownService.ifPaytmLinkSendBtn = paytmlinksms_Data.length > 0 &&
                    (paytmlinksms_Data[0].value === true || paytmlinksms_Data[0].value === "true");

                this.systemconfigListdatatotalRecords = this.systemconfigListData.length;

                const maxPages = Math.ceil(this.systemconfigListdatatotalRecords / this.systemconfigListdataitemsPerPage);
                if (!this.currentPagesystemconfigListdata || this.currentPagesystemconfigListdata < 1) {
                    this.currentPagesystemconfigListdata = 1;
                } else if (this.currentPagesystemconfigListdata > maxPages && maxPages > 0) {
                    this.currentPagesystemconfigListdata = maxPages;
                }

                const startIndex = (this.currentPagesystemconfigListdata - 1) * this.systemconfigListdataitemsPerPage;
                const endIndex = startIndex + this.systemconfigListdataitemsPerPage;
                this.systemconfigListData = this.systemconfigListData.slice(startIndex, endIndex);
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || "Something went wrong"}`, 'Failed!');

            }
        );
    }


    addEditsystemconfig(systemconfigId) {
        this.submitted = true;

        if (this.systemconfigGroupForm.valid) {
            if (systemconfigId) {
                const url = "/system/configuration/" + systemconfigId;
                this.createsystemconfigData = this.systemconfigGroupForm.value;
                this.systemconfigService.updateMethod(url, this.createsystemconfigData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.systemconfigGroupForm.reset();
                        this.issystemconfigEdit = false;
                        this.viewsystemconfigListData = [];
                        this.systemconfigGroupForm.controls.name.enable();
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.toastr.success("Successfull Updated", 'Success!');

                        if (!this.searchkey) {
                            this.getsystemconfigList("");
                        } else {
                            this.searchSysConfig();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                const url = "/system/configuration/";
                this.createsystemconfigData = this.systemconfigGroupForm.value;

                this.systemconfigService.postMethod(url, this.createsystemconfigData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.systemconfigGroupForm.reset();
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.toastr.success("Successfull Created", 'Success!');

                        if (!this.searchkey) {
                            this.getsystemconfigList("");
                        } else {
                            this.searchSysConfig();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            }
        }
    }

    editsystemconfig(systemconfigData: any) {
        if (systemconfigData) {
            this.viewsystemconfigListData = systemconfigData;
            this.issystemconfigEdit = true;
            this.editMode = true;
            this.systemconfigGroupForm.patchValue(systemconfigData);
            this.systemconfigGroupForm.controls.name.disable();
            this.dialogRef = this.dialog.open(this.systemDialogTemplate, {
                width: '800px'
            });

        }
    }

    searchSysConfig() {
        if (!this.searchkey || this.searchkey !== this.searchSysConfigName) {
            this.currentPagesystemconfigListdata = 1;
        }
        this.searchkey = this.searchSysConfigName ? this.searchSysConfigName.trim() : '';

        if (this.showItemPerPage === 1) {
            this.systemconfigListdataitemsPerPage = this.pageITEM;
        } else {
            this.systemconfigListdataitemsPerPage = this.showItemPerPage;
        }

        const url = this.searchkey ? `/system/configuration/searchConfigurationByName?name=${this.searchkey}` : "/system/configuration/";

        this.systemconfigService.getMethod(url).subscribe(
            (response: any) => {
                const allData = response.clientlist || [];

                this.filteredSearchResults = allData.filter(data =>
                    data.name.toLowerCase().includes(this.searchkey.toLowerCase())
                );

                this.systemconfigListdatatotalRecords = this.filteredSearchResults.length;

                const maxPages = Math.ceil(this.systemconfigListdatatotalRecords / this.systemconfigListdataitemsPerPage);
                if (this.currentPagesystemconfigListdata > maxPages && maxPages > 0) {
                    this.currentPagesystemconfigListdata = maxPages;
                }
                if (this.currentPagesystemconfigListdata < 1) {
                    this.currentPagesystemconfigListdata = 1;
                }

                this.updatePagedList();

                const paytmlinksms_Data = allData.filter(data => data.name === "paytmlinksms_enable");
                this.commondropdownService.ifPaytmLinkSendBtn = paytmlinksms_Data.length > 0 &&
                    (paytmlinksms_Data[0].value === true || paytmlinksms_Data[0].value === "true");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }


    updatePagedList() {
        const startIndex = (this.currentPagesystemconfigListdata - 1) * this.systemconfigListdataitemsPerPage;
        const endIndex = startIndex + this.systemconfigListdataitemsPerPage;
        this.systemconfigListData = this.filteredSearchResults.slice(startIndex, endIndex);
    }

    clearSysConfig() {
        this.searchSysConfigName = "";
        this.getsystemconfigList("");
        this.submitted = false;
        this.systemconfigGroupForm.reset();
        this.issystemconfigEdit = false;
        this.systemconfigGroupForm.controls.name.setValue("");
        this.systemconfigGroupForm.controls.value.setValue("");
    }

    filteredSearchResults: any[] = [];

    pageChangedsystemconfigList(event: any) {
        this.currentPagesystemconfigListdata = event.pageIndex + 1;
        this.systemconfigListdataitemsPerPage = event.pageSize;

        if (!this.searchkey || this.searchkey === '') {
            this.getsystemconfigList(this.systemconfigListdataitemsPerPage);
        } else {
            this.updatePagedList();
        }
    }

    canExit() {
        if (!this.systemconfigGroupForm.dirty) return true;
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
    openSystemDialog(edit: boolean = false, systemData: any = null) {
        this.editMode = edit;

        if (edit && systemData) {
            this.systemconfigGroupForm.patchValue(systemData);
            this.systemconfigGroupForm.controls.name.disable();
        } else {
            this.systemconfigGroupForm.reset();
            this.systemconfigGroupForm.controls.name.enable();
            this.editMode = false;
            this.issystemconfigEdit = false;
        }

        this.dialogRef = this.dialog.open(this.systemDialogTemplate, {
            width: '800px'
        });
    }
    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

}
