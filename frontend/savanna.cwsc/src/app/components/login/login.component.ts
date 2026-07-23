import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { AuthenticationService } from "src/app/service/authentication.service";
import { LoginService } from "src/app/service/login.service";
import { MessageService } from "primeng/api";
import {
  UntypedFormBuilder,
  Validators,
  UntypedFormGroup,
  AbstractControl,
  ValidationErrors
} from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { interval, Subscription } from "rxjs";
import { countries } from "../model/country";
import * as uuid from "uuid";
import { TITLE } from "../../RadiusUtils/RadiusConstants";
import { Title } from "@angular/platform-browser";
import { SharedModule } from "src/app/shared/shared.module";
import * as CryptoJS from "crypto-js";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MatInputModule } from "@angular/material/input";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
  standalone: true,
  imports: [SharedModule, MatInputModule]
})
export class LoginComponent implements OnInit {
  invalidLogin = false;
  createLoginForm: UntypedFormGroup;
  submitted = false;
  loginUserName: any;
  UserName = RadiusConstants.StaffUsername;
  Password = RadiusConstants.StaffPassword;
  isForgotPassword: boolean = false;
  forgotForm!: UntypedFormGroup;
  countries: any = countries;
  isChangePasswordBtnShow: boolean = false;
  isProceedBtnShow: boolean = false;
  isResendBtnDisable: boolean = false;
  isResendBtnShow: boolean = false;
  SAVBILL_OTP_PROFILE = "OTP";
  isForgotSubmitted: boolean = false;
  sendOtpCountDown = 30;
  sendOtpSubscription: Subscription;
  sendOTPobs$ = interval(1000);
  facebookLink = RadiusConstants.FACEBOOK_LINK;
  twitterLink = RadiusConstants.TWITTER_LINK;
  linkedinLink = RadiusConstants.LINKEDIN_LINK;
  generatedUUID: string = uuid.v4();
  qrCodeContent: string = "";
  customer: any;
  isQrCodeopen: boolean = false;
  stompClient: any;
  connect: any;
  messages: any;
  isQrStatusCall: boolean = true;
  obs1$ = interval(1000);
  subscription1: Subscription;
  qrcodeCount = RadiusConstants.QR_COUNT;
  isqrvalid: boolean = false;
  qrResponse: any;
  storedUUID: any;
  captchaStatus: any = "";
  showOtpInput: boolean = false;
  secretKey = RadiusConstants.SECRET_KEY;

  showPassword = false;
  _passwordType = "password";

  captchaConfig: any = {
    type: 1,
    length: 6,
    cssClass: "captcha",
    back: {
      stroke: "#f7b206"
    },
    font: {
      color: "#000000",
      size: "35px"
    }
  };
  loginEnable: boolean = RadiusConstants.LOGIN_CAPTCHA === "false" ? false : true;
  captchaError: any;
  captchaSuccess: any;
  constructor(
    private router: Router,
    private fb: UntypedFormBuilder,
    private authservice: AuthenticationService,
    private loginService: LoginService,
    private commonDropdownService:CommondropdownService,
    private messageService: MessageService,
    private spinner: NgxSpinnerService,private toastr: ToastrService,
    private titleService: Title,
    
  ) {
   
  }

  ngOnInit(): void {
    this.loginEnable = RadiusConstants.LOGIN_CAPTCHA.toLowerCase() === "false" ? false : true;
    this.captchaSuccess = "";
    this.captchaError = "";
    this.titleService.setTitle(TITLE);
    const obs$ = interval(15 * 60 * 1000);
    obs$.subscribe(d => {
      this.loginService.refreshToken();
    });
    this.staffLogin();
    localStorage.setItem("hostName", window.location.hostname);
    this.createLoginForm = this.fb.group({
      username: ["", Validators.required],
      password: ["", Validators.required]
    });

    this.forgotForm = this.fb.group({
      userName: ["", [Validators.required]],
      otp: [""]
    });
    this.webSocketConnect();
  }

  staffLogin() {
    let data = {
      username: this.UserName,
      password: this.Password
    };
    this.loginService.generateToken(data).subscribe((response: any) => {
      this.loginService.loginUser(response.accessToken);
      this.loginService.getAclEntry();
    });
  }

  async getDemoGraphic() {
    this.spinner.show();

    this.loginService.demographic().subscribe(
      (response: any) => {
        localStorage.setItem("demographic", JSON.stringify(response.demographicmappingtable));
        RadiusConstants.masterdata(response.demographicmappingtable);
        this.spinner.hide();
      },
      (error: any) => {
        if (error.error.status == 404) {
        } else {
          this.toastr.error(`${error.error.msg}`, "Failed!");
        }
        this.spinner.hide();
      }
    );
  }

  async checkLogin() {
    this.submitted = true;
    // if (this.captchaSuccess === "" && this.loginEnable) {
    //   this.captchaError = "Please verify the captcha.";
    //   return;
    // }

    if (this.createLoginForm.valid) {
      this.spinner.show();
      this.loginService.postlogin(this.createLoginForm.value).subscribe(
        (response: any) => {
          const timestamp = Date.now();
          localStorage.setItem("loggedInUser", response.fistName);
          // localStorage.setItem("userId", response.userId + "|" + timestamp);
          // const calculatedHash = CryptoJS.enc.Base64.stringify(
          //   CryptoJS.HmacSHA256(
          //     localStorage.getItem("userId"),
          //     CryptoJS.enc.Utf8.parse(this.secretKey)
          //   )
          // );
          // localStorage.setItem("userIdH", calculatedHash);
          // localStorage.setItem("mvnoId", response.mvnoId);
          const userIdValue = response.userId + "|" + timestamp;
          const mvnoIdValue = response.mvnoId + "|" + timestamp;
          // Keys
          const userIdKey = this.commonDropdownService.createHash("userId");
          const userIdHashKey = this.commonDropdownService.createHash("userIdH");
          const mvnoIdKey = this.commonDropdownService.createHash("mvnoId");
          const mvnoIdHashKey = this.commonDropdownService.createHash("mvnoIdH");
          // Hashed Values
          const userIdHash = this.commonDropdownService.createHash(userIdValue);
          const mvnoIdHash = this.commonDropdownService.createHash(mvnoIdValue);
          // Set into localStorage
          localStorage.setItem(userIdKey, userIdValue);
          localStorage.setItem(userIdHashKey, userIdHash);
          localStorage.setItem(mvnoIdKey, mvnoIdValue);
          localStorage.setItem(mvnoIdHashKey, mvnoIdHash);
          this.isQrStatusCall = false;
          // this.subscription1.unsubscribe();
          this.getDemoGraphic();
          this.getCurrencySymbol();
          setTimeout(() => {
            this.router.navigate(["/home/dashboard"]);
            this.spinner.hide();
          }, 1000);
        },
        (error: any) => {
          localStorage.removeItem("loggedInUser");
          if(error.error){
              this.toastr.error(`${error.error.message}`, "Failed!");

          }
          this.spinner.hide();
          this.router.navigate(["/login"]);
        }
      );
    }
  }

  forgotPasswordClick() {
    this.isForgotPassword = true;
    this.isProceedBtnShow = true;
    this.forgotForm.controls.otp.disable();
  }

  loginCheck() {
    this.isForgotSubmitted = true;
    if (this.forgotForm.valid) {
      this.checkCustomerExists(this.forgotForm.value);
    }
  }

  checkCustomerExists(data) {
    this.spinner.show();
    const url = "/customer/customerUsernameIsAlreadyExists/" + data.userName;
    this.loginService.cmsGetMethod(url).subscribe(
      (response: any) => {
        if (response.status == 200) {
          if (response.isAlreadyExists == true) {
            this.spinner.hide();
            this.customer = response.customer;
            this.generateOTP();
            this.isChangePasswordBtnShow = true;
          } else {
            this.spinner.hide();

            this.toastr.error(`User not found`, "Failed!");
          }
        } else {
          this.toastr.error(`${response.message}`, "Failed!");
        }
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  openQrCode() {
    this.isQrCodeopen = true;
    this.generatedUUID = uuid.v4();
    this.qrCodeContent = this.generatedUUID;
    this.checkqrcode();
    this.savecode();
  }

  ngOnDestroy() {
    if (this.sendOtpSubscription) {
      this.sendOtpSubscription.unsubscribe();
    }
  }

  generateOTP() {
    this.sendOtpCountDown = 30;
    this.isForgotSubmitted = false;
    this.forgotForm.controls.otp.enable();
    this.forgotForm.controls.otp.setValidators(Validators.required);
    this.forgotForm.updateValueAndValidity();
    this.isProceedBtnShow = false;
    this.isResendBtnDisable = true;
    this.isResendBtnShow = true;
    const data = this.forgotForm.value;
    data.mobileNumber = this.customer.mobile;
    data.profile = this.SAVBILL_OTP_PROFILE;
    data.emailId = this.customer.email;
    this.loginService.getOTPBSS(data).subscribe(
      (response: any) => {
        if (response.status == 200) {
          this.toastr.success(`${response.otp}`, "Success!");

          this.sendOtpSubscription = this.sendOTPobs$.subscribe(e => {
            if (this.sendOtpCountDown > 0) {
              this.sendOtpCountDown = this.sendOtpCountDown - 1;
            }
            if (this.sendOtpCountDown == 0) {
              this.isResendBtnDisable = false;
              this.sendOtpSubscription.unsubscribe();
            }
          });
        } else {
          this.toastr.error(`${response.message}`, "Failed!");
        }
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  hideForgotModal() {
    this.forgotForm.reset();
    this.isForgotPassword = false;
    this.isForgotSubmitted = false;
    this.isResendBtnShow = false;
    this.isChangePasswordBtnShow = false;
    this.forgotForm.controls.otp.clearAsyncValidators();
    this.forgotForm.controls.otp.clearValidators();
    this.forgotForm.updateValueAndValidity();
    if (this.sendOtpSubscription) {
      this.sendOtpSubscription.unsubscribe();
    }
    if (this.subscription1) {
      this.subscription1.unsubscribe();
    }
    this.sendOtpCountDown = 30;
    this.qrcodeCount = RadiusConstants.QR_COUNT;
  }

  changePassword() {
    this.isForgotSubmitted = true;
    if (this.forgotForm.valid) {
      this.spinner.show();
      const data = this.forgotForm.value;
      data.mobileNumber = this.customer.mobile;
      data.emailId = this.customer.email;

      this.loginService.validateOTPBSS(data).subscribe(
        (response: any) => {
          this.spinner.hide();
          if (response.status == 200) {
            const updateData = {
              custId: this.customer.id,
              newpassword: this.forgotForm.value.otp,
              password: this.forgotForm.value.otp,
              remarks: "",
              selfcarepwd: this.forgotForm.value.otp
            };
            const url = "/updatePassword";
            this.loginService.portalUpdatePassword(url, updateData).subscribe(
              (response: any) => {
                if (response.responseCode == 200) {
                  this.isForgotSubmitted = false;
                  this.forgotForm.controls.otp.disable();
                  this.isChangePasswordBtnShow = false;
                  this.isResendBtnShow = false;
                  this.hideForgotModal();
                  this.spinner.hide();

                  this.toastr.success(
                    `Your Password has been reset as same as given OTP.`,
                    "Success!"
                  );
                } else {
                  this.toastr.error(`${response.ERROR}`, "Failed!");

                  this.spinner.hide();
                }
              },
              (error: any) => {
                this.toastr.error(`${error.error.msg}`, "Failed!");
                this.spinner.hide();
              }
            );
          } else {
            this.toastr.error(`${response.otp}`, "Failed!");
          }
        },
        (error: any) => {
          this.toastr.error(`${error.error.msg}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  facebookClick() {
    window.open(this.facebookLink, "_blank");
  }

  twitterClick() {
    window.open(this.twitterLink, "_blank");
  }

  linkedinClick() {
    window.open(this.linkedinLink, "_blank");
  }

  getCurrencySymbol() {
    this.loginService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
      localStorage.setItem("CURRENCY_SYMBOL", res.data.value);
    });
  }
  checkqrcode() {
    this.subscription1 = this.obs1$.subscribe(d => {
      if (this.isQrStatusCall === true) {
        if (this.qrcodeCount > 0) {
          this.qrcodeCount = this.qrcodeCount - 1;
          // this.checkqrcodeapi();
          if (this.storedUUID?.code === this.generatedUUID) {
            let data = {
              username: this.storedUUID.username,
              password: this.storedUUID.password
            };
            this.loginService.postlogin(data).subscribe(
              (response: any) => {
                const timestamp = Date.now();
                localStorage.setItem("loggedInUser", response.fistName);
                // localStorage.setItem("userId",
                //   response.userId + "|" + timestamp
                // );
                // const userIdHash = this.commonDropdownService.createHash(response.userId + timestamp);
                // localStorage.setItem("userIdH", userIdHash);
                // localStorage.setItem("mvnoId", response.mvnoId + "|" + timestamp);
                // const mvnoIdHash = this.commonDropdownService.createHash(response.mvnoId + timestamp);
                // localStorage.setItem("mvnoIdH", mvnoIdHash);
                const userIdValue = response.userId + "|" + timestamp;
                const mvnoIdValue = response.mvnoId + "|" + timestamp;

                // Keys
                const userIdKey = this.commonDropdownService.createHash("userId");
                const userIdHashKey = this.commonDropdownService.createHash("userIdH");

                const mvnoIdKey = this.commonDropdownService.createHash("mvnoId");
                const mvnoIdHashKey = this.commonDropdownService.createHash("mvnoIdH");

                // Hashed Values
                const userIdHash = this.commonDropdownService.createHash(userIdValue);
                const mvnoIdHash = this.commonDropdownService.createHash(mvnoIdValue);

                // Set into localStorage
                localStorage.setItem(userIdKey, userIdValue);
                localStorage.setItem(userIdHashKey, userIdHash);

                localStorage.setItem(mvnoIdKey, mvnoIdValue);
                localStorage.setItem(mvnoIdHashKey, mvnoIdHash);
                this.isQrStatusCall = false;
                this.subscription1.unsubscribe();
                this.getDemoGraphic();
                this.getCurrencySymbol();
                setTimeout(() => {
                  this.router.navigate(["/home/dashboard"]);
                  this.spinner.hide();
                }, 1000);
              },
              (error: any) => {
                localStorage.removeItem("loggedInUser");
                this.toastr.error(`Try to Login With Correct Credentials!`, "Failed!");
                this.spinner.hide();
                this.router.navigate(["/login"]);
              }
            );
            this.subscription1.unsubscribe();
          }
        }
        if (this.qrcodeCount == 0) {
          this.expireqrcode();
          this.subscription1.unsubscribe();
        }
      }
    });
  }
  checkqrcodeapi() {
    this.spinner.hide();
    let data = {
      code: this.generatedUUID
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

        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }
  savecode() {
    let data = { code: this.generatedUUID };
    this.loginService.saveqrcode(data).subscribe(
      (response: any) => {},
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  webSocketConnect() {
    // let webSocketUrl = new URL(RadiusConstants.SAVBILL_COMMON_BASE_URL).origin;
    // webSocketUrl = webSocketUrl + "/websocket-endpoint/";
    // let websocket = new SockJS(webSocketUrl);
    // this.stompClient = Stomp.over(websocket);
    // this.connect = this.stompClient.connect({}, (frame) => {
    //   this.connect = this.stompClient.subscribe("/topic/qr", (messages) => {
    //     const data = JSON.parse(messages.body);
    //     this.storedUUID = data;
    //   });
    // });
  }

  expireqrcode() {
    let data = { code: this.generatedUUID };
    this.loginService.expireqrcode(data).subscribe(
      (response: any) => {
        this.generatedUUID = uuid.v4();
        this.qrCodeContent = this.generatedUUID;
        this.qrcodeCount = RadiusConstants.QR_COUNT;
        this.savecode();
        this.checkqrcode();
        this.isQrCodeopen = true;
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  getFormattedTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const minutesString = minutes < 10 ? `0${minutes}` : `${minutes}`;
    const secondsString = remainingSeconds < 10 ? `0${remainingSeconds}` : `${remainingSeconds}`;
    return `${minutesString}:${secondsString}`;
  }

  onPasswordKeyPress(event: KeyboardEvent): void {
    if (event.key === " ") {
      event.preventDefault();
      const passwordControl = this.createLoginForm.controls["password"];
      passwordControl.setErrors({ noSpace: true });
      passwordControl.markAsTouched();
    }
  }
}
