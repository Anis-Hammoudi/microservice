import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  registerData: RegisterRequest = {
    email: '',
    password: '',
    role: 'CLIENT'
  };
  
  confirmPassword = '';
  loading = false;
  error = '';
  success = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.registerData.email || !this.registerData.password || !this.confirmPassword) {
      this.error = 'Veuillez remplir tous les champs';
      return;
    }

    if (this.registerData.password !== this.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas';
      return;
    }

    if (this.registerData.password.length < 6) {
      this.error = 'Le mot de passe doit contenir au moins 6 caractères';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        console.log('Inscription réussie', response);
        this.success = 'Compte créé avec succès ! Vous pouvez maintenant vous connecter.';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Erreur d\'inscription', error);
        if (error.error && error.error.error) {
          this.error = error.error.error;
        } else {
          this.error = 'Une erreur est survenue lors de l\'inscription';
        }
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}
