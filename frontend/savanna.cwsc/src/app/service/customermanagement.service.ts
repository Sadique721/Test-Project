import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { map } from "rxjs/operators";
import { CommondropdownService } from "./commondropdown.service";

@Injectable({
  providedIn: "root"
})
export class CustomermanagementService {
  baseUrl = RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL;
  baseradiusUrl = RadiusConstants.SAVBILL_RADIUS_BASE_URL;
  revenueUrl = RadiusConstants.SAVBILL_REVENUE_URL;
  protalUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
  notificationUrl = RadiusConstants.SAVBILL_NOTIFICATION_BASE_URL;
  commonUrl = RadiusConstants.SAVBILL_COMMON_BASE_URL;
  loggedInUser = localStorage.getItem("loggedInUser");
  constructor(
    private http: HttpClient,
    private commonDropdownService: CommondropdownService
  ) {}
  mvnoId = this.commonDropdownService.getMvnoId();

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  getCommonSavBill(url) {
    return this.http.get(RadiusConstants.SAVBILL_COMMON_BASE_URL + url);
  }

  postMethodPlan(url, data) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  postSuffBalance(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_REVENUE_URL + url, data);
  }
  getMethodForLeadApproveStaff(url, data) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  commonPostMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_COMMON_BASE_URL + url, data);
  }

  notidicationpostMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_NOTIFICATION_BASE_URL + url, data);
  }

  deleteMethod(url) {
    return this.http.delete(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  updateMethod(url, data) {
    return this.http.put(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  PostSubMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL + url, data);
  }

  paymentData(url) {
    return this.http.get(RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL + url);
  }

  getCutomerTicketData(url) {
    return this.http.get(RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL + url);
  }

  getRetunItemList(custid: any) {
    return this.http.get(
      RadiusConstants.SAVBILL_COMMON_BASE_URL + "/getReturnforCustomer?id=" + custid
    );
  }

  getPaytmLink(custid) {
    return this.http.post(`${this.baseUrl}/generatePaytmLinkAndSend?custId=` + custid, "");
  }

  // https://bss.5net.in:30080/SavbillRadius/findAcctCdrByUserName?mvnoId=1&page=1&size=5&userName=surya123&framedIpAddress=&fromDate=&toDate=

  getAcctCdrDataByUsername(userName, framedIpAddress, fromDate, toDate, page, size) {
    return this.http.get(
      `${this.baseradiusUrl}/findAcctCdrByUserName?mvnoId=${this.mvnoId}&page=${page}&size=${size}&userName=` +
        encodeURIComponent(userName) +
        "&framedIpAddress=" +
        encodeURIComponent(framedIpAddress) +
        "&fromDate=" +
        fromDate +
        "&toDate=" +
        toDate
    );
  }
  getAllCDRsForExport() {
    return this.http.get(`${this.baseradiusUrl}/getAllCDRSForExport?mvnoId=${this.mvnoId}`);
  }

  exportExcel(data: any) {
    if (data.username && data.framedIpAddress == null) {
      return this.http.get(
        `${this.baseradiusUrl}/exportExcel?mvnoId=${this.mvnoId}&userName=radiustest` +
          data.username
      );
    } else if (data.username == null && data.framedIpAddress) {
      return this.http.get(
        `${this.baseradiusUrl}/exportExcel?mvnoId=${this.mvnoId}&framedId=` + data.framedIpAddress
      );
    } else {
      return this.http.get(
        `${this.baseradiusUrl}/exportExcel?mvnoId=${this.mvnoId}&framedId=` +
          data.framedIpAddress +
          `&userName=` +
          data.username
      );
    }
  }
  AllAcctCdrData(page, size) {
    return this.http.get(
      `${this.baseradiusUrl}/acctCdrs?mvnoId=${this.mvnoId}&page=${page}&size=${size}`
    );
  }

  getAllCDRExport(data) {
    const url =
      `${this.baseradiusUrl}/exportExcel?mvnoId=${this.mvnoId}&userName=` +
      data.userName +
      "&fromDate" +
      data.fromDate +
      "&toDate" +
      data.toDate;
    return this.http.get(url, { responseType: "blob" }).pipe(
      map((res: any) => {
        return new Blob([res], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        });
      })
    );
  }
  downloadPDFInvoice(type: any): any {
    const url = this.revenueUrl + `${type}`;
    return this.http.get(url, { responseType: "blob" }).pipe(
      map((res: any) => {
        return new Blob([res], { type: "application/pdf" });
      })
    );
  }

  generateMethodInvoice(url) {
    return this.http.get(this.revenueUrl + url);
  }

  getofferPriceWithTax(planId: any, discount, planGroupId: any = "") {
    let plangroup = "";
    if (planGroupId !== "planGroupId") {
      plangroup = "&planGroupId=" + planGroupId;
    }
    return this.http.get(
      `${this.revenueUrl}/getOfferPriceWithTax/plan?planIds=` +
        planId +
        "&discount=" +
        discount +
        plangroup
    );
  }

  downloadInvoice(type: any): any {
    const url = RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + `${type}`;
    return this.http.get(url, { responseType: "blob" }).pipe(
      map((res: any) => {
        return new Blob([res], { type: "application/pdf" });
      })
    );
  }

  postMethodPasssHeader(url, data) {
    const headers = { rf: "bss" };
    return this.http.post(`${this.baseUrl}` + url, data, {
      headers
    });
  }

  getProtalMethod(url) {
    return this.http.get(this.protalUrl + url);
  }
  savbillRadius(url) {
    return this.http.get(this.baseradiusUrl + url);
  }

  getPlansByTypeServiceModeStatusAndServiceArea(
    url,
    type,
    serviceId,
    serviceAreaId,
    mode,
    status,
    planGroup,
    validty,
    unitV
  ) {
    if (status == null) status = "ACTIVE";
    return this.http.get(
      `${RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL}${url}?serviceAreaId=${serviceAreaId}&serviceId=${serviceId}&type=${type}&mode=${mode}&status=${status}&planGroup=${planGroup}&unitsOfValidity=${unitV}&validity=${validty}`
    );
  }

  getPlansByTypeServiceModeStatusAndServiceAreaWithoutService(
    url,
    type,
    serviceId,
    serviceAreaId,
    mode,
    status,
    planGroup
  ) {
    if (status == null) status = "ACTIVE";
    return this.http.get(
      `${RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL}${url}?serviceAreaId=${serviceAreaId}&serviceId=${serviceId}&type=${type}&mode=${mode}&status=${status}&planGroup=${planGroup}`
    );
  }

  getCustQuotaList(custid: any) {
    return this.http.get(
      RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + "/customer/custQuota/" + custid
    );
  }

  getCDRDataByCustomerId(mvnoId, data) {
    return this.http.post(`${this.baseradiusUrl}/findAcctCdr?mvnoId=` + mvnoId, data);
  }

  postMethodForWallet(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_REVENUE_URL + url, data);
  }

  getConfigurationByName(name) {
    return this.http.get(
      RadiusConstants.SAVBILL_COMMON_BASE_URL +
        `/system/configuration/getConfigurationByName?name=${name}`
    );
  }
    getCustomerChangePlanDueAmount(url, data) {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data);
    }

    getCustomerInsufficientBalance(url, data) {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data);
    }
}
