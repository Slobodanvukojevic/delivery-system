import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LockerService {

    private http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/api/lockers';

    getLockers(): Observable<any[]> {
        return this.http.get<any[]>(this.apiUrl);
    }

    getNearby(lat: number, lng: number, radius: number): Observable<any> {
        return this.http.get<any>(
            `http://localhost:8080/api/locations/nearby?lat=${lat}&lng=${lng}&radius=${radius}`
        );
    }
}