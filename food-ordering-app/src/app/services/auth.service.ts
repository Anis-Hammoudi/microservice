import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap, switchMap, map, catchError } from 'rxjs/operators';

export interface AuthResponse {
  token: string;
  role: string;
}

export interface UserInfo {
  userId: number;
  email: string;
  role: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8084/auth';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.initializeFromToken();
  }

  private initializeFromToken() {
    const token = this.getToken();
    if (token) {
      this.validateToken().subscribe({
        next: (userInfo) => this.currentUserSubject.next(userInfo),
        error: () => this.logout()
      });
    }
  }

  /**
   * FIX: This method now ensures user info is fetched and the app's state is updated
   * before returning a value to the component, preventing race conditions with route guards.
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      switchMap(authResponse => {
        // 1. Save the token immediately.
        localStorage.setItem('token', authResponse.token);

        // 2. Fetch the full user info using the new token.
        return this.getUserInfo().pipe(
          // 3. Update the BehaviorSubject with the fetched user info.
          tap(userInfo => {
            this.currentUserSubject.next(userInfo);
            console.log('AuthService: currentUserSubject has been updated.', userInfo);
          }),
          // 4. Map the stream back to the original authResponse.
          // The component's `subscribe` block needs this original response
          // to perform the role-based redirection.
          map(() => authResponse)
        );
      })
    );
  }

  register(userInfo: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userInfo);
  }

  logout() {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    // Consider navigating to login page here if not handled elsewhere
    // this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null && this.currentUserSubject.value !== null;
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  getUserInfo(): Observable<UserInfo> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserInfo>(`${this.apiUrl}/user-info`, { headers });
  }

  validateToken(): Observable<UserInfo> {
    const headers = this.getAuthHeaders();
    return this.http.get<UserInfo>(`${this.apiUrl}/validate`, { headers });
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }
}
