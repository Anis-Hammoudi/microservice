import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface Order {
  id?: number;
  clientId?: number; // Maintenant optionnel car automatiquement défini par le backend
  items: string[];
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8081/orders';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  createOrder(items: string[]): Observable<Order> {
    const order: Order = {
      items
    };
    
    const headers = this.authService.getAuthHeaders();

    return this.http.post<Order>(this.apiUrl, order, { headers });
  }

  getOrderStatus(orderId: number): Observable<Order> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`, { headers });
  }

  // Get order status with polling for real-time updates
  getOrderStatusWithPolling(orderId: number, intervalMs: number = 5000): Observable<Order> {
    return interval(intervalMs).pipe(
      startWith(0),
      switchMap(() => this.getOrderStatus(orderId))
    );
  }

  // Récupère les commandes de l'utilisateur connecté (automatiquement filtré par le backend)
  getMyOrders(): Observable<Order[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Order[]>(this.apiUrl, { headers });
  }

  // Pour la compatibilité, mais utilise maintenant l'authentification
  getOrdersByClientId(clientId: number): Observable<Order[]> {
    return this.getMyOrders();
  }

  getAllOrders(): Observable<Order[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Order[]>(this.apiUrl, { headers });
  }

  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/status`, status, { headers });
  }
}
