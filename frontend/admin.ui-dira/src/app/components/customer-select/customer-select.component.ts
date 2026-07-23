import { Component, Input, Output, OnInit, EventEmitter, ViewChild, Inject } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MatDialog } from "@angular/material/dialog";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";

export interface fetchData {
    custId?: any,
    type?: any,
    selectedParentCust?: any,
    // dialogRef?: any
}

declare var $: any;

@Component({
    selector: "app-customer-select",
    templateUrl: "./customer-select.component.html",
    styleUrls: ["./customer-select.component.css"],
    standalone: false
})
export class CustomerSelectComponent implements OnInit {
    @Input() custId;
    @Input() type;
    @Input() selectedCust: any = [];
    @Output() selectedCustChange = new EventEmitter();
    @Output() closeParentCust = new EventEmitter();
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    dataSource!: MatTableDataSource<any>;
    newFirst = 1;
    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    prepaidParentCustomerList: any;
    parentCustomerListdatatotalRecords: any;
    parentFieldEnable = false;
    searchParentCustValue = "";
    searchParentCustOption = "";
    displayAssignPlanInventoryModal: boolean = false;
    @ViewChild("customerSelect") customerSelect;
    constructor(
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private dialog: MatDialog,
        // public dialogRef: MatDialogRef<CustomerSelectComponent>,
        public dialogRef: MatDialogRef<CustomerSelectComponent>,
        @Inject(MAT_DIALOG_DATA) public data: fetchData
    ) {
        this.type = data.type;
        this.custId = data.custId;
        this.selectedCust = data.selectedParentCust;
    }

    ngOnInit(): void {
        this.displayAssignPlanInventoryModal = true;
        this.selectedCust = [];
        this.getParentCustomerData();
    }

    // ngAfterViewInit() {
    //     this.dialog.open(this.customerSelect);

    // }




    getParentCustomerData() {
        let currentPage;
        // if (pageData) {
        //   currentPage = pageData + 1;
        // } else {
        currentPage = this.currentPageParentCustomerListdata;
        // }

        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const type = this.type || this.data?.type
        const url = `/parentCustomers/list/${type}`;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                const list = this.prepaidParentCustomerList;
                if (this.custId)
                    this.prepaidParentCustomerList = list.filter(cust => cust.id !== this.custId);
                else this.prepaidParentCustomerList = list;

                // this.prepaidParentCustomerList = filterList;
                // console.log("list", filterList);

                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    selParentSearchOption(event) {
        // console.log("value", event.value);
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    searchParentCustomer() {
        const searchParentData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage
        };

        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();

        const url = "/parentCustomers/search/" + this.type;
        // console.log("this.searchData", this.searchData)
        this.customerManagementService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.msg}`, 'Info!');


                    // this.customerListData = [];
                    this.parentCustomerListdatatotalRecords = 0;
                } else {
                    this.prepaidParentCustomerList = response.parentCustomerList;
                    const list = this.prepaidParentCustomerList;
                    const filterList = list.filter(cust => cust.id !== this.custId);
                    this.prepaidParentCustomerList = filterList;
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData();
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    paginate(event: PageEvent) {
        this.currentPageParentCustomerListdata = event.pageIndex + 1;
        this.parentCustomerListdataitemsPerPage = event.pageSize;
        // this.first = event.first;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }

    async saveSelCustomer() {
        // this.selectedCustChange.emit(this.selectedCust);
        // this.modalCloseParentCustomer();
        this.dialogRef.close(this.selectedCust);
    }

    modalCloseParentCustomer() {
        this.closeParentCust.emit(this.selectedCust);
        this.displayAssignPlanInventoryModal = false;
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
        // this.dialog.closeAll();
    }

    onCancel() {
        // this.closeParentCust.emit();
        this.dialogRef.close(false);
    }
}
