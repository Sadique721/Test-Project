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
import { RadiusClientService } from "src/app/service/radius-client.service";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr'; // Add this import

declare var $: any;

@Component({
    selector: "app-radius-client-list",
    templateUrl: "./radius-client-list.component.html",
    styleUrls: ["./radius-client-list.component.scss"],
    standalone: false
})
export class RadiusClientListComponent implements OnInit {
    searchkey: string;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    pageIndex = 0
    pageSize = RadiusConstants.ITEMS_PER_PAGE;
    profileData: any = [];
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
    modalToggle: boolean = false;
    searchSubmitted = false;
    searchProfileForm: UntypedFormGroup;
    showItemPerPage: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;

    clientDataList: any[] = [];
    searchClientForm: UntypedFormGroup;
    clientData: any = [];
    clientIpAddress: string;
    OneClientData: any = [];

    client = {
        clientId: 0,
        clientIp: "",
        sharedKey: "",
        timeOut: "",
        ipType: "",
        mvnoId: ""
    };
    userId: string;
    superAdminId: string;

    constructor(
        private toastr: ToastrService, // Add this injection
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        public commondropdownService: CommondropdownService,
        public datepipe: DatePipe,
        public loginService: LoginService,
        public invoicePaymentListService: InvoicePaymentListService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private acctProfileService: AcctProfileService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private radiusClientService: RadiusClientService
    ) {
        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROFILES_EDIT);

        this.findAllClient("");
    }

    async ngOnInit() {
        this.searchClientForm = this.fb.group({
            clientIpAddress: [""]
        });
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
    }

    async searchClientByIp() {
        this.searchSubmitted = true;
        if (this.searchClientForm.value.clientIpAddress == null) {
            this.searchClientForm.value.clientIpAddress = "";
        }

        if (this.searchClientForm.valid) {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchClientForm.value.clientIpAddress.trim()
            ) {
                this.currentPage = 1;
            }
            if (this.showItemPerPage) {
                this.itemsPerPage = this.showItemPerPage;
            }

            this.clientDataList = [];
            let clientIpAddress = this.searchClientForm.value.clientIpAddress.trim()
                ? this.searchClientForm.value.clientIpAddress.trim()
                : "";

            this.searchkey = clientIpAddress;
            this.clientData = [];
            this.radiusClientService.getClientDataByIp(clientIpAddress).subscribe(
                (response: any) => {
                    this.clientData = response.clientList;
                    //this.totalRecords = response.clientList.totalRecords;
                    this.clientDataList = this.clientData;
                },
                (error: any) => {
                    if (error.error.status == 404) {
                        this.totalRecords = 0;
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.totalRecords = 0;
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                }
            );
        }
    }

    async findAllClient(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.radiusClientService.findAllClientData(page, size, (this.clientIpAddress = "")).subscribe(
            (response: any) => {
                this.clientData = response.clientList;
                // this.totalRecords = response.clientList.totalRecords;
                this.clientDataList = this.clientData;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    deleteConfirm(clientId, mvnoId, index) {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete this Record?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result === true) {
                this.deleteClientById(clientId, mvnoId, index);
            } else {
                this.toastr.info(`You have rejected`, 'Info!');
            }
        });
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        let userId = localStorage.getItem("userId");
        if (userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            this.toastr.info(`You are not authorized to do this operation. Please contact to the administrator`, 'Info!');
            this.modalToggle = false;
            return false;
        }
        return true;
    }

    async deleteClientById(clientId, selectedMvnoId, index) {
        this.radiusClientService.deleteClientById(clientId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllClient("");
                } else {
                    this.searchClientByIp();
                }
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    showClientDetail(clientId) {
        this.modalToggle = true;
        this.dialog.open(this.detailsDialog, {
            width: '550px',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.radiusClientService.getClientDataById(clientId).subscribe(
            (response: any) => {
                this.OneClientData = response;
                this.client = this.OneClientData.client;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    closeClientDetailsModel() {
        this.modalToggle = false;
    }

    clearSearchForm() {
        this.searchSubmitted = false;
        this.searchClientForm.reset();
        this.currentPage = 1;
        this.findAllClient("");
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber.pageIndex + 1;
        this.itemsPerPage = pageNumber.pageSize
        if (!this.searchkey) {
            this.findAllClient("");
        } else {
            this.searchClientByIp();
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllClient(this.showItemPerPage);
        } else {
            this.searchClientByIp();
        }
    }

    displayedColumns: string[] = [
        'id',
        'clientIpAddress',
        'vendor',
        'sharedKey',
        'ipType',
        'mvnoName',
        'actions'
    ];

    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
}
