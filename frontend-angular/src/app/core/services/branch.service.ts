import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BranchService {

    private http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/api/locations/nearby';

    getBranches(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}?lat=44.8&lng=20.4&radius=1000`);
    }
}