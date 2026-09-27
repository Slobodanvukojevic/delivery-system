import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../core/services/order.service';

@Component({
    selector: 'app-order-pickup',
    imports: [FormsModule],
    templateUrl: './order-pickup.html',
    styleUrl: './order-pickup.css'
})
export class OrderPickup {
    private orderService = inject(OrderService);
    private cdr = inject(ChangeDetectorRef);

    pickupCode = '';
    message = '';
    uspeh = false;
    loading = false;

    potvrdiPreuzimanje(): void {
        if (!this.pickupCode) {
            this.message = 'Unesite pickup kod';
            return;
        }

        this.loading = true;
        this.message = '';
        this.uspeh = false;

        this.orderService.pickupOrder(this.pickupCode).subscribe({
            next: (response) => {
                this.uspeh = true;
                this.message = 'Porudzbina #' + response.id + ' uspesno predata.';
                this.pickupCode = '';
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                this.message = err.error?.message || 'Pogresan pickup kod ili porudzbina nije spremna';
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }
}