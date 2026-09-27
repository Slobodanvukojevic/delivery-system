import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'orderStatus'
})
export class OrderStatusPipe implements PipeTransform {

    private prevodi: { [key: string]: string } = {
        'PENDING': 'Na cekanju',
        'ACCEPTED_AT_BRANCH': 'Primljeno u poslovnici',
        'IN_SORTING': 'Sortiranje',
        'IN_TRANSIT': 'U transportu',
        'PLACED_IN_LOCKER': 'U paketomatu',
        'READY_FOR_BRANCH_PICKUP': 'Spremno za preuzimanje',
        'OUT_FOR_DELIVERY': 'Na dostavi',
        'DELIVERED': 'Isporuceno',
        'PICKED_UP_BY_CUSTOMER': 'Preuzeto',
        'RETURN_TO_SENDER': 'Vraceno posiljaocu',
        'CANCELLED': 'Otkazano'
    };

    transform(value: string): string {
        return this.prevodi[value] || value;
    }
}