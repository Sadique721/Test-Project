import { Component, OnInit } from "@angular/core";
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
@Component({
  selector: "app-logout",
  templateUrl: "./logout.component.html",
  styleUrls: ["./logout.component.css"],
})
export class LogoutComponent implements OnInit {
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
  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private messageService: MessageService,
    private spinner: NgxSpinnerService,
    private primengConfig: PrimeNGConfig,
    private router: Router,
    private route: ActivatedRoute
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

    this.getCurrentUserActivePlan();
    this.getcustomer();
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
    let urlParams: any = JSON.parse(localStorage.getItem("urlParam"));
    this.spinner.show();
    let request = {
      userName: this.customerData.username,
      password: this.customerData.password,
      name: "mtik",
    };
    if (urlParams && urlParams.name == "mtik") {
      this.loginService.getDeviceDetails(request).subscribe(
        (response: any) => {
          var str = response.message.loginurl;
          console.log("get device data response  :::", response);
          if (response.message.logouturl !== null) {
            document.location.href = response.message.logouturl;
          } else {
            this.customerLogout();
          }
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
      let data = {
        custId: this.customerData.id,
        username: this.customerData.username,
        mvnoId: this.customerData.mvnoId,
      };
      this.loginService.getLogoutCustomer(data).subscribe(
        (response: any) => {
          if (response.device) {
            document.location.href = response.message.logouturl;
          }
          this.messageService.add({
            severity: "success",
            summary: "Success",
            detail: "Customer Logout Successfully",
            icon: "far fa-times-circle",
          });
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

  customerLogout() {
    let data = {
      custId: this.customerData.id,
      username: this.customerData.username,
      mvnoId: this.customerData.mvnoId,
    };
    this.loginService.getCustomerLogout(data).subscribe(
      (response: any) => {
        localStorage.removeItem("customerId");
        // window.close();
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
}
