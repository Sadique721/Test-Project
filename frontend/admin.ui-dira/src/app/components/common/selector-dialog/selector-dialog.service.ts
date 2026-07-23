import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})

export class SelectorDialogService {

    constructor(private http: HttpClient) { }

    getDataList(payload, url): Observable<any> {
        return this.http.post(url, payload);
    }
}
