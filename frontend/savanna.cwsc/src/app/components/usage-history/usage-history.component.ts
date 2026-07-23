import { Component, OnInit } from "@angular/core";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import * as FileSaver from "file-saver";
import moment from "moment";
import { DatePipe } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { CommondropdownService } from "src/app/service/commondropdown.service";

declare var $: any;
@Component({
  selector: "app-usage-history",
  templateUrl: "./usage-history.component.html",
  styleUrls: ["./usage-history.component.scss"],
  standalone: true,
  imports: [SharedModule],
  providers: [DatePipe]
})
export class UsageHistoryComponent implements OnInit {
  customerId: string;
  mvnoId: string;
  usageDataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  showItemPerPage = 0;
  pageITEM = RadiusConstants.ITEMS_PER_PAGE;
  cdrListDatatotalRecords: any;
  cdrListData: any = [];
  currentPageUsageListdata = 1;
  cdrShowItemPerPage = 1;
  customerCdrTotalRecords: any;
  pageLimitOptions = RadiusConstants.pageLimitOptions;
  selectedTimeFrame = "";
  selectedDate = "";
  maxDate: Date | undefined;
  totalDataListLength = 0;
  currentPageSize;

  cols = [
    {
      field: "createdate",
      header: "Date",
      customExportHeader: "Date"
    },
    {
      field: "outputOctant",
      header: "Download(MB)",
      customExportHeader: "Download(MB)"
    },
    {
      field: "inputOctant",
      header: "Upload(MB)",
      customExportHeader: "Upload(MB)"
    },
    {
      field: "acctSessionTime",
      header: "Session Time",
      customExportHeader: "Session Time"
    }
  ];

  displaycolom = ["createdate", "outputOctant", "inputOctant", "acctSessionTime"];
  timeFrameOption = [
    { label: "Week", value: "Week" },
    { label: "Month", value: "Month" },
    { label: "Last 6 Months", value: "Last 6 Months" }
  ];

  constructor(
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    public customerManagementService: CustomermanagementService,
    public commondropdownService: CommondropdownService,
    // private messageService: MessageService,
    private datePipe: DatePipe
  ) {}
  ngOnInit(): void {
    this.customerId = this.commondropdownService.getUserId();
    this.mvnoId = this.commondropdownService.getMvnoId();
    this.maxDate = new Date();
    this.maxDate.setDate(this.maxDate.getDate() - 1);
    this.maxDate.setMonth(this.maxDate.getMonth());
    this.maxDate.setFullYear(this.maxDate.getFullYear());
    this.getUsageData("");
  }

  getUsageData(size) {
    this.spinner.show();
    this.cdrListData = [];

    // let page_list;
    // if (size) {
    //   page_list = size;
    //   this.usageDataitemsPerPage = size;
    // } else {
    //   if (this.showItemPerPage == 0) {
    //     this.usageDataitemsPerPage = this.pageITEM;
    //   } else {
    //     this.usageDataitemsPerPage = this.showItemPerPage;
    //   }
    // }
 
    var request = {
      custId: this.customerId,
      page: this.currentPageUsageListdata,
      pageSize: this.usageDataitemsPerPage
    };

    let url = "findAcctCdr?mvnoId=" + this.mvnoId;
    this.customerManagementService.getCDRDataByCustomerId(this.mvnoId, request).subscribe(
      (response: any) => {
        this.spinner.hide();
        if (response.acctCdr) {
          this.cdrListData = response.acctCdr.content;
          this.customerCdrTotalRecords = response.acctCdr.totalElements;

          // if (this.cdrListData != null && this.cdrListData.length > 0) {
          //   if (this.showItemPerPage > this.usageDataitemsPerPage) {
          //     this.totalDataListLength = this.cdrListData.length % this.showItemPerPage;
          //   } else {
          //     this.totalDataListLength = this.cdrListData.length % this.usageDataitemsPerPage;
          //   }
          // }

          this.cdrListData.forEach(element => {
            const dateObj = new Date(element.acctSessionTime * 1000);
            const hours = dateObj.getUTCHours();
            const minutes = dateObj.getUTCMinutes();
            const seconds = dateObj.getSeconds();

            const timeString =
              hours.toString().padStart(2, "0") +
              ":" +
              minutes.toString().padStart(2, "0") +
              ":" +
              seconds.toString().padStart(2, "0");

            element.acctSessionTime = timeString;

            element.createdate = moment.utc(element.createdate).format("DD-MM-YYYY HH:mm:ss");

            if (element.inputOctant != null) {
              element.inputOctant = Number(element.inputOctant).toFixed(2);
            }
            if (element.outputOctant != null) {
              element.outputOctant = Number(element.outputOctant).toFixed(2);
            }
          });
          this.cdrListDatatotalRecords = response.acctCdr.totalElements;
        }
      },
      (error: any) => {
        this.toastr.error(`${error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  cdrDataPageChange(pageNumber) {
    
    this.currentPageUsageListdata = pageNumber.pageIndex + 1;
    this.usageDataitemsPerPage = pageNumber.pageSize;
    if (this.selectedTimeFrame || this.selectedDate) {
      this.searchUsageHistory("");
    } else {
      this.getUsageData("");
    }
  }

  TotalItemPerPageChange(event) {
    this.showItemPerPage = Number(event.value);
    if (this.currentPageUsageListdata > 1) {
      this.currentPageUsageListdata = 1;
    }
    if (!this.selectedTimeFrame && !this.selectedDate) {
      this.getUsageData(this.showItemPerPage);
    } else {
      this.searchUsageHistory("");
    }
  }

  exportCurrentPlanToExcel() {
    import("xlsx").then(xlsx => {
      let z = this.cdrListData.map((ele: any) => {
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
      this.saveAsExcelFile(excelBuffer, "usage");
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

  searchUsageHistory(size) {
    let date = "";
    if (this.selectedDate !== "") {
      date = this.datePipe.transform(this.selectedDate, "yyyy-MM-dd");
    }
    // let page_list;
    // if (size) {
    //   page_list = size;
    //   this.usageDataitemsPerPage = size;
    //   this.currentPageSize = size;
    // } else {
    //   if (this.showItemPerPage == 0) {
    //     this.usageDataitemsPerPage = this.pageITEM;
    //   } else {
    //     this.usageDataitemsPerPage = this.showItemPerPage;
    //   }
    // }

    var request = {
      custId: this.customerId,
      page: this.currentPageUsageListdata,
      pageSize: this.usageDataitemsPerPage,
      timeFrame: this.selectedTimeFrame,
      searchDate: date
    };

    this.spinner.show();
    let url = "";
    this.customerManagementService.getCDRDataByCustomerId(this.mvnoId, request).subscribe(
      (response: any) => {
        this.cdrListData = [];
        this.spinner.hide();
        if (response.acctCdr != undefined) {
          this.cdrListData = response.acctCdr.content;
          this.customerCdrTotalRecords = response.acctCdr.totalElements;
          this.cdrListData.forEach(element => {
            const dateObj = new Date(element.acctSessionTime * 1000);
            const hours = dateObj.getUTCHours();
            const minutes = dateObj.getUTCMinutes();
            const seconds = dateObj.getSeconds();

            const timeString =
              hours.toString().padStart(2, "0") +
              ":" +
              minutes.toString().padStart(2, "0") +
              ":" +
              seconds.toString().padStart(2, "0");

            element.acctSessionTime = timeString;

            element.createdate = moment.utc(element.createdate).format("DD-MM-YYYY HH:mm:ss");

            if (element.inputOctant != null) {
              element.inputOctant = Number(element.inputOctant).toFixed(2);
            }
            if (element.outputOctant != null) {
              element.outputOctant = Number(element.outputOctant).toFixed(2);
            }
          });
          this.cdrListDatatotalRecords = response.acctCdr.totalElements;
        }
        if (this.cdrListData.length === 0) {
          this.toastr.error(`${response.errorMessage}`, "Failed!");
        }
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  clearUsageSearch() {
    this.selectedTimeFrame = "";
    this.selectedDate = "";
    this.showItemPerPage = 0;
    this.currentPageSize = RadiusConstants.pageLimitOptions[0];
    this.currentPageUsageListdata = 1;
    this.customerCdrTotalRecords = 0;
    this.getUsageData("");
  }
}
