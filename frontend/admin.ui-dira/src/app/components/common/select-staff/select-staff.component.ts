import { Component, Input, Output, OnInit, EventEmitter, viewChild, ViewChild, Inject } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomerService } from "src/app/service/customer.service";
import { StaffService } from "src/app/service/staff.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { ToastrService } from 'ngx-toastr';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatButtonModule } from '@angular/material/button';


export interface DialogData {
    selectedStaff?: any;
}


declare var $: any;

@Component({
    selector: "app-select-staff",
    templateUrl: "./select-staff.component.html",
    styleUrls: ["./select-staff.component.css"],
    standalone: false
})
export class SelectStaffComponent implements OnInit {
    staffTableColumns: string[] = ['select', 'name', 'username', 'partnerName'];
    @Input() selectedStaff: any = [];

    @Output() selectedStaffChange = new EventEmitter();
    @Output() closeStaff = new EventEmitter();
    @ViewChild("SelectedSaff") SelectedSaff;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    newFirst = 0;

    parentStaffListdataitemsPerPageForStaff = RadiusConstants.ITEMS_PER_PAGE;
    // parentstaffListdatatotalRecords: any;
    parentstaffListdatatotalRecords = 0;
    currentPageParentStaffListdata = 1;

    searchDeatil = "";
    dataSource = new MatTableDataSource<any>();
    staffData = [];
    displayDTVHistory: boolean = false;
    // dialogRef: MatDialogRef<SelectStaffComponent>


    constructor(
        private dialog: MatDialog,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private customerService: CustomerService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private staffService: StaffService,
        public dialogRef: MatDialogRef<SelectStaffComponent>,
        @Inject(MAT_DIALOG_DATA) public data: DialogData
    ) { }

    ngOnInit(): void {
        // this.clearSearchForm();
        this.newFirst = 0;
        this.getStaffDetailById();
        // this.selectedStaff = [];
        this.displayDTVHistory = true;
    }
    // ngAfterViewInit() {
    //     this.dialogRef = this.dialog.open(this.SelectedSaff, {
    //         width: '1100px',
    //         maxWidth: '90vw',
    //         height: 'auto',
    //         autoFocus: false,
    //         // id: `unique-dialog-id-${new Date().getTime()}` // ensure unique id to force new dialog instance
    //     });
    //     this.dialogRef.afterClosed().subscribe((result) => {
    //         // this.dialogRef = null;
    //         this.dialogRef.close();
    //         this.modalCloseStaff();
    //         console.log("dialog close in save btn =>", result)
    //         if (result) {
    //             this.selectedStaff = result;
    //             this.selectedStaffChange.emit(this.selectedStaff);
    //         }
    //     });

    //     this.dataSource.paginator = this.paginator;
    //     this.dataSource.sort = this.sort;
    // }
    getStaffDetailById() {
        let currentPageForStaff;
        currentPageForStaff = this.currentPageParentStaffListdata;
        const data = {
            page: this.currentPageParentStaffListdata,
            pageSize: this.parentStaffListdataitemsPerPageForStaff
        };

        const url = "/staffuser/Activestaff?product=BSS";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            this.staffData = response.staffUserlist;
            this.parentstaffListdatatotalRecords = response.pageDetails.totalRecords;
            this.dataSource.data = this.staffData
            // this.staffDataList.forEach((element, i) => {
            //   element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
            //   this.data.push(element.id);
            // });
        });
    }

    searchStaffByName() {
        this.newFirst = 0;

        var searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: this.searchDeatil.trim(),
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                },
                {
                    filterColumn: "status",
                    filterValue: "Active",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentStaffListdata,
            pageSize: this.parentStaffListdataitemsPerPageForStaff
        };
        this.staffService.staffSearch(searchData).subscribe(
            (response: any) => {
                if (response.responseCode === 404 || response.responseCode === 204) {
                    this.parentstaffListdatatotalRecords = 0;
                    this.staffData = [];
                    // this.dataSource.data = [];
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.staffData = response.dataList;
                    // this.dataSource.data = response.dataList || [];
                    this.parentstaffListdatatotalRecords = response.totalRecords;
                    this.dataSource.data = this.staffData
                }
            },
            (error: any) => {
                this.dataSource.data = [];
                this.parentstaffListdatatotalRecords = 0;
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    paginateStaff(event: PageEvent) {
        this.currentPageParentStaffListdata = event.pageIndex + 1;
        // this.newFirst = event.first;
        this.parentStaffListdataitemsPerPageForStaff = event.pageSize;
        if (this.searchDeatil) {
            this.searchStaffByName();
        } else {
            this.getStaffDetailById();
        }
    }

    clearSearchForm() {
        this.searchDeatil = "";

        this.currentPageParentStaffListdata = 1;
        this.getStaffDetailById();
    }
    saveButtonDisabled = false;

    saveSelstaff() {
        if (!this.selectedStaff) {
            this.toastr.warning("No staff selected");
            return;
        }

        this.selectedStaffChange.emit(this.selectedStaff);
        this.modalCloseStaff();
        this.dialogRef.close(this.selectedStaff);
    }


    // saveSelstaff() {
    //     console.log("Save button clicked");
    //     console.log("selectedStaff:", this.selectedStaff);
    //     if (this.selectedStaff) {
    //         this.selectedStaffChange.emit(this.selectedStaff);
    //         this.dialogRef.close(this.selectedStaff);
    //     } else {
    //         this.toastr.warning("No staff selected");
    //     }

    // console.log("Save button clicked.");
    // this.selectedStaffChange.emit(this.selectedStaff);
    //  this.dialogRef.close(this.selectedStaff);
    // console.log("selectedStaff:", this.selectedStaff)
    // this.selectedStaffChange.emit(this.selectedStaff);
    // this.dialogRef.close(this.selectedStaff);
    // this.staffCustList = [
    //   {
    //     id: Number(this.selectedStaffCust.id),
    //     name: this.selectedStaffCust.firstname,
    //   },
    // ];
    // this.modalCloseStaff();
    // this.modalCloseStaff();

    // this.dialogRef.close(this.selectedStaff);

    // }

    modalCloseStaff() {
        this.displayDTVHistory = false;
        // this.closeStaff.emit();
        this.currentPageParentStaffListdata = 1;
        this.newFirst = 1;
        this.searchDeatil = "";
        this.staffData = [];
    }
    closeDialog() {
        this.modalCloseStaff();
        this.dialogRef.close(false);
    }

}
