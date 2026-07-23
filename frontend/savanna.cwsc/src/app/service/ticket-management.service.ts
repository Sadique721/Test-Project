import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ɵAnimationGroupPlayer } from "@angular/animations";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class TicketManagementService {
  httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" })
  };

  constructor(private http: HttpClient) {}

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_TICKET_MANAGEMENT + url);
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_TICKET_MANAGEMENT + url, data);
  }

  postMethodOLT(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, data);
  }

  assignMethod(url, formData) {
    return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, formData);
  }

  deleteMethod(url) {
    return this.http.delete(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
  }

  updateMethod(url, data) {
    return this.http.put(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }
  getCutomerTicketData(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_TICKET_MANAGEMENT + "/case" + url, data);
  }
  downloadFile(url): Observable<any> {
    return this.http.get(RadiusConstants.SAVBILL_TICKET_MANAGEMENT + url, {
      responseType: "blob",
      headers: this.httpOptions.headers
    });
  }
}
