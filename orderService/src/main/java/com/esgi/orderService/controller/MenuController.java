package com.esgi.orderService.controller;

import com.esgi.orderService.model.MenuItem;
import com.esgi.orderService.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems(@RequestParam(required = false) String category) {
        if (category != null) {
            return ResponseEntity.ok(menuItemRepository.findByCategory(category));
        }
        return ResponseEntity.ok(menuItemRepository.findByAvailable(true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> item = menuItemRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem saved = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        menuItem.setId(id);
        MenuItem updated = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        menuItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = menuItemRepository.findAll().stream()
                .map(MenuItem::getCategory)
                .distinct()
                .toList();
        return ResponseEntity.ok(categories);
    }
} 