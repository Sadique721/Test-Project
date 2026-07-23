import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AttributeService {

    private baseUrl = RadiusConstants.DIAMETER_PORT + '/api/attributes';

    constructor(private http: HttpClient) { }

    getAll(): Observable<any[]> {
        return this.http.get<any[]>(`${this.baseUrl}`);
    }
    getSearch(key: any): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}?name=${key}`);
    }
    getById(id: number): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}?id=${id}`);
    }

    create(data: any): Observable<any> {
        return this.http.post(`${this.baseUrl}`, data);
    }

    update(id: number, data: any): Observable<any> {
        return this.http.put(`${this.baseUrl}/${id}`, data);
    }

    delete(id: number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${id}`);
    }
}
