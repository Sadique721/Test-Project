import { url } from "inspector";
import { Component, OnInit } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService } from "primeng/api";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { StaffService } from "src/app/service/staff.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { AreaManagementService } from "src/app/service/area-management.service";
import { ToastrService } from 'ngx-toastr';
import moment from "moment";


@Component({
    selector: "app-cust-details",
    templateUrl: "./customer-details.component.html",
    styleUrls: ["./customer-details.component.scss"],
    standalone: false
})
export class CustomerDetailsComponent implements OnInit {
    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));

    customerLedgerDetailData: any;
    customerNetworkLocationDetailData: any;
    customerId: number;
    customerBill: "";
    serviceAreaDATA: any;
    presentAdressDATA: any = [];
    customerPopName: any = "";
    customerAddress: any;
    macList: string = "";
    locationList: string = "";
    isParentLocation: string = "NO";
    customerInventoryList: any;
    activePlanList: any;
    paymentHistoryList: any;
    staffUserData: any;
    demographicLabel: any;
    walletValue: number;
    dueValue: number;
    subAreaListDD: any[];
    buildingListDD: any[];
    buildingNoDD: any[];
    presentSubAreaAdressDATA: any = [];
    framedIpAddress: any;
    KraTitle = RadiusConstants.KRA_PIN;

    _AAApasswordType = "password";
    AAAshowPassword = false;
    loginUserName: any;
    _CWSCpasswordType = "password";
    CWSCshowPassword = false;
    aclPreCustCode: any;

    constructor(
        private spinner: NgxSpinnerService,
        private messageService: MessageService,
        private customerManagementService: CustomermanagementService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private route: ActivatedRoute,
        public statusCheckService: StatusCheckService,
        private router: Router,
        private toastr: ToastrService,
        private staffService: StaffService,
        public revenueManagementService: RevenueManagementService,
        public areaManagementService: AreaManagementService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.getaclEntries();
    }

    async ngOnInit() {
        this.loginUserName = localStorage.getItem("loginUserName");
        this.demographicLabel = RadiusConstants.DEMOGRAPHICDATA || [];
        this.getWalletData(this.customerId);
        // this.getCustomersDetail(this.customerId);
        this.getCustomerNetworkLocationDetail(this.customerId);
        this.getAllCustomerInventoryList(this.customerId);
        this.getActivePlanList(this.customerId);
        this.getPaymentHistory(this.customerId);
        this.getFramedIpAddressIp();
    }

    serviceAreaAndBuildingNameFromCustomerId() {
        const url = "/BuildingAndSubareaNames/" + this.customerId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.presentAdressDATA.subarea = response?.data?.name;
                this.presentAdressDATA.buildingName = response?.data?.building_name;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getFramedIpAddressIp() {
        const url = "/liveUser/getFramedIpAddress/" + this.customerId;
        this.customerManagementService.savbillRadius(url).subscribe(
            (response: any) => {
                this.framedIpAddress = response.data;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getDemographicLabel(currentName: string): string {
        if (!this.demographicLabel || this.demographicLabel.length === 0) {
            return currentName;
        }

        const label = this.demographicLabel.find(item => item.currentName === currentName);
        return label ? label.newName : currentName;
    }

    listCustomer() {
        this._CWSCpasswordType = "password";
        this._AAApasswordType = "password";
        this.AAAshowPassword = false;
        this.CWSCshowPassword = false;
        this.router.navigate(["/home/customer/list/" + this.custType]);
    }

    getCustomersDetail(custId) {
        this.getaclEntries();
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerLedgerDetailData = response.customers;
                this.customerAddress = this.customerLedgerDetailData.addressList.find(
                    address => address.version.toLowerCase() === "new"
                );
                if (this.customerLedgerDetailData?.birthDate) {
                    this.customerLedgerDetailData.birthDate = moment(this.customerLedgerDetailData.birthDate).format("YYYY-MM-DD");
                }

                if (this.paymentHistoryList && this.paymentHistoryList.length > 0) {
                    this.paymentHistoryList.forEach(payment => {
                        if (payment.paymentDate) {
                            payment.paymentDate = this.convertStringToDate(payment.paymentDate);
                        }
                    });
                }

                var macArray = [];
                this.customerLedgerDetailData.customerLocations.forEach(element => {
                    if (macArray.indexOf(element.mac) === -1) {
                        macArray.push(element.mac);
                    }
                });
                this.macList = macArray.join(", ");

                var locationArray = [];
                this.customerLedgerDetailData.customerLocations.forEach(element => {
                    if (locationArray.indexOf(element.locationName) === -1) {
                        locationArray.push(element.locationName);
                    }
                });
                this.locationList = locationArray.join(", ");

                if (this.customerLedgerDetailData.customerLocations.length > 0) {
                    var custLocation = this.customerLedgerDetailData.customerLocations.some(
                        location => location.isParentLocation == true
                    );

                    this.isParentLocation = custLocation ? "YES" : "NO";
                }

                // //pop Name
                // if (this.customerLedgerDetailData.popid) {
                //   let partnerurl = "/popmanagement/" + this.customerLedgerDetailData.popid;
                //   this.customerManagementService.getMethod(partnerurl).subscribe((response: any) => {
                //     this.customerPopName = response.data.name;
                //   });
                // }

                // serviceArea Name
                if (this.customerLedgerDetailData.serviceareaid) {
                    const serviceareaurl = "/serviceArea/" + this.customerLedgerDetailData.serviceareaid;
                    this.savbillCommonBaseService.get(serviceareaurl).subscribe((response: any) => {
                        this.serviceAreaDATA = response.data.name;
                    });
                }

                // Address
                if (this.customerLedgerDetailData.addressList[0].addressType) {
                    const areaurl = "/area/" + this.customerLedgerDetailData.addressList[0].areaId;

                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                        this.presentAdressDATA = response.data;
                        // let findsubData = this.subAreaListDD?.find(x => x.id == this.customerLedgerDetailData.addressList[0]?.subareaId)
                        // this.presentAdressDATA.subarea = findsubData?.name;
                        // let findBuildData = this.buildingListDD?.find(x => x.buildingMgmtId == this.customerLedgerDetailData.addressList[0]?.building_mgmt_id)
                        // this.presentAdressDATA.buildingName = findBuildData?.buildingName;
                        this.presentAdressDATA.buildingNumber =
                            this.customerLedgerDetailData.addressList[0]?.buildingNumber;
                        this.serviceAreaAndBuildingNameFromCustomerId();
                    });
                }
                if (this.customerLedgerDetailData.planMappingList.length > 0) {
                    this.customerBill = this.customerLedgerDetailData.planMappingList[0].billTo;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getWalletData(custID) {
        const data = {
            CREATE_DATE: "",
            END_DATE: "",
            amount: "",
            balAmount: "",
            custId: custID,
            description: "",
            id: "",
            refNo: "",
            transcategory: "",
            transtype: ""
        };
        const url = "/wallet";
        this.revenueManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.walletValue = response.customerWalletDetails;
                this.getCustomersDetail(this.customerId);
                if (this.walletValue >= 0) {
                    this.dueValue = 0;
                } else {
                    this.dueValue = Math.abs(this.walletValue);
                }
            },
            (error: any) => {
                this.getCustomersDetail(this.customerId);
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getCustomerNetworkLocationDetail(custId) {
        if (this.statusCheckService.isActiveInventoryService) {
            const url = `/customer/getCustNetworkDetail?customerId=${custId}`;
            this.customerManagementService.getCustNetworkLocDetail(url).subscribe(
                (response: any) => {
                    this.customerNetworkLocationDetailData = response.data;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            );
        }
    }
    getAllCustomerInventoryList(custId) {
        const url = `/inwards/getAllCustomerInventoryList?custId=${custId}`;
        this.customerManagementService.getCustNetworkLocDetail(url).subscribe(
            (response: any) => {
                this.customerInventoryList = response.dataList;
                const staffId = this.customerInventoryList[0]?.staffId;
                if (staffId) {
                    this.staffService.getStaffUserData(staffId).subscribe((response: any) => {
                        this.staffUserData = response.Staff;
                    });
                }
            },
            (error: any) => { }
        );
    }
    getActivePlanList(custId) {
        const url = `/subscriber/getActivePlanList/${custId}?isNotChangePlan=true`;
        this.customerManagementService.getActivePlanList(url).subscribe(
            (response: any) => {
                this.activePlanList = response.dataList;
            },
            (error: any) => { }
        );
    }

    getPaymentHistory(custId) {
        const url = `/paymentHistory/${custId}`;
        this.customerManagementService.getPaymentHistory(url).subscribe(
            (response: any) => {
                this.paymentHistoryList = response.dataList;
            },
            (error: any) => { }
        );
    }

    convertStringToDate(dateString: string): Date | null {
        if (!dateString) return null;
        const parts = dateString.split('-');
        if (parts.length !== 3) return null;
        const day = +parts[0];
        const month = +parts[1] - 1; // month index starts at 0
        const year = +parts[2];
        return new Date(year, month, day);
    }

    roundAmount(amount: number): number {
        return Math.round(amount);
    }

    getaclEntries() {
        const aclvalue: any[] = JSON.parse(localStorage.getItem("aclEntries") || "[]");
        if (this.custType == "Prepaid") {
            var aclPreCust = aclvalue.find(item => item.code === "pre_cust_view_password");
        } else {
            if (this.custType == "Postpaid") {
                var aclPreCust = aclvalue.find(item => item.code === "post_cust_view_password");
            }
        }

        this.aclPreCustCode =
            aclPreCust?.code === "pre_cust_view_password" ||
                aclPreCust?.code === "post_cust_view_password"
                ? aclPreCust.code
                : null;
    }
}
