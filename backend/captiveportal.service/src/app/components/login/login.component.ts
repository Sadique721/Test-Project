import { Component, OnInit, ChangeDetectorRef, ViewEncapsulation, ViewChild, ElementRef } from "@angular/core";
import {
  ActivatedRoute,
  Router,
  RouterModule,
  NavigationExtras,
} from "@angular/router";
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
import { AuthenticationService } from "src/app/service/authentication.service";
import { LoginService } from "src/app/service/login.service";
import { MessageService } from "primeng/api";
import { FormBuilder, Validators, FormGroup } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { PrimeNGConfig } from "primeng/api";
import { countries } from "src/app/components/model/country";
import { Icustomer } from "src/app/components/model/customer";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TITLE } from "../../RadiusUtils/RadiusConstants";
import { Title } from "@angular/platform-browser";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { catchError, last, map, tap } from "rxjs/operators";
import { Observable, of, throwError, Subscription, interval } from "rxjs";
import { DatePipe } from "@angular/common";
// import {
//   Flutterwave,
//   InlinePaymentOptions,
//   PaymentSuccessResponse,
// } from "flutterwave-angular-v3";
import {
  FLUTTERWAVE_CONSTANT,
  RAZORPAY_CONSTANT,
} from "src/app/components/constants/paymentGatewayConstant";
import { HttpClient } from "@angular/common/http";
import * as uuid from "uuid";
declare var FB: any;
declare var Razorpay: any;

import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import { VASTClient, VASTParser, VASTTracker } from "@dailymotion/vast-client";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
  providers: [DatePipe],
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent implements OnInit {
  countries: any = countries;
  freeFlow: boolean = FREEFLOW;
  voucherFlow: boolean = VOUCHERFLOW;
  onlineFlow: boolean = ONLINEFLOW;
  activeIndex: number = 0;
  createLoginForm!: FormGroup;
  otpForm!: FormGroup;
  voucherForm!: FormGroup;
  buyForm!: FormGroup;
  addLoginForm!: FormGroup;
  submitted = false;
  enterOTP = false;
  envCountryCode = RadiusConstants.COUNTRY_CODE;
  captivePortalUserBSS = {
    username: RadiusConstants.USERNAME,
    password: RadiusConstants.PASSWORD,
  };
  urlParm: any;
  customerId: any;
  baseURL: any;
  code: any;
  fbURL: any;
  isPaymentShow: boolean = false;
  isPartner: boolean = true;
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

  buyOnlineModal: boolean = false;
  mobileNumberLength: number;
  mobileNumberPattern: string ;
  planList: any = [];
  dataList: any = [];
  selectedPlanDetail: any;
  deviceData: any;
  voucherData: any;
  iframeWidth: number = 150;
  iframeHeight: number = 150;
  isPaymentGatewayConfigured: boolean = false;
  paymentGateway: any;
  savedConfig: any;
  paymentkeyValuePairs: { [key: string]: any } = {};
  checkoutPlan: any;
  checkoutCust: any;
  checkoutValue: any;
  orderId: any;
  selRenewPlanData: any;
  displayDialog: boolean;
  displayRating: boolean;
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
  voucherDetail;
  custData: any = {};
  sideadhtmlContent: any;
  bottomadhtmlContent: any;
  mobileadhtmlContent: any;
  videoVisible: boolean = false;
  ratingForm: FormGroup;
  isRatingError: boolean = false;
  isRatingPresent: boolean = false;
  customerFeedback: any;
  generatedUUID: string = "";
  qrCodeContent: string = this.generatedUUID;
  configLoaded: boolean = false;
  isQrCodeopen: boolean = false;
  isQrStatusCall: boolean = true;
  obs1$ = interval(1000);
  subscription1: Subscription;
  qrcodeCount = 180;
  isqrvalid: boolean = false;
  qrResponse: any;
  stompClient: any;
  connect: any;
  messages: any;
  storedUUID: any;
  videoUrl: any;
  changePlanRequest: any = {
    custId: null, // not null
    deactivatePlanReqModels: [
      {
        billToOrg: false,
        newPlanGroupId: null,
        planGroupId: null,
        newPlanId: null, //not null
        custServiceMappingId: null, //not null
        discount: 0,
      },
    ],
    planGroupChange: false,
    planGroupFullyChanged: false,
    paymentOwner: "yogesh Patil",
    paymentOwnerId: null, //not null
    billableCustomerId: null, // not null
    isParent: true,
    remark: "captive portal",
  };
  partnerListByServiceArea: any;
  razorpayCallbackResponse: any;
  ispaymentReceiptHtml: boolean = false;
  razopayredirectTimeInSeconds: any;
  requestParamName: string = "";
  remainingTime: number = 0;
  videoTimer: any;
  @ViewChild('adVideo') adVideo!: ElementRef;
  videoSources: string[] = ["assets/video/video1.MP4", "assets/video/video2.MP4"];
  selectedVideo: string = "";
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private authservice: AuthenticationService,
    private loginService: LoginService,
    private messageService: MessageService,
    private spinner: NgxSpinnerService,
    private primengConfig: PrimeNGConfig,
    private titleService: Title,
    private sanitizer: DomSanitizer,
    private datePipe: DatePipe,
    // private flutterWave: Flutterwave,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {
    // Removed this.captivePortalTokenBSS() from here
  }

  ngOnInit(): void {
    this.titleService.setTitle(TITLE);
    var str = location.href;
    this.baseURL = location.href;

    if (str.includes("?")) {
      this.urlParm = str.split("?").pop()!;
      this.code = JSON.parse(
        '{"' +
          decodeURI(this.urlParm)
            .replace(/"/g, '\\"')
            .replace(/&/g, '","')
            .replace(/=/g, '":"') +
          '"}'
      );
      localStorage.setItem("urlParam", JSON.stringify(this.code));
      let urlParams: any = JSON.parse(localStorage.getItem("urlParam"));
      this.requestParamName = this.code.name;
      this.enterOTP = false;
    }

    this.http
      .get("../assets/html/adserversidead.html", { responseType: "text" })
      .subscribe((data: string) => {
        this.sideadhtmlContent = this.sanitizer.bypassSecurityTrustHtml(data);
      });

    this.http
      .get("../assets/html/adserverbottomad.html", { responseType: "text" })
      .subscribe((data: string) => {
        this.bottomadhtmlContent = this.sanitizer.bypassSecurityTrustHtml(data);
      });

    this.http
      .get("../assets/html/adservermobilead.html", { responseType: "text" })
      .subscribe((data: string) => {
        this.mobileadhtmlContent = this.sanitizer.bypassSecurityTrustHtml(data);
      });

    this.primengConfig.ripple = true;
    this.createLoginForm = this.fb.group({
      userName: ["", Validators.required],
      password: ["", Validators.required],
    });
    this.addLoginForm = this.fb.group({
      firstname: ["", Validators.required],
      lastname: ["", Validators.required],
    });

    this.otpForm = this.fb.group({
      countryCode: ["+" + this.envCountryCode, Validators.required],
      mobileNumber: ["", Validators.required],
      otp: [""],
      email: ["", Validators.email],
      profile: [savbill_OTP_PROFILE],
      name:["", Validators.required]
    });

    this.buyForm = this.fb.group({
      planName: [""],
      mobile: ["", Validators.required],
    });

    this.voucherForm = this.fb.group({
      code: ["+" + this.envCountryCode, Validators.required],
      mobileNo: [
        "",
        [Validators.required, Validators.maxLength(this.mobileNumberLength)],
      ],
      voucherCode: ["", Validators.required],
    });

    this.fbURL = `https://www.facebook.com/v2.5/dialog/oauth?client_id=470605771919688&response_type=code&redirect_uri=${encodeURIComponent(
      this.baseURL
    )}&scope=email%2Cpublic_profile`;

    this.captivePortalTokenBSS();

    this.ratingForm = this.fb.group({
      rating: [, Validators.required],
      feedback: [""],
      custId: [""],
    });
    this.webSocketConnect();
  }

  extractLength(regexString) {
    const match = regexString.match(/\{(\d+)\}/);
    if (match) {
        return parseInt(match[1], 10);
    }
    return null; 
  }

  getAddURL(): SafeResourceUrl {
    const url = `http://192.168.24.32/adServer/www/delivery/afr.php?zoneid=2&location=${this.code.mac}&cb=INSERT_RANDOM_NUMBER_HERE`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  getAddURLWithoutMac(): SafeResourceUrl {
    const url = `http://${this.code.server}/adServer/www/delivery/afr.php?zoneid=1&cb=445566`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  initData() {
    this.getPlanByServiceArea();

    this.loginService
      .getConfigurationByName("MOBILE_NUMBER")
      .subscribe((res: any) => {
        this.mobileNumberLength = Number(res.data.value);
        this.mobileNumberPattern = res.data.value
        this.configLoaded = true; 
      });

    this.getAllServiceByServiceAreaId();
    this.getAllBranchesByServiceAreaId();

    this.loginService
      .getConfigurationByName("PAYMENT_GATEWAY_FOR_CAPTIVE")
      .subscribe((res: any) => {
        this.paymentGateway = res.data.value;
        this.checkPaymentGatewayConfiguration();

        console.log("Payment gateway -----", this.paymentGateway);
      });
  }

  getPlanByServiceArea() {
    this.spinner.show();
    var array = this.code.sa.split(",").map(Number);
    localStorage.setItem("sa", array);
    var data = {
      sa: array,
    };
    this.loginService.postMethod("/getAllPlansByServiceArea", data).subscribe(
      (response: any) => {
        console.log(response);
        this.planList = response.planList;
        this.planList.map((e, i) => {
          e.label = e.name + -+e.offerprice + -+e.validity + e.unitsOfValidity;
        });
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  captivePortalTokenBSS() {
    this.spinner.show();
    this.loginService.generateTokenBSS(this.captivePortalUserBSS).subscribe(
      (response: any) => {
        localStorage.setItem("token", response.accessToken);
        this.initData();
        this.spinner.hide();
      },
      (error: any) => {
        localStorage.removeItem("loggedInUser");
        this.spinner.hide();
      }
    );
  }

  handleChange(e) {
    this.enterOTP = false;
    this.createLoginForm = this.fb.group({
      userName: ["", Validators.required],
      password: ["", Validators.required],
    });
    this.otpForm = this.fb.group({
      countryCode: ["+" + this.envCountryCode, Validators.required],
      mobileNumber: ["", Validators.required],
      otp: [""],
      email: ["", Validators.email],
      profile: [savbill_OTP_PROFILE],
      name:["",Validators.required]
    });
    this.voucherForm = this.fb.group({
      code: ["+" + this.envCountryCode, Validators.required],
      mobileNo: ["", Validators.required],
      voucherCode: ["", Validators.required],
    });
    this.addLoginForm = this.fb.group({
      firstname: ["", Validators.required],
      lastname: ["", Validators.required],
    });
    console.log("e :::", e);
    if (e.index === 4) {
      this.openQrCode();
    }
  }

  getDevice(data) {
    this.spinner.show();
    this.code = { ...data, ...this.code };
    this.addLoginForm.reset();
    if (this.requestParamName) {
      this.loginService.getDeviceDetails(this.code).subscribe(
        (response: any) => {
          var str = response.message.loginurl;
          this.addLoginForm.reset();
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
    } else {
      this.loginService.getDeviceDetailsForLogin(this.code).subscribe(
        (response: any) => {
          var str = response.message.loginurl;
          this.addLoginForm.reset();
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
  }

  loginURL(url: any, details: any) {
    if (url !== null) {
      document.location.href = url;
    }
    this.spinner.show();
    this.logoutUrl();
  }

  resend(data: any) {
    var request = {
      countryCode: data.countryCode,
      emailId: data.email,
      mobileNumber: data.mobileNumber,
      otp: "",
      profile: "OTP",
    };

    this.resendotp(request);
  }

  resendotp(data: any) {
    data.profile = savbill_OTP_PROFILE;
    this.loginService.getOTP(data).subscribe(
      (response: any) => {
        this.otpForm = this.fb.group({
          countryCode: [this.otpForm.value.countryCode, Validators.required],
          mobileNumber: [this.otpForm.value.mobileNumber, Validators.required],
          otp: ["", Validators.required],
          profile: [savbill_OTP_PROFILE],
          email: [this.otpForm.value.email, Validators.email],
          name:[this.otpForm.value.name,Validators.required]
        });
        this.enterOTP = true;
        this.submitted = false;
        this.messageService.add({
          severity: "success",
          summary: "Successfully",
          detail: response.message,
          icon: "far fa-check-circle",
        });
        this.spinner.hide();
      },
      (error: any) => {
        if (error.error.status == 429) {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.error,
            icon: "far fa-times-circle",
          });
        } else {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.errorMessage,
            icon: "far fa-times-circle",
          });
        }
        this.spinner.hide();
      }
    );
  }

  getOtp() {
    if (!this.configLoaded) {
    this.messageService.add({
      severity: "info",
      summary: "Info",
      detail: "System initializing. Please try again.",
      icon: "far fa-times-circle",
    });
    return;
  }
    const mobileLength = this.extractLength(this.mobileNumberPattern);
    this.submitted = true;
    if (typeof this.mobileNumberLength === "undefined") {
      return;
    }
    console.log("mobileLength :::", mobileLength,this.otpForm.value.mobileNumber.length);
    if (this.otpForm.value.mobileNumber.length !== mobileLength) {
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail:
          "Mobile Number should be " + mobileLength + " digit.",
        icon: "far fa-times-circle",
      });
      return;
    }
    console.log("hhhh" , this.otpForm)
    if (this.enterOTP) {
      if (this.otpForm.value.otp) {
        let requestData = {
          countryCode: this.otpForm.value.countryCode,
          emailId: this.otpForm.value.email,
          mobileNumber: this.otpForm.value.mobileNumber
            ? this.otpForm.value.mobileNumber
            : "9898989898",
          otp: this.otpForm.value.otp,
          profile: this.otpForm.value.profile,
        };
        this.spinner.show();
        this.loginService.validateOTP(requestData).subscribe(
          (response: any) => {
            this.submitted = false;
           // this.checkCustomerBSS();
           this.startVideoAd();
            this.spinner.hide();
          },
          (error: any) => {
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: "OTP is invalid or OTP is expired",
              icon: "far fa-times-circle",
            });
            this.spinner.hide();
          }
        );
      }
    } else {
      if (this.otpForm.valid) {
        this.spinner.show();
        this.otpForm.patchValue({ otp: "" });
        this.enterOTP = true;
        this.submitted = false;
        let requestData = {
          countryCode: this.otpForm.value.countryCode,
          emailId: this.otpForm.value.email,
          mobileNumber: this.otpForm.value.mobileNumber
            ? this.otpForm.value.mobileNumber
            : "9898989898",
          otp: this.otpForm.value.otp,
          profile: this.otpForm.value.profile,
        };
        this.resendotp(requestData);
      }
    }
  }

  startVideoAd() {
  const randomIndex = Math.floor(Math.random() * this.videoSources.length);
  this.selectedVideo = this.videoSources[randomIndex] + '?t=' + new Date().getTime();
  
  this.videoVisible = true;
  this.cdr.detectChanges();

  if (this.adVideo) {
    const videoElement = this.adVideo.nativeElement;
    videoElement.load();
    videoElement.onloadedmetadata = () => {
      this.remainingTime = Math.floor(videoElement.duration);
      if (this.videoTimer) clearInterval(this.videoTimer);
      this.videoTimer = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime--;
        }
      }, 1000);
    };
  }
}

onVideoEnded() {
  this.videoVisible = false;
  if (this.videoTimer) {
    clearInterval(this.videoTimer);
  }
  this.checkCustomerBSS();
}

  checkCustomersBSS() {
    this.submitted = true;
    if (!this.createLoginForm.valid) {
      return;
    }
    this.spinner.show();
    let value;
    if (this.otpForm.valid) value = this.otpForm.controls.mobileNumber.value;
    else value = this.createLoginForm.controls.userName.value;

    const data = {
      password: this.createLoginForm.controls.password.value,
      username: this.createLoginForm.controls.userName.value,
    };

    this.loginService.loginSubscribeCustomer(data).subscribe(
      (response: any) => {
        if (response.status == 200) {
          this.spinner.hide();
          // this.messageService.add({
          //   severity: "success",
          //   summary: "Successfully",
          //   detail: response.message,
          //   icon: "far fa-check-circle",
          // });
          this.deviceData = data;
          console.log("test :::", response);
          localStorage.setItem("customerId", response.userId);
          this.getDevice(this.deviceData);
          this.spinner.hide();
        } else {
          (error: any) => {
            this.spinner.hide();
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: error.error.ERROR,
              icon: "far fa-times-circle",
            });
          };
        }
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

  checkCustomerBSS() {
    this.spinner.show();
    let value;
    value = this.otpForm.controls.mobileNumber.value
      ? this.otpForm.controls.mobileNumber.value
      : this.otpForm.controls.email.value;

    let filterData = {
      filters: [
        {
          filterColumn: "usernameequalto",
          filterCondition: "and",
          filterDataType: "string",
          filterOperator: "equalto",
          filterValue: value,
        },
      ],
    };
    this.loginService.getCustomer(filterData).subscribe(
      (response: any) => {
        
        if (!response?.customerList?.length) {
          this.createCustomerBSS();
          this.spinner.hide();
          return;
        }

        localStorage.setItem("customerId", response.customerList[0].id);

        const url = "/customers/" + response.customerList[0].id;
        this.customerId = response.customerList[0].id
        this.loginService.getCustomerById(url).subscribe((response: any) => {
          if (response.status == 200) {
            this.spinner.hide();
            let currentPlanList = [];
            let customerPlan = response.customers.planMappingList;
            this.deviceData = {
              username: response.customers.username,
              password: response.customers.password,
            };
            if (customerPlan.length > 0) {
              this.CheckPlanQuota(customerPlan, currentPlanList);
            } else {
              this.activeIndex = 2;
              this.messageService.add({
                severity: "info",
                summary: "Info",
                detail: "No data plan found kindly purchase new plan",
                icon: "far fa-times-circle",
              });
            }
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

        this.spinner.hide();
      },
      (error: any) => {
        this.createCustomerBSS();
        this.spinner.hide();
        //      this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //      detail: error.error.msg,
        //     icon: "far fa-times-circle",
        // });
      }
    );
  }

  createCustomerBSS() {
    this.spinner.show();
    if (this.voucherForm.valid) {
      this.createCustomerPojo.username = this.voucherForm.value.mobileNo;
      this.createCustomerPojo.password = this.voucherForm.value.voucherCode;
      this.createCustomerPojo.mobile = this.voucherForm.value.mobileNo;
      this.createCustomerPojo.planMappingList[0] = {
        planId: this.voucherData.id,
        service: this.voucherData.serviceName,
        validity: this.voucherData.validity,
        discount: 0,
        billTo: "CUSTOMER",
        newAmount: null,
        offerPrice: this.voucherData.offerprice,
        isInvoiceToOrg: false,
        istrialplan: null,
      };
      this.createCustomerPojo.firstname = this.voucherForm.value.voucherCode;
    } else {
      let userName;
      let password;
      if (this.otpForm.value.mobileNumber) {
        userName = this.otpForm.controls.mobileNumber.value;
        // password = this.otpForm.controls.otp.value;
      } else if (this.otpForm.value.email) {
        userName = this.otpForm.controls.email.value;
        // password = this.otpForm.controls.otp.value;
      } else {
        userName = "9898989898";
        // password = "9898989898";
      }
      this.createCustomerPojo.username = userName;
      this.createCustomerPojo.password = this.otpForm.controls.mobileNumber.value || '9898989898';
      this.createCustomerPojo.mobile = this.otpForm.controls.mobileNumber.value
        ? this.otpForm.controls.mobileNumber.value
        : "9898989898";
      this.createCustomerPojo.email = this.otpForm.value.email
        ? this.otpForm.value.email
        : "cf@gmail.com";
       this.createCustomerPojo.firstname = this.otpForm.controls.name.value
    }
    if (this.selectedPlanDetail) {
      this.planList.forEach((element) => {
        if (element.id == this.selectedPlanDetail) {
          this.createCustomerPojo.planMappingList[0] = {
            planId: element.id,
            service: element.serviceName,
            validity: element.validity,
            discount: 0,
            billTo: "CUSTOMER",
            newAmount: null,
            offerPrice: element.offerprice,
            isInvoiceToOrg: false,
            istrialplan: null,
          };
        }
      });
    }
    this.loginService.createCustomerBSS(this.createCustomerPojo).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customer.id);
        this.spinner.hide();
        this.deviceData = {
          username: response.customer.username,
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

  validateVoucher() {
    if (typeof this.mobileNumberLength === "undefined") {
      return;
    }
    if (this.voucherForm.value.mobileNo.length !== this.mobileNumberLength) {
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail:
          "Mobile Number should be " + this.mobileNumberLength + " digit.",
        icon: "far fa-times-circle",
      });
      return;
    }
    if (this.voucherForm.valid) {
      console.log(this.voucherForm.value);
      this.spinner.show();
      const url = `/voucher/verify?code=${this.voucherForm.value.voucherCode}`;

      this.loginService.postMethod(url, "").subscribe(
        (response: any) => {
          this.voucherData = response.plan;
          this.voucherDetail = response.voucher;
          console.log("plan :::", this.voucherData);
          const serviceareamatch = this.voucherData.serviceAreaNameList.some(
            (voucherservicearea) =>
              voucherservicearea.id == RadiusConstants.SA_ID
          );
          console.log(
            "this.voucherData.serviceAreaNameList,id ::: ",
            this.voucherData.serviceAreaNameList[0].id
          );
          console.log("RadiusConstants.SA_ID ::", RadiusConstants.SA_ID);
          if (serviceareamatch === false) {
            this.spinner.hide();
            this.messageService.add({
              severity: "info",
              summary: "Info",
              detail:
                "Unfortunately, the voucher you've presented cannot be utilized in this specific area.",
              icon: "far fa-times-circle",
            });
            return;
          }
          let filterData = {
            filters: [
              {
                filterColumn: "usernameequalto",
                filterCondition: "and",
                filterDataType: "string",
                filterOperator: "equalto",
                filterValue: this.voucherForm.value.mobileNo,
              },
            ],
          };
          this.loginService.getCustomer(filterData).subscribe(
            (response: any) => {
              let custId = response.customerList[0].id;
              localStorage.setItem("customerId", custId);
              this.getCustomersDetail(custId).subscribe((res: any) => {
                this.validateVoucherMethod();
                this.renewPlan.paymentOwnerId = 2;
                this.renewPlan.custId = custId;
                this.renewPlan.planId = this.voucherData.id;
                this.renewPlan.isAdjusted = true;
                this.renewPlan.custServiceMappingId =
                  this.custData.planMappingList[0].custServiceMappingId;
                this.renewPlan.voucherId = this.voucherDetail.id;

                if (
                  this.voucherData.planGroup == "Registration and Renewal" ||
                  this.voucherData.planGroup == "Renew"
                ) {
                  this.renewPlan.purchaseType = "Renew";
                } else if (
                  this.voucherData.planGroup == "Volume Booster" ||
                  this.voucherData.planGroup == "Bandwidth Booster"
                ) {
                  this.renewPlan.purchaseType = "Addon";
                }

                var changePlanRequestDTOList = [];
                this.recordPayment.paymentListPojos = [];
                changePlanRequestDTOList.push(this.renewPlan);

                this.recordPayment.customerid = custId;
                this.recordPayment.isAdjusted = true;
                this.recordPayment.chequedate = this.datePipe.transform(
                  new Date(),
                  "yyyy-MM-dd"
                );
                this.recordPayment.amount = this.voucherData.offerprice; //nee to set amount of plan

                var paymentListPojos = {
                  tdsAmountAgainstInvoice: 0,
                  abbsAmountAgainstInvoice: 0,
                  amountAgainstInvoice: this.voucherData.offerprice, //nee to set amount of plan
                  invoiceId: 0,
                };

                this.recordPayment.paymentListPojos.push(paymentListPojos);

                const request = {
                  changePlanRequestDTOList: changePlanRequestDTOList,
                  recordPayment: this.recordPayment,
                  custChargeDetailsList: [],
                };

                // API call for Addon or renew
                const url = "/subscriber/changePlan01";
                this.loginService
                  .postMethod(url, request)
                  .subscribe((response: any) => {
                    this.spinner.hide();
                    this.deviceData = {
                      username: res.customers.username,
                      password: res.customers.password,
                    };
                    this.getDevice(this.deviceData);
                  });
              });
            },
            (error: any) => {
              if (error.error.status == 404) {
                //  create Independent customer
                if (
                  this.voucherData.planGroup != "Registration" &&
                  this.voucherData.planGroup != "Registration and Renewal"
                ) {
                  this.messageService.add({
                    severity: "info",
                    summary: "Info",
                    detail:
                      "We regret to inform you that you cannot use the addon voucher at this time as you are not yet a registered customer.",
                    icon: "far fa-times-circle",
                  });
                  return;
                }
                this.validateVoucherMethod();
                this.createCustomerPojo.countryCode =
                  this.voucherForm.value.code;
                this.createCustomerPojo.mobile =
                  this.voucherForm.value.mobileNo;
                this.createCustomerPojo.username =
                  this.voucherForm.value.mobileNo;
                this.createCustomerPojo.password =
                  this.voucherForm.value.mobileNo;
                this.createCustomerPojo.email = "CF@gmail.com";
                this.createCustomerPojo.contactperson =
                  this.voucherForm.value.mobileNo;
                this.createCustomerPojo.planMappingList[0].planId =
                  this.voucherData.id;
                this.createCustomerPojo.planMappingList[0].voucherId =
                  this.voucherDetail.id;

                this.recordPayment.paymentListPojos = [];
                this.recordPayment.amount = this.voucherData.offerprice;
                this.recordPayment.isAdjusted = true;

                var paymentListPojos = {
                  tdsAmountAgainstInvoice: 0,
                  abbsAmountAgainstInvoice: 0,
                  amountAgainstInvoice: this.voucherData.offerprice, //nee to set amount of plan
                  invoiceId: 0,
                };

                this.recordPayment.chequedate = this.datePipe.transform(
                  new Date(),
                  "yyyy-MM-dd"
                );
                this.recordPayment.paymentListPojos.push(paymentListPojos);

                this.createCustomerPojo.paymentDetails = this.recordPayment;
                this.createCustomerPojo.customerLocations = [];
                this.createCust(this.createCustomerPojo).subscribe(
                  (res: any) => {
                    localStorage.setItem("customerId", res.customer.id);
                    this.deviceData = {
                      username: res.customer.username,
                      password: res.customer.password,
                    };
                    this.getDevice(this.deviceData);
                  }
                );
              } else {
                this.messageService.add({
                  severity: "error",
                  summary: "Error",
                  detail: error.error.ERROR,
                  icon: "far fa-times-circle",
                });
              }
              this.spinner.hide();
            }
          );
          this.spinner.hide();
        },
        (error: any) => {
          this.messageService.add({
            severity: "info",
            summary: "Info",
            detail: error.error.errorMessage
              ? error.error.errorMessage
              : error.error.msg,
            icon: "far fa-times-circle",
          });
          this.spinner.hide();
        }
      );
    }
  }

  CheckPlanQuota(customerPlan, currentPlanList) {
    let currentDate = new Date();
const validPlans = customerPlan.filter(p => p.expiryDate);

const latestPlan = validPlans.reduce((max, current) => {
  return new Date(current.expiryDate).getTime() >
         new Date(max.expiryDate).getTime()
    ? current
    : max;
});
    [latestPlan].forEach((element) => {
      let exDate = new Date(element.expiryDate);
      if (exDate.getTime() > currentDate.getTime()) {
        if (element.quotaList[0].quotaType == "Data") {
          if (
            element.quotaList[0].usedQuota < element.quotaList[0].totalQuota
          ) {
            currentPlanList.push(element.planId);
            // this.showInternerSessionStartPopup();
            this.getDevice(this.deviceData);
            this.spinner.hide();
          }
        } else if (element.quotaList[0].quotaType == "Time") {
          if (
            element.quotaList[0].timeQuotaUsed <
            element.quotaList[0].timeTotalQuota
          ) {
            currentPlanList.push(element.planId);
            this.getDevice(this.deviceData);
            this.spinner.hide();
          }
        } else if (element.quotaList[0].quotaType == "Both") {
          if (
            element.quotaList[0].usedQuota < element.quotaList[0].totalQuota &&
            element.quotaList[0].timeQuotaUsed <
              element.quotaList[0].timeTotalQuota
          ) {
            currentPlanList.push(element.planId);
            this.getDevice(this.deviceData);
            this.spinner.hide();
          }
        }
      } else {
        this.renewCustomerPlan(customerPlan);
      }
    });
    if (currentPlanList.length <= 0) {
      this.activeIndex = 1;
      if (this.subscription1) {
        this.subscription1.unsubscribe();
      }
      this.isQrCodeopen = false;
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail: "Your quota is exhausted please buy a plan/voucher",
        icon: "far fa-times-circle",
      });
      this.spinner.hide();
    } else {
      this.messageService.add({
        severity: "success",
        summary: "Successfully",
        detail: "Login Success.",
        icon: "far fa-check-circle",
      });
      this.spinner.hide();
    }
  }

  renewCustomerPlan(customerPlan) {
    const data = {
  "changePlanRequestDTOList": [
    {
      "purchaseType": "Renew",
      "isPaymentReceived": false,
      "remarks": "e",
      "paymentOwnerId": null, 
      "billableCustomerId": customerPlan[0]?.billableCustomerId || null, 
      "addonStartDate": null,
      "addonEndDate": null,
      "ChangePlanCategory": "",
      "isAdvRenewal": false,
      "custId": this.customerId,
      "recordPaymentDTO": {  
      },
      "isRefund": false,
      "planBindWithOldPlans": [
      ],
      "newPlanList": null,
      "planMappingList": null,
      "isParent": true,
      "renewalForBooster": false,
      "discount": 0,
      "planId": customerPlan[0]?.planId,
      "custServiceMappingId": customerPlan[0]?.custServiceMappingId
    }
  ],
  "recordPayment": null,
  "custChargeDetailsList": [    
  ]
};
      this.loginService.renewCustomerPlan(data).subscribe((response) => {
        this.getDevice(this.deviceData);
      this.spinner.hide();
      }, () => {
        this.messageService.add({
        severity: "error",
        summary: "Error",
        detail: "Something went wrong",
        icon: "far fa-times-circle",
      });
      this.spinner.hide();
      });
  }

  keypressId(event: any) {
    const pattern = /^[0-9]+$/;
    let inputChar = String.fromCharCode(event.charCode);
    if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
      event.preventDefault();
    }
  }

  onInput(event: any) {
    const pattern = /^[0-9]+$/;
    let inputValue = event.target.value;

    // Remove non-numeric characters
    inputValue = inputValue.replace(/[^0-9]/g, "");

    // Limit to 10 digits
    inputValue = inputValue.slice(0, this.mobileNumberLength);

    // Update the input value only if it doesn't exceed the maximum length
    if (event.target.value.length <= this.mobileNumberLength) {
      event.target.value = inputValue;
    }

    // Now, you can access the 10-digit value in your Angular code
    const mobileNumber = inputValue;
  }

  getOtpForOnline() {
    this.submitted = true;
    if (typeof this.mobileNumberLength === "undefined") {
      return;
    }
    if (this.otpForm.value.mobileNumber.length !== this.mobileNumberLength) {
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail:
          "Mobile Number should be " + this.mobileNumberLength + " digit.",
        icon: "far fa-times-circle",
      });
      return;
    }
    if (this.enterOTP) {
      if (this.otpForm.value.otp) {
        let requestData = {
          countryCode: this.otpForm.value.countryCode,
          emailId: this.otpForm.value.email,
          mobileNumber: this.otpForm.value.mobileNumber
            ? this.otpForm.value.mobileNumber
            : "9898989898",
          otp: this.otpForm.value.otp,
          profile: this.otpForm.value.profile,
        };
        this.spinner.show();
        this.loginService.validateOTP(requestData).subscribe(
          (response: any) => {
            this.submitted = false;
            this.checkCustomerBSSforOnline();
            this.spinner.hide();
          },
          (error: any) => {
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: "OTP is invalid or OTP is expired",
              icon: "far fa-times-circle",
            });
            this.spinner.hide();
          }
        );
      }
    } else {
      if (this.otpForm.valid) {
        this.spinner.show();
        this.otpForm.patchValue({ otp: "" });
        this.enterOTP = true;
        this.submitted = false;
        let requestData = {
          countryCode: this.otpForm.value.countryCode,
          emailId: this.otpForm.value.email,
          mobileNumber: this.otpForm.value.mobileNumber
            ? this.otpForm.value.mobileNumber
            : "9898989898",
          otp: this.otpForm.value.otp,
          profile: this.otpForm.value.profile,
        };
        this.resendotp(requestData);
      }
    }
  }

  openPaymentGateways(plan: any) {
    this.displayDialog = false;
    this.spinner.hide();
    if (this.savedConfig.length === 0) {
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail: "Payment Gateway Configuration Not Found!!!",
        icon: "far fa-times-circle",
      });
    } else if (this.savedConfig.length === 1) {
      if (this.savedConfig[0].paymentConfigName === "PAYTM") {
        this.spinner.show();
        this.addPayment(plan);
      } else if (this.savedConfig[0].paymentConfigName === "FLUTTERWAVE") {
        this.spinner.show();
        this.renewActivePlan(plan);
      } else if (this.savedConfig[0].paymentConfigName === "RAZORPAY") {
        this.spinner.show();
        this.buyPlanWithRazorpay(plan);
      } else {
        this.checkoutPlan = plan;
      }
    } else if (this.savedConfig.length > 1) {
      this.checkoutPlan = plan;
      this.displayDialog = true;
    }
  }

  checkPaymentGatewayConfiguration() {
    this.spinner.show();
    this.loginService.getActivePaymentConfiguration().subscribe(
      (response: any) => {
        this.savedConfig = [];
        if (response.status == 204) {
          this.isPaymentGatewayConfigured = false;
        } else {
          var activeConfig = response.activePaymentConfig;
          var config = activeConfig.some(
            (config) => config.paymentConfigName == this.paymentGateway
          );
          this.savedConfig = activeConfig;

          const keyValuePairs: { [key: string]: any } = {};
          for (const config of this.savedConfig) {
            for (const mappingItem of config.paymentConfigMappingList) {
              keyValuePairs[mappingItem.paymentParameterName] =
                mappingItem.paymentParameterValue;
            }
          }
          this.paymentkeyValuePairs = keyValuePairs;
          this.isPaymentGatewayConfigured = config;
        }
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
  checkCustomerBSSforOnline() {
    this.spinner.show();
    let value;
    value = this.otpForm.controls.mobileNumber.value;

    let filterData = {
      filters: [
        {
          filterColumn: "usernameequalto",
          filterCondition: "and",
          filterDataType: "string",
          filterOperator: "equalto",
          filterValue: value,
        },
      ],
    };
    this.loginService.getCustomer(filterData).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customerList[0].id);
        const url = "/customers/" + response.customerList[0].id;
        var selectedPlan;
        this.loginService.getCustomerById(url).subscribe((res: any) => {
          if (this.selectedPlanDetail) {
            this.planList.forEach((element) => {
              if (element.id == this.selectedPlanDetail) {
                selectedPlan = element;
                this.checkoutPlan = selectedPlan;
              }
            });
            this.checkoutCust = res.customers;
            this.checkoutValue = this.checkoutPlan.offerprice;
            this.openPaymentGateways(this.checkoutPlan);
          }
        });

        this.spinner.hide();
      },
      (error: any) => {
        console.log("come in error &&&&&");
        this.spinner.hide();

        this.createCustomerPojo.countryCode = this.otpForm.value.countryCode;
        this.createCustomerPojo.mobile = this.otpForm.value.mobileNumber;
        this.createCustomerPojo.username = this.otpForm.value.mobileNumber;
        this.createCustomerPojo.password = this.otpForm.value.otp;
        this.createCustomerPojo.email = "CF@gmail.com";
        this.createCustomerPojo.contactperson = this.otpForm.value.mobileNumber;

        let selectedPlan;
        this.createCust(this.createCustomerPojo).subscribe((res: any) => {
          if (this.selectedPlanDetail) {
            this.planList.forEach((element) => {
              if (element.id == this.selectedPlanDetail) {
                selectedPlan = element;
                this.checkoutPlan = selectedPlan;
              }
            });
            this.checkoutCust = res.customer;
            this.checkoutValue = this.checkoutPlan.offerprice;
            this.openPaymentGateways(this.checkoutPlan);
          }
        });
      }
    );
  }
  createCust(customerRequest: any): Observable<any> {
    this.spinner.show();
    return this.loginService.createCustomerBSS(customerRequest).pipe(
      map((res: any) => {
        // this.custData = res.customers;
        return res;
      }),
      catchError((error: any) => {
        this.spinner.hide();
        return throwError(error);
      })
    );
  }

  buyPlan(savedConfig: any) {
    this.checkout(this.checkoutPlan, savedConfig);
  }

  checkout(plan: any, selectedConfig: any) {
    if (selectedConfig.paymentConfigName === "PAYTM") {
      this.spinner.show();
      this.addPayment(plan);
    } else if (selectedConfig.paymentConfigName === "FLUTTERWAVE") {
      this.spinner.show();
      this.renewActivePlan(plan);
    } else if (selectedConfig.paymentConfigName === "RAZORPAY") {
      this.spinner.show();
      this.buyPlanWithRazorpay(plan);
    } else {
      this.spinner.show();
      this.checkoutPlan = plan;
    }
  }

  addPayment(plan) {
    let data;
    let amount = plan.offerprice;
    if (amount <= 0) {
      this.spinner.hide();
      this.messageService.add({
        severity: "info",
        summary: "Info",
        detail: "Amount can't be zero",
        icon: "far fa-times-circle",
      });
    } else {
      data = {
        custId: this.checkoutCust.id,
        payment: amount,
        status: "Initiate",
        planId: this.checkoutPlan.id,
        isFromCaptive: true,
      };
      this.loginService.addPayment(data).subscribe(
        (response: any) => {
          this.orderId = response.CustomerPayment.orderId;
          this.paymentGateWay(this.orderId, amount);
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

  paymentGateWay(orderId, paymentAmount) {
    let data: any;
    let amount = paymentAmount;
    if (amount < 0) {
      // Multiply number with -1
      // to make it positive
      amount = amount * -1;
    }
    data = {
      custId: this.checkoutCust.id,
      customerName: this.checkoutCust.username,
      mobileNo: this.checkoutCust.mobile,
      customerEmail: this.checkoutCust.email,
      txnAmount: amount,
      orderId: this.orderId,
    };

    this.loginService.paymentGateway(data).subscribe(
      (response: any) => {
        window.location.href = response.paytmRedirectUrl;
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
  renewActivePlan(plan) {
    this.selRenewPlanData = plan;
    // this.addRenewPayment(
    //   plan.offerprice,
    //   this.checkoutCust.id,
    //   this.selRenewPlanData.id
    // );
    // this.renewPlanAfterPay();
  }

  // addRenewPayment(payAmount, custId, planId) {
  //   let data;
  //   data = {
  //     custId: custId,
  //     payment: payAmount,
  //     status: "Initiate",
  //     planId: planId,
  //     isFromCaptive: true,
  //   };
  //   this.loginService.addPayment(data).subscribe(
  //     (response: any) => {
  //       this.orderId = response.CustomerPayment.orderId;
  //       this.renewPaymentGateWay(this.orderId, payAmount);
  //       this.spinner.hide();
  //     },
  //     (error: any) => {
  //       this.messageService.add({
  //         severity: "error",
  //         summary: "Error",
  //         detail: error.error.ERROR,
  //         icon: "far fa-times-circle",
  //       });
  //       this.spinner.hide();
  //     }
  //   );
  // }

  // renewPaymentGateWay(orderId, payAmount) {
  //   console.log("this.paymentkeyValuePairs", this.paymentkeyValuePairs);
  //   const FLUTTERWAVE_RENEW_CWSC_CALLBACK_URL_VALUE =
  //     this.paymentkeyValuePairs[
  //       FLUTTERWAVE_CONSTANT.FLUTTERWAVE_RENEW_CWSC_CALLBACK_URL
  //     ];
  //   const FLUTTERWAVE_PUBLIC_KEY_VALUE =
  //     this.paymentkeyValuePairs[FLUTTERWAVE_CONSTANT.FLUTTERWAVE_PUBLIC_KEY];
  //   const FLUTTERWAVE_CURRENCY =
  //     this.paymentkeyValuePairs[FLUTTERWAVE_CONSTANT.FLUTTERWAVE_CURRENCY];
  //   let data: any;
  //   let amount = payAmount;
  //   if (amount < 0) {
  //     // Multiply number with -1
  //     // to make it positive
  //     amount = amount * -1;
  //   }
  //   data = {
  //     custId: this.checkoutCust.id,
  //     customerName: this.checkoutCust.username,
  //     mobileNo: this.checkoutCust.mobile,
  //     customerEmail: this.checkoutCust.email,
  //     txnAmount: amount,
  //     orderId: orderId,
  //   };

  //   let paymentData: InlinePaymentOptions = {
  //     public_key: FLUTTERWAVE_PUBLIC_KEY_VALUE,
  //     tx_ref: orderId,
  //     amount: amount,
  //     currency: FLUTTERWAVE_CURRENCY,
  //     redirect_url: FLUTTERWAVE_RENEW_CWSC_CALLBACK_URL_VALUE,
  //     meta: { counsumer_id: this.checkoutCust.custId },
  //     customer: {
  //       name: this.checkoutCust.username,
  //       email: this.checkoutCust.email,
  //       phone_number: this.checkoutCust.mobile,
  //     },
  //     callback: this.makePaymentCallback,
  //     onclose: this.closedPaymentModal,
  //     callbackContext: this,
  //   };
  //   console.log("paymentData :::", paymentData);
  //   this.flutterWave.inlinePay(paymentData);
  // }
  // makePaymentCallback(response: PaymentSuccessResponse): void {
  //   console.log("Pay", response);
  //   this.flutterWave.closePaymentModal(5);
  // }
  // closedPaymentModal(): void {
  //   console.log("payment is closed");
  // }
  getCustomersDetail(custId): Observable<any> {
    const url = "/customers/" + custId;
    this.spinner.show();
    this.custData = {};
    return this.loginService.getMethod(url).pipe(
      map((res: any) => {
        this.custData = res.customers;
        return res;
      }),
      catchError((error: any) => {
        this.spinner.hide();
        return throwError(error);
      })
    );
  }
  validateVoucherMethod() {
    const url = `/voucher/validate?code=${this.voucherForm.value.voucherCode}`;
    this.loginService.postMethod(url, "").subscribe((response: any) => {});
  }
  onclickstart() {
    this.videoVisible = true;
  }
  videoEnded() {
    this.videoVisible = false;
    this.checkadCustomerBSS();
  }

  checkadCustomerBSS() {
    this.spinner.show();
    let value;
    value = this.addLoginForm.controls.firstname.value;
    let filterData = {
      filters: [
        {
          filterColumn: "usernameequalto",
          filterCondition: "and",
          filterDataType: "string",
          filterOperator: "equalto",
          filterValue: value,
        },
      ],
    };
    this.loginService.getCustomer(filterData).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customerList[0].id);

        this.deviceData = {
          username: response.customerList[0].username,
          password: response.customerList[0].username,
        };

        const url = "/customers/" + response.customerList[0].id;
        console.log("enter in customer is found :::", this.renewPlan);
        this.loginService.getCustomerById(url).subscribe((res: any) => {
          if (response.status == 200) {
            this.deviceData = {
              username: res.customers.username,
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
        this.createAdCustomerBSS();
        this.spinner.hide();
      }
    );
  }
  createAdCustomerBSS() {
    this.spinner.show();
    let createCustRequest = { ...this.createCustomerPojo };
    createCustRequest.username = this.addLoginForm.controls.firstname.value;
    createCustRequest.password = this.addLoginForm.controls.firstname.value;
    createCustRequest.firstname = this.addLoginForm.controls.firstname.value;
    createCustRequest.lastname = this.addLoginForm.controls.lastname.value;
    let mobileNumber: string = "98989898984568";
    if (mobileNumber.length <= this.mobileNumberLength) {
      createCustRequest.mobile = mobileNumber;
    } else {
      createCustRequest.mobile = mobileNumber.substring(
        0,
        this.mobileNumberLength
      );
    }
    createCustRequest.email = "cf@gmail.com";
    this.loginService.createCustomerBSS(createCustRequest).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customer.id);
        this.spinner.hide();
        this.deviceData = {
          username: response.customer.username,
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

  onRating() {
    this.displayRating = true;
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
          this.isRatingPresent = true;
          this.spinner.hide();
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
          this.ratingForm.reset();
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
  logoutUrl() {
    this.spinner.hide();
    const logoutUrl = window.location.origin + "/#/logout";
    console.log("logoutUrl :::", logoutUrl);
    window.open(logoutUrl, "_blank");
  }

  openQrCode() {
    this.generatedUUID = uuid.v4();
    this.qrCodeContent = this.generatedUUID;
    console.log("entire expire q");
    this.isQrCodeopen = true;
    this.checkqrcode();
    this.savecode();
  }

  checkqrcode() {
    this.subscription1 = this.obs1$.subscribe((d) => {
      if (this.isQrStatusCall === true) {
        if (this.qrcodeCount > 0) {
          this.qrcodeCount = this.qrcodeCount - 1;
          if (this.storedUUID?.code === this.generatedUUID) {
            let data = {
              username: this.storedUUID.username,
              password: this.storedUUID.password,
            };
            this.deviceData = {
              username: this.storedUUID.username,
              password: this.storedUUID.password,
            };
            this.checkqrCustomer(this.storedUUID.username);
            this.subscription1.unsubscribe();
            this.activeIndex = 1;
            this.qrcodeCount = 180;
          }
        }
        if (this.qrcodeCount == 0) {
          if (this.isQrCodeopen == true) {
            this.expireqrcode();
          }
          this.subscription1.unsubscribe();
        }
      }
    });
  }

  checkqrcodeapi() {
    this.spinner.hide();
    let data = {
      code: this.generatedUUID,
    };

    this.loginService.getTransactionstatus(data).subscribe(
      (response: any) => {
        console.log("transactionresponse:::", response);
        if (response.status == 200) {
          if (response.qrstatus === true) {
            this.qrResponse = response.qrresponse;
            this.isqrvalid = response.qrstatus;
          }
        }
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

  savecode() {
    let data = { code: this.generatedUUID };
    this.loginService.saveqrcode(data).subscribe(
      (response: any) => {},
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
  getFormattedTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    const minutesString = minutes < 10 ? `0${minutes}` : `${minutes}`;
    const secondsString =
      remainingSeconds < 10 ? `0${remainingSeconds}` : `${remainingSeconds}`;

    return `${minutesString}:${secondsString}`;
  }

  expireqrcode() {
    let data = { code: this.generatedUUID };
    this.loginService.expireqrcode(data).subscribe(
      (response: any) => {
        this.generatedUUID = uuid.v4();
        this.qrCodeContent = this.generatedUUID;
        this.qrcodeCount = 180;
        this.savecode();
        this.checkqrcode();
        this.isQrCodeopen = true;
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
  hideForgotModal() {
    if (this.subscription1) {
      this.subscription1.unsubscribe();
    }
    this.activeIndex = 1;
    this.qrcodeCount = 180;
  }
  initFacebookLogin() {
    // FB.getLoginStatus(({ authResponse }) => {
    // if (authResponse) {
    //   this.messageService.add({
    //     severity: "success",
    //     summary: "Successfully",
    //     detail: "Already login",
    //     icon: "far fa-check-circle",
    //   });
    // } else {
    FB.login(
      (response: any) => {
        if (response.authResponse) {
          FB.api(
            "/me",
            {
              fields: "email,name",
            },
            (userInfo) => {
              console.log("userInfo :::: ", userInfo);
              // this.createCustomerPojo.email = "test_fb_6@gmail.com";
              // this.createLoginForm.patchValue({
              //   userName: "test_fb_6@gmail.com",
              // });
              // this.checkCustomerBSSWIthEmail();
              this.checkFacebookCustomerBSS(userInfo.email, userInfo.name);
            }
          );
        } else {
          console.log("User cancelled login or did not fully authorize.");
        }
      },
      { scope: "email" }
    );
    // }
    // });
  }
  checkqrCustomer(value: any) {
    let filterData = {
      filters: [
        {
          filterColumn: "usernameequalto",
          filterCondition: "and",
          filterDataType: "string",
          filterOperator: "equalto",
          filterValue: value,
        },
      ],
    };
    this.loginService.getCustomer(filterData).subscribe(
      (response: any) => {
        localStorage.setItem("customerId", response.customerList[0].id);

        const url = "/customers/" + response.customerList[0].id;
        this.loginService.getCustomerById(url).subscribe((response: any) => {
          if (response.status == 200) {
            this.spinner.hide();
            let currentPlanList = [];
            let customerPlan = response.customers.planMappingList;
            this.deviceData = {
              username: response.customers.username,
              password: response.customers.password,
            };
            if (customerPlan.length > 0) {
              this.CheckPlanQuota(customerPlan, currentPlanList);
              this.qrResponse = "";
              this.isqrvalid = false;
            }
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

        this.spinner.hide();
      },
      (error: any) => {
        // this.createCustomerBSS();
        this.spinner.hide();
        //      this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //      detail: error.error.msg,
        //     icon: "far fa-times-circle",
        // });
      }
    );
  }
  webSocketConnect() {
    let webSocketUrl = new URL(RadiusConstants.savbill_COMMON_BASE_URL).origin;
    webSocketUrl = webSocketUrl + "/websocket-endpoint/";
    console.log("webSocketUrl :::", webSocketUrl);
    let websocket = new SockJS(webSocketUrl);
    this.stompClient = Stomp.over(websocket);
    this.connect = this.stompClient.connect({}, (frame) => {
      this.connect = this.stompClient.subscribe("/topic/qr", (messages) => {
        const data = JSON.parse(messages.body);
        this.storedUUID = data;
      });
    });
  }
  vastClient() {
    this.videoVisible = true;
    const vastClient = new VASTClient();

    vastClient
      .get(RadiusConstants.VAST_URL)
      .then((res) => {
        console.log("res ::::", res);
        const ad = res.ads[0];
        const creative = res.ads[0].creatives[0];
        this.videoUrl = res.ads[0].creatives[0].mediaFiles[0].fileURL;
        console.log("videoUrl ::", this.videoUrl);
        const vastTracker = new VASTTracker(vastClient, ad, creative);

        // Track an impression for the given ad
        vastTracker.trackImpression();
        // Do something with the parsed VAST response
      })
      .catch((err) => {
        // Deal with the error
      });
  }

  changePlan(customer: any, plan: any) {
    let changePlanPojos = [];
    let customerId = customer.id;
    let planId = plan.id;
    let planMappingList = customer.planMappingList;
    this.changePlanRequest.custId = customerId;
    this.changePlanRequest.deactivatePlanReqModels[0].newPlanId = planId;
    this.changePlanRequest.deactivatePlanReqModels[0].custServiceMappingId =
      planMappingList[0].custServiceMappingId;
    this.changePlanRequest.paymentOwnerId = planMappingList[0].createdById;
    this.changePlanRequest.billableCustomerId = customerId;

    changePlanPojos.push(this.changePlanRequest);
    let data = {
      deactivatePlanReqDTOS: changePlanPojos,
      recordPayment: null,
    };
    this.loginService.changePlan(data).subscribe(
      (response: any) => {
        this.messageService.add({
          severity: "success",
          summary: "Success",
          detail: "plan change successfully",
          icon: "far fa-times-circle",
        });
      },
      (error: any) => {
        if (error.error.status === 417) {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: error.error.ERROR,
            icon: "far fa-times-circle",
          });
        }
      }
    );
  }

  getAllServiceByServiceAreaId() {
    let data = [];
    data.push(SA_ID);
    let url = "/serviceArea/getAllServicesByServiceAreaId";
    this.spinner.show();
    this.loginService.postMethod(url, data).subscribe(
      (response: any) => {
        console.log(response);
        this.dataList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  handleBranchAndPartnerData(response) {
    if (response.dataList && response.dataList.length > 0) {
      let branchId = response.dataList[0].id;
      this.createCustomerPojo.branch = branchId;
      this.createCustomerPojo.partnerid = 1;
      console.log("Branch", this.createCustomerPojo.branch);
      console.log("Partner", this.createCustomerPojo.partnerid);
    } else {
      const url = "/getPartnerByServiceAreaId/" + SA_ID;
      this.loginService.getMethod(url).subscribe(
        (partnerResponse: any) => {
          this.partnerListByServiceArea = partnerResponse.partnerList;
          const defaultPartner = this.partnerListByServiceArea.find(
            (partner) => partner.id === "default"
          );
          if (defaultPartner) {
            this.createCustomerPojo.partnerid = defaultPartner.id;
          } else if (this.partnerListByServiceArea.length > 0) {
            this.createCustomerPojo.partnerid =
              this.partnerListByServiceArea[0].id;
          } else {
            this.createCustomerPojo.partnerid = 1;
          }
          this.createCustomerPojo.branch = null;
          console.log("Partner", this.createCustomerPojo.partnerid);
          console.log("Branch", this.createCustomerPojo.branch);
        },
        (error: any) => {}
      );
    }
    this.spinner.hide();
  }

  getAllBranchesByServiceAreaId() {
    let data = [];
    data.push(SA_ID);
    let url = "/branchManagement/getAllBranchesByServiceAreaId";
    this.loginService.getBranchByServiceId(url, data).subscribe(
      (response: any) => {
        console.log(response);
        this.handleBranchAndPartnerData(response);
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }
  buyPlanWithRazorpay(plan: any) {
    this.displayDialog = false;
    this.selRenewPlanData = plan;
    this.intiateRazorpay(
      plan.offerprice,
      this.checkoutCust.id,
      this.selRenewPlanData.id
    );
  }
  intiateRazorpay(payAmount, custId, planId) {
    let data;
    data = {
      custId: custId,
      payment: payAmount,
      status: "Initiate",
      planId: planId,
      isFromCaptive: true,
    };
    this.loginService.addPayment(data).subscribe(
      (response: any) => {
        this.orderId = response.CustomerPayment.orderId;
        this.razorPay(this.orderId, payAmount);
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
  razorPay(orderId: any, payAmount: any) {
    console.log("this.paymentkeyValuePairs", this.paymentkeyValuePairs);
    const RAZORPAY_CALLBACK_URL_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_CALLBACK_URL];
    const RAZORPAY_CURRENCY_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_CURRENCY];
    const RAZORPAY_KEY_ID_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_KEY_ID];
    const RAZORPAY_SECRET_KEY_VALUE =
      this.paymentkeyValuePairs[RAZORPAY_CONSTANT.RAZORPAY_SECRET_KEY];
    let callback = RAZORPAY_CALLBACK_URL_VALUE;
    console.log("callback :::", callback);
    const razorpayOption = {
      description: "savbill Nettech",
      currency: RAZORPAY_CURRENCY_VALUE,
      amount: Number(payAmount) * 100,
      name: "savbill Nettech",
      key: RAZORPAY_KEY_ID_VALUE,
      secretKey: RAZORPAY_SECRET_KEY_VALUE,
      handler: (response: any) => {
        if (response != null && response.razorpay_payment_id != null) {
          this.processresponse(response, orderId);
        } else {
          this.messageService.add({
            severity: "error",
            summary: "Error",
            detail: "Payment Failed from razorpay",
            icon: "far fa-times-circle",
          });
        }
      },
      prefill: {
        name: this.checkoutCust.username,
        email: this.checkoutCust.email,
        mobileNo: this.checkoutCust.mobile,
      },
      theme: {
        color: "#f7b206",
      },
      modal: {
        ondismiss: () => {
          console.log("dismissed");
        },
      },
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
      pgTransactionId: res.razorpay_payment_id,
    };
    this.razaopaycallback(callbackurl, data);
  }
  razaopaycallback(callbackurl: any, data: any) {
    this.spinner.hide();
    this.loginService.razorpaycallback(callbackurl, data).subscribe(
      (response: any) => {
        console.log("in response of razorpay callback");
        this.messageService.add({
          severity: "success",
          summary: "Success",
          detail: "plan buy successfully",
          icon: "far fa-times-circle",
        });
        this.razorpayCallbackResponse = response.callbackResponse;
        this.razopayredirectTimeInSeconds = parseInt(
          this.razorpayCallbackResponse.redirectTimeInSecond
        );
        if (
          typeof this.razorpayCallbackResponse.redirectTimeInSecond ===
            "number" &&
          !isNaN(this.razorpayCallbackResponse.redirectTimeInSecond)
        ) {
          this.razopayredirectTimeInSeconds = parseInt(
            this.razorpayCallbackResponse.redirectTimeInSecond
          );
        } else {
          // Handle the case where redirectTimeInSecond is not a valid number
          console.error(
            "Invalid redirectTimeInSecond:",
            this.razorpayCallbackResponse.redirectTimeInSecond
          );
          // You may want to set a default value or handle this case differently
        }

        this.ispaymentReceiptHtml = true;
        this.cdr.detectChanges();
        var self = this;
        var downloadTimer = setInterval(function () {
          if (self.razopayredirectTimeInSeconds <= 0) {
            clearInterval(downloadTimer);
            document.getElementById("home").click();
          } else {
            document.getElementById("countdown").innerHTML =
              "Redirecting to Home in " +
              self.razopayredirectTimeInSeconds +
              " seconds";
          }
          self.razopayredirectTimeInSeconds -= 1;
        }, 1000);
      },
      (error: any) => {
        this.messageService.add({
          severity: "error",
          summary: "Error",
          detail: "Something went wrong",
          icon: "far fa-times-circle",
        });
      }
    );
  }

  checkFacebookCustomerBSS(username: any, name: any) {
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
          username: response.customerList[0].username,
          password: response.customerList[0].username,
        };

        const url = "/customers/" + response.customerList[0].id;
        console.log("enter in customer is found :::", this.renewPlan);
        this.loginService.getCustomerById(url).subscribe((res: any) => {
          if (response.status == 200) {
            this.deviceData = {
              username: res.customers.username,
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
        this.createFacebookCustomerBSS(username, name);
        this.spinner.hide();
      }
    );
  }

  createFacebookCustomerBSS(username: any, name: any) {
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
          username: response.customer.username,
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
  loginWithTwitter(): void {
    this.spinner.show();
    window.location.href = RadiusConstants.SOCIAL_LOGIN_URL;
  }
}