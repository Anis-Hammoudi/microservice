import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService, Order } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-suivi-commande',
  imports: [CommonModule, FormsModule],
  templateUrl: './suivi-commande.component.html',
  styleUrl: './suivi-commande.component.scss'
})
export class SuiviCommandeComponent implements OnInit, OnDestroy {
  searchOrderId: string = '';
  currentOrder: Order | null = null;
  myOrders: Order[] = [];
  errorMessage: string = '';
  orderSubscription: Subscription | null = null;
  isPolling: boolean = false;
  loading: boolean = false;

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadMyOrders();
  }

  loadMyOrders() {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.myOrders = orders.sort((a, b) => (b.id || 0) - (a.id || 0)); // Trier par ID décroissant
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des commandes:', error);
        this.errorMessage = 'Erreur lors du chargement de vos commandes';
        this.loading = false;
      }
    });
  }

  searchOrder() {
    if (!this.searchOrderId) {
      this.errorMessage = 'Veuillez entrer un numéro de commande';
      return;
    }

    const orderId = parseInt(this.searchOrderId);
    if (isNaN(orderId)) {
      this.errorMessage = 'Numéro de commande invalide';
      return;
    }

    // Stop any existing polling
    this.stopPolling();

    // Start polling for order updates
    this.isPolling = true;
    this.orderSubscription = this.orderService.getOrderStatusWithPolling(orderId, 3000).subscribe({
      next: (order) => {
        this.currentOrder = order;
        this.errorMessage = '';
        
        // Stop polling if order is delivered or cancelled
        if (order.status === 'DELIVERED' || order.status === 'CANCELLED') {
          this.stopPolling();
        }
      },
      error: (error) => {
        this.errorMessage = 'Commande introuvable';
        this.currentOrder = null;
        this.stopPolling();
      }
    });
  }

  stopPolling() {
    this.isPolling = false;
    if (this.orderSubscription) {
      this.orderSubscription.unsubscribe();
      this.orderSubscription = null;
    }
  }

  ngOnDestroy() {
    this.stopPolling();
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'NEW': 'Nouvelle commande',
      'SENT': 'Envoyée à la cuisine',
      'READY': 'Prête',
      'IN_DELIVERY': 'En livraison',
      'DELIVERED': 'Livrée',
      'CANCELLED': 'Annulée'
    };
    return statusMap[status] || status;
  }

  getStatusProgress(status: string): number {
    const progressMap: { [key: string]: number } = {
      'NEW': 20,
      'SENT': 40,
      'READY': 60,
      'IN_DELIVERY': 80,
      'DELIVERED': 100,
      'CANCELLED': 0
    };
    return progressMap[status] || 0;
  }

  getStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'NEW': 'bg-blue-500',
      'SENT': 'bg-yellow-500',
      'READY': 'bg-orange-500',
      'IN_DELIVERY': 'bg-purple-500',
      'DELIVERED': 'bg-green-500',
      'CANCELLED': 'bg-red-500'
    };
    return colorMap[status] || 'bg-gray-500';
  }
}
