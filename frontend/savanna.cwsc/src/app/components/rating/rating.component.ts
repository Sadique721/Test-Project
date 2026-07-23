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
import { NgxStarRatingModule } from "ngx-star-rating";

@Component({
  selector: "app-rating",
  templateUrl: "./rating.component.html",
  styleUrls: ["./rating.component.css"],
  standalone: true,
  imports: [SharedModule, NgxStarRatingModule],
  providers: [DatePipe]
})
export class RatingComponent implements OnInit {
  customerID: any;

  ratingForm: FormGroup;

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
    this.ratingForm = this.fb.group({
      rating: [, Validators.required],
      feedback: [""],
      custId: [""],
      id: [""]
    });
    this.customerID = this.commondropdownService.getUserId();
    this.getCustomersDetail();
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

  isEditing: boolean = false;
  isEditRating: boolean = false;
  isRatingError: boolean = false;
  isRatingPresent: boolean = false;
  customerFeedback: any;

  saveRating() {
    this.isRatingError = true;
    if (this.ratingForm.valid) {
      var request = this.ratingForm.value;
      request.custId = this.customerLedgerDetailData.id;
      this.customerFeedbackService.saveCustomerFeedback(request).subscribe(
        (response: any) => {
          this.toastr.success(`${response.message}`, "Success!");

          this.isRatingPresent = true;
          this.spinner.hide();
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
        },
        (error: any) => {
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  editRating() {
    this.isEditRating = true;
  }

  cancelEditRating() {
    this.isEditRating = false;
    if (this.customerFeedback) this.ratingForm.patchValue(this.customerFeedback);
  }

  getFeedbackBuCustomer() {
    this.spinner.show();
    this.customerFeedbackService.getCustomerFeedback(this.customerLedgerDetailData.id).subscribe(
      (response: any) => {
        if (response.status == 200) {
          this.isRatingPresent = true;
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
        }
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  updateRating() {
    this.isRatingError = true;
    if (this.ratingForm.valid) {
      var request = this.ratingForm.value;
      request.custId = this.customerLedgerDetailData.id;
      this.customerFeedbackService.updateCustomerFeedback(request).subscribe(
        (response: any) => {
          this.toastr.success(`${response.message}`, "Success!");

          this.isRatingPresent = true;
          this.isEditRating = false;
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
          this.spinner.hide();
        },
        (error: any) => {
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }
}
