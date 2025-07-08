import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { SuiviCommandeComponent } from './components/suivi-commande/suivi-commande.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent },
  { path: 'suivi-commande', component: SuiviCommandeComponent },
  { path: '**', redirectTo: '/home' } // Route wildcard pour les pages non trouvées
];
