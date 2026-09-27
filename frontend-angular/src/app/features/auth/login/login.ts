import { Component, inject } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-login',
    imports: [FormsModule, RouterLink],
    templateUrl: './login.html',
    styleUrl: './login.css'
})
export class Login {
    public message = '';
    private authService = inject(AuthService);
    private router = inject(Router);

    login(loginForma: NgForm): void {
        if (loginForma.invalid) {
            this.message = 'Popunite sva polja ispravno';
            return;
        }

        const { email, password } = loginForma.value;

        this.authService.login(email, password).subscribe({
            next: () => {
                this.router.navigate(['/dashboard']);
            },
            error: (err) => {
                this.message = err.error?.message || 'Pogresan email ili lozinka';
            }
        });
    }
}