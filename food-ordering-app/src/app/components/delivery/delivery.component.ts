import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DeliveryService, Order} from './delivery.service'; // Adjust path
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';

@Component({
  selector: 'app-delivery',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './delivery.component.html'
})
export class DeliveryComponent implements OnInit {
  orders$!: Observable<Order[]>;
  public message: string | null = null;
  public error: string | null = null;

  constructor(private deliveryService: DeliveryService) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  /**
   * Fetches the list of orders from the service.
   */
  loadOrders(): void {
    this.orders$ = this.deliveryService.getOrders().pipe(
      catchError(err => {
        this.error = 'Failed to load orders';
        return of([]);
      })
    );
  }
  /**
   * Handles the click event to start an order's delivery.
   * @param orderId The ID of the order.
   */
  onStartDelivery(orderId: number): void {
    this.clearMessages();
    this.deliveryService.startDelivery(orderId).subscribe({
      next: (response) => {
        this.message = `Delivery for order #${orderId} has started!`;
        // Refresh the list to show the updated status
        this.loadOrders();
      },
      error: (err) => {
        this.error = `Failed to start delivery for order #${orderId}. Please try again.`;
        console.error(err);
      }
    });
  }

  private clearMessages(): void {
    this.message = null;
    this.error = null;
  }
  displayStatus(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ');
  }

}
