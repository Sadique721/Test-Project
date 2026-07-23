import { Component, OnInit, ViewChild, AfterViewInit, TemplateRef, Input } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { TaskManagementService } from "src/app/service/task-management.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from "ngx-toastr";

import { DatePipe } from "@angular/common";
import * as FileSaver from 'file-saver';
import * as XLSX from 'xlsx';
import { LoginService } from "src/app/service/login.service";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
import { CustNotes } from "../../model/CustNotes";

(pdfMake as any).vfs = (pdfFonts as any).pdfMake ? (pdfFonts as any).pdfMake.vfs : pdfFonts;

@Component({
    selector: "app-customer-notes",
    templateUrl: "./customer-notes.component.html",
    styleUrls: ["./customer-notes.component.css"],
    standalone: false
})
export class CustomerNotesComponent implements OnInit {
    @ViewChild("addNotesDialogTemplate") addNotesDialogTemplate;
    addNotesDialogRef!: MatDialogRef<any>;
    customerId = 0;
    custType: string = "";
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerNotesListPdf: any = [];
    totalRecords = 0;
    custData: any;
    staffData: any = [];
    staffDetailModal: boolean = false;
    pageLimitOptions = RadiusConstants.pageLimitOptions;

    customerNotesList: MatTableDataSource<any> = new MatTableDataSource<any>();
    displayedColumns: string[] = ['id', 'createdByName', 'createdOn', 'createdStaffTeam', 'notes'];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('staffDetailDialog') staffDetailDialog: TemplateRef<any>;
    @ViewChild('serviceAreaDetailDialog') serviceAreaDetailDialog: TemplateRef<any>;
    dialogRefStaff: MatDialogRef<any>;
    dialogRefService: MatDialogRef<any>;

    exportXLSAccess: boolean = false;
    @Input() function: any = "";

    addNotesForm: UntypedFormGroup;
    custIdForNotes: any;
    addNotesPopup: boolean = false;
    notesSubmitted: boolean = false;
    addNotesData: CustNotes;
    mvnoid: number;
    staffid: number;
    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService,
        private loginService: LoginService,
        private customerManagementService: CustomermanagementService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public datepipe: DatePipe,
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private dialog: MatDialog
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.mvnoid = Number(localStorage.getItem("mvnoId"));
        this.staffid = Number(localStorage.getItem("userId"));
    }

    ngOnInit() {
        this.getCustomersDetail(this.customerId);

        switch (this.function) {
            case "acct-cdr":
                this.exportXLSAccess = this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_CDR_XLS);
                break;
            case "live_user":
                this.exportXLSAccess = this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_LIVE_USERS_XLS);
                break;
            default:
                this.exportXLSAccess = true;
                break;
        }
        this.addNotesForm = this.fb.group({
            id: [""],
            notes: ["", Validators.required]
        });
    }

    ngAfterViewInit() {
        this.customerNotesList.paginator = this.paginator;
        this.customerNotesList.sort = this.sort;
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    exportPdf() {
        const url = `/findAllCustomerNotes/${this.customerId}`;
        this.customerManagementService.getMethodForCustomerNotesPdf(url).subscribe(
            async (response: any) => {
                this.customerNotesListPdf = await response.customerNotesList.map(
                    (item: any) => ({
                        ...item,
                        createdOn: this.datepipe.transform(item.createdOn, 'dd-MM-yyyy HH:mm:ss')
                    })
                );;

                const docDefinition: any = {
                    content: [
                        { text: 'Customer Notes', style: 'header' },
                        {
                            columns: [
                                { text: `Customer Name: ${this.custData.custname}`, width: '*' },
                                { text: `Account Number: ${this.custData.acctno}`, width: '*' }
                            ],
                            margin: [0, 0, 0, 8]
                        },

                        {
                            columns: [
                                { text: `OLT : ${this.custData.oltName}`, width: '*' },
                                { text: `FAT: ${this.custData.areaName}`, width: '*' }
                            ],
                            margin: [0, 0, 0, 8]
                        },

                        {
                            columns: [
                                { text: `Service Area: ${this.custData.serviceareaName}`, width: '*' },
                                { text: `Account Status: ${this.custData.customerServiceMappingList[0].status}`, width: '*' }
                            ],
                            margin: [0, 0, 0, 20]
                        },

                        {
                            table: {
                                headerRows: 1,
                                widths: ['*', '*', '*'],
                                body: [
                                    ['Notes', 'Created By', 'Created Date and time'],
                                    ...this.customerNotesListPdf.map(c => [c.notes, c.createdByName, c.createdOn])
                                ],
                            },
                        }
                    ],
                    styles: {
                        header: {
                            fontSize: 18,
                            bold: true,
                            alignment: 'center',
                            margin: [0, 0, 0, 15]
                        }
                    }
                };
                pdfMake.createPdf(docDefinition).download('customer-notes.pdf');
            },
        );
    }

    exportExcel() {
        const url = `/findAllCustomerNotes/${this.customerId}`;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            const exportData = response.customerNotesList;

            exportData.map((item: any) => {
                item['Customer Name'] = this.custData.custname;
                item['Account Number'] = this.custData.acctno;
                item['Notes'] = item.notes;
                item['Created By'] = item.createdByName;
                item['Id'] = item.id;
                item['Create Time'] = this.datepipe.transform(item.createdOn, 'dd-MM-yyyy HH:mm:ss');
                item['Account Status.'] = this.custData.customerServiceMappingList[0]?.status || '-';
                item['FAT'] = this.custData.areaName;
                item['Service Area'] = this.custData.serviceareaName;
                item['OLT'] = this.custData.oltName;
                delete item.notes;
                delete item.createdBy;
                delete item.id;
                delete item.createdOn;
                delete item.custId;
                delete item.createdByName;
            });
            const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(exportData);
            const workbook: XLSX.WorkBook = {
                Sheets: { 'data': worksheet },
                SheetNames: ['data'],
            };
            const excelBuffer: any = XLSX.write(workbook, {
                bookType: 'xlsx',
                type: 'array'
            });
            this.saveAsExcelFiles(excelBuffer, 'CustomerNotes');
        });
    }

    saveAsExcelFiles(buffer: any, fileName: string): void {
        const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
        const EXCEL_EXTENSION = '.xlsx';
        const data: Blob = new Blob([buffer], { type: EXCEL_TYPE });
        const timestamp = new Date().getTime();
        FileSaver.saveAs(data, `${fileName}_export_${timestamp}${EXCEL_EXTENSION}`);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            this.getAllCustomerNotes();
        });
    }

    getAllCustomerNotes() {
        const url = `/findAllCustomerNotesWithPagination/${this.customerId}?page=${this.currentPage}&pageSize=${this.itemsPerPage}`;
        this.customerManagementService.getMethodForCustomerNotes(url).subscribe(
            async (response: any) => {
                if (response?.customerNotesList?.length === 0) {
                    this.customerNotesList.data = [];
                    this.totalRecords = 0;
                } else {
                    this.customerNotesList = await response.customerNotesList?.content || [];
                    this.totalRecords = await response?.customerNotesList?.totalElements || 0;
                }
                this.updatePaginator();
            },
            (error: any) => {
                this.customerNotesList.data = [];
                this.totalRecords = 0;
                this.toastr.error(`${error.error.ERROR}`, "Failed to fetch customer notes");
                this.updatePaginator();
            }
        );
    }

    updatePaginator() {
        if (this.paginator) {
            this.customerNotesList.paginator = this.paginator;
            this.customerNotesList.sort = this.sort;
            this.paginator.length = this.totalRecords;
            this.paginator.pageIndex = this.currentPage - 1;
        }
    }

    pageChangeEventForChildCustomers(event: PageEvent) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        this.getAllCustomerNotes();
    }

    itemPerPageChangeEvent(event: any) {
        this.currentPage = 1;
        this.itemsPerPage = Number(event.value);
        if (this.paginator) this.paginator.firstPage();
        this.getAllCustomerNotes();
    }

    closeModalStaff() {
        if (this.dialogRefStaff) {
            this.dialogRefStaff.close();
        }
    }

    serviceAreaDetailModal: boolean = false;
    serviceAreaList: any = [];
    branchId: any;
    serviceareaCheck = true;

    getServiceByBranch(e) {
        this.branchId = e.value;
        this.serviceareaCheck = false;
        const url = "/findServiceAreaByBranchId?BranchId=" + this.branchId;
        this.savbillCommonBaseService.getConnection(url).subscribe((response: any) => {
            this.serviceAreaList = response.serviceAreaList;
            //$("#PlanDetailsShow").modal("show");
        });
    }

    onClickServiceArea() {
        this.serviceAreaList = this.staffData.serviceAreasNameList || [];
        // Open dialog only after data is set
        this.dialogRefService = this.dialog.open(this.serviceAreaDetailDialog, {
            width: '500px',
            disableClose: true
        });
        this.dialogRefService.afterClosed().subscribe(() => this.closeModalOfArea());
    }

    closeModalOfArea() {
        if (this.dialogRefService) {
            this.dialogRefService.close();
        }
    }

    openStaffDetailModal(staffId: number) {
        this.savbillCommonBaseService.get(`/getStaffUser/${staffId}`).subscribe(
            (response: any) => {
                this.staffData = response.Staff;
                // Open dialog only after data is set
                this.dialogRefStaff = this.dialog.open(this.staffDetailDialog, {
                    width: '900px',
                    height: '500px'
                });
                this.dialogRefStaff.afterClosed().subscribe(() => this.closeModalStaff());
            },
            error => this.showError("Failed to load staff details", error)
        );
    }


    private showError(message: string, error: any) {
        // this.messageService.add({
        //     severity: "error",
        //     summary: message,
        //     detail: error.error?.msg || error.error?.ERROR || "An unexpected error occurred",
        //     icon: "far fa-times-circle"
        // }); 
        this.toastr.error(error.error?.ERROR || error.error?.msg || "An unexpected error occurred");
    }
    addNotesSetFunction() {
        this.custIdForNotes = this.customerId;
        this.addNotesDialogRef = this.dialog.open(this.addNotesDialogTemplate, {
            width: '600px'
        });
    }
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
                                this.getAllCustomerNotes()
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
}
