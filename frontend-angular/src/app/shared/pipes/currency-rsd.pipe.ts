import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'currencyRsd'
})
export class CurrencyRsdPipe implements PipeTransform {
    transform(value: number): string {
        if (value == null) return '0 RSD';
        return value.toFixed(2) + ' RSD';
    }
}