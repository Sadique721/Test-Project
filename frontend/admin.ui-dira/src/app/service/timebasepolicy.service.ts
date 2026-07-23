import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TimeBasePolicy } from "../components/model/time-base-policy";

@Injectable({
  providedIn: "root",
})
export class TimebasepolicyService {
  mvnoId = localStorage.getItem("mvnoId");
  loggedInUser = localStorage.getItem("loggedInUser");
  baseUrl = RadiusConstants.SAVBILL_COMMON_BASE_URL.concat("/timebasepolicy");

  constructor(private http: HttpClient) {}
  searchbasepolicy(page, pagesize, data) {
    return this.http.post(
      `${this.baseUrl}/search?page=` +
        page +
        "&pageSize=" +
        pagesize +
        "&sortBy=createdate&sortOrder=0",
      data
    );
  }

  getAlltimebasepolicy(data) {
    // return this.http.post(`${this.baseUrl}`,data);

    return this.http.get(`${this.baseUrl}/all`);
  }

  getAlltimebasepolicywithpagination(data) {
    // return this.http.post(`${this.baseUrl}`,data);

    return this.http.post(`${this.baseUrl}`, data);
  }

  getPolicyById(policyId) {
    return this.http.get(`${this.baseUrl}/` + policyId);
  }

  addNewPolicyDetails(policyData) {
    return this.http.post(`${this.baseUrl}/save`, policyData);
  }

  updatePolicyDetails(policyData) {
    return this.http.post(`${this.baseUrl}/update`, policyData);
  }

  deletePolicy(data) {
    return this.http.post(`${this.baseUrl}/delete`, data);
  }

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_COMMON_BASE_URL + url);
  }
}
