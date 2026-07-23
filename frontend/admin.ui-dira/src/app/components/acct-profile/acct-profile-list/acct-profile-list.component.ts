import { DatePipe } from "@angular/common";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { LoginService } from "src/app/service/login.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { AcctProfileService } from "src/app/service/radius-profile.service";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { AcctProfileCreateComponent } from '../acct-profile-create/acct-profile-create.component';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
import { ToastrService } from 'ngx-toastr';
declare var $: any;

@Component({
    selector: "app-acct-profile-list",
    templateUrl: "./acct-profile-list.component.html",
    styleUrls: ["./acct-profile-list.component.scss"],
    standalone: false
})
export class AcctProfileListComponent implements OnInit {
    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild('radiusProfileDetailDialog') radiusProfileDetailDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;

    // dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['id', 'name', 'status', 'action'];
    searchkey: string;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    profileData: any = [];
    // totalRecords: number;
    createAccess: any;
    editAccess: any;
    deleteAccess: any;
    loggedInUser: any;
    radiusProfileDetail: any = [];
    proxyServerName: string = "-";
    coaDMProfileName: string = "-";
    mappingMasterName: string = "-";
    checkItem: string = "-";
    isProfileDetailsModelVisible: boolean = false;
    modalToggle: boolean = true;
    searchSubmitted = false;
    searchProfileForm: UntypedFormGroup;
    showItemPerPage: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;


    totalRecords = 0;

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        public commondropdownService: CommondropdownService,
        public datepipe: DatePipe,
        public loginService: LoginService,
        public invoicePaymentListService: InvoicePaymentListService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private acctProfileService: AcctProfileService,
        private fb: UntypedFormBuilder
    ) {
        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_EDIT);
    }

getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_DELETE)) {
            return ['id', 'name', 'status', 'action'];
        } else {
            return ['id', 'name', 'status'];
        }
    }

    async ngOnInit() {
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.searchProfileForm = this.fb.group({
            name: [""]
        });
        this.findAllAcctProfile("");
    }
    onCancel(): void {
        this.dialogRef.close();
    }

    async findAllAcctProfile(list) {
        this.searchkey = "";
        let size = list || this.itemsPerPage;
        let page = this.currentPage;
        this.profileData = [];

        this.acctProfileService.findAllAcctProfile(page, size).subscribe(
            (response: any) => {

                this.profileData = response.radiusProfileList;
                this.totalRecords = response.totalRecords || this.profileData.length;


                this.dataSource = new MatTableDataSource(this.profileData);


                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            error => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            }
        );
    }

    async getRadiusProfileDetail(radiusProfileId, selectedMvnoId) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.isProfileDetailsModelVisible = true;
            this.acctProfileService.getProfileById(radiusProfileId).subscribe(
                (response: any) => {
                    this.radiusProfileDetail = response.radiusProfile;
                    if (this.radiusProfileDetail.proxyServer != null) {
                        this.proxyServerName = this.radiusProfileDetail.proxyServer.name;
                    } else {
                        this.proxyServerName = "-";
                    }
                    if (this.radiusProfileDetail.checkItem != null) {
                        this.checkItem = this.radiusProfileDetail.checkItem;
                    } else {
                        this.checkItem = "-";
                    }
                    if (this.radiusProfileDetail.coaDMProfile != null) {
                        this.coaDMProfileName = this.radiusProfileDetail.coaDMProfile.name;
                    } else {
                        this.coaDMProfileName = "-";
                    }
                    if (this.radiusProfileDetail.mappingMaster != null) {
                        this.mappingMasterName = this.radiusProfileDetail.mappingMaster.name;
                    } else {
                        this.mappingMasterName = "-";
                    }
                    this.dialog.open(this.radiusProfileDetailDialog, {
                        width: '1000px',
                        disableClose: true
                    });
                },
                error => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
        }
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        let userId = localStorage.getItem("userId");
        if (userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            this.toastr.info("You are not authorized to do this operation. Please contact to the administrator", 'Info!');

            return false;
        }
        return true;
    }

    closeProfileDetailsModel() {
        this.isProfileDetailsModelVisible = false;
    }
    async changeStatus(name: string, status: string, selectedMvnoId: any, event: any) {
        this.modalToggle = true;


        const newStatus = event.checked ? "Active" : "Inactive";

        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.acctProfileService.changeSatus(name, newStatus, selectedMvnoId).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.message}`, 'Success!');


                    if (!this.searchkey) {
                        this.findAllAcctProfile("");
                    } else {
                        this.searchProfileByName();
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
        }
    }
    async searchProfileByName(event?: Event | KeyboardEvent) {
        if (event && event.preventDefault) {
            event.preventDefault();
        }

        this.searchSubmitted = true;
        const name = this.searchProfileForm.value.name?.trim() || "";


        if (!this.searchkey || this.searchkey !== name) {
            this.currentPage = 1;
        }
        this.searchkey = name;

        this.itemsPerPage = this.showItemPerPage || RadiusConstants.ITEMS_PER_PAGE;

        if (!name) {
            this.findAllAcctProfile(this.itemsPerPage);
            return;
        }

        this.acctProfileService.findByName(name).subscribe(
            (response: any) => {
                this.profileData = response.radiusProfileList;
                this.dataSource.data = this.profileData;

                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
            },
            error => {
                this.totalRecords = 0;
                this.profileData = [];
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            }
        );
    }


    // async searchProfileByName() {

    //     this.searchSubmitted = true;
    //     if (this.searchProfileForm.value.name == null) {
    //         this.searchProfileForm.value.name = "";
    //     }
    //     if (!this.searchkey || this.searchkey !== this.searchProfileForm.value.name) {
    //         this.currentPage = 1;
    //     }
    //     this.searchkey = this.searchProfileForm.value.name;
    //     if (this.showItemPerPage) {
    //         this.itemsPerPage = this.showItemPerPage;
    //     }
    //     let name = this.searchProfileForm.value.name.trim()
    //         ? this.searchProfileForm.value.name.trim()
    //         : "";
    //     if (this.searchProfileForm.valid) {
    //         this.profileData = [];
    //         this.acctProfileService.findByName(name).subscribe(
    //             (response: any) => {
    //                 this.profileData = response.radiusProfileList;
    //             },
    //             error => {
    //                 this.totalRecords = 0;
    //                 if (error.error.status == 404) {
    //                     this.messageService.add({
    //                         severity: "info",
    //                         summary: "Info",
    //                         detail: error.error.errorMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 } else {
    //                     this.messageService.add({
    //                         severity: "error",
    //                         summary: "Error",
    //                         detail: error.error.errorMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 }
    //             }
    //         );
    //     }
    // }

    pageChanged(event: PageEvent) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        // this.findAllAcctProfile(this.itemsPerPage);
        if (!this.searchkey) {
            this.findAllAcctProfile(this.itemsPerPage);
        } else {
            this.searchProfileByName();
        }

    }



    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllAcctProfile(this.showItemPerPage);
        } else {
            this.searchProfileByName();
        }
    }

    clearSearchForm() {
        this.searchSubmitted = false;
        this.searchProfileForm.reset();
        this.currentPage = 1;
        this.findAllAcctProfile("");
    }

    deleteConfirm(radiusProfileId, selectedMvnoId, index) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Profile?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteAcctProfileById(radiusProfileId, selectedMvnoId, index);
                },
                reject: () => {
                    this.toastr.info("You have rejected", 'Info!');

                }
            });
        }
    }

    async deleteAcctProfileById(radiusProfileId, selectedMvnoId, index) {
        this.acctProfileService.deleteAcctProfileById(radiusProfileId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0 && this.profileData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllAcctProfile("");
                } else {
                    this.searchProfileByName();
                }
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    deleteConfirmOnProfileDialog(profile: any, index: number) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Profile',
                description: `Are you sure you want to delete "${profile.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {

                this.deleteAcctProfileById(profile.radiusProfileId, profile.mvnoId, index);
            }
        });
    }

    openAcctProfileDialog() {
        this.dialogRef = this.dialog.open(AcctProfileCreateComponent, {
            width: '1200px',
            data: { edit: false }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result === 'refresh') {
                this.findAllAcctProfile(this.itemsPerPage);
            }
        });
    }

    editAcctProfile(profileId: string) {
        this.dialogRef = this.dialog.open(AcctProfileCreateComponent, {
            width: '1200px',
            data: { edit: true, id: profileId, mvnoId: localStorage.getItem("mvnoId") }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result === 'refresh') {
                this.findAllAcctProfile(this.itemsPerPage);
            }
        });
    }

}
