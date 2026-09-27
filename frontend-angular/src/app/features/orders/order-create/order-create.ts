import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';

@Component({
    selector: 'app-order-create',
    imports: [ReactiveFormsModule, RouterLink],
    templateUrl: './order-create.html',
    styleUrl: './order-create.css'
})
export class OrderCreate {
    private fb = inject(FormBuilder);
    private orderService = inject(OrderService);
    private router = inject(Router);
    private cdr = inject(ChangeDetectorRef);

    message = '';
    loading = false;

    forma = this.fb.group({
        senderName: ['', [Validators.required, Validators.minLength(3)]],
        senderPhone: ['', [Validators.required, Validators.pattern('^[0-9+\\- ]{8,20}$')]],
        customerName: ['', [Validators.required, Validators.minLength(3)]],
        customerPhone: ['', [Validators.required, Validators.pattern('^[0-9+\\- ]{8,20}$')]],
        weight: [1, [Validators.required, Validators.min(1), Validators.max(30)]],
        pickupAddress: ['', Validators.required],
        dropoffAddress: ['', Validators.required],
        deliveryMethod: ['HOME_DELIVERY', Validators.required],
        selectedLockerId: [null]
    });

    onSubmit(): void {
        if (this.forma.invalid) {
            this.message = 'Popunite sva polja ispravno';
            return;
        }

        this.loading = true;
        const v = this.forma.value;

        const request = {
            senderName: v.senderName,
            senderPhone: v.senderPhone,
            customerName: v.customerName,
            customerPhone: v.customerPhone,
            weight: v.weight,
            pickupAddress: v.pickupAddress,
            dropoffAddress: v.dropoffAddress,
            deliveryMethod: v.deliveryMethod,
            selectedLockerId: v.selectedLockerId
        };

        this.orderService.createOrder(request).subscribe({
            next: () => {
                this.router.navigate(['/orders']);
            },
            error: (err) => {
                this.message = err.error?.message || 'Greska pri kreiranju porudzbine';
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }

    get senderName() { return this.forma.get('senderName'); }
    get senderPhone() { return this.forma.get('senderPhone'); }
    get customerName() { return this.forma.get('customerName'); }
    get customerPhone() { return this.forma.get('customerPhone'); }
    get weight() { return this.forma.get('weight'); }
    get pickupAddress() { return this.forma.get('pickupAddress'); }
    get dropoffAddress() { return this.forma.get('dropoffAddress'); }
}