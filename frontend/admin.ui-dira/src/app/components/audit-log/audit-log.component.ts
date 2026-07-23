// import { Component, OnInit } from "@angular/core";
// import { UntypedFormBuilder, Validators, FormGroup, FormControl, FormArray } from "@angular/forms";
// import { MessageService } from "primeng/api";
// import { NgxSpinnerService } from "ngx-spinner";
// import { ConfirmationService } from "primeng/api";
// import { AuditlogService } from "src/app/service/auditlog.service";
// import { Regex } from "src/app/constants/regex";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import * as _ from "lodash";
// import { CommondropdownService } from "src/app/service/commondropdown.service";
// import { LoginService } from "src/app/service/login.service";
// import { AclClassConstants } from "src/app/constants/aclClassConstants";
// import { AclConstants } from "src/app/constants/aclOperationConstants";
// import { TicketManagementService } from "src/app/service/ticket-management.service";
// import { CountryManagementService } from "src/app/service/country-management.service";
// import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
// import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
// import { NotificationBaseService } from "src/app/service/notification-base.service";
// import { MatTableDataSource } from "@angular/material/table";
// import { MatPaginator, PageEvent } from '@angular/material/paginator';
// import { MatSort } from "@angular/material/sort";
// import { MatDialog } from "@angular/material/dialog";
// @Component({
//     selector: "app-audit-log",
//     templateUrl: "./audit-log.component.html",
//     styleUrls: ["./audit-log.component.css"],
//     standalone: false
// })
// export class AuditLogComponent implements OnInit {
//   dataSource = new MatTableDataSource<any>([]);
//   displayedColumns: string[] = [
//     'entityName', 'entityType', 'moduleName', 'actionType', 
//     'authorUserName', 'authorUserTeams', 'updatedOn', 
//     'ipAddress','snapshot'
//   ];
//   AuditlogCategoryList: any;
//   showDialogue: boolean = false;
//   jsondata: any;
//   snapshotdata: any;
//   currentPageAuditlogListdata = 1;
//   AuditlogListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
//   AuditlogListdatatotalRecords: any;
//   AuditlogListData: any = [];
//   viewAuditlogListData: any = [];
//   searchAuditlogUrl: any;

//   searchModuleName = "";
//   searchUserName = "";
//   searchDate = "";
//   toDate = "";
//   searchData: any;
//   searchKey: string = "";
//   AclClassConstants;
//   AclConstants;

//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   showItemPerPage: any;
//   searchkey: string;
//   activeIndex: number = 0;
//   searchOption: any;
//   searchOptions = [{ label: "Profile Name", value: "profile" }];
//   moduleList = [
//     { label: "Common", value: "COMMON" },
//     { label: "CMS", value: "CMS" },
//     { label: "Ticket", value: "TICKET" },
//     { label: "Inventory", value: "INVENTORY" },
//     { label: "Revenue", value: "REVENUE" },
//     { label: "Notification", value: "NOTIFICATION" },
//   ];
//   selectedModule: any = "COMMON";
//   selectedModuleName: any = "Common";
//   startDate: string;
//   endDate: string;
//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     public commondropdownService: CommondropdownService,
//     private messageService: MessageService,
//     private AuditlogService: AuditlogService,
//     private TicketManagementService: TicketManagementService,
//     private CountryManagementService: CountryManagementService,
//     public loginService: LoginService,
//     public savbillCommonBaseService: SavbillCommonBaseService,
//     public RevenueManagementService: RevenueManagementService,
//     public NotificationBaseService: NotificationBaseService
//   ) {
//     this.loginService = loginService;
//     this.AclClassConstants = AclClassConstants;
//     this.AclConstants = AclConstants;
//   }

//   ngOnInit(): void {
//     this.searchData = {
//       filters: [
//         {
//           filterValue: "",
//           filterColumn: "any",
//           filterOperator: "equalto",
//           filterCondition: "and",
//         },
//       ],
//       module: "",
//       fromDate: "",
//       toDate: "",
//       page: "",
//       pageSize: "",
//     };

//     this.getAuditlogList("", this.selectedModule);
//   }

//   TotalItemPerPage(event, module) {
//     this.showItemPerPage = Number(event.value);
//     if (this.currentPageAuditlogListdata > 1) {
//       this.currentPageAuditlogListdata = 1;
//     }
//     if (!this.searchKey && (!this.startDate || !this.endDate)) {
//       this.getAuditlogList("", this.selectedModule);
//     } else {
//       this.searchAudit();
//     }
//   }

//   getAuditlogList(list, module) {
//     let size;
//     this.searchkey = "";
//     let pagelist = this.currentPageAuditlogListdata;
//     if (list) {
//       size = list;
//       this.AuditlogListdataitemsPerPage = list;
//     } else {
//       size = this.AuditlogListdataitemsPerPage;
//     }

//     const url = "/auditTrail/all?pageIndex=" + (pagelist - 1) + "&pageSize=" + size;
//     let data = {
//       page: pagelist,
//       pageSize: size,
//     };
//     if (module == "CMS") {
//       this.selectedModuleName = "Customer";
//       this.AuditlogService.getMethod(url).subscribe(
//         (response: any) => {
//           this.AuditlogListData = response.byObject;
//           this.AuditlogListdatatotalRecords = response.totalRecords;

//           this.searchKey = "";
//           console.log("AuditlogListData", this.AuditlogListData);
//         },
//         (error: any) => {
//           console.log(error, "error");
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (module == "TICKET") {
//       this.selectedModuleName = "Ticket";
//       this.TicketManagementService.getMethod(url).subscribe(
//         (response: any) => {
//           this.AuditlogListData = response.byObject;
//           this.AuditlogListdatatotalRecords = response.totalRecords;

//           this.searchKey = "";
//           console.log("AuditlogListData", this.AuditlogListData);
//         },
//         (error: any) => {
//           console.log(error, "error");
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     }
//     // ......
//     else if (module == "INVENTORY") {
//       this.selectedModuleName = "Inventory";
//       this.AuditlogService.getInventoryMethod(url).subscribe(
//         (response: any) => {
//           this.AuditlogListData = response.byObject;
//           this.AuditlogListdatatotalRecords = response.totalRecords;

//           this.searchKey = "";
//           console.log("AuditlogListData", this.AuditlogListData);
//         },
//         (error: any) => {
//           console.log(error, "error");
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (module == "REVENUE") {
//       this.selectedModuleName = "Revenue";
//       this.RevenueManagementService.generateMethod(url).subscribe(
//         (response: any) => {
//           this.AuditlogListData = response.byObject;
//           this.AuditlogListdatatotalRecords = response.totalRecords;

//           this.searchKey = "";
//           console.log("AuditlogListData", this.AuditlogListData);
//         },
//         (error: any) => {
//           console.log(error, "error");
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (module == "NOTIFICATION") {
//       this.selectedModuleName = "Notification";
//       this.NotificationBaseService.get(url).subscribe(
//         (response: any) => {
//           this.AuditlogListData = response.byObject;
//           this.AuditlogListdatatotalRecords = response.totalRecords;

//           this.searchKey = "";
//           console.log("AuditlogListData", this.AuditlogListData);
//         },
//         (error: any) => {
//           console.log(error, "error");
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else {
//       this.selectedModuleName = "Common";
//       this.CountryManagementService.getMethod(url).subscribe(
//         (response: any) => {
//           this.AuditlogListData = response.byObject;
//           this.AuditlogListdatatotalRecords = response.totalRecords;

//           this.searchKey = "";
//           console.log("AuditlogListData", this.AuditlogListData);
//         },
//         (error: any) => {
//           console.log(error, "error");
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     }
//   }

//   pageChangedAuditlogList(pageNumber) {
//     this.currentPageAuditlogListdata = pageNumber;
//     if (!this.searchKey && (!this.startDate || !this.endDate)) {
//       this.getAuditlogList("", this.selectedModule);
//     } else {
//       this.searchAudit();
//     }
//   }

//   clearSearchAuditlog() {
//     this.getAuditlogList("", "");
//     this.searchModuleName = "";
//     this.searchUserName = "";
//     this.searchDate = "";
//     this.toDate = "";
//   }

//   handleChange(event: any) {
//     this.currentPageAuditlogListdata = 1;
//     this.AuditlogListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//     if (this.activeIndex == 0) {
//       this.getAuditlogList("", "CMS");
//     } else if (this.activeIndex == 1) {
//       this.getAuditlogList("", "TICKET");
//     } else if (this.activeIndex == 2) {
//       this.getAuditlogList("", "INVENTORY");
//     } else if (this.activeIndex == 3) {
//       this.getAuditlogList("", "REVENUE");
//     } else if (this.activeIndex == 4) {
//       this.getAuditlogList("", "NOTIFICATION");
//     } else {
//       this.getAuditlogList("", "COMMON");
//     }
//   }
//   snapShotOpen(snapshot) {
//     this.spinner.show();
//     this.showDialogue = true;
//     this.snapshotdata = "";
//     this.snapshotdata = snapshot;
//     this.spinner.hide();
//   }
//   snapShotClose() {
//     this.showDialogue = false;
//   }

//   moduleChange(event: any) {
//     this.searchKey = "";
//     this.startDate = "";
//     this.endDate = "";
//     this.currentPageAuditlogListdata = 1;
//     this.AuditlogListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//     this.getAuditlogList("", this.selectedModule);
//   }

//   search() {
//     this.currentPageAuditlogListdata = 1;
//     this.AuditlogListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//     this.searchAudit();
//   }

//   clearSearch() {
//     this.searchKey = "";
//     this.startDate = "";
//     this.endDate = "";
//     this.currentPageAuditlogListdata = 1;
//     this.AuditlogListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//     this.getAuditlogList("", this.selectedModule);
//   }

//   searchAudit() {
//     let url = "/auditTrail/byModule";
//     let request = {
//       moduleName: "",
//       entityName: this.searchKey,
//       pageIndex: this.currentPageAuditlogListdata,
//       pageSize: this.AuditlogListdataitemsPerPage,
//       startDate: this.startDate ? this.startDate : null,
//       endDate: this.endDate ? this.endDate : null,
//     };
//     if (this.selectedModule == "CMS") {
//       request.moduleName = "CMS";
//       this.AuditlogService.postMethod(url, request).subscribe(
//         (response: any) => {
//           if (response.responseCode == 404) {
//             this.AuditlogListData = [];
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             this.AuditlogListData = response.data;
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "success",
//               summary: "Success",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (this.selectedModule == "TICKET") {
//       request.moduleName = "Ticket Management";
//       this.TicketManagementService.postMethod(url, request).subscribe(
//         (response: any) => {
//           if (response.responseCode == 404) {
//             this.AuditlogListData = [];
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             this.AuditlogListData = response.data;
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "success",
//               summary: "Success",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (this.selectedModule == "INVENTORY") {
//       request.moduleName = "Inventory Management";
//       this.AuditlogService.postInventoryMethod(url, request).subscribe(
//         (response: any) => {
//           if (response.responseCode == 404) {
//             this.AuditlogListData = [];
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             this.AuditlogListData = response.data;
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "success",
//               summary: "Success",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (this.selectedModule == "REVENUE") {
//       request.moduleName = "Revenue Management";
//       this.RevenueManagementService.postMethod(url, request).subscribe(
//         (response: any) => {
//           if (response.responseCode == 404) {
//             this.AuditlogListData = [];
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             this.AuditlogListData = response.data;
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "success",
//               summary: "Success",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else if (this.selectedModule == "NOTIFICATION") {
//       request.moduleName = "Notification Management";
//       this.NotificationBaseService.post(url, request).subscribe(
//         (response: any) => {
//           if (response.responseCode == 404) {
//             this.AuditlogListData = [];
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             this.AuditlogListData = response.data;
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "success",
//               summary: "Success",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     } else {
//       request.moduleName = "Common GateWay";
//       this.savbillCommonBaseService.post(url, request).subscribe(
//         (response: any) => {
//           if (response.responseCode == 404) {
//             this.AuditlogListData = [];
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           } else {
//             this.AuditlogListData = response.data;
//             this.AuditlogListdatatotalRecords = response.totalRecords;
//             this.messageService.add({
//               severity: "success",
//               summary: "Success",
//               detail: response.responseMessage,
//               icon: "far fa-times-circle",
//             });
//           }
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle",
//           });
//         }
//       );
//     }
//   }

//   preventManualInput(event: KeyboardEvent) {
//     event.preventDefault();
//   }
// }

import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AuditlogService } from "src/app/service/auditlog.service";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import { CountryManagementService } from "src/app/service/country-management.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { NotificationBaseService } from "src/app/service/notification-base.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from 'rxjs';
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-audit-log",
    templateUrl: "./audit-log.component.html",
    styleUrls: ["./audit-log.component.css"],
    standalone: false
})
export class AuditLogComponent implements OnInit {
    AuditlogListData: any[] = [];
    dataSource = new MatTableDataSource<any>([]);
    displayedColumns: string[] = [
        'profileName', 'entityType', 'moduleName', 'actionType',
        'authorUserName', 'authorUserTeams', 'updatedOn',
        'ipAddress', 'snapshot'
    ];

    currentPageAuditlogListdata = 1;
    AuditlogListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    AuditlogListdatatotalRecords: number = 0;
    @ViewChild('snapshotDialog') snapshotDialog!: TemplateRef<any>;

    searchKey: string = "";
    startDate: string;
    endDate: string;
    selectedModule: string = "COMMON";
    selectedModuleName: string = "Common";
    moduleList = [
        { label: "Common", value: "COMMON" },
        { label: "CMS", value: "CMS" },
        { label: "Ticket", value: "TICKET" },
        { label: "Inventory", value: "INVENTORY" },
        { label: "Revenue", value: "REVENUE" },
        { label: "Notification", value: "NOTIFICATION" }
    ];
    pageLimitOptions = RadiusConstants.pageLimitOptions;

    AclClassConstants = AclClassConstants;
    AclConstants = AclConstants;

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    snapshotdata: any;
    showDialogue: boolean = false;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private AuditlogService: AuditlogService, private toastr: ToastrService,
        private TicketManagementService: TicketManagementService,
        private CountryManagementService: CountryManagementService,
        private RevenueManagementService: RevenueManagementService,
        private NotificationBaseService: NotificationBaseService,
        private savbillCommonBaseService: SavbillCommonBaseService,
        public loginService: LoginService,
        private dialog: MatDialog
    ) { }

    ngOnInit(): void {
        this.getAuditlogList("", this.selectedModule);
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    getAuditlogList(list: any, module: string) {
        let size;
        this.searchKey = "";
        let pagelist = this.currentPageAuditlogListdata;

        if (list) {
            size = list;
            this.AuditlogListdataitemsPerPage = list;
        } else {
            size = this.AuditlogListdataitemsPerPage || RadiusConstants.PER_PAGE_ITEMS || 10;
        }

        if (!size || size <= 0) {
            size = RadiusConstants.PER_PAGE_ITEMS || 10;
        }

        const url = "/auditTrail/all?pageIndex=" + (pagelist - 1) + "&pageSize=" + size;

        this.searchKey = "";

        const handleResponse = (response: any) => {
            this.AuditlogListData = response.byObject || response.data || [];
            this.AuditlogListdatatotalRecords = response.totalRecords || 0;

            this.dataSource.data = this.AuditlogListData;

            setTimeout(() => {
                if (this.paginator) {
                    this.paginator.length = this.AuditlogListdatatotalRecords;
                    this.paginator.pageIndex = this.currentPageAuditlogListdata - 1;
                    this.paginator.pageSize = this.AuditlogListdataitemsPerPage;
                }
            });
        };

        const handleError = (error: any) => {
            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        };

        switch (module) {
            case "CMS":
                this.selectedModuleName = "Customer";
                this.AuditlogService.getMethod(url).subscribe(handleResponse, handleError);
                break;
            case "TICKET":
                this.selectedModuleName = "Ticket";
                this.TicketManagementService.getMethod(url).subscribe(handleResponse, handleError);
                break;
            case "INVENTORY":
                this.selectedModuleName = "Inventory";
                this.AuditlogService.getInventoryMethod(url).subscribe(handleResponse, handleError);
                break;
            case "REVENUE":
                this.selectedModuleName = "Revenue";
                this.RevenueManagementService.generateMethod(url).subscribe(handleResponse, handleError);
                break;
            case "NOTIFICATION":
                this.selectedModuleName = "Notification";
                this.NotificationBaseService.get(url).subscribe(handleResponse, handleError);
                break;
            default:
                this.selectedModuleName = "Common";
                this.CountryManagementService.getMethod(url).subscribe(handleResponse, handleError);
        }
    }

    pageChangedAuditlogList(event: PageEvent) {
        this.currentPageAuditlogListdata = event.pageIndex + 1;
        this.AuditlogListdataitemsPerPage = event.pageSize;

        if (!this.searchKey && (!this.startDate || !this.endDate)) {
            this.getAuditlogList("", this.selectedModule);
        } else {
            this.searchAudit();
        }
    }


    TotalItemPerPage(event: any) {
        this.AuditlogListdataitemsPerPage = Number(event.value);

        // Reset page to 1 when page size changes
        this.currentPageAuditlogListdata = 1;

        if (!this.searchKey && (!this.startDate || !this.endDate)) {
            this.getAuditlogList("", this.selectedModule);
        } else {
            this.searchAudit();
        }
    }

    moduleChange(selectedValue: string) {
        this.selectedModule = selectedValue;
        this.searchKey = "";
        this.startDate = "";
        this.endDate = "";
        this.currentPageAuditlogListdata = 1;
        this.AuditlogListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
        this.getAuditlogList("", this.selectedModule);
    }

    search() {
        this.currentPageAuditlogListdata = 1;
        this.AuditlogListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
        this.searchAudit();
    }

    clearSearch() {
        this.searchKey = "";
        this.startDate = "";
        this.endDate = "";
        this.currentPageAuditlogListdata = 1;
        this.AuditlogListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
        this.getAuditlogList("", this.selectedModule);
    }

    searchAudit() {
        const url = "/auditTrail/byModule";
        const request = {
            moduleName: this.selectedModule,
            entityName: this.searchKey,
            pageIndex: this.currentPageAuditlogListdata,
            pageSize: this.AuditlogListdataitemsPerPage,
            startDate: this.startDate || null,
            endDate: this.endDate || null,
        };

        let serviceCall;

        switch (this.selectedModule) {
            case "CMS":
                serviceCall = this.AuditlogService.postMethod(url, request);
                break;
            case "TICKET":
                request.moduleName = "Ticket Management";
                serviceCall = this.TicketManagementService.postMethod(url, request);
                break;
            case "INVENTORY":
                request.moduleName = "Inventory Management";
                serviceCall = this.AuditlogService.postInventoryMethod(url, request);
                break;
            case "REVENUE":
                request.moduleName = "Revenue Management";
                serviceCall = this.RevenueManagementService.postMethod(url, request);
                break;
            case "NOTIFICATION":
                request.moduleName = "Notification Management";
                serviceCall = this.NotificationBaseService.post(url, request);
                break;
            default:
                request.moduleName = "Common GateWay";
                serviceCall = this.savbillCommonBaseService.post(url, request);
        }

        serviceCall.subscribe(
            (response: any) => {
                if (response.responseCode === 404) {
                    this.AuditlogListData = [];
                    this.AuditlogListdatatotalRecords = response.totalRecords || 0;
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.AuditlogListData = response.data || [];
                    this.AuditlogListdatatotalRecords = response.totalRecords || 0;
                    this.toastr.success(`${response.responseMessage}`, "Successfully ");
                }
                this.dataSource.data = this.AuditlogListData;

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    snapShotOpen(snapshotContent: string) {
        this.snapshotdata = snapshotContent;

        this.dialog.open(this.snapshotDialog, {
            width: '800px',

        });
    }

    snapShotClose() {
        this.dialog.closeAll();
    }


    preventManualInput(event: KeyboardEvent) {
        event.preventDefault();
    }

    canExit(): Observable<boolean> | boolean {
        const hasUnsavedChanges = this.searchKey?.trim() !== "" || this.startDate || this.endDate;
        if (!hasUnsavedChanges) {
            return true;
        } else {
            return new Observable((observer: Observer<boolean>) => {
                this.confirmationService.confirm({
                    header: "Alert",
                    message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                    icon: "pi pi-info-circle",
                    accept: () => {
                        observer.next(true);
                        observer.complete();
                    },
                    reject: () => {
                        observer.next(false);
                        observer.complete();
                    }
                });
            });
        }
    }
}
