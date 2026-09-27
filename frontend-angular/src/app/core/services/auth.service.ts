import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { User } from '../models/user';

@Injectable({ providedIn: 'root' })
export class AuthService {

    private http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/api/auth';

    login(email: string, password: string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/login`, { email, password })
            .pipe(
                tap(response => {
                    const user = new User(
                        response.userId,
                        email,
                        response.fullName,
                        response.role,
                        response.token
                    );
                    localStorage.setItem('user', JSON.stringify(user));
                    localStorage.setItem('token', response.token);
                })
            );
    }

    register(email: string, password: string, fullName: string, role: string, branchId: number | null = null): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, {
            email,
            password,
            fullName,
            phone: '+38164' + Math.floor(Math.random() * 1000000),
            role,
            branchId
        }).pipe(
            tap(response => {
                const user = new User(
                    response.userId,
                    email,
                    response.fullName,
                    response.role,
                    response.token
                );
                localStorage.setItem('user', JSON.stringify(user));
                localStorage.setItem('token', response.token);
            })
        );
    }

    logout(): void {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
    }

    getCurrentUser(): User | null {
        const userJson = localStorage.getItem('user');
        if (userJson) {
            return JSON.parse(userJson);
        }
        return null;
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }

    getRole(): string | null {
        const user = this.getCurrentUser();
        return user ? user.role : null;
    }
}