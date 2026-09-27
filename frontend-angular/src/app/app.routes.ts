import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Dashboard } from './features/dashboard/dashboard';
import { OrderList } from './features/orders/order-list/order-list';
import { OrderDetail } from './features/orders/order-detail/order-detail';
import { OrderCreate } from './features/orders/order-create/order-create';
import { OrderPickup } from './features/orders/order-pickup/order-pickup';
import { BranchList } from './features/branches/branch-list/branch-list';
import { LockerList } from './features/lockers/locker-list/locker-list';
import { CourierList } from './features/couriers/courier-list/courier-list';
import { Layout } from './shared/components/layout/layout';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { ReportList } from './features/reports/report-list/report-list';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    {
        path: '',
        component: Layout,
        canActivate: [authGuard],
        children: [
            { path: 'dashboard', component: Dashboard },
            { path: 'orders', component: OrderList },
            { path: 'orders/create', component: OrderCreate },
            { path: 'orders/pickup', component: OrderPickup },
            { path: 'orders/:id', component: OrderDetail },
            {
                path: 'branches',
                component: BranchList,
                canActivate: [roleGuard],
                data: { roles: ['ADMIN', 'BRANCH_WORKER'] }
            },
            {
                path: 'lockers',
                component: LockerList,
                canActivate: [roleGuard],
                data: { roles: ['ADMIN', 'BRANCH_WORKER'] }
            },
            {
                path: 'couriers',
                component: CourierList,
                canActivate: [roleGuard],
                data: { roles: ['ADMIN'] }
            },
            {
                path: 'reports',
                component: ReportList,
                canActivate: [roleGuard],
                data: { roles: ['ADMIN'] }
            }
        ]
    },
    { path: '**', redirectTo: '/login' }
];