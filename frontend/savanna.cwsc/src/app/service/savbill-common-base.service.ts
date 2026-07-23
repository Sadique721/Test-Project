import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
// import { SAVBILL_COMMON_BASE_URL } from '../RadiusUtils/RadiusConstants';
import {
  SAVBILL_API_GATEWAY_COMMON_MANAGEMENT,
  SAVBILL_COMMON_BASE_URL,
} from "../RadiusUtils/RadiusConstants";
@Injectable({
  providedIn: "root",
})
export class SavbillCommonBaseService {
  constructor(private http: HttpClient) {}

  get(path: string) {
    return this.http.get(SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + path);
  }

  deleteData(path: string) {
    return this.http.delete(SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + path);
  }

  post(path: string, data: any) {
    return this.http.post(SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + path, data);
  }
  postCMS(path: string, data: any) {
    return this.http.post(SAVBILL_COMMON_BASE_URL + path, data);
  }

  put(path: string, data: any) {
    return this.http.put(SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + path, data);
  }
  getCMS(path: string) {
    return this.http.get(SAVBILL_COMMON_BASE_URL + path);
  }

  postMethod(url, data) {
    return this.http.post(SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, data);
  }

  getConnection(url) {
    return this.http.get(SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
  }
}
