import { Component, OnInit } from "@angular/core";
import {
  Router,
  RouterEvent,
  RouteConfigLoadStart,
  RouteConfigLoadEnd,
  ActivatedRoute,
} from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { StatusCheckService } from "src/app/service/status-check-service.service";

@Component({
    selector: "app-radius-customer-details-menu",
    templateUrl: "./radius-customer-details-menu.component.html",
    styleUrls: ["./radius-customer-details-menu.component.css"],
    standalone: false
})
export class RadiusCustomerDetailsMenuComponent implements OnInit {
  custId;
  childUrlSegment = "";
  childCustomerDataList: any = {};
  isDetails = false;
  isPlan = false;
  isCDR = false;
    selectedTabIndex = 0;
  constructor(
    private route: ActivatedRoute,
    public savbillCommonBaseService: SavbillCommonBaseService,
    public loginService: LoginService,
    public statusCheckService: StatusCheckService,
    private router: Router
  ) {
      this.custId = this.route.snapshot.firstChild.paramMap.get("custId")!;
        // set active tab based on current route
    const url = this.router.url;
    if (url.includes('/plans/')) {
      this.selectedTabIndex = 1;
    } else if (url.includes('/cdr/')) {
      this.selectedTabIndex = 2;
    } else {
      this.selectedTabIndex = 0; // default to Home
    }

  }
  ngOnInit() {
    this.custId = this.route.snapshot.firstChild.paramMap.get("custId")!;
    this.childUrlSegment = this.route.firstChild.snapshot.url[0].path;
    this.checkOpenMenu(this.childUrlSegment);

// Subscribe to params instead of snapshot (so it updates when switching tabs)
    this.route.firstChild?.params.subscribe((params) => {
      this.custId = params["custId"];
      this.setActiveTab(this.router.url);
    });

   
  }

 checkOpenMenu(childUrl) {
     switch (childUrl) {
      case "x":
          this.isDetails = true;
        break;
        case "plans":
        this.isPlan = true;
        break;
        case "cdr":
            this.isCDR = true;
            break;
        }
    }

     /** Called when tab is clicked */
    onTabChange(index: number) {
      this.selectedTabIndex = index;
      this.resetFlags();
    
      switch (index) {
        case 0:
          this.isDetails = true;
          this.router.navigate(["x", this.custId], { relativeTo: this.route });
          break;
        case 1:
          this.isPlan = true;
          this.router.navigate(["plans", this.custId], { relativeTo: this.route });
          break;
        case 2:
          this.isCDR = true;
          this.router.navigate(["cdr", this.custId], { relativeTo: this.route });
          break;
      }
    }
     /** Set active tab based on current route */
  private setActiveTab(url: string) {
    this.resetFlags();

    if (url.includes("/plans/")) {
      this.selectedTabIndex = 1;
      this.isPlan = true;
    } else if (url.includes("/cdr/")) {
      this.selectedTabIndex = 2;
      this.isCDR = true;
    } else {
      this.selectedTabIndex = 0;
      this.isDetails = true;
    }
  }
   /** Reset tab flags */
  private resetFlags() {
    this.isDetails = false;
    this.isPlan = false;
    this.isCDR = false;
  }
}
