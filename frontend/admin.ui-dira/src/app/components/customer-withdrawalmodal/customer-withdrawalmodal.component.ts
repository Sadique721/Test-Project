import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable } from "rxjs";
import { Regex } from "src/app/constants/regex";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PartnerService } from "src/app/service/partner.service";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { MatDialog } from "@angular/material/dialog";
import { MatTab } from "@angular/material/tabs";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";

declare var $: any;
@Component({
    selector: "app-customer-withdrawalmodal",
    templateUrl: "./customer-withdrawalmodal.component.html",
    styleUrls: ["./customer-withdrawalmodal.component.css"],
    standalone: false
})
export class CustomerWithdrawalmodalComponent implements OnInit {
    @ViewChild("closebutton") closebutton;
    @Input() dialogId: string;
    @ViewChild('withdrawalDialog') withdrawalDialog!: TemplateRef<any>;
    @Input() wCustID: Observable<any>;
    @Output() walletCustomerID = new EventEmitter();
    @Output() closeSelectStaff = new EventEmitter();
    withdrawalcustID = "";
    paymentColumns = ['select', 'transactionref', 'referenceNo', 'paymentMode', 'remainingAmount'];

    manageBalanceGroupForm: UntypedFormGroup;
    submitted = false;
    ifwithdrawalCommision = false;
    ifWithdrawalOnlineMode = false;
    ifWithdrawalCash = false;
    partnerData = [];

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
    displayDialogWithDraw: boolean = false;
    currentPagePaymentListdata = 1;
    paymentListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymentListdatatotalRecords: number;
    paymentListData: any = [];
    paymentListDataselector: any;
    pageLimitOptionsPayment = RadiusConstants.pageLimitOptions;
    pageITEMPayment = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPagePayment = 1;
    paymentListDatalength = 0;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    masterSelected: boolean;
    checklist: any;
    checkedList: any[] = [];
    selectedStaffCust: [];
    paginatedPaymentData = new MatTableDataSource<any>([]);
    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private messageService: MessageService,
        private partnerService: PartnerService,
        public revenueManagementService: RevenueManagementService,
        private dialog: MatDialog,
        private toast: ToastrService
    ) { }

    ngOnInit(): void {
        this.displayDialogWithDraw = true;
        this.manageBalanceGroupForm = this.fb.group({
            operation: ["", Validators.required],
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
        this.masterSelected = false;
        this.mvnoId = localStorage.getItem("mvnoId");
    }

    ngAfterViewInit() {
        this.dialog.open(this.withdrawalDialog, {
            width: '1000px',
            disableClose: true
        });
    }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toast.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
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
            this.toast.info('Withdrawal amount can not be greater than wallet amount..', 'Information');
            this.manageBalanceGroupForm.controls.withdrawalAmount.setValue(0);
        }

        this.manageBalanceGroupForm.patchValue({
            ReamainingCommision: parseFloat(amount.toString()).toFixed(2),
        });
    }

    submitBalance() {
        this.submitted = true;
        if (this.manageBalanceGroupForm.valid) {
            if (!this.getAllowWithDrawal(this.checkedList)) {

                this.toast.info('Please select payments more than withdrawal amount.', 'Information');
                return;
            }
            let withDrawCreditdocId = [];
            this.checkedList.forEach(element => {
                withDrawCreditdocId.push(element.id);
            });

            if (this.manageBalanceGroupForm.value.mode == "Cash") {
                let data = {
                    amount: Number(this.manageBalanceGroupForm.value.withdrawalAmount),
                    bank: "",
                    branch: "",
                    bankManagement: "",
                    customerid: this.withdrawalcustID,
                    withDrawCreditdocId: withDrawCreditdocId,
                    mvnoId: Number(this.mvnoId),
                    paymentreferenceno: this.manageBalanceGroupForm.value.referenceNo,
                    paymode: this.manageBalanceGroupForm.value.mode,
                    paytype: "payment",
                    referenceno: this.manageBalanceGroupForm.value.referenceNo,
                    remark: this.manageBalanceGroupForm.value.remarks,
                    type: "Payment",
                };
                this.withdrawalAmount(data);
            } else if (this.manageBalanceGroupForm.value.mode == "Online") {
                let data = {
                    amount: Number(this.manageBalanceGroupForm.value.withdrawalAmount),
                    bank: this.manageBalanceGroupForm.value.bank,
                    branch: this.manageBalanceGroupForm.value.branch,
                    bankManagement: "",
                    customerid: this.withdrawalcustID,
                    withDrawCreditdocId: withDrawCreditdocId,
                    mvnoId: Number(this.mvnoId),
                    paymentreferenceno: this.manageBalanceGroupForm.value.referenceNo,
                    paymode: this.manageBalanceGroupForm.value.mode,
                    paytype: "payment",
                    referenceno: this.manageBalanceGroupForm.value.referenceNo,
                    remark: this.manageBalanceGroupForm.value.remarks,
                    type: "Payment",
                };
                this.withdrawalAmount(data);
            }
        }
    }

    withdrawalAmount(data) {
        if (data.withdrawAmount != 0) {
            let url = "/withdraw/payment";
            this.revenueManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.walletCustomerID.emit(this.withdrawalcustID);
                    this.ifRedirectManageBalance = false;

                    // this.closeSelectStafff();

                    this.toast.success(`Withdrawal successful`, 'Success!');
                    this.displayDialogWithDraw = false;
                    this.closeSelectStaff.emit(this.selectedStaffCust);
                    this.dialog.closeAll()
                },
                (error: any) => {
                    this.toast.error(`${error.error.ERROR}`, 'Failed!')
                    this.displayDialogWithDraw = false;
                    this.dialog.closeAll()
                    this.closeSelectStaff.emit(this.selectedStaffCust);
                }
            );
        } else {
            this.toast.info('You have rejected the transfer because the value of the Withdrawal Amount is Zero', 'Information');
        }
    }

    closeSelectStafff() {
        this.displayDialogWithDraw = false;
        this.dialog.closeAll()
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

    validationWithdrawalCommission() {
        this.manageBalanceGroupForm.get("mode").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("mode").updateValueAndValidity();
        this.manageBalanceGroupForm.get("remarks").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("remarks").updateValueAndValidity();
        this.manageBalanceGroupForm.get("referenceNo").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("referenceNo").updateValueAndValidity();

        this.manageBalanceGroupForm.get("withdrawalAmount").setValidators([Validators.required]);
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
        this.manageBalanceGroupForm.get("bank").setValidators([Validators.required]);
        this.manageBalanceGroupForm.get("bank").updateValueAndValidity();
        this.manageBalanceGroupForm.get("branch").setValidators([Validators.required]);
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
                this.paginatedPaymentData.data = this.paymentListData;
                this.paymentListDataselector = response.dataList;
                this.paymentListdatatotalRecords = response.totalRecords || response.dataList.length;
                this.paymentListDatalength = this.paymentListData.length;
            },
            (error: any) => {
                // console.log(error, "error")

                this.toast.error(`${error.error.ERROR}`, 'Failed!')
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

    pageChangedForPayments(pageNumber): void {
        this.currentPagePaymentListdata = pageNumber.pageIndex + 1;
        this.paymentListdataitemsPerPage = pageNumber.pageSize;
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
