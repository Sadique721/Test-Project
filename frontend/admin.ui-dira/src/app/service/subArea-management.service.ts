import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { HttpResponseCache } from './http-response-cache';
import { Observable } from 'rxjs';
const httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" }),
};

@Injectable({
    providedIn: 'root'
})
export class SubAreaManagementService {
    post(url: string, formData: FormData) {
        throw new Error("Method not implemented.");
    }

    constructor(private http: HttpClient, private cache: HttpResponseCache) { }

    getMethod<T>(url: string) {
        return this.http.get<T>(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
    }

    postMethod(url, data) {
        return this.http.post(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, data);
    }

    deleteMethod(url) {
        return this.http.delete(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
    }

    updateMethod(url, data) {
        return this.http.put(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, data);
    }


    getMethodWithCache(url) {
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, {
            params: { from_cache: "true" }, // Return the cached response if available.
        });
    }

    clearCache(url) {
        if (
            this.cache.hasStored(
                RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url + "?from_cache=true"
            )
        ) {
            this.cache.remove(
                RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url + "?from_cache=true"
            );
        }
    }

    getMethodWithCacheFromSales(url) {
        return this.http.get(RadiusConstants.SAVBILL_LEAD_BASE_URL + url, {
            params: { from_cache: "true" }, // Return the cached response if available.
        });
    }

    downloadFile(subAreaId, uniquename): Observable<any> {
        const get_url = RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + "/subarea/document/download/" + subAreaId + "/" + uniquename + "/";
        return this.http.get(get_url, {
            observe: 'response',
            responseType: 'blob' as 'json',
            headers: httpOptions.headers,
        });
    }

}
