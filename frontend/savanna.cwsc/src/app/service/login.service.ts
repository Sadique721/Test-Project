import { HttpClient, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "../RadiusUtils/RadiusConstants";
import { Injectable } from "@angular/core";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";

const httpOptions = {
  headers: new HttpHeaders({ "Content-Type": "application/json" })
};

@Injectable({
  providedIn: "root"
})
export class LoginService {
  constructor(
    private http: HttpClient,
    private messageService: MessageService,
    private router: Router
  ) {}

  baseloginUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
  cmsUrl = RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL;
  commonUrl = RadiusConstants.SAVBILL_COMMON_BASE_URL;
  intigrationUrl = RadiusConstants.SAVBILL_INTEGRATION_SYSTEM_BASE_URL;

  cmsGetMethod(url) {
    return this.http.get(this.cmsUrl + url);
  }

  cmsPostMethod(url, data) {
    return this.http.post(`${this.cmsUrl}` + url, data);
  }

  commonGetMethod(url) {
    return this.http.get(this.commonUrl + url);
  }

  portalUpdatePassword(url, data) {
    return this.http.post(`${this.baseloginUrl}` + url, data, httpOptions);
  }

  postlogin(data) {
    return this.http.post(`${this.baseloginUrl}/login`, data, httpOptions);
  }

  generateToken(data) {
    return this.http.post(`${this.commonUrl}/login`, data, httpOptions);
  }

  refreshAPIToken() {
    return this.http.get(`${this.commonUrl}/refreshtoken`);
  }

  demographic() {
    return this.http.get(`${this.commonUrl}/getdemographicmapping`);
  }

  getConfigurationByName(data) {
    return this.http.get(
      `${this.commonUrl}/system/configuration/getConfigurationByName?name=${data}`
    );
  }

  async refreshToken() {
    this.refreshAPIToken().subscribe(
      (response: any) => {
        localStorage.setItem("token", response.accessToken);
      },
      (error: any) => {
        localStorage.removeItem("loggedInUser");
      }
    );
  }

  loginUser(token) {
    localStorage.setItem("token", token);
    return true;
  }

  isLoggedIn() {
    let token = localStorage.getItem("token");
    if (token == undefined || token === "" || token == null) {
      return false;
    } else {
      return true;
    }
  }

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("loggedInUser");
    localStorage.removeItem("userId");
    localStorage.removeItem("aclEntries");
    localStorage.removeItem("mvnoId");
    localStorage.clear();
    this.router?.navigate(["/login"]);
    return true;
  }

  getToken() {
    return localStorage.getItem("token");
  }

  public getAclEntry() {
    const url = "/acl/getAclEntry";
    this.commonGetMethod(url).subscribe(
      (res: any) => {
        if (res.dataList != null) {
          localStorage.setItem("aclEntries", JSON.stringify(res.dataList));
        }
      },
      err => {
        this.messageService.add({
          severity: "error",
          summary: err.error.errorMessage,
          detail: "Something was wrong. Try again",
          icon: "far fa-times-circle"
        });
      }
    );
  }

  hasPermission(...itemCodes: string[]): boolean {
    const rolePermissions = JSON.parse(localStorage.getItem("aclEntries"));

    if (rolePermissions != null) {
      return rolePermissions.some((item: any) => {
        return itemCodes.includes(item.code);
      });
    }
    return false;
  }

  getOTPBSS(data: any) {
    return this.http.post(`${this.cmsUrl}/otp/generate`, data);
  }

  validateOTPBSS(data: any) {
    return this.http.post(`${this.cmsUrl}/otp/validate`, data);
  }

  getTransactionstatus(data) {
    return this.http.post(`${this.cmsUrl}/customerqrlogin/getqrstatus`, data);
  }

  saveqrcode(data) {
    return this.http.post(`${this.cmsUrl}/customerqrlogin/saveqrcode`, data);
  }

  expireqrcode(data) {
    return this.http.post(`${this.cmsUrl}/customerqrlogin/expireqrcode`, data);
  }
}
