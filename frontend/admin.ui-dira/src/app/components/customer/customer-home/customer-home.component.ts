import { DataSource } from "@angular/cdk/collections";
import { Component, OnDestroy, ViewChild } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { url } from "inspector";
import { ToastrService } from "ngx-toastr";
import { co } from "node_modules/@fullcalendar/core/internal-common";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import { TicketManagementComponent } from "../../ticket-management/ticket-management.component";
import { MatDialog } from "@angular/material/dialog";
import { MatTabGroup } from "@angular/material/tabs";
import { PRE_CUST_CONSTANTS, POST_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { LoginService } from "src/app/service/login.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";

@Component({
    selector: "app-customer-home",
    templateUrl: "./customer-home.component.html",
    styleUrl: "./customer-home.component.css",
    standalone: false
})
export class CustomerHomeComponent implements OnDestroy {
    customerId: number;
    custType: string;
    getWallatData: any = [];
    WalletAmount: any = "";
    customerDetailSubject: any;
    renewPaymentLinkAccess: boolean = false;
    PRE_CUST_CONSTANTS = PRE_CUST_CONSTANTS;
    POST_CUST_CONSTANTS = POST_CUST_CONSTANTS;
    editAccess: boolean = false;
    riseTicketAccess: boolean = false;

    customeroverviewDetails: any = {
        customerInformation: {},
        planInformation: {},
        ticketsList: new MatTableDataSource([]),
        ticketsListClosed: new MatTableDataSource([]),
        paymentsHistory: new MatTableDataSource([]),
        outstandingAmount: new MatTableDataSource([])
    };
    sendPaymentLinkAccess: boolean = false;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private customerManagementService: CustomermanagementService,
        private liveUserService: LiveUserService,
        private ticketManagementService: TicketManagementService,
        private revenueService: RevenueManagementService,
        private toastr: ToastrService,
        private dialog: MatDialog,
        private loginService: LoginService,
        public statusCheckService: StatusCheckService,
        public commondropdownService: CommondropdownService


    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;

        this.renewPaymentLinkAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.RENEW_PAYMENT_PRE_CUST
                : POST_CUST_CONSTANTS.SEND_PAYMENT_LINK_POST_CUST
        );
        this.editAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.EDIT_PRE_CUST
                : POST_CUST_CONSTANTS.EDIT_POST_CUST_LIST
        );
        this.riseTicketAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_TICKETS
                : POST_CUST_CONSTANTS.POST_CUST_TICKETS
        ) && statusCheckService.isActiveTicketService

        this.sendPaymentLinkAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.SEND_PAYMENT_LINK_PRE_CUST
                : POST_CUST_CONSTANTS.SEND_PAYMENT_LINK_POST_CUST
        );
    }
    ngOnDestroy(): void {
        if (this.customerDetailSubject) {
            this.customerDetailSubject.unsubscribe();
        }
    }

    isOverviewShow: boolean = true;
    isProfileShow: boolean = false;
    isTicketsShow: boolean = false;
    isInvoicesShow: boolean = false;
    isPaymentsShow: boolean = false;
    isPlansShow: boolean = false;

    ngOnInit(): void {
        this.getCustomerDetails();
        this.getPlanDetails();
        this.getPaymentsHistory();
        this.outstandingAmount();
        this.getWalletAmount();
    }

    getLiveUser(username: string) {
        this.liveUserService
            .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                users: [username]
            })
            .subscribe({
                next: (res: any) => {
                    const liveUsers: string[] = res.liveUsers || res.liveusers || [];
                    if (liveUsers.includes(username)) {
                        this.customeroverviewDetails.customerInformation.connectionStatus = "Online";
                    } else {
                        this.customeroverviewDetails.customerInformation.connectionStatus = "Offline";
                    }
                },
                error: (err: any) => {
                    console.log("Error", err);
                }
            });
    }
    formatDate(dateString: string): string {
        if (!dateString) return "";

        const [date] = dateString.split(" ");

        const [day, month, year] = date.split("-");
        return `${day}-${month}-${year}`;
    }

    customerCurrentData: any
    getCustomerDetails() {
        const url = "/customers/" + this.customerId;

        this.customerDetailSubject = this.customerManagementService.customerDetail$.subscribe({
            next: (res: any) => {
                if (res) {
                    this.customerCurrentData = res.customers
                    this.getLiveUser(res.customers.username);
                    this.getTickets(res.customers.username);
                    this.getClosedTickets(res.customers.username);
                    // this.getTickets("In Progress", res.customers.username);
                    // this.getTickets("Resolved", res.customers.username);
                    // this.getTickets("Raise and Close", res.customers.username);
                    // this.getTickets("Closed", res.customers.username);
                    this.customeroverviewDetails.customerInformation = {
                        name: `${res.customers.firstname} ${res.customers.lastname}`,
                        id: res.customers.id,
                        account: res.customers.acctno ?? "N/A",
                        status: res.customers.status,
                        wallet: res.customers.walletbalance ?? "N/A",
                        currency: res.customers.currency ?? "N/A"
                    };
                }
            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }

    getPlanDetails() {
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerId +
            "?isAllRequired=true&isNotChangePlan=true";
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.custCurrentPlanList = response.dataList;
                this.customeroverviewDetails.planInformation = {
                    name:
                        response.dataList && response.dataList.length > 0
                            ? response.dataList[0].planName
                            : "N/A",
                    renewalDate:
                        response.dataList && response.dataList.length > 0 ? response.dataList[0].endDate : "N/A"
                };

            },
            (error: any) => { }
        );
    }

    // getTickets(status: string, username: string) {
    //     const url = "/case/case/search";
    //     const data = {
    //         // filters: [
    //         //     { filterValue: status, filterColumn: "TICKET_STATUS" },
    //         //     { filterValue: username, filterColumn: "CUSTOMER_USERNAME" }
    //         // ],
    //         "filters": [
    //             {
    //                 "filterColumn": "TICKET_STATUS",
    //                 "filterListValues": [
    //                     "In Progress",
    //                     "open",
    //                     "Raise and Close",
    //                     "Closed",
    //                     "Resolved"
    //                 ]
    //             },
    //             {
    //                 "filterValue": username,
    //                 "filterColumn": "CUSTOMER_USERNAME"
    //             }
    //         ],
    //         page: 1,
    //         pageSize: 20,
    //         sortBy: "createdate",
    //         sortOrder: 0
    //     };
    //     this.ticketManagementService.postMethod(url, data).subscribe({
    //         next: (res: any) => {
    //             console.log("Tickets", res);
    //             // if (status === "open" || status === "In Progress" || status === 'Resolved') this.customeroverviewDetails.ticketsList.data = res.dataList;
    //             res.dataList.forEach(element => {
    //                 if (element.caseStatus === "Open" || element.caseStatus === "In Progress" || element.caseStatus === "Resolved"){
    //                     this.customeroverviewDetails.ticketsList.data = res.dataList;
    //                 } else {
    //                     this.customeroverviewDetails.ticketsListClosed.data = res.dataList;
    //                 }

    //             });

    //         },
    //         error: (err: any) => {
    //             console.log("Error", err);
    //         }
    //     });
    // }
    getClosedTickets(username: string) {
        const url = "/case/case/search";

        const data = {
            filters: [
                {
                    filterColumn: "TICKET_STATUS",
                    filterListValues: [
                        "Raise and Close",
                        "Closed"
                    ]
                },
                {
                    filterValue: username,
                    filterColumn: "CUSTOMER_USERNAME"
                },
                { filterValue: "true", filterColumn: "CUST_360" }
            ],
            page: 1,
            pageSize: 3,
            sortBy: "createdate",
            sortOrder: 0
        };

        this.ticketManagementService.postMethod(url, data).subscribe({
            next: (res: any) => {

                const allTickets = res.dataList || [];


                this.customeroverviewDetails.ticketsListClosed.data = allTickets
                    .filter(t =>
                        ["Raise and Close", "Closed"].includes(t.caseStatus)
                    )
                    .slice(-3);

                this.customeroverviewDetails.ticketsListClosed._updateChangeSubscription?.();
            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }

    getTickets(username: string) {
        const url = "/case/case/search";

        const data = {
            filters: [
                {
                    filterColumn: "TICKET_STATUS",
                    filterListValues: [
                        "In Progress",
                        "Open",
                        "Resolved"
                    ]
                },
                {
                    filterValue: username,
                    filterColumn: "CUSTOMER_USERNAME"
                },
                { filterValue: "true", filterColumn: "CUST_360" }
            ],
            page: 1,
            pageSize: 20,
            sortBy: "createdate",
            sortOrder: 0
        };

        this.ticketManagementService.postMethod(url, data).subscribe({
            next: (res: any) => {

                const allTickets = res.dataList || [];
                this.customeroverviewDetails.ticketsList.data = allTickets.filter(t =>
                    ["Open", "In Progress", "Resolved"].includes(t.caseStatus)
                );
                this.customeroverviewDetails.ticketsList._updateChangeSubscription?.();
                this.customeroverviewDetails.ticketsListClosed._updateChangeSubscription?.();
            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }


    getWalletAmount() {
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
        this.revenueService.postMethod(url, data).subscribe((response: any) => {
            this.getWallatData = response;
            this.WalletAmount = response.customerWalletDetails;


        });
    }

    get firstThreePayments() {
        return this.customeroverviewDetails?.paymentsHistory?.data.slice(0, 3) || [];
    }

    getPaymentsHistory() {
        const url = '/paymentHistory/' + this.customerId;
        this.revenueService.getMethod(url).subscribe({
            next: (res: any) => {
                // this.customeroverviewDetails.ticketsList.data = res.dataList;
                const filteredPayments = (res.dataList || []).filter(
                    (item: any) => item.type === 'Payment'
                );
                this.customeroverviewDetails.paymentsHistory.data = filteredPayments;

            },
            error: (err: any) => {
                console.log("Error", err);
            }
        });
    }


    // sendPaymentLink() {
    //     this.customerManagementService.postMethod(url, null).subscribe(
    //         (response: any) => {
    //             if (response.responseCode === 417) {
    //                 this.toastr.success(`${response.message}`, 'Success!');
    //             } else {
    //                 let payData = response.data;
    //                 if (response.data == null) {
    //                     this.toastr.info(`${response.responseMessage}`, 'Something went wrong!');

    //                 } else {
    //                     window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
    //                 }
    //             }
    //         }
    //     );


    //     // next: (res: any) => {
    //     // },
    //     // error: (err: any) => {
    //     //     this.toastr.error(`${err.error.msg}`, 'Error');
    //     // }
    // }

    sendPaymentLink(custId, isRenew) {
        const url = "/generatePaymentLink/" + custId;
        this.customerManagementService.postMethod(url, null).subscribe(
            (response: any) => {
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Into!');

                } else {
                    let payData = response.data;
                    if (response.data == null) {
                        this.toastr.info(`${response.responseMessage}`, 'Something went wrong!');

                    } else {
                        window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
                    }
                }
            },
            (error: any) => {
                console.log(error, "error");
                if (error.responseCode === 417) {
                    this.toastr.info(`${error.responseMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');



                }
            }
        );
    }

    raiseTicket() {
        const dialogRef = this.dialog.open(TicketManagementComponent, {
            width: '80%',
            data: {
                customerDetailsData: this.customerCurrentData
            }
        });
        dialogRef.afterClosed().subscribe(result => {

        });
        // this.router.navigate([`/home/customer/details/Prepaid/tickets/${this.customerId}`]);
    }

    outstandingAmount() {

        const url =
            "/invoice/search?billrunid=" +
            "&docnumber=" +
            "&customerid=" +
            this.customerId +
            "&billfromdate=" +
            "&billtodate=" +
            "&custmobile=" +
            "&isInvoiceVoid=true" +
            "&isOutstandingDue=true"
            ;

        this.revenueService.postMethod(url, { page: 1, pageSize: 1000 }).subscribe({
            next: (res: any) => {
                this.customeroverviewDetails.outstandingAmount.data = res.invoicesearchlist;
            }
        });
    }

    getConnectionStatusClass(status: string): string {
        switch (status.toLowerCase()) {
            case "online":
                return "Online";
            case "offline":
                return "Offline";
            case "active":
                return "Active";
            case "inactive":
                return "Inactive";
            case "Rejected":
                return "Rejected";
            case "NewActivation":
                return "NewActivation";
            default:
                return "Expired";
        }
    }

    editProfile() {
        this.router.navigate([`/home/customer/edit/${this.custType}/${this.customerId}`]);
    }

    customerData = {
        name: "Lemor Nova",
        id: "8303789",
        account: "27-628267",
        status: "Active",
        connectionStatus: "Online"
    };

    planData = {
        name: "Family 70 GB",
        remainingDays: 9
    };

    walletData = {
        currency: "KES",
        amount: 6300
    };

    usageData = {
        used: 47,
        total: 70
    };

    outstandingData = {
        currency: "UGX",
        amount: 15000
    };

    paymentsData = [
        {
            payment: "0000465",
            dueDate: "01-Jun-2025",
            amount: "UGX 3000",
            amountClass: "amount-green"
        },
        {
            payment: "0000575",
            dueDate: "25-Jun-2025",
            amount: "UGX 2500",
            amountClass: "amount-orange"
        }
    ];

    lastPayments = [
        {
            payment: "0000365",
            dueDate: "30-Jun-2025",
            amount: "UGX 3000",
            amountClass: "amount-green"
        }
    ];

    openTickets = [
        {
            ticketNo: "2578",
            type: "Inquiry",
            started: "07-July-2025",
            call: "Received"
        }
    ];

    closedTickets = [
        {
            ticketNo: "2888",
            type: "Inquiry",
            started: "25-July-2025",
            call: "Received"
        }
    ];

    paymentColumns = ["paymentdate", "paymode", "paymentreferenceno", "amount", "adjustedAmount", "status"];
    outstandingColumns = ["docnumber", "createdate", "totalamount", "adjustedAmount"];
    ticketColumns = ["ticketNo", "type", "started", "call"];

    onTabChange(event: any) {
        this.isOverviewShow = event.tab.textLabel === "Overview";
        this.isProfileShow = event.tab.textLabel === "Profile";
        this.isTicketsShow = event.tab.textLabel === "Tickets";
        this.isInvoicesShow = event.tab.textLabel === "Invoices";
        this.isPaymentsShow = event.tab.textLabel === "Payments";
        this.isPlansShow = event.tab.textLabel === "Plans";
    }
    @ViewChild('tabGroup') tabGroup: MatTabGroup;

    handleBackToList() {
        this.tabGroup.selectedIndex = 0;
        this.isOverviewShow = true;
        this.isProfileShow = false;
        this.isTicketsShow = false;
        this.isInvoicesShow = false;
        this.isPaymentsShow = false;
        this.isPlansShow = false;
    }

    getUsagePercentage(): number {
        if (this.usageData.total === 0) return 0;
        return (this.usageData.used / this.usageData.total) * 100;
    }
}
