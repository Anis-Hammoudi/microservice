import { Component, OnInit } from '@angular/core';
import { FoodService, FoodItem } from '../../services/food.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface CartItem extends FoodItem {
  quantity: number;
}

@Component({
  selector: 'app-home',
  standalone: true, // <-- Important for standalone
  imports: [CommonModule, FormsModule], // <-- Add needed modules
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
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
    this.foodService.getMenuItems().subscribe((items: FoodItem[]) => {
      this.menuItems = items;
    });
  }

  loadCategories() {
    this.foodService.getCategories().subscribe((categories: string[]) => {
      this.categories = categories;
    });
  }

  filterByCategory(category: string) {
    this.selectedCategory = category;
    if (category) {
      this.foodService.getItemsByCategory(category).subscribe((items: FoodItem[]) => {
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

    const orderItems = this.cart.map(item => `${item.name} x${item.quantity}`);

    this.orderService.createOrder(orderItems).subscribe({
      next: (order: any) => {
        this.lastOrderId = order.id!;
        this.orderStatus = `Commande #${order.id} créée avec succès! Statut: ${order.status}`;
        this.cart = [];
        this.showCart = false;

        setTimeout(() => {
          alert(`Votre commande #${order.id} a été envoyée à la cuisine!`);
        }, 500);
      },
      error: (error: any) => {
        console.error('Erreur lors de la création de la commande:', error);
        this.orderStatus = 'Erreur lors de la création de la commande. Veuillez réessayer.';
      }
    });
  }
}
