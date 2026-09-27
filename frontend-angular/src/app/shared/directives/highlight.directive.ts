import { Directive, ElementRef, Input, inject } from '@angular/core';

@Directive({
    selector: '[appHighlight]'
})
export class HighlightDirective {
    private el = inject(ElementRef);

    @Input() set appHighlight(boja: string) {
        if (boja) {
            this.el.nativeElement.style.backgroundColor = boja;
        }
    }
}