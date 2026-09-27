import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Order } from '../../../core/models/order';
import { OrderService } from '../../../core/services/order.service';
import { OrderStatusPipe } from '../../../shared/pipes/order-status.pipe';
import { DeliveryMethodPipe } from '../../../shared/pipes/delivery-method.pipe';
import { CurrencyRsdPipe } from '../../../shared/pipes/currency-rsd.pipe';

@Component({
    selector: 'app-order-detail',
    imports: [RouterLink, DatePipe, OrderStatusPipe, DeliveryMethodPipe, CurrencyRsdPipe],
    templateUrl: './order-detail.html',
    styleUrl: './order-detail.css'
})
export class OrderDetail implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private orderService = inject(OrderService);
    private cdr = inject(ChangeDetectorRef);

    porudzbina: Order | undefined;
    loading = false;
    akcijaLoading = false;
    message = '';
    uspeh = false;

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const id = Number(params.get('id'));
            this.ucitajPorudzbinu(id);
        });
    }

    ucitajPorudzbinu(id: number): void {
        this.loading = true;
        this.message = '';

        this.orderService.getOrder(id).subscribe({
            next: (o) => {
                this.porudzbina = new Order(
                    o.id, o.senderName, o.senderPhone, o.customerName, o.customerPhone,
                    o.weight, o.pickupAddress, o.dropoffAddress, o.status, o.deliveryMethod,
                    o.pickupCode, o.selectedBranchId, o.selectedLockerId, o.price, o.createdAt
                );
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                this.message = err.error?.message || 'Porudzbina nije pronadjena';
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }


    acceptOrder(): void {
        if (!this.porudzbina) return;

        this.akcijaLoading = true;
        this.message = '';
        this.uspeh = false;

        this.orderService.acceptOrder(this.porudzbina.id, this.porudzbina.weight).subscribe({
            next: (o) => {
                this.uspeh = true;
                this.message = 'Porudzbina prihvacena u poslovnici.';
                this.akcijaLoading = false;
                this.ucitajPorudzbinu(o.id);
            },
            error: (err) => {
                this.message = err.error?.message || 'Greska pri prihvatanju porudzbine';
                this.akcijaLoading = false;
                this.cdr.detectChanges();
            }
        });
    }

    assignLocker(): void {
        if (!this.porudzbina) return;

        this.akcijaLoading = true;
        this.message = '';
        this.uspeh = false;

        this.orderService.assignLocker(this.porudzbina.id).subscribe({
            next: (res: any) => {
                this.uspeh = true;
                this.message = `Paket smesten u ${res.lockerName}, sanducic #${res.compartmentNumber}. Pickup kod: ${res.pickupCode}`;
                this.akcijaLoading = false;
                this.ucitajPorudzbinu(res.orderId);
            },
            error: (err) => {
                this.message = err.error?.message || 'Greska pri dodeli paketomata';
                this.akcijaLoading = false;
                this.cdr.detectChanges();
            }
        });
    }

    markReadyForPickup(): void {
        this.updateStatus('READY_FOR_BRANCH_PICKUP');
    }

    markInTransit(): void {
        this.updateStatus('IN_TRANSIT');
    }

    markOutForDelivery(): void {
        this.updateStatus('OUT_FOR_DELIVERY');
    }

    markDelivered(): void {
        this.updateStatus('DELIVERED');
    }

    private updateStatus(status: string): void {
        if (!this.porudzbina) return;

        this.akcijaLoading = true;
        this.message = '';
        this.uspeh = false;

        this.orderService.updateStatus(this.porudzbina.id, status).subscribe({
            next: (o) => {
                this.uspeh = true;
                this.message = 'Status promenjen u: ' + status;
                this.akcijaLoading = false;
                this.ucitajPorudzbinu(o.id);
            },
            error: (err) => {
                this.message = err.error?.message || 'Greska pri promeni statusa';
                this.akcijaLoading = false;
                this.cdr.detectChanges();
            }
        });
    }

    nazad(): void {
        this.router.navigate(['/orders']);
    }
}