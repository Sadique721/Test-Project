import { DatePipe } from "@angular/common";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { LoginService } from "src/app/service/login.service";
import { ActivatedRoute, Router } from "@angular/router";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { AcctProfileService } from "src/app/service/radius-profile.service";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { VlanProfileService } from "src/app/service/vlan-profile.service";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";


declare var $: any;

@Component({
    selector: "app-vlan-profile-list",
    templateUrl: "./vlan-profile-list.component.html",
    styleUrls: ["./vlan-profile-list.component.scss"],
    standalone: false
})
export class VlanProfileListComponent implements OnInit {
    searchkey: string;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    profileData: any;
    totalRecords: number;
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
    pageLimitOptions = [
        { value: 5 },
        { value: 10 },
        { value: 20 },
        { value: 50 },
        { value: 100 },
        { value: 500 },
        { value: 1000 }
    ];
    searchOptionSelect = [
        { label: "VLAN Name", value: "vlanName" },
        { label: "NAS Identifier", value: "nasIdentifier" }
    ];
    searchOption: any;
    selectedProfiles: Set<string> = new Set();
    newFirst: 0;
    isProfileChecked: boolean = false;
    isVlanAuditModel: boolean = false;
    vlanprofileId: any;
    auditDetails: any;
    vlanProfileDetailModal: boolean = false;
    vlanProfileData: any;
    userId: string;
    superAdminId: string;

    constructor(
        private toastr: ToastrService,

        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        public commondropdownService: CommondropdownService,
        public datepipe: DatePipe,
        public loginService: LoginService,
        public invoicePaymentListService: InvoicePaymentListService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private vlanProfileService: VlanProfileService,
        private fb: UntypedFormBuilder, private dialog: MatDialog,
    ) {
        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_EDIT);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_DELETE)) {
            return [
                'select',
                'vlanName',
                'nasType',
                'nasIdentifier',
                'lastAuthMatched',
                'actions'
            ];
        } else {
            return [
                'select',
                'vlanName',
                'nasType',
                'nasIdentifier',
                'lastAuthMatched'
            ];
        }
    }

    async ngOnInit() {
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.searchProfileForm = this.fb.group({
            vlanName: [""],
            nasIdentifier: [""]
        });
        this.findAllVLANProfile("");
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
    }

    async findAllVLANProfile(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.profileData = [];
        var pageRequest = {
            size: size,
            page: page
        };
        this.vlanProfileService.findAllVLANProfile(pageRequest).subscribe(
            (response: any) => {
                let data = response.vlanList.data;
                this.profileData = data.map(element => {
                    return { ...element, isSingleProfileChecked: this.selectedProfiles.has(element.vlanId) };
                });
                const allSelected = this.profileData.every(profile => profile.isSingleProfileChecked);
                this.isProfileChecked = allSelected;
                this.totalRecords = response.vlanList.totalRecords;
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

    allSelectProfiles(event: any) {
        const checked = event.checked;
        this.profileData.forEach(profile => {
            profile.isSingleProfileChecked = checked;
            if (checked) {
                this.selectedProfiles.add(profile.vlanId);
            } else {
                this.selectedProfiles.delete(profile.vlanId);
            }
        });
        this.isProfileChecked = event.checked;
    }

    addProfileChecked(vlanId: string, event: any) {
        const checked = event.checked;
        const profile = this.profileData.find(p => p.vlanId === vlanId);
        if (profile) {
            profile.isSingleProfileChecked = checked;
            if (checked) {
                this.selectedProfiles.add(vlanId);
            } else {
                this.selectedProfiles.delete(vlanId);
            }
        }
        this.isProfileChecked = this.profileData.every(profile => profile.isSingleProfileChecked);
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        let userId = localStorage.getItem("userId");
        if (userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            error: (error) => {
                this.toastr.info(`${error.error.errorMessage}`, 'You are not authorized to do this operation. Please contact to the administrator!');
            }

            return false;
        }
        return true;
    }

    closeProfileDetailsModel() {
        this.isProfileDetailsModelVisible = false;
    }

    async searchProfileByName() {
        this.searchSubmitted = true;

        if (!this.searchkey || this.searchkey !== this.searchOption) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchOption;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }

        let page = this.currentPage;
        let size = this.itemsPerPage;
        if (this.searchProfileForm.valid) {
            this.profileData = [];
            this.vlanProfileService.search(this.searchProfileForm.value, page, size).subscribe(
                (response: any) => {
                    this.profileData = response.vlanList.data;
                    this.totalRecords = response.vlanList.totalRecords;
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
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber.pageIndex + 1;
        this.itemsPerPage = pageNumber.pageSize;
        if (!this.searchkey) {
            this.findAllVLANProfile("");
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
            this.findAllVLANProfile(this.showItemPerPage);
        } else {
            this.searchProfileByName();
        }
    }

    clearSearchForm() {
        this.searchSubmitted = false;
        this.searchProfileForm.reset();
        this.currentPage = 1;
        this.searchOption = "";
        this.findAllVLANProfile("");
    }

    deleteConfirm(vlanProfileId, selectedMvnoId, index): void {
        if (!vlanProfileId) return;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: `Delete VLAN Profile`,
                description: `Do you want to delete this Profile ?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteVLANProfileById(vlanProfileId, selectedMvnoId, index);
            }
        });
    }

    deleteMultipleVLAnConfirm(): void {
        if (this.selectedProfiles.size === 0) {
            this.toastr.info('No profiles selected for deletion!', 'Info');
            return;
        }

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: `Delete Multiple Profiles`,
                description: `Do you want to delete multiple Vlan Profile ?`,
                yesLabel: "Delete All",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteMultipleVLANProfile();
            }
        });
    }

    deleteMultipleVLANProfile() {
        if (this.selectedProfiles.size === 0) {
            error: (error) => {
                this.toastr.info(`${error.error.errorMessage}`, 'No users selected for deletion!');
            }


            return;
        }
        const vlanIds = Array.from(this.selectedProfiles);
        this.vlanProfileService.deleteVLANProfile(vlanIds).subscribe(
            (response: any) => {
                this.selectedProfiles.clear();
                this.currentPage = 1;
                if (!this.searchkey) {
                    this.findAllVLANProfile("");
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

    async deleteVLANProfileById(vlanProfileId, selectedMvnoId, index) {
        let userId = localStorage.getItem("userId");
        let loggedInUser = localStorage.getItem("loggedInUser");
        this.vlanProfileService
            .deleteVLANProfileById(vlanProfileId, selectedMvnoId, userId, loggedInUser)
            .subscribe(
                (response: any) => {
                    if (
                        this.currentPage != 1 &&
                        index == 0 &&
                        this.profileData.length % this.itemsPerPage == 1
                    ) {
                        this.currentPage = this.currentPage - 1;
                    }
                    if (!this.searchkey) {
                        this.findAllVLANProfile("");
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

    selSearchOption(event) {
        if (event.value == "vlanName") {
            this.searchProfileForm.patchValue({
                nasIdentifier: ""
            });
        } else {
            this.searchProfileForm.patchValue({
                vlanName: ""
            });
        }
    }
    viewAudit(vlanProfileId) {
        let size;
        this.vlanprofileId = vlanProfileId;
        let page = this.currentPage;
        var pageRequest = {
            size: this.itemsPerPage,
            page: this.currentPage
        };
        this.vlanProfileService.findVLANAudit(pageRequest, vlanProfileId).subscribe(
            (response: any) => {
                this.auditDetails = response.vlanAuditList.data;
                this.totalRecords = response.vlanAuditList.totalRecords;
                this.isVlanAuditModel = true;
                this.dialog.open(this.isVlanAuditModelDialog, {
                    width: '80%',
                    disableClose: true // same as data-backdrop="static" data-keyboard="false"
                });
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

    TotalItemPerPageAudit(pageNumber) {
        this.itemsPerPage = pageNumber.value;
        this.auditDetails = null;
        this.viewAudit(this.vlanprofileId);
    }
    pageAuditChanged(pageNumber) {
        this.currentPage = pageNumber;
        this.viewAudit(this.vlanprofileId);
    }

    //   TotalItemPerPage(event) {
    //     this.showItemPerPage = Number(event.value);
    //     if (this.currentPage > 1) {
    //       this.currentPage = 1;
    //     }
    //     if (!this.searchkey) {
    //       this.findAllVLANProfile(this.showItemPerPage);
    //     } else {
    //       this.searchProfileByName();
    //     }
    //   }
    closeAuditDetailsModel() {
        this.isVlanAuditModel = false;
    }

    getVlanProfileDetails(vlanId) {
        this.vlanProfileDetailModal = true;
        this.dialog.open(this.vlanProfileDetailModalDailog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.getVLANProfileById(vlanId);
    }

    closeVlanProfileDetails() {
        this.vlanProfileDetailModal = false;
    }

    getVLANProfileById(vlanProfileId) {
        this.vlanProfileService.getProfileById(vlanProfileId).subscribe(
            (response: any) => {
                this.vlanProfileData = response.vlan;
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    findAllVLANAudit(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.profileData = [];
        var pageRequest = {
            size: size,
            page: page
        };
        this.vlanProfileService.findAllVLANAudit(pageRequest).subscribe((response: any) => {
        });
    }

    displayedColumns: string[] = [
        'select',
        'vlanName',
        'nasType',
        'nasIdentifier',
        'lastAuthMatched',
        'actions'
    ];
    mappingDisplayedColumns: string[] = ['radiusAttribute', 'regex'];

    auditDisplayedColumns: string[] = [
        'entitytName',
        'action',
        'actionByName',
        'remark',
        'actionDateTime'
    ];

    @ViewChild('isVlanAuditModelDialog') isVlanAuditModelDialog!: TemplateRef<any>;


    @ViewChild('vlanProfileDetailModalDailog') vlanProfileDetailModalDailog!: TemplateRef<any>;
}
