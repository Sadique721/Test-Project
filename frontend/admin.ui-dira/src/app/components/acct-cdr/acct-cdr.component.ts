import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { AcctCdrService } from "src/app/service/acct-cdr.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as XLSX from "xlsx";
import { DatePipe } from "@angular/common";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CustomerDetailsService } from "../../service/customer-details.service";
import { BehaviorSubject } from "rxjs";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { LiveUserService } from "src/app/service/live-user.service";
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from "@angular/material/dialog";
declare var $: any;

@Component({
    selector: "app-acct-cdr",
    templateUrl: "./acct-cdr.component.html",
    styleUrls: ["./acct-cdr.component.css"],
    standalone: false
})
export class AcctCdrComponent implements OnInit {
    searchAcctCdrForm: UntypedFormGroup;
    searchSubmitted = false;
    groupData: any[] = [];
    fileName = "CDR.xlsx";
    //Used and required for pagination
    totalRecords: number;
    currentPage = 1;
    presentGroupForm: UntypedFormGroup;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    dialogId: boolean = false;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: any;
    //Used to store error data and error message
    errorData: any = [];
    errorMsg = "";
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    editMode: boolean = false;
    cdrDetail: any = [];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    hideTable: boolean = false;
    value: string = "Simple Search";
    displayShiftLocationDetails: boolean = false;
    rootInstanceData: any = null;
    searchOption: any = [];

    cols: any[] = [
        {
            field: "",
            header: "",
            customExportHeader: ""
        }
    ];
    selectedCols: any[] = [];
    exportColumns: any = [];

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    exportCSVAccess: any;
    exportXLSAccess: any;
    exportPDFAccess: any;

    custId = new BehaviorSubject({
        custId: ""
    });
    searchOptionSelect = [
        { label: "User Name", value: "userName" },
        { label: "Framed Ip Address", value: "framedIpAddress" },
        { label: "Nas Ip Address", value: "nasIpAddress" },
        { label: "Class Attribute", value: "classAttribute" },
        { label: "Account Status Type", value: "acctStatusType" },
        { label: "Nas Identifier", value: "nasIdentifier" },
        { label: "Framed Route", value: "framedRoute" },
        { label: "Nas Port Type", value: "nasPortType" },
        { label: "Nas Port Id", value: "nasPortId" },
        { label: "Account Multi Session Id", value: "acctMultiSessionId" },
        { label: "Framed Ipv6 Address", value: "framedIpv6Address" },
        { label: "Account Session Id", value: "acctSessionId" },
        { label: "Source Ip Address", value: "sourceIpAddress" }
    ];
    userId: string;
    superAdminId: string;
    messageService: any;

    constructor(
        private toastr: ToastrService,
        private AcctCdrService: AcctCdrService,
        private liveUserService: LiveUserService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private datePipe: DatePipe,
        loginService: LoginService, private dialog: MatDialog,
        private customerDetailsService: CustomerDetailsService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.exportCSVAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_CDR_CSV);
        this.exportXLSAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_CDR_XLS);
        this.exportPDFAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_CDR_PDF);

        this.findAllAcctCdr("");
    }

    ngOnInit(): void {
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
        this.searchAcctCdrForm = this.fb.group({
            userName: [""],
            framedIpAddress: [""],
            nasIpAddress: [""],
            classAttribute: [""],
            acctStatusType: [""],
            acctSessionId: [""],
            nasIdentifier: [""],
            framedRoute: [""],
            nasPortType: [""],
            nasPortId: [""],
            acctMultiSessionId: [""],
            framedIpv6Address: [""],
            sourceIpAddress: [""],
            fromDate: [""],
            toDate: [""]
        });
        this.presentGroupForm = this.fb.group({
            maxconcurrentsession: [""]
        });
    }
    async exportExcel() {
        this.groupData = [];
        this.AcctCdrService.getAllCDRsForExport().subscribe((response: any) => {
            this.groupData = response.acctCdrList;
            if (response.acctCdrList.length > 0) {
                const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.groupData);
                const wb: XLSX.WorkBook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
                XLSX.writeFile(wb, this.fileName);
            } else {
                error: (error) => {
                    this.toastr.info(response.message, 'No record found for export!');

                }
            }
        });
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllAcctCdr(this.showItemPerPage);
        } else {
            this.search();
        }
    }

    async findAllAcctCdr(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.AcctCdrService.findAllAcctCdrData(page, size).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(response.message, 'No record found for export!');



                } else {
                    this.cols = [];
                    this.selectedCols = [];
                    this.groupData = response.acctCdr.content;
                    this.groupData.forEach(element => {
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
                        element.isDeleted = false;
                        element.isView = true;
                        element.isDisconnect = false;
                        element.createDate = this.datePipe.transform(
                            element.createDate,
                            "dd MMM yyyy HH:mm:ss"
                        );
                        element.lastModificationDate = this.datePipe.transform(
                            element.lastModificationDate,
                            "dd MMM yyyy HH:mm:ss"
                        );
                    });

                    this.totalRecords = response.acctCdr.totalElements;
                    this.getRootInstanceData(this.groupData);
                }
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(error.error.errorMessage, 'info!');



                } else {
                    this.toastr.error(error.error.errorMessage, 'Failed!');



                }
            }
        );
    }

    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    cdrDetailModal: boolean = false;
    async getCdrDetail(event: any) {
        this.AcctCdrService.getCdrDetail(event.cdrId, event.mvnoid).subscribe(
            (response: any) => {
                this.cdrDetail = response.cdrDetail;
                this.cdrDetailModal = true;
                this.dialog.open(this.detailsDialog, {
                    width: '80%',
                    disableClose: true
                });
            },
            (error: any) => {
                this.toastr.error(error.error.errorMessage, 'Failed!');


            }
        );
    }
    deleteConfirm(authRespId, mvnoId) {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete this record?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result === true) {
                this.deleteGroupById(authRespId, mvnoId);
            } else {
                this.toastr.info(`You have rejected`, "Info!");
            }
        });

        // this.confirmationService.confirm({
        //     message: "Do you want to delete this record?",
        //     header: "Delete Confirmation",
        //     icon: "pi pi-info-circle",
        //     accept: () => {
        //         this.deleteGroupById(authRespId, mvnoId);
        //     },
        //     reject: () => {
        //         error: (error) => {
        //             this.toastr.info(error.ERROR || error.responseMessage, 'You have rejected!');


        //         }


        //     }
        // });
    }
    async deleteGroupById(cdrId, selectedMvnoId) {
        this.AcctCdrService.deleteAcctCdrById(cdrId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && this.groupData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllAcctCdr("");
                } else {
                    this.search();
                }
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(error.error.errorMessage, 'Failed!');


            }
        );
    }

    openModal(id, custId) {
        this.dialogId = true;

        this.custId.next({
            custId: custId
        });
    }

    closeSelectStaff() {
        this.dialogId = false;
    }

    async search() {
    // this.currentPage = 1;

    var f = "";
    var t = "";

    this.searchSubmitted = true;
    if (this.searchAcctCdrForm.value.userName == null) {
      this.searchAcctCdrForm.value.userName = "";
    }
    if (this.searchAcctCdrForm.value.framedIpAddress == null) {
      this.searchAcctCdrForm.value.framedIpAddress = "";
    }
    if (this.searchAcctCdrForm.value.nasIpAddress == null) {
      this.searchAcctCdrForm.value.nasIpAddress = "";
    }
    if (this.searchAcctCdrForm.value.classAttribute == null) {
      this.searchAcctCdrForm.value.classAttribute = "";
    }
    if (this.searchAcctCdrForm.value.acctStatusType == null) {
      this.searchAcctCdrForm.value.acctStatusType = "";
    }
    if (this.searchAcctCdrForm.value.nasIdentifier == null) {
      this.searchAcctCdrForm.value.nasIdentifier = "";
    }
    if (this.searchAcctCdrForm.value.framedRoute == null) {
      this.searchAcctCdrForm.value.framedRoute = "";
    }
    if (this.searchAcctCdrForm.value.nasPortType == null) {
      this.searchAcctCdrForm.value.nasPortType = "";
    }
    if (this.searchAcctCdrForm.value.nasPortId == null) {
      this.searchAcctCdrForm.value.nasPortId = "";
    }
    if (this.searchAcctCdrForm.value.acctMultiSessionId == null) {
      this.searchAcctCdrForm.value.acctMultiSessionId = "";
    }
    if (this.searchAcctCdrForm.value.framedIpv6Address == null) {
      this.searchAcctCdrForm.value.framedIpv6Address = "";
    }
    if (this.searchAcctCdrForm.value.acctSessionId == null) {
      this.searchAcctCdrForm.value.acctSessionId = "";
    }
    if (this.searchAcctCdrForm.value.sourceIpAddress == null) {
      this.searchAcctCdrForm.value.sourceIpAddress = "";
    }
    if (this.searchAcctCdrForm.value.fromDate) {
      f = this.datePipe.transform(this.searchAcctCdrForm.controls.fromDate.value, "yyyy-MM-dd");
    }
    if (this.searchAcctCdrForm.value.toDate) {
      t = this.datePipe.transform(this.searchAcctCdrForm.controls.toDate.value, "yyyy-MM-dd");
    }
    if (this.searchAcctCdrForm.valid) {
      const isSearchKeyValid = this.searchOption.includes(this.searchkey);
      const hasDates = this.searchAcctCdrForm.value.fromDate || this.searchAcctCdrForm.value.toDate;
      if (!this.searchkey || (!isSearchKeyValid && !hasDates)) {
        this.currentPage = 1;
      }
      if (this.searchOption.length === 0) {
        this.searchkey = this.searchAcctCdrForm.value.fromDate
          ? this.searchAcctCdrForm.value.fromDate
          : this.searchAcctCdrForm.value.toDate;
      } else {
        this.searchkey = this.searchOption[0];
      }
      if (this.showItemPerPage) {
        this.itemsPerPage = this.showItemPerPage;
      }
      this.groupData = [];
      const { fromDate, toDate, ...rest } = this.searchAcctCdrForm.value;
      let searchData = {
        page: this.currentPage,
        size: this.itemsPerPage,
        fromDate: f,
        toDate: t,
        ...rest
      };
      this.AcctCdrService.getAcctCdrDataByUsername(searchData).subscribe(
        (response: any) => {
          if (response.acctCdr && response.acctCdr.content && response.acctCdr.content.length > 0) {
            this.groupData = response.acctCdr.content;

            this.groupData.forEach(element => {
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
              element.isDeleted = true;
              element.isView = true;
              element.isDisconnect = true;
              element.createDate = this.datePipe.transform(
                element.createDate,
                "dd MMM yyyy HH:mm:ss"
              );
              element.lastModificationDate = this.datePipe.transform(
                element.lastModificationDate,
                "dd MMM yyyy HH:mm:ss"
              );
            });

            this.totalRecords = response.acctCdr.totalElements;
            this.getRootInstanceData(this.groupData);
          } else {
            this.groupData = [];
            this.totalRecords = 0;
            this.messageService.add({
              severity: "info",
              summary: "Info",
              detail: response.infomsg,
              icon: "far fa-times-circle"
            });
          }
        },
        (error: any) => {
          this.groupData = [];
          this.getRootInstanceData(this.groupData);
          if (error.error.status == 404) {
            this.totalRecords = 0;
            this.messageService.add({
              severity: "info",
              summary: "Info",
              detail: error.error.infomsg,
              icon: "far fa-times-circle"
            });
          } else {
            this.totalRecords = 0;
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: error.error.errorMessage,
              icon: "far fa-times-circle"
            });
          }
        }
      );
    }
  }

    clearSearchForm() {
        this.currentPage = 1;
        this.itemsPerPage = 5;
        this.searchSubmitted = false;
        this.findAllAcctCdr("");
        this.searchAcctCdrForm.reset();
    }
    pageChanged(pageNumber) {
        this.currentPage = pageNumber.pageIndex + 1;
        this.itemsPerPage = pageNumber.pageSize
        if (!this.searchkey) {
            this.findAllAcctCdr("");
        } else {
            this.search();
        }
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        let userId = localStorage.getItem("userId");
        if (userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            this.searchAcctCdrForm.reset();
            error: (error) => {
                this.toastr.info(error.error.infomsg, 'You are not authorized to do this operation. Please contact to the administrator!');
            }

            return false;
        }
        return true;
    }

    manageLocalData(data: any) {
        this.cols = [];
        this.selectedCols = [];
        this.rootInstanceData = data;
        if (data?.[0]) {
            this.cols = Object.keys(data?.[0]).map(ele => ({
                field: ele,
                header: ele,
                customExportHeader: ele
            }));
            this.cols.push({
                field: "Actions",
                header: "Actions"
            });

            Object.keys(data?.[0]).forEach(element => {
                if (
                    element === "userName" ||
                    element === "framedIpAddress" ||
                    element === "acctInputOctets" ||
                    element === "acctOutputOctets" ||
                    element === "callingStationId" ||
                    element === "framedIPv6Prefix" ||
                    element === "createDate" ||
                    element === "acctSessionTime" ||
                    element === "nasIpAddress"
                ) {
                    this.selectedCols.push({
                        field: element,
                        header: element,
                        customExportHeader: element
                    });
                }
            });
            this.selectedCols.push({
                field: "Actions",
                header: "Actions"
            });
        }
        this.exportColumns = Object.keys(data?.[0]).map(col => ({
            title: col,
            dataKey: col
        }));
    }

    getRootInstanceData(data: any) {
        if (data) {
            this.manageLocalData(data);
        }
    }


    @ViewChild('displayShiftLocationDetailsDialog') displayShiftLocationDetailsDialog!: TemplateRef<any>;

    closeShiftLocation() {
        this.displayShiftLocationDetails = false;
    }

    open(event) {
        this.custId = event.custid;
        this.displayShiftLocationDetails = true;
        this.dialog.open(this.displayShiftLocationDetailsDialog, {
            width: '400px',
            disableClose: true
        });
    }
    saveShiftLocation(dialogRef) {
        // this.custId =
        const custId = this.custId;
        const data = {
            custId: Number(custId),
            maxconcurrentsession: this.presentGroupForm.get("maxconcurrentsession").value
        };
        this.liveUserService.postLiveUserDetail(data).subscribe(
            (response: any) => {
                dialogRef.close()
                this.displayShiftLocationDetails = false;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    selSearchOption(event) {
        this.searchOption = event.value;
    }


    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
}
