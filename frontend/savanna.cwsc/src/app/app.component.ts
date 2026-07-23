import { Component, OnInit } from "@angular/core";
import { Router, RouteConfigLoadStart, RouteConfigLoadEnd, Event } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Title } from "@angular/platform-browser";
import { TITLE } from "../app/RadiusUtils/RadiusConstants";
import { UserActivityService } from "./service/user-activity.service";
import { SessionService } from "./service/session.service";
import { debounceTime } from "rxjs";
@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
  standalone: false
})
export class AppComponent implements OnInit {
  config!: { version: string };
  title = "SavbillGui";
  constructor(
    private router: Router,
    private spinner: NgxSpinnerService,
    private http: HttpClient,
    private titleService: Title,
    private sessionService: SessionService,
    private activityService: UserActivityService
  ) {
    router.events.subscribe((event: Event) => {
      if (event instanceof RouteConfigLoadStart) {
        this.spinner.show();
      } else if (event instanceof RouteConfigLoadEnd) {
        this.spinner.hide();
      }
    });
  }
  ngOnInit(): void {
    this.titleService.setTitle(TITLE);
    //  this.sessionService.startInactivityTimer();
    // this.activityService
    //   .onActivity()
    //   .pipe(debounceTime(300))
    //   .subscribe(() => {
    //     this.sessionService.resetInactivityTimer();
    //   });

    // // Sync logout across tabs
    // window.addEventListener("storage", event => {
    //   if (event.key === "logout-event") {
    //     this.sessionService.logout();
    //   }
    // });
  }
}
