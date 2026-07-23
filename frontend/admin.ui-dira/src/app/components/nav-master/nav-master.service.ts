import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { SAVBILL_INTEGRATION_SYSTEM_BASE_URL } from "../../RadiusUtils/RadiusConstants";

@Injectable({
  providedIn: "root",
})
export class NavMasterService {
  constructor(private http: HttpClient) {}

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_INTEGRATION_SYSTEM_BASE_URL + url);
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_INTEGRATION_SYSTEM_BASE_URL + url, data);
  }

  deleteMethod(url) {
    return this.http.delete(RadiusConstants.SAVBILL_INTEGRATION_SYSTEM_BASE_URL + url);
  }

  updateMethod(url, data) {
    return this.http.put(RadiusConstants.SAVBILL_INTEGRATION_SYSTEM_BASE_URL + url, data);
  }

  getMethodForApiGateway(url) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }
}
