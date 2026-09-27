import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CourierService {

    private http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/api/users';

    getCouriers(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}?role=COURIER`);
    }
}