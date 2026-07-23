import { Component, Input, Output, OnInit, EventEmitter } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";import { ToastrService } from "ngx-toastr";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";

declare var $: any;

@Component({
  selector: "app-select-staff",
  templateUrl: "./select-staff.component.html",
  styleUrls: ["./select-staff.component.css"],
})
export class SelectStaffComponent implements OnInit {
  @Input() selectedStaff: any = [];
  @Output() selectedStaffChange = new EventEmitter();
  @Output() closeStaff = new EventEmitter();
  newFirst = 0;

  parentStaffListdataitemsPerPageForStaff = RadiusConstants.ITEMS_PER_PAGE;
  parentstaffListdatatotalRecords: any;
  currentPageParentStaffListdata = 1;

  searchDeatil = "";
  staffData = [];

  constructor(
    private spinner: NgxSpinnerService,private toastr: ToastrService,
    private customerManagementService: CustomermanagementService,
    public confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.newFirst = 0;
    this.getStaffDetailById();
    // this.selectedStaff = [];
    $("#selectStaff").modal("show");
  }

  getStaffDetailById() {
    let currentPageForStaff;
    currentPageForStaff = this.currentPageParentStaffListdata;
    const data = {
      page: currentPageForStaff,
      pageSize: this.parentStaffListdataitemsPerPageForStaff,
    };
    const url = "/staffuser/list";
    this.customerManagementService
      .commonPostMethod(url, data)
      .subscribe((response: any) => {
        this.staffData = response.staffUserlist;
        this.parentstaffListdatatotalRecords =
          response.pageDetails.totalRecords;
        // this.staffDataList.forEach((element, i) => {
        //   element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
        //   this.data.push(element.id);
        // });
        this.spinner.hide();
      });
  }

  searchStaffByName() {
    this.spinner.show();
    var searchData = {
      filters: [
        {
          filterDataType: "",
          filterValue: this.searchDeatil.trim(),
          filterColumn: "any",
          filterOperator: "equalto",
          filterCondition: "and",
        },
      ],
      page: this.currentPageParentStaffListdata,
      pageSize: this.parentStaffListdataitemsPerPageForStaff,
    };
    let url = "/staffuser/search";
    this.customerManagementService.commonPostMethod(url, searchData).subscribe(
      (response: any) => {
        //
        this.staffData = response.dataList;
        this.parentstaffListdatatotalRecords = response.totalRecords;
        this.spinner.hide();
      },
      (error: any) => {
        this.parentstaffListdatatotalRecords = 0;
        if (error.error.status == 404) {
          this.messageService.add({
            severity: "info",
            summary: "Info",
            detail: error.error.msg,
            icon: "far fa-times-circle",
          });
          this.staffData = [];
        } else {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.ERROR,
            icon: "far fa-times-circle",
          });
        }
        this.spinner.hide();
      }
    );
  }

  paginateStaff(event) {
    this.currentPageParentStaffListdata = event.page + 1;
    // this.first = event.first;
    if (this.searchDeatil) {
      this.searchStaffByName();
    } else {
      this.getStaffDetailById();
    }
  }

  clearSearchForm() {
    this.searchDeatil = "";
    this.spinner.hide();
    this.currentPageParentStaffListdata = 1;
    this.getStaffDetailById();
  }

  saveSelstaff() {
    this.selectedStaffChange.emit(this.selectedStaff);
    // this.staffCustList = [
    //   {
    //     id: Number(this.selectedStaffCust.id),
    //     name: this.selectedStaffCust.firstname,
    //   },
    // ];
    // this.modalCloseStaff();
    $("#selectStaff").modal("hide");
  }

  modalCloseStaff() {
    $("#selectStaff").modal("hide");
    this.closeStaff.emit();
    this.currentPageParentStaffListdata = 1;
    this.newFirst = 1;
    this.searchDeatil = "";
    this.staffData = [];
  }
}
