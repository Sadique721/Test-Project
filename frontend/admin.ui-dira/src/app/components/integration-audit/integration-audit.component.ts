// import { Component, OnInit } from "@angular/core";
// import { DatePipe } from "@angular/common";
// import { NgxSpinnerService } from "ngx-spinner";
// import { MessageService } from "primeng/api";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { CustomerService } from "src/app/service/customer.service";
// import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
// import { PartnerService } from "src/app/service/partner.service";
// import { CommondropdownService } from "src/app/service/commondropdown.service";
// import { IntegrationAuditService } from "src/app/service/integration-audit.service";

// @Component({
//     selector: "app-integration-audit",
//     templateUrl: "./integration-audit.component.html",
//     styleUrls: ["./integration-audit.component.css"],
//     standalone: false
// })
// export class IntegrationAuditComponent implements OnInit {
//   integrationAuditData: any = [];
//   currentPage: number = 1;
//   itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
//   totalRecords: number;
//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   showItemPerPage: any = 5;
//   pageITEM = RadiusConstants.ITEMS_PER_PAGE;
//   viewAccess: any;
//   isViewDetails: boolean = false;
//   detailsHeader: any;
//   detailsBody: any;
//   searchkey: string;
//   searchView: boolean = true;
//   searchName: string;
//   searchData: any;
//   apiAuditData: any;
//   constructor(
//     private customerService: CustomerService,
//     private spinner: NgxSpinnerService,
//     private messageService: MessageService,
//     public partnerService: PartnerService,
//     public savbillCommonBaseService: SavbillCommonBaseService,
//     public datepipe: DatePipe,

//     public integrationAuditService: IntegrationAuditService
//   ) {}

//   ngOnInit(): void {
//     this.getAllIntegrationAuditData("");
//     this.searchData = {
//       filter: [
//         {
//           filterDataType: "",
//           filterValue: "",
//           filterColumn: "any",
//           filterOperator: "equalto",
//           filterCondition: "and"
//         }
//       ]
//     };
//   }

//   async getAllIntegrationAuditData(list) {


//     let request = {
//       page: this.currentPage,
//       pageSize: this.itemsPerPage
//     };

//     this.integrationAuditService.getIntegrationConfigurationById(request).subscribe(
//       (response: any) => {
//         this.integrationAuditData = response.dataList;


//         this.integrationAuditData = this.integrationAuditData.map(item => {
//           item.responsePayload = item.responsePayload.slice(0, item.responsePayload.length);
//           return item;
//         });
//         this.totalRecords = response.totalRecords;
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.errorMessage,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   TotalItemPerPage(event) {
//     if (this.currentPage > 1) {
//       this.currentPage = 1;
//     }
//     this.getAllIntegrationAuditData(this.showItemPerPage);
//   }

//   pageChanged(pageNumber) {
//     this.currentPage = pageNumber;
//     this.getAllIntegrationAuditData("");
//   }

//   openDetailsModel(audit, header) {
//     this.isViewDetails = true;
//     this.detailsHeader = header;
//     try {
//       const parsed = typeof audit === "string" ? JSON.parse(audit) : audit;
//       this.detailsBody = JSON.stringify(parsed, null, 3);
//     } catch (error) {
//       this.detailsBody = audit;
//     }
//   }

//   search() {
//     this.currentPage = 1;
//     this.searchData.filter[0].filterValue = this.searchName ? this.searchName.trim() : "";

//     const url = `/search?page=${this.currentPage}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
//     this.integrationAuditService.postMethod(url, this.searchData).subscribe(
//       (response: any) => {
//         this.integrationAuditData = response.dataList;
//         this.totalRecords = response.totalRecords;
//       },
//       (error: any) => {
//         this.totalRecords = 0;
//         if (error.error.status == 404) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: error.error.msg,
//             icon: "far fa-times-circle"
//           });
//           this.apiAuditData = [];
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle"
//           });
//         }
//       }
//     );
//   }
//   clearSearch() {
//     this.searchName = "";
//     this.searchData.filter[0].filterValue = "";
//     this.getAllIntegrationAuditData("");
//   }

//   formatTimestamp(timestamp: string): string {
//     return timestamp.replace("T", " ");
//   }
// }

import {
  Component,
  OnInit,
  AfterViewInit,
  TemplateRef,
  ViewChild,
  ViewContainerRef,
  OnDestroy,
} from "@angular/core";
import { DatePipe } from "@angular/common";
import { NgxSpinnerService } from "ngx-spinner";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatDialogRef } from "@angular/material/dialog";
import { MessageService, ConfirmationService } from "primeng/api";
import { Observable } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import {
  Overlay,
  OverlayRef,
} from "@angular/cdk/overlay";
import { TemplatePortal } from "@angular/cdk/portal";
import { IntegrationAuditService } from "src/app/service/integration-audit.service";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: "app-integration-audit",
  templateUrl: "./integration-audit.component.html",
  styleUrls: ["./integration-audit.component.css"],
  standalone: false
})
export class IntegrationAuditComponent implements OnInit, OnDestroy {
  integrationAuditData: any[] = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = [
    "id", "apiUrl", "requestTime", "headerDetails", "httpMethod", "requestPayload",
    "responsePayload", "httpStatusCode", "responseTime", "userName", "referenceNumber",
    "usernameForAudit", "ipAddress", "environmentInfo", "dependencies"
  ];
  currentPage: number = 1;
  itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
  totalRecords: number = 0;
  pageLimitOptions = RadiusConstants.pageLimitOptions;
  showItemPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
  searchName: string = "";
  searchData: any = {
    filter: [
      {
        filterDataType: "",
        filterValue: "",
        filterColumn: "any",
        filterOperator: "equalto",
        filterCondition: "and"
      }
    ]
  };
  isViewDetails: boolean = false;
  detailsHeader: string = '';
  detailsBody: any;
  dialogRef!: MatDialogRef<any>;
  closeDialogTimeout: any;
  private overlayRef: OverlayRef | null = null;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild("detailsDialog") detailsDialogTemplate!: TemplateRef<any>;

  constructor(
    private spinner: NgxSpinnerService,
    private messageService: MessageService,
    private datepipe: DatePipe,
    private overlay: Overlay, private toastr: ToastrService,
    private vcr: ViewContainerRef,
    private confirmationService: ConfirmationService,
    private integrationAuditService: IntegrationAuditService
  ) { }

  ngOnInit(): void {
    this.getAllIntegrationAuditData("");
  }

  async getAllIntegrationAuditData(list: any) {
  const request = {
    page: this.currentPage,
    pageSize: this.itemsPerPage,
  };

   this.integrationAuditService.getIntegrationConfigurationById(request).subscribe(
    (response: any) => {
      this.integrationAuditData = response.dataList;
      this.dataSource.data = this.integrationAuditData;
      this.totalRecords = response.totalRecords;

      if (this.paginator) {
        this.paginator.pageIndex = this.currentPage - 1;
      }
    },
    (error: any) => {
       this.toastr.error(`${error.error.ERROR}`|| 'Failed to fetch data');
    }
  );
}


  TotalItemPerPage(event: any) {
    if (this.currentPage > 1) {
      this.currentPage = 1;
    }
    this.itemsPerPage = event.value || this.itemsPerPage;
    this.getAllIntegrationAuditData(this.itemsPerPage);
  }

  pageChanged(event: PageEvent) {
  this.currentPage = event.pageIndex + 1;
  this.itemsPerPage = event.pageSize;
  
  if (this.searchName && this.searchName.trim() !== "") {
    this.search();
  } else {
    this.getAllIntegrationAuditData("");
  }
}


  search() {
    this.currentPage = 1;
    this.searchData.filter[0].filterValue = this.searchName ? this.searchName.trim() : "";

    const url = `/search?page=${this.currentPage}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
    this.integrationAuditService.postMethod(url, this.searchData).subscribe(
      (response: any) => {
        this.integrationAuditData = response.dataList;
        this.totalRecords = response.totalRecords;
        this.dataSource.data = this.integrationAuditData;
      },
      (error: any) => {
        this.totalRecords = 0;
        if (error.error?.status === 404) {
          this.toastr.info(error.responseMessage, 'Info!');
          this.integrationAuditData = [];
        } else {
            this.toastr.error(`${error.error.ERROR}`, 'Search error');
        }
      }
    );
  }

  clearSearch() {
    this.searchName = "";
    this.searchData.filter[0].filterValue = "";
    this.currentPage = 1;
    this.getAllIntegrationAuditData("");
  }

  openDetailsOnHover(event: MouseEvent, details: any, header: string) {
    const positionStrategy = this.overlay
      .position()
      .flexibleConnectedTo(event.target as HTMLElement)
      .withPositions([
        {
          originX: "end",
          originY: "center",
          overlayX: "start",
          overlayY: "center",
          offsetX: 8,
        },
        {
          originX: "start",
          originY: "center",
          overlayX: "end",
          overlayY: "center",
          offsetX: -8,
        },
      ]);

    this.overlayRef = this.overlay.create({
      positionStrategy,
      hasBackdrop: true,
      scrollStrategy: this.overlay.scrollStrategies.reposition(),
      panelClass: "hover-dialog-panel",
    });

    let body: string;
    try {
      const parsed = typeof details === "string" ? JSON.parse(details) : details;
      body = JSON.stringify(parsed, null, 3);
    } catch {
      body = details;
    }

    const portal = new TemplatePortal(this.detailsDialogTemplate, this.vcr, {
      $implicit: { header, body },
    });

    this.overlayRef.attach(portal);
    this.overlayRef.outsidePointerEvents().subscribe(() => this.closeOverlay());
  }
  ngOnDestroy() {
    this.closeOverlay();
  }
  closeOverlay() {
    if (this.overlayRef) {
      this.overlayRef.dispose();
      this.overlayRef = null;
    }
    if (this.closeDialogTimeout) {
      clearTimeout(this.closeDialogTimeout);
      this.closeDialogTimeout = null;
    }
  }

  formatTimestamp(timestamp: string): string {
    return timestamp ? timestamp.replace("T", " ") : "";
  }

  canExit(): Observable<boolean> | boolean {
    if (!this.hasUnsavedChanges()) {
      return true;
    }
    return new Observable<boolean>((observer) => {
      this.confirmationService.confirm({
        header: "Confirm Navigation",
        message: "You have unsaved changes. Do you really want to leave?",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          observer.next(true);
          observer.complete();
        },
        reject: () => {
          observer.next(false);
          observer.complete();
        },
      });
    });
  }

  private hasUnsavedChanges(): boolean {
    return false;
  }
}


