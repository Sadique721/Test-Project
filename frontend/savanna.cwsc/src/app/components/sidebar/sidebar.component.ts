import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import * as ACLCONSTANTS from "src/app/constants/aclConstants";

@Component({
  selector: "app-sidebar",
  templateUrl: "./sidebar.component.html",
  styleUrls: ["./sidebar.component.css"],
})
export class SidebarComponent implements OnInit {
  loggedInUser: string = "";
  ACL_CONSTANTS = ACLCONSTANTS;

  constructor(private router: Router, public loginService: LoginService) {}

  ngOnInit(): void {
    this.loggedInUser = localStorage.getItem("loggedInUser");
  }

  login() {
    this.router.navigate(["/login"]);
  }

  // logout() {
  //   sessionStorage.removeItem('username');
  //   sessionStorage.removeItem('password');
  //   this.router.navigate(['/login']);
  // }

  logout() {
    //It will remove token from local storage
    this.loginService.logout();
    this.router.navigate(["/login"]);
    //It will reload the context.
    // location.reload()
  }
}
