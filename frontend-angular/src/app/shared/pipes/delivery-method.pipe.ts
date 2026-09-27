import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'deliveryMethod'
})
export class DeliveryMethodPipe implements PipeTransform {

    private prevodi: { [key: string]: string } = {
        'HOME_DELIVERY': 'Kucna dostava',
        'BRANCH_PICKUP': 'Preuzimanje u poslovnici',
        'LOCKER_PICKUP': 'Preuzimanje u paketomatu'
    };

    transform(value: string): string {
        return this.prevodi[value] || value;
    }
}
