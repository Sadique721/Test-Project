import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

@Injectable({
  providedIn: "root"
})
export class VoucherConfigurationService {
  constructor(private http: HttpClient) {}
  mvnoId = localStorage.getItem("mvnoId");
  loggedInUser = localStorage.getItem("loggedInUser");
  baseUrl = RadiusConstants.SAVBILL_COMMON_BASE_URL + "/voucherManagement";

  getAllVouchers(page, size) {
    return this.http.get(`${this.baseUrl}/all` + "?page=" + page + "&size=" + size);
  }

  getAllReseller() {
    return this.http.get(
      `${RadiusConstants.SAVBILL_COMMON_BASE_URL}/Reseller/getAllResellers?mvnoId=${this.mvnoId}`
    );
  }

  changeVoucherConfigStatus(id, status, selectedMvnoId) {
    // let userId = localStorage.getItem("userId");
    // let superAdminId = RadiusConstants.SUPERADMINID;
    // if (userId == superAdminId) {
    //   return this.http.get(
    //     `${this.baseUrl}/updateStatus?id=` +
    //       id +
    //       "&status=" +
    //       status +
    //       "&mvnoId=" +
    //       selectedMvnoId +
    //       "&lastModifiedBy=" +
    //       this.loggedInUser
    //   );
    // } else {
    return this.http.get(
      `${this.baseUrl}/updateStatus?id=` +
        id +
        "&status=" +
        status +
        "&mvnoId=" +
        this.mvnoId +
        "&lastModifiedBy=" +
        this.loggedInUser
    );
    // }
  }

  searchVoucher(name, type, fromDate, toDate, page, size) {
    return this.http.get(
      `${this.baseUrl}/all?name=` +
        encodeURIComponent(name.trim()) +
        "&voucherCodeFormat=" +
        type +
        "&mvnoId=" +
        this.mvnoId +
        "&fromDate=" +
        fromDate +
        "&toDate=" +
        toDate
    );
  }

  deleteById(id) {
    return this.http.delete(`${this.baseUrl}/delete?configId=` + id);
  }

  viewVoucherConfigDetail(id, selectedMvnoId) {
    // let userId = localStorage.getItem("userId");
    // let superAdminId = RadiusConstants.SUPERADMINID;
    // if (userId == superAdminId) {
    //   return this.http.get(`${this.baseUrl}/findById?configId=` + id + "&mvnoId=" + selectedMvnoId);
    // } else {
    return this.http.get(`${this.baseUrl}/findById?configId=` + id + "&mvnoId=" + this.mvnoId);
    // }
  }

  saveVoucherConfig(voucherConfig) {
    //return this.http.post(`${this.baseUrl}/addVoucherConfig`, voucherConfig);
    // let userId = localStorage.getItem("userId");
    // let superAdminId = RadiusConstants.SUPERADMINID;
    // if (userId == superAdminId) {
    //   return this.http.post(`${this.baseUrl}/addVoucherConfig`, voucherConfig);
    // } else {
    return this.http.post(`${this.baseUrl}/addVoucherConfig`, voucherConfig);
    // }
  }

  updateVoucherConfig(voucherConfig) {
    //return this.http.put(`${this.baseUrl}/update`, voucherConfig);
    // let userId = localStorage.getItem("userId");
    // let superAdminId = RadiusConstants.SUPERADMINID;
    // if (userId == superAdminId) {
    //   return this.http.put(`${this.baseUrl}/update`, voucherConfig);
    // } else {
    return this.http.put(`${this.baseUrl}/update`, voucherConfig);
    // }
  }

  getAllPlans() {
    return this.http.get(
      `${RadiusConstants.SAVBILL_COMMON_BASE_URL}/plan` + "?mvnoId=" + this.mvnoId
    );
  }

  getValidPlans() {
    return this.http.get(
      `${RadiusConstants.SAVBILL_COMMON_BASE_URL}/postpaidplan/all?planGroup=ALL&type=NORMAL`
    );
  }

  // generateVoucher(configId, batchName, selectedMvnoId) {
  //   let data = '';
  //   if (this.loggedInUser == 'superadmin') {
  //     return this.http.post(
  //       `${RadiusConstants.SAVBILL_WIFI_BASE_URL}/voucher/generate?batchName=` +
  //         encodeURIComponent(batchName) +
  //         '&configId=' +
  //         configId +
  //         '&mvnoId=' +
  //         selectedMvnoId,
  //       data
  //     );
  //   } else {
  //     return this.http.post(
  //       `${RadiusConstants.SAVBILL_WIFI_BASE_URL}/voucher/generate?batchName=` +
  //         encodeURIComponent(batchName) +
  //         '&configId=' +
  //         configId +
  //         '&mvnoId=' +
  //         this.mvnoId,
  //       data
  //     );
  //   }
  // }
  generateVoucherBatch(data) {
    // let userId = localStorage.getItem("userId");
    // let superAdminId = RadiusConstants.SUPERADMINID;
    // if (userId == superAdminId) {
    //   return this.http.post(`${RadiusConstants.SAVBILL_COMMON_BASE_URL}/voucherbatch/generate`, data);
    // } else {
    return this.http.post(`${RadiusConstants.SAVBILL_COMMON_BASE_URL}/voucherbatch/generate`, data);
    // }
  }
}
