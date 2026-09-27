import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (route: ActivatedRouteSnapshot) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const dozvoljeneRole = route.data['roles'] as string[];
    const trenutnaRola = authService.getRole();

    if (trenutnaRola && dozvoljeneRole.includes(trenutnaRola)) {
        return true;
    }

    router.navigate(['/dashboard']);
    return false;
};