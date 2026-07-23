import { DatePipe } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { SharedModule } from "src/app/shared/shared.module";
import {
  FormBuilder,
  Validators,
  FormGroup,
  FormControl,
  FormArray,
  AbstractControl
} from "@angular/forms";
import { ToastrService } from "ngx-toastr";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { CustomerFeedbackService } from "src/app/service/customerfeedback.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";

@Component({
  selector: "app-changePassword",
  templateUrl: "./changePassword.component.html",
  styleUrls: ["./changePassword.component.css"],
  standalone: true,
  imports: [SharedModule],
  providers: [DatePipe]
})
export class ChangePasswordComponent implements OnInit {
  changePasswordForm: FormGroup;
  changePasswordvalue: any;
  customerID: any;
  showOld = false;
  showNew = false;
  showConfirm = false;

  _passwordType = "password";
  _passwordType1 = "password";
  _passwordType2 = "password";
  constructor(
    private fb: FormBuilder,
    // private messageService: MessageService,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    public customerdetailsilsService: CustomerdetailsilsService,

    public datepipe: DatePipe,
    public statusCheckService: StatusCheckService,
    public customerFeedbackService: CustomerFeedbackService,
    public commondropdownService: CommondropdownService
  ) {}

  ngOnInit() {
    this.customerID = this.commondropdownService.getUserId();
    this.changePasswordForm = this.fb.group({
      custId: [this.customerID],
      newpassword: ["", Validators.required],
      confirmNewPassword: ["", Validators.required],
      password: ["", Validators.required],
      remarks: [""],
      selfcarepwd: [""]
    });
  }
  createcustomerData: any = [];
  customerLedgerDetailData: any = [];
  getCustomersDetail() {
    this.spinner.show();
    const url = "/customers/" + this.customerID;
    this.customerdetailsilsService.getMethod(url).subscribe((response: any) => {
      this.createcustomerData = { ...response.customers };
      this.customerLedgerDetailData = { ...response.customers };
      this.spinner.hide();
    });
  }
  //change Password
  changePassword() {
    let url = "/updatePassword";
    //
    // this.changeSubmitted = false;
    this.spinner.show();

    if (this.changePasswordForm.valid) {
      // if (this.changePasswordForm.value.newpassword === this.changePasswordForm.value.selfcarepwd) {
      const newPassword = this.changePasswordForm.value.newpassword;
      const confirmPassword = this.changePasswordForm.value.confirmNewPassword;
      const oldPassword = this.changePasswordForm.value.password;
      if (newPassword !== oldPassword) {
        if (newPassword === confirmPassword) {
          this.changePasswordForm.value.id = this.customerLedgerDetailData.id;
          this.changePasswordForm.value.remarks = "";
          this.changePasswordForm.value.selfcarepwd = this.changePasswordForm.value.newpassword;
          this.changePasswordvalue = this.changePasswordForm.value;

          this.customerdetailsilsService
            .postSubscriberMethod(url, this.changePasswordvalue)
            .subscribe(
              (response: any) => {
                // this.changeSubmitted = true;
                this.spinner.hide();
                if (response.responseCode == 406) {

                  this.toastr.error(`${response.responseMessage}`, "Failed!");
                } else {
                  this.toastr.success(`Password Update Successfully`, "Success!");
                  this.clearChangePasswordForm();
                  // this.closebutton.nativeElement.click();
                }
              },
              (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
              }
            );
          // }
        } else {
          this.toastr.error(`New Password and Confirm Password should be the same`, "Failed!");

          this.spinner.hide();
        }
      } else {
        this.toastr.error(`New Password should be different from the Old Password`, "Failed!");

        this.spinner.hide();
      }
    }
  }

  clearChangePasswordForm() {
    this.changePasswordForm.reset();
  }
}
