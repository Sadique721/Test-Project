import { Injectable } from "@angular/core";
import { HttpClient, HttpParams, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { map } from "rxjs/operators";

@Injectable({
    providedIn: "root"
})
export class RevenueManagementService {
    baseUrl = RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL;
    baseradiusUrl = RadiusConstants.SAVBILL_RADIUS_BASE_URL;
    billingEngineUrl = RadiusConstants.SAVBILL_PAYMENT_RECEIPT_BASE_URL;
    protalUrl = RadiusConstants.SAVBILL_SUBSCRIBER_BASE_URL;
    notificationUrl = RadiusConstants.SAVBILL_NOTIFICATION_BASE_URL;
    loggedInUser = localStorage.getItem("loggedInUser");
    mvnoId = localStorage.getItem("mvnoId");
    constructor(private http: HttpClient) { }

    getMethod(url,isTextRes?) {
        if(isTextRes){
            return this.http.get(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, { responseType: 'text' });
        }
        return this.http.get(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url);
    }

    postMethod(url, data) {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data);
    }

    deleteMethod(url) {
        return this.http.delete(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url);
    }

    updateMethod(url, data) {
        return this.http.put(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data);
    }
    downloadPDF(type: any): any {
        const url = RadiusConstants.SAVBILL_PAYMENT_RECEIPT_BASE_URL + `${type}`;
        return this.http.get(url, { responseType: "blob" }).pipe(
            map((res: any) => {
                return new Blob([res], { type: "application/pdf" });
            })
        );
    }
    generateMethod(url) {
        return this.http.get(RadiusConstants.SAVBILL_PAYMENT_RECEIPT_BASE_URL + url);
    }
    getAllInvoiceByCustomer(url) {
        return this.http.get(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url);
    }

    postMethodPasssHeader(url, data) {
        const headers = { rf: "bss" };
        return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data, {
            headers
        });
    }

    postMethodPasssHeader1(url, data) {
        const headers = { rf: "bss" };
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data, {
            headers
        });
    }

    downloadPDFInvoice(type: any): any {
        const url = RadiusConstants.SAVBILL_PAYMENT_RECEIPT_BASE_URL + `${type}`;
        return this.http.get(url, { responseType: "blob" }).pipe(
            map((res: any) => {
                return new Blob([res], { type: "application/pdf" });
            })
        );
    }

    paymentData(url) {
        return this.http.get(RadiusConstants.SAVBILL_PAYMENT_RECEIPT_BASE_URL + url);
    }

    downloadInvoice(type: any): any {
        const url = RadiusConstants.SAVBILL_PAYMENT_RECEIPT_BASE_URL + `${type}`;
        return this.http.get(url, { responseType: "blob" }).pipe(
            map((res: any) => {
                return new Blob([res], { type: "application/pdf" });
            })
        );
    }

    getInvoiceDataById(id) {
        return this.http.get(`${this.baseUrl}/partnerInvoiceDetails/` + id);
    }

    postMethodWithData(url) {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, null);
    }

    getIspPayload(url) {
        return this.http.get(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url);
    }
}
