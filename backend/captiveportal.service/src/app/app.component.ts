import { Component, OnInit } from "@angular/core";
import {
  Router,
  RouterEvent,
  RouteConfigLoadStart,
  RouteConfigLoadEnd,
} from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { TITLE } from "./RadiusUtils/RadiusConstants";
import { Title } from "@angular/platform-browser";
declare var require: any;
@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit {
  title = "savbillCaptive";
  config!: { version: string };
  constructor(
    private router: Router,
    private spinner: NgxSpinnerService,
    private http: HttpClient,
    private titleService: Title
  ) {
    router.events.subscribe((event: RouterEvent) => {
      if (event instanceof RouteConfigLoadStart) {
        this.spinner.show();
      } else if (event instanceof RouteConfigLoadEnd) {
        this.spinner.hide();
      }
    });
  }
  ngOnInit(): void {
    this.titleService.setTitle(TITLE);
    this.config = require("src/assets/config.json");
    console.log(this.config.version);

    const headers = new HttpHeaders()
      .set("Cache-Control", "no-cache")
      .set("Pragma", "no-cache");

    this.http
      .get<{ version: string }>("/assets/config.json", { headers })
      .subscribe((config) => {
        if (config.version !== this.config.version) {
          this.http
            .get("", { headers, responseType: "text" })
            .subscribe(() => location.reload());
        }
      });
  }
}
