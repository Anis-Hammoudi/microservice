import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service'; // Adjust path if necessary

// Define the structure of an Order object
export interface Order {
  id: number;
  clientId: number;
  items: string[]; // Assuming items is an array of strings
  status: 'PREPARING' | 'READY';
}

@Injectable({
  providedIn: 'root'
})
export class KitchenService {
  // The API URL for your kitchen microservice
  private apiUrl = 'http://localhost:8082/kitchen/orders';

  constructor(private http: HttpClient, private authService: AuthService) { }

  /**
   * Fetches all orders from the kitchen service.
   * @returns An Observable array of orders.
   */
  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl, { headers: this.authService.getAuthHeaders() });
  }

  /**
   * Marks a specific order as "READY".
   * @param orderId The ID of the order to update.
   * @returns An Observable with the response from the server.
   */
  markOrderAsReady(orderId: number): Observable<string> {
    const url = `${this.apiUrl}/${orderId}/ready`;
    const options = {
      headers: this.authService.getAuthHeaders(),
      responseType: 'text' as 'json' // FIX: Tell HttpClient to expect a plain text response
    };
    // The generic <string> is now redundant but kept for clarity.
    return this.http.post<string>(url, {}, options);
  }
}
