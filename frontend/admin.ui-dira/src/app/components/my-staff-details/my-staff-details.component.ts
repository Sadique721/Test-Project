import { Component, OnDestroy, OnInit, TemplateRef, ViewChild } from "@angular/core";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { UntypedFormBuilder, FormArray, UntypedFormGroup, Validators, FormControl } from "@angular/forms";
import { StaffService } from "src/app/service/staff.service";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { SystemconfigService } from "../../service/systemconfig.service";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { ActivatedRoute, Event, NavigationEnd, Router } from "@angular/router";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { SETTINGS } from "src/app/constants/aclConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { error } from "console";
import { Subscription } from "rxjs";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { MatDialog, MatDialogConfig, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";

declare var $: any;

@Component({
    selector: "app-my-staff-details",
    templateUrl: "./my-staff-details.component.html",
    styleUrls: ["./my-staff-details.component.css"],
    standalone: false
})
export class MyStaffDetailsComponent implements OnInit, OnDestroy {
    selectedTabName: string = 'StaffDetails';
    paymentReciptForm: UntypedFormGroup;
    radiusWalletGroupForm: UntypedFormGroup;
    changePasswordForm: UntypedFormGroup;
    selectedIndex: number = 0;
    staffImg: SafeResourceUrl;
    AclClassConstants;
    AclConstants;
    satffUserData: any = [];
    isStaffPersonalData = false;
    isStaffReceiptData = false;
    ifWalletStaffShow = false;

    parentStaffList: any = [];
    currentReceiptPage: number = 1;
    itemsReceiptPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalReceiptRecords: number;
    userId = "";
    currency: string;

    userName: "";
    ifgenerateOtpField = true;
    userNameForPasswordUpdate = "";
    mvnoIdForPwdChange = "";
    staffOTPValue = "";
    staffPhoneNumber = "";
    staffEmail = "";
    staffCountryCode = "";
    _passwordNewType = "password";
    showNewPassword = false;
    pageLimitOptionsLedger = RadiusConstants.pageLimitOptions;

    paymentModes = [];
    staffLegderChequeData = [];
    selectedCheques;
    searchOptionSelect = ["Mode", "Status"];
    searchOption: any;
    searchDeatil: any;
    additionalDetails: any = [];
    receiptAccess: boolean = false;
    receiptMgmtAccess: boolean = false;
    profileWalletAccess: boolean = false;
    profileChangePassAccess: boolean = false;
    // ******************
    private dialogRef: MatDialogRef<any> | null = null;
    @ViewChild('serviceAreaDialog') serviceAreaDialog!: TemplateRef<any>;
    @ViewChild('buisnessunitDialog') buisnessunitDialog!: TemplateRef<any>;
    @ViewChild('addNewRecipetDialog') addNewRecipetDialog!: TemplateRef<any>;
    @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
loggedInUserId: number;
    private routerEventsSubscription: Subscription;
    dataSources: any;
    currentPageSize: number;
    constructor(
        private staffService: StaffService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private systemService: SystemconfigService,
        private sanitizer: DomSanitizer,
        private route: ActivatedRoute,
        public loginService: LoginService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private customerManagementService: CustomermanagementService,
        private router: Router,
        public statusCheckService: StatusCheckService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.routerEventsSubscription = this.router.events.subscribe(event => {
            this.userId = this.route.snapshot.paramMap.get("id");
            if (event instanceof NavigationEnd) {
                this.userId = this.route.snapshot.paramMap.get("id")!;
                this.initData();
            }
        });
        this.userId = this.route.snapshot.paramMap.get("id");
        this.getDetails();
    }

    ngAfterViewInit() {
        if (this.dataSources) {
            this.dataSources.paginator = this.paginator;
        }
    }

    ngOnDestroy(): void {
        // Unsubscribe from the router events when the component is destroyed
        if (this.routerEventsSubscription) {
            this.routerEventsSubscription.unsubscribe();
        }
    }

    getDetails() {
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
    }

    ngOnInit(): void {
        // this.userId = this.route.snapshot.paramMap.get("id");

        this.initData();
        this.route.queryParamMap.subscribe(params => {
            this.selectedTabName = params.get('mystaff') || 'StaffDetails';
            this.setSelectedIndexBasedOnName();
        });
    }
    setSelectedIndexBasedOnName() {
        switch (this.selectedTabName) {
            case 'StaffDetails':
                this.selectedIndex = 0;
                break;
            case 'StaffReceipt':
                this.selectedIndex = 1;
                break;
            case 'Wallet':
                this.selectedIndex = 2;
                break;
            case 'ChangePassword':
                this.selectedIndex = 3;
                break;
            default:
                this.selectedIndex = 0;
        }
    }

    initData() {
        this.receiptAccess = false;
        this.receiptMgmtAccess = false;
        this.profileWalletAccess = false;
        this.profileChangePassAccess = false;
        this.loggedInUserId = Number(localStorage.getItem("userId"));

        const hasQueryParams = Object.keys(this.route.snapshot.queryParams).length > 0;
        const userId = hasQueryParams ? this.loggedInUserId : this.userId;

        if (this.loggedInUserId == Number(this.userId)) {
            this.receiptAccess = this.loginService.hasPermission(SETTINGS.STAFF_RECEIPT);
            this.receiptMgmtAccess = this.loginService.hasPermission(SETTINGS.STAFF_RECEIPT_MGMT);
            this.profileWalletAccess = this.loginService.hasPermission(SETTINGS.MY_PROFILE_WALLET);
            this.profileChangePassAccess = this.loginService.hasPermission(
                SETTINGS.MY_PROFILE_CHANGE_PASSWORD
            );
        } else {
            this.receiptAccess = this.loginService.hasPermission(SETTINGS.STAFF_DETAILS_RECEIPT);
            this.receiptMgmtAccess = this.loginService.hasPermission(SETTINGS.STAFF_CREATE_RECEIPT);
            this.profileWalletAccess = this.loginService.hasPermission(SETTINGS.STAFF_DETAILS_WALLET);
            this.profileChangePassAccess = this.loginService.hasPermission(
                SETTINGS.STAFF_CHANGE_PASSWORD
            );
        }
        this.paymentReciptForm = this.fb.group({
            prefix: ["", Validators.required],
            receiptFrom: ["", Validators.required],
            receiptTo: ["", Validators.required]
        });

        this.radiusWalletGroupForm = this.fb.group({
            date: ["", Validators.required],
            paymentMode: ["", Validators.required],
            amount: [""],
            bankId: ["", Validators.required],
            remarks: ["", Validators.required]
        });
        this.changePasswordForm = this.fb.group({
            userName: [{ value: "", disabled: true }],
            newPassword: ["", [Validators.required]]
        });

        this.staffDetialsOpen(userId);
        this.getBankDetail();
        this.openStaffID = this.userId;
    }

    staffreciptMappingList: any = [];
    openStaffID = "";
    staffDetialsOpen(id) {
        this.selectedIndex = 0;
        this.isStaffPersonalData = true;
        this.isStaffReceiptData = false;
        this.ifWalletStaffShow = false;
        this.openStaffID = id;
        this.getstaffData(id);
    }

    pageReceiptChanged(event: PageEvent) {
        this.currentReceiptPage = event.pageIndex + 1;
        this.currentPageSize = event.pageSize;
    }

    getstaffData(id) {
        this.staffService.getStaff(id).subscribe((response: any) => {
            this.satffUserData = response.Staff;
            this.userName = this.satffUserData.username;
            if (this.statusCheckService.isActiveCMS) {
                this.getStaffReceiptDatabyStaffId(this.satffUserData.id);
            }
            this.staffImg = this.sanitizer.bypassSecurityTrustResourceUrl(
                `data:image/png;base64, ${this.satffUserData.profileImage}`
            );
        });
    }
    openStaffStaffReceipt() {
        this.isStaffPersonalData = false;
        this.isStaffReceiptData = true;
        this.ifWalletStaffShow = false;
    }

    getWallatData: any;
    WalletAmount: any;
    openStaffWallet() {
        this.isStaffPersonalData = false;
        this.isStaffReceiptData = false;
        this.ifWalletStaffShow = true;
        this.additionalDetails = [];
        const url = "/staff_ledger_details/walletAmount/" + this.openStaffID;
        this.staffService.getFromCMS(url).subscribe((response: any) => {
            this.getWallatData = response;
            this.WalletAmount = response.availableAmount;
        });

        this.getstaffLegderData();
    }

    staffRecepetId = "";
    addNewReceipt(id) {
        this.staffRecepetId = id;
        this.dialogRef = this.dialog.open(this.addNewRecipetDialog, {
            width: "600px",
            hasBackdrop: true,
            panelClass: 'custom-dialog-class',
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(() => {
            this.staffRecepetId == "";
            this.paymentReciptForm.reset();
            this.dialogRef = null;
            this.dialogRef.close();
        });
    }

    clearpaymentReciptForm() {
        this.staffRecepetId == "";
        this.paymentReciptForm.reset();
    }

    saveNewRecipt() {
        let staffUserServiceMappingList = {
            fromreceiptnumber: this.paymentReciptForm.value.receiptFrom,
            id: "",
            identityKey: "",
            isActive: true,
            isDeleted: true,
            mvnoId: "",
            prefix: this.paymentReciptForm.value.prefix,
            stfmappingId: this.staffRecepetId,
            toreceiptnumber: this.paymentReciptForm.value.receiptTo
        };

        this.customerManagementService.addNewReceipt(staffUserServiceMappingList).subscribe(
            (response: any) => {
                this.getstaffData(this.staffRecepetId);
                this.dialogRef.close();
                this.closeServiceAreaDialog();
                this.paymentReciptForm.reset();
                $("#paymentReciptModal").modal("hide");
                this.clearpaymentReciptForm();
                this.openStaffStaffReceipt();
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    searchReceptNumber = "";
    clearReceiptForm() {
        this.searchReceptNumber = "";
        this.staffreciptMappingList = this.satffUserData.staffUserServiceMappingList;
    }

    bankDataList: any = [];
    getBankDetail() {
        const url = "/bankManagement/searchByStatus";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    showWithdrawalAmountModel() {
        $("#staffWalletModal").modal("show");
    }

    clearWalletStaffForm() {
        this.radiusWalletGroupForm.reset();
        this.selectedCheques = [];
    }

    closeWalletForm() {
        this.radiusWalletGroupForm.reset();
        this.selectedCheques = [];
        $("#staffWalletModal").modal("hide");
    }
    saveManageBalance() {
        let data: any = {};
        let data1 = this.radiusWalletGroupForm.value;
        var dataList = [];
        if (this.radiusWalletGroupForm.value.paymentMode.toUpperCase() == "CHEQUE") {
            this.selectedCheques.forEach(element => {
                data = {
                    action: "",
                    amount: element.amount,
                    bankId: this.radiusWalletGroupForm.value.bankId,
                    bankName: this.bankDataList.find(
                        item => item.id == this.radiusWalletGroupForm.value.bankId
                    ).bankname,
                    date: this.radiusWalletGroupForm.value.date,
                    remarks: this.radiusWalletGroupForm.value.remarks,
                    paymentMode: this.radiusWalletGroupForm.value.paymentMode,
                    chequeno: element.chequeno,
                    chequedate: element.chequedate,
                    // transactionType: "DR",
                    buId: "",
                    creditDocId: "",
                    custId: "",
                    id: this.openStaffID,
                    identityKey: "",
                    mvnoId: ""
                    // staffUser: {
                    //   id: this.openStaffID,
                    // },
                };
                dataList.push(data);
            });
        } else {
            data = {
                action: "",
                amount: this.radiusWalletGroupForm.value.amount,
                bankId: this.radiusWalletGroupForm.value.bankId,
                date: this.radiusWalletGroupForm.value.date,
                remarks: this.radiusWalletGroupForm.value.remarks,
                paymentMode: this.radiusWalletGroupForm.value.paymentMode,
                bankName: this.bankDataList.find(item => item.id == this.radiusWalletGroupForm.value.bankId)
                    .bankname,
                // transactionType: "DR",
                buId: "",
                creditDocId: "",
                custId: "",
                id: this.openStaffID,
                identityKey: "",
                mvnoId: ""
                // staffUser: {
                //   id: this.openStaffID,
                // },
            };
            dataList.push(data);
        }
        const url = "/staff_ledger_details/transferredToBank";
        this.staffService.postApiMethod(url, dataList).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Error!');
                } else if (response.responseCode == 405) {
                    this.radiusWalletGroupForm.reset();
                    this.paymentModes = [];
                    $("#staffWalletModal").modal("hide");

                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.radiusWalletGroupForm.reset();
                    $("#staffWalletModal").modal("hide");

                    this.openStaffWallet();

                    this.toastr.success(`${response.message}`, 'Success!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    itemsLegderPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentLegderPage = 1;
    totalLegderRecords: string;
    staffLegderData: any = [];

    pageLegderChanged(e) {
        this.currentLegderPage = e;
    }
    staffData: any;
    getstaffLegderData() {
        const url = "/staff_ledger_details/getStaffLedgerDetailsbyStaffId/" + this.openStaffID;
        this.staffService.getFromCMS(url).subscribe(
            (response: any) => {
                this.staffData = response.dataList;
                this.staffLegderData = response.dataList;
                this.staffLegderChequeData = this.staffLegderData.filter(
                    item => item.paymentMode === "Cheque" && item.status === "Pending"
                );
                this.paymentModes = [];
                var filteredModes = [...new Set(this.staffLegderData.map(item => item.paymentMode))];
                filteredModes.forEach((item: string) => {
                    if (item != "") {
                        this.paymentModes.push({
                            label: item,
                            value: item
                        });
                    }
                });
                this.additionalDetiails();
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    onPaymentModeChange(event) {
        if (event.value === "CASH") {
            this.radiusWalletGroupForm.controls.amount.setValidators(Validators.required);
        } else if (event.value === "Cheque") {
            this.radiusWalletGroupForm.controls.amount.clearValidators();
        }
        this.radiusWalletGroupForm.controls.amount.updateValueAndValidity();
    }

    getCustomerDataForPasswordChange(staff) {
        this.ifgenerateOtpField = true;
        this.staffOTPValue = "";
        this.mvnoIdForPwdChange = staff.mvnoId;
        this.userNameForPasswordUpdate = staff.username;
        this.staffPhoneNumber = staff.phone;
        this.staffCountryCode = staff.countryCode;
        this.staffEmail = staff.email;
        this.changePasswordForm.patchValue({
            userName: this.userNameForPasswordUpdate
        });
    }
    // generate OTP
    genrateOtp(staff) {
        this.ifgenerateOtpField = true;
        this.staffOTPValue = "";
        this.mvnoIdForPwdChange = staff.mvnoId;
        this.userNameForPasswordUpdate = staff.username;
        this.staffPhoneNumber = staff.phone;
        this.staffCountryCode = staff.countryCode;
        this.staffEmail = staff.email;
        this.changePasswordForm.patchValue({
            userName: this.userNameForPasswordUpdate
        });
        this.staffOTPValue = "";
        let data = {
            countryCode: this.staffCountryCode,
            mobileNumber: this.staffPhoneNumber,
            emailId: this.staffEmail,
            profile: "OTP"
        };


        let url = "/otp/generate";

        this.staffService.postApiFromCMS(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Error!');
                } else if (response.responseCode == 405) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.otp}`, 'Success!');
                }
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Error!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
                console.log(error, "error");
            }
        );
    }

    // Validate OTP
    ValidOtp() {
        let data = {
            mobileNumber: this.staffPhoneNumber,
            emailId: this.staffEmail,
            otp: this.staffOTPValue
        };

        let url = "/otp/validate";

        this.staffService.postApiFromCMS(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.ifgenerateOtpField = true;

                    this.toastr.error(`${response.responseMessage}`, 'Error!');
                } else {
                    this.ifgenerateOtpField = false;

                    this.toastr.success(`${response.message}`, 'Success!');
                }
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Error!');
                } else {
                    this.toastr.error(`${error.error.msg}`, 'Error!');
                }
                console.log(error, "error");
            }
        );
    }

    changePassword() {
        this.changePasswordForm.value.userName = this.userNameForPasswordUpdate;
        this.staffService.changePassword(this.changePasswordForm.value).subscribe(
            (response: any) => {

                $("#changePasswordModal").modal("hide");
                this.clearChangePasswordForm();
                this.toastr.success(`${response.responseMessage}`, 'Success!');
            },
            (error: any) => {
                console.log("Error:::::::::::::: ", error);
                if (error.status == 500) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }

    clearChangePasswordForm() {
        this.ifgenerateOtpField = true;
        this.staffOTPValue = "";
        // this.changePasswordForm.controls.otp.reset();
        //this.changePasswordForm.reset();
    }

    totalItemPerPageForLedger(event): void {
        this.itemsLegderPerPage = Number(event.value);
        // if (this.currentPageCustomerDocListdata > 1) {
        //   this.currentLegderPage = 1;
        // }
    }
    search() {
        if (this.searchOption == "Mode") {
            this.staffLegderData = [];
            this.staffData.forEach(element => {
                if (element.paymentMode === this.searchDeatil) this.staffLegderData.push(element);
            });
        } else if (this.searchOption == "Status") {
            this.staffLegderData = [];
            this.staffData.forEach(element => {
                if (element.status === this.searchDeatil) this.staffLegderData.push(element);
            });
        }
    }
    clearSearch() {
        this.staffLegderData = this.staffData;
        this.searchDeatil = null;
        this.searchOption = null;
    }
    additionalDetiails() {
        var lookup = {};
        var items = this.staffData;
        var result = [];

        for (var item, i = 0; (item = items[i++]);) {
            var mode = item.paymentMode;

            if (!(mode in lookup)) {
                lookup[mode] = 1;
                result.push(mode);
            }
        }

        let totalCollection = 0;
        let totalWithdraw = 0;
        result.forEach(mode => {
            this.staffData.forEach(element => {
                if (element.paymentMode === mode && element.action === "Collected")
                    totalCollection = totalCollection + element.amount;
                else if (element.paymentMode === mode && element.action === "Withdraw")
                    totalWithdraw = totalWithdraw + element.amount;
            });
            let data = {
                mode: mode,
                credit: totalCollection,
                withdraw: totalWithdraw
            };
            this.additionalDetails.push(data);
        });

    }
    getStaffReceiptDatabyStaffId(id) {
        return this.customerManagementService
            .getStaffReceiptDataByStaffId(this.satffUserData.id)
            .subscribe(
                (response: any) => {
                    this.staffreciptMappingList = response.dataList;
                    this.dataSources = new MatTableDataSource(this.staffreciptMappingList);
                    this.dataSources.paginator = this.paginator;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
    }

    navigateToRadiusStaff() {
        this.router.navigate(["/home/radiusstaff"]);
    }

    openServiceAreaDialog(event: MouseEvent) {

        if (this.dialogRef) {
            return;
        }

        this.dialogRef = this.dialog.open(this.serviceAreaDialog, {
            width: "600px",
            hasBackdrop: true,
            panelClass: 'custom-dialog-class',
            disableClose: true
        });

        this.dialogRef.backdropClick().subscribe(() => {
            // this.closeServiceAreaDialog();
            this.dialogRef?.close();
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null;
        });


    }

    openBuisnessUnitAreaDialog(event: MouseEvent) {
        if (this.dialogRef) {
            return;
        }

        this.dialogRef = this.dialog.open(this.buisnessunitDialog, {
            width: "600px",
            hasBackdrop: true,
            panelClass: 'custom-dialog-class',
            disableClose: true
        });

        this.dialogRef.backdropClick().subscribe(() => {
            // this.closeServiceAreaDialog();
            this.dialogRef?.close(); // Close only, don't nullify here
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null;
        });
    }


    openteamDialog(event: MouseEvent) {
        if (this.dialogRef) {
            return;
        }

        const dialogWidth = 600; // same as open() width
        const gap = 20; // right side ka gap chahiye

        // Calculate left position
        let left = event.clientX + 10;

        // Agar dialog right edge se bahar nikal raha ho to adjust karo
        if (left + dialogWidth + gap > window.innerWidth) {
            left = window.innerWidth - dialogWidth - gap;
        }

        this.dialogRef = this.dialog.open(this.teamDialog, {
            width: dialogWidth + "px",
            hasBackdrop: true,
            panelClass: "custom-dialog-class",
            disableClose: true,
            position: {
                left: left + "px",
                // top: event.clientY + 3 + "px"
            }
        });

        this.dialogRef.backdropClick().subscribe(() => {
            // this.closeServiceAreaDialog();
            this.dialogRef?.close();
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null;
        });
    }


    closeServiceAreaDialog() {
        if (this.dialogRef) {
            this.dialogRef.close();
            // this.dialogRef = null;
        }
    }
}
