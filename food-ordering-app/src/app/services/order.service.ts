import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';

export interface Order {
  id?: number;
  clientId: number;
  items: string[];
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8081/orders';

  constructor(private http: HttpClient) { }

  createOrder(clientId: number, items: string[]): Observable<Order> {
    const order: Order = {
      clientId,
      items
    };
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<Order>(this.apiUrl, order, { headers });
  }

  getOrderStatus(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`);
  }

  // Get order status with polling for real-time updates
  getOrderStatusWithPolling(orderId: number, intervalMs: number = 5000): Observable<Order> {
    return interval(intervalMs).pipe(
      startWith(0),
      switchMap(() => this.getOrderStatus(orderId))
    );
  }

  getOrdersByClientId(clientId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}?clientId=${clientId}`);
  }

  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl);
  }

  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/status`, status, { headers });
  }
}
