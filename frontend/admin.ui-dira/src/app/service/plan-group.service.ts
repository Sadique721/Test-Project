import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

@Injectable({
  providedIn: "root",
})
export class PlanGroupService {
  baseUrl = RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL;

  constructor(private http: HttpClient) {}
  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  deleteMethod(url) {
    return this.http.delete(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  updateMethod(url, data) {
    return this.http.put(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  getPlanGroupList(data) {
    return this.http.post(`${this.baseUrl}/planGroupMappings/list`, data);
  }

  searchPlanGroup(data) {
    return this.http.post(`${this.baseUrl}/planGroupMappings/search`, data);
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
      `${RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL}${url}?serviceId=${serviceId}${serviceAreaId}&type=${type}&mode=${mode}&status=${status}&planGroup=${planGroup}&unitsOfValidity=${unitV}&validity=${validty}`
    );
  }
}
