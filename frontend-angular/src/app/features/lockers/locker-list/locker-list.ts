import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { LockerService } from '../../../core/services/locker.service';

@Component({
    selector: 'app-locker-list',
    imports: [],
    templateUrl: './locker-list.html',
    styleUrl: './locker-list.css'
})
export class LockerList implements OnInit {
    private lockerService = inject(LockerService);
    private cdr = inject(ChangeDetectorRef);

    lockers: any[] = [];
    loading = false;

    ngOnInit(): void {
        this.lockerService.getLockers().subscribe({
            next: (data) => {
                this.lockers = data;
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Greska', err);
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }

    zauzetostProcenat(l: any): number {
        if (!l.totalCompartments) return 0;
        return Math.round(((l.totalCompartments - l.availableCompartments) / l.totalCompartments) * 100);
    }

    zauzetostBoja(l: any): string {
        const proc = this.zauzetostProcenat(l);
        if (proc >= 90) return 'bg-danger';
        if (proc >= 70) return 'bg-warning';
        return 'bg-success';
    }
}