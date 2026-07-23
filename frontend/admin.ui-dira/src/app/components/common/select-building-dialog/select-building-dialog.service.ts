import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class SelectBuildingDialogService {

    constructor(private http: HttpClient) { }

    getBuildingList(options): Observable<any> {
        const url = "/subarea/allWithPagination";
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, options);
    }

    getBuildingListBySearchValue(payload): Observable<any> {
        const url = "/subarea";
        return this.http.post(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, payload);
    }
}
