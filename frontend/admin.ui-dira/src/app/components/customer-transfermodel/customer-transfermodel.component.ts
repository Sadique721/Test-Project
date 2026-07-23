import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable } from "rxjs";
import { Regex } from "src/app/constants/regex";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PartnerService } from "src/app/service/partner.service";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { CustomerService } from "src/app/service/customer.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from "ngx-toastr";
import { PageEvent } from "@angular/material/paginator";

@Component({
    selector: 'app-customer-transfermodel',
    templateUrl: './customer-transfermodel.component.html',
    styleUrls: ['./customer-transfermodel.component.css'],
    standalone: false
})
export class CustomerTransfermodelComponent implements OnInit {

    @ViewChild("closebutton") closebutton;
    @Input() dialogId: string;
    @Input() wCustID: Observable<any>;
    @Output() walletCustomerID = new EventEmitter();
    @Output() closeSelectStaff = new EventEmitter();
    withdrawalcustID = "";
    paymentColumns = ['select', 'transactionref', 'referenceNo', 'paymentMode', 'remainingAmount'];
    dialogRef!: MatDialogRef<any>;



    manageBalanceGroupForm: FormGroup;
    submitted = false;
    ifwithdrawalCommision = false;
    ifWithdrawalOnlineMode = false;
    ifWithdrawalCash = false;
    // displaySelectCustomer: boolean = false;
    parentCustomerListdatatotalRecords: any;
    currentPageParentCustomerListdata = 1;
    newFirst = 0;
    searchParentCustValue = "";
    searchParentCustOption = "";
    parentCustList: any;
    customerDetails: any;
    selectedParentCust: any = [];
    customerList = [];
    searchOptionSelect = [];
    searchDeatil = "";
    partnerData = [];
    searchOptionSelect1: any = [];
    BalanceOpertation = [{ label: " Withdrawal Of Wallet ", value: "withdrawalOfCommission" }];
    paymentmode = [
        { label: "Cash", value: "cash" },
        { label: "Online", value: "online" },
    ];
    partnerName: any;
    ifRedirectManageBalance = false;
    walletAmount: any = "";
    bankDataList: any = [];
    mvnoId: string;
    // displayDialogTransfer: boolean = false;
    currentPagePaymentListdata = 1;
    paymentListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymentListdatatotalRecords: number = 0;
    paymentListData: any = [];
    paymentListDataselector: any;
    pageLimitOptionsPayment = RadiusConstants.pageLimitOptions;
    pageITEMPayment = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPagePayment = 1;
    paymentListDatalength = 0;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    masterSelected: boolean;
    parentFieldEnable = false;
    checklist: any;
    checkedList: any[] = [];
    selectedStaffCust: [];
    customerData: any;
    @ViewChild('displayDialogTransfer') displayDialogTransfer!: TemplateRef<any>;
    @ViewChild('displayselectCustomer') displayselectCustomer!: TemplateRef<any>;
    dialogRefCustomer!: MatDialogRef<any>;


    constructor(
        private fb: FormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private messageService: MessageService,
        private partnerService: PartnerService,
        public revenueManagementService: RevenueManagementService,
        public customerService: CustomerService,
        private customerManagementService: CustomermanagementService,
        private dialog: MatDialog,
        private toastr: ToastrService

    ) { }

    ngOnInit(): void {
        // this.displayDialogTransfer = true;
        this.searchOptionSelect1 = this.commondropdownService.customerSearchOption;
        this.manageBalanceGroupForm = this.fb.group({
            operation: [""],
            mode: [""],
            currentBalance: [""],
            commission: [this.walletAmount],
            remarks: [""],
            transfer: [""],
            amount: ["", [Validators.pattern(Regex.numeric)]],
            newCommission: [""],
            newBalance: [""],
            withdrawalAmount: [""],
            ReamainingCommision: [""],
            referenceNo: [""],
            bank: [""],
            branch: [""],
            addcredit: [""],
            currentCredit: [""],
            NewCredit: [""],
            customersId: [],
        });

        this.wCustID.subscribe(value => {
            // this.closeSelectStafff();
            if (value.wCustID && value.WalletAmount) {
                this.withdrawalcustID = value.wCustID;
                this.walletAmount = value.WalletAmount;
                this.getPayments("");

                this.manageBalanceGroupForm.patchValue({
                    commission: this.walletAmount,
                    operation: "withdrawalOfCommission",
                });
            }
        });

        this.getOpetationType();
        this.getBankDetail();
        // this.getCustomer();
        this.masterSelected = false;
        this.mvnoId = localStorage.getItem("mvnoId");
    }

    ngAfterViewInit() {
        this.transfermodalopen();
    }


    selParentSearchOption(value: any) {
        this.searchParentCustOption = value;
        this.parentFieldEnable = !!value;
    }

    async saveSelCustomer(isOpenModal, data) {
        this.manageBalanceGroupForm.controls.customersId.setValue(data.id);
        this.customerDetails = data;

        this.parentCustList = [
            {
                id: Number(this.selectedParentCust.id),
                name: this.selectedParentCust.username
                // mobile: this.selectedParentCust.mobile,
                // email: this.selectedParentCust.email
            }
        ];
        let custdata = {
            value: this.selectedParentCust.id
        };
        this.selCustomer(custdata);
        if (isOpenModal) this.modalCloseParentCustomer();
    }


    selCustomer(event): void {
        // this.ticketGroupForm.controls.department.setValue("");
        // this.getCustomersDetail(event.value);
        // this.getservicesByCustomer(event.value);
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData("");
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    paginate(event) {
        this.currentPageParentCustomerListdata = event.pageIndex + 1;
        this.parentCustomerListdataitemsPerPage = event.pageSize;
        // this.first = event.first;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData(this.parentCustomerListdataitemsPerPage);
        }
    }

    getParentCustomerData(parentCustomerListdataitemsPerPage) {
        let currentPage;
        currentPage = this.currentPageParentCustomerListdata;
        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const url = "/customers/list";
        this.customerService.postMethod(url, data).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                this.newFirst = 1;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    searchParentCustomer() {
        // this.currentPageParentCustomerListdata = 1;
        const searchParentData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage
        };

        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();

        const url = "/customers/search/Both";
        this.customerService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 400 || error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    // this.customerListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            }
        );
    }

    modalCloseParentCustomer() {
        // this.displaySelectCustomer = false;
        this.dialogRefCustomer.close();
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 1;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        this.customerList = [];

        // console.log("this.newFirst1", this.newFirst)
    }
    getBankDetail() {
        const url = "/bankManagement/searchByStatus";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    getCustomer() {
        // const url = "/customers/list";
        // const custerlist = {
        //     page: 1,
        //     pageSize: 10000
        // };
        // this.customerManagementService.postMethod(url, custerlist).subscribe(
        //     (response: any) => {
        //         this.customerData = response.customerList;
        //     },
        //     (error: any) => {
        //         console.log(error, "error");
        //         this.toastr.error(`${error.error.ERROR}`, 'Failed!')
        //         // this.messageService.add({
        //         //     severity: "error",
        //         //     summary: "Error",
        //         //     detail: error.error.ERROR,
        //         //     icon: "far fa-times-circle"
        //         // });
        //     }
        // );
    }

    async modalOpenParentCustomer() {
        // this.displaySelectCustomer = true;
        this.selectCustomerDialogOpen();
        await this.getParentCustomerData("");
        this.newFirst = 1;
        this.selectedParentCust = [];
        //  console.log("this.newFirst2", this.newFirst)
    }

    selectCustomerDialogOpen() {
        this.dialogRefCustomer = this.dialog.open(this.displayselectCustomer, {
            width: '1000px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRefCustomer.afterClosed().subscribe(result => {
            this.dialogRefCustomer = null;
        });
    }

    getOpetationType() {
        let operationType = "withdrawalOfCommission";
        if (operationType == "withdrawalOfCommission") {
            this.ifwithdrawalCommision = true;
            this.validationWithdrawalCommission();
        } else if (operationType == "") {
            this.ifwithdrawalCommision = false;
            this.clearWithdrawalCommission();
        }
    }

    onKey(e: any) {
        this.manageBalanceGroupForm.value.totalBalance =
            Number(this.manageBalanceGroupForm.value.currentBalance) +
            Number(this.manageBalanceGroupForm.value.addBalance);

        this.manageBalanceGroupForm.patchValue({
            totalBalance: this.manageBalanceGroupForm.value.totalBalance,
        });
    }

    getOpetationMode(event) {
        let modeType = event.value;
        if (modeType == "Cash") {
            this.ifWithdrawalOnlineMode = false;
            this.ifWithdrawalCash = true;
            this.OnlineFieldClear();
        } else if (modeType == "Online") {
            this.ifWithdrawalOnlineMode = true;
            this.ifWithdrawalCash = false;
            this.OnlineFieldValidation();
        } else {
            this.ifWithdrawalOnlineMode = false;
            this.ifWithdrawalCash = false;
            this.OnlineFieldClear();
        }
    }
    onKeyWithdrawalAmount() {
        let amount =
            Number(this.manageBalanceGroupForm.value.commission) -
            Number(this.manageBalanceGroupForm.value.withdrawalAmount);
        if (amount < 0) {
            this.toastr.info(`Withdrawal amount can not be greater than wallet amount..`, 'Info!')
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Information",
            //     detail: "Withdrawal amount can not be greater than wallet amount..",
            //     icon: "far fa-check-circle",
            // });
            this.manageBalanceGroupForm.controls.withdrawalAmount.setValue(0);
        }

        this.manageBalanceGroupForm.patchValue({
            ReamainingCommision: parseFloat(amount.toString()).toFixed(2),
        });
    }

    submitBalance() {

        if (this.manageBalanceGroupForm.invalid) {
            Object.keys(this.manageBalanceGroupForm.controls).forEach(key => {
                const control = this.manageBalanceGroupForm.get(key);
                control.markAsTouched();
            });
            return;
        }

        if (this.manageBalanceGroupForm.valid) {
            this.customerDetails;
            this.submitted = true;
            let withDrawCreditdocId = [];
            if (!this.getAllowWithDrawal(this.checkedList)) {

                this.toastr.info('Please select payments more than withdrawal amount.', 'Information');
                return;
            }
            this.checkedList.forEach(element => {
                withDrawCreditdocId.push(element.id);
            });
            let data = {
                amount: Number(this.manageBalanceGroupForm.value.withdrawalAmount),
                bank: this.manageBalanceGroupForm.value.bank,
                branch: this.manageBalanceGroupForm.value.branch,
                bankManagement: "",
                customerid: this.withdrawalcustID,
                toCustomerId: this.customerDetails.id,
                withDrawCreditdocId: withDrawCreditdocId,
                mvnoId: Number(this.mvnoId),
                paymentreferenceno: this.manageBalanceGroupForm.value.referenceNo,
                paymode: this.manageBalanceGroupForm.value.mode,
                paytype: "payment",
                referenceno: this.manageBalanceGroupForm.value.referenceNo,
                remark: this.manageBalanceGroupForm.value.remarks,
                type: "Payment",
            };
            this.transferAmount(data);
        }
        //   if (this.manageBalanceGroupForm.valid) {
        //     if (!this.getAllowWithDrawal(this.checkedList)) {
        //       this.messageService.add({
        //         severity: "info",
        //         summary: "Information",
        //         detail: "Please select payments more than transfer amount.",
        //         icon: "far fa-check-circle",
        //       });
        //       return;
        //     }
        //     let withDrawCreditdocId = [];
        //     this.checkedList.forEach(element => {
        //       withDrawCreditdocId.push(element.id);
        //     });

        //     if (this.manageBalanceGroupForm.value.mode == "Cash") {
        //       let data = {
        //         amount: Number(this.manageBalanceGroupForm.value.withdrawalAmount),
        //         bank: "",
        //         branch: "",
        //         bankManagement: "",
        //         customerid: this.withdrawalcustID,
        //         withDrawCreditdocId: withDrawCreditdocId,
        //         mvnoId: Number(this.mvnoId),
        //         paymentreferenceno: this.manageBalanceGroupForm.value.referenceNo,
        //         paymode: this.manageBalanceGroupForm.value.mode,
        //         paytype: "payment",
        //         referenceno: this.manageBalanceGroupForm.value.referenceNo,
        //         remark: this.manageBalanceGroupForm.value.remarks,
        //         type: "Payment",
        //       };
        //       this.withdrawalAmount(data);
        //     } else if (this.manageBalanceGroupForm.value.mode == "Online") {
        //       let data = {
        //         amount: Number(this.manageBalanceGroupForm.value.withdrawalAmount),
        //         bank: this.manageBalanceGroupForm.value.bank,
        //         branch: this.manageBalanceGroupForm.value.branch,
        //         bankManagement: "",
        //         customerid: this.withdrawalcustID,
        //         withDrawCreditdocId: withDrawCreditdocId,
        //         mvnoId: Number(this.mvnoId),
        //         paymentreferenceno: this.manageBalanceGroupForm.value.referenceNo,
        //         paymode: this.manageBalanceGroupForm.value.mode,
        //         paytype: "payment",
        //         referenceno: this.manageBalanceGroupForm.value.referenceNo,
        //         remark: this.manageBalanceGroupForm.value.remarks,
        //         type: "Payment",
        //       };
        //       this.withdrawalAmount(data);
        //     }
        //   }
    }

    transferAmount(data) {
        if (data.withdrawAmount != 0) {
            let url = "/transfer/payment";
            this.revenueManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.walletCustomerID.emit(this.withdrawalcustID);
                    this.ifRedirectManageBalance = false;

                    // this.closeSelectStafff();
                    this.toastr.success(`Successfully Transfered`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.msg,
                    //     icon: "far fa-check-circle",
                    // });
                    // this.displayDialogTransfer = false;
                    this.dialogRef.close();
                    this.closeSelectStaff.emit(this.selectedStaffCust);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle",
                    // });

                    // this.displayDialogTransfer = false;
                    this.dialogRef.close();
                    this.closeSelectStaff.emit(this.selectedStaffCust);
                }
            );
        } else {
            this.toastr.info(`You have rejected the transfer because the value of the Withdrawal Amount is Zero`, 'Info!')
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Rejected",
            //     detail: "You have rejected the transfer because the value of the Withdrawal Amount is Zero",
            // });
        }
    }

    closeSelectStafff() {
        this.dialogRef.close();
        // this.displayDialogTransfer = false;
        this.closeSelectStaff.emit(this.selectedStaffCust);
        this.submitted = false;
        this.ifWithdrawalOnlineMode = false;
        this.ifWithdrawalCash = false;
        this.ifwithdrawalCommision = true;
        this.manageBalanceGroupForm.reset();
        this.manageBalanceGroupForm.controls.operation.setValue("");
        this.manageBalanceGroupForm.controls.mode.setValue("");
        this.clearWithdrawalCommission();
        this.OnlineFieldClear();
    }

    transfermodalopen() {
        // this.manageBalanceGroupForm.get('commission')?.disable();
        this.manageBalanceGroupForm.get('ReamainingCommision')?.disable();
        this.manageBalanceGroupForm.get('customersId')?.disable();
        this.dialogRef = this.dialog.open(this.displayDialogTransfer, {
            width: '1200px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef = null;
            this.manageBalanceGroupForm.reset();
        });
    }

    validationWithdrawalCommission() {
        // this.manageBalanceGroupForm.get("mode").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("mode").updateValueAndValidity();
        this.manageBalanceGroupForm.get("remarks").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("remarks").updateValueAndValidity();
        this.manageBalanceGroupForm.get("referenceNo").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("referenceNo").updateValueAndValidity();

        // this.manageBalanceGroupForm.get("withdrawalAmount").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("withdrawalAmount").updateValueAndValidity();
    }
    clearWithdrawalCommission() {
        this.manageBalanceGroupForm.get("mode").clearValidators();
        this.manageBalanceGroupForm.get("mode").updateValueAndValidity();
        this.manageBalanceGroupForm.get("withdrawalAmount").clearValidators();
        this.manageBalanceGroupForm.get("withdrawalAmount").updateValueAndValidity();
        this.manageBalanceGroupForm.get("referenceNo").clearValidators();
        this.manageBalanceGroupForm.get("referenceNo").updateValueAndValidity();
        this.manageBalanceGroupForm.get("remarks").clearValidators();
        this.manageBalanceGroupForm.get("remarks").updateValueAndValidity();
    }

    OnlineFieldValidation() {
        // this.manageBalanceGroupForm.get("bank").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("bank").updateValueAndValidity();
        // this.manageBalanceGroupForm.get("branch").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("branch").updateValueAndValidity();
    }

    OnlineFieldClear() {
        this.manageBalanceGroupForm.get("bank").clearValidators();
        this.manageBalanceGroupForm.get("bank").updateValueAndValidity();
        this.manageBalanceGroupForm.get("branch").clearValidators();
        this.manageBalanceGroupForm.get("branch").updateValueAndValidity();

        this.manageBalanceGroupForm.patchValue({
            commission: this.walletAmount,
            operation: "withdrawalOfCommission",
        });

        this.getOpetationType();
    }

    onKeyaddcredit(e) {
        this.manageBalanceGroupForm.value.NewCredit =
            Number(this.manageBalanceGroupForm.value.currentCredit) +
            Number(this.manageBalanceGroupForm.value.addcredit);

        this.manageBalanceGroupForm.patchValue({
            NewCredit: this.manageBalanceGroupForm.value.NewCredit,
        });
    }

    cancelMangeBalnce() {
        this.ifRedirectManageBalance = false;
        // this.closeSelectStafff();
    }

    getPayments(list): void {
        let size;
        const page = this.currentPagePaymentListdata;
        if (list) {
            size = list;
            this.paymentListdataitemsPerPage = list;
        } else {
            size = this.paymentListdataitemsPerPage;
        }

        const url = `/getWithdrawPayments/${this.withdrawalcustID}`;
        const paymentList = {
            page,
            pageSize: size,
        };
        this.revenueManagementService.postMethod(url, paymentList).subscribe(
            (response: any) => {
                this.paymentListData = response.dataList;
                this.paymentListDataselector = response.dataList;
                this.paymentListdatatotalRecords = response.totalRecords || response.dataList.length;
                if (this.showItemPerPagePayment > this.paymentListdataitemsPerPage) {
                    this.paymentListDatalength = this.paymentListData.length % this.showItemPerPagePayment;
                } else {
                    this.paymentListDatalength =
                        this.paymentListData.length % this.paymentListdataitemsPerPage;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    totalItemPerPageForPayments(event): void {
        this.showItemPerPagePayment = Number(event.value);
        if (this.currentPagePaymentListdata > 1) {
            this.currentPagePaymentListdata = 1;
        }
        this.getPayments(this.showItemPerPagePayment);
    }

    pageChangedForPayments(pageEvent: PageEvent): void {
        this.currentPagePaymentListdata = pageEvent.pageIndex + 1;
        this.paymentListdataitemsPerPage = pageEvent.pageSize;
        this.getPayments(this.paymentListdataitemsPerPage);
    }


    // The master checkbox will check/ uncheck all items
    checkUncheckAll() {
        for (let i = 0; i < this.paymentListData.length; i++) {
            this.paymentListData[i].isSelected = this.masterSelected;
        }
        this.getCheckedItemList();
    }

    // Check All Checkbox Checked
    isAllSelected() {
        this.masterSelected = this.paymentListData.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemList();
    }

    // Get List of Checked Items
    getCheckedItemList() {
        this.checkedList = [];
        for (let i = 0; i < this.paymentListData.length; i++) {
            if (this.paymentListData[i].isSelected) {
                this.checkedList.push(this.paymentListData[i]);
            }
        }
    }

    getAllowWithDrawal(checkedList: any[]): boolean {
        if (checkedList.length == 0) {
            return false;
        } else {
            let allowedAmount = 0;
            let withdrawalAmount = Number(this.manageBalanceGroupForm.value.withdrawalAmount);
            this.checkedList.forEach(element => {
                allowedAmount = allowedAmount + element.remainingAmount;
            });
            if (allowedAmount - withdrawalAmount < 0) {
                return false;
            }
        }
        return true;
    }
}

