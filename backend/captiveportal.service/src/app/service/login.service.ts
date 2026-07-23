import { HttpClient, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class LoginService {
  constructor(private http: HttpClient) {}

  commonUrl = RadiusConstants.savbill_COMMON_BASE_URL;
  radiusUrl = RadiusConstants.savbill_RADIUS_BASE_URL;
  cmsUrl = RadiusConstants.savbill_CMS_BASE_URL;
  mvnoId = RadiusConstants.MVNO_ID;
  portalUrl = RadiusConstants.BSS_PORTAL_URL;

  generateTokenBSS(data: any) {
    return this.http.post(`${this.commonUrl}/login`, data);
  }

  // getCustomer(data: any) {
  //   return this.http.get(
  //     `${this.wifiUrl}/Customer/getCustomer?mvnoId=${this.mvnoId}&password=${data.password}&userName=${data.userName}`
  //   );
  // }
  //   loginByCustomer(data: any) {
  //     return this.http.post(
  //       `${this.wifiUrl}/Customer/Login?mvnoId=${this.mvnoId}`,
  //       data
  //     );
  //   }
  logOutCustomer(userName: any) {
    localStorage.removeItem("customerId");
    localStorage.removeItem("userName");
    return this.http.get(
      `${this.cmsUrl}/Customer/Logout?userName=` +
        `${userName}&mvnoId=${this.mvnoId}`
    );
  }

  logout() {
    localStorage.removeItem("token");
    return true;
  }

  getToken() {
    return localStorage.getItem("token");
  }

  getOTP(data: any) {
    return this.http.post(`${this.cmsUrl}/otp/generate`, data);
  }
  validateOTP(data: any) {
    return this.http.post(`${this.cmsUrl}/otp/validate`, data);
  }
  createCustomerBSS(data: any) {
    return this.http.post(`${this.cmsUrl}/customers`, data);
  }
  // validateVoucher(voucherCode: any) {
  //     return this.http.post(`${this.wifiUrl}/voucher/validate?code=` + encodeURIComponent(voucherCode) + `&mvnoId=${this.mvnoId}`, {});
  // }
  getCustomer(data) {
    return this.http.post(`${this.cmsUrl}/customers/search/Prepaid`, data);
  }
  // createCustomer(data: any) {
  //     return this.http.post(`${this.wifiUrl}/Customer/save?mvnoId=${this.mvnoId}`, data);
  // }
  getPlanDetails(id: number) {
    return this.http.get(
      `${this.cmsUrl}/Customer/planDetail?customerId=${id}&mvnoId=${this.mvnoId}`
    );
  }
  getDeviceDetails(data: any) {
    return this.http.post(
      `${this.radiusUrl}/Device/getDeviceData?mvnoId=${this.mvnoId}`,
      data
    );
  }

  getDeviceDetailsForLogin(data: any) {
    return this.http.post(
      `${this.radiusUrl}/Device/customerLogin?mvnoId=${this.mvnoId}`,
      data
    );
  }

  getLogoutCustomer(data: any) {
    return this.http.post(`${this.radiusUrl}/customer/logout`, data);
  }

  getCustomerById(url) {
    return this.http.get(this.cmsUrl + url);
  }

  // getMethod(url) {
  //     return this.http.get(this.wifiUrl + url);
  // }
  postMethod(url, data) {
    return this.http.post(this.cmsUrl + url, data);
  }
  getMethod(url) {
    return this.http.get(this.cmsUrl + url);
  }

  loginSubscribeCustomer(data: any) {
    return this.http.post(`${this.portalUrl}/login`, data);
  }

  renewCustomerPlan(data) {
    const url = `${this.cmsUrl}/subscriber/changePlan01`;
    return this.http.post(url, data);
  }

  getConfigurationByName(data) {
    return this.http.get(
      `${this.commonUrl}/system/configuration/getConfigurationByName?name=${data}`
    );
  }

  addPayment(data) {
    return this.http.post(`${this.cmsUrl}/addCustomerPaymentStatus`, data);
  }
  paymentGateway(data) {
    return this.http.post(`${this.cmsUrl}/submitPaymentDetail`, data);
  }

  saveCustomerFeedback(data: any) {
    return this.http.post(`${this.cmsUrl}/customerfeedback/create`, data);
  }
  getCustomerFeedback(id) {
    return this.http.get(
      `${this.cmsUrl}/customerfeedback/findByCustomerId?customerId=` + id
    );
  }
  updateCustomerFeedback(data: any) {
    return this.http.post(`${this.cmsUrl}/customerfeedback/update`, data);
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
  changePlan(data: any) {
    return this.http.post(
      `${this.cmsUrl}/subscriber/deactivatePlanInBulk`,
      data
    );
  }
  getBranchByServiceId(url, data) {
    return this.http.post(this.commonUrl + url, data);
  }
  razorpaycallback(url, data) {
    return this.http.post(url, data);
  }
  getActivePaymentConfiguration() {
    return this.http.get(
      `${this.commonUrl}/paymentconfig/getActivePaymentConfig?paymentGatewayFor=PAYMENT_GATEWAY_FOR_CAPTIVE`
    );
  }

  getCustomerLogout(data: any) {
    return this.http.post(`${this.radiusUrl}/customer/logoutCustomer`, data);
  }
}
