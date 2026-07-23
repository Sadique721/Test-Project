import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { SavbillCommonBaseService } from "./savbill-common-base.service";
import { Observable } from "rxjs";
import { Acl } from "../components/generic-component/acl/acl-gerneric-component/model/acl";
import { ResponseData } from "../components/radius-role/base-save-update-response";
const httpOptions = {
  headers: new HttpHeaders({ "Content-Type": "application/json" }),
};
@Injectable({
  providedIn: "root",
})
export class RoleService extends SavbillCommonBaseService {
  constructor(http: HttpClient) {
    super(http);
  }

  baseUrl = "/role";

  public getAllOperation(): Observable<any> {
    const get_url = "/acl/getModuleOperations";
    return this.get(get_url);
  }

  public getCommonList(): Observable<any> {
    const get_url = "/commonList/commonStatus";
    return this.get(get_url);
  }

  getById(id) {
    return this.get(`${this.baseUrl}/` + id);
  }

  getRoleById(id) {
    return this.get(`${this.baseUrl}/product/${id}?productName=BSS`);
  }

  getByName(roleData) {
    return this.post(`${this.baseUrl}/searchRoleByProduct?productType=BSS`, roleData);
  }
  getAll() {
    return this.get(`${this.baseUrl}/all`);
  }

  add(data) {
    return this.post(`${this.baseUrl}/save`, data);
  }
  addUpdateRole(url, data, isEdit) {
    if (isEdit) return this.put(`${this.baseUrl}` + url, data);
    else return this.post(`${this.baseUrl}` + url, data);
  }

  update(data) {
    return this.post(`${this.baseUrl}/update`, data);
  }

  delete(roleId) {
    return this.deleteData(`${this.baseUrl}/delete/` + roleId);
  }

  getDataPostAPI(data) {
    return this.post(`${this.baseUrl}/permissions?productType=BSS`, data);
  }

  getDataPostAPIWithFlag(data, isloggedInUser) {
    return this.post(`${this.baseUrl}/permissions?productType=BSS&isloggedInUser=`+isloggedInUser, data);
  }

  getAllACLMenu() {
    return this.get(`/acl/getCommonAclMenu/BSS`);
  }
}
