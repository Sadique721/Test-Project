import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormBuilder, Validators, FormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import { DatePipe } from "@angular/common";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute } from "@angular/router";
import { DomSanitizer } from "@angular/platform-browser";
import { saveAs as importedSaveAs } from "file-saver";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import * as FileSaver from "file-saver";
import moment from "moment";
import { SharedModule } from "src/app/shared/shared.module";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
import { CommondropdownService } from "src/app/service/commondropdown.service";

declare var $: any;
@Component({
  selector: "app-subscription-history",
  templateUrl: "./subscription-history.component.html",
  styleUrls: ["./subscription-history.component.scss"],
  standalone: true,
  imports: [SharedModule],
  providers: [DatePipe]
})
export class SubscriptionHistoryComponent implements OnInit {
  customerId: string;
  badgeTypeForStatus: any;
  displayStatus: any;
  visibleQuotaDetails: boolean = false;
  custQuotaList: any = [];
  CurrentPlanShowItemPerPage = 1;
  custCurrentPlanList: any = [];
  futurePlanShowItemPerPage = 1;
  custFuturePlanList: any = [];
  expiredShowItemPerPage = 1;
  custExpiredPlanList: any = [];
  currentPagecustomerExpiryPlanListdata = 0;
  currentPagecustomerFuturePlanListdata = 0;
  currentPagecustomerCurrentPlanListdata = 0;
  customerCurrentPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  customerFuturePlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  customerExpiryPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  pageITEM = RadiusConstants.ITEMS_PER_PAGE;
  pageLimitOptions = RadiusConstants.pageLimitOptions;
  currentPagecustQuotaList = 0;
  custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custQuotaListtotalRecords: String;
  customerExpiryPlanListdatatotalRecords: any;
  customerFuturePlanListdatatotalRecords: any;
  customerCurrentPlanListdatatotalRecords: any;
  freePlanId = RadiusConstants.FREE_PLAN;
  currency: any;
  currencySymbol = localStorage.getItem("CURRENCY_SYMBOL");
  cols = [
    {
      field: "planName",
      header: "Plan Name",
      customExportHeader: "Plan Name"
    },
    { field: "validity", header: "Validity", customExportHeader: "Validity" },
    {
      field: "custPlanStatus",
      header: "Plan Status",
      ustomExportHeader: "Plan Status"
    },
    { field: "offerPrice", header: "Amount", customExportHeader: "Amount" },
    {
      field: "dbStartDate",
      header: "Start Date",
      customExportHeader: "Start Date"
    },
    {
      field: "dbEndDate",
      header: "Service Expiry Date",
      customExportHeader: "Service Expiry Date"
    },
    {
      field: "dbExpiryDate",
      header: "Billing End Date",
      customExportHeader: "Billing End Date"
    }
  ];
  displaycolom = [
    "planName",
    "validity",
    "custPlanStatus",
    "offerPrice",
    "dbStartDate",
    "dbEndDate",
    "dbExpiryDate"
  ];
  displayedColumns: string[] = [
    "planName",
    "quotaType",
    "totalQuota",
    "usedQuota",
    "quotaUnit",
    "timeTotalQuota",
    "timeQuotaUsed",
    "timeQuotaUnit"
  ];
  constructor(
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    public datepipe: DatePipe,
    public customerManagementService: CustomermanagementService,
    public commondropdownService: CommondropdownService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.customerId = this.commondropdownService.getUserId();
    this.getcustCurrentPlan(this.customerId, "");
    this.getcustFuturePlan(this.customerId, "");
    this.getcustExpiredPlan(this.customerId, "");
    this.getCustomersDetail(this.customerId);
  }
  getCustomersDetail(custId) {
    const url = "/customers/" + custId;
    this.customerManagementService.getMethod(url).subscribe((response: any) => {
      let custDetails = response.customers;
      this.currency = custDetails.currency ? custDetails.currency : this.currencySymbol;
      this.spinner.hide();
    });
  }
  getcustCurrentPlan(custId, size) {
    let page_list;
    // if (size) {
    //   page_list = size;
    //   this.customerCurrentPlanListdataitemsPerPage = size;
    // } else {
    //   if (this.CurrentPlanShowItemPerPage == 1) {
    //     this.customerCurrentPlanListdataitemsPerPage = this.pageITEM;
    //   } else {
    //     this.customerCurrentPlanListdataitemsPerPage = this.CurrentPlanShowItemPerPage;
    //   }
    // }
    this.spinner.show();
    const url = "/subscriber/getActivePlanList/" + custId + "?isNotChangePlan=true";
    this.customerManagementService.getMethod(url).subscribe(
      (response: any) => {
        // this.custCurrentPlanList = response.dataList;
        this.custCurrentPlanList = response.dataList.filter(
          item =>
            item.custPlanStatus.toLowerCase() != "newactivation" && item.planId != this.freePlanId
        );

        // this.custCurrentPlanList.forEach(element => {
        //   element.dbStartDate = moment.utc(element.dbStartDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.dbEndDate = moment.utc(element.dbEndDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.dbExpiryDate = moment.utc(element.dbExpiryDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.offerPrice = element.offerPrice + "  " + this.currencySymbol;
        // });
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  checkStatus(planStatus, workflowStatus) {
    let status = planStatus.toLowerCase();
    let statusWorkflow = workflowStatus ? workflowStatus.toLowerCase() : "";

    if (statusWorkflow == "newactivation" || statusWorkflow == "rejected") {
      if (statusWorkflow == "newactivation") this.badgeTypeForStatus = "green";
      else this.badgeTypeForStatus == "red";
      this.displayStatus = workflowStatus.toUpperCase();
    } else {
      this.displayStatus = planStatus.toUpperCase();
      switch (status) {
        case "active":
        case "ingrace":
          this.badgeTypeForStatus = "green";
          break;
        case "terminate":
        case "stop":
        case "inactive":
        case "expired":
          this.badgeTypeForStatus = "red";
          break;
        case "hold":
        case "disable":
          this.badgeTypeForStatus = "grey";
          break;
        default:
          break;
      }
    }
    return true;
  }

  pageChangedcustomerCurrentPlanListData(pageNumber) {
    this.currentPagecustomerCurrentPlanListdata = pageNumber;
    this.getcustCurrentPlan(this.customerId, "");
  }

  TotalCurrentPlanItemPerPage(event) {
    this.CurrentPlanShowItemPerPage = Number(event.value);
    if (this.currentPagecustomerCurrentPlanListdata > 1) {
      this.currentPagecustomerCurrentPlanListdata = 1;
    }
    this.getcustCurrentPlan(this.customerId, this.CurrentPlanShowItemPerPage);
  }

  getcustFuturePlan(custId, size) {
    // let page_list;
    // if (size) {
    //   page_list = size;
    //   this.customerFuturePlanListdataitemsPerPage = size;
    // } else {
    //   if (this.futurePlanShowItemPerPage == 1) {
    //     this.customerFuturePlanListdataitemsPerPage = this.pageITEM;
    //   } else {
    //     this.customerFuturePlanListdataitemsPerPage = this.futurePlanShowItemPerPage;
    //   }
    // }
    this.spinner.show();
    const url = "/subscriber/getFuturePlanList/" + custId;
    this.customerManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.custFuturePlanList = response.dataList;

        // this.custFuturePlanList.forEach(element => {
        //   element.dbStartDate = moment.utc(element.dbStartDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.dbEndDate = moment.utc(element.dbEndDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.dbExpiryDate = moment.utc(element.dbExpiryDate).format("DD-MM-YYYY HH:mm:ss");
        // });

        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getcustExpiredPlan(custId, size) {
    let page_list;
    // if (size) {
    //   page_list = size;
    //   this.customerExpiryPlanListdataitemsPerPage = size;
    // } else {
    //   if (this.expiredShowItemPerPage == 1) {
    //     this.customerExpiryPlanListdataitemsPerPage = this.pageITEM;
    //   } else {
    //     this.customerExpiryPlanListdataitemsPerPage = this.expiredShowItemPerPage;
    //   }
    // }
    this.spinner.show();
    const url = "/subscriber/getExpiredPlanList/" + custId;
    this.customerManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.custExpiredPlanList = response.dataList;

        // this.custExpiredPlanList.forEach(element => {
        //   element.dbStartDate = moment.utc(element.dbStartDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.dbEndDate = moment.utc(element.dbEndDate).format("DD-MM-YYYY HH:mm:ss");

        //   element.dbExpiryDate = moment.utc(element.dbExpiryDate).format("DD-MM-YYYY HH:mm:ss");
        // });

        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  pageChangedcustomerExpiryPlanListData(pageNumber) {
    this.currentPagecustomerExpiryPlanListdata = pageNumber;
    this.getcustExpiredPlan(this.customerId, "");
  }

  TotalExpiredPlanItemPerPage(event) {
    this.expiredShowItemPerPage = Number(event.value);
    if (this.currentPagecustomerExpiryPlanListdata > 1) {
      this.currentPagecustomerExpiryPlanListdata = 1;
    }
    this.getcustExpiredPlan(this.customerId, this.expiredShowItemPerPage);
  }

  pageChangedcustFuturePlanListData(pageNumber) {
    this.currentPagecustomerFuturePlanListdata = pageNumber;
    this.getcustFuturePlan(this.customerId, "");
  }

  TotalFuturePlanItemPerPage(event) {
    this.futurePlanShowItemPerPage = Number(event.value);
    if (this.currentPagecustomerFuturePlanListdata > 1) {
      this.currentPagecustomerFuturePlanListdata = 1;
    }
    this.getcustFuturePlan(this.customerId, this.futurePlanShowItemPerPage);
  }

  @ViewChild("detailsQuotaDialog") detailsQuotaDialog!: TemplateRef<any>;

  dataSource = new MatTableDataSource<any>();
  quotaPlanDetailsModel(planData) {
    this.visibleQuotaDetails = true;
    this.spinner.show();
    this.customerManagementService.getCustQuotaList(this.customerId).subscribe(
      (response: any) => {
        const data = response.custQuotaList || [];

        // FILTER LIST
        this.custQuotaList = data.filter((e: any) => e.cprId == planData.planmapid);

        // SET TABLE DATASOURCE
        this.dataSource.data = this.custQuotaList || [];

        // OPEN MODAL
        setTimeout(() => {
          this.dialog.open(this.detailsQuotaDialog, {
            width: "80%"
          });
        }, 500);
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  closeModel() {
    this.visibleQuotaDetails = false;
  }

  pageChangedCustQuotaList(pageNumber) {
    this.currentPagecustQuotaList = pageNumber;
  }

  exportCurrentPlanToExcel() {
    import("xlsx").then(xlsx => {
      let z = this.custCurrentPlanList.map((ele: any) => {
        let x = {};
        this.cols.forEach((d: any) => {
          x = { ...x, [d.field]: ele?.[d.field] };
        });
        return x;
      });
      const worksheet = xlsx.utils.json_to_sheet(z);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ["data"] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: "xlsx",
        type: "array"
      });
      this.saveAsExcelFile(excelBuffer, "current");
    });
  }

  exportFuturePlanToExcel() {
    import("xlsx").then(xlsx => {
      let z = this.custFuturePlanList.map((ele: any) => {
        let x = {};
        this.cols.forEach((d: any) => {
          x = { ...x, [d.field]: ele?.[d.field] };
        });
        return x;
      });
      const worksheet = xlsx.utils.json_to_sheet(z);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ["data"] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: "xlsx",
        type: "array"
      });
      this.saveAsExcelFile(excelBuffer, "future");
    });
  }

  exportExpirePlanToExcel() {
    import("xlsx").then(xlsx => {
      let z = this.custExpiredPlanList.map((ele: any) => {
        let x = {};
        this.cols.forEach((d: any) => {
          x = { ...x, [d.field]: ele?.[d.field] };
        });
        return x;
      });
      const worksheet = xlsx.utils.json_to_sheet(z);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ["data"] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: "xlsx",
        type: "array"
      });
      this.saveAsExcelFile(excelBuffer, "previous");
    });
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
    let EXCEL_EXTENSION = ".xlsx";
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE
    });
    FileSaver.saveAs(data, fileName + "_export_" + new Date().getTime() + EXCEL_EXTENSION);
  }
}
