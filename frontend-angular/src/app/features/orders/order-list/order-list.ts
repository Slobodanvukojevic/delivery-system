import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Order } from '../../../core/models/order';
import { OrderService } from '../../../core/services/order.service';
import { OrderStatusPipe } from '../../../shared/pipes/order-status.pipe';
import { ORDER_STATUSES } from '../../../core/models/order-status';
import { OrderCard } from '../order-card/order-card';

@Component({
    selector: 'app-order-list',
    imports: [FormsModule, RouterLink, OrderStatusPipe, OrderCard],
    templateUrl: './order-list.html',
    styleUrl: './order-list.css'
})
export class OrderList implements OnInit {
    private orderService = inject(OrderService);
    private router = inject(Router);
    private cdr = inject(ChangeDetectorRef);

    svePorudzbine: Order[] = [];
    prikazanePorudzbine: Order[] = [];
    loading = false;
    errorMessage = '';

    odabraniStatus: string = '';
    pretraga: string = '';
    statusi = ORDER_STATUSES;

    ngOnInit(): void {
        this.ucitajPorudzbine();
    }

    ucitajPorudzbine(): void {
        this.loading = true;
        this.errorMessage = '';

        this.orderService.getOrders(this.odabraniStatus, this.pretraga).subscribe({
            next: (response) => {
                const orders = Array.isArray(response)
                    ? response
                    : (response?.content || []);

                this.svePorudzbine = orders.map((o: any) => this.mapToOrder(o));
                this.prikazanePorudzbine = [...this.svePorudzbine];
                this.loading = false;
                this.cdr.detectChanges();  // <-- KLJUCNO
            },
            error: (err) => {
                this.errorMessage = err.error?.message || 'Greska pri ucitavanju porudzbina';
                this.loading = false;
                this.cdr.detectChanges();  // <-- KLJUCNO
            }
        });
    }

    mapToOrder(o: any): Order {
        return new Order(
            o.id, o.senderName, o.senderPhone, o.customerName, o.customerPhone,
            o.weight, o.pickupAddress, o.dropoffAddress, o.status, o.deliveryMethod,
            o.pickupCode, o.selectedBranchId, o.selectedLockerId, o.price, o.createdAt
        );
    }

    primeniFiltere(): void {
        this.ucitajPorudzbine();
    }

    resetujFiltere(): void {
        this.odabraniStatus = '';
        this.pretraga = '';
        this.ucitajPorudzbine();
    }

    prikaziDetalje(id: number): void {
        this.router.navigate(['/orders', id]);
    }
}