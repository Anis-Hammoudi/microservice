import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError,tap } from 'rxjs/operators';

export interface FoodItem {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  image?: string;
  available?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FoodService {
  private apiUrl = 'http://localhost:8081/menu';

  constructor(private http: HttpClient) { }

  getMenuItems(): Observable<FoodItem[]> {
    return this.http.get<FoodItem[]>(this.apiUrl).pipe(
      tap(data => console.log('Received menu items:', data)),
      catchError(error => {
        console.error('Error fetching menu items:', error);
        return of(this.getHardcodedItems());
      })
    );
  }


  getItemsByCategory(category: string): Observable<FoodItem[]> {
    return this.http.get<FoodItem[]>(`${this.apiUrl}?category=${category}`).pipe(
      tap(data => console.log(`Received items for category ${category}:`, data)),
      catchError(error => {
        console.error('Error fetching items by category:', error);
        const items = this.getHardcodedItems().filter(item => item.category === category);
        return of(items);
      })
    );
  }


  getItemById(id: number): Observable<FoodItem | undefined> {
    return this.http.get<FoodItem>(`${this.apiUrl}/${id}`).pipe(
      tap(data => console.log(`Received item with id ${id}:`, data)),
      catchError(error => {
        console.error('Error fetching item by id:', error);
        const item = this.getHardcodedItems().find(item => item.id === id);
        return of(item);
      })
    );
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`).pipe(
      tap(data => console.log('Received categories:', data)),
      catchError(error => {
        console.error('Error fetching categories:', error);
        const categories = [...new Set(this.getHardcodedItems().map(item => item.category))];
        return of(categories);
      })
    );
  }

  // Fallback hardcoded items in case backend is not available
  private getHardcodedItems(): FoodItem[] {
    return [
      {
        id: 1,
        name: 'Salade César',
        description: 'Laitue romaine, parmesan, croûtons, sauce César',
        price: 12.50,
        category: 'Salades',
        available: true
      },
      {
        id: 2,
        name: 'Burger Classique',
        description: 'Bœuf, cheddar, laitue, tomate, oignon, sauce maison',
        price: 15.90,
        category: 'Burgers',
        available: true
      },
      {
        id: 3,
        name: 'Pizza Margherita',
        description: 'Tomate, mozzarella, basilic frais',
        price: 13.50,
        category: 'Pizzas',
        available: true
      },
      {
        id: 4,
        name: 'Pizza Pepperoni',
        description: 'Tomate, mozzarella, pepperoni',
        price: 14.90,
        category: 'Pizzas',
        available: true
      },
      {
        id: 5,
        name: 'Pâtes Carbonara',
        description: 'Pâtes fraîches, lardons, crème, parmesan',
        price: 13.90,
        category: 'Pâtes',
        available: true
      },
      {
        id: 6,
        name: 'Poulet Rôti',
        description: 'Poulet fermier, pommes de terre, légumes de saison',
        price: 18.50,
        category: 'Plats',
        available: true
      }
    ];
  }
}
