import { Component, ElementRef, OnInit } from "@angular/core";
import { LoginService } from "src/app/service/login.service";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService } from "primeng/api";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { interval } from "rxjs";
@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"],
})
export class DashboardComponent implements OnInit {
  userName: any;
  customerId: any;
  displayedColumns: string[] = ["key", "value"];
  customerDetails: any;
  captivePortalUser = {
    userName: "captiveportal",
    password: "captiveportal",
  };
  logouturl: any;
  constructor(
    private elementRef: ElementRef,
    private router: Router,
    private loginService: LoginService,
    private spinner: NgxSpinnerService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.customerId = localStorage.getItem("customerId");
    this.logouturl = localStorage.getItem("logOut");
    this.userName = localStorage.getItem("userName");
    this.getDetails();
    this.elementRef.nativeElement.ownerDocument.body.style.backgroundColor =
      "rgba(244, 203, 23, 1)";
  }

  async getDetails() {
    this.spinner.show();
    this.loginService.getPlanDetails(this.customerId).subscribe(
      (response: any) => {
        this.customerDetails = response.customerPlanDetail;
        console.log(this.customerDetails);
        this.spinner.hide();
      },
      (error) => {
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
  async logoutCustomer() {
    this.spinner.show();
    this.loginService.logOutCustomer(this.userName).subscribe(
      (response) => {
        window.open(this.logouturl, "/home");
        this.router.navigate(["/portal"]);
        this.spinner.hide();
      },
      (error) => {
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
}
