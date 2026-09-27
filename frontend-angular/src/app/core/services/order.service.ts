import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OrderService {

    private http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/api/orders';
    getOrders(status?: string, search?: string, page: number = 0, size: number = 20): Observable<any> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        if (status) params = params.set('status', status);
        if (search) params = params.set('search', search);

        return this.http.get<any>(this.apiUrl, { params });
    }

    getOrder(id: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/${id}`);
    }

    createOrder(order: any): Observable<any> {
        return this.http.post<any>(this.apiUrl, order);
    }

    acceptOrder(id: number, weightConfirmed: number): Observable<any> {
        return this.http.patch<any>(`${this.apiUrl}/${id}/accept`, { weightConfirmed });
    }

    assignLocker(id: number): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/${id}/assign-locker`, {});
    }

    pickupOrder(pickupCode: string): Observable<any> {
        return this.http.patch<any>(`${this.apiUrl}/pickup`, { pickupCode });
    }

    updateStatus(id: number, status: string): Observable<any> {
        return this.http.patch<any>(`${this.apiUrl}/${id}/status`, { status });
    }
}