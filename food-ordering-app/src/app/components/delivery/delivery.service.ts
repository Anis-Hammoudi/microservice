import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service'; // Adjust path if necessary

// Define the structure of a Delivery Order object
export interface Order {
  id: number;
  clientId: number;
  items: string[];
  status: 'READY_FOR_PICKUP' | 'IN_DELIVERY' | 'DELIVERED';
}

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  // The API URL for your delivery microservice.
  // **Please verify this port number.** I have assumed 8083.
  private apiUrl = 'http://localhost:8083/delivery/orders';

  constructor(private http: HttpClient, private authService: AuthService) { }

  /**
   * Fetches all orders from the delivery service.
   * @returns An Observable array of delivery orders.
   */
  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl, { headers: this.authService.getAuthHeaders() });
  }

  /**
   * Marks a specific order as "IN_DELIVERY".
   * @param orderId The ID of the order to start delivering.
   * @returns An Observable with the response from the server.
   */
  startDelivery(orderId: number): Observable<string> {
    const url = `${this.apiUrl}/${orderId}/start-delivery`;
    const options = {
      headers: this.authService.getAuthHeaders(),
      responseType: 'text' as 'json' // Expect a plain text response
    };
    return this.http.post<string>(url, {}, options);
  }
}
