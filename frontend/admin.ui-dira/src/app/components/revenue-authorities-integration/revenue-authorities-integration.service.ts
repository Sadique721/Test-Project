import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { HttpResponseCache } from "src/app/service/http-response-cache";

@Injectable({
    providedIn: "root",
})
export class KRAIntegrationService {
    constructor(private http: HttpClient, private cache: HttpResponseCache) { }

    getMethod(url: string) {
        return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
    }

    postMethod(url: string, data: any) {
        return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data,);
    }

    postMethodRevenue(url: string, data: any) {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data,);
    }

    getMethodRevenue(url: string) {
        return this.http.get(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url);
    }

    downloadRevenueFile(url: string): any {
        return this.http.get(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, { responseType: 'blob' });
    }

    postMethodWithTextResponse(url: string, data: any) {
        return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data, { responseType: "text" });
    }

    postMethodRevenueWithTextResponse(url: string, data: any) {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data, { responseType: "text" });
    }

    deleteMethod(url: string) {
        return this.http.delete(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
    }

    updateMethod(url: string, data: any) {
        return this.http.put(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, data);
    }

    getMethodWithCache(url: string) {
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, {
            params: { from_cache: "true" },
        });
    }

    clearCache(url: string) {
        const cacheUrl = RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url + "?from_cache=true";
        if (this.cache.hasStored(cacheUrl)) {
            this.cache.remove(cacheUrl);
        }
    }

    bulkDownloadInvoicePdf(url: string, data: any): any {
        return this.http.post(RadiusConstants.SAVBILL_REVENUE_MANAGEMENT_BASE_URL + url, data, { responseType: 'blob' });
    }
}