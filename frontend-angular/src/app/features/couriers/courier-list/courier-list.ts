import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CourierService } from '../../../core/services/courier.service';

@Component({
    selector: 'app-courier-list',
    imports: [FormsModule],
    templateUrl: './courier-list.html',
    styleUrl: './courier-list.css'
})
export class CourierList implements OnInit {
    private courierService = inject(CourierService);
    private cdr = inject(ChangeDetectorRef);

    sviKuriri: any[] = [];
    prikazani: any[] = [];
    odabraniBranchId: number | null = null;

    ngOnInit(): void {
        this.courierService.getCouriers().subscribe({
            next: (data) => {
                this.sviKuriri = data;
                this.prikazani = [...data];
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Greska', err);
                this.cdr.detectChanges();
            }
        });
    }

    filtriraj(): void {
        if (this.odabraniBranchId) {
            this.prikazani = this.sviKuriri.filter(c => c.branchId === this.odabraniBranchId);
        } else {
            this.prikazani = [...this.sviKuriri];
        }
        this.cdr.detectChanges();
    }
}