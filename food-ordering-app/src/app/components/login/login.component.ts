import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginData: LoginRequest = {
    email: '',
    password: ''
  };

  loading = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.loginData.email || !this.loginData.password) {
      this.error = 'Veuillez remplir tous les champs';
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Connexion réussie, redirection selon le rôle...', response);

        // --- UPDATED REDIRECTION LOGIC ---
        if (response.role === 'CHEF') {
          this.router.navigate(['/kitchen']);
        } else if (response.role === 'LIVREUR') { // FIX: Added redirect for DELIVERY role
          this.router.navigate(['/delivery']);
        } else if (response.role === 'CLIENT') {
          this.router.navigate(['/home']);
        } else {
          // Default redirect for any other roles.
          this.router.navigate(['/home']);
        }
      },
      error: (error) => {
        console.error('Erreur de connexion', error);
        this.error = 'Email ou mot de passe incorrect';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
