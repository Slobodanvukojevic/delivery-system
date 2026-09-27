import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-navbar',
    imports: [RouterLink],
    templateUrl: './navbar.html',
    styleUrl: './navbar.css'
})
export class Navbar {
    private authService = inject(AuthService);
    private router = inject(Router);

    korisnik = this.authService.getCurrentUser();

    logout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}