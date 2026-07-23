import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { map } from "rxjs/operators";

@Injectable({
  providedIn: "root",
})
export class CustomerdetailsilsService {
  baseUrl = RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL;
  paymentUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
  subscribeUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
  commonUrl = RadiusConstants.SAVBILL_COMMON_BASE_URL;
  revenueUrl = RadiusConstants.SAVBILL_REVENUE_URL;
  inventoryUrl = RadiusConstants.SAVBILL_INVENTORY_MANAGEMENT;
  intigrationUrl = RadiusConstants.SAVBILL_INTEGRATION_SYSTEM_BASE_URL;

  constructor(private http: HttpClient) {}

  getMethod(id) {
    return this.http.get(`${this.baseUrl}` + id);
  }

  inventoryGetMethod(id) {
    return this.http.get(`${this.inventoryUrl}` + id);
  }

  revenueGetMethod(id) {
    return this.http.get(`${this.revenueUrl}` + id);
  }

  commonGetMethod(id) {
    return this.http.get(`${this.commonUrl}` + id);
  }

  getPaymentMethod(id) {
    return this.http.get(`${this.paymentUrl}` + id);
  }

  postMethod(url, data) {
    return this.http.post(`${this.baseUrl}` + url, data);
  }

  postRevenueMethod(url, data) {
    return this.http.post(`${this.revenueUrl}` + url, data);
  }

  postSubscriberMethod(url, data) {
    return this.http.post(`${this.subscribeUrl}` + url, data);
  }

  addPayment(data) {
    return this.http.post(`${this.baseUrl}/addCustomerPaymentStatus`, data);
  }

  updatePayment(orderId, status) {
    return this.http.post(
      `${this.baseUrl}/updateCustomerPaymentStatus?orderid=${orderId}&status=${status}`,
      ""
    );
  }

  paymentGateway(data) {
    return this.http.post(`${this.baseUrl}/submitPaymentDetail`, data);
  }

  getCutomerTicketData(url) {
    return this.http.get(RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL + url);
  }

  downloadPDFInvoice(type: any): any {
    const url = this.revenueUrl + `${type}`;
    return this.http.get(url, { responseType: "blob" }).pipe(
      map((res: any) => {
        return new Blob([res], { type: "application/pdf" });
      })
    );
  }

  getCustomerLedger(data) {
    const url = "/customerLedgers";
    return this.http.post(`${this.revenueUrl}` + url, data);
  }

  getSubscriberPaymentHistory(id) {
    const url = "/paymentHistory/" + id;
    return this.http.get(`${this.revenueUrl}` + url);
  }

  updateMethod(url, data) {
    return this.http.put(this.baseUrl + url, data);
  }

  getmethodforrevenue(url) {
    return this.http.get(this.revenueUrl + url);
  }

  getBalanceData(data) {
    return this.http.get(`${this.baseUrl}/getbalance?fri=` + data);
  }

  sendDebit(data) {
    return this.http.post(`${this.baseUrl}/debit`, data);
  }

  updateCustomerMobile(data) {
    console.log("in service");
    return this.http.post(
      `${this.baseUrl}/customers/updateCustomerMobileNo`,
      data
    );
  }

  getTransactionstatus(data) {
    return this.http.post(
      `${this.baseUrl}/paymentGateway/gettransactionstatus`,
      data
    );
  }

  getDeviceDetails(data: any, mvnoId) {
    return this.http.post(
      `${this.baseUrl}/Device/getDeviceData?mvnoId=` + mvnoId,
      data
    );
  }
  getConfigurationByName(data) {
    return this.http.get(
      `${this.baseUrl}/system/configuration/getConfigurationByName?name=${data}`
    );
  }

  getActivePaymentConfiguration() {
    return this.http.get(
      `${this.commonUrl}/paymentconfig/getActivePaymentConfig?paymentGatewayFor=PAYMENT_GATEWAY_FOR_CWSC`
    );
  }
  razorpaycallback(url, data) {
    return this.http.post(url, data);
  }

  buyPlanUsingMomo(data) {
    return this.http.post(`${this.intigrationUrl}/requestToPay`, data);
  }

  getIntigrationTransactionstatus(data) {
    return this.http.post(`${this.intigrationUrl}/getpaymentstatus`, data);
  }

  buyPlanUsingAirtel(data) {
    return this.http.post(`${this.intigrationUrl}/airtel/requestToPay`, data);
  }

  buyPlanUsingSelcom(data) {
    return this.http.post(`${this.intigrationUrl}/selcomPay`, data);
  }

  postIntegrationMethod(url, data) {
    return this.http.post(`${this.intigrationUrl}` + url, data);
  }
    buyPlanUsingMpesaExpress(data) {
        return this.http.post(`${this.intigrationUrl}/c2b/mpesa/express/initiatePayment`, data);
    }
}
