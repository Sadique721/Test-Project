import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { BehaviorSubject } from "rxjs";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { MatDialog } from "@angular/material/dialog";
import { MatTab } from "@angular/material/tabs";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-customer-wallet",
    templateUrl: "./customer-wallet.component.html",
    styleUrls: ["./customer-wallet.component.scss"],
    standalone: false
})
export class CustomerWalletComponent implements OnInit {
    @Input() cafCustomerID: any
    @Input() cafCustomerType: any

    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    displayDialogWithDraw: boolean = false;
    displayDialogTransfer: boolean = false;
    withdrawalAmountAccess: boolean = false;
    customerId: number;
    getWallatData: any = [];
    WalletAmount: any = "";
    currency: string;
    customerLedgerDetailData: any;
    wCustID = new BehaviorSubject({
        wCustID: "",
        WalletAmount: ""
    });
    showWalletDetails: boolean = false;
    walletAuditList: any = [];
    currentPageWalletAuditSlab = 1;
    walletAuditItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    walletAuditTotalRecords: number = 0;
    pageLimitOptionsWalletAudit = RadiusConstants.pageLimitOptions;
    walletAuditShowItemPerpage: any;
    @ViewChild("auditDialog") walletAuditTable: any;
    paginatedWalletAuditList = new MatTableDataSource<any>([]);
    auditColumns = ['fromName', 'toName', 'amount', 'createdDate'];

    constructor(
        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private revenueManagementService: RevenueManagementService,
        private route: ActivatedRoute,
        private systemService: SystemconfigService,
        private router: Router,
        loginService: LoginService,
        private messageService: MessageService,
        private dialog: MatDialog,
        private toast: ToastrService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.withdrawalAmountAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_WALLET
                : POST_CUST_CONSTANTS.POST_CUST_WALLET
        );
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    }

    async ngOnInit() {
        if (this.cafCustomerID) {
            this.customerId = this.cafCustomerID
            this.custType = this.cafCustomerType;
            this.withdrawalAmountAccess = true
        }
        this.addWalletIncustomer();
        this.getCustomersDetail(this.customerId);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.customerLedgerDetailData = response.customers;
            this.customerLedgerDetailData?.currency
                ? (this.currency = this.customerLedgerDetailData?.currency)
                : this.systemService
                    .getConfigurationByName("CURRENCY_FOR_PAYMENT")
                    .subscribe((res: any) => {
                        this.currency = res.data.value;
                    });
        });
    }
    @Output() backToList = new EventEmitter<void>();
    @Input() isFromCaf?: boolean = false;

    customerDetailOpen() {
        if (this.isFromCaf) {
            this.backToList.emit();
            return;
        }
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    addWalletIncustomer() {
        const data = {
            CREATE_DATE: "",
            END_DATE: "",
            amount: "",
            balAmount: "",
            custId: this.customerId,
            description: "",
            id: "",
            refNo: "",
            transcategory: "",
            transtype: ""
        };
        const url = "/wallet";
        this.revenueManagementService.postMethod(url, data).subscribe((response: any) => {
            this.getWallatData = response;
            this.WalletAmount = response.customerWalletDetails;
        });
    }

    closeSelectStaff() {
        this.displayDialogWithDraw = false;
        this.displayDialogTransfer = false;
    }

    selectedStaffChange(event) {
        this.displayDialogWithDraw = false;
        this.displayDialogTransfer = false;
    }

    withdrawalAmountModel(modelID, wCustID, WalletAmount) {
        this.displayDialogWithDraw = true;
        // this.PaymentamountService.show(modelID);
        this.wCustID.next({
            wCustID,
            WalletAmount
        });
    }

    transferAmountModel(modelID, wCustID, WalletAmount) {
        this.displayDialogTransfer = true;
        // this.PaymentamountService.show(modelID);
        this.wCustID.next({
            wCustID,
            WalletAmount
        });
    }

    getChildParentWalletPaymentAudit() {
        this.showWalletDetails = true;
        this.dialog.open(this.walletAuditTable, {
            width: "800px",
        });
        this.getWalletAuditData("");
    }

    getWalletAuditData(list: any) {
        let page = this.currentPageWalletAuditSlab;
        if (list) {
            this.walletAuditItemsPerPage = list;
        }
        let size = this.walletAuditItemsPerPage;
        const url = `/transfer/getTransferAudit?custId=${this.customerId}`;
        let obj = {
            page: page,
            pageSize: size
        };
        this.revenueManagementService.postMethod(url, obj).subscribe(
            (response: any) => {
                this.walletAuditList = response.dataList;
                this.paginatedWalletAuditList.data = this.walletAuditList;
                this.walletAuditTotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toast.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }

    closeWalletAudit() {
        this.showWalletDetails = false;
        this.currentPageWalletAuditSlab = 0;
        this.walletAuditShowItemPerpage = 0;
        this.walletAuditTotalRecords = 0;
    }

    pageChangedWalletAuditList(pageNumber) {
        this.currentPageWalletAuditSlab = pageNumber.pageIndex + 1;
        this.getWalletAuditData(this.walletAuditShowItemPerpage);
    }

    TotalItemPerPageWalletAudit(event) {
        this.walletAuditShowItemPerpage = Number(event.value);

        if (this.currentPageWalletAuditSlab > 1) {
            this.currentPageWalletAuditSlab = 1;
        }
        this.getWalletAuditData(this.walletAuditShowItemPerpage);
    }
}
