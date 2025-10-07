import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, UserInfo } from './services/auth.service';
import { Observable } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule], // <-- missing comma fixed!
  templateUrl: './app.component.html', // <-- update path if this is app.component
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'food-ordering-app';
  currentUser$: Observable<UserInfo | null>;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
