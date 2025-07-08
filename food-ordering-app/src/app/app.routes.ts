import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./components/register/register.component').then(m => m.RegisterComponent) },
  { path: 'home', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent), canActivate: [AuthGuard] },
  { path: 'suivi-commande', loadComponent: () => import('./components/suivi-commande/suivi-commande.component').then(m => m.SuiviCommandeComponent), canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/home' }
];
