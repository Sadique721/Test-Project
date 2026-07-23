import { Component, OnInit, ChangeDetectorRef } from "@angular/core";
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService, PrimeNGConfig } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { ActivatedRoute, Router } from "@angular/router";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TITLE } from "../../RadiusUtils/RadiusConstants";
import {
  savbill_OTP_PROFILE,
  savbill_PLAN_NAME,
  FREE_PLAN,
  PARTNER_ID,
  SA_ID,
  S_NAME,
  SERVICE_ID,
  USERNAME,
  PASSWORD,
  FREEFLOW,
  ONLINEFLOW,
  VOUCHERFLOW,
  BRANCH_ID,
} from "src/app/RadiusUtils/RadiusConstants";
import { DatePipe } from "@angular/common";
@Component({
  selector: "app-twitter",
  templateUrl: "./twitter.component.html",
  styleUrls: ["./twitter.component.css"],
  providers: [DatePipe],
})
export class TwitterComponent implements OnInit {
  reportProblemForm!: FormGroup;
  sendReportSubmitted: boolean = false;
  value = 80;
  reportAQuestionModal: boolean = false;
  reportData: Array<any> = [
    {
      name: "Can't Connect to Blue Crane Communications Uganda Limited",
      value: "Can't Connect to Blue Crane Communications Uganda Limited",
    },
    { name: "Can't register or login", value: "Can't register or login" },
    { name: "Internet speed is too slow", value: "Internet speed is too slow" },
    {
      name: "Can't get balance information",
      value: "Can't get balance information",
    },
    {
      name: "Charged for data i did'nt use",
      value: "Charged for data i did'nt use",
    },
    { name: "Other", value: "Other" },
  ];
  userCurrentActivePlanList: any = [];
  currentUser: any;
  ratingForm: FormGroup;
  isRatingError: boolean = false;
  isRatingPresent: boolean = false;
  customerFeedback: any;
  displayRating: boolean = false;
  customerData: any;
  userData: any;
  envCountryCode = RadiusConstants.COUNTRY_CODE;
  deviceData: any;
  createCustomerPojo: any = {
    username: "CF1",
    password: "1111",
    firstname: "CF",
    lastname: "CF",
    email: "cf@gmail.com",
    title: "Mr",
    pan: "",
    gst: "",
    aadhar: "",
    passportNo: "",
    tinNo: null,
    contactperson: "CF",
    failcount: 0,
    custtype: "Prepaid",
    phone: "",
    mobile: 8640845009,
    countryCode: "+" + this.envCountryCode,
    cafno: 12,
    voicesrvtype: "",
    didno: "",
    calendarType: "English",
    partnerid: PARTNER_ID,
    salesremark: "",
    servicetype: "",
    serviceareaid: SA_ID,
    status: "Active",
    parentCustomerId: null,
    latitude: null,
    longitude: null,
    billTo: "CUSTOMER",
    isInvoiceToOrg: false,
    istrialplan: false,
    popid: null,
    discount: 0,
    plangroupid: "",
    planMappingList: [
      {
        planId: FREE_PLAN,
        service: S_NAME,
        validity: 1,
        discount: 0,
        billTo: "CUSTOMER",
        newAmount: null,
        offerPrice: 1210,
        isInvoiceToOrg: false,
        istrialplan: null,
      },
    ],
    addressList: [
      {
        addressType: "Present",
        landmark: "aa",
        areaId: RadiusConstants.AREA_ID,
        pincodeId: RadiusConstants.PINCODE_ID,
        cityId: RadiusConstants.CITY_ID,
        stateId: RadiusConstants.STATE_ID,
        countryId: RadiusConstants.COUNTRY_ID,
        version: "NEW",
      },
    ],
    overChargeList: [],
    custMacMapppingList: [],
    branch: BRANCH_ID,
    paymentDetails: {
      amount: 0,
      paymode: null,
      referenceno: null,
      paymentdate: null,
    },
    isCustCaf: null,
    dunningCategory: "Silver",
  };
  renewPlan: any = {
    purchaseType: "Renew",
    isPaymentReceived: "false",
    remarks: "remark",
    paymentOwnerId: 2,
    billableCustomerId: null,
    addonStartDate: null,
    ChangePlanCategory: "",
    isAdvRenewal: false,
    custId: "",
    recordPaymentDTO: {},
    isRefund: false,
    planBindWithOldPlans: [],
    newPlanList: null,
    planMappingList: null,
    isParent: true,
    discount: 0,
    planId: "",
    custServiceMappingId: "",
  };

  recordPayment: any = {
    amount: "",
    bank: "",
    chequedate: "2023-10-25",
    customerid: 0,
    paymode: "ONLINE",
    referenceno: "123456",
    remark: "online payment",
    reciptNo: "",
    type: "Payment",
    paytype: "",
    tdsAmount: 0,
    abbsAmount: 0,
    invoiceId: [0],
    onlinesource: "E_PAY",
    paymentListPojos: [],
    isAdjusted: false,
  };
  code: any;
  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private messageService: MessageService,
    private spinner: NgxSpinnerService,
    private primengConfig: PrimeNGConfig,
    private router: Router,
    private route: ActivatedRoute,
    private datePipe: DatePipe
  ) {
    this.currentUser = localStorage.getItem("customerId");
  }

  ngOnInit(): void {
    this.reportProblemForm = this.fb.group({
      desc: [""],
      phno: ["", Validators.required],
      issue_list: this.fb.array([], [Validators.required]),
    });

    this.ratingForm = this.fb.group({
      rating: [, Validators.required],
      feedback: [""],
      custId: [""],
    });
    this.getTwitterUser();
  }

  showReportAProblemDialog() {
    this.reportAQuestionModal = true;
  }

  sendReport() {
    this.sendReportSubmitted = true;
    console.log("data", this.reportProblemForm.value, this.reportProblemForm);
    if (this.reportProblemForm.valid) {
      this.spinner.show();
      const url = "reportproblem/savereport";
      this.loginService.postMethod(url, this.reportProblemForm.value).subscribe(
        (response: any) => {
          if (response.responseCode == 200) {
            this.hideReportAProblemDialog();
            this.messageService.add({
              severity: "success",
              summary: "Success",
              detail: response.responseMessage,
              icon: "far fa-times-circle",
            });
          } else {
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: response.responseMessage,
              icon: "far fa-times-circle",
            });
          }
          this.spinner.hide();
        },
        (error: any) => {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.ERROR,
            icon: "far fa-times-circle",
          });
          this.spinner.hide();
        }
      );
    } else {
      console.log("I am not valid");
    }
  }

  onCheckboxChange(e: any, value: any) {
    const checkArray: FormArray = this.reportProblemForm.get(
      "issue_list"
    ) as FormArray;
    if (e.checked) {
      checkArray.push(new FormControl(value));
    } else {
      let i: number = 0;
      checkArray.controls.forEach((item: any) => {
        if (item.value == value) {
          checkArray.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  hideReportAProblemDialog() {
    this.reportAQuestionModal = false;
    this.sendReportSubmitted = false;
    this.reportProblemForm.reset();
    // const checkArray: FormArray = this.reportProblemForm.get('issue') as FormArray;
    //  checkArray.controls.forEach((item: any) => {
    //     item.patchValue("")
    //   });
  }

  getCurrentUserActivePlan() {
    this.spinner.show();
    const url = "/subscriber/getActivePlanList/" + this.currentUser;
    this.loginService.getMethod(url).subscribe(
      (response: any) => {
        this.userCurrentActivePlanList = response.dataList;
        const usedDataPercentageLabel = "usedDataPercentage";
        const usedTimePercentageLabel = "usedTimePercentage";
        this.userCurrentActivePlanList.forEach((element) => {
          if (element.quotaType == "Data") {
            const usedDataPercentage =
              (element.volUsedQuota * 100) / element.volTotalQuota;
            console.log(usedDataPercentage);
            element[usedDataPercentageLabel] = usedDataPercentage.toFixed(2);
          } else {
            const usedTimePercentage =
              (element.timeUsedQuota * 100) / element.timeTotalQuota;
            console.log(usedTimePercentage);
            element[usedTimePercentageLabel] = usedTimePercentage.toFixed(2);
          }
        });
        console.log(this.userCurrentActivePlanList);
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.messageService.add({
          severity: "error",
          summary: "Error",
          detail: error.error.ERROR,
          icon: "far fa-times-circle",
        });
        this.spinner.hide();
      }
    );
  }

  goToLogin() {
    this.router.navigate(["/portal"], {
      relativeTo: this.route,
      queryParams: { name: "buy" },
    });
  }

  getFeedbackBuCustomer() {
    this.spinner.show();
    this.loginService
      .getCustomerFeedback(localStorage.getItem("customerId"))
      .subscribe(
        (response: any) => {
          if (response.status == 200) {
            this.customerFeedback = response.customerFeedback;
            this.isRatingPresent = true;
            this.updateRating();
          } else {
            this.saveRating();
          }
          this.spinner.hide();
        },
        (error: any) => {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.ERROR,
            icon: "far fa-times-circle",
          });
          this.spinner.hide();
        }
      );
  }
  saveRating() {
    this.isRatingError = true;
    if (this.ratingForm.valid) {
      var request = this.ratingForm.value;
      request.custId = localStorage.getItem("customerId");
      this.loginService.saveCustomerFeedback(request).subscribe(
        (response: any) => {
          this.messageService.add({
            severity: "success",
            summary: "Success",
            detail: response.message,
            icon: "far fa-times-circle",
          });
          this.ratingForm.reset();
          this.isRatingPresent = true;
          this.spinner.hide();
          this.customerFeedback = response.customerFeedback;
          this.logout();
        },
        (error: any) => {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.ERROR,
            icon: "far fa-times-circle",
          });
          this.spinner.hide();
        }
      );
    }
  }

  updateRating() {
    this.isRatingError = true;
    if (this.ratingForm.valid) {
      var request = this.ratingForm.value;
      request.custId = localStorage.getItem("customerId");
      request.id = this.customerFeedback.id;
      this.loginService.updateCustomerFeedback(request).subscribe(
        (response: any) => {
          this.messageService.add({
            severity: "success",
            summary: "Success",
            detail: response.message,
            icon: "far fa-times-circle",
          });
          this.isRatingPresent = true;
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
          this.logout();
          this.spinner.hide();
        },
        (error: any) => {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.ERROR,
            icon: "far fa-times-circle",
          });
          this.spinner.hide();
        }
      );
    }
  }
  logout() {
    localStorage.removeItem("customerId");
    this.spinner.show();
    let request = {
      userName: this.customerData.username,
      password: this.customerData.password,
      name: "mtik",
    };
    this.loginService.getDeviceDetails(request).subscribe(
      (response: any) => {
        var str = response.message.loginurl;
        console.log("get device data response  :::", response);
        document.location.href = response.message.logouturl;
        this.spinner.hide();
      },
      (error: any) => {
        this.messageService.add({
          severity: "error",
          summary: "Error",
          detail: error.error.message,
          icon: "far fa-times-circle",
        });
        this.spinner.hide();
      }
    );
  }
  onRating() {
    this.displayRating = true;
  }
  getcustomer() {
    const url = "/customers/" + localStorage.getItem("customerId");
    this.loginService.getCustomerById(url).subscribe(
      (response: any) => {
        this.customerData = response.customers;
      },
      (error: any) => {
        this.spinner.hide();
        this.messageService.add({
          severity: "error",
          summary: "Error",
          detail: error.error.ERROR,
          icon: "far fa-times-circle",
        });
      }
    );
  }
  getTwitterUser() {
    this.route.queryParams.subscribe((params) => {
      if (params["user"]) {
        try {
          // Decode and parse the user JSON string
          const userJson = decodeURIComponent(params["user"]);
          this.userData = JSON.parse(userJson);
          this.createCustomerWithTwitter(this.userData);
        } catch (error) {
          console.error("Error parsing user JSON:", error);
        }
      }
    });
  }
  createCustomerWithTwitter(payload: any) {
    console.log("payload in login component::", payload);
    this.checkTwitterCustomerBSS(payload.username, payload.displayName);
  }
  checkTwitterCustomerBSS(username: any, name: any) {
    this.spinner.show();
    let filterData = {
      filters: [
        {
          filterColumn: "usernameequalto",
          filterCondition: "and",
          filterDataType: "string",
          filterOperator: "equalto",
          filterValue: username,
        },
      ],
    };
    this.loginService.getCustomer(filterData).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customerList[0].id);

        this.deviceData = {
          userName: response.customerList[0].username,
          password: response.customerList[0].username,
        };

        const url = "/customers/" + response.customerList[0].id;
        console.log("enter in customer is found :::", this.renewPlan);
        this.loginService.getCustomerById(url).subscribe((res: any) => {
          if (response.status == 200) {
            this.deviceData = {
              userName: res.customers.username,
              password: res.customers.password,
            };
            console.log("enter in renew :::", this.renewPlan);
            // this.spinner.hide();
            let currentPlanList = [];
            const url =
              "/subscriber/getActivePlanList/" +
              res.customers.id +
              "?isNotChangePlan=false";
            this.loginService.getMethod(url).subscribe((response: any) => {
              currentPlanList = response.dataList;
              console.log("currentPlanList :::", currentPlanList.length);
              if (currentPlanList.length === 0) {
                let customerPlan = res.customers.planMappingList;
                this.renewPlan.paymentOwnerId = 2;
                this.renewPlan.custId = res.customers.id;
                this.renewPlan.isAdjusted = false;
                this.renewPlan.custServiceMappingId =
                  res.customers.planMappingList[0].custServiceMappingId;
                console.log("enter in renew :::", this.renewPlan);

                var changePlanRequestDTOList = [];
                this.recordPayment.paymentListPojos = [];
                changePlanRequestDTOList.push(this.renewPlan);

                this.recordPayment.customerid = res.customers.id;
                this.recordPayment.isAdjusted = false;
                this.recordPayment.chequedate = this.datePipe.transform(
                  new Date(),
                  "yyyy-MM-dd"
                );
                this.recordPayment.amount = 0; //nee to set amount of plan

                var paymentListPojos = {
                  tdsAmountAgainstInvoice: 0,
                  abbsAmountAgainstInvoice: 0,
                  amountAgainstInvoice: 0, //nee to set amount of plan
                  invoiceId: 0,
                };

                this.recordPayment.paymentListPojos.push(paymentListPojos);

                const request = {
                  changePlanRequestDTOList: changePlanRequestDTOList,
                  recordPayment: this.recordPayment,
                  custChargeDetailsList: [],
                };
                // API call for Addon or renew
                const renewurl = "/subscriber/changePlan01";
                this.loginService
                  .postMethod(renewurl, request)
                  .subscribe((response: any) => {
                    this.spinner.hide();
                    this.getDevice(this.deviceData);
                  });
              } else {
                this.spinner.hide();
                this.getDevice(this.deviceData);
              }
            });
          } else {
            this.spinner.hide();
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: response.error.ERROR,
              icon: "far fa-times-circle",
            });
          }
        });

        // this.spinner.hide();
      },
      (error: any) => {
        this.createTwitterCustomerBSS(username, name);
        this.spinner.hide();
      }
    );
  }

  createTwitterCustomerBSS(username: any, name: any) {
    this.spinner.show();
    let createCustRequest = { ...this.createCustomerPojo };
    createCustRequest.username = username;
    createCustRequest.password = username;
    createCustRequest.firstname = name;
    createCustRequest.lastname = name;
    createCustRequest.mobile = "9898989898";
    createCustRequest.email = "cf@gmail.com";
    this.loginService.createCustomerBSS(createCustRequest).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customer.id);
        this.spinner.hide();
        this.deviceData = {
          userName: response.customer.username,
          password: response.customer.password,
        };
        this.getDevice(this.deviceData);
      },
      (error: any) => {
        this.messageService.add({
          severity: "error",
          summary: "Error",
          detail: error.error.errorMessage,
          icon: "far fa-times-circle",
        });
        this.spinner.hide();
      }
    );
  }
  getDevice(data) {
    this.spinner.show();
    let urlParams: any = JSON.parse(localStorage.getItem("urlParam"));
    this.code = { ...data, ...urlParams };
    console.log("this.code :::", this.code);
    this.loginService.getDeviceDetails(this.code).subscribe(
      (response: any) => {
        var str = response.message.loginurl;
        localStorage.setItem("logOut", response.message.logouturl);
        this.loginURL(str, data);
        this.spinner.hide();
      },
      (error: any) => {
        this.messageService.add({
          severity: "error",
          summary: "Error",
          detail: error.error.message,
          icon: "far fa-times-circle",
        });
        this.spinner.hide();
      }
    );
  }
  loginURL(url: any, details: any) {
    document.location.href = url;
    this.spinner.show();
    this.logoutUrl();
  }
  logoutUrl() {
    this.spinner.hide();
    const logoutUrl = window.location.origin + "/#/logout";
    console.log("logoutUrl :::", logoutUrl);
    window.open(logoutUrl, "_blank");
  }
}
