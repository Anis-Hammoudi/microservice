package com.esgi.orderService.config;

import com.esgi.orderService.model.MenuItem;
import com.esgi.orderService.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MenuItemRepository menuItemRepository) {
        return args -> {
            // Check if menu items already exist
            if (menuItemRepository.count() == 0) {
                List<MenuItem> menuItems = List.of(
                    MenuItem.builder()
                        .name("Salade César")
                        .description("Laitue romaine, parmesan, croûtons, sauce César")
                        .price(12.50)
                        .category("Salades")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Burger Classique")
                        .description("Bœuf, cheddar, laitue, tomate, oignon, sauce maison")
                        .price(15.90)
                        .category("Burgers")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Pizza Margherita")
                        .description("Tomate, mozzarella, basilic frais")
                        .price(13.50)
                        .category("Pizzas")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Pizza Pepperoni")
                        .description("Tomate, mozzarella, pepperoni")
                        .price(14.90)
                        .category("Pizzas")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Pâtes Carbonara")
                        .description("Pâtes fraîches, lardons, crème, parmesan")
                        .price(13.90)
                        .category("Pâtes")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Poulet Rôti")
                        .description("Poulet fermier, pommes de terre, légumes de saison")
                        .price(18.50)
                        .category("Plats")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Tiramisu")
                        .description("Mascarpone, café, cacao, biscuits")
                        .price(6.50)
                        .category("Desserts")
                        .available(true)
                        .build(),
                    MenuItem.builder()
                        .name("Crème Brûlée")
                        .description("Crème vanillée, sucre caramélisé")
                        .price(7.00)
                        .category("Desserts")
                        .available(true)
                        .build()
                );
                
                menuItemRepository.saveAll(menuItems);
                System.out.println("✅ Menu items initialized successfully!");
            }
        };
    }
} 