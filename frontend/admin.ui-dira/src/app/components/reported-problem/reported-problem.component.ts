import { Component, OnInit,ViewChild  } from "@angular/core";
import { FormBuilder, Validators, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ReportedProblemService } from "src/app/service/reported-problem.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-reported-problem",
    templateUrl: "./reported-problem.component.html",
    styleUrls: ["./reported-problem.component.css"],
    standalone: false
})
export class ReportedProblemComponent implements OnInit {
    title = "Reported Problem";
  reportedProblemForm: FormGroup;
  submitted = false;

  reportedProblemData: any = [];
  dataSource = new MatTableDataSource<any>([]);
  displayedColumns: string[] = ["issue", "phno", "desc"];
  reportedProblemtotalRecords: number = 0;
  searchPhoneNo: string = "";
  searchKey: string = "";
  public loginService: LoginService;
  AclClassConstants;
  AclConstants;
  pageLimitOptions = RadiusConstants.pageLimitOptions;
  showItemPerPage: any;
  currentPageReportedProblem = 1;
  reportedProblemitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

  @ViewChild(MatSort) sort: MatSort = Object.create(null);
  @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

  constructor(
        private fb: FormBuilder,
    private spinner: NgxSpinnerService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService, private toastr: ToastrService,
    private reportedProblemService: ReportedProblemService,
    loginService: LoginService
  )  {
    this.loginService = loginService;
    this.AclClassConstants = AclClassConstants;
    this.AclConstants = AclConstants;
    this.reportedProblemForm = this.fb.group({
      phoneNo: ["", [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.getReportedData("");
  }

 ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  getReportedData(list) {
    let size;

    let pageList = this.currentPageReportedProblem;
    if (list) {
      size = list;
      this.reportedProblemitemsPerPage = list;
    } else {
      size = this.reportedProblemitemsPerPage;
    }

    let pageData = {
      page: pageList,
      pageSize: size,
    };

    const url = "/reportproblem";
    this.reportedProblemService.postMethod(url, pageData).subscribe(
      (response: any) => {
        this.reportedProblemData = response.dataList;
        this.reportedProblemtotalRecords = response.totalRecords;
         this.dataSource = new MatTableDataSource<any>(this.reportedProblemData);
        if (this.paginator) {
          this.dataSource.paginator = this.paginator;
        }
        if (this.sort) {
          this.dataSource.sort = this.sort;
        }
      },
      (error: any) => {
         this.toastr.error(`${error.error.ERROR}`, 'Failed!');
      }
    );
  }

  searchReportedData() {
    if (!this.searchPhoneNo) {
      (error: any) => {
      this.toastr.error(`${error.error.ERROR}`, 'Please Select Phone No.');}
      return;
    }

    if (!this.searchKey || this.searchKey !== this.searchPhoneNo) {
      this.currentPageReportedProblem = 1;
    }
    if (this.showItemPerPage) {
      this.reportedProblemitemsPerPage = this.showItemPerPage;
    }
    this.searchKey = this.searchPhoneNo;

    let filterData = {
      filter: [
        {
          filterColumn: "phno",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: this.searchPhoneNo,
        },
      ],
    };
    //const url = "/reportproblem/phno?phno="+ this.searchPhoneNo;
    const url =
      "/reportproblem/pagination?page=" +
      this.currentPageReportedProblem +
      "&pageSize=" +
      this.reportedProblemitemsPerPage +
      "&sortBy=report_id&sortOrder=0";
    this.reportedProblemService.postMethod(url, filterData).subscribe(
      (response: any) => {
        this.reportedProblemData = response.dataList;
        this.reportedProblemtotalRecords = response.totalRecords;
      },
      (error: any) => {
        this.reportedProblemtotalRecords = 0;
        if (error.error.status == 404) {
          this.toastr.info(error.responseMessage, 'Info!');
          this.reportedProblemData = [];
            this.dataSource = new MatTableDataSource<any>([]);
        } else {
           this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        }
      }
    );
  }

  clearReportedData() {
    this.searchPhoneNo = "";
    this.searchKey = "";
     this.reportedProblemForm.reset();
    this.currentPageReportedProblem = 1;
    this.getReportedData("");
  }

   pageChangedReportedProblemList(event: PageEvent) {
    this.currentPageReportedProblem = event.pageIndex + 1;
    this.reportedProblemitemsPerPage = event.pageSize;
    if (!this.searchPhoneNo) {
      this.getReportedData("");
    } else {
      this.searchReportedData();
    }
  }

  TotalItemPerPage(event) {
    this.showItemPerPage = Number(event.value);
    if (this.currentPageReportedProblem > 1) {
      this.currentPageReportedProblem = 1;
    }
    if (!this.searchPhoneNo) {
      this.getReportedData(this.showItemPerPage);
    } else {
      this.searchReportedData();
    }
  }
}
