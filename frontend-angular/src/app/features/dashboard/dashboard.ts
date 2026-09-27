import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { OrderService } from '../../core/services/order.service';
import { BranchService } from '../../core/services/branch.service';
import { LockerService } from '../../core/services/locker.service';
import { User } from '../../core/models/user';
import { Order } from '../../core/models/order';
import { OrderStatusPipe } from '../../shared/pipes/order-status.pipe';

@Component({
    selector: 'app-dashboard',
    imports: [RouterLink, DatePipe, DecimalPipe, OrderStatusPipe],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
    private authService = inject(AuthService);
    private orderService = inject(OrderService);
    private branchService = inject(BranchService);
    private lockerService = inject(LockerService);
    private cdr = inject(ChangeDetectorRef);

    korisnik: User | null = this.authService.getCurrentUser();

    ukupno = 0;
    isporuceno = 0;
    naCekanju = 0;
    ukupnaZarada = 0;
    brojPoslovnica = 0;
    brojPaketomata = 0;
    poslednjePorudzbine: Order[] = [];

    ngOnInit(): void {
        this.ucitajStatistiku();
        this.ucitajBrojeve();
    }

    ucitajStatistiku(): void {
        this.orderService.getOrders(undefined, undefined, 0, 100).subscribe({
            next: (response: any) => {
                const orders = Array.isArray(response) ? response : (response?.content || []);
                this.ukupno = orders.length;
                this.isporuceno = orders.filter((o: any) =>
                    o.status === 'DELIVERED' || o.status === 'PICKED_UP_BY_CUSTOMER'
                ).length;
                this.naCekanju = orders.filter((o: any) =>
                    o.status === 'PENDING' || o.status === 'ACCEPTED_AT_BRANCH'
                ).length;
                this.ukupnaZarada = orders
                    .filter((o: any) => o.status === 'DELIVERED' || o.status === 'PICKED_UP_BY_CUSTOMER')
                    .reduce((sum: number, o: any) => sum + (o.price || 0), 0);

                this.poslednjePorudzbine = [...orders]
                    .sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                    .slice(0, 5);

                this.cdr.detectChanges();
            },
            error: (err: any) => {
                console.error('Greska', err);
                this.cdr.detectChanges();
            }
        });
    }

    ucitajBrojeve(): void {
        this.branchService.getBranches().subscribe({
            next: (res: any) => {
                this.brojPoslovnica = (res.branches || res || []).length;
                this.cdr.detectChanges();
            },
            error: () => {}
        });

        this.lockerService.getLockers().subscribe({
            next: (res: any) => {
                this.brojPaketomata = (res || []).length;
                this.cdr.detectChanges();
            },
            error: () => {}
        });
    }
}