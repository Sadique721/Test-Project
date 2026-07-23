import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { map } from "rxjs/operators";

@Injectable({
  providedIn: "root",
})
export class CustomerFeedbackService {
  baseUrl = RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL;
  paymentUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
  subscribeUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
  commonUrl = RadiusConstants.SAVBILL_COMMON_BASE_URL;
  revenueUrl = RadiusConstants.SAVBILL_REVENUE_URL;
  inventoryUrl = RadiusConstants.SAVBILL_INVENTORY_MANAGEMENT;

  constructor(private http: HttpClient) {}

  getCustomerFeedback(id) {
    return this.http.get(
      `${this.baseUrl}/customerfeedback/findByCustomerId?customerId=` + id
    );
  }

  saveCustomerFeedback(data: any) {
    return this.http.post(`${this.baseUrl}/customerfeedback/create`, data);
  }

  updateCustomerFeedback(data: any) {
    return this.http.post(`${this.baseUrl}/customerfeedback/update`, data);
  }
}
