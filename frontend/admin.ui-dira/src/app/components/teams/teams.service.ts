import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

@Injectable({
  providedIn: "root",
})
export class TeamsService {
  constructor(private http: HttpClient) {}

  getAllTeam(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/teams/permissions?productType=BSS",
      data
    );
  }

  getAllParentTeam() {
    return this.http.get(
      RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/teams?productType=BSS"
    );
  }

  getTeamById(id) {
    return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/teams/" + id);
  }

  createTeam(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/teams/save",
      data
    );
  }

  deleteTeam(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/teams/delete",
      data
    );
  }

  updateTeam(data) {
    return this.http.post(
      RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/teams/update",
      data
    );
  }

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, data);
  }
}
