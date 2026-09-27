import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-report-list',
    imports: [DecimalPipe, FormsModule],
    templateUrl: './report-list.html',
    styleUrl: './report-list.css'
})
export class ReportList implements OnInit {
    private http = inject(HttpClient);
    private cdr = inject(ChangeDetectorRef);
    private authService = inject(AuthService);

    private readonly apiUrl = 'http://localhost:8080/api/reports';

    report: any = null;
    loading = false;
    errorMessage = '';

    trenutniMesec: string = new Date().toISOString().substring(0, 7);
    branchId: number = 1;

    ngOnInit(): void {
        this.ucitajReport();
    }

    ucitajReport(): void {
        this.loading = true;
        this.errorMessage = '';

        const params = new HttpParams().set('month', this.trenutniMesec);
        this.http.get<any>(`${this.apiUrl}/branch/${this.branchId}/monthly`, { params }).subscribe({
            next: (data) => {
                this.report = data;
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                this.errorMessage = err.error?.message || 'Greska pri ucitavanju izvestaja';
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }
}