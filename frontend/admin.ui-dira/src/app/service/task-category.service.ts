import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';

@Injectable({
    providedIn: 'root'
})
export class TaskCategoryService {

    constructor(private http: HttpClient) { }

    getMethod(url) {
        return this.http.get(RadiusConstants.SAVBILL_TASK_MANAGEMENT + url);
    }

    postMethod(url, data) {
        return this.http.post(RadiusConstants.SAVBILL_TASK_MANAGEMENT + url, data);
    }

    deleteMethod(url) {
        return this.http.delete(RadiusConstants.SAVBILL_TASK_MANAGEMENT + url);
    }

    updateMethod(url, data) {
        return this.http.put(RadiusConstants.SAVBILL_TASK_MANAGEMENT + url, data);
    }
}
