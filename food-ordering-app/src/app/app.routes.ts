import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { SuiviCommandeComponent } from './components/suivi-commande/suivi-commande.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'suivi-commande', component: SuiviCommandeComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/home' } // Route wildcard pour les pages non trouvées
];
