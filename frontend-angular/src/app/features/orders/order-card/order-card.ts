import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Order } from '../../../core/models/order';
import { OrderStatusPipe } from '../../../shared/pipes/order-status.pipe';
import { DeliveryMethodPipe } from '../../../shared/pipes/delivery-method.pipe';
import { CurrencyRsdPipe } from '../../../shared/pipes/currency-rsd.pipe';
import { HighlightDirective } from '../../../shared/directives/highlight.directive';

@Component({
    selector: 'app-order-card',
    imports: [OrderStatusPipe, DeliveryMethodPipe, CurrencyRsdPipe, HighlightDirective],
    templateUrl: './order-card.html',
    styleUrl: './order-card.css'
})
export class OrderCard {
    @Input() order!: Order;
    @Output() detalji = new EventEmitter<number>();

    prikaziDetalje(): void {
        this.detalji.emit(this.order.id);
    }

    statusBoja(): string {
        if (this.order.status === 'DELIVERED' || this.order.status === 'PICKED_UP_BY_CUSTOMER') {
            return 'lightgreen';
        }
        if (this.order.status === 'RETURN_TO_SENDER' || this.order.status === 'CANCELLED') {
            return 'lightcoral';
        }
        if (this.order.status === 'PENDING') {
            return 'lightyellow';
        }
        return 'lightblue';
    }
}