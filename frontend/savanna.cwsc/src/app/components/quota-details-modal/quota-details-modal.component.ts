import { Component, OnInit, Input } from "@angular/core";
import { Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";import { ToastrService } from "ngx-toastr";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";

@Component({
  selector: "app-quota-details-modal",
  templateUrl: "./quota-details-modal.component.html",
  styleUrls: ["./quota-details-modal.component.css"],
})
export class QuotaDetailsModalComponent implements OnInit {
  @Input() dialogId: string;
  @Input() PlanQuota: Observable<any>;
  custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custQuotaListtotalRecords: String;
  currentPagecustQuotaList = 1;
  custQuotaList: any = [];
  planMappingId: any;
  planData: any;
  custid: any;

  constructor(
    private messageService: MessageService,
    private spinner: NgxSpinnerService,private toastr: ToastrService,
    private customerManagementService: CustomermanagementService,
    private paymentamountService: PaymentamountService
  ) {}

  ngOnInit(): void {
    
    this.PlanQuota.subscribe(value => {
        this.custid = value.custid
        this.planData = value.PlanData
        if(this.custid){
          this.getCustQuotaList(this.custid);
        }
    });}

  getCustQuotaList(custId) {
    this.customerManagementService.getCustQuotaList(custId).subscribe(
      (response: any) => {
        let data = response.custQuotaList
        this.custQuotaList = data.filter((e)=>e.cprId == this.planData.planmapid);
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

  pageChangedCustQuotaList(pageNumber) {
    this.currentPagecustQuotaList = pageNumber;
  }
}
