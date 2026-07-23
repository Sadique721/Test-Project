import { Component, OnInit, Input, Output, EventEmitter, Inject, ViewChild, TemplateRef } from "@angular/core";
import { Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ToastrService } from 'ngx-toastr';

import {
    COUNTRY,
    CITY,
    STATE,
    PINCODE,
    AREA,
    SUBAREA,
    BUILDING
} from "src/app/RadiusUtils/RadiusConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { PartnerService } from "src/app/service/partner.service";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { CustomerService } from "src/app/service/customer.service";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from '@angular/material/paginator';

export interface fetchData {
    custId: any,
}

@Component({
    selector: "app-customer-details",
    templateUrl: "./customer-details.component.html",
    styleUrls: ["./customer-details.component.css"],
    standalone: false
})
export class CustomerDetailsComponent implements OnInit {
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    subareaTitle = SUBAREA;
    buildingTitle = BUILDING;
    KraTitle = RadiusConstants.KRA_PIN;
    dialogId: boolean = false;
    @Output() selectedStaffChange = new EventEmitter();
    @Output() closeSelectStaff = new EventEmitter();
    @Input() custId: Observable<any>;
    // @ViewChild("customerDetails") customerDetails: TemplateRef<any>;
    customerDetailsdialogRef!: MatDialogRef<any>;
    customerId: any;
    partnerDATA: any = [];
    presentAdressDATA: any = [];
    permentAdressDATA: any = [];
    paymentAdressDATA: any = [];
    chargeDATA = [];
    dataPlan = [];
    postpaidplanData: any;
    customerDetailData: any = {
        title: "",
        firstname: "",
        contactperson: "",
        gst: "",
        pan: "",
        aadhar: "",
        cafno: "",
        acctno: "",
        username: "",
        mobile: "",
        phone: "",
        email: "",
        serviceareaid: "",
        servicetype: "",
        custtype: "",
        didno: "",
        voicesrvtype: "",
        partnerid: "",
        salesremark: "",
        birthDate: "",
        paymentDetails: {
            amount: "",
            referenceno: "",
            paymode: "",
            paymentdate: ""
        },
        addressList: [
            {
                fullAddress: "",
                pincodeId: "",
                areaId: "",
                cityId: "",
                stateId: "",
                countryId: ""
            }
        ]
    };
    paymentAddressData: any = [
        {
            fullAddress: "",
            pincodeId: "",
            areaId: "",
            cityId: "",
            stateId: "",
            countryId: "",
            landmark: ""
        }
    ];
    permanentAddressData: any = [
        {
            fullAddress: "",
            pincodeId: "",
            areaId: "",
            cityId: "",
            stateId: "",
            countryId: "",
            landmark: ""
        }
    ];
    custChargeDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custChargeDeatiltotalRecords: String;
    currentPagecustChargeDeatilList = 0;

    custPlanDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custPlanDeatiltotalRecords: String;
    currentPagecustPlanDeatilList = 0;

    custMacAddItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    custMacAddtotalRecords: String;
    currentPagecustMacAddList = 0;
    serviceAreaDATA: any;
    paymentDataamount: any;
    paymentDatareferenceno: any;
    paymentDatapaymentdate: any;
    paymentDatapaymentMode: any;

    ifIndividualPlan = false;
    ifPlanGroup = false;
    planGroupName: any = "";
    planMappingList: any = [];
    FinalAmountList: any = [];

    customerBill: "";
    custInvoiceToOrg: boolean;
    prepaidCustType: any = RadiusConstants.CUSTOMER_TYPE.PREPAID;
    postpasidCustType: any = RadiusConstants.CUSTOMER_TYPE.POSTPAID;
    selectedStaffCust: any;
    independentAAA: boolean = RadiusConstants.INDPENDENT_AAA === "false" ? false : true;
    currentPageSlab: number;
    constructor(
        private dialog: MatDialog,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        private partnerService: PartnerService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public customerService: CustomerService,
        public dialogRef: MatDialogRef<CustomerDetailsComponent>,
        @Inject(MAT_DIALOG_DATA) public data: fetchData
    ) { }

    ngOnInit(): void {
        this.dialogId = true;
        if (this.data) {

            this.custId = this.data.custId;

            this.getCustomersDetail(this.custId);
        }

        // this.custId.subscribe(value => {
        //     console.log("value :::: ", value.custId);
        //     if (value.custId) {
        //         this.getCustomersDetail(value.custId);
        //     }
        // });
    }

    // ngAfterViewInit() {
    //     this.customerDetailsdialogRef = this.dialog.open(this.customerDetails, {
    //         width: '50%',
    //         maxWidth: '90vw',
    //         height: 'auto',
    //         autoFocus: false,
    //         disableClose: true // same as data-backdrop="static" data-keyboard="false"
    //     });
    // }
    closeDialogId() {
        this.dialogRef.close(true);
        this.closeSelectStaff.emit(this.selectedStaffCust);
        this.dialogId = false;
    }

    ngOnChanges() {
        // console.log("custIdchanges", this.custId);
    }

    getCustomersDetail(custId) {
        this.presentAdressDATA = [];
        this.permentAdressDATA = [];
        this.paymentAdressDATA = [];
        this.partnerDATA = [];
        this.chargeDATA = [];
        let plandatalength = 0;
        let chargeLength = 0;
        this.paymentDataamount = "";
        this.paymentDatareferenceno = "";
        this.paymentDatapaymentdate = "";
        this.paymentDatapaymentMode = "";

        const url = "/customers/" + custId;
        if (this.independentAAA) {
            this.customerService.getCustomerById(custId).subscribe(
                (response: any) => {
                    this.customerDetailData = response?.customer;

                    if (response?.customer?.creditDocuments?.length > 0) {
                        this.paymentDataamount = response.customer?.creditDocuments[0]?.amount;
                        this.paymentDatareferenceno = response.customer?.creditDocuments[0]?.referenceno;
                        this.paymentDatapaymentdate = response.customer?.creditDocuments[0]?.paymentdate;
                        this.paymentDatapaymentMode = response.customer?.creditDocuments[0]?.paymode;
                    }

                    const paymentaddressType = response.customer?.addressList?.filter(
                        key => key.addressType === "Payment"
                    );
                    if (paymentaddressType) {
                        this.paymentAddressData = paymentaddressType;
                    } else {
                        this.paymentAddressData = {
                            fullAddress: ""
                        };
                    }
                    const permanentaddressType = response.customer?.addressList?.filter(
                        key => key.addressType === "Permanent"
                    );
                    if (permanentaddressType) {
                        this.permanentAddressData = permanentaddressType;
                    } else {
                        this.permanentAddressData = {
                            fullAddress: ""
                        };
                    }

                    //partner Name
                    if (this.customerDetailData.partnerid) {
                        let partnerurl = "/partner/" + this.customerDetailData.partnerid;
                        this.partnerService.getMethodNew(partnerurl).subscribe((response: any) => {
                            this.partnerDATA = response.partnerlist.name;

                            //  console.log("partnerDATA", this.partnerDATA);
                        });
                    }

                    //serviceArea Name
                    if (this.customerDetailData.serviceareaid) {
                        let serviceareaurl = "/serviceArea/" + this.customerDetailData.serviceareaid;
                        this.savbillCommonBaseService.get(serviceareaurl).subscribe((response: any) => {
                            this.serviceAreaDATA = response.data.name;

                            // console.log("partnerDATA", this.serviceAreaDATA);
                        });
                    }

                    //Address
                    if (
                        this.customerDetailData?.addressList?.length > 0 &&
                        this.customerDetailData?.addressList !== undefined
                    ) {
                        if (this.customerDetailData.addressList[0].addressType) {
                            let areaurl = "/area/" + this.customerDetailData.addressList[0].areaId;

                            this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                this.presentAdressDATA = response.data;

                                // console.log("presentAdressDATA", this.presentAdressDATA);
                            });
                        }
                    }
                    if (
                        this.customerDetailData?.addressList?.length > 1 &&
                        this.customerDetailData?.addressList !== undefined
                    ) {
                        var j = 0;
                        while (j < this.customerDetailData.addressList.length) {
                            const addres1 = this.customerDetailData.addressList[j].addressType;
                            if (addres1) {
                                if ("Payment" == addres1) {
                                    let areaurl = "/area/" + this.customerDetailData.addressList[j].areaId;
                                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                        this.paymentAdressDATA = response.data;

                                        // console.log("paymentAdressDATA", this.paymentAdressDATA);
                                    });
                                } else {
                                    let areaurl = "/area/" + this.customerDetailData.addressList[j].areaId;
                                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                        this.permentAdressDATA = response.data;

                                        // console.log("permentAdressDATA", this.permentAdressDATA);
                                    });
                                }
                            }
                            j++;
                        }
                    }

                    // //plan deatils
                    // while (plandatalength < this.customerDetailData.planMappingList.length) {
                    //   let planurl = "/postpaidplan/" + this.customerDetailData.planMappingList[plandatalength].planId;
                    //   this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                    //     this.dataPlan.push(response.postPaidPlan.name);
                    //     // console.log("dataPlan", this.dataPlan);
                    //   })
                    //   plandatalength++;
                    // }
                    if (
                        this.customerDetailData?.planMappingList?.length > 0 &&
                        this.customerDetailData?.planMappingList !== undefined
                    ) {
                        this.customerBill = this.customerDetailData.planMappingList[0].billTo;
                        this.custInvoiceToOrg = this.customerDetailData.planMappingList[0].isInvoiceToOrg;
                    }

                    if (this.customerDetailData.plangroupid) {
                        this.ifIndividualPlan = false;
                        this.ifPlanGroup = true;
                        let planGroupurl =
                            "/findPlanGroupById?planGroupId=" + this.customerDetailData.plangroupid;

                        this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                            this.planGroupName = response.planGroup.planGroupName;
                        });
                    } else {
                        this.ifIndividualPlan = true;
                        this.ifPlanGroup = false;
                        //plan deatils
                        this.planMappingList = this.customerDetailData.planMappingList;
                        while (plandatalength < this.customerDetailData.planMappingList.length) {
                            let planId = this.customerDetailData.planMappingList[plandatalength].planId;
                            let discount;
                            if (
                                this.customerDetailData.planMappingList[plandatalength].discount == null ||
                                this.customerDetailData.planMappingList[plandatalength].discount == ""
                            ) {
                                discount = 0;
                            } else {
                                discount = this.customerDetailData.planMappingList[plandatalength].discount;
                            }

                            let planurl = "/postpaidplan/" + planId;
                            this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                                this.dataPlan.push(response.postPaidPlan);
                                // console.log("dataPlan", this.dataPlan);
                            });

                            this.customerManagementService
                                .getofferPriceWithTax(planId, discount)
                                .subscribe((response: any) => {
                                    if (response.result.finalAmount) {
                                        this.FinalAmountList.push(response.result.finalAmount);
                                    } else {
                                        this.FinalAmountList.push(0);
                                    }
                                });
                            plandatalength++;
                        }
                    }
                    //charger Data
                    // while (chargeLength < this.customerDetailData.overChargeList.length) {
                    //   let chargeurl = "/charge/" + this.customerDetailData.overChargeList[chargeLength].chargetype;
                    //   this.customerManagementService.getMethod(chargeurl).subscribe((response: any) => {
                    //     this.chargeDATA.push(response.chargebyid.name);
                    //     // console.log("chargeDATA", this.chargeDATA);
                    //   })
                    //   chargeLength++;
                    // }

                    // console.log("this.paymentAddressData", this.paymentAddressData);
                    // console.log("this.permanentAddressData", this.permanentAddressData);
                    // console.log("this.customerDetailData", this.customerDetailData);
                },
                (error: any) => {
                    console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        } else {
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.customerDetailData = response.customers;

                    if (response?.customer?.creditDocuments?.length > 0) {
                        this.paymentDataamount = response.customers.creditDocuments[0].amount;
                        this.paymentDatareferenceno = response.customers.creditDocuments[0].referenceno;
                        this.paymentDatapaymentdate = response.customers.creditDocuments[0].paymentdate;
                        this.paymentDatapaymentMode = response.customers.creditDocuments[0].paymode;
                    }

                    const paymentaddressType = response.customers.addressList.filter(
                        key => key.addressType === "Payment"
                    );
                    if (paymentaddressType) {
                        this.paymentAddressData = paymentaddressType;
                    } else {
                        this.paymentAddressData = {
                            fullAddress: ""
                        };
                    }
                    const permanentaddressType = response.customers.addressList.filter(
                        key => key.addressType === "Permanent"
                    );
                    if (permanentaddressType) {
                        this.permanentAddressData = permanentaddressType;
                    } else {
                        this.permanentAddressData = {
                            fullAddress: ""
                        };
                    }

                    //partner Name
                    if (this.customerDetailData.partnerid) {
                        let partnerurl = "/partner/" + this.customerDetailData.partnerid;
                        this.partnerService.getMethodNew(partnerurl).subscribe((response: any) => {
                            this.partnerDATA = response.partnerlist.name;

                            //  console.log("partnerDATA", this.partnerDATA);
                        });
                    }

                    //serviceArea Name
                    if (this.customerDetailData.serviceareaid) {
                        let serviceareaurl = "/serviceArea/" + this.customerDetailData.serviceareaid;
                        this.savbillCommonBaseService.get(serviceareaurl).subscribe((response: any) => {
                            this.serviceAreaDATA = response.data.name;

                            // console.log("partnerDATA", this.serviceAreaDATA);
                        });
                    }

                    //Address
                    if (this.customerDetailData.addressList.length > 0) {
                        if (this.customerDetailData.addressList[0].addressType) {
                            let areaurl = "/area/" + this.customerDetailData.addressList[0].areaId;

                            this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                this.presentAdressDATA = response.data;

                                // console.log("presentAdressDATA", this.presentAdressDATA);
                            });
                        }
                    }
                    if (this.customerDetailData.addressList.length > 1) {
                        var j = 0;
                        while (j < this.customerDetailData.addressList.length) {
                            const addres1 = this.customerDetailData.addressList[j].addressType;
                            if (addres1) {
                                if ("Payment" == addres1) {
                                    let areaurl = "/area/" + this.customerDetailData.addressList[j].areaId;
                                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                        this.paymentAdressDATA = response.data;

                                        // console.log("paymentAdressDATA", this.paymentAdressDATA);
                                    });
                                } else {
                                    let areaurl = "/area/" + this.customerDetailData.addressList[j].areaId;
                                    this.savbillCommonBaseService.get(areaurl).subscribe((response: any) => {
                                        this.permentAdressDATA = response.data;

                                        // console.log("permentAdressDATA", this.permentAdressDATA);
                                    });
                                }
                            }
                            j++;
                        }
                    }

                    // //plan deatils
                    // while (plandatalength < this.customerDetailData.planMappingList.length) {
                    //   let planurl = "/postpaidplan/" + this.customerDetailData.planMappingList[plandatalength].planId;
                    //   this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                    //     this.dataPlan.push(response.postPaidPlan.name);
                    //     // console.log("dataPlan", this.dataPlan);
                    //   })
                    //   plandatalength++;
                    // }
                    if (this.customerDetailData.planMappingList.length > 0) {
                        this.customerBill = this.customerDetailData.planMappingList[0].billTo;
                        this.custInvoiceToOrg = this.customerDetailData.planMappingList[0].isInvoiceToOrg;
                    }

                    if (this.customerDetailData.plangroupid) {
                        this.ifIndividualPlan = false;
                        this.ifPlanGroup = true;
                        let planGroupurl =
                            "/findPlanGroupById?planGroupId=" + this.customerDetailData.plangroupid;

                        this.customerManagementService.getMethod(planGroupurl).subscribe((response: any) => {
                            this.planGroupName = response.planGroup.planGroupName;
                        });
                    } else {
                        this.ifIndividualPlan = true;
                        this.ifPlanGroup = false;
                        //plan deatils
                        this.planMappingList = this.customerDetailData.planMappingList;
                        while (plandatalength < this.customerDetailData.planMappingList.length) {
                            let planId = this.customerDetailData.planMappingList[plandatalength].planId;
                            let discount;
                            if (
                                this.customerDetailData.planMappingList[plandatalength].discount == null ||
                                this.customerDetailData.planMappingList[plandatalength].discount == ""
                            ) {
                                discount = 0;
                            } else {
                                discount = this.customerDetailData.planMappingList[plandatalength].discount;
                            }

                            let planurl = "/postpaidplan/" + planId;
                            this.customerManagementService.getMethod(planurl).subscribe((response: any) => {
                                this.dataPlan.push(response.postPaidPlan);
                                // console.log("dataPlan", this.dataPlan);
                            });

                            this.customerManagementService
                                .getofferPriceWithTax(planId, discount)
                                .subscribe((response: any) => {
                                    if (response.result.finalAmount) {
                                        this.FinalAmountList.push(response.result.finalAmount);
                                    } else {
                                        this.FinalAmountList.push(0);
                                    }
                                });
                            plandatalength++;
                        }
                    }
                    //charger Data
                    // while (chargeLength < this.customerDetailData.overChargeList.length) {
                    //   let chargeurl = "/charge/" + this.customerDetailData.overChargeList[chargeLength].chargetype;
                    //   this.customerManagementService.getMethod(chargeurl).subscribe((response: any) => {
                    //     this.chargeDATA.push(response.chargebyid.name);
                    //     // console.log("chargeDATA", this.chargeDATA);
                    //   })
                    //   chargeLength++;
                    // }

                    // console.log("this.paymentAddressData", this.paymentAddressData);
                    // console.log("this.permanentAddressData", this.permanentAddressData);
                    // console.log("this.customerDetailData", this.customerDetailData);
                },
                (error: any) => {
                    console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }
    pageChangedcustChargeDetailList(pageNumber) {
        this.currentPagecustChargeDeatilList = pageNumber;
    }

    pageChangedcustPlanDetailList(pageNumber) {
        this.currentPagecustPlanDeatilList = pageNumber;
    }

    pageChangedCountryList(event: PageEvent) {
        this.currentPageSlab = event.pageIndex + 1;
        this.custPlanDeatilItemPerPage = event.pageSize;
    }

    pageChangedcustMacAddDetailList(pageNumber) {
        this.currentPagecustMacAddList = pageNumber;
    }
    planDisplay = ['service', 'plan', 'validity', 'discount', 'finalPrice']
    chargeDisplay = ['chargetype', 'validity', 'price', 'actualprice', 'charge_date']
    macDisply = ['macAddress']
}
