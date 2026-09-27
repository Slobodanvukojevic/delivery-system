import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { BranchService } from '../../../core/services/branch.service';

@Component({
    selector: 'app-branch-list',
    imports: [],
    templateUrl: './branch-list.html',
    styleUrl: './branch-list.css'
})
export class BranchList implements OnInit {
    private branchService = inject(BranchService);
    private cdr = inject(ChangeDetectorRef);

    branches: any[] = [];
    loading = false;

    ngOnInit(): void {
        this.branchService.getBranches().subscribe({
            next: (response) => {
                this.branches = response.branches || response || [];
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Greska', err);
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }
}