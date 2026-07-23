import { DatePipe } from "@angular/common";
import { Component, OnInit, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { LoginService } from "src/app/service/login.service";
import { ActivatedRoute, Router } from "@angular/router";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { StaffService } from "src/app/service/staff.service";
import { RoleService } from "src/app/service/role.service";
import { MvnoManagementService } from "src/app/service/mvno-management.service";
import moment from "moment";
import { MatDialog } from "@angular/material/dialog";

import { ToastrService } from 'ngx-toastr';
declare var $: any;

@Component({
    selector: "app-mvno-list",
    templateUrl: "./mvno-list.component.html",
    styleUrls: ["./mvno-list.component.scss"],
    standalone: false
})
export class MvnoListComponent implements OnInit {
    mvnoTitle = RadiusConstants.MVNO;
    currentPageMvno = 1;
    mvnoitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    mvnototalRecords: any;
    mvnoListData: any;
    mvnoData: any;
    searchMvnoName: any;
    AclClassConstants;
    AclConstants;
    searchData: any;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    mvnoIdData: any;
    migrationDialog: boolean = false;
    oldMvno: any = {};
    newMvnoId: any;
    oldMvnoName: any = {};
    newMvnoName: any;
    mvnoNameList: any[] = [];
    mvnoOptions: { name: string; id: any }[] = [];
    mvnoMasterOptions: { name: string; id: any }[] = [];
    workFlowData: any[] = [];
    isDownloadView: boolean = false;
    generateIspInvoiceForm: UntypedFormGroup;
    maxDisbleDate: Date;
    isShowCreateView: boolean = false;
    mvnoId: string
    @ViewChild('migrationDialog') migrationDialogRef: any;
    constructor(
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private toastr: ToastrService,
        private customerManagementService: CustomermanagementService,
        private mvnoManagementService: MvnoManagementService,
        private dialog: MatDialog
    ) {
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    async ngOnInit() {
        this.getMvnoNames();
        this.searchData = {
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
        this.getMVNOData("");

        this.generateIspInvoiceForm = this.fb.group({
            startDate: ["", Validators.required],
            // endDate: ["", Validators.required]
        });
        this.maxDisbleDate = new Date();
    }

    clearMvno() {
        this.searchMvnoName = "";
        this.getMVNOData("");
    }

    clearIspForm() {
        this.generateIspInvoiceForm.reset();
    }

    closeDialog() {
        this.migrationDialog = false;
        this.newMvnoId = "";
        this.dialog.closeAll();
    }
    pageChangedMvnoList(pageNumber) {
        this.currentPageMvno = pageNumber.pageIndex + 1;
        if (this.searchkey) {
            this.searchMvno();
        } else {
            this.getMVNOData("");
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMvno > 1) {
            this.currentPageMvno = 1;
        }
        if (!this.searchkey) {
            this.getMVNOData(this.showItemPerPage);
        } else {
            this.searchMvno();
        }
    }

    searchMvno(): void {
        if (!this.searchkey || this.searchkey != this.searchMvnoName) {
            this.currentPageMvno = 1;
        }
        this.searchkey = this.searchMvnoName;
        if (this.showItemPerPage) {
            this.mvnoitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchMvnoName ? this.searchMvnoName.trim() : "";
        const page = {
            page: this.currentPageMvno,
            pageSize: this.mvnoitemsPerPage
        };
        this.mvnoManagementService.searchMVNO(page, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode === 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                    this.mvnoListData = [];
                    this.mvnototalRecords = 0;
                } else {
                    this.mvnoListData = response.dataList;
                    this.mvnototalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getMVNOData(list) {
        let size;
        this.searchkey = "";
        let pageList = this.currentPageMvno;
        if (list) {
            size = list;
            this.mvnoitemsPerPage = list;
        } else {
            size = this.mvnoitemsPerPage;
        }
        const url = "/mvno";
        let mvnodata = {
            page: pageList,
            pageSize: size
        };
        this.mvnoManagementService.postMethod(url, mvnodata).subscribe(
            (response: any) => {
                this.mvnoListData = response.dataList;
                this.mvnototalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    getMvnoNames() {
        const url = "/mvno/getMvnoNameAndIds";
        this.mvnoManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.mvnoNameList = response.dataList;
                this.mvnoOptions = this.mvnoNameList.map(mvno => ({
                    name: mvno.name,
                    id: mvno.id
                }));
                this.mvnoMasterOptions = [
                    ...this.mvnoNameList.map(mvno => ({
                        name: mvno.name,
                        id: mvno.id
                    }))
                ];
            },
            (error: any) => {
                console.log("Error:::", error);
            }
        );
    }
    sendMvnoId(mvno) {
        this.mvnoOptions = this.mvnoMasterOptions.filter(mvNo => mvNo.id != mvno.id);
        this.migrationDialog = true;
        this.dialog.open(this.migrationDialogRef);
        this.oldMvno = mvno;
        this.oldMvnoName = mvno.name;
        this.isDownloadView = false;
    }

    transferISP() {
        this.workFlowData = [];
        this.mvnoManagementService.PostMvnoId(this.oldMvno.id, this.newMvnoId).subscribe(
            (response: any) => {
                if (response.dataList) {
                    this.isDownloadView = true;
                    this.workFlowData = response.dataList;
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.migrationDialog = false;
                    this.isDownloadView = false;
                    this.toastr.success(`Successfully Sended`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
                this.dialog.closeAll()
            },
            (error: any) => {
                this.toastr.error(`Failed to Send`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    deleteConfirmonMvno(mvnoData) {
        if (mvnoData) {
            this.confirmationService.confirm({
                message: "Do you want to delete this MVNO?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteMvno(mvnoData);
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                }
            });
        }
    }

    deleteMvno(id) {
        const url = "/mvno/delete/" + id;
        this.mvnoManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageMvno != 1 && this.mvnoListData.length == 1) {
                    this.currentPageMvno = this.currentPageMvno - 1;
                }
                this.toastr.success(`Successfully Deleted`, 'Success!')
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Successfully",
                //     detail: response.message,
                //     icon: "far fa-check-circle"
                // });
                if (this.searchkey) {
                    this.searchMvno();
                } else {
                    this.getMVNOData("");
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    downloadFile(data: any[], filename: string = "data") {
        const csvData = this.convertToCSV(this.workFlowData);
        const blob = new Blob([csvData], { type: "text/csv;charset=utf-8;" });
        const link = document.createElement("a");
        if (link.download !== undefined) {
            const url = URL.createObjectURL(blob);
            const currentDate = new Date().toISOString().slice(0, 10); // YYYY-MM-DD format
            const fullFilename = `${this.oldMvnoName.name}_Pendingtask${currentDate}.csv`;
            link.setAttribute("href", url);
            link.setAttribute("download", fullFilename);
            link.style.visibility = "hidden";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }

    private convertToCSV(objArray: any[]): string {
        const array = typeof objArray !== "object" ? JSON.parse(objArray) : objArray;
        let str = "";
        let row = "";

        // Extract header
        const headers = Object.keys(array[0]);
        row += headers.join(",") + "\r\n";
        str += row;

        // Extract data
        for (let i = 0; i < array.length; i++) {
            let line = "";
            for (const index in array[i]) {
                if (line !== "") line += ",";
                line += array[i][index];
            }
            str += line + "\r\n";
        }
        return str;
    }

    generateInvoice() {
        var request = { ...this.generateIspInvoiceForm.value };
        request.startDate = moment(request.startDate).startOf("day").format("YYYY-MM-DD");
        // request.endDate = moment(request.endDate).startOf("day").format("YYYY-MM-DD");
        let url = `/invoiceV2/genarateIspInvoice`;
        this.customerManagementService.postMethod(url, request).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    response.responseMessage;
                    this.toastr.success(`${response.message}`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully Generated",
                    //     detail: response.message,
                    //     icon: "far fa-check-circle"
                    // });
                    this.clearIspForm();
                } else if (response.responseCode == 204) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                    this.clearIspForm();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
                this.clearIspForm();
            }
        );
    }

    createMvno() {
        this.isShowCreateView = true;
    }

    closeCreateDialog(event) {
        if (event) {
            this.getMVNOData("");
        }
        this.isShowCreateView = false;
        this.mvnoId = '';
    }

    editMvno(mvnoId) {
        this.mvnoId = mvnoId;
        this.isShowCreateView = true;

    }
}
