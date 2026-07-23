import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { DatePipe } from "@angular/common";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { LeadManagementService } from "src/app/service/lead-management-service";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-customer-verify-list",
    templateUrl: "./customer-verify-list.component.html",
    styleUrls: ["./customer-verify-list.component.css"],
    standalone: false
})
export class CustomerVerifyListComponent implements OnInit {
    mobileNo: string = "";
    data: any[] = [];
    searchData: any;
    custType: string = "";
    constructor(
        private toastr: ToastrService,

        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        public datePipe: DatePipe,
        public customerManagementService: CustomermanagementService,
        private leadManagementService: LeadManagementService
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.mobileNo = params['mobilenumber'] || "";
        });

        // this.custType = this.route.snapshot.paramMap.get("custType")!;
        this.custType = "Prepaid";
        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: this.mobileNo,
                    filterColumn: "mobile",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: 1,
            pageSize: 20,
            status: RadiusConstants.CUSTOMER_STATUS.ACTIVE,
            fromDate: null,
            toDate: null,
        };
        this.searchCustomer();
    }

    searchCustomer() {
        const url = "/customers/search/" + this.custType;
        this.customerManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.customerList && response.customerList.length > 0) {
                    this.data = response.customerList;
                    this.router.navigate(['/home/customer/list/Prepaid'], { queryParams: { mobilenumber: this.mobileNo } });
                } else {
                    this.searchCustomerCaf();
                }
            },
            (error: any) => {
                this.searchCustomerCaf();
                console.log(error, "customer");
                if (error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    searchCustomerCaf() {
        this.searchData.status = RadiusConstants.CUSTOMER_STATUS.NEW_ACTIVATION;
        const cafUrl = "/customers/search/" + this.custType;
        this.customerManagementService.postMethod(cafUrl, this.searchData).subscribe(
            (response: any) => {
                if (response.customerList && response.customerList.length > 0) {
                    this.data = response.customerList;
                    this.router.navigate(['/home/customer-caf/list/Prepaid'], { queryParams: { mobilenumber: this.mobileNo } });
                } else {
                    this.searchLead();
                }
            },
            (error: any) => {
                this.searchLead();
                console.log(error, "customer-caf");
                if (error.status == 404) {

                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    searchLead() {
        this.searchData = {
            filterBy: "",
            filters: [
                {
                    filterDataType: "",
                    filterValue: this.mobileNo,
                    filterColumn: "mobile",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: 1,
            pageSize: 20,
            sortOrder: ""
        };
        const leadUrl = "/leadMaster/search";
        // this.customerManagementService.postMethod(leadUrl, this.searchData).subscribe(
        this.leadManagementService
            .postMethod(leadUrl, this.searchData, null, null)
            .subscribe(
                (response: any) => {
                    if (response.leadMasterList && response.leadMasterList.length > 0) {
                        this.data = response.leadMasterList;
                        this.router.navigate(['/home/lead-management'], { queryParams: { mobilenumber: this.mobileNo } });
                    } else {
                        this.router.navigate(['/home/lead-management'], { queryParams: { mobilenumber: this.mobileNo } });
                    }
                },
                (error: any) => {
                    console.log(error, "leadMaster");
                    this.router.navigate(['/home/lead-management'], { queryParams: { mobilenumber: this.mobileNo } });
                    // if (error.error.status == 404) {
                    //     this.messageService.add({
                    //         severity: "info",
                    //         summary: "Info",
                    //         detail: error.error.msg,
                    //         icon: "far fa-times-circle"
                    //     });
                    // } else {
                    this.toastr.error(`${error.error}`, 'Failed!');


                    // }
                }
            );
    }
}


