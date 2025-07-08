import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FoodService, FoodItem } from '../../services/food.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

interface CartItem extends FoodItem {
  quantity: number;
}

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  menuItems: FoodItem[] = [];
  categories: string[] = [];
  selectedCategory: string = '';
  cart: CartItem[] = [];
  showCart = false;
  orderStatus: string = '';
  lastOrderId: number | null = null;

  constructor(
    private foodService: FoodService,
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadMenuItems();
    this.loadCategories();
  }

  loadMenuItems() {
    this.foodService.getMenuItems().subscribe(items => {
      this.menuItems = items;
    });
  }

  loadCategories() {
    this.foodService.getCategories().subscribe(categories => {
      this.categories = categories;
    });
  }

  filterByCategory(category: string) {
    this.selectedCategory = category;
    if (category) {
      this.foodService.getItemsByCategory(category).subscribe(items => {
        this.menuItems = items;
      });
    } else {
      this.loadMenuItems();
    }
  }

  addToCart(item: FoodItem) {
    const existingItem = this.cart.find(cartItem => cartItem.id === item.id);
    if (existingItem) {
      existingItem.quantity++;
    } else {
      this.cart.push({ ...item, quantity: 1 });
    }
  }

  removeFromCart(index: number) {
    this.cart.splice(index, 1);
  }

  updateQuantity(index: number, quantity: number) {
    if (quantity > 0) {
      this.cart[index].quantity = quantity;
    } else {
      this.removeFromCart(index);
    }
  }

  getCartTotal(): number {
    return this.cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  toggleCart() {
    this.showCart = !this.showCart;
  }

  placeOrder() {
    if (this.cart.length === 0) {
      alert('Votre panier est vide!');
      return;
    }

    // Create order items array with quantities
    const orderItems = this.cart.map(item => 
      `${item.name} x${item.quantity}`
    );

    // Le clientId sera automatiquement extrait du token JWT
    this.orderService.createOrder(orderItems).subscribe({
      next: (order) => {
        this.lastOrderId = order.id!;
        this.orderStatus = `Commande #${order.id} créée avec succès! Statut: ${order.status}`;
        this.cart = [];
        this.showCart = false;
        
        // Show success message
        setTimeout(() => {
          alert(`Votre commande #${order.id} a été envoyée à la cuisine!`);
        }, 500);
      },
      error: (error) => {
        console.error('Erreur lors de la création de la commande:', error);
        this.orderStatus = 'Erreur lors de la création de la commande. Veuillez réessayer.';
      }
    });
  }
}
