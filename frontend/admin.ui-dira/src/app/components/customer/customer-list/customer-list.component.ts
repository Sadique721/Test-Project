import { DatePipe } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import {
    AREA,
    CITY,
    COUNTRY,
    PINCODE,
    STATE,
    CUSTOMER_PREPAID,
    CUSTOMER_POSTPAID
} from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { LoginService } from "src/app/service/login.service";
import { ActivatedRoute, Router } from "@angular/router";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import * as FileSaver from "file-saver";
import jsPDF from "jspdf";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { CustNotes } from "../../model/CustNotes";
import { BehaviorSubject } from "rxjs/internal/BehaviorSubject";
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { CustomerNearByDevicesComponent } from '../../customer-near-by-devices/customer-near-by-devices.component'
import { CustChangeStatusComponent } from "../../cust-change-status/cust-change-status.component";
declare var $: any;

@Component({
    selector: "app-customer-list",
    templateUrl: "./customer-list.component.html",
    styleUrls: ["./customer-list.component.scss"],
    standalone: false
})
export class CustomerListComponent implements OnInit {
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild("addNotesDialogTemplate") addNotesDialogTemplate;
    dataSource: MatTableDataSource<any>;
    addNotesDialogRef!: MatDialogRef<any>;
    displayedColumns: string[] = [
        'name',
        'username',
        'serviceArea',
        'mobile',
        'acctno',
        'connectionMode',
        'mvnoName',
        'action'
    ];
    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    custTitle = CUSTOMER_PREPAID;
    currentPagecustomerListdata = 1;
    customerListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    customerListdatatotalRecords: number = 0;
    customerListData: any = [];
    viewcustomerListData: any = [];
    searchOption = "";
    searchDeatil = "";
    searchData;
    AclClassConstants;
    AclConstants;
    searchkey2: string;
    searchkey: string;
    searchOptionSelect = this.commondropdownService.customerSearchOption;
    isPlanOnDemand = false;
    showNearLocationModal = false;
    showChangeStatusModal = false;
    uploadAccess: boolean = false;
    notesAccess: boolean = false;
    editAccess: boolean = false;
    nearByDeviceAccess: boolean = false;
    sendPaymentLinkAccess: boolean = false;
    changeStatusAccess: boolean = false;
    selectedCustId = 0;
    custStatus = "";
    fromDate = "";
    toDate = "";
    addNotesForm: UntypedFormGroup;

    cols = [
        {
            field: "name",
            header: "Name",
            customExportHeader: "Name"
        },
        {
            field: "username",
            header: "Username",
            customExportHeader: "Username"
        },
        {
            field: "serviceArea",
            header: "Service Area",
            customExportHeader: "Service Area"
        },
        {
            field: "mobile",
            header: "Mobile Number",
            customExportHeader: "Mobile Number"
        },
        {
            field: "acctno",
            header: "Account No",
            customExportHeader: "Account No"
        },
        {
            field: "connectionMode",
            header: "	Connection Status",
            customExportHeader: "	Connection Status"
        },
        {
            field: "mvnoName",
            header: "ISP Name",
            customExportHeader: "ISP Name"
        }
    ];
    public buttonText: string;
    public buttonRouterLink: string[];
    customerType: any;
    mvnoid: number;
    staffid: number;
    renewPaymentLinkAccess: boolean = false;
    addNotesAccess: boolean = false;
    custId: number;
    dialog: any = false
    createAccess: boolean = false;
    activeStaffListNameSort: any;

    constructor(
        private toastr: ToastrService,
        private dialogs: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private customerManagementService: CustomermanagementService,
        public commondropdownService: CommondropdownService,
        public datepipe: DatePipe,
        public loginService: LoginService,
        public invoicePaymentListService: InvoicePaymentListService,
        private datePipe: DatePipe,
        private route: ActivatedRoute,
        private router: Router,
        private liveUserService: LiveUserService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public matDialog: MatDialog,
    ) {
        this.mvnoid = Number(localStorage.getItem("mvnoId"));
        this.staffid = Number(localStorage.getItem("userId"));
        this.custType = this.route.snapshot.paramMap.get("custType")!;
        const type = this.custType.toLowerCase();

        if (type === 'prepaid') {
            this.buttonText = 'Add Prepaid Customer';
            this.buttonRouterLink = ['/home/customer/create', 'Prepaid'];
        } else if (type === 'postpaid') {
            this.buttonText = 'Add Postpaid Customer';
            this.buttonRouterLink = ['/home/customer/create', 'Postpaid'];
        } else {
            // Default or handle other cases if necessary
            this.buttonText = 'Add Customer';
            this.buttonRouterLink = ['/home/customer/create', this.custType];
        }
        let staffID = localStorage.getItem("userId");
        let loggedInUser = localStorage.getItem("loggedInUser");
        this.partnerId = Number(localStorage.getItem("partnerId"));
        this.dataSource = new MatTableDataSource([]);
        this.editAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.EDIT_PRE_CUST
                : POST_CUST_CONSTANTS.EDIT_POST_CUST_LIST
        );
        this.uploadAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.UPLOAD_DOCS_PRE_CUST
                : POST_CUST_CONSTANTS.UPLOAD_DOCUMENTS_POST_CUST_LIST
        );
        this.nearByDeviceAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_NEAR_BY_DEVICE
                : POST_CUST_CONSTANTS.POST_CUST_NEAR_BY_DEVICE
        );
        this.sendPaymentLinkAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.SEND_PAYMENT_LINK_PRE_CUST
                : POST_CUST_CONSTANTS.SEND_PAYMENT_LINK_POST_CUST
        );
        this.changeStatusAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.CHANGE_STATUS_PRE_CUST
                : POST_CUST_CONSTANTS.CHANGE_STATUS_POST_CUST
        );
        this.custType == "Prepaid"
            ? (this.custTitle = CUSTOMER_PREPAID)
            : (this.custTitle = CUSTOMER_POSTPAID);
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.renewPaymentLinkAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.RENEW_PAYMENT_PRE_CUST
                : POST_CUST_CONSTANTS.SEND_PAYMENT_LINK_POST_CUST
        );
        this.addNotesAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.ADD_NOTES_PRE_CUST
                : POST_CUST_CONSTANTS.SEND_PAYMENT_LINK_POST_CUST
        );
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    async ngOnInit() {
        this.createAccess = this.loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.CREATE_PRE_CUST
                : POST_CUST_CONSTANTS.CREATE_POST_CUST_LIST
        );
        this.commondropdownService.getAllActiveStaff();
        this.commondropdownService.getTeamList();
        this.commondropdownService.getCustomerStatus();
        // this.getCustomerList("");
        this.getCustomerType();

        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: "",
            pageSize: "",
            fromDate: "",
            toDate: "",
            status: ""
        };
        this.addNotesForm = this.fb.group({
            id: [""],
            notes: ["", Validators.required]
        });
        this.route.queryParams.subscribe(params => {
            let mobileno = params["mobilenumber"];
            if (mobileno) {
                this.searchOption = "mobile";
                this.searchDeatil = mobileno;
            } else {
                this.searchOption = "any";
                if (this.custType === 'Prepaid' && this.customerManagementService.getCustomerPrepaidSearchData()) {
                    this.searchOption = this.customerManagementService.getCustomerPrepaidSearchData()?.searchOption ?? "";
                    this.searchDeatil = this.customerManagementService.getCustomerPrepaidSearchData()?.searchDeatil ?? "";
                    this.fromDate = this.customerManagementService.getCustomerPrepaidSearchData()?.fromDate ?? "";
                    this.toDate = this.customerManagementService.getCustomerPrepaidSearchData()?.toDate ?? "";
                }

            }
            this.searchCustomer();
        });
        this.getStaffNameSort();
    }

    custIdForNotes: any;
    addNotesPopup: boolean = false;
    notesSubmitted: boolean = false;
    addNotesData: CustNotes;

    addNotesSetFunction(custId: number) {
        this.custIdForNotes = custId;
        this.addNotesDialogRef = this.matDialog.open(this.addNotesDialogTemplate, {
            width: '600px'
        });
    }

    getStaffNameSort() {
        const url = "/staffList/dropdown/all2";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.activeStaffListNameSort = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    // saveNotes(leadId: any) {
    //     this.notesSubmitted = true;
    //     console.log("hello called ! ",this.addNotesForm.value)
    //     console.log("leadId ! ",leadId)
    //     if (this.addNotesForm.valid) {
    //         if (leadId) {
    //             console.log("valid",leadId)
    //             const url = "/add/notes";
    //             this.addNotesData = {
    //                 id: 0,
    //                 custId: leadId,
    //                 notes: this.addNotesForm.controls.notes.value
    //             };
    //             this.customerManagementService
    //                 .postMethodForCustNotes(url, this.addNotesData, this.mvnoid, this.staffid)
    //                 .subscribe(
    //                     (response: any) => {
    //                         this.notesSubmitted = false;
    //                         if (response.status == 406) {
    //                             this.addNotesPopup = false;
    //                             this.addNotesForm.reset();
    //                             this.toastr.error(`${response.message}`, 'Failed!');


    //                         } else {
    //                             if (!this.searchkey) {
    //                                 this.getCustomerList("");
    //                             } else {
    //                                 this.searchCustomer();
    //                             }
    //                             this.addNotesPopup = false;
    //                             this.addNotesForm.reset();
    //                             this.toastr.success(`${response.message}`, 'Success!');


    //                         }
    //                     },
    //                     (error: any) => {
    //                         this.addNotesPopup = false;
    //                         this.addNotesForm.reset();
    //                         this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


    //                     }
    //                 );
    //         } else {
    //             this.addNotesForm.reset();
    //             this.addNotesPopup = false;
    //             (error: any) => {

    //                 this.toastr.error(`${error.error.errorMessage}`, 'Lead Id is missing!');


    //             }

    //         }
    //     } else {
    //         (error: any) => {

    //             this.toastr.error(`${error.error.errorMessage}`, 'Required column is missing!');


    //         }

    //         this.addNotesPopup = true;
    //     }
    // }

    saveNotes() {
        this.notesSubmitted = true;
        if (this.addNotesForm.valid) {
            if (this.custIdForNotes) {  // Use the class property here
                const url = "/add/notes";
                this.addNotesData = {
                    id: 0,
                    custId: this.custIdForNotes,
                    notes: this.addNotesForm.controls.notes.value
                };
                this.customerManagementService
                    .postMethodForCustNotes(url, this.addNotesData, this.mvnoid, this.staffid)
                    .subscribe(
                        (response: any) => {
                            this.notesSubmitted = false;
                            if (response.status == 406) {
                                this.addNotesPopup = false;
                                this.addNotesForm.reset();
                                this.toastr.error(`${response.message}`, 'Failed!');
                            } else {
                                if (!this.searchkey) {
                                    this.getCustomerList("");
                                } else {
                                    this.searchCustomer();
                                }
                                this.addNotesPopup = false;
                                this.addNotesForm.reset();
                                this.toastr.success(`${response.message}`, 'Success!');
                                // Close the dialog here after success
                                if (this.addNotesDialogRef) {
                                    this.addNotesDialogRef.close();
                                }
                            }
                        },
                        (error: any) => {
                            this.addNotesPopup = false;
                            this.addNotesForm.reset();
                            this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                            // Close the dialog here after success
                            if (this.addNotesDialogRef) {
                                this.addNotesDialogRef.close();
                            }
                        }
                    );
            } else {
                this.addNotesForm.reset();
                this.addNotesPopup = false;
                this.toastr.error('Lead Id is missing!', 'Failed!');
            }
        } else {
            this.toastr.error('Required field is missing!', 'Failed!');
            this.addNotesPopup = true;
        }
    }


    getCustomerList(list) {
        let size;
        this.searchkey = "";
        this.searchkey2 = "";
        const page = this.currentPagecustomerListdata;

        const url = `/customers/list/` + this.custType + "?orgcusttype=false";
        const custerlist = {
            page,
            pageSize: this.customerListdataitemsPerPage,
            status: "Active"
        };

        this.customerManagementService.postMethod(url, custerlist).subscribe(
            (response: any) => {
                if (response.status == 204) {

                    this.toastr.info(`${response.msg}`, 'Info!');

                    this.customerListData = [];
                    this.dataSource.data = []; // ADD THIS LINE
                    this.customerListdatatotalRecords = 0;
                } else {
                    this.customerListData = response.customerList;
                    this.dataSource = new MatTableDataSource<any>(this.customerListData);

                    const usernameList: string[] = [];
                    this.customerListData.forEach(element => {
                        usernameList.push(element.username);
                    });

                    this.liveUserService
                        .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                            users: usernameList
                        })
                        .subscribe((res: any) => {
                            const liveUsers: string[] = res.liveusers;
                            this.customerListData.forEach(element => {
                                if (liveUsers.findIndex(e => e == element.username) < 0) {
                                    element.connectionMode = "Offline";
                                } else {
                                    element.connectionMode = "Online";
                                }
                            });

                            // UPDATE DATASOURCE HERE - AFTER GETTING CONNECTION STATUS
                            this.dataSource.data = this.customerListData;
                        });

                    this.customerListdatatotalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    clearSearchcustomer() {
        this.currentPagecustomerListdata = 1;
        this.customerManagementService.setCustomerPrepaidSearchData(null);
        this.getCustomerList("");
        this.searchDeatil = "";
        this.searchOption = "";
        this.fromDate = "";
        this.toDate = "";
    }

    customerDetailsNavigate(custId) {
        this.router.navigate(['/home/customer/details/' + this.custType + '/x/', custId]);
    }


    selSearchOption(event) {
        // console.log("value", event.value);
        this.searchDeatil = "";
        // if (event.value) {
        //   this.fieldEnable = true;
        // } else {
        //   this.fieldEnable = false;
        // }
    }

    searchCustomer() {
        if (
            this.searchOption !== "cafCreatedDate" &&
            this.searchOption !== "expiryDate" &&
            this.searchOption !== "firstactivationdate"
        ) {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchDeatil.trim() ||
                !this.searchkey2 ||
                this.searchkey2 !== this.searchOption.trim()
            ) {
                // this.currentPagecustomerListdata = 1;
            }
            this.searchkey = this.searchDeatil.trim();
            this.searchkey2 = this.searchOption.trim();

            this.searchData.filters[0].filterValue = this.searchDeatil.trim();
            this.searchData.filters[0].filterColumn = this.searchOption.trim();
        } else {
            if (
                !this.searchkey ||
                this.searchkey !== this.searchDeatil ||
                !this.searchkey2 ||
                this.searchkey2 !== this.searchOption
            ) {
                this.currentPagecustomerListdata = 1;
            }
            let searchDeatil = this.datePipe.transform(this.searchDeatil, "yyyy-MM-dd");
            this.searchkey = searchDeatil;
            this.searchkey2 = this.searchOption;

            this.searchData.filters[0].filterValue = searchDeatil;
            this.searchData.filters[0].filterColumn = this.searchOption;
        }
        this.searchData.fromDate = this.datePipe.transform(this.fromDate, "yyyy-MM-dd");
        this.searchData.toDate = this.datePipe.transform(this.toDate, "yyyy-MM-dd");
        this.searchData.status = RadiusConstants.CUSTOMER_STATUS.ACTIVE;
        this.customerManagementService.setCustomerPrepaidSearchData({ searchDeatil: this.searchDeatil, searchOption: this.searchOption, fromDate: this.fromDate, toDate: this.toDate });
        if (this.searchOption == "service") {
            this.getSearchCustomerByService();
        } else {
            this.searchData.page = this.currentPagecustomerListdata;
            this.searchData.pageSize = this.customerListdataitemsPerPage;
            const url = "/customers/search/" + this.custType;
            // console.log("this.searchData", this.searchData)
            this.customerManagementService.postMethod(url, this.searchData).subscribe(
                (response: any) => {
                    if (response.status == 204) {
                        this.toastr.info(`${response.msg}`, 'Info!');

                        this.customerListData = [];
                        this.dataSource.data = []; // ADD THIS LINE
                        this.customerListdatatotalRecords = 0;
                    } else {
                        this.customerListData = response.customerList;
                        this.dataSource = new MatTableDataSource<any>(this.customerListData);


                        const usernameList: string[] = [];
                        this.customerListData.forEach(element => {
                            usernameList.push(element.username);
                        });

                        this.liveUserService
                            .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                                users: usernameList
                            })
                            .subscribe((res: any) => {
                                const liveUsers: string[] = res.liveusers;
                                this.customerListData.forEach(element => {
                                    if (liveUsers.findIndex(e => e == element.username) < 0) {
                                        element.connectionMode = "Offline";
                                    } else {
                                        element.connectionMode = "Online";
                                    }
                                });

                                // UPDATE DATASOURCE HERE
                                this.dataSource.data = this.customerListData;
                            });

                        this.customerListdatatotalRecords = response.pageDetails.totalRecords;
                    }
                },
                (error: any) => {
                    this.customerListdatatotalRecords = 0;
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.msg}`, 'Info!');

                        this.customerListData = [];
                        this.dataSource.data = []; // ADD THIS LINE
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                }
            );
        }
    }

    pageChangedcustomerList(pageNumber) {
        this.currentPagecustomerListdata = pageNumber;
        if (this.searchkey) {
            this.searchCustomer();
        } else {
            this.getCustomerList("");
        }
    }

    TotalItemPerPage(event) {
        this.customerListdataitemsPerPage = Number(event.value);
        if (this.currentPagecustomerListdata > 1) {
            this.currentPagecustomerListdata = 1;
        }
        if (!this.searchkey) {
            this.getCustomerList(this.customerListdataitemsPerPage);
        } else {
            this.searchCustomer();
        }
    }

    getSearchCustomerByService() {
        const url =
            "/getByCustomerService?page=" +
            this.currentPagecustomerListdata +
            "&pageSize=" +
            this.customerListdataitemsPerPage +
            "&service=" +
            this.searchDeatil +
            "&customerType=" +
            this.custType;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerListData = response.customers.content;

                const usernameList: string[] = [];
                this.customerListData.forEach(element => {
                    usernameList.push(element.username);
                });

                this.liveUserService
                    .postMethod("/liveUser/isCustomersOnlineOrOffline", {
                        users: usernameList
                    })
                    .subscribe((res: any) => {
                        const liveUsers: string[] = res.liveusers;
                        this.customerListData.forEach(element => {
                            if (liveUsers.findIndex(e => e == element.username) < 0) {
                                element.connectionMode = "Offline";
                            } else {
                                element.connectionMode = "Online";
                            }
                        });

                        // UPDATE DATASOURCE HERE
                        this.dataSource.data = this.customerListData;
                    });

                this.customerListdatatotalRecords = response.customers.totalElements;
                this.customerListdataitemsPerPage = response.pageDetails.totalRecordsPerPage;
                this.currentPagecustomerListdata = response.pageDetails.currentPageNumber;
            },
            (error: any) => {
                this.customerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.customerListData = [];
                    this.dataSource.data = []; // ADD THIS LINE
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    getPaytmLink(custId) {
        this.customerManagementService.getPaytmLink(custId).subscribe(
            (response: any) => {
                this.toastr.success(`${response.msg}`, 'Success!');


            },

            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    stopBilling(id) {
        const url = "/invoiceV2/stop/billing/" + id;
        let data = {};
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                if (this.searchkey) {
                    this.searchCustomer();
                } else {
                    this.getCustomerList("");
                } this.toastr.success(`${response.message}`, 'Success!');


            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    playstartBilling(id) {
        const url = "/invoiceV2/start/billing/" + id;
        let data = {};
        this.customerManagementService.postMethodPasssHeader(url, data).subscribe(
            (response: any) => {
                if (this.searchkey) {
                    this.searchCustomer();
                } else {
                    this.getCustomerList("");
                }
                this.toastr.success(`${response.message}`, 'Success!');


            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openChangeStatus(id, status) {
        this.selectedCustId = id;
        this.custStatus = status;
        this.showChangeStatusModal = true;
        const dialogRef = this.dialogs.open(CustChangeStatusComponent, {
            width: '900px',
            disableClose: true,
            data: {
                custId: id,
                custStatus: status,
                moduleType: 'cpm'
            },

        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.searchCustomer();
                dialogRef.close();
            }
        });
    }

    closeChangeStatusEvent(isStatusChanged) {
        this.selectedCustId = 0;
        this.custStatus = "";
        this.showChangeStatusModal = false;
        if (isStatusChanged) this.getCustomerList("");
    }

    openNearLocationModal(custId) {
        this.selectedCustId = custId;
        const dialogRef = this.matDialog.open(CustomerNearByDevicesComponent, {
            width: '1000px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: {
                custId: custId,
                customerId: custId, // Send both for compatibility
                type: 'nearByDevices'
            },
            disableClose: false,
            panelClass: 'custom-dialog-container',
            autoFocus: false
        });
        dialogRef.afterClosed().subscribe((result: any) => {

            if (result?.success) {
            }
        });
    }

    closeNearLocationModal() {
        this.showNearLocationModal = false;
        this.selectedCustId = 0;
    }

    exportCustomer() {
        import("xlsx").then(xlsxModule => {
            const xlsx = xlsxModule.default || xlsxModule;

            let z = this.customerListData.map((ele: any) => {
                let x = {};
                this.cols.forEach((d: any) => {
                    x = { ...x, [d.customExportHeader]: ele?.[d.field] };
                });
                return x;
            });

            const worksheet = xlsx.utils.json_to_sheet(z);
            const workbook = { Sheets: { data: worksheet }, SheetNames: ["data"] };
            const excelBuffer: any = xlsx.write(workbook, {
                bookType: "xlsx",
                type: "array"
            });
            this.saveAsExcelFile(excelBuffer, "Customer");
        });
    }

    saveAsExcelFile(buffer: any, fileName: string): void {
        let EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        let EXCEL_EXTENSION = ".xlsx";
        const data: Blob = new Blob([buffer], {
            type: EXCEL_TYPE
        });
        FileSaver.saveAs(data, fileName + "_Export_" + new Date().getTime() + EXCEL_EXTENSION);
    }

    getCustomerType() {
        const url = "/commonList/Customer_Type";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.customerType = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openPaymentGateways(custId, isRenew) {
        const url = "/generatePaymentLink/" + custId;
        this.customerManagementService.postMethod(url, null).subscribe(
            (response: any) => {
                let payData = response.data;
                if (response.data == null) {
                    this.toastr.info(`${response.responseMessage}`, 'No Unpaid Invoice Found for this Customer!');

                } else {
                    window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
                    //   this.router.navigate(["/customer/payMethod/" + payData]);
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    openRenewPaymentGateways(custId, isRenew) {
        const url = "/generatePaymentLink/" + custId;
        this.customerManagementService.postMethod(url, null).subscribe(
            (response: any) => {

                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    let payData = response.data;
                    if (response.data == null) {
                        this.toastr.info('Something went wrong!');

                    } else {
                        window.open(`${window.location.origin}/#/customer/payMethod/${payData}`);
                        //   this.router.navigate(["/customer/payMethod/" + payData]);
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

    openModal(custId) {
        // this.dialog = true;
        // this.custId = custId;
        this.dialog = false;    // Ensure closed first
        this.custId = null;     // Reset id first

        setTimeout(() => {
            this.dialog = true; // Open dialog after reset
            this.custId = custId; // Set new custId
        }, 0);
    }

    closeSelectStaff() {
        this.custId = null;
        this.dialog = false;
    }
    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    firstPage() {
        this.currentPagecustomerListdata = 1;
        if (this.searchkey) {
            this.searchCustomer();
        } else {
            this.getCustomerList("");
        }
    }

    previousPage() {
        if (this.currentPagecustomerListdata > 1) {
            this.currentPagecustomerListdata--;
            if (this.searchkey) {
                this.searchCustomer();
            } else {
                this.getCustomerList("");
            }
        }
    }

    nextPage() {
        const maxPage = Math.ceil(this.customerListdatatotalRecords / this.customerListdataitemsPerPage);
        if (this.currentPagecustomerListdata < maxPage) {
            this.currentPagecustomerListdata++;
            if (this.searchkey) {
                this.searchCustomer();
            } else {
                this.getCustomerList("");
            }
        }
    }

    lastPage() {
        this.currentPagecustomerListdata = Math.ceil(
            this.customerListdatatotalRecords / this.customerListdataitemsPerPage
        );
        if (this.searchkey) {
            this.searchCustomer();
        } else {
            this.getCustomerList("");
        }
    }

    pageChangedCustomerList(event: PageEvent): void {
        this.currentPagecustomerListdata = event.pageIndex + 1;
        this.customerListdataitemsPerPage = event.pageSize;

        // if (this.searchkey) {
        this.searchCustomer();
        // } else {
        //     this.getCustomerList("");
        // }
    }

}
