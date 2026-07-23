import { HttpClient } from '@angular/common/http';
import * as ReadiusConstant from 'src/app/RadiusUtils/RadiusConstants';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ControllerBaseServiceService {

  constructor(private http: HttpClient) { }

  get(path: string) {
    return this.http.get(ReadiusConstant.SAVBILL_CONTROLLER_BASE_URL + path);
  }

  delete(path: string) {
    return this.http.delete(ReadiusConstant.SAVBILL_CONTROLLER_BASE_URL + path);
  }

  // post(path: string) {
  //   return this.http.post(ReadiusConstant.SAVBILL_CONTROLLER_BASE_URL + path);
  // }

  post(path: string, data: any) {
    return this.http.post(ReadiusConstant.SAVBILL_CONTROLLER_BASE_URL + path, data);
  }

  put(path: string, data: any) {
    return this.http.put(ReadiusConstant.SAVBILL_CONTROLLER_BASE_URL + path, data);
  }
}
