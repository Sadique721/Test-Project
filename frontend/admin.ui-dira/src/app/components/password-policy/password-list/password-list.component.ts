import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { PasswordPolicyService } from "src/app/service/password-policy/password-policy.service";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PasswordCreateComponent } from "../password-create/password-create.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-password-list",
    templateUrl: "./password-list.component.html",
    styleUrls: ["./password-list.component.css"],
    standalone: false
})
export class PasswordListComponent implements OnInit {
    @ViewChild('passwordPolicyDialog') passwordPolicyDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    displayedColumns: string[] = [
        'name', 'pattern', 'expiration_days', 'disable_recycling_prevention',
        'disable_account_lockout', 'min_length', 'max_length', 'status', 'action'
    ];

    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;
    passwordListData: any;
    passwordData: any;
    searchName: any;
    searchData: any;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    passwordPolicyForm: any;
    editMode: boolean;
    editPasswordId: string = ''

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private PasswordPolicyService: PasswordPolicyService
    ) { }

    async ngOnInit() {
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
        this.getPasswordPolicyData("");
    }

    clearMvno() {
        this.searchName = "";
        this.getPasswordPolicyData("");
    }

    pageChangedList(event: any) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.searchPasswordPolicy();
        } else {
            this.getPasswordPolicyData("");
        }
    }


    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getPasswordPolicyData(this.showItemPerPage);
        } else {
            this.searchPasswordPolicy();
        }
    }

    searchPasswordPolicy() {
        if (!this.searchkey || this.searchkey != this.searchName) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchName
            ? this.searchName.trim()
            : "";
        this.searchData.page = this.currentPage;
        this.searchData.pageSize = this.itemsPerPage;
        const url = "/passwordPolicy/search";
        this.PasswordPolicyService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.statusCode == 204) {
                    this.toastr.info(`${response.msg}`, 'Info!');

                    this.passwordListData = [];
                    this.totalRecords = 0;
                } else {
                    this.passwordListData = response.passwordList;
                    this.totalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getPasswordPolicyData(list) {
        let size;
        this.searchkey = "";
        let pageList = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        const url = "/passwordPolicy/getAllWithPagination";
        let params = {
            page: pageList,
            pageSize: size,
        };
        this.PasswordPolicyService.postMethod(url, params).subscribe(
            (response: any) => {
                this.passwordListData = response.passwordList;
                this.totalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    deleteConfirmonMvno(passwordData) {
        if (passwordData) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Password Policy?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteMvno(passwordData);
                },
                reject: () => {
                    this.toastr.info("You have rejected", 'info!');

                },
            });
        }
    }

    deleteMvno(id) {
        const url = "/passwordPolicy/delete/" + id;
        this.PasswordPolicyService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && this.passwordListData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                this.toastr.success("Successfull Deleted", 'Success!');

                if (this.searchkey) {
                    this.searchPasswordPolicy();
                } else {
                    this.getPasswordPolicyData("");
                }
            },
            (error: any) => {
                // console.log(error, "error")
                if (error.error.status == 417) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }
    openPasswordPolicyDialog(edit: boolean = false, policyData: any = null) {
        this.dialogRef = this.dialog.open(PasswordCreateComponent, {
            width: '1200px',
            data: { edit: false }
        });

        this.dialogRef.afterClosed().subscribe(result => {

            if (result === 'refresh') {
                this.getPasswordPolicyData("");
            }
        });
    }

    editPasswordPolicy(passwordId: string) {
        this.dialogRef = this.dialog.open(PasswordCreateComponent, {
            width: '1200px',
            data: { edit: true, passwordId: passwordId }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result === 'refresh') {
                this.getPasswordPolicyData("");
            }
        });
    }

}
