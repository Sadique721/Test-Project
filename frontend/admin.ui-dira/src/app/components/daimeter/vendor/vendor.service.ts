import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Vendor, VendorResponse, VendorSearchParams } from './vendor.interface';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';

@Injectable({
    providedIn: 'root'
})
export class VendorService {
    // Using the provided API endpoint

    private baseUrl = RadiusConstants.DIAMETER_PORT + '/vendors';

    constructor(private http: HttpClient) { }

    getAll(): Observable<Vendor[]> {
        return this.http.get<Vendor[]>(this.baseUrl)
            .pipe(
                catchError(this.handleError)
            );
    }

    getSearch(searchTerm: string): Observable<Vendor[]> {
        const params = new HttpParams().set('name', searchTerm);
        return this.http.get<Vendor[]>(this.baseUrl, { params })
            .pipe(
                catchError(this.handleError)
            );
    }

    getById(id: number): Observable<Vendor> {
        return this.http.get<Vendor>(`${this.baseUrl}/${id}`)
            .pipe(
                catchError(this.handleError)
            );
    }

    create(data: Vendor): Observable<VendorResponse> {
        // Ensure the payload matches the expected structure
        const payload = {
            name: data.name,
            description: data.description || '',
            status: data.status
        };

        return this.http.post<VendorResponse>(this.baseUrl, payload)
            .pipe(
                catchError(this.handleError)
            );
    }

    update(id: number, data: Vendor): Observable<VendorResponse> {
        // Ensure the payload matches the expected structure
        const payload = {
            vendor_id: data.vendor_id,
            name: data.name,
            description: data.description || '',
            status: data.status
        };

        return this.http.put<VendorResponse>(`${this.baseUrl}/${id}`, payload)
            .pipe(
                catchError(this.handleError)
            );
    }

    delete(id: number): Observable<VendorResponse> {
        return this.http.delete<VendorResponse>(`${this.baseUrl}/${id}`)
            .pipe(
                catchError(this.handleError)
            );
    }

    // Advanced search with multiple parameters
    searchVendors(params: VendorSearchParams): Observable<Vendor[]> {
        let httpParams = new HttpParams();

        if (params.name) httpParams = httpParams.set('name', params.name);
        if (params.status) httpParams = httpParams.set('status', params.status);
        if (params.page) httpParams = httpParams.set('page', params.page.toString());
        if (params.size) httpParams = httpParams.set('size', params.size.toString());

        return this.http.get<Vendor[]>(this.baseUrl, { params: httpParams })
            .pipe(
                catchError(this.handleError)
            );
    }

    private handleError(error: any): Observable<never> {
        console.error('VendorService Error:', error);
        return throwError(() => error);
    }
}
