import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },

  // Public routes
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent) },

  // --- CLIENT Routes ---
  // Both AuthGuard and RoleGuard are used. The user must be logged in AND have the role 'CLIENT'.
  {
    path: 'home',
    loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { expectedRole: 'CLIENT' }
  },
  {
    path: 'suivi-commande',
    loadComponent: () => import('./components/suivi-commande/suivi-commande.component').then(m => m.SuiviCommandeComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { expectedRole: 'CLIENT' }
  },

  // --- CHEF Route ---
  // This route is only accessible to users with the 'CHEF' role.
  {
    path: 'kitchen',
    loadComponent: () => import('./components/kitchen/kitchen.component').then(m => m.KitchenComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { expectedRole: 'CHEF' }
  },

  // --- DELIVERY Route ---
  {
    path: 'delivery',
    loadComponent: () => import('./components/delivery/delivery.component').then(m => m.DeliveryComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { expectedRole: 'LIVREUR' } // Assumes a 'DELIVERY' role exists
  },

  // Fallback route
  { path: '**', redirectTo: '/home' }
];
