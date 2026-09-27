import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-register',
    imports: [FormsModule, RouterLink],
    templateUrl: './register.html',
    styleUrl: './register.css'
})
export class Register {
    public message = '';
    private authService = inject(AuthService);
    private router = inject(Router);

    register(registerForma: NgForm): void {
        if (registerForma.invalid) {
            this.message = 'Popunite sva polja ispravno';
            return;
        }

        const { email, password, fullName, role } = registerForma.value;

        this.authService.register(email, password, fullName, role).subscribe({
            next: () => {
                this.router.navigate(['/dashboard']);
            },
            error: (err) => {
                this.message = err.error?.message || 'Greska pri registraciji';
            }
        });
    }
}