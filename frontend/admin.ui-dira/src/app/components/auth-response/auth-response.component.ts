import { status } from "./../../RadiusUtils/RadiusConstants";
import { Component, OnInit, ViewChild, TemplateRef } from "@angular/core";
import { AuthResponseService } from "src/app/service/auth-response.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { UntypedFormGroup, UntypedFormBuilder, Validators, FormControl, FormArray, FormGroup, FormBuilder } from "@angular/forms";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { DatePipe } from "@angular/common";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-auth-response",
    templateUrl: "./auth-response.component.html",
    styleUrls: ["./auth-response.component.css"],
    standalone: false
})
export class AuthResponseComponent implements OnInit {
    dataSource = new MatTableDataSource<any>();
    displayedColumns: string[] = ['id', 'userName', 'replyMessage', 'packetType', 'clientIp', 'clientGroup', 'eventTime'];

    searchAuthRespForm: UntypedFormGroup;
    searchSubmitted = false;
    searchAcctCdrData = {
        userName: ""
    };
    userName = "";
    groupData: any[] = [];
    filterdData: any[] = [];
    //Used and required for pagination
    totalRecords: number;
    currentPage = 0;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pageSizeOptions: number[] = [5,10,20,50,100];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    showItemPerPage: any;
    searchkey: string;
    //Used to store error data and error message
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    dialogRef: any;
    errorMsg = "";

    editMode: boolean = false;

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    deleteAuthAccess: any;

    constructor(
        private AuthResponseService: AuthResponseService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private datePipe: DatePipe,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.deleteAuthAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_AUTHEN_AUDIT_DELETE);
        this.findAllAuth("");
    }

    ngOnInit(): void {
        this.searchAuthRespForm = this.fb.group({
            username: [""],
            replymessage: [""],
            packettype: [""],
            clientip: [""],
            clientgroup: [""],
            fromDate: [""],
            toDate: [""]
        });
        if (this.deleteAuthAccess) {
            this.displayedColumns.push('action');
        }
    }

    // ngAfterViewInit() {
    //     this.dataSource.paginator = this.paginator;
    //     this.dataSource.sort = this.sort;
    // }

    findAllAuth(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage + 1;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.AuthResponseService.findAllAuthResponseData(page, size).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.message}`, 'Info!');
                    this.dataSource.data = [];
                    this.totalRecords = 0;
                } else {
                    this.groupData = response.authResponse.content;
                    this.totalRecords = response.authResponse.totalElements;
                    this.filterdData = this.groupData;
                    this.dataSource.data = this.groupData;
                }
                if (this.paginator) {
                    this.paginator.length = this.totalRecords;
                    this.paginator.pageIndex = this.currentPage;
                    this.paginator.pageSize = this.itemsPerPage;
                }
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    this.dataSource.data = [];
                    this.totalRecords = 0;
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    this.dataSource.data = [];
                    this.totalRecords = 0;
                }
            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.currentPage = 0;
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllAuth(this.showItemPerPage);
        } else {
            this.searchAuthResp();
        }
    }

    onPageChange(event: PageEvent) {
        this.currentPage = event.pageIndex;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.findAllAuth("");
        } else {
            // For search results, call search method to maintain search params
            this.AuthResponseService.findAllAuthResponseData(
                this.currentPage + 1,
                this.itemsPerPage,
                this.searchkey
            ).subscribe(
                (response: any) => {
                    this.groupData = response.authResponse.content;
                    this.totalRecords = response.authResponse.totalElements;
                    this.filterdData = this.groupData;
                    this.dataSource.data = this.groupData;
                },
                (error: any) => {
                    this.totalRecords = 0;
                    this.dataSource.data = [];
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                }
            );
        }
    }

    deleteConfirm(auth) {
        this.dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            disableClose: true,
            data: {
                title: "Delete Confirmation",
                description: `Are you sure you want to delete "${auth.userName}"?`,
                yesLabel: "Confirm",
                noLabel: "Cancel"
            }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteGroupById(auth.authresId);
                this.clearSearchForm();
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });

        // message: "Do you want to delete this record?",
        // header: "Delete Confirmation",
        // icon: "pi pi-info-circle",
        // accept: () => {
        //     this.deleteGroupById(authRespId);
        // },
        // reject: () => {
        //     this.messageService.add({
        //         severity: "info",
        //         summary: "Rejected",
        //         detail: "You have rejected"
        //     });
        // }
    }

    deleteGroupById(authRespId) {
        this.AuthResponseService.deleteAuthResponseById(authRespId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && this.groupData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllAuth("");
                } else {
                    this.searchAuthResp();
                }
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    searchAuthResp() {
        this.searchSubmitted = true;
        this.currentPage = 0;

        if (this.searchAuthRespForm.valid) {
            // Reset searchkey and build properly
            this.searchkey = "";

            Object.keys(this.searchAuthRespForm.value).forEach(key => {
                if (this.searchAuthRespForm.value[key] !== null && this.searchAuthRespForm.value[key] !== '') {
                    if (key === 'fromDate' || key === 'toDate') {
                        // Handle date formatting properly
                        if (this.searchAuthRespForm.value[key] instanceof Date) {
                            this.searchkey += `&${key}=${this.datePipe.transform(
                                this.searchAuthRespForm.value[key],
                                "yyyy-MM-dd"
                            )}`;
                        } else if (typeof this.searchAuthRespForm.value[key] === 'string') {
                            this.searchkey += `&${key}=${this.searchAuthRespForm.value[key]}`;
                        }
                    } else {
                        this.searchkey += `&${key}=${this.searchAuthRespForm.value[key]}`;
                    }
                }
            });

            this.filterdData = [];
            this.dataSource.data = [];

            // Reset paginator to first page BEFORE API call
            if (this.paginator) {
                this.paginator.pageIndex = 0;
            }

            this.AuthResponseService.findAllAuthResponseData(
                this.currentPage + 1,
                this.itemsPerPage,
                this.searchkey
            ).subscribe(
                (response: any) => {
                    this.groupData = response.authResponse.content;
                    this.totalRecords = response.authResponse.totalElements;
                    this.filterdData = this.groupData;
                    this.dataSource.data = this.groupData;

                    if (this.paginator) {
                        this.paginator.length = this.totalRecords;
                        this.paginator.pageIndex = this.currentPage;
                        this.paginator.pageSize = this.itemsPerPage;
                    }
                },
                (error: any) => {
                    if (error.error.status == 404) {
                        this.totalRecords = 0;
                        this.dataSource.data = [];
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.totalRecords = 0;
                        this.dataSource.data = [];
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                    // Reset paginator on error too
                    if (this.paginator) {
                        this.paginator.length = 0;
                        this.paginator.pageIndex = 0;
                    }
                }
            );
        }
    }

    clearSearchForm() {
        this.searchSubmitted = false;
        this.currentPage = 0;
        this.searchAuthRespForm.reset();

        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }

        this.findAllAuth("");
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber - 1;
        if (!this.searchkey) {
            this.findAllAuth("");
        } else {
            this.searchAuthResp();
        }
    }
}
