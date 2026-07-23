import { Injectable, NgZone } from "@angular/core";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { timer, Subscription } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "./login.service";

@Injectable({ providedIn: "root" })
export class SessionService {
  private timerSub: Subscription;
  private readonly sessionKey = "mySessionKey";
  private timeOut = RadiusConstants.SESSIONTIMEOUT;
  constructor(
    private router: Router,
    private zone: NgZone,
    private messageService: MessageService,
    private loginService: LoginService
  ) {}

  startInactivityTimer() {
    this.clearInactivityTimer();
    localStorage.setItem(this.sessionKey, "active");

    const timeout = this.timeOut;
    this.zone.runOutsideAngular(() => {
      this.timerSub = timer(timeout).subscribe(() => {
        this.zone.run(() => this.logout());
      });
    });
  }

  resetInactivityTimer() {
    if (this.isSessionActive()) {
      this.startInactivityTimer();
    }
  }

  clearInactivityTimer() {
    if (this.timerSub) this.timerSub.unsubscribe();
    localStorage.removeItem(this.sessionKey);
  }

  isSessionActive(): boolean {
    return localStorage.getItem(this.sessionKey) === "active";
  }

  logout() {
    this.clearInactivityTimer();
    localStorage.setItem("logout-event", Date.now().toString());
    this.loginService.logout();

    this.messageService.add({
      severity: "warn",
      summary: "Session Timeout",
      detail: "You have been logged out due to inactivity.",
      icon: "far fa-check-circle"
    });
    this.router.navigate(["/login"]);
  }
}
