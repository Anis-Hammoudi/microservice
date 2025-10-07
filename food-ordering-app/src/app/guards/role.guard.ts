import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService, UserInfo } from '../services/auth.service'; // Adjust path if necessary

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  /**
   * This method determines if a user is authorized to access a route.
   * It works by comparing the user's role to the 'expectedRole' defined
   * in the route's 'data' property in your app.routes.ts file.
   */
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // Extract the 'expectedRole' from the route's data configuration.
    // Example: { path: 'kitchen', ..., data: { expectedRole: 'CHEF' } }
    const expectedRole = route.data['expectedRole'];

    // Access the current user information from the AuthService.
    return this.authService.currentUser$.pipe(
      map((user: UserInfo | null) => {
        // Check if the user is logged in and their role matches the expected role.
        if (user && user.role === expectedRole) {
          // If they match, allow access to the route.
          return true;
        } else {
          // If the user is not logged in or their role does not match,
          // log a warning and redirect them to the home page.
          console.warn(`Access denied. User does not have the required role: ${expectedRole}`);
          return this.router.createUrlTree(['/home']);
        }
      })
    );
  }
}
