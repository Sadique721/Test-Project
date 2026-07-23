import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { Policy, PolicyResponse, PolicySearchParams } from './policy.interface';

@Injectable({
    providedIn: 'root'
})
export class PolicyService {
    private baseUrl = RadiusConstants.DIAMETER_PORT + '/qos';

    constructor(private http: HttpClient) { }

    getAll(): Observable<Policy[]> {
        return this.http.get<Policy[]>(this.baseUrl)
            .pipe(
                catchError(this.handleError)
            );
    }

    getSearch(searchTerm: string): Observable<Policy[]> {
        const params = new HttpParams().set('name', searchTerm);
        return this.http.get<Policy[]>(this.baseUrl, { params })
            .pipe(
                catchError(this.handleError)
            );
    }

    getById(id: number): Observable<Policy[]> {
        const params = new HttpParams().set('id', id.toString());
        return this.http.get<Policy[]>(this.baseUrl, { params })
            .pipe(
                catchError(this.handleError)
            );
    }

    create(data: Policy): Observable<PolicyResponse> {
        const payload = {
            name: data.name,
            description: data.description,
            thPolicyName: data.thPolicyName,
            baseParam1: data.baseParam1 || 'N/A',
            baseParam2: data.baseParam2 || 'N/A',
            baseParam3: data.baseParam3 || 'N/A',
            thParam1: data.thParam1 || 'N/A',
            thParam2: data.thParam2 || 'N/A',
            thParam3: data.thParam3 || 'N/A',
            basePolicyName: data.basePolicyName,
            mvnoid: data.mvnoid,
            type: data.type || null,
            qosSpeed: data.qosSpeed,
            upstreamProfileUID: data.upstreamProfileUID || null,
            downstreamProfileUID: data.downstreamProfileUID || null,
            isDeleted: 0
        };

        return this.http.post<PolicyResponse>(this.baseUrl, payload)
            .pipe(
                catchError(this.handleError)
            );
    }

    update(id: number, data: Policy): Observable<PolicyResponse> {
        const payload = {
            id: id,
            name: data.name,
            description: data.description,
            thPolicyName: data.thPolicyName,
            baseParam1: data.baseParam1 || 'N/A',
            baseParam2: data.baseParam2 || 'N/A',
            baseParam3: data.baseParam3 || 'N/A',
            thParam1: data.thParam1 || 'N/A',
            thParam2: data.thParam2 || 'N/A',
            thParam3: data.thParam3 || 'N/A',
            basePolicyName: data.basePolicyName,
            mvnoid: data.mvnoid,
            type: data.type || null,
            qosSpeed: data.qosSpeed,
            upstreamProfileUID: data.upstreamProfileUID || null,
            downstreamProfileUID: data.downstreamProfileUID || null,
            isDeleted: 0
        };

        return this.http.put<PolicyResponse>(`${this.baseUrl}/${id}`, payload)
            .pipe(
                catchError(this.handleError)
            );
    }

    delete(id: number): Observable<PolicyResponse> {
        return this.http.delete<PolicyResponse>(`${this.baseUrl}/${id}`)
            .pipe(
                catchError(this.handleError)
            );
    }

    // Advanced search with multiple parameters
    searchPolicies(params: PolicySearchParams): Observable<Policy[]> {
        let httpParams = new HttpParams();

        if (params.name) httpParams = httpParams.set('name', params.name);
        if (params.type) httpParams = httpParams.set('type', params.type);
        if (params.mvnoid) httpParams = httpParams.set('mvnoid', params.mvnoid.toString());
        if (params.page) httpParams = httpParams.set('page', params.page.toString());
        if (params.size) httpParams = httpParams.set('size', params.size.toString());

        return this.http.get<Policy[]>(this.baseUrl, { params: httpParams })
            .pipe(
                catchError(this.handleError)
            );
    }

    private handleError(error: any): Observable<never> {
        console.error('PolicyService Error:', error);
        return throwError(() => error);
    }
}
