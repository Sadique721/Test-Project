import { DatePipe } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { SharedModule } from "src/app/shared/shared.module";
import { ToastrService } from "ngx-toastr";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { CustomerFeedbackService } from "src/app/service/customerfeedback.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { NgxStarRatingModule } from "ngx-star-rating";
import { FormBuilder } from "@angular/forms";

@Component({
  selector: "app-wallet",
  templateUrl: "./wallet.component.html",
  styleUrls: ["./wallet.component.css"],
  standalone: true,
  imports: [SharedModule],
  providers: [DatePipe]
})
export class WalletComponent implements OnInit {
  customerID: any;
  getWallatData: any = [];
  currency: any;
  currencySymbol = localStorage.getItem("CURRENCY_SYMBOL");
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

    let data = {
      CREATE_DATE: "",
      END_DATE: "",
      amount: "",
      balAmount: "",
      create_DATE: "",
      custId: this.customerID,
      description: "",
      end_DATE: "",
      id: "",
      refNo: "",
      transcategory: "",
      transtype: ""
    };
    const url = "/wallet";
    this.customerdetailsilsService.postRevenueMethod(url, data).subscribe((response: any) => {
      this.getWallatData = response;
    });
    if (!this.currencySymbol) {
      this.getCustomersDetail();
    }
  }
  // createcustomerData: any = [];
  // customerLedgerDetailData: any = [];
  getCustomersDetail() {
    this.spinner.show();
    const url = "/customers/" + this.customerID;
    this.customerdetailsilsService.getMethod(url).subscribe((response: any) => {
      let customerData = { ...response.customers };
      this.currencySymbol = customerData.currency ? customerData.currency : this.currencySymbol;

      this.spinner.hide();
    });
  }
}
