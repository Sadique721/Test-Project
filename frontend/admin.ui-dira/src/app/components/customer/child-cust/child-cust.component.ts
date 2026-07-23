import { Component, EventEmitter, Input, OnInit, Output, ViewChild, AfterViewInit } from "@angular/core";
import { DatePipe, formatDate } from "@angular/common";
import { UntypedFormBuilder, FormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { ToastrService } from 'ngx-toastr';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";

declare var $: any;
@Component({
    selector: "app-child-cust",
    templateUrl: "./child-cust.component.html",
    styleUrls: ["./child-cust.component.css"],
    standalone: false
})
export class ChildCustComponent implements OnInit {
    displayedColumns: string[] = ['select', 'name', 'username', 'serviceArea', 'mobileNumber', 'accountNo'];
    childCustomerDataList = new MatTableDataSource<any>([]);
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    custData: any = {};
    customerId = 0;
    custType: string = "";
    checkedList: any = [];
    pageNumberForChildsPage = 1;
    pageSizeForChildsPage = RadiusConstants.ITEMS_PER_PAGE;
    childCustomerDataTotalRecords: number = 0;
    masterSelected: boolean;
    removeAccess: boolean = false;
    makeParentAccess: boolean = false;
    checklist: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    constructor(
        private toastr: ToastrService,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        public datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        private route: ActivatedRoute,
        private router: Router,
        loginService: LoginService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.removeAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CHILD_CUSTS_REMOVE
                : POST_CUST_CONSTANTS.POST_CUST_CHILD_CUST_REMOVE
        );
        this.makeParentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CHILD_CUSTS_MAKE_PARENT
                : POST_CUST_CONSTANTS.POST_CUST_CHILD_CUST_MAKE
        );
    }

    ngAfterViewInit(): void {
        this.childCustomerDataList.paginator = this.paginator;
        this.paginator.page.subscribe((event: PageEvent) => this.pageChangeEventForChildCustomers(event));
    }

    ngOnInit(): void {
        if (history.state.data) {
            this.custData = history.state.data;
            this.getChildCustomers();
        } else this.getCustomersDetail(this.customerId);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            this.getChildCustomers();
        });
    }
    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    getChildCustomers() {
        const url = `/getAllActualChildCustomer?customerId=${this.customerId}`;
        const data = {
            page: this.pageNumberForChildsPage,
            pageSize: this.pageSizeForChildsPage,
        };
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                // this.assignedInventoryList = res.dataList;
                this.childCustomerDataList.data = response.customerList;
                this.childCustomerDataTotalRecords = response.pageDetails.totalRecords;
                this.childCustomerDataList.data.forEach(element => {
                    element.isSelected = false;
                });
                if (this.paginator) {
                    this.paginator.length = this.childCustomerDataTotalRecords;
                    this.childCustomerDataList.paginator = this.paginator;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangeEventForChildCustomers(event: PageEvent): void {
  this.pageNumberForChildsPage = event.pageIndex + 1;
  this.pageSizeForChildsPage = event.pageSize;
  this.getChildCustomers();
}

    itemPerPageChangeEvent(event) {
        this.pageSizeForChildsPage = Number(event.value);
        this.pageNumberForChildsPage = 1;
        this.getChildCustomers();
    }

    // The master checkbox will check/ uncheck all items
    checkUncheckAll() {
        const isSelected = this.masterSelected;
        this.childCustomerDataList.data.forEach(item => (item.isSelected = isSelected));
        this.getCheckedItemList();
    }

    // Check All Checkbox Checked
    isAllSelected() {
        this.masterSelected = this.childCustomerDataList.data.every(function (item: any) {
            return item.isSelected == true;
        });
        this.getCheckedItemList();
    }

    // Get List of Checked Items
    getCheckedItemList() {
        this.checkedList = this.childCustomerDataList.data.filter(item => item.isSelected).map(item => item.id);
    }

    removeFromParent() {
        const url = `/removeParent`;
        // let data = {
        //   page: this.pageNumberForChildsPage,
        //   pageSize: this.pageSizeForChildsPage,
        // };
        this.customerManagementService.postMethod(url, this.checkedList).subscribe(
            (response: any) => {
                // this.assignedInventoryList = res.dataList;
                this.getChildCustomers();
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    makeParent(oldParentId: number) {
        if (this.checkedList.length == 1) {
            const url = `/updateParent?newParentId=${this.checkedList[0]}&oldParentId=${oldParentId}`;
            // let data = {
            //   page: this.pageNumberForChildsPage,
            //   pageSize: this.pageSizeForChildsPage,
            // };
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    // this.assignedInventoryList = res.dataList;
                    // this.getChildCustomers();
                    // this.listCustomer();
                    this.router.navigate(["/home/customer/details/" + this.custType]);
                    this.toastr.success(`${response.message}`, 'Changed parent successfully!');


                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Please select only one customer to make parent!');


            }


        }
    }
}
