import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KitchenService, Order } from './kitchen.service'; // Adjust path if needed
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-kitchen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kitchen.component.html'
})
export class KitchenComponent implements OnInit {
  orders$!: Observable<Order[]>;
  public message: string | null = null;
  public error: string | null = null;

  constructor(private kitchenService: KitchenService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  /**
   * Fetches the list of orders from the kitchen service.
   */
  loadOrders(): void {
    this.orders$ = this.kitchenService.getOrders().pipe(
      catchError(err => {
        this.error = 'Failed to load orders';
        return of([]);
      })
    );
  }

  /**
   * Handles the click event to mark an order as ready.
   * @param orderId The ID of the order.
   */
  onMarkAsReady(orderId: number): void {
    this.clearMessages();
    this.kitchenService.markOrderAsReady(orderId).subscribe({
      next: () => {
        this.message = `Order #${orderId} has been marked as ready!`;
        this.loadOrders();
      },
      error: (err) => {
        this.error = `Failed to update order #${orderId}. Please try again.`;
        console.error(err);
      }
    });
  }

  private clearMessages(): void {
    this.message = null;
    this.error = null;
  }
}
