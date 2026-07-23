import { ChangeDetectorRef, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { Component } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import * as moment from "moment";
import { RAZORPAY_CONSTANT } from "src/app/constants/paymentGatewayConstant";
import { asyncScheduler, interval, Subscription } from "rxjs";
import * as uuid from "uuid";
import { countries } from "../model/country";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { DatePipe } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { MatDialog } from "@angular/material/dialog";

declare var $: any;
declare var Razorpay: any;
@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
  standalone: true,
  imports: [SharedModule],
  providers: [DatePipe]
})
export class DashboardComponent implements OnInit {
  value: string = "1000";
  customerId: string;
  mvnoId: string;
  customerCurrentPlanListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  CurrentPlanShowItemPerPage = 1;
  pageITEM = RadiusConstants.ITEMS_PER_PAGE;
  custCurrentPlanList: any = [];
  currentPagecustomerCurrentPlanListdata = 0;
  customerCurrentPlanListdatatotalRecords: any;
  pageLimitOptions = RadiusConstants.pageLimitOptions;
  badgeTypeForStatus: any;
  displayStatus: any;
  totalQuota: Number = 0;
  usedQuota: Number = 0;
  remainQuota: Number = 0;
  quotaUnit: String = "";
  lastLogin: any = "";
  lastLoginString: String = "A day ago";
  cdrListData: any;
  freePlanId = RadiusConstants.FREE_PLAN;
  selectedAmount: any;
  selRenewAmount: any;
  orderId: any;
  displayPaymentDialog: boolean = false;
  paymentForm: FormGroup;
  submitted: boolean = false;
  savedConfig: any;
  isPaymentGatewayConfigured: boolean = false;
  paymentkeyValuePairs: { [key: string]: any } = {};
  paymentGateway: any;
  paymentConfirmationModal: boolean = false;
  buyplan: any;
  displayDialog: boolean;
  custDetails: any = null;
  isLoading: boolean = true;
  presentFullAddress: any;
  presentAdressDATA: any;
  razorpayCallbackResponse: any;
  razopayredirectTimeInSeconds: any;
  exitBuy: boolean = true;
  paymentstatusCount = RadiusConstants.TIMER_COUNT;
  custServiceData = [];
  serviceSerialNumbers = [];
  isShowConnection = true;
  subscription2: Subscription;
  obs1$ = interval(1000);
  transactionStatus: boolean = false;
  paymentSucessModel: boolean = false;
  getWallatData: any;
  WalletAmount: any;
  mpinModal: boolean = false;
  mpinForm: FormGroup;
  isMpinFormSubmitted: boolean = false;
  mobileError: boolean = false;
  inputMobile: string = "";
  countries: any = countries;
  payMethod: any;
  paymentAmount: void;
  currency: string;
  currencySymbol = localStorage.getItem("CURRENCY_SYMBOL");

  constructor(
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    // private messageService: MessageService,
    private customerdetailsilsService: CustomerdetailsilsService,
    public customerManagementService: CustomermanagementService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
    public commondropdownService: CommondropdownService
  ) {
    this.customerId = this.commondropdownService.getUserId();
    this.mvnoId = this.commondropdownService.getMvnoId();
    this.customerManagementService
      .getConfigurationByName("CURRENCY_FOR_PAYMENT")
      .subscribe((res: any) => {
        
        this.currency = res.data.value;
      });
    this.getcustCurrentPlan(this.customerId, "");
    this.getCustomersDetail(this.customerId);
    this.getserviceData();
    this.getUsageData();
    this.addWalletIncustomer();
    this.checkPaymentGatewayConfiguration();
    this.commondropdownService.getsystemconfigList();
    this.paymentForm = this.fb.group({
      amount: ["", [Validators.required, Validators.min(1)]]
    });
    this.mpinForm = this.fb.group({
      countryCode: [""],
      mobileNumber: ["", [Validators.required, Validators.maxLength(10)]]
    });
  }

  ngOnInit(): void {}

  addWalletIncustomer() {
    const data = {
      CREATE_DATE: "",
      END_DATE: "",
      amount: "",
      balAmount: "",
      custId: this.customerId,
      description: "",
      id: "",
      refNo: "",
      transcategory: "",
      transtype: ""
    };
    const url = "/wallet";
    this.customerManagementService.postMethodForWallet(url, data).subscribe((response: any) => {
      this.getWallatData = response;
      this.WalletAmount = response.customerWalletDetails;
    });
  }

  getcustCurrentPlan(custId, size) {
    let page_list;
    if (size) {
      page_list = size;
      this.customerCurrentPlanListdataitemsPerPage = size;
    } else {
      if (this.CurrentPlanShowItemPerPage == 1) {
        this.customerCurrentPlanListdataitemsPerPage = this.pageITEM;
      } else {
        this.customerCurrentPlanListdataitemsPerPage = this.CurrentPlanShowItemPerPage;
      }
    }
    this.spinner.show();
    this.totalQuota = 0;
    this.usedQuota = 0;
    const url = "/subscriber/getActivePlanList/" + custId + "?isNotChangePlan=true";
    this.customerManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.custCurrentPlanList = response.dataList;
        this.totalQuota = this.custCurrentPlanList.reduce((acc, item) => {
          if (item.volQuotaUnit === "GB") {
            return Number(acc) + Number(item.volTotalQuota);
          } else {
            return Number(acc) + Number(item.volTotalQuota) / 1024;
          }
        }, 0);

        this.usedQuota = this.custCurrentPlanList.reduce((acc, item) => {
          let usedQuota;

          if (item.volTotalQuota <= item.volUsedQuota) {
            usedQuota = item.volTotalQuota;
          } else {
            usedQuota = item.volUsedQuota;
          }
          if (item.volQuotaUnit === "GB") {
            return Number(acc) + Number(usedQuota);
          } else {
            return Number(acc) + Number(usedQuota) / 1024;
          }
        }, 0);

        if (this.usedQuota > this.totalQuota) {
          this.usedQuota = this.totalQuota;
        }

        this.remainQuota = Number(this.totalQuota) - Number(this.usedQuota);

        this.quotaUnit = "GB";

        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  pageChangedcustomerCurrentPlanListData(pageNumber) {
    this.currentPagecustomerCurrentPlanListdata = pageNumber;
    this.getcustCurrentPlan(this.customerId, "");
  }

  TotalCurrentPlanItemPerPage(event) {
    this.CurrentPlanShowItemPerPage = Number(event.value);
    if (this.currentPagecustomerCurrentPlanListdata > 1) {
      this.currentPagecustomerCurrentPlanListdata = 1;
    }
    this.getcustCurrentPlan(this.customerId, this.CurrentPlanShowItemPerPage);
  }

  checkStatus(planStatus, workflowStatus) {
    let status = planStatus.toLowerCase();
    let statusWorkflow = workflowStatus ? workflowStatus.toLowerCase() : "";

    if (statusWorkflow == "newactivation" || statusWorkflow == "rejected") {
      if (statusWorkflow == "newactivation") this.badgeTypeForStatus = "green";
      else this.badgeTypeForStatus == "red";
      this.displayStatus = workflowStatus.toUpperCase();
    } else {
      this.displayStatus = planStatus.toUpperCase();
      switch (status) {
        case "active":
        case "ingrace":
          this.badgeTypeForStatus = "green";
          break;
        case "terminate":
        case "stop":
        case "inactive":
        case "expired":
          this.badgeTypeForStatus = "red";
          break;
        case "hold":
        case "disable":
          this.badgeTypeForStatus = "grey";
          break;
        default:
          break;
      }
    }
    return true;
  }

  getUsageData() {
    this.spinner.show();

    var request = {
      custId: this.customerId,
      page: 1,
      pageSize: 5
    };

    this.customerManagementService
      .getCDRDataByCustomerId(this.mvnoId, request)
      .subscribe((response: any) => {
        this.spinner.hide();
        if (response.acctCdr) {
          this.cdrListData = response.acctCdr.content;
          if (this.cdrListData.length > 0) {
            this.lastLogin = moment
              .utc(this.cdrListData[0].createdate)
              .format("MM-DD-YYYY HH:mm:ss");
          }
        }
        this.spinner.hide();
      });
  }

  @ViewChild("displayPaymentDialogs") displayPaymentDialogs!: TemplateRef<any>;
  @ViewChild("displayDialogs") displayDialogs!: TemplateRef<any>;
  openDialog() {
    this.displayPaymentDialog = true;
    this.dialog.open(this.displayPaymentDialogs, {
      width: "550px",
      disableClose: true
    });
  }
  proceedPayment(dialogRef) {
    this.submitted = true;
    if (this.paymentForm.valid) {
      this.displayDialog = false;
      this.displayPaymentDialog = false;

      dialogRef.close();
      const amount = this.paymentForm.value.amount;
      this.paymentAmount = amount;
      console.log("this.savedConfig :::", this.savedConfig);
      if (this.savedConfig.length === 0) {
        this.toastr.error(`Payment Gateway Configuration Not Found!`, "Failed!");
      } else if (this.savedConfig.length === 1) {
        if (this.savedConfig[0].paymentConfigName === "PAYTM") {
          this.spinner.show();
          this.addPayment(amount);
        } else if (this.savedConfig[0].paymentConfigName === "FLUTTERWAVE") {
          this.spinner.show();
          // this.renewActivePlan(amount);
        } else if (this.savedConfig[0].paymentConfigName === "RAZORPAY") {
          this.spinner.show();
          this.buyPlanWithRazorpay(amount);
        } else if (this.savedConfig[0].paymentConfigName === "MoMo Pay") {
          this.spinner.show();
          this.buyMomoPayPlan(amount);
        } else if (this.savedConfig[0].paymentConfigName === "AIRTEL") {
          this.spinner.show();
          this.airtelPayPlan(amount);
        } else if (this.savedConfig[0].paymentConfigName === "SELCOM") {
          this.spinner.show();
          this.buySelcomPayPlan(amount);
        } else if (this.savedConfig[0].paymentConfigName === "MPESA") {
          this.payMethod = "MPESA";
          this.spinner.show();
          this.showMpinModal();
          this.buyplan = amount;
        } 
        else {
          this.paymentConfirmationModal = false;
          // this.spinner.show();
          this.buyplan = amount;
          // this.showMpinModal();
        }
      } else if (this.savedConfig.length >= 1) {
        this.selectedAmount = amount;
        this.displayDialog = true;
        this.dialog.open(this.displayDialogs, {
          width: "50%",
          disableClose: true
        });
      }
      this.paymentForm.reset();
      this.submitted = false;
    }
  }

  buyPlan(savedConfig: any, dialogRef) {
    console.log("saveConfigInDialog ::::", savedConfig);
    // this.buy(this.selectedAmount, savedConfig);
    dialogRef.close();
    this.payMethod = savedConfig.paymentConfigName;
    if (["FLUTTERWAVE", "RAZORPAY", "PAYTM"].includes(this.payMethod)) {
      this.mpinModal = false;
      if (this.payMethod === "FLUTTERWAVE") {
        this.spinner.show();
        // this.renewActivePlan(amount);
      } else if (this.payMethod === "RAZORPAY") {
        this.spinner.show();
        this.buyPlanWithRazorpay(this.paymentAmount);
      } else if (this.payMethod === "PAYTM") {
        this.spinner.show();
        this.addPayment(this.paymentAmount);
      }
    } else {
      this.showMpinModal();
    }
  }
  buy(dialogRef) {
    if (this.payMethod === "PAYTM") {
      this.spinner.show();
      this.addPayment(this.paymentAmount);
    } else if (this.payMethod === "FLUTTERWAVE") {
      this.spinner.show();
      // this.renewActivePlan(amount);
    } else if (this.payMethod === "RAZORPAY") {
      this.spinner.show();
      this.buyPlanWithRazorpay(this.paymentAmount);
    } else if (this.payMethod === "MoMo Pay") {
      this.spinner.show();
      this.buyMomoPayPlan(this.paymentAmount);
    } else if (this.payMethod === "AIRTEL") {
      this.spinner.show();
      this.airtelPayPlan(this.paymentAmount);
    } else if (this.payMethod === "SELCOM") {
      this.spinner.show();
      this.buySelcomPayPlan(this.paymentAmount);
    }else if (this.payMethod === "MPESA") {
      this.spinner.show();
      this.buyMpesaExpressPlan(this.paymentAmount);
    } 
    else {
      this.paymentConfirmationModal = false;
      this.buyplan = this.paymentAmount;

      this.toastr.error(`Invoice payment is not available for this gateway.`, "Failed!");

      // this.mpinModal = false;

      // this.spinner.show();

      // this.showMpinModal();
    }
    dialogRef.close();
  }

  closePaymentDialog() {
    this.displayPaymentDialog = false;
    this.paymentForm.reset();
    this.submitted = false;
  }

  checkPaymentGatewayConfiguration() {
    this.spinner.show();
    this.customerdetailsilsService.getActivePaymentConfiguration().subscribe(
      (response: any) => {
        this.savedConfig = [];
        if (response.status == 204) {
          this.isPaymentGatewayConfigured = false;
        } else {
          var activeConfig = response.activePaymentConfig;
          var config = activeConfig.some(config => config.paymentConfigName == this.paymentGateway);
          this.savedConfig = activeConfig;

          const keyValuePairs: { [key: string]: any } = {};
          for (const config of this.savedConfig) {
            for (const mappingItem of config.paymentConfigMappingList) {
              keyValuePairs[mappingItem.paymentParameterName] = mappingItem.paymentParameterValue;
            }
          }
          this.paymentkeyValuePairs = keyValuePairs;
          this.isPaymentGatewayConfigured = config;
        }
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getCustomersDetail(custId) {
    this.isLoading = true;
    const url = "/customers/" + custId;
    this.customerManagementService.getMethod(url).subscribe((response: any) => {
      this.custDetails = response.customers;
      this.isLoading = false;
      
      this.currency = this.custDetails.currency ? this.custDetails.currency : this.currencySymbol;
      console.log("custDetails :::::", this.custDetails);
      //Address
      if (this.custDetails.addressList.length > 0) {
        if (this.custDetails.addressList[0].addressType) {
          this.presentFullAddress = this.custDetails.addressList[0].fullAddress;
          let areaurl = "/area/" + this.custDetails.addressList[0].areaId;

          this.customerdetailsilsService.commonGetMethod(areaurl).subscribe((response: any) => {
            this.presentAdressDATA = response.data;
          });
        }
      }
      this.spinner.hide();
    });
  }
  addPayment(amount) {
    let data;
    if (amount <= 0) {
      this.spinner.hide();

      this.toastr.error(`Amount can't be zero.`, "Failed!");
    }
    data = {
      custId: this.custDetails.id,
      payment: amount,
      status: "Initiate",
      isFromCaptive: false,
      mvnoid: this.custDetails.mvnoId,
      customerUsername: this.custDetails.username,
      merchantName: "Paytm"
    };
    this.customerdetailsilsService.addPayment(data).subscribe(
      (response: any) => {
        let orderId = response.CustomerPayment.orderId;
        this.paymentGateWay(orderId, amount);
        // this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  paymentGateWay(orderId, paymentAmount) {
    let data: any;
    let amount = paymentAmount;
    if (amount < 0) {
      amount = amount * -1;
    }
    data = {
      custId: this.custDetails.id,
      customerName: this.custDetails.username,
      mobileNo: this.custDetails.mobile,
      customerEmail: this.custDetails.email,
      txnAmount: amount,
      orderId: orderId
    };

    this.customerdetailsilsService.paymentGateway(data).subscribe(
      (response: any) => {
        window.location.href = response.paytmRedirectUrl;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  buyPlanWithRazorpay(amount: any) {
    this.displayDialog = false;
    this.selRenewAmount = amount;
    this.intiateRazorpay(amount, this.custDetails.id);
  }
  intiateRazorpay(payAmount, custId) {
    let data;
    data = {
      custId: custId,
      payment: payAmount,
      status: "Initiate",
      isFromCaptive: false
    };
    this.customerdetailsilsService.addPayment(data).subscribe(
      (response: any) => {
        this.orderId = response.CustomerPayment.orderId;
        this.razorPay(this.orderId, payAmount);
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  razorPay(orderId: any, payAmount: any) {
    console.log("this.paymentkeyValuePairs", this.paymentkeyValuePairs);
    const RAZORPAY_CALLBACK_URL_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_CALLBACK_URL];
    const RAZORPAY_CURRENCY_VALUE = this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_CURRENCY];
    const RAZORPAY_KEY_ID_VALUE = this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_KEY_ID];
    const RAZORPAY_SECRET_KEY_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_SECRET_KEY];
    let callback = RAZORPAY_CALLBACK_URL_VALUE;
    console.log("callback :::", callback);
    const razorpayOption = {
      description: "Savbill Nettech",
      currency: RAZORPAY_CURRENCY_VALUE,
      amount: Number(payAmount) * 100,
      name: "Savbill Nettech",
      key: RAZORPAY_KEY_ID_VALUE,
      secretKey: RAZORPAY_SECRET_KEY_VALUE,
      handler: (response: any) => {
        if (response != null && response.razorpay_payment_id != null) {
          this.processresponse(response, orderId);
        } else {
          this.toastr.error(`Payment Failed from razorpay`, "Failed!");
        }
      },
      prefill: {
        name: this.custDetails.username,
        email: this.custDetails.email,
        mobileNo: this.custDetails.mobile
      },
      theme: {
        color: "#f7b206"
      },
      modal: {
        ondismiss: () => {
          console.log("dismissed");
        }
      }
    };

    const success = (Id: any) => {
      console.log(Id);
    };
    console.log("razorpay :::", razorpayOption);
    var razorpayoptions = new Razorpay(razorpayOption);
    razorpayoptions.open();
  }

  processresponse(res: any, orderId: any) {
    const RAZORPAY_CALLBACK_URL_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_CALLBACK_URL];
    console.log("response:::", res);
    let callbackurl = RAZORPAY_CALLBACK_URL_VALUE;
    let data = {
      orderId: orderId,
      pgTransactionId: res.razorpay_payment_id
    };
    this.razaopaycallback(callbackurl, data);
  }

  razaopaycallback(callbackurl: any, data: any) {
    this.spinner.hide();
    this.customerdetailsilsService.razorpaycallback(callbackurl, data).subscribe(
      (response: any) => {
        console.log("in response of razorpay callback");
        // this.messageService.add({
        //   severity: "success",
        //   summary: "Success",
        //   detail: "plan buy successfully",
        //   icon: "far fa-times-circle",
        // });
        this.razorpayCallbackResponse = response.callbackResponse;
        localStorage.setItem("TransactionId", this.razorpayCallbackResponse.pgTransactionId);
        localStorage.setItem("OrderId", this.razorpayCallbackResponse.orderId);
        this.razopayredirectTimeInSeconds = parseInt(
          this.razorpayCallbackResponse.redirectTimeInSecond
        );
        if (
          typeof this.razorpayCallbackResponse.redirectTimeInSecond === "number" &&
          !isNaN(this.razorpayCallbackResponse.redirectTimeInSecond)
        ) {
          this.razopayredirectTimeInSeconds = parseInt(
            this.razorpayCallbackResponse.redirectTimeInSecond
          );
          localStorage.setItem("RedirectTimeInSecond", this.razopayredirectTimeInSeconds);
        } else {
          // Handle the case where redirectTimeInSecond is not a valid number
          console.error(
            "Invalid redirectTimeInSecond:",
            this.razorpayCallbackResponse.redirectTimeInSecond
          );
          // You may want to set a default value or handle this case differently
        }
        this.cdr.detectChanges();
        this.reciptUrl();
      },
      (error: any) => {
        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }
  reciptUrl() {
    this.spinner.hide();
    const receiptUrl = window.location.origin + "/#/receipt";
    console.log("receiptUrl :::", receiptUrl);
    window.open(receiptUrl, "_blank");
  }

  buyMomoPayPlan(amount) {
    this.exitBuy = true;
    this.isMpinFormSubmitted = true;
    this.mpinModal = false;
    this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
    // let custServiceList = this.custServiceData.filter((serviceelement) => serviceelement.service === plan.serviceName);
    // custServiceList = custServiceList.map((element) => element.customerServiceMappingId);
    let data = {
      customerId: this.custDetails.id,
      amount: amount,
      isFromCaptive: false,
      merchantName: "MoMo Pay",
      customerUserName: this.custDetails.username,
      customerUUID: uuid.v4(),
      mvnoId: this.custDetails.mvnoId,
      // custServiceMappingId: custServiceList[0],
      mobileNumber:
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      partnerId: this.custDetails.partnerid,
      accountNumber: this.custDetails?.acctno ?? "",
      isAdvancePayment: true
    };
    console.log("buyMomoPayPlan", data);
    this.customerdetailsilsService.buyPlanUsingMomo(data).subscribe(
      (response: any) => {
        this.spinner.hide();
        //localStorage.setItem("transactionId"),
        (localStorage.setItem("transactionId", response.data.data.orderId),
          console.log("in response of momo buy"));
        this.paymentConfirmationModal = true;
        this.isMpinFormSubmitted = false;
        this.mobileError = false;
        this.inputMobile = "";
        this.mpinForm.reset();
        this.exitBuy = false;
        // this.subscription2 = this.obs1$.subscribe((d) => {
        //   if (this.paymentstatusCount > 0) {
        //     this.paymentstatusCount = this.paymentstatusCount - 1;
        //     this.getStatusSuccessByMomo("SUCCESSFUL");
        //     if (this.transactionStatus === true) {
        //       this.subscription2.unsubscribe();
        //     }
        //   }
        //   if (this.paymentstatusCount == 0) {
        //
        //     this.subscription2.unsubscribe();
        //   }
        // });
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }
  hidepaymentConfirmDialog() {
    this.paymentConfirmationModal = false;
    this.displayDialog = false;
  }

  hidepaymentSucessDialog() {
    this.paymentSucessModel = false;
  }
  getserviceData() {
    this.spinner.show();
    let services = [];
    const url = "/subscriber/getActivePlanList/" + this.customerId + "?isNotChangePlan=true";
    this.customerManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.custServiceData = [];
        var keepGping = false;
        response.dataList.forEach(service => {
          {
            if (
              service.planId !== this.freePlanId &&
              service.custPlanStatus.toLowerCase() !== "newactivation"
            ) {
              this.custServiceData.push(service);
            }
          }
        });
        if (this.custServiceData.length > 0) {
          this.serviceSerialNumbers = [];
          this.custServiceData.forEach(item => {
            if (!keepGping) {
              var filteredItem = item.customerInventorySerialnumberDtos.filter(
                item => item.primary
              );
              if (filteredItem.length > 0) {
                this.isShowConnection = false;
                this.serviceSerialNumbers.push({
                  serialNumber: filteredItem[0].serialNumber,
                  custPlanMapppingId: item.custPlanMapppingId
                });
              } else {
                this.isShowConnection = true;
                this.serviceSerialNumbers = [];
                keepGping = true;
              }
            }
          });
        }
        let data = this.custServiceData;
        this.custServiceData = [];
        data.forEach(element => {
          if (
            element.custPlanStatus.toLowerCase() != "terminate" &&
            element.planId != this.freePlanId
          ) {
            this.custServiceData.push(element);
          }
        });
        this.customerCurrentPlanListdatatotalRecords = this.custServiceData.length;
        console.log("custServiceData ::: ", this.custServiceData);

        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getStatusSuccessByMomo(status) {
    this.spinner.hide();
    let data = {
      orderId: localStorage.getItem("transactionId"),
      status: status
    };
    this.customerdetailsilsService.getIntigrationTransactionstatus(data).subscribe(
      (response: any) => {
        console.log("transactionresponse:::", response);
        if (response.responseCode == 200) {
          if (response.data.istransactionsuccess === "true") {
            this.transactionStatus = response.istransactionsuccess;
            let data = {
              userName: this.custDetails.username,
              password: this.custDetails.password
            };
            // this.getDevice(data);
            this.paymentConfirmationModal = false;
            this.subscription2.unsubscribe();
            this.paymentSucessModel = true;
          }
        }
      },
      (error: any) => {
        this.spinner.hide();
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  @ViewChild("paymentConfirmationModals") paymentConfirmationModals!: TemplateRef<any>;
  airtelPayPlan(amount) {
    this.exitBuy = true;
    this.isMpinFormSubmitted = true;
    this.mpinModal = false;
    this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
    // let custServiceList = this.custServiceData.filter((serviceelement) => serviceelement.service === plan.serviceName);
    // custServiceList = custServiceList.map((element) => element.customerServiceMappingId);
    let data = {
      customerId: this.custDetails.id,
      amount: amount,
      isFromCaptive: false,
      merchantName: "AIRTEL",
      customerUserName: this.custDetails.username,
      mvnoId: this.custDetails.mvnoId,
      // custServiceMappingId: custServiceList[0],
      mobileNumber: this.mpinForm.value.mobileNumber ?? "",
      isAdvancePayment: true,
      partnerId: this.custDetails.partnerid,
      accountNumber: this.custDetails?.acctno ?? ""
    };
    console.log("airtelPayPlan", data);
    this.customerdetailsilsService.buyPlanUsingAirtel(data).subscribe(
      (response: any) => {
        this.spinner.hide();
        this.isMpinFormSubmitted = false;
        this.mpinForm.reset();
        //localStorage.setItem("transactionId"),
        if (response.responseCode === 417) {
          this.toastr.error(`${response.responseMessage}`, "Failed!");
          return;
        }
        (localStorage.setItem("transactionId", response.data.data.transaction.id),
          console.log("in response of momo buy"));
        this.exitBuy = false;
        this.paymentConfirmationModal = true;
        this.dialog.open(this.paymentConfirmationModals, {
          width: "550px",
          disableClose: true
        });

        this.mobileError = false;
        this.inputMobile = "";
        // this.subscription2 = this.obs1$.subscribe((d) => {
        //   if (this.paymentstatusCount > 0) {
        //     this.paymentstatusCount = this.paymentstatusCount - 1;
        //     this.getStatusSuccessByMomo("SUCCESSFUL");
        //     if (this.transactionStatus === true) {
        //       this.subscription2.unsubscribe();
        //     }
        //   }
        //   if (this.paymentstatusCount == 0) {
        //     this.exitBuy = false;
        //     this.subscription2.unsubscribe();
        //   }
        // });
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }

  buySelcomPayPlan(amount) {
    this.exitBuy = true;
    this.isMpinFormSubmitted = true;
    this.mpinModal = false;
    this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
    // let custServiceList = this.custServiceData.filter((serviceelement) => serviceelement.service === plan.serviceName);
    // custServiceList = custServiceList.map((element) => element.customerServiceMappingId);
    let customerPaymentDTO = {
      customerId: this.custDetails.id,
      buid: this.custDetails.buId,
      amount: amount,
      isBuyPlan: true,
      isAdvancePayment: true,
      isFromCaptive: true,
      merchantName: "SELCOM",
      customerUserName: this.custDetails.username,
      customerUUID: uuid.v4(),
      mvnoId: this.custDetails.mvnoId,
      // custServiceMappingId: custServiceList[0],
      mobileNumber:
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      orderId: null,
      invoiceId: null,
      partnerId: this.custDetails.partnerid,
      partnerPaymentId: this.custDetails.partnerPaymentId ?? null,
      status: this.custDetails.status
    };
    let selcomPayPayment = {
      vendor: "",
      order_id: null,
      buyer_email: this.custDetails.email,
      buyer_name: this.custDetails.username,
      buyer_phone:
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      gateway_buyer_uuid: "",
      amount: amount,
      currency: "",
      payment_methods: "",
      "billing.firstname": this.custDetails.firstname ?? "",
      "billing.lastname": this.custDetails.lastname ?? "",
      "billing.address_1": this.custDetails?.addressList[0]?.landmark ?? "",
      "billing.city": this.presentAdressDATA.cityName ?? "",
      "billing.state_or_region": this.presentAdressDATA.stateName ?? "",
      "billing.country": this.presentAdressDATA.countryName ?? "",
      "billing.phone":
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      no_of_items: 1,
      webhook: ""
    };

    let data = {
      customerPaymentDTO: customerPaymentDTO,
      selcomPayPayment: selcomPayPayment
    };
    console.log("buySelcomPayPlan", data);

    this.customerdetailsilsService.buyPlanUsingSelcom(data).subscribe(
      (response: any) => {
        this.spinner.hide();
        this.mobileError = false;
        this.inputMobile = "";
        this.isMpinFormSubmitted = false;
        this.mpinForm.reset();
        if (response.responseCode === 417) {
          this.toastr.error(`${response.responseMessage}`, "Failed!");
          return;
        } else if (response.responseCode === 200 && response.data && response.data.data) {
          const paymentLink = response.data.data;
          window.open(paymentLink, "_blank");
        } else {
          this.toastr.error(
            `${response.responseMessage}|| "Unexpected response received.`,
            "Failed!"
          );
        }
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }

  onInputMobile(event: any) {
    const inputValue = event.target.value;

    if (inputValue.startsWith("0")) {
      this.mobileError = true;
    } else {
      this.mobileError = false;
    }
  }

  onKeymobilelength(event: any) {
    const value = this.mpinForm.controls["mobileNumber"].value ?? "";

    const str = value.toString().replace(/,/g, "").trim();

    let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
    if (!mobilenumberlength) {
      mobilenumberlength = 10;
    }

    if (str.length === 0) {
      this.inputMobile = ""; // no error
      return;
    }

    if (str.length < mobilenumberlength) {
      this.inputMobile = `${mobilenumberlength} character required.`;
    } else if (str.length > mobilenumberlength) {
      this.inputMobile = `${mobilenumberlength} character required.`;
    } else {
      this.inputMobile = ""; // perfect
    }
  }

  hideMpinModal() {
    this.isMpinFormSubmitted = false;
    this.mpinForm.reset();
    // this.mpinForm.updateValueAndValidity();
    this.mpinModal = false;
    this.mobileError = false;
    this.inputMobile = "";
  }

  @ViewChild("mpinModals") mpinModals!: TemplateRef<any>;
  showMpinModal() {
    this.spinner.hide();
    this.mpinModal = true;

    this.mpinForm.controls.mobileNumber.reset();
    this.mpinForm.controls.countryCode.setValue(this.custDetails.countryCode);
    this.mpinForm.controls.mobileNumber.setValue(this.custDetails.mobile);
    this.dialog.open(this.mpinModals, {
      width: "550px",
      disableClose: true
    });
  }

  cols = [
    {
      field: "planName",
      header: "Plan Name",
      customExportHeader: "Plan Name"
    },
    // { field: "validity", header: "Validity", customExportHeader: "Validity" },
    {
      field: "custPlanStatus",
      header: "Plan Status",
      ustomExportHeader: "Plan Status"
    },
    { field: "offerPrice", header: "Amount", customExportHeader: "Amount" },
    {
      field: "dbStartDate",
      header: "Start Date",
      customExportHeader: "Start Date"
    },
    {
      field: "dbEndDate",
      header: "Service Expiry Date",
      customExportHeader: "Service Expiry Date"
    }
    // {
    //   field: "dbExpiryDate",
    //   header: "Billing End Date",
    //   customExportHeader: "Billing End Date"
    // }
  ];
  displaycolom = [
    "planName",
    // "validity",
    "custPlanStatus",
    "offerPrice",
    "dbStartDate",
    "dbEndDate"
    // "dbExpiryDate"
  ];

   buyMpesaExpressPlan(amount:any) {
          this.exitBuy = true;
          this.isMpinFormSubmitted = true;
          this.mpinModal = false;
          this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
          let data = {
              customerId: this.custDetails.id,
              amount: amount,
              // isFromCaptive: true,
              customerUserName: this.custDetails.username,
              // customerUUID: uuid.v4(),
              mvnoId: this.custDetails.mvnoId,
              // mobileNumber:
              //     this.createcustomerData.countryCode.replace("+", "") +
              //     (this.createcustomerData.mobile ?? ""),
              payerMobileNumber:
                  this.mpinForm.value.countryCode.replace("+", "") +
                  (this.mpinForm.value.mobileNumber ?? ""),
              // merchantName: null,
              // invoiceId: invoice.id,
              // partnerId: this.createcustomerData.partnerid,
              accountNumber: this.custDetails?.acctno ?? "",
              // custServiceMappingId: this.createcustomerData.planMappingList[0].custServiceMappingId,
              // buid: this.createcustomerData?.buId,
              // orderId: "",
              // planId: this.createcustomerData.planMappingList[0].planId,
              // hash: null,
              // isAdvancePayment: false,
              // isBuyPlan: true,
              // partnerPaymentId: this.createcustomerData.partnerid,
              // status: "PENDING"
          };
          this.customerdetailsilsService.buyPlanUsingMpesaExpress(data).subscribe(
              (response: any) => {
                  this.spinner.hide();
                  if (response.responseCode == 200) {
                      this.paymentConfirmationModal = true;
                      this.toastr.success(`${response.data.ResponseDescription}`, 'Success!');
                  } else {
                      this.toastr.info(`${response?.data?.errorMessage}`, 'Info!');
                  }
                  this.spinner.hide();
              },
              (error: any) => {
                  this.spinner.hide();
                  this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');
  
  
              }
          );
      }
}
