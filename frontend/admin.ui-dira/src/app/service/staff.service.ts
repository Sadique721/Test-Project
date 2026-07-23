import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { SavbillCommonBaseService } from "./savbill-common-base.service";
import { Observable, of, throwError } from "rxjs";
import { catchError, map } from "rxjs/operators";

const httpOptions = {
    headers: new HttpHeaders({ "Content-Type": "application/json" })
};

@Injectable({
    providedIn: "root"
})
export class StaffService extends SavbillCommonBaseService {
    mvnoId = localStorage.getItem("mvnoId");
    loggedInStaffDetail: any;

    constructor(http: HttpClient) {
        super(http);
    }

    baseUrl: string = "/staffuser";
    staffImg: any;

    getByName(staffName) {
        return this.get(`${this.baseUrl}/findByName?userName=` + staffName);
    }

    getAll() {
        this.mvnoId = localStorage.getItem("mvnoId");
        return this.get(`${this.baseUrl}/findAll?mvnoId=${this.mvnoId}`);
    }

    getAllStaff() {
        return this.get("/staffuser/allActive");
    }

    getAllActiveByServiceArea(serviceAreaIds) {
        return this.post("/staffuser/allActiveByServiceArea", serviceAreaIds);
    }

    getAllStaffList(data) {
        return this.post("/staffuser/list?product=BSS", data);
    }

    add(data) {
        return this.post("/staffuser", data);
    }

    update(data, staff_id) {
        const update_url = "/staffuser/" + staff_id;
        return this.put(update_url, data);
    }

    delete(staffId) {
        const delete_url = "/staffuser/" + staffId;
        return this.deleteData(delete_url);
    }

    changePassword(data) {
        return this.put(`${this.baseUrl}/changepassword`, data);
    }

    getAllRoleData(): Observable<any> {
        const get_url = "/role/?productType=BSS";

        return this.get(get_url).pipe(
            map(res => res),
            catchError((error: any) => {
                return throwError(error);
            })
        );
    }

    getAllRoleDataForLoggedInUser(): Observable<any> {
        const get_url = "/role/byLoggedInUser/?productType=BSS";
        return this.get(get_url);
    }

    getTeamsData(): Observable<any> {
        const get_url = "/teams/getAllTeamsWithoutPagination";
        return this.get(get_url).pipe(
            map(res => res),
            catchError((error: any) => {
                return throwError(error);
            })
        );
    }

    staffSearch(data) {
        return this.post("/staffuser/search", data);
    }

    getStaffUserData(id, isApiCall = false) {
        if (isApiCall) {
            return this.get("/staffuser/" + id).pipe(
                map(res => {
                    this.loggedInStaffDetail = res;
                    return this.loggedInStaffDetail;
                })
            );
        } else {
            return of(this.loggedInStaffDetail);
        }
    }

    getById(staffId, isApiCall = false) {
        return this.getStaffUserData(staffId, isApiCall);
    }

    getStaff(id) {
        return this.get("/getStaffUser/" + id);
    }

    getStaffUserProfile(id) {
        return this.get("/staff/profileImage/" + id);
    }
    postMethod(data) {
        return this.post(`/mvno`, data);
    }

    getMethod(url) {
        return this.get(url);
    }

    getFromCMS(url) {
        return this.getCMS(url);
    }

    postApiMethod(url, data) {
        return this.post(url, data);
    }

    postApiFromCMS(url, data) {
        return this.postCMS(url, data);
    }

    getAllBU() {
        return this.get(`/businessUnit/all`);
    }

    addNewReceipt(data) {
        return this.post(`/staff/Reciept`, data);
    }

    staffReceiptSearch(receptNumber, prefix, data) {
        return this.postCMS(`/staff/searchbyReciept?recieptNo=${receptNumber}&prefix=${prefix}`, data);
    }

    postcallMethod(url, data) {
        return this.post(url, data);
    }

    getBUFromStaff() {
        return this.get(`/businessUnit/getBUFromStaff`);
    }

    getAllStaffListWithoutPagination() {
        return this.get("/staffuser/ActivestaffWithoutPaggination");
    }
}
