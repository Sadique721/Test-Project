import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { NgxSpinnerService } from "ngx-spinner";

@Injectable({
  providedIn: "root"
})
export class PartnerService {
  serviceAreaList: any = [];
  serviceAreaService: any;

  constructor(
    private http: HttpClient,
    private spinner: NgxSpinnerService
  ) {}

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  getMethodNew(url) {
    return this.http.get(RadiusConstants.PMS_URL + url);
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  postMethodNew(url, data) {
    return this.http.post(RadiusConstants.PMS_URL + url, data);
  }

  deleteMethod(url) {
    return this.http.delete(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  deleteMethodNew(url) {
    return this.http.delete(RadiusConstants.PMS_URL + url);
  }

  updateMethod(url, data) {
    return this.http.put(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  updateMethodNew(url, data) {
    return this.http.put(RadiusConstants.PMS_URL + url, data);
  }

  searchTax(url, data) {
    return this.http.post(RadiusConstants.PMS_URL + url, data);
  }

  searchManagePartner(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  balanaceData(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + "/partnerLedger",
      data
    );
  }

  addBalance(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + "/partner/addBalanceInPartner",
      data
    );
  }

  transferBalance(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + "/partner/transferBalance",
      data
    );
  }

  withdrawalCommission(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + "/partner/withdrawCommission",
      data
    );
  }
  getActivePartner(url) {
    return this.http.get(RadiusConstants.PMS_URL + url);
  }

  getserviceAreaList(url) {
    return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
  }

  getPincodeList(url) {
    return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
  }
}
